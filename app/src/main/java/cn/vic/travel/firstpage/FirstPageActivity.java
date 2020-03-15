package cn.vic.travel.firstpage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import cn.vic.travel.R;
import cn.vic.travel.Utils;
import cn.vic.travel.localdata.FileUtil;
import cn.vic.travel.login.LoginActivity;
import cn.vic.travel.register.RegisterActivity;
import static cn.vic.travel.Utils.debugToast;
import static cn.vic.travel.Utils.saveLog;
import static cn.vic.travel.network.VicApplication.addToActivities;
import static cn.vic.travel.network.VicApplication.shutActivity;

public class FirstPageActivity extends AppCompatActivity {
    private Button btnRegister;
    private Button btnLoginn;
    private String extraStrinng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        extraStrinng=getIntent().getStringExtra("shut");
        if(extraStrinng!=null && !TextUtils.isEmpty(extraStrinng)){
            shutActivity(extraStrinng);     //关闭页面
        }
        addToActivities("FirstPageActivity",this);      //加入activity列表
        btnRegister=(Button )findViewById(R.id.btn_register);
        btnLoginn=(Button )findViewById(R.id.btn_login);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstPageActivity.this, RegisterActivity.class));
            }
        });
        btnLoginn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstPageActivity.this, LoginActivity.class));
            }
        });
    }
}
