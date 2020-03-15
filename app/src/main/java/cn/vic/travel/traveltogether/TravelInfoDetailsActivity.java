package cn.vic.travel.traveltogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.vic.travel.R;
import cn.vic.travel.network.NetUser;
import cn.vic.travel.network.VicApplication;

import static cn.vic.travel.Utils.debugToast;
import static cn.vic.travel.Utils.getTimeInString;

public class TravelInfoDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView tvContent;
    TextView tvDeparture;
    TextView tvDestination;
    TextView tvPeopleNumber;
    TextView tvDate;
    TravelInfo travelInfo;
    FloatingActionButton fab;
    int thisItemPosition;
    private final static int RESULTCODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_info_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvContent=(TextView) findViewById(R.id.tv_content);
        tvDeparture=(TextView) findViewById(R.id.tv_departure);
        tvDestination=(TextView) findViewById(R.id.tv_destination);
        tvPeopleNumber=(TextView) findViewById(R.id.tv_people_number);
        tvDate=(TextView) findViewById(R.id.tv_date);
        fab= (FloatingActionButton) findViewById(R.id.fab);
        //获取里面的Persion里面的数据
        travelInfo= (TravelInfo)(getIntent().getExtras().getSerializable("travelInfo"));
        thisItemPosition= getIntent().getIntExtra("position",-1);
        toolbar.setTitle(travelInfo.getTitle());
        setSupportActionBar(toolbar);
        tvDeparture.setText("出发地："+travelInfo.getDeparture());
        tvDestination.setText("目的地："+travelInfo.getDestination());
        tvPeopleNumber.setText( "已加入："+travelInfo.getNumberOfPeople().intValue()+"/"+travelInfo.getMaxNumberofPeople().intValue());

        String startDate=getTimeInString(travelInfo.getStartTime(),true);
        String endDate=getTimeInString(travelInfo.getEndTime(),true);

        tvDate.setText(startDate+"—"+endDate);
        tvContent.setText(travelInfo.getRemarks());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(travelInfo.getNumberOfPeople().intValue()<travelInfo.getMaxNumberofPeople().intValue()){
                    joinTravel();
                }
                else{
                    Toast.makeText (VicApplication.getInstance(),"人数已满",Toast.LENGTH_LONG ).show ();
                }
            }
        });

    }


    /**
     * 加入旅行
     */
    private void joinTravel(){
        NetUser user = BmobUser.getCurrentUser ( NetUser.class );
        String traveluser = user.getUsername ();//同行人名
        final String title = travelInfo.getTitle();//标题
        String username = travelInfo.getUsername();//发行人名
        TravelUser travelUser = new TravelUser ( traveluser, title, username );
        travelUser.save ( new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    UpdateTravelNumber(travelInfo.getNumberOfPeople().intValue()+1,travelInfo.getObjectId());       //人数加一
                } else {
                    Toast.makeText (VicApplication.getInstance(),"加入失败！",Toast.LENGTH_LONG ).show ();
                }
            }
        } );
    }

//    //查询发起记录
//    public  void selectTravelInfo(final String title) {
//        BmobQuery<TravelInfo> travelInfoBmobQuery=new BmobQuery<TravelInfo>();
//        travelInfoBmobQuery.addWhereEqualTo("title",title);
//        travelInfoBmobQuery.findObjects(new FindListener<TravelInfo>() {
//            @Override
//            public void done(List<TravelInfo> list, BmobException e) {
//                if(e==null){
//                    if(list!=null){
//                        for (TravelInfo travelInfo : list) {
//                            selectTravelNumber ( travelInfo.getTitle(),travelInfo.getObjectId() );
//                            if(travelInfo.getNumberOfPeople()!=null&&travelInfo.getMaxNumberofPeople()!=null){
//                                tvPeopleNumber.setText("已加入："+travelInfo.getNumberOfPeople().intValue()+"/"+travelInfo.getMaxNumberofPeople().intValue());
//                            }
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//    //查询人数
//    public void selectTravelNumber(final String title, final String travelInfoId) {
//        //显示已加入的人数
//        BmobQuery<TravelUser> travelUserBmobQuery = new BmobQuery<TravelUser> ();
//        travelUserBmobQuery.addWhereEqualTo ( "title", title );
//        travelUserBmobQuery.findObjects ( new FindListener<TravelUser>() {
//            @Override
//            public void done(List<TravelUser> list, BmobException e) {
//                if (e == null) {
//                    UpdateTravelNumber ( list.size (),travelInfoId );
//                }
//            }
//        } );
//    }


    //更新人数
    public void UpdateTravelNumber(final int num,String objectId) {
        travelInfo.setNumberOfPeople ( num );
        travelInfo.update ( objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    tvPeopleNumber.setText("已加入："+travelInfo.getNumberOfPeople().intValue()+"/"+travelInfo.getMaxNumberofPeople().intValue());
                    Toast.makeText (VicApplication.getInstance(),"加入成功！",Toast.LENGTH_LONG ).show ();
                    fab.setEnabled(false);      //将按钮置为不可点击
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("travelInfo",travelInfo);
                    //设置结果码和返回数据,以及当前item索引
                    setResult(RESULTCODE, new Intent().putExtra("travelInfoBundle",bundle)
                            .putExtra("position",thisItemPosition));
                }
                else {
                    debugToast ( ""+e.toString () );
                }
            }
        } );
    }


}
