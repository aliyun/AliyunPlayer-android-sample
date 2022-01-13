package com.aliyun.preloadandcache;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.source.UrlSource;
import com.aliyun.playerview.java.PlayerView;
import com.aliyun.playerview.java.RenderType;
import com.aliyun.preloadandcache.utils.Utils;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";

    private static final String VIDEO_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";

    private static final int REQUEST_PERMISSION_STORAGE = 0x0001;

    //cache file path
    public static String CACHE_DIR;

    private long mStartTime;
    private PlayerView mPlayerView;
    private AliPlayer mAliPlayer;
    private SwitchCompat mEnableCacheSwitch;
    private TextView mDeleteCacheFileTextView;
    private ImageView mRevertImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermissionAndCacheFile();
        initView();
        initPlayer();
        initPlayerListener();

        ConfigHelper.initLocalCacheAndMediaLoader(CACHE_DIR);

        setUrlDataSource();
    }

    private void initPermissionAndCacheFile() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        }

        CACHE_DIR = getExternalCacheDir().getAbsolutePath() + File.separator + "Preload";
        Log.e(TAG, "cache_dir_path : " + CACHE_DIR);
    }

    private void initView(){
        mPlayerView = findViewById(R.id.playerView);

        mRevertImageView = findViewById(R.id.iv_revert);
        mEnableCacheSwitch = findViewById(R.id.switch_enable_cache);
        mDeleteCacheFileTextView = findViewById(R.id.tv_delete);

        mRevertImageView.setOnClickListener(this);
        mDeleteCacheFileTextView.setOnClickListener(this);
        mEnableCacheSwitch.setOnCheckedChangeListener(this);
    }

    private void initPlayer(){
        mAliPlayer = AliPlayerFactory.createAliPlayer(this);
        mPlayerView.bind(mAliPlayer, RenderType.TEXTURE_VIEW);

        mAliPlayer.setAutoPlay(true);
    }

    private void initPlayerListener(){
        mAliPlayer.setOnRenderingStartListener(() -> {

            long detailTime = System.currentTimeMillis() - mStartTime;
            Toast.makeText(this, "duration : " + detailTime, Toast.LENGTH_SHORT).show();

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
        //prepare
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);
        mStartTime = System.currentTimeMillis();
        mAliPlayer.prepare();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == mEnableCacheSwitch){
            ConfigHelper.enableCache(isChecked,VIDEO_URL);
        }
    }

    @Override
    public void onClick(View v) {
        if(mDeleteCacheFileTextView == v){
            Utils.deleteCacheFile(new File(CACHE_DIR));
            Toast.makeText(this, "CacheFile Delete", Toast.LENGTH_SHORT).show();
        } else if(mRevertImageView == v){
            setUrlDataSource();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConfigHelper.cancelMediaLoader(VIDEO_URL);
        mPlayerView.isPlaying(false);
        mAliPlayer.stop();
        mAliPlayer.release();
    }
}