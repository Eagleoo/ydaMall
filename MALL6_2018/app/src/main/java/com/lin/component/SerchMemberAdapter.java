package com.lin.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mall.model.MemberInfo;
import com.mall.util.Util;
import com.mall.view.R;

public class SerchMemberAdapter extends BaseAdapter {
	private Context context;
	private List<MemberInfo> list = new ArrayList<MemberInfo>();
	private LayoutInflater inflater;
	private List<MemberInfo> usersstring = new ArrayList<MemberInfo>();
	private List<View> viewList = new ArrayList<View>();
	// private Map<Integer, View> map = new HashMap<Integer, View>();
	private int layoutid;
	private Handler handler;
	public final int YES = 135;
	public final int NO = YES + 1;
	private int theState;
	private String hand = "";
	private int x = 0;

	public SerchMemberAdapter(Context context, int layoutid) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.layoutid = layoutid;
	}

	public SerchMemberAdapter(Context context, int layoutid, Handler handler) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.layoutid = layoutid;
		this.handler = handler;
	}

	public void setList(List<MemberInfo> list, int state) {
		this.list.addAll(list);

		this.theState = state;
		this.notifyDataSetChanged();
		SetDataToUserString();
	}

	public void clear() {
		if (null != this.list && this.list.size() > 0) {
			this.list.clear();
		}
		if (null != this.usersstring && this.usersstring.size() > 0) {
			this.usersstring.clear();
		}

	}

	public List<MemberInfo> getList() {
		return list;
	}

	public List<MemberInfo> getUsersstring() {
		return usersstring;
	}

	public void clearlist() {
		this.list.clear();
		// map.clear();
		this.notifyDataSetChanged();
	}

	public void clearUserString() {
		if(null != this.usersstring && this.usersstring.size() > 0)
		this.usersstring.clear();
	}

	public void UpData() {
		SetDataToUserString();
		this.notifyDataSetChanged();
		
	}

	public void SetDataToUserString() {
		for (int i = 0; i < this.list.size(); i++) {
			MemberInfo m = this.list.get(i);
			if (m.isSelected()) {
				if (!usersstring.contains(m)) {
					usersstring.add(m);
				}
			}
		}
	}

	public void setState(String state, int x) {
		this.hand = state;
		this.x = x;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		ViewHolder holder = null;
		final HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();
		final int index = position;
		final MemberInfo m = list.get(position);
		// if (map.get(position) == null) {
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = inflater.inflate(layoutid, null);

			holder.t1 = (TextView) convertView.findViewById(R.id.search_t1);
			holder.t2 = (TextView) convertView.findViewById(R.id.search_t2);
			holder.level = (TextView) convertView.findViewById(R.id.level);
			holder.c1 = (CheckBox) convertView.findViewById(R.id.search_c1_);
			holder.letter = (TextView) convertView
					.findViewById(R.id.item_shopuser_tvletter);
			convertView.setTag(holder);
			// map.put(position, convertView);
		} else {
			// convertView = map.get(position);
			holder = (ViewHolder) convertView.getTag();
		}
		final LinearLayout item = (LinearLayout) convertView
				.findViewById(R.id.item_push_search_List);
		// 从0-9的时候，就是第一页的时候，convertview 均为空，

		if (!m.getName().equals("") && m.getName() != null) {
			holder.t1.setText("  " + m.getName());
		} else {
			holder.t1.setText("  " + "暂无姓名");
		}

		holder.level.setText(m.getUserLevel());
		// holder.t1.setText(m.getUserid());;
		holder.t1.setTextColor(Color.BLACK);
		if (!Util.isNull(m.getPhone())) {
			holder.t2.setText(m.getPhone());
		}
		holder.t2.setTextColor(Color.BLACK);

		if (theState == 0) {
			holder.letter.setVisibility(View.GONE);

		} else if (theState == 1) {

			int section = m.getSortLetter().charAt(0);

			if (position == getPositionForSection(section)) {
				if (position == 0) {
					holder.letter.setVisibility(View.GONE);
				} else {
					//
					// Message.obtain(handler, LETTER,
					// s.getSortLetters().charAt(0)).sendToTarget();
					// Message.obtain(handler, POSITION, arg0).sendToTarget();
					holder.letter.setVisibility(View.VISIBLE);

					holder.letter.setText(m.getSortLetter());
				}

			} else {
				holder.letter.setVisibility(View.GONE);
			}

		} else if (theState == 2) {

			holder.letter.setVisibility(View.GONE);

		}
		if (hand.equals("gone")) {
			if (position == this.x) {
				holder.letter.setVisibility(View.GONE);
			}

		}

		item.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					item.setBackgroundColor(context.getResources().getColor(
							R.color.gray_div));
					break;
				case MotionEvent.ACTION_MOVE:
					item.setBackgroundColor(context.getResources().getColor(
							R.color.gray_div));
					break;
				case MotionEvent.ACTION_UP:
					item.setBackgroundColor(context.getResources().getColor(
							R.color.white));
					break;
				case MotionEvent.ACTION_CANCEL:
					item.setBackgroundColor(context.getResources().getColor(
							R.color.white));
					break;
				}
				return false;
			}
		});
		
		holder.c1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					
					if (!usersstring.contains(m)) {
						usersstring.add(m);
						m.setSelected(true);
					}
					buttonView.setChecked(true);
					buttonView.invalidate();
					handler.sendEmptyMessage(YES);
					state.put(position, isChecked);

				} else {
					
					buttonView.setChecked(false);
					if (usersstring.contains(m)) {
						m.setSelected(false);
						usersstring.remove(m);
						buttonView.invalidate();
					}
					handler.sendEmptyMessage(NO);
					state.remove(position);
				}
			}
		});
		final CheckBox check = holder.c1;

		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (check.isChecked()) {
					list.get(position).setSelected(false);
					// state.put(position, false);

				//	handler.sendEmptyMessage(NO);
				} else {
					list.get(position).setSelected(true);
					// state.put(position, true);

					//handler.sendEmptyMessage(YES);
				}
				check.toggle();

				// check.setChecked(state.get(position));
				SerchMemberAdapter.this.notifyDataSetChanged();

			}
		});

		// if (null != state.get(position) && state.containsKey(position)) {
		// holder.c1.setChecked(state.get(position));
		// } else {
		// holder.c1.setChecked(false);
		// }
		holder.c1.setChecked(m.isSelected());

		return convertView;
	}

	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetter();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;

	}

	static class ViewHolder {
		TextView t1;
		TextView t2;
		TextView level;
		TextView letter;
		CheckBox c1;
	}
}
