package cn.vic.travel.guide;

import android.os.Parcel;

import com.amap.api.services.core.LatLonPoint;

/**
 * 经纬度坐标类，格式：经度,纬度
 * Snake 创建于 2018/6/22.
 */

public class LonLatPoint extends LatLonPoint {
    public LonLatPoint(double longitude, double latitude) {
        super(latitude, longitude);
    }

    protected LonLatPoint(Parcel parcel) {
        super(parcel);
    }
}
