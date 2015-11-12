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

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

public class PullService extends Service {
    private static final String TAG = PullService.class.getSimpleName();
    Context mContext = this;

    //handler & runnable
    private Handler handler = null;
    private Runnable runnable;


    int minUpdateInterval[] = new int[24];
    int maxUpdateInterval[] = new int[24];
    int connectionTimeout;
    int reconnectInterval;
    int maxReconnectInterval;
    boolean keepAlive;
    //lastTimestamp
    long lastTimeStamp = 0;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spEditor;

    int notifyid = 0;


    String notifyUrl = "https://192.168.1.79:8088/getNotice";

    String strategyUrl = "https://192.168.1.79:8088/getStrategy";
    static StrategyResult strategyResult;
    static NotifyResult notifyResult;

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
        //sharedpreference
        sharedPreferences = getSharedPreferences("updateStrategy", Context.MODE_PRIVATE);
        spEditor = sharedPreferences.edit();
        getStrategyAndPull(true);

        handler = new Handler();
        runnable = new Runnable(){
            @Override
            public void run() {
                pullMessageByStrategy();
                handler.postDelayed(this, getRandomInterval());//

                //debug only
                //pullMessageNow();
                //handler.postDelayed(this, 10000);//
            }
        };

        handler.postDelayed(runnable, 5000);//


    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"service destroyed");
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    private void getStrategyAndPull(boolean needRefresh){
        if(needRefresh){
            new PullStrategyTask().execute(strategyUrl);
        }else {
            //更新策略
            for (int i = 0; i < 24; i++) {
                minUpdateInterval[i] = sharedPreferences.getInt("minUpdateInterval" + i, 0);
                maxUpdateInterval[i] = sharedPreferences.getInt("maxUpdateInterval" + i, 0);
            }

            connectionTimeout = sharedPreferences.getInt("connectionTimeout", 0);
            reconnectInterval = sharedPreferences.getInt("reconnectInterval", 0);
            maxReconnectInterval = sharedPreferences.getInt("maxReconnectInterval", 0);
            keepAlive = sharedPreferences.getBoolean("keepAlive", false);
            //lastTimestamp
            lastTimeStamp = sharedPreferences.getLong("lastTimeStamp", 0);

            pullMessageByStrategy();
        }
    }
    private void pullMessageByStrategy(){
        long interval = System.currentTimeMillis() - lastTimeStamp;

        if (interval >= getRandomInterval()){
            new PullNotifyTask().execute(notifyUrl);
        }

    }

    public void pullMessageNow(){
        new PullNotifyTask().execute(notifyUrl);

    }

    private long getRandomInterval(){
        int h = getHour();
        Random random = new Random();
        int max = maxUpdateInterval[h];
        int min = minUpdateInterval[h];
        long randomInterval = ((long)random.nextInt(max - min + 1) + min) * 60 * 1000;
        return  randomInterval;
    }


    class PullStrategyTask extends AsyncTask<String,Void,String> {

        @Override

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            int status;
            if (result != null) {
                Log.d("PullStrategyTask",result);
                strategyResult = GsonTools.getResult(result, StrategyResult.class);
                status = strategyResult.getStatus();
            } else {
                status = 204;
            }

            if(status == 200) {
                for (int i=0 ;i<24;i++) {
                    minUpdateInterval[i] = strategyResult.getMinUpdateInterval(i);
                    spEditor.putInt("minUpdateInterval" + i , minUpdateInterval[i]);

                    maxUpdateInterval[i] = strategyResult.getMaxUpdateInterval(i);
                    spEditor.putInt("maxUpdateInterval" + i, maxUpdateInterval[i]);
                }

                connectionTimeout = strategyResult.getConnectionTimeout();
                spEditor.putInt("connectionTimeout", connectionTimeout);

                reconnectInterval = strategyResult.getReconnectInterval();
                spEditor.putInt("reconnectInterval", reconnectInterval);

                maxReconnectInterval = strategyResult.getMaxReconnectInterval();
                spEditor.putInt("maxReconnectInterval", maxReconnectInterval);

                keepAlive = strategyResult.getKeepAlive();
                spEditor.putBoolean("keepAlive", keepAlive);

                spEditor.commit();
            } else {
                Log.d(TAG,String.valueOf(status));
            }

        }


        @Override
        protected String doInBackground(String... params) {
            try {
                String result = HttpUtils.postResponse(params[0], lastTimeStamp);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override

        protected void onPreExecute() {
            super.onPreExecute();


        }
    }


    class PullNotifyTask extends AsyncTask<String,Void,String> {

        @Override

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            int status;
            if (result != null) {
                Log.d("PullNotifyTask",result);
                notifyResult = GsonTools.getResult(result, NotifyResult.class);
                lastTimeStamp = notifyResult.getTimestamp();
                status = notifyResult.getStatus();
            } else {
                status = 204;
            }

            if(status == 200) {
                FinalMessage finalMessage = notifyResult.getFinalMessage();
                showNotification(finalMessage);
            } else {
                Log.d(TAG,String.valueOf(status));
            }
        }


        @Override
        protected String doInBackground(String... params) {
            try {
                String result = HttpUtils.postResponse(params[0], lastTimeStamp);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override

        protected void onPreExecute() {
            super.onPreExecute();


        }
    }


    private int getHour(){
        Calendar c = Calendar.getInstance();
        int Hr24=c.get(Calendar.HOUR_OF_DAY);
        return Hr24;
    }

    private void showNotification(FinalMessage finalMessage){
        int id = notifyid;
        notifyid++;//通知id

        String title = finalMessage.getTitle();
        String contentAbstract = finalMessage.getContentAbstract();
        String icon = finalMessage.getIcon();
        String msgid = finalMessage.getId();
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
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        notification.contentIntent =pendingIntent;


        //remoteviewt
        RemoteViews remoteViews =
                new RemoteViews(this.getPackageName(), R.layout.notification);
        notification.contentView = remoteViews;
        remoteViews.setTextViewText(R.id.title,title);
        remoteViews.setTextViewText(R.id.text, contentAbstract);
        MyPicasso.getInstance(mContext).load(icon).into(remoteViews,R.id.image,id,notification);


        mNotifyMgr.notify(id, notification);



    }
}
