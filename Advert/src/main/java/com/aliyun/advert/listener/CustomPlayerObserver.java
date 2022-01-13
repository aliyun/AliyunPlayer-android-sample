package com.aliyun.advert.listener;

import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;

public abstract class CustomPlayerObserver {

    public void onAdvPrepared(){}

    public void onSourcePrepared(){}

    public void onAdvRenderingStart(){}

    public void onSourceRenderingStart(){}

    public void onAdvInfo(InfoBean infoBean){}

    public void onSourceInfo(InfoBean infoBean){}

    public void onAdvCompletion(){}

    public void onSourceCompletion(){}

    public void onAdvError(ErrorInfo errorInfo){}

    public void onSourceError(ErrorInfo errorInfo){}
}
