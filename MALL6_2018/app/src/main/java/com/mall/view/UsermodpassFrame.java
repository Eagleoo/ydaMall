package com.mall.view;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;

public class UsermodpassFrame extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usermodpass_frame);
		Util.initTop(this, "修改登录密码", Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UsermodpassFrame.this, UserInfoFrame.class));
			}
		});
		init();

		// 得到输入的数据
		final TextView old_pass = (TextView) findViewById(R.id.oldpass);
		final TextView new_pass = (TextView) findViewById(R.id.newpass);
		final TextView ok_pass = (TextView) findViewById(R.id.okpass);
		TextView up_clear = (TextView) findViewById(R.id.up_clear);
		up_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				old_pass.setText("");
				new_pass.setText("");
				ok_pass.setText("");
			}
		});
		final TextView btn = (TextView) findViewById(R.id.okBtn);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				if (old_pass.getText().toString().equals("")) {
					Toast.makeText(UsermodpassFrame.this, "请输入原密码!", Toast.LENGTH_SHORT).show();
					return;
				} else if (new_pass.getText().toString().equals("") || new_pass.getText().toString().length() < 6) {
					Toast.makeText(UsermodpassFrame.this, "请输入新密码,至少6位!", Toast.LENGTH_SHORT).show();
					return;
				} else if (ok_pass.getText().toString().equals("")) {
					Toast.makeText(UsermodpassFrame.this, "请再次输入新密码!", Toast.LENGTH_SHORT).show();
					return;
				} else if (!ok_pass.getText().toString().equals(new_pass.getText().toString())) {
					Toast.makeText(UsermodpassFrame.this, "两次输入密码不一致,请重新输入!", Toast.LENGTH_SHORT).show();
					return;
				} else {
					Util.asynTask(UsermodpassFrame.this, "密码修改中...", new IAsynTask() {

						@Override
						public void updateUI(Serializable runData) {
							if ("success".equals(runData + "")) {
								UserData.setUser(null);
								Util.showIntent("登录密码修改成功，请重新登录！", UsermodpassFrame.this, LoginFrame.class);
							} else {
								Util.show(runData + "", UsermodpassFrame.this);
							}
						}

						@Override
						public Serializable run() {
							// 验证原密码是否输入正确
							Web web = new Web(Web.getModPass,
									"userId=" + UserData.getUser().getUserId() + "&pwd="
											+ new MD5().getMD5ofStr(ok_pass.getText().toString()) + "&oldPwd="
											+ new MD5().getMD5ofStr(old_pass.getText().toString()) + "&newPwd="
											+ new MD5().getMD5ofStr(new_pass.getText().toString()));
							return web.getPlan();
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
