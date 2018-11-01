package com.mall.card;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardUtil;
import com.mall.util.Util;
import com.mall.view.R;

/**
 * 交换名片
 * 
 * @author admin
 * 
 */
public class CardExchangeCard extends Activity {
	@ViewInject(R.id.private_groups)
	private LinearLayout private_groups;
	private int width;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_exchange_card);
		ViewUtils.inject(this);
		private_groups.setVisibility(View.GONE);
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width=dm.widthPixels;
	}

	@OnClick({ R.id.recommendation, R.id.nearby, R.id.richscan,
			R.id.private_groups ,R.id.tv_add})
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.recommendation:// 推荐
			Util.showIntent(this, CardRecommendationPeople.class);
			break;

		case R.id.nearby:// 附近的人
			Util.showIntent(this, CardPeopleNearby.class);
			break;
		case R.id.private_groups:// 私密群组

			break;
		case R.id.tv_add:
			
			Util.showIntent(this, CardAddNewCard.class);
			
			
			break;
		case R.id.richscan:// 扫一扫
			if (CardUtil.myCardLinkman == null) {
				CardUtil.xuznzeAdd(this,width,"1");
//				Toast.makeText(this, "您还没有拍自己的名片，请前去扫描", Toast.LENGTH_SHORT)
//						.show();
			} else {
				if (CardUtil.myCardLinkman.getIsme().equals("1")) {
					Util.showIntent(this, CardQRCode.class);
				} else {
					CardUtil.xuznzeAdd(this,width,"1");
//					Toast.makeText(this, "您好没有拍自己的名片，请前去扫描", Toast.LENGTH_SHORT)
//							.show();
				}
			}

			break;
		}
		
		
	}
	
}
