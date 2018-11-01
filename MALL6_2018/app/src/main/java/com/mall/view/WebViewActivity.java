package com.mall.view;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadTask;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.CustomProgressDialog;
import com.mall.util.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static com.mall.view.App.getContext;

public class WebViewActivity extends AppCompatActivity {

    @ViewInject(R.id.web_view)
    private WebView mWebView;
    @ViewInject(R.id.progressBar1)
    private ProgressBar pg1;

    int num = 0;

    @Override
    protected void onResume() {
        super.onResume();
        if (num == 1) {
            mWebView.goBack();
            num = 0;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        num = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ViewUtils.inject(this);
        Aria.download(this).register();
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

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                Log.e("url", "url" + url);
                String name = url.substring(url.lastIndexOf("/") + 1,
                        url.length()).toLowerCase();
                try {
                    name = URLDecoder.decode(name, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.e("namne", "sda" + name);
                cpd = Util.showProgress("下载中...", WebViewActivity.this);

                Aria.download(WebViewActivity.this)
                        .load(url)
                        .setDownloadPath(Environment.getExternalStorageDirectory().getPath() + "/" + name)    //文件保存路径
                        .start();   //启动下载
                namefile = Environment.getExternalStorageDirectory().getPath() + "/" + name;
            }
        });


        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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

    String namefile = "";
    CustomProgressDialog cpd;

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.loadUrl("about:blank");
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
        if (cpd != null) {
            cpd.dismiss();
            cpd.cancel();
        }
        Toast.makeText(WebViewActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
        Log.e("namefile", "namefile" + namefile);
        Util.openFiles(namefile);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cpd != null) {
            cpd.dismiss();
            cpd.cancel();
        }

        Aria.download(this).unRegister();
        //清空所有Cookie
        CookieSyncManager.createInstance(getContext());  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now

        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        mWebView.getSettings().setJavaScriptEnabled(false);
        mWebView.clearCache(true);
    }
}
