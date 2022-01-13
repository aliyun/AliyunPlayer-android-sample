package com.aliyun.advert;

import androidx.annotation.NonNull;

import com.aliyun.advert.listener.CustomPlayerObserver;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;

public class PlanAController {

    private final PlanAActivity mPlanAActivity;
    private AliyunPlayerHelper mAliyunPlayerHelper;
    private int[] mAdvPosition = new int[]{};

    public PlanAController(@NonNull PlanAActivity planAActivity){
        mPlanAActivity = planAActivity;
    }

    public void createPlayer() {
        mAliyunPlayerHelper = AliyunPlayerHelper.getInstance();
        mAliyunPlayerHelper.createPlayer(mPlanAActivity);

        mAliyunPlayerHelper.setCustomPlayerObserver(new CustomPlayerObserver() {
            @Override
            public void onAdvPrepared() {

            }

            @Override
            public void onSourcePrepared() {
            }

            @Override
            public void onAdvRenderingStart() {
            }

            @Override
            public void onSourceRenderingStart() {
            }

            @Override
            public void onAdvInfo(InfoBean infoBean) {
            }

            @Override
            public void onSourceInfo(InfoBean infoBean) {
            }

            @Override
            public void onAdvCompletion() {
            }

            @Override
            public void onSourceCompletion() {
            }

            @Override
            public void onAdvError(ErrorInfo errorInfo) {
            }

            @Override
            public void onSourceError(ErrorInfo errorInfo) {
            }
        });
    }
}
