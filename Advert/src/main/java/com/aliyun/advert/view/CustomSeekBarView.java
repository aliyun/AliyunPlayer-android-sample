package com.aliyun.advert.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

public class CustomSeekBarView extends androidx.appcompat.widget.AppCompatSeekBar {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mViewWidth;
    private int mPaddingLeft,mPaddingRight;
    private int[] mAdvPositionArr;
    private float[] mLineStartX = new float[0];
    private float mAdvRatio;
    private float mAdvWith;

    public CustomSeekBarView(@NonNull Context context) {
        super(context);
    }

    public CustomSeekBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mViewWidth = getWidth();
        this.mPaddingLeft = getPaddingLeft();
        this.mPaddingRight = getPaddingRight();

        mPaint.setStrokeWidth(2.0f);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        calculateCoordinates();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        for (float lineStartX : mLineStartX) {
            canvas.drawRect(lineStartX, 0, lineStartX + mAdvWith, getHeight(), mPaint);
        }
        super.onDraw(canvas);
    }

    /**
     * max = (AdvDuration Duration * n) + SourceVideo Duration
     * @param advDuration Adv duration
     */
    public void setAdvDuration(long advDuration) {
        mAdvRatio = advDuration * 1.0f / getMax() * 1.0f;
    }

    private void calculateCoordinates(){
        float realWidth = mViewWidth - mPaddingLeft - mPaddingRight;
        mAdvWith = realWidth * mAdvRatio;
        if(mAdvPositionArr != null){
            int size = mAdvPositionArr.length;
            for(int i = 0;i < mAdvPositionArr.length;i++){
                mLineStartX[i] = mPaddingLeft + (realWidth - size * mAdvWith) * mAdvPositionArr[i] / 100f + (i * mAdvWith);
            }
        }
    }

    public void setPosition(int[] advPositionArr) {
        this.mAdvPositionArr = advPositionArr;
        this.mLineStartX = new float[mAdvPositionArr.length];
        Arrays.sort(advPositionArr);
        calculateCoordinates();
        invalidate();
    }
}
