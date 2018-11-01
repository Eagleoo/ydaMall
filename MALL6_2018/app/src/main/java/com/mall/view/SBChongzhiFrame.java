package com.mall.view;

import java.io.IOException;
import java.io.Serializable;

import net.sourceforge.mm.MMPay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.PayType;
import com.mall.util.PayTypeClickCallback;
import com.mall.util.UserData;
import com.mall.util.Util;

public class SBChongzhiFrame extends Activity {
    private EditText money;
    private TextView line;


    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;

    @ViewInject(R.id.pay_item_sb_ban)
    private TextView pay_item_sb_ban;
    private String payType = "3";

    @ViewInject(R.id.pay_money_frame_two_line)
    private View twoLine;
    @ViewInject(R.id.shop_pay_pay_twoPwd)
    private EditText twoPwd;

    private String select = "rec";
    // 交易密码
    private String pwd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.account_rechang_frame);
        ViewUtils.inject(this);

        this.findViewById(R.id.pay_item_xj_line).setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_sb_line).setVisibility(View.GONE);
        View ali = this.findViewById(R.id.pay_item_ali_line);
        View rec = this.findViewById(R.id.pay_item_rec_line);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        PayType.addPayTypeListener(new PayTypeClickCallback() {
            @Override
            public void click(View v) {
                if (R.id.pay_item_rec_line == v.getId()) {
                    twoLine.setVisibility(View.VISIBLE);
                } else
                    twoLine.setVisibility(View.GONE);
            }

        }, this, R.id.pay_item_uppay_line, twoLine, ali, rec, uppay, weixin);
        init();
    }


    /**
     * 生成订单号方式
     *
     * @return
     */
    public String getPayType() {
        payType = PayType.getPayType(this);
        if ("2".equals(payType))
            return "rec";
        if ("3".equals(payType))
            return "bank";
        if ("4".equals(payType))
            return "ali";
        if ("5".equals(payType))
            return "sb";
        if ("6".equals(payType))
            return "wx";
        return "";
    }

    private void initComponent() {
        Util.initTop(this, "账户充值", Integer.MIN_VALUE, null);
        money = Util.getEditText(R.id.account_rechang_money, this);
        line = (TextView) this.findViewById(R.id.account_rechang_show);

        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {

                if (null == runData) {
                    Util.show("获取帐户信息失败！", SBChongzhiFrame.this);

                }
                User user = UserData.getUser();
                if (user == null) {
                    return;
                }

                if ("rec".equals(select)) {
                    pay_item_rec_ban.setText("余额:"
                            + Util.getDouble(
                            Util.getDouble(user.getRecharge()), 3));
                } else if ("sto".equals(select)) {
                } else if ("recCon".equals(select)) {

                } else if ("sb".equals(select)) {
                    pay_item_sb_ban.setText("余额:"
                            + Util.getDouble(Util.getDouble(user.getShangbi()),
                            3));

                }
            }

            @Override
            public Serializable run() {
                return Web.reDoLogin();
            }
        });
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
                String m = money.getText().toString();
                if (m.matches("^\\d+[\\.\\d+]*$")) {
                    String s = null;
                    if (Double.parseDouble(m) > 50000.00D) {
                        line.setText("充值金额须小于伍万");
                    } else {
                        try {
                            s = Util.getToUp(Double.parseDouble(m));
                        } catch (Exception ee) {
                            line.setText("转换错误！");
                        }
                        if (!"溢出".equals(s)) {
                            line.setText(s);
                        }
                    }
                } else
                    line.setText("无效格式");
            }
        });
    }

    @OnClick(R.id.account_rechang_cle)
    public void cleClick(View v) {
        money.setText("");
        line.setText("");
    }

    @OnClick(R.id.account_rechang_sub)
    public void subClick(View v) {
        final String m = money.getText().toString();
        String s = line.getText().toString();
        if (Util.isNull(m)) {
            Util.show("请输入要充值的金额！", this);
            return;
        }
        if (twoLine.getVisibility() == View.VISIBLE) {
            pwd = twoPwd.getText().toString().replace(" ", "");
            if (pwd.equals("")) {
                Util.show("请输入交易密码", this);
                return;
            }
            pwd = new MD5().getMD5ofStr(pwd);

        }

        if (Util.isNull(s) || "无效格式".equals(s) || "充值金额须小于伍万".equals(s)) {
            Util.show("请检查您输入的金额！", this);
            return;
        }

        if (!Util.isInt(m)) {
            Util.show("充值金额只能是整数。", this);
            return;
        }
        select = getPayType();
        Util.asynTask(this, "正在提交，请稍等...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String data = (String) runData;
                if (Util.isNull(runData)) {
                    Util.show("网络异常请重试！", SBChongzhiFrame.this);
                    return;
                } else {

                    if (data.contains("success")) {
                        if (select.equals("bank") || select.equals("ali") || select.equals("wx")) {

                            String[] split = data.split(":");

                            if (split.length >= 3) {

                                // 订单生成后付款
                                if (select.equals("ali")) {
                                    aliPay(split[1], Double.parseDouble(split[2]));
                                } else if ("wx".equals(select)) {
                                    wxPay(split[1], Double.parseDouble(split[2]));
                                }

                            }
                        } else {
                            Util.show("支付成功。", SBChongzhiFrame.this);
                            finish();
                        }

                    } else {
                        Util.show(data, SBChongzhiFrame.this);
                    }

                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.sbChongzhi, "userid="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&money=" + m
                        + "&type=" + payType + "&twoPwd=" + pwd);

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
        CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "微信支付中...");
        new MMPay(this, cpd, money, orderId, "商币充值").pay();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId, "商币充值——" + money + "元", money,
                new AliPayCallBack() {
                    @Override
                    public void doSuccess(String aliResultCode) {
                        Util.showIntent("充值成功，请重新登录查看！", SBChongzhiFrame.this,
                                LoginFrame.class);
                    }

                    @Override
                    public void doFailure(String aliResultCode) {
                        Util.show("支付失败：支付宝状态码" + aliResultCode,
                                SBChongzhiFrame.this);
                    }
                });
    }

    // 每个数字对应的大写

    private static final String[] num = {

            "零", "壹", "贰", "叁", "肆", "伍",

            "陆", "柒", "捌", "玖",

    };

    // 从低到高排列的单位

    private static final String[] bit = {

            "圆", "拾", "佰", "仟", "万", "拾",

            "佰", "仟", "亿", "拾", "佰", "仟",

            "万", "拾", "佰", "仟", "亿"

    };

    // 金额里面的角和分

    private static final String[] jf = {

            "角", "分"

    };

    /**
     * 处理金额的整数部分,返回"...圆整"
     *
     * @param integer
     * @return String
     * @throws Exception
     */

    public static String praseUpcaseRMB(String integer) throws Exception {

        StringBuilder sbdr = new StringBuilder("");

        int j = integer.length();

        if (j > bit.length) {

            throw new Exception("\n只能处理亿万亿以内的数据(含亿万亿)!");

        }

        char[] rmb = integer.toCharArray();

        for (int i = 0; i < rmb.length; i++) {

            int numLocate = Integer.parseInt("" + rmb[i]); // 大写数字位置

            int bitLocate = j - 1 - i; // 数字单位的位置

			/*
			 * 
			 * 连续大写零只添加一个
			 */

            if (numLocate == 0) {

                if (!sbdr.toString().endsWith(num[0])) {

                    sbdr.append(num[numLocate]);

                }

                continue;

            }

			/*
			 * 
			 * 下面的if语句保证
			 * 
			 * 10065004583.05-->壹佰亿陆仟伍佰万肆仟伍佰捌拾叁圆零伍分
			 */

            if (bit[bitLocate].equals("仟")) {

                String s = sbdr.toString();

                if (!s.endsWith(bit[bitLocate + 1]) && s.length() > 0) {

                    if (s.endsWith(num[0])) {

                        sbdr.deleteCharAt(sbdr.length() - 1);

                    }

                    sbdr.append(bit[bitLocate + 1]);

                }

            }

            sbdr.append(num[numLocate]);

            sbdr.append(bit[bitLocate]);

        }// end for

		/*
		 * 
		 * 去掉结尾"零"后,补全
		 */

        if (sbdr.toString().endsWith(num[0])) {

            sbdr.deleteCharAt(sbdr.length() - 1);

            sbdr.append("圆整");

        } else {

            sbdr.append("整");

        }

        return sbdr.toString();

    }

    /**
     * 处理带小数的金额,整数部分交由上一个方法处理,小数部分自己处理
     *
     * @param integer
     * @param decimal
     * @return String
     * @throws Exception
     */

    public static String praseUpcaseRMB(String integer, String decimal)
            throws Exception {

        String ret = praseUpcaseRMB(integer);

        ret = ret.split("整")[0]; // 处理整数部分

        StringBuilder sbdr = new StringBuilder("");

        sbdr.append(ret);

        char[] rmbjf = decimal.toCharArray();

        for (int i = 0; i < rmbjf.length; i++) {

            int locate = Integer.parseInt("" + rmbjf[i]);

            if (locate == 0) {

                if (!sbdr.toString().endsWith(num[0])) {

                    sbdr.append(num[locate]);

                }

                continue;

            }

            sbdr.append(num[locate]);

            sbdr.append(jf[i]);

        }

        return sbdr.toString();

    }

    /**
     * 将double形式的字符串(有两位小数或无小数)转换成人民币的大写格式
     *
     * @param doubleStr
     * @return String
     * @throws Exception
     */

    public static String doChangeRMB(String doubleStr) throws Exception {

        String result = null;

        if (doubleStr.contains(".")) { // 金额带小数

            int dotloc = doubleStr.indexOf(".");

            int strlen = doubleStr.length();

            String integer = doubleStr.substring(0, dotloc);

            String decimal = doubleStr.substring(dotloc + 1, strlen);

            result = praseUpcaseRMB(integer, decimal);

        } else { // 金额是整数

            String integer = doubleStr;

            result = praseUpcaseRMB(integer);

        }

        return result;

    }

    /**
     * 将double数值(有两位小数或无小数)转换成人民币的大写格式
     *
     * @param rmbDouble
     * @return String
     * @throws Exception
     */

    public static String doChangeRMB(double rmbDouble) throws Exception {

        String result = null;

        double theInt = Math.rint(rmbDouble);

        if (theInt > rmbDouble) {

            theInt -= 1;

        }

        double theDecimal = rmbDouble - theInt;

        String integer = new Long((long) theInt).toString();

        String decimal = "" + Math.round(theDecimal * 100);

        if (decimal.equals("0")) {

            result = praseUpcaseRMB(integer);

        } else {

            result = praseUpcaseRMB(integer, decimal);

        }

        return result;

    }
}
