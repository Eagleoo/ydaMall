package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;

public class ProductDetailHtml extends Activity {
	private ImageView cancel;
	private WebView webview;
	private String content;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			setContentView(R.layout.product_detail);
			content = this.getIntent().getStringExtra("content");
			cancel = (ImageView) this.findViewById(R.id.delete);
			webview = (WebView) this.findViewById(R.id.detail);
			WebSettings webSettings= webview.getSettings();
			webSettings.setUseWideViewPort(true);
			webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
			webSettings.setBuiltInZoomControls(true);//显示放大缩小 controler 
			webSettings.setSupportZoom(true);
			webSettings.setLoadWithOverviewMode(true);
			cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ProductDetailHtml.this.finish();
				}
			});
			webview.loadDataWithBaseURL(null, content + "", "text/html", "utf-8",
					"");
//		setContentView(R.layout.product_detail);
//		cancel = (ImageView) this.findViewById(R.id.delete);
//		webview = (WebView) this.findViewById(R.id.detail);
//		cancel.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				ProductDetailHtml.this.finish();
//			}
//		});
//		final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "努力加载中...");
//		webview.loadUrl("http://"+Web.webServer+"/phone/ProductDetailById.aspx?id=&type=productExplain");
//		webview.setWebViewClient(new WebViewClient(){
//
//			@Override
//			public void onPageFinished(WebView view, String url) {
//				super.onPageFinished(view, url);
//				if(cpd.isShowing()){
//					cpd.cancel();
//					cpd.dismiss();
//				}
//			}
//			
//		});
	}
}
