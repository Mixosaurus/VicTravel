package cn.vic.travel.chat;

/**
 * Snake 创建于 2018/6/18.
 */

public class ImageLoaderFactory {
    private static volatile ILoader sInstance;

    private ImageLoaderFactory() {}

    public static ILoader getLoader() {
        if (sInstance == null) {
            synchronized (ImageLoaderFactory.class) {
                if (sInstance == null) {
                    sInstance = new UniversalImageLoader();
                }
            }
        }
        return sInstance;
    }
}
