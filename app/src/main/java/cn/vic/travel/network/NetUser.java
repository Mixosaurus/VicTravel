package cn.vic.travel.network;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.vic.travel.chat.QueryUserListener;
import cn.vic.travel.chat.UpdateCacheListener;

/**
 * Snake 创建于 2018/4/14.
 * 网络用户信息实体类
 */

public class NetUser extends BmobUser {
    private BmobFile user_photo;

    private static NetUser userInstance = new NetUser();

    public static NetUser getInstance() {
        return userInstance;
    }

    public NetUser(){

    }

    public NetUser(String username, String password){
        super.setUsername(username);
        super.setPassword(password);
    }
    public NetUser(String name, String phoneNumber, String password){
        super.setUsername(name);
        //this.phone=phoneNumber;
        super.setMobilePhoneNumber(phoneNumber);
        super.setPassword(password);
    }

    public BmobFile getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(BmobFile user_photo) {
        this.user_photo = user_photo;
    }


    /**
     * TODO 用户管理：2.6、查询指定用户信息
     *
     * @param objectId
     * @param listener
     */
    public void queryUserInfo(String objectId, final QueryUserListener listener) {
        BmobQuery<NetUser> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", objectId);
        query.findObjects(
                new FindListener<NetUser>() {
                    @Override
                    public void done(List<NetUser> list, BmobException e) {
                        if (e == null) {

                            if (list != null && list.size() > 0) {
                                listener.done(list.get(0), null);
                            } else {
                                listener.done(null, new BmobException(000, "查无此人"));
                            }
                        } else {
                            listener.done(null, e);
                        }
                    }
                });
    }


    /**
     * 更新用户资料和会话资料
     *
     * @param event
     * @param listener
     */
    public void updateUserInfo(MessageEvent event, final UpdateCacheListener listener) {
        final BmobIMConversation conversation = event.getConversation();
        final BmobIMUserInfo info = event.getFromUserInfo();
        final BmobIMMessage msg = event.getMessage();
        String username = info.getName();
        String avatar = info.getAvatar();
        String title = conversation.getConversationTitle();
        String icon = conversation.getConversationIcon();
        //SDK内部将新会话的会话标题用objectId表示，因此需要比对用户名和私聊会话标题，后续会根据会话类型进行判断
        if (!username.equals(title) || (avatar != null && !avatar.equals(icon))) {
            NetUser.getInstance().queryUserInfo(info.getUserId(), new QueryUserListener() {
                @Override
                public void done(NetUser s, BmobException e) {
                    if (e == null) {
                        String name = s.getUsername();
                        //String avatar = s.getAvatar();
                        // TODO: 2018/7/29 无头像 
                        String avatar ="";
                        conversation.setConversationIcon(avatar);
                        conversation.setConversationTitle(name);
                        info.setName(name);
                        info.setAvatar(avatar);
                        //TODO 用户管理：2.7、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                        BmobIM.getInstance().updateUserInfo(info);
                        //TODO 会话：4.7、更新会话资料-如果消息是暂态消息，则不更新会话资料
                        if (!msg.isTransient()) {
                            BmobIM.getInstance().updateConversation(conversation);
                        }
                    } else {
                        //Logger.e(e);
                    }
                    listener.done(null);
                }
            });
        } else {
            listener.done(null);
        }
    }

}

