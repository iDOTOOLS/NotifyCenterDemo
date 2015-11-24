package com.idotools.notifycenterdemo.Model;

/**
 * Created by LvWind on 15/10/29.
 * class of result of strategy request
 */
public class StrategyResult {
    private MinUpdateInterval minUpdateInterval;
    private MaxUpdateInterval maxUpdateInterval;
    private int socketTimeout;
    private int reconnectInterval;
    private int maxReconnectInterval;
    private boolean keepalive;
    private int status;

    public StrategyResult(){}

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getMaxReconnectInterval() {
        return maxReconnectInterval;
    }

    public int getReconnectInterval() {
        return reconnectInterval;
    }

    public boolean getKeepAlive() {
        return keepalive;
    }

    public int getStatus() {
        return status;
    }

    public int getMaxUpdateInterval(int t) {
        if(maxUpdateInterval.getTime(t) != 0){
            return maxUpdateInterval.getTime(t);
        } else {
            return maxUpdateInterval.getTimeDefault();
        }
    }

    public int getMinUpdateInterval(int t) {
        if(minUpdateInterval.getTime(t) != 0){
            return minUpdateInterval.getTime(t);
        } else {
            return minUpdateInterval.getTimeDefault();
        }
    }

    private class MinUpdateInterval{
        private int time0;
        private int time1;
        private int time2;
        private int time3;
        private int time4;
        private int time5;
        private int time6;
        private int time7;
        private int time8;
        private int time9;
        private int time10;
        private int time11;
        private int time12;
        private int time13;
        private int time14;
        private int time15;
        private int time16;
        private int time17;
        private int time18;
        private int time19;
        private int time20;
        private int time21;
        private int time22;
        private int time23;
        private int timeDefault;

        public int getTimeDefault() {
            return timeDefault;
        }

        public int getTime(int t){
            switch (t){
                case 0:     return time0;
                case 1:	    return time1;
                case 2:	    return time2;
                case 3:	    return time3;
                case 4:	    return time4;
                case 5:	    return time5;
                case 6:	    return time6;
                case 7:	    return time7;
                case 8:	    return time8;
                case 9:	    return time9;
                case 10:    return time10;
                case 11:	return time11;
                case 12:	return time12;
                case 13:	return time13;
                case 14:	return time14;
                case 15:	return time15;
                case 16:	return time16;
                case 17:	return time17;
                case 18:	return time18;
                case 19:	return time19;
                case 20:	return time20;
                case 21:	return time21;
                case 22:	return time22;
                case 23:	return time23;
                default:    return timeDefault;
            }
        }
    }
    private class MaxUpdateInterval{
        private int time0;
        private int time1;
        private int time2;
        private int time3;
        private int time4;
        private int time5;
        private int time6;
        private int time7;
        private int time8;
        private int time9;
        private int time10;
        private int time11;
        private int time12;
        private int time13;
        private int time14;
        private int time15;
        private int time16;
        private int time17;
        private int time18;
        private int time19;
        private int time20;
        private int time21;
        private int time22;
        private int time23;
        private int timeDefault;

        public int getTimeDefault() {
            return timeDefault;
        }

        public int getTime(int t){
            switch (t){
                case 0:     return time0;
                case 1:	    return time1;
                case 2:	    return time2;
                case 3:	    return time3;
                case 4:	    return time4;
                case 5:	    return time5;
                case 6:	    return time6;
                case 7:	    return time7;
                case 8:	    return time8;
                case 9:	    return time9;
                case 10:    return time10;
                case 11:	return time11;
                case 12:	return time12;
                case 13:	return time13;
                case 14:	return time14;
                case 15:	return time15;
                case 16:	return time16;
                case 17:	return time17;
                case 18:	return time18;
                case 19:	return time19;
                case 20:	return time20;
                case 21:	return time21;
                case 22:	return time22;
                case 23:	return time23;
                default:    return timeDefault;
            }
        }
    }

}
