package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.BannerInfo;
import com.mall.phone.SIMCardInfo;
import com.mall.util.Data;
import com.mall.util.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.OnClick;

/**
 *
 * 功能： 加载页面<br>
 * 时间： 2013-4-16<br>
 * 备注： <br>
 *
 * @author Lin.~
 *
 */
public class LoadFrame extends Activity {

	@ViewInject(R.id.laod_gress)
	private ProgressBar pb;

	@ViewInject(R.id.rl)
	RelativeLayout rl;

	@ViewInject(R.id.banner)
	ImageView banner;

	@ViewInject(R.id.skip)
	TextView skip;

	private Context context;

	int number=5;

	private Random random = new Random();
	private boolean isDoMain = false;

	private Thread thread;





	public void load() {
		try {
			pb.setProgress(random.nextInt(30));
			Data.getShen();
			pb.setProgress(pb.getProgress() + random.nextInt(30));

			doMain();
			pb.setProgress(100);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				PrintWriter out = new PrintWriter(new File(
						"/sdcard/yuanda/log.txt"));
				out.println("----------------"
						+ new SimpleDateFormat("yyyy-MM-dd").format(new Date())
						+ "-----------------");
				out.println(e.getLocalizedMessage());
				out.println(e.getMessage());
				out.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		} finally {
		}
	}


	private static  class MyHandler extends  Handler{
		private final WeakReference<LoadFrame> mActivity;
		public MyHandler(LoadFrame loadFrame){
			mActivity=new WeakReference<LoadFrame>(loadFrame);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (mActivity.get()!=null){
				if (msg.what==1234){
					mActivity.get().skip.setText("跳 过"+mActivity.get().number--+"");
				}else{
					mActivity.get().doMain();
				}

			}

		}
	}
	MyHandler myHandler=new MyHandler(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.load);
		context=this;

		ViewUtils.inject(this);
		Util.add(this);
		SIMCardInfo.setContext(this);


		skip.setBackground(SelectorFactory.newShapeSelector()
				.setDefaultBgColor(Color.parseColor("#4c000000"))
				.setStrokeWidth(Util.dpToPx(context, 1))
				.setCornerRadius(Util.dpToPx(context, 15))
				.setDefaultStrokeColor(Color.parseColor("#bf959595"))
				.create());


//		final Handler handler = new Handler() {
//			public void handleMessage(Message msg) {
//				if (isDoMain)
//					return;
//				doMain();
//			}
//		};
		thread = new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < 5; i++) {
					if (isDoMain)
						return;
					else {
						try {
							thread.sleep(1000);
							Message msg = new Message();
							msg.what=1234;
							myHandler.sendMessage(msg);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("run", "success");
				msg.setData(data);
				myHandler.sendMessage(msg);
			}
		});
		thread.start();
		if (Data.getbanner()!=null){
			final BannerInfo bannerInfo=Data.getbanner();
			Log.e("getTime","15"+bannerInfo.toString());
			if (!Util.isNull(bannerInfo.getPath())){
				rl.setVisibility(View.GONE);
				Log.e("图片地址banner",bannerInfo.getPath()+"LL");
				Picasso.with(context).load("file://"+bannerInfo.getPath()).into(banner);
				skip.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						doMain();
					}
				});
				banner.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						doMain();
						if (isNumeric(bannerInfo.getUrl_android())){
							Util.showIntent(context, ProductDeatilFream.class,new String[]{"url"},new String[]{bannerInfo.getUrl_android()});
						}else {
							Util.openWeb(context,bannerInfo.getUrl_android());
						}

					}
				});
			}

		}




	}

	public boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() ){
			return false;
		}
		return true;
	}

	@OnClick({R.id.toshopmall})
	public void click(View view){
		switch (view.getId()){
			case R.id.toshopmall:
				doMain();
				Util.showIntent(LoadFrame.this, ProductDeatilFream.class,new String[]{"url"},new String[]{"231771"});
				break;
		}
	}

	private void doMain() {
		synchronized(this){
			if(isDoMain)
				return ;
			isDoMain = true;
		}
		Intent intent = new Intent();
		intent.setClass(this, Lin_MainFrame.class);
		this.startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
		pb.setProgress(100);
	}




}
