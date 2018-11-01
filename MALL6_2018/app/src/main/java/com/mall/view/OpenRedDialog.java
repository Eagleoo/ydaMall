package com.mall.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mall.serving.community.view.droidflakes.Flake;
import com.mall.serving.community.view.droidflakes.FlakeView;
import com.mall.util.Util;

public class OpenRedDialog extends Dialog {

	ImageView close, open, imageend,successiv,toshop;
	LinearLayout container;
	RelativeLayout end;
	RelativeLayout rel;
	FlakeView flakeView;
	Context context;

	TextView titile;

	OnOpenListener onOpenListener;

	public void setOnClickListener(OnOpenListener l) {
		this.onOpenListener = l;
	}

	public OpenRedDialog(Context context) {
		super(context, R.style.reddialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.open_dialog);
		rel = (RelativeLayout) findViewById(R.id.rel);
		close = (ImageView) findViewById(R.id.close);
		toshop = (ImageView) findViewById(R.id.toshop);
		toshop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.showIntent(context, StoreMainFrame.class);
				dismiss();
			}
		});
		titile = (TextView) findViewById(R.id.titile);
		titile.setBackground(context.getResources().getDrawable(R.drawable.dotted_line));
//		close_red=(ImageView) findViewById(R.id.close_red);
		imageend = (ImageView) findViewById(R.id.imageend);
		open = (ImageView) findViewById(R.id.open);
		container = (LinearLayout) findViewById(R.id.container);
		successiv=(ImageView) findViewById(R.id.successiv);
		end = (RelativeLayout) findViewById(R.id.end);
		ObjectAnimator animator1 = ObjectAnimator.ofFloat(rel, "rotation", 0,
				360);
		ObjectAnimator animator2 = ObjectAnimator.ofFloat(rel, "scaleX", 0f,
				1.0f);
		ObjectAnimator animator3 = ObjectAnimator.ofFloat(rel, "scaleY", 0f,
				1.0f);
		ObjectAnimator animator4 = ObjectAnimator.ofFloat(rel, "alpha", 1.0f,
				0.2f, 1.0F);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(animator1, animator2, animator3, animator4);
		set.setDuration(1000);
		set.start();
		setCanceledOnTouchOutside(false);

		container.setVisibility(View.VISIBLE);
		Flake.speedMin = 500;
		flakeView = new FlakeView(context, R.drawable.redpocket);

		container.removeAllViews();
		container.addView(flakeView);
		flakeView.resume();

		close.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		end.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		successiv.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		open.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onOpenListener.onClick();
			}
		});
	}
	
	public void startAnimator(){
		ObjectAnimator animator = ObjectAnimator.ofFloat(open,
				"rotationY", 0, 360);
		animator.setDuration(1000).start();
		animator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				rel.setVisibility(View.GONE);
				flakeView.pause();
				container.setVisibility(View.GONE);
				end.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	public void setMoney(String money){
		TextView text=(TextView) end.findViewById(R.id.money);

		text.setText(money);
	}

	public interface OnOpenListener {
		void onClick();
	}
}
