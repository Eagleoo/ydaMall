package com.mall.view;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;

/**
 * 提现窗体
 * 
 * @author kaifa
 * 
 */
public class RequestCPTX extends Activity {
	
	private TextView tixian_account_type;
	private TextView bank = null;
	private TextView bankCard = null;
	private TextView busMoney = null;
	private EditText txMoney = null;
	private EditText twoP = null;
	private Button btn = null;
	private Button close = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.request_tixian);
		Util.initTop(this, "提现申请", Integer.MIN_VALUE, null);
	}

	@Override
	protected void onStart() {
		super.onStart();
		init();
	}

	public void init() {
		if (null == UserData.getUser()) {
			Util.showIntent("对不起，请先登录！", this, LoginFrame.class);
			return;
		}
		User user = UserData.getUser();
		if ("0".equals(UserData.getUser().getBank())) {
			Util.showIntent("对不起，提现必须完善银行信息！", this, UpdateBankFrame.class);
			return;
		}
		tixian_account_type = Util.getTextView(R.id.tixian_account_type,this);
		bank = Util.getTextView(R.id.tixian_bank, this);
		bankCard = Util.getTextView(R.id.tixian_bankCard, this);
		busMoney = Util.getTextView(R.id.tixian_cost, this);
		txMoney = Util.getEditText(R.id.tixian_money, this);
		twoP = Util.getEditText(R.id.tixian_twoPwd, this);
		btn = Util.getButton(R.id.tixian_submit, this);
		close = Util.getButton(R.id.tixian_clear, this);

		bank.setText(user.getBank());
		tixian_account_type.setText("彩票账户余额：");
		bankCard.setText(user.getBankCard());
		busMoney.setText(new Web(Web.getMoney, "userid=" + user.getUserId()
				+ "&md5Pwd=" + user.getMd5Pwd() + "&type=cp").getPlan());
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String requestMoney = txMoney.getText().toString();
				String twoPwd = twoP.getText().toString();
				if (Util.isNull(requestMoney)) {
					Util.show("请输入提现金额。", RequestCPTX.this);
					return;
				}
				if (!requestMoney.matches("^\\d+$")) {
					Util.show("提现金额格式错误。", RequestCPTX.this);
					return;
				}
				final int m = Integer.parseInt(requestMoney);

				String oldMoney = busMoney.getText().toString();
				if (Double.parseDouble(oldMoney) < Double.parseDouble(m + "")) {
					Util.show("账户余额不足。", RequestCPTX.this);
					return;
				}
				if (m < 100) {
					Util.show("提现金额必须大于100，并且是100的倍数。", RequestCPTX.this);
					return;
				}
				if(0 != m % 100){
					Util.show("提现金额必须是100的倍数。", RequestCPTX.this);
					return;
				}
				if (Util.isNull(twoPwd)) {
					Util.show("请输入交易密码。", RequestCPTX.this);
					return;
				}
				final String tp = new MD5().getMD5ofStr(twoPwd);
				Util.asynTask(RequestCPTX.this, "申请提现中...", new IAsynTask() {
					@Override
					public void updateUI(Serializable runData) {
						if ("success".equals(runData)) {
							Util.showIntent("提现申请成功。是否需要去查看？", RequestCPTX.this,
							TXDetailsFrame.class, new String[] {
											"parentName", "userKey" }, new String[] {
											"提现", "cptixian" });
						} else {
							Util.show(runData+"", RequestCPTX.this);
						}						
					}
					
					@Override
					public Serializable run() {
							Web web = new Web(Web.cpTX, "userid="
								+ UserData.getUser().getUserId() + "&md5Pwd="
								+ UserData.getUser().getMd5Pwd() + "&handler=&money="
								+ m + "&twoPwd=" + tp);
						return web.getPlan();
					}
				});
			}
		});
		
		close.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				txMoney.setText("");
				twoP.setText("");
			}
		});
	}
}
