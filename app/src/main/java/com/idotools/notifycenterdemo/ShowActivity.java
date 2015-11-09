package com.idotools.notifycenterdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.*;
import com.idotools.notifycenterdemo.Interface.JsInterface;

public class ShowActivity extends AppCompatActivity{ //implements SwipeRefreshLayout.OnRefreshListener{
    Context mContext = this;
    WebView webView;
    //SwipeRefreshLayout mSwipeLayout;

    String msgid;
    String baseUrl = "https://192.168.1.102:8089/";

    String intentType; //list or article. get from intent

    JsInterface jsInterface;

    //TODO: get this flag from sharedPreference
    boolean showPicFlag = true;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
//        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
//        mSwipeLayout.setOnRefreshListener(this);
        jsInterface = new JsInterface(mContext);
        webView.addJavascriptInterface(jsInterface, "NativeInterface");

        //set webViewClient
        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress == 100) {
//                    mSwipeLayout.setRefreshing(false);
//                    //Log.d("",webView.getUrl());
//                } else {
//                    if (!mSwipeLayout.isRefreshing())
//                        mSwipeLayout.setRefreshing(true);
//                }
//
//                super.onProgressChanged(view, newProgress);
//            }
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
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 忽略SSL错误
                handler.proceed();
            }

        });


        //show H5 pages
        Intent intent = getIntent();
        showPicFlag =intent.getBooleanExtra("showPicFlag",true);
        intentType = intent.getStringExtra("type");
        if (intentType.equals("article")) {
            msgid = intent.getStringExtra("msgid");
            webView.loadUrl(getArticleUrl(msgid));
        } else if (intentType.equals("list")){
            webView.loadUrl(getListUrl());
        }


    }

    private String getArticleUrl(String msgid){
        String articleUrl = baseUrl + "article?articleId=" + msgid + "&showPic=" +showPicFlag;
        Log.d("url",articleUrl);
        return articleUrl;
    }
    private String getListUrl() {
        String articleUrl = baseUrl + "articleList" + "?showPic=" +showPicFlag;
        Log.d("url",articleUrl);
        return articleUrl;
    }


//    @Override
//    public void onRefresh() {
//        webView.reload();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if(intentType.equals("article")){
                        if(webView.getUrl().equals(getListUrl())){
                            finish();
                        }else{
                            webView.loadUrl(getListUrl());
                            intentType  = "list";
                        }

                    }else {
                        if (jsInterface.isCloseWebView){
                            finish();
                        }else {
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
}