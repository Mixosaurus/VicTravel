package cn.vic.travel.localdata;

import android.arch.persistence.room.*;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * Snake 创建于 2018/5/1.
 * 本地用户信息实体类
 */
@Entity(tableName = "LocalUser")        //表名与类名相同
public class LocalUser {
    private String name;
    @PrimaryKey @NonNull//(autoGenerate = true)
    private String phoneNumber;
    private String password;
    private String age;
    private String gender;

    //private BitmapDrawable photo;
    //private File photo;

    public LocalUser(String name, String phoneNumber, String password) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAge() {
        return age;
    }
    public void setAge(String age) {
        this.age = age;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }


}
