package com.aliyun.backgroundplay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.source.UrlSource;
import com.aliyun.playerview.java.PlayerView;
import com.aliyun.playerview.java.RenderType;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";
    private static final String VIDEO_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";

    private PlayerView mPlayerView;
    private AliPlayer mAliPlayer;
    private Switch mEnableBackgroundPlaySwitch;
    private boolean mEnableBackgroundPlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initPlayer();
        initPlayerListener();
        setUrlDataSource();
    }

    private void initView(){
        mPlayerView = findViewById(R.id.playerView);
        mEnableBackgroundPlaySwitch = findViewById(R.id.enable_background_play_switch);
    }

    private void initPlayer(){
        mAliPlayer = AliPlayerFactory.createAliPlayer(this);
        mPlayerView.bind(mAliPlayer, RenderType.TEXTURE_VIEW);

        mAliPlayer.setAutoPlay(true);

        mEnableBackgroundPlaySwitch.setOnCheckedChangeListener(this);
    }

    private void initPlayerListener(){
        mAliPlayer.setOnRenderingStartListener(() -> {
            mPlayerView.isPlaying(true);
            mPlayerView.setDuration(mAliPlayer.getDuration());
        });

        mAliPlayer.setOnInfoListener(infoBean -> {
            if(infoBean.getCode() == InfoCode.BufferedPosition){
                mPlayerView.setBufferPosition(infoBean.getExtraValue());
            }else if(infoBean.getCode() == InfoCode.CurrentPosition){
                mPlayerView.setCurrentPosition(infoBean.getExtraValue());
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
        mAliPlayer.prepare();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mEnableBackgroundPlay = isChecked;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!mEnableBackgroundPlay){
            mPlayerView.isPlaying(true);
            mAliPlayer.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!mEnableBackgroundPlay){
            mPlayerView.isPlaying(false);
            mAliPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.isPlaying(false);
        mAliPlayer.stop();
        mAliPlayer.release();
    }
}