package com.mall.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lin.component.CustomProgressDialog;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MyPopWindow;
import com.mall.util.PayType;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.carMall.RechargeResultActivity;

import net.sourceforge.mm.MMPay;

import java.io.Serializable;

/**
 * 功能： 账户充值<br>
 * 时间： 2013-7-17<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class AccountRecharge extends Activity {

    private EditText money;
    private TextView line;
    private Button sub;
    private Button cle;

    String userKey;
    String title = "账户充值";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.account_rechang_frame);
        ViewUtils.inject(this);
        userKey = getIntent().getStringExtra("userKey");
        this.findViewById(R.id.pay_item_rec_line).setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_sb_line).setVisibility(View.GONE);
        View ali = this.findViewById(R.id.pay_item_ali_line);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        View xianjin = this.findViewById(R.id.pay_item_xj_line);
        PayType.addPayTypeListener(this, R.id.pay_item_weixin_line, null, weixin, ali, xianjin);

        if (userKey.equals("gwq")) {
        } else if (userKey.equals("购物券充值")) {
            title = "购物券充值";
        } else {
            xianjin.setVisibility(View.GONE);
        }
        init();
    }


    public String getPayType() {
        return PayType.getPayType(this);
    }


    private void initComponent() {
        Util.initTop(this, title, Integer.MIN_VALUE, null);
        money = Util.getEditText(R.id.account_rechang_money, this);
        line = (TextView) this.findViewById(R.id.account_rechang_show);
        sub = Util.getButton(R.id.account_rechang_sub, this);
        cle = Util.getButton(R.id.account_rechang_cle, this);
    }

    private void init() {
        initComponent();
        money.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable ss) {

                if (Util.isDouble(money.getText().toString())) {
                    line.setText(Util.getToUp(Double.parseDouble(money.getText()
                            .toString())));
                } else
                    line.setText("无效格式");
            }
        });
        sub.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String m = money.getText().toString();
                final String payType_ = getPayType();
                String s = line.getText().toString();
                if (Util.isNull(m)) {
                    Util.show("请输入要充值的金额！", AccountRecharge.this);
                    return;
                }

                if (Util.isNull(s) || "无效格式".equals(s) || "充值金额须小于伍万".equals(s)) {
                    Util.show("请检查您输入的金额！", AccountRecharge.this);
                    return;
                }
//				if(!Util.isInt(m)){
//					Util.show("充值金额只能是整数。", AccountRecharge.this);
//					return ;
//				}
                if (userKey.equals("gwq")) {
                    Util.showIntent(AccountRecharge.this, RechargeResultActivity.class);
                    return;
                }
                if (title.equals("购物券充值")) {
                    if ("1".equals(payType_)) {
                        String str = "请转账到公司指定账户，转账成功后，拨打4006663838客服热线提交转账凭证。\n" + "\n" +
                                "指定账户：\n" +
                                "开户名：深圳远大创业投资发展有限公司\n" +
                                "帐  号：4100 3900 0400 04960\n" +
                                "开户行：中国农业银行软件园支行\n" +
                                "\n" +
                                "开户名：深圳远大创业投资发展有限公司\n" +
                                "帐  号：4420 1516 9000 5251 8548\n" +
                                "开户行：中国建设银行深圳市铁路支行";
                        new MyPopWindow.MyBuilder(AccountRecharge.this, str, "确定", null).setColor("#F13232")
                                .setisshowclose(false)
                                .build().showCenter();
                        return;
                    }


                }


                Util.asynTask(AccountRecharge.this, "正在提交，请稍等...",
                        new IAsynTask() {
                            @Override
                            public void updateUI(Serializable runData) {
                                if (Util.isNull(runData)) {
                                    Util.show("网络错误，请重试！", AccountRecharge.this);
                                    return;
                                }
                                if ((runData + "").startsWith("success:")) {
                                    String[] att = (runData + "").split(":");
                                    if ("4".equals(payType_)) {
                                        aliPay(att[1], Double.parseDouble(att[2]));
                                    } else if ("6".equals(payType_)) {
                                        weixinPay(att[1], Double.parseDouble(att[2]));
                                    } else if ("1".equals(payType_)) {

                                    }
                                } else
                                    Util.show(runData + "", AccountRecharge.this);
                            }

                            @Override
                            public Serializable run() {
                                String url = Web.recChongzhi;
                                if (title.equals("购物券充值")) {
                                    url = "/Shopping_voucherChongzhi";
                                }

                                Web web = new Web(url, "userid="
                                        + UserData.getUser().getUserId()
                                        + "&md5Pwd="
                                        + UserData.getUser().getMd5Pwd()
                                        + "&money=" + m
                                        + "&type=" + payType_);
                                return web.getPlan();
                            }
                        });
            }
        });
        cle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                money.setText("");
                line.setText("");
            }
        });
    }

    private void weixinPay(final String orderId, final double money) {
        CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "微信支付中...");
        new MMPay(this, cpd, money, orderId, "充值账户充值" + orderId).pay();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId, "充值账户充值", money,
                new AliPayCallBack() {
                    @Override
                    public void doSuccess(String aliResultCode) {
                        Util.show("充值成功！", AccountRecharge.this);
                    }

                    @Override
                    public void doFailure(String aliResultCode) {
                        Util.show("充值失败，支付宝状态码：" + aliResultCode, AccountRecharge.this);
                    }
                });
    }
}

	
	
	
	