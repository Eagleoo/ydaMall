package com.way.note;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mall.view.R;

/**
 * 
 * @author way
 * 
 */
public class AlarmSettingAdapter extends BaseAdapter {
	private List<String> items;
	private List<String> values;
	private LayoutInflater inflater;
	private static final String TAG = "AlarmSettingAdapter";

	public AlarmSettingAdapter(Context context, List<String> items,
			List<String> values) {
		Log.v(TAG, "construction  items-->" + items);
		Log.v(TAG, "construction  values-->" + values);
		inflater = LayoutInflater.from(context);
		this.items = items;
		this.values = values;
	}

	public void updateItem(List<String> values) {
		Log.v(TAG, "construction  values-->" + values);
		this.values = values;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup group) {
		Log.v(TAG, "getView  position-->" + position);
		ViewHolder holder = new ViewHolder();
		if (view == null) {
			view = inflater.inflate(R.layout.note_alarm_setting_item_layout, null);
			holder.item = (TextView) view.findViewById(R.id.alarm_item);
			holder.item_value = (TextView) view
					.findViewById(R.id.alarm_item_value);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		// title = title.length() > 15 ? title.substring(0, 15) + "..." : title;
		holder.item.setText(items.get(position));
		holder.item_value.setText(values.get(position));
	//	holder.item_value.setText("2014-41-55");
		return view;
	}

	private class ViewHolder {
		private TextView item;
		private TextView item_value;
	}
}
