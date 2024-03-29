package com.idotools.notifycenterdemo.Interface;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import com.idotools.notifycenterdemo.ImageActivity;
import com.idotools.notifycenterdemo.MyApplication;

/**
 * Created by LvWind on 15/11/3.
 * JavaScript Interface
 */
public class JsInterface {
    Context mContext;
    boolean closeLayerFlag;
    public boolean isCloseWebView =true;
    public JsInterface(Context c){
        mContext = c;
    }

    @JavascriptInterface
    public void loadLargePic(String url){
        Intent intent = new Intent(mContext , ImageActivity.class);
        intent.putExtra("imageurl",url);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void loadLargePic(String[] urls, int position){
        Intent intent = new Intent(mContext , ImageActivity.class);
        intent.putExtra("imageurls",urls);
        intent.putExtra("clickPosition",position);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void setIsCloseWebView(boolean isCloseWebView){
        this.isCloseWebView = isCloseWebView;
    }

    @JavascriptInterface
    public void closeLayer(boolean flag){
        closeLayerFlag = flag;
    }
    @JavascriptInterface
    public int getWidthDp(){
        DisplayMetrics metrics =  mContext.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        float density = metrics.density;
        int height = metrics.heightPixels;
        if (height < width){
            int t = height;
            height = width;
            width = t;
        }

        int widthDp =(int) (width/density +0.5f);
        return widthDp;
    }
    @JavascriptInterface
    public String  userId(){
        return MyApplication.getUserId();
    }

    @JavascriptInterface
    public  String languageCode(){
        return MyApplication.getLanguageInfo();
    }

    @JavascriptInterface
    public boolean isPicShow(){
        return MyApplication.getShowPicFlag();
    }

    @JavascriptInterface
    public  int fontSizeLevel(){
        return MyApplication.getFontSizeLevel();
    }

    @JavascriptInterface
    public void showErrorMessage(String msg){
        Toast.makeText(MyApplication.getAppContext(),msg,Toast.LENGTH_SHORT).show();
    }

}
