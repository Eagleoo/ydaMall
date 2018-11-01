package com.mall.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.ShopAddress;
import com.mall.model.Zone;
import com.mall.net.Web;
import com.mall.util.AsynTask;
import com.mall.util.Data;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;
import java.util.List;

@ContentView(R.layout.shop_address_frame)
public class ShopAddressFrame extends Activity {
	private EditText name;
	private EditText phone;
	private Spinner shen;
	private Spinner shi;
	private Spinner qu;
	private EditText address;
	private EditText zipCode;
	private CheckBox isDefault;
	private Button save;
	private LinearLayout ll_add_address;
	private RelativeLayout rl_add_select;
	private TextView add_select;
	private TextView add_select_develop;
	private TextView add_location;
	int tag = 1;
	int in1 = 0;
	int in2 = 0;
	int in3 = 0;

	private List<Zone> shenZone = Data.getShen();
	private List<Zone> shiZone = null;
	private List<Zone> quZone = null;
	private String updateID = "-1";
	private ShopAddress sa;
	private String guids;

	private String shenName = "";
	private String cityName = "";
	private String quName = "";
	private String add_shen = "";
	private String add_shi = "";
	private String add_qu = "";
	private boolean isDefaultZone = false;

	private boolean isposition=false;

	private PositionService locationService;

	@ViewInject(R.id.addnametitle)
	TextView addnametitle;

	@ViewInject(R.id.phonetitle)
			private  TextView phonetitle;

	@ViewInject(R.id.yzbm)
			private TextView yzbm;

	@ViewInject(R.id.add_ssq)
			TextView add_ssq;

	@ViewInject(R.id.add_addresstitle)
			TextView add_addresstitle;


	ServiceConnection locationServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName na, IBinder service) {
			PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
			locationService = locationBinder.getService();
			locationService.startLocation();
			locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
				@Override
				public void onProgress(AMapLocation progress) {
					name.setText(UserData.getUser().getName());
					phone.setText(UserData.getUser().getMobilePhone());
					shenName = progress.getProvince();
					cityName = progress.getCity();
					quName = progress.getDistrict();
					if (null != shenName && null != cityName && null != quName) {
						add_location.setText(progress.getProvince() + "-" + progress.getCity() + "-"
								+ progress.getDistrict());
						Log.e("tag", "地址" + progress.getProvince() + "-" + progress.getCity() + "-"
								+ progress.getDistrict());
						isDefaultZone = true;
						isposition=true;

					} else {
						add_location.setText("暂无定位信息");
						isposition=false;
					}
					getId();
					init();
				}

				@Override
				public void onError(AMapLocation error) {
					add_location.setText("暂无定位信息");
					isposition=false;
				}
			});

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.shop_address_frame);

		ViewUtils.inject(this);

		inittext();

		initComponent();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (locationService!=null){
			locationService.stopLocation();
			getApplicationContext().unbindService(locationServiceConnection);
		}

	}

	private void inittext() {
		phonetitle.setText(Util.justifyString("手机号码:",6));
		addnametitle.setText(Util.justifyString("收货人姓名:",6));
		yzbm.setText(Util.justifyString("邮政编码:",6));
		add_ssq.setText(Util.justifyString("省市区:",6));
		add_addresstitle.setText(Util.justifyString("详细地址:",6));
	}

	private void initComponent() {
		Util.initTop(this, "新增收货地址", Integer.MIN_VALUE, null);
		name = Util.getEditText(R.id.add_name, this);
		phone = Util.getEditText(R.id.add_phone3_3, this);
		shen = Util.getSpinner(R.id.add_shen, this);

		shi = Util.getSpinner(R.id.add_shi, this);
		qu = Util.getSpinner(R.id.add_qu, this);
		add_location = Util.getTextView(R.id.add_location, this);

		address = Util.getEditText(R.id.add_address, this);
		zipCode = Util.getEditText(R.id.add_zipCode, this);
		save = Util.getButton(R.id.shop_address_add, this);
		isDefault = (CheckBox) this.findViewById(R.id.add_moren);
		rl_add_select = (RelativeLayout) this.findViewById(R.id.rl_add_select);
		add_select = (TextView) this.findViewById(R.id.add_select);
		add_select_develop = (TextView) this.findViewById(R.id.add_select_develop);
		ll_add_address = (LinearLayout) this.findViewById(R.id.ll_add_address);
		final String id = getIntent().getStringExtra("id");
		shen.setAdapter(new ArrayAdapter<Zone>(this, android.R.layout.simple_spinner_item, shenZone));
		guids = getIntent().getStringExtra("guid");



		if (Util.isNull(id)) {
			Intent intent = new Intent();
			intent.setAction(PositionService.TAG);
			intent.setPackage(getPackageName());
			getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);

			return;
		}

		Util.initTop(this, "收货地址修改", Integer.MIN_VALUE, null);

		Util.asynTask(this, "正在获取您的收货地址...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (null != runData) {
					sa = (ShopAddress) runData;
					updateID = sa.getShoppingAddId();
					name.setText(sa.getName());

					phone.setText(sa.getMobilePhone());
					address.setText(sa.getAddress());
					zipCode.setText(sa.getZipCode());

					boolean selected = false;
					if (!Util.isNull(sa.getIsDefault())) {
						if ("true".equals((sa.getIsDefault() + "").toLowerCase()))
							selected = true;
					}
					isDefault.setChecked(selected);
					int index = 0;
					shiZone = Data.getShi(sa.getShen());
					quZone = Data.getQu(sa.getShi());
					ArrayAdapter<Zone> shiAdapter = new ArrayAdapter<Zone>(ShopAddressFrame.this,
							android.R.layout.simple_spinner_item, shiZone);
					ArrayAdapter<Zone> quAdapter = new ArrayAdapter<Zone>(ShopAddressFrame.this,
							android.R.layout.simple_spinner_item, quZone);
					for (Zone zone : shenZone) {
						if (zone.getId().equals(sa.getShen())) {
							shen.setSelection(index, true);
							Log.d("shen", "--index=" + index + "    name=" + zone.getName());
							shenName = shen.getSelectedItem().toString();
							break;
						}
						index++;
					}
					int shiIndex = 0;
					for (Zone sz : shiZone) {
						if (sz.getId().equals(sa.getShi())) {
							Log.d("shi", "--index=" + shiIndex + "    name=" + sz.getName());
							shi.setAdapter(shiAdapter);
							shi.setSelection(shiIndex, true);
							cityName = shi.getSelectedItem().toString();
							break;
						}
						shiIndex++;
					}
					int quIndex = 0;
					for (Zone qz : quZone) {
						if (qz.getId().equals(sa.getZone())) {
							Log.d("qu", "--index=" + quIndex + "    name=" + qz.getName());
							qu.setAdapter(quAdapter);
							qu.setSelection(quIndex, true);
							quName = qu.getSelectedItem().toString();
							init();
							break;
						}
						quIndex++;
					}
					add_location.setText(shenName + "-" + cityName + "-" + quName);

					init();
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getShopAddressById, "userId=" + UserData.getUser().getUserId() + "&md5Pwd="
						+ UserData.getUser().getMd5Pwd() + "&id=" + id);
				return web.getObject(ShopAddress.class);
			}
		});
	}

	private void loadZone(final String city, final Zone zone) {
		Util.asynTask(new AsynTask() {
			@Override
			public Serializable run() {
				if ("shen".equals(city))
					shiZone = Data.getShi(zone.getId());
				else if ("shi".equals(city))
					quZone = Data.getQu(zone.getId());
				return city;
			}

			@Override
			public void updateUI(Serializable result) {
				super.updateUI(result);
				String city = result.toString();

				ArrayAdapter<Zone> adapter = new ArrayAdapter<Zone>(ShopAddressFrame.this,
						android.R.layout.simple_spinner_item, "shen".equals(city) ? shiZone : quZone);
				Spinner sp = null;
				if ("shen".equals(city)) {
					sp = shi;
					shi.setEnabled(true);
					qu.setEnabled(true);
				} else if ("shi".equals(city)) {
					sp = qu;
					qu.setEnabled(true);
				}
				sp.setAdapter(adapter);


				try {
					add_location.setText(shen.getSelectedItem().toString() + "-" +
							shi.getSelectedItem().toString()+"-" +((Zone)sp.getAdapter().getItem(0)).getName());
				}catch (Exception e){

				}


				if (isDefaultZone && "shen".equals(city)) {
					int i = 0;
					if (null != shiZone && shiZone.size() > 0) {
						for (Zone z : shiZone) {
							if (z.getId().equals(add_shi)) {
								sp.setSelection(i);
								break;
							}
							i++;
						}
					}

				}
				if (isDefaultZone && "shi".equals(city)) {
					int i = 0;
					if (null != quZone && quZone.size() > 0) {
						for (Zone z : quZone) {
							if (z.getId().equals(add_qu)) {
								sp.setSelection(i);
								break;
							}
							i++;
						}
						isDefaultZone = false;
					}
				}
			}

		});
	}

	private void init() {

		rl_add_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (tag) {
				case 1:
					add_select.setVisibility(View.GONE);
					add_select_develop.setVisibility(View.VISIBLE);
					ll_add_address.setVisibility(View.VISIBLE);
					tag = 2;
					break;
				case 2:
					add_select.setVisibility(View.VISIBLE);
					add_select_develop.setVisibility(View.GONE);
					ll_add_address.setVisibility(View.GONE);
					tag = 1;
					break;
				default:
					break;
				}

			}
		});
		shen.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (in1 == 0) {
					final Zone shen_zone = (Zone) shen.getSelectedItem();
					shi.setEnabled(false);
					qu.setEnabled(false);
					loadZone("shen", shen_zone);
				}

				if (in1 > 0) {

					final Zone shen_zone = (Zone) shen.getSelectedItem();
					shi.setEnabled(false);
					qu.setEnabled(false);
					loadZone("shen", shen_zone);

					add_shen = ((Zone) shen.getSelectedItem()).getId();

					add_location.setText(shen.getSelectedItem().toString());
				}
				in1++;

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		shi.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (in2 == 0) {
					final Zone shen_zone = (Zone) shi.getSelectedItem();
					qu.setEnabled(false);
					loadZone("shi", shen_zone);
				}

				if (in2 > 0) {

					final Zone shen_zone = (Zone) shi.getSelectedItem();
					qu.setEnabled(false);
					loadZone("shi", shen_zone);
					add_location.setText(shen.getSelectedItem().toString() + "-" + shi.getSelectedItem().toString());
					add_shi = ((Zone) shi.getSelectedItem()).getId();

				}
				in2++;

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		qu.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				try {

					Log.e("区选择1","in3"+in3);

					if (in3 == 0) {
						Log.e("区选择2","in3"+in3);
//						if (isposition){
							quName = add_location.getText().toString().split("-")[2];
//						}else{
//							quName = add_location.getText().toString().split("-")[1];
//						}

						Log.e("区选择3","in3"+in3);
						String quSp = qu.getSelectedItem().toString();
						Log.e("区选择4","in3"+in3);

						Log.e("区选择5","in3"+in3);
						// if (!quSp.equals(quName)) {
						if (!Util.isNull(add_shi) && !Util.isNull(quName)) {
							quZone = Data.getQu(add_shi);
							int indexqu = 0;
							if (null != quZone && quZone.size() > 0) {
								for (Zone qz : quZone) {
									if (qz.getName().equals(quName)) {
										add_qu = qz.getId();
										break;
									}
									indexqu++;
								}
							}
							if (!Util.isNull(add_qu)) {
								qu.setSelection(indexqu);
							}
						}

					}

					if (in3 > 0) {
						add_location.setText(shen.getSelectedItem().toString() + "-" + shi.getSelectedItem().toString()
								+ "-" + qu.getSelectedItem().toString());
						add_qu = ((Zone) qu.getSelectedItem()).getId();
					}
					in3++;
				}catch (Exception e){}



			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("-1".equals(updateID))
					insert();
				else
					update();
			}
		});

	}

	private void update() {
		final String uName = name.getText().toString();

		final String ph = phone.getText().toString();
		final String add_shen = ((Zone) shen.getSelectedItem()).getId();
		final String add_shi = ((Zone) shi.getSelectedItem()).getId();
		final String add_qu = ((Zone) qu.getSelectedItem()).getId();
		final String add = address.getText().toString();
		final String zp = zipCode.getText().toString();
		if (Util.isNull(uName)) {
			Util.show("请输入您的姓名", ShopAddressFrame.this);
			return;
		}
		if (Util.isNull(ph)) {
			Util.show("请输入您的手机号码", ShopAddressFrame.this);
			return;
		}
		if (!Util.isPhone(ph)) {
			Util.show("手机号码格式错误", ShopAddressFrame.this);
			return;
		}
		if (Util.isNull(add_qu)) {
			Util.show("所选“区或县”获取编号异常，请重试！", ShopAddressFrame.this);
			return;
		}
		if (Util.isNull(add)) {
			Util.show("请输入您的详细地址", ShopAddressFrame.this);
			return;
		}

		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if ("success".equals(runData + "")) {
					Util.asynTask( new IAsynTask() {
						@Override
						public void updateUI(Serializable runData) {
							if ("success".equals(runData + "")) {

							} else
								Util.show(runData + "", ShopAddressFrame.this);
						}

						@Override
						public Serializable run() {
							Web web = new Web(Web.addShopAddress,
									"userId=" + UserData.getUser().getUserId() + "&md5Pwd="
											+ UserData.getUser().getMd5Pwd() + "&name="
											+ Util.get(sa.getName()) + "&shen=" + sa.getShen() + "&shi="
											+ sa.getShi() + "&qu=" + sa.getZone() + "&zipCode="
											+ sa.getZipCode() + "&phone=" + sa.getMobilePhone() + "&gj="
											+ "" + "&quhao=" + "" + "&zuoji=" + sa.getPhone()
											+ "&address=" + Util.get(sa.getAddress())
											+ "&isDefault=true");

							return web.getPlan();
						}
					});
					finish();
				} else
					Util.show(runData + "", ShopAddressFrame.this);
			}

//			@Override
//			public Serializable run() {
//				Web web = new Web(Web.addShopAddress,
//						"userId=" + UserData.getUser().getUserId() + "&md5Pwd="
//								+ UserData.getUser().getMd5Pwd() + "&name="
//								+ Util.get(sa.getName()) + "&shen=" + sa.getShen() + "&shi="
//								+ sa.getShi() + "&qu=" + sa.getZone() + "&zipCode="
//								+ sa.getZipCode() + "&phone=" + sa.getMobilePhone() + "&gj="
//								+ "" + "&quhao=" + "" + "&zuoji=" + sa.getPhone()
//								+ "&address=" + Util.get(sa.getAddress())
//								+ "&isDefault=true");
////
////				Web web = new Web(Web.updateShopAddress,
////						"id=" + updateID + "&userId=" + UserData.getUser().getUserId() + "&md5Pwd="
////								+ UserData.getUser().getMd5Pwd() + "&name=" + Util.get(uName) + "&shen=" + add_shen
////								+ "&shi=" + add_shi + "&zone=" + add_qu + "&zipCode="
////								+ (Util.isNull(zp) ? zp : "001231") + "&mobilePhone=" + ph + "&address=" + Util.get(add)
////								+ "&isDefault=" + (isDefault.isChecked() ? "true" : "false") + "&gj=&quhao=&phone=");
////				Log.e("*****是否默认******",isDefault.isChecked()+"");
//				return web.getPlan();
//			}
			@Override
			public Serializable run() {
				Web web = new Web(Web.deleteUserShopAddress, "userid=" + UserData.getUser().getUserId()
						+ "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&sid=" + sa.getShoppingAddId());
				return web.getPlan();
			}
		});
	}

	private void insert() {
		final String uName = name.getText().toString();

		final String ph = phone.getText().toString();
		// add_shen = ((Zone) shen.getSelectedItem()).getId();
		// add_shi = ((Zone) shi.getSelectedItem()).getId();
		// add_qu = ((Zone) qu.getSelectedItem()).getId();
		final String add = address.getText().toString();
		final String zp = zipCode.getText().toString();
		if (Util.isNull(uName)) {
			Util.show("请输入您的姓名", ShopAddressFrame.this);
			return;
		}

		if (Util.isNull(ph)) {
			Util.show("请输入您的手机号码", ShopAddressFrame.this);
			return;
		}
		if (!Util.isPhone(ph)) {
			Util.show("手机号码格式错误", ShopAddressFrame.this);
			return;
		}
		if (Util.isNull(add_location.getText().toString())) {
			Util.show("请选择您的收货地区", ShopAddressFrame.this);
			return;
		}
		if (Util.isNull(add_qu)) {
			Util.show("所选“区或县”获取编号异常，请重试！", ShopAddressFrame.this);
			return;
		}
		if (Util.isNull(add)) {
			Util.show("请输入您的详细地址", ShopAddressFrame.this);
			return;
		}

		Util.asynTask(ShopAddressFrame.this, "正在添加您的收货地址。\n请稍等...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if ("success".equals(runData + "")) {
//					if (Util.isNull(guids)) {
//						Util.showIntent(ShopAddressFrame.this, ShopAddressManagerFrame.class);
//						Toast.makeText(ShopAddressFrame.this, "添加成功！", Toast.LENGTH_LONG).show();
//						ShopAddressFrame.this.finish();
//					} else {
//						Util.showIntent(ShopAddressFrame.this, PayCommitFrame.class, new String[] { "guid" },
//								new String[] { guids });
					Toast.makeText(ShopAddressFrame.this, "添加成功！", Toast.LENGTH_LONG).show();
						ShopAddressFrame.this.finish();
//					}
				} else
					Util.show(runData + "", ShopAddressFrame.this);
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.addShopAddress,
						"userId=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
								+ "&name=" + Util.get(uName) + "&shen=" + add_shen + "&shi=" + add_shi + "&qu=" + add_qu
								+ "&zipCode=" + (Util.isNull(zp) ? zp : "001231") + "&phone=" + ph + "&address="
								+ Util.get(add) + "&isDefault=" + (isDefault.isChecked() ? "true" : "false")
								+ "&gj=&quhao=&zuoji=");
				// Web web = new Web(Web.addShopAddress, "userId="
				// + UserData.getUser().getUserId() + "&md5Pwd="
				// + UserData.getUser().getMd5Pwd() + "&name="
				// + Util.get(uName) + "&shen=" + add_shen
				// + "&shi=" + add_shi + "&qu=" + add_qu
				// + "&zipCode=" + zp + "&phone=" + ph + "&gj="
				// + gj + "&quhao=" + qh + "&zuoji=" + zj
				// + "&address=" + Util.get(add) + "&isDefault="
				// + (isDefault.isChecked() ? "true" : "false"));

				return web.getPlan();
			}
		});
	}

	private void getId() {
		if (!Util.isNull(add_location.getText().toString())
				&& !add_location.getText().toString().trim().contains("定位信息")) {
			shenName = add_location.getText().toString().split("-")[0];
			cityName = add_location.getText().toString().split("-")[1];
			quName = add_location.getText().toString().split("-")[2];
			if (null != shenZone && shenZone.size() > 0) {
				int index = 0;
				for (Zone sz : shenZone) {
					if (sz.getName().equals(shenName)) {
						add_shen = sz.getId();
						break;
					}
					index++;
				}
				if (!Util.isNull(add_shen)) {
					shen.setSelection(index);
					shiZone = Data.getShi(add_shen);
					int indexshi = 0;
					if (null != shiZone && shiZone.size() > 0) {
						for (Zone shiz : shiZone) {
							if (shiz.getName().equals(cityName)) {
								add_shi = shiz.getId();
								break;
							}
							indexshi++;
						}
						if (!Util.isNull(add_shi)) {
							shi.setSelection(indexshi);
							quZone = Data.getQu(add_shi);
							if (null != quZone && quZone.size() > 0) {
								int indexqu = 0;
								for (Zone qz : quZone) {
									if (qz.getName().equals(quName)) {
										add_qu = qz.getId();
										break;
									}
									indexqu++;
								}
								if (!Util.isNull(add_qu)) {
									qu.setSelection(indexqu);
								}
							}

						}

					}
				}
			}

		} else {
			shen.setSelection(0);
			shenName = shen.getSelectedItem().toString();

			if (null != shenZone && shenZone.size() > 0) {

				for (Zone sz : shenZone) {
					if (sz.getName().equals(shenName)) {
						add_shen = sz.getId();
						break;
					}

				}
				if (!Util.isNull(add_shen)) {
					try {
						shiZone = Data.getShi(add_shen);

						if (null != shiZone && shiZone.size() > 0) {
							shi.setSelection(0);
							cityName = shi.getSelectedItem().toString();
							for (Zone shiz : shiZone) {
								if (shiz.getName().equals(cityName)) {
									add_shi = shiz.getId();
									break;
								}
							}
							quZone = Data.getQu(add_shi);
							if (null != quZone && quZone.size() > 0) {
								qu.setSelection(0);
								quName = qu.getSelectedItem().toString();
								for (Zone qz : quZone) {
									if (qz.getName().equals(quName)) {
										add_qu = qz.getId();
										break;
									}
								}
							}

						}
					}catch (Exception e){

					}

				}
			}

		}

	}
}
