package com.android.internal.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneBroadcastReceiver extends BroadcastReceiver {

	private final static String TAG = "PHONE_STATE";

	private boolean isAnswer = true;

	public PhoneBroadcastReceiver(boolean isAnswer) {
		super();
		this.isAnswer = isAnswer;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i(TAG, "[Broadcast]" + action);

		// 呼入电话
		if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
			Log.i(TAG, "[Broadcast]PHONE_STATE");
			doReceivePhone(context, intent, isAnswer);
		}
	}

	/**
	 * 处理电话广播.
	 * 
	 * @param context
	 * @param intent
	 */
	public void doReceivePhone(Context context, Intent intent, boolean isAnswer) {
		String phoneNumber = intent
				.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		int state = telephony.getCallState();

		switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:
			Log.i(TAG, "[Broadcast]等待接电话=" + phoneNumber);
			try {
				if (isAnswer) {
					PhoneUtil.answer(context);
					PhoneUtil.isAnswer = false;
					System.out.println("PhoneBroadcastReceiver  Answer");
				} else {
					PhoneUtil.endcall(context);
					System.out.println("PhoneBroadcastReceiver  endcall");
				}

			} catch (Exception e) {
				Log.e(TAG, "[Broadcast]Exception=" + e.getMessage(), e);
			}
			break;
		case TelephonyManager.CALL_STATE_IDLE:
			Log.i(TAG, "[Broadcast]电话挂断=" + phoneNumber);
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Log.i(TAG, "[Broadcast]通话中=" + phoneNumber);
			break;
		}
	}

}
