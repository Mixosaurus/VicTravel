package cn.vic.travel.chat;

import cn.bmob.newim.listener.BmobListener1;
import cn.bmob.v3.exception.BmobException;

/**
 * Snake 创建于 2018/7/29 17:25
 */
public abstract class UpdateCacheListener  extends BmobListener1 {
    public abstract void done(BmobException e);

    @Override
    protected void postDone(Object o, BmobException e) {
        done(e);
    }
}
