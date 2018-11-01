package com.mall.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mall.model.Game;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

public class QQGameFream extends Activity {

	public Spinner gameOne_class;

	// 一级分类数组
	String gameOne_class_arr[] = { "请选择则游戏名称" };
	String gameOne_class_arr_id[] = { "0" };
	// 二级分类数组
	String gameTwo_class_arr[] = { "请选择游戏类型" };
	String gameTwo_class_arr_id[] = { "0" };

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qqgame_fream_main);
		Util.initTop(this, "Q币充值", Integer.MIN_VALUE, null);

		gameOne_class = (Spinner) findViewById(R.id.qq_p_name);
		if (null != UserData.getUser()) {
			// 得到一级分类
			try {
				Web web = new Web(Web.getGameClass, "categoryId=-1");

				List<Game> list = web.getList(Game.class);

				String s1 = "请选择游戏名称";
				String s1_id = "0";
				if (list != null && list.size() != 0) {
					for (Game g : list) {
						s1 += ("," + g.getCardName());
						s1_id += ("," + g.getCardid());
					}
				}

				gameOne_class_arr = s1.split(",");
				gameOne_class_arr_id = s1_id.split(",");

				ArrayAdapter<String> gameOneClassArray = new ArrayAdapter<String>(
						this, android.R.layout.simple_spinner_item,
						gameOne_class_arr);

				gameOneClassArray
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				gameOne_class.setAdapter(gameOneClassArray);
				gameOne_class.setSelection(1, true);
				gameOne_class
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// bindGameTwoClass(gameOne_class_arr_id[arg2]);

							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub

							}
						});

			} catch (Exception e) {
				e.printStackTrace();
				Util.showIntent("获取游戏列表错误！", QQGameFream.this,
						Lin_MainFrame.class);
			}

			Button btn = (Button) findViewById(R.id.qq_commitBtn);
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setClass(QQGameFream.this, QQGameFream.class);
					intent.putExtra("name", "");
					intent.putExtra("mz", "");
					intent.putExtra("num", "");
					intent.putExtra("price", "");
					QQGameFream.this.startActivity(intent);
				}
			});
		} else {
			Util.showIntent("对不起，您还没有登录。", this, LoginFrame.class);
			return;
		}

	}

	public void bindGameTwoClass(final String id) {
		
		Util.asynTask(this, "正在获取游戏分类...", new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {

				HashMap<String, List<Game>> map2_ = (HashMap<String, List<Game>>) runData;
				List<Game> list = map2_.get("list");
				Spinner gameTwo_class = (Spinner) findViewById(R.id.qq_name);
				String str = "请选择游戏类型";
				String str_id = "0";

				final List<HashMap<String, String>> lm = new ArrayList<HashMap<String, String>>();
				if (list.size() != 0 || list != null) {
					for (Game g : list) {
						HashMap<String, String> map = new HashMap<String, String>();
						// 保存数据
						map.put("price", g.getPrice());
						map.put("money", g.getMoney());
						map.put("number", g.getAmounts());

						str += ("," + g.getCardName());
						str_id += ("," + g.getCardid());
					}
				}

				gameTwo_class_arr = str.split(",");
				gameTwo_class_arr_id = str_id.split(",");

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(QQGameFream.this,
						android.R.layout.simple_spinner_item, gameTwo_class_arr);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				gameTwo_class.setAdapter(adapter);

				gameTwo_class
						.setOnItemSelectedListener(new OnItemSelectedListener() {
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								LinearLayout g3 = (LinearLayout) findViewById(R.id.q3);
								g3.setVisibility(View.VISIBLE);
								LinearLayout g4 = (LinearLayout) findViewById(R.id.q4);
								g4.setVisibility(View.VISIBLE);
								LinearLayout g5 = (LinearLayout) findViewById(R.id.q5);
								g5.setVisibility(View.VISIBLE);
								LinearLayout g6 = (LinearLayout) findViewById(R.id.q6);
								// 得到对应的控件并显示对应的数据
								TextView money = (TextView) findViewById(R.id.qq_mz);// 面值
								final TextView price = (TextView) findViewById(R.id.qq_price);// 价格
								final Spinner number = (Spinner) findViewById(R.id.qq_num); // 数量

								money.setText(Util.getDouble(
										Double.parseDouble(lm.get(arg2).get(
												"money")), 2)
										+ "");
								price.setText(Util.getDouble(
										Double.parseDouble(lm.get(arg2).get(
												"price")), 2)
										+ "");

								String s = lm.get(arg2).get("number");

								String numArr[] = {};

								// 绑定数量下拉框
								if (s.indexOf("-") >= 0) {
									String b = "";
									for (int i = Integer.parseInt(s.split("-")[0]); i <= Integer
											.parseInt(s.split("-")[1]); i++) {
										b += i + ",";
									}
									b = b.substring(0, b.length() - 1);
									numArr = b.split(",");
								} else if (s.indexOf(",") >= 0) {
									numArr = s.split(",");
								} else {
									numArr = new String[] { s };
								}

								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
										QQGameFream.this,
										android.R.layout.simple_spinner_item,
										numArr);
								adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								number.setAdapter(adapter);

								// 显示总价格
								final TextView countMoney = (TextView) findViewById(R.id.qq_count_price);
								countMoney.setTextColor(Color.RED);

								number.setOnItemSelectedListener(new OnItemSelectedListener() {

									public void onItemSelected(
											AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										// 设置总价
										float count = Float.parseFloat(price
												.getText().toString())
												* Integer.parseInt(number
														.getSelectedItem()
														.toString());

										countMoney.setText(count + " 元");
									}

									@Override
									public void onNothingSelected(
											AdapterView<?> arg0) {

									}
								});
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub

							}
						});
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getGameTwoClass, "categoryId=" + id);

				List<Game> list = web.getList(Game.class);
				HashMap<String, List<Game>> map = new HashMap<String, List<Game>>();
				map.put("list", list);
				return map;
			}
		});

	}
}