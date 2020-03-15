package cn.vic.travel.conversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMConversationType;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.vic.travel.R;
import cn.vic.travel.chat.ChatActivity;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/8/11 09:56
 * 私聊，与群聊区分
 */
public class PrivateConversation extends Conversation {


    private BmobIMConversation conversation;
    private BmobIMMessage lastMsg;
    private BmobIMUserInfo receiverInfo =new BmobIMUserInfo();      //消息接收者信息

    public BmobIMUserInfo getReceiverInfo() {
        return receiverInfo;
    }

    public BmobIMConversation getConversation() {
        return conversation;
    }

    public PrivateConversation(BmobIMConversation conversation) {
        this.conversation = conversation;
        conversationType = BmobIMConversationType.setValue(conversation.getConversationType());
        conversationId = conversation.getConversationId();
        receiverInfo=conversation.getMessages().get(0).getBmobIMUserInfo();     //从第一条消息中获取接收者信息

        if (conversationType == BmobIMConversationType.PRIVATE) {
            conversationName =conversation.getConversationTitle();
            if (TextUtils.isEmpty(conversationName)) conversationName = conversationId;
        }
        else{
            conversationName ="未知会话";
        }
        List<BmobIMMessage> msgs =conversation.getMessages();
        if(msgs!=null && msgs.size()>0){
            lastMsg =msgs.get(0);
        }
    }

    @Override
    public void readAllMessages() {
        conversation.updateLocalCache();
    }

    @Override
    public Object getAvatar() {
        if (conversationType == BmobIMConversationType.PRIVATE){
            String avatar =  conversation.getConversationIcon();
            if (TextUtils.isEmpty(avatar)){//头像为空，使用默认头像
                return R.mipmap.head;
            }else{
                return avatar;
            }
        }else{
            return R.mipmap.head;
        }
    }

    @Override
    public String getLastMessageContent() {
        if(lastMsg!=null){
            String content =lastMsg.getContent();
            if(lastMsg.getMsgType().equals(BmobIMMessageType.TEXT.getType()) || lastMsg.getMsgType().equals("agree")){
                return content;
            }else if(lastMsg.getMsgType().equals(BmobIMMessageType.IMAGE.getType())){
                return "[图片]";
            }else if(lastMsg.getMsgType().equals(BmobIMMessageType.VOICE.getType())){
                return "[语音]";
            }else if(lastMsg.getMsgType().equals(BmobIMMessageType.LOCATION.getType())){
                return"[位置]";
            }else if(lastMsg.getMsgType().equals(BmobIMMessageType.VIDEO.getType())){
                return "[视频]";
            }else{//开发者自定义的消息类型，需要自行处理
                return "[未知]";
            }
        }else{//防止消息错乱
            return "";
        }
    }

    @Override
    public long getLastMessageTime() {
        if(lastMsg!=null) {
            return lastMsg.getCreateTime();
        }else{
            return 0;
        }
    }

    @Override
    public int getUnReadCount() {
        // 查询指定会话下的未读消息数
        return (int) BmobIM.getInstance().getUnReadCount(conversation.getConversationId());
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("c", conversation);
        if (bundle != null) {
            intent.putExtra(context.getPackageName(), bundle);
        }
        context.startActivity(intent);
    }

    @Override
    public void onLongClick(Context context) {
        //TODO 会话：4.5、删除会话，以下两种方式均可以删除会话
        //BmobIM.getInstance().deleteConversation(conversation.getConversationId());
        BmobIM.getInstance().deleteConversation(conversation);
    }
}
