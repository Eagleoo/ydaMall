package com.mall.serving.query.activity.oilprice;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.model.OilPriceInfo;
import com.mall.serving.query.model.OilPriceInfo.Data;
import com.mall.serving.query.model.OilPriceInfo.Data.Gastprice;
import com.mall.serving.query.model.OilPriceInfo.Data.Price;
import com.mall.view.R;

@ContentView(R.layout.query_oil_price_detail_activity)
public class OilPriceDetailActivity extends BaseActivity {

	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.tv_name)
	private TextView tv_name;

	@ViewInject(R.id.tv_1)
	private TextView tv_1;
	@ViewInject(R.id.tv_2)
	private TextView tv_2;
	@ViewInject(R.id.tv_3)
	private TextView tv_3;
	@ViewInject(R.id.tv_4)
	private TextView tv_4;
	@ViewInject(R.id.tv_5)
	private TextView tv_5;
	@ViewInject(R.id.tv_6)
	private TextView tv_6;
	@ViewInject(R.id.ll_oil)
	private LinearLayout ll_oil;
	@ViewInject(R.id.ll_price)
	private LinearLayout ll_price;

	private Data data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();
		getIntentData();
	}

	private void setView() {
		top_center.setText("加油站详情");
		top_left.setVisibility(View.VISIBLE);

	}

	private void getIntentData() {
		Intent intent = getIntent();
		data = new OilPriceInfo().new Data();
		if (intent.hasExtra("data")) {
			data = (Data) intent.getSerializableExtra("data");
		}
		tv_name.setText(data.getName());
		tv_1.setText(data.getDistance() + "米");
		tv_2.setText(data.getBrandname());
		tv_3.setText(data.getType());
		tv_4.setText(data.getDiscount());
		tv_5.setText(data.getAddress());
		tv_6.setText(data.getFwlsmc());

		List<Price> plist = data.getPrice();
		List<Gastprice> gList = data.getGastprice();

		if (gList != null && plist != null) {
			for (Gastprice g : gList) {
				Price p = new OilPriceInfo().new Data().new Price();
				p.setPrice(g.getPrice());
				p.setType(g.getName());
				plist.add(p);
			}
		}

		int width = Util.getScreenWidth();
		width = width / plist.size();
		for (int i = 0; i < plist.size(); i++) {
			Price price = plist.get(i);
			TextView tv_oil = new TextView(context);
			TextView tv_price = new TextView(context);

			tv_oil.setText(price.getType());
			tv_price.setText(price.getPrice());
			tv_oil.setTextColor(context.getResources().getColor(R.color.black));
			tv_price.setTextColor(context.getResources().getColor(
					R.color.yellow));

			tv_oil.setTextSize(13);
			tv_price.setTextSize(13);
			tv_oil.setGravity(Gravity.CENTER);
			tv_price.setGravity(Gravity.CENTER);

			LayoutParams params = new LayoutParams(width,
					LayoutParams.WRAP_CONTENT);
			tv_oil.setLayoutParams(params);
			tv_price.setLayoutParams(params);

			ll_oil.addView(tv_oil);
			ll_price.addView(tv_price);

		}

	}

}
