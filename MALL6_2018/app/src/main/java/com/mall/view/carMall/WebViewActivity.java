package com.mall.view.carMall;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.ProxySiteFrame;
import com.mall.view.R;
import com.mall.view.RedEnvelopesPackage.RedEnvelopeRechargeActivity;

import java.lang.ref.WeakReference;

public class WebViewActivity extends AppCompatActivity {

    @ViewInject(R.id.web_view)
    private WebView mWebView;
    @ViewInject(R.id.progressBar1)
    private ProgressBar pg1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_introduce);
        ViewUtils.inject(this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        Util.initTitle(this, title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setCacheMode(WebSettings.LOAD_DEFAULT); // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);


        //支持js
        settings.setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根

                if (newProgress == 100) {
                    pg1.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    pg1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pg1.setProgress(newProgress);//设置进度值
                }

            }
        });
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.addJavascriptInterface(this, "videolistner");
        //如果不设置WebViewClient，请求会跳转系统浏览器
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                // 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
                // super.onReceivedSslError(view, handler, error);

                // 接受所有网站的证书，忽略SSL错误，执行访问网页
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("拦截", "url" + url);
                //该方法在Build.VERSION_CODES.LOLLIPOP以前有效，从Build.VERSION_CODES.LOLLIPOP起，建议使用shouldOverrideUrlLoading(WebView, WebResourceRequest)} instead
                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
                //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

                addVoidClickListner(view);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
                //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    if (request.getUrl().toString().contains("sina.cn")){
////                        view.loadUrl("http://ask.csdn.net/questions/178242");
//                        return true;
//                    }
//                }

                return false;
            }

        });
        mWebView.loadUrl(url);
    }

    private void addVoidClickListner(WebView view) {
//        view.loadUrl("javascript:(function(){" +
//
//                "var jinru = document.getElementsByClassName(\"jinru\"); " +
//                "var img1=jinru[0].getElementsByTagName(\"img\");"+
//
//                "    img1[0].onclick=function()  " +
//                "    {  "
//                + "        window.videolistner.openImage1(this.src);  " +
//
//
//                "}})();function go_toCar(){ } ");

//        view.loadUrl("javascript:function go_toCar(){alert('123') }  ");
//        view.loadUrl("javascript:function go_toCar(){alert('123') }"+
//                "javascript:function go_toShing(){alert('1234') }  ");
        view.loadUrl("javascript:function go_toCar(){ window.videolistner.openImage1(\"toCar\"); }" +
                "javascript:function go_toShing(){window.videolistner.openImage1(\"toShing\"); }  ");

    }

    MyHander hander = new MyHander(this);

    class MyHander extends Handler {
        private WeakReference<WebViewActivity> mActivity;

        public MyHander(WebViewActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            WebViewActivity webViewActivity = mActivity.get();
            String str = "仅创客和商家可参与购物拼车";
            new MyPopWindow.MyBuilder(WebViewActivity.this, str, "立即申请", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WebViewActivity.this, ProxySiteFrame.class);
                    WebViewActivity.this.startActivity(intent);
                }
            }).setColor("#F13232")
                    .setisshowclose(true)
                    .build().showCenter();
        }
    }

    @JavascriptInterface
    public void openImage1(String img) {
        System.out.println("KK1" + img);
        Log.e("地址1", "img" + img);
        if (img.equals("toCar")) {

            if (!Util.checkLoginOrNot()) {
                Util.show("请先登录", WebViewActivity.this);
                Util.showIntent(WebViewActivity.this, LoginFrame.class);
                return;
            }

            if (!UserData.getUser().getUserLevel().contains("城市经理") && !UserData.getUser().getUserLevel().contains("城市总监")
                    && !UserData.getUser().getUserLevel().contains("联盟商家")) {
                hander.sendEmptyMessage(123);
                return;
            }

            Util.showIntent(WebViewActivity.this, RedEnvelopeRechargeActivity.class,
                    new String[]{"userKey"}, new String[]{"购物券充值"}
            );
        } else {
            Util.showIntent(WebViewActivity.this, CarShopActivity.class);

            finish();
        }
        //dd


    }

}
