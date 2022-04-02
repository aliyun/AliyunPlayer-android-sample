package com.aliyun.pictureinpicture;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PictureInPictureParams;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.source.UrlSource;
import com.aliyun.playerview.java.PlayerView;
import com.aliyun.playerview.java.RenderType;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String VIDEO_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";

    private PlayerView mPlayerView;
    private AliPlayer mAliPlayer;
    private Rational mAspectRatio;
    private TextView mPictureInPictureTextView;
    private ActionBar mActionBar;

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
        mActionBar = getSupportActionBar();
        mPictureInPictureTextView = findViewById(R.id.tv_pip);
        mPlayerView = findViewById(R.id.playerView);
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

        mAliPlayer.setOnVideoSizeChangedListener(new IPlayer.OnVideoSizeChangedListener() {

            @Override
            public void onVideoSizeChanged(int width, int height) {
                mAspectRatio = new Rational(width, height);
            }
        });

        mAliPlayer.setOnErrorListener((errorInfo) -> Log.e(TAG, "onError: " + errorInfo.getCode() + " --- " + errorInfo.getMsg() ));

        mPictureInPictureTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSupport = getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE);
                if(isSupport){
                    enterPip();
                }else{
                    Toast.makeText(MainActivity.this, "I won't support Picture In Picture", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUrlDataSource(){
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);
        mAliPlayer.prepare();
    }

    private void enterPip(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mActionBar.hide();
            PictureInPictureParams mPipParams = new PictureInPictureParams.Builder()
                    .setAspectRatio(mAspectRatio)
                    .build();
            enterPictureInPictureMode(mPipParams);
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        mPictureInPictureTextView.setVisibility(isInPictureInPictureMode ? View.GONE : View.VISIBLE);
        if(!isInPictureInPictureMode){
            mActionBar.show();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.e(TAG, "onUserLeaveHint: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAliPlayer != null){
            mAliPlayer.release();
        }
    }
}