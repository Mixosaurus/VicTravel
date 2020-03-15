package cn.vic.travel.network;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Snake 创建于 2018/6/10.
 */

public class UserPhoto {
    private String userName;
    private BmobFile userphoto;
    private NetUser netUser;

    public UserPhoto(String userName, BmobFile userphoto, NetUser netUser) {
        this.userName = userName;
        this.userphoto = userphoto;
        this.netUser = netUser;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public BmobFile getUserphoto() {
        return userphoto;
    }
    public void setUserphoto(BmobFile userphoto) {
        this.userphoto = userphoto;
    }
    public NetUser getNetUser() {
        return netUser;
    }
    public void setNetUser(NetUser netUser) {
        this.netUser = netUser;
    }


}
