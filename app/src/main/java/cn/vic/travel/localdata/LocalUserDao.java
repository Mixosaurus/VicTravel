package cn.vic.travel.localdata;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import java.util.List;

/**
 * Snake 创建于 2018/5/1.
 * 实体(LocalUser)操作接口类
 */
@Dao
public interface LocalUserDao {

    //返回所有记录
    @Query("select * FROM LocalUser")
    List<LocalUser> getUserList();

    //删除记录
    @Delete()
    void deleteUser(LocalUser localUser);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUser(LocalUser localUser);


}
