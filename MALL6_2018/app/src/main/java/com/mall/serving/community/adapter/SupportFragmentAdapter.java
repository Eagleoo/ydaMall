package com.mall.serving.community.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class SupportFragmentAdapter extends FragmentPagerAdapter {

	List list;
	private FragmentManager fm;

	public SupportFragmentAdapter(FragmentManager fm, List list) {
		super(fm);
		this.list = list;
		this.fm = fm;

	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return super.instantiateItem(container, position);
	}

	@Override
	public Fragment getItem(int paramInt) {
		Fragment fragment = (Fragment) list.get(paramInt);

		return fragment;
	}

	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		}
		return list.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void setFragments(List lists) {
//		if (this.list != null) {
//
//			for (int i = 0; i < list.size(); i++) {
//				Fragment f = (Fragment) list.get(i);
//
//				fm.beginTransaction().remove(f).commit();
////				f.onDestroy();
//				f=null;
//			}
//
//		}
		list.clear();
		list.addAll(lists);
		notifyDataSetChanged();
	}
}
