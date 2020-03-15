package cn.vic.travel.writetip;
import cn.vic.travel.ResultCallBack;
/**
 * 数据处理类(Model层)
 * Snake 创建于 2018/4/26.
 */
public interface IWriteTipModel {
    /**
     * 获取地理位置并进行检查
     * @return
     */
    String getLocation() throws Exception;

    void doUploadTip(final ResultCallBack<String> callBack);

    void setTip(String tipContent);

    void startLocation() throws Exception ;

    void stopLocation();
}
