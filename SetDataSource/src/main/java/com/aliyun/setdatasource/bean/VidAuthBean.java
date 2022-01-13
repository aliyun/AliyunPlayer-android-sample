package com.aliyun.setdatasource.bean;

import com.google.gson.annotations.SerializedName;

public class VidAuthBean {

    private boolean result;
    private String requestId;
    private String message;
    private String code;
    private VidAuthInfo data;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public VidAuthInfo getData() {
        return data;
    }

    public void setData(VidAuthInfo data) {
        this.data = data;
    }

    public static class VidAuthInfo{
        @SerializedName("videoId")
        private String videoId;
        @SerializedName("playAuth")
        private String playAuth;

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getPlayAuth() {
            return playAuth;
        }

        public void setPlayAuth(String playAuth) {
            this.playAuth = playAuth;
        }
    }
}
