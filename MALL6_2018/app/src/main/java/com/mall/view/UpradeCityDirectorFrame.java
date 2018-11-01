package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;

import net.sourceforge.mm.MMPay;

import java.io.Serializable;

/**
 * 功能： 升级城市总监<br>
 * 时间： 2014-10-15<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class UpradeCityDirectorFrame extends Activity {

    @ViewInject(R.id.update_proxy_frame_userLevel)
    private TextView update_proxy_frame_userLevel;
    @ViewInject(R.id.update_proxy_frame_shopLevel)
    private TextView update_proxy_frame_shopLevel;
    @ViewInject(R.id.update_proxy_frame_payMoney)
    private TextView update_proxy_frame_payMoney;
    @ViewInject(R.id.pay_item_rec_radio)
    private ImageView angel_requestCZ1;
    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;
    @ViewInject(R.id.pay_item_uppay_radio) //银联支付
    private ImageView angel_requestWY1;
    @ViewInject(R.id.pay_item_weixin_radio)  //微信
    private ImageView angel_requestWX1;
    @ViewInject(R.id.pay_item_ali_radio)  //支付宝
    private ImageView pay_item_ali_radio;
    @ViewInject(R.id.pay_item_xj_radio)
    private ImageView angel_requestXJ1;

    @ViewInject(R.id.update_proxy_frame_twoPwdLine)
    private LinearLayout update_proxy_frame_twoPwdLine;
    @ViewInject(R.id.update_proxy_frame_twoPwd)
    private EditText update_proxy_frame_twoPwd;

    private User user = null;

    CustomProgressDialog cus = null;

    @OnClick({R.id.pay_item_rec_line, R.id.pay_item_xj_line,
            R.id.pay_item_uppay_line, R.id.pay_item_weixin_line, R.id.pay_item_ali_line})
    public void payTypeClick(View v) {
        if (v.getId() == R.id.pay_item_ali_line
                || v.getId() == R.id.pay_item_uppay_line
                || v.getId() == R.id.pay_item_weixin_line) {
            update_proxy_frame_twoPwdLine.setVisibility(View.GONE);
        } else {
            update_proxy_frame_twoPwdLine.setVisibility(View.VISIBLE);
            if ("0".equals(UserData.getUser().getSecondPwd())) {
                VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(UpradeCityDirectorFrame.this,
                                SetSencondPwdFrame.class);
                    }
                }, null);
                voipDialog.show();
            }
        }
        angel_requestCZ1.setImageResource(R.drawable.pay_item_no_checked);
        angel_requestWY1.setImageResource(R.drawable.pay_item_no_checked);
        angel_requestWX1.setImageResource(R.drawable.pay_item_no_checked);
        pay_item_ali_radio.setImageResource(R.drawable.pay_item_no_checked);
        angel_requestXJ1.setImageResource(R.drawable.pay_item_no_checked);

        angel_requestCZ1.setTag(null);
        angel_requestWY1.setTag(null);
        angel_requestWX1.setTag(null);
        pay_item_ali_radio.setTag(null);
        angel_requestXJ1.setTag(null);
        if (R.id.pay_item_rec_line == v.getId()) {
            angel_requestCZ1.setImageResource(R.drawable.pay_item_checked);
            angel_requestCZ1.setTag("checked");
        } else if (R.id.pay_item_uppay_line == v.getId()) {
            angel_requestWY1.setImageResource(R.drawable.pay_item_checked);
            angel_requestWY1.setTag("checked");
        } else if (R.id.pay_item_weixin_line == v.getId()) {
            angel_requestWX1.setImageResource(R.drawable.pay_item_checked);
            angel_requestWX1.setTag("checked");
        } else if (R.id.pay_item_ali_line == v.getId()) {
            pay_item_ali_radio.setImageResource(R.drawable.pay_item_checked);
            pay_item_ali_radio.setTag("checked");
        } else if (R.id.pay_item_xj_line == v.getId()) {
            angel_requestXJ1.setImageResource(R.drawable.pay_item_checked);
            angel_requestXJ1.setTag("checked");
        }
    }

    public String getPayType() {
        if ("checked".equals(angel_requestCZ1.getTag() + ""))
            return "2";
        if ("checked".equals(angel_requestWY1.getTag() + ""))
            return "3";
        if ("checked".equals(angel_requestWX1.getTag() + ""))
            return "6";
        if ("checked".equals(pay_item_ali_radio.getTag() + ""))
            return "4";
        if ("checked".equals(angel_requestXJ1.getTag() + ""))
            return "1";
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.upgrade_city_director_frame);
        ViewUtils.inject(this);
        initData();
    }

    private void initData() {
        Util.initTop(this, "申请〔升级城市总监〕", Integer.MIN_VALUE, null);

//		this.findViewById(R.id.pay_item_rec_line).setVisibility(View.GONE);
//        this.findViewById(R.id.pay_item_xj_line).setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_sb_line).setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_uppay_line).setVisibility(View.GONE);
        user = UserData.getUser();
        update_proxy_frame_userLevel.setText("会员角色：" + user.getUserLevel());
        update_proxy_frame_userLevel.setVisibility(View.GONE);
        update_proxy_frame_shopLevel.setText("系统角色：" + user.getShowLevel());
        payTypeClick(this.findViewById(R.id.pay_item_weixin_line));
        int amount = 99800 - 19800;

        update_proxy_frame_payMoney.setText("￥" + amount + "");
        update_proxy_frame_payMoney.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("获取账户余额失败，请重试！", UpradeCityDirectorFrame.this);
                    return;
                }
                pay_item_rec_ban.setText("余额:" + runData + "");
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getMoney, "userid=" + user.getUserId()
                        + "&md5Pwd=" + user.getMd5Pwd() + "&type=rec");
                return web.getPlan();
            }
        });
    }

    @OnClick(R.id.update_proxy_frame_submit)
    public void submitClick(View view) {
        final String payType_ = getPayType();
        Log.e("提交的payType_", payType_ + "");
        String twoPwd = update_proxy_frame_twoPwd.getText().toString();
        if ("2".equals(payType_) && Util.isNull(twoPwd)) {
            Util.show("请输入您的交易密码！", this);
            return;
        }
        final User user = UserData.getUser();
        cus = Util.showProgress("正在为您升级中...", this);
        NewWebAPI.getNewInstance().upgradeCity(user.getUserId(),
                user.getMd5Pwd(), payType_, new MD5().getMD5ofStr(twoPwd), new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        JSONObject json = JSON.parseObject(result.toString());
                        String message = json.getString("message");
                        if (200 != json.getIntValue("code")) {
                            Util.show(message, UpradeCityDirectorFrame.this);
                        } else {
                            if (-1 == message.indexOf(":")) {

                                Util.show(message, UpradeCityDirectorFrame.this);
                                Util.showIntent(UpradeCityDirectorFrame.this,
                                        UserCenterFrame.class);
                            } else {
                                String[] values = message.split(":");
//								bankPay(values[0],values[1]);

                                double money = Double.parseDouble(values[2]);
                                String orderId = values[1];
                                Log.e("消息", "订单" + orderId + "支付金额" + money);

                                if ("6".equals(payType_))
                                    wxPay(orderId + "",
                                            money);
                                else if ("4".equals(payType_))
                                    aliPay(orderId + "",
                                            money);
                                else
                                    Util.show("无效的支付方式！",
                                            UpradeCityDirectorFrame.this);
                            }
                        }
                        cus.dismiss();
                        cus = null;
                    }
                });
    }

    private void wxPay(final String orderId, final double money) {
        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(this,
                "微信支付中...");
        new MMPay(this, pd, money, orderId, "申请城市总监").pay();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId, "申请城市总监", money, new AliPayCallBack() {
            @Override
            public void doSuccess(String aliResultCode) {
                Util.showIntent(UpradeCityDirectorFrame.this,
                        Lin_MainFrame.class);
                Util.show("城市总监申请成功！", UpradeCityDirectorFrame.this);
            }

            @Override
            public void doFailure(String aliResultCode) {
                Util.show("申请失败，支付宝状态码" + aliResultCode,
                        UpradeCityDirectorFrame.this);
            }
        });
    }


    /**
     * 银联支付回调地址
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
        String str = data.getExtras().getString("pay_result");
        if ("success".equalsIgnoreCase(str)) {
            msg = "支付成功！";
        } else if ("fail".equalsIgnoreCase(str)) {
            msg = "支付失败！";
        } else if ("cancel".equalsIgnoreCase(str)) {
            msg = "用户取消";
        }

        new MyPopWindow.MyBuilder(this,msg,"确定",null)
        .setTitle("支付结果通知").build().showCenter();
        ;
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("支付结果通知");
//        builder.setMessage(msg);
//        builder.setInverseBackgroundForced(true);
//        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.create().show();
    }

    @OnClick(R.id.update_proxy_frame_quit)
    public void qunitClick(View view) {
        this.finish();
    }

}
