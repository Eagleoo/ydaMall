package com.mall.serving.redpocket.activity;

import java.io.Serializable;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.android.common.logging.Log;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.net.NewWebAPI;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.dialog.CustomDialog;
import com.mall.serving.redpocket.fragment.RedPocketSentFragment;
import com.mall.serving.redpocket.util.CustomEditDialog;
import com.mall.serving.redpocket.util.ShareUtil;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.view.AccountRecharge;
import com.mall.view.R;
import com.mall.view.SBChongzhiFrame;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@ContentView(R.layout.redpocket_detail_activity)
public class RedPocketDetailActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	@ViewInject(R.id.tv_red_1)
	private View tv_red_1;
	@ViewInject(R.id.tv_red_2)
	private View tv_red_2;
	@ViewInject(R.id.tv_red_3)
	private View tv_red_3;

	@ViewInject(R.id.et_num)
	private EditText et_num;
	@ViewInject(R.id.et_money)
	private EditText et_money;
	@ViewInject(R.id.et_text)
	private EditText et_text;

	@ViewInject(R.id.tv_send_money)
	private TextView tv_send_money;
	@ViewInject(R.id.tv_explain)
	private TextView tv_explain;
	@ViewInject(R.id.tv_unit)
	private TextView tv_unit;

	private int red;
	private int type = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();
		startAnim();
		tv_red_3.setScaleX(0.8f);
		tv_red_3.setScaleY(0.8f);
	}

	private void setView() {
		Intent intent = getIntent();
		if (intent.hasExtra("red")) {
			red = intent.getIntExtra("red", 0);
		}
		String title = "";
		String hint = "";
		if (red == 0) {
			title = "拼手气红包";
			hint = "群里每个人抢到的金额随机，最大不超过 ";
		} else {
			title = "普通红包";
			hint = "群里每个人抢到的金额相等，最大不超过 ";
		}
		top_center.setText(title);
		tv_explain.setText(hint);
		top_left.setVisibility(View.VISIBLE);

		et_money.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
				String str = text.toString().trim();

				String unit = tv_unit.getText().toString();
				String money = str + unit;
				tv_send_money.setText(money);

				if (TextUtils.isEmpty(str)) {
					money = "";
				}
				String hint = "";

				if (red == 0) {

					hint = "群里每个人抢到的金额随机，最大不超过 " + money;
				} else {

					hint = "群里每个人抢到的金额相等，最大不超过" + money;
				}
				tv_explain.setText(hint);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

	}

	@OnClick({ R.id.tv_sumbit })
	public void click(final View v) {

		final String money = et_money.getText().toString().trim();
		final String num = et_num.getText().toString().trim();
		final String text = et_text.getText().toString().trim();

		if (TextUtils.isEmpty(num)) {
			Util.show("红包数量不能为空");
			return;
		}
		if (Util.getInt(num) < 3 || Util.getInt(num) > 300) {
			Util.show("红包数量不能小于3个大于300个");
			return;
		}
		if (TextUtils.isEmpty(money)) {
			Util.show("红包金额不能为空");
			return;
		}
		if (Util.getInt(money) < 3) {
			Util.show("红包金额不能小于3");
			return;
		}

		final CustomEditDialog dialog = new CustomEditDialog(context);
		dialog.show();

		String str = et_money.getText().toString().trim();

		String unit = tv_unit.getText().toString().trim();

		Spanned html = Html.fromHtml("本次发红包将支付金额：<font color=\"#f7b406\">" + str + unit + "</font>");

		dialog.getTv_dialog_content().setText(html);
		// dialog.getEt_dialog_content().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		dialog.setLeftClick(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				String trim = dialog.getEt_dialog_content().getText().toString().trim();
				if (TextUtils.isEmpty(trim)) {
					Util.show("请输入交易密码");
					return;
				}
				String str = text;
				if (TextUtils.isEmpty(str)) {
					str = "有钱就是任性！";
				}
				String pwd = new MD5().getMD5ofStr(trim);
				sendRed_Packets(v, num, money, str, pwd);
				dialog.dismiss();
			}
		});

	}

	private void startAnim() {
		RotateAnimation animation_1 = AnimeUtil.getRotateAnimation(-25);
		RotateAnimation animation_2 = AnimeUtil.getRotateAnimation(25);
		tv_red_1.startAnimation(animation_1);
		tv_red_2.startAnimation(animation_2);
		tv_red_3.startAnimation(animation_2);

	}

	@OnRadioGroupCheckedChange({ R.id.rg })
	public void onCheckedChanged(RadioGroup arg0, int id) {

		switch (id) {
		case R.id.rb_rg_1:
			type = 0;

			break;
		case R.id.rb_rg_2:
			type = 1;

			break;
		case R.id.rb_rg_3:
			type = 2;

			break;
		case R.id.rb_rg_4:
			type = 3;

			break;

		}
		String unit = ShareUtil.typeStr[type];
		tv_unit.setText(unit);
		String str = et_money.getText().toString();
		tv_send_money.setText(str + unit);
	}

	private void sendRed_Packets(final View v, final String times, final String money, final String remark,
			String pwd) {
		v.setEnabled(false);
		Log.e("come1------",  "in");
		final String unit = tv_unit.getText().toString();
		Util.showProgress("支付中，请稍后..", context);
		NewWebAPI.getNewInstance().sendRed_Packets(times, red + "", type + "", money, remark, pwd,
				UserData.getUser().getUserId(), UserData.getUser().getMd5Pwd(), new WebRequestCallBack() {
					@Override
					public void success(Object result) {
						super.success(result);
						Map<String, String> map = JsonUtil.getNewApiJson(result.toString());
						if (null != map && map.size() > 0) {
							String message = map.get("message");
							if (map.get("code").equals("200") && !TextUtils.isEmpty(message)) {

								String redpocket = money + unit + "已装入" + times + "个红包。";
								Util.showIntent(context, RedPocketShareActivity.class,
										new String[] { "red", "message", "text", "redpocket", "type" },
										new Serializable[] { red, message, remark, redpocket, type });

								sendBroadcast(new Intent(RedPocketSentFragment.RedPocketSentAction));
								v.setEnabled(true);
							} else if (!TextUtils.isEmpty(message) && message.contains("余额不足")) {
								v.setEnabled(true);

								if (type == 3 || type == 1) {
									if (!TextUtils.isEmpty(message)) {
										Util.show(message);
									}
									return;
								}
								new CustomDialog("余额不足", "你的账户余额不足，是否前去充值？", context, "取消", "确定", null,
										new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										Intent intent = new Intent();
										Class newClass = null;

										switch (type) {
										case 0:

											newClass = AccountRecharge.class;
											break;
										case 1:

											newClass = SBChongzhiFrame.class;
											break;
										case 3:
											break;
										}

										if (null != newClass) {
											intent.setClass(context, newClass);
											context.startActivity(intent);
										}
									}
								}).show();
							} else {
								v.setEnabled(true);
								if (!TextUtils.isEmpty(message)) {
									Util.show(message);
								}

							}
						} else {
							Util.show("网络异常，请重试！");
						}

					}

					@Override
					public void requestEnd() {
						// TODO Auto-generated method stub
						super.requestEnd();
						Util.closeProgress();
					}

					@Override
					public void fail(Throwable e) {
						// TODO Auto-generated method stub
						super.fail(e);
						v.setEnabled(true);
					}

				});

	}

}
