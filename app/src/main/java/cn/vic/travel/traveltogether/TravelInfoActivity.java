package cn.vic.travel.traveltogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.vic.travel.R;
import cn.vic.travel.chat.OnRecyclerViewListener;
import cn.vic.travel.network.NetUser;
import cn.vic.travel.network.VicApplication;

import static cn.vic.travel.Utils.debugToast;

import static cn.vic.travel.traveltogether.TravelInfoAdapter.LOADING_MORE;
import static cn.vic.travel.traveltogether.TravelInfoAdapter.PULLUP_LOAD_MORE;

/**
 * Snake 创建于 2018/8/10.
 * 查看他人旅行信息页面
 * item被点击后，将相关信息传入下一页面(TravelInfoDetailsActivity)
 * 信息被处理完后传回本页面
 */

public class TravelInfoActivity extends AppCompatActivity {
    private int INFO_LIMIT=6;
    private TravelInfoAdapter mAdapter;
    private SwipeRefreshLayout srlTravelInfo;
    private int i=0;        //查询忽略数量
    private final static int REQUESTCODE = 1;   //请求码
    RecyclerView rvTravelInfo;
    LinearLayoutManager layoutManager;
    NetUser netUser = new NetUser ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_info);
        srlTravelInfo = (SwipeRefreshLayout) findViewById(R.id.srl_travel_info);
        rvTravelInfo = (RecyclerView) findViewById(R.id.rv_travel_info);
        mAdapter=new TravelInfoAdapter();
        layoutManager = new LinearLayoutManager(this);
        rvTravelInfo.setLayoutManager(layoutManager);
        rvTravelInfo.setAdapter(mAdapter);
        mAdapter.setInfoLimit(INFO_LIMIT);
        srlTravelInfo.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               queryTravelInfoByTime ();

            }
        });
        initLoadMoreListener();
        queryTravelInfoByLimit();
        setListener();
    }

    private void setListener() {
        mAdapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                //mAdapter.getTravelInfo(position);
                Bundle bundleTravelInfo=new Bundle();
                bundleTravelInfo.putSerializable("travelInfo",mAdapter.getTravelInfo(position));
                //将item索引传入下一个页面
                startActivityForResult(new Intent(TravelInfoActivity.this,TravelInfoDetailsActivity.class)
                        .putExtras(bundleTravelInfo)
                        .putExtra("position",position)
                        ,REQUESTCODE);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
    }

    /**
     * 按数量限制查询
     */
    private void queryTravelInfoByLimit(){
        BmobQuery<TravelInfo> query = new BmobQuery<TravelInfo>();
        query.addQueryKeys ( "objectId,maxNumberOfPeople,username,user,title,startTime,remarks,numberOfPeople,endTime,destination,departure" );
        query.setLimit (INFO_LIMIT);
        query.setSkip(i);
        query.findObjects ( new FindListener<TravelInfo> () {
            @Override
            public void done(List<TravelInfo> list, BmobException e) {
                if (e == null) {
                    mAdapter.addMessages(list);
                }
                else {
                    debugToast ( "查询失败 " + e.toString ());
                }
            }
        } );
        if(srlTravelInfo.isRefreshing()){
            srlTravelInfo.setRefreshing(false);
        }
        i=i+INFO_LIMIT;
    }

    /**
     * 按时间查询
     */
    private void queryTravelInfoByTime(){
        BmobQuery<TravelInfo> query = new BmobQuery<TravelInfo>();
        List<BmobQuery<TravelInfo>> and=new ArrayList<BmobQuery<TravelInfo>> (  );
        //大于当前时间00：00：00
        BmobQuery<TravelInfo> q1=new BmobQuery<TravelInfo> (  );
        Date date=new Date ();

        String str=dateChange ( date );
        Date startDate=null;
        SimpleDateFormat strformat=new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" );
        try {
            startDate=strformat.parse ( str );
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        q1.addWhereGreaterThanOrEqualTo ( "createdAt",new BmobDate ( startDate ) );
        and.add ( q1 );

        //小于当前时间23：59：59
        BmobQuery<TravelInfo> q2=new BmobQuery<TravelInfo> (  );
        String str1=dateChangeEnd( date );
        Date endDate=null;
        try {
            endDate=strformat.parse ( str1 );
        }
        catch (ParseException e) {
            e.printStackTrace ();
        }
        q2.addWhereLessThanOrEqualTo ( "createdAt",new BmobDate ( endDate ) );
        and.add ( q2 );

        //复合查询，查询当天的记录
        query.and ( and );
        query.findObjects ( new FindListener<TravelInfo> () {
            @Override
            public void done(List<TravelInfo> list, BmobException e) {
                if (e==null) {
                    mAdapter.deleteRepeatedMessage(list);       //删除重复数据
                    mAdapter.addMessagesToStart ( list,0 );         //添加到最前端
                    if(mAdapter.getLatestInfoSize()>0){
                        Toast.makeText (VicApplication.getInstance(),"更新了"+list.size()+"条数据",Toast.LENGTH_LONG ).show ();
                    }
                    else {
                        Toast.makeText (VicApplication.getInstance(),"无最新数据",Toast.LENGTH_LONG ).show ();
                    }
                }
            }
        } );
        if(srlTravelInfo.isRefreshing()){
            srlTravelInfo.setRefreshing(false);
        }
    }

    //起始时间
    private String dateChange(Date date)
    {
        SimpleDateFormat strformat=new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" );
        String strDate=strformat.format ( date );
        String[] strings=strDate.split ( " " );
        String[] strings1=strings[1].split ( ":" );
        String string;
        for (int i=0;i<strings1.length;i++)
        {
            strings1[i]="00";
        }
        string=strings1[0]+":"+strings1[1]+":"+strings1[2];
        String str=strings[0]+" "+string;

        return  str;
    }

    //截至时间
    private String dateChangeEnd(Date date)
    {
        SimpleDateFormat strformat=new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" );
        String strDate=strformat.format ( date );
        String[] strings=strDate.split ( " " );
        String[] strings1=strings[1].split ( ":" );
        String string;
        strings1[0]="23";
        strings1[1]="59";
        strings1[2]="59";
        string=strings1[0]+":"+strings1[1]+":"+strings1[2];
        String str=strings[0]+" "+string;

        return  str;
    }
    private void initLoadMoreListener() {

        rvTravelInfo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem ;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if(newState==RecyclerView.SCROLL_STATE_IDLE&&lastVisibleItem+1==mAdapter.getItemCount()){
                    //设置正在加载更多
                    mAdapter.changeMoreStatus(LOADING_MORE);
                    queryTravelInfoByLimit();
                    //设置回到上拉加载更多
                    mAdapter.changeMoreStatus(PULLUP_LOAD_MORE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem=layoutManager.findLastVisibleItemPosition();
            }
        });

    }

    /**
     * 在下一个页面中更改travelInfo对象信息后在本页面同步该信息
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            if (requestCode == REQUESTCODE) {
                Bundle travelInfoBundle = data.getBundleExtra("travelInfoBundle");      //取出Bundle
                TravelInfo travelInfo= (TravelInfo) travelInfoBundle.get("travelInfo");     //取出travelInfo对象
                int position=data.getIntExtra("position",-1);
                mAdapter.getTravelInfo(position).setNumberOfPeople(travelInfo.getNumberOfPeople());
            }
        }
    }



}
