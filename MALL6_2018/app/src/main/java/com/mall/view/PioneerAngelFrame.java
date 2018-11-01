package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.InviterInfo;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.PayType;
import com.mall.util.UserData;
import com.mall.util.Util;

import net.sourceforge.mm.MMPay;

import java.io.Serializable;

public class PioneerAngelFrame extends Activity {

    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;

    @ViewInject(R.id.request_angel_name_line)
    private TableRow request_angel_name_line;
    @ViewInject(R.id.request_angel_name)
    private TextView request_angel_name;
    @ViewInject(R.id.angel_show_money)
    private TextView show_Money;
    @ViewInject(R.id.angel_zsdw1)
    private EditText angel_zsdw1;
    @ViewInject(R.id.angel_wangMoney1)
    private TextView angel_wangMoney1;

    @ViewInject(R.id.angel_twoPwd1)
    private EditText angel_twoPwd1;
    @ViewInject(R.id.request_angel_two_table)
    private TableLayout request_angel_two_table;

    private float bao_money = 30000.00F;
    @ViewInject(R.id.cb_agree)
    private CheckBox cb_agree;
    @ViewInject(R.id.tv_agree)
    private TextView tv_agree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.pioneer_angel_frame);
        ViewUtils.inject(this);
        this.findViewById(R.id.pay_item_sb_line).setVisibility(View.GONE);
        View xj = this.findViewById(R.id.pay_item_xj_line);
        View rec = this.findViewById(R.id.pay_item_rec_line);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        View ali = this.findViewById(R.id.pay_item_ali_line);
        PayType.addPayTypeListener(this, R.id.pay_item_uppay_line, request_angel_two_table, xj, rec, uppay, weixin, ali);
        init();

        Spanned html1 = Html.fromHtml("我已阅读并同意<font color=\"#49afef\">《创业大使合同》"
                + "</font>");
        tv_agree.setText(html1);
    }

    @OnClick(R.id.tv_agree)
    public void agree(View v) {

        Util.showIntent(this, ProvisionActivity.class,
                new String[]{"PioneerAngelFrame"},
                new String[]{"PioneerAngelFrame"});

    }

    private void init() {
        Util.initTop(this, "申请〔创业大使〕", Integer.MIN_VALUE, null);
        User user = UserData.getUser();
        if (null == user) {
            Util.showIntent("对不起，请先登录！", this, LoginFrame.class);
            return;
        }
        if (!"远大商城".equals(user.getInviter()))
            getInviter(user.getInviter(), false);
        angel_zsdw1.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getInviter(angel_zsdw1.getText().toString(), true);
                }
            }
        });

        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("获取账户余额失败，请充值！", PioneerAngelFrame.this);
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


    public String getPayType() {
        return PayType.getPayType(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        User user = UserData.getUser();
        if (null == user) {
            Util.showIntent(this, Lin_MallActivity.class);
        }
    }

    @OnClick(R.id.angel_submit1)
    public void submitClick(View v) {
        final String zsdw = angel_zsdw1.getText().toString();
        final String twopwd = angel_twoPwd1.getText().toString();
        if (Util.isNull(zsdw)) {
            Util.show("辅导老师不能为空！", this);
            return;
        }
        final String payType_ = getPayType();
        if (Util.isNull(twopwd) && ("1".equals(payType_) || "2".equals(payType_))) {
            Util.show("请输入交易密码！", this);
            return;
        }
        if (!cb_agree.isChecked()) {
            Util.show("请阅读并同意《创业大使合同》", this);
            return;
        }

        if (!Util.checkUserInfocomplete()){
            VoipDialog voipDialog = new
                    VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", PioneerAngelFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.showIntent( PioneerAngelFrame.this,
                            UpdateUserMessageActivity.class);
                }
            }, null);
            voipDialog.show();
            return;
        }


        if ("1".equals(payType_) || "2".equals(payType_)) {
            if ("0".equals(UserData.getUser().getSecondPwd())) {
                VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消",  new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(PioneerAngelFrame.this, SetSencondPwdFrame.class);
                    }
                },null);
                voipDialog.show();
                return;
            }
        }
        Util.asynTask(this, "正在提交您的申请...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (Util.isNull(runData)) {
                    Util.show("网络错误，申请失败！", PioneerAngelFrame.this);
                    return;
                }
                if ("success".equals(runData + "")) {
                    Util.showIntent("申请成功！", PioneerAngelFrame.this,
                            UserCenterFrame.class);
                } else if ((runData + "").contains("success:")) {
                    final String[] ayy = (runData + "").split(":");
                    if ("4".equals(payType_)) {
                        new AliPayNet(PioneerAngelFrame.this).pay(ayy[1],
                                "用户申请创业大使", Double.parseDouble(ayy[2]),
                                new AliPayCallBack() {
                                    @Override
                                    public void doSuccess(
                                            String aliResultCode) {
                                        Util.showIntent("支付成功,请重新登录查看！",
                                                PioneerAngelFrame.this,
                                                LoginFrame.class);
                                    }

                                    @Override
                                    public void doFailure(
                                            String aliResultCode) {
                                        Util.show("支付失败：支付宝状态码"
                                                        + aliResultCode,
                                                PioneerAngelFrame.this);
                                    }
                                });
                    } else if ("6".equals(payType_)) {
                        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(PioneerAngelFrame.this, "微信支付中...");
                        new MMPay(PioneerAngelFrame.this, pd, Double.parseDouble(ayy[2]), ayy[1], "申请创业大使").pay();
                    }
                } else {
                    String msg = runData + "";
                    if ("您的充值账户余额不足！".equals(msg))
                        Util.showIntent("充值账户余额不足，是否前去充值？"
                                , PioneerAngelFrame.this, AccountRecharge.class);
                    else
                        Util.show(msg, PioneerAngelFrame.this);
                }
            }

            @Override
            public Serializable run() {
                User user = UserData.getUser();
                Web web = new Web(Web.requestPoititionAngel, "userid="
                        + user.getUserId() + "&md5Pwd=" + user.getMd5Pwd()
                        + "&twoPwd=" + new MD5().getMD5ofStr(twopwd)
                        + "&packageNum="
                        + angel_wangMoney1.getText().toString() + "&szdw="
                        + Util.get(zsdw) + "&payMoneyType=" + payType_);
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
            SharedPreferences sharedata = getSharedPreferences("data", 0);
            sharedata.edit().clear().commit();
        } else if ("fail".equalsIgnoreCase(str)) {
            msg = "支付失败！";
        } else if ("cancel".equalsIgnoreCase(str)) {
            msg = "用户取消";
        }
        if ("success".equalsIgnoreCase(str)) {
            Util.showIntent("支付成功。", this,
                    Lin_MallActivity.class);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("支付结果通知");
            builder.setMessage(msg);
            builder.setInverseBackgroundForced(true);
            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
    }

    @OnClick(R.id.angel_clear1)
    public void clearClick(View v) {
        v.setEnabled(false);
        angel_zsdw1.setEnabled(true);
        angel_zsdw1.setText("");
        request_angel_name.setVisibility(View.GONE);
        request_angel_name_line.setVisibility(View.GONE);
        int num = 1;
        angel_wangMoney1.setText(num + "");
        show_Money.setText((num * bao_money) + "");
        angel_twoPwd1.setText("");
        v.setEnabled(true);
    }

    private void getInviter(final String name, final boolean lock) {
        Util.asynTask(this, "正在获取招商信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null != runData) {
                    InviterInfo info = (InviterInfo) runData;
                    if (Util.getInt(info.getLevel()) == 5 || "1".equals(info.getIsSite()) || "远大商城".equals(info.getUserid())) {
                        angel_zsdw1.setText(info.getUserid());
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
                        request_angel_name.setText("用户资料： " + name + "　" + phone);
                        request_angel_name.setVisibility(View.VISIBLE);
                        request_angel_name_line.setVisibility(View.VISIBLE);
                        if (!lock) {
                            if (5 == Util.getInt(info.getLevel()) || (3 < Util.getInt(info.getShopType()) && 10 > Util.getInt(info.getShopType()))) {
                                angel_zsdw1.setEnabled(false);
                            }
                        }
                    } else {
                        request_angel_name.setText("用户资料： 　");
                        request_angel_name.setVisibility(View.GONE);
                        request_angel_name_line.setVisibility(View.GONE);
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

    @OnClick(R.id.angel_jian)
    public void jianClick(View v) {
        v.setEnabled(false);
        String value = angel_wangMoney1.getText().toString();
        int num = Integer.parseInt(value);
        if (1 < num) {
            num--;
            angel_wangMoney1.setText(num + "");
            show_Money.setText((num * bao_money) + "");
        }
        v.setEnabled(true);
    }

    @OnClick(R.id.angel_jia)
    public void jiaClick(View v) {
        v.setEnabled(false);
        String value = angel_wangMoney1.getText().toString();
        int num = Integer.parseInt(value);
        num++;
        angel_wangMoney1.setText(num + "");
        show_Money.setText((num * bao_money) + "");
        v.setEnabled(true);
    }
}
