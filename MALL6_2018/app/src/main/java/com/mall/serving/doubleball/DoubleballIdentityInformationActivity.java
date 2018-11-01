package com.mall.serving.doubleball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

import java.io.Serializable;

@ContentView(R.layout.doubleball_identity_information)
public class DoubleballIdentityInformationActivity extends Activity {
	@ViewInject(R.id.topCenter)
	private TextView topCenter;
	@ViewInject(R.id.et_real_name)
	private EditText et_real_name;
	@ViewInject(R.id.et_ID_number)
	private EditText et_ID_number;
	@ViewInject(R.id.et_phone_number)
	private EditText et_phone_number;
	@ViewInject(R.id.et_answer)
	private EditText et_answer;
	@ViewInject(R.id.tv_question)
	private TextView tv_question;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();
		user = UserData.getUser();
		et_real_name.setText(user.getName());
		et_ID_number.setText(!Util.isNull(UserData.getUser().getIdNo())?UserData.getUser().getIdNo():UserData.getUser().getPassport());
		et_phone_number.setText(user.getMobilePhone());
	}

	private void setView() {
		topCenter.setText("身份信息");
	}

	/**
	 * 返回点击事件
	 * 
	 * @param v
	 */
	@OnClick(R.id.topback)
	public void topback(View v) {
		finish();

	}

	/**
	 * 提交信息点击事件
	 * 
	 * @param v
	 */
	@OnClick(R.id.tv_submit)
	public void submit(View v) {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}
		String answer = et_answer.getText().toString();
		String ID_number = et_ID_number.getText().toString();
		String phone_number = et_phone_number.getText().toString();
		String real_name = et_real_name.getText().toString();
		if (Util.isNull(real_name)) {
			Util.show("真实姓名不能为空", this);
			return;
		}
		if (!real_name.matches("^[\\u4e00-\\u9fa5]{2,4}$")) {
			Util.show("真实姓名只能是2-4位中文", this);
			return;
		}
		if (Util.isNull(ID_number)) {
			Util.show("身份证号不能为空", this);
			return;
		}
		if (!ID_number.matches("\\d{17}[\\d|x|X]")) {
			Util.show("身份证号格式错误", this);
			return;
		}
		if (Util.isNull(phone_number)||!Util.checkPhoneNumber(phone_number)) {
			Util.show("电话号码格式错误", this);
			return;
		}
		if (!user.getMobilePhone().equals(phone_number)) {
			user.setMobilePhone(phone_number);
		}

		Util.asynTask(this, "正在更新您的资料...", new IAsynTask() {
			@Override
			public Serializable run() {
				Web web = new Web(Web.updateUser, "userid="
						+ UserData.getUser().getUserId() + "&md5Pwd="
						+ UserData.getUser().getMd5Pwd() + "&realName="
						+ Util.get(et_real_name.getText().toString())
						+ "&mail=" + user.getMail() + "&idNo="
						+ et_ID_number.getText().toString() + "&sex="
						+ Util.get(user.getSex()));
				return web.getPlan();
			}

			@Override
			public void updateUI(Serializable runData) {
				if ("success".equals(runData + "")) {

					Util.show("个人信息完善成功!",
							DoubleballIdentityInformationActivity.this);
					getIntentData();
				} else {
					Util.show(runData + "",
							DoubleballIdentityInformationActivity.this);
				}
			}
		});

	}

	/**
	 * 选择问题点击事件
	 * 
	 * @param v
	 */
	@OnClick(R.id.tv_question)
	public void question(View v) {

	}

	/**
	 * 把上一个页面得到的数据传到下一个页面
	 */
	private void getIntentData() {
		Intent intent = getIntent();
		intent.setClass(this, DoubleballOrderPayActivity.class);

		startActivity(intent);
		finish();

	}

}
