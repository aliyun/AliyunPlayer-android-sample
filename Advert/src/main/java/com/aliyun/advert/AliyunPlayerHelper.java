package com.aliyun.advert;

import android.content.Context;
import android.view.SurfaceHolder;

import com.aliyun.advert.listener.CustomPlayerObserver;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;

import java.lang.ref.WeakReference;

/**
 *  Create two players (AdvPlayer and SourcePlayer)
 *  AdvPlayer play the BeginningVideo and EndingVideo
 *  SourcePlayer play the FeatureVideo
 */
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

        //set AdvPlayer startBuffer
        PlayerConfig config = mAdvPlayer.getConfig();
        config.mStartBufferDuration = 200;
        mAdvPlayer.setConfig(config);

        //set DataSource
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(ADV_URL);
        mAdvPlayer.setDataSource(urlSource);

        urlSource.setUri(VIDEO_URL);
        mSourcePlayer.setDataSource(urlSource);

        initListener();
    }

    public void setAdvDisplay(SurfaceHolder surfaceHolder){
        if(mAdvPlayer != null){
            mAdvPlayer.setDisplay(surfaceHolder);
        }
    }

    public void setSourceDisplay(SurfaceHolder surfaceHolder){
        if(mSourcePlayer != null){
            mSourcePlayer.setDisplay(surfaceHolder);
        }
    }

    public void advSurfaceChanged(){
        if(mAdvPlayer != null){
            mAdvPlayer.surfaceChanged();
        }
    }

    public void sourceSurfaceChanged(){
        if(mSourcePlayer != null){
            mSourcePlayer.surfaceChanged();
        }
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

    public void pauseAdv(){
        if(mAdvPlayer != null){
            mAdvPlayer.pause();
        }
    }

    public void pauseSource(){
        if(mSourcePlayer != null){
            mSourcePlayer.pause();
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

    public long getAdvDuration(){
        if(mAdvPlayer != null && mAdvPlayer.getMediaInfo() != null){
            return mAdvPlayer.getMediaInfo().getDuration();
        }
        return 0;
    }

    public long getSourceDuration(){
        if(mSourcePlayer!= null && mSourcePlayer.getMediaInfo() != null){
            return mSourcePlayer.getMediaInfo().getDuration();
        }
        return 0;
    }

    public void seekToAdv(int progress) {
        if(mAdvPlayer != null){
            mAdvPlayer.seekTo(progress);
        }
    }

    public void seekToSource(int progress) {
        if(mSourcePlayer != null){
            mSourcePlayer.seekTo(progress);
        }
    }

    public void setCustomPlayerObserver(CustomPlayerObserver customPlayerObserver){
        this.mCustomPlayerObserver = customPlayerObserver;
    }

    private boolean isAdvPlayer(AliPlayer aliplayer){
        return aliplayer == mAdvPlayer;
    }

    public void release() {
        if(mAdvPlayer != null){
            mAdvPlayer.release();
        }
        if(mSourcePlayer != null){
            mSourcePlayer.release();
        }
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
