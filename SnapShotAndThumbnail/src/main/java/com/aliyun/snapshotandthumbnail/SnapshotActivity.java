package com.aliyun.snapshotandthumbnail;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.source.UrlSource;
import com.aliyun.playerview.java.PlayerView;
import com.aliyun.playerview.java.RenderType;

public class SnapshotActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SnapshotActivity";
    private static final String VIDEO_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";

    private boolean mInSnapshotting = false;
    private AliPlayer mAliPlayer;
    private PlayerView mPlayerView;
    private TextView mSnapshotTextView;
    private ImageView mSnapshotImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snapshot);

        initView();
        initPlayer();
        initPlayerListener();
        setUrlDataSource();
    }

    private void initView(){
        mPlayerView = findViewById(R.id.playerView);
        mSnapshotTextView = findViewById(R.id.tv_snapshot);
        mSnapshotImageView = findViewById(R.id.iv_snapshot);

        mSnapshotTextView.setOnClickListener(this);
    }

    private void initPlayer(){
        mAliPlayer = AliPlayerFactory.createAliPlayer(this);
        mPlayerView.bind(mAliPlayer, RenderType.TEXTURE_VIEW);

        mAliPlayer.setAutoPlay(true);
    }

    private void initPlayerListener(){
        mAliPlayer.setOnRenderingStartListener(() -> {
            mPlayerView.isPlaying(true);
            mPlayerView.setDuration(mAliPlayer.getDuration());
        });

        mAliPlayer.setOnSnapShotListener((bitmap, width, height) -> {
            mInSnapshotting = false;
            mSnapshotImageView.setImageBitmap(bitmap);
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

    @Override
    public void onClick(View v) {
        if(v == mSnapshotTextView && !mInSnapshotting){
            mAliPlayer.snapshot();
            mInSnapshotting = true;
        }
    }
}