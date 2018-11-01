package com.mall.view;

import java.io.Serializable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;

/**
 * 
 * 功能： 业务审批<br>
 * 时间： 2013-3-8<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class BusCreateFrame extends Activity {

	@ViewInject(R.id.aduditListView)
	private ListView listView;
	private int page = 1;
	private int lastItem = 0;
	private int status = 0; // 0可以加载，1不能加载，2加载中

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.businessinfo);
		ViewUtils.inject(this);
		Util.initTop(this, "业务审批", Integer.MIN_VALUE, null);
		init();
	}

	private void init() {
		page();
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= listView.getAdapter().getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					synchronized (listView) {
						if (0 == status) {
							status = 1;
							page();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

	private void page() {
		final CustomProgressDialog cpd = CustomProgressDialog
				.showProgressDialog(this, "正在获取您的业务...");
		status = 2;
		User user = UserData.getUser();
		NewWebAPI.getNewInstance().getMyAdudit(user.getUserId(),
				user.getMd5Pwd(), page, 15, new WebRequestCallBack() {
					@Override
					public void success(Object result) {
						if (null == result) {
							Util.show("数据加载中，请稍后...", BusCreateFrame.this);
							return;
						}
						JSONObject json = JSON.parseObject(result.toString());
						if (200 != json.getIntValue("code")) {
							String message = json.getString("message");
							Util.show(message, BusCreateFrame.this);
							return;
						}
						page++;
						ListAdapter adapter = listView.getAdapter();
						JSONArray array = json.getJSONArray("list");
						JSONObject[] objs = array.toArray(new JSONObject[] {});
						if (null == adapter) {
							adapter = new BaseMallAdapter<JSONObject>(
									R.layout.business_info_item,
									BusCreateFrame.this, objs) {
								@Override
								public View getView(int position,
										View convertView, ViewGroup parent,
										final JSONObject t) {
									setText(R.id.bus_info_item_name,
											Util.getNo_pUserId(t.getString("userId")));
									setText(R.id.bus_info_item_date,
											t.getString("date"));
									setText(R.id.bus_info_item_state,
											t.getString("status"));
									setText(R.id.bus_info_item_type,
											t.getString("applyMode"));
									bindListener(convertView, t);
									return convertView;
								}
							};
							listView.setAdapter(adapter);
						} else {
							((BaseMallAdapter<JSONObject>) adapter).add(objs);
							((BaseMallAdapter<JSONObject>) adapter).updateUI();
						}
					}

					private TextView getText(View root, int id) {
						return (TextView) root.findViewById(id);
					}

					private RadioButton getRadio(View root, int id) {
						return (RadioButton) root.findViewById(id);
					}

					public void doCreate(final DialogInterface dialog,
							String openOrBack, String applyId,
							String audioType, String twoPwd, String remark) {
						final CustomProgressDialog cpd = Util.showProgress("正在处理您的操作...", BusCreateFrame.this);
						final User user = UserData.getUser();
						NewWebAPI.getNewInstance().aduditBusiness(
								user.getUserId(), user.getMd5Pwd(), applyId,
								audioType, new MD5().getMD5ofStr(twoPwd),
								remark, openOrBack, new WebRequestCallBack() {
									@Override
									public void success(Object result) {
										JSONObject obj = JSON
												.parseObject(result.toString());
										if (200 != obj.getIntValue("code")) {
											Util.show(obj.getString("message"),
													BusCreateFrame.this);
										} else {
											Util.show(obj.getString("message"),
													BusCreateFrame.this);
											dialog.cancel();
											dialog.dismiss();
										}
									}

									@Override
									public void requestEnd() {
										cpd.cancel();
										cpd.dismiss();
										super.requestEnd();
									}
									
								});
					}

					public void bindListener(View view, final JSONObject t) {
						view.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								AlertDialog dialog = new AlertDialog.Builder(
										BusCreateFrame.this).create();
								dialog.setTitle("业务明细：");
								View root = LayoutInflater.from(
										BusCreateFrame.this).inflate(
										R.layout.audit_bus_item_detail, null);
								TextView userId = getText(root,
										R.id.aududit_item_detail_userId);
								userId.setText("申请会员：" + Util.getNo_pUserId(t.getString("userId")));
								TextView money = getText(root,
										R.id.aududit_item_detail_money);
								money.setText("申请金额：" + t.getString("quantity"));
								TextView state = getText(root,
										R.id.aududit_item_detail_state);
								state.setText("申请状态：" + t.getString("status"));
								TextView sqType = getText(root,
										R.id.aududit_item_detail_busType);
									sqType.setText("申请业务："
											+ t.getString("applyMode"));
								TextView payType = getText(root,
										R.id.aududit_item_detail_payType);
								payType.setText("支付方式："
										+ t.getString("applyType"));
								TextView handler = getText(root,
										R.id.aududit_item_detail_handle);
								handler.setText("辅导老师：" + Util.getNo_pUserId(t.getString("processorId")));

								final EditText remark = (EditText) root
										.findViewById(R.id.aududit_item_detail_remark);
								final EditText twoPwd = (EditText) root
										.findViewById(R.id.aududit_item_detail_twoPwd);
								if ("处理中".equals(t.getString("status"))) {
									final RadioButton recban = getRadio(root,
											R.id.aududit_item_detail_rec);
									final RadioButton raiban = getRadio(root,
											R.id.aududit_item_detail_rai);
									final RadioButton zcbban = getRadio(root,
											R.id.aududit_item_detail_zcb);
									
									if(UserData.getUser().l5DateIs2015_06_10after() || "8".equals(UserData.getUser().getLevelId())){
										raiban.setVisibility(View.GONE);
										recban.setVisibility(View.GONE);
										zcbban.setVisibility(View.VISIBLE);
										zcbban.setText("创业包：" + UserData.getUser().getZcb() + "");
										zcbban.setChecked(true);//设置为选中状态
									}else{
										zcbban.setVisibility(View.GONE);
										raiban.setVisibility(View.GONE);
										recban.setVisibility(View.VISIBLE);
										Util.asynTask(new IAsynTask() {
											@Override
											public void updateUI(
													Serializable runData) {
												recban.setText("充值账户：" + runData
														+ "");
											}
											@Override
											public Serializable run() {
												final User user = UserData
														.getUser();
												Web web = new Web(Web.getMoney,
														"userid="
																+ user.getUserId()
																+ "&md5Pwd="
																+ user.getMd5Pwd()
																+ "&type=rec");
												return web.getPlan();
											}
										});
									}
									
									
									dialog.setButton(
											"开通",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													String auditType = "";
													if (recban.isChecked())
														auditType = recban
																.getTag() + "";
													else if(raiban.isChecked())
														auditType = raiban
																.getTag() + "";
													else
														auditType = zcbban
														.getTag() + "";
													doCreate(
															dialog,
															"open",
															t.getString("applyId"),
															auditType,
															twoPwd.getText()
																	.toString(),
															remark.getText()
																	.toString());
												}
											});
									dialog.setButton3(
											"驳回",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													String auditType = "";
													if (recban.isChecked())
														auditType = recban
																.getTag() + "";
													else if(raiban.isChecked())
														auditType = raiban
																.getTag() + "";
													else
														auditType = zcbban.getTag() + "";
													doCreate(
															dialog,
															"back",
															t.getString("applyId"),
															auditType,
															twoPwd.getText()
																	.toString(),
															remark.getText()
																	.toString());
												}
											});
								} else {
									root.findViewById(
											R.id.aududit_item_detail_moneyLine)
											.setVisibility(View.GONE);
									root.findViewById(
											R.id.aududit_item_detail_pwdLine)
											.setVisibility(View.GONE);
									remark.setVisibility(View.GONE);
								}
								dialog.setButton2("关闭",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.cancel();
												dialog.dismiss();
											}
										});
								dialog.setView(root);
								dialog.show();
							}
						});
					}

					@Override
					public void requestEnd() {
						super.requestEnd();
						status = 0;
						cpd.cancel();
						cpd.dismiss();
					}
				});
	}

}
