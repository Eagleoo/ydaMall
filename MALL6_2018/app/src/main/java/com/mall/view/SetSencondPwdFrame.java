package com.mall.view;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;

/**
 * 
 * 功能： 注册之后直接购买商品，设置交易密码<br>
 * 时间： 2014-1-24<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class SetSencondPwdFrame extends Activity {

	@ViewInject(R.id.set_two1)
	private EditText two1;
	@ViewInject(R.id.set_two2)
	private EditText two2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.set_sencondpwd_frame);
		Util.initTop(this, "完善交易密码", Integer.MIN_VALUE, null);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (null == UserData.getUser()) {
			Util.showIntent("您还未登录，请先登录！", this, LoginFrame.class);
			return;
		}
		if (!"0".equals(UserData.getUser().getSecondPwd())) {
			Util.showIntent("您的交易密码以完善，是否需要修改？", this, UpdateTwoPwdFrame.class,
					Lin_MainFrame.class);
			return;
		}
		ViewUtils.inject(this);
	}

	@OnClick(R.id.set_submit)
	public void submitClick(View v) {
		v.setEnabled(false);
		if (Util.isNull(two1.getText().toString())) {
			Util.show("请输入您的交易密码！", this);
		} else if (6 > two1.getText().toString().length()) {
			Util.show("交易密码至少6位！", this);
		} else if (Util.isNull(two2.getText().toString())) {
			Util.show("请输入确认交易密码！", this);
		} else if (6 > two2.getText().toString().length()) {
			Util.show("确认交易密码至少6位！", this);
		} else if (!two1.getText().toString().equals(two2.getText().toString())) {
			Util.show("2次交易密码不相同！", this);
		} else {
			Util.asynTask(this, "正在完善您的交易密码...", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if ("success".equals(runData + "")) {
						Util.show("交易密码完善成功！", SetSencondPwdFrame.this);
						Web.reDoLogin();
						SetSencondPwdFrame.this.finish();
					} else
						Util.show(runData + "", SetSencondPwdFrame.this);
				}

				@Override
				public Serializable run() {
					Web web = new Web(Web.updateTwoPwd_n2, "userid="
							+ UserData.getUser().getUserId() + "&md5Pwd="
							+ UserData.getUser().getMd5Pwd() + "&twoPwd="
							+ new MD5().getMD5ofStr(two1.getText().toString()));
					return web.getPlan();
				}
			});
		}
		v.setEnabled(true);
	}
}
