package com.mall.serving.redpocket.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.serving.community.adapter.SupportFragmentAdapter;
import com.mall.serving.community.fragment.BaseReceiverFragment;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.view.dialog.CustomListDialog;
import com.mall.serving.community.view.dialog.CustomListDialog.OnItemClick;
import com.mall.serving.community.view.droidflakes.Flake;
import com.mall.serving.community.view.droidflakes.FlakeView;
import com.mall.serving.redpocket.fragment.RedPocketReceiveFragment;
import com.mall.serving.redpocket.fragment.RedPocketSendFragment;
import com.mall.serving.redpocket.fragment.RedPocketSentFragment;
import com.mall.util.Util;
import com.mall.view.R;

@ContentView(R.layout.redpocket_index_activity)
public class RedPocketIndexActivity extends FragmentActivity {

	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	@ViewInject(R.id.rg)
	private RadioGroup group;
	@ViewInject(R.id.rb_rg_1)
	private RadioButton rb_rg_1;
	@ViewInject(R.id.rb_rg_2)
	private RadioButton rb_rg_2;
	@ViewInject(R.id.rb_rg_3)
	private RadioButton rb_rg_3;
	@ViewInject(R.id.pager)
	public ViewPager pager;

	@ViewInject(R.id.container)
	public LinearLayout container;
	private FlakeView flakeView;

	private FragmentManager supportManager;
	private List<BaseReceiverFragment> list;
	private SupportFragmentAdapter adapter;

	private int index;
	private Context context;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		ViewUtils.inject(this);
		context = this;
		setView();
		setViewPager();
		getIntentData();
	}

	private void setView() {
		top_center.setText("远大红包");
		top_left.setVisibility(View.VISIBLE);
		rb_rg_1.setText("我要发红包");
		rb_rg_2.setText("发出的红包");
		rb_rg_3.setText("收到的红包");
		top_right.setText("红包规则");
		top_right.setVisibility(View.VISIBLE);
		top_right.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.drawable.community_trigon, 0);
	}

	private void getIntentData() {
		Intent intent = getIntent();

		if (intent.hasExtra("type")) {
			int type = intent.getIntExtra("type", 0);
			pager.setCurrentItem(1);

		} else {
			container.setVisibility(View.VISIBLE);
			Flake.speedMin = 300;
			flakeView = new FlakeView(this, R.drawable.redpocket);

			container.removeAllViews();
			container.addView(flakeView);
			flakeView.resume();
			container.postDelayed(new Runnable() {

				@Override
				public void run() {
					AlphaAnimation alpha = new AlphaAnimation(100, 0);
					alpha.setDuration(3000);
					AnimeUtil.startAnimation(context, container, alpha,
							new OnAnimEnd() {

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
									container.setVisibility(View.GONE);
									Flake.speedMin = 100;
								}
							});

				}
			}, 4000);
		}

	}

	@OnClick(R.id.top_right)
	public void click(View v) {
		if(0 == pager.getCurrentItem()){
			View pop = LayoutInflater.from(this).inflate(R.layout.redpackage_dialog_guize,null);
		
			ImageView iv_1 = (ImageView) pop.findViewById(R.id.iv_1);
			

			final PopupWindow popupWindow = new PopupWindow(this);
			popupWindow.setWidth(Util.dpToPx(this,220));
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setFocusable(false);
			popupWindow.setOutsideTouchable(false);
			popupWindow.setContentView(pop);
			popupWindow.showAtLocation(top_right, Gravity.CENTER, 0, -120);
			iv_1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					popupWindow.dismiss();
				}
			});

			return ;
		}
		new CustomListDialog(this, "筛选", new String[] { "全部", "一周内", "一月内",
				"一年内" }, new OnItemClick() {

			@Override
			public void itemClick(AdapterView<?> arg0, View arg1, int p,
					long arg3) {

				if (index == 1) {
					RedPocketSentFragment sent = (RedPocketSentFragment) list
							.get(index);
					sent.getSelectList(p);
				} else if (index == 2) {
					RedPocketReceiveFragment rece = (RedPocketReceiveFragment) list
							.get(index);
					rece.getSelectList(p);
				}

			}
		}).show();
	}

	private void setViewPager() {
		supportManager = getSupportFragmentManager();
		buidData();
		adapter = new SupportFragmentAdapter(supportManager, list);
		pager.setAdapter(adapter);

		pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int paramInt) {

				RadioButton childAt = (RadioButton) group.getChildAt(paramInt);
				childAt.setChecked(true);
			}

			@Override
			public void onPageScrolled(int paramInt1, float paramFloat,
					int paramInt2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int paramInt) {
				// TODO Auto-generated method stub

			}
		});

		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int num = 0;
				switch (checkedId) {
				case R.id.rb_rg_1:
					num = 0;
					break;
				case R.id.rb_rg_2:
					num = 1;
					break;
				case R.id.rb_rg_3:
					num = 2;
					break;

				default:
					break;
				}

				if (num == 0) {
					top_right.setVisibility(View.VISIBLE);
					top_right.setText("红包规则");
				} else {
					top_right.setVisibility(View.VISIBLE);
					top_right.setText("筛选");
				}

				index = num;
				pager.setCurrentItem(num);
			}
		});

	}

	private void buidData() {

		list = new ArrayList<BaseReceiverFragment>();
		list.add(new RedPocketSendFragment());
		list.add(new RedPocketSentFragment());
		list.add(new RedPocketReceiveFragment());

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	

}
