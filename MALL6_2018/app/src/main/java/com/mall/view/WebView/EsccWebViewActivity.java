package com.mall.view.WebView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lin.component.CustomProgressDialog;
import com.mall.BasicActivityFragment.BasicActivity;
import com.mall.MessageEvent;
import com.mall.net.Web;
import com.mall.net.WebViewJsInterface;
import com.mall.util.Util;
import com.mall.view.App;
import com.mall.view.MoreTextView;
import com.mall.view.R;
import com.mall.view.carMall.OrderResultActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

public class EsccWebViewActivity extends BasicActivity implements WebViewJsInterface.StrCallBack{


    @BindView(R.id.escc_web_view)
    public WebView mWebView;
    @BindView(R.id.toback)
    public MoreTextView toback;
    CustomProgressDialog dialog;

    @Override
    public int getContentViewId() {
        return R.layout.escc_webview;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        final String orderId = intent.getStringExtra("orderId");
        final String date_order = intent.getStringExtra("date_order");

        toback.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);


        //支持js
        settings.setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.addJavascriptInterface(new WebViewJsInterface(this),"webviewcall");
        //如果不设置WebViewClient，请求会跳转系统浏览器
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e("开始加载了","开始加载了");
//                if (dialog==null){
//                    Log.e("dialog","dialog");
//                    dialog = CustomProgressDialog.showProgressDialog( EsccWebViewActivity.this,"正在加载...");
//                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("现在加载的页面",url);
                dialog = CustomProgressDialog.showProgressDialog( EsccWebViewActivity.this,"正在加载...");
                if (url.contains(Web.esccurl+"/escc_pay/CheckStatus.aspx")){
                    Util.showIntent(EsccWebViewActivity.this, OrderResultActivity.class, new String[]{"result", "number_order", "date_order"}, new String[]{"0", orderId, date_order});
                    finish();
                }
                return super.shouldOverrideUrlLoading(view,url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("加载结束了","加载结束了");
                mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                if (dialog!=null){
                    dialog.cancel();
                    dialog.dismiss();
                }

            }

        });
        mWebView.loadUrl(url);
    }

    @Override
    public void EventCallBack(MessageEvent messageEvent) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()&&!mWebView.getUrl().contains("login.aspx?action=yuandapay")) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空所有Cookie
        CookieSyncManager.createInstance(App.getContext());  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now

        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        mWebView.getSettings().setJavaScriptEnabled(false);
        mWebView.clearCache(true);
    }

    @Override
    public void doCall(String str) {
        try {
            JSONObject jsonObject=new JSONObject(str);
            String type=jsonObject.optString("type")+"";
            if (type.equals("")){

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
