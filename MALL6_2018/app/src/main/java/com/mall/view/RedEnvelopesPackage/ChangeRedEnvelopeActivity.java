package com.mall.view.RedEnvelopesPackage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.view.VideoAudioDialog;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnFocusChange;
import com.lin.component.CustomProgressDialog;
import com.mall.model.InviterInfo;
import com.mall.model.RedPackageInLetBean;
import com.mall.model.UserAccountWithdrawal;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.MoneyInputFilter;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.AccountHelp.AccountHelpUtil;
import com.mall.view.BusinessCircle.RedBeanAccountActivity;
import com.mall.view.PerfectInformationFrame;
import com.mall.view.R;
import com.mall.view.UpdateBankFrame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 转入 程序
 */

@ContentView(R.layout.activity_change_red_envelope)
public class ChangeRedEnvelopeActivity extends AppCompatActivity {
    private Context context;

    private String title = "";

    @ViewInject(R.id.root)
    private View root;

    @ViewInject(R.id.handertitle)
    private TextView handertitle;

    @ViewInject(R.id.payee_ll)
    private View payee_ll;

    @ViewInject(R.id.shengfuzhen_ll)
    private View shengfuzhen_ll;


    @ViewInject(R.id.money_tv)
    private TextView money_tv;

    @ViewInject(R.id.money_et)
    private EditText money_et;

    @ViewInject(R.id.password_et)
    private EditText password_et;

    @ViewInject(R.id.bean_tag)
    private TextView bean_tag;

    @ViewInject(R.id.selectaccount_ll)
    private View selectaccount_ll;

    @ViewInject(R.id.bankcardid_ll)
    private View bankcardid_ll;

    @ViewInject(R.id.rednumber_tv)
    private TextView rednumber_tv;

    @ViewInject(R.id.touser_ed)
    private EditText touser_ed;

    @ViewInject(R.id.bankcardid_ed)
    private EditText bankcardid_ed;
    @ViewInject(R.id.selectaccount_ed)
    private EditText selectaccount_ed;

    @ViewInject(R.id.shengfuzhen_ed)
    private EditText shengfuzhen_ed;

    @ViewInject(R.id.payee_tv)
    private TextView payee_tv;
    @ViewInject(R.id.imagedown)
    private ImageView imagedown;
    @ViewInject(R.id.userinfo)
    private TextView userinfo;

    @ViewInject(R.id.submit)
    private TextView submit;

    @ViewInject(R.id.type_tv)
    private TextView type_tv;


    @ViewInject(R.id.tag1)
    private TextView tag1;
    @ViewInject(R.id.bank_name)
    private TextView bank_name;

    @ViewInject(R.id.withdrawallist)
    private View withdrawallist;

    @ViewInject(R.id.pay_item_weixin_radio)
    private ImageView pay_item_weixin_radio;
    @ViewInject(R.id.pay_item_ali_radio)
    private ImageView pay_item_ali_radio;
    @ViewInject(R.id.pay_item_uppay_radio)
    private ImageView pay_item_uppay_radio;

    @ViewInject(R.id.pay_item_weixin_line)
    private View pay_item_weixin_line;

    @ViewInject(R.id.pay_item_ali_line)
    private View pay_item_ali_line;

    @ViewInject(R.id.pay_item_uppay_line)
    private View pay_item_uppay_line;

    private String tx_type = "0"; //0:银行卡，1微信，2支付宝

    private boolean isopen;

    RedPackageInLetBean redPackageInLetBean;

    private int weixinbanding = 0; //0 为位绑定 1为绑定
    private int zhifubaobanding = 0;
    private int yinghangbanding = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ViewUtils.inject(this);
        Intent intent = getIntent();
        String str = intent.getStringExtra("title");
        if (!Util.isNull(str)) {
            title = str;
        }
        Log.e("标题", "title" + title);
        init();

//        isTixianDat()
    }


    private void isTixianDate() {
        final CustomProgressDialog cpd = Util.showProgress("加载中...", this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + "GetdateMessage" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd(),
                new NewWebAPIRequestCallback() {

                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }

                        setSubmitState(true);


                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
        );
    }


    @OnFocusChange(R.id.money_et)
    public void moneyliser(View v, boolean hasFour) {
        if (hasFour) {
            // 此处为得到焦点时的处理内容
        } else {
            // 此处为失去焦点时的处理内容
            String str = ((EditText) v).getText().toString();
            Log.e("数据长度", str + "ll");
            Log.e("下标", "lastIndexOf" + str.indexOf(".0") + "HH" + (str.length() - 1));

            if (str.indexOf(".") == str.length() - 1) {
                ((EditText) v).setText(str + "00");
            }

        }
    }


    private void init() {

        bank_name.setText(Util.justifyString("银行卡", 4));

        if (title.equals("现金券提现")) {
            if ("0".equals(UserData.getUser().getBank())) {
                VoipDialog voipDialog = new VoipDialog("对不起，提现必须完善银行信息", context, "确定", "取消", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(context, UpdateBankFrame.class);
                        context.startActivity(intent);
                        if (context instanceof Activity)
                            ((Activity) context).overridePendingTransition(android.R.anim.slide_in_left,
                                    android.R.anim.slide_out_right);
                    }
                }, null);
                voipDialog.show();
                return;
            }
        }

        initview();


    }

    @Override
    protected void onResume() {
        super.onResume();
        getRedBoxmyselfInfo();
        if (title.equals("现金券提现")) {
            checkCanCarryAccount();
        }


    }

    private void initviewstate() {
        payee_ll.setVisibility(View.GONE);
        shengfuzhen_ll.setVisibility(View.GONE);
        selectaccount_ll.setVisibility(View.GONE);
        bankcardid_ll.setVisibility(View.GONE);
//        bean_tag.setVisibility(View.INVISIBLE);
    }


    @OnFocusChange(R.id.touser_ed)
    public void tjrFocus(View v, boolean hasFocus) {
        if (Util.isNull(touser_ed.getText().toString())) {
            touser_ed.setTextColor(Color.parseColor("#535353"));
        }

        if (!hasFocus && !Util.isNull(touser_ed.getText().toString())) {
            Util.asynTask(this, "正在获取转账用户信息...", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (null != runData) {
                        InviterInfo info = (InviterInfo) runData;
                        if (Util.getInt(info.getLevel()) >= 2 || Util.getInt(info.getShopType()) >= 3
                                || "远大商城".equals(info.getUserid())) {
                            String name = info.getName();
                            if (!Util.isNull(name))
                                name = name.substring(0, 1) + "**";
                            String phone = info.getPhone();
                            if (!Util.isNull(phone))
                                phone = phone.substring(0, 3) + "****"
                                        + phone.substring(phone.length() - 4, phone.length());
                            userinfo.setVisibility(View.VISIBLE);
                            userinfo.setText("姓名：" + name + "\t\t\t手机：" + phone);
                        } else {
                            userinfo.setText("该会员不能存在。");
                        }
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.getInviter, "userId=" + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&uid=" + Util.get(touser_ed.getText().toString()));
                    return web.getObject(InviterInfo.class);
                }
            });
        }
    }


    private void initview() {
        handertitle.setText(title);
        if (title.equals("现金券提现")) {

            tag1.setVisibility(View.VISIBLE);

            setSubmitState(false);

            isTixianDate();

            isTitle();

            InputFilter[] inputFilters = {new MoneyInputFilter(0)};

            money_et.setFilters(inputFilters);
            money_tv.setText("提现金额");
            type_tv.setText("提现账户");
            money_et.setHint("提现金额必须≥100,并且只能是100的整数!");

            payee_ll.setVisibility(View.VISIBLE);
            shengfuzhen_ll.setVisibility(View.VISIBLE);
            selectaccount_ll.setVisibility(View.VISIBLE);
            bankcardid_ll.setVisibility(View.VISIBLE);
            touser_ed.setText(UserData.getUser().getName());
            touser_ed.setFocusable(false);
            shengfuzhen_ed.setText(!Util.isNull(UserData.getUser().getIdNo()) ? UserData.getUser().getIdNo() : UserData.getUser().getPassport());
            shengfuzhen_ed.setFocusable(false);
            bankcardid_ed.setText(UserData.getUser().getBankCard());
            bankcardid_ed.setFocusable(false);
            selectaccount_ed.setFocusable(false);
            selectaccount_ed.setText(UserData.getUser().getBank());
            bean_tag.setText("现金券");

        } else if (title.equals("现金券转账")) {
            bankcardid_ll.setVisibility(View.GONE);
            payee_ll.setVisibility(View.VISIBLE);
            shengfuzhen_ll.setVisibility(View.GONE);
            money_tv.setText("转账金额");
            money_et.setHint("请输入100的整数倍");
            InputFilter[] inputFilters = {new MoneyInputFilter(2)};

            money_et.setFilters(inputFilters);


            selectaccount_ll.setVisibility(View.VISIBLE);
            selectaccount_ed.setHint("充值账户");

            selectaccount_ed.setFocusable(false);
            payee_tv.setText(Util.justifyString("收款人", 4));
            bean_tag.setText("现金券");

        } else if (title.equals("提现账户转账")) {
            bankcardid_ll.setVisibility(View.GONE);
            payee_ll.setVisibility(View.VISIBLE);
            shengfuzhen_ll.setVisibility(View.GONE);
            money_tv.setText("转账金额");
            money_et.setHint("请输入100的整数倍");
            InputFilter[] inputFilters = {new MoneyInputFilter(2)};

            money_et.setFilters(inputFilters);


            selectaccount_ll.setVisibility(View.GONE);
            selectaccount_ed.setHint("充值账户");

            selectaccount_ed.setFocusable(false);
            payee_tv.setText(Util.justifyString("收款人", 4));
            bean_tag.setText("余额");

        } else if (title.equals("转入消费券")) {

            bankcardid_ll.setVisibility(View.GONE);
            shengfuzhen_ll.setVisibility(View.GONE);
            money_tv.setText("转账金额");
            InputFilter[] inputFilters = {new MoneyInputFilter(2)};

            money_et.setFilters(inputFilters);
            money_et.setHint("输入转账金额");
            payee_ll.setVisibility(View.VISIBLE);
            selectaccount_ll.setVisibility(View.VISIBLE);
            selectaccount_ed.setHint("消费券账户");
            imagedown.setVisibility(View.GONE);
            selectaccount_ed.setFocusable(false);
            payee_tv.setText(Util.justifyString("收款人", 4));
            bean_tag.setText("消费券");

        } else if (title.equals("封入红包盒")) {
            InputFilter[] inputFilters = {new MoneyInputFilter(0)};

            money_et.setFilters(inputFilters);

            bean_tag.setText("红包豆");
            payee_ll.setVisibility(View.GONE);
            shengfuzhen_ll.setVisibility(View.GONE);
            selectaccount_ll.setVisibility(View.GONE);
            bankcardid_ll.setVisibility(View.GONE);
            money_et.setHint("请输入封入金额");
        } else if (title.equals("充值币账户")) {
            InputFilter[] inputFilters = {new MoneyInputFilter(0)};

            money_et.setFilters(inputFilters);
            handertitle.setText("充值币转红包豆");
            money_tv.setText("转入金额");
            bean_tag.setText("充值币");
            bankcardid_ll.setVisibility(View.GONE);
            shengfuzhen_ll.setVisibility(View.GONE);
            money_tv.setText("转账金额");
            payee_ll.setVisibility(View.VISIBLE);
            selectaccount_ll.setVisibility(View.GONE);
            selectaccount_ed.setHint("红包豆账户");
            imagedown.setVisibility(View.GONE);
            selectaccount_ed.setFocusable(false);
            payee_tv.setText(Util.justifyString("收款人", 4));


        } else if (title.equals("红包豆转入消费券")) {
            InputFilter[] inputFilters = {new MoneyInputFilter(0)};

            money_et.setFilters(inputFilters);
            payee_ll.setVisibility(View.GONE);
            selectaccount_ll.setVisibility(View.GONE);
            bean_tag.setText("红包豆");
            shengfuzhen_ll.setVisibility(View.GONE);
            bankcardid_ll.setVisibility(View.GONE);

            money_tv.setText("转入金额");

        }

    }

    @OnClick({R.id.submit, R.id.top_back, R.id.imagedown, R.id.pay_item_weixin_line,
            R.id.pay_item_ali_line, R.id.pay_item_uppay_line, R.id.selectaccount_ll})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.pay_item_weixin_line:
            case R.id.pay_item_ali_line:
            case R.id.pay_item_uppay_line:
                setWithdrawallistWay(view);
                break;
            case R.id.top_back:
                finish();
                break;
            case R.id.submit:
                if (title.equals("封入红包盒") || title.equals("红包豆转入消费券")) {
                    String str1 = money_et.getText().toString();
                    String str2 = password_et.getText().toString();
                    if (Util.isNull(str1)) {
                        Toast.makeText(context, "请输入金额", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Util.isNull(str2)) {
                        Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double money = Double.parseDouble(money_et.getText().toString());
                    if (title.equals("封入红包盒")) {
                        if (money >= 1) {
                            setSubmitState(false);
                            conversionstype("0", money + "", str2);
                        } else {
                            Toast.makeText(context, "请输入封入金额", Toast.LENGTH_SHORT).show();
                        }

                    } else if (title.equals("红包豆转入消费券")) {
                        if ((money % 100) == 0) {
                            Log.e("title", title + "LL");
                            setSubmitState(false);
                            conversionstype("1", money + "", str2);
                        } else {
                            Toast.makeText(context, "请输入100的整数倍", Toast.LENGTH_SHORT).show();
                        }
                    }
//                    if (title.equals("封入红包盒")||money>=1){
//                        conversionstype("0", money + "", str2);
//                    }else {
//                        Toast.makeText(context, "请输入封入金额", Toast.LENGTH_SHORT).show();
//                    }
//
//                    if ((money % 100) == 0) {
//                        Log.e("title", title + "LL");
//
//                        //封入红包盒子
//                        setSubmitState(false);
//                        if (title.equals("封入红包盒")) {
//                            conversionstype("0", money + "", str2);
//                        } else if (title.equals("红包豆转入消费券")) {
//                            conversionstype("1", money + "", str2);
//                        }
//                    } else {
//                        Toast.makeText(context, "请输入100的整数倍", Toast.LENGTH_SHORT).show();
//                    }

                } else if (title.equals("充值币账户") ||
                        title.equals("转入消费券") || title.equals("现金券转账") || title.equals("提现账户转账")) {
                    String str1 = money_et.getText().toString();
                    String str2 = password_et.getText().toString();
                    String touser = touser_ed.getText().toString();


                    if (Util.isNull(touser)) {
                        Toast.makeText(context, "请输入收款用户", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (Util.isNull(str1)) {
                        Toast.makeText(context, "请输入金额", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Util.isNull(str2)) {
                        Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Double money = Double.parseDouble(money_et.getText().toString());

                    if (title.equals("转入消费券")) {
                        setSubmitState(false);
                        TransferXiaofeiquan(money + "", str2, touser, title);
                    } else if (title.equals("提现账户转账")) {
                        setSubmitState(false);
                        crashTixian(money + "", touser, str2);

                    } else if (title.equals("现金券转账")) {

                        try {
                            if (zType.equals("3")) {
                                if (Integer.parseInt(money_et.getText().toString()) % 100 != 0) {
                                    Util.show("转账金额请输入100或者100的整数倍");
                                    return;
                                }

                            }

                        } catch (Exception e) {

                        }

                        setSubmitState(false);


                        RechargeToRedbean(money + "", str2, touser, "CashCouponZZ");


                    } else {
                        if ((money % 100) == 0) {
                            if (title.equals("充值币账户")) {
                                setSubmitState(false);
                                RechargeToRedbean(money + "", str2, touser, "RechargeToRedbean");
                            }
//                            else if(title.equals("转入消费券")){
//                                TransferXiaofeiquan(money+"",str2,touser,title);
//                            }
//                            else  if (title.equals("现金券转账")){
//
//                            }


                        } else {
                            Toast.makeText(context, "请输入100的整数倍", Toast.LENGTH_SHORT).show();
                        }
                    }


                } else if (title.equals("现金券提现")) {
                    String touser = touser_ed.getText().toString();
                    String shengfuzheng = shengfuzhen_ed.getText().toString();
                    String moneystr = money_et.getText().toString();
                    String bankcard = selectaccount_ed.getText().toString();
                    String bankid = bankcardid_ed.getText().toString();
                    String password = password_et.getText().toString();

                    if (Util.isNull(touser)) {
                        Toast.makeText(context, "请输入转账用户名", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (Util.isNull(moneystr)) {
                        Toast.makeText(context, "请输入转账金额", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (Util.isNull(shengfuzheng)) {
                        Toast.makeText(context, "请输入身份证号", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Util.isNull(bankcard)) {
                        Toast.makeText(context, "请输入开户银行", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Util.isNull(bankid)) {
                        Toast.makeText(context, "请输入银行卡号", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Util.isNull(password)) {
                        Toast.makeText(context, "请输入交易密码", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double money = Double.parseDouble(moneystr);

                    if (money < 100 || money % 100 != 0) {
                        Toast.makeText(context, "提现金额必须≥100，并且只能是100的整数!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    setSubmitState(false);
                    tixianjinge(moneystr + "", password);

//                    if ((money%100)==0){
//                        tixianjinge(money+"",password);
//                    }else{
//                        Toast.makeText(context,"请输入100的整数倍",Toast.LENGTH_SHORT).show();
//                    }
                }
                break;
            case R.id.imagedown:
            case R.id.selectaccount_ll:
                if (title.equals("现金券提现")) {
                    if (isopen) {
                        isopen = false;
                        withdrawallist.setVisibility(View.GONE);
                        imagedown.animate().rotation(0);
                    } else {
                        isopen = true;
                        withdrawallist.setVisibility(View.VISIBLE);
                        imagedown.animate().rotation(180);
                    }
                } else if (title.equals("现金券转账")) {
                    showSelectNumberDialog();
                }


                break;
        }
    }

    private void crashTixian(String money, String touser, String pwd) {

        final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "请求中...");
        Map<String, String> param = new HashMap<String, String>();
        String md5Pwd = UserData.getUser().getMd5Pwd();
        String userId = UserData.getUser().getUserId();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("twoPwd", new MD5().getMD5ofStr(pwd));
        param.put("reciverId", touser);
        param.put("zType", "2");
        param.put("dpoint", money);
        NewWebAPI.getNewInstance().getWebRequest("/User.aspx?call=businessToMoney", param,
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }

                        JSONObject json = JSON.parseObject(result.toString());
                        int code = json.getIntValue("code");
                        String message = json.getString("message");
                        if (code != 200) {
                            Util.show(message);
                            return;
                        }

                        Util.showIntent("提现申请成功。是否需要去查看？", context, AccountParticularsListActivity.class,
                                new String[]{"title", "type_"}, new String[]{"提现", "4"});


                    }


                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        setSubmitState(true);
                        cpd.dismiss();

                    }
                });

    }

    NumbersAdapter adapter;
    PopupWindow popupWindow;
    private List<String> numbers = new ArrayList<String>();

    /**
     * 弹出选择号码对话框
     */
    @SuppressLint("ResourceAsColor")
    private void showSelectNumberDialog() {
        numbers.clear();
        numbers.add("充值账户");
        numbers.add("红包豆账户");

        ListView lv = new ListView(this);
        lv.setBackgroundResource(R.drawable.liner2_border_white);
        // 隐藏滚动条
        lv.setVerticalScrollBarEnabled(false);
        // 让listView没有分割线
        lv.setDividerHeight(1);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectaccount_ed.setText(numbers.get(i));
                popupWindow.dismiss();
                if (numbers.get(i).equals("充值账户")) {
                    zType = "2";
                } else {
                    zType = "3";
                }

            }
        });
        lv.setCacheColorHint(android.R.color.transparent);
        adapter = new NumbersAdapter();
        lv.setAdapter(adapter);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        popupWindow = new PopupWindow(lv, selectaccount_ed.getWidth() - 4, numbers.size()
                * Util.dpToPx(this, 40F));
        // 设置点击外部可以被关闭
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(root,
                Gravity.CENTER, 0, 0); // 显示
    }

    class NumbersAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return numbers.size();
        }

        @Override
        public Object getItem(int position) {
            return numbers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NumberViewHolder mHolder = null;
            if (convertView == null) {
                mHolder = new NumberViewHolder();
                convertView = LayoutInflater.from(ChangeRedEnvelopeActivity.this)
                        .inflate(R.layout.item_spinner_numbers, null);
                mHolder.tvNumber = (TextView) convertView
                        .findViewById(R.id.tv_number);
                convertView.setTag(mHolder);
            } else {
                mHolder = (NumberViewHolder) convertView.getTag();
            }
            Log.e("selectaccount_", selectaccount_ed.getText().toString() + "KK");

            String str = selectaccount_ed.getText().toString().equals("") ? "充值账户" : selectaccount_ed.getText().toString();
            if (str.equals(numbers.get(position))) {
                Drawable dra = ChangeRedEnvelopeActivity.this.getResources()
                        .getDrawable(R.drawable.pay_item_checked);
                dra.setBounds(0, 0, dra.getMinimumWidth(),
                        dra.getMinimumWidth());
                mHolder.tvNumber.setCompoundDrawables(dra, null, null, null);
            }
            mHolder.tvNumber.setText(numbers.get(position));
            return convertView;
        }

    }

    public class NumberViewHolder {
        public TextView tvNumber;
    }

    private void tixianjinge(final String money, final String twoPwd) {


        final CustomProgressDialog cpd = Util.showProgress("申请提现中...", this);

        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + "CashCouponTX" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        + "&twoPwd=" + new MD5().getMD5ofStr(twoPwd) + "&money=" + money + "&tx_type=" + tx_type
                ,
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

                        Log.e("Object result", result.toString() + "LL");
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }

                        Util.show("提现申请成功", context);
                        finish();
//                        Util.showIntent("提现申请成功。是否需要去查看？", context, AccountParticularsListActivity.class,
//                                new String[] { "title", "url" }, new String[] { "现金券账户", "getRed_boxAccount" });

//


                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                        setSubmitState(true);
                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });

//        Util.asynTask(context, "申请提现中...", new IAsynTask() {
//
//            @Override
//            public void updateUI(Serializable runData) {
//                setSubmitState(true);
//                if ("success".equals(runData)) {
//                    Util.showIntent("提现申请成功。是否需要去查看？", context, AccountParticularsListActivity.class,
//                            new String[] { "title", "url" }, new String[] { "现金券账户", "getRed_boxAccount" });
//
//                    finish();
//
//                } else {
//                    Util.show(runData + "", context);
//                }
//            }
//
//            @Override
//            public Serializable run() {
//                Web web = new Web(Web.requestTX, "userid=" + UserData.getUser().getUserId() + "&md5Pwd="
//                        + UserData.getUser().getMd5Pwd() + "&handler=&money=" + money + "&twoPwd=" + new MD5().getMD5ofStr(twoPwd));
//                return web.getPlan();
//            }
//        });
    }


    /**
     * 转入消费券 其实就是转入积分账户
     */
    private void TransferXiaofeiquan(final String money, final String password, final String userId, final String title) {


        Util.asynTask(context, "正在为您转账...",
                new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        setSubmitState(true);
                        if ("success".equals(runData + "")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    context);
                            builder.setMessage("转账成功");
                            builder.setTitle("提示");
                            builder.setPositiveButton(
                                    "确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {


//                                                Intent     intent= new Intent(context, AccountParticularsListActivity.class);
//                                                intent.putExtra("title","消费券账户");
//                                                intent.putExtra("url","GetConsumptionVolume");
//                                                startActivity(intent);
                                            finish();


                                        }
                                    });
                            builder.create().show();
                        } else {
                            Util.show(runData + "",
                                    context);
                        }
                    }

                    @Override
                    public Serializable run() {
                        String param = "userId="
                                + UserData.getUser().getUserId()
                                + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd()
                                + "&twoPwd="
                                + new MD5().getMD5ofStr(password)
                                + "&reciverId=" + Util.get(userId)
                                + "&dpoint=" + money;


                        Web web = null;
                        if (title.equals("转入消费券")) {
                            param += "&zType=2";
                            web = new Web(Web.conToCon, param);
                        } else if (title.equals("现金券转账")) {
                            param += "&zType=" + zType;
                            web = new Web("/CashCouponZZ", param);
                        }

                        return web.getPlan();

                    }
                });

    }

    String zType = "2";

    /**
     * @param money
     * @param password
     * @param userid   部门 下的用户
     */
    public void RechargeToRedbean(String money, String password, String userid, String call) {


        String tisi = "";
        if (call.equals("CashCouponZZ")) {
            tisi = "正在为您转账...";
        } else {
            tisi = "正在为您转账...";
        }

        final CustomProgressDialog cpd = Util.showProgress(tisi, this);


        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + call + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        + "&twoPwd=" + new MD5().getMD5ofStr(password) + "&dpoint=" + money + "&reciverId=" + userid + "&Type=" + zType
                ,
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
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }

                        Util.show(json.getString("message"), context);
                        finish();
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                        setSubmitState(true);
                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });

    }


    public void conversionstype(String type, String money, String password) {
        String tisi = "";
        if (title.equals("封入红包盒")) {
            tisi = "正在为你封入红包盒请稍等...";
        } else {
            tisi = "正在为你转账请稍等...";
        }

        final CustomProgressDialog cpd = Util.showProgress(tisi, this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=SetRedbean&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        + "&towpwd=" + new MD5().getMD5ofStr(password) + "&money=" + money + "&type=" + type
                ,
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
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }

                        if (title.equals("红包豆转入消费券")) {

                            VideoAudioDialog videoAudioDialog = new VideoAudioDialog(context);
                            videoAudioDialog.setContent("红包豆已转入消费券,是否前去查看？");
                            videoAudioDialog.showcancel(View.VISIBLE);
                            videoAudioDialog.setLeft("否");
                            videoAudioDialog.setLeft(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, RedBeanAccountActivity.class);
                                    intent.putExtra("title", "消费券账户");
                                    intent.putExtra("bean", redPackageInLetBean);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            videoAudioDialog.setRight("是");
                            videoAudioDialog.setRight(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    finish();
                                }
                            });
                            videoAudioDialog.show();

                        } else if (title.equals("封入红包盒")) {
                            VideoAudioDialog videoAudioDialog = new VideoAudioDialog(context);
                            videoAudioDialog.setContent("红包豆已封入红包盒,是否前去查看？");
                            videoAudioDialog.showcancel(View.VISIBLE);
                            videoAudioDialog.setLeft("否");
                            videoAudioDialog.setLeft(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, RedEnvelopeBoxActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            videoAudioDialog.setRight("是");
                            videoAudioDialog.setRight(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    finish();
                                }
                            });
                            videoAudioDialog.show();
                        } else {
                            Util.show(json.getString("message"), context);
                            finish();
                        }

                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                        setSubmitState(true);
                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });

    }

    public void getRedBoxmyselfInfo() {

        if (title.equals("提现账户转账")) {


            try {

                AccountHelpUtil.getAccountYE(context, "Present_account", new AccountHelpUtil.CallBackAccountYE() {
                    @Override
                    public void getMoney(double balancemoney) {
                        String money = balancemoney + "";
                        rednumber_tv.setText(money.split("\\.")[0]);
                    }

                    @Override
                    public void getMoneyStr(String account_money) {

                    }
                });
            } catch (IllegalArgumentException e) {
            }

        } else {
            final CustomProgressDialog cpd = Util.showProgress("正在获取您的信息...", this);
            NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=GetRedBoxInfo&userId="
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
                            JSONObject json = JSON.parseObject(result.toString());
                            if (200 != json.getIntValue("code")) {
                                Util.show(json.getString("message"), context);
                                return;
                            }

                            try {

                                Gson gson = new Gson();
                                setRedPackageInfo(gson.fromJson(result.toString(), RedPackageInLetBean.class));
                            } catch (Exception e) {

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

    private void setRedPackageInfo(RedPackageInLetBean bean) {

        if (title.equals("充值币账户")) {
            rednumber_tv.setText(bean.getRechargecoin());
        } else if (title.equals("转入消费券")) {
            rednumber_tv.setText(bean.getConsumption());
        } else if (title.equals("红包豆转入消费券")) {
            rednumber_tv.setText(bean.getRedbean().split("\\.")[0]);
        } else if (title.equals("现金券转账")) {
            rednumber_tv.setText(bean.getCashroll());
        } else if (title.equals("现金券提现")) {
            rednumber_tv.setText(bean.getCashroll());
        } else {
            rednumber_tv.setText(bean.getRedbean().split("\\.")[0]);
        }


    }

    public void setSubmitState(boolean isclick) {

        if (isclick) {
            submit.setBackgroundResource(R.drawable.redboxarc);
            submit.setClickable(true);
            submit.setText("确认");
        } else {
            if (title.equals("现金券提现")) {
                submit.setText("每周一可提现");
                submit.setBackgroundResource(R.drawable.redboxarcnoclick);
                submit.setClickable(false);
            } else {
                submit.setBackgroundResource(R.drawable.redboxarcnoclick);
                submit.setClickable(false);
                submit.setText("确认");
            }

        }

    }

    private void isTitle() {
        final CustomProgressDialog cpd = Util.showProgress("加载中...", this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + "ToStringTixian" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd(),
                new NewWebAPIRequestCallback() {

                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }
                        tag1.setText(Html.fromHtml("<font color=\"#FF2146\">*"
                                + "</font>" + json.getString("message")));
//                        setSubmitState(true);


                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
        );
    }

    private void checkCanCarryAccount() {
        final CustomProgressDialog cpd = Util.showProgress("检查用户账户列表...", this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + "get_tx_way" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()

                , new NewWebAPIRequestCallback() {

                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }

                        Gson gson = new Gson();
                        UserAccountWithdrawal userAccountWithdrawal = gson.fromJson(result.toString(), UserAccountWithdrawal.class);
                        UserAccountWithdrawal.ListBean listBean = userAccountWithdrawal.getList().get(0);
                        if (listBean.getBank().equals("0")) {
                            pay_item_uppay_line.setVisibility(View.GONE);//  银行卡
                            yinghangbanding = 0;

                        } else {
                            setWithdrawallistWay(pay_item_uppay_line);
                            yinghangbanding = 1;
                        }
                        if (listBean.getWx().equals("0")) {
                            pay_item_weixin_line.setVisibility(View.GONE);
                            weixinbanding = 0;

                        } else {
                            setWithdrawallistWay(pay_item_weixin_line);
                            weixinbanding = 1;
                        }
                        if (listBean.getAlipay().equals("0")) {
                            pay_item_ali_line.setVisibility(View.GONE);
                            zhifubaobanding = 0;

                        } else {
                            setWithdrawallistWay(pay_item_ali_line);
                            zhifubaobanding = 1;
                        }
                        showtobanding();

                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {

                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                });
    }

    private void showtobanding() {
        String str = "";
        if (weixinbanding == 0) {
            str = "微信";
        } else if (zhifubaobanding == 0) {
            str = "支付宝";
        } else if (yinghangbanding == 0) {
            str = "银行卡";
        }
        VoipDialog voipDialog = new VoipDialog("对不起，您还未绑定" + str + "！", this, "现在去", "等一下去",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(context, PerfectInformationFrame.class);
                    }
                }, null);
        if (!str.equals("")) {
            voipDialog.show();
        }

    }

    private void setWithdrawallistWay(View view) {
        pay_item_weixin_radio.setImageResource(R.drawable.pay_item_no_checked);
        pay_item_ali_radio.setImageResource(R.drawable.pay_item_no_checked);
        pay_item_uppay_radio.setImageResource(R.drawable.pay_item_no_checked);
        switch (view.getId()) {
            case R.id.pay_item_weixin_line:  //微信
                pay_item_weixin_radio.setImageResource(R.drawable.pay_item_checked);
                selectaccount_ed.setText("微信提现");
                bankcardid_ll.setVisibility(View.GONE);
                tx_type = "1";
                break;
            case R.id.pay_item_ali_line: // 支付宝
                pay_item_ali_radio.setImageResource(R.drawable.pay_item_checked);
                selectaccount_ed.setText("支付宝提现");
                bankcardid_ll.setVisibility(View.GONE);
                tx_type = "2";
                break;
            case R.id.pay_item_uppay_line:  //银行卡
                pay_item_uppay_radio.setImageResource(R.drawable.pay_item_checked);
                selectaccount_ed.setText("银行卡提现");
                bankcardid_ll.setVisibility(View.VISIBLE);
                tx_type = "0";
                break;
        }

    }
}
