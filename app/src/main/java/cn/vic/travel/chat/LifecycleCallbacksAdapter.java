package cn.vic.travel.chat;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * 避免实现所有生命周期回调方法
 * Snake 创建于 2018/6/14.
 */

public class LifecycleCallbacksAdapter implements Application.ActivityLifecycleCallbacks {
    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override public void onActivityStarted(Activity activity) {

    }

    @Override public void onActivityResumed(Activity activity) {

    }

    @Override public void onActivityPaused(Activity activity) {

    }

    @Override public void onActivityStopped(Activity activity) {

    }

    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override public void onActivityDestroyed(Activity activity) {

    }
}
