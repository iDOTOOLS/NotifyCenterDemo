package com.idotools.notifycenterdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.idotools.notifycenterdemo.Interface.JsInterface;

/**
 * Created by LvWind on 15/10/28.
 * Activity of show pictures with viewPager
 */
public class ShowActivity extends AppCompatActivity { //implements SwipeRefreshLayout.OnRefreshListener{
    private int MSG_TIMEOUT = -1;
    private Context mContext = this;
    private WebView webView;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;

    private String msgid;
    private String baseUrl = "https://data3.idotools.com:10325/notificationCenter";
    private String errorUrl = "file:///android_asset/errorPage.html";
    private String finalUrl = "";

    private String intentType; //list or article. get from intent

    private JsInterface jsInterface;
    private long timestamp;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        sp = getSharedPreferences("updateStrategy", MODE_PRIVATE);
        editor = sp.edit();

        relativeLayout = (RelativeLayout) findViewById(R.id.show_activity_relative_layout);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        webView.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = webView.getContext().getDir("database",MODE_PRIVATE).getAbsolutePath();
        webView.getSettings().setDatabasePath(cacheDirPath);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAppCachePath(cacheDirPath);


        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);

//        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
//        mSwipeLayout.setOnRefreshListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        jsInterface = new JsInterface(mContext);
        webView.addJavascriptInterface(jsInterface, "NativeInterface");
        webView.addJavascriptInterface(this, "errorPage");

        //set webViewClient
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            webView.setVisibility(View.VISIBLE);
                        }
                    },300);


                    //mSwipeLayout.setRefreshing(false);
                    //Log.d("",webView.getUrl());
                } else {
                    webView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
//                    if (!mSwipeLayout.isRefreshing())
//                        mSwipeLayout.setRefreshing(true);
                }

                super.onProgressChanged(view, newProgress);
            }

        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                    return false;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                load404page();
                //super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 忽略SSL错误
                handler.proceed();
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }
        });


        //show H5 pages
        Intent intent = getIntent();
        intentType = intent.getStringExtra("type");
        if (intentType.equals("article")) {
            msgid = intent.getStringExtra("msgid");
            finalUrl = getArticleUrl(msgid);
            webView.loadUrl(finalUrl);
        } else if (intentType.equals("list")) {  //主动进入列表

            finalUrl = getListUrl();
            webView.loadUrl(finalUrl);
        }
        //update list timestamp
        timestamp = System.currentTimeMillis();
        editor.putLong("listTimeStamp",timestamp).apply();


    }

    private String getArticleUrl(String msgid) {
        String articleUrl = baseUrl + "?action=article"
                + "&articleId=" + msgid
                + "&userId=" + MyApplication.getUserId()
                + "&languageCode="+ MyApplication.getLanguageInfo();
        Log.d("url", articleUrl);
        return articleUrl;
    }

    private String getListUrl() {
        String articleUrl = baseUrl + "?action=articleList"
                + "&userId=" + MyApplication.getUserId()
                + "&languageCode="+ MyApplication.getLanguageInfo();
        Log.d("url", articleUrl);
        return articleUrl;
    }
    private void load404page(){
        webView.loadUrl(errorUrl + "?deviceLang=" + MyApplication.getLanguageInfo());
    }


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TIMEOUT){
                load404page();
            }
        }
    };


    /**
     * rewrite the back key press event
     * based on the return value of jsInterface
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:

                    if (jsInterface.isCloseWebView) {
                        finish();
                    } else {
                        webView.loadUrl("javascript:closeLayer()");
                    }
                    return false;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        msgid = intent.getStringExtra("msgid");
        finalUrl = getArticleUrl(msgid);
        webView.loadUrl(finalUrl);
    }

    @JavascriptInterface
    public String reload() {
        return finalUrl;

    }


    /**
     *A fix of the exception
     *android.view.WindowLeaked: Activity has leaked window android.widget.ZoomButtonsController$Container that was originally added here
     *http://stackoverflow.com/questions/27254570/android-view-windowleaked-activity-has-leaked-window-android-widget-zoombuttons
     **/
    @Override
    public void finish(){
        relativeLayout.removeView(webView);
        super.finish();
    }

}