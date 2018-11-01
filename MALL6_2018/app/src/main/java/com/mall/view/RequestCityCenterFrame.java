package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.InviterInfo;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.PayType;
import com.mall.util.UserData;
import com.mall.util.Util;

import net.sourceforge.mm.MMPay;

import java.io.Serializable;

/**
 * 功能： 申请城市总监<br>
 * 时间： 2013-3-7<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class RequestCityCenterFrame extends Activity {

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
    private TextView wangdian11 = null;
    @ViewInject(R.id.request_site_two)
    private TableRow twoRow;
    private ImageButton selText;
    @ViewInject(R.id.siteType1)
    private ImageButton lanView;
    @ViewInject(R.id.siteType2)
    private ImageButton hongView;
    @ViewInject(R.id.siteType3)
    private ImageButton jinView;
    @ViewInject(R.id.request_site_name_line)
    private TextView txt;
    @ViewInject(R.id.request_site_name_line_line)
    private TableRow txt_line;
    @ViewInject(R.id.jindai)
    private TextView jindai;
    @ViewInject(R.id.jiameng)
    private TextView jiameng;
    @ViewInject(R.id.cb_agree)
    private CheckBox cb_agree;
    @ViewInject(R.id.tv_agree)
    private TextView tv_agree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.request_record_new_proxy);
        ViewUtils.inject(this);
        selText = lanView;
        selText.setTag(-7, "金钻店");
        this.findViewById(R.id.pay_item_sb_line).setVisibility(View.GONE);
        View xj = this.findViewById(R.id.pay_item_xj_line);
        View rec = this.findViewById(R.id.pay_item_rec_line);
//		rec.setVisibility(View.GONE);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        View ali = this.findViewById(R.id.pay_item_ali_line);
        PayType.addPayTypeListener(this, R.id.pay_item_weixin_line, twoRow, xj, rec, uppay, weixin, ali);
        uppay.setVisibility(View.GONE);
        twoRow.setVisibility(View.GONE);
        Spanned html1 = Html.fromHtml("我已阅读并同意<font color=\"#49afef\">《城市总监合同》"
                + "</font>");
        tv_agree.setText(html1);
    }

    @OnClick(R.id.tv_agree)
    public void agree(View v) {

        Util.showIntent(this, ProvisionActivity.class,
                new String[]{"RequestCityCenterFrame"},
                new String[]{"RequestCityCenterFrame"});

    }

    @Override
    public void onStart() {
        super.onStart();
        intComponent();
    }

    private void chanegeDrawable(int imageid, TextView view) {
        Drawable icon = this.getResources().getDrawable(imageid);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        view.setCompoundDrawables(icon, null, null, null);
    }

    @OnClick({R.id.jiameng, R.id.jindai})
    public void yanzheng(View v) {
        switch (v.getId()) {
            case R.id.jindai:
                chanegeDrawable(R.drawable.pay_item_checked, jindai);
                chanegeDrawable(R.drawable.pay_item_no_checked, jiameng);
                wangdian.setText("￥100000");
                break;

            case R.id.jiameng:
                chanegeDrawable(R.drawable.pay_item_checked, jiameng);
                chanegeDrawable(R.drawable.pay_item_no_checked, jindai);
                wangdian.setText("￥50000");
                break;
        }
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
                    if (Util.isNull(info.getUserid())) {
                        txt_line.setVisibility(View.GONE);
                        return;
                    }

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
                        txt_line.setVisibility(View.VISIBLE);
                        if (!lock) {
                            zsdw_.setEnabled(false);
                        }
                    } else {
                        txt.setText("用户资料： 　");
                        txt_line.setVisibility(View.GONE);
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
        Util.initTop(this, "申请〔城市总监〕", Integer.MIN_VALUE, null);
        if (null != UserData.getUser()) {
            getInviter(UserData.getUser().getInviter(), false);

            twoPwd = Util.getEditText(R.id.vip_twoPwd, this);
            zsdw_ = Util.getEditText(R.id.vip_zsdw, this);
            wangdian = Util.getTextView(R.id.vip_wangMoney, this);
            wangdian11 = Util.getTextView(R.id.vip_wangMoney11, this);

            wangdian.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

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
                        Util.show("获取账户余额失败，请充值！", RequestCityCenterFrame.this);
                        return;
                    }
                    pay_item_rec_ban.setText("余额:" + runData + "");
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.getMoney, "userid="
                            + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&type=rec");
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
            Util.show("请阅读并同意《城市总监合同》", this);
            return;
        }

        if (!Util.checkUserInfocomplete()){
            VoipDialog voipDialog = new
                    VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", RequestCityCenterFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.showIntent( RequestCityCenterFrame.this,
                            UpdateUserMessageActivity.class);
                }
            }, null);
            voipDialog.show();
            return;
        }



        final String ccTYpe = "6";
        final String payType_ = getPayType();
        final String money = wangdian11.getText().toString().replace("￥", "");
//		String requestTyte = "99800".equals(money) ? "0" : "1";
        String requestTyte = "29800".equals(money) ? "0" : "1";
        if ("1".equals(payType_) || "2".equals(payType_)) {
            if ("0".equals(UserData.getUser().getSecondPwd())) {
                VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消",
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Util.showIntent(RequestCityCenterFrame.this,
                                        SetSencondPwdFrame.class);
                            }
                        }, null);
                voipDialog.show();
                return;
            }
        }

        final CustomProgressDialog cpd = CustomProgressDialog
                .showProgressDialog(this, "正在申请城市总监...");
        User user = UserData.getUser();
        NewWebAPI.getNewInstance().requestCityDirector(user.getUserId(),
                user.getMd5Pwd(),
                new MD5().getMD5ofStr(twoPwd.getText().toString()),
                zsdw_.getText().toString(), requestTyte, payType_,
                new WebRequestCallBack() {

                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (Util.isNull(result)) {
                            Util.show("网络超时，请重试！", RequestCityCenterFrame.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        int code = json.getIntValue("code");
                        if (200 != code && 201 != code) {
                            Util.show(json.getString("message"),
                                    RequestCityCenterFrame.this);
                            return;
                        }
                        if (200 == code) {
                            Util.showIntent(RequestCityCenterFrame.this, Lin_MainFrame.class, new String[]{"toTab"}, new String[]{"usercenter"});
                            RequestCityCenterFrame.this.finish();
                        }
                        if (201 == code) {
                            if ("6".equals(payType_))
                                wxPay(json.getString("orderId"),
                                        json.getDoubleValue("money"));
                            else if ("4".equals(payType_))
                                aliPay(json.getString("orderId"),
                                        json.getDoubleValue("money"));
                            else
                                Util.show("无效的支付方式！",
                                        RequestCityCenterFrame.this);
                        }
                    }

                    @Override
                    public void fail(Throwable e) {
                        super.fail(e);
                        Util.show("申请失败！", RequestCityCenterFrame.this);
                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        cpd.cancel();
                        cpd.dismiss();
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

    private void wxPay(final String orderId, final double money) {
        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(this,
                "微信支付中...");
        new MMPay(this, pd, money, orderId, "申请城市总监").pay();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId, "申请城市总监", money, new AliPayCallBack() {
            @Override
            public void doSuccess(String aliResultCode) {
                Util.showIntent(RequestCityCenterFrame.this,
                        Lin_MainFrame.class);
                Util.show("城市总监申请成功！", RequestCityCenterFrame.this);
            }

            @Override
            public void doFailure(String aliResultCode) {
                Util.show("申请失败，支付宝状态码" + aliResultCode,
                        RequestCityCenterFrame.this);
            }
        });
    }

    @OnClick({R.id.siteType1, R.id.siteType2, R.id.siteType3})
    public void hong(View v) {
        selText = (ImageButton) v;
        lanView.setBackgroundDrawable(this.getResources().getDrawable(
                R.drawable.no_sel_lan));

        hongView.setBackgroundDrawable(this.getResources().getDrawable(
                R.drawable.no_sel_hong));

        jinView.setBackgroundDrawable(this.getResources().getDrawable(
                R.drawable.no_sel_jin));
        wangdian.setText(v.getTag() + "");

        if (R.id.siteType1 == v.getId()) {
            selText.setBackgroundDrawable(this.getResources().getDrawable(
                    R.drawable.sel_lan));
            selText.setTag(-7, "蓝钻店");
        }
        if (R.id.siteType2 == v.getId()) {
            selText.setBackgroundDrawable(this.getResources().getDrawable(
                    R.drawable.sel_hong));
            selText.setTag(-7, "红钻店");
        }
        if (R.id.siteType3 == v.getId()) {
            selText.setBackgroundDrawable(this.getResources().getDrawable(
                    R.drawable.sel_jin));
            selText.setTag(-7, "金钻店");
        }
    }
}
