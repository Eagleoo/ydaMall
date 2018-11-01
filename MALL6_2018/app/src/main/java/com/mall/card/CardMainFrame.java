package com.mall.card;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.card.adapter.CardUtil;
import com.mall.util.Util;
import com.mall.view.R;

/**
 * 一元热购主页面<br>
 * 
 * @author Administrator
 * 
 */
public class CardMainFrame extends TabActivity
		implements AMapLocationListener
{

	public static CardMainFrame newInstance = null;
//	private LocationManagerProxy mAMapLocManager = null;
	public TabHost tabHost;
	private static int size = 0;
	@ViewInject(R.id.mingpian)
	private TextView mingpian;
	@ViewInject(R.id.jiaohuan)
	private TextView jiaohuan;
	@ViewInject(R.id.add_card)
	private TextView add_card;
	@ViewInject(R.id.hezuo)
	private TextView hezuo;
	@ViewInject(R.id.my)
	private TextView my;
	private TabWidget tabWidget;
	private int state = 0;
	public static CardMainFrame cardMainFrame;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_main_frame);
		ViewUtils.inject(this);
		cardMainFrame = this;
		// dingwei();
		newInstance = this;
		initTab();
		if (0 == size) {
			size++;
		}
	}
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	CardUtil.cardCount=0;
}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (state != 0) {
			tabHost.setCurrentTabByTag("mingpian");
			chanege(mingpian);
			state = 0;
			return true;
		} else {
			CardUtil.isMe = "0";
			CardUtil.cardCount = 0;
			CardUtil.cardLinkman = null;
			CardUtil.myCardLinkman = null;
			CardUtil.exchangeUser.clear();
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getKeyCode() == KeyEvent.KEYCODE_BACK
					&& event.getRepeatCount() == 0) {

				finish();
				return true;
			}

			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getKeyCode() == KeyEvent.KEYCODE_BACK
					&& event.getRepeatCount() == 0) {
				finish();

				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	public void initTab() {
		if (!Util.list.contains(this))
			Util.list.add(this);
		tabHost = getTabHost();
		tabWidget = tabHost.getTabWidget();
		tabHost.addTab(tabHost.newTabSpec("mingpian").setIndicator("mingpian")
				.setContent(new Intent(this, CardPhonekook.class)));

		Intent fujinIntent = new Intent(this, CardExchangeCard.class);
		tabHost.addTab(tabHost.newTabSpec("add_card").setIndicator("add_card")
				.setContent(fujinIntent));

		Intent userCenter = new Intent(this, CardUserCentre.class);
		tabHost.addTab(tabHost.newTabSpec("my").setIndicator("my")
				.setContent(userCenter));
		tabHost.addTab(tabHost.newTabSpec("mood").setIndicator("mood")
				.setContent(new Intent(this, ScrollViewMainActivity.class)));
		Intent intent = new Intent(this, CardExchangeCard.class);
		tabHost.addTab(tabHost.newTabSpec("jiaohuan").setIndicator("jiaohuan")
				.setContent(intent));
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			// 设置tab背景颜色
			tabHost.setBackgroundColor(getResources().getColor(
					R.color.transparent));
		}
	}

	/**
	 * 返回首页
	 * 
	 * @return
	 */
	public TextView getMain_tab_home() {
		return mingpian;
	}

	/**
	 * 活动揭晓
	 * 
	 * @return
	 */
	public TextView getMain_tab_lmsj() {
		return add_card;
	}

	/**
	 * 会员中心
	 * 
	 * @return
	 */
	public TextView getMain_tab_catagory() {
		return my;
	}

	/**
	 * 所有商品
	 * 
	 * @return
	 */
	public TextView getMain_tab_car() {
		return jiaohuan;
	}

	/**
	 * 购物车
	 * 
	 * @return
	 */
	public TextView getMain_tab_buy() {
		return hezuo;
	}

	private void chanege(TextView view) {
		mingpian.setTextColor(getResources()
				.getColor(R.color.new_shangc_buqian));
		jiaohuan.setTextColor(getResources()
				.getColor(R.color.new_shangc_buqian));
		hezuo.setTextColor(getResources().getColor(R.color.new_shangc_buqian));
		my.setTextColor(getResources().getColor(R.color.new_shangc_buqian));
		view.setTextColor(getResources().getColor(R.color.card_bu_zi));
	}

	private void chanegeDrawable(int imageid, TextView view) {

		Drawable icon = this.getResources().getDrawable(imageid);
		icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
		view.setCompoundDrawables(null, icon, null, null);

	}

	public void fujinFind(View v) {
		v.setEnabled(false);
		tabHost.setCurrentTabByTag("fujin");
		v.setEnabled(true);
	}

	@Override
	public void onBackPressed() {
		if (state != 0) {
			tabHost.setCurrentTabByTag("home");
		} else {
			super.onBackPressed();
		}
		return; /* 很蛋疼得发现，不写return，就会自动执行系统默认的back动作，原因未知 */
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (state != 0) {
				tabHost.setCurrentTabByTag("home");
			}else {
				CardUtil.isMe = "0";
				CardUtil.cardCount = 0;
				CardUtil.cardLinkman = null;
				CardUtil.myCardLinkman = null;
				CardUtil.faqiRe=null;
				CardUtil.shoudaoRe=null;
				CardUtil.exchangeUser.clear();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {

	}
//
//	@Override
//	public void onLocationChanged(Location location) {
//		// AmapV2版本中定位不会回调此方法啦
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//
//	}
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//	}
//
//	@Override
//	public void onLocationChanged(AMapLocation location) {
//		if (location != null) {
//			Double geoLat = location.getLatitude();
//			Double geoLng = location.getLongitude();
//			CardUtil.lat = "" + geoLat;
//			CardUtil.lng = "" + geoLng;
//			if (mAMapLocManager != null) {
//				mAMapLocManager.removeUpdates(this);
//			}
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		CardUtil.isMe = "0";
//		CardUtil.cardCount = 0;
//		CardUtil.cardLinkman = null;
//		CardUtil.myCardLinkman = null;
//		CardUtil.faqiRe=null;
//		CardUtil.shoudaoRe=null;
//		CardUtil.exchangeUser.clear();
//		super.onDestroy();
//		if (mAMapLocManager != null) {
//			mAMapLocManager.removeUpdates(this);
//		}
//	}


}
