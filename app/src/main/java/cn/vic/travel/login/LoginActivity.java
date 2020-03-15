package cn.vic.travel.login;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import cn.vic.travel.R;
import cn.vic.travel.firstpage.FirstPageActivity;
import cn.vic.travel.homepage.HomePageActivity;
import cn.vic.travel.network.VicApplication;

import static cn.vic.travel.Utils.debugToast;
import static cn.vic.travel.network.VicApplication.shutActivity;

public class LoginActivity extends AppCompatActivity implements ILoginContract.View{

    private EditText etName;
    private EditText etPassword;
    private Button btnLogin;
    private ImageButton ibtnBack;       //返回箭头
    private ILoginContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etName = (EditText ) findViewById ( R.id.et_name);
        etPassword = (EditText ) findViewById ( R.id.et_password);
        btnLogin = (Button ) findViewById ( R.id.btn_login);
        ibtnBack=(ImageButton)findViewById(R.id.ibtn_ligin_back);
        //创建Presenter,传入View，即本类自身
        mPresenter=new LoginPresenter(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnLoginClick();
            }
        });
        ibtnBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    ibtnBack.getBackground().setAlpha(100);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    ibtnBack.getBackground().setAlpha(255);
                    shutActivity(LoginActivity.this);      //关闭当前页面
                }
                return false;
            }
        });

        //从注册页面传递的信息
        String name=getIntent().getStringExtra("name");
        String passWord= getIntent().getStringExtra("passWord");
        if(name!=null&&passWord!=null){
            etName.setText(name);
            etPassword.setText(passWord);
        }
    }
    //登录按钮点击事件
    public void onBtnLoginClick() {
        String name= etName.getText ().toString ();
        String password= etPassword.getText ().toString ();
        //由mPresenter执行登录功能
        mPresenter.doLogin(name,password);
    }

    @Override
    public void toast(CharSequence info) {
        Toast.makeText (this,info,Toast.LENGTH_LONG ).show();
    }

    @Override
    public void changeToHomePageActivity() {
        //跳转至主页
        startActivity (new Intent(LoginActivity.this, HomePageActivity.class));
        shutActivity(LoginActivity.this);      //关闭当前页面
        shutActivity("FirstPageActivity");      //关闭FirstPageActivity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        mPresenter.onViewDestory();
    }

}
