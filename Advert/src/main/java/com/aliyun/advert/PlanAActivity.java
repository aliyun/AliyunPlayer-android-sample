package com.aliyun.advert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.TextView;

public class PlanAActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private AppCompatSeekBar mSeekBar;
    private TextView mDurationTextView;
    private TextView mCurrentPositionTextView;
    private AliyunPlayerHelper mAliyunPlayerHelper;
    private PlanAController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_aactivity);

        mController = new PlanAController(this);

        initView();

        mController.createPlayer();
    }

    private void initView(){
        mSeekBar = findViewById(R.id.seekBar);
        mSurfaceView = findViewById(R.id.surface_view);
        mDurationTextView = findViewById(R.id.tv_duration);
        mCurrentPositionTextView = findViewById(R.id.tv_current_position);
    }
}