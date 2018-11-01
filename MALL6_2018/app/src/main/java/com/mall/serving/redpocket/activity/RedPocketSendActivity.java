package com.mall.serving.redpocket.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.net.NewWebAPI;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.redpocket.adapter.RedPocketAdapter;
import com.mall.serving.redpocket.model.SendRed_PacketsListInfo;
import com.mall.serving.redpocket.model.SendRed_Packets_InfoList;
import com.mall.serving.redpocket.util.ShareUtil;
import com.mall.util.UserData;
import com.mall.view.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@ContentView(R.layout.redpocket_send_detail_activity)
public class RedPocketSendActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	@ViewInject(R.id.tv_1)
	private TextView tv_1;
	@ViewInject(R.id.tv_2)
	private TextView tv_2;
	@ViewInject(R.id.tv_3)
	private TextView tv_3;
	

	private SendRed_PacketsListInfo info;

	private List list;

	@ViewInject(R.id.listview)
	private ListView listview;

	private RedPocketAdapter adapter;
	private String type;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();

		list = new ArrayList();
		adapter = new RedPocketAdapter(context, list, 1);
		listview.setDividerHeight(1);
		listview.setBackgroundResource(R.color.main_deep_bg);
		listview.setAdapter(adapter);
		AnimeUtil.setAnimationEmptyView(context, listview, true);
		getSendRed_Packets_InfoList();
	}

	private void setView() {
		Intent intent = getIntent();
		info = new SendRed_PacketsListInfo();
		if (intent.hasExtra("info")) {
			info = (SendRed_PacketsListInfo) intent
					.getSerializableExtra("info");
		}

		top_center.setText("红包详情");
		top_left.setVisibility(View.VISIBLE);

		String state = info.getStatus();
		int intState = Util.getInt(state);
		String textState = "正在发放中";
		switch (intState) {
		case 0:
			textState = "正在发放中";
			break;
		case 1:
			textState = "发放完毕";
			break;
		case 2:
			textState = "红包过期";
			break;

		}

		

		int xtype = Util.getInt(info.getType());
		if (xtype >= 4) {
			xtype = 0;
		}
		
		Spanned html1 = Html.fromHtml("红包状态：<font color=\"#f7b406\">"
				+ textState +"</font>");
		tv_1.setText(html1);
//		Spanned html2 = Html.fromHtml("红包总金额：<font color=\"#f7b406\">"
//				+ info.getMoney() + ShareUtil.typeStr[xtype]+ "</font>" );
//		tv_2.setText(html2);
		Spanned html3 = Html.fromHtml("已领取：<font color=\"#f7b406\">"
				+ info.getUsedmoney()+"/"+info.getMoney()+ ShareUtil.typeStr[xtype]+ "</font>" );
		tv_2.setText(html3);
		if (intState == 2) {
			tv_3.setVisibility(View.VISIBLE);
		} else {
			tv_3.setVisibility(View.GONE);
		}
		Spanned html4 = Html.fromHtml("退回金额：<font color=\"#f7b406\">"
				+ info.getLave()  + ShareUtil.typeStr[xtype]+ "</font>");
		tv_3.setText(html4);

	}

	private void getSendRed_Packets_InfoList() {

		NewWebAPI.getNewInstance().getSendRed_Packets_InfoList(1 + "",
				100 + "", info.getOrderid(), UserData.getUser().getUserId(),
				UserData.getUser().getMd5Pwd(), new WebRequestCallBack() {

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
							List<SendRed_Packets_InfoList> mlist = JsonUtil
									.getPersons(
											lists,
											new TypeToken<List<SendRed_Packets_InfoList>>() {
											});
							if (mlist != null && mlist.size() > 0) {
								list.clear();

								int m = 0;
								for (int i = 0; i < mlist.size(); i++) {
									SendRed_Packets_InfoList infoList = mlist
											.get(i);

									String money = infoList.getMoney();
									infoList.setType(info.getType());
									m += Util.getInt(money);
									if (!TextUtils.isEmpty(infoList.getUserid())) {
										list.add(infoList);
									}

								}

								adapter.notifyDataSetChanged();
								System.out.println("总数：" + mlist.size()
										+ "总金额：" + m);
							}

						}
					}

					@Override
					public void requestEnd() {

						super.requestEnd();
						AnimeUtil.setNoDataEmptyView("暂无人领取...",
								R.drawable.community_dynamic_empty, context,
								listview, true, null);
					}

				});

	}

}
