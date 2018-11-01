package com.mall.serving.query.activity.idcard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.model.IDCardInfo;
import com.mall.view.R;

import java.io.Serializable;
import java.util.Map;

@ContentView(R.layout.query_idcard_activity)
public class IDCardQueryActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;
	@ViewInject(R.id.iv_center)
	private ImageView iv_center;

	@ViewInject(R.id.tv_number)
	private TextView tv_number;
	@ViewInject(R.id.tv_gender)
	private TextView tv_gender;
	@ViewInject(R.id.tv_birthday)
	private TextView tv_birthday;
	@ViewInject(R.id.tv_city)
	private TextView tv_city;
	@ViewInject(R.id.tv_hint)
	private TextView tv_hint;
	@ViewInject(R.id.et_search)
	private TextView et_search;
	@ViewInject(R.id.iv_search)
	private TextView iv_search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();
	}

	private void setView() {
		top_center.setText("身份证查询");
		top_left.setVisibility(View.VISIBLE);

		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence str, int arg1, int arg2, int arg3) {
				if (!TextUtils.isEmpty(str)) {
					tv_hint.setVisibility(View.GONE);
				} else {
					tv_hint.setVisibility(View.VISIBLE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	@OnClick({ R.id.iv_search })
	public void click(final View v) {
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
				String text = et_search.getText().toString().trim();
				if (!TextUtils.isEmpty(text)) {
					idcardQuery(v, text);

				} else {
					Util.show("请输入身份证号码");
				}

			}
		});

	}

	private void idcardQuery(final View v, final String cardno) {
		v.setEnabled(false);
		AnimeUtil.startImageAnimation(iv_center);
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");
				String lists = map.get("list");
				if (TextUtils.isEmpty(lists)) {
					return;
				}
				final IDCardInfo info = JsonUtil.getPerson(lists, IDCardInfo.class);
				if (info != null) {

					tv_birthday.setText(info.getBirthday());
					tv_number.setText(cardno);
					tv_city.setText(info.getArea());
					tv_gender.setText(info.getSex());

				} else {
					Util.show("请输入正确的身份证号");
				}
				v.setEnabled(true);
				iv_center.setVisibility(View.GONE);
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web(
						"http://apis.juhe.cn/idcard/index?key=0c550c6ad5beef5b0a2ed27ea118b20d&cardno=" + cardno);
				return web.getPlan();
			}
		});
	}

}
