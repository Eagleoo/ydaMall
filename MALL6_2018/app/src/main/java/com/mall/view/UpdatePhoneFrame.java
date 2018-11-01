package com.mall.view;

import java.io.Serializable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.UserInfo;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.Dialog_can_not_receive;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.squareup.picasso.Picasso;

/**
 * 
 * 功能：修改手机号码<br>
 * 时间：2013-9-6<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class UpdatePhoneFrame extends Activity {

	private TextView phone;
	private LinearLayout yzmpic1, yzmpic2;
	private ImageView yzmpic_view1, yzmpic_view2;
	private EditText oldNum, newPhone, newNum, twoPwd, yzmpic_code1, yzmpic_code2;
	private Button sendOldNum, sendNewNum, submit, reset;
	// private String oldCode, newCode = "ocCode_";
	private String userid = "";
	private TextView cannotyam;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.update_phone_frame);
		init();
	}

	private void initComponent() {
		Util.initTop(this, "修改绑定手机", Integer.MIN_VALUE, null);
		phone = Util.getTextView(R.id.update_phone_phone, this);
		cannotyam = Util.getTextView(R.id.cannotyam, this);
		oldNum = Util.getEditText(R.id.update_phone_oldNUm, this);
		newPhone = Util.getEditText(R.id.update_phone_newPhone, this);
		newNum = Util.getEditText(R.id.update_phone_newNum, this);
		sendOldNum = Util.getButton(R.id.update_phone_sendOldNum, this);
		sendNewNum = Util.getButton(R.id.update_phone_sendNewNum, this);
		twoPwd = Util.getEditText(R.id.update_phone_twoPwd, this);
		submit = Util.getButton(R.id.update_phone_submit, this);
		reset = Util.getButton(R.id.update_phone_reset, this);
		yzmpic1 = (LinearLayout) findViewById(R.id.yzmpic1);
		yzmpic2 = (LinearLayout) findViewById(R.id.yzmpic2);
		yzmpic_code1 = (EditText) findViewById(R.id.yzmpic_code1);
		yzmpic_code2 = (EditText) findViewById(R.id.yzmpic_code2);
		yzmpic_view2 = (ImageView) findViewById(R.id.yzmpic_view2);
		yzmpic_view1 = (ImageView) findViewById(R.id.yzmpic_view1);
	}

	private void init() {
		initComponent();
		if (null == UserData.getUser()) {
			Util.showIntent("请先登录。", this, LoginFrame.class);
			return;
		}
		Util.asynTask(this, "正在获取您的手机号码...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (Util.isNull(runData)) {
					Util.showIntent("您还没有绑定手机号码！", UpdatePhoneFrame.this, UserCenterFrame.class, UserCenterFrame.class);
					return;
				}
				if ((runData + "").contains("|,|")) {
					if ((runData + "").split("\\|,\\|").length < 2) {
						Util.showIntent("您还没有绑定手机号码！", UpdatePhoneFrame.this, UserCenterFrame.class,
								UserCenterFrame.class);
						return;
					}
					userid = (runData + "").split("\\|,\\|")[0];
					String oldPhone = (runData + "").split("\\|,\\|")[1];
					if (Util.isNull(oldPhone)) {
						Util.showIntent("您还没有绑定手机号码！", UpdatePhoneFrame.this, UserCenterFrame.class,
								UserCenterFrame.class);
						return;
					}
					phone.setText(oldPhone);
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getUserInfo_1, "userId=" + UserData.getUser().getUserId());
				return web.getPlan();
			}
		});
		cannotyam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int width = getWindowManager().getDefaultDisplay().getWidth() / 10 * 8;
				int height = width / 3 * 5;
				Dialog_can_not_receive dialog = new Dialog_can_not_receive(UpdatePhoneFrame.this, R.style.dialog, width,
						height);
				dialog.show();

			}
		});
		sendNewNum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String newPhoneNumber = newPhone.getText().toString();
				if (!Util.isPhone(newPhoneNumber)) {
					Util.show("新手机号码格式不正确！", UpdatePhoneFrame.this);
					return;
				}
				Util.asynTask(UpdatePhoneFrame.this, "正在发送验证码，请稍等...", new IAsynTask() {
					@Override
					public void updateUI(Serializable runData) {
						if ((runData + "").equals("图形验证码不正确")) {
							yzmpic2.setVisibility(View.VISIBLE);
							Picasso.with(UpdatePhoneFrame.this).load("http://" + Web.webImage
									+ "/ashx/ImgCode.ashx?action=imgcode&phone=" + newPhone.getText().toString())
									.skipMemoryCache().into(yzmpic_view2);
							Util.show(runData + "", UpdatePhoneFrame.this);
							return;
						}
						if (-1 != (runData + "").indexOf("success")) {
							// newCode = (runData + "").split(":")[1];
							Util.show("验证码发送成功，请注意查收...", UpdatePhoneFrame.this);
							doUpdateSendNum(sendNewNum);
						} else
							Util.show(runData + "", UpdatePhoneFrame.this);
					}

					@Override
					public Serializable run() {
						Web web = new Web(Web.sendNewCode,
								"userId=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
										+ "&newPhone=" + newPhone.getText().toString() + "&imgcode="
										+ yzmpic_code2.getText().toString());
						return web.getPlan();
					}
				});

			}
		});
		sendOldNum.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.asynTask(UpdatePhoneFrame.this, "正在发送验证码，请稍等...", new IAsynTask() {
					@Override
					public void updateUI(Serializable runData) {
						if ((runData + "").equals("图形验证码不正确")) {
							yzmpic1.setVisibility(View.VISIBLE);
							Picasso.with(UpdatePhoneFrame.this).load("http://" + Web.webImage
									+ "/ashx/ImgCode.ashx?action=imgcode&phone=" +UserData.getUser().getMobilePhone())
									.skipMemoryCache().into(yzmpic_view1);
							Util.show(runData + "", UpdatePhoneFrame.this);
							return;
						}
						if (-1 != (runData + "").indexOf("success")) {
							// oldCode = (runData + "").split(":")[1];
							Util.show("验证码发送成功，请注意查收...", UpdatePhoneFrame.this);
							doUpdateSendNum(sendOldNum);
						} else
							Util.show(runData + "", UpdatePhoneFrame.this);
					}

					@Override
					public Serializable run() {
						Web web = new Web(Web.sendOldCode, "userId=" + UserData.getUser().getUserId() + "&md5Pwd="
								+ UserData.getUser().getMd5Pwd() + "&imgcode=" + yzmpic_code1.getText().toString());
						return web.getPlan();
					}
				});
			}
		});

		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.isNull(oldNum.getText().toString())) {
					Util.show("请输入原手机收到的验证码。", UpdatePhoneFrame.this);
				} else if (Util.isNull(newNum.getText().toString()))
					Util.show("请输入要重新绑定的新手机收到的验证码。", UpdatePhoneFrame.this);
				else if (Util.isNull(twoPwd.getText().toString()))
					Util.show("请输入您的交易密码。", UpdatePhoneFrame.this);
				// else if (!oldNum.getText().toString().equals(oldCode))
				// Util.show("原手机验证码错误！", UpdatePhoneFrame.this);
				// else if (!newNum.getText().toString().equals(newCode))
				// Util.show("新手机验证码错误！", UpdatePhoneFrame.this);
				else {
					Util.asynTask(UpdatePhoneFrame.this, "正在修改您的绑定手机，请稍等...", new IAsynTask() {
						@Override
						public void updateUI(Serializable runData) {
							if ("success".equals(runData + "")) {
								// Util.showIntent("绑定手机修改成功！",
								// UpdatePhoneFrame.this,
								// AccountManagerFrame.class);
								AlertDialog.Builder builder = new Builder(UpdatePhoneFrame.this);
								builder.setMessage("绑定手机修改成功！");
								builder.setTitle("提示");
								builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent();
										intent.setClass(UpdatePhoneFrame.this, AccountManagerFrame.class);
										intent.putExtra("className", UpdatePhoneFrame.this.getClass().toString());
										startActivity(intent);
										finish();
									}
								});
								// Util.showIntent("转账成功！",
								// MoneyToMoneyFrame.this,
								// AccountManagerFrame.class);
								builder.create().show();
							} else
								Util.show(runData + "", UpdatePhoneFrame.this);
						}

						@Override
						public Serializable run() {
							Web web = new Web(Web.updatePhone, "userId=" + UserData.getUser().getUserId() + "&md5Pwd="
									+ UserData.getUser().getMd5Pwd() + "&oldCode=" + oldNum.getText().toString().trim()
									+ "&newCode=" + newNum.getText().toString().trim() + "&newPhone="
									+ newPhone.getText().toString() + "&tpwd="
									+ new MD5().getMD5ofStr(twoPwd.getText().toString()));
							return web.getPlan();
						}
					});
				}

			}
		});

	}

	private void doUpdateSendNum(final Button btn) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bundle data = msg.getData();
				int index = data.getInt("index");
				if (0 > index)
					btn.setText("发送验证码" + index);
				else
					btn.setText("发送验证码");

			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				int i = 120;
				while (i >= 0) {
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putInt("index", i);
					msg.setData(data);
					handler.sendMessage(msg);
					i--;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
