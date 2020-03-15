package cn.vic.travel.localdata;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Snake 创建于 2018/5/1.
 * 本地数据库抽象类，可导出数据表
 */

@Database(entities = {LocalUser.class}, version = 2,exportSchema = false)
public abstract class VicDatabase extends RoomDatabase {
    private static VicDatabase sInstance;
    //获取单例
    public static VicDatabase getDatabase(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(), VicDatabase .class,"user.db").build();
        }
        return sInstance;
    }
    //销毁单例
    public static void onDestroy() {
        sInstance = null;
    }
    //返回数据库操作对象
    public abstract LocalUserDao getLocalUserDao();
}
