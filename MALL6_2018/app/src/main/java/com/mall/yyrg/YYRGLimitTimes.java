package com.mall.yyrg;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.adapter.TimeListRecord;

/**
 * 限时抢购的功能
 * 
 * @author Administrator
 * 
 */
public class YYRGLimitTimes extends Activity {

	@ViewInject(R.id.thisweek)
	private TextView thisweek;
	@ViewInject(R.id.nextweek)
	private TextView nextweek;
	@ViewInject(R.id.lin_thisweek)
	private LinearLayout lin_thisweek;
	@ViewInject(R.id.lin_nextweek)
	private LinearLayout lin_nextweek;
	@ViewInject(R.id.ll_yyrg_limit_time)
	private LinearLayout ll_yyrg_limit_time;
	private int state = 1;

	@ViewInject(R.id.qianggouing)
	private TextView qianggouing;
	@ViewInject(R.id.wangqi)
	private TextView wangqi;
	@ViewInject(R.id.top)
	private LinearLayout top;

	private PopupWindow distancePopup = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_limit_time_announces);
		ViewUtils.inject(this);
		YYRGLimitTimeUtil.isRunThread = true;
		YYRGLimitTimeUtil
				.getGoodProductTimeListRecord(this, ll_yyrg_limit_time,false);

	}

	@OnClick({ R.id.top_back, R.id.fi1, R.id.fi2, R.id.qianggouing,
			R.id.wangqi })
	public void onclick(View v) {
		switch (v.getId()) {
		case R.id.top_back:
			finish();
			break;
		case R.id.fi1:
			state = 1;
			thisweek.setTextColor(getResources()
					.getColor(R.color.yyrg_topcolor));
			thisweek.setBackgroundColor(getResources().getColor(R.color.bg));
			nextweek.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			lin_thisweek.setVisibility(View.VISIBLE);
			lin_nextweek.setVisibility(View.INVISIBLE);
			nextweek.setTextColor(getResources().getColor(R.color.bg));
			ll_yyrg_limit_time.removeAllViews();
			YYRGLimitTimeUtil.addLimitTimeGroup(this, ll_yyrg_limit_time,false);
			break;
		case R.id.fi2:
			state = 2;
			thisweek.setTextColor(getResources().getColor(R.color.bg));
			lin_thisweek.setVisibility(View.INVISIBLE);
			lin_nextweek.setVisibility(View.VISIBLE);
			nextweek.setTextColor(getResources()
					.getColor(R.color.yyrg_topcolor));
			thisweek.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			nextweek.setBackgroundColor(getResources().getColor(R.color.bg));
			ll_yyrg_limit_time.removeAllViews();
		
			YYRGLimitTimeUtil.addLimitTimeGroup(this, ll_yyrg_limit_time,true);
			break;
		case R.id.qianggouing:

			ArrayList timeList = new ArrayList<TimeListRecord>();
			if (YYRGLimitTimeUtil.allList != null) {
				
				for (TimeListRecord info : YYRGLimitTimeUtil.allList) {
					if (TextUtils.isEmpty(info.getAwardUserid())) {
						timeList.add(info);
					}

				}
			}
			
			Util.showIntent(this, YYRGLimitTimeOther.class,new String[]{"title","list"},new Serializable[]{"抢购进行中",timeList});
			break;
		case R.id.wangqi:
			ArrayList timeLists = new ArrayList<TimeListRecord>();
			if (YYRGLimitTimeUtil.allList != null) {
				
				for (TimeListRecord info : YYRGLimitTimeUtil.allList) {
					if (!TextUtils.isEmpty(info.getAwardUserid())) {
						timeLists.add(info);
					}

				}
			}
			Util.showIntent(this, YYRGLimitTimeOther.class,new String[]{"title","list"},new Serializable[]{"往期回顾",timeLists});

			break;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		YYRGLimitTimeUtil.isRunThread = false;
	}
}
