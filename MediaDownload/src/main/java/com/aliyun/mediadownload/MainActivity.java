package com.aliyun.mediadownload;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aliyun.downloader.AliDownloaderFactory;
import com.aliyun.downloader.AliMediaDownloader;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.nativeclass.MediaInfo;

public class MainActivity extends AppCompatActivity {

    private static final String VIDEO_ID = "63566edb9f61417bb46b0bb2b26cb29e";
    private AliMediaDownloader mAliMediaDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMediaDownloader();
    }

    private void initMediaDownloader(){
        mAliMediaDownloader = AliDownloaderFactory.create(this);
        //set Dowload Video Path
        mAliMediaDownloader.setSaveDir(getFilesDir().getAbsolutePath());

        mAliMediaDownloader.setOnPreparedListener(new AliMediaDownloader.OnPreparedListener() {
            @Override
            public void onPrepared(MediaInfo mediaInfo) {

            }
        });

        mAliMediaDownloader.setOnProgressListener(new AliMediaDownloader.OnProgressListener() {
            @Override
            public void onDownloadingProgress(int percent) {

            }

            @Override
            public void onProcessingProgress(int percent) {

            }
        });

        mAliMediaDownloader.setOnCompletionListener(new AliMediaDownloader.OnCompletionListener() {
            @Override
            public void onCompletion() {

            }
        });

        mAliMediaDownloader.setOnErrorListener(new AliMediaDownloader.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {

            }
        });
    }
}