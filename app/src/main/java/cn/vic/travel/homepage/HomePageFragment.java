package cn.vic.travel.homepage;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.vic.travel.guide.GuideActivity;
import cn.vic.travel.R;
import cn.vic.travel.UnityArActivity;
import cn.vic.travel.network.VicApplication;
import cn.vic.travel.traveltogether.SetUpTravelActivity;
import cn.vic.travel.traveltogether.TravelInfoActivity;
import cn.vic.travel.writetip.WriteTipActivity;

import static cn.vic.travel.localdata.Flag.SEEK_TIP;
import static cn.vic.travel.localdata.Flag.STORY;

/**
 * 主页Fragment,几乎所有界面逻辑皆在此处
 * Snake 创建于 2018/4/22.
 */

public class HomePageFragment extends Fragment {
    private Button btnWriteTip;
    private Button btnSeekTip;
    private Button btnStory;
    private Button btnTravelInfo;
    private Button btnGuide;
    private Button btnSetUpTravel;

    private VicApplication vicAppliction= VicApplication.getInstance();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //加载布局文件
        View view = inflater.inflate(R.layout.fragment_home_page, null);
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnSeekTip=(Button)getActivity().findViewById(R.id.btn_seektip);
        btnWriteTip=(Button)getActivity().findViewById(R.id.btn_writetip);
        btnStory=(Button)getActivity().findViewById(R.id.btn_story);
        btnGuide=(Button)getActivity().findViewById(R.id.btn_guide);
        btnTravelInfo =(Button)getActivity().findViewById(R.id.btn_travel_info);
        btnSetUpTravel=(Button)getActivity().findViewById(R.id.btn_setup_travel);

        btnSeekTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),UnityArActivity.class).putExtra("flag", SEEK_TIP));      //指示Unity3D切换至找纸条场景
            }
        });

        btnWriteTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),WriteTipActivity.class));        //切换至写纸条场景
            }
        });

        btnStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),UnityArActivity.class).putExtra("flag", STORY));    //指示Unity3D切换至故事场景
            }
        });

        btnTravelInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String privateDir=getActivity().getExternalFilesDir("").getAbsolutePath()+"/images/"+currentUserName+"/";      //本应用的私有数据存储路径
//                String currentUserName= VicApplication.getInstance().getCurrentUserName();
//                String testDir = Environment.getExternalStorageDirectory()+ "/TestImage/"+currentUserName+"/";   //测试目录
//                List<String> s =new FileUtil().findFile(testDir);
//                for (String path:s) {
//                    //删除数据库中的图片
//                    //new FileUtil().deleteFile(path);
//                    vicAppliction.deleteBitmap(path);
//                }
              //  startActivity(new Intent(getActivity(), SetUpTravelActivity.class));
                startActivity(new Intent(getActivity(), TravelInfoActivity.class));
            }
        });

        btnGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),GuideActivity.class));
            }
        });

        btnSetUpTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),SetUpTravelActivity.class));
            }
        });

    }

    public static HomePageFragment getInstance() {
        return new HomePageFragment();
    }
}
