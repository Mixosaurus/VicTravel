package cn.vic.travel.writetip;
import cn.vic.travel.ResultCallBack;
import cn.vic.travel.network.NetUser;
import cn.vic.travel.network.Point;
import cn.vic.travel.network.VicApplication;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import static cn.vic.travel.Utils.*;

/**
 * Snake 创建于 2018/4/26.
 */
public class WriteTipModel implements IWriteTipModel{
    private String strTipContent;
    private VicApplication vicAppliction=VicApplication.getInstance();
    @Override
    public void setTip(String tipContent) {
        strTipContent=tipContent;
    }

    @Override
    public void startLocation() throws Exception {
        if(vicAppliction!=null){
            vicAppliction.startContinueLocation();      //开启定位
            debugToast("连续定位开启");
        }
        else{
            throw new Exception("Application is null");
        }
    }

    @Override
    public void stopLocation() {
        vicAppliction.stopContinueLocation();
    }

    @Override
    public String getLocation() throws Exception {
        if(vicAppliction!=null){
            return vicAppliction.getLocation();
        }
        else throw new Exception("VicAppliction is null");
    }

    @Override
    public void doUploadTip(final ResultCallBack<String> callBack) {
        try {
            if (strTipContent.isEmpty()) {
                callBack.onFail("内容不能为空");
                return;
            }
            NetUser user = BmobUser.getCurrentUser(NetUser.class);      //当前用户
            String userName=user.getUsername();
            String[] locationArray = getLocation().split(",");
            //debugToast("获取到的位置："+getLocation());
            //若位置信息存在异常
            if (locationArray[0].equals("!")) {
                //高德SDK错误码:
                switch(locationArray[1]){
                    case "高德SDK错误码:11":
                        callBack.onFail("基站信息错误，请检查SIM卡");
                        break;
                    case "高德SDK错误码:12":
                        callBack.onFail("缺少定位权限，请开启");
                        break;
                    case "高德SDK错误码:13":
                        callBack.onFail("基站信息错误，请检查SIM卡");
                        break;
                    case "高德SDK错误码:14":
                        callBack.onFail("GPS信号差，请稍后再试");
                        break;
                    case "高德SDK错误码:18":
                        callBack.onFail("请开启WIFI");
                        break;
                    default:
                        callBack.onFail("定位失败,请稍后重试");
                        debugToast(locationArray[1]);
                        break;
                }
            } else {
                double Longitude = Double.parseDouble(locationArray[0]);      //经度
                double Latitude = Double.parseDouble(locationArray[1]);       //纬度
                BmobGeoPoint tipPoint = new BmobGeoPoint(Longitude, Latitude);
                Point point = new Point(tipPoint, user, strTipContent,userName);
                point.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            callBack.onSuccess("发表成功");
                        } else {
                            callBack.onFail("发表失败");
                        }
                    }
                });
            }
        }
        // 字符串不能转换为合法的double类型值时,异常处理
        catch (NumberFormatException numberFormatException) {
            debugToast(numberFormatException.toString());
        }
        catch (Throwable t){
            debugToast(t.toString());
        }
    }
}
