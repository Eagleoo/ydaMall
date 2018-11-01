package com.mall.yyrg.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.mall.net.Web;
import com.mall.view.R;
import com.mall.yyrg.model.HotShopRecord;

public class AllRegouRecordleAdapter extends BaseAdapter {

	private List<HotShopRecord> list = new ArrayList<HotShopRecord>();
	private Context context;
	private LayoutInflater flater;
	private int width;
	private BitmapUtils bmUtil;

	public AllRegouRecordleAdapter(Context context, int width) {
		super();
		this.width = width;
		this.context = context;
		flater = LayoutInflater.from(context);
		bmUtil = new BitmapUtils(context);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
	}

	public void setList(List<HotShopRecord> list) {
		this.list.addAll(list);
		this.notifyDataSetChanged();
	}

	public void clear() {
		this.list.clear();
	}

	public void updateUI() {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = flater.inflate(R.layout.yyrg_all_regou_item, null);
		}
		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		TextView user_id = (TextView) convertView.findViewById(R.id.user_id);
		TextView user_address = (TextView) convertView
				.findViewById(R.id.user_address);
		TextView regou_count = (TextView) convertView
				.findViewById(R.id.regou_count);
		TextView regou_time = (TextView) convertView
				.findViewById(R.id.regou_time);

		String face = list.get(position).getLogo();
		if (TextUtils.isEmpty(face) || face.equals("offlogo.jpg")) {
			face = "http://" + Web.webImage + "/" + "offlogo.jpg";
		}
		if (!face.contains("http")) {
			face = "http://" + Web.webImage + face;
		}

		bmUtil.display(image, face);

		user_address.setText(list.get(position).getBuyIPAddr());
		user_id.setText(list.get(position).getUserId().replace("_p", ""));
		regou_count.setText(list.get(position).getTotalTimes());
		regou_time.setText(list.get(position).getBuyTime());
		return convertView;
	}

}
