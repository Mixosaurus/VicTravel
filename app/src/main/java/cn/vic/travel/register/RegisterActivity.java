package cn.vic.travel.register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import cn.bmob.v3.exception.BmobException;
import cn.vic.travel.R;
import cn.vic.travel.login.LoginActivity;

import static cn.vic.travel.network.VicApplication.shutActivity;

public class RegisterActivity extends AppCompatActivity implements IRegisterContract.View{
    private EditText etName;
    private EditText etPhoneNumber;
    private EditText etPassword;
    private Button btnRegister;
    private Button btnCancel;
    private ImageButton ibtnBack;
    private Intent intent;
    private BmobException bmobException;
    private IRegisterContract.Presenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName = (EditText ) findViewById ( R.id.et_name );
        etPhoneNumber = (EditText ) findViewById ( R.id.et_phonenumber );
        etPassword = (EditText ) findViewById ( R.id.et_password );
        btnRegister = (Button ) findViewById ( R.id.btn_register );
        btnCancel =(Button )findViewById ( R.id.btn_cancel );
        ibtnBack=(ImageButton) findViewById ( R.id.ibtn_register_back);

        //创建Presenter,传入View，即本类自身
        mPresenter=new RegisterPresenter(this);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnRegisterClick();
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
                    shutActivity(RegisterActivity.this);      //关闭当前页面
                }
                return false;
            }
        });

    }
    //注册按钮点击事件
    public void onBtnRegisterClick() {
        String name = etName.getText ().toString ();
        String phoneNumber = etPhoneNumber.getText ().toString ();
        String password = etPassword.getText ().toString ().trim ();
        //由presenter执行注册功能
        mPresenter.doRegister(name,phoneNumber,password);
    }

    @Override
    public void toast(CharSequence info) {
        Toast.makeText (this,info,Toast.LENGTH_LONG ).show();
    }

    @Override
    public void changeToLoginActivity(String name,String passWord) {
        //转跳到登录页面
        startActivity(new Intent (RegisterActivity.this,LoginActivity.class).putExtra("name",name).putExtra("passWord",passWord));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        mPresenter.onViewDestory();
    }
}
