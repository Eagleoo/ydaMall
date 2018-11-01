package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.model.UserAccountWithdrawal;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;

/**
 * 提现窗体
 * 
 * @author kaifa
 * 
 */
@ContentView(R.layout.request_tixian)
public class RequestTX extends Activity {

	private TextView bank = null;
	private TextView bankCard = null;
	private TextView busMoney = null;
	private TextView tixian_account_type = null;
	private EditText txMoney = null;
	private ImageView imagedown=null;
	private EditText twoP = null;
	private Button btn = null;
	private Button close = null;
	private TextView rname;
	private TextView id_card;
	private TextView imgtitle_320;
	private TextView hint;
	private String tx_type="0"; //0:银行卡，1微信，2支付宝
	private Context context;

    @ViewInject(R.id.withdrawallist)
    private View withdrawallist;
    @ViewInject(R.id.pay_item_weixin_radio)
    private ImageView pay_item_weixin_radio;
    @ViewInject(R.id.pay_item_ali_radio)
    private ImageView pay_item_ali_radio;
    @ViewInject(R.id.pay_item_uppay_radio)
    private ImageView pay_item_uppay_radio;

	@ViewInject(R.id.pay_item_weixin_line)
	private View pay_item_weixin_line;

	@ViewInject(R.id.pay_item_ali_line)
	private View pay_item_ali_line;

	@ViewInject(R.id.pay_item_uppay_line)
	private View pay_item_uppay_line;

	@ViewInject(R.id.withdrawalsway)
	private TextView withdrawalsway;

@ViewInject(R.id.bankinfo)
private View bankinfo;

	private boolean isopen;

	private int yewubasenumber=500;


	private int weixinbanding=0; //0 为位绑定 1为绑定
	private int zhifubaobanding=0;
	private int yinghangbanding=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.setContentView(R.layout.request_tixian);
        ViewUtils.inject(this);
		context=this;
		Util.initTop(this, "提现申请", Integer.MIN_VALUE, null);

        withdrawallist.setVisibility(View.GONE);
	}

	private void  setWithdrawallistWay(View view){
        pay_item_weixin_radio.setImageResource(R.drawable.pay_item_no_checked);
        pay_item_ali_radio.setImageResource(R.drawable.pay_item_no_checked);
        pay_item_uppay_radio.setImageResource(R.drawable.pay_item_no_checked);
        switch (view.getId()){
            case R.id.pay_item_weixin_line:  //微信
                pay_item_weixin_radio.setImageResource(R.drawable.pay_item_checked);
				withdrawalsway.setText("微信提现");
				bankinfo.setVisibility(View.GONE);
                tx_type="1";
                break;
            case R.id.pay_item_ali_line: // 支付宝
                pay_item_ali_radio.setImageResource(R.drawable.pay_item_checked);
				withdrawalsway.setText("支付宝提现");
				bankinfo.setVisibility(View.GONE);
                tx_type="2";
                break;
            case R.id.pay_item_uppay_line:  //银行卡
                pay_item_uppay_radio.setImageResource(R.drawable.pay_item_checked);
				withdrawalsway.setText("银行卡提现");
				bankinfo.setVisibility(View.VISIBLE);
                tx_type="0";
                break;
        }
        Log.e("tx_type",tx_type+"tx_type");

    }

    @OnClick({R.id.pay_item_weixin_line,
	R.id.pay_item_ali_line,R.id.pay_item_uppay_line
	})
	private void clickWithdrawallistWay(View view){
		setWithdrawallistWay(view);
	}

	@Override
	protected void onStart() {
		super.onStart();
		init();
		checkCanCarryAccount();
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
		imgtitle_320 = Util.getTextView(R.id.imgtitle_320, this);
		imgtitle_320.setText("提现明细");
		imgtitle_320.setVisibility(View.VISIBLE);
		bank = Util.getTextView(R.id.tixian_bank, this);
		bankCard = Util.getTextView(R.id.tixian_bankCard, this);
		busMoney = Util.getTextView(R.id.tixian_cost, this);
		tixian_account_type = Util.getTextView(R.id.tixian_account_type, this);
		txMoney = Util.getEditText(R.id.tixian_money, this);
		twoP = Util.getEditText(R.id.tixian_twoPwd, this);
		btn = Util.getButton(R.id.tixian_submit, this);
		close = Util.getButton(R.id.tixian_clear, this);
		rname = (TextView) findViewById(R.id.rname);
		hint = (TextView) findViewById(R.id.hint);
		id_card = (TextView) findViewById(R.id.id_card);
		imagedown= (ImageView) findViewById(R.id.imagedown);
		rname.setText(UserData.getUser().getName());
		id_card.setText(!Util.isNull(UserData.getUser().getIdNo())?UserData.getUser().getIdNo():UserData.getUser().getPassport());
		bank.setText(user.getBank());
		bankCard.setText(user.getBankCard());
		imagedown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isopen){
					isopen=false;
					withdrawallist.setVisibility(View.GONE);
					imagedown.animate().rotation(0);
				}else{
					isopen=true;
					withdrawallist.setVisibility(View.VISIBLE);
					imagedown.animate().rotation(180);
				}


			}
		});
		bankCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Util.showIntent(RequestTX.this, UpdateBankFrame.class);

			}
		});
		if (getIntent().getStringExtra("userKey").equals("bus")) {
		String account_money=	getIntent().getStringExtra("account_money");
//			busMoney.setText(
//					new Web(Web.getMoney, "userid=" + user.getUserId() + "&md5Pwd=" + user.getMd5Pwd() + "&type=bus")
//							.getPlan());
			busMoney.setText(account_money);
			tixian_account_type.setText("业务账户余额：");
			hint.setText("        提现说明：只有500或500的整数倍时才可提现至银行卡账户，提现需扣除12%个人综合所得税（含手续费）。");
			txMoney.setHint("请输入500或500的整数倍");
		} else {
			String account_money=	getIntent().getStringExtra("account_money");
			busMoney.setText(account_money);
//			busMoney.setText(
//					new Web(Web.getMoney, "userid=" + user.getUserId() + "&md5Pwd=" + user.getMd5Pwd() + "&type=red_c")
//							.getPlan());
			tixian_account_type.setText("现金红包余额：");
			txMoney.setHint("请输入100或100的整数倍");
			hint.setText("        提现说明：只有100或100的整数倍时才能提现至银行卡账户。提现将扣除12%的个人综合所得税（含手续费）。");
		}if (getIntent().getStringExtra("userKey").equals("bus")) {
		String account_money=	getIntent().getStringExtra("account_money");
//			busMoney.setText(
//					new Web(Web.getMoney, "userid=" + user.getUserId() + "&md5Pwd=" + user.getMd5Pwd() + "&type=bus")
//							.getPlan());
			busMoney.setText(account_money);
			tixian_account_type.setText("业务账户余额：");
			hint.setText("        提现说明：只有500或500的整数倍时才可提现至银行卡账户，提现需扣除12%个人综合所得税（含手续费）。");
			txMoney.setHint("请输入500或500的整数倍");
		} else {
			String account_money=	getIntent().getStringExtra("account_money");
			busMoney.setText(account_money);
//			busMoney.setText(
//					new Web(Web.getMoney, "userid=" + user.getUserId() + "&md5Pwd=" + user.getMd5Pwd() + "&type=red_c")
//							.getPlan());
			tixian_account_type.setText("现金红包余额：");
			txMoney.setHint("请输入100或100的整数倍");
			hint.setText("        提现说明：只有100或100的整数倍时才能提现至银行卡账户。提现将扣除12%的个人综合所得税（含手续费）。");
		}
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String requestMoney = txMoney.getText().toString();
				String twoPwd = twoP.getText().toString();
				if (Util.isNull(requestMoney)) {
					Util.show("请输入提现金额。", RequestTX.this);
					return;
				}
				if (!requestMoney.matches("^\\d+$")) {
					Util.show("提现金额格式错误。", RequestTX.this);
					return;
				}
				final int m = Integer.parseInt(requestMoney);

				String oldMoney = busMoney.getText().toString();
				if (Double.parseDouble(oldMoney) < Double.parseDouble(m + "")) {
					Util.show("账户余额不足。", RequestTX.this);
					return;
				}
				if (Util.isNull(twoPwd)) {
					Util.show("请输入交易密码。", RequestTX.this);
					return;
				}
				final String tp = new MD5().getMD5ofStr(twoPwd);
				if (getIntent().getStringExtra("userKey").equals("bus")) {
					if (0 != m % 500&&m<yewubasenumber) {
						Util.show("提现金额必须大于等于"+yewubasenumber+"，并且是"+yewubasenumber+"的倍数。", RequestTX.this);
						return;


					}
					Util.asynTask(RequestTX.this, "申请提现中...", new IAsynTask() {

						@Override
						public void updateUI(Serializable runData) {
							Log.e("业务账户提现返回",runData.toString()+"LL");
							if ("success".equals(runData)) {
								Util.showIntent("提现申请成功。是否需要去查看？", RequestTX.this, AccountDetailsFrame.class,
										new String[] { "parentName", "userKey", "enumType"}, new String[] { "提现", "bus" ,"12"});
								
							
							} else {
								Util.show(runData + "", RequestTX.this);
							}
						}

						@Override
						public Serializable run() {
							Web web = new Web(Web.requestTX, "userid=" + UserData.getUser().getUserId() + "&md5Pwd="
									+ UserData.getUser().getMd5Pwd() + "&handler=&money=" + m + "&twoPwd=" + tp+"&tx_type="+tx_type
							);
							return web.getPlan();
						}
					});
				} else {
					if (0 != m % 100) {
						Util.show("提现金额必须大于等于100，并且是100的倍数。", RequestTX.this);
						return;
					}
					Util.asynTask(RequestTX.this, "申请提现中...", new IAsynTask() {

						@Override
						public void updateUI(Serializable runData) {
							if ("success".equals(runData)) {
								Util.showIntent("提现申请成功。是否需要去查看？", RequestTX.this, AccountDetailsFrame.class,
										new String[] { "parentName", "userKey","enumType" }, new String[] { "提现", "xj","40" });
								

								
							} else {
								Util.show(runData + "", RequestTX.this);
							}
						}

						@Override
						public Serializable run() {
							Web web = new Web("/Red_Cash_TX", "userid=" + UserData.getUser().getUserId() + "&md5Pwd="
									+ UserData.getUser().getMd5Pwd() + "&handler=&money=" + m + "&twoPwd=" + tp+"&tx_type="+tx_type);
							return web.getPlan();
						}
					});
				}
			}
		});

		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txMoney.setText("");
				twoP.setText("");
			}
		});
		imgtitle_320.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (getIntent().getStringExtra("userKey").equals("bus")) {
					Intent intent = new Intent(RequestTX.this, AccountDetailsFrame.class);
					intent.putExtra("parentName", "提现");
					intent.putExtra("userKey", "bus");
					intent.putExtra("enumType", "12");
					startActivity(intent);
				} else {
					Intent intent = new Intent(RequestTX.this, AccountDetailsFrame.class);
					intent.putExtra("parentName", "提现");
					intent.putExtra("userKey", "xj");
					intent.putExtra("enumType", "40");
					startActivity(intent);
				}
			}
		});
	}

	private void checkCanCarryAccount(){
		final CustomProgressDialog cpd = Util.showProgress("检查用户账户列表...", this);
		NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call="+"get_tx_way"+"&userId="
						+ UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()

				, new NewWebAPIRequestCallback(){

					@Override
					public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }

						Gson gson=new Gson();
						UserAccountWithdrawal userAccountWithdrawal=gson.fromJson(result.toString(),UserAccountWithdrawal.class);
						UserAccountWithdrawal.ListBean listBean=  userAccountWithdrawal.getList().get(0);
						if (listBean.getBank().equals("0")){
							pay_item_uppay_line.setVisibility(View.GONE);//  银行卡
							yinghangbanding=0;
						}else{
							setWithdrawallistWay(pay_item_uppay_line);
							yinghangbanding=1;
						}
						if (listBean.getWx().equals("0")){
							weixinbanding=0;
							pay_item_weixin_line.setVisibility(View.GONE);

						}else{
							setWithdrawallistWay(pay_item_weixin_line);
							weixinbanding=1;
						}
						if (listBean.getAlipay().equals("0")){
							zhifubaobanding=0;
							pay_item_ali_line.setVisibility(View.GONE);
						}else {
							setWithdrawallistWay(pay_item_ali_line);
							zhifubaobanding=1;
						}

						showtobanding();

					}



					@Override
					public void fail(Throwable e) {

					}

					@Override
					public void timeout() {

					}

					@Override
					public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
					}
				});
	}

	private void showtobanding(){
		String str="";
		if (weixinbanding==0){
			str="微信";
		}else if(zhifubaobanding==0){
			str="支付宝";
		}else  if (yinghangbanding==0){
			str="银行卡";
		}
		VoipDialog voipDialog = new VoipDialog("对不起，您还未绑定"+str+"！", this, "等一下去", "现在去", null,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Util.showIntent(context, PerfectInformationFrame.class);
					}
				});
		if (!str.equals("")){
			voipDialog.show();
		}

	}

}
