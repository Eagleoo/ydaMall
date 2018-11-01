package com.mall.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.util.Util;

/**
 * 
 * 功能： 购物卡<br>
 * 时间： 2013-7-13<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class ShopCardFrame extends Activity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;

	private TextView td1, td2, td3, td4, td5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.shop_card_frame1);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		findview();
		setView();

	}

	private void findview() {
		// TODO Auto-generated method stub
		td1 = (TextView) this.findViewById(R.id.td1);
		td2 = (TextView) this.findViewById(R.id.td2);
		td3 = (TextView) this.findViewById(R.id.td3);
		td4 = (TextView) this.findViewById(R.id.td4);
		td5 = (TextView) this.findViewById(R.id.td5);
	}

	private void setView() {

		top_center.setText("〔远大购物卡〕简介");
		top_left.setVisibility(View.VISIBLE);

		String title1 = "<font color=\"#ff0000\">　　<b>特点一:</b></font>";
		String title2 = "<font color=\"#ff0000\">　　<b>特点二:</b></font>";
		String title3 = "<font color=\"#ff0000\">　　<b>特点三:</b></font>";
		String title4 = "<font color=\"#ff0000\">　　<b>特点四:</b></font>";
		String title5 = "<font color=\"#ff0000\">　　<b>特点五:</b></font>";

		String content1 = "<font color=\"000000\">购物卡最低1000元起，最高10万元。</font>";
		String content2 = "<font color=\"000000\">购物卡本金可购买〔远大云商〕平台具有“购物卡标识”的所有商品。</font>";
		String content3 = "<font color=\"000000\">每月赠送1.5%的电子消费券（连续五年），所赠电子消费券可购买〔远大云商〕平台所有商品和服务（亦可兑换现金）。</font>";
		String content4 = "<font color=\"000000\">每月赠送0.5%的消费券（连续五年），所赠消费券可换购〔远大云商〕平台所有商品和服务。</font>";
		String content5 = "<font color=\"000000\">首次购卡金额超过10万元时，即赠送个人创业大系统（城市经理）。</font>";

		td1.setText(Html.fromHtml(title1 + content1));
		td2.setText(Html.fromHtml(title2 + content2));
		td3.setText(Html.fromHtml(title3 + content3));
		td4.setText(Html.fromHtml(title4 + content4));
		td5.setText(Html.fromHtml(title5 + content5));
	}

	@OnClick(R.id.dhweiuf)
	public void onclick(View view) {
		Util.showIntent(this, RequestShopCardFrame.class);
	}

}
