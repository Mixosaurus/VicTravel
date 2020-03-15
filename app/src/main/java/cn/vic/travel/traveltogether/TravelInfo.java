package cn.vic.travel.traveltogether;

import java.sql.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.vic.travel.network.NetUser;

/**
 * Snake 创建于 2018/8/10 10:21
 */
public class TravelInfo extends BmobObject{
    private String title;          //标题
    private String departure;        //出发地
    private String destination;     //目的地
    private BmobDate startTime;        //出发时间
    private BmobDate endTime;         //结束时间
    private Number numberOfPeople;     //参加人数
    private Number maxNumberOfPeople;//总人数
    private String remarks;     //备注
    private String username;//发起人名字
    private NetUser user;//关联

    public TravelInfo(){ }

    public TravelInfo(String title, String departure, String destination, BmobDate startTime, BmobDate endTime,Number numberOfPeople,Number maxNumberOfPeople,  String remarks, String username, NetUser user) {
        this.title = title;
        this.departure = departure;
        this.destination = destination;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numberOfPeople=numberOfPeople;
        this.maxNumberOfPeople=maxNumberOfPeople;
        this.remarks = remarks;
        this.username = username;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public BmobDate getStartTime() {
        return startTime;
    }

    public void setStartTime(BmobDate startTime) {
        this.startTime = startTime;
    }

    public BmobDate getEndTime() {
        return endTime;
    }

    public void setEndTime(BmobDate endTime) {
        this.endTime = endTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public NetUser getUser() {
        return user;
    }

    public void setUser(NetUser user) {
        this.user = user;
    }

    public Number getMaxNumberofPeople() {
        return maxNumberOfPeople;
    }

    public void setMaxNumberofPeople(Number maxNumberOfPeople) {
        this.maxNumberOfPeople = maxNumberOfPeople;
    }

    public Number getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(Number numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    @Override
    public boolean equals(Object o) {
        TravelInfo otherItem = (TravelInfo)o;
        return otherItem.getTitle().equals(this.getTitle())&&
                otherItem.getUsername().equals(this.getUsername())&&
                otherItem.getDestination().equals(this.getDestination())&&
                otherItem.getDeparture().equals(this.getDeparture())&&
                otherItem.getRemarks().equals(this.getRemarks());
    }

}
