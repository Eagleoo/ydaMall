package com.mall.serving.query.activity.cookbook;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.query.model.CookBookInfo.Data;
import com.mall.serving.query.model.CookBookInfo.Data.Steps;
import com.mall.view.R;

@ContentView(R.layout.query_cookbook_detail_activity)
public class CookBookDetailActivity extends BaseActivity {

	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.tv_tag)
	private TextView tv_tag;
	@ViewInject(R.id.tv_imtro)
	private TextView tv_imtro;
	@ViewInject(R.id.ll_ingredients)
	private LinearLayout ll_ingredients;
	@ViewInject(R.id.ll_steps)
	private LinearLayout ll_steps;
	@ViewInject(R.id.iv)
	private ImageView iv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		top_left.setVisibility(View.VISIBLE);

		getIntentData();
	}

	private void getIntentData() {

		Intent intent = getIntent();
		if (intent.hasExtra("data")) {

			Data data = (Data) intent.getSerializableExtra("data");
			initView(data);
		}

	}

	private void initView(Data data) {
		top_center.setText(data.getTitle());
		List<String> albums = data.getAlbums();
		if (albums.size() > 0) {
			AnimeUtil.getImageLoad().displayImage(albums.get(0), iv,
					AnimeUtil.getImageSimpleOption());
		}

		Spanned tags = Html.fromHtml("标签：" + "<font color='#989898'>"
				+ data.getTags() + "</font>");
		Spanned imtro = Html.fromHtml("介绍：" + "<font color='#989898'>"
				+ data.getImtro() + "</font>");

		tv_tag.setText(tags);
		tv_imtro.setText(imtro);
		String burden = data.getIngredients() + ";" + data.getBurden();

		String[] split = burden.split(";");

		for (int i = 0; i < split.length; i++) {
			LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			String str = split[i];
			String[] strs = str.split(",");
			LinearLayout ll = new LinearLayout(context);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			TextView tv_1 = new TextView(context);
			TextView tv_2 = new TextView(context);
			tv_1.setTextColor(context.getResources()
					.getColor(R.color.gray_text));
			tv_2.setTextColor(context.getResources()
					.getColor(R.color.gray_text));
			tv_2.setGravity(Gravity.RIGHT);
			tv_2.setLayoutParams(params2);
			ll.addView(tv_1);
			ll.addView(tv_2);
			ll.setPadding(5, 5, 5, 5);
			params2.setMargins(0, 0, 0, 1);
			ll.setLayoutParams(params2);
			ll.setBackgroundResource(R.color.white);

			ll_ingredients.addView(ll);
			if (strs.length >= 2) {
				tv_1.setText(strs[0]);
				tv_2.setText(strs[1]);
			}

		}

		List<Steps> sList = data.getSteps();
		if (sList == null) {
			return;
		}
		for (int i = 0; i < sList.size(); i++) {
			Steps steps = sList.get(i);
			LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			LinearLayout ll = new LinearLayout(context);
			ll.setOrientation(LinearLayout.VERTICAL);
			TextView tv_1 = new TextView(context);
			ImageView iv_1 = new ImageView(context);
			tv_1.setTextColor(context.getResources()
					.getColor(R.color.gray_text));
			iv_1.setLayoutParams(params2);
			ll.addView(tv_1);
			ll.addView(iv_1);

			tv_1.setText(steps.getStep());
			AnimeUtil.getImageLoad().displayImage(steps.getImg(), iv_1,
					AnimeUtil.getImageSimpleOption());
			ll.setLayoutParams(params2);
			ll_steps.addView(ll);

		}

	}

}
