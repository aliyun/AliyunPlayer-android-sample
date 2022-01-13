package com.aliyun.preloadandcache;

import com.aliyun.loader.MediaLoader;
import com.aliyun.player.AliPlayerGlobalSettings;
import com.aliyun.preloadandcache.utils.Utils;

public class ConfigHelper {

    //load duration (ms)
    private static final int PRELOAD_PRELOAD_DURATION_MS = 5 * 1000;
    //max buffer memory (kb)
    public static final int CACHE_MEMORY_SIZE = 10 * 1024;

    //expire time (min)
    public static final int CACHE_EXPIRED_TIME = 30 * 24 * 60;
    //max capacity size (M)
    public static final int CACHE_SIZE = 20 * 1024;
    //free storage size (M)
    public static final int CACHE_FREE_STORAGE_SIZE = 0;
    private static MediaLoader mMediaLoader;
    private static String mCacheDir;

    public static void initLocalCacheAndMediaLoader(String cacheDir){
        initLocalCache(cacheDir);
        initMediaLoader();
    }

    private static void initLocalCache(String cacheDir) {
        mCacheDir = cacheDir;
        AliPlayerGlobalSettings.enableLocalCache(false,CACHE_MEMORY_SIZE,cacheDir);
        AliPlayerGlobalSettings.setCacheFileClearConfig(CACHE_EXPIRED_TIME,CACHE_SIZE,CACHE_FREE_STORAGE_SIZE);
        AliPlayerGlobalSettings.setCacheUrlHashCallback(s -> {
            String result = s;
            if (s.contains("?")) {
                String[] split = s.split("\\?");
                result = split[0];
            }
            return Utils.stringToMD5(result);
        });
    }

    private static void initMediaLoader() {
        mMediaLoader = MediaLoader.getInstance();
        mMediaLoader.setOnLoadStatusListener(new MediaLoader.OnLoadStatusListener() {
            @Override
            public void onError(String url, int code, String msg) {
            }

            @Override
            public void onCompleted(String s) {
            }

            @Override
            public void onCanceled(String s) {
            }
        });
    }

    public static void enableCache(boolean enableCache,String url){
        AliPlayerGlobalSettings.enableLocalCache(enableCache,CACHE_MEMORY_SIZE,mCacheDir);
        if(enableCache && mMediaLoader != null){
            mMediaLoader.load(url,PRELOAD_PRELOAD_DURATION_MS);
        }
    }

    public static void cancelMediaLoader(String url){
        if(mMediaLoader != null){
            mMediaLoader.cancel(url);
        }
    }


}
