package cn.vic.travel.network;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by spark on 2018/4/14.
 *
 */

public class Point extends BmobObject {
    private BmobGeoPoint tip_point;
    private NetUser Point_user;
    private String tip;
    private String userName;

    @Override
    public String toString() {
        return new StringBuffer().append(tip_point.getLongitude()).append(',').append(tip_point.getLatitude()).toString();
    }

    public Point(BmobGeoPoint point, NetUser user, String tip){
        tip_point=point;
        Point_user=user;
        this.tip=tip;
    }
    public Point(BmobGeoPoint point, NetUser user, String tip,String userName){
        tip_point=point;
        Point_user=user;
        this.tip=tip;
        this.userName=userName;
    }

    public BmobGeoPoint getTip_point() {
        return tip_point;
    }

    public void setTip_point(BmobGeoPoint tip_point) {
        this.tip_point = tip_point;
    }

    public NetUser getPoint_user() {
        return Point_user;
    }

    public void setPoint_user(NetUser point_user) {
        Point_user = point_user;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
