package cn.vic.travel.network;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.WeakHashMap;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.vic.travel.ResultCallBack;
import cn.vic.travel.chat.IMMLeaks;
import cn.vic.travel.chat.IMMessageHandler;
import cn.vic.travel.chat.UniversalImageLoader;

import static cn.vic.travel.Utils.*;
/**
 * Snake 创建于 2018/4/23.
 * 用于初始化Bmob对象
 * 提供以下两种方式进行初始化操作：
 * 第一：设置BmobConfig，允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)
 * BmobConfig config =new BmobConfig.Builder(this)
 * .setApplicationId(APPID)     //设置appkey
 * .setConnectTimeout(30)       //请求超时时间（单位为秒）：默认15s
 * .setUploadBlockSize(1024*1024)       //文件分片上传时每片的大小（单位字节），默认512*1024
 * .setFileExpiration(5500)     //文件的过期时间(单位为秒)：默认1800s
 * .build();
 * Bmob.initialize(config);
 */

public class VicApplication extends MultiDexApplication {
    public static String APPID = "9cca55bb0435602905af0324d269ada7";
    private AMapLocationClient locationClientContinue = null;
    private static String strLocationInfo = "!,Location is empty";
    private static VicApplication instance;
    private String currentUserName;
    private static WeakHashMap<String,Activity> livingActivities=new WeakHashMap<String,Activity>();        //应用当前未被释放的Activities
    //开启Dex分割
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this) ;
    }

    //将activity加入列表
    public static void  addToActivities(String key,Activity activity){
        if(activity!=null){
            livingActivities.put(key,activity);
        }
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        try{
            Bmob.initialize(this, APPID);
            BmobIM.init(this);
            BmobIM.registerDefaultMessageHandler(new IMMessageHandler(this));
            UniversalImageLoader.initImageLoader(this);         //初始化图片加载器
        }
        catch (Throwable t){
            debugToast(t.toString());
            //log(t.toString());
        }
    }
    public String getLocation() {
        return strLocationInfo;
    }

    //返回全局Application实例
    public static VicApplication getInstance() {
        return instance;
    }

    //开启连续定位
    public void startContinueLocation(){
    if(null == locationClientContinue){
        locationClientContinue = new AMapLocationClient(this);
    }

    //使用连续的定位方式  默认连续
    AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
    // 地址信息
    locationClientOption.setNeedAddress(true);
    locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
    locationClientContinue.setLocationOption(locationClientOption);
    locationClientContinue.setLocationListener(locationContinueListener);
    locationClientContinue.startLocation();
}

    //单次客户端的定位监听
    AMapLocationListener locationContinueListener = new AMapLocationListener() {
        //定位回调监听，当定位完成后调用此方法
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (location.getErrorCode() == 0) {
                strLocationInfo=new StringBuffer()
                        .append(location.getLongitude())        //经度
                        .append(',')
                        .append(location.getLatitude())      //纬度
                        .toString();
                //UnityPlayer.UnitySendMessage("Canvas","GetString",strLocation);
            } else {
                strLocationInfo = "!,高德SDK错误码:" + location.getErrorCode();
            }
        }
    };

    /**
     * 获取附近的纸条
     * @param longitude 经度
     * @param latitude 纬度
     */
    public void getNearbyTips(final ResultCallBack<List<Point>> resultCallBack, final double longitude, final double latitude) {
        //当前用户坐标
        BmobGeoPoint origin=new BmobGeoPoint (longitude,latitude);
        BmobQuery<Point> query=new BmobQuery<Point> (  );
        final ProcessInfo processInfo=new ProcessInfo();
        query.addWhereWithinKilometers( "tip_point",origin,0.3000 );
        query.findObjects ( new FindListener<Point> () {
            @Override
            public void done(List<Point> list, BmobException e) {
                if(e==null){
                    //debugToast ( "查询成功，共"+list.size ()+"条数据");
                    processInfo.SortList(list,longitude,latitude);
                    resultCallBack.onSuccess(list);
                }
                else {
                    resultCallBack.onFail("查询失败："+e.toString());
                    //log(e.toString());
                }
            }
        });
    }

    /**
     * 上传图片
     * @param path 图片路径
     */
    public void uploadBtimap(String path) {
        final BmobFile bmobFile=new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                NetUser user= BmobUser.getCurrentUser(NetUser.class);
                user.setUser_photo(bmobFile);
                user.update(user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null) {
                            debugToast("上传图片成功！");
                        }
                        else {
                            debugToast("上传图片失败！"+e.getMessage());
                        }
                    }
                });
            }
        });
    }

    /**
     * 删除图片
     * @param path 图片路径
     */
    public void deleteBitmap(String path ) {
        BmobFile file=new BmobFile();
        file.setUrl(path);
        file.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                NetUser user= BmobUser.getCurrentUser(NetUser.class);
                user.remove("user_photo");
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null) {
                            debugToast("图片删除成功！");
                        }
                        else {
                            debugToast("图片删除失败！"+e.getErrorCode()+","+e.getMessage());
                        }
                    }
                });
            }
        });
    }

    /**
     * 0, "disconnect"
     * 1, "connecting"
     * 2, "connected"
     * -1, "Network is unavailable."
     * -2, "kick off by other user"
     */
    public void initIM(){
        final NetUser user = BmobUser.getCurrentUser(NetUser.class);
        //判断用户是否登录，并且连接状态不是已连接，则进行连接操作
        if (!TextUtils.isEmpty(user.getObjectId()) &&  BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            try{
                //连接Bmob服务器，暂时以用户名作为身份标识
                BmobIM.connect(user.getUsername(), new ConnectListener() {
                    @Override
                    public void done(String uid, BmobException e) {
                        //若服务器连接成功
                        if (e == null) {
                            // 更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                            BmobIMUserInfo userInfo=new BmobIMUserInfo(user.getObjectId(), user.getUsername(),null);
                            BmobIM.getInstance(). updateUserInfo(userInfo);
                            debugToast("IM服务器连接成功");
                            //EventBus.getDefault().post(new RefreshEvent());       //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                        }
                        //若服务器连接不成功
                        else {
                            debugToast("IM服务器连接不成功，"+e.getMessage());
                        }
                    }
                });
            }
            catch (Exception e){
                debugToast("连接异常："+e.toString());
            }

//            //监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
//            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
//                @Override
//                public void onChange(ConnectionStatus status) {
//                    debugToast("连接状态："+status.getMsg()+"code: "+status.getCode());
//                    //Logger.i(BmobIM.getInstance().getCurrentStatus().getMsg());
//                }
//            });
        }
        else{
            //BmobIM.getInstance().getCurrentStatus().getMsg();
        }

        //解决leancanary提示InputMethodManager内存泄露的问题
        //todo 暂时注释
        IMMLeaks.fixFocusedViewLeak(this);
    }

    //停止单次客户端
    public void stopContinueLocation(){
        if(null != locationClientContinue){
            locationClientContinue.stopLocation();
            //清除上次定位结果
            strLocationInfo = "!,Location is empty";
        }
    }

    //直接根据Activity引用关闭
    public static void shutActivity(Activity activity) {
        activity.finish();
    }

    public static void shutActivity(String activityName) {
        //livingActivities.
        if(livingActivities.containsKey(activityName)){
            livingActivities.get(activityName).finish();
            //activity.finish();
        }
    }

}


