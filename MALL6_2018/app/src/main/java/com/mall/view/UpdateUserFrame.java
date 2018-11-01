package com.mall.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * 
 * 功能： 修改用户信息<br>
 * 时间： 2013-4-9<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class UpdateUserFrame extends Activity {

	private EditText rName = null;
	private EditText mail = null;
	private EditText idNoe = null;
	private TextView userId = null;
	private boolean isSex = true;
	private RadioButton sexNan = null;
	private RadioButton sexNv = null;
	private Button sub = null;
	private Button cle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.update_user_info);
		Util.initTop(this, "完善个人信息", Integer.MIN_VALUE, null);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (null != UserData.getUser()) {
			if (!Util.isNull(UserData.getUser().getIdNo())||!Util.isNull(UserData.getUser().getPassport())) {
				Util.showIntent("您的资料已完善，不需要再修改！", this, UserInfoFrame.class);
			} else
				init();
		}
	}

	public void init() {
		if (null != UserData.getUser()) {
			userId = Util.getTextView(R.id.updName, this);
			try {
				userId.setText(Util.getNo_pUserId(java.net.URLDecoder.decode(UserData.getUser()
						.getUserId(), "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			rName = Util.getEditText(R.id.updRealName, this);
			mail = Util.getEditText(R.id.updEmail, this);
			idNoe = Util.getEditText(R.id.updIdNo, this);
			
			sexNan = Util.getRadioButton(R.id.updNan, this);
			sexNv = Util.getRadioButton(R.id.updNv, this);
			
			sexNan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					sexNan.setChecked(isChecked);
					sexNv.setChecked(!isChecked);
					isSex = true;
				}
			});
			sexNv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					sexNv.setChecked(isChecked);
					sexNan.setChecked(!isChecked);
					isSex = false;
				}
			});
			sub = Util.getButton(R.id.updSubmit, this);
			cle = Util.getButton(R.id.updClear, this);
			sub.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					if (Util.isNull(idNoe.getText().toString())) {
						Util.show("昵称不能为空", UpdateUserFrame.this);
						return;
					} else {
						idNoe.addTextChangedListener(new TextWatcher() {
							 String tmp = "";
							 String digits = "/\\:*?<>|\"\n\t";
							 @Override
							 public void onTextChanged(CharSequence s, int start, int before,
							  int count) {
								 idNoe.setSelection(s.length());
							 }
							 @Override
							 public void beforeTextChanged(CharSequence s, int start, int count,
							  int after) {
							 tmp = s.toString();
							 }
							 @Override
							 public void afterTextChanged(Editable s) {
							 String str = s.toString();
							 if (str.equals(tmp)) {
							  return;
							 }
							 StringBuffer sb = new StringBuffer();
							 for (int i = 0; i < str.length(); i++) {
							  if (digits.indexOf(str.charAt(i)) < 0) {
							  sb.append(str.charAt(i));
							  }
							 }
							 tmp = sb.toString();
							 idNoe.setText(tmp);
							 }
						
							});
							
					}
					if (Util.isNull(rName.getText().toString())) {
						Util.show("真实姓名不能为空", UpdateUserFrame.this);
						return;
					}
					if (!rName.getText().toString()
							.matches("^[\\u4e00-\\u9fa5]{2,4}$")) {
						Util.show("真实姓名只能是2-4位中文", UpdateUserFrame.this);
						return;
					}
					if (Util.isNull(idNoe.getText().toString())) {
						Util.show("身份证号不能为空", UpdateUserFrame.this);
						return;
					}
					if (!idNoe.getText().toString().matches("\\d{17}[\\d|x|X]")) {
						Util.show("身份证号格式错误", UpdateUserFrame.this);
						return;
					}
					if (!Util.isNull(mail.getText().toString()) 
							&& !mail.getText().toString().matches("^\\w+@\\w+\\.\\w+$")) {
						Util.show("电子邮箱格式错误", UpdateUserFrame.this);
						return;
					}
					Util.asynTask(UpdateUserFrame.this, "正在更新您的资料...", new IAsynTask(){
						@Override
						public Serializable run() {
							Web web = new Web(Web.updateUser, "userid="
									+ UserData.getUser().getUserId()
									+ "&md5Pwd="
									+ UserData.getUser().getMd5Pwd()
									+ "&realName="
									+ Util.get(rName.getText().toString())
									+ "&mail=" + mail.getText().toString()
									+ "&idNo=" + idNoe.getText().toString()
									+ "&sex=" + Util.get(isSex ? "男" : "女"));
							return web.getPlan();
						}

						@Override
						public void updateUI(Serializable runData) {
							if ("success".equals(runData+"")) {								
								Util.showIntent("个人信息完善成功!",
										UpdateUserFrame.this, UserCenterFrame.class,
										Lin_MainFrame.class);
							} else {
								Util.show(runData+"", UpdateUserFrame.this);
							}
						}						
					});
				}
			});
			cle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					idNoe.setText("");
					rName.setText("");
					mail.setText("");
				}
			});
		}
	}
}
