package cn.vic.travel.localdata;

import android.os.Environment;

/**
 * 字符串常量类
 * Snake 创建于 2018/6/7.
 */

public class Flag {
    public static final String debugDir= Environment.getExternalStorageDirectory()+"/"+"VicTravel"+"/";
    public static final String STORY="STORY";     //指示Unity3D，切换至故事场景
    public static final String SEEK_TIP="SEEK_TIP";      //指示Unity3D，切换至寻找纸条场景
    public static final String GUIDE="GUIDE";       //指示Unity3D，切换至向导场景
    public static final double EARTH_RADIUS=6378.137;     //地球半径
    public static final int REQUEST_CODE = 0x00000011;     //图片选择器请求码

    //时间格式
    public final static String FORMAT_TIME = "HH:mm";
    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_DATE_TIME_SECOND = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_MONTH_DAY_TIME = "MM-dd HH:mm";

}
