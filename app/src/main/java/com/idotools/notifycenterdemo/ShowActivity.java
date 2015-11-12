package com.idotools.notifycenterdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.idotools.notifycenterdemo.Interface.JsInterface;

import java.util.Timer;

public class ShowActivity extends AppCompatActivity { //implements SwipeRefreshLayout.OnRefreshListener{
    int MSG_TIMEOUT=-1;
    Context mContext = this;
    WebView webView;
    ProgressBar progressBar;

    String msgid;
    String baseUrl = "https://192.168.1.79:8088/";
    String errorUrl = "file:///android_asset/errorPage.html";
    String finalUrl = "";

    String intentType; //list or article. get from intent

    JsInterface jsInterface;
    Timer timer;
    long timeout = 5000;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);


        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        webView.getSettings().setDatabaseEnabled(true);
        String cacheDirPath =webView.getContext().getDir("database",MODE_PRIVATE).getPath();
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

        });


        //show H5 pages
        Intent intent = getIntent();
        //showPicFlag = intent.getBooleanExtra("showPicFlag", true);
        intentType = intent.getStringExtra("type");
        if (intentType.equals("article")) {
            msgid = intent.getStringExtra("msgid");
            finalUrl = getArticleUrl(msgid);
            webView.loadUrl(finalUrl);
        } else if (intentType.equals("list")) {
            finalUrl = getListUrl();
            webView.loadUrl(finalUrl);
        }


    }

    private String getArticleUrl(String msgid) {
        String articleUrl = baseUrl + "article?articleId=" + msgid
                + "&userId=" +MyApplication.getUserId()
                + "&languageCode="+MyApplication.getLanguageInfo();
        Log.d("url", articleUrl);
        return articleUrl;
    }

    private String getListUrl() {
        String articleUrl = baseUrl + "articleList?"
                + "&userId=" +MyApplication.getUserId()
                + "&languageCode="+MyApplication.getLanguageInfo();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (intentType.equals("article")) {
                        if (webView.getUrl().equals(getListUrl())) {
                            finish();
                        } else {
                            webView.loadUrl(getListUrl());
                            intentType = "list";
                        }

                    } else {
                        if (jsInterface.isCloseWebView) {
                            finish();
                        } else {
                            webView.loadUrl("javascript:closeLayer()");
                        }

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

    @JavascriptInterface
    public String reload() {
        return finalUrl;

    }
}