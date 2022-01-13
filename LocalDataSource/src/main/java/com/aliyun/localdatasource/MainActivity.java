package com.aliyun.localdatasource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.source.UrlSource;
import com.aliyun.playerview.java.PlayerView;
import com.aliyun.playerview.java.RenderType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_PERMISSION_STORAGE = 0x0001;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private File mVideoFile;
    private PlayerView mPlayerView;
    private AliPlayer mAliPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File mExternalFilesDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        mVideoFile = new File(mExternalFilesDir.getAbsolutePath(), "video.mp4");
        Log.e(TAG, " Video Path " + mExternalFilesDir.getAbsolutePath());

        initPlayer();
        initView();
        initPlayerListener();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        } else {
            checkVideoFile();
        }
    }

    private void initPlayer() {
        mAliPlayer = AliPlayerFactory.createAliPlayer(this);
        mAliPlayer.setAutoPlay(true);
    }

    private void initView() {
        mPlayerView = findViewById(R.id.player_view);
        mPlayerView.bind(mAliPlayer, RenderType.SURFACE_VIEW);
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
            public void onLoadingProgress(int i, float v) { }

            @Override
            public void onLoadingEnd() {
                mPlayerView.isLoading(false);
            }
        });

        mAliPlayer.setOnErrorListener((errorInfo) -> Log.e(TAG, "onError: " + errorInfo.getCode() + " --- " + errorInfo.getMsg() ));
    }

    private void playLocalVideo(String uri){
        Log.e(TAG, "playLocalVideo: " + uri );
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(uri);
        mAliPlayer.setDataSource(urlSource);
        mAliPlayer.prepare();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            checkVideoFile();
        }
    }

    private void checkVideoFile() {
        if (!mVideoFile.exists()) {
            copyAssets();
        } else {
            playLocalVideo(mVideoFile.getAbsolutePath());
        }
    }

    private void copyAssets() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try (InputStream source = getAssets().open("video.mp4");
                     OutputStream destination = new FileOutputStream(mVideoFile)) {

                    byte[] buffer = new byte[1024];
                    int nread;
                    while ((nread = source.read(buffer)) != -1) {
                        if (nread == 0) {
                            nread = source.read();
                            if (nread < 0) {
                                break;
                            }
                            destination.write(nread);
                            continue;
                        }
                        destination.write(buffer, 0, nread);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playLocalVideo(mVideoFile.getAbsolutePath());
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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