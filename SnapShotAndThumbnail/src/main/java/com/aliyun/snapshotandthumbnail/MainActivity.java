package com.aliyun.snapshotandthumbnail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mSnapshotButton;
    private Button mThumbnailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        mSnapshotButton = findViewById(R.id.btn_snapshot);
        mThumbnailButton = findViewById(R.id.btn_thumbnail);

        mSnapshotButton.setOnClickListener(this);
        mThumbnailButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mSnapshotButton){
            Intent intent = new Intent(this,SnapshotActivity.class);
            startActivity(intent);
        }else if(v == mThumbnailButton){
            Intent intent = new Intent(this,ThumbnailActivity.class);
            startActivity(intent);
        }
    }
}