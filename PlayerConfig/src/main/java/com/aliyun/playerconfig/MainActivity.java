package com.aliyun.playerconfig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.nativeclass.PlayerConfig;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static AliPlayer mAliPlayer;

    private EditText mReferrerEditText;
    private EditText mUserAgentEditText;
    private EditText mMaxDelayTimeEditText;
    private EditText mMaxProbeSizeEditText;
    private EditText mNetworkTimeoutEditText;
    private EditText mMaxBufferDurationEditText;
    private EditText mNetworkRetryCountEditText;
    private EditText mHighBufferDurationEditText;
    private EditText mStartBufferDurationEditText;
    private EditText mPositionTimerIntervalMSEditText;
    private EditText mMaxBackwardBufferDurationMSEditText;

    private Switch mEnableSeiSwitch;
    private Switch mDisableVideoSwitch;
    private Switch mDisableAudioSwitch;
    private Switch mClearFrameWhenStopSwitch;
    private Switch mEnableVideoTunnelRenderSwitch;

    private Button mPlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAliPlayer = AliPlayerFactory.createAliPlayer(this);
        initView();
        initViewData();
    }

    private void initView(){
        mReferrerEditText = findViewById(R.id.et_referrer);
        mUserAgentEditText = findViewById(R.id.et_user_agent);
        mMaxProbeSizeEditText = findViewById(R.id.et_max_probe_size);
        mMaxDelayTimeEditText = findViewById(R.id.et_max_delay_time);
        mNetworkTimeoutEditText = findViewById(R.id.et_network_timeout);
        mNetworkRetryCountEditText = findViewById(R.id.et_network_retry_count);
        mMaxBufferDurationEditText = findViewById(R.id.et_max_buffer_duration);
        mHighBufferDurationEditText = findViewById(R.id.et_high_buffer_duration);
        mStartBufferDurationEditText = findViewById(R.id.et_start_buffer_duration);
        mPositionTimerIntervalMSEditText = findViewById(R.id.et_position_timer_interval_ms);
        mMaxBackwardBufferDurationMSEditText = findViewById(R.id.et_max_backward_buffer_duration_ms);

        mEnableSeiSwitch = findViewById(R.id.switch_enable_sei);
        mDisableVideoSwitch = findViewById(R.id.switch_disable_video);
        mDisableAudioSwitch = findViewById(R.id.switch_disable_audio);
        mClearFrameWhenStopSwitch = findViewById(R.id.switch_clear_frame_when_stop);
        mEnableVideoTunnelRenderSwitch = findViewById(R.id.switch_enable_video_tunnelrender);

        mPlayButton = findViewById(R.id.btn_play);
        mPlayButton.setOnClickListener(this);
    }

    private void initViewData(){
        PlayerConfig config = mAliPlayer.getConfig();
        mReferrerEditText.setText(config.mReferrer);
        mUserAgentEditText.setText(config.mReferrer);
        mMaxProbeSizeEditText.setText(String.valueOf(config.mMaxProbeSize));
        mMaxDelayTimeEditText.setText(String.valueOf(config.mMaxDelayTime));
        mNetworkTimeoutEditText.setText(String.valueOf(config.mNetworkTimeout));
        mNetworkRetryCountEditText.setText(String.valueOf(config.mNetworkRetryCount));
        mMaxBufferDurationEditText.setText(String.valueOf(config.mMaxBufferDuration));
        mHighBufferDurationEditText.setText(String.valueOf(config.mHighBufferDuration));
        mStartBufferDurationEditText.setText(String.valueOf(config.mStartBufferDuration));
        mPositionTimerIntervalMSEditText.setText(String.valueOf(config.mPositionTimerIntervalMs));
        mMaxBackwardBufferDurationMSEditText.setText(String.valueOf(config.mMaxBackwardBufferDurationMs));

        mEnableSeiSwitch.setChecked(config.mEnableSEI);
        mDisableVideoSwitch.setChecked(config.mDisableVideo);
        mDisableAudioSwitch.setChecked(config.mDisableAudio);
        mClearFrameWhenStopSwitch.setChecked(config.mClearFrameWhenStop);
        mEnableVideoTunnelRenderSwitch.setChecked(config.mEnableVideoTunnelRender);
    }

    private void saveConfig(){
        PlayerConfig config = mAliPlayer.getConfig();
        config.mReferrer = mReferrerEditText.getText().toString();
        config.mUserAgent = mUserAgentEditText.getText().toString();
        config.mMaxProbeSize = Integer.parseInt(mMaxProbeSizeEditText.getText().toString());
        config.mMaxDelayTime = Integer.parseInt(mMaxDelayTimeEditText.getText().toString());
        config.mNetworkTimeout = Integer.parseInt(mNetworkTimeoutEditText.getText().toString());
        config.mNetworkRetryCount = Integer.parseInt(mNetworkRetryCountEditText.getText().toString());
        config.mMaxBufferDuration = Integer.parseInt(mMaxBufferDurationEditText.getText().toString());
        config.mHighBufferDuration = Integer.parseInt(mHighBufferDurationEditText.getText().toString());
        config.mStartBufferDuration = Integer.parseInt(mStartBufferDurationEditText.getText().toString());
        config.mPositionTimerIntervalMs = Integer.parseInt(mPositionTimerIntervalMSEditText.getText().toString());
        config.mMaxBackwardBufferDurationMs = Integer.parseInt(mMaxBackwardBufferDurationMSEditText.getText().toString());

        config.mEnableSEI = mEnableSeiSwitch.isChecked();
        config.mDisableVideo = mDisableVideoSwitch.isChecked();
        config.mDisableAudio = mDisableAudioSwitch.isChecked();
        config.mClearFrameWhenStop = mClearFrameWhenStopSwitch.isChecked();
        config.mEnableVideoTunnelRender = mEnableVideoTunnelRenderSwitch.isChecked();

        mAliPlayer.setConfig(config);
    }

    private void play(){
        saveConfig();
        Intent intent = new Intent(this,PlayActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        if(v == mPlayButton){
            play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAliPlayer.release();
    }
}