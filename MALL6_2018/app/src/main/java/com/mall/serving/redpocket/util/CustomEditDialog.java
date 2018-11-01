package com.mall.serving.redpocket.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mall.view.R;

public class CustomEditDialog extends Dialog {

	private String title;
	private String message;
	private String left;
	private String right;
	private View.OnClickListener leftClick;
	private View.OnClickListener rightClick;
	private TextView dialog_left;
	private TextView dialog_right;
	private TextView tv_dialog_title;
	private TextView tv_dialog_content;
	private EditText et_dialog_content;

	public CustomEditDialog(String title, String message, final Context context,
			String right, String left, final View.OnClickListener rightClick,
			final View.OnClickListener leftClick) {
		super(context, R.style.CustomDialog);
		this.title = title;
		this.message = message;
		this.left = left;
		this.right = right;
		this.leftClick = leftClick;
		this.rightClick = rightClick;

	}

	public TextView getTv_dialog_content() {
		return tv_dialog_content;
	}

	public void setTv_dialog_content(TextView tv_dialog_content) {
		this.tv_dialog_content = tv_dialog_content;
	}

	public CustomEditDialog(Context context) {
		super(context, R.style.CustomDialog);
	}

	public EditText getEt_dialog_content() {
		return et_dialog_content;
	}

	public void setEt_dialog_content(EditText et_dialog_content) {
		this.et_dialog_content = et_dialog_content;
	}

	public View.OnClickListener getLeftClick() {
		return leftClick;
	}

	public void setLeftClick(View.OnClickListener leftClick) {
		this.leftClick = leftClick;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_custom_dialog_edit);
		getWindow().setBackgroundDrawable(new BitmapDrawable());
		setCanceledOnTouchOutside(true);
		tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);
		tv_dialog_content = (TextView) findViewById(R.id.tv_dialog_content);
		et_dialog_content = (EditText) findViewById(R.id.et_dialog_content);
		dialog_left = (TextView) findViewById(R.id.dialog_left);
		dialog_right = (TextView) findViewById(R.id.dialog_right);
		final TextView dialog_sure = (TextView) findViewById(R.id.dialog_sure);

		if (TextUtils.isEmpty(title)) {
			title = "温馨提示";
		}

		tv_dialog_title.setText(title);
		tv_dialog_content.setText(message);
//		if (TextUtils.isEmpty(left) || TextUtils.isEmpty(right)) {
//			dialog_left.setVisibility(View.GONE);
//			dialog_right.setVisibility(View.GONE);
//			dialog_sure.setVisibility(View.VISIBLE);
//		} else {
//			dialog_left.setText(left);
//			dialog_right.setText(right);
//
//		}

		View.OnClickListener clickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == dialog_left) {
					if (leftClick != null) {
						leftClick.onClick(v);
					}

//					dismiss();
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
				if (rightClick != null) {
					rightClick.onClick(dialog_right);
					return true;
				}

			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
