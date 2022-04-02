package com.aliyun.advert;

import android.view.SurfaceHolder;
import androidx.annotation.NonNull;
import com.aliyun.advert.listener.CustomPlayerObserver;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlanController {

    private static final int OFFSET = 500;
    private IView mPlanView;
    private AliyunPlayerHelper mAliyunPlayerHelper;
    private SurfaceHolder mSurfaceHolder;

    private int mAdvPosition = 0;
    private long mAdvDuration = 0;
    private long mSourceDuration = 0;
    private boolean mPlayingAdv = false;
    private long mCurrentSourcePosition = 0;
    private final List<Integer> mAdvPositionList = new ArrayList<>();

    public PlanController(@NonNull IView iView) {
        mPlanView = iView;
    }

    public void createPlayer() {
        mAliyunPlayerHelper = AliyunPlayerHelper.getInstance();
        mAliyunPlayerHelper.createPlayer(mPlanView.getContext());

        mAliyunPlayerHelper.setCustomPlayerObserver(new CustomPlayerObserver() {
            @Override
            public void onAdvPrepared() {
                if (mAdvDuration == 0) {
                    mAdvDuration = mAliyunPlayerHelper.getAdvDuration();
                    mPlanView.setAdvDuration(mAdvDuration);
                    intentPlay();
                }
            }

            @Override
            public void onSourcePrepared() {
                if (mSourceDuration == 0) {
                    mSourceDuration = mAliyunPlayerHelper.getSourceDuration();
                    mPlanView.setSourceDuration(mSourceDuration);
                    intentPlay();
                }
            }

            @Override
            public void onAdvRenderingStart() { }

            @Override
            public void onSourceRenderingStart() { }

            @Override
            public void onAdvInfo(InfoBean infoBean) {
                if (infoBean.getCode() == InfoCode.CurrentPosition) {
                    long progress = infoBean.getExtraValue();
                    //fix seekBar bounce
                    if(mAdvPosition > 0 && progress == 0){
                        return ;
                    }
                    if (mPlanView instanceof PlanBActivity) {
                        progress = progress + (mAdvDuration * mAdvPosition) + mCurrentSourcePosition;
                    }
                    mPlanView.setAdvSeekBarProgress(progress);
                } else if (infoBean.getCode() == InfoCode.BufferedPosition) {
                    if (mPlanView instanceof PlanAActivity) {
                        mPlanView.setAdvSeekBarSecondProgress(infoBean.getExtraValue());
                    }
                } else if (infoBean.getCode() == InfoCode.LoopingStart) {
                    mAdvPosition++;
                    startSourceVideo();
                }
            }

            @Override
            public void onSourceInfo(InfoBean infoBean) {
                if (infoBean.getCode() == InfoCode.CurrentPosition) {
                    mCurrentSourcePosition = infoBean.getExtraValue();
                    mPlanView.setSourceSeekBarProgress(mPlanView instanceof PlanBActivity ? (mAdvDuration * mAdvPosition + mCurrentSourcePosition) : mCurrentSourcePosition);
                    //play advVideo
                    if(mAdvPosition < mAdvPositionList.size()){
                        Integer percent = mAdvPositionList.get(mAdvPosition);
                        long durationPercent = mSourceDuration * percent / 100;
                        if(mCurrentSourcePosition > (durationPercent - OFFSET) && mCurrentSourcePosition < (durationPercent + OFFSET)){
                            startAdvVideo();
                        }
                    }
                } else if (infoBean.getCode() == InfoCode.BufferedPosition) {
                    if (mPlanView instanceof PlanAActivity) {
                        mPlanView.setSourceSeekBarSecondProgress(infoBean.getExtraValue());
                    }
                }
            }

            @Override
            public void onAdvCompletion() { }

            @Override
            public void onSourceCompletion() { }

            @Override
            public void onSourceSeekComplete() { }

            @Override
            public void onAdvError(ErrorInfo errorInfo) { }

            @Override
            public void onSourceError(ErrorInfo errorInfo) { }
        });
        mAliyunPlayerHelper.prepareAdv();
        mAliyunPlayerHelper.prepareSource();
    }

    /**
     * insert position
     *
     * @param positionArray percentage
     */
    public void setAdvPosition(int[] positionArray) {
        int[] new_array = new int[positionArray.length];
        System.arraycopy(positionArray,0,new_array,0,positionArray.length);
        Arrays.sort(new_array);
        for (int i : new_array) {
            mAdvPositionList.add(i);
        }
    }

    private void intentPlay(){
        if(mSourceDuration == 0 || mAdvDuration == 0){
            return ;
        }

        /*
         * plan-B，Total Duration = BeginningDuration * n + SourceDuration。
         * The Duration can only be obtained after {Player.OnPrepared()}
         */
        if(mPlanView instanceof PlanBActivity){
            long totalDuration = mAdvDuration * mAdvPositionList.size() + mSourceDuration;
            ((PlanBActivity) mPlanView).setTotalDuration(totalDuration);
        }

        if(!mAdvPositionList.isEmpty() && mAdvPositionList.get(0) == 0){
            startAdvVideo();
        }else{
            startSourceVideo();
        }
    }

    private void startAdvVideo() {
        mAliyunPlayerHelper.pauseSource();
        if (mPlanView instanceof PlanAActivity) {
            mPlanView.setAdvDuration(mAliyunPlayerHelper.getAdvDuration());
        }
        setSurfaceHolder(null, mSurfaceHolder);
        mAliyunPlayerHelper.startAdv();
        mPlayingAdv = true;
    }

    private void startSourceVideo() {
        mAliyunPlayerHelper.pauseAdv();
        if (mPlanView instanceof PlanAActivity) {
            mPlanView.setSourceDuration(mAliyunPlayerHelper.getSourceDuration());
        }
        setSurfaceHolder(mSurfaceHolder, null);
        mAliyunPlayerHelper.startSource();
        mPlayingAdv = false;
    }

    public void seekTo(int progress) {
        if(mPlayingAdv){
            if(mPlanView instanceof PlanAActivity){
                seekAdvTo(progress);
            }
        }else{
            if(mPlanView instanceof PlanAActivity){
                seekSourceTo(progress);
            }else{
                for(int i = 0,j = mAdvPositionList.size() - 1;i < mAdvPositionList.size(); i++,j--){
                    //calculate the progress of each ADV position
                    float minResult = mSourceDuration * mAdvPositionList.get(i) / 100.0f + mAdvDuration * i;
                    if(progress < minResult){
                        mAdvPosition = i;
                        break;
                    }
                }
                seekSourceTo((int) (progress - mAdvDuration * mAdvPosition));
            }
        }
    }

    public void seekAdvTo(int progress) {
        mAliyunPlayerHelper.seekToAdv(progress);
    }

    public void seekSourceTo(int progress) {
        mAliyunPlayerHelper.seekToSource(progress);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mSurfaceHolder = holder;
        if (mPlayingAdv) {
            setSurfaceHolder(null, holder);
        } else {
            setSurfaceHolder(holder, null);
        }
    }

    public void surfaceChanged() {
        if (mPlayingAdv) {
            mAliyunPlayerHelper.advSurfaceChanged();
        } else{
            mAliyunPlayerHelper.sourceSurfaceChanged();
        }
    }

    public void surfaceDestroy(SurfaceHolder holder) {
        this.mSurfaceHolder = null;
        setSurfaceHolder(null, null);
    }

    private void setSurfaceHolder(SurfaceHolder sourceSurface, SurfaceHolder advSurfaceHolder) {
        mAliyunPlayerHelper.setSourceDisplay(null);
        mAliyunPlayerHelper.setAdvDisplay(null);
        if (sourceSurface != null && advSurfaceHolder == null) {
            mAliyunPlayerHelper.setSourceDisplay(sourceSurface);
        } else if (sourceSurface == null && advSurfaceHolder != null) {
            mAliyunPlayerHelper.setAdvDisplay(advSurfaceHolder);
        }
    }

    public void onStart() {
        if (mPlayingAdv) {
            mAliyunPlayerHelper.startAdv();
        } else {
            mAliyunPlayerHelper.startSource();
        }
    }

    public void onPause() {
        if (mPlayingAdv) {
            mAliyunPlayerHelper.pauseAdv();
        } else {
            mAliyunPlayerHelper.pauseSource();
        }
    }

    public void release() {
        mPlanView = null;
        mAliyunPlayerHelper.release();
        mAdvPosition = 0;
        mAdvDuration = 0;
        mSourceDuration = 0;
        mCurrentSourcePosition = 0;
        mAdvPositionList.clear();
    }
}
