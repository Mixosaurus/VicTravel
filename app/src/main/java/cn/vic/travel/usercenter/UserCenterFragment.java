package cn.vic.travel.usercenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cn.bmob.v3.BmobUser;
import cn.vic.travel.R;
import cn.vic.travel.ImageLoaderTest;
import cn.vic.travel.firstpage.FirstPageActivity;

import static cn.vic.travel.network.VicApplication.shutActivity;

/**
 * Snake 创建于 2018/8/16.
 * 用户主页页面
 */

public class UserCenterFragment extends Fragment {

    private ConstraintLayout clPortrait;
    private View itemCheckVersion;
    private View itemAbout;
    private View itemClear;
    private View itemLogout;
    private View itemResetPassword;
    private View itemSetting;
    private Button btnTest;
    private Intent intent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        clPortrait=(ConstraintLayout)getActivity().findViewById(R.id.cl_portrait);
        itemAbout = (View) getActivity().findViewById(R.id.include_about);
        itemCheckVersion = (View) getActivity().findViewById(R.id.include_check_version);
        itemClear = (View) getActivity().findViewById(R.id.include_clear);
        itemLogout =(View) getActivity().findViewById(R.id.include_logout);
        itemResetPassword=(View) getActivity().findViewById(R.id.include_reset_password);
        itemSetting=(View) getActivity().findViewById(R.id.include_item_1);
        btnTest=(Button)getActivity().findViewById(R.id.btn_Test);

        clPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),UserInfoDetailsActivity.class));
            }
        });

        itemSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AppSettingActivity.class));
            }
        });

        itemAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog("关于","VicApp应用开发团队,保留所有权利",false,null);
            }
        });
        itemCheckVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog("版本","已是最新版本",false,null);
            }
        });
        itemClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog("缓存","已清除缓存",false,null);
            }
        });

        itemLogout.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                showNormalDialog("退出登录", "您确定要退出登录？",true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BmobUser.logOut();   //清除缓存用户对象
                        //getActivity().finish();
                        startActivity(new Intent(getActivity(), FirstPageActivity.class).putExtra("shut","HomePageActivity"));
                    }
                });

            }
        } );

        itemResetPassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ResetPasswordActivity.class));
            }
        } );

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ImageLoaderTest.class));
            }
        });


     }

    /**
     * 显示对话框
     * @param title 对话框标题
     * @param message 对话框消息提示
     */
    private void showNormalDialog(String title,String message,Boolean hasNegativeButton,DialogInterface.OnClickListener positiveButtonListener){
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(getActivity());
        normalDialog.setIcon(R.mipmap.ic_launcher_round);
        normalDialog.setTitle(title);
        normalDialog.setMessage(message);
        if(positiveButtonListener!=null){
            normalDialog.setPositiveButton("确定", positiveButtonListener);
        }
        else {
            normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });
        }
        if(hasNegativeButton){
            normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });
        }
        normalDialog.show();
    }

    public static UserCenterFragment getInstance() {
        return new UserCenterFragment();
    }

}
