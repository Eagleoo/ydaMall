package com.mall.card;

import java.io.Serializable;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardUtil;
import com.mall.card.bean.CardLinkman;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

public class CardAddNewCard extends Activity {
	@ViewInject(R.id.surname)
	private EditText surname;
	@ViewInject(R.id.name)
	private EditText name;
	@ViewInject(R.id.user_phone)
	private EditText user_phone;
	@ViewInject(R.id.user_phone2)
	private EditText user_phone2;
	@ViewInject(R.id.user_phone3)
	private EditText user_phone3;
	@ViewInject(R.id.emial)
	private EditText emial;
	@ViewInject(R.id.position)
	private EditText position;
	@ViewInject(R.id.company)
	private EditText company;
	@ViewInject(R.id.address)
	private EditText address;
	@ViewInject(R.id.qq)
	private EditText qq;
	@ViewInject(R.id.linphone1)
	private LinearLayout linphone1;
	@ViewInject(R.id.linphone2)
	private LinearLayout linphone2;
	@ViewInject(R.id.linqq)
	private LinearLayout linqq;
	@ViewInject(R.id.linnet)
	private LinearLayout linnet;
	@ViewInject(R.id.netAddress)
	private EditText netAddress;
	private LinearLayout lin_dh1;
	@ViewInject(R.id.gd_phone1)
	private EditText gd_phone1;
	@ViewInject(R.id.lin_dh2)
	private LinearLayout lin_dh2;
	@ViewInject(R.id.gd_phone2)
	private EditText gd_phone2;
	private int statem = 0;
	private int statec = 0;
	private String Name;
	private String phoneNumber;
	private String state;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_add_new_card);
		ViewUtils.inject(this);
		Name = getIntent().getStringExtra("Name");
		phoneNumber = getIntent().getStringExtra("phoneNumber");
		state = getIntent().getStringExtra("state");
		if (!TextUtils.isEmpty(state)) {
			CardLinkman linkman = CardUtil.cardLinkman;
			if (!TextUtils.isEmpty(linkman.getPhone2())
					|| !TextUtils.isEmpty(linkman.getPhone3())
					|| !TextUtils.isEmpty(linkman.getQq())
					|| !TextUtils.isEmpty(linkman.getFaxNumber())) {
				linphone1.setVisibility(View.VISIBLE);
				linphone2.setVisibility(View.VISIBLE);
				lin_dh2.setVisibility(View.VISIBLE);
				statem = 1;

			}
			if (!TextUtils.isEmpty(linkman.getWebSite())
					|| !TextUtils.isEmpty(linkman.getFixedTelephone())
					|| !TextUtils.isEmpty(linkman.getFixedTelephone2())) {
				linqq.setVisibility(View.VISIBLE);
				linnet.setVisibility(View.VISIBLE);
				statec = 1;
			}
			name.setText(linkman.getName());
			user_phone.setText(linkman.getPhone());
			user_phone2.setText(linkman.getPhone2());
			user_phone3.setText(linkman.getPhone3());
			qq.setText(linkman.getQq());
			emial.setText(linkman.getFaxNumber());
			position.setText(linkman.getDuty());
			company.setText(linkman.getCompanyName());
			address.setText(linkman.getCompanyAddress());
			netAddress.setText(linkman.getWebSite());
			gd_phone1.setText(linkman.getFixedTelephone());
			gd_phone2.setText(linkman.getFixedTelephone2());
		}
		if (TextUtils.isEmpty(Name)) {

		} else {
			name.setText(Name);
			user_phone.setText(phoneNumber);
		}

	}

	/**
	 * 
	 */
	public void addNewCard(final String path) {
		final User user = UserData.getUser();
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				CardUtil.isMe = "0";
				if (runData != null) {
					Map<String, String> map = CardUtil.JsonToMap(runData
							.toString());
					if (map != null) {

						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Toast.makeText(CardAddNewCard.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
								finish();
							} else {
								Toast.makeText(CardAddNewCard.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							Toast.makeText(CardAddNewCard.this,
									"网络不给力哦，请检查网络是否连接后，再试一下",
									Toast.LENGTH_SHORT).show();
						}
					} else {

						Toast.makeText(CardAddNewCard.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(CardAddNewCard.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
							.show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard, Web.addBusinessCard,
						"addBusinessCard", "userId=" + user.getUserId()
								+ "&md5Pwd=" + user.getMd5Pwd() + path);
				String s = web.getPlan();
				return s;
			}

		});
	}

	/**
	 * 
	 */
	public void updateCard(final String path) {
		final User user = UserData.getUser();
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				CardUtil.isMe = "0";
				if (runData != null) {
					Map<String, String> map = CardUtil.JsonToMap(runData
							.toString());
					if (map != null) {

						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Toast.makeText(CardAddNewCard.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
								finish();
								CardOneCardMessage.cardMessage.finish();
							} else {
								Toast.makeText(CardAddNewCard.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {

							Toast.makeText(CardAddNewCard.this,
									"网络不给力哦，请检查网络是否连接后，再试一下",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(CardAddNewCard.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}
				} else {

					Toast.makeText(CardAddNewCard.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
							.show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard, Web.editBusinessCard,
						"editBusinessCard", "userId="
								+ UserData.getUser().getUserId() + "&md5Pwd="
								+ UserData.getUser().getMd5Pwd() + path
								+ "&id=" + CardUtil.cardLinkman.getId());
				String s = web.getPlan();
				return s;
			}

		});
	}

	@OnClick(R.id.top_back)
	public void topback(View view) {
		deleteTishi();
	}

	@OnClick({ R.id.wancheng, R.id.more_message, R.id.more_com_message })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.more_message:
			if (statem == 0) {
				linphone1.setVisibility(View.VISIBLE);
				linphone2.setVisibility(View.VISIBLE);
				lin_dh2.setVisibility(View.VISIBLE);
				statem = 1;
			} else {
				linphone1.setVisibility(View.GONE);
				linphone2.setVisibility(View.GONE);
				lin_dh2.setVisibility(View.GONE);

				statem = 0;
			}
			break;
		case R.id.more_com_message:
			if (statec == 0) {
				linqq.setVisibility(View.VISIBLE);
				linnet.setVisibility(View.VISIBLE);
				statec = 1;
			} else {
				linqq.setVisibility(View.GONE);
				linnet.setVisibility(View.GONE);
				statec = 0;
			}
			break;
		case R.id.wancheng:
			checked();
			
			break;

		default:
			break;
		}
	}

	private void deleteTishi() {
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.card_delete_dialog, null);
		dialog = new Dialog(this, R.style.CustomDialogStyle);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView yihou_update = (TextView) layout
				.findViewById(R.id.yihou_update);
		TextView now_update = (TextView) layout.findViewById(R.id.now_update);
		TextView update_count = (TextView) layout
				.findViewById(R.id.update_count);
		update_count.setText("您编辑的名片信息尚未保存，确定要离开吗？");
		yihou_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		now_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				dialog.dismiss();
			}
		});
	}
	
	
	private void checked() {
		String nameS = name.getText().toString().trim();
		
		String phone1 = user_phone.getText().toString().trim();
		String phone2 = user_phone2.getText().toString().trim();
		
		String phone3 = user_phone3.getText().toString().trim();
		
		String gdPhone1 = gd_phone1.getText().toString().trim();

		String gdPhone2 = gd_phone2.getText().toString().trim();
		String www = netAddress.getText().toString().trim();
		
		if (TextUtils.isEmpty(nameS)) {
		
			
			Util.show("请输入姓名", getApplicationContext());
			return;
		}
		if (TextUtils.isEmpty(phone1)) {
			
			Util.show("请输入电话", getApplicationContext());
			return;
		}
		if (TextUtils.isEmpty(nameS)||!CardUtil.checkZNAndEN(nameS)) {
			
			Util.show("名字不能为空，只能包含中文和英文", getApplicationContext());
			return;
		}
		if (!TextUtils.isEmpty(phone1)&&!CardUtil.isMobileNO(phone1)) {
			
			Util.show("电话号码不正确", getApplicationContext());
			return;
		}
		if (!TextUtils.isEmpty(phone2)&&!CardUtil.isMobileNO(phone2)) {
			
			Util.show("电话号码不正确", getApplicationContext());
			return;
		}
		if (!TextUtils.isEmpty(phone3)&&!CardUtil.isMobileNO(phone3)) {
			
			Util.show("电话号码不正确", getApplicationContext());
			return;
		}
		
		
		if (!TextUtils.isEmpty(gdPhone1)&&!CardUtil.checkZJ(gdPhone1)) {

			Util.show("固定电话1格式不正确，请重新输入", getApplicationContext());
			return;
		}
		if (!TextUtils.isEmpty(gdPhone2)&&!CardUtil.checkZJ(gdPhone2)) {
			
			Util.show("固定电话2格式不正确，请重新输入", getApplicationContext());
			return;
		}
		
		
		if (TextUtils.isEmpty(www)&&CardUtil.checkWeb("http://"
				+ www)) {
			Util.show("网址格式不正确，请重新输入", getApplicationContext());
			return;
		}
		
		
		
		
		String path = "&name=" + name.getText().toString().trim()
				+ "&phone=" + user_phone.getText().toString().trim()
				+ "&phone2=" + user_phone2.getText().toString().trim()
				+ "&phone3=" + user_phone3.getText().toString().trim()
				+ "&companyName=" + company.getText().toString().trim()
				+ "&headCompanyAddress="
				+ address.getText().toString().trim() + "&companyAddress="
				+ address.getText().toString().trim() + "&fixedTelephone="
				+ gd_phone1.getText().toString().trim()
				+ "&fixedTelephone2="
				+ gd_phone2.getText().toString().trim() + "&duty="
				+ position.getText().toString().trim() + "&webSite="
				+ netAddress.getText().toString().trim() + "&phone400="
				+ "" + "&qq=" + qq.getText().toString().trim() + "&remark="
				+ "&isMe=" + CardUtil.isMe;
		if (TextUtils.isEmpty(state)) {
			addNewCard(path);
		} else {
			updateCard(path);
		}

		
		
		
		
	}
	
	
	

}
