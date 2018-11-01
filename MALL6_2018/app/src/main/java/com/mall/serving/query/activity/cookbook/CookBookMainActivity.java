package com.mall.serving.query.activity.cookbook;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.textview.TextDrawable;
import com.mall.serving.query.adapter.CookBookGridAdapter;
import com.mall.serving.query.model.ConstellationInfo;
import com.mall.serving.query.model.CookBookIndexInfo;
import com.mall.serving.query.model.CookBookInfo;
import com.mall.serving.query.model.CookBookInfo.Data;
import com.mall.serving.query.net.JuheWeb;
import com.mall.serving.query.net.JuheWeb.JuheRequestCallBack;
import com.mall.view.R;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

@ContentView(R.layout.query_cookbook_activity)
public class CookBookMainActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;

	@ViewInject(R.id.tv_hint)
	private TextView tv_hint;
	@ViewInject(R.id.et_search)
	private EditText et_search;

	@ViewInject(R.id.iv_search)
	private ImageView iv_search;
	@ViewInject(R.id.ll_hsv)
	private LinearLayout ll_hsv;
	@ViewInject(R.id.gridview)
	private GridView gridview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();
		setListener();
		getCookDetail();
		getIndex();
	}

	private void setView() {
		top_left.setVisibility(View.VISIBLE);
		top_center.setText("菜谱大全");

	}

	@OnClick({ R.id.iv_search })
	public void click(View v) {

		AnimeUtil.startAnimation(context, v, R.anim.small_2_big, new OnAnimEnd() {

			@Override
			public void start() {
				// TODO Auto-generated method stub

			}

			@Override
			public void repeat() {
				// TODO Auto-generated method stub

			}

			@Override
			public void end() {
				String trim = et_search.getText().toString().trim();
				if (TextUtils.isEmpty(trim)) {
					Util.show(" 请输入菜谱、食材");
					return;
				}

				Util.showIntent(context, CookBookSearchActivity.class, new String[] { "cook" },
						new Serializable[] { trim });

			}
		});

	}

	private void setListener() {
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {

				String str = paramCharSequence.toString();
				if (TextUtils.isEmpty(str)) {
					tv_hint.setVisibility(View.VISIBLE);
				} else {
					tv_hint.setVisibility(View.GONE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable paramEditable) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void getCookDetail() {
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");
				if (map.get("code").equals("200")) {
					String lists = map.get("list");

					CookBookInfo info = JsonUtil.getPerson(lists, CookBookInfo.class);
					List<Data> mlist = info.getData();

					CookBookGridAdapter adapter = new CookBookGridAdapter(context, mlist);
					gridview.setAdapter(adapter);
				}

			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web(
						"http://apis.juhe.cn/cook/index?key=96877d4d3eeb6b8891dc32d6a9ea7a6e&cid=3&pn=0&rn=4");
				return web.getPlan();
			}
		});

	}

	private void getIndex() {
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");
				if (map.get("code").equals("200")) {
					String lists = map.get("list");

					List<CookBookIndexInfo> mlist = JsonUtil.getPersons(lists,
							new TypeToken<List<CookBookIndexInfo>>() {
							});

					for (int i = 0; i < mlist.size(); i++) {

						final CookBookIndexInfo info = mlist.get(i);
						LinearLayout ll = new LinearLayout(context);
						TextView tv_1 = new TextView(context);
						TextView tv = new TextView(context);
						ll.setOrientation(LinearLayout.HORIZONTAL);
						ll.setGravity(Gravity.CENTER);

						int px = Util.dpToPx(10);
						ll.setPadding(0, px, 0, px);
						int width = Util.getScreenWidth();
						ll.setLayoutParams(new LayoutParams(width / 4, LayoutParams.WRAP_CONTENT));
						Random random = new Random();

						int co = Color.rgb(random.nextInt(200) + 20, random.nextInt(200) + 20,
								random.nextInt(200) + 20);
						TextDrawable td = TextDrawable.builder().buildRound("", co);
						LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 30);
						layoutParams.setMargins(0, 0, 10, 0);
						tv_1.setLayoutParams(layoutParams);
						tv_1.setBackgroundDrawable(td);

						tv.setText(info.getName());
						tv.setTextSize(12);

						ll.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								AnimeUtil.startAnimation(context, v, R.anim.small_2_big, new OnAnimEnd() {

									@Override
									public void start() {
										// TODO Auto-generated
										// method stub

									}

									@Override
									public void repeat() {
										// TODO Auto-generated
										// method stub

									}

									@Override
									public void end() {
										Util.showIntent(context, CookBookSearchActivity.class,
												new String[] { "indexData" }, new Serializable[] { info });

									}
								});

							}
						});
						ll.addView(tv_1);
						ll.addView(tv);
						ll_hsv.addView(ll);
					}

				}

			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://apis.juhe.cn/cook/category?key=96877d4d3eeb6b8891dc32d6a9ea7a6e");
				return web.getPlan();
			}
		});
	}

}
