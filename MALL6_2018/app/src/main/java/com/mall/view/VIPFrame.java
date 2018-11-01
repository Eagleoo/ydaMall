package com.mall.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import net.sourceforge.mm.MMPay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.CustomProgressDialog;
import com.mall.model.InviterInfo;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.PayType;
import com.mall.util.PayTypeClickCallback;
import com.mall.util.UserData;
import com.mall.util.Util;

public class VIPFrame extends Activity {

    @ViewInject(R.id.pay_item_sb_radio)
    private ImageView angel_requestSB1;
    @ViewInject(R.id.pay_item_sb_ban)
    private TextView pay_item_sb_ban;
    @ViewInject(R.id.pay_item_rec_radio)
    private ImageView angel_requestCZ1;
    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;


    @ViewInject(R.id.vip_two_line)
    private TableRow vip_two_line;
    @ViewInject(R.id.vip_name_2_2)
    private TableRow vip_name_2_2;
    @ViewInject(R.id.request_vip_line_name)
    private TextView request_vip_line_name;

    private EditText v_zsdw = null;
    @ViewInject(R.id.vip_money)
    private TextView vip_money;
    private EditText twoPwd = null;
    private Button sub = null;
    private Button cle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.request_vip);
        ViewUtils.inject(this);
        initComponent();
        init();
        this.findViewById(R.id.pay_item_xj_line).setVisibility(View.GONE);
        View rec = this.findViewById(R.id.pay_item_rec_line);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);
        View sb = this.findViewById(R.id.pay_item_sb_line);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        PayType.addPayTypeListener(new PayTypeClickCallback() {
            @Override
            public void click(View v) {
                if (v.getId() == R.id.pay_item_ali_line
                        || v.getId() == R.id.pay_item_uppay_line
                        || v.getId() == R.id.pay_item_weixin_line) {
                    vip_two_line.setVisibility(View.GONE);
                } else {
                    vip_two_line.setVisibility(View.VISIBLE);
                    if ("0".equals(UserData.getUser().getSecondPwd())) {
                        VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", VIPFrame.this, "确定", "取消", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Util.showIntent(VIPFrame.this, SetSencondPwdFrame.class);
                            }
                        }, null);
                        voipDialog.show();
                    }
                }
                if (R.id.pay_item_sb_line == v.getId()) {
                    angel_requestSB1.setImageResource(R.drawable.pay_item_checked);
                    angel_requestSB1.setTag("checked");
                    vip_money.setText("380.00");
                } else
                    vip_money.setText("38.00");
            }
        }, this, R.id.pay_item_rec_line, vip_two_line, rec, sb, uppay, weixin);
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("获取账户余额失败，请充值！", VIPFrame.this);
                    return;
                }
                HashMap<String, String> map = (HashMap<String, String>) runData;
                pay_item_rec_ban.setText("余额:" + map.get("rec") + "");
                pay_item_sb_ban.setText("余额:" + map.get("sb") + "");
            }

            @Override
            public Serializable run() {
                Web recWeb = new Web(Web.getMoney,
                        "userid=" + UserData.getUser().getUserId()
                                + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                                + "&type=rec");
                Web sbWeb = new Web(Web.getMoney,
                        "userid=" + UserData.getUser().getUserId()
                                + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                                + "&type=sb");
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("rec", recWeb.getPlan());
                map.put("sb", sbWeb.getPlan());
                return map;
            }
        });
    }

    public String getPayType() {
        return PayType.getPayType(this);
    }

    private void getInviter(final String name) {
        Util.asynTask(this, "正在获取招商信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null != runData) {
                    InviterInfo info = (InviterInfo) runData;
                    if (Util.getInt(info.getLevel()) > 2
                            || Util.getInt(info.getShopType()) >= 3) {
                        v_zsdw.setText(info.getUserid());
                        vip_name_2_2.setVisibility(View.VISIBLE);
                        request_vip_line_name.setVisibility(View.VISIBLE);
                        String name = info.getName();
                        if (!Util.isNull(name))
                            name = name.substring(0, 1) + "**";
                        String phone = info.getPhone();
                        if (!Util.isNull(phone))
                            phone = phone.substring(0, 3)
                                    + "****"
                                    + phone.subSequence(phone.length() - 4,
                                    phone.length());
                        request_vip_line_name.setText("姓名：" + name
                                + "\t\t\t手机：" + phone);
                    } else {
                        vip_name_2_2.setVisibility(View.GONE);
                        request_vip_line_name.setVisibility(View.GONE);
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

    private void init() {
        if (null == UserData.getUser()) {
            Util.showIntent("请先登录！", this, LoginFrame.class);
            return;
        }

        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, String> map = (HashMap<String, String>) runData;
                pay_item_rec_ban.setText("余额:" + map.get("rec") + "");
                pay_item_sb_ban.setText("余额:" + map.get("sb") + "");
            }

            @Override
            public Serializable run() {
                Web recWeb = new Web(Web.getMoney,
                        "userid=" + UserData.getUser().getUserId()
                                + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                                + "&type=rec");
                Web sbWeb = new Web(Web.getMoney,
                        "userid=" + UserData.getUser().getUserId()
                                + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                                + "&type=sb");
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("rec", recWeb.getPlan());
                map.put("sb", sbWeb.getPlan());
                return map;
            }
        });


        getInviter(UserData.getUser().getInviter());
        v_zsdw.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    getInviter(v_zsdw.getText().toString());
            }
        });
        sub.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String payType_ = getPayType();
                if ("1".equals(payType_) || "2".equals(payType_)) {
                    if ("0".equals(UserData.getUser().getSecondPwd())) {
                        VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", VIPFrame.this, "确定", "取消", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Util.showIntent(VIPFrame.this, SetSencondPwdFrame.class);
                            }
                        }, null);
                        voipDialog.show();
                        return;
                    }
                }
                if (Util.isNull(v_zsdw.getText().toString())) {
                    Util.show("请输入辅导老师！", VIPFrame.this);
                    return;
                } else if (Util.isNull(twoPwd.getText().toString())
                        && ("checked".equals(angel_requestSB1.getTag() + "") || "checked"
                        .equals(angel_requestCZ1.getTag() + ""))) {
                    Util.show("请输入交易密码！", VIPFrame.this);
                    return;
                } else {
                    Util.asynTask(VIPFrame.this, "正在申请VIP...", new IAsynTask() {
                        @Override
                        public void updateUI(Serializable runData) {
                            if (null == runData) {
                                Util.show("网络错误，请重试！", VIPFrame.this);
                                return;
                            }
                            if ("success".equals(runData + "")) {
                                Util.showIntent("操作成功，您已成为VIP会员！", VIPFrame.this, UserCenterFrame.class
                                        , Lin_MainFrame.class);
                            } else if ((runData + "").contains("success:")) {
                                String[] ayy = (runData + "").split(":");
                                if ("4".equals(payType_))
                                    aliPay(ayy[1], Double.parseDouble(ayy[2]));
                                else if ("6".equals(payType_))
                                    wxPay(ayy[1], Double.parseDouble(ayy[2]));
                            } else {
                                String msg = runData + "";
                                if ("充值账户余额不足！".equals(msg))
                                    Util.showIntent("充值账户余额不足，是否前去充值？"
                                            , VIPFrame.this, AccountRecharge.class);
                                else
                                    Util.show(msg, VIPFrame.this);
                            }
                        }

                        @Override
                        public Serializable run() {
                            Web web = new Web(Web.upToVip, "userId="
                                    + UserData.getUser().getUserId()
                                    + "&md5Pwd="
                                    + UserData.getUser().getMd5Pwd()
                                    + "&zsdw="
                                    + Util.get(v_zsdw.getText().toString())
                                    + "&twoPwd="
                                    + new MD5().getMD5ofStr(twoPwd.getText()
                                    .toString()) + "&payType="
                                    + payType_);
                            return web.getPlan();
                        }
                    });
                }
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
        new MMPay(this, pd, money, orderId, "申请VIP").pay();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId, "申请VIP会员", money,
                new AliPayCallBack() {
                    @Override
                    public void doSuccess(String aliResultCode) {
                        Util.show("申请成功！", VIPFrame.this);
                    }

                    @Override
                    public void doFailure(String aliResultCode) {
                        Util.show("申请失败，支付宝状态码：" + aliResultCode, VIPFrame.this);
                    }
                });
    }

    private void initComponent() {
        Util.initTop(this, "申请VIP", Integer.MIN_VALUE, null);

        v_zsdw = Util.getEditText(R.id.vip_zsdw, this);
        twoPwd = Util.getEditText(R.id.vip_twoPwd, this);
        sub = Util.getButton(R.id.vip_submit, this);
        cle = Util.getButton(R.id.vip_clear, this);
        cle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v_zsdw.setText("");
                twoPwd.setText("");
            }
        });
    }

}
