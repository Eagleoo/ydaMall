package com.mall.serving.doubleball;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.mall.util.Util;
import com.mall.view.R;

public class DoubleballDialog extends Dialog {

	private String message;
	private String left;
	private String right;
	private View.OnClickListener leftClick;
	private View.OnClickListener rightClick;
	private TextView dialog_left;

	public DoubleballDialog(String message, final Context context,
			String right, String left, final View.OnClickListener rightClick,
			final View.OnClickListener leftClick) {
		super(context, R.style.Dialog);

		this.message = message;
		this.left = left;
		this.right = right;
		this.leftClick = leftClick;
		this.rightClick = rightClick;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doubleball_dialog);
		getWindow().setBackgroundDrawable(new BitmapDrawable());
		TextView tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);
		TextView tv_dialog_content = (TextView) findViewById(R.id.tv_dialog_content);
		dialog_left = (TextView) findViewById(R.id.dialog_left);
		final TextView dialog_right = (TextView) findViewById(R.id.dialog_right);
		final TextView dialog_sure = (TextView) findViewById(R.id.dialog_sure);
		View diglog_bottom = findViewById(R.id.diglog_bottom);

		tv_dialog_title.setText("温馨提示");
		tv_dialog_content.setText(message);
		if (Util.isNull(left) || Util.isNull(right)) {
			dialog_left.setVisibility(View.GONE);
			dialog_right.setVisibility(View.GONE);
			diglog_bottom.setVisibility(View.VISIBLE);
		} else {
			dialog_left.setText(left);
			dialog_right.setText(right);

		}

		View.OnClickListener clickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == dialog_left) {
					if (leftClick != null) {
						leftClick.onClick(v);
					}

					dismiss();
				} else if (v == dialog_right || v == dialog_sure) {
					if (rightClick != null) {
						rightClick.onClick(v);
					}

					dismiss();
				}

			}
		};

		dialog_left.setOnClickListener(clickListener);
		dialog_right.setOnClickListener(clickListener);
		dialog_sure.setOnClickListener(clickListener);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK && isShowing()) {
				if (leftClick != null) {
					leftClick.onClick(dialog_left);
					return true;
				}

				
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
