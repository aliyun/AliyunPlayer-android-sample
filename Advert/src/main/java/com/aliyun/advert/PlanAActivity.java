package com.aliyun.advert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.content.Context;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyun.playerview.java.TimeFormater;

/**
 * Plan-a is presented using multiple progress bars
 * Beginning Video --> Source Video --> EndingVideo
 */
public class PlanAActivity extends AppCompatActivity implements IView {

    private SurfaceView mSurfaceView;
    private AppCompatSeekBar mAdvSeekBar;
    private AppCompatSeekBar mSourceSeekBar;
    private TextView mDurationTextView;
    private TextView mCurrentPositionTextView;
    private PlanController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_aactivity);

        mController = new PlanController(this);

        initView();
        initListener();

        mController.createPlayer();
    }

    private void initView(){
        mAdvSeekBar = findViewById(R.id.adv_seekBar);
        mSourceSeekBar = findViewById(R.id.source_seekBar);
        mSurfaceView = findViewById(R.id.surface_view);
        mDurationTextView = findViewById(R.id.tv_duration);
        mCurrentPositionTextView = findViewById(R.id.tv_current_position);
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

        mAdvSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mController.seekAdvTo(seekBar.getProgress());
            }
        });

        mSourceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mController.seekSourceTo(seekBar.getProgress());
            }
        });
    }

    public void showSeekAdvBar(boolean isShow){
        mAdvSeekBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mSourceSeekBar.setVisibility(isShow ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setAdvDuration(long duration) {
        showSeekAdvBar(true);
        mAdvSeekBar.setMax((int) duration);
        mDurationTextView.setText(TimeFormater.formatMs(duration));
    }

    @Override
    public void setSourceDuration(long duration) {
        showSeekAdvBar(false);
        mSourceSeekBar.setMax((int) duration);
        mDurationTextView.setText(TimeFormater.formatMs(duration));
    }

    @Override
    public void showAdvError(int errorCode, String msg) {
        Toast.makeText(this, "error:" + errorCode + " --- " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSourceError(int errorCode, String msg) {
        Toast.makeText(this, "source error:" + errorCode + " --- " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setAdvSeekBarProgress(long progress) {
        mAdvSeekBar.setProgress((int) progress);
        mCurrentPositionTextView.setText(TimeFormater.formatMs(progress));
    }

    @Override
    public void setAdvSeekBarSecondProgress(long progress) {
        mAdvSeekBar.setSecondaryProgress((int) progress);
    }

    @Override
    public void setSourceSeekBarProgress(long progress) {
        mSourceSeekBar.setProgress((int) progress);
        mCurrentPositionTextView.setText(TimeFormater.formatMs(progress));
    }

    @Override
    public void setSourceSeekBarSecondProgress(long progress) {
        mSourceSeekBar.setSecondaryProgress((int) progress);
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