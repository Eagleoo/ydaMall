package com.mall.view;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lin.component.CustomProgressDialog;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;

public class ChangPwdFrame extends Activity {

	private String userId;
	private String phone;
	private String yzm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changedpwd_frame);
		Util.initTop(this, "修改登录密码", Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ChangPwdFrame.this,
						UserInfoFrame.class));
			}
		});
		init();

		// 得到输入的数据
		Intent intent=getIntent();
		userId=intent.getStringExtra("userId");
		phone=intent.getStringExtra("phone");
		yzm=intent.getStringExtra("yzm");
		
		final TextView new_pass = (TextView) findViewById(R.id.newpass);
		final TextView ok_pass = (TextView) findViewById(R.id.okpass);
		TextView up_clear=(TextView) findViewById(R.id.up_clear);
		up_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				new_pass.setText("");
				ok_pass.setText("");
			}
		});
		final TextView btn = (TextView) findViewById(R.id.okBtn);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
			 if (new_pass.getText().toString().equals("")
						|| new_pass.getText().toString().length() < 6) {
					Toast.makeText(ChangPwdFrame.this, "请输入新密码,至少6位!",
							Toast.LENGTH_SHORT).show();
					return;
				} else if (ok_pass.getText().toString().equals("")) {
					Toast.makeText(ChangPwdFrame.this, "请再次输入新密码!",
							Toast.LENGTH_SHORT).show();
					return;
				} else if (!ok_pass.getText().toString()
						.equals(new_pass.getText().toString())) {
					Toast.makeText(ChangPwdFrame.this, "两次输入密码不一致,请重新输入!",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(ChangPwdFrame.this, "密码修改中...");
					Map<String,String> map = new HashMap<String,String>();
					map.put("userId",userId );
					map.put("phone",phone );
					map.put("code",yzm );
					map.put("newMd5Pwd",new MD5().getMD5ofStr(new_pass.getText().toString()));
					NewWebAPI.getNewInstance().getNewInstance().getWebRequest("/ShortMessage.aspx?call=updateLoginPwd",map, new WebRequestCallBack(){

						@Override
						public void success(Object result) {
							super.success(result);
							JSONObject json = JSON.parseObject(result.toString());
							
								if (200 == json.getIntValue("code")) {
									
									UserData.setUser(null);
									Util.showIntent("登录密码修改成功，请重新登录！",
											ChangPwdFrame.this, LoginFrame.class);
									return;
								}else if(707==json.getIntValue("code")){
									Util.show(json.getString("message"), ChangPwdFrame.this);
								}else{
									Util.show("网络错误，请稍等。", ChangPwdFrame.this);
								}
								
								return;
							
							
						}

						@Override
						public void requestEnd() {
							super.requestEnd();
							cpd.cancel();
							cpd.dismiss();
						}
						
					});
				
				}
			}
		});
	}

	private void init() {
		Util.initTop(this, "修改密码", Integer.MIN_VALUE, null);
	}

}
