package com.mall.serving.redpocket.adapter;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.redpocket.activity.RedPocketSendActivity;
import com.mall.serving.redpocket.model.FromRed_Packets_InfoList;
import com.mall.serving.redpocket.model.SendRed_PacketsListInfo;
import com.mall.serving.redpocket.model.SendRed_Packets_InfoList;
import com.mall.serving.redpocket.util.ShareUtil;
import com.mall.view.R;

public class RedPocketAdapter extends NewBaseAdapter {

	private int type;

	

	public RedPocketAdapter(Context c, List list, int type) {
		super(c, list);
		this.type = type;
	}

	@Override
	public View getView(int position, View v, ViewGroup arg2) {

		if (v == null) {

			ViewCache cache = new ViewCache();
			v = LayoutInflater.from(context).inflate(
					R.layout.redpocket_sent_item, null);

			cache.iv_head = (ImageView) v.findViewById(R.id.iv_head);
			cache.tv_name = (TextView) v.findViewById(R.id.tv_name);
			cache.tv_money = (TextView) v.findViewById(R.id.tv_money);
			cache.tv_time = (TextView) v.findViewById(R.id.tv_time);
			cache.tv_sum = (TextView) v.findViewById(R.id.tv_sum);

			v.setTag(cache);

		}

		ViewCache cache = (ViewCache) v.getTag();
		switch (type) {
		case 0:
			final SendRed_PacketsListInfo listinfo = (SendRed_PacketsListInfo) list
					.get(position);
			cache.tv_name.setText(Util.getNo_pUserId(listinfo.getSenduser()));
			String money = listinfo.getMoney();

			String mtypeS = listinfo.getType();
			int mtype = Util.getInt(mtypeS);
			if (mtype >= 4) {
				mtype = 0;
			}
			Spanned html = Html.fromHtml("<font color=\"#f7b406\">" + money+ ShareUtil.typeStr[mtype]
					+ "</font>" );
			cache.tv_money.setText(html);
			cache.tv_time.setText(listinfo.getSendtime());
			cache.tv_sum
					.setText(listinfo.getUsed() + "/" + listinfo.getTimes());

			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					

					Util.showIntent(context, RedPocketSendActivity.class,
							new String[] { "info" },
							new Serializable[] { listinfo });

				}
			});

			v.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					String state = listinfo.getStatus();
					int intState = Util.getInt(state);
					if (intState==0) {
						ShareUtil.showShareDialog(context, listinfo.getRemark(),
								listinfo.getOrderid());
					}
					

					return false;
				}
			});

			break;
		case 1:
			cache.iv_head.setVisibility(View.VISIBLE);
			SendRed_Packets_InfoList infoList = (SendRed_Packets_InfoList) list
					.get(position);
			String userid = infoList.getUserid();

			cache.tv_name.setText(Util.getNo_pUserId(userid));

			String mtypeX = infoList.getType();
			int xtype = Util.getInt(mtypeX);
			if (xtype >= 4) {
				xtype = 0;
			}
			Spanned html1 = Html.fromHtml("抢到<font color=\"#f7b406\">"
					+ infoList.getMoney()+ ShareUtil.typeStr[xtype] + "</font>" );

			cache.tv_money.setText(html1);
			cache.tv_time.setText(infoList.getRemark());
			cache.tv_sum.setText(infoList.getDate());
			AnimeUtil.getImageLoad().displayImage(infoList.getUserimg(),
					cache.iv_head, AnimeUtil.getImageRectOption());

			break;

		case 2:

			FromRed_Packets_InfoList fInfo = (FromRed_Packets_InfoList) list
					.get(position);
			cache.tv_name.setText(Util.getNo_pUserId(fInfo.getSenduser())
					+ "发放的红包");
			String mtypeSS = fInfo.getAtype();
			int mmtype = Util.getInt(mtypeSS);
			if (mmtype >= 4) {
				mmtype = 0;
			}
			Spanned html2 = Html.fromHtml("抢到<font color=\"#f7b406\">"
					+ fInfo.getMoney() + ShareUtil.typeStr[mmtype]+ "</font>" );
			cache.tv_money.setText(html2);
			cache.tv_time.setText(fInfo.getDate());
			cache.tv_sum.setVisibility(View.GONE);
			break;

		}

		return v;
	}

	class ViewCache {
		private ImageView iv_head;
		private TextView tv_name;
		private TextView tv_money;
		private TextView tv_time;
		private TextView tv_sum;
	}

}
