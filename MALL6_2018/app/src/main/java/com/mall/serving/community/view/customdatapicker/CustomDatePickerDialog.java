package com.mall.serving.community.view.customdatapicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mall.serving.community.view.customdatapicker.CustomDatePicker.ChangingListener;
import com.mall.view.R;

public class CustomDatePickerDialog extends Dialog {

	private TextView dialog_left;
	private TextView dialog_right;
	private onDateListener listener;
	private Calendar c = Calendar.getInstance();
	private  CustomDatePicker cdp;
	public CustomDatePickerDialog(Context context, Calendar c) {
		super(context, R.style.CustomDialog);
		this.c = c;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_custom_datepicker_dialog);
		getWindow().setBackgroundDrawable(new BitmapDrawable());
		setCanceledOnTouchOutside(true);
		TextView tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);
		final TextView tv_dialog_content = (TextView) findViewById(R.id.tv_dialog_content);
		dialog_left = (TextView) findViewById(R.id.dialog_left);
		dialog_right = (TextView) findViewById(R.id.dialog_right);
		final TextView dialog_sure = (TextView) findViewById(R.id.dialog_sure);
		cdp = (CustomDatePicker) findViewById(R.id.cdp);

		cdp.setDate(c);
		
		
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy年MM月dd日 E");
		String string = sdfFrom.format(c.getTime());
		
		tv_dialog_content.setText(string);

		cdp.addChangingListener(new ChangingListener() {

			@Override
			public void onChange(Calendar c1) {
				c = c1;
				SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy年MM月dd日 E");
				String string = sdfFrom.format(c.getTime());
				
				tv_dialog_content.setText(string);

			}
		});
		View.OnClickListener clickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == dialog_left) {
					if (listener != null) {
						listener.dateFinish(c);
					}

				} else if (v == dialog_right || v == dialog_sure) {

				}
				dismiss();
			}
		};

		dialog_left.setOnClickListener(clickListener);
		dialog_right.setOnClickListener(clickListener);

	}
	
	public void setNowData(Calendar c){
		cdp.setNowData(c);
		
		
	}

	public void addDateListener(onDateListener listener) {
		this.listener = listener;
	}

	public interface onDateListener {
		void dateFinish(Calendar c);
	}

}
