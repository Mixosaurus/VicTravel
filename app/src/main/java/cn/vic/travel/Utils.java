package cn.vic.travel;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.vic.travel.localdata.FileUtil;
import cn.vic.travel.network.VicApplication;

import static cn.vic.travel.localdata.Flag.*;

/**
 * Snake 创建于 2018/4/23.
 * 调试辅助工具类
 */

public class Utils {

    public static void debugToast(CharSequence text) {
        Toast.makeText (VicApplication.getInstance(),text,Toast.LENGTH_LONG ).show ();
    }

    public static void log(String msg) {
        Log.e("Log","==============================================================================================================================================================");
        Log.e("Log", msg);
    }
    //显示bmob后端云Throwable信息
    public static void showBmobThrowable(Throwable e) {
        Log.i("Bmob","==============================================================================================================================================================");
        if(e instanceof BmobException){
            Log.e("Bmob", "错误码："+((BmobException)e).getErrorCode()+",错误描述："+((BmobException)e).getMessage());
        }else{
            Log.e("Bmob", "错误描述："+e.getMessage());
        }
    }

    /**
     * 将调试信息写入文本文件
     * @param content 调试字符串
     */
    public static void saveLog(String content){
        new FileUtil().saveFile(content,debugDir ,"Log");
    }

    /**
     * 将SHA1码保存到文件，使用正则表达式匹配
     */
    public static void saveSHA1(boolean hasColon){
        String SHA1=new Utils().getSHA1();
        if(!SHA1.equals("SHA1获取失败")){
            if(hasColon){
                String regex = "(.{2})";
                SHA1=SHA1.replaceAll(regex, "$1:");
            }
            new FileUtil().saveFile(SHA1,debugDir ,"SHA1");
        }
        else{
            debugToast("SHA1获取失败");
        }
    }

    /**
     * 将像素转换为dp
     * @param px 像素
     * @return dp
     */
    public int convertToDp(float px){
        final float scale = VicApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * 将dp转换为像素
     * @param dp dp
     * @return 像素
     */
    public int convertToPx(float dp) {
        final float scale = VicApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dp * scale+0.5f);
    }

    /**
     * 获取以SHA1算法生成的数字签名
     * @return 数字签名
     */
    public String getSHA1(){
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = VicApplication.getInstance().getPackageManager().getPackageInfo(VicApplication.getInstance().getPackageName(), PackageManager.GET_SIGNATURES);
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

    public static String getTime(boolean hasYear,long time) {
        String pattern=FORMAT_DATE_TIME;
        if(!hasYear){
            pattern = FORMAT_MONTH_DAY_TIME;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(time));
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_TIME);
        return format.format(new Date(time));
    }

    public static String getChatTime(boolean hasYear,long timesamp) {
        long clearTime = timesamp;
        String result;
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(clearTime);
        int temp = Integer.parseInt(sdf.format(today)) - Integer.parseInt(sdf.format(otherDay));
        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(clearTime);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(clearTime);
                break;
            case 2:
                result = "前天 " + getHourAndMin(clearTime);
                break;
            default:
                result = getTime(hasYear,clearTime);
                break;
        }
        return result;
    }

    /**
     * 将BmobDate类型时间转换为String
     * @param date BmobDate类型时间
     * @param containDash 是否含有“-”
     * @return String类型的时间
     */
    public static String getTimeInString(BmobDate date,Boolean containDash) {
        String strTime=date.getDate ();
        String[] spltTime=strTime.split ( " " );
        String time=spltTime[0];
        String[] splttime=time.split ( "-" );
        int y = Integer.parseInt(splttime[0])-1900;
        int m = Integer.parseInt(splttime[1])-1;
        int d=Integer.parseInt ( splttime[2] );
        String strY=String.valueOf ( y );
        String strM=String.valueOf ( m );
        String strD=String.valueOf ( d );
        if(containDash){
            return strY+"-"+strM+"-"+strD;
        }
        else{
            return strY+strM+strD;
        }
    }


}
