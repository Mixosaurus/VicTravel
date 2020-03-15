package cn.vic.travel.network;

import java.util.List;
import static cn.vic.travel.Utils.*;
import static cn.vic.travel.localdata.Flag.EARTH_RADIUS;

/**
 * Snake 创建于 2018/5/9.
 */

public class ProcessInfo {
    /**
     * 以直线距离排序传入List，从小到大
     * TODO: 2018/5/19 待优化
     * @param list
     * @param selfLongitude 自身经度
     * @param selfLatitude 自身纬度
     */
    public void SortList( List<Point> list,double selfLongitude, double selfLatitude){
        Point point;
        for (int i=0;i<list.size();i++) {
            for(int j=i+1;j<list.size();j++)
            {
                double d1= getDistance(list.get(i).getTip_point().getLongitude(),list.get(i).getTip_point().getLatitude(),selfLongitude,selfLatitude);
                double d2= getDistance(list.get(j).getTip_point().getLongitude(),list.get(j).getTip_point().getLatitude(),selfLongitude,selfLatitude);
                if(d1>d2)
                {
                    point= list.get(i);
                    list.set(i,list.get(j));
                    list.set (j,point);
                }
            }
        }
    }

    /**
     * 利用地球半径计算距离
     * @param otherLongitude 贴士经度
     * @param otherLatitude 贴士纬度
     * @param selfLongitude 自身经度
     * @param selfLatitude 自身纬度
     * @return 距离
     */
    private double getDistance(double otherLongitude, double otherLatitude, double selfLongitude, double selfLatitude) {
        class distanceCalculateUtil{
            private double rad(double d) {
                return d * Math.PI / 180;
            }
        }
        double a, b, sa2, sb2;
        otherLatitude = new distanceCalculateUtil().rad(otherLatitude);
        selfLatitude = new distanceCalculateUtil().rad(selfLatitude);
        a = otherLatitude - selfLatitude;
        b = new distanceCalculateUtil().rad(otherLongitude - selfLongitude);
        sa2 = Math.sin(a/ 2.0);
        sb2 = Math.asin(b / 2);
        return 2 * EARTH_RADIUS* Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(otherLatitude)* Math.cos(selfLatitude) * sb2 * sb2))*1000;
    }

    /**
     * 获取贴士内容
     */
    public static String getTipContent( List<Point> list,String name,String coord){
        String coordStr[]=coord.split(",");
        double tipLongitude=Double.parseDouble(coordStr[0]);        //贴士经度
        double tipLatitude=Double.parseDouble(coordStr[1]);         //贴士纬度
        for (Point p:list) {
            double listLongitude=p.getTip_point().getLongitude();
            double listLatitude=p.getTip_point().getLatitude();
            //若贴士坐标与链表中存储的贴士坐标相等，且用户名相等
            if(name.equals(p.getUserName())&&tipLongitude==listLongitude&&tipLatitude==listLatitude){
                return p.getTip();
            }
        }
        return null;
    }
}
