package com.mall.view;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.util.Util;
import com.mall.view.messageboard.MessageBoardFrame;

public class Lin_MainFindFrame extends TabActivity {

	public static Lin_MainFindFrame newInstance = null;

	TabHost tabHost;
	@ViewInject(R.id.homeButton)
	private TextView homeButton;
	@ViewInject(R.id.huodong)
	private TextView huodong;
	@ViewInject(R.id.fujin)
	private TextView fujin;
	@ViewInject(R.id.xinqing)
	private TextView xinqing;
	@ViewInject(R.id.my_card)
	private TextView my;
	private TabWidget tabWidget;
	private int state = 11;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_frame);
		ViewUtils.inject(this);
		newInstance = this;
		initTab();

	}





	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	
	    Util.aa = getWindowManager().getDefaultDisplay().getWidth() ;
	    Log.e("数据23",Util.aa +"");

	}
	

	@OnClick({ R.id.homeButton, R.id.huodong, R.id.fujin, R.id.xinqing,
			R.id.my_card })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.homeButton:
			state = 7;
			finish();
			Util.showIntent(this, Lin_MainFrame.class);
			break;

		case R.id.huodong:
			chanegeDrawable(R.drawable.new_page_shouye, homeButton);
			chanegeDrawable(R.drawable.new_page_notepad_1, huodong);
			chanegeDrawable(R.drawable.new_page_fujin, fujin);
			chanegeDrawable(R.drawable.new_page_xinqing, xinqing);
			chanegeDrawable(R.drawable.new_page_my, my);
			tabHost.setCurrentTabByTag("huodong");
			state = 1;
			break;
		case R.id.fujin:
			state = 2;
			chanegeDrawable(R.drawable.new_page_shouye, homeButton);
			chanegeDrawable(R.drawable.new_page_notepad, huodong);
			chanegeDrawable(R.drawable.new_page_fujin_1, fujin);
			chanegeDrawable(R.drawable.new_page_xinqing, xinqing);
			chanegeDrawable(R.drawable.new_page_my, my);
			fujinFind(view);
			break;

		case R.id.xinqing:
			state = 3;
			chanegeDrawable(R.drawable.new_page_shouye, homeButton);
			chanegeDrawable(R.drawable.new_page_notepad, huodong);
			chanegeDrawable(R.drawable.new_page_fujin, fujin);
			chanegeDrawable(R.drawable.new_page_xinqing_1, xinqing);
			chanegeDrawable(R.drawable.new_page_my, my);
			tabHost.setCurrentTabByTag("mood");
			break;
		case R.id.my_card:
			state = 4;
			chanegeDrawable(R.drawable.new_page_shouye, homeButton);
			chanegeDrawable(R.drawable.new_page_notepad, huodong);
			chanegeDrawable(R.drawable.new_page_fujin, fujin);
			chanegeDrawable(R.drawable.new_page_xinqing, xinqing);
			chanegeDrawable(R.drawable.new_page_my_1, my);
			tabHost.setCurrentTabByTag("my");
			break;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (state!=0) {
			finish();
			Util.showIntent(this, Lin_MainFrame.class);
			return true;
		}else {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getKeyCode() == KeyEvent.KEYCODE_BACK
					&& event.getRepeatCount() == 0) {
				Intent home = new Intent(Intent.ACTION_MAIN);
				home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				home.addCategory(Intent.CATEGORY_HOME);
				startActivity(home);
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
				.setContent(new Intent(this, FindLMSJFrame.class)));
		Intent fujinIntent = new Intent(this, MapLMSJFrame.class);
		fujinIntent.putExtra("from", "shouye");
		tabHost.addTab(tabHost.newTabSpec("fujin").setIndicator("fujin")
				.setContent(fujinIntent));
		Intent userCenter = new Intent(this, UserCenterFrame.class);
		userCenter.putExtra("from", "shouye");
		tabHost.addTab(tabHost.newTabSpec("my").setIndicator("my")
				.setContent(userCenter));
		tabHost.addTab(tabHost.newTabSpec("mood").setIndicator("mood")
				.setContent(new Intent(this, MessageBoardFrame.class)));
		Intent intent = new Intent(this, HuoDongFrame.class);
		intent.putExtra("type", "tj");
		intent.putExtra("from", "shouye");
		tabHost.addTab(tabHost.newTabSpec("huodong").setIndicator("huodong")
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
		return homeButton;
	}

	/**
	 * 附近
	 * 
	 * @return
	 */
	public TextView getMain_tab_lmsj() {
		return fujin;
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
	 * 活动
	 * 
	 * @return
	 */
	public TextView getMain_tab_car() {
		return huodong;
	}

	/**
	 * 心情
	 * 
	 * @return
	 */
	public TextView getMain_tab_buy() {
		return xinqing;
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
		}else {
			super.onBackPressed();
		}
		return; /* 很蛋疼得发现，不写return，就会自动执行系统默认的back动作，原因未知 */
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (state != 0) {
				tabHost.setCurrentTabByTag("home");
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	

}
