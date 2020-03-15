package cn.vic.travel.guide;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.bmob.v3.BmobWrapper;
import cn.vic.travel.R;
import cn.vic.travel.UnityArActivity;
import cn.vic.travel.localdata.FileUtil;
import cn.vic.travel.network.VicApplication;

import static cn.vic.travel.Utils.debugToast;
import static cn.vic.travel.Utils.saveLog;
import static cn.vic.travel.localdata.Flag.GUIDE;
import static cn.vic.travel.localdata.Flag.STORY;

public class GuideActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener, Inputtips.InputtipsListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private SearchView mSearchView;// 输入搜索关键字
    private ImageView mBack;
    private ListView mInputListView;
    private List<Tip> mCurrentTipList;
    private InputTipsAdapter mIntipAdapter;
    private RouteSearch mRouteSearch;
    private WalkRouteResult mWalkRouteResult;


    // TODO: 2018/6/23 测试目录
    String testDir = Environment.getExternalStorageDirectory()+"/"+VicApplication.getInstance().getCurrentUserName()+"/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initSearchView();
        mInputListView = (ListView) findViewById(R.id.inputtip_list);
        mInputListView.setOnItemClickListener(this);
        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult result, int i) {
                StringBuffer breakPointArray=new StringBuffer();     //拐点数组
                if (result != null && result.getPaths() != null) {
                    if (result.getPaths().size() > 0) {
                        mWalkRouteResult = result;
                        final WalkPath walkPath = mWalkRouteResult.getPaths().get(0);       //取第一条路线
                        List<WalkStep> walskStep=walkPath.getSteps();
                        double startLongitude=mWalkRouteResult.getStartPos().getLongitude();     //起点经度
                        double startLatitude=mWalkRouteResult.getStartPos().getLatitude();     //起点纬度
                        breakPointArray.append(startLongitude+","+startLatitude+"~");       //将起点存入字符串
                        //将坐标存入字符串，以~分隔，不含起点和终点
                        for(WalkStep walkStep: walkPath.getSteps()){
                            double longitude=walkStep.getPolyline().get(0).getLongitude();
                            double latitude=walkStep.getPolyline().get(0).getLatitude();
                            breakPointArray.append(longitude+","+latitude+"~");
                            //saveLog(longitude+","+latitude);
                        }
                        double endLongitude=mWalkRouteResult.getTargetPos().getLongitude();    //终点经度
                        double endLatitude=mWalkRouteResult.getTargetPos().getLatitude();      //终点纬度
                        breakPointArray.append(endLongitude+","+endLatitude);       //将起点存入字符串
                        String breakPointStr=breakPointArray.toString();
                        saveLog(breakPointArray.toString());
                        startActivity(new Intent( GuideActivity.this,UnityArActivity.class)
                                .putExtra("flag", GUIDE)
                                .putExtra("breakPointStr",breakPointStr));    //指示Unity3D切换至故事场景
                    }
                    else if (result != null && result.getPaths() == null) {
                        toast("对不起，没有搜索到相关数据");
                    }
                }
                else {
                    toast("对不起，没有搜索到相关数据");
                }
            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });
        // 开启连续定位
        VicApplication.getInstance().startContinueLocation();
    }

    private void initSearchView() {
        mSearchView = (SearchView) findViewById(R.id.keyWord);
        mSearchView.setOnQueryTextListener(this);
        //设置SearchView默认为展开显示
        mSearchView.setIconified(false);
        mSearchView.onActionViewExpanded();
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setSubmitButtonEnabled(false);
    }

    /**
     * 输入提示回调
     *
     * @param tipList
     * @param rCode
     */
    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == 1000) {
            // 正确返回
            mCurrentTipList = tipList;
            List<String> listString = new ArrayList<String>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            mIntipAdapter = new InputTipsAdapter(getApplicationContext(), mCurrentTipList);
            mInputListView.setAdapter(mIntipAdapter);
            mIntipAdapter.notifyDataSetChanged();
        } else {
            toast(new StringBuffer(rCode));
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mCurrentTipList != null) {
            Tip tip = (Tip) adapterView.getItemAtPosition(i);
            String[] locationArray = getLocation().split(",");  //获取地理位置，格式：经度,纬度
            if(!locationArray[0].equals("!")){
                double selfLongitude=Double.parseDouble(locationArray[0]);
                double selfLatitude= Double.parseDouble(locationArray[1]);
                //debugToast("起点"+selfLongitude+"，"+selfLatitude);
                //debugToast("终点"+tip.getPoint().getLongitude()+"，"+tip.getPoint().getLatitude());
                searchRouteResult(new LonLatPoint(selfLongitude,selfLatitude),new LonLatPoint(tip.getPoint().getLongitude(),tip.getPoint().getLatitude()));
                // TODO: 显示进度条
            }
            else{
                debugToast("无位置信息");
            }
        }
    }

    /**
     * 按下确认键触发，本例为键盘回车或搜索键
     *
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_WORDS_NAME, query);
        //setResult(MainActivity.RESULT_CODE_KEYWORDS, intent);
        // TODO: 2018/6/21 关闭当前页面
        return false;
    }

    /**
     * 输入字符变化时触发
     *
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if (!IsEmptyOrNullString(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, Constants.DEFAULT_CITY);
            Inputtips inputTips = new Inputtips(GuideActivity.this.getApplicationContext(), inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        } else {
            if (mIntipAdapter != null && mCurrentTipList != null) {
                mCurrentTipList.clear();
                mIntipAdapter.notifyDataSetChanged();
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            this.finish();
        }
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(LonLatPoint startPoint ,LonLatPoint endPoint) {
        if (startPoint == null) {
            toast("定位中，稍后再试...");
            return;
        }
        if (endPoint == null) {
            toast("终点未设置");
        }
        //showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        // 步行路径规划
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
        // 异步路径规划步行模式查询
        mRouteSearch.calculateWalkRouteAsyn(query);
    }

    public String getLocation(){
        return VicApplication.getInstance().getLocation();
    }

    public static boolean IsEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    public void toast(CharSequence text) {
        Toast.makeText (VicApplication.getInstance(),text,Toast.LENGTH_LONG ).show ();
    }

}
