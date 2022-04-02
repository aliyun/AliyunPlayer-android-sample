package com.aliyun.videolistbyplayer.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLayoutManager extends LinearLayoutManager {

    private PagerSnapHelper mPagerSnapHelper;
    private OnViewPagerListener mOnViewPagerListener;
    private int mOldPosition = -1;

    public CustomLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }

    private void init() {
        mPagerSnapHelper = new PagerSnapHelper();
    }

    public void setOnViewPagerListener(OnViewPagerListener listener){
        this.mOnViewPagerListener = listener;
    }

    @Override
    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        if(recyclerView != null){
            mPagerSnapHelper.attachToRecyclerView(recyclerView);

            recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(@NonNull View view) {
                    int childCount = recyclerView.getChildCount();
                    if(mOnViewPagerListener != null && childCount == 1){
                        mOnViewPagerListener.onPageSelected(0);
                    }else{
                        int position = getPosition(view);
                        mOnViewPagerListener.onPageShow(position);
                    }
                }

                @Override
                public void onChildViewDetachedFromWindow(@NonNull View view) {
                    if(mOnViewPagerListener != null){
                        mOnViewPagerListener.onPageRelease();
                    }
                }
            });
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if(state == RecyclerView.SCROLL_STATE_IDLE){
            if(mOnViewPagerListener != null){
                View snapView = mPagerSnapHelper.findSnapView(CustomLayoutManager.this);
                if(snapView != null){
                    int position = getPosition(snapView);
                    if(mOldPosition != position){
                        mOnViewPagerListener.onPageSelected(position);
                        mOldPosition = position;
                    }
                }
            }
        }
    }

    public interface OnViewPagerListener{
        void onPageShow(int position);
        void onPageSelected(int position);
        void onPageRelease();
    }
}
