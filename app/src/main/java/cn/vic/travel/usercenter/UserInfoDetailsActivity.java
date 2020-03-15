package cn.vic.travel.usercenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;

import java.util.List;

import cn.vic.travel.R;
import cn.vic.travel.network.VicApplication;
import cn.vic.travel.widgets.BottomDialog;

import static cn.vic.travel.Utils.debugToast;
import static cn.vic.travel.localdata.Flag.REQUEST_CODE;

/**
 * Snake 创建于 2018/8/26.
 * 用户详细信息页面
 */

public class UserInfoDetailsActivity extends AppCompatActivity {

    TextView textView;
    private View itemPortrait;
    private View itemConstellation;
    private VicApplication vicApplication=VicApplication.getInstance();
    private BottomDialog bottomDialog;      //底部对话框
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_details);
        itemPortrait= findViewById(R.id.include_item_1);
        itemConstellation= findViewById(R.id.include_item_2);
        bottomDialog=new BottomDialog(this,R.style.BottomDialog);
        textView=(TextView)findViewById(R.id.tv_general) ;
        setItemName();
        setOnClickListener();
    }

    //图片完成选择时调用
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            List<String> nativeImages = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);     //原始路径的图片
            debugToast("imageList size: "+nativeImages.size());
            try {

//                //把图片上传至数据库
//                uploadBtimap(new FileUtil().findFile(testDir));
            }
            catch (Exception e) {
                debugToast(e.toString());
                e.printStackTrace();
            }
        }
    }

    //设置条目名称
    void setItemName(){
        textView.setText("头像");
    }

    //设置条目点击事件
    void setOnClickListener(){
        itemPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.show();
            }
        });
        bottomDialog.setOnClickListener(new BottomDialog.BottonDialogListener() {
            @Override
            public void onButton1Listener() {
                //开启图片选择器
                ImageSelectorUtils.openPhoto(UserInfoDetailsActivity.this, REQUEST_CODE, false, 9);
            }
            @Override
            public void onButton2Listener() {
                //debugToast("拍照");
            }

        });
    }


    //上传图片
    private void uploadBtimap(List<String> images){
        for(String path :images){
            vicApplication.uploadBtimap(path);
        }
    }



}
