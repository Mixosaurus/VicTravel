package cn.vic.travel.showtipdetail;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.vic.travel.R;
import cn.vic.travel.chat.ChatActivity;
import cn.vic.travel.chat.IMMLeaks;
import cn.vic.travel.network.NetUser;
import cn.vic.travel.network.VicApplication;
import cn.vic.travel.register.RegisterActivity;

import static cn.vic.travel.Utils.*;

public class TipDetailActivity extends AppCompatActivity {
    TextView tvTip;
    Button btnHi;
    Button btnViewInfo;

    BmobIMUserInfo info;    //用户信息
    String tipOwnerName;        //贴士发布者，即信息接收者
    TextView tvTipOwnerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tip_detail);
        tvTip=(TextView) findViewById(R.id.tv_tip);
        btnHi=(Button)findViewById(R.id.btn_hi);
        tvTipOwnerName=(TextView)findViewById(R.id.tv_tip_owner_name);

        Bundle bundle = getIntent().getExtras();    //得到传过来的bundle
        String tipContent;      //贴士内容
        //VicApplication.getInstance().initIM();
        checkStatusAndConnect();       //初始化IM

        if (bundle != null) {
            tipOwnerName = bundle.getString("userName");    //读出贴士拥有着数据
            tipContent = bundle.getString("tipContent");    //读出贴士内容数据
            tvTipOwnerName.setText(tipOwnerName);
            tvTip.setText(tipContent);
        }
        else{
            debugToast("Bundle为null");
        }

        btnHi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStatusAndConnect();
                chat("填写接收者的头像",tipOwnerName,tipOwnerName);
            }
        });
    }

    // TODO: 2018/7/31 待优化  0为未连接
    private void checkStatusAndConnect(){
            //监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
                    // 0为未连接
                    if(status.getCode()==ConnectionStatus.DISCONNECT.getCode()){
                        VicApplication.getInstance().initIM();
                    }
                }
            });

        //解决leancanary提示InputMethodManager内存泄露的问题
        //todo 暂时注释
        //IMMLeaks.fixFocusedViewLeak(getApplication());
    }

//    int getIMConnectStatus(){
//        int connectStatus;
//        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
//            @Override
//            public void onChange(ConnectionStatus status) {
//                debugToast(status.getMsg());
//                connectStatus=status;
//                //Logger.i(BmobIM.getInstance().getCurrentStatus().getMsg());
//            }
//        });
//        return connectStatus;
//    }
    
    private void chat( String receiverAvatar,String receiverUserId,String receiverName ) {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            debugToast("尚未连接IM服务器");
            return;
        }
        BmobIMUserInfo receiverInfo =new BmobIMUserInfo();
        receiverInfo.setAvatar(receiverAvatar);
        receiverInfo.setUserId(receiverUserId);        //接收者的身份标识，暂设为用户名
        receiverInfo.setName(receiverName);
        // 创建一个常态会话入口，陌生人聊天
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(receiverInfo, null);

        Bundle bundle = new Bundle();
        bundle.putSerializable("conversationEntrance", conversationEntrance);

        Intent intent = new Intent();
        intent.setClass(this, ChatActivity.class);
        if (bundle != null){
            intent.putExtra(getPackageName(), bundle);
            intent.putExtra("tipOwnerName",receiverName);
            startActivity(intent);
        }
    }
}
