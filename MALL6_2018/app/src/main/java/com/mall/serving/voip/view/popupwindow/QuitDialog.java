package com.mall.serving.voip.view.popupwindow;

import com.mall.view.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class QuitDialog extends Dialog {

	private View.OnClickListener leftClick;
	private View.OnClickListener rightClick;
	private String leftstr;
	private String rightstr;
	private String message;
	TextView left;
	TextView right;
	TextView content;

	public QuitDialog(Context context, final View.OnClickListener leftClick,
			final View.OnClickListener rightClick) {
		super(context, R.style.FullHeightDialog);
		this.leftClick = leftClick;
		this.rightClick = rightClick;
	}

	public QuitDialog(Context context, String Message, String left,
			String right, final View.OnClickListener leftClick,
			final View.OnClickListener rightClick) {
		super(context, R.style.FullHeightDialog);
		this.leftClick = leftClick;
		this.rightClick = rightClick;
		this.leftstr = left;
		this.rightstr = right;
		this.message = Message;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.g_dialog_quit);
		left = (TextView) this.findViewById(R.id.dialog_left);
		right = (TextView) this.findViewById(R.id.dialog_right);
		content = (TextView) this.findViewById(R.id.message);
		if (null != leftstr) {
			left.setText(leftstr);
		}
		if (null != rightstr) {
			right.setText(rightstr);
		}
		if (null != message) {
			content.setText(message);
		}

		View.OnClickListener clickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == left) {
					dismiss();
					if (leftClick != null) {
						leftClick.onClick(v);
					}
				} else if (v == right) {
					dismiss();
					if (rightClick != null) {
						rightClick.onClick(v);
					}
				}
			}
		};

		left.setOnClickListener(clickListener);
		right.setOnClickListener(clickListener);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK && isShowing()) {
				if (leftClick != null) {
					leftClick.onClick(left);
					return true;
				}

			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
