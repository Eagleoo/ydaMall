package com.mall.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.view.VideoAudioDialog;
import com.google.gson.Gson;
import com.lin.component.CustomProgressDialog;
import com.mall.BasicActivityFragment.BasicActivity;
import com.mall.CapitalAccount.AddBankCardActivity;
import com.mall.MessageEvent;
import com.mall.PopWindowHelp.BasePopWindow;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.model.UserAccountWithdrawal;
import com.mall.model.YunYingModel;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.util.BankUtil;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.AccountHelp.AccountHelpUtil;
import com.mall.view.AccountHelp.CashAccount;
import com.mall.view.AccountHelp.CashBean;
import com.mall.view.RedEnvelopesPackage.AccountParticularsListActivity;
import com.mall.view.databinding.ItemPopwindtextBinding;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.mall.view.AccountHelp.CashBean.CashObjects.HeadQuarter;
import static com.mall.view.AccountHelp.CashBean.CashObjects.OperationsCenter;
import static com.mall.view.CashwithdrawalActivity.InputMoneyState.illegal;
import static com.mall.view.CashwithdrawalActivity.InputMoneyState.init;
import static com.mall.view.CashwithdrawalActivity.InputMoneyState.normal;
import static com.mall.view.CashwithdrawalActivity.InputMoneyState.outside;

public class CashwithdrawalActivity extends BasicActivity {


    @BindView(R.id.toYunyingIv)
    public ImageView toYunyingIv;
    @BindView(R.id.toYunying)
    public View toYunying;

    @BindView(R.id.money)
    public EditText moneyInpurtEditText;

    @BindView(R.id.yu_e)
    public TextView yu_e;

//    @BindView(R.id.tixian_twoPwd)
//    public EditText twoP;


    public String type_;

    @BindView(R.id.submit)
    public TextView submit;

    @BindView(R.id.weixin_pay_icon)
    public ImageView weixin_pay_icon;

    @BindView(R.id.zhifubao_pay_icon)
    public ImageView zhifubao_pay_icon;


    @BindView(R.id.bank_pay_icon)
    public ImageView bank_pay_icon;

    @BindView(R.id.downup)
    public ImageView downup;

    private String weixinstate = "-1";// 0 为绑定 1为绑定
    private String zhifubaoinstate = "-1";// 0 为绑定 1为绑定
    private String bankinstate = "-1";// 0 为绑定 1为绑定


//    private String tx_type = "-1"; //0:银行卡，1微信，2支付宝


    String account_money = "";

    @BindView(R.id.weixinname_tag)
    TextView weixinname_tag;

    @BindView(R.id.zhifubaoname_tag)
    TextView zhifubaoname_tag;

    @BindView(R.id.weixinname)
    TextView weixinname;

    @BindView(R.id.zhifubaoname)
    TextView zhifubaoname;

    @BindView(R.id.name)
    TextView bankname;

    @BindView(R.id.number)
    TextView banknumber;

    @BindView(R.id.weixiniv_tag)
    ImageView weixiniv_tag;

    @BindView(R.id.zhifubao_tag)
    ImageView zhifubao_tag;

    @BindView(R.id.cashwithdrawal_header)
    ImageView cashwithdrawal_header;

    @BindView(R.id.tixianfeiyong)
    TextView tixianfeiyong;

    @BindView(R.id.weixiniv_rl)
    View weixiniv_rl;

    @BindView(R.id.zhifubao_rl)
    View zhifubao_rl;

    @BindView(R.id.bank_rl)
    View bank_rl;
    @BindView(R.id.tixianinfotv)
    TextView tixianinfotv;

    @BindView(R.id.yunyingtv)
    TextView yunyingtv;


    private SharedPreferences sp;

    private int times = 0;

    private int netmoney = 0;

    CashBean cashBean;


    @Override
    public int getContentViewId() {
        return R.layout.activity_cashwithdrawal;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {

        init();
    }

    @Override
    public void EventCallBack(MessageEvent messageEvent) {

    }

    /**
     * 根据提现 设置 微信 支付宝 银行卡 是否显示
     *
     * @param money
     */
    private void setshowCashToAccount(int money) {

        if (money < 0) {
            weixiniv_rl.setVisibility(View.GONE);
            zhifubao_rl.setVisibility(View.GONE);
            bank_rl.setVisibility(View.GONE);
        } else if (money <= netmoney && money >= 0) {
            weixiniv_rl.setVisibility(View.VISIBLE);
            zhifubao_rl.setVisibility(View.VISIBLE);
            bank_rl.setVisibility(View.GONE);
        } else {
            weixiniv_rl.setVisibility(View.VISIBLE);
            zhifubao_rl.setVisibility(View.VISIBLE);
            bank_rl.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 设置 微信 支付宝 银行卡 是否绑定的状态
     */

    private void setCashToAccountState() {

        if (bankinstate.equals("0")) {//  银行卡

            bankname.setTextColor(Color.parseColor("#A6A6A7"));
            banknumber.setTextColor(Color.parseColor("#A6A6A7"));
            bankname.setText("银行卡");
            banknumber.setText("未绑定");
            cashwithdrawal_header.setImageResource(R.drawable.ccc);

        } else {


            settagicon(bank_pay_icon);

            bankname.setTextColor(Color.parseColor("#000000"));
            banknumber.setTextColor(Color.parseColor("#000000"));

            if (!Util.isNull(UserData.getUser().getBank())) {
                bankname.setText(UserData.getUser().getBank());
                String name = UserData.getUser().getBank();
                if (name.equals("农业银行")) {
                    name = "中国农业银行";
                }
                if (name.equals("民生银行")) {
                    name = "中国民生银行";
                }
                Integer[] integers = BankUtil.getBankId().get(name);
                if (integers != null) {
                    cashwithdrawal_header.setImageResource(integers[2]);
                }


                String str = UserData.getUser().getBankCard();
                Log.e("银行卡", "str" + str);
                if (str.length() > 4) {
                    banknumber.setText("尾号" + str.substring(str.length() - 4, str.length()) + "储蓄卡");
                } else {
                    banknumber.setText("尾号" + str + "储蓄卡");
                }
            }


        }

        if (zhifubaoinstate.equals("0")) { //支付宝
//                            bindingzhifubao.setText("\t未绑定支付宝,去绑定");
            zhifubaoname_tag.setTextColor(Color.parseColor("#A6A6A7"));
            zhifubaoname.setTextColor(Color.parseColor("#A6A6A7"));
            zhifubao_tag.setImageResource(R.drawable.zfb);
            zhifubaoname.setText("未绑定");
        } else {

            settagicon(zhifubao_pay_icon);

            zhifubaoname_tag.setTextColor(Color.parseColor("#000000"));
            zhifubaoname.setTextColor(Color.parseColor("#000000"));
            zhifubao_tag.setImageResource(R.drawable.pay_item_ali);
            zhifubaoname.setText(UserData.getUser().getAlipay());
//                            bindingzhifubao.setText("\t你已经绑定了支付宝");

        }

        if (weixinstate.equals("0")) { // 微信
//                            bindingwechat.setText("\t未绑定微信,去绑定");
            weixinname_tag.setTextColor(Color.parseColor("#A6A6A7"));
            weixinname.setTextColor(Color.parseColor("#A6A6A7"));
            weixinname.setText("未绑定");
            weixiniv_tag.setImageResource(R.drawable.qq);
        } else {

            settagicon(weixin_pay_icon);

            weixinname_tag.setTextColor(Color.parseColor("#000000"));
            weixinname.setTextColor(Color.parseColor("#000000"));
            weixinname.setText("");
            weixiniv_tag.setImageResource(R.drawable.third_weixin);


        }
    }

    private void init() {

        setshowCashToAccount(-1);
        setSubmitState(false, "", "");
        initCash();
        isCash();


        moneyInpurtEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 输入前的监听
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
                Log.e("内容监听", s.toString());
                int money = 0;
                try {
                    money = Integer.parseInt(s.toString());
                } catch (Exception e) {

                }
                setshowCashToAccount(money);
                setCaseMoneyState(money);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void getYunYinglist() {

        final CustomProgressDialog cpd = Util.showProgress("正在获取您的账户信息...", this);

        NewWebAPI.getNewInstance().getWebRequest("/" + "user.aspx?call=getcity_ceo" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        + "&Page=" + 1 + "&pageSize=" + 999
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

                        Gson gson = new Gson();
                        final YunYingModel yunYingModel = gson.fromJson(result.toString(), YunYingModel.class);
                        if (Util.isNull(yunYingModel) || yunYingModel.getList().size() == 0) {
                            return;
                        }
                        toYunying.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view1) {
                                final List<YunYingModel.ListBean> listBeans = new ArrayList<>();
                                listBeans.clear();
                                listBeans.addAll(yunYingModel.getList());


                                View view = LayoutInflater.from(context).inflate(R.layout.popwindowrecycle, null);
                                TextView textView = view.findViewById(R.id.tv);
                                TextView cleantv = view.findViewById(R.id.cleantv);
                                textView.setVisibility(View.VISIBLE);
                                cleantv.setVisibility(View.VISIBLE);
                                textView.setText("请求选择运营中心");
                                cleantv.setText("取消");
                                RecyclerView pop_re = view.findViewById(R.id.pop_re);
                                pop_re.setLayoutManager(new LinearLayoutManager(context));
                                int with = (int) (Util.getScreenSize(context).getWidth() * 0.75);
                                int hight = (int) (Util.getScreenSize(context).getHeight() * 0.75);

                                final BasePopWindow popWindow = new BasePopWindow.Builder(context, view).setWidth(with, hight).setIschagebackcolor(true).build();

                                cleantv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        yunyingtv.setText("向运营中心提现");
                                        cashBean.setOperationsCenteruserid("");
                                        toYunyingIv.setImageResource(R.drawable.thumb_up);
                                        cashBean.setmCashObjects(HeadQuarter);
                                        cashBean.setmCashFees(0.12);
                                        popWindow.dismiss();
                                    }
                                });
                                BaseRecycleAdapter adapter = new BaseRecycleAdapter<YunYingModel.ListBean>(context, listBeans) {

                                    @Override
                                    public void setIteamData(ViewDataBinding mBinding, List<YunYingModel.ListBean> list, int position) {
                                        YunYingModel.ListBean listBean = list.get(position);
                                        ItemPopwindtextBinding itemPopwindtextBinding = (ItemPopwindtextBinding) mBinding;
                                        itemPopwindtextBinding.setItem(listBean.getName());
                                    }

                                    @Override
                                    public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
                                        return DataBindingUtil.inflate(mInflater, R.layout.item_popwindtext, parent, false);
                                    }

                                    @Override
                                    public int setShowRule(int position) {
                                        return 0;
                                    }
                                };
                                adapter.setmOnRecyclerviewItemClickListener(new BaseRecycleAdapter.OnRecyclerviewItemClickListener() {
                                    @Override
                                    public void onItemClickListener(View v, int position) {
                                        YunYingModel.ListBean listBean = listBeans.get(position);

                                        yunyingtv.setText("向" + listBean.getName() + "提现");
                                        cashBean.setOperationsCenteruserid(listBean.getUserid());
                                        toYunyingIv.setImageResource(R.drawable.thumb_dn);
                                        cashBean.setmCashObjects(OperationsCenter);
                                        switch (cashBean.getmCashAccountName()) {
                                            case "Present_account":
                                                cashBean.setmCashFees(0.0326);
                                                break;
                                            default:
                                                cashBean.setmCashFees(0.12);
                                                break;
                                        }
                                        popWindow.dismiss();
                                    }
                                });
                                pop_re.setAdapter(adapter);
                                popWindow.showCenter();

                            }
                        });


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

    private void initCash() {
        Intent intent = getIntent();
        if (intent.hasExtra("userKey")) {
            final String key = intent.getStringExtra("userKey");
            Log.e("账户key", "key:" + key);

            type_ = CashAccount.accountType(key) + "";
            cashBean = getCashBean(key);
            cashBean.setmCashObjects(CashBean.CashObjects.HeadQuarter);
            setMoneyInpurtEditText();
            AccountHelpUtil.getAccountYE(context, key, new AccountHelpUtil.CallBackAccountYE() {
                @Override
                public void getMoney(double balancemoney) {
                    cashBean.setmAccountBalance(balancemoney);

                    switch (key) {
                        case "Present_account":
                            cashBean.setmCashObjects(CashBean.CashObjects.HeadQuarter);
                            cashBean.setmCashFees(0.0326);
                            break;
                        default:
                            cashBean.setmCashFees(0.12);
                            break;
                    }

                    setCaseMoneyState(0);

                    if (type_.equals("4")) {
                        toYunying.setVisibility(View.GONE);

                    }
//                    getYunYinglist();
                    setCashInstructions(false);
                }

                @Override
                public void getMoneyStr(String account_money) {

                }
            });


        }
    }

    private CashBean getCashBean(String key) {
        if (key.equals("bus")) {
            return new CashBean(key, 1000, 100);
        } else {
            return new CashBean(key, 100, 100);
        }


    }


    //输入金额状态
    enum InputMoneyState {
        //超出,初始化,合理 ， 不合法
        outside, init, normal, illegal
    }

    // 检查输入的金额是否合法
    private InputMoneyState checkLegitimateMoney(double money) {

        if (!Util.isNull(cashBean)) {
            if (money > cashBean.getmAccountBalance()) { //超出余额
                return outside;
            } else {
                //小于提现下线 但 输入规则不符合
                if (money < cashBean.getmCashStarMoney()) {
                    return init;
                }
                //大于提现下线 但 输入规则不符合
                if (money % cashBean.getmCashMagn() == 0) {
                    return normal;
                }
                return illegal;

            }
        }


        return illegal;
    }


    private void setMoneyInpurtEditText() {


        String hintStr = "请输入" + (int) cashBean.getmCashStarMoney() + "起且" + cashBean.getmCashMagn() + "的整数倍";
        SpannableString ss = new SpannableString(hintStr);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
        moneyInpurtEditText.setHintTextColor(Color.parseColor("#808080"));
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        moneyInpurtEditText.setHint(new SpannedString(ss));
    }

    /**
     * @param money 更加输入的金额设置 提示信息
     */
    private void setCaseMoneyState(double money) {


        String yuetr = "";

        InputMoneyState inputMoneyState = checkLegitimateMoney(money);
        switch (inputMoneyState) {
            case outside:
                yuetr = "金额已超出可提现金额";
                break;
            case illegal:
            case init:
                yuetr = "当前余额:" + cashBean.getmAccountBalance();
                break;
            case normal:
                DecimalFormat df = new DecimalFormat("#.00");
                String moneyStr = df.format(money * cashBean.getmCashFees());
                yuetr = "扣除个人综合费用:" + (moneyStr);
                break;


        }

        yu_e.setText(yuetr);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        sp = this.getSharedPreferences("tixiantimes", MODE_PRIVATE);
        times = sp.getInt("times", 0);
        Log.e("times", "tixiantimes" + times);
        if (times == 0) {
            SharedPreferences.Editor edit = sp.edit();
            edit.clear();
            edit.putInt("times", 1).commit();
//            setCashInstructions(false);
        }
    }


    @OnClick({R.id.close, R.id.mingxi, R.id.weixiniv_rl,
            R.id.zhifubao_rl, R.id.bank_rl, R.id.submit,
            R.id.all_tv, R.id.downup, R.id.check_box
//            , R.id.toYunying
    })
    public void click(View view) {
        switch (view.getId()) {
            case R.id.toYunying:
//                if (cashBean != null) {
//                    CashBean.CashObjects cashObjects = cashBean.getmCashObjects();
//                    switch (cashObjects) {
//                        case HeadQuarter:
//
//                            toYunyingIv.setImageResource(R.drawable.thumb_dn);
//                            cashBean.setmCashObjects(OperationsCenter);
//                            cashBean.setmCashFees(0.0326);
//                            break;
//                        case OperationsCenter:
//                            toYunyingIv.setImageResource(R.drawable.thumb_up);
//                            cashBean.setmCashObjects(HeadQuarter);
//                            cashBean.setmCashFees(0.08);
//                            yunyingtv.setText("向运营中心提现");
//                            cashBean.setOperationsCenteruserid("");
//                            break;
//                    }
//                    String requestMoney = moneyInpurtEditText.getText().toString();
//                    int money = 0;
//                    if (!Util.isNull(requestMoney)) {
//                        money = Integer.parseInt(requestMoney);
//                    }
//
//                    setCaseMoneyState(money);
//                    setCashInstructions(true);
//                }


                break;
            case R.id.check_box:

                break;
            case R.id.downup:
                setCashInstructions(false);
                break;
            case R.id.close:
                finish();
                break;
            case R.id.mingxi:
//                Util.showIntent("提现申请成功。是否需要去查看？", context, AccountParticularsListActivity.class,
//                        new String[] { "title", "type_"}, new String[] { "提现", type_ });
                Intent intent = new Intent(context, AccountParticularsListActivity.class);
                intent.putExtra("title", "提现");
                intent.putExtra("type_", type_);
                startActivity(intent);
                break;
            case R.id.weixiniv_rl:
                settagicon(weixin_pay_icon);
                break;
            case R.id.zhifubao_rl:
                settagicon(zhifubao_pay_icon);
                break;
            case R.id.bank_rl:
                settagicon(bank_pay_icon);
                break;
            case R.id.submit:
                String requestMoney = moneyInpurtEditText.getText().toString();
                int monrey = Integer.parseInt(requestMoney);
                if (Util.isNull(requestMoney)) {
                    Util.show("请输入提现金额", context);
                    return;
                }
                if (0 != monrey % cashBean.getmCashMagn() || monrey < cashBean.getmCashStarMoney()) {
                    Util.show("提现金额必须大于等于" + cashBean.getmCashStarMoney() + "，并且是" + cashBean.getmCashMagn() + "的倍数。", context);
                    return;
                }


                if (cashBean.getmCashType().getType_().equals("-1")) {
                    Util.show("请选择提现方式。", context);
                    return;
                }

                if (!requestMoney.matches("^\\d+$")) {
                    Util.show("提现金额格式错误。", context);
                    return;
                }
                if (monrey > cashBean.getmAccountBalance()) {
                    Util.show("账户余额不足。", context);
                    return;
                }


                submitmoney(monrey);
                break;
            case R.id.all_tv:
                moneyInpurtEditText.setText(account_money);
                break;
        }

    }


    boolean isopen = true;


    private void setCashInstructions(boolean isk) {
        boolean isCenter = (cashBean.getmCashObjects() == OperationsCenter);
        String tixianinfo = "提现说明：<br/>";
        //只有1000及1000以上才可提现（100的整数倍）；
        tixianinfo += "1、只有大于等于" + (int)cashBean.getmCashStarMoney() + "及" + cashBean.getmCashStarMoney() + "以上才可提现" + "(" + cashBean.getmCashMagn() + "的整数倍)才可提现；<br/>";

        tixianinfo += "2、提现时需扣除" + cashBean.getmCashFees() * 100 + "%的个人综合费用；<br/> ";
        tixianinfo += isCenter ? "提现申请待运营中心审核通过后，由运营中心转款至个人账户" : "3、提现金额为" + netmoney + "或" + netmoney + "以下时，仅支持提现到微信或支付宝，"
                + netmoney + "以上时，支持提现到微信、支付宝、银行卡；<br/> ";

        tixianinfo += isCenter ? "4、为保证资金安全,提现只能提到本人账户<br/>" : "4、为保证资金安全，提现只能提到本人的相关账户；<br/>";


        tixianinfo += isCenter ? "" : "5、5个工作日到账，请耐心等待。<br/>";

        if (!isk) {
            if (isopen) {
                isopen = false;
                downup.animate().rotation(180);

                tixianinfotv.setVisibility(View.VISIBLE);
            } else {
                isopen = true;
                downup.animate().rotation(0);

                tixianinfotv.setVisibility(View.GONE);
            }
        }


        tixianinfotv.setText(Html.fromHtml(tixianinfo));

    }


    private void setSubmitPass(String twoP, final TextView view, final PopupWindow mPopUpWindow) {


        if (Util.isNull(twoP)) {
            Util.show("请输入交易密码。", context);
            return;
        }

        String requestMoney = moneyInpurtEditText.getText().toString();


        final int m = Integer.parseInt(requestMoney);


        final String tp = new MD5().getMD5ofStr(twoP);
        setSubmitbuttonState(false, view);

        switch (cashBean.getmCashAccountName()) {
            case "bus":
            case "red_c":
            case "Present_account":
                yewu_xianSubmit(m, tp, new SubmitCallBack() {

                    @Override
                    public void successCallBack() {
                        Util.showIntent("提现申请成功。是否需要去查看？", context, AccountParticularsListActivity.class,
                                new String[]{"title", "type_"}, new String[]{"提现", type_});

//                        Util.showIntent("提现申请成功。是否需要去查看？", context, AccountParticularsListActivity.class,
//                                new String[]{"title", "type_"}, new String[]{"提现账户", type_});
                    }

                    @Override
                    public void failureCallBack(String message) {
                        Util.show(message + "", context);
                    }


                    @Override
                    public void completeCallBack() {
                        mPopUpWindow.dismiss();
                        setSubmitbuttonState(true, view);
                    }
                });
                break;
//            case "Present_account":
//                tixianjinge(requestMoney + "", tp, new SubmitCallBack() {
//                    @Override
//                    public void successCallBack() {
//                        Util.showIntent("提现申请成功。是否需要去查看？", context, AccountParticularsListActivity.class,
//                                new String[]{"title", "type_"}, new String[]{"提现账户", type_});
//                    }
//
//                    @Override
//                    public void failureCallBack(String message) {
//
//                    }
//
//                    @Override
//                    public void completeCallBack() {
//                        mPopUpWindow.dismiss();
//                        setSubmitbuttonState(true, view);
//                    }
//                });
//                break;
            case "cash_c":
                tixianjinge(requestMoney + "", tp, new SubmitCallBack() {
                    @Override
                    public void successCallBack() {
                        Util.showIntent("提现申请成功。是否需要去查看？", context, AccountParticularsListActivity.class,
                                new String[]{"title", "type_"}, new String[]{"提现", type_});
                    }

                    @Override
                    public void failureCallBack(String message) {

                    }

                    @Override
                    public void completeCallBack() {
                        mPopUpWindow.dismiss();
                        setSubmitbuttonState(true, view);
                    }
                });
                break;


        }

    }

    interface SubmitCallBack {
        void successCallBack();

        void failureCallBack(String message);

        void completeCallBack();
    }


    /**
     * 业务 现金券提现
     *
     * @param m              提现金额
     * @param tp             提现密码
     * @param submitCallBack
     */
    private void yewu_xianSubmit(final double m, final String tp, final SubmitCallBack submitCallBack) {


        Util.asynTask(context, "申请提现中...", new IAsynTask() {

            @Override
            public void updateUI(Serializable runData) {


                Log.e("业务账户提现返回", runData.toString() + "LL");


                if (submitCallBack != null) {


                    if ("success".equals(runData)) {

                        submitCallBack.successCallBack();


                    } else {
                        submitCallBack.failureCallBack(runData.toString());

                    }
                    submitCallBack.completeCallBack();
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web();
                switch (cashBean.getmCashAccountName()) {


                    case "bus":
                        web = new Web(Web.requestTX, "userid=" + UserData.getUser().getUserId() + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd() + "&handler=&money=" + m + "&twoPwd=" + tp + "&tx_type=" + cashBean.getmCashType().getType_()
                                + "&pro_u=" + ((cashBean.getmCashObjects() == OperationsCenter) ? cashBean.getOperationsCenteruserid() : "远大商城")
                        );
                        break;
                    case "red_c":
                        web = new Web("/Red_Cash_TX", "userid=" + UserData.getUser().getUserId() + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd() + "&handler=&money=" + m + "&twoPwd=" + tp + "&tx_type=" + cashBean.getmCashType().getType_()
                                + "&pro_u=" + ((cashBean.getmCashObjects() == OperationsCenter) ? cashBean.getOperationsCenteruserid() : "远大商城")
                        );
                        break;
                    case "Present_account":
                        web = new Web("/Present_account_TX", "userid=" + UserData.getUser().getUserId() + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd() + "&handler=&money=" + m + "&twoPwd=" + tp + "&tx_type=" + cashBean.getmCashType().getType_()
                                + "&pro_u=" + ((cashBean.getmCashObjects() == OperationsCenter) ? cashBean.getOperationsCenteruserid() : "远大商城")
                        );
                        break;

                }

                return web.getPlan();
            }
        });
    }


    private void submitmoney(int monrey) {


        View mContentView = LayoutInflater.from(context).inflate(R.layout.layout_input_password, null);
        mContentView.
                setBackground(SelectorFactory.newShapeSelector()
                        .setDefaultBgColor(Color.parseColor("#ffffff"))
                        .setStrokeWidth(Util.dpToPx(context, 1))
                        .setCornerRadius(Util.dpToPx(context, 10))
                        .setDefaultStrokeColor(Color.parseColor("#bf767675"))
                        .create()
                );
        int with = (int) (Util.getScreenWidth() * 0.7);

        final EditText topassword = (EditText) mContentView.findViewById(R.id.topassword);
        final TextView tixiantis = (TextView) mContentView.findViewById(R.id.tixiantis);
        double tixianfei = (monrey * cashBean.getmCashFees());
        tixiantis.setText("到账金额" + (monrey - tixianfei) + "元,个人综合费用" + tixianfei + "元");
        final PopupWindow mPopUpWindow = ShowPopWindow.showShareWindow(mContentView, context, with, ViewGroup.LayoutParams.WRAP_CONTENT, 0);

        TextView submit_password = (TextView) mContentView.findViewById(R.id.submit_password);
        submit_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSubmitPass(topassword.getText().toString(), (TextView) v, mPopUpWindow);
            }
        });


    }


    private void tixianjinge(final String money, final String twoPwd, final SubmitCallBack submitCallBack) {

//Present_account_TX

        final CustomProgressDialog cpd = Util.showProgress("申请提现中...", this);

        String call = "CashCouponTX";
        if (type_.equals("4")) {
            call = "Present_account_TX";
        }

        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + call + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        + "&twoPwd=" + twoPwd + "&money=" + money + "&tx_type=" + cashBean.getmCashType().getType_()

                        + "&pro_u=" + ((cashBean.getmCashObjects() == OperationsCenter) ? cashBean.getOperationsCenteruserid() : "远大商城")
                ,
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        submitCallBack.failureCallBack("网络超时！");
                        return;
                    }

                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            submitCallBack.failureCallBack("网络异常，请重试！");
                            return;
                        }


                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            submitCallBack.failureCallBack(json.getString("message"));
                            return;
                        }


                        submitCallBack.successCallBack();


                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();

                        submitCallBack.completeCallBack();
                    }

                    @Override
                    public void fail(Throwable e) {
                        submitCallBack.failureCallBack("");
                    }
                });


    }

    private void settagicon(View view) {

        switch (view.getId()) {
            case R.id.weixin_pay_icon:
                if (weixinstate.equals("0")) {
                    showtobanding(AddBankCardActivity.tagweixin);
                    return;
                } else if (weixinstate.equals("-1 ")) {
                    Toast.makeText(context, "未检查到是否绑定了微信", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
            case R.id.zhifubao_pay_icon:
                if (zhifubaoinstate.equals("0")) {
                    showtobanding(AddBankCardActivity.tagzhifubao);
                    return;
                } else if (zhifubaoinstate.equals("-1")) {
                    Toast.makeText(context, "未检查到是否绑定了支付宝", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
            case R.id.bank_pay_icon:


                if (bankinstate.equals("0")) {
                    showtobanding(AddBankCardActivity.tagbank);
                    return;
                } else if (bankinstate.equals("-1")) {
                    Toast.makeText(context, "未检查到是否绑定了银行卡", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
        }

        weixin_pay_icon.setImageResource(R.drawable.pay_item_no_checked1);
        zhifubao_pay_icon.setImageResource(R.drawable.pay_item_no_checked1);
        bank_pay_icon.setImageResource(R.drawable.pay_item_no_checked1);
        switch (view.getId()) {
            case R.id.weixin_pay_icon:
                if (weixinstate.equals("1")) {
                    weixin_pay_icon.setImageResource(R.drawable.pay_item_checked1);
                    cashBean.setmCashType(CashBean.CashType.weixin);

                }

                break;
            case R.id.zhifubao_pay_icon:
                if (zhifubaoinstate.equals("1")) {
                    zhifubao_pay_icon.setImageResource(R.drawable.pay_item_checked1);
                    cashBean.setmCashType(CashBean.CashType.zhifubao);

                }

                break;
            case R.id.bank_pay_icon:
                if (bankinstate.equals("1")) {
                    bank_pay_icon.setImageResource(R.drawable.pay_item_checked1);
                    cashBean.setmCashType(CashBean.CashType.bank);

                }

                break;
        }

    }

    private void showtobanding(final String str) {
        View mContentView = LayoutInflater.from(context).inflate(R.layout.d_video_audio_dialog, null);
        int with = (int) (Util.getScreenWidth() * 0.75);
        final PopupWindow mPopUpWindow = ShowPopWindow.showShareWindow(mContentView, context, with, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        TextView topopwind = (TextView) mContentView.findViewById(R.id.video_audio_dialog_content);
        TextView cancel = (TextView) mContentView.findViewById(R.id.video_audio_dialog_cancel);
        TextView sure = (TextView) mContentView.findViewById(R.id.video_audio_dialog_sure);
        sure.setText("去绑定");
        topopwind.setText("对不起,您还未" + str + "！是否去绑定。");
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopUpWindow.dismiss();

                Util.showIntent(context, AddBankCardActivity.class, new String[]{"title"}, new String[]{str});
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopUpWindow.dismiss();
            }
        });
    }

    // 网络请求检查账户是否绑定 微信 支付宝 银行卡

    private void check_band_account() {
        final CustomProgressDialog cpd = Util.showProgress("账户检查中...", this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + "get_tx_way" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                ,
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


                        Gson gson = new Gson();
                        UserAccountWithdrawal userAccountWithdrawal = gson.fromJson(result.toString(), UserAccountWithdrawal.class);
                        UserAccountWithdrawal.ListBean listBean = userAccountWithdrawal.getList().get(0);
                        weixinstate = listBean.getWx();
                        zhifubaoinstate = listBean.getAlipay();
                        bankinstate = listBean.getBank();
                        setCashToAccountState();


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


    // 检查是否可以提现
    private void isCash() {
        final CustomProgressDialog cpd = Util.showProgress("加载中...", this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + "GetdateMessage" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&type=" + CashAccount.accountType(cashBean.getmCashAccountName()),
                new NewWebAPIRequestCallback() {

                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());

                        downup.setVisibility(View.VISIBLE);
                        tixianinfotv.setVisibility(View.VISIBLE);
                        try {
                            netmoney = json.getInteger("money");
                        } catch (Exception e) {

                        }

                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            String info = "";
                            try {
                                info = json.getString("info");
                            } catch (Exception e) {

                            }
                            setSubmitState(false, info, json.getString("message"));
                            return;
                        }

                        setSubmitState(true, "", "");
                        check_band_account();


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


    public void setSubmitbuttonState(boolean isclick, TextView view) {

        if (view != null) {
            if (isclick) {
                submit.setClickable(true);
                submit.setBackgroundResource(R.drawable.redboxarc);
            } else {
                submit.setClickable(false);
                submit.setBackgroundResource(R.drawable.redboxarcnoclick);
                view.setText("正在提交中");
            }
        }

    }


    public void setSubmitState(boolean isclick, String info, String string) {


        if (isclick) {
            submit.setBackgroundResource(R.drawable.redboxarc);
            submit.setClickable(true);
            submit.setText("确认");
        } else {

            if (!Util.isNull(string)) {
                submit.setText(string);
            }
            if (!Util.isNull(info)) {
                VideoAudioDialog dialog = new VideoAudioDialog(this);
                dialog.setTitle("温馨提示");
                dialog.setRightColor(getResources().getColor(R.color.red));
                dialog.showcancel(View.GONE);
                dialog.setRight("确认");
                dialog.setContent(info);
                dialog.show();
            }


            submit.setBackgroundResource(R.drawable.redboxarcnoclick);
            submit.setClickable(false);
        }

    }

}

