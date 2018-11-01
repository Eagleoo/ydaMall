package com.mall.serving.community.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mall.serving.community.util.Util;
import com.mall.serving.redpocket.fragment.RedPocketSentFragment;

public class BaseReceiverFragment extends BaseFragment {

	public FragmentActivity context;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		context = (FragmentActivity) getActivity();

		registerReceiver(new String[] { Intent.ACTION_MEDIA_MOUNTED,
				Intent.ACTION_MEDIA_REMOVED,
				RedPocketSentFragment.RedPocketSentAction });
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void fragementOnResume() {

	}

	public void fragementOnStart() {

	}

	public void onMenuClick() {

	}

	

	public void onReceiveBroadcast(Intent intent) {
		/**
		 * version 3.5 for listener SDcard status
		 */
		if (Intent.ACTION_MEDIA_REMOVED
				.equalsIgnoreCase(intent.getAction())
				|| Intent.ACTION_MEDIA_MOUNTED.equalsIgnoreCase(intent
						.getAction())) {

			updateExternalStorageState();
			return;
		}
	}

	boolean mExternalStorageAvailable = false;
	boolean mExternalStorageWriteable = false;

	void updateExternalStorageState() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		handleExternalStorageState(mExternalStorageAvailable,
				mExternalStorageWriteable);
	}

	void handleExternalStorageState(boolean available, boolean writeable) {

		if (!available || !writeable) {

			Util.show("储存卡已拔出，语音功能将暂时不可用");
		}

	}

	

	
}
