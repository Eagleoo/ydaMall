package com.mall.serving.community.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment {

	public FragmentActivity context;
	public InternalReceiver internalReceiver = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		context = (FragmentActivity) getActivity();

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
		// TODO Auto-generated method stub

	}

	class InternalReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent == null || TextUtils.isEmpty(intent.getAction())) {
				return;
			}

			onReceiveBroadcast(intent);
		}
	}

	public void onReceiveBroadcast(Intent intent) {

	}

	boolean mExternalStorageAvailable = false;
	boolean mExternalStorageWriteable = false;

	public final void registerReceiver(String[] actionArray) {

		IntentFilter intentfilter = new IntentFilter();

		for (String action : actionArray) {
			intentfilter.addAction(action);
		}

		if (internalReceiver == null) {
			internalReceiver = new InternalReceiver();
		}
		context.registerReceiver(internalReceiver, intentfilter);
	}

	@Override
	public void onDestroy() {
		if (internalReceiver != null) {
			context.unregisterReceiver(internalReceiver);
		}

		super.onDestroy();

	}
}
