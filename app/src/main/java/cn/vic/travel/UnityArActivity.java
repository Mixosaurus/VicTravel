package cn.vic.travel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import cn.vic.travel.guide.GuideActivity;
import cn.vic.travel.homepage.HomePageActivity;
import cn.vic.travel.network.Point;
import cn.vic.travel.network.ProcessInfo;
import cn.vic.travel.network.VicApplication;
import cn.vic.travel.showtipdetail.TipDetailActivity;
import cn.vic.travel.writetip.WriteTipActivity;

import static cn.vic.travel.Utils.debugToast;
import static cn.vic.travel.Utils.log;
import static cn.vic.travel.network.VicApplication.shutActivity;

/**
 * Unity3D界面
 * Snake 创建于 2018/5/8.
 */

public class UnityArActivity extends UnityPlayerActivity {
    private Context mContext=null;
    private AMapLocationClient locationClientSingle = null;
    private VicApplication vicAppliction= VicApplication.getInstance();
    private List<Point> pointListlist;      //贴士信息链表
    private String sceneFlag;
    private String breakPointString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        sceneFlag=getIntent().getStringExtra("flag");       //获取内容指示
        //若存在拐点字符串
        if(getIntent().hasExtra("breakPointStr")){
            breakPointString=getIntent().getStringExtra( "breakPointStr");       //获取拐点字符串
        }

        //UnityPlayer.UnitySendMessage("AndroidAPI","getFlag",result);
        vicAppliction.startContinueLocation();       //开启定位
    }

    /**
     * 获取用户名数组
     * @return 用户名数组
     */
    public void getArray(){
        log("getArray() called");
        String[] locationArray = getLocation().split(",");
        vicAppliction.getNearbyTips(new ResultCallBack<List<Point>>(){
            @Override
            public void onSuccess(List<Point> list) {
                //数据最大长度
                int ArrayMaxSize=20;
                pointListlist=list;     //更新链表
                StringBuffer nameArray=new StringBuffer();
                StringBuffer coordArray=new StringBuffer();
                // TODO: 2018/5/19 待优化
                //若list长度小于数据长度
                if(list.size()<ArrayMaxSize){
                    for(int i=0;i<list.size();i++){
                        nameArray.append(list.get(i).getUserName()).append("~");
                        coordArray.append(list.get(i).toString()).append("~");
                    }
                }
                //若list长度大于数组长度
                else{
                    for(int i=0;i<ArrayMaxSize;i++){
                        nameArray.append(list.get(i).getUserName()).append("~");
                        coordArray.append(list.get(i).toString()).append("~");
                    }
                }
                UnityPlayer.UnitySendMessage("AndroidAPI","setNameArray", nameArray.toString());     //将用户名数组传入
                UnityPlayer.UnitySendMessage("AndroidAPI","setCoordArray", coordArray.toString());     //将坐标数组传入
            }
            @Override
            public void onFail(String errorInfo) {
                debugToast(errorInfo);
            }
        },Double.parseDouble(locationArray[0]),Double.parseDouble(locationArray[1]));
    }

    public void getTip(String location){

    }

    /**
     * Unity3D获取场景标记
     * @return 场景标记
     */
    public String getSceneFlag() {
        return sceneFlag;
    }

    /**
     * 转跳到写纸条页面
     */
    public void changeToWriteTipActivity(){
        startActivity(new Intent(mContext,WriteTipActivity.class));
    }

    /**
     * Unity3D获取拐点字符串
     * @return
     */
    public String getBreakPointString(){
        return breakPointString;
    }

    /**
     * 转跳到纸条详细信息页面
     */
    public void changeToTipDetailActivity(String nameAndCoord){
        log("changeToTipDetailActivity() called");
        String[] nameAndCoordString= nameAndCoord.split("~");   //分割字符串，格式：用户名~坐标
        String name=nameAndCoordString[0];      //用户名字符串
        String coord=nameAndCoordString[1];     //坐标字符串
        String tipContent=ProcessInfo.getTipContent(pointListlist,name,coord);        //贴士内容
        Bundle bundleName = new Bundle();
        Bundle bundleTipContent = new Bundle();
        bundleName.putString("userName", name);
        bundleTipContent.putString("tipContent",tipContent);
        if(tipContent!=null){
            startActivity(new Intent(mContext, TipDetailActivity.class).putExtras(bundleName).putExtras(bundleTipContent));
        }
        else{
            debugToast("无内容");
        }
    }

    /**
     * 显示对话框
     * @param mTitle 标题
     * @param mContent 内容
     */
    public void showDialog(final String mTitle,final String mContent){
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(UnityArActivity.this);
                mBuilder.setTitle(mTitle);
                mBuilder.setMessage(mContent);
                mBuilder.setPositiveButton("Check", null);
                mBuilder.show();
            }
        });
    }

    /**
     * 震动
     * @param mTime 持续时间
     */
    public void setVibrator(long mTime){
        Vibrator mVibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);
        if (null != mVibrator) {
            mVibrator.vibrate(mTime);
        }
    }

    /**
     * 显示Toast
     * @param mContent 内容
     */
    public void showToast(final String mContent){
        this.runOnUiThread(new Runnable(){
            @Override
            public void run(){
                Toast.makeText(mContext,mContent,Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO: 2018/4/24  Unity需进行异常处理
    public String getLocation(){
        //log("getLocation() called");
        //判断是Application引用是否为空
        if(vicAppliction!=null){
            return ((VicApplication)getApplication()).getLocation();
        }
        else{
            UnityPlayer.UnitySendMessage("AndroidAPI","GetToast","Application引用为空");
            return "VicAppliction is null";
        }
    }

    /**
     * 关闭Unity界面
     */
    public void shutUnityArActivity(){
        shutActivity((Activity)this);
    }

    /**
     * 转跳到向导页面
     */
    public void changeToGuideActivity(){
        startActivity(new Intent(mContext,GuideActivity.class));
    }

    /**
     * 返回主页
     */
    public void changeToHomePageActivity(){
        startActivity(new Intent(mContext,HomePageActivity.class));
    }

    public String getSHA1(){
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1"); byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (byte aPublicKey : publicKey) {
                String appendString = Integer.toHexString(0xFF & aPublicKey).toUpperCase(Locale.US);
                if (appendString.length() == 1) hexString.append("0");
                hexString.append(appendString);
            }
            return hexString.toString();
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "SHA1获取失败";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vicAppliction.stopContinueLocation();
    }

}
