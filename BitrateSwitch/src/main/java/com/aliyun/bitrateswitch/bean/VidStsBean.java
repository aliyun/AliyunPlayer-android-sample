package com.aliyun.bitrateswitch.bean;

import com.google.gson.annotations.SerializedName;

public class VidStsBean {

    private boolean result;
    private String requestId;
    private String message;
    private String code;
    private VidStsInfo data;

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

    public VidStsInfo getData() {
        return data;
    }

    public void setData(VidStsInfo data) {
        this.data = data;
    }

    public static class VidStsInfo{
        @SerializedName("accessKeyId")
        private String accessKeyId;
        @SerializedName("securityToken")
        private String securityToken;
        @SerializedName("accessKeySecret")
        private String accessKeySecret;
        @SerializedName("expiration")
        private String expiration;

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getSecurityToken() {
            return securityToken;
        }

        public void setSecurityToken(String securityToken) {
            this.securityToken = securityToken;
        }

        public String getAccessKeySecret() {
            return accessKeySecret;
        }

        public void setAccessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
        }

        public String getExpiration() {
            return expiration;
        }

        public void setExpiration(String expiration) {
            this.expiration = expiration;
        }
    }
}
