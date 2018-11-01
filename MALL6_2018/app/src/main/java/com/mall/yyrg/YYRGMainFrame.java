package com.mall.yyrg;

import java.io.Serializable;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.Lin_MallActivity;
import com.mall.view.R;

/**
 * 一元热购主页面<br>
 * 
 * @author Administrator
 * 
 */
public class YYRGMainFrame extends TabActivity {

	public static YYRGMainFrame newInstance = null;
	
	TabHost tabHost;
	private static int size = 0;
	@ViewInject(R.id.yyrg_home)
	private TextView yyrg_home;
	@ViewInject(R.id.all_goods)
	private TextView all_goods;
	@ViewInject(R.id.hd_jiexiao)
	private TextView hd_jiexiao;
	@ViewInject(R.id.shopping_cart)
	private TextView shopping_cart;
	@ViewInject(R.id.my_regou)
	private TextView my_regou;
	private TabWidget tabWidget;
	private int state = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_main_frame);
		ViewUtils.inject(this);
		newInstance = this;
		initTab();
		if (0 == size) {
			size++;
		}

		Intent intent = getIntent();
		if ("page".equals(intent.getStringExtra("goto"))) {
			String page = intent.getStringExtra("page");
			if (Util.isInt(page)) {
				Util.asynTask(new IAsynTask() {
					@Override
					public void updateUI(Serializable runData) {
						tabHost.setCurrentTabByTag("home");
						Lin_MallActivity ma = (Lin_MallActivity) tabHost
								.getCurrentView().getContext();
					}

					@Override
					public Serializable run() {
						try {
							Thread.currentThread().sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						return null;
					}
				});
			}
		}
	}

	@OnClick({ R.id.yyrg_home, R.id.all_goods, R.id.hd_jiexiao,
			R.id.shopping_cart, R.id.my_regou })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.yyrg_home:
			state = 0;
			chanegeDrawable(R.drawable.yyrg_shouye_1, yyrg_home);
			chanegeDrawable(R.drawable.yyrg_sysp, all_goods);
			chanegeDrawable(R.drawable.yyrg_hdjx, hd_jiexiao);
			chanegeDrawable(R.drawable.new_shangc_cart, shopping_cart);
			chanegeDrawable(R.drawable.yyrg_wdrg, my_regou);
			tabHost.setCurrentTabByTag("home");
			chanege(yyrg_home);
			break;

		case R.id.all_goods:
			chanegeDrawable(R.drawable.yyrg_shouye, yyrg_home);
			chanegeDrawable(R.drawable.yyrg_sysp_1, all_goods);
			chanegeDrawable(R.drawable.yyrg_hdjx, hd_jiexiao);
			chanegeDrawable(R.drawable.new_shangc_cart, shopping_cart);
			chanegeDrawable(R.drawable.yyrg_wdrg, my_regou);
			tabHost.setCurrentTabByTag("all_goods");
			chanege(all_goods);
			state = 1;
			break;
		case R.id.hd_jiexiao:
			state = 2;
			chanegeDrawable(R.drawable.yyrg_shouye, yyrg_home);
			chanegeDrawable(R.drawable.yyrg_sysp, all_goods);
			chanegeDrawable(R.drawable.yyrg_hdjx_1, hd_jiexiao);
			chanegeDrawable(R.drawable.new_shangc_cart, shopping_cart);
			chanegeDrawable(R.drawable.yyrg_wdrg, my_regou);
			tabHost.setCurrentTabByTag("hd_jiexiao");
			chanege(hd_jiexiao);
			break;

		case R.id.shopping_cart:
			state = 3;
			chanegeDrawable(R.drawable.yyrg_shouye, yyrg_home);
			chanegeDrawable(R.drawable.yyrg_sysp, all_goods);
			chanegeDrawable(R.drawable.yyrg_hdjx, hd_jiexiao);
			chanegeDrawable(R.drawable.new_shangc_cart_1, shopping_cart);
			chanegeDrawable(R.drawable.yyrg_wdrg, my_regou);
			tabHost.setCurrentTabByTag("mood");
			chanege(shopping_cart);
			break;
		case R.id.my_regou:
			state = 4;
			chanegeDrawable(R.drawable.yyrg_shouye, yyrg_home);
			chanegeDrawable(R.drawable.yyrg_sysp, all_goods);
			chanegeDrawable(R.drawable.yyrg_hdjx, hd_jiexiao);
			chanegeDrawable(R.drawable.new_shangc_cart, shopping_cart);
			chanegeDrawable(R.drawable.yyrg_wdrg_1, my_regou);
			tabHost.setCurrentTabByTag("my_regou");
			chanege(my_regou);
			break;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (state != 0) {
			chanegeDrawable(R.drawable.yyrg_shouye_1, yyrg_home);
			chanegeDrawable(R.drawable.yyrg_sysp, all_goods);
			chanegeDrawable(R.drawable.yyrg_hdjx, hd_jiexiao);
			chanegeDrawable(R.drawable.new_shangc_cart, shopping_cart);
			chanegeDrawable(R.drawable.yyrg_wdrg, my_regou);
			tabHost.setCurrentTabByTag("home");
			chanege(yyrg_home);
			state = 0;
			return true;
		} else {
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
		tabHost.addTab(tabHost.newTabSpec("home").setIndicator("home")
				.setContent(new Intent(this, YYRGFrame.class)));

		Intent fujinIntent = new Intent(this, YYRGRevealedFrame.class);
		tabHost.addTab(tabHost.newTabSpec("hd_jiexiao")
				.setIndicator("hd_jiexiao").setContent(fujinIntent));

		Intent userCenter = new Intent(this, YYRGMyRegouFrame.class);
		tabHost.addTab(tabHost.newTabSpec("my_regou").setIndicator("my_regou")
				.setContent(userCenter));
		tabHost.addTab(tabHost.newTabSpec("mood").setIndicator("mood")
				.setContent(new Intent(this, YYRGMyRegouCart.class)));
		Intent intent = new Intent(this, YYRGGoodsList.class);
		tabHost.addTab(tabHost.newTabSpec("all_goods")
				.setIndicator("all_goods").setContent(intent));
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
		return yyrg_home;
	}

	/**
	 * 活动揭晓
	 * 
	 * @return
	 */
	public TextView getMain_tab_lmsj() {
		return hd_jiexiao;
	}

	/**
	 * 会员中心
	 * 
	 * @return
	 */
	public TextView getMain_tab_catagory() {
		return my_regou;
	}

	/**
	 * 所有商品
	 * 
	 * @return
	 */
	public TextView getMain_tab_car() {
		return all_goods;
	}

	/**
	 * 购物车
	 * 
	 * @return
	 */
	public TextView getMain_tab_buy() {
		return shopping_cart;
	}
	private void chanege(TextView view){
		yyrg_home.setTextColor(getResources().getColor(R.color.yyrg_shouye_zi));
		all_goods.setTextColor(getResources().getColor(R.color.yyrg_shouye_zi));
		hd_jiexiao.setTextColor(getResources().getColor(R.color.yyrg_shouye_zi));
		shopping_cart.setTextColor(getResources().getColor(R.color.yyrg_shouye_zi));
		my_regou.setTextColor(getResources().getColor(R.color.yyrg_shouye_zi));
		view.setTextColor(getResources().getColor(R.color.yyrg_topcolor));
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

//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		// File file = new File(strCrtPath);
//		/*
//		 * String strParentPath = file.getParent(); if (strParentPath != null) {
//		 * AddEachFile(strParentPath); 这里是你截获Back按键要做的事情 } else {
//		 * super.onBackPressed(); 如果你不打算做其他事情了，就调这个，会执行系统的默认动作 }
//		 */
//		if (state != 0) {
//			tabHost.setCurrentTabByTag("home");
//		} else {
//			super.onBackPressed();
//		}
//		return; /* 很蛋疼得发现，不写return，就会自动执行系统默认的back动作，原因未知 */
//	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			if (state != 0) {
//				tabHost.setCurrentTabByTag("home");
//			}
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

}
