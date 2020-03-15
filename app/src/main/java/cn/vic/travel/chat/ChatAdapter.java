package cn.vic.travel.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.v3.BmobUser;
import cn.vic.travel.R;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/6/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    //语音
    private final int TYPE_SEND_VOICE =6;
    private final int TYPE_RECEIVER_VOICE = 7;
    //视频
    private final int TYPE_SEND_VIDEO =8;
    private final int TYPE_RECEIVER_VIDEO = 9;

    //同意添加好友成功后的样式
    private final int TYPE_AGREE = 10;

    /**
     * 显示时间间隔:10分钟
     */
    private final long TIME_INTERVAL = 10 * 60 * 1000;

    private List<BmobIMMessage> msgs = new ArrayList<>();

    private OnRecyclerViewListener onRecyclerViewListener;

    private String currentUid;       //当前用户ID，用以区分消息是否由自身发出
    BmobIMConversation conversation;

    public ChatAdapter(Context context,BmobIMConversation c) {
        try {
            currentUid = BmobIM.getInstance().getCurrentUid();
            //currentUid = BmobUser.getCurrentUser().getObjectId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.conversation =c;
    }
    public int findPosition(BmobIMMessage message) {
        int index = this.getCount();
        int position = -1;
        while(index-- > 0) {
            if(message.equals(this.getItem(index))) {
                position = index;
                break;
            }
        }
        return position;
    }

    public int findPosition(long id) {
        int index = this.getCount();
        int position = -1;
        while(index-- > 0) {
            if(this.getItemId(index) == id) {
                position = index;
                break;
            }
        }
        return position;
    }

    public int getCount() {
        return this.msgs == null?0:this.msgs.size();
    }

    public void addMessages(List<BmobIMMessage> messages) {
        msgs.addAll(0, messages);
        notifyDataSetChanged();
    }

    public void addMessage(BmobIMMessage message) {
        msgs.addAll(Arrays.asList(message));
        notifyDataSetChanged();
    }

    /**获取消息
     * @param position
     * @return
     */
    public BmobIMMessage getItem(int position){
        return this.msgs == null?null:(position >= this.msgs.size()?null:this.msgs.get(position));
    }

    /**移除消息
     * @param position
     */
    public void remove(int position){
        msgs.remove(position);
        notifyDataSetChanged();
    }

    /**
     *  查询第一条消息
     * @return 第一条消息
     */
    public BmobIMMessage getFirstMessage() {
        if (null != msgs && msgs.size() > 0) {
            return msgs.get(0);
        } else {
            return null;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SEND_TXT) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent_message,parent,false);
            return new SendTextHolder(parent.getContext(),conversation,onRecyclerViewListener,view);
        }
        else if (viewType == TYPE_RECEIVER_TXT) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received_message,parent,false);
            return new ReceiveTextHolder(parent.getContext(),onRecyclerViewListener,view);
        }
        else{//开发者自定义的其他类型，可自行处理
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder)holder).bindData(msgs.get(position));
        if (holder instanceof ReceiveTextHolder) {
            ((ReceiveTextHolder)holder).showTime(shouldShowTime(position));
        }
        else if (holder instanceof SendTextHolder) {
            ((SendTextHolder)holder).showTime(shouldShowTime(position));
        }

    }

    @Override
    public int getItemViewType(int position) {

        BmobIMMessage message = msgs.get(position);
        if(message.getMsgType().equals(BmobIMMessageType.TEXT.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
        }
        else if(message.getMsgType().equals("agree")) {//显示欢迎
            return TYPE_AGREE;
        }
        else{
            return -1;
        }
    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    //设置RecyclerView监听器
    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private boolean shouldShowTime(int position) {
        if (position == 0) {
            return true;
        }
        long lastTime = msgs.get(position - 1).getCreateTime();
        long curTime = msgs.get(position).getCreateTime();
        return curTime - lastTime > TIME_INTERVAL;
    }

}
