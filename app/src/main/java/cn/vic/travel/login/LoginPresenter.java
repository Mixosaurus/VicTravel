package cn.vic.travel.login;
import android.os.Environment;
import android.text.TextUtils;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.vic.travel.chat.IMMLeaks;
import cn.vic.travel.localdata.FileUtil;
import cn.vic.travel.network.NetUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.vic.travel.network.VicApplication;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/4/22.
 * 初始化即時通訊
 */
public class LoginPresenter implements ILoginContract.Presenter {
    private ILoginContract.View mLoginView;
    private static CompositeSubscription mCompositeSubscription;    //管理所有的订阅
    private NetUser user;
    public LoginPresenter(ILoginContract.View view){
        mLoginView=view;
    }
    public void doLogin(String name, String password){
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(password)){
            mLoginView.toast("用户名或密码不能为空");
            return;
        }
        user=new NetUser(name,password);
        addSubscription(user.login ( new SaveListener<NetUser>() {
            @Override
            public void done(NetUser users, BmobException e) {
                if(e==null){
                    //获取当前用户名，储存至VicAppliction
                    VicApplication.getInstance().setCurrentUserName(BmobUser.getCurrentUser(NetUser.class).getUsername());
                    // TODO: 2018/6/9 创建图片存储路径，待修改
                    String testDir = Environment.getExternalStorageDirectory()+ "/TestImage/"+VicApplication.getInstance().getCurrentUserName()+"/";
                    new FileUtil().createFolder(testDir);
                    VicApplication.getInstance().initIM();      //初始化IM
                    //initIM()
                    mLoginView.changeToHomePageActivity();
                }
                else{
                    switch (e.getErrorCode()){
                        case 101:
                            mLoginView.toast("用户名或密码错误");
                            break;
                        case 9016:
                            mLoginView.toast("网络不可用");
                            break;
                        default :
                            debugToast(e.toString());
                            break;

                    }
                }
            }
        } ));
    }
    private void addSubscription(Subscription s) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(s);
    }

    /**
     * 在activity生命周期结束时取消订阅，解除对context的引用
     */
    @Override
    public void onViewDestory() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }



    /**
     * 初始化IM
     */
    private void initIM(){
        //判断用户是否登录，并且连接状态不是已连接，则进行连接操作
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            //连接Bmob服务器，暂时以用户名作为身份标识
        debugToast("initIM() called");
//        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
//            @Override
//            public void onChange(ConnectionStatus status) {
//                debugToast("连接状态："+status.getMsg());
//                //Logger.i(BmobIM.getInstance().getCurrentStatus().getMsg());
//            }
//        });

            BmobIM.connect(user.getUsername(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    //若服务器连接成功
                    if (e == null) {
                        // 更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                        BmobIMUserInfo userInfo=new BmobIMUserInfo(user.getObjectId(), user.getUsername(),null);
                        BmobIM.getInstance(). updateUserInfo(userInfo);

                        mLoginView.changeToHomePageActivity();

                        //EventBus.getDefault().post(new RefreshEvent());       //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                    }
                    //若服务器连接不成功
                    else {
                        debugToast("服务器连接不成功，"+e.getMessage());
                    }
                }
            });
//            //监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
                    debugToast("连接状态："+status.getMsg());
                    //Logger.i(BmobIM.getInstance().getCurrentStatus().getMsg());
                }
            });
        }
        else if (BmobIM.getInstance().getCurrentStatus().getCode() == ConnectionStatus.CONNECTED.getCode()){
            debugToast("已连接，直接转跳");
            mLoginView.changeToHomePageActivity();
        }

        //解决leancanary提示InputMethodManager内存泄露的问题
        //todo 暂时注释
        IMMLeaks.fixFocusedViewLeak(VicApplication.getInstance());
    }

}
