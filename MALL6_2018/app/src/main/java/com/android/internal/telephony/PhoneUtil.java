package com.android.internal.telephony;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;

import com.lidroid.xutils.util.LogUtils;

import java.lang.reflect.Method;

public class PhoneUtil {

	public static boolean isAnswer = false;
	
	public static void answer(Context mContext) {
		LogUtils.e("是否需要自动接听："+isAnswer);
		// 为了避免自动截图那个其他人来电
		if(!isAnswer){
			return ;
		}
		try {
//			Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
//			IBinder binder = (IBinder) method.invoke(null, new Object[] { Context.TELEPHONY_SERVICE });
//			ITelephony telephony = ITelephony.Stub.asInterface(binder);
//			telephony.answerRingingCall();
		} catch (Exception e) {
			Log.d("Sandy", "", e);
			try {
				Log.e("Sandy", "for version 4.1 or larger");
				Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
				KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
				intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
				mContext.sendOrderedBroadcast(intent, "android.permission.CALL_PRIVILEGED");
			} catch (Exception e2) {

				Log.d("Sandy", "", e2);

				Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);

				KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);

				meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);

				mContext.sendOrderedBroadcast(meidaButtonIntent, null);

			}

		}

	}

	public static void endcall(Context mContext) {
		try {

			Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
			IBinder binder = (IBinder) method.invoke(null, new Object[] { Context.TELEPHONY_SERVICE });
			// ITelephony telephony = ITelephony.Stub.asInterface(binder);
			// telephony.endCall();

		} catch (Exception e) {

		}

	}

}
