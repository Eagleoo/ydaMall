package com.mall.serving.voip.view.popupwindow;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mall.view.R;

public class Dialog_can_not_receive extends Dialog {

	private int width;
	private int height;

	public Dialog_can_not_receive(Context context, int style) {
		super(context, style);
	}

	public Dialog_can_not_receive(Context context, int style, int width,
			int height) {
		super(context, style);

		this.width = width;
		this.height = height;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.d_connotyzm);

		Button sure = (Button) findViewById(R.id.connot_yzm_but);
		LinearLayout lin = (LinearLayout) this
				.findViewById(R.id.dialog_cannot_layout);

		LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) lin
				.getLayoutParams();
		lp.width = width;
		lp.height = lp.WRAP_CONTENT;
		lin.setLayoutParams(lp);

		lin.setBackgroundResource(R.drawable.textview_rounded_cannot_receive_white);

		View.OnClickListener click = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		};

		sure.setOnClickListener(click);

	}

}
