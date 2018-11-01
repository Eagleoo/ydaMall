package com.mall.pushmessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.model.MemberInfo;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

public class PushUsersList extends Activity {
	private ListView listview;
	private ArrayList<MemberInfo> list = new ArrayList<MemberInfo>();
	private SerchMemberAdapter adapter;
	private String listtype = "";
	private List<String> userlist = new ArrayList<String>();
	private List<String> zshylist = new ArrayList<String>();
	private List<String> xfhyuserlist = new ArrayList<String>();
	private List<String> tjhylist = new ArrayList<String>();
	private ArrayList<String> lists = new ArrayList<String>();
	private int windowHeight=400;
	private String totalCount="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push_user_list);
		WindowManager wm = this.getWindowManager();
		windowHeight = wm.getDefaultDisplay().getHeight();
		getIntentData();
		initView();
	}
	private void getIntentData(){
		listtype = this.getIntent().getStringExtra("listtype");
		totalCount=this.getIntent().getStringExtra("totalcount");
		if (listtype.equals("zdy")) {
			userlist = this.getIntent().getStringArrayListExtra("zdylist");
			if (userlist == null || userlist.size() == 0) {
				Toast.makeText(PushUsersList.this, "还未选中会员",Toast.LENGTH_LONG).show();
				PushUsersList.this.finish();
			} else {
				for (int i = 0; i < userlist.size(); i++) {
					String[] s = userlist.get(i).split(",,,");
					MemberInfo m = new MemberInfo();
					m.setName(s[0]);
					m.setPhone(s[1]);
					m.setRegTime(s[2]);
					m.setUserid(s[3]);
					m.setSelected(true);
					list.add(m);
				}
			}
		} else if (listtype.equals("zshy")) {
			zshylist = this.getIntent().getStringArrayListExtra("zshylist");
			if (zshylist == null || zshylist.size() == 0) {
				Toast.makeText(PushUsersList.this, "还未选中会员",Toast.LENGTH_LONG).show();
				PushUsersList.this.finish();
			} else {
				for (int i = 0; i < zshylist.size(); i++) {
					String[] s = zshylist.get(i).split(",,,");
					MemberInfo m = new MemberInfo();
					m.setName(s[0]);
					m.setPhone(s[1]);
					m.setRegTime(s[2]);
					m.setUserid(s[3]);
					m.setSelected(true);
					list.add(m);
				}
				
			}
		}else if (listtype.equals("yaoqing")) {
			tjhylist = this.getIntent().getStringArrayListExtra("tjhylist");
			if (tjhylist == null || tjhylist.size() == 0) {
				Toast.makeText(PushUsersList.this, "还未选中会员",Toast.LENGTH_LONG).show();
				PushUsersList.this.finish();
			} else {
				for (int i = 0; i < tjhylist.size(); i++) {
					String[] s = tjhylist.get(i).split(",,,");
					MemberInfo m = new MemberInfo();
					m.setName(s[0]);
					m.setPhone(s[1]);
					m.setRegTime(s[2]);
					m.setUserid(s[3]);
					m.setSelected(true);
					list.add(m);
				}
				
			}
		}
	}
	private void initView() {
		TextView topright = (TextView)findViewById(R.id.topright);
		TextView top_back = (TextView)findViewById(R.id.top_back);
		TextView NavigateTitle = (TextView)findViewById(R.id.NavigateTitle);
		NavigateTitle.setText("选中会员");
		top_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Util.showIntent(PushUsersList.this, PushMessage.class);

			}
		});
		topright.setText("保存");
		topright.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				for (MemberInfo m : adapter.getUsersstring()) {
					String s = m.getName() + ",,," + m.getPhone()+ ",,,"+ m.getRegTime() + ",,," + m.getUserid();
					lists.add(s);
				}
				Intent intent = new Intent(PushUsersList.this,PushMessage.class);
				if (listtype.equals("zdy")) {
					intent.putStringArrayListExtra("zdylist", lists);
				}else if(listtype.equals("zshy")){
					intent.putExtra("totalcount", totalCount);
					intent.putStringArrayListExtra("allzshylist", lists);
				}else if(listtype.equals("yaoqing")){
					intent.putExtra("totalcount", totalCount);
					intent.putStringArrayListExtra("alltjhylist", lists);
				}
				PushUsersList.this.startActivity(intent);
			}
		});
		if (UserData.getUser()!=null) {
		} else {
			Util.showIntent(PushUsersList.this, LoginFrame.class);
		}
		//		Util.initTop2(PushUsersList.this, "选中会员", "保存", new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				Util.showIntent(PushUsersList.this, PushMessage.class);
		//			}
		//		}, 
		//				new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				for (MemberInfo m : adapter.getUsersstring()) {
		//					String s = m.getName() + ",,," + m.getUserid() + ",,,"+ m.getRegTime() + ",,," + m.getPhone();
		//					lists.add(s);
		//				}
		//				Intent intent = new Intent(PushUsersList.this,PushMessage.class);
		//				if (listtype.equals("zdy")) {
		//					intent.putStringArrayListExtra("zdylist", lists);
		//				}else if(listtype.equals("zshy")){
		//					intent.putExtra("totalcount", totalCount);
		//					intent.putStringArrayListExtra("allzshylist", lists);
		//				}else if(listtype.equals("tjhy")){
		//					intent.putExtra("totalcount", totalCount);
		//					intent.putStringArrayListExtra("alltjhylist", lists);
		//				}
		//				PushUsersList.this.startActivity(intent);
		//			}
		//		}
		//				);
		//		TextView topright = (TextView) this.findViewById(R.id.topright);
		//		topright.setVisibility(View.GONE);
		listview = (ListView) this.findViewById(R.id.push_listview);
		android.view.ViewGroup.LayoutParams parames = listview.getLayoutParams();
		int _170dp=Util.dpToPx(PushUsersList.this, 40);
		parames.height=windowHeight-_170dp;
		if (adapter == null) {
			adapter = new SerchMemberAdapter(PushUsersList.this,R.layout.search_member_list);
		}
		adapter.setList(list);
		listview.setAdapter(adapter);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==event.KEYCODE_BACK){
			Util.showIntent(PushUsersList.this, PushMessage.class);
		}
		return super.onKeyDown(keyCode, event);
	}

	public class SerchMemberAdapter extends BaseAdapter {
		private Context context;
		private List<MemberInfo> list = new ArrayList<MemberInfo>();
		private LayoutInflater inflater;
		private List<MemberInfo> usersstring = new ArrayList<MemberInfo>();
		private List<View> viewList = new ArrayList<View>();
		private Map<Integer, View> map = new HashMap<Integer, View>();
		private int layoutid;
		public SerchMemberAdapter(Context context, int layoutid) {
			this.context = context;
			inflater = LayoutInflater.from(context);
			this.layoutid = layoutid;
		}

		public void setList(List<MemberInfo> list) {
			this.list.addAll(list);
			this.usersstring.addAll(list);
			this.notifyDataSetChanged();
		}

		public List<MemberInfo> getList() {
			return list;
		}

		public List<MemberInfo> getUsersstring() {
			return usersstring;
		}

		public void clearlist() {
			this.list.clear();
			this.notifyDataSetChanged();
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
			if (map.get(position)==null) {
				convertView = inflater.inflate(layoutid, null);
				holder = new ViewHolder();
				holder.t1 = (TextView) convertView.findViewById(R.id.search_t1);
				holder.t2 = (TextView) convertView.findViewById(R.id.search_t2);
				holder.level = (TextView) convertView.findViewById(R.id.level);
				holder.c1 = (CheckBox) convertView.findViewById(R.id.search_c1_);
				holder.item_shopuser_tvletter = (TextView) convertView.findViewById(R.id.item_shopuser_tvletter);
				convertView.setTag(holder);
				convertView.setFocusable(false);
				holder.c1.setFocusable(true);
				map.put(position, convertView);
			} else {
				convertView=map.get(position);
				holder = (ViewHolder) convertView.getTag();
			}
			if (!m.getName().equals("") && m.getName() != null) {
				holder.t1.setText("  " + m.getName());
			} else {
				holder.t1.setText("  " + "暂无姓名");
			}
			holder.item_shopuser_tvletter.setVisibility(View.GONE);
			holder.t1.setTextColor(Color.BLACK);
			holder.level.setText(m.getLevel());
			if(m.getPhone().contains("_P")){
				holder.t2.setText(m.getPhone().replace("_P", ""));
			}else{
				holder.t2.setText(m.getPhone());
			}
			holder.t2.setTextColor(Color.BLACK);
			holder.c1.setChecked(m.isSelected());
			if (m.isSelected()) {
				if (!usersstring.contains(m)) {
					usersstring.add(m);
				}
			}
			holder.c1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						System.out.println(isChecked+"");
						if (!usersstring.contains(m)) {
							usersstring.add(m);
							m.setSelected(true);
						}
						buttonView.invalidate();
						state.put(position, isChecked);
					} else {
						System.out.println(isChecked+"");
						if (usersstring.contains(m)) {
							m.setSelected(false);
							usersstring.remove(m);
							buttonView.invalidate();
						}
						state.remove(position);
					}
				}
			});
			return convertView;
		}

	}
	static class ViewHolder {
		TextView t1;
		TextView t2;
		TextView level;
		TextView item_shopuser_tvletter;
		CheckBox c1;
	}
}
