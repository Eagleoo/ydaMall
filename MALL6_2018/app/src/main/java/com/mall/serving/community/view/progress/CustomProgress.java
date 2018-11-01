package com.mall.serving.community.view.progress;




import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mall.view.R;

public class CustomProgress extends PopupWindow {

	private static CustomProgress customProgressDialog;

	public CustomProgress(Context context, String str) {
		super(context);
		View v = LayoutInflater.from(context).inflate(
				R.layout.community_custom_progress_dialog, null);
		TextView tv = (TextView) v.findViewById(R.id.c_tv_loadingmsg);
		
	
		tv.setText(str);

		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		// setBackgroundDrawable(getBackground());
		setFocusable(true);
		setOutsideTouchable(false);

		setContentView(v);

		showAtLocation(v, Gravity.CENTER, 0, 0);
	}

	/**
	 * [Summary] setMessage 提示内容
	 * 
	 * @param strMessage
	 * @return
	 * 
	 */
	public CustomProgress setMessage(String strMessage) {

		return this;
	}

	// 显示滚动进度
	public static void showProgressDialog(Context context, String strMessage) {
		if (customProgressDialog == null) {
			customProgressDialog = new CustomProgress(context, strMessage);
		}

	}

	// 隐藏滚动进度
	public static void hideProgressDialog() {
		if (customProgressDialog != null) {

			customProgressDialog.dismiss();
			customProgressDialog = null;
		}
	}

}
