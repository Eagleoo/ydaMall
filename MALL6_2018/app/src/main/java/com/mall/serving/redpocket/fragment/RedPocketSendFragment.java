package com.mall.serving.redpocket.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.serving.community.fragment.BaseReceiverFragment;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.redpocket.activity.RedPocketDetailActivity;
import com.mall.view.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RedPocketSendFragment extends BaseReceiverFragment {

	@ViewInject(R.id.iv_plane)
	private ImageView iv_plane;
	@ViewInject(R.id.iv_money)
	private ImageView iv_money;
	@ViewInject(R.id.tv_red_1)
	private View tv_red_1;
	@ViewInject(R.id.tv_red_2)
	private View tv_red_2;
	@ViewInject(R.id.tv_red_3)
	private View tv_red_3;

	private Animation mMoveAction;
	private Animation mScalAction;
	private Animation mDownAction;
	private int[] starS = { R.drawable.redpocket_star_two,
			R.drawable.redpocket_star_three, R.drawable.redpocket_star_four };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mScalAction = AnimationUtils.loadAnimation(context, R.anim.unzoom_in);
		mScalAction.setDuration(200);
		mMoveAction = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_in);
		mMoveAction.setDuration(2000);
		mDownAction = AnimationUtils.loadAnimation(context,
				R.anim.in_translate_top);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FrameLayout v = (FrameLayout) inflater.inflate(
				R.layout.redpocket_send_fragment, container, false);
		ViewUtils.inject(this, v);

		startAnim(v);
		tv_red_3.setScaleX(0.8f);
		tv_red_3.setScaleY(0.8f);
		return v;
	}

	@OnClick({ R.id.tv_redpocket1, R.id.tv_redpocket2 })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.tv_redpocket1:

			Util.showIntent(context, RedPocketDetailActivity.class,
					new String[] { "red" }, new Serializable[] { 0 });
			break;
		case R.id.tv_redpocket2:
			Util.showIntent(context, RedPocketDetailActivity.class,
					new String[] { "red" }, new Serializable[] { 1 });

			break;

		}
	}

	@SuppressLint("NewApi")
	private void startAnim(FrameLayout v) {

		RotateAnimation animation_1 = AnimeUtil.getRotateAnimation(-25);
		RotateAnimation animation_2 = AnimeUtil.getRotateAnimation(25);
		tv_red_1.startAnimation(animation_1);
		tv_red_2.startAnimation(animation_2);
		tv_red_3.startAnimation(animation_2);

		iv_plane.setVisibility(View.GONE);
		mDownAction.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				iv_plane.setVisibility(View.VISIBLE);
				iv_plane.startAnimation(mMoveAction);

			}
		});

		iv_money.startAnimation(mDownAction);

		final List<View> views = new ArrayList<View>();
		for (int i = 0; i < 6 + Math.random() * 6; i++) {
			final ImageView iv = new ImageView(context);

			iv.setImageResource(starS[i % 3]);
			int top = (int) (Math.random() * 200);
			int left = (int) (Math.random() * Util.getScreenWidth());

			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(left, top, 0, 0);
			iv.setLayoutParams(layoutParams);
			v.addView(iv, 0);
			views.add(iv);

		}
		viewsAnim(views);

	}

	private void viewsAnim(final List<View> views) {

		mScalAction.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {

				int m = (int) (Math.random() * views.size());
				final View iv = views.get(m);
				iv.postDelayed(new Runnable() {

					@Override
					public void run() {
						iv.startAnimation(mScalAction);

					}
				}, 200);

			}
		});

		views.get(0).startAnimation(mScalAction);

	}

}
