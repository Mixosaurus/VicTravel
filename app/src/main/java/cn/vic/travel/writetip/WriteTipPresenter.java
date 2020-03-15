package cn.vic.travel.writetip;
import android.app.Activity;

import cn.vic.travel.ResultCallBack;
import cn.vic.travel.network.VicApplication;

import static cn.vic.travel.Utils.*;
/**
 * Snake 创建于 2018/4/25.
 */
public class WriteTipPresenter implements IWriteTipContract.Presenter {
    private final IWriteTipContract.View mWriteTipActivity;
    private final IWriteTipModel mWriteTipModel;
    public WriteTipPresenter(IWriteTipContract.View view){
        mWriteTipActivity=view;
        mWriteTipModel=new WriteTipModel();
    }
    @Override
    public void uploadTip(String tipContent) {
        mWriteTipModel.setTip(tipContent);
        mWriteTipModel.doUploadTip(new ResultCallBack<String>() {
            @Override
            public void onSuccess(String promptInfo) {
                mWriteTipActivity.toast(promptInfo);
                VicApplication.shutActivity((Activity)mWriteTipActivity);       //关闭当前页面
            }

            @Override
            public void onFail(String errorInfo) {
                mWriteTipActivity.toast(errorInfo);
            }
        });
    }

    @Override
    public void startLocation() {
        try {
            mWriteTipModel.startLocation();
        } catch (Exception e) {
            debugToast(e.getMessage());
        }
    }
    // 销毁时结束定位
    @Override
    public void onViewDestory() {
        mWriteTipModel.stopLocation();
    }
}
