package com.mall.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.serving.doubleball.DoubleballNumericalSelectionActivity;
import com.mall.serving.filmticket.FilmList;
import com.mall.serving.orderplane.OrderPlaneIndex;
import com.mall.serving.resturant.ResturantIndex;
import com.mall.serving.scenic.activity.ScenicSpotsMainActivity;
import com.mall.serving.voip.util.VoipPhoneUtils;
import com.mall.util.Data;
import com.mall.util.UserData;
import com.mall.util.Util;

public class BMFWFrane extends Activity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	
	
	
	
	@ViewInject(R.id.viewPager)
	private ViewPager viewPager;
	@ViewInject(R.id.rg_point)
	private RadioGroup rg_point;
	private List<View> list;
	private boolean isWhile = true;
	private Handler bannerHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (-1 == msg.what) {
				if (null != msg.obj) {
					viewPager.setCurrentItem(Integer.parseInt(msg.obj + ""));
				}
			}
		}
	};

	private String unum = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout2);
		unum = this.getIntent().getStringExtra("unum");
		if (Util.isNull(unum))
			unum = "";
		User user = UserData.getUser();
		if(null == user){
			VoipPhoneUtils.showIntent("您还未登录，请先登录！", this,
					"确定", "取消", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Util.showIntent(BMFWFrane.this,
									LoginFrame.class);
						}
					}, null);
		}

		ViewUtils.inject(this);
		setView();
		setBanner();
	}
	
	
	private void setView() {
		top_center.setText("生活服务");
		top_left.setVisibility(View.VISIBLE);

	}

	@OnClick(R.id.main_layout2_lxkf)
	public void callKF(View view) {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ Util._400));
		startActivity(intent);

	}

	private void buildBannerList() {
		list = new ArrayList<View>();
		int bmfw[] = { R.drawable.bm_ban_dhph, R.drawable.bm_ban_wscp };
		for (int i = 0; i < bmfw.length; i++) {
			ImageView i1 = new ImageView(this);
			if(0 == i){
				i1.setTag("http://www.yda360.com/dhcenter.aspx");
			}else {
				i1.setTag("http://www.yda360.com/Product/hgtuan.aspx");
			}
			i1.setBackgroundResource(bmfw[i]);
			list.add(i1);
		}
		for (int i = 0; i < list.size(); i++) {
			View lv = list.get(i);
			lv.setTag(i+"");
			lv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if("0".equals(v.getTag()+"")){
						Util.showIntent(BMFWFrane.this, SBQFrame.class);
					}else if("2".equals(v.getTag()+"")){
						Data.setProductClass(1);
						Util.showIntent(BMFWFrane.this, HuangouFrame.class);
					}
				}
			});
		}
	}

	private void setBanner() {

		buildBannerList();

		for (int i = 0; i < list.size(); i++) {
			View view = (RadioButton) LayoutInflater.from(this).inflate(
					R.layout.banner_point, null);
			android.widget.RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
					Util.dpToPx(this, 7), Util.dpToPx(this, 7));
			params.setMargins(3, 0, 3, 0);
			view.setLayoutParams(params);

			rg_point.addView(view);

		}
		RadioButton rb = (RadioButton) rg_point.getChildAt(0);
		rb.setChecked(true);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				RadioButton rb = (RadioButton) rg_point.getChildAt(position);
				rb.setChecked(true);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		viewPager.setAdapter(new BannerAdapter(list));
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (isWhile) {
					int count = viewPager.getAdapter().getCount();
					int index = viewPager.getCurrentItem();
					for (int i = 0; i < count; i++) {
						try {
							Thread.currentThread().sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Message msg = new Message();
						index = viewPager.getCurrentItem() + 1;
						if (index > count)
							index = 0;
						msg.obj = i;
						msg.what = -1;
						bannerHandle.sendMessage(msg);
					}
				}
			}
		}).start();
	}
	
	@OnClick({R.id.tv_phone,R.id.tv_qq,R.id.tv_game,R.id.tv_hotel,R.id.tv_film,R.id.tv_plane,R.id.tv_lottery})
	public void click(View v){
		User user = UserData.getUser();
		if(null == user || Util.isNull(user.getMobilePhone())){
			VoipPhoneUtils.showIntent("请先完善资料！", this,
					"确定", "取消", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Util.showIntent(BMFWFrane.this,
									UpdateUserMessageActivity.class);
						}
					}, new OnClickListener() {
						@Override
						public void onClick(View v) {
							Util.showIntent(BMFWFrane.this,
									Lin_MainFrame.class);
						}
					});
			return ;
		}
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tv_phone:
			intent.setClass(this, PhoneFream.class);
			intent.putExtra("unum", unum);
			this.startActivity(intent);
			break;
		case R.id.tv_qq:
			intent.setClass(this, GameFrame.class);
			intent.putExtra("unum", unum);
			intent.putExtra("cid", "-1");
			this.startActivity(intent);
			break;
		case R.id.tv_game:
			intent.setClass(this, GameFrame.class);
			intent.putExtra("unum", unum);
			intent.putExtra("cid", "-2");
			this.startActivity(intent);
			break;
		case R.id.tv_hotel:
			intent.setClass(this, ResturantIndex.class);
			intent.putExtra("unum", unum);
			this.startActivity(intent);
			break;
		case R.id.tv_film:
			intent.setClass(this, FilmList.class);
			intent.putExtra("unum", unum);
			this.startActivity(intent);
			break;
		case R.id.tv_plane:
			intent.setClass(this, OrderPlaneIndex.class);
			intent.putExtra("unum", unum);
			this.startActivity(intent);
			break;
		case R.id.tv_lottery:
			Util.show("由财政部、民政部及国家体育总局等多个部门相续下发《关于开展擅自利用互联网销售彩票行为自查自纠工作有关问题的通知》及《体育总局关于切实落实彩票资金专项审计意见加强体育彩票管理工作的通知》等多项通知。受该通知影响，远大彩票暂停销售！",this);
//			intent.setClass(this, OrderPlaneIndex.class);
//			intent.putExtra("unum", unum);
//			this.startActivity(intent);
			break;

		default:
			break;
		}
		
	}

	@OnClick(R.id.main_layout2_cp)
	public void caipiao(View view) {
		User user = UserData.getUser();
		if(null == user || Util.isNull(user.getMobilePhone())){
			VoipPhoneUtils.showIntent("请先完善资料！", this,
					"确定", "取消", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Util.showIntent(BMFWFrane.this,
									UpdateUserMessageActivity.class);
						}
					}, null);
			return ;
		}
		Util.showIntent(this, DoubleballNumericalSelectionActivity.class);
	}

	@OnClick(R.id.topback)
	public void topback(View view) {
		finish();
	}

	@OnClick(R.id.main_layout2_mp)
	public void menpiao(View view) {
//		Util.show("景区门票即将开售，敬请关注！", this);
		Util.showIntent(this, ScenicSpotsMainActivity.class);
	}

	@OnClick(R.id.main_layout2_zc)
	public void zuche(View view) {
		Util.show("特价租车即将开始，敬请关注！", this);
	}

	@Override
	protected void onDestroy() {
		isWhile = false;
		super.onDestroy();
	}

	private class BannerAdapter extends PagerAdapter {

		private List<View> list;

		public BannerAdapter(List<View> list) {
			super();
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(ViewGroup arg0, int arg1) {
			arg0.addView(list.get(arg1), LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			return list.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
}
