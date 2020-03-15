package cn.vic.travel.writetip;
/**
 * Snake 创建于 2018/4/25.
 */
public interface IWriteTipContract {
    interface View{
        /**
         * 以Toast显示错误信息
         * @param info 提示给用户的错误信息
         */
        void toast(CharSequence info);
        /**
         * 转跳到下一页()
         */
        void changeToHomePageActivity();
    }
    interface Presenter{
        void uploadTip(String tipContent);

        /**
         * 开启定位
         */
        void startLocation();
        /**
         * 在View对象生命周期结束时调用，释放资源
         */
        void onViewDestory();
    }
}
