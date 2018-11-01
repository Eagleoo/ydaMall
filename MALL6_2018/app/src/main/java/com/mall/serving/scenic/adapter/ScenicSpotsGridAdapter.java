package com.mall.serving.scenic.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.view.R;

public class ScenicSpotsGridAdapter extends NewBaseAdapter {

	private int type;
	public ScenicSpotsGridAdapter(Context c, List list,int type) {
		super(c, list);
		this.type=type;
	}

	@Override
	public View getView(int p, View v, ViewGroup arg2) {

		if (v == null) {
			v = LayoutInflater.from(context).inflate(
					R.layout.scenic_spots_item1, null);
		}

		return v;
	}

}
