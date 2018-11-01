package com.mall.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.PayType;
import com.mall.util.UserData;
import com.mall.util.Util;

import net.sourceforge.mm.MMPay;

import java.io.Serializable;

/**
 * 功能： 购物卡账户现金追加<br>
 * 时间： 2013-8-24<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class MoneyAppendFrame extends Activity {

    @ViewInject(R.id.money_append_up)
    private TextView up;
    @ViewInject(R.id.money_append_money)
    private EditText money;

    @ViewInject(R.id.pay_item_rec_radio)
    private ImageView angel_requestCZ1;
    @ViewInject(R.id.pay_item_sb_radio)
    private ImageView angel_requestSB1;
    @ViewInject(R.id.pay_item_reccon_radio)
    private ImageView angel_requestRECCON1;
    @ViewInject(R.id.pay_item_uppay_radio)
    private ImageView angel_requestWY1;
    @ViewInject(R.id.pay_item_ali_radio)
    private ImageView angel_requestAL1;
    @ViewInject(R.id.pay_item_sto_radio)
    private ImageView angel_requestSTO1;
    @ViewInject(R.id.pay_item_sto_line)
    private LinearLayout stoLine;

    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;

    @ViewInject(R.id.pay_item_sb_ban)
    private TextView pay_item_sb_ban;

    @ViewInject(R.id.pay_money_frame_two_line)
    private View twoLine;
    @ViewInject(R.id.shop_pay_pay_twoPwd)
    private EditText twoPwd;

    // 交易密码
    private String pwd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.money_append_frame);
        ViewUtils.inject(this);
        this.findViewById(R.id.pay_item_rec_line).setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_xj_line).setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_sb_line).setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_ali_line).setVisibility(View.GONE);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        PayType.addPayTypeListener(this, R.id.pay_item_uppay_line, twoLine, weixin);
        twoLine.setVisibility(View.GONE);
        init();
    }


    /**
     * 生成订单号方式
     *
     * @return
     */
    public String getPayType() {
        return PayType.getPayType(this);
    }

    private void init() {
        Util.initTitle(this, "现金追加", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
            public void afterTextChanged(Editable s) {
                if (Util.isDouble(money.getText().toString())) {
                    up.setText(Util.getToUp(Double.parseDouble(money.getText()
                            .toString())));
                }
            }
        });

    }

    @OnClick(R.id.money_append_clear)
    public void clearClick(View v) {
        up.setText("");
        money.setText("");
    }

    @OnClick(R.id.money_append_submit)
    public void submitClick(View v) {
        final String m = money.getText().toString();
        if (Util.isNull(m)) {
            Util.show("请输入要充值的金额。", MoneyAppendFrame.this);
        } else if (!Util.isInt(m))
            Util.show("充值金额只能是整数。", this);
        else {
            final String payType_ = getPayType();
            Log.e("---", payType_);
            Util.asynTask(this, "正在处理，请稍等。", new IAsynTask() {
                @Override
                public Serializable run() {
                    Web web = new Web(Web.moneyAppend, "userId="
                            + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&appMoney=" + m
                            + "&payType=" + payType_ + "&twoPwd=" + pwd);

                    return web.getPlan();
                }

                @Override
                public void updateUI(Serializable runData) {
                    String data = (String) runData;
                    if (null == runData) {
                        Util.show("网络错误，请重试！", MoneyAppendFrame.this);
                        return;
                    }
                    if (data.contains("success")) {

                        if ("3".equals(payType_) || payType_.equals("4") || "6".equals(payType_)) {

                            String[] split = data.split(":");

                            if (split.length >= 3) {

                                // 订单生成后付款
                                if (payType_.equals("4")) {
                                    aliPay(split[1], Double.parseDouble(split[2]));
                                } else if ("6".equals(payType_))
                                    wxPay(split[1], Double.parseDouble(split[2]));

                            }
                        } else {
                            Util.show("支付成功。", MoneyAppendFrame.this);
                            finish();
                        }
                    } else {
                        Util.show(data, MoneyAppendFrame.this);
                    }
                }
            });
        }
    }

    private void wxPay(final String orderId, final double money) {
        CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "微信支付中...");
        new MMPay(this, cpd, money, orderId, "购物卡现金追加").pay();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId, "购物卡现金追加", money,
                new AliPayCallBack() {
                    @Override
                    public void doSuccess(String aliResultCode) {
                        Util.show("充值成功！", MoneyAppendFrame.this);
                    }

                    @Override
                    public void doFailure(String aliResultCode) {
                        Util.show("充值失败，支付宝状态码：" + aliResultCode,
                                MoneyAppendFrame.this);
                    }
                });
    }

}
