package com.mall.net;

import android.webkit.JavascriptInterface;

/**
 * Created by Administrator on 2018/9/10.
 */

public class WebViewJsInterface {
    StrCallBack strCallBack;
    public interface StrCallBack{
        void doCall(String str);
    }

    public WebViewJsInterface(StrCallBack strCallBack) {
        this.strCallBack = strCallBack;
    }
    @JavascriptInterface
    public void setValue(String value){
        if (strCallBack!=null){
            strCallBack.doCall(value);
        }
    }
}
