package com.aliyun.bitrateswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.aliyun.bitrateswitch.bean.VidStsBean;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.Definition;
import com.aliyun.player.source.VidSts;
import com.aliyun.playerview.java.PlayerView;
import com.aliyun.playerview.java.RenderType;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String VID = "979559fa48d445e9b80c8eddff0c1509";

    private final Gson mGson = new Gson();
    private final BitrateHelper mBitrateHelper = new BitrateHelper();
    private final OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().build();

    private PlayerView mPlayerView;
    private AliPlayer mAliPlayer;
    private Spinner mBitrateSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPlayer();
        initView();
        initPlayerListener();
        initDataSource();
    }

    private void initPlayer(){
        mAliPlayer = AliPlayerFactory.createAliPlayer(this);
        mAliPlayer.setAutoPlay(true);
    }

    private void initView(){
        mPlayerView = findViewById(R.id.playerView);
        mPlayerView.bind(mAliPlayer, RenderType.SURFACE_VIEW);
        mBitrateSpinner = findViewById(R.id.bitrate_spinner);
    }

    private void initPlayerListener(){
        mAliPlayer.setOnRenderingStartListener(() -> {
            mPlayerView.isPlaying(true);
            mPlayerView.setDuration(mAliPlayer.getDuration());
        });

        //init Spinner bitrate
        mAliPlayer.setOnTrackReadyListener(mediaInfo -> {
            List<TrackInfo> trackInfos = mediaInfo.getTrackInfos();
            mBitrateHelper.setTrackInfos(trackInfos);

            setSpinnerBitrate();
        });

        mAliPlayer.setOnTrackChangedListener(new IPlayer.OnTrackChangedListener() {
            @Override
            public void onChangedSuccess(TrackInfo trackInfo) {
                Toast.makeText(MainActivity.this, String.format("%s bitrate change success",trackInfo.getVideoBitrate()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {
                Toast.makeText(MainActivity.this, "change bitrate fail", Toast.LENGTH_SHORT).show();
            }
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

    private void setSpinnerBitrate(){
        ArrayAdapter<Object> mBitrateAdapter = new ArrayAdapter<>(this,R.layout.item_spinner_adapter, mBitrateHelper.getBitrateArr());
        mBitrateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBitrateSpinner.setAdapter(mBitrateAdapter);

        //Default first bitrate
        mBitrateSpinner.setSelection(1,true);

        mBitrateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //change bitrate
                TrackInfo trackInfo = mBitrateHelper.getTrackInfo(position);
                mAliPlayer.selectTrack(trackInfo.getIndex());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void initDataSource(){
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
                        vidSts.setVid(VID);
                        vidSts.setAccessKeyId(vidStsBean.getData().getAccessKeyId());
                        vidSts.setAccessKeySecret(vidStsBean.getData().getAccessKeySecret());
                        vidSts.setSecurityToken(vidStsBean.getData().getSecurityToken());

                        //Set the bitrate definition type returned by the VOD server
                        List<Definition> list = new ArrayList<>();
                        list.add(Definition.DEFINITION_AUTO);
                        vidSts.setDefinition(list);

                        mAliPlayer.setDataSource(vidSts);
                        mAliPlayer.prepare();
                    }
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