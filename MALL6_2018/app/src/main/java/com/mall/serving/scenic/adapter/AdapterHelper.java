package com.mall.serving.scenic.adapter;

import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterHelper<T> {

	private SparseArray<View> views;
	private Context context;

	private List<T> list;

	private View v;

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public View getV() {
		return v;
	}

	public void setV(View v) {
		this.v = v;
	}

	public AdapterHelper() {
		super();
		views = new SparseArray<View>();
	}

	public AdapterHelper(Context context, List<T> list, View v) {
		super();
		this.context = context;
		this.list = list;
		this.v = v;
		views = new SparseArray<View>();
	}

	public void setTextViewText(int rid, Character text) {
		View view = views.get(rid);
		if (view != null && view instanceof TextView) {
			TextView tv = (TextView) view;
			tv.setText(text);

		} else {
			views.put(rid, v.findViewById(rid));
		}

	}

	public void setImageViewResource(int rid, int resid) {
		View view = views.get(rid);
		if (view != null && view instanceof ImageView) {
			ImageView iv = (ImageView) view;
			iv.setImageResource(resid);

		} else {
			views.put(rid, v.findViewById(rid));
		}

	}

	public void setViewBackgroundResource(int rid, int resid) {
		View view = views.get(rid);
		if (view != null) {

			view.setBackgroundResource(resid);

		} else {
			views.put(rid, v.findViewById(rid));
		}

	}

	public View getViewById(int rid) {
		View view = views.get(rid);
		if (view == null) {
			view = v.findViewById(rid);
		}
		return view;

	}

}
