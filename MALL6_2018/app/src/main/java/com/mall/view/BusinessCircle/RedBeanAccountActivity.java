package com.mall.view.BusinessCircle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.view.VideoAudioDialog;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.ExpireBean;
import com.mall.model.RedAccountBean;
import com.mall.model.RedPackageInLetBean;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.CashwithdrawalActivity;
import com.mall.view.MoreTextView;
import com.mall.view.R;
import com.mall.view.RedEnvelopesPackage.AccountParticularsListActivity;
import com.mall.view.RedEnvelopesPackage.ChangeRedEnvelopeActivity;
import com.mall.view.RedEnvelopesPackage.RedEnvelopeRechargeActivity;
import com.mall.view.SelectorFactory;
import com.mall.view.SetSencondPwdFrame;
import com.mall.view.StoreMainFrame;
import com.mall.view.UpdateUserMessageActivity;
import com.mall.view.carMall.CarShopActivity;
import com.mall.view.carMall.SelectCarNormActivity;
import com.mall.widget.NumberTextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ContentView(R.layout.activity_red_bean_account)
public class RedBeanAccountActivity extends AppCompatActivity {

    private Context context;

    @ViewInject(R.id.titile)
    private TextView titile;

    @ViewInject(R.id.carquan)
    private TextView carquan;

    @ViewInject(R.id.redbeanaccount_lv)
    private ListView redbeanaccountlist;


    @ViewInject(R.id.handertitle)
    private TextView handertitle;


    @ViewInject(R.id.ll1)
    private View ll1;

    @ViewInject(R.id.ll2)
    private View ll2;

    @ViewInject(R.id.ll3)
    View ll3;

    @ViewInject(R.id.ll4)
    View ll4;

    @ViewInject(R.id.rednumber_tv)
    private NumberTextView rednumber;

    @ViewInject(R.id.expiredlin)
    private LinearLayout expiredlin;

    @ViewInject(R.id.toshop)
    private TextView toshop;
    @ViewInject(R.id.expiredtime)
    private TextView expiredtime;

    @ViewInject(R.id.mtv3)
    private MoreTextView mtv3;


    Myadapter myadapter;

    private String title = "";

    RedPackageInLetBean redPackageInLetBean;

    String url = "";


    private List<RedAccountBean.ListBean> redlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ViewUtils.inject(this);
        Intent intent = getIntent();

        String str = intent.getStringExtra("title");
        redPackageInLetBean = (RedPackageInLetBean) intent.getSerializableExtra("bean");
        if (!Util.isNull(str)) {
            title = str;
        }

        init(redPackageInLetBean);
        Util.kfg = "1";

    }


    private void init(RedPackageInLetBean redPackageInLetBean) {
        initview(redPackageInLetBean);

    }

    private void initbutton() {
        ll1.setVisibility(View.GONE);
        ll2.setVisibility(View.GONE);
        ll3.setVisibility(View.GONE);
        ll4.setVisibility(View.GONE);
    }

    private void getYE() {
        User user = UserData.getUser();
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        map.put("type", "1,2,3,4,5,6,7,8,9,10,13,14,15,16,17,18");//,19
        NewWebAPI.getNewInstance().getWebRequest("/Money.aspx?call=getMoney",
                map, new WebRequestCallBack() {

                    @Override
                    public void success(Object result) {
                        // TODO Auto-generated method stub
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常,请重试", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show("网络异常,请重试", context);
                            return;
                        }

                        String number = "0.00";

                        if (title.equals("我的购物券")) {

                            if (!Util.isNull(json.getString("shopping_voucher"))) {
                                number = json.getString("shopping_voucher");
                            }
                            toshop.setText("立即购物");
                            toshop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Util.showIntent(context, CarShopActivity.class
                                    );
                                }
                            });
                        } else if (title.equals("我的拼车券")) {
                            if (!Util.isNull(json.getString("carpool_coupons"))) {
                                number = json.getString("carpool_coupons");
                            }
                            isGoCar(number);
                        } else if (title.equals("提现账户")) {

//                            if (!Util.isNull(json.getString("Present_account"))) {
//                                number = json.getString("Present_account");
//                            }
                        }
                        rednumber.setNumberString(number);


                    }
                });
    }

    private void isGoCar(String money) {

        final CustomProgressDialog cpd = Util.showProgress("加载中...", this);

        NewWebAPI.getNewInstance().getWebRequest("/" + "carpool.aspx" + "?call=" + "getCarpool_coupons_car" + "&money_="
                        + money.split("\\.")[0]
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
                        final JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }


                        carquan.setText(json.getString("message"));
                        toshop.setText("立即充值");
                        if (json.getString("type").equals("1")) {
                            toshop.setText("立即拼车");
                        }
                        carquan.setText(json.getString("message"));

                        toshop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!json.getString("type").equals("1")) {

                                    Util.showIntent(context, RedEnvelopeRechargeActivity.class,
                                            new String[]{"userKey"}, new String[]{"购物券充值"}
                                    );


                                } else {
                                    Util.showIntent(context, SelectCarNormActivity.class, new String[]{"money"}
                                            , new String[]{json.getString("car")}
                                    );
                                }
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

    public void getRedBoxmyselfInfo() {
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

                        Gson gson = new Gson();
                        redPackageInLetBean = gson.fromJson(result.toString(), RedPackageInLetBean.class);
                        initview(redPackageInLetBean);


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


    private void initview(RedPackageInLetBean redPackageInLetBean) {
        initbutton();


        handertitle.setText(title);
        if (title.equals("我的购物券")) {

            carquan.setVisibility(View.VISIBLE);
            expiredtime.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) titile.getLayoutParams();
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            titile.setLayoutParams(lp);
            titile.setText("当前余额(元)");

            toshop.setBackground(SelectorFactory.newShapeSelector()
                    .setStrokeWidth(Util.dpToPx(context, 1))
                    .setDefaultBgColor(Color.parseColor("#1AE1E1E1"))
                    .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                    .setPressedStrokeColor(Color.parseColor("#F3F3F3"))
                    .setCornerRadius(Util.dpToPx(context, 1))
                    .create());


            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            ll4.setVisibility(View.GONE);
            ll3.setVisibility(View.VISIBLE);
            mtv3.setText("充值");
            expiredlin.setVisibility(View.VISIBLE);

            try {
                //拼车券 数值修改
                if (!Util.isNull(redPackageInLetBean)) {
                    rednumber.setNumberString(redPackageInLetBean.getRedbean());
                } else {
                    rednumber.setText("");
                }
            } catch (Exception e) {
                rednumber.setText("");
            }

        } else if (title.equals("我的拼车券")) {
            carquan.setVisibility(View.VISIBLE);
            expiredtime.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) titile.getLayoutParams();
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            titile.setLayoutParams(lp);
            titile.setText("当前余额");

            toshop.setBackground(SelectorFactory.newShapeSelector()
                    .setStrokeWidth(Util.dpToPx(context, 1))
                    .setDefaultBgColor(Color.parseColor("#1AE1E1E1"))
                    .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                    .setPressedStrokeColor(Color.parseColor("#F3F3F3"))
                    .setCornerRadius(Util.dpToPx(context, 1))
                    .create());


            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            ll3.setVisibility(View.GONE);
            ll4.setVisibility(View.GONE);

            expiredlin.setVisibility(View.VISIBLE);


        } else if (title.equals("红包豆账户")) {
            ll1.setVisibility(View.VISIBLE);
            if (!Util.isNull(redPackageInLetBean)) {
                rednumber.setNumberString(redPackageInLetBean.getRedbean());
            } else {
                rednumber.setText("");
            }
            //Todo
        } else if (title.equals("现金券账户") || title.equals("提现账户")) {
            ll2.setVisibility(View.VISIBLE);
            if (!Util.isNull(redPackageInLetBean)) {
                rednumber.setNumberString(redPackageInLetBean.getCashroll());
            } else {
                rednumber.setText("");
            }
        } else if (title.equals("消费券账户")) {
            ll3.setVisibility(View.GONE);

            toshop.setText("立即换购商品");
            toshop.setBackground(SelectorFactory.newShapeSelector()
                    .setStrokeWidth(1)
                    .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                    .setPressedStrokeColor(Color.parseColor("#F3F3F3"))
                    .setCornerRadius(Util.dpToPx(context, 2))
                    .create());
//            有x消费券将在2018.1.31号过期

            getOutData();

            if (!Util.isNull(redPackageInLetBean)) {
                rednumber.setNumberString(redPackageInLetBean.getConsumption());
            } else {
                rednumber.setText("");
            }
        } else if (title.equals("充值币账户")) {
            ll4.setVisibility(View.VISIBLE);
            if (!Util.isNull(redPackageInLetBean)) {
                rednumber.setNumberString(redPackageInLetBean.getRechargecoin());
            } else {
                rednumber.setText("");
            }

        }

        if (myadapter == null) {
            myadapter = new Myadapter(context, redlist);
            redbeanaccountlist.setAdapter(myadapter);
        }
        myadapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("titlexxx", "sss" + title);
        initdata();
        if (title.equals("我的购物券") || title.equals("我的拼车券")||title.equals("提现账户")) {
            getYE();
        } else {
            getRedBoxmyselfInfo();
        }

    }

    String host = "red_box.aspx";

    private void initdata() {
        if (title.equals("我的购物券")) {
            url = "getShopping_voucherAccount";
            host = "carpool.aspx";
        } else if (title.equals("我的拼车券")) {
            url = "getCarpool_couponsAccount";
            host = "carpool.aspx";
        } else if (title.equals("红包豆账户")) {
            url = "getRedbeanAccount";
        } else if (title.equals("消费券账户")) {
            url = "GetConsumptionVolume";
        } else if (title.equals("现金券账户")) {
            url = "getRed_boxAccount";
        } else if (title.equals("充值币账户")) {
            url = "getRed_boxRechargePoint";
        } else if (title.equals("提现账户")) {
            host = "user.aspx";
            url = "GetPresent_accountAccount";
        }

        getHongbaodouList(host, url);
        myadapter.notifyDataSetInvalidated();
    }

    @OnClick({R.id.top_back,
            R.id.torecharge_ll1, R.id.topackup_ll1, R.id.tovouchers_ll1,
            R.id.torecharge_ll2, R.id.torecharge_ll22,
            R.id.ll3, R.id.ll4,
            R.id.rednumber_tv,
            R.id.tomingxi, R.id.toshop
    })
    private void click(View view) {
        Intent intent;
        if ("0".equals(UserData.getUser().getSecondPwd()) && view.getId() != R.id.top_back && view.getId() != R.id.tomingxi) {
            VoipDialog voipDialog = new VoipDialog("为保障您的交易安全，请先设置您的交易密码", RedBeanAccountActivity.this, "确定", "取消",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(RedBeanAccountActivity.this,
                                    SetSencondPwdFrame.class);
                            startActivity(intent);
                        }
                    }, null);
            voipDialog.show();
            return;
        }
        switch (view.getId()) {
            case R.id.toshop:
                intent = new Intent(context, StoreMainFrame.class);
                startActivity(intent);
                break;
            case R.id.top_back:
                finish();
                break;
            case R.id.torecharge_ll1:
                intent = new Intent(context, RedEnvelopeRechargeActivity.class);
                intent.putExtra("title", "红包豆充值");
                startActivity(intent);
                break;
            case R.id.topackup_ll1:
                intent = new Intent(context, ChangeRedEnvelopeActivity.class);
                intent.putExtra("title", "封入红包盒");
                startActivity(intent);
                break;

            case R.id.tovouchers_ll1:
                intent = new Intent(context, ChangeRedEnvelopeActivity.class);
                intent.putExtra("title", "红包豆转入消费券");
                startActivity(intent);
                break;

            case R.id.torecharge_ll2:

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


                intent = new Intent(context, CashwithdrawalActivity.class);
                if (title.equals("提现账户")) {
                    intent.putExtra("userKey", "Present_account");
                } else {
                    intent.putExtra("userKey", "red");
                }

                intent.putExtra("account_money", rednumber.getText().toString());
                startActivity(intent);
                break;
            case R.id.torecharge_ll22:
                intent = new Intent(context, ChangeRedEnvelopeActivity.class);
                if (title.equals("提现账户")) {
                    intent.putExtra("title", "提现账户转账");
                } else {
                    intent.putExtra("title", "现金券转账");
                }
                startActivity(intent);
                break;
            case R.id.ll3:

                if (title.equals("我的购物券")) {
                    Util.showIntent(context, RedEnvelopeRechargeActivity.class,
                            new String[]{"userKey"}, new String[]{"购物券充值"}
                    );
                } else {
                    intent = new Intent(context, ChangeRedEnvelopeActivity.class);
                    intent.putExtra("title", "转入消费券");
                    startActivity(intent);
                }

                break;
            case R.id.ll4:
                intent = new Intent(context, ChangeRedEnvelopeActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("url", url);
                startActivity(intent);
                break;
            case R.id.rednumber_tv:
            case R.id.tomingxi:
                intent = new Intent(context, AccountParticularsListActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("url", url);
                intent.putExtra("host", host);
                startActivity(intent);
                break;
        }

    }

    private void getHongbaodouList(String host, String call) {

        final CustomProgressDialog cpd = Util.showProgress("正在获取您的账户信息...", this);

        NewWebAPI.getNewInstance().getWebRequest("/" + host + "?call=" + call + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        + "&Page=" + 1 + "&pageSize=" + 5 + "&enumType=" + "" + "&starTime=" +
                        "" + "&endTime=" + ""
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
                        RedAccountBean redAccountBean = gson.fromJson(result.toString(), RedAccountBean.class);
                        List<RedAccountBean.ListBean> list = redAccountBean.getList();
                        redlist.clear();
                        redlist.addAll(list);
                        myadapter.notifyDataSetChanged();


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

    public void getOutData() {

        NewWebAPI.getNewInstance().getWebRequest("/User.aspx?call=" + "Get_ConsumeAct_Expire" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()

                ,
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
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
                        ExpireBean redAccountBean = gson.fromJson(result.toString(), ExpireBean.class);

                        List<ExpireBean.ListBean> list = redAccountBean.getList();
                        ExpireBean.ListBean listBean = list.get(0);
                        expiredlin.setVisibility(View.VISIBLE);

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date curDate = new Date(System.currentTimeMillis());
                        String str = formatter.format(curDate);
                        Log.e("今天的时间", "str" + str);

                        String data = Util.getDateStr(str, Long.parseLong(Util.isNull(listBean.getDAYS()) ? "0" : listBean.getDAYS()));
                        expiredtime.setText(Html.fromHtml("( 有" + "<font color=\"#FFF200\">" + number(listBean.getGQ_MONEY())
                                        + "</font>" + "消费券将在" + data + "号过期 )"
//                                +",将有"+"<font color=\"#FFF200\">"+18+"</font>个过期"
                        ));


                    }

                    @Override
                    public void requestEnd() {

                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });
    }

    public static String number(String num) {
        DecimalFormat format = new DecimalFormat("0.00");
        String a = format.format(new BigDecimal(num));
        return a;
    }

    class Myadapter extends BaseAdapter {

        private Context context;
        private List<RedAccountBean.ListBean> redlist;

        public Myadapter(Context context, List<RedAccountBean.ListBean> redlist) {
            this.context = context;
            this.redlist = redlist;
        }

        @Override
        public int getCount() {
            if (redlist == null) {
                return 0;
            }
            return redlist.size();
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
        public View getView(int positon, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.account_list_item, parent, false);

            TextView detailItemType = (TextView) convertView.findViewById(R.id.detailItemType);
            TextView detailItemDate = (TextView) convertView.findViewById(R.id.detailItemDate);
            TextView detailItemMoney = (TextView) convertView.findViewById(R.id.detailItemMoney);

            View itemLine = convertView.findViewById(R.id.itemLine);

            final RedAccountBean.ListBean listBean = redlist.get(positon);
            itemLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoAudioDialog dialog = new VideoAudioDialog(context);
                    dialog.showcancel(View.GONE).showContent(View.GONE).showtag1(View.GONE).showtag2(View.GONE)
                            .showdialogtag3(View.VISIBLE)
                            .showdialogtag4(View.VISIBLE)
                            .showdialogtag5(View.GONE)
                            .showdialogtag6(View.VISIBLE)
                            .showlinview1(View.GONE)
                            .setRightColor(getResources().getColor(R.color.red))
                            .showtime(View.VISIBLE);


                    dialog.setSure("确认");
                    dialog.setTitle("详细信息");
                    dialog.settime(listBean.getDate());

                    dialog.setTextline3a(Util.justifyString("类型:", 5));
                    dialog.setTextline3b(listBean.getType());
                    dialog.setTextline4a(Util.justifyString("金额:", 5));
                    dialog.setTextline4b(listBean.getIncome());

                    if (!Util.isNull(listBean.getOrderid())) {
                        dialog.showdialogtag5(View.VISIBLE);
                        dialog.setTextline5a(Util.justifyString("交易单号:", 5));
                        dialog.setTextline5b(listBean.getOrderid());
                    }


                    dialog.setTextline6a(Util.justifyString("交易描述:", 5));
                    dialog.setTextline6b(listBean.getDetail());

                    dialog.show();
                }
            });

            detailItemType.setText(redlist.get(positon).getType());
            detailItemDate.setText(redlist.get(positon).getDate());
            if (!Util.isNull(redlist.get(positon).getIncome())) {
                if (redlist.get(positon).getIncome().indexOf("-") != -1) {
                    detailItemMoney.setTextColor(Color.parseColor("#E21918"));
                    detailItemMoney.setText(redlist.get(positon).getIncome());
                } else {
                    detailItemMoney.setTextColor(Color.parseColor("#85B84F"));
                    detailItemMoney.setText("+" + redlist.get(positon).getIncome());
                }
            }


            return convertView;
        }
    }
}
