package com.mall.view;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mall.model.AduditProxy;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.UserData;
import com.mall.util.Util;

public class AduditProxyFream extends Activity {

	private Handler handler = new Handler();
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adudit_proxy);
		init();
		Util.initTop(this, "代理审批", Integer.MIN_VALUE, null);

		ListView listView = (ListView) findViewById(R.id.adudit);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setAlwaysDrawnWithCacheEnabled(true);
		bind(listView, 1);

	}

	public void bind(final ListView listView, final int page) {
		pd = ProgressDialog.show(AduditProxyFream.this, null, "数据加载中....");
		new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				// 得到用户登录信息
				User u = UserData.getUser();

				ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

				try {
					Web web = new Web(Web.getAllProxy, "userid="
							+ u.getUserId() + "&md5Pwd=" + u.getMd5Pwd()
							+ "&pageSize=60&page=" + page);

					List<AduditProxy> la = web.getList(AduditProxy.class);
					if (la != null && la.size() != 0) {
						for (AduditProxy as : la) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put("name", as.getName());
							map.put("city", as.getCity());
							map.put("prov", as.getProvince());
							map.put("county", as.getCounty());
							map.put("cost", as.getCost());
							map.put("status", as.getStatus());
							map.put("level", as.getLevel());
							map.put("idno", as.getIdno());
							map.put("date", as.getDate());
							map.put("userId", as.getUserId());
							list.add(map);
						}
					} else {
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("name", "null");
					}
					setAdapter(listView, new AduditProxyListener(
							AduditProxyFream.this, list, R.layout.adudit,
							new String[] { "userId", "name", "status" },
							new int[] { R.id.name, R.id.r_name, R.id.status }));
					pd.dismiss();
					Looper.loop();
				} catch (Exception e) {
					Util.showIntent("网络链接错误,请稍后重试...", AduditProxyFream.this,
							BusinessInfoFrame.class);
				}
			}
		}).start();
	}

	public void setAdapter(final ListView listView, final AduditProxyListener ma) {
		handler.post(new Runnable() {
			public void run() {
				listView.setAdapter(ma);
			}
		});
	}

	public void init() {
		Util.initTop(this, "代理审批", Integer.MIN_VALUE, null);
		if (null != UserData.getUser()) {
		} else {
			Util.showIntent("对不起，您还没有登录。", this, LoginFrame.class);
			return;
		}
	}
}

class AduditProxyListener extends SimpleAdapter {

	private Context context;

	private List<? extends Map<String, ?>> data;

	public AduditProxyListener(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
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

					showMessage(
							data.get(position).get("name") + "",
							data.get(position).get("idno") + "",
							data.get(position).get("prov") + " > "
									+ data.get(position).get("city") + " > "
									+ data.get(position).get("county"), data
									.get(position).get("cost") + "",
							data.get(position).get("level") + "",
							data.get(position).get("status") + "",
							data.get(position).get("date") + "");

				}
			});
		}
		return v;
	}

	public void showMessage(String name, String idno, String prov, String cost,
			String level, String status, String date) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		View layout = LayoutInflater.from(context).inflate(
				R.layout.adudit_proxy_detail, null);
		dialog.setIcon(android.R.drawable.ic_dialog_dialer);
		dialog.setTitle("详细信息:");
		dialog.setView(layout);
		// 得到控件，并赋值
		TextView tname = (TextView) layout.findViewById(R.id.name);
		TextView tidno = (TextView) layout.findViewById(R.id.idno);
		TextView tprov = (TextView) layout.findViewById(R.id.province);
		TextView tcost = (TextView) layout.findViewById(R.id.cost);
		TextView tlevel = (TextView) layout.findViewById(R.id.level);
		TextView tstatus = (TextView) layout.findViewById(R.id.status);
		TextView tdate = (TextView) layout.findViewById(R.id.date);
		tname.setText(name);
		tidno.setText(idno);
		tprov.setText(prov);
		tcost.setText(Double.parseDouble(cost) + "  元");
		tlevel.setText(level);
		tstatus.setText(status);
		tdate.setText(date);
		dialog.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

}
