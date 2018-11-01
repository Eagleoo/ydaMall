package com.lin.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.mall.util.Util;

public class NProgressBar extends ProgressBar {
	private CharSequence text;
	private int textColor=Color.parseColor("#535353");

	public NProgressBar(Context context) {
		super(context);
	}

	public NProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Rect rect = new Rect();
		Paint paint = new Paint();
		paint.setTextSize(Util.dpToPx(this.getContext(), 16F));
		paint.setColor(textColor);
		paint.getTextBounds(this.text.toString(), 0, this.text.toString().length(), rect);
		float x = (getWidth() / 2) - rect.centerX();
		float y = (getHeight() / 2) - rect.centerY();
		canvas.drawText(this.text.toString(), x, y, paint);
	}

	public void setText(CharSequence text) {
		this.text = text;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public CharSequence getText() {
		return text;
	}	
}
