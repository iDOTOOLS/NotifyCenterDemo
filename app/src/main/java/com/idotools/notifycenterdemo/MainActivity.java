package com.idotools.notifycenterdemo;

import android.app.ActivityManager;
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

    Button button,button2,button3,button4,button6;
    TextView TVtitle,TVcontentAbstract;
    ImageView IVicon;


    @Override
    protected void onStart() {
        super.onStart();
        //bind service
        Intent intent = new Intent(MainActivity.this,PullService.class);
        bindService(intent, mConnection, 0);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(mConnection);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
