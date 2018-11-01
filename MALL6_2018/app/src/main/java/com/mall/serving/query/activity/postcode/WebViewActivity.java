package com.mall.serving.query.activity.postcode;

import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;

import com.mall.serving.community.activity.BaseActivity;

public class WebViewActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		WebView wv = new WebView(context);
		wv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(wv);
//		wv.loadDataWithBaseURL(
//				"http://m.kuaidi100.com/index_all.html?type=yuantong&postid=560031072312",
//				"", "text/html", "gb2312", "");
		
		wv.loadUrl("http://m.kuaidi100.com/index_all.html?type=yuantong&postid=560031072312");
		

	}

}
