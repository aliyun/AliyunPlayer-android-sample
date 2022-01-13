package com.aliyun.playerconfig;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.source.UrlSource;
import com.aliyun.playerview.java.PlayerView;
import com.aliyun.playerview.java.RenderType;
import com.aliyun.playerview.java.TimeFormater;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PlayActivity";
    private static final String VIDEO_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";

    private AliPlayer mAliPlayer;
    private PlayerView mPlayerView;
    private TextView mStopTextView;
    private TextView mPrepareTextView;
    private TextView mBufferPositionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        mAliPlayer = MainActivity.mAliPlayer;
        initView();
        initPlayerListener();
        setDataSource();
    }

    private void initView() {
        mPlayerView = findViewById(R.id.player_view);
        mPlayerView.bind(mAliPlayer, RenderType.SURFACE_VIEW);

        mStopTextView = findViewById(R.id.tv_stop);
        mPrepareTextView = findViewById(R.id.tv_prepare);
        mBufferPositionTextView = findViewById(R.id.tv_buffer_position);

        mStopTextView.setOnClickListener(this);
        mPrepareTextView.setOnClickListener(this);
    }

    private void initPlayerListener(){
        mAliPlayer.setOnRenderingStartListener(() -> {
            mPlayerView.isPlaying(true);
            mPlayerView.setDuration(mAliPlayer.getDuration());
        });

        mAliPlayer.setOnInfoListener(infoBean -> {
            if(infoBean.getCode() == InfoCode.BufferedPosition){
                long extraValue = infoBean.getExtraValue();
                mBufferPositionTextView.setText(getResources().getString(R.string.aliyun_player_config_buffer_position,TimeFormater.formatMs(extraValue)));
                mPlayerView.setBufferPosition(extraValue);
            }else if(infoBean.getCode() == InfoCode.CurrentPosition){
                mPlayerView.setCurrentPosition(infoBean.getExtraValue());
            }else if(infoBean.getCode() == InfoCode.NetworkRetry){
                Log.e(TAG, "NetworkRetry ");
            }
        });

        mAliPlayer.setOnLoadingStatusListener(new IPlayer.OnLoadingStatusListener() {
            @Override
            public void onLoadingBegin() {
                mPlayerView.isLoading(true);
            }

            @Override
            public void onLoadingProgress(int i, float v) {

            }

            @Override
            public void onLoadingEnd() {
                mPlayerView.isLoading(false);
            }
        });

        mAliPlayer.setOnErrorListener((errorInfo) -> Log.e(TAG, "onError: " + errorInfo.getCode() + " --- " + errorInfo.getMsg() ));
    }

    private void setDataSource(){
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);
        mAliPlayer.prepare();
    }

    @Override
    public void onClick(View v) {
        if(v == mStopTextView){
            mAliPlayer.stop();
        }else if(v == mPrepareTextView){
            mAliPlayer.setAutoPlay(true);
            setDataSource();
        }
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
}