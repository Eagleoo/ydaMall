package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.view.VideoAudioDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.Stored;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.widget.NumberTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能： 账户列表<br>
 * 时间： 2014-6-20<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class AccountListFrame extends Activity {

    @ViewInject(R.id.account_list_list)
    private LinearLayout list;
    private String state;
    private TextView topCenter;
    @ViewInject(R.id.account_zhtx)
    private TextView account_zhtx;
    @ViewInject(R.id.topye)
    private TextView topye;
    @ViewInject(R.id.nimabi)
    private TextView nimabi;
    @ViewInject(R.id.account_zhzz)
    private TextView account_zhzz;
    @ViewInject(R.id.account_txmx)
    private TextView account_txmx;
    @ViewInject(R.id.account_showmore)
    private TextView account_showmore;
    @ViewInject(R.id.account_money)
    private NumberTextView account_money;
    @ViewInject(R.id.account_cjwt)
    private TextView account_cjwt;
    @ViewInject(R.id.account_listview)
    private ListView account_listview;
    @ViewInject(R.id.loading)
    private ImageView loading;
    @ViewInject(R.id.account_Lin2)
    private LinearLayout account_Lin2;
    @ViewInject(R.id.top_liner)
    private LinearLayout top_liner;
    @ViewInject(R.id.account_rel1)
    private RelativeLayout account_rel1;
    @ViewInject(R.id.account_rel2)
    private RelativeLayout account_rel2;
    @ViewInject(R.id.account_rel3)
    private RelativeLayout account_rel3;
    private RelativeLayout[] Rels = {account_rel1, account_rel2, account_rel3};
    private TextView[] textviews = {account_zhzz, account_zhtx, account_txmx};
    private LinearLayout acount_top_linel11;
    private MyAdapter1 adapter = null;
    private String type;
    private String gwkNumer = "1";
    private Context context;
    private String totixian = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.account_list_frame);
        ViewUtils.inject(this);
        context = this;
        state = getIntent().getStringExtra("state");
        topCenter = findViewById(R.id.center);
        Rels = new RelativeLayout[]{account_rel1, account_rel2, account_rel3};
        textviews = new TextView[]{account_zhzz, account_zhtx, account_txmx};
        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getYE();
    }

    private void getYE() {
        User user = UserData.getUser();
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        map.put("type", "1,2,3,4,5,6,7,8,9,10,13,14,15");

        NewWebAPI.getNewInstance().getWebRequest("/Money.aspx?call=getMoney",
                map, new NewWebAPIRequestCallback() {

                    @Override
                    public void success(Object result) {
                        // TODO Auto-generated method stub
                        if (null == result) {
                            Util.show("网络异常,请重试", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show("网络异常,请重试", context);
                            return;
                        }
                        if (getIntent().getStringExtra("userKey").equals("xj")) {
                            account_money.setNumberString(json.getString("red_c"));
                        } else if (getIntent().getStringExtra("userKey").equals("hb")) {
                            account_money.setNumberString(json.getString("red_s"));
                        } else if (getIntent().getStringExtra("userKey").equals("gwq")) {
                            account_money.setNumberString("0");
                        } else {
                            if (getIntent().getStringExtra("userKey").equals("sto")) {
                                account_money.setNumberString(json.getString("shop2"));
                            } else if (getIntent().getStringExtra("userKey").equals("sto1")) {
                                account_money.setNumberString(json.getString("shop3"));
                            } else {
                                account_money.setNumberString(json.getString(getIntent().getStringExtra("userKey") + ""));
                            }
                        }


                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {

                    }

                    @Override
                    public void requestEnd() {


                    }
                });
    }

    @SuppressLint("NewApi")
    private void init() {

        acount_top_linel11 = findViewById(R.id.acount_top_linel11);

        Intent intent = this.getIntent();
        final String parentName = intent.getStringExtra("parentName");
        Log.e("标题检查", "标题账户:" + parentName);
        if (intent.hasExtra("yeMoney")) {
            String yeMoney = intent.getStringExtra("yeMoney");
        }
        if (parentName.equals("商币账户")) {
            topye.setText("余额(枚)");
        }

        type = intent.getStringExtra("userKey");
        String content = "";
        if ("业务账户".equals(parentName)) {

            content = "1、业务账户收入可直接在平台内消费，需要消费时请先转入自己的“充值账户”。\n2、业务账户金额满1000或100整数倍时可提现，提现需扣除12%个人综合所得税（含手续费）。";
            showdialog(content);

        }
        if ("商币账户".equals(parentName)) {
            content = "1、商币可用于兑换远大平台的所有商品和增值服务。\n2、商币账户收入来源于消费赠送、账户充值、其他收入分配等，商币账户之间可以转账。\n3、商币充值时，充值1元,收入5商币+5消费券，消费券可用于远大平台商品换购。";
            showdialog(content);
        }
        if (!TextUtils.isEmpty(state)) {
            Util.initTitle(this, "密码管理", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            loading.setVisibility(View.GONE);
        } else if (parentName.equals("用户管理")) {
            Util.initTitle(this, parentName, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else if (type.equals("xj")) {
            Util.initTitle(this, "我的现金红包", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            topye.setText("余额");
            content = "1、现金红包可直接在平台内消费，需要消费时请先转入自己的“充值账户”。\n2、现金红包满100或100整数倍时可提现，提现需扣除12%个人综合所得税（含手续费）。";
            showdialog(content);

        } else if (type.equals("hb")) {
            Util.initTitle(this, "我的红包种子", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            topye.setText("余数");
            nimabi.setText("明细列表");
        } else {
            Util.initTitle(this, parentName + "详细", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        String[] items = getItems(parentName);
        Log.e("itmes", Arrays.asList(items) + "KK");
        final AccountItemClick click = new AccountItemClick(this, parentName, type);

        if ("用户管理".equals(parentName)) {
            acount_top_linel11.setVisibility(View.GONE);
            account_Lin2.setVisibility(View.GONE);
            int _10dp = Util.dpToPx(this, 10F);
            int color_535353 = Color.parseColor("#535353");
            Resources res = this.getResources();
            Drawable jt = res.getDrawable(R.drawable.jt);
            jt.setBounds(0, 0, jt.getMinimumWidth(), jt.getMinimumHeight());
            int i = 0;
            for (String name : items) {
                TextView itemView = new TextView(this);
                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                if (!TextUtils.isEmpty(state) && i == 2) {

                    lp.setMargins(0, 20, 0, 0);

                } else {
                    lp.setMargins(0, 1, 0, 0);
                }
                if (topCenter.getText().toString().equals("密码管理")) {
                    lp.setMargins(0, 1, 1, 0);
                }
                itemView.setLayoutParams(lp);
                itemView.setText(name);
                itemView.setTextSize(17F);
                itemView.setTextColor(color_535353);
                itemView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                itemView.setCompoundDrawables(null, null, jt, null);
                itemView.setBackgroundColor(Color.WHITE);
                itemView.setPadding(_10dp, _10dp, _10dp, _10dp);
                list.addView(itemView);
                itemView.setOnClickListener(click);
            }
            return;
        }
        int i = 0;
        for (String name : items) {
            if (name.contains("账户2明细")) {
                gwkNumer = "2";
            }

            if (!name.equals("账户明细") && !name.contains("账户1") && !name.contains("账户2")) {
                if (parentName.contains("商币") && name.contains("账户转账")) {

                } else if (parentName.equals("商币账户")) {

                    if (!name.equals("商币充值")) {
                        textviews[i].setText(name);
                        top_liner.setVisibility(View.VISIBLE);
                        Rels[i].setVisibility(View.VISIBLE);
                        textviews[i].setOnClickListener(click);
                        i++;
                    }

                } else if (parentName.equals("现金红包账户")) {
                    Log.e("parentName: 现金红包", "KK" + i + "parentName" + parentName + "name" + name);
                    textviews[i].setText(name);
                    top_liner.setVisibility(View.VISIBLE);
                    Rels[i].setVisibility(View.VISIBLE);
                    textviews[i].setOnClickListener(click);
                    if (name.equals("提现")) {
                        textviews[i].setText("温馨提示");
                        textviews[i].setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String content = "基于国家政策调控，本账户暂时停止提现功能，产生的所有现金红包均可转至充值账户消费。";
                                VoipDialog voip = new VoipDialog(content, AccountListFrame.this, "我知道了", "", null, null);
                                voip.show();
                            }
                        });
                    }
                    i++;


                } else if (parentName.equals("业务账户")) {
                    Log.e("parentName: 现金红包", "KK" + i + "parentName" + parentName + "name" + name);
                    textviews[i].setText(name);
                    top_liner.setVisibility(View.VISIBLE);
                    Rels[i].setVisibility(View.VISIBLE);
                    textviews[i].setOnClickListener(click);
                    if (name.equals("账户提现")) {
                        textviews[i].setText("温馨提示");
                        textviews[i].setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String content = "基于国家政策调控，本账户提现金额临时调整为1000元起，并依据提现金额按相关标准代扣个人所得税。申请提现后请于5个工作日内至当地运营中心领取。";

                                VideoAudioDialog dialog = new VideoAudioDialog(context);

                                dialog.setTitle("温馨提示");
                                dialog.setLeft("我知道了");
                                dialog.setRight("我要提现");
                                dialog.setContent(content);
                                dialog.setLeft(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        totixian = "";

                                        String childrenName = "账户提现";
                                        Intent intent = new Intent();
                                        String newType = type;
                                        String newParentName = parentName;
                                        Class newClass = null;
                                        if (childrenName.contains("明细") || childrenName.contains("更多")) {
                                            newClass = AccountDetailsFrame.class;
                                        } else {
                                            if ("0".equals(UserData.getUser().getSecondPwd())) {
                                                VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", AccountListFrame.this, "确定", "取消", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Util.showIntent(AccountListFrame.this,
                                                                SetSencondPwdFrame.class);
                                                    }
                                                }, null);
                                                voipDialog.show();
                                                return;
                                            }
                                        }
                                        if (childrenName.contains("转账")) {
                                            newClass = MoneyToMoneyFrame.class;
                                            if ("sb".equals(type) && "账户转账".equals(childrenName))
                                                newClass = SBMoneyToMoneyFrame.class;
                                        } else if (childrenName.contains("修改登录密码"))
                                            newClass = UsermodpassFrame.class;
                                        else if (childrenName.contains("忘记交易密码")) {
                                            String secondPwd2 = UserData.getUser().getSecondPwd();
                                            String MobilePhone = UserData.getUser().getMobilePhone();// 得到用户的手机号
                                            if (TextUtils.isEmpty(secondPwd2) || secondPwd2.equals("0")) {
                                                VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", AccountListFrame.this, "确定", "取消", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Util.showIntent(AccountListFrame.this,
                                                                SetSencondPwdFrame.class);
                                                    }
                                                }, null);
                                                voipDialog.show();
                                            } else if (!Util.checkUserInfocomplete()) {
                                                VoipDialog voipDialog = new VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", AccountListFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Util.showIntent(AccountListFrame.this,
                                                                UpdateUserMessageActivity.class);
                                                    }
                                                }, null);
                                                voipDialog.show();
                                            } else {
                                                newClass = ForgetTradePasswordFrame.class;
                                            }
                                        } else if (childrenName.contains("修改交易密码")) {
                                            String secondPwd = UserData.getUser().getSecondPwd();
                                            if (TextUtils.isEmpty(secondPwd) || secondPwd.equals("0")) {
                                                VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", AccountListFrame.this, "确定", "取消", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Util.showIntent(AccountListFrame.this,
                                                                SetSencondPwdFrame.class);
                                                    }
                                                }, null);
                                                voipDialog.show();
                                            } else {
                                                Util.showIntent(AccountListFrame.this, UpdateTwoPwdFrame.class);
                                            }
                                        } else if (childrenName.contains("账户提现") || childrenName.equals("提现")) {
                                            newClass = CashwithdrawalActivity.class;
                                            totixian = "1";
                                        } else if (childrenName.contains("彩票提现"))
                                            newClass = RequestCPTX.class;
                                        else if (childrenName.contains("账户充值")) {
                                            if (parentName.contains("话费账户")) {
                                            } else {
                                                newClass = AccountRecharge.class;
                                            }
                                        } else if (childrenName.contains("商币充值")) {
                                            newClass = SBChongzhiFrame.class;
                                        } else if (childrenName.contains("现金追加")) {
                                            newClass = MoneyAppendFrame.class;
                                        } else if (childrenName.contains("手机号码")) {
                                            newClass = UpdatePhoneFrame.class;
                                        } else if (childrenName.contains("购买创业包")) {
                                            newClass = PioneerAngelShopFrame.class;
                                        }
                                        if (null != newClass) {
                                            User user = UserData.getUser();
                                            Log.e("newClass", (newClass == CashwithdrawalActivity.class) + "sss");
                                            if (totixian.equals("1")) {

                                                Log.e("LevelId", user.getLevelId() + "LK");
                                                if (!Util.checkUserInfocomplete()) {
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


                                            }
                                            intent.setClass(context, newClass);
                                            intent.putExtra("parentName", newParentName);
                                            intent.putExtra("childrenName", childrenName);
                                            intent.putExtra("userKey", newType);
                                            intent.putExtra("account_money", account_money.getText().toString());
                                            if ("sto".equals(type) && childrenName.contains("账户2明细") && "wu".equals(UserData.getUser().getS2()))
                                                Util.showIntent("您还没有购物卡账户是否前去申请？", AccountListFrame.this, RequestShopCardFrame.class);
                                            else if (childrenName.contains("现金追加") && "wu".equals(UserData.getUser().getS2()))
                                                Util.showIntent("您还没有购物卡账户是否前去申请？", AccountListFrame.this, RequestShopCardFrame.class);
                                            else if ("sto".equals(type) && childrenName.contains("转账"))
                                                Util.show("购物卡转帐功能，暂时关闭！", context);
                                            else if ("sto1".equals(type) && childrenName.contains("转账"))
                                                Util.show("购物卡转帐功能，暂时关闭！", context);
                                            else
                                                context.startActivity(intent);
                                        }
                                    }
                                });
                                dialog.show();
                            }
                        });
                    }
                    i++;
                } else {
                    textviews[i].setText(name);
                    top_liner.setVisibility(View.VISIBLE);
                    Rels[i].setVisibility(View.VISIBLE);
                    textviews[i].setOnClickListener(click);
                    i++;
                }
            }
        }
        account_showmore.setOnClickListener(click);

        bind(1);
    }

    private void showdialog(final String content) {
        account_cjwt.setVisibility(View.VISIBLE);
        account_cjwt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                VoipDialog voip = new VoipDialog(content, AccountListFrame.this, "确定", null, null, null);
                voip.show();
            }
        });
    }

    private String[] getItems(String parentName) {
        if (null != UserData.getUser()) {
            int levelId = Util.getInt(UserData.getUser().getLevelId());
            int shopTypeId = Util.getInt(UserData.getUser().getShopTypeId());
            boolean isL5DateIs2015_06_10after = UserData.getUser().l5DateIs2015_06_10after();
            String[] oneOrder = null;
            String[][] twoOrder = null;
            String[] guanli = null;
            guanli = new String[]{"修改登录密码", "修改交易密码", "忘记交易密码", "修改手机号码"};
            String[] shopCards = null;
            LogUtils.e("UserData.getUser().getShopCard1()=" + UserData.getUser().getShopCard1());
            List<String> temp = new ArrayList<String>();
            if ("you".equals(UserData.getUser().getS1()))
                temp.add("账户1明细");
            temp.add("账户2明细");
            temp.add("现金追加");
            shopCards = temp.toArray(new String[]{});
            // 创业大使
            if (8 == levelId) {
                LogUtils.v("8 == levelId");
                oneOrder = new String[]{"话费账户", "业务账户", "消费券账户", "充值账户", "彩票账户", "商币账户", "购物卡账户", "新购物卡账户", "权益账户",
                        "我的创业包", "用户管理", "红包种子账户", "现金红包账户", "购物劵账户"};

                if ("1".equals(UserData.getUser().getIsSite())) {
                    twoOrder = new String[][]{{"账户明细"}, {"账户明细", "账户转账", "账户提现"}, {"账户明细", "账户转账"},
                            {"账户明细", "账户转账", "账户充值"}, {"账户明细", "账户转账", "彩票提现"}, {"账户明细", "商币转账", "商币充值"},
                            shopCards, shopCards, {"账户明细"}, {"我的创业包明细", "购买创业包"}, guanli, {"账户明细"},
                            {"账户明细", "账户转账", "提现"}, {"账户明细", "账户充值"}};
                } else {
                    twoOrder = new String[][]{{"账户明细"}, {"账户明细", "账户转账", "账户提现"}, {"账户明细", "账户转账"},
                            {"账户明细", "账户转账", "账户充值"}, {"账户明细", "账户转账", "彩票提现"}, {"账户明细", "商币转账", "账户转账", "商币充值"},
                            shopCards, shopCards, {"账户明细"}, {"创业包明细", "购买创业包"}, guanli, {"账户明细"},
                            {"账户明细", "账户转账", "提现"}, {"账户明细", "账户充值"}};
                }
                // 代理
            } else if (5 == levelId || 6 == levelId) {
                LogUtils.v("(3 < levelId && 8 > levelId) && 3 < shopTypeId");
                oneOrder = new String[]{"话费账户", "业务账户", "消费券账户", "充值账户", "彩票账户", "商币账户", "购物卡账户", "新购物卡账户", "我的创业包",
                        "权益账户", "用户管理", "红包种子账户", "现金红包账户", "购物劵账户"};
                if ("1".equals(UserData.getUser().getIsSite())) {
                    twoOrder = new String[][]{{"账户明细"}, {"账户明细", "账户转账", "账户提现"}, {"账户明细", "账户转账"},
                            {"账户明细", "账户转账", "账户充值"}, {"账户明细", "账户转账", "彩票提现"}, {"账户明细", "商币转账", "账户转账", "商币充值"},
                            shopCards, shopCards, {"创业包明细", "购买创业包"}, {"账户明细"}, guanli, {"账户明细"},
                            {"账户明细", "账户转账", "提现"}, {"账户明细", "账户充值"}};
                } else {
                    twoOrder = new String[][]{{"账户明细"}, {"账户明细", "账户转账", "账户提现"}, {"账户明细", "账户转账"},
                            {"账户明细", "账户转账", "账户充值"}, {"账户明细", "账户转账", "彩票提现"}, {"账户明细", "商币转账", "商币充值"},
                            shopCards, shopCards, {"创业包明细", "购买创业包"}, {"账户明细"}, guanli, {"账户明细"},
                            {"账户明细", "账户转账", "提现"}, {"账户明细", "账户充值"}};
                }
                // 网店没有权益
            } else if (3 < shopTypeId && 10 > shopTypeId) {
                LogUtils.v("3 < shopTypeId && 10 > shopTypeId");
                oneOrder = new String[]{"话费账户", "业务账户", "消费券账户", "充值账户", "彩票账户", "商币账户", "购物卡账户", "新购物卡账户", "用户管理",
                        "红包种子账户", "现金红包账户", "购物劵账户"};
                twoOrder = new String[][]{{"账户明细"}, {"账户明细", "账户转账", "账户提现"}, {"账户明细", "账户转账"},
                        {"账户明细", "账户转账", "账户充值"}, {"账户明细", "账户转账", "彩票提现"}, {"账户明细", "商币转账", "账户转账", "商币充值"},
                        shopCards, shopCards, guanli, {"账户明细"}, {"账户明细", "账户转账", "提现"}, {"账户明细", "账户充值"}};
                // 如果是联盟商家
            } else if (10 == shopTypeId) {
                LogUtils.v("10 == shopTypeId");
                oneOrder = new String[]{"话费账户", "业务账户", "消费券账户", "充值账户", "彩票账户", "商币账户", "购物卡账户", "新购物卡账户", "用户管理",
                        "红包种子账户", "现金红包账户", "购物劵账户"};
                twoOrder = new String[][]{{"账户明细"}, {"账户明细", "账户转账", "账户提现"}, {"账户明细", "账户转账"},
                        {"账户明细", "账户转账", "账户充值"}, {"账户明细", "账户转账", "彩票提现"}, {"账户明细", "商币转账", "账户转账", "商币充值"},
                        shopCards, shopCards, guanli, {"账户明细"}, {"账户明细", "账户转账", "提现"}, {"账户明细", "账户充值"}};
                // 如果不是代理，也不是网店，则说明是普通用户
            } else if (1 == levelId) {
                LogUtils.v("else");
                oneOrder = new String[]{"话费账户", "消费券账户", "充值账户", "彩票账户", "商币账户", "购物卡账户", "新购物卡账户", "用户管理", "红包种子账户",
                        "现金红包账户", "购物劵账户"};
                twoOrder = new String[][]{{"账户明细"}, {"账户明细", "账户转账"}, {"账户明细", "账户转账", "账户充值"},
                        {"账户明细", "账户转账", "彩票提现"}, {"账户明细", "商币转账"}, shopCards, shopCards, guanli, {"账户明细"},
                        {"账户明细", "账户转账", "提现"}, {"账户明细", "账户充值"}};
            } else {
                oneOrder = new String[]{"话费账户", "业务账户", "消费券账户", "充值账户", "彩票账户", "商币账户", "购物卡账户", "新购物卡账户", "用户管理",
                        "红包种子账户", "现金红包账户", "购物劵账户"};
                twoOrder = new String[][]{{"账户明细"}, {"账户明细", "账户转账", "账户提现"}, {"账户明细", "账户转账"},
                        {"账户明细", "账户转账", "账户充值"}, {"账户明细", "账户转账", "彩票提现"}, {"账户明细", "商币转账"}, shopCards,
                        shopCards, guanli, {"账户明细"}, {"账户明细", "账户转账", "提现"}, {"账户明细", "账户充值"}};
            }
            int ci = 0;
            for (String k : oneOrder) {
                if (k.equals(parentName)) {
                    break;
                }

                ci++;
            }

            return twoOrder[ci];
        }
        return null;
    }

    class AccountItemClick implements OnClickListener {

        private AccountListFrame frame;
        private String parentName;
        private String type;

        public AccountItemClick(AccountListFrame frame, String parentName, String type) {
            this.frame = frame;
            this.parentName = parentName;
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            totixian = "";
            TextView view = (TextView) v;
            String childrenName = view.getText().toString().trim();
            Intent intent = new Intent();
            String newType = type;
            String newParentName = parentName;
            Class newClass = null;
            if (childrenName.contains("明细") || childrenName.contains("更多")) {
                newClass = AccountDetailsFrame.class;
            } else {
                if ("0".equals(UserData.getUser().getSecondPwd())) {
                    VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", AccountListFrame.this, "确定", "取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.showIntent(AccountListFrame.this,
                                    SetSencondPwdFrame.class);
                        }
                    }, null);
                    voipDialog.show();
                    return;
                }
            }
            if (childrenName.contains("转账")) {
                newClass = MoneyToMoneyFrame.class;
                if ("sb".equals(type) && "账户转账".equals(childrenName))
                    newClass = SBMoneyToMoneyFrame.class;
            } else if (childrenName.contains("修改登录密码"))
                newClass = UsermodpassFrame.class;
            else if (childrenName.contains("忘记交易密码")) {
                String secondPwd2 = UserData.getUser().getSecondPwd();
                String MobilePhone = UserData.getUser().getMobilePhone();// 得到用户的手机号
                if (TextUtils.isEmpty(secondPwd2) || secondPwd2.equals("0")) {
                    VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", AccountListFrame.this, "确定", "取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.showIntent(AccountListFrame.this,
                                    SetSencondPwdFrame.class);
                        }
                    }, null);
                    voipDialog.show();
                } else if (!Util.checkUserInfocomplete()) {
                    VoipDialog voipDialog = new VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", AccountListFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.showIntent(AccountListFrame.this,
                                    UpdateUserMessageActivity.class);
                        }
                    }, null);
                    voipDialog.show();
                } else {
                    newClass = ForgetTradePasswordFrame.class;
                }
            } else if (childrenName.contains("修改交易密码")) {
                String secondPwd = UserData.getUser().getSecondPwd();
                if (TextUtils.isEmpty(secondPwd) || secondPwd.equals("0")) {
                    VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", AccountListFrame.this, "确定", "取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.showIntent(AccountListFrame.this,
                                    SetSencondPwdFrame.class);
                        }
                    }, null);
                    voipDialog.show();
                } else {
                    Util.showIntent(AccountListFrame.this, UpdateTwoPwdFrame.class);
                }
            } else if (childrenName.contains("账户提现") || childrenName.equals("提现")) {
                newClass = CashwithdrawalActivity.class;
                totixian = "1";
            } else if (childrenName.contains("彩票提现"))
                newClass = RequestCPTX.class;
            else if (childrenName.contains("账户充值")) {
                if (parentName.contains("话费账户")) {
                } else {
                    newClass = AccountRecharge.class;
                }
            } else if (childrenName.contains("商币充值")) {
                newClass = SBChongzhiFrame.class;
            } else if (childrenName.contains("现金追加")) {
                newClass = MoneyAppendFrame.class;
            } else if (childrenName.contains("手机号码")) {
                newClass = UpdatePhoneFrame.class;
            } else if (childrenName.contains("购买创业包")) {
                newClass = PioneerAngelShopFrame.class;
            }
            if (null != newClass) {
                User user = UserData.getUser();
                Log.e("newClass", (newClass == CashwithdrawalActivity.class) + "sss");
                if (totixian.equals("1")) {

                    Log.e("LevelId", user.getLevelId() + "LK");
                    if (!Util.checkUserInfocomplete()) {
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


                }
                intent.setClass(frame, newClass);
                intent.putExtra("parentName", newParentName);
                intent.putExtra("childrenName", childrenName);
                intent.putExtra("userKey", newType);
                intent.putExtra("account_money", account_money.getText().toString());
                if ("sto".equals(type) && childrenName.contains("账户2明细") && "wu".equals(UserData.getUser().getS2()))
                    Util.showIntent("您还没有购物卡账户是否前去申请？", AccountListFrame.this, RequestShopCardFrame.class);
                else if (childrenName.contains("现金追加") && "wu".equals(UserData.getUser().getS2()))
                    Util.showIntent("您还没有购物卡账户是否前去申请？", AccountListFrame.this, RequestShopCardFrame.class);
                else if ("sto".equals(type) && childrenName.contains("转账"))
                    Util.show("购物卡转帐功能，暂时关闭！", frame);
                else if ("sto1".equals(type) && childrenName.contains("转账"))
                    Util.show("购物卡转帐功能，暂时关闭！", frame);
                else
                    frame.startActivity(intent);
            }
        }

    }

    private void bind(int page) {
        final String url = "userid=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                + "&pageSize=" + 5 + "&page=" + page + "&enumType=0";

        // final String parentValue = intent.getStringExtra("parentName");
        // final String userKey = intent.getStringExtra("userKey");

        Util.asynTask1(this, "正在获取您的帐户信息...", new IAsynTask() {

            @Override
            public void updateUI(Serializable runData) {
                loading.setVisibility(View.GONE);
                if (null == runData) {
                    Util.show("无效的帐户类型...", AccountListFrame.this);
                    return;
                }

                HashMap<String, List<Stored>> map3 = (HashMap<String, List<Stored>>) runData;
                List<Stored> list = map3.get("list");

                if (null == list || 0 == list.size()) {
                    Util.show("您没有更多的明细", AccountListFrame.this);
                    return;
                }
                List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("date", list.get(i).getDate());
                    map2.put("type", list.get(i).getType());
                    map2.put("money", list.get(i).getIncome());
                    map2.put("desc", list.get(i).getDetail());
                    map2.put("bann", list.get(i).getBalance());
                    map2.put("income", list.get(i).getIncome());
                    dataList.add(map2);
                }
                if (null == adapter) {
                    int width = 0;
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    width = dm.widthPixels;
                    account_listview.setAdapter(adapter = new MyAdapter1(AccountListFrame.this, dataList, width));
                } else {
                    adapter.addChildren(dataList);
                }
            }

            @Override
            public Serializable run() {
                Web web = null;
                if ("bus".equals(type)) {
                    web = new Web(Web.getBusinessAccount, url);
                } else if ("rec".equals(type)) {
                    web = new Web(Web.getRechargeAccount, url);
                } else if ("cp".equals(type)) {
                    web = new Web(Web.getLotteryAccount, url);
                } else if ("rai".equals(type)) {
                    web = new Web(Web.getInterestsAccount, url);
                } else if ("han".equals(type)) {
                    web = new Web(Web.getHandselAccount, url);
                } else if ("sto".equals(type)) {
                    web = new Web(Web.getStoredAccount, url.replaceFirst("&enumType=0", "&enumType=" + gwkNumer));
                } else if ("sto1".equals(type)) {
                    web = new Web(Web.getStoredAccount, url.replaceFirst("&enumType=0", "&enumType=3"));
                } else if ("sb".equals(type)) {
                    web = new Web(Web.getSBDetailList, url);
                } else if ("pho".equals(type)) {
                    web = new Web(Web.getPhoneAccount, url);
                } else if ("zcb".equals(type)) {
                    web = new Web(Web.getZCBDetailList, url);
                } else if ("tixian".equals(type)) {
                    web = new Web(Web.txming, url);
                } else if ("hb".equals(type)) {
                    web = new Web(Web.redurl, "/getRed_SeedAccount", url);
                } else if ("xj".equals(type)) {
                    web = new Web(Web.redurl, "/getRed_CashAccount", url);
                } else if ("gwq".equals(type)) {
                    web = new Web(Web.getBusinessAccount, url);
                } else {
                    return null;
                }
                HashMap<String, List<Stored>> map = new HashMap<String, List<Stored>>();
                List<Stored> list = web.getList(Stored.class);
                map.put("list", list);
                return map;
            }
        });
    }

    class ViewHolder {
        public TextView date;
        public TextView type;
        public TextView bann;
        public TextView money;
        public LinearLayout itemLine;
    }

    class MyAdapter1 extends BaseAdapter {

        private LayoutInflater mInflater;
        private Activity account = null;
        private List<Map<String, Object>> dataList = null;
        private int width;

        public MyAdapter1(Activity context, List<Map<String, Object>> dataList, int width) {
            this.account = context;
            this.mInflater = LayoutInflater.from(context);
            this.dataList = dataList;
            this.width = width;
        }

        public void addChildren(List<Map<String, Object>> dataList) {
            this.dataList.addAll(dataList);
            this.notifyDataSetChanged();
        }

        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return dataList.get(0);
        }

        @Override
        public long getItemId(int arg0) {
            return dataList.get(0).hashCode();
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.account_list_item, null);
                holder.date = (TextView) convertView.findViewById(R.id.detailItemDate);
                holder.type = (TextView) convertView.findViewById(R.id.detailItemType);
                holder.money = (TextView) convertView.findViewById(R.id.detailItemMoney);
                holder.itemLine = (LinearLayout) convertView.findViewById(R.id.itemLine);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Context c = convertView.getContext();
            OnClickListener click = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object desc = dataList.get(position).get("desc");
                    if (Util.isNull(desc)) {
                        Util.detailInformation(account, "暂无详细内容!", "详细信息", width);
                    } else {
                        String message = "时间：" + dataList.get(position).get("date") + "\n类型："
                                + dataList.get(position).get("type") + "\n金额："
                                + Util.deleteX(dataList.get(position).get("money") + "") + "\n描述："
                                + desc.toString().replaceAll("_p", "") + "";
                        Util.detailInformation(account, message, "详细信息", width);
                    }
                }
            };
            holder.itemLine.setOnClickListener(click);
            holder.date.setText(dataList.get(position).get("date") + "");

            String money = dataList.get(position).get("money") + "";

            if (money.contains("-")) {
                holder.money.setTextColor(getResources().getColor(R.color.green_deep));
            } else {
                holder.money.setTextColor(getResources().getColor(R.color.yellow_deep));
            }
            holder.money.setText(money);
            holder.type.setText(dataList.get(position).get("type") + "");
            return convertView;
        }

    }
}
