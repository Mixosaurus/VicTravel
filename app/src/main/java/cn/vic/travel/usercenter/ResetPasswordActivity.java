package cn.vic.travel.usercenter;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.vic.travel.R;
import cn.vic.travel.firstpage.FirstPageActivity;
import cn.vic.travel.network.NetUser;
import cn.vic.travel.network.VicApplication;
import cn.vic.travel.register.RegisterActivity;
import cn.vic.travel.traveltogether.SetUpTravelActivity;

import static cn.vic.travel.Utils.debugToast;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etNewAgainPassword;
    private Button btnPreservePassword;
    private ImageView ivBack;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_reset_password );
        etOldPassword=(EditText)findViewById ( R.id.et_old_password );
        etNewPassword=(EditText)findViewById ( R.id.et_new_password );
        etNewAgainPassword=(EditText)findViewById ( R.id.et_new_again_password );
        ivBack=(ImageView)findViewById ( R.id.iv_back );
        btnPreservePassword=(Button)findViewById ( R.id.btn_preserved_password );
        btnPreservePassword.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String oldPassword=etOldPassword.getText ().toString ();
                String newPassword=etNewPassword.getText ().toString ();
                String newAgainPassword=etNewAgainPassword.getText ().toString ();
               NetUser user= BmobUser.getCurrentUser (NetUser.class);
                if(newPassword.isEmpty ()||newAgainPassword.isEmpty ()||oldPassword.isEmpty ()) {
                    if (newPassword.isEmpty ())
                    {
                        debugToast ( "请输入新密码!" );
                    }
                    else if(oldPassword.isEmpty ())
                    {
                        debugToast ( "请输入旧密码！" );
                    }
                    else
                    {
                        debugToast ( "再一次输入不能为空" );
                    }
                }
                else{
                    user.setPassword ( newPassword );
                    user.update ( user.getObjectId (), new UpdateListener () {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                debugToast ("密码修改成功，可以用新密码进行登录啦");
                                BmobUser.logOut();   //清除缓存用户对象
                                BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了
                                startActivity(new Intent(ResetPasswordActivity.this, FirstPageActivity.class));
                            }else{
                                debugToast ("失败:" + e.getMessage());
                            }
                        }
                    } );
                }
            }
        } );

        ivBack.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                VicApplication.getInstance ().shutActivity ( ResetPasswordActivity.this  );
            }
        } );
    }


}
