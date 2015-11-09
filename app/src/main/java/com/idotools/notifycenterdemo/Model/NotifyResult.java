package com.idotools.notifycenterdemo.Model;

/**
 * Created by LvWind on 15/10/28.
 */
public class NotifyResult {
    private FinalMessage message;
    private long timestamp;
    private int status;


    public FinalMessage getFinalMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
