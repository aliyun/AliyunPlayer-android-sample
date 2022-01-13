package com.aliyun.snapshotandthumbnail;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.Thumbnail;
import com.aliyun.player.source.VidSts;
import com.aliyun.playerview.java.PlayerView;
import com.aliyun.playerview.java.RenderType;
import com.aliyun.snapshotandthumbnail.bean.VidStsBean;
import com.aliyun.thumbnail.ThumbnailBitmapInfo;
import com.aliyun.thumbnail.ThumbnailHelper;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ThumbnailActivity extends AppCompatActivity {

    private static final String TAG = "ThumbnailActivity";
    private static final String VID = "63566edb9f61417bb46b0bb2b26cb29e";

    private AliPlayer mAliPlayer;
    private PlayerView mPlayerView;
    private ImageView mThumbnailImageView;
    private boolean mPrepareThumbnailSuccess = false;

    private final Gson mGson = new Gson();
    private final OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().build();
    private ThumbnailHelper mThumbnailHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumbnail);

        initView();
        initPlayer();
        initPlayerListener();
        initDataSource();
    }

    private void initView(){
        mPlayerView = findViewById(R.id.playerView);
        mThumbnailImageView = findViewById(R.id.iv_thumbnail);
    }

    private void initPlayer(){
        mAliPlayer = AliPlayerFactory.createAliPlayer(this);
        mPlayerView.bind(mAliPlayer, RenderType.TEXTURE_VIEW);

        mPlayerView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser && mThumbnailHelper != null && mPrepareThumbnailSuccess){
                    //request Thumbnail Bitmap
                    mThumbnailImageView.setVisibility(View.VISIBLE);
                    mThumbnailHelper.requestBitmapAtPosition(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mThumbnailImageView.setVisibility(View.GONE);
            }
        });
    }

    private void initPlayerListener(){
        mAliPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                mAliPlayer.start();
                initThumbnail();
            }
        });

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

    private void initDataSource(){
        Request request = new Request.Builder()
                .url("https://alivc-demo.aliyuncs.com/demo/getSts")
                .get()
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(ThumbnailActivity.this, "get VidSts Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    ResponseBody body = response.body();
                    if(body != null){
                        String data = body.string();
                        VidStsBean vidStsBean = mGson.fromJson(data, VidStsBean.class);

                        VidSts vidSts = new VidSts();
                        vidSts.setVid(VID);
                        vidSts.setAccessKeyId(vidStsBean.getData().getAccessKeyId());
                        vidSts.setAccessKeySecret(vidStsBean.getData().getAccessKeySecret());
                        vidSts.setSecurityToken(vidStsBean.getData().getSecurityToken());

                        mAliPlayer.setDataSource(vidSts);
                        mAliPlayer.prepare();
                    }
                }
            }
        });
    }

    private void initThumbnail() {
        MediaInfo mediaInfo = mAliPlayer.getMediaInfo();
        if(mediaInfo != null){
            //get sprite pictures URL
            List<Thumbnail> thumbnailList = mediaInfo.getThumbnailList();
            if(thumbnailList != null && thumbnailList.size() > 0){
                mThumbnailHelper = new ThumbnailHelper(thumbnailList.get(0).mURL);

                mThumbnailHelper.setOnPrepareListener(new ThumbnailHelper.OnPrepareListener() {
                    @Override
                    public void onPrepareSuccess() {
                        mPrepareThumbnailSuccess = true;
                    }

                    @Override
                    public void onPrepareFail() {
                        mPrepareThumbnailSuccess = false;
                    }
                });

                mThumbnailHelper.setOnThumbnailGetListener(new ThumbnailHelper.OnThumbnailGetListener() {
                    @Override
                    public void onThumbnailGetSuccess(long positionMs, ThumbnailBitmapInfo thumbnailBitmapInfo) {
                        Bitmap thumbnailBitmap = thumbnailBitmapInfo.getThumbnailBitmap();
                        mThumbnailImageView.setImageBitmap(thumbnailBitmap);
                    }

                    @Override
                    public void onThumbnailGetFail(long positionMs, String errorMsg) {
                        Toast.makeText(ThumbnailActivity.this, "get Thumbnail Fail " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });

                //thumbnail prepare
                mThumbnailHelper.prepare();
            }

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
        mPlayerView.isPlaying(false);
        mAliPlayer.stop();
        mAliPlayer.release();
    }
}