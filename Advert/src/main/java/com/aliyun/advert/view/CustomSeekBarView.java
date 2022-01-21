package com.aliyun.advert.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomSeekBarView extends androidx.appcompat.widget.AppCompatSeekBar {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mRatio;
    private int mViewWidth;
    private float mBeginningX, mEndingX;
    private int mPaddingLeft,mPaddingRight;

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
        calculateCoordinates();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStrokeWidth(5.0f);
        mPaint.setColor(Color.WHITE);
        if(mBeginningX != 0 && mEndingX != 0){
            //Beginning
            canvas.drawLine(mBeginningX,0, mBeginningX,getHeight(),mPaint);
            //ending
            canvas.drawLine(mEndingX,0, mEndingX,getHeight(),mPaint);
        }
    }

    /**
     * max = (BeginningVideo Duration + EndingVideo Duration) + SourceVideo Duration
     * @param splitLine BeginningVideo duration
     */
    public void setSplitLine(long splitLine) {
        mRatio = splitLine * 1.0f / getMax();
        calculateCoordinates();
    }

    private void calculateCoordinates(){
        float splitLineX = (mViewWidth - mPaddingLeft - mPaddingRight) * mRatio;
        mBeginningX = splitLineX + mPaddingLeft;
        mEndingX = mViewWidth - mPaddingRight - splitLineX;
    }
}
