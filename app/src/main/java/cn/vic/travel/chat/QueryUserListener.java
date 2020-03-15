package cn.vic.travel.chat;

import cn.bmob.newim.listener.BmobListener1;
import cn.bmob.v3.exception.BmobException;
import cn.vic.travel.network.NetUser;

/**
 * Snake 创建于 2018/7/29 17:30
 */
public abstract class QueryUserListener extends BmobListener1<NetUser> {

    public abstract void done(NetUser s, BmobException e);

    @Override
    protected void postDone(NetUser o, BmobException e) {
        done(o, e);
    }
}
