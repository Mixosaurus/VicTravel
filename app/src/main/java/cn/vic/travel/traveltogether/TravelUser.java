package cn.vic.travel.traveltogether;

import cn.bmob.v3.BmobObject;
import cn.vic.travel.network.NetUser;

/**
 * Created by 15508 on 2018/8/10.
 */

public class TravelUser extends BmobObject {
    private String traveluser;//同行人名
    private String title;//标题
    private  String username;//发行人名
    private TravelInfo user;//关联

    public TravelUser(String traveluser, String title, String username, TravelInfo user) {
        this.traveluser = traveluser;
        this.title = title;
        this.username = username;
        this.user = user;
    }

    public TravelUser(String traveluser, String title, String username) {
        this.traveluser = traveluser;
        this.title = title;
        this.username = username;
    }

    public String getTraveluser() {
        return traveluser;
    }

    public void setTraveluser(String traveluser) {
        this.traveluser = traveluser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public TravelInfo getUser() {
        return user;
    }

    public void setUser(TravelInfo user) {
        this.user = user;
    }
}
