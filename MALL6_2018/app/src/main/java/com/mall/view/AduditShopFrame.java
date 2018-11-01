package com.mall.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mall.model.AduditShop;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;

public class AduditShopFrame extends Activity {

	private Handler handler = new Handler();
	private ProgressDialog pd;
	private int p = 1;
	private boolean isFoot = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adudit_shop);
		init();
		Util.initTop(this, "审批城市经理", Integer.MIN_VALUE, null);

		final ListView listView = (ListView) findViewById(R.id.adudit);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setAlwaysDrawnWithCacheEnabled(true);
		bind(listView, p);
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 判断是否已滑动或者处于底部
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && isFoot) {
					// 滚动到底部
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						p++;
						bind(listView, p);
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount == totalItemCount) {
					isFoot = true;
				}
			}
		});
	}

	public void bind(final ListView listView, final int page) {
		pd = ProgressDialog.show(AduditShopFrame.this, null, "数据加载中....");
		new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				// 得到用户登录信息
				User u = UserData.getUser();

				ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

				try {
					Web web = new Web(Web.getAllSite, "userid=" + u.getUserId()
							+ "&md5Pwd=" + u.getMd5Pwd() + "&pageSize=60&page="
							+ page);

					List<AduditShop> la = web.getList(AduditShop.class);
					if (la != null && la.size() != 0) {
						for (AduditShop as : la) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("name", as.getApplyerId());
							map.put("applyMode", as.getApplyMode());
							map.put("rname", as.getName());
							map.put("cost", as.getCost());
							map.put("date", as.getDate());
							map.put("payType", as.getPayType());
							map.put("status", as.getStatus());
							map.put("siteType", as.getSiteType());
							map.put("id", as.getApplyId());
							list.add(map);
						}
					} else {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("name", "null");
					}
					setAdapter(listView, new AduditShopListener(
							AduditShopFrame.this, AduditShopFrame.this, list,
							R.layout.adudit, new String[] { "name", "rname",
									"status" }, new int[] { R.id.name,
									R.id.r_name, R.id.status }));
					pd.dismiss();
					Looper.loop();
				} catch (Exception e) {
					Util.showIntent("网络链接错误,请稍后重试...", AduditShopFrame.this,
							BusinessInfoFrame.class);
				}
			}
		}).start();
	}

	public void setAdapter(final ListView listView, final AduditShopListener ma) {
		handler.post(new Runnable() {
			public void run() {
				listView.setAdapter(ma);
			}
		});
	}

	public void init() {
		Util.initTop(this, "审批城市经理", Integer.MIN_VALUE, null);
		if (null != UserData.getUser()) {
		} else {
			Util.showIntent("对不起，您还没有登录。", this, LoginFrame.class);
			return;
		}
	}
}

class AduditShopListener extends SimpleAdapter {

	private Context context;
	private AduditShopFrame frame = null;

	private List<? extends Map<String, ?>> data;

	public AduditShopListener(AduditShopFrame frame, Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		this.frame = frame;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);

		if (data.get(position).get("name").toString().equals("null")) {
			TextView t = (TextView) v.findViewById(R.id.name);
			t.setText("暂无数据.....");
			t.setHeight(200);
			t.setTextColor(Color.RED);
		} else {
			v.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					showMessage(data.get(position).get("id") + "",
							data.get(position).get("name") + "",
							data.get(position).get("applyMode") + "",
							data.get(position).get("cost") + "",
							data.get(position).get("date") + "",
							data.get(position).get("status") + "",
							data.get(position).get("siteType") + "");

				}
			});
		}
		return v;
	}

	public void showMessage(final String id, final String name, String mode,
			String cost, String date, String status, String type) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		View layout = LayoutInflater.from(context).inflate(
				R.layout.aducit_shop_detail, null);
		dialog.setIcon(android.R.drawable.ic_dialog_dialer);
		dialog.setTitle("详细信息:");
		dialog.setView(layout);
		// 得到控件，并赋值
		TextView sapplyerId = (TextView) layout.findViewById(R.id.sname);
		TextView sapplyMode = (TextView) layout.findViewById(R.id.sApplyMode);
		TextView scost = (TextView) layout.findViewById(R.id.scost);
		TextView sdate = (TextView) layout.findViewById(R.id.sdate);
		TextView sstatus = (TextView) layout.findViewById(R.id.sstatus);
		TextView stype = (TextView) layout.findViewById(R.id.ssiteType);

		sapplyerId.setText(name);
		sapplyMode.setText(mode);
		scost.setText(Double.parseDouble(cost) + "  元");
		sdate.setText(date);
		sstatus.setText(status);
		stype.setText(type);
		if (!"处理中".equals(sstatus.getText().toString())) {
			return;
		}
		dialog.setPositiveButton("开通", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				final AlertDialog.Builder dialog1 = new AlertDialog.Builder(
						context);
				final View layout = LayoutInflater.from(context).inflate(
						R.layout.audit_shop_kt, null);
				dialog1.setView(layout);
				dialog1.setTitle("开通创业空间");
				final RadioButton chongzhi = (RadioButton) layout
						.findViewById(R.id.audit_chongzhi);
				final RadioButton quanyi = (RadioButton) layout
						.findViewById(R.id.audit_quanyi);
				final RadioButton zhichanbao = (RadioButton) layout
						.findViewById(R.id.audit_zhichanbao);
				
				if(UserData.getUser().l5DateIs2015_06_10after()){
					chongzhi.setVisibility(View.GONE);
					quanyi.setVisibility(View.GONE);
					zhichanbao.setVisibility(View.VISIBLE);
					zhichanbao.setText("创业包  ￥ "
							+ Html.fromHtml("<html><body><span style='color:green'>"
									+ UserData.getUser().getZcb()
									+ "</span></body></html>"));
				}else{
					chongzhi.setVisibility(View.VISIBLE);
					quanyi.setVisibility(View.VISIBLE);
					zhichanbao.setVisibility(View.GONE);
					chongzhi.setText("充值账户  ￥ "
							+ Html.fromHtml("<html><body><span style='color:green'>"
									+ UserData.getUser().getRecharge()
									+ "</span></body></html>"));
					quanyi.setText("权益账户  ￥ "
							+ Html.fromHtml("<html><body><span style='color:green'>"
									+ UserData.getUser().getInterests()
									+ "</span></body></html>"));
				}
				dialog1.setIcon(android.R.drawable.ic_dialog_dialer);
				dialog1.setPositiveButton("开通",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String tempType = "0";
								if (chongzhi.isChecked())
									tempType = "0";
								else if(quanyi.isChecked())
									tempType = "1";
								else
									tempType = "2";
								final EditText twoPwd = (EditText) layout
										.findViewById(R.id.audit_shop_tpwd);
								final EditText remarkId = (EditText) layout
										.findViewById(R.id.audit_shop_remark);
								payShopSite(id, tempType, twoPwd.getText()
										.toString(), remarkId.getText()
										.toString(), name);
							}
						});
				dialog1.setNegativeButton("关闭",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				dialog1.show();

			}
		});
		dialog.setNegativeButton("驳回", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				final AlertDialog.Builder dialog1 = new AlertDialog.Builder(
						context);
				final View layout = LayoutInflater.from(context).inflate(
						R.layout.audit_shop_kt, null);
				dialog1.setView(layout);
				dialog1.setTitle("驳回申请");
				final RadioButton chongzhi = (RadioButton) layout
						.findViewById(R.id.audit_chongzhi);
				final RadioButton quanyi = (RadioButton) layout
						.findViewById(R.id.audit_quanyi);
				chongzhi.setText("充值账户  - "
						+ Html.fromHtml("<html><body><span style='color:green'>"
								+ UserData.getUser().getRecharge()
								+ "</span></body></html>"));
				quanyi.setText("权益账户  - "
						+ Html.fromHtml("<html><body><span style='color:green'>"
								+ UserData.getUser().getInterests()
								+ "</span></body></html>"));
				layout.findViewById(R.id.ttt_1).setVisibility(View.GONE);
				dialog1.setIcon(android.R.drawable.ic_dialog_dialer);
				dialog1.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								String tempType = "0";
								if (chongzhi.isChecked())
									tempType = "0";
								else
									tempType = "1";
								final EditText twoPwd = (EditText) layout
										.findViewById(R.id.audit_shop_tpwd);
								final EditText remarkId = (EditText) layout
										.findViewById(R.id.audit_shop_remark);
								back(id,tempType,twoPwd.getText().toString(),remarkId.getText().toString(),name);
							}
						});
				dialog1.setNegativeButton("关闭",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				dialog1.show();
			}
		});

		dialog.show();
	}

	/**
	 * 开通
	 * 
	 * @param id
	 * @param type
	 * @param twoPwd
	 * @param remark
	 * @param name
	 */
	private void payShopSite(final String id, final String type,
			final String twoPwd, final String remark, final String name) {
		Util.asynTask(frame, "正在开通中...", new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				if (null == runData) {
					Util.show("网络错误，请重试！", frame);
					return;
				}
				if ("success".equals(runData + "")) {
					Util.showIntent("会员【" + name + "】的创业空间开通成功！", frame,
							AduditShopFrame.class);
				} else
					Util.show(runData + "", frame);
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.createShopSite, "userid="
						+ UserData.getUser().getUserId() + "&md5Pwd="
						+ UserData.getUser().getMd5Pwd() + "&tpwd="
						+ new MD5().getMD5ofStr(twoPwd) + "&applyerId=" + id
						+ "&payType=" + type + "&status=1&remark="
						+ Util.get(remark));
				return web.getPlan();
			}
		});
	}

	/**
	 * 驳回
	 * 
	 * @param id
	 * @param type
	 * @param twoPwd
	 * @param remark
	 * @param name
	 */
	public void back(final String id, final String type, final String twoPwd,
			final String remark, final String name) {
		Util.asynTask(frame, "正在驳回中...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (null == runData) {
					Util.show("网络错误，请重试！", frame);
					return;
				}
				if ("success".equals(data.get(0))) {
					Util.showIntent("成功驳回会员【" + name + "】的申请", frame,
							AduditShopFrame.class);
				} else
					Util.show(runData+"", frame);
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.createShopSite, "userid="
						+ UserData.getUser().getUserId() + "&md5Pwd="
						+ UserData.getUser().getMd5Pwd() + "&tpwd="
						+ twoPwd + "&applyerId=" + id
						+ "&payType=" + type + "&status=4&remark="
						+ Util.get(remark));
				return web.getPlan();
			}
		});
	}
}
