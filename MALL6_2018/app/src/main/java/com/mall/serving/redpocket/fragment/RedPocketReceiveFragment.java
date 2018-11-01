package com.mall.serving.redpocket.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.fragment.BaseReceiverFragment;
import com.mall.serving.community.net.NewWebAPI;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.GetLastDateTime;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.redpocket.adapter.RedPocketAdapter;
import com.mall.serving.redpocket.model.FromRed_Packets_InfoList;
import com.mall.util.UserData;
import com.mall.view.R;

public class RedPocketReceiveFragment extends BaseReceiverFragment {

	private List list;
	private List listAll;
	private List listAll2;

	@ViewInject(R.id.listview)
	private ListView listview;
	@ViewInject(R.id.rb_rg_1)
	private RadioButton rb_rg_1;

	private RedPocketAdapter adapter;

	private int type = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		list = new ArrayList();
		listAll = new ArrayList();
		listAll2 = new ArrayList();
		adapter = new RedPocketAdapter(context, list, 2);

		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.redpocket_sent_fragment, container,
				false);
		ViewUtils.inject(this, v);

		listview.setAdapter(adapter);
		AnimeUtil.setAnimationEmptyView(context, listview, true);
		AnimeUtil.setNoDataEmptyView("暂未收到过红包...",
				R.drawable.community_dynamic_empty, context,
				listview, true, null);
		getFromRed_Packets_InfoList();
		return v;
	}

	@OnRadioGroupCheckedChange({ R.id.rg })
	public void onCheckedChanged(RadioGroup arg0, int id) {

		switch (id) {
		case R.id.rb_rg_1:
			type = 0;
			break;
		case R.id.rb_rg_2:
			type = 1;
			break;
		case R.id.rb_rg_3:
			type = 2;
			break;
		case R.id.rb_rg_4:
			type = 3;
			break;

		}
		setChangeAdapter(type);

	}

	private void setChangeAdapter(int type) {
		list.clear();
		for (int i = 0; i < listAll.size(); i++) {
			FromRed_Packets_InfoList info = (FromRed_Packets_InfoList) listAll
					.get(i);

			if (info.getAtype().equals(type + "")) {
				list.add(info);
			}
		}
		listAll2.clear();
		listAll2.addAll(list);
		
		adapter.notifyDataSetChanged();
	
	}

	public void getSelectList(int num) {
		String time = " ";
		switch (num) {
		case 0:

			break;
		case 1:
			time = GetLastDateTime.getInstance().lastWeek();
			break;
		case 2:
			time = GetLastDateTime.getInstance().lastMonth();
			break;
		case 3:
			time = GetLastDateTime.getInstance().lastYear();
			break;

		}
		list.clear();
		System.out.println(time);
		for (int i = 0; i < listAll2.size(); i++) {
			FromRed_Packets_InfoList info = (FromRed_Packets_InfoList) listAll2
					.get(i);

			String sendtime = info.getDate();
             System.out.println(sendtime);
			if (sendtime.compareTo(time) > 0) {
				list.add(info);
			}

		}
		adapter.notifyDataSetChanged();

	}
	
	private void getFromRed_Packets_InfoList() {

		NewWebAPI.getNewInstance().getFromRed_Packets_InfoList(1 + "", 100 + "",
				UserData.getUser().getUserId(), UserData.getUser().getMd5Pwd(),
				new WebRequestCallBack() {

					@Override
					public void success(Object result) {

						super.success(result);

						Map<String, String> map = JsonUtil.getNewApiJson(result
								.toString());

						String message = map.get("message");
						if (map.get("code").equals("200")) {
							String lists = map.get("list");
							if (TextUtils.isEmpty(lists)) {
								return;
							}
							List<FromRed_Packets_InfoList> mlist = JsonUtil
									.getPersons(
											lists,
											new TypeToken<List<FromRed_Packets_InfoList>>() {
											});
							if (mlist != null && mlist.size() > 0) {
								listAll.clear();
								listAll.addAll(mlist);

								rb_rg_1.setChecked(true);
								setChangeAdapter(0);
							}

						}

					}

					@Override
					public void requestEnd() {
						// TODO Auto-generated method stub
						super.requestEnd();
						AnimeUtil.setNoDataEmptyView("暂未发红包...",
								R.drawable.community_dynamic_empty, context,
								listview, true, null);
					}

				});

	}

}
