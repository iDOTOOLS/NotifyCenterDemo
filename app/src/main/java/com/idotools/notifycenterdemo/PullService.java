package com.idotools.notifycenterdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import com.idotools.notifycenterdemo.Model.FinalMessage;
import com.idotools.notifycenterdemo.Model.NotifyResult;
import com.idotools.notifycenterdemo.Model.StrategyResult;
import com.idotools.notifycenterdemo.Tools.GsonTools;
import com.idotools.notifycenterdemo.Tools.HttpUtils;
import com.idotools.notifycenterdemo.Tools.MyPicasso;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Random;
/**
 * Created by LvWind on 15/10/28.
 * the main service of pull messages and strategy
 */
public class PullService extends Service {
    private static final String TAG = PullService.class.getSimpleName();
    private Context mContext = this;

    //handler & runnable
    private Handler handler = null;
    private Runnable runnable;


    private int minUpdateInterval[] = new int[24];
    private int maxUpdateInterval[] = new int[24];
    private int socketTimeout;
    private int reconnectInterval = 0;
    private int maxReconnectInterval;
    private boolean keepAlive;
    //lastTimestamp
    private long lastTimeStamp;

    private int reconnectCountStrategy = 0;
    private int reconnectCountNotify = 0;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;

    private int notifyid = 0;


    private static final String notifyUrl = "https://data3.idotools.com:10325/getNotice";
    private static final String strategyUrl = "https://data3.idotools.com:10325/getStrategy";

    private static StrategyResult strategyResult;
    private static NotifyResult notifyResult;

    private AsyncTask retryTask;

    public PullService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public class LocalBinder extends Binder{
        PullService getService() {
            return PullService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("updateStrategy", Context.MODE_PRIVATE);
        spEditor = sharedPreferences.edit();

        /**get default strategy in sharedPreference */
        getStrategy(false);

        handler = new Handler();
        runnable = new Runnable(){
            @Override
            public void run() {
                pullMessageByStrategy();
                getStrategy(true);
                handler.postDelayed(this, getRandomInterval());
            }
        };

        handler.postDelayed(runnable, getRandomInterval());//

    }


    @Override
    public void onDestroy() {
        Log.d(TAG,"service destroyed");
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }


    /**
     * get strategy from server or local
     * @param needRefresh flag of get new strategy from server
     * */
    private void getStrategy(boolean needRefresh){
        if(needRefresh){
            if (MyApplication.hasNetworkConnection()) {
                new PullStrategyTask().execute(0);
            } else {
                Log.d("getStrategy","no network");
            }
        }
        //更新策略
        for (int i = 0; i < 24; i++) {
            minUpdateInterval[i] = sharedPreferences.getInt("minUpdateInterval" + i, 1);
            maxUpdateInterval[i] = sharedPreferences.getInt("maxUpdateInterval" + i, 3);
        }

        socketTimeout = sharedPreferences.getInt("socketTimeout", 30);
        reconnectInterval = sharedPreferences.getInt("reconnectInterval", 60);
        maxReconnectInterval = sharedPreferences.getInt("maxReconnectInterval", 3600);
        keepAlive = sharedPreferences.getBoolean("keepAlive", false);
        //lastTimestamp
        lastTimeStamp = sharedPreferences.getLong("lastTimeStamp", 0);
    }

    /**
     * pull messages obey the strategy
     * */
    private void pullMessageByStrategy(){
        lastTimeStamp = sharedPreferences.getLong("lastTimeStamp",0);
        long interval = System.currentTimeMillis() - lastTimeStamp;

        if (MyApplication.hasNetworkConnection()) {
            if (interval >= getRandomInterval()) {
                if (retryTask != null){
                    retryTask.cancel(true);
                    new PullNotifyTask().execute(0);
                } else {
                    new PullNotifyTask().execute(0);
                }
            }
        } else {
            Log.d("pullMessageByStrategy","no network");
        }

    }

    /**
     * pull messages immediately
     * for test
     * */
    public  void pullMessageNow(){
        lastTimeStamp = sharedPreferences.getLong("lastTimeStamp",0);
        if (MyApplication.hasNetworkConnection()) {
            new PullNotifyTask().execute(0);
            new PullStrategyTask().execute(0);
        } else {
            Log.d("pullMessageNow","no network");
        }
    }

    /**
     * retrying pull messages
     * @param count retry count
     * */
    public void pullMessageRetry(int count){
        int interval = (int)Math.pow(2,count-1)* reconnectInterval;
        if (interval > maxReconnectInterval){
            interval = maxReconnectInterval;
        }

        if (MyApplication.hasNetworkConnection()) {
            retryTask = new PullNotifyTask().execute(interval);
            Log.i("pullMessageRetry", "retry count=" + count);
            Log.i("pullMessageRetry", "retry Interval=" + interval);
        } else {
            Log.d("pullMessageRetry","no network");
        }
    }

    /**
     * get the interval time randomly between min and max
     * by minutes
     * */
    private long getRandomInterval(){
        int h = getHour();
        Random random = new Random();
        int max = maxUpdateInterval[h];
        int min = minUpdateInterval[h];


        if (min > max){ //人工配置时间间隔失误,没有配对时
            return max * 60 *1000;

        }else{
            long randomInterval = ((long)random.nextInt(max - min + 1) + min) * 60 * 1000;
            return  randomInterval;
        }


    }


    /**
     * Async task of pulling strategy
     * @Integer the retry interval time
     * */
    class PullStrategyTask extends AsyncTask<Integer,Void,String> {

        @Override

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int status;

            if (result != null){
                if(result.equals("UnknownHost")){
                    status = 404;
                } else{
                    Log.d("PullStrategyTask",result);
                    strategyResult = GsonTools.getResult(result, StrategyResult.class);
                    status = strategyResult.getStatus();
                }
            } else {
                status = 204;
            }


            if(status == 200) {
                reconnectCountStrategy=0;
                for (int i=0 ;i<24;i++) {
                    minUpdateInterval[i] = strategyResult.getMinUpdateInterval(i);
                    spEditor.putInt("minUpdateInterval" + i , minUpdateInterval[i]);

                    maxUpdateInterval[i] = strategyResult.getMaxUpdateInterval(i);
                    spEditor.putInt("maxUpdateInterval" + i, maxUpdateInterval[i]);
                }

                socketTimeout = strategyResult.getSocketTimeout();
                spEditor.putInt("connectionTimeout", socketTimeout);

                reconnectInterval = strategyResult.getReconnectInterval();
                spEditor.putInt("reconnectInterval", reconnectInterval);

                maxReconnectInterval = strategyResult.getMaxReconnectInterval();
                spEditor.putInt("maxReconnectInterval", maxReconnectInterval);

                keepAlive = strategyResult.getKeepAlive();
                spEditor.putBoolean("keepAlive", keepAlive);

                spEditor.commit();
            } else if (status == 204){
                reconnectCountStrategy=0;
                Log.d(TAG,"204: no update");
            } else if (status == 404){
                reconnectCountStrategy++;
                //pullMessageRetry(reconnectCountStrategy);
                Log.d(TAG,"404");
            } else {
                reconnectCountStrategy=0;
                Log.d(TAG,"error status: " + status);
            }

        }


        @Override
        protected String doInBackground(Integer... params) {
            int interval = params[0];
            //重连延时
            try {
                Thread.sleep(interval *1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                String result = HttpUtils.postResponse(strategyUrl, lastTimeStamp);
                return result;
            } catch (SSLHandshakeException e){
                Log.d(TAG,"SSL Handshake error");
                return "UnknownHost";
            }
            catch (UnknownHostException e){
                Log.d(TAG,"connection error");
                return "UnknownHost";
            } catch (IOException e) {

                Log.d(TAG,"IOException");
                e.printStackTrace();
                return null;
            }
        }

        @Override

        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    /**
     * Async task of pulling notification
     * @Integer the retry interval time
     * */
    class PullNotifyTask extends AsyncTask<Integer,Void,String> {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int status;

            if (result != null){
                if(result.equals("UnknownHost")){
                    status = 404;
                } else {
                    Log.d("PullNotifyTask",result);
                    notifyResult = GsonTools.getResult(result, NotifyResult.class);
                    lastTimeStamp = notifyResult.getTimestamp();
                    spEditor.putLong("lastTimeStamp",lastTimeStamp).commit();
                    status = notifyResult.getStatus();
                }
            } else {
                status = 204;
            }


            if(status == 200) {
                FinalMessage finalMessage = notifyResult.getFinalMessage();
                showNotification(finalMessage);
                reconnectCountNotify =0;
            } else if (status == 204){
                reconnectCountNotify =0;
                Log.d(TAG,"204: no update");
            } else if (status == 404){
                reconnectCountNotify++;
                pullMessageRetry(reconnectCountNotify);
                Log.d(TAG,"404");
            } else {
                reconnectCountNotify=0;
                Log.d(TAG,"error status: " + status);
            }
        }


        @Override
        protected String doInBackground(Integer... params) {
            int interval = params[0];
            Log.i("PullNotifyTask","interval:" + interval);
            //重连延时
            try {
                Thread.sleep(interval *1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            try {
                String result = HttpUtils.postResponse(notifyUrl, lastTimeStamp);
                return result;

            } catch (SSLHandshakeException e){
                Log.d(TAG,"SSL Handshake error");
                return "UnknownHost";
            }catch (UnknownHostException e){
                Log.d(TAG,"connection error");
                //e.printStackTrace();
                return "UnknownHost";
            } catch (IOException e) {

                Log.d(TAG,"IOException");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    /**
     * get the hour of the day
     * 24-hours format
     * */
    private int getHour(){
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * build notifications and show
     * */
    private void showNotification(FinalMessage finalMessage){

        int id = notifyid;
        notifyid++;//通知id
        String title = finalMessage.getTitle();
        String contentAbstract = finalMessage.getContentAbstract();
        String icon = finalMessage.getIcon();
        String msgid = finalMessage.getId();

        String lastMsgId = sharedPreferences.getString("lastMsgId","");
        if (msgid == null || lastMsgId.equals(msgid)){
            return;
        } else {
            spEditor.putString("lastMsgId", msgid);
        }


        //bulid notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_notify)
                        .setContentTitle(title)
                        .setContentText(contentAbstract);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification= mBuilder.build();

        notification.flags = Notification.FLAG_AUTO_CANCEL;

        Intent intent = new Intent(mContext, ShowActivity.class);
        intent.putExtra("type","article");
        intent.putExtra("msgid", msgid);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,id,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        notification.contentIntent =pendingIntent;


        //remoteView
        RemoteViews remoteViews =
                new RemoteViews(this.getPackageName(), R.layout.notification);
        notification.contentView = remoteViews;
        remoteViews.setTextViewText(R.id.title,title);
        remoteViews.setTextViewText(R.id.text, contentAbstract);
        MyPicasso.getInstance(mContext).load(icon).into(remoteViews,R.id.image,id,notification);

        mNotifyMgr.notify(id, notification);
    }
}
