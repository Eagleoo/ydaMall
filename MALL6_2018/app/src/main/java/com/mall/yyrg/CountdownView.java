package com.mall.yyrg;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mall.util.Util;
import com.mall.view.R;

public class CountdownView extends LinearLayout {
	private TextView ss_text1;
	private TextView ss_text2;
	private TextView ss_text3;
	private TextView ss_text4;
	private TextView ss_text5;
	private TextView ss_text6;
	private TextView ss_text7;
	private TextView tv_limit_time;
	private TextView shisi_text;
	private View ll_limit_time;
	private boolean isRunThread = false;

	public CountdownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.HORIZONTAL);
		View v = LayoutInflater.from(context).inflate(
				R.layout.yyrg_limit_time_item, null);
		v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, Util.dpToPx(context, 50)));
		addView(v);
		
		

		ss_text1 = (TextView) v.findViewById(R.id.ss_text1);
		ss_text2 = (TextView) v.findViewById(R.id.ss_text2);
		ss_text3 = (TextView) v.findViewById(R.id.ss_text3);
		ss_text4 = (TextView) v.findViewById(R.id.ss_text4);
		ss_text5 = (TextView) v.findViewById(R.id.ss_text5);
		ss_text6 = (TextView) v.findViewById(R.id.ss_text6);
		ss_text7 = (TextView) v.findViewById(R.id.ss_text7);

		tv_limit_time = (TextView) v.findViewById(R.id.tv_limit_time);
		shisi_text = (TextView) v.findViewById(R.id.shisi_text);

		ll_limit_time = v.findViewById(R.id.ll_limit_time);

	}

	public void startCountDown(final Date date) {
		isRunThread = true;
		final String[] strs = { "周二10：00", "周二14：00", "周五10：00", "周五14：00" };
		int i = 0;
		
		if (date.getHours() == 10) {
			
			
			if (date.getDay() == 2) {
				i = 0;
			} else {
				i = 2;
			}
		} else {
			if (date.getDay() == 2) {
				i = 1;
			} else {
				i = 3;
			}

		}
		tv_limit_time.setText(strs[i]);

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (200 == msg.what) {
					Calendar c1 = Calendar.getInstance();
					long long1 = date.getTime() - c1.getTimeInMillis();
					if (long1 < 0) {
						ll_limit_time.setVisibility(View.GONE);
						shisi_text.setText("已揭晓");
					} else {
						long1 = long1 / 1000;
						long hour = (int) (long1 / (60 * 60));
						long fen = (long1 % (60 * 60)) / 60;
						long miao = ((long1 % (60 * 60)) % 60);
						if (hour / 10 >= 10) {
							ss_text1.setVisibility(View.VISIBLE);
							ss_text1.setText(hour / 100 + "");
							ss_text2.setText((hour %100)/10 + "");
						} else {
							ss_text1.setVisibility(View.GONE);
							ss_text2.setText(((hour) / 10) + "");
						}

						
						ss_text3.setText(((hour) % 10) + "");
						ss_text4.setText(fen / 10 + "");
						ss_text5.setText(fen % 10 + "");
						ss_text6.setText(miao / 10 + "");
						ss_text7.setText(miao % 10 + "");
					}

				}
			}
		};
		new Thread() {

			public void run() {
				while (isRunThread) {
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Message msg = new Message();
					msg.what = 200;
					handler.sendMessage(msg);

				}

			};

		}.start();

	}

	public void stopCountDown() {
		isRunThread = false;

	}

}
