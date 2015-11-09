package com.idotools.notifycenterdemo.Model;

/**
 * Created by LvWind on 15/10/29.
 */
public class NotifyRequest {
    private long lastTimestamp = 0;
    private String secretKey = "YKTnw55hQBZBnc1d";
    private String districtCode = "110105";

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
}
