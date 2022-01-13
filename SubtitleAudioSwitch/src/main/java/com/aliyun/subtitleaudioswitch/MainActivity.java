package com.aliyun.subtitleaudioswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.UrlSource;
import com.aliyun.playerview.java.PlayerView;
import com.aliyun.playerview.java.RenderType;
import com.aliyun.subtitle.SubtitleView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String URL = "https://alivc-player.oss-cn-shanghai.aliyuncs.com/duoaudio/duomalv/index.m3u8";

    private final AudioHelper mAudioHelper = new AudioHelper();
    private final SubtitleHelper mSubtitleHelper = new SubtitleHelper();

    private AliPlayer mAliPlayer;
    private Spinner mAudioSpinner;
    private PlayerView mPlayerView;
    private Spinner mSubtitleSpinner;
    //show subtitle View
    private SubtitleView mSubtitleView;

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
        mAudioSpinner = findViewById(R.id.audio_spinner);
        mSubtitleSpinner = findViewById(R.id.subtitle_spinner);

        mSubtitleView = findViewById(R.id.subtitleview);

        SubtitleView.DefaultValueBuilder defaultValueBuilder = new SubtitleView.DefaultValueBuilder().setSize(50);
        mSubtitleView.setDefaultValue(defaultValueBuilder);
    }

    private void initPlayerListener(){
        mAliPlayer.setOnRenderingStartListener(() -> {
            mPlayerView.isPlaying(true);
            mPlayerView.setDuration(mAliPlayer.getDuration());
        });

        //init Spinner
        mAliPlayer.setOnTrackReadyListener(mediaInfo -> {
            List<TrackInfo> trackInfos = mediaInfo.getTrackInfos();
            mAudioHelper.setTrackInfos(trackInfos);
            mSubtitleHelper.setTrackInfos(trackInfos);

            setSpinnerAudio();
            setSpinnerSubtitle();
        });

        //subtitle show & hide
        mAliPlayer.setOnSubtitleDisplayListener(new IPlayer.OnSubtitleDisplayListener() {
            @Override
            public void onSubtitleExtAdded(int trackIndex, String url) { }

            @Override
            public void onSubtitleShow(int trackIndex, long id, String data) {
                SubtitleView.Subtitle subtitle = new SubtitleView.Subtitle();
                subtitle.id = String.valueOf(id);
                subtitle.content = data;

                mSubtitleView.show(subtitle);
            }

            @Override
            public void onSubtitleHide(int trackIndex, long id) {
                mSubtitleView.dismiss(String.valueOf(id));
            }

            @Override
            public void onSubtitleHeader(int trackIndex, String header) { }
        });

        mAliPlayer.setOnTrackChangedListener(new IPlayer.OnTrackChangedListener() {
            @Override
            public void onChangedSuccess(TrackInfo trackInfo) {
                if(trackInfo.getType() == TrackInfo.Type.TYPE_AUDIO){
                    Toast.makeText(MainActivity.this, String.format("%s audio change success",trackInfo.getAudioLang()), Toast.LENGTH_SHORT).show();
                }else if(trackInfo.getType() == TrackInfo.Type.TYPE_SUBTITLE){
                    Toast.makeText(MainActivity.this, String.format("%s subtitle change success",trackInfo.getSubtitleLang()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {
                Toast.makeText(MainActivity.this, "change fail", Toast.LENGTH_SHORT).show();
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
            public void onLoadingProgress(int percent, float netSpeed) { }

            @Override
            public void onLoadingEnd() {
                mPlayerView.isLoading(false);
            }
        });

        mAliPlayer.setOnErrorListener((errorInfo) -> Log.e(TAG, "onError: " + errorInfo.getCode() + " --- " + errorInfo.getMsg() ));
    }

    private void setSpinnerSubtitle(){
        ArrayAdapter<Object> mSubtitleAdapter = new ArrayAdapter<>(this,R.layout.item_spinner_adapter, mSubtitleHelper.getSubtitleArr());
        mSubtitleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSubtitleSpinner.setAdapter(mSubtitleAdapter);

        mSubtitleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //change subtitle
                TrackInfo trackInfo = mSubtitleHelper.getTrackInfo(position);
                mAliPlayer.selectTrack(trackInfo.getIndex());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setSpinnerAudio(){
        ArrayAdapter<Object> mAudioAdapter = new ArrayAdapter<>(this,R.layout.item_spinner_adapter, mAudioHelper.getAudioArr());
        mAudioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAudioSpinner.setAdapter(mAudioAdapter);

        mAudioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //change audio
                TrackInfo trackInfo = mAudioHelper.getTrackInfo(position);
                mAliPlayer.selectTrack(trackInfo.getIndex());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void initDataSource(){
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(URL);
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
}