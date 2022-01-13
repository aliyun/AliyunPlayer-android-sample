package com.aliyun.advert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mPlanAButton;
    private Button mPlanBButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        mPlanAButton = findViewById(R.id.btn_plan_a);
        mPlanBButton = findViewById(R.id.btn_plan_b);

        mPlanAButton.setOnClickListener(this);
        mPlanBButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mPlanAButton){
            Intent intent = new Intent(this,PlanAActivity.class);
            startActivity(intent);
        }else if(v == mPlanBButton){
            Intent intent = new Intent(this,PlanBActivity.class);
            startActivity(intent);
        }
    }
}