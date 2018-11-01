package com.mall.yyrg;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;

import net.sourceforge.mm.MMPay;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.adapter.YYRGUtil;

public class YYRGPayMoneyFrame extends Activity {
	// 合计需支付的金额
	@ViewInject(R.id.shop_pay_pay_money_product_des)
	private TextView heji;
	// 现金账户支付的checkbox
	@ViewInject(R.id.pay_item_xj_radio)
	private ImageView pay_item_xj_radio;
	// 充值账户支付的checkbox
	@ViewInject(R.id.pay_item_rec_radio)
	private ImageView pay_item_rec_radio;
	// 充值账户的余额
	@ViewInject(R.id.pay_item_rec_ban)
	private TextView pay_item_rec_ban;
	// 商币账户支付的checkbox
	@ViewInject(R.id.pay_item_sb_radio)
	private ImageView pay_item_sb_radio;
	// 商币账户的余额
	@ViewInject(R.id.pay_item_sb_ban)
	private TextView pay_item_sb_ban;
	// 银联支付的checkbox
	@ViewInject(R.id.pay_item_uppay_radio)
	private ImageView pay_item_uppay_radio;
	// 微信支付的checkbox
	@ViewInject(R.id.pay_item_weixin_radio)
	private ImageView pay_item_weixin_radio;
	// 支付密码
	@ViewInject(R.id.shop_pay_pay_twoPwd)
	private EditText shop_pay_pay_twoPwd;
	@ViewInject(R.id.pay_money_frame_two_line)
	private LinearLayout pay_money_frame_two_line;
	// 支付的类型1,现金账户支付，2充值账户支付3，商币账户支付，4，银联支付
	private int state = 1;
	private String yscids;
	private double zhifu = 0.00;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_pay_money_frame);
		ViewUtils.inject(this);
		getAmount();
		yscids = getIntent().getStringExtra("yscids");
		for (int i = 0; i < YYRGUtil.hotShopCars.size(); i++) {
			zhifu = zhifu
					+ 1
					* Double.parseDouble(YYRGUtil.hotShopCars.get(i)
							.getPersonTimes());
		}
		DecimalFormat df = new DecimalFormat("#.00");

		heji.setText("需支付：" + df.format(zhifu));
	}

	@OnClick({ R.id.top_back, R.id.pay_item_xj_line, R.id.pay_item_rec_line,
			R.id.pay_item_sb_line, R.id.pay_item_uppay_line,
			R.id.pay_submit_payMoney, R.id.pay_item_weixin_line })
	public void onclick(View view) {
		DecimalFormat df = new DecimalFormat("#.00");
		switch (view.getId()) {
		case R.id.top_back:
			finish();
			break;

		case R.id.pay_item_xj_line:// 现金账户支付
			state = 1;
			heji.setText("需支付：￥" + df.format(zhifu));
			changeImg(pay_item_xj_radio, pay_item_rec_radio,
					pay_item_weixin_radio, pay_item_sb_radio,
					pay_item_uppay_radio);
			pay_money_frame_two_line.setVisibility(View.GONE);
			break;
		case R.id.pay_item_rec_line:// 充值账户支付
			state = 2;
			heji.setText("需支付：￥" + df.format(zhifu));
			changeImg(pay_item_rec_radio, pay_item_xj_radio,
					pay_item_weixin_radio, pay_item_sb_radio,
					pay_item_uppay_radio);
			pay_money_frame_two_line.setVisibility(View.VISIBLE);
			break;
		case R.id.pay_item_sb_line:// 上币账户支付
			state = 5;
			heji.setText("需支付：￥" + df.format(zhifu * 10));
			changeImg(pay_item_sb_radio, pay_item_xj_radio,
					pay_item_weixin_radio, pay_item_rec_radio,
					pay_item_uppay_radio);
			pay_money_frame_two_line.setVisibility(View.VISIBLE);
			break;
		case R.id.pay_item_uppay_line:// 银联支付
			state = 3;
			heji.setText("需支付：￥" + df.format(zhifu));
			changeImg(pay_item_uppay_radio, pay_item_weixin_radio,
					pay_item_xj_radio, pay_item_rec_radio, pay_item_sb_radio);
			pay_money_frame_two_line.setVisibility(View.GONE);
			break;
		case R.id.pay_item_weixin_line:// 银联支付
			state = 6;
			heji.setText("需支付：￥" + df.format(zhifu));
			changeImg(pay_item_weixin_radio, pay_item_uppay_radio,
					pay_item_xj_radio, pay_item_rec_radio, pay_item_sb_radio);
			pay_money_frame_two_line.setVisibility(View.GONE);
			break;
		case R.id.pay_submit_payMoney:// 支付订单
			if (state == 2 || state == 5) {
				if (TextUtils.isEmpty(shop_pay_pay_twoPwd.getText().toString()
						.trim())) {
					Toast.makeText(this, "请输入支付密码", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			createOrder();
			break;
		}
	}

	/**
	 * 改变checkbox的图片
	 */
	private void changeImg(ImageView... imageViews) {
		for (int i = 0; i < imageViews.length; i++) {
			if (i == 0) {
				imageViews[i].setImageResource(R.drawable.pay_item_checked);
			} else {
				imageViews[i].setImageResource(R.drawable.pay_item_no_checked);
			}
		}
	}

	/**
	 * 确认订单
	 */
	private void createOrder() {
		Util.asynTask(this, "确认订单信息……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				LogUtils.e("支付返回" + runData);
				if (runData != null) {
					String[] message = runData.toString().split(":");
					if (message.length > 1) {
						if (message[0].equals("success")
								&& (!message[1].equals("-1"))) {
							payOrder();
						} else {
							Toast.makeText(YYRGPayMoneyFrame.this,
									runData.toString(), Toast.LENGTH_LONG)
									.show();
						}
					} else {
						Toast.makeText(YYRGPayMoneyFrame.this,
								runData.toString(), Toast.LENGTH_LONG).show();
					}

					/*
					 * }else { Toast.makeText(YYRGPayMoneyFrame.this,
					 * "操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show(); }
					 */
				} else {
					Toast.makeText(YYRGPayMoneyFrame.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.pay_createOrder,
						"userId=" + UserData.getUser().getUserId() + "&md5Pwd="
								+ UserData.getUser().getMd5Pwd() + "&yscids="
								+ yscids);
				return web.getPlan();
			}
		});
	}

	/**
	 * 支付订单
	 */
	private void payOrder() {
		Util.asynTask(this, "支付中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				LogUtils.e("支付返回[" + runData + "]");
				if (runData == null) {
					Util.show("网络异常，请重试！", YYRGPayMoneyFrame.this);
					return;
				}
				if (state == 1 || state == 2 || state == 5) {
					if (runData.toString().equals("success")) {
						Toast.makeText(YYRGPayMoneyFrame.this, "支付成功",
								Toast.LENGTH_SHORT).show();
						Util.showIntent(YYRGPayMoneyFrame.this,
								YYRGMyRegouFrame.class);
						finish();
					} else {
						Toast.makeText(YYRGPayMoneyFrame.this,
								runData.toString(), Toast.LENGTH_SHORT).show();
					}
				} else if (state == 3) {// 网银支付
					final String[] orderId = runData.toString().split(":");
					Log.v("order", runData.toString());
					if (orderId.length > 1) {
						if (orderId[0].equals("success")) {
							Util.asynTask(YYRGPayMoneyFrame.this, "银联支付中...",
									new IAsynTask() {
										@Override
										public void updateUI(
												Serializable runData) {
											if (!Util.isNull(runData)) {

											} else {
												Util.show("获取银联流水号错误，请重试！",
														YYRGPayMoneyFrame.this);
											}
										}

										@Override
										public Serializable run() {
											return null;
										}
									});
							return;
						} else {
							Toast.makeText(YYRGPayMoneyFrame.this,
									runData.toString(), Toast.LENGTH_SHORT)
									.show();
						}
					}
				} else if (6 == state) {
					final String[] orderId = runData.toString().split(":");
					Log.v("order", runData.toString());
					if (orderId.length > 1) {
						if (orderId[0].equals("success")) {
							CustomProgressDialog cpd = CustomProgressDialog
									.showProgressDialog(YYRGPayMoneyFrame.this,
											"微信支付中...");
							new MMPay(YYRGPayMoneyFrame.this, cpd, Double
									.parseDouble(orderId[2]), orderId[1],
									"一元热购支付").pay();
						} else {
							Toast.makeText(YYRGPayMoneyFrame.this,
									runData.toString(), Toast.LENGTH_SHORT)
									.show();
						}
					}
				} else {
					Toast.makeText(YYRGPayMoneyFrame.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				MD5 md5 = new MD5();
				Web web = new Web(Web.yyrgAddress, Web.pay_1yygOrder, "userId="
						+ UserData.getUser().getUserId()
						+ "&md5Pwd="
						+ UserData.getUser().getMd5Pwd()
						+ "&payType="
						+ state
						+ "&twoPwd="
						+ md5.getMD5ofStr(shop_pay_pay_twoPwd.getText()
								.toString().trim()));
				return web.getPlan();
			}
		});
	}

	/**
	 * 获取账户信息
	 */
	private void getAmount() {
		Util.asynTask(this, "正在获取您的帐户信息...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				User user = (User) runData;
				if (null == user) {
					Util.show("获取帐户信息失败！", YYRGPayMoneyFrame.this);
					return;
				}
				pay_item_rec_ban.setText("余额："
						+ Util.getDouble(Util.getDouble(user.getRecharge()), 3)
						+ "");
				pay_item_sb_ban.setText("余额："
						+ Util.getDouble(Util.getDouble(user.getShangbi()), 3)
						+ "");
			}

			@Override
			public Serializable run() {
				return Web.reDoLogin();
			}
		});
	}
}
