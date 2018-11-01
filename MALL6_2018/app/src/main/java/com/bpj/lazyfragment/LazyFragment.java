package com.bpj.lazyfragment;

import android.support.v4.app.Fragment;
import android.util.Log;

public class LazyFragment extends Fragment {
	/**
	 * 当前Fragment是否显示：true,显示
	 */

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		Log.e("当前Fragment是否显示","isVisibleToUser"+isVisibleToUser);

		if(isVisibleToUser){

			onVisible();
		}
	}

	protected void onVisible(){

	}

}
