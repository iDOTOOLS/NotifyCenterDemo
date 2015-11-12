package com.idotools.notifycenterdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    Context mContext = this;
    PullService mService;
    boolean mBound = false;

//    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor spEditor;

    Button button,button2,button3,button4,button5,button6;
    TextView TVtitle,TVcontentAbstract;
    ImageView IVicon;


    @Override
    protected void onStart() {
        super.onStart();
        //bind service
//        Intent intent = new Intent(MainActivity.this,PullService.class);
//
//        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mService != null) {
            unbindService(mConnection);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sharedpreference
        //sharedPreferences = getSharedPreferences("updateStrategy", Context.MODE_PRIVATE);
        //spEditor = sharedPreferences.edit();

        //init view
        setContentView(R.layout.activity_main);
        TVtitle = (TextView) findViewById(R.id.title);
        TVcontentAbstract = (TextView) findViewById( R.id.content_abstract);
        IVicon = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button6 = (Button) findViewById(R.id.button6);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "button pull clicked");
                if(mService != null)
                {
                    mService.pullMessageNow();
                } else {
                    Toast.makeText(mContext,"Start service first",Toast.LENGTH_SHORT).show();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "button stop clicked");
                Intent intent = new Intent(MainActivity.this,PullService.class);
                stopService(intent);
                mService = null;
                Toast.makeText(mContext,"Service Stopped",Toast.LENGTH_SHORT).show();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "button start clicked");
                Intent intent = new Intent(MainActivity.this,PullService.class);
                startService(intent);
                bindService(intent, mConnection, 0);
                Toast.makeText(mContext,"Service started",Toast.LENGTH_SHORT).show();

            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "button list clicked");
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                intent.putExtra("type", "list");
                startActivity(intent);
                Log.d("fontSize", String.valueOf(MyApplication.getFontSizeLevel()));

            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);

            }
        });

    }



    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            PullService.LocalBinder binder = (PullService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    //    private void postRequest(final String url){
//        RequestBody body = RequestBody.create(JSON, requestStirng);
//        final Request request = new Request.Builder().url(url).post(body).build();
//        //异步线程
//        client.newCall(request).enqueue(new Callback() {
//
//
//            @Override
//            public void onFailure(Request request, IOException e) {
//            // TODO
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                String responseString = response.body().string();
//                Log.d("responseString", responseString);
//                int statusCode = response.code();
//                //Log.d("header", response.header("Connection"));
//
//                if (statusCode == 204){
//                    notifyRequestHandler.obtainMessage(MSG_FAILURE_204, null).sendToTarget();
//                } else {
//                    if (url.equals(notifyUrl)) {
//                        NotifyResult notifyResult = GsonTools.getResult(responseString, NotifyResult.class);
//                        int status = notifyResult.getStatus();
//                        if (status == 200) {
//                            notifyRequestHandler.obtainMessage(MSG_SUCCESS_200, notifyResult).sendToTarget();
//                        } else if (status == 204) {
//                            notifyRequestHandler.obtainMessage(MSG_FAILURE_204, null).sendToTarget();
//                        } else if (status == 404) {
//                            notifyRequestHandler.obtainMessage(MSG_FAILURE_404, null).sendToTarget();
//                        } else if (status == 500) {
//                            notifyRequestHandler.obtainMessage(MSG_FAILURE_500, null).sendToTarget();
//                        }
//
//                    } else if(url.equals(strategyUrl)){
//                        StrategyResult strategyResult = gsonTools.getResult(responseString,StrategyResult.class);
//                        int status = strategyResult.getStatus();
//                        if (status == 200){
//                            strategyRequestHandler.obtainMessage(MSG_SUCCESS_200, strategyResult).sendToTarget();
//                        } else if (status == 204) {
//                            strategyRequestHandler.obtainMessage(MSG_FAILURE_204, null).sendToTarget();
//                        } else if (status == 404) {
//                            strategyRequestHandler.obtainMessage(MSG_FAILURE_404, null).sendToTarget();
//                        } else if (status == 500) {
//                            strategyRequestHandler.obtainMessage(MSG_FAILURE_500, null).sendToTarget();
//                        }
//
//                    }
//                }
//                response.body().close(); //关闭连接
//            }
//        });
//    }


//    private void showMessage(FinalMessage finalMessage){
//        int id = notifyid;
//        notifyid++;//通知id
//
//        String title = finalMessage.getTitle();
//        String contentAbstract = finalMessage.getContentAbstract();
//        Log.d("ID",finalMessage.getId());
//        long msgid = Long.parseLong(finalMessage.getId()); //信息id
//        String icon = finalMessage.getIcon();
//
//        TVtitle.setText(title);
//        TVcontentAbstract.setText(contentAbstract);
//        Picasso.with(mContext).load(icon).into(IVicon);
//
//
//        //bulid notification
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.ic_stat_notify)
//                        .setContentTitle(title)
//                        .setContentText(contentAbstract);
//        NotificationManager mNotifyMgr =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        Notification notification= mBuilder.build();
//
//
//        //remoteviewt
//        RemoteViews remoteViews =
//                new RemoteViews(this.getPackageName(), R.layout.notification);
//        notification.contentView = remoteViews;
//        remoteViews.setTextViewText(R.id.title,title);
//        remoteViews.setTextViewText(R.id.text, contentAbstract);
//        mNotifyMgr.notify(id, notification);
//
//
//        Picasso.with(mContext).load(icon).into(remoteViews,R.id.image,id,notification);
//
//
//    }
//    //忽略自签SSL证书错误
//    private static OkHttpClient getUnsafeOkHttpClient() {
//        try {
//            // Create a trust manager that does not validate certificate chains
//            final TrustManager[] trustAllCerts = new TrustManager[]{
//                    new X509TrustManager() {
//                        @Override
//                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                        }
//
//                        @Override
//                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                        }
//
//                        @Override
//                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                            return null;
//                        }
//                    }
//            };
//
//            // Install the all-trusting trust manager
//            final SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//            // Create an ssl socket factory with our all-trusting manager
//            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//            OkHttpClient okHttpClient = new OkHttpClient();
//            okHttpClient.setSslSocketFactory(sslSocketFactory);
//            okHttpClient.setHostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
//
//            return okHttpClient;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }


}
