package cn.vic.travel.chat;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.v3.exception.BmobException;
import cn.vic.travel.R;
import cn.vic.travel.network.NetUser;
import cn.vic.travel.network.VicApplication;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/6/18.
 * 聊天界面，含页面消息接收器
 */

public class ChatActivity extends AppCompatActivity implements MessageListHandler{
    EditText edit_msg;
    NetUser user;
    BmobIMUserInfo receiverInfo;    //用户信息
    BmobIMConversation mConversationManager;
    ChatAdapter adapter;
    SwipeRefreshLayout sw_refresh;
    RecyclerView rc_view;
    LinearLayout ll_chat;
    LinearLayout layout_more;
    LinearLayout layout_add;
    LinearLayout layout_emo;
    Button btn_chat_keyboard;
    Button btn_chat_voice;
    Button btn_speak;
    Button btn_chat_add;
    Button btn_chat_send;
    TextView tv_tip_owner_name;
    protected LinearLayoutManager layoutManager;
    /**
     * 消息发送监听器
     */
    public MessageSendListener listener = new MessageSendListener() {

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            //文件类型的消息才有进度值
            //Logger.i("onProgress：" + value);
            debugToast("onProgress：" + value);
        }

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            adapter.addMessage(msg);
            edit_msg.setText("");
            scrollToBottom();
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            adapter.notifyDataSetChanged();
            edit_msg.setText("");
            //java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.TextView.setText(java.lang.CharSequence)' on a null object reference
            scrollToBottom();
            if (e != null) {
                toast(e.getMessage());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tv_tip_owner_name=(TextView) findViewById(R.id.tv_tip_owner_name);
        edit_msg=(EditText)findViewById(R.id.edit_msg);
        sw_refresh=(SwipeRefreshLayout)findViewById(R.id.srl_chat);
        rc_view=(RecyclerView) findViewById(R.id.rc_view);
        ll_chat=(LinearLayout) findViewById(R.id.ll_chat);
        layout_more=(LinearLayout) findViewById(R.id.layout_more);
        layout_add=   (LinearLayout) findViewById(R.id.layout_add);
        layout_emo=(LinearLayout) findViewById(R.id.layout_emo);
        btn_chat_keyboard=(Button)findViewById(R.id.btn_chat_keyboard);
        btn_chat_voice=(Button)findViewById(R.id.btn_chat_voice);
        btn_speak=(Button)findViewById(R.id.btn_speak);
        btn_chat_add=(Button)findViewById(R.id.btn_chat_add);
        btn_chat_send=(Button)findViewById(R.id.btn_chat_send);
        if (getIntent() != null && getIntent().hasExtra(getPackageName())) {
            Bundle b=getIntent().getBundleExtra(getPackageName());
            BmobIMConversation conversationEntrance = (BmobIMConversation)b.getSerializable("conversationEntrance");
            mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        }
        String tipOwnerName=getIntent().getStringExtra("tipOwnerName");     //从上一Activity获取当前对话者用户名
        tv_tip_owner_name.setText(tipOwnerName);
        btn_chat_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClick(v);
            }
        });
        edit_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEditClick(v);
            }
        });
        //发送消息按钮点击
        btn_chat_send .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendClick(v);
            }
        });

        edit_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
            }
        });

        initSwipeLayout();
        initBottomView();
    }

    @Override
    protected void onResume(){
        BmobIM.getInstance().addMessageListHandler(this);       //添加页面消息监听器
        super.onResume();
    }

    @Override
    protected void onPause(){
        BmobIM.getInstance().removeMessageListHandler(this);        //移除页面消息监听器
        super.onPause();
    }

    /**
     * 初始化底部输入区
     */
    private void initBottomView() {
        edit_msg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                    scrollToBottom();
                }
                return false;
            }
        });
        edit_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                scrollToBottom();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 输入框点击事件
     * @param view
     */
    public void onEditClick(View view) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
            layout_more.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_chat_emo)
    public void onEmoClick(View view) {
        if (layout_more.getVisibility() == View.GONE) {
            showEditState(true);
        } else {
            if (layout_add.getVisibility() == View.VISIBLE) {
                layout_add.setVisibility(View.GONE);
                layout_emo.setVisibility(View.VISIBLE);
            } else {
                layout_more.setVisibility(View.GONE);
            }
        }
    }

    /**
     * btn_chat_add按钮点击事件
     * @param view
     */
    public void onAddClick(View view) {
        if (layout_more.getVisibility() == View.GONE) {
            layout_more.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            if (layout_emo.getVisibility() == View.VISIBLE) {
                layout_emo.setVisibility(View.GONE);
                layout_add.setVisibility(View.VISIBLE);
            } else {
                layout_more.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.btn_chat_voice)
    public void onVoiceClick(View view) {
        edit_msg.setVisibility(View.GONE);
        layout_more.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.GONE);
        btn_chat_keyboard.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.VISIBLE);
        hideSoftInputView();
    }

    @OnClick(R.id.btn_chat_keyboard)
    public void onKeyClick(View view) {
        showEditState(false);
    }

    /**
     * btn_chat_send按钮点击事件
     * 发送消息
     */
    public void onSendClick(View view) {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            toast("尚未连接IM服务器");
            return;
        }
        sendMessage();
    }

    @OnClick(R.id.tv_picture)
    public void onPictureClick(View view) {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            toast("尚未连接IM服务器");
            return;
        }
        sendLocalImageMessage();
    }

    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     *
     * @param isEmo 用于区分文字和表情
     * @return void
     */
    private void showEditState(boolean isEmo) {
        edit_msg.setVisibility(View.VISIBLE);
        btn_chat_keyboard.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        edit_msg.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(View.GONE);
            showSoftInputView();
        }
    }


    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(edit_msg, 0);
        }
    }

    /**
     * 发送文本消息
     */
    private void sendMessage() {
        String text = edit_msg.getText().toString();
        if (TextUtils.isEmpty(text.trim())) {
            Toast.makeText (VicApplication.getInstance(),"请输入内容",Toast.LENGTH_LONG ).show ();
            return;
        }
        //发送文本消息
        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(text);
        //可随意设置额外信息
        Map<String, Object> map = new HashMap<>();
        map.put("level", "1");
        msg.setExtraMap(map);
        msg.setExtra("OK");
        mConversationManager.sendMessage(msg, listener);
    }

    /**
     * 发送本地图片文件
     */
    public void sendLocalImageMessage() {
        //TODO 发送消息：6.2、发送本地图片消息
        //正常情况下，需要调用系统的图库或拍照功能获取到图片的本地地址，开发者只需要将本地的文件地址传过去就可以发送文件类型的消息
        BmobIMImageMessage image = new BmobIMImageMessage("/storage/emulated/0/netease/cloudmusic/网易云音乐相册/小梦大半_1371091013186741.jpg");
        mConversationManager.sendMessage(image, listener);
    }

    /**
     * 初始化
     */
    private void initSwipeLayout() {
        //sw_refresh.setRefreshing(false);
        //sw_refresh.setEnabled(false);
        layoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(this, mConversationManager);      //传入ConversationManager用以重发
        rc_view.setAdapter(adapter);
        ll_chat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_chat.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                //自动刷新
                queryMessages(null);
                //getLocalMessage();
            }
        });
        //下拉加载
        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage msg = adapter.getFirstMessage();
                queryMessages(msg);
                debugToast("正在刷新");
            }
        });

        //设置RecyclerView的点击事件
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                debugToast("点击：" + position);
                //Logger.i("" + position);
            }

            @Override
            public boolean onItemLongClick(int position) {
                //长按删除指定聊天消息
                mConversationManager.deleteMessage(adapter.getItem(position));
                adapter.remove(position);
                return true;
            }
        });
    }

    /**
     * 首次加载，可设置msg为null，下拉刷新的时候，默认取消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
     *
     * @param msg
     */
    public void queryMessages(BmobIMMessage msg) {
        if(msg!=null){
            debugToast("msg.getContent(): "+msg.getContent());
        }
        // 查询指定会话的消息记录
        mConversationManager.queryMessages(msg, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                //debugToast("done");
                sw_refresh.setRefreshing(false);
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        adapter.addMessages(list);
                        layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                        //debugToast("Message get");
                    }
                } else {
                    toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }
//
//    void getLocalMessage(){
//        List<BmobIMConversation> list =BmobIM.getInstance().loadAllConversation();
//        if(list!=null && list.size()>0){
//            for (BmobIMConversation item:list){
//                switch (item.getConversationType()){
//                    case 1://私聊
//                        item.getConversationId();
//                        item.getConversationType();
//                        //debugToast("item.getConversationId: "+item.getConversationId());
//
//
//
//                        //conversationList.add(new PrivateConversation(item));
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//
//    }

    private void toast(CharSequence text){
        Toast.makeText (VicApplication.getInstance(),text,Toast.LENGTH_LONG ).show ();
    }

    /**
     * 滑动到最底端
     */
    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
    }

    /**
     * 在当前页面接收消息
     * @param list
     */
    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        for(MessageEvent event:list){
            addMessageToChat(event);
        }
    }

    /**
     * 将消息添加到当前页面
     * @param event
     */
    private void addMessageToChat(MessageEvent event){
        BmobIMMessage msg = event.getMessage();
        //如果是当前会话的消息
        if(event != null){

            if (mConversationManager != null && mConversationManager.getConversationId().equals(event.getConversation().getConversationId())
                    && !msg.isTransient()) {    //并且不为暂态消息
                if (adapter.findPosition(msg) < 0) {    //如果未添加到界面中
                    adapter.addMessage(msg);
                    //更新该会话下面的已读状态
                    mConversationManager.updateReceiveStatus(msg);
                }
                scrollToBottom();
            }
            else {
                debugToast("不是与当前聊天对象的消息");
                //Logger.i("不是与当前聊天对象的消息");
            }
        }
    }


}
