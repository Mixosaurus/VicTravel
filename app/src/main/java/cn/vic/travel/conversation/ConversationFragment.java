package cn.vic.travel.conversation;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.v3.BmobUser;
import cn.vic.travel.R;
import cn.vic.travel.chat.ChatActivity;
import cn.vic.travel.chat.OnRecyclerViewListener;
import cn.vic.travel.widgets.SwipeItemLayout;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/5/28.
 * 消息列表展示页面
 */

public class ConversationFragment extends Fragment {

    RecyclerView recyclerView;
    SwipeRefreshLayout sw_refresh;
    ConversationAdapter mAdapter;
    LinearLayoutManager layoutManager;
    View rootView;

    private OnSwipeItemListener onSwipeItemListener=new OnSwipeItemListener() {
        @Override
        public void onItemClick(int position) {
            PrivateConversation conversation= (PrivateConversation)mAdapter.getConversation(position);      //获取conversation
            BmobIMUserInfo receiverInfo=conversation.getReceiverInfo();         //获取用户信息
            BmobIMConversation bmobIMConversation=conversation.getConversation();
            if(bmobIMConversation!=null){
                //debugToast("position: "+position+"receiver: "+receiverInfo.getUserId()+" ，Username: "+BmobUser.getCurrentUser().getUsername());
                chat("",receiverInfo.getUserId(),receiverInfo.getUserId() );
            }
        }
        @Override
        public void onItemDelete(int position){
            mAdapter.removeItem(position);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message, null);          //加载布局文件
        mAdapter=new ConversationAdapter();
        EventBus.getDefault().register(this);
        return rootView;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView=getActivity().findViewById(R.id.rc_view);
        sw_refresh=getActivity().findViewById(R.id.srl_conversation);

        //layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getActivity()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));
        setListener();
    }

    private void setListener() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                sw_refresh.setRefreshing(true);
                query();        //查询会话
            }
        });

        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
        mAdapter.setSwipeItemListener(onSwipeItemListener);
    }

    /**
     * 对话方法
     * @param receiverAvatar 接收者头像
     * @param receiverUserId 接收者Id
     * @param receiverName 接收者名
     */
    private void chat( String receiverAvatar,String receiverUserId,String receiverName ) {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            debugToast("尚未连接IM服务器");
            return;
        }

        BmobIMUserInfo receiverInfo = new BmobIMUserInfo();
        receiverInfo.setAvatar(receiverAvatar);
        receiverInfo.setUserId(receiverUserId);        //接收者的身份标识，暂设为用户名
        receiverInfo.setName(receiverName);
        // 创建一个常态会话入口，陌生人聊天
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(receiverInfo, null);
        Bundle bundle = new Bundle();
        bundle.putSerializable("conversationEntrance", conversationEntrance);

        Intent intent = new Intent();
        intent.setClass(getActivity(), ChatActivity.class);
        if (bundle != null) {
            intent.putExtra(getActivity().getPackageName(), bundle);
            intent.putExtra("tipOwnerName", receiverName);
            startActivity(intent);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public static ConversationFragment getInstance() {
        return new ConversationFragment();
    }

    /**
     查询本地会话
     */
    public void query(){
        mAdapter.addMessages(getConversations());
        sw_refresh.setRefreshing(false);
    }

    /**
     * 获取会话列表的数据：增加新朋友会话
     * @return
     */
    private List<Conversation> getConversations(){
        //添加会话
        List<Conversation> conversationList = new ArrayList<>();
        conversationList.clear();
        // 查询全部会话
        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        if(list!=null && list.size()>0){
            for (BmobIMConversation item:list){
                switch (item.getConversationType()){
                    case 1:     //私聊，与群聊区分
                        conversationList.add(new PrivateConversation(item));
                        break;
                    default:
                        break;
                }
            }
        }
        // 重新排序
        Collections.sort(conversationList);
        //debugToast("conversationList size: "+conversationList.size());
        return conversationList;
    }


//    /**注册自定义消息接收事件
//     * @param event
//     */
//    @Subscribe
//    public void onEventMainThread(RefreshEvent event){
//        log("---会话页接收到自定义消息---");
//        //因为新增`新朋友`这种会话类型
//        adapter.bindDatas(getConversations());
//        adapter.notifyDataSetChanged();
//    }

    /**
     * 注册离线消息接收事件
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(OfflineMessageEvent event){
        mAdapter.addMessages(getConversations());
    }

    /**
     * 注册消息接收事件
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event){
        mAdapter.addMessages(getConversations());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
