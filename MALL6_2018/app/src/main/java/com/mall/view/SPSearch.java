package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.Util;

public class SPSearch extends Activity {

	private EditText a_search_edit;
	private TextView a_search_button;
	private TextView topCenter;
	private ImageView topback;
	private ImageView topright;

	private TextView search_type;
	private PopupWindow distancePopup = null;
	private ListView listview;
	private BaseMallAdapter<JSONObject> adapter = null;

	private int page = 1;
	private int size = 12;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_search);
		init();

	}

	private void init() {
		findview();
		listview();
		setListener();

	}

	private void listview() {

		// adapter =

	}

	private void setListener() {
		a_search_edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				a_search_button.setText("搜索");

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (Util.isNull(a_search_edit.getText().toString())) {
					a_search_button.setText("取消");
				} else {

					// page = 1;
					//
					// adapter = null;
					// loadData(a_search_edit.getText().toString());
				}
			}
		});

		a_search_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String text = a_search_button.getText().toString();
				if ("取消".equals(text)) {
					finish();
				} else if ("搜索".equals(text)) {
					String sValue = a_search_edit.getText().toString();
					sreach(sValue);
				}

			}
		});

		search_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getPopupWindow();
				startPopupWindow();
				distancePopup.showAsDropDown(v);
			}

		});


		topback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 搜索商品或商家
	 * 
	 * @param sValue
	 *            关键字
	 */
	private void sreach(String sValue) {
		if (!Util.isNull(sValue) && !"".equals(sValue.trim())) {
			if (search_type.getText().toString().trim().equals("商品")) {
				Intent intent = new Intent();
				intent.setClass(SPSearch.this, ProductSreachFrame.class);
				intent.putExtra("type1", "查询");
				intent.putExtra("sValue", sValue);
			
				  startActivity(intent);
		
						
			} else if (search_type.getText().toString().trim().equals("商家")) {
				Intent intent = new Intent();
				intent.setClass(SPSearch.this, LMSJFrame.class);
				intent.putExtra("type", "search");
				intent.putExtra("keyword", sValue);
				SPSearch.this.startActivity(intent);
			}
			a_search_edit.setText("");
		} else
			Util.show("请输入您要查询的" + search_type.getText().toString() + "!",
					SPSearch.this);

	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow() {
		View pview = getLayoutInflater().inflate(
				R.layout.dialog_new_page_search, null, false);
		TextView soushangpin = (TextView) pview.findViewById(R.id.soushangpin);
		TextView soushangjia = (TextView) pview.findViewById(R.id.soushangjia);
		soushangjia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				a_search_edit.setTag("noAjax");
				search_type.setText("商家");
				distancePopup.dismiss();
			}
		});
		soushangpin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				a_search_edit.setTag("ajax");
				search_type.setText("商品");
				distancePopup.dismiss();
			}
		});
		initpoputwindow(pview);
	}

	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
	}

	/**
	 * 新建一个popupwindow实例
	 * 
	 * @param view
	 */
	private void initpoputwindow(View view) {
		distancePopup = new PopupWindow(view,
				android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		// distancePopup.setAnimationStyle(R.style.popupanimation);
	}

	private void findview() {
		a_search_button = (TextView) this.findViewById(R.id.a_search_button);
		a_search_edit = (EditText) this.findViewById(R.id.serachText);

		search_type = (TextView) this.findViewById(R.id.search);
		listview = (ListView) this.findViewById(R.id.a_search_listview);
		topCenter = (TextView) this.findViewById(R.id.topCenter);
		topCenter.setText("搜索结果");
		topback = (ImageView) this.findViewById(R.id.topback);
		topright = (ImageView) this.findViewById(R.id.topright);
		topright.setVisibility(View.GONE);
	}

	private void loadData(String name) {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}
		final CustomProgressDialog cpd = Util.showProgress("加载中...", this);
		NewWebAPI.getNewInstance().getWebRequest(

				// 接口：http://www.yda360.com/Handler/ProductSearch.aspx?lx=product&r=1553&name=g&sourcePhoneType=iPhone&v_v=1.8.8&platType=iPhone_Mos_YDYS

				"/Handler/ProductSearch.aspx?lx=product&r=1553&name=" + name,

				new WebRequestCallBack() {
					@Override
					public void success(Object result) {
						if (Util.isNull(result)) {
							Util.show("网络错误，请重试！", SPSearch.this);
							return;
						}
						JSONObject json = JSON.parseObject(result.toString());
						if (200 != json.getIntValue("code")) {
							Util.show(json.getString("message"), SPSearch.this);
							return;
						}
						JSONObject[] objs = json.getJSONArray("list").toArray(
								new JSONObject[] {});
						if (null == adapter || 1 == page) {
							adapter = (new BaseMallAdapter<JSONObject>(
									R.layout.item_search_activity,
									SPSearch.this, objs) {
								@Override
								public View getView(int position,
										View convertView, ViewGroup parent,
										final JSONObject t) {

									setText(R.id.item_search_name,
											t.getString("name"));
									setText(R.id.item_search_jilu,
											t.getString("sbj"));
									convertView
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											Util.showIntent(
													SPSearch.this,
													ProductDeatilFream.class,
													new String[] { "url" },
													new String[] { t
														.getString("pid") });

										}
									});
									return convertView;
								}
							});
							listview.setAdapter(adapter);
						} else {
							adapter.add(objs);
							adapter.updateUI();
						}
						page++;
					}

					@Override
					public void requestEnd() {
						super.requestEnd();
						cpd.dismiss();
					}

					@Override
					public void fail(Throwable e) {
						super.fail(e);
					}
				});
	}

}
