package com.aliyun.advert.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomSeekBarView extends androidx.appcompat.widget.AppCompatSeekBar {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        mPaint.setColor(Color.RED);
    }

    public CustomSeekBarView(@NonNull Context context) {
        super(context);
    }

    public CustomSeekBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //canvas.drawLine(getWidth() * rect,0,getWidth() * rect,getHeight(),mPaint);
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
