package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.example.view.VideoAudioDialog;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
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

public class SiteFrame extends Activity {

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
    private TextView wangdian1 = null;
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

    @ViewInject(R.id.cb_agree)
    private CheckBox cb_agree;
    @ViewInject(R.id.tv_agree)
    private TextView tv_agree;

    @ViewInject(R.id.site_yucunMoney)
    private TextView site_yucunMoney;

    private String type1 = "";
    String ccTYpe = "";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.request_site);
        ViewUtils.inject(this);
        context = this;

        checkIsyunnan();

        type1 = getIntent().getStringExtra("type") + "";
        if (type1 == null || type1.equals("null")) {
            type1 = "";
        }
        selText = lanView;
        selText.setTag(-7, "金钻店");
        this.findViewById(R.id.pay_item_sb_line).setVisibility(View.GONE);
        View xj = this.findViewById(R.id.pay_item_xj_line);
        View rec = this.findViewById(R.id.pay_item_rec_line);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        View ali = this.findViewById(R.id.pay_item_ali_line);
//		PayType.addPayTypeListener(this, R.id.pay_item_uppay_line, twoRow, xj,
        PayType.addPayTypeListener(this, R.id.pay_item_weixin_line, twoRow, xj,
                rec, uppay, weixin, ali);
//				rec.setVisibility(View.GONE);
        uppay.setVisibility(View.GONE);
        twoRow.setVisibility(View.GONE);

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

    int levelId = 0;

    private void getInviter(final String name, final boolean lock) {
        Util.asynTask(this, "正在获取招商信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null != runData) {
                    InviterInfo info = (InviterInfo) runData;
                    levelId = Util.getInt(info.getLevel());
                    int shopId = Util.getInt(info.getShopType());
                    boolean isSite = "1".equals(info.getIsSite());
                    if (5 == levelId || isSite) {
                        zsdw_.setText(Util.getNo_pUserId(info.getUserid()));
                        String name = info.getName();
                        if (!Util.isNull(name) && 2 <= name.length()) {
                            name = name.substring(0, 1) + "*";
                            if (3 <= info.getName().length())
                                name += info.getName().substring(
                                        info.getName().length() - 1);
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
                    } else {
                        try {
                            if (info.getUserid().equals("远大云商") || info.getUserid().equals("远大商城")) {

                            } else {
                                Util.show("该用户不能做为辅导老师！", SiteFrame.this);
                                txt.setText("用户资料： 　");
                                txt.setVisibility(View.GONE);
                                zsdw_.setEnabled(true);
                                if (!Util.isNull(info.getUserid()))
                                    zsdw_.setText(info.getUserid());
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                        }

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


    boolean isyunnan = false;

    public void checkIsyunnan() {
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
                            isyunnan = true;
                            wangdian1.setText("￥" + 2980);
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

    private void intComponent() {
        if (type1.equals("2680")) {
            Util.initTop(this, "申请〔幸福大中华双创计划〕", Integer.MIN_VALUE, null);
        } else {
            Util.initTop(this, "申请〔城市经理〕", Integer.MIN_VALUE, null);
        }

        if (null != UserData.getUser()) {
            getInviter(UserData.getUser().getInviter(), false);

            twoPwd = Util.getEditText(R.id.vip_twoPwd, this);
            zsdw_ = Util.getEditText(R.id.vip_zsdw, this);
            wangdian = Util.getTextView(R.id.vip_wangMoney, this);
            wangdian1 = Util.getTextView(R.id.vip_wangMoney1, this);

            if (type1.equals("2680")) {
                wangdian.setText("2680");
                site_yucunMoney.setText("志愿者费用：");
            } else {
                wangdian.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }


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
                        Util.show("获取账户余额失败，请充值！", SiteFrame.this);
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
        if (Util.isNull(wangdian.getText().toString())) {
            Util.show("请选择购买城市经理类型", this);
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



        String type = "4";
        if ("蓝钻店".equals(selText.getTag(-7) + ""))
            type = "4";
        else if ("红钻店".equals(selText.getTag(-7) + ""))
            type = "5";
        else if ("金钻店".equals(selText.getTag(-7) + ""))
            type = "6";

        ccTYpe = "6";

        if (type1.equals("2680")) {
            ccTYpe = "16";
        }
        final String payType_ = getPayType();
        final String money = wangdian1.getText().toString().replace("￥", "");
        if ("1".equals(payType_) || "2".equals(payType_)) {
            if ("0".equals(UserData.getUser().getSecondPwd())) {
                VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(SiteFrame.this,
                                SetSencondPwdFrame.class);
                    }
                }, null);
                voipDialog.show();
                return;
            }
        }
        Util.asynTask(SiteFrame.this, "正在处理，请稍等...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("网络错误，请重试！", SiteFrame.this);
                    return;
                }
                Log.e("申请", "1");
                if ("success".equals(runData + "")) {
                    Log.e("申请", "2");
                    if (type1.equals("2680")) {
                        Log.e("申请", "3");

                        VideoAudioDialog dialog = new VideoAudioDialog(context);
                        dialog.setLeft("返回首页").setRight("进入会员中心");
                        dialog.setTitle("提     示");
                        dialog.setContent("恭喜你成功申请幸福大中华志愿者双创计划，欢迎你加盟远大云商创业服务平台！");

                        dialog.setLeft(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
//								Util.openWeb(VoiceDetailActivity.this, urlOrPath.toString());
//								Intent intent =new Intent(context,Lin_MainFrame.class);
//								startActivity(intent);
//								
//								finish();
                                Util.showIntent(context, UserCenterFrame.class);
                                finish();

                            }
                        });
                        dialog.setRight(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                // TODO Auto-generated method stub
                                Util.showIntent(context, Lin_MainFrame.class);
                                finish();
                            }
                        });
                        dialog.show();

                    } else {
                        Log.e("申请", "4");
                        Util.showIntent(SiteFrame.this, Lin_MainFrame.class, new String[]{"toTab"}, new String[]{"usercenter"});
//						Intent intent =new Intent(SiteFrame.this,ProxySiteFrame.class);
//						startActivity(intent);
                        Toast.makeText(context, "城市经理申请成功！", Toast.LENGTH_LONG).show();
                        finish();
                    }

                } else if ((runData + "").contains("success:")) {
                    Log.e("申请", "5");
                    String[] orderId = (runData + "").split(":");


                    if ("4".equals(payType_))
                        aliPay(orderId[1], Double.parseDouble(orderId[2]));
                    else if ("6".equals(payType_))
                        wxPay(orderId[1], Double.parseDouble(orderId[2]));
                } else {
                    Log.e("申请", "6");
                    String msg = runData + "";
                    if ("您的充值账户余额不足！".equals(msg)) {
                        Util.showIntent("充值账户余额不足，是否前去充值？", SiteFrame.this,
                                AccountRecharge.class);
                    } else {
//						if (type1.equals("2680")) {
//							Log.e("申请","3");
//						
//							VideoAudioDialog dialog = new VideoAudioDialog(context);
//							dialog.setLeft("返回首页").setRight("进入会员中心");
//							dialog.setTitle("提     示");
//							dialog.setContent("恭喜你成功申请幸福大中华志愿者双创计划，欢迎你加盟远大云商创业服务平台！");
//							
//							dialog.setLeft(new OnClickListener() {
//								@Override
//								public void onClick(View v) {
////									Util.openWeb(VoiceDetailActivity.this, urlOrPath.toString());
////									Intent intent =new Intent(context,Lin_MainFrame.class);
////									startActivity(intent);
////									
////									finish();
//								
//									Util.showIntent( context,UserCenterFrame.class);
//			}
//							});
//							dialog.setRight(new OnClickListener() {
//								
//								@Override
//								public void onClick(View arg0) {
//									// TODO Auto-generated method stub
//									Util.showIntent( context,Lin_MainFrame.class);
//								}
//							});
//							dialog.show();
//							
//						}else{
                        Util.show(msg, SiteFrame.this);
//						}


                    }

                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.upToSite, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&twoPwd="
                        + new MD5().getMD5ofStr(twoPwd.getText().toString())
                        + "&payType=" + getPayType() + "&money=" + money
                        + "&zsdw=" + Util.get(zsdw_.getText().toString())
                        + "&zType=" + ccTYpe);
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

    private void wxPay(final String orderId, final double money) {
        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(this,
                "微信支付中...");
        new MMPay(this, pd, money, orderId, "申请城市经理").pay();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId, "申请城市经理", money, new AliPayCallBack() {
            @Override
            public void doSuccess(String aliResultCode) {
                Util.showIntent(SiteFrame.this, Lin_MainFrame.class);
                Util.show("城市经理申请成功！", SiteFrame.this);
            }

            @Override
            public void doFailure(String aliResultCode) {
                Util.show("申请失败，支付宝状态码" + aliResultCode, SiteFrame.this);
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
