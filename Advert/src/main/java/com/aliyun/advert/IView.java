package com.aliyun.advert;

import android.content.Context;

interface IView {

    Context getContext();

    //set seekBar progress
    void setAdvSeekBarProgress(long progress);
    void setAdvSeekBarSecondProgress(long progress);

    //set seekBar secondProgress
    void setSourceSeekBarProgress(long progress);
    void setSourceSeekBarSecondProgress(long progress);

    //set video duration
    void setAdvDuration(long duration);
    void setSourceDuration(long duration);

    //video error
    void showAdvError(int errorCode,String msg);
    void showSourceError(int errorCode,String msg);

}
