package cn.vic.travel.writetip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.vic.travel.R;
import cn.vic.travel.homepage.HomePageActivity;

public class WriteTipActivity extends AppCompatActivity implements IWriteTipContract.View {
    private Button btnPublish;
    //private Button btnCancel;
    private EditText etInputTip;
    private IWriteTipContract.Presenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tip);
        etInputTip =(EditText )findViewById ( R.id.et_tip_content ) ;
        btnPublish = (Button ) findViewById ( R.id.btn_publish );

        //btnCancel =(Button )findViewById ( R.id.btn_back );

        mPresenter=new WriteTipPresenter(this);
        //发表按钮点击事件
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.uploadTip(etInputTip.getText().toString());  //上传纸条内容
            }
        });
        //取消按钮点击事件
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeToHomePageActivity();     //转跳到主页
//            }
//        });

        mPresenter.startLocation();     //开启定位
    }
    @Override
    public void toast(CharSequence info) {
        Toast.makeText ( WriteTipActivity.this,info,Toast.LENGTH_LONG ).show ();
    }

    @Override
    public void changeToHomePageActivity() {
        startActivity (new Intent(this, HomePageActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onViewDestory();     //停止定位
    }
}
