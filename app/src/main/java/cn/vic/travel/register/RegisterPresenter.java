package cn.vic.travel.register;
import cn.vic.travel.network.NetUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static cn.vic.travel.Utils.debugToast;

/**
 * Snake 创建于 2018/4/22.
 * Presenter实体类，持有View的引用
 */
public class RegisterPresenter implements IRegisterContract.Presenter {
    private final IRegisterContract.View mRegisterView;
    private static CompositeSubscription mCompositeSubscription;    //管理所有的订阅
    /**
     * @param view Avtivity的引用
     */
    public RegisterPresenter(IRegisterContract.View view) {
        mRegisterView = view;
    }

    /**
     *注册功能实现方法
     * @param name 用户名
     * @param phoneNumber 手机号
     * @param password 密码
     */
    @Override
    public void doRegister(final String name, String phoneNumber, final String password) {
        if(name.isEmpty () || phoneNumber.isEmpty () || password.isEmpty ()){
            //注册失败，提示用户
            mRegisterView.toast("请输入完整信息");
            return;
        }
        NetUser myUser = new NetUser(name,phoneNumber,password);
        addSubscription(myUser.signUp(new SaveListener<NetUser>() {
            @Override
            public void done(NetUser myUser, BmobException e) {
                if (e == null) {
                    mRegisterView.changeToLoginActivity(name,password);
                }
                //注册失败，提示用户
                else {
                    switch (e.getErrorCode()){
                        case 202:
                            mRegisterView.toast("用户名已被注册");
                            break;
                        case 9016:
                            mRegisterView.toast("网络不可用");
                            break;
                        case 301:
                            mRegisterView.toast("请填写正确的手机号");
                            break;
                        case 209:
                            mRegisterView.toast("手机号已存在");
                            break;
                        default :
                            debugToast(e.toString());
                            break;
                    }
                }
            }
        }));
    }

    /**
     * 解决Subscription内存泄露问题
     * @param s 订阅者
     */
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
}
