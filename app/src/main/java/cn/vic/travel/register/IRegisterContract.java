package cn.vic.travel.register;

/**
 * Snake 创建于 2018/4/22.
 * 契约接口
 */
public interface IRegisterContract {
    interface View{
        /**
         * 以Toast显示错误信息
         * @param info 提示给用户的错误信息
         */
        void toast(CharSequence info);
        /**
         * 转跳到下一页(登陆页面)
         */
        void changeToLoginActivity(String name,String passWord);
    }
    interface Presenter{
        /**
         * 注册功能实现方法
         * @param name 用户名
         * @param phoneNumber 手机号
         * @param password 密码
         */
        void doRegister(String name,String phoneNumber,String password);
        /**
         * 在View对象生命周期结束时调用，释放资源
         */
        void onViewDestory();
    }
}
