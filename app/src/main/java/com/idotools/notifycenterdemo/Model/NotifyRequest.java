package com.idotools.notifycenterdemo.Model;

import com.idotools.notifycenterdemo.MyApplication;

/**
 * Created by LvWind on 15/10/29.
 */
public class NotifyRequest {
    private long lastTimestamp =MyApplication.getLastTimestamp();
    private String secretKey = "YKTnw55hQBZBnc1d";
    private String districtCode = "110105";
    private String userId = MyApplication.getUserId(); //TODO get from SDK
    private String locale = MyApplication.getLanguageInfo();

    public NotifyRequest(){
    }
    public NotifyRequest(long lastTimestamp){
        this.lastTimestamp = lastTimestamp;
    }

    public long getTimestamp() {
        return lastTimestamp;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setTimestamp(long timestamp) {
        this.lastTimestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
