package com.mall.serving.doubleball.selectnumview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mall.util.Util;
import com.mall.view.R;

public class SelectNumView extends LinearLayout {
	private Gallery gallery;
	private int count;
	private Context context;
	private boolean isRed = true;
	private int digit;

	public SelectNumView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		gallery = new Gallery(context);
		gallery.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		addView(gallery);
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setRed(boolean isRed) {
		this.isRed = isRed;
	}

	public void creat() {
		SelectNumAdapter adapter = new SelectNumAdapter(context, count);
		gallery.setAdapter(adapter);
		if (isRed) {
			gallery.setSelection(6);
			gallery.setSelected(true);
		}else {
			gallery.setSelection(2);
			gallery.setSelected(true);
		}

		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {

				digit = position + 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

	}

	public int getNum() {
		return digit;

	}

	class SelectNumAdapter extends BaseAdapter {
		Context context;
		int num;

		public SelectNumAdapter(Context context, int num) {
			super();
			this.context = context;
			this.num = num;
		}

		@Override
		public int getCount() {

			return num;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			int width = Util.dpToPx(context, 150);
			int height = Util.dpToPx(context, 30);

			if (v == null) {
				ViewCache cache = new ViewCache();
				v = LayoutInflater.from(context).inflate(
						R.layout.doubleball_selectnumview, null);

				v.setLayoutParams(new Gallery.LayoutParams(width / 2,
						height - 5));
				cache.tv_selectnum_red = (TextView) v
						.findViewById(R.id.tv_selectnum_red);
				cache.tv_selectnum_blue = (TextView) v
						.findViewById(R.id.tv_selectnum_blue);

				v.setTag(cache);
			}
			ViewCache cache = (ViewCache) v.getTag();
			cache.tv_selectnum_red.setText((position + 1) + "");
			cache.tv_selectnum_blue.setText((position + 1) + "");
			if (isRed) {
				cache.tv_selectnum_red.setVisibility(View.VISIBLE);
				cache.tv_selectnum_blue.setVisibility(View.GONE);
			} else {
				cache.tv_selectnum_red.setVisibility(View.GONE);
				cache.tv_selectnum_blue.setVisibility(View.VISIBLE);
			}
			return v;
		}

		class ViewCache {
			TextView tv_selectnum_red;
			TextView tv_selectnum_blue;

		}

	}

}
