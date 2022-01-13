package com.aliyun.advert;

import android.content.Context;

import com.aliyun.advert.listener.CustomPlayerObserver;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.source.UrlSource;

import java.lang.ref.WeakReference;

public class AliyunPlayerHelper {

    public static final String VIDEO_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";
    public static final String ADV_URL = "https://alivc-demo-cms.alicdn.com/video/videoAD.mp4";

    private static AliyunPlayerHelper mInstance = null;
    private CustomPlayerObserver mCustomPlayerObserver;

    private AliPlayer mAdvPlayer;
    private AliPlayer mSourcePlayer;

    private AliyunPlayerHelper(){}

    public static AliyunPlayerHelper getInstance(){
        if(mInstance == null){
            synchronized (AliyunPlayerHelper.class){
                if(mInstance == null){
                    mInstance = new AliyunPlayerHelper();
                }
            }
        }
        return mInstance;
    }

    public void createPlayer(Context context){
        mAdvPlayer = AliPlayerFactory.createAliPlayer(context);
        mSourcePlayer = AliPlayerFactory.createAliPlayer(context);

        UrlSource urlSource = new UrlSource();
        urlSource.setUri(ADV_URL);
        mAdvPlayer.setDataSource(urlSource);

        urlSource.setUri(VIDEO_URL);
        mSourcePlayer.setDataSource(urlSource);

        initListener();
    }

    private void initListener(){
        mAdvPlayer.setOnInfoListener(new MyOnInfoListener(this,mAdvPlayer));
        mAdvPlayer.setOnErrorListener(new MyOnErrorListener(this,mAdvPlayer));
        mAdvPlayer.setOnPreparedListener(new MyOnPreparedListener(this,mAdvPlayer));
        mAdvPlayer.setOnCompletionListener(new MyOnCompletionListener(this,mAdvPlayer));
        mAdvPlayer.setOnRenderingStartListener(new MyOnRenderingStartListener(this,mAdvPlayer));

        mSourcePlayer.setOnInfoListener(new MyOnInfoListener(this,mSourcePlayer));
        mSourcePlayer.setOnErrorListener(new MyOnErrorListener(this,mSourcePlayer));
        mSourcePlayer.setOnPreparedListener(new MyOnPreparedListener(this,mSourcePlayer));
        mSourcePlayer.setOnCompletionListener(new MyOnCompletionListener(this,mSourcePlayer));
        mSourcePlayer.setOnRenderingStartListener(new MyOnRenderingStartListener(this,mSourcePlayer));
    }

    public void prepareAdv(){
        if(mAdvPlayer != null){
            mAdvPlayer.prepare();
        }
    }

    public void prepareSource(){
        if(mSourcePlayer != null){
            mSourcePlayer.prepare();
        }
    }

    public void startAdv(){
        if(mAdvPlayer != null){
            mAdvPlayer.start();
        }
    }

    public void startSource(){
        if(mSourcePlayer != null){
            mSourcePlayer.start();
        }
    }

    public void stopAdv(){
        if(mAdvPlayer != null){
            mAdvPlayer.start();
        }
    }

    public void stopSource(){
        if(mSourcePlayer != null){
            mSourcePlayer.stop();
        }
    }

    public void setCustomPlayerObserver(CustomPlayerObserver customPlayerObserver){
        this.mCustomPlayerObserver = customPlayerObserver;
    }

    private boolean isAdvPlayer(AliPlayer aliplayer){
        return aliplayer == mAdvPlayer;
    }

    // =================== Player Listener ===================
    private static class MyOnPreparedListener implements IPlayer.OnPreparedListener{

        private final AliPlayer mAliPlayer;
        private final WeakReference<AliyunPlayerHelper> weakReference;

        public MyOnPreparedListener(AliyunPlayerHelper helper,AliPlayer aliPlayer){
            this.mAliPlayer = aliPlayer;
            weakReference = new WeakReference<>(helper);
        }

        @Override
        public void onPrepared() {
            AliyunPlayerHelper aliyunPlayerHelper = weakReference.get();
            if(aliyunPlayerHelper != null){
                if(aliyunPlayerHelper.mCustomPlayerObserver != null){
                    if(aliyunPlayerHelper.isAdvPlayer(mAliPlayer)){
                        aliyunPlayerHelper.mCustomPlayerObserver.onAdvPrepared();
                    }else{
                        aliyunPlayerHelper.mCustomPlayerObserver.onSourcePrepared();
                    }
                }
            }
        }
    }

    private static class MyOnRenderingStartListener implements IPlayer.OnRenderingStartListener{

        private final AliPlayer mAliPlayer;
        private final WeakReference<AliyunPlayerHelper> weakReference;

        public MyOnRenderingStartListener(AliyunPlayerHelper helper,AliPlayer aliPlayer){
            this.mAliPlayer = aliPlayer;
            weakReference = new WeakReference<>(helper);
        }

        @Override
        public void onRenderingStart() {
            AliyunPlayerHelper aliyunPlayerHelper = weakReference.get();
            if(aliyunPlayerHelper != null){
                if(aliyunPlayerHelper.mCustomPlayerObserver != null){
                    if(aliyunPlayerHelper.isAdvPlayer(mAliPlayer)){
                        aliyunPlayerHelper.mCustomPlayerObserver.onAdvRenderingStart();
                    }else{
                        aliyunPlayerHelper.mCustomPlayerObserver.onSourceRenderingStart();
                    }
                }
            }
        }
    }

    private static class MyOnInfoListener implements IPlayer.OnInfoListener{

        private final AliPlayer mAliPlayer;
        private final WeakReference<AliyunPlayerHelper> weakReference;

        public MyOnInfoListener(AliyunPlayerHelper helper,AliPlayer aliPlayer){
            this.mAliPlayer = aliPlayer;
            weakReference = new WeakReference<>(helper);
        }

        @Override
        public void onInfo(InfoBean infoBean) {
            AliyunPlayerHelper aliyunPlayerHelper = weakReference.get();
            if(aliyunPlayerHelper != null){
                if(aliyunPlayerHelper.mCustomPlayerObserver != null){
                    if(aliyunPlayerHelper.isAdvPlayer(mAliPlayer)){
                        aliyunPlayerHelper.mCustomPlayerObserver.onAdvInfo(infoBean);
                    }else{
                        aliyunPlayerHelper.mCustomPlayerObserver.onSourceInfo(infoBean);
                    }
                }
            }
        }
    }

    private static class MyOnCompletionListener implements IPlayer.OnCompletionListener{

        private final AliPlayer mAliPlayer;
        private final WeakReference<AliyunPlayerHelper> weakReference;

        public MyOnCompletionListener(AliyunPlayerHelper helper,AliPlayer aliPlayer){
            this.mAliPlayer = aliPlayer;
            weakReference = new WeakReference<>(helper);
        }

        @Override
        public void onCompletion() {
            AliyunPlayerHelper aliyunPlayerHelper = weakReference.get();
            if(aliyunPlayerHelper != null){
                if(aliyunPlayerHelper.mCustomPlayerObserver != null){
                    if(aliyunPlayerHelper.isAdvPlayer(mAliPlayer)){
                        aliyunPlayerHelper.mCustomPlayerObserver.onAdvCompletion();
                    }else{
                        aliyunPlayerHelper.mCustomPlayerObserver.onSourceCompletion();
                    }
                }
            }
        }
    }

    private static class MyOnErrorListener implements IPlayer.OnErrorListener{

        private final AliPlayer mAliPlayer;
        private final WeakReference<AliyunPlayerHelper> weakReference;

        public MyOnErrorListener(AliyunPlayerHelper helper,AliPlayer aliPlayer){
            this.mAliPlayer = aliPlayer;
            weakReference = new WeakReference<>(helper);
        }

        @Override
        public void onError(ErrorInfo errorInfo) {
            AliyunPlayerHelper aliyunPlayerHelper = weakReference.get();
            if(aliyunPlayerHelper != null){
                if(aliyunPlayerHelper.mCustomPlayerObserver != null){
                    if(aliyunPlayerHelper.isAdvPlayer(mAliPlayer)){
                        aliyunPlayerHelper.mCustomPlayerObserver.onAdvError(errorInfo);
                    }else{
                        aliyunPlayerHelper.mCustomPlayerObserver.onSourceError(errorInfo);
                    }
                }
            }
        }
    }
}
