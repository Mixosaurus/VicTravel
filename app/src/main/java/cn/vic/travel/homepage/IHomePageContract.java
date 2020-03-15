package cn.vic.travel.homepage;

import cn.vic.travel.IBasePresenter;
import cn.vic.travel.IBaseView;

/**
 * Created by spark on 2018/4/22.
 */

public interface IHomePageContract {
    interface View extends IBaseView<Presenter> {

    }
    interface Presenter extends IBasePresenter {

    }
}
