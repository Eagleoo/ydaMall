package com.mall.CapitalAccount;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.android.appDemo4.AliPayNet;
import com.alipay.android.appDemo4.AuthResult;
import com.bumptech.glide.Glide;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lin.component.CustomProgressDialog;
import com.mall.BasicActivityFragment.BasicActivity;
import com.mall.MessageEvent;
import com.mall.model.Bank;
import com.mall.model.ThirdPartyLogin;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.util.BankCardTextWatcher;
import com.mall.util.BankUtil;
import com.mall.util.IAsynTask;
import com.mall.util.L;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.SelectorFactory;
import com.mall.view.ShowPopWindow;
import com.mall.view.messageboard.MyToast;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

import static com.mall.util.BankUtil.getNameOfBank;
import static com.mall.util.Util.MSG_AUTH_CANCEL;
import static com.mall.util.Util.MSG_AUTH_COMPLETE;
import static com.mall.util.Util.MSG_AUTH_ERROR;
import static com.mall.util.Util.MSG_LOGIN;

public class AddBankCardActivity extends BasicActivity implements PlatformActionListener {

    @BindView(R.id.nametag)
    TextView nametag;
    @BindView(R.id.caridtag)
    TextView caridtag;
    @BindView(R.id.banknametag)
    TextView banknametag;
    @BindView(R.id.useridtag)
    TextView useridtag;
    @BindView(R.id.phonetag)
    TextView phonetag;
    @BindView(R.id.passwordtag)
    TextView passwordtag;

    @BindView(R.id.banknumber)
    EditText banknumber;


    @BindView(R.id.showbanknameline)
    View showbanknameline;

    @BindView(R.id.next_tv)
    TextView next_tv;

    @BindView(R.id.lefttitle_tv)
    TextView lefttitle;

    @BindView(R.id.phone)
    EditText phone;


    @BindView(R.id.phone_yanzlin)
    View phone_yanzlin;

    @BindView(R.id.yzmpic_view)
    ImageView yzmpic_view;

    @BindView(R.id.yzminput)
    public EditText code;//

    @BindView(R.id.update_basic_user_info_send_code)
    public Button send_code;

    @BindView(R.id.yzmpic_code)
    public EditText yzmpic_code;//


    @BindView(R.id.name_tv)
    public TextView name_tv;

    @BindView(R.id.bankname)
    public TextView bAccount;

    @BindView(R.id.bank_idCard)
    public TextView idCard;


    @BindView(R.id.pass_editText)
    public EditText pass_editText;

    @BindView(R.id.bankzhihangname)
    public EditText bankzhihangname;

    @BindView(R.id.banknamezhihangtag)
    public TextView banknamezhihangtag;

    @BindView(R.id.idcard_line)
    public LinearLayout idcard_line;
    @BindView(R.id.phone_line)
    public LinearLayout phone_line;
    @BindView(R.id.passwordline)
    public LinearLayout passwordline;


    private DbUtils db = null;
    private DbUtils dbUtils;


    String title = "";


    private int s = 60;

    private String smsState = "";

    CustomProgressDialog cpdlogin;
    private Platform platform;

    Bank bank;

    public static String tagbank = "添加银行卡";
    public static String tagweixin = "绑定微信";
    public static String tagzhifubao = "绑定支付宝";
    public static String tagQQ = "绑定QQ";


    class TimeThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (s > 0) {
                s--;
                Message message = new Message();
                message.what = 707;
                message.obj = s;
                handler.sendMessage(message);
                if (0 == s)
                    break;
                try {
                    this.sleep(1000);//睡1s
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    ;

    @Override
    public int getContentViewId() {
        return R.layout.activity_add_bank_card;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {
        init();
    }

    @Override
    public void EventCallBack(MessageEvent messageEvent) {

    }

    private void init() {
        dbUtils = DbUtils.create(this, "YDLoginUsers");
        db = DbUtils.create(context);
        try {
            db.deleteAll(ThirdPartyLogin.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        initgetIntent();
        initview();
        getBank();
    }

    public void getBank() {
        Util.asynTask(this, "正在获取银行类型...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<Bank>> map = (HashMap<String, List<Bank>>) runData;
                List<Bank> list1 = map.get("list");
                List<String> data = new ArrayList<String>(list1.size());
                for (Bank bank : list1) {
                    data.add(bank.getDesc());
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getBank, "");
                List<Bank> list1 = web.getList(Bank.class);
                HashMap<String, List<Bank>> map = new HashMap<String, List<Bank>>();
                map.put("list", list1);
                return map;
            }
        });
    }

    private void initgetIntent() {
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        lefttitle.setText(title);

    }

    private void initview() {
        User user = UserData.getUser();
        String pn = user.getMobilePhone();
        Log.e("图片地址", "http://" + Web.webImage + "/ashx/ImgCode.ashx?action=imgcode&phone=" + pn);
        if (!Util.isNull(pn)) {
            if (Util.isPhone(pn)) {

                Glide.with(context).load("http://" + Web.webImage + "/ashx/ImgCode.ashx?action=imgcode&phone=" + pn).skipMemoryCache(true).into(yzmpic_view);
            }
        }

        if (user.getLevelId().equals("4") || user.getLevelId().equals("6")) {
            idcard_line.setVisibility(View.GONE);
            phone_line.setVisibility(View.GONE);
        }

        inittag();
        initviewbing();
        next_tv.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#FF2146"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setCornerRadius(Util.dpToPx(context, 3))
                .create()
        );
    }

    private void initviewbing() {
        User user = UserData.getUser();
        name_tv.setText(user.getName());
        idCard.setText(!Util.isNull(UserData.getUser().getIdNo()) ? UserData.getUser().getIdNo() : UserData.getUser().getPassport());
        if (Util.isNull(user.getMobilePhone())) {
            phone.setCursorVisible(true);
        } else {
            phone.setCursorVisible(false);
            phone.setText(user.getMobilePhone());
        }
    }

    private void inittag() {
        if (title.equals(tagbank)) {
            nametag.setText(Util.justifyString("持卡人姓名", 5));
            caridtag.setText(Util.justifyString("银行卡号", 5));
            banknametag.setText(Util.justifyString("开户银行", 5));
            banknumber.setHint("请输入您的银行卡号");
            banknamezhihangtag.setText(Util.justifyString("开户行支行", 5));
            BankCardTextWatcher.bind(banknumber);
            banknumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (!Util.isNull(s)) {
                        Log.e("CharSequence", "CharSequence" + s);
                        String str = s.toString();
                        if (str.trim().replaceAll(" ", "").length() < 16 || str.trim().replaceAll(" ", "").length() > 19) {
                            showbanknameline.setVisibility(View.GONE);

                            bAccount.setText("");
                            bankzhihangname.setText("");
                            bAccount.setOnClickListener(null);
                            bank = null;
                        } else {
                            if (title.equals(tagbank)) {
                                String number = banknumber.getText().toString().trim().replaceAll("\\s*", "");
                                Log.e("number", number);

                                bank = BankUtil.getBank(getNameOfBank(number));
                                if (!Util.isNull(bank)) {
                                    showbanknameline.setVisibility(View.VISIBLE);
                                    bAccount.setText(bank.getName());
                                    bAccount.setOnClickListener(null);
                                } else {
                                    bank = null;
                                    MyToast.makeText(context, "请输入你的开户银行", 5).show();
                                    showbanknameline.setVisibility(View.VISIBLE);

                                    bAccount.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            View mContentView = LayoutInflater.from(context).inflate(R.layout.layout_addbankcard, null);
                                            ListView banklist = (ListView) mContentView.findViewById(R.id.banklist);
                                            banklist.setAdapter(new BaseAdapter() {
                                                @Override
                                                public int getCount() {
                                                    return BankUtil.getListStr().size();
                                                }

                                                @Override
                                                public Object getItem(int position) {
                                                    return position;
                                                }

                                                @Override
                                                public long getItemId(int position) {
                                                    return position;
                                                }

                                                @Override
                                                public View getView(int position, View convertView, ViewGroup parent) {
                                                    View view = LayoutInflater.from(context).inflate(R.layout.itemtext, parent, false);
                                                    TextView textView = (TextView) view.findViewById(R.id.tv);
                                                    textView.setText(BankUtil.getListStr().get(position));
                                                    return view;
                                                }
                                            });
                                            int with = (int) (Util.getScreenWidth() * 0.7);
                                            final PopupWindow mPopUpWindow = ShowPopWindow.showShareWindow(mContentView, context, with, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                                            banklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    bank = BankUtil.getBank(BankUtil.getListStr().get(position));
                                                    bAccount.setText(BankUtil.getListStr().get(position));
                                                    mPopUpWindow.dismiss();
                                                }
                                            });

                                        }
                                    });
                                }
                            }
                        }


                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        } else if (title.equals(tagweixin)) {
            banknumber.setInputType(InputType.TYPE_CLASS_TEXT);
            nametag.setText(Util.justifyString("真实姓名", 5));
            banknametag.setText(Util.justifyString("微信账户", 5));
            caridtag.setText(Util.justifyString("微信账户", 5));
            banknumber.setHint("请确定输入的微信号与绑定的一致");
            banknumber.setText(UserData.getUser().getWeixin());
//            banknumber.setCursorVisible(false);
        } else if (title.equals(tagzhifubao)) {
            banknumber.setInputType(InputType.TYPE_CLASS_TEXT);
            nametag.setText(Util.justifyString("真实姓名", 5));
            banknametag.setText(Util.justifyString("支付宝账户", 5));
            caridtag.setText(Util.justifyString("支付宝账户", 5));
            banknumber.setHint("请输入您的支付宝账户");
        } else if (title.equals(tagQQ)) {
            nametag.setText(Util.justifyString("真实姓名", 5));
            banknametag.setText(Util.justifyString("QQ账户", 5));
            caridtag.setText(Util.justifyString("QQ账户", 5));
            banknumber.setHint("请输入您的QQ账户");
            passwordline.setVisibility(View.GONE);
        }
        if (Util.isNull(UserData.getUser().getPassport())) {
            useridtag.setText(Util.justifyString("身份证", 5));
        } else {
            useridtag.setText(Util.justifyString("证件号", 5));
        }

        phonetag.setText(Util.justifyString("联系电话", 5));
        passwordtag.setText(Util.justifyString("交易密码", 5));


    }

    @OnClick({R.id.update_basic_user_info_send_code, R.id.addbankcardprompt, R.id.close, R.id.next_tv})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.update_basic_user_info_send_code:
                String pn = phone.getText().toString();
                if (Util.isNull(pn)) {
                    Util.MyToast(context, "请输入的手机号", 1);
                    return;
                }
                if (!Util.isPhone(phone.getText().toString())) {
                    Util.show("您的手机号格式错误！", context);
                    return;
                    //？？？
                }

                Util.asynTask(context, "正在发送验证码", new IAsynTask() {

                    @Override
                    public void updateUI(Serializable runData) {
                        // TODO Auto-generated method stub
                        if (null == runData) {
                            Util.show("网络错误，请重试！", context);
                            phone.setCompoundDrawables(null, null, null, null);
                            return;
                        }
                        if ((runData + "").equals("图形验证码不正确")) {
                            Glide.with(context).load("http://" + Web.webImage + "/ashx/ImgCode.ashx?action=imgcode&phone=" + phone.getText().toString()).skipMemoryCache(true).into(yzmpic_view);
                            Util.show(runData + "", context);
                            return;
                        }
                        if ((runData + "").startsWith("success:")) {
                            String[] ayy = (runData + "").split(":");
                            phone.setTag(-707, ayy[1]);
                            Resources res = context.getResources();
                            Drawable ok = res.getDrawable(R.drawable.registe_success);
                            ok.setBounds(0, 0, ok.getMinimumWidth(), ok.getMinimumHeight());
                            smsState = ayy[1];
                            phone.setCompoundDrawables(null, null, ok, null);
                            if (Web.url == Web.test_url || Web.url == Web.test_url2) {
                                code.setText(ayy[1]);
                            }
                            //开启线程
                            new TimeThread().start();
                        } else {
                            Util.show(runData + "", context);
                            phone.setCompoundDrawables(null, null, null, null);
                        }
                    }

                    @Override
                    public Serializable run() {
                        // TODO Auto-generated method stub
                        Web web = new Web(Web.sendPhoneValidataCode,
                                "userId=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&phone="
                                        + phone.getText().toString() + "&imgcode=" + yzmpic_code.getText().toString());
                        return web.getPlan();
                    }
                });

                break;
            case R.id.next_tv:
                if (!title.equals(tagQQ)) {
                    if (Util.isNull(pass_editText.getText().toString())) {
                        MyToast.makeText(context, "请输入交易密码", 5).show();
                        return;
                    }
                }

                String number = banknumber.getText().toString().trim().replaceAll(" ", "");
                if (title.equals(tagweixin)) {
                    bindzhifubao(number, "1");
                } else if (title.equals(tagzhifubao)) {
                    bindzhifubao(number, "0");
                } else if (title.equals(tagbank)) {
                    bindbank();
                } else if (title.equals(tagQQ)) {
                    bindqq();
                }


//                Intent intent =new Intent(context,MyAccount_BankListActivity.class);
//                intent.putExtra(MyAccount_BankListActivity.TITLETAG,MyAccount_BankListActivity.tag1);
//                startActivity(intent);
                break;
            case R.id.close:
                finish();
                break;
            case R.id.addbankcardprompt:
                View mContentView = LayoutInflater.from(context).inflate(R.layout.addbankidinstructions, null);
                mContentView.
                        setBackground(SelectorFactory.newShapeSelector()
                                .setDefaultBgColor(Color.parseColor("#ffffff"))
                                .setStrokeWidth(Util.dpToPx(context, 1))
                                .setCornerRadius(Util.dpToPx(context, 5))
                                .setDefaultStrokeColor(Color.parseColor("#bf767675"))
                                .create()
                        );

                String str = "为保证账户资金的安全,只能绑定用户本人的";

                if (title.equals(tagbank)) {
                    str += "银行卡";
                } else if (title.equals(tagweixin)) {
                    str += "微信";
                } else if (title.equals(tagzhifubao)) {
                    str += "支付宝";
                }
                int with = (int) (Util.getScreenWidth() * 0.6);
                TextView message = (TextView) mContentView.findViewById(R.id.message);
                message.setText(str);
                final PopupWindow mPopUpWindow = ShowPopWindow.showShareWindow(mContentView, context, with, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                TextView closepopwind = (TextView) mContentView.findViewById(R.id.close_popwind);

                closepopwind.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopUpWindow.dismiss();
                    }
                });

                break;
        }
    }

    private void bindqq() {
        authorize(new QZone());
    }

    private void authorize(Platform plat) {
        if (title.equals(tagQQ)) {
            cpdlogin = Util.showProgress("QQ登录中...", this);
        } else {
            cpdlogin = Util.showProgress("微信登录中...", this);
        }


        plat.setPlatformActionListener(this);
        plat.removeAccount(true);
        if (plat instanceof QZone) {
            if (Util.isInstall(this, "com.tencent.mobileqq"))
                plat.SSOSetting(false);
            else
                plat.SSOSetting(true);
        } else
            plat.SSOSetting(false);
        plat.showUser(null);
    }


    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
        if (action == Platform.ACTION_USER_INFOR) {
            L.e("[" + hashMap + "]");
            L.e("------User getUserGender ---------" + platform.getDb().getUserGender());
            L.e("------User getUserIcon ---------" + platform.getDb().getUserIcon());
            L.e("------User getUserName ---------" + platform.getDb().getUserName());
            L.e("------User Name ---------" + platform.getName());
            L.e("------User ID ---------" + platform.getDb().getUserId());
            L.e("------Open ID ---------" + platform.getDb().getUserId());
//            banknumber.setText(platform.getDb().getUserName());
            this.platform = platform;

            handler.sendEmptyMessage(MSG_AUTH_COMPLETE);

        }
    }

    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
        if (action == Platform.ACTION_USER_INFOR) {
            handler.sendEmptyMessage(MSG_AUTH_ERROR);
        }
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            handler.sendEmptyMessage(MSG_AUTH_CANCEL);
        }
    }

    private void bindingaccount(String openid, String type_, String code, String code_img) {
        String message = "";
        if (type_.equals("1")) {
            message = "微信";
        } else if (type_.equals("2")) {
            message = "支付宝";
        }

        final CustomProgressDialog cpd = Util.showProgress(message + "绑定中...", this);
        final String finalMessage = message;

        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + "band_wx_alipay" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        + "&open_id=" + openid + "&type_=" + type_ + "&code=" + code + "&code_img=" + code_img + "&twoPwd=" + new MD5().getMD5ofStr(pass_editText.getText().toString())
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
                        if (200 == json.getIntValue("code")) {
                            Util.show("你已经成功绑定了" + finalMessage, context);
                        }

                        finish();


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


    private void bindzhifubao(final String str, String tag) {

        if (tag.equals("1")) {

            Util.asynTask(this, "正在更新您的资料...", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if ("success".equals(runData + "")) {
//                        Util.show("你已经成功绑定了支付宝", context);
//                        finish();
                        authorize(new Wechat());
                    } else {
                        Util.show(runData + "", context);
                    }

                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.updateUInfo_2017_bypwd, "userId="
                            + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&mail="
                            + UserData.getUser().getMail() + "&qq="
                            + UserData.getUser().getQq() + "&weixin="
                            + str + "&nickName="
                            + Util.get(UserData.getUser().getNickName()) + "&sex="
                            + Util.get(UserData.getUser().getSex()) + "&alipay=" + UserData.getUser().getAlipay()
                            + "&secpwd=" +
                            new MD5().getMD5ofStr(pass_editText.getText().toString())
                    );
                    return web.getPlan();
                }
            });


        } else {

            Util.asynTask(context, "正在更新您的资料...", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if ("success".equals(runData + "")) {


                        try {


                            new AliPayNet(AddBankCardActivity.this).functionlogIn(new AliPayNet.dologincallback() {
                                @Override
                                public void call(final AuthResult authResult) {
                                    String resultStatus = authResult.getResultStatus();
                                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                                        User user = UserData.getUser();
                                        user.setAlipay(str);
                                        UserData.setUser(user);
                                        bindingaccount(authResult.getUser_id(), "2", "", "");
                                    } else {
                                        Toast.makeText(context,
                                                "授权失败", Toast.LENGTH_SHORT).show();
                                        Log.e("授权失败", String.format("authCode:%s", authResult.getAuthCode()));
                                    }

                                }
                            });


                        } catch (Exception e) {

                        }


                    } else {
                        Util.show(runData + "", context);
                    }

                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.updateUInfo_2017_bypwd, "userId="
                            + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&mail="
                            + UserData.getUser().getMail() + "&qq="
                            + UserData.getUser().getQq() + "&weixin="
                            + UserData.getUser().getWeixin() + "&nickName="
                            + Util.get(UserData.getUser().getNickName()) + "&sex="
                            + Util.get(UserData.getUser().getSex()) + "&alipay=" + str
                            + "&secpwd=" +
                            new MD5().getMD5ofStr(pass_editText.getText().toString())
                    );
                    return web.getPlan();
                }
            });


        }


    }


    private void bindbank() {
        final String name = name_tv.getText().toString();
        final String account = bankzhihangname.getText().toString();
        final String idcard = idCard.getText().toString();
        final String uPhone = phone.getText().toString();
        final String bType_temp = bAccount.getText().toString().trim();
        final String two = pass_editText.getText().toString();
        final String yzmcode = code.getText().toString();

        if (bType_temp.equals("选择银行")) {
            Util.show("请选择银行", context);
            return;
        }

        if (bank == null) {
            Util.show("请选择支持的银行类型", context);
            return;
        }
        final String bType = bank.getId();
        final String bankcard = banknumber.getText().toString().trim().replaceAll(" ", "");
        if (Util.isNull(name)) {
            Util.show("请输入您的真实姓名.", context);
            return;
        }
        if (Util.isNull(account)) {
            Util.show("请输入您的开户行支行.", context);
            return;
        }


        int levelid = Integer.parseInt(UserData.getUser().getLevelId());
        if (6 == levelid || 4 == levelid) {

        } else {
            if (Util.isNull(idcard)) {
                Util.show("请输入您的身份证.", context);
                return;
            }

            if (Util.isNull(uPhone)) {
                Util.show("请输入您的手机号码.", context);
                return;
            }
        }


        if (Util.isNull(bankcard)) {
            Util.show("请输入您的银行卡号.", context);
            return;
        }
        if (Util.isNull(two)) {
            Util.show("请输入您的交易密码.", context);
            return;
        }


        Util.asynTask(context, "正在完善您的银行信息...", new IAsynTask() {

            @Override
            public void updateUI(Serializable runData) {
                if ("success".equals(runData)) {
                    Web.reDoLogin();
                    // Util.showIntent("修改完成，是否需要去提现！",
                    // UpdateBankFrame.this, RequestTX.class);
                    Util.show("银行卡绑定成功", context);
                    User user = UserData.getUser();
//                    bankname.split("·")[0];
                    try {
                        user.setBank(bType_temp.split("·")[0]);
                        UserData.setUser(user);
                    } catch (Exception e) {
                        Log.e("Add_Exception", e.toString());
                    }
                    finish();
                } else
                    Util.show(runData + "", context);
            }

            @Override
            public Serializable run() {
                Web web;
                if (!Util.isNull(UserData.getUser().getPassport())) {
                    web = new Web(Web.updateBank,
                            "userid=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                                    + "&twoPwd=" + new MD5().getMD5ofStr(two) + "&accountName=" + Util.get(account)
                                    + "&name=" + Util.get(name) + "&passport=" + idcard + "&phone=" + uPhone
                                    + "&bankType=" + bType + "&banCard=" + bankcard + "&yzmcode=" + "" + "&overseas=" + 1 + "&idCard=");
                } else {
                    web = new Web(Web.updateBank,
                            "userid=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                                    + "&twoPwd=" + new MD5().getMD5ofStr(two) + "&accountName=" + Util.get(account)
                                    + "&name=" + Util.get(name) + "&idCard=" + idcard + "&phone=" + uPhone
                                    + "&bankType=" + bType + "&banCard=" + bankcard + "&yzmcode=" + "" + "&overseas=" + 0);

                }

                return web.getPlan();
            }
        });
    }


    private void bindingQQ(final String sessionId) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("third_user", sessionId + "");
        param.put("type", "0");
        NewWebAPI.getNewInstance().getWebRequest("/User.aspx?call=SL_Binding_third_user", param,
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
                        // 说明系统错误
                        if (707 != code && 200 != code) {
                            Util.show(message, context);
                            // 说明绑定过，
                        } else if (200 == code) {
                            Util.show("该QQ号已经被绑定.", context);
                        } else {
                            final ThirdPartyLogin waitLoginUser = new ThirdPartyLogin();
                            waitLoginUser.shareId = sessionId + "";
                            waitLoginUser.userId = "";
                            waitLoginUser.userName = "0";
                            try {
                                db.deleteAll(ThirdPartyLogin.class);
                                db.save(waitLoginUser);
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            dologin(UserData.getUser().getMd5Pwd(), UserData.getUser().getUserId(), sessionId);

                        }
                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                    }
                });
    }

    private void dologin(final String lpwd, final String nName, String sessionId) {
        Log.e("dologin", "lpwd" + lpwd + "nName" + nName + "sessionId" + sessionId);

        final CustomProgressDialog cpd = Util.showProgress("QQ绑定中...", this);
        NewWebAPI.getNewInstance().doLogin(nName, lpwd, "", "", "", "", sessionId, "0", "",
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (Util.isNull(result)) {
                            Util.show("网络超时，请重试！", context);
                            return;
                        }
                        // LogUtils.e(result.toString());
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {

                        }
                        User user = JSON.parseObject(result.toString(), User.class);
                        user.setUserName(nName);
                        user.setLoginDate("这是我保存的日期");
                        user.setId(Long.parseLong(user.getUserNo()));
                        user.setMd5Pwd(lpwd);
                        MyToast.makeText(context, "QQ绑定成功", 5);
                        finish();

                    }

                    @Override
                    public void fail(Throwable e) {
                        super.fail(e);
                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        cpd.cancel();
                        cpd.dismiss();
                    }
                });

    }
    //开线程

    //使用Handler更新UI

    MyHanlder handler = new MyHanlder(this);

    private static class MyHanlder extends Handler {
        private WeakReference weakReference;

        public MyHanlder(Context context) {
            weakReference = new WeakReference(context);  //将持有的外部类转换弱引用

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AddBankCardActivity addBankCardActivity = (AddBankCardActivity) weakReference.get();


            if (addBankCardActivity.cpdlogin != null) {
                addBankCardActivity.cpdlogin.dismiss();
                addBankCardActivity.cpdlogin.cancel();
            }
            if (707 == msg.what) {
                if ("0".equals(msg.obj + "")) {
                    addBankCardActivity.send_code.setText("短信验证");
                    addBankCardActivity.send_code.setEnabled(true);
                    addBankCardActivity.s = 60;
                } else {
                    int ss = Util.getInt(msg.obj);
                    if (10 > ss)
                        ss = Integer.parseInt("0" + ss);
                    addBankCardActivity.send_code.setText("\u3000" + ss + "秒\u3000");
                    addBankCardActivity.send_code.setEnabled(false);
                }
            } else {
                switch (msg.what) {
                    case MSG_LOGIN: {

                    }
                    break;
                    case MSG_AUTH_CANCEL: {
                        Toast.makeText(addBankCardActivity, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
                        System.out.println("-------MSG_AUTH_CANCEL--------");
                    }
                    break;
                    case MSG_AUTH_ERROR: {
                        Toast.makeText(addBankCardActivity, R.string.auth_error, Toast.LENGTH_SHORT).show();
                        System.out.println("-------MSG_AUTH_ERROR--------");
                    }
                    break;
                    case MSG_AUTH_COMPLETE: {
                        Toast.makeText(addBankCardActivity, R.string.auth_complete, Toast.LENGTH_SHORT).show();
                        System.out.println("--------MSG_AUTH_COMPLETE-------");
                        if (addBankCardActivity.platform != null) {
                            if (addBankCardActivity.title.equals(tagQQ)) {
                                addBankCardActivity.bindingQQ(addBankCardActivity.platform.getDb().getUserId());
                            } else {
                                addBankCardActivity.bindingaccount(addBankCardActivity.platform.getDb().getUserId(), "1", "", "");
                            }

//                            bindingaccount("oa1CCw8ime4AsPOJw--_uV-NnDD8","1","","");

                        }
                    }
                    break;
                }
            }


        }
    }


}
