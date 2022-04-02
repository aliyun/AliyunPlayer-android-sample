package com.aliyun.advert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.advert.view.CustomSeekBarView;
import com.aliyun.playerview.java.TimeFormater;

/**
 * Plan-b is presented using a progress bar
 * Beginning Video --> Source Video --> EndingVideo
 */
public class PlanBActivity extends AppCompatActivity implements IView{

    private SurfaceView mSurfaceView;
    private TextView mDurationTextView;
    private TextView mCurrentPositionTextView;
    private CustomSeekBarView mCustomSeekBarView;
    private PlanController mController;
    //adv duration
    private long mAdvDuration;
    //source duration
    private long mSourceDuration;
    private final int[] mAdvPositionArr = new int[]{0,50,100};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_bactivity);

        mController = new PlanController(this);
        mController.setAdvPosition(mAdvPositionArr);

        initView();
        initListener();

        mController.createPlayer();
    }

    private void initView(){
        mSurfaceView = findViewById(R.id.surface_view);
        mDurationTextView = findViewById(R.id.tv_duration);
        mCurrentPositionTextView = findViewById(R.id.tv_current_position);
        mCustomSeekBarView = findViewById(R.id.custom_seekbar);
    }

    private void initListener(){
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mController.surfaceCreated(holder);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                mController.surfaceChanged();
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                mController.surfaceDestroy(holder);
            }
        });

        mCustomSeekBarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mController.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setAdvSeekBarProgress(long progress) {
        mCustomSeekBarView.setProgress((int) progress);
        mCurrentPositionTextView.setText(TimeFormater.formatMs(progress));
    }

    @Override
    public void setAdvSeekBarSecondProgress(long progress) { }

    @Override
    public void setSourceSeekBarSecondProgress(long progress) { }

    @Override
    public void setSourceSeekBarProgress(long progress) {
        mCustomSeekBarView.setProgress((int) (progress));
        mCurrentPositionTextView.setText(TimeFormater.formatMs(progress));
    }

    @Override
    public void setAdvDuration(long advDuration) {
        this.mAdvDuration = advDuration;
    }

    @Override
    public void setSourceDuration(long duration) { this.mSourceDuration = duration; }

    @Override
    public void showAdvError(int errorCode, String msg) {
        Toast.makeText(this, "error:" + errorCode + " --- " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSourceError(int errorCode, String msg) {
        Toast.makeText(this, "source error:" + errorCode + " --- " + msg, Toast.LENGTH_SHORT).show();
    }

    public void setTotalDuration(long totalDuration){
        mDurationTextView.setText(TimeFormater.formatMs(totalDuration));
        mCustomSeekBarView.setMax((int) totalDuration);
        mCustomSeekBarView.setAdvDuration(mAdvDuration);
        mCustomSeekBarView.setPosition(mAdvPositionArr);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mController.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mController.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mController.release();
    }
}