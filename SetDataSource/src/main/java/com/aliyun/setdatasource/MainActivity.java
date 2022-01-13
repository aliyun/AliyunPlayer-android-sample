package com.aliyun.setdatasource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.source.VidSts;
import com.aliyun.playerview.java.PlayerView;
import com.aliyun.playerview.java.RenderType;
import com.aliyun.setdatasource.bean.VidAuthBean;
import com.aliyun.setdatasource.bean.VidStsBean;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 *
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String VIDEO_URL = "http://player.alicdn.com/video/aliyunmedia.mp4";
    private static final String VIDEO_ID = "63566edb9f61417bb46b0bb2b26cb29e";

    private PlayerView mPlayerView;
    private AliPlayer mAliPlayer;

    private final Gson mGson = new Gson();
    private final OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().build();

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

        mAliPlayer.setOnErrorListener((errorInfo) -> Log.e(TAG, "onError: " + errorInfo.getCode() + " --- " + errorInfo.getMsg() ));
    }

    private void setUrlDataSource(){
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(VIDEO_URL);
        mAliPlayer.setDataSource(urlSource);
        mAliPlayer.prepare();
    }

    private void setVidStsDataSource(){
        Request request = new Request.Builder()
                .url("https://alivc-demo.aliyuncs.com/demo/getSts")
                .get()
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(MainActivity.this, "get VidSts Error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    ResponseBody body = response.body();
                    if(body != null){
                        String data = body.string();
                        VidStsBean vidStsBean = mGson.fromJson(data, VidStsBean.class);

                        VidSts vidSts = new VidSts();
                        vidSts.setVid(VIDEO_ID);
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

    private void setVidAuthDataSource(){
        Request request = new Request.Builder()
                .url("https://alivc-demo.aliyuncs.com/demo/getVideoPlayAuth?videoId="+VIDEO_ID)
                .get()
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(MainActivity.this, "get VidAuth error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    ResponseBody body = response.body();
                    if(body != null){
                        String data = body.string();
                        VidAuthBean vidAuthBean = mGson.fromJson(data, VidAuthBean.class);

                        VidAuth vidAuth = new VidAuth();
                        vidAuth.setVid(vidAuthBean.getData().getVideoId());
                        vidAuth.setPlayAuth(vidAuthBean.getData().getPlayAuth());

                        mAliPlayer.setDataSource(vidAuth);
                        mAliPlayer.prepare();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.data_source_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.url){
            setUrlDataSource();
        }else if(itemId == R.id.vid_sts){
            setVidStsDataSource();
        }else if(itemId == R.id.vid_auth){
            setVidAuthDataSource();
        }
        return true;
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