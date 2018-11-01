package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.InviterInfo;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.PayType;
import com.mall.util.UserData;
import com.mall.util.Util;

import net.sourceforge.mm.MMPay;

import java.io.Serializable;

public class Site_ZCFrame extends Activity {


    @ViewInject(R.id.pay_item_xj_radio)
    private ImageView angel_requestXJ1;
    @ViewInject(R.id.pay_item_rec_radio)
    private ImageView angel_requestCZ1;
    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;
    @ViewInject(R.id.pay_item_uppay_radio)
    private ImageView angel_requestWY1;
    @ViewInject(R.id.pay_item_ali_radio)
    private ImageView angel_requestAL1;


    private EditText twoPwd = null;
    private EditText zsdw_ = null;
    private TextView wangdian = null;
    @ViewInject(R.id.request_site_two)
    private TableRow twoRow;
    @ViewInject(R.id.request_site_name_line)
    private TextView txt;
    @ViewInject(R.id.cb_agree)
    private CheckBox cb_agree;
    @ViewInject(R.id.tv_agree)
    private TextView tv_agree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.request_site_zc);
        ViewUtils.inject(this);
        this.findViewById(R.id.pay_item_sb_line).setVisibility(View.GONE);
        View xj = this.findViewById(R.id.pay_item_xj_line);
        View rec = this.findViewById(R.id.pay_item_rec_line);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        View ali = this.findViewById(R.id.pay_item_ali_line);
        PayType.addPayTypeListener(this, R.id.pay_item_uppay_line, twoRow, xj, rec, uppay, weixin, ali);

        Spanned html1 = Html.fromHtml("我已阅读并同意<font color=\"#49afef\">《城市经理合同》"
                + "</font>");
        tv_agree.setText(html1);
    }

    @OnClick(R.id.tv_agree)
    public void agree(View v) {

        Util.showIntent(this, ProvisionActivity.class,
                new String[]{"Site_ZCFrame"},
                new String[]{"Site_ZCFrame"});

    }

    @Override
    public void onStart() {
        super.onStart();
        intComponent();
    }


    public String getPayType() {
        return PayType.getPayType(this);
    }


    private void getInviter(final String name, final boolean lock) {
        Util.asynTask(this, "正在获取招商信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null != runData) {
                    InviterInfo info = (InviterInfo) runData;
                    int levelId = Util.getInt(info.getLevel());
                    int shopId = Util.getInt(info.getShopType());
                    boolean isSite = "1".equals(info.getIsSite());
                    if (5 == levelId || isSite) {
                        zsdw_.setText(Util.getNo_pUserId(info.getUserid()));
                        String name = info.getName();
                        if (!Util.isNull(name) && 2 <= name.length()) {
                            name = name.substring(0, 1) + "*";
                            if (3 <= info.getName().length())
                                name += info.getName().substring(info.getName().length() - 1);
                        }
                        String phone = info.getPhone();
                        if (!Util.isNull(phone))
                            phone = phone.substring(0, 3)
                                    + "****"
                                    + phone.subSequence(phone.length() - 4,
                                    phone.length());
                        txt.setText("用户资料： " + name + "　" + phone);
                        txt.setVisibility(View.VISIBLE);
                        if (!lock) {
                            zsdw_.setEnabled(false);
                        }
                    }
                } else {
                    txt.setText("用户资料： 　");
                    txt.setVisibility(View.GONE);
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getInviter, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&uid="
                        + Util.get(name));
                return web.getObject(InviterInfo.class);
            }
        });
    }

    private void intComponent() {
        Util.initTop(this, "申请〔城市经理(众筹)〕", Integer.MIN_VALUE, null);
        if (null != UserData.getUser()) {
            getInviter(UserData.getUser().getInviter(), false);

            twoPwd = Util.getEditText(R.id.vip_twoPwd, this);
            zsdw_ = Util.getEditText(R.id.vip_zsdw, this);
            wangdian = Util.getTextView(R.id.vip_wangMoney, this);
            twoRow = (TableRow) this.findViewById(R.id.request_site_two);

            zsdw_.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        getInviter(zsdw_.getText().toString(), true);
                }
            });
            Util.asynTask(new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (null == runData) {
                        Util.show("获取账户余额失败，请充值！", Site_ZCFrame.this);
                        return;
                    }
                    pay_item_rec_ban.setText("余额:" + runData + "");
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.getMoney,
                            "userid=" + UserData.getUser().getUserId()
                                    + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                                    + "&type=rec");
                    return web.getPlan();
                }
            });
        }
    }

    @OnClick(R.id.vip_clear)
    public void clear(View v) {
        twoPwd.setText("");
        if (zsdw_.isEnabled())
            zsdw_.setText("");
    }

    @OnClick(R.id.vip_submit)
    public void submit(View v) {
        if (Util.isNull(zsdw_.getText() + "")) {
            Util.show("请输入辅导老师", this);
            return;
        }
        if (twoRow.getVisibility() == TableRow.VISIBLE
                && Util.isNull(twoPwd.getText() + "")) {
            Util.show("请输入交易密码!", this);
            return;
        }
        if (!cb_agree.isChecked()) {
            Util.show("请阅读并同意《城市经理合同》", this);
            return;
        }
        if (!Util.checkUserInfocomplete()){
            VoipDialog voipDialog = new
                    VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", Site_ZCFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.showIntent(Site_ZCFrame.this,
                            UpdateUserMessageActivity.class);
                }
            }, null);
            voipDialog.show();
            return;
        }





        final String ccTYpe = "6";
        final String payType_ = getPayType();
        final String money = wangdian.getText().toString().replace("￥", "");
        if ("1".equals(payType_) || "2".equals(payType_)) {
            if ("0".equals(UserData.getUser().getSecondPwd())) {
                VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(Site_ZCFrame.this, SetSencondPwdFrame.class);
                    }
                }, null);
                voipDialog.show();
                return;
            }
        }
        Util.asynTask(Site_ZCFrame.this, "正在处理，请稍等...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("网络错误，请重试！", Site_ZCFrame.this);
                    return;
                }
                if ("success".equals(runData + ""))
                    Util.showIntent("城市经理(众筹)申请成功！", Site_ZCFrame.this,
                            ProxySiteFrame.class);
                else if ((runData + "").contains("success:")) {
                    String[] orderId = (runData + "").split(":");
                    if ("4".equals(payType_))
                        aliPay(orderId[1], Double.parseDouble(orderId[2]));
                    else if ("6".equals(payType_))
                        wxPay(orderId[1], Double.parseDouble(orderId[2]));
                } else {
                    String msg = runData + "";
                    if ("您的充值账户余额不足！".equals(msg))
                        Util.showIntent("充值账户余额不足，是否前去充值？"
                                , Site_ZCFrame.this, AccountRecharge.class);
                    else
                        Util.show(msg, Site_ZCFrame.this);
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.upToSite_ZC, "userId="
                        + UserData.getUser().getUserId()
                        + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd()
                        + "&twoPwd="
                        + new MD5()
                        .getMD5ofStr(twoPwd.getText().toString())
                        + "&payType="
                        + getPayType()
                        + "&money="
                        + money
                        + "&zsdw="
                        + Util.get(zsdw_.getText()
                        .toString()) + "&zType=" + ccTYpe);
                return web.getPlan();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId,
                "手机客户端申请购物卡",
                Double.parseDouble(wangdian.getText().toString()),
                new AliPayCallBack() {
                    @Override
                    public void doSuccess(String aliResultCode) {
                        Util.showIntent("申请成功！是否前去查看申请记录！",
                                Site_ZCFrame.this,
                                WebSiteRequestRecordFrame.class);
                    }

                    @Override
                    public void doFailure(String aliResultCode) {
                        Util.showIntent("申请失败！支付宝状态码："
                                        + aliResultCode,
                                Site_ZCFrame.this,
                                WebSiteRequestRecordFrame.class);
                    }
                });
    }

    private void wxPay(final String orderId, final double money) {
        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(this, "微信支付中...");
        new MMPay(this, pd, money, orderId, "开通城市经理(众筹)").pay();
    }

}
