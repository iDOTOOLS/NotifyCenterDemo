package com.idotools.notifycenterdemo.Interface;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import com.idotools.notifycenterdemo.ImageActivity;
import com.idotools.notifycenterdemo.ShowActivity;

/**
 * Created by LvWind on 15/11/3.
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
    public int getDpi(){
        DisplayMetrics metrics =  mContext.getResources().getDisplayMetrics();
        return metrics.densityDpi;
    }

}
