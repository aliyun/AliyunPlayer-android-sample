package com.aliyun.memoryplay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.source.UrlSource;
import com.aliyun.playerview.java.PlayerView;
import com.aliyun.playerview.java.RenderType;

public class PlayActivity extends AppCompatActivity {

    private static final String TAG = "PlayActivity";
    private static final String VIDEO_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";

    private PlayerView mPlayerView;
    private AliPlayer mAliPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initView();
        initPlayer();
        initPlayerListener();
        setUrlDataSource();
    }

    private void initView(){
        mPlayerView = findViewById(R.id.playerView);
    }

    private void initPlayer(){
        mAliPlayer = AliPlayerFactory.createAliPlayer(this);
        mPlayerView.bind(mAliPlayer, RenderType.TEXTURE_VIEW);

        mAliPlayer.setAutoPlay(true);
    }

    private void initPlayerListener(){
        mAliPlayer.setOnRenderingStartListener(() -> {
            mPlayerView.setDuration(mAliPlayer.getDuration());
            mPlayerView.isPlaying(true);
        });

        mAliPlayer.setOnInfoListener(infoBean -> {
            if(infoBean.getCode() == InfoCode.BufferedPosition){
                long extraValue = infoBean.getExtraValue();
                mPlayerView.setBufferPosition(extraValue);

                //save position
                if(Global.ENABLE_MEMORY_PLAY){
                    Global.POSITION = extraValue;
                }

            }else if(infoBean.getCode() == InfoCode.CurrentPosition){
                long duration = mPlayerView.getDuration();
                long extraValue = infoBean.getExtraValue();

                //avoid seekBar jumping
                if(duration >= extraValue){
                    mPlayerView.setCurrentPosition(extraValue);
                }

            }
        });

        mAliPlayer.setOnLoadingStatusListener(new IPlayer.OnLoadingStatusListener() {
            @Override
            public void onLoadingBegin() {
                mPlayerView.isLoading(true);
            }

            @Override
            public void onLoadingProgress(int percent, float netSpeed) { }

            @Override
            public void onLoadingEnd() {
                mPlayerView.isLoading(false);
            }
        });

        mAliPlayer.setOnErrorListener((errorInfo) -> Log.e(TAG, "onError: " + errorInfo.getCode() + " --- " + errorInfo.getMsg() ));
    }

    private void setUrlDataSource(){
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);

        //seek before prepare
        if(Global.ENABLE_MEMORY_PLAY){
            mAliPlayer.seekTo(Global.POSITION);
        }
        mAliPlayer.prepare();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlayerView.isPlaying(true);
        mAliPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlayerView.isPlaying(false);
        mAliPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.isPlaying(false);
        mAliPlayer.stop();
        mAliPlayer.release();
    }
}