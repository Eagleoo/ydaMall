package com.mall.officeonline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.mall.view.R;

public class VedioWeb extends Activity {
    private WebView mWebView;  
    private Handler mHandler=new Handler();  
    private String mFlashFilename;  
    private ProgressDialog mProgressDialog;  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.veido_web);  
        mWebView=(WebView)findViewById(R.id.webview1);  
        setTitle("flash播放器");  
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
        //设置可以访问文件  
        settings.setAllowFileAccess(true);
        mWebView.getSettings().setJavaScriptEnabled(true);  
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setAllowFileAccess(true);  
  
//        mWebView.setBackgroundColor(0);
   
//        mWebView.setWebViewClient(new WebViewClient() {
//        	@Override
//        	public void onPageFinished(WebView view, String url) {
//        	Log.d("view.getTitle()", "view.getTitle()" + view.getTitle());
//        
//        	view.setLayerType(View.LAYER_TYPE_HARDWARE,null);
//
//        	
//        
//        
//        }
//    }
        mWebView.setWebViewClient(new WebViewClient(){
        	@Override
        	public void onPageFinished(WebView view, String url) {
        		// TODO Auto-generated method stub
        		super.onPageFinished(view, url);
        		view.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        	}
        });
        
        mWebView.loadUrl("http://player.youku.com/player.php/sid/XOTU5MTY2MTY0/v.swf");
//        mWebView.loadUrl("http://www.baidu.com");
        
        mWebView.getSettings().setJavaScriptEnabled(true);  
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setAllowFileAccess(true);  
  
        mWebView.setBackgroundColor(0); 
 
}
    

}
