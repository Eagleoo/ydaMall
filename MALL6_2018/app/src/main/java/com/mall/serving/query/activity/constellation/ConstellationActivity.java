package com.mall.serving.query.activity.constellation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.picviewpager.ViewPagerAdapter;
import com.mall.serving.community.view.textview.TextDrawable;
import com.mall.serving.query.model.ConstellationInfo;
import com.mall.serving.query.view.dialog.ConstellationPopupWindow;
import com.mall.serving.voip.util.SharedPreferencesUtils;
import com.mall.view.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.query_constellation_activity)
public class ConstellationActivity extends BaseActivity {

	@ViewInject(R.id.rg)
	private RadioGroup rg;
	@ViewInject(R.id.pager)
	public ViewPager pager;
	@ViewInject(R.id.tv_con)
	public TextView tv_con;

	// private String dateString = "";

	public static String TAG = "com.mall.serving.query.activity.constellation.ConstellationActivity";

	private String[] types = { "today", "tomorrow", "week" };
	private ViewPagerAdapter pagerAdapter;
	private int num;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		// SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy年MM月dd日");
		// dateString = sdfFrom.format(new Date());
		num = SharedPreferencesUtils.getSharedPreferencesInt(TAG);
		setCons();
		initView();
		registerReceiverAtBase(new String[] { TAG });

	}

	private void setCons() {
		TextDrawable td = TextDrawable.builder().buildRound("",
				Color.parseColor("#" + ConstellationPopupWindow.colors[num]));
		tv_con.setBackgroundDrawable(td);
		tv_con.setText(ConstellationPopupWindow.cons[num] + "座");
		Drawable rightDrawable = getResources().getDrawable(ConstellationPopupWindow.rids[num]);
		rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
		tv_con.setCompoundDrawables(null, rightDrawable, null, null);
		RadioButton childAt = (RadioButton) rg.getChildAt(0);
		childAt.setChecked(true);

	}

	private List<View> getListData() {
		List<View> vList = new ArrayList<View>();

		for (int i = 0; i < 3; i++) {
			View view = LayoutInflater.from(context).inflate(R.layout.query_constellation_item1, null);
			String str = tv_con.getText().toString();
			constellationQuery(str, i, view);
			vList.add(view);
		}
		return vList;

	}

	private void initView() {
		List<View> vList = getListData();
		pagerAdapter = new ViewPagerAdapter(vList);
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int p) {
				RadioButton childAt = (RadioButton) rg.getChildAt(p);
				childAt.setChecked(true);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup rg, int rid) {
				int num = 0;
				switch (rid) {
				case R.id.rb_1:
					num = 0;
					break;
				case R.id.rb_2:
					num = 1;
					break;
				case R.id.rb_3:
					num = 2;
					break;
				}

				pager.setCurrentItem(num);

			}
		});

	}

	private void constellationQuery(final String consName, final int type, final View view) {
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				ConstellationInfo info = JsonUtil.getPerson(runData.toString(), ConstellationInfo.class);
				if (info==null){
					com.mall.util.Util.show("网络异常,请检查网络！", context);
					return;
				}

				LinearLayout lv = (LinearLayout) view;
				View tl = view.findViewById(R.id.tl);
				View ll = view.findViewById(R.id.ll);
				TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
				LinearLayout ll_1 = (LinearLayout) view.findViewById(R.id.ll_1);
				LinearLayout ll_2 = (LinearLayout) view.findViewById(R.id.ll_2);
				LinearLayout ll_3 = (LinearLayout) view.findViewById(R.id.ll_3);
				LinearLayout ll_4 = (LinearLayout) view.findViewById(R.id.ll_4);
				LinearLayout ll_5 = (LinearLayout) view.findViewById(R.id.ll_5);
				LinearLayout ll_6 = (LinearLayout) view.findViewById(R.id.ll_6);
				LinearLayout ll_7 = (LinearLayout) view.findViewById(R.id.ll_7);
				LinearLayout ll_8 = (LinearLayout) view.findViewById(R.id.ll_8);

				if (type == 2) {
					tl.setVisibility(View.GONE);
					ll.setVisibility(View.GONE);

					addTextview(lv, info.getHealth(), 10);
					addTextview(lv, info.getJob(), 10);
					addTextview(lv, info.getLove(), 10);
					addTextview(lv, info.getMoney(), 10);
					addTextview(lv, info.getWork(), 10);

				} else {
					tv_date.setText(info.getDatetime());
					float all = getFloat(info.getAll());
					float love = getFloat(info.getLove());
					float work = getFloat(info.getWork());
					float money = getFloat(info.getMoney());
					float health = getFloat(info.getHealth());
					addStar(ll_1, all);
					addStar(ll_2, love);
					addStar(ll_3, work);
					addStar(ll_4, money);
					addStar(ll_5, health);
					addTextview(ll_6, info.getQFriend(), 5);
					addTextview(ll_7, info.getColor(), 5);
					addTextview(ll_8, info.getNumber(), 5);
					addTextview(lv, info.getSummary(), 10);

				}

			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://web.juhe.cn:8080/constellation/getAll?&consName=" + consName + "&type="
						+ types[type] + "&key=703e2d1a648c3d036fc794a8e4b77253&dtype=json");
				return web.getPlan();
			}
		});
	}

	private float getFloat(String a) {
		float result = 0;
		if (a.contains("%")) {
			String split = a.replace("%", "");

			result = Util.getInt(split) / (float) 20;
		}

		return result;

	}

	private void addStar(LinearLayout ll, float fl) {
		for (int i = 0; i < fl; i++) {
			ImageView iv = new ImageView(context);
			iv.setImageResource(R.drawable.query_star);
			ll.addView(iv);
		}

	}

	private void addTextview(LinearLayout ll, String str, int num) {
		TextView tv = new TextView(context);
		tv.setTextColor(context.getResources().getColor(R.color.black));
		int _5dp = Util.dpToPx(num);
		tv.setPadding(_5dp, _5dp, _5dp, _5dp);
		tv.setText(str);
		tv.setTextSize(14);
		ll.addView(tv);

	}

	@OnClick(R.id.tv_con)
	public void click(View v) {

		AnimeUtil.startAnimation(context, v, R.anim.small_2_big, new OnAnimEnd() {

			@Override
			public void start() {
				// TODO Auto-generated method stub

			}

			@Override
			public void repeat() {
				// TODO Auto-generated method stub

			}

			@Override
			public void end() {
				new ConstellationPopupWindow(context);

			}
		});

	}

	@Override
	public void onReceiveBroadcast(Intent intent) {
		// TODO Auto-generated method stub
		super.onReceiveBroadcast(intent);

		if (intent.getAction().equals(TAG)) {
			if (intent.hasExtra("num")) {

				num = intent.getIntExtra("num", 0);
				SharedPreferencesUtils.setSharedPreferencesInt(TAG, num);
				setCons();
				List<View> vList = getListData();
				pagerAdapter = new ViewPagerAdapter(vList);
				pager.setAdapter(pagerAdapter);
			}

		}

	}

}
