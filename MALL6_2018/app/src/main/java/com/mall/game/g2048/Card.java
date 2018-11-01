package com.mall.game.g2048;


import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mall.view.R;

public class Card extends FrameLayout {
	public static int width;
	public static int height;
	private TextView label;
	public Card(Context context) {
		super(context);
		LayoutParams lp = null;
		View background = new View(getContext());
		lp = new LayoutParams(-1, -1);
		lp.setMargins(10, 10, 0, 0);
		background.setBackgroundResource(R.drawable.twozerofoureightbg);
		addView(background, lp);
		label = new TextView(getContext());
		label.setTextSize(28);
		label.setTextColor(Color.RED);
		TextPaint tp = label.getPaint();
		tp.setFakeBoldText(true);
		label.setGravity(Gravity.CENTER);
		lp = new LayoutParams(-1, -1);
		lp.setMargins(10, 10, 0, 0);
		addView(label, lp);
		setNum(0);
	}
	private int num = 0;
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
		if (num<=0) {
			label.setText("");
		}else{
			label.setText(String.valueOf(num));
			
		}

		switch (num) {
		case 0:
			label.setBackgroundColor(0x00000000);
			break;
		case 2:
			label.setBackgroundColor(Color.parseColor("#eee4da"));
			label.setTextColor(Color.parseColor("#a1988e"));
			break;
		case 4:
			label.setBackgroundColor(Color.parseColor("#ede0c8"));
			label.setTextColor(Color.parseColor("#a1988e"));
			break;
		case 8:
			label.setBackgroundColor(Color.parseColor("#f2b179"));
			label.setTextColor(Color.WHITE);
			break;
		case 16:
			label.setBackgroundColor(Color.parseColor("#f59563"));
			label.setTextColor(Color.WHITE);
			break;
		case 32:
			label.setBackgroundColor(Color.parseColor("#f67c5f"));
			label.setTextColor(Color.WHITE);
			break;
		case 64:
			label.setBackgroundColor(0xfff65e3b);
			label.setTextColor(Color.WHITE);
			break;
		case 128:
			label.setBackgroundColor(0xffedcf72);
			break;
		case 256:
			label.setBackgroundColor(0xffedcc61);
			break;
		case 512:
			label.setBackgroundColor(0xffedc850);
			break;
		case 1024:
			label.setBackgroundColor(0xffedc53f);
			break;
		case 2048:
			label.setBackgroundColor(0xffedc22e);
			break;
		default:
			label.setBackgroundColor(0xff3c3a32);
			break;
		}
	}

	public boolean equals(Card another) {
		return getNum()==another.getNum();
	}
	
	public TextView getLabel() {
		return label;
	}
	
	public void addScaleAnimation(){
		ScaleAnimation sa = new ScaleAnimation(0.1f, 1, 0.1f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		sa.setDuration(1000);
		setAnimation(null);
		getLabel().startAnimation(sa);
	}

}
