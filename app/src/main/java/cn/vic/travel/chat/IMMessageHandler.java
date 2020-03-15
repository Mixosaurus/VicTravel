package cn.vic.travel.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.exception.BmobException;
import cn.vic.travel.R;
import cn.vic.travel.homepage.HomePageActivity;
import cn.vic.travel.network.NetUser;

import static cn.vic.travel.Utils.debugToast;
import static cn.vic.travel.Utils.log;

/**
 * Snake 创建于 2018/7/25 09:27
 * 接收来自任何人的信息
 */
public class IMMessageHandler extends BmobIMMessageHandler {

    private Context context;

    public IMMessageHandler(Context context) {
        this.context = context;
    }


    @Override
    public void onMessageReceive(MessageEvent messageEvent) {
        super.onMessageReceive(messageEvent);
        executeMessage(messageEvent);
    }

    @Override
    public void onOfflineReceive(OfflineMessageEvent offlineMessageEvent) {
        super.onOfflineReceive(offlineMessageEvent);
        Map<String, List<MessageEvent>> map = offlineMessageEvent.getEventMap();
        //挨个检测下离线消息所属的用户的信息是否需要更新
        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list = entry.getValue();
            int size = list.size();
            for(MessageEvent event:list)
                executeMessage(event);      //处理每条消息
        }
    }

    private void executeMessage(final MessageEvent event) {
        //debugToast("全局接收到："+event.getMessage().getContent());
        //检测用户信息是否需要更新
        NetUser.getInstance().updateUserInfo(event, new UpdateCacheListener() {
            @Override
            public void done(BmobException e) {
                BmobIMMessage msg = event.getMessage();
                if (BmobIMMessageType.getMessageTypeValue(msg.getMsgType()) == 0) {
                    //自定义消息类型：0
                    processCustomMessage(msg, event.getFromUserInfo());
                } else {
                    //SDK内部内部支持的消息类型
                    processSDKMessage(msg, event);
                }
            }
        });

    }

    private void processCustomMessage(BmobIMMessage msg, BmobIMUserInfo fromUserInfo) {
        debugToast("自定义消息");
    }


    /**
     * 处理SDK支持的消息
     * 两种通知栏显示方式：
     * 1.多个用户的多条消息合并成一条通知：有XX个联系人发来了XX条消息
     * BmobNotificationManager.getInstance(context).showNotification(event, pendingIntent);
     * 2.始终只有一条通知，新消息覆盖旧消息
     * BmobNotificationManager.getInstance(context).showNotification(largeIcon,info.getName(), msg.getContent(), "您有一条新消息", pendingIntent);
     * @param msg
     * @param event
     */
    private void processSDKMessage(BmobIMMessage msg, MessageEvent event) {
        if (BmobNotificationManager.getInstance(context).isShowNotification()) {
            //如果需要显示通知栏，SDK提供以下两种显示方式：
            Intent pendingIntent = new Intent(context, HomePageActivity.class);
            pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            BmobIMUserInfo info = event.getFromUserInfo();
            //这里可以是应用图标，也可以将聊天头像转成bitmap
            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            BmobNotificationManager.getInstance(context).showNotification(largeIcon,info.getName(), msg.getContent(), "您有一条新消息", pendingIntent);
        }
        else {
            //直接发送消息事件
            EventBus.getDefault().post(event);
        }
    }

}
