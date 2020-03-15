package cn.vic.travel;

/**
 * Snake 创建于 2018/4/26.
 */

public interface ResultCallBack<T> {
    void onSuccess(T info);
    void onFail(String errorInfo);
}
