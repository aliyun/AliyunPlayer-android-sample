package com.aliyun.memoryplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button mPlayButton;
    private Switch mEnableMemoryPlaySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        mPlayButton = findViewById(R.id.btn_play);
        mEnableMemoryPlaySwitch = findViewById(R.id.switch_enable_memory_play);

        mPlayButton.setOnClickListener(this);
        mEnableMemoryPlaySwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mPlayButton){
            Intent intent = new Intent(this,PlayActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == mEnableMemoryPlaySwitch){
            Global.ENABLE_MEMORY_PLAY = isChecked;
            if(isChecked){
                //clear
                Global.POSITION = 0;
            }
        }
    }
}