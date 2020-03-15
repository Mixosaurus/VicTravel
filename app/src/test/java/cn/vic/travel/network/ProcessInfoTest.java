package cn.vic.travel.network;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Snake 创建于 2018/5/19.
 */
public class ProcessInfoTest {
    @Test
    public void sortList(List<Point> list, double selfLongitude, double selfLatitude) throws Exception {
        Point point;
        for (int i=0;i<list.size();i++) {
            for (int j = i + 1; j < list.size(); j++) {
                double d1 = getOtherLongitude(list.get(i).getTip_point().getLongitude(), list.get(i).getTip_point().getLatitude(), selfLongitude, selfLatitude);
                double d2 = getOtherLongitude(list.get(j).getTip_point().getLongitude(), list.get(j).getTip_point().getLatitude(), selfLongitude, selfLatitude);
                if (d1 > d2) {
                    point = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, point);
                }
            }
        }
    }
    private double getOtherLongitude(double otherLongitude,double otherLatitude,double selfLongitude, double selfLatitude)
    {
        return Math.sqrt(Math.pow(selfLongitude-otherLongitude,2)+Math.pow(selfLatitude-otherLatitude,2));
    }
}