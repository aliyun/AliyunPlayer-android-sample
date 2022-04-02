package com.aliyun.videolistbyplayer;

import android.content.Context;
import android.view.Surface;

import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.videolistbyplayer.bean.ListVideoBean;
import com.aliyun.videolistbyplayer.player.AliPlayerManager;
import com.aliyun.videolistbyplayer.player.AliPlayerPreload;

import java.util.ArrayList;
import java.util.LinkedList;


public class ListPlayerController{

    private static final ListPlayerModel mListPlayerModel = new ListPlayerModel();
    private final AliPlayerManager mAliPlayerManager;
    private final AliPlayerPreload mAliPlayerPreload;

    private Context mContext;
    private int mOldPosition = 0;
    private boolean mIsFirstPlay = true;

    public ListPlayerController(Context context){
        this.mContext = context;
        mAliPlayerPreload = AliPlayerPreload.getInstance();
        mAliPlayerManager = new AliPlayerManager(context);

        mAliPlayerPreload.setUrls(mListPlayerModel.getData());
        mAliPlayerManager.setUrls(mListPlayerModel.getData());

        mAliPlayerManager.setOnPlayerListener(new AliPlayerManager.OnPlayerListener() {
            @Override
            public void onRenderingStart() {
                if(mContext instanceof VideoListActivity){
                    ((VideoListActivity)mContext).hideCoverView();
                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                if(mContext instanceof VideoListActivity){
                    ((VideoListActivity)mContext).showError(errorInfo);
                }
            }
        });
    }

    /**
     * load assets/videolist.txt
     */
    public static void init(Context context) {
        mListPlayerModel.loadData(context.getApplicationContext());
    }

    public LinkedList<ListVideoBean> getData(){
        return mListPlayerModel.getData();
    }

    public void setRenderView(Surface surface) {
        mAliPlayerManager.setSurface(surface);
    }

    public void surfacChanged() {
        mAliPlayerManager.surfaceChanged();
    }

    public void start(int position) {
        if(position == mOldPosition && !mIsFirstPlay){
            return ;
        }
        int flag = position - mOldPosition;
        if (Math.abs(flag) == 1) {
            if (flag < 0) {
                mAliPlayerManager.moveToPre();
            } else {
                mAliPlayerManager.moveToNext();
            }
        } else {
            mAliPlayerManager.moveTo(position);
            mIsFirstPlay = false;
        }
        mAliPlayerPreload.moveTo(position);
        mOldPosition = position;
    }

    public void destroy() {
        mAliPlayerManager.release();
    }

    public void startPlay(int position, int itemPosition) {
        LinkedList<ListVideoBean> data = mListPlayerModel.getData();
        ListVideoBean listVideoBean = data.get(position);
        ArrayList<ListVideoBean.HListVideoBean> horizontalVideoData = listVideoBean.getHorizontalVideoData();
        ListVideoBean.HListVideoBean hListVideoBean = horizontalVideoData.get(itemPosition);
        mAliPlayerManager.startPlay(hListVideoBean.getUrl());
    }
}
