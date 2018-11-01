package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnFocusChange;
import com.lin.component.CustomProgressDialog;
import com.mall.model.InviterInfo;
import com.mall.model.checkisyunnanmodel;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.PayType;
import com.mall.util.UserData;
import com.mall.util.Util;

import net.sourceforge.mm.MMPay;

import java.io.Serializable;
import java.util.List;

/**
 * 功能： 申请联盟商家<br>
 * 时间： 2013-12-28<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class RequestAllianceFrame extends Activity {

    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;

    @ViewInject(R.id.alliance_zsdw)
    private EditText szdw;
    @ViewInject(R.id.alliance_two)
    private EditText two;
    @ViewInject(R.id.alliance_money)
    private EditText money;
    @ViewInject(R.id.alliance_twoLine____1)
    private TableRow alliance_twoLine;
    @ViewInject(R.id.lmsj_payMoney_1)
    private TableRow lmsj_payMoney_1;
    @ViewInject(R.id.request_alliance_name_line_row)
    private TableRow row;
    @ViewInject(R.id.request_alliance_name_line)
    private TextView txt;

    @ViewInject(R.id.jindai)
    private TextView jindai;
    @ViewInject(R.id.jiameng)
    private TextView jiameng;
    private int payMoney = 6;

    @ViewInject(R.id.cb_agree)
    private CheckBox cb_agree;
    @ViewInject(R.id.tv_agree)
    private TextView tv_agree;
    @ViewInject(R.id.line1)
    private View line1;

    private Context context;


    private boolean isuseryunnan = false;
    private boolean ischuangke = false;

    @OnFocusChange(R.id.alliance_zsdw)
    public void onBlur(View v, boolean hasFour) {
        if (!hasFour)
            getInviter(szdw.getText().toString(), true);
    }

    @OnFocusChange({R.id.lmsj_type_chongzhi, R.id.lmsj_type_kaihu})
    public void onFour(View view) {
        if (view.getId() == R.id.lmsj_type_kaihu) {
            lmsj_payMoney_1.setVisibility(View.GONE);
            money.setText("980");
            payMoney = 6;
            money.setEnabled(false);
        } else {
            money.setText("1000");
            money.setEnabled(true);
            payMoney = 3;
            lmsj_payMoney_1.setVisibility(View.VISIBLE);
        }
    }

    public String getPayType() {
        return PayType.getPayType(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.findViewById(R.id.pay_item_sb_line).setVisibility(View.GONE);
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
                        szdw.setText(info.getUserid());
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
                        txt.setText("用户资料：  " + name + "　" + phone);
                        txt.setVisibility(View.VISIBLE);
                        row.setVisibility(View.VISIBLE);
//						if(!lock){
                        ischuangke = true;
                        szdw.setEnabled(false);
                        checkUserIsyunnan();
//						}else{
//						}
                    } else {
                        ischuangke = false;
                        checkUserIsyunnan();
                    }
                } else {
                    ischuangke = false;
                    txt.setText("用户资料：  　");
                    txt.setVisibility(View.GONE);
                    row.setVisibility(View.GONE);
                    checkUserIsyunnan();

                }
            }

            // 申请方式1在线申请后台审批，2充值账户支付 3网银支付 4支付宝支付
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        this.setContentView(R.layout.request_alliance_frame);
        ViewUtils.inject(this);
        Util.initTop(this, "申请〔商圈系统〕", Integer.MIN_VALUE, null);
        if (null == UserData.getUser()) {
            Util.showIntent("对不起，您还没登录，请先登录", this, LoginFrame.class);
            return;
        }


        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("获取账户余额失败，请重试！", RequestAllianceFrame.this);
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


        getInviter(UserData.getUser().getInviter(), false);


        View rec = this.findViewById(R.id.pay_item_rec_line);
//		rec.setVisibility(View.GONE);
        View xj = this.findViewById(R.id.pay_item_xj_line);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        View ali = this.findViewById(R.id.pay_item_ali_line);
        PayType.addPayTypeListener(this, R.id.pay_item_weixin_line, alliance_twoLine, rec, xj, uppay, weixin, ali);


        uppay.setVisibility(View.GONE);
        alliance_twoLine.setVisibility(View.GONE);
        money.setText("");
        payMoney = 3;
        money.setEnabled(true);
        Spanned html1 = Html.fromHtml("我已阅读并同意<font color=\"#49afef\">《联盟商家合同》"
                + "</font>");
        tv_agree.setText(html1);
    }

    @OnClick(R.id.tv_agree)
    public void agree(View v) {

        Util.showIntent(this, ProvisionActivity.class,
                new String[]{"RequestAllianceFrame"},
                new String[]{"RequestAllianceFrame"});

    }

    @OnClick({R.id.jiameng, R.id.jindai})
    public void yanzheng(View v) {
        switch (v.getId()) {
            case R.id.jindai:
                chanegeDrawable(R.drawable.pay_item_checked, jindai);
                chanegeDrawable(R.drawable.pay_item_no_checked, jiameng);
                money.setText("980");
                payMoney = 6;
                money.setEnabled(false);
                break;

            case R.id.jiameng:
                chanegeDrawable(R.drawable.pay_item_checked, jiameng);
                chanegeDrawable(R.drawable.pay_item_no_checked, jindai);
                money.setText("");
                payMoney = 3;
                money.setEnabled(true);

                break;
        }
    }

    private void chanegeDrawable(int imageid, TextView view) {
        Drawable icon = this.getResources().getDrawable(imageid);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        view.setCompoundDrawables(icon, null, null, null);
    }

    @OnClick(R.id.alliance_submit)
    public void submitClick(View v) {
        if (Util.isNull(szdw.getText().toString())) {
            Util.show("请填写您的辅导老师。", this);
            return;
        }
        if (Util.isNull(money.getText().toString())) {
            Util.show("对不起，请输入充值金额！", this);
            return;
        }
        final String moneyS = money.getText().toString().replace("￥", "");
        if (!moneyS.matches("^\\d+$")) {
            Util.show("对不起，充值金额错误！", this);
            return;
        }
        if (1000 > Integer.parseInt(moneyS) && 3 == payMoney) {
            Util.show("对不起，充值金额请大于1000！", this);
            return;
        }
        final String payType_ = getPayType();
        if (("1".equals(payType_) || "2".equals(payType_))
                && Util.isNull(two.getText().toString())) {
            Util.show("请输入您的交易密码！", this);
            return;
        }
        if (!cb_agree.isChecked()) {
            Util.show("请阅读并同意《联盟商家合同》", this);
            return;
        }
        if (!Util.checkUserInfocomplete()){
            VoipDialog voipDialog = new
                    VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", context, "立即登记", "稍后登记", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.showIntent(context,
                            UpdateUserMessageActivity.class);
                }
            }, null);
            voipDialog.show();
            return;
        }



        if ("1".equals(payType_) || "2".equals(payType_)) {
            if ("0".equals(UserData.getUser().getSecondPwd())) {
                VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(RequestAllianceFrame.this, SetSencondPwdFrame.class);
                    }
                }, null);
                voipDialog.show();
                return;
            }
        }
        Util.asynTask(this, "正在提交您的申请", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null != runData) {
                    if ("success".equals(runData + "")) {
                        Util.show("申请成功！", RequestAllianceFrame.this);
                        Util.showIntent(RequestAllianceFrame.this, Lin_MainFrame.class, new String[]{"toTab"}, new String[]{"usercenter"});
                    } else if ((runData + "").contains("success:ali:")) {
                        String[] info = (runData + "").split(":");
                        new AliPayNet(RequestAllianceFrame.this).pay(info[2],
                                "申请联盟商家",
                                Util.getDouble(money.getText().toString()),
                                new AliPayCallBack() {
                                    @Override
                                    public void doSuccess(String aliResultCode) {
                                        Util.show("申请成功！",
                                                RequestAllianceFrame.this);
                                        Util.showIntent(
                                                RequestAllianceFrame.this,
                                                UserCenterFrame.class);
                                    }

                                    @Override
                                    public void doFailure(String aliResultCode) {
                                        Util.show("申请失败，支付宝状态码："
                                                        + aliResultCode,
                                                RequestAllianceFrame.this);
                                    }
                                });
                    } else if ((runData + "").contains("success:wx:")) {
                        final String[] info = (runData + "").split(":");
                        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(RequestAllianceFrame.this, "微信支付中...");
                        new MMPay(RequestAllianceFrame.this, pd, Util.getDouble((money.getText()
                                .toString())), info[2], "申请联盟商家").pay();
                    } else {
                        String msg = runData + "";
                        if ("您的充值账户余额不足！".equals(msg))
                            Util.showIntent("充值账户余额不足，是否前去充值？"
                                    , RequestAllianceFrame.this, AccountRecharge.class);
                        else
                            Util.show(msg, RequestAllianceFrame.this);
                    }
                } else
                    Util.show("网络错误，请重试！", RequestAllianceFrame.this);
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.requestAlliance, "userid="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&payType="
                        + payType_ + "&twoPwd="
                        + new MD5().getMD5ofStr(two.getText().toString())
                        + "&inviter=" + Util.get(szdw.getText().toString())
                        + "&money=" + moneyS + "&payMoney=" + payMoney);
                return web.getPlan();
            }
        });
    }

    @OnClick(R.id.alliance_clear)
    public void clearClicl(View v) {
        if (szdw.isEnabled())
            szdw.setText("");
        two.setText("");
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


    public void checkUserIsyunnan() {

        final CustomProgressDialog cpd = Util.showProgress("加载中...", this);
//        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=GetRedBoxInfo&userId="
        NewWebAPI.getNewInstance().getWebRequest("/red_box_v2.aspx?call=check_user_is_yunnan&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd(),
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @Override
                    public void success(Object result) {

                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }

                        Log.e("是否是云南", result.toString() + "KK");
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }
                        Gson gson = new Gson();

                        checkisyunnanmodel check = gson.fromJson(result.toString(), checkisyunnanmodel.class);
                        if (check.getList() != null && check.getList().size() != 0) {
                            List<checkisyunnanmodel.ListBean> listBeans = check.getList();
                            if (!listBeans.get(0).getINVITER_ZONEID().equals("0")) {
                                Log.e("邀请人 和 被邀请人", "都是云南的");
                                isuseryunnan = true;
                                line1.setVisibility(View.GONE);
                                row.setVisibility(View.GONE);
                                if (ischuangke) { // 是创客

                                } else {
                                    szdw.setText("远大商城");
                                }

                            }
                        }
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });


    }
}
