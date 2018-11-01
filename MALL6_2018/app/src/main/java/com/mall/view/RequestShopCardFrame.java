package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
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
import java.io.UnsupportedEncodingException;

/**
 * 功能： 申请购物卡<br>
 * 时间： 2013-8-26<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class RequestShopCardFrame extends Activity {


    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;


    @ViewInject(R.id.siteType1)
    private TextView siteTypeText = null;
    @ViewInject(R.id.request_site_two_2)
    private TableRow row = null;
    @ViewInject(R.id.vip_twoPwd1)
    private EditText twoPwd = null;
    @ViewInject(R.id.vip_zsdw1)
    private EditText zsdw_ = null;
    @ViewInject(R.id.vip_wangMoney1)
    private EditText wangdian = null;
    @ViewInject(R.id.request_site_name_line)
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.request_shop_card_frame);
        ViewUtils.inject(this);
        this.findViewById(R.id.pay_item_sb_line).setVisibility(View.GONE);
        View ali = this.findViewById(R.id.pay_item_ali_line);
        View rec = this.findViewById(R.id.pay_item_rec_line);
        View xj = this.findViewById(R.id.pay_item_xj_line);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        PayType.addPayTypeListener(this, R.id.pay_item_uppay_line, row, ali, rec, xj, uppay, weixin, ali);
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("获取账户余额失败，请充值！", RequestShopCardFrame.this);
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

    @Override
    public void onStart() {
        super.onStart();
        intComponent();
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
                        zsdw_.setText(info.getUserid());
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
                            if (2 < Util.getInt(info.getLevel()) || 3 < Util.getInt(info.getShopType()) || "1".equals(info.getIsSite())) {
                                zsdw_.setEnabled(false);
                            }
                        }
                    } else {
                        txt.setText("用户资料： 　");
                        txt.setVisibility(View.GONE);
                    }
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
        Util.initTop(this, "申请〔远大购物卡〕", Integer.MIN_VALUE, null);
        if (null != UserData.getUser()) {
            getInviter(UserData.getUser().getInviter(), false);
            zsdw_.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        getInviter(zsdw_.getText().toString(), true);
                }
            });
            wangdian.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String value = wangdian.getText().toString();
                    if (Util.isInt(value) || Util.isDouble(value)) {
                        Double t = Double.parseDouble(value);
                        if (t < 100000) {
                            siteTypeText.setText("否");
                        } else {
                            siteTypeText.setText("是");
                        }

						
						/*if (t<1000) {
                            siteTypeText.setText("请不要小于1000");
						}
						else if (t >= 100000) {
							siteTypeText.setText("城市经理");
						}
						else {
							siteTypeText.setText("");
						} */
                    } else
                        siteTypeText.setText("格式错误！");
                }
            });
        } else {
            Util.showIntent("您还没登录！", this, LoginFrame.class);
        }
    }


    public String getPayType() {
        return PayType.getPayType(this);
    }


    @OnClick(R.id.vip_clear1)
    public void quiClick(View v) {
        twoPwd.setText("");
        zsdw_.setText("");
    }

    @OnClick(R.id.vip_submit1)
    public void subClick(View v) {
        if (null == UserData.getUser()) {
            Util.showIntent("您还没登录！", this, LoginFrame.class);
            return;
        }
        if (Util.isNull(zsdw_.getText() + "")) {
            Util.show("请输入辅导老师", RequestShopCardFrame.this);
            return;
        }

        if (Util.isNull(wangdian.getText().toString())) {
            Util.show("请输入购物卡金额", RequestShopCardFrame.this);
            return;
        }
        if (row.getVisibility() != TableRow.GONE
                && Util.isNull(twoPwd.getText() + "")) {
            Util.show("请输入交易密码!", RequestShopCardFrame.this);
            return;
        }
        if (!Util.checkUserInfocomplete()){
            VoipDialog voipDialog = new
                    VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", RequestShopCardFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.showIntent(RequestShopCardFrame.this,
                            UpdateUserMessageActivity.class);
                }
            }, null);
            voipDialog.show();
            return;
        }



        final String payType_ = getPayType();

        if ("1".equals(payType_) || "2".equals(payType_)) {
            if ("0".equals(UserData.getUser().getSecondPwd())) {
                VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(RequestShopCardFrame.this, SetSencondPwdFrame.class);
                    }
                }, null);
                voipDialog.show();
                return;
            }
        }
        Util.asynTask(this, "正在处理，请稍等...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("网络错误，请重试！", RequestShopCardFrame.this);
                    return;
                }
                if ("success".equals(runData + ""))
                    Util.showIntent("购物卡申请成功！", RequestShopCardFrame.this,
                            ProxySiteFrame.class);
                else if ((runData + "").contains("success:")) {
                    String[] values = (runData + "").split(":");
                    if ("4".equals(payType_))
                        aliPay(values[1], Double.parseDouble(values[2]));
                    else if ("6".equals(payType_))
                        wxPay(values[1], Double.parseDouble(values[2]));
                } else {
                    String msg = runData + "";
                    if ("您的充值账户余额不足！".equals(msg))
                        Util.showIntent("充值账户余额不足，是否前去充值？"
                                , RequestShopCardFrame.this, AccountRecharge.class);
                    else
                        Util.show(msg, RequestShopCardFrame.this);
                }
            }

            @Override
            public Serializable run() {
                Web web = null;
                try {
                    web = new Web(Web.requestSC, "userId="
                            + UserData.getUser().getUserId()
                            + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd()
                            + "&twoPwd="
                            + new MD5()
                            .getMD5ofStr(twoPwd.getText().toString())
                            + "&payType="
                            + payType_
                            + "&money="
                            + wangdian.getText().toString()
                            + "&zsdw="
                            + java.net.URLEncoder.encode(zsdw_.getText()
                            .toString(), "UTF-8") + "&zType=");//+ ccTYpe

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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

    private void wxPay(final String orderId, final double money) {
        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(this, "微信支付中...");
        new MMPay(this, pd, money, orderId, "申请购物卡").pay();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId,
                "手机客户端申请购物卡",
                Double.parseDouble(wangdian.getText().toString()),
                new AliPayCallBack() {
                    @Override
                    public void doSuccess(String aliResultCode) {
                        Util.showIntent("申请成功！是否前去查看申请记录！",
                                RequestShopCardFrame.this,
                                WebSiteRequestRecordFrame.class);
                    }

                    @Override
                    public void doFailure(String aliResultCode) {
                        Util.showIntent("申请失败！支付宝状态码："
                                        + aliResultCode,
                                RequestShopCardFrame.this,
                                WebSiteRequestRecordFrame.class);
                    }
                });
    }
}
