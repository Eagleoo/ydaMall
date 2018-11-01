package com.mall.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.mall.model.Zone;
import com.mall.net.Web;
import com.mall.util.AsynTask;
import com.mall.util.Data;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;
import java.util.List;

public class UpdateTwoPwd extends Activity {

	private Spinner shen = null;
	private Spinner shi = null;
	private Spinner qu = null;
	private EditText tjr = null;
	private EditText name = null;
	private EditText idCard = null;
	private EditText pho = null;
	private Button sub = null;
	private Button clo = null;
	private List<Zone> shenZone;
	private List<Zone> shiZone = null;
	private List<Zone> quZone = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.update_pwd_zone);
	}

	private void initComponet() {
		shen = Util.getSpinner(R.id.upz_shen, this);
		shi = Util.getSpinner(R.id.upz_shi, this);
		qu = (Spinner) this.findViewById(R.id.upz_qu1);
		sub = Util.getButton(R.id.upz_submit, this);
		clo = Util.getButton(R.id.upz_clear, this);
		tjr = Util.getEditText(R.id.upz_tjr, this);
		pho = Util.getEditText(R.id.upz_phone, this);
		name = Util.getEditText(R.id.upz_realName, this);
		idCard = Util.getEditText(R.id.upz_idCarde, this);
		if (!"0".equals(UserData.getUser().getZoneId())
				&& !"".equals(UserData.getUser().getZoneId())) {
			setGone(shen);
			setGone(shi);
			setGone(qu);
		}
		if (Util.isPhone(UserData.getUser().getMobilePhone())) {
			pho.setText(UserData.getUser().getMobilePhone());
			setGone(pho);
		}
		if (!Util.isNull(UserData.getUser().getInviter())) {
			tjr.setText(UserData.getUser().getInviter());
			setGone(tjr);
		}
	}

	private void setGone(View view) {
		((View) view.getParent()).setVisibility(View.GONE);
	}

	private void loadZone(final String city, final Zone zone) {
		String message = "";
		if ("shen".equals(city))
			message = "城市";
		if ("shi".equals(city))
			message = "区县";
		Util.asynTask(this, "正在获取" + message + "信息...", new AsynTask() {
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

				ArrayAdapter<Zone> adapter = new ArrayAdapter<Zone>(
						UpdateTwoPwd.this,
						android.R.layout.simple_spinner_item, "shen"
								.equals(city) ? shiZone : quZone);
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
				sp.setSelection(0);
			}

		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		Util.initTop(this, "完善详细信息", Integer.MIN_VALUE, null);
		if (null == UserData.getUser()) {
			Util.showIntent("对不起，您还没登录!", this, LoginFrame.class);
			return;
		}
		initComponet();
		if (!Util.isNull(UserData.getUser().getIdNo())||!Util.isNull(UserData.getUser().getPassport())) {
			Util.showIntent("对不起，您已经完善过了。是否前去修改!", this, UpdateUserMessageActivity.class,UserCenterFrame.class);
			return;
		}
		Util.asynTask(this, "正在获取地域信息...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				shen.setAdapter(new ArrayAdapter<Zone>(UpdateTwoPwd.this,
						android.R.layout.simple_spinner_item, shenZone));
				shen.setSelection(0);
				shen.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						final Zone shen_zone = (Zone) shen.getSelectedItem();
						shi.setEnabled(false);
						qu.setEnabled(false);
						loadZone("shen", shen_zone);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
				shi.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						final Zone shen_zone = (Zone) shi.getSelectedItem();
						qu.setEnabled(false);
						loadZone("shi", shen_zone);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
			}

			@Override
			public Serializable run() {
				shenZone = Data.getShen();
				return "";
			}
		});

		sub.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.isNull(pho.getText().toString())) {
					Util.show("手机号码不能为空", UpdateTwoPwd.this);
					return;
				}
				if (!Util.isPhone(pho.getText().toString())) {
					Util.show("手机号码格式错误", UpdateTwoPwd.this);
					return;
				}
				if (Util.isNull(name.getText().toString())) {
					Util.show("真实姓名不能为空", UpdateTwoPwd.this);
					return;
				}
				if (Util.isNull(idCard.getText().toString())) {
					Util.show("身份证号码不能为空", UpdateTwoPwd.this);
					return;
				}
				if (!idCard.getText().toString().matches("^\\d{17}[x|X|\\d]$")) {
					Util.show("身份证号码格式错误！", UpdateTwoPwd.this);
					return;
				}
				if (Util.isNull(tjr.getText().toString())) {
					Util.show("推荐人不能为空", UpdateTwoPwd.this);
					return;
				}
				String zoneID = "";
				if ("0".equals(UserData.getUser().getZoneId())){
					if(null != ((Zone) qu.getSelectedItem()))
						zoneID = ((Zone) qu.getSelectedItem()).getId();
					else{
						Util.show("请选择您的地址！", UpdateTwoPwd.this);
						return;
					}	
				}else
					zoneID = UserData.getUser().getZoneId();
				final String tempID = zoneID;
				Util.asynTask(UpdateTwoPwd.this, "正在完善您的信息...",
						new IAsynTask() {
							@Override
							public void updateUI(Serializable runData) {
								if ("success".equals(runData + "")) {
									Util.showIntent("信息完善成功",
											UpdateTwoPwd.this,
											UserCenterFrame.class);
								} else {
									Util.show(runData + "", UpdateTwoPwd.this);
								}
							}

							@Override
							public Serializable run() {
								Web web = new Web(Web.setPwdZone, "userid="
										+ UserData.getUser().getUserId()
										+ "&md5Pwd="
										+ UserData.getUser().getMd5Pwd()
										+ "&zoneid=" + tempID + "&tjr="
										+ Util.get(tjr.getText().toString())
										+ "&pohone=" + pho.getText().toString()
										+ "&name="
										+ Util.get(name.getText().toString())
										+ "&idCard="
										+ idCard.getText().toString());
								return web.getPlan();
							}
						});
			}
		});
		clo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tjr.setText("");
			}
		});
	}
}
