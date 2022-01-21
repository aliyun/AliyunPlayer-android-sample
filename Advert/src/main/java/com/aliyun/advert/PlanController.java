package com.aliyun.advert;

import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.aliyun.advert.bean.PlayBackStatus;
import com.aliyun.advert.listener.CustomPlayerObserver;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;

public class PlanController {

    private IView mPlanView;
    private AliyunPlayerHelper mAliyunPlayerHelper;
    private PlayBackStatus mPlayBackStatus = PlayBackStatus.FREE;
    private SurfaceHolder mSurfaceHolder;

    private long mAdvDuration = 0;
    private long mSourceDuration = 0;

    public PlanController(@NonNull IView iView) {
        mPlanView = iView;
    }

    public void createPlayer() {
        mAliyunPlayerHelper = AliyunPlayerHelper.getInstance();
        mAliyunPlayerHelper.createPlayer(mPlanView.getContext());

        mAliyunPlayerHelper.setCustomPlayerObserver(new CustomPlayerObserver() {
            @Override
            public void onAdvPrepared() {
                if (mPlanView instanceof PlanAActivity) {
                    if (mPlayBackStatus == PlayBackStatus.FREE) {
                        mPlayBackStatus = PlayBackStatus.ADV;
                        startAdvVideo();
                    }
                } else {
                    if (mAdvDuration == 0) {
                        mAdvDuration = mAliyunPlayerHelper.getAdvDuration();
                        mPlanView.setAdvDuration(mAdvDuration);
                        planBCalculationDuration();
                    }
                }
            }

            @Override
            public void onSourcePrepared() {
                if (mPlanView instanceof PlanBActivity) {
                    if (mSourceDuration == 0) {
                        mSourceDuration = mAliyunPlayerHelper.getSourceDuration();
                        mPlanView.setSourceDuration(mSourceDuration);
                        planBCalculationDuration();
                    }
                }
            }

            @Override
            public void onAdvRenderingStart() {
                if (mPlanView instanceof PlanAActivity) {
                    mAliyunPlayerHelper.prepareSource();
                }
            }

            @Override
            public void onSourceRenderingStart() {
                mAliyunPlayerHelper.prepareAdv();
            }

            @Override
            public void onAdvInfo(InfoBean infoBean) {
                if (infoBean.getCode() == InfoCode.CurrentPosition) {
                    long progress = infoBean.getExtraValue();
                    if (mPlanView instanceof PlanBActivity) {
                        if (mPlayBackStatus == PlayBackStatus.ENDING_ADV) {
                            progress = mAdvDuration + mSourceDuration + infoBean.getExtraValue();
                        }
                    }
                    mPlanView.setAdvSeekBarProgress(progress);
                } else if (infoBean.getCode() == InfoCode.BufferedPosition) {
                    if (mPlanView instanceof PlanAActivity) {
                        mPlanView.setAdvSeekBarSecondProgress(infoBean.getExtraValue());
                    }
                }
            }

            @Override
            public void onSourceInfo(InfoBean infoBean) {
                if (infoBean.getCode() == InfoCode.CurrentPosition) {
                    mPlanView.setSourceSeekBarProgress(mPlanView instanceof PlanBActivity ? (mAdvDuration + infoBean.getExtraValue()) : infoBean.getExtraValue());
                } else if (infoBean.getCode() == InfoCode.BufferedPosition) {
                    if (mPlanView instanceof PlanAActivity) {
                        mPlanView.setSourceSeekBarSecondProgress(infoBean.getExtraValue());
                    }
                }
            }

            @Override
            public void onAdvCompletion() {
                if (mPlayBackStatus != PlayBackStatus.ENDING_ADV) {
                    mPlayBackStatus = PlayBackStatus.SOURCE;
                    startSourceVideo();
                }
            }

            @Override
            public void onSourceCompletion() {
                mPlayBackStatus = PlayBackStatus.ENDING_ADV;
                startAdvVideo();
            }

            @Override
            public void onAdvError(ErrorInfo errorInfo) {
            }

            @Override
            public void onSourceError(ErrorInfo errorInfo) {
            }
        });
        if (mPlanView instanceof PlanBActivity) {
            mAliyunPlayerHelper.prepareSource();
        }
        mAliyunPlayerHelper.prepareAdv();
    }

    /**
     * plan-B，Total Duration = BeginningDuration * 2 + SourceDuration。
     * The Duration can only be obtained after {Player.OnPrepared()}
     */
    private void planBCalculationDuration() {
        if (mAdvDuration == 0 || mSourceDuration == 0) {
            return;
        }
        if (mPlayBackStatus == PlayBackStatus.FREE) {
            mPlayBackStatus = PlayBackStatus.ADV;
            startAdvVideo();
        }
        long totalDuration = mAdvDuration * 2 + mSourceDuration;
        ((PlanBActivity)mPlanView).setTotalDuration(totalDuration);
    }

    private void startAdvVideo() {
        if (mPlanView instanceof PlanAActivity) {
            mPlanView.setAdvDuration(mAliyunPlayerHelper.getAdvDuration());
        }
        setSurfaceHolder(null, mSurfaceHolder);
        mAliyunPlayerHelper.startAdv();
    }

    private void startSourceVideo() {
        if (mPlanView instanceof PlanAActivity) {
            mPlanView.setSourceDuration(mAliyunPlayerHelper.getSourceDuration());
        }
        setSurfaceHolder(mSurfaceHolder, null);
        mAliyunPlayerHelper.startSource();
    }

    /**
     * seek
     * 0 ~ Beginning Duration : invoke AdvPlayer.seekTo()
     * Beginning duration ~ Source Duration + Beginning Duration : invoke SourcePlayer.seekTo()
     * Source Duration + Beginning Duration ~ Ending ： invoke AdvPlayer.seekTo()
     */
    public void seekTo(int progress) {
        if (progress < mAdvDuration) {
            if(mPlayBackStatus == PlayBackStatus.ADV){
                seekAdvTo(progress);
            }
        } else if (progress > mAdvDuration && progress < mAdvDuration + mSourceDuration) {
            if(mPlayBackStatus == PlayBackStatus.SOURCE){
                seekSourceTo((int) (progress - mAdvDuration));
            }
        } else {
            if(mPlayBackStatus == PlayBackStatus.ENDING_ADV){
                seekAdvTo((int) (progress - mSourceDuration - mAdvDuration));
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
        if (mPlayBackStatus == PlayBackStatus.ADV || mPlayBackStatus == PlayBackStatus.ENDING_ADV || mPlayBackStatus == PlayBackStatus.FREE) {
            setSurfaceHolder(null, holder);
        } else {
            setSurfaceHolder(holder, null);
        }
    }

    public void surfaceChanged() {
        if (mPlayBackStatus == PlayBackStatus.SOURCE) {
            mAliyunPlayerHelper.sourceSurfaceChanged();
        } else if (mPlayBackStatus == PlayBackStatus.ADV || mPlayBackStatus == PlayBackStatus.ENDING_ADV) {
            mAliyunPlayerHelper.advSurfaceChanged();
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
        if(mPlayBackStatus == PlayBackStatus.ADV || mPlayBackStatus == PlayBackStatus.ENDING_ADV){
            mAliyunPlayerHelper.startAdv();
        }else if(mPlayBackStatus == PlayBackStatus.SOURCE){
            mAliyunPlayerHelper.startSource();
        }
    }

    public void onPause() {
        if(mPlayBackStatus == PlayBackStatus.ADV || mPlayBackStatus == PlayBackStatus.ENDING_ADV){
            mAliyunPlayerHelper.pauseAdv();
        }else if(mPlayBackStatus == PlayBackStatus.SOURCE){
            mAliyunPlayerHelper.pauseSource();
        }
    }

    public void release() {
        mPlanView = null;
        mAliyunPlayerHelper.release();
    }
}
