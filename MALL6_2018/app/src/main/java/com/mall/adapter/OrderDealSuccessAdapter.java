package com.mall.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.model.OrderOne;
import com.mall.model.OrderTwo;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class OrderDealSuccessAdapter extends BaseAdapter {

	private List<OrderOne> list = new ArrayList<OrderOne>();
	private LayoutInflater inflater;
	private Context context;
	private OrderDealSuccessTwoOrderAdapter adapter;
	private int width;

	public OrderDealSuccessAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;

	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public void clear() {
		list.clear();
		;
		notifyDataSetChanged();
	}

	public void setList(List<OrderOne> list) {
		this.list.addAll(list);
		this.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.item_order_deal_success,
					null);
			holder.oneOrderCode = (TextView) convertView
					.findViewById(R.id.item_order_deal_suc_oneorder_code);
			holder.oneOrderAllMoney = (TextView) convertView
					.findViewById(R.id.item_order_deal_suc_oneorder_allmoney);
			holder.oneOrderTime = (TextView) convertView
					.findViewById(R.id.item_order_deal_suc_oneorder_time);
			// holder.order_state = (TextView) convertView
			// .findViewById(R.id.order_state);
			holder.line = convertView.findViewById(R.id.xian_);
			holder.oneOrderView = (LinearLayout) convertView
					.findViewById(R.id.item_order_deal_suc_ll_oneorder_layout);
			holder.oneOrderListView = (ListView) convertView
					.findViewById(R.id.item_order_deal_listview);
			holder.item_order_deal_suc_oneorder_delete = (ImageView) convertView
					.findViewById(R.id.item_order_deal_suc_oneorder_delete);
			holder.line.setVisibility(View.GONE);
			holder.oneOrderListView.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final Holder holderView = holder;
		Log.i("tag", "---前---" + list.get(position).cost);
		holderView.oneOrderCode.setText("订单号:" + list.get(position).orderId);
		holderView.oneOrderTime.setText(list.get(position).date.substring(0,
				list.get(position).date.lastIndexOf(":")));
		holder.item_order_deal_suc_oneorder_delete
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						delSuccess(position);
					}
				});
		holderView.oneOrderView
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						delSuccess(position);
						return false;
					}

				});

		holderView.line.setVisibility(View.VISIBLE);
		holderView.oneOrderListView.setVisibility(View.VISIBLE);
		String oderType=list.get(position).ordertype;
		adapter = new OrderDealSuccessTwoOrderAdapter(context,oderType);

		holderView.oneOrderListView.setAdapter(adapter);

		List<OrderTwo> twoList = new ArrayList<OrderTwo>();
		twoList = JSON.parseArray(list.get(position).secondOrder,
				OrderTwo.class);
		Log.e("数据列表长度",twoList.size()+"");
		if (null != twoList && twoList.size() > 0) {
			adapter.setWidth(width);
			holderView.oneOrderAllMoney
					.setText("共" + twoList.size() + "件商品   共计 ￥"
							+ list.get(position).cost.replace(".00", ""));

			adapter.setList(twoList);
			adapter.UpData();
		}
		setListViewHeightBasedOnChildren(holderView.oneOrderListView);

		return convertView;
	}
	
	
	public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
               ListAdapter listAdapter = listView.getAdapter();
              if (listAdapter == null) {        return;    }
              int totalHeight = 0;
              for (int i = 0, len = listAdapter.getCount(); i < len; i++) {                          
                    // listAdapter.getCount()返回数据项的数目
                   View listItem = listAdapter.getView(i, null, listView);        
                   // 计算子项View 的宽高
                   listItem.measure(0, 0); 
                  // 统计所有子项的总高度
                   totalHeight += listItem.getMeasuredHeight();
          } 
         ViewGroup.LayoutParams params = listView.getLayoutParams();
         params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
         listView.setLayoutParams(params);
	}

	class Holder {

		String tag = "1";
		private View oneOrderView;
		private TextView oneOrderCode;
		private ImageView oneOrderDelete;
		private ImageView item_order_deal_suc_oneorder_delete;
		private TextView oneOrderTime;
		private TextView oneOrderAllMoney;
		private ListView oneOrderListView;
		private View line;
	}

	private void delSuccess(final int position) {
		VoipDialog voipDialog;
		voipDialog = new VoipDialog("你当前正在删除订单,您确定要这么做吗？", context, "确定", "取消",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// voipDialog.cancel();
						// voipDialog.dismiss();

						Map<String, String> map = new HashMap<String, String>();
						map.put("orderId", list.get(position).orderId);
						map.put("userId", UserData.getUser().getUserId());
						map.put("md5Pwd", UserData.getUser().getMd5Pwd());
						NewWebAPI.getNewInstance().getWebRequest(
								"/YdaOrder.aspx?call=deletMallOrder", map,
								new WebRequestCallBack() {

									@Override
									public void success(Object result) {
										if (Util.isNull(result)) {
											Util.show("网络异常，请重试！", context);
											return;
										}
										JSONObject json = JSONObject
												.parseObject(result.toString());
										if (200 != json.getIntValue("code")) {
											Util.show("网络异常，请重试！", context);
											return;
										}
										String msg = json.getString("message");
										if (msg.contains("成功"))
											Util.show("订单删除成功", context);
										list.remove(position);
										OrderDealSuccessAdapter.this
												.notifyDataSetChanged();

									}

								});

					}
				}, new OnClickListener() {

					@Override
					public void onClick(View v) {
					}

				});
		voipDialog.show();

	}

}
