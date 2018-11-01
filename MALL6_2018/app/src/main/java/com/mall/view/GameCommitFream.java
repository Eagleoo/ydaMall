package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.GameCommit;
import com.mall.net.Web;
import com.mall.util.AsynTask;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.MyPopWindow;
import com.mall.util.PayType;
import com.mall.util.UserData;
import com.mall.util.Util;

import net.sourceforge.mm.MMPay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameCommitFream extends Activity {
    private String areaArr[] = {};
    private String serverArr[] = {};
    private Spinner area = null;
    private Spinner server = null;
    @ViewInject(R.id.gamePay_sb_money)
    private TextView gamePay_sb_money;

    @ViewInject(R.id.gamePay_gc_______9)
    private View gamePay_gc_______9;


    @ViewInject(R.id.gamePay_gc10)
    private LinearLayout twoLine;


    @ViewInject(R.id.pay_item_rec_radio)
    private ImageView angel_requestCZ1;
    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;
    @ViewInject(R.id.pay_item_uppay_radio)
    private ImageView angel_requestWY1;
    @ViewInject(R.id.pay_item_ali_radio)
    private ImageView angel_requestAL1;
    @ViewInject(R.id.pay_item_sb_radio)
    private ImageView angel_requestSB1;
    @ViewInject(R.id.pay_item_sb_ban)
    private TextView pay_item_sb_ban;

    private String unum = "";

    public String getPayType() {
        return PayType.getPayType(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_commit_fream);
        Util.initTop(this, "提交订单", Integer.MIN_VALUE, null);
        ViewUtils.inject(this);
        unum = this.getIntent().getStringExtra("unum");

        this.findViewById(R.id.pay_item_xj_line).setVisibility(View.GONE);
        View rec = this.findViewById(R.id.pay_item_rec_line);
        View sb = this.findViewById(R.id.pay_item_sb_line);
        View pay_item_ali_line = this.findViewById(R.id.pay_item_ali_line);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);

        PayType.addPayTypeListener(this, R.id.pay_item_rec_line, twoLine, sb, rec, weixin, uppay);

        rec.setVisibility(View.GONE);
        sb.setVisibility(View.GONE);
        gamePay_gc_______9.setVisibility(View.GONE);
        pay_item_ali_line.setVisibility(View.GONE);
        twoLine.setVisibility(View.GONE);


        // 得到传入的数据：
        Intent intent = getIntent();

        final String gameName = intent.getStringExtra("name");
        final String gameMz = intent.getStringExtra("mz");
        final String gameNum = intent.getStringExtra("num");
        final String cardId = intent.getStringExtra("cardId");
        final String price = intent.getStringExtra("price");
        final String success = intent.getStringExtra("success");

        TextView tName = (TextView) findViewById(R.id.gamePay_gp_name);
        TextView tMz = (TextView) findViewById(R.id.gamePay_gp_mz);
        TextView tNum = (TextView) findViewById(R.id.gamePay_gp_number);
        TextView tMoney = (TextView) findViewById(R.id.gamePay_gp_money);

        Util.asynTask(this, "正在获取您的余额...", new AsynTask() {
            @Override
            public Serializable run() {
                HashMap<String, String> map = new HashMap<String, String>();
                Web rec_web = new Web(Web.getMoney, "userid="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&type=rec");
                String recMoney = rec_web.getPlan();
                map.put("rec", Util.getDouble(Util.getDouble(recMoney), 3) + "");

                Web bus_web = new Web(Web.getMoney, "userid="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&type=sb");
                String busMoney = bus_web.getPlan();
                map.put("sb", Util.getDouble(Util.getDouble(busMoney), 3) + "");
                return map;
            }

            @Override
            public void updateUI(Serializable result) {
                HashMap<String, String> data = (HashMap<String, String>) result;
                pay_item_rec_ban.setText("余额:" + Util.getDouble(Util.getDouble(data.get("rec")), 2));
                pay_item_sb_ban.setText("余额:" + Math.round(Util.getDouble(data.get("sb"))));
            }
        });

        tName.setText(gameName);
        tMz.setText(gameMz);
        tNum.setText(gameNum);
        tMoney.setText(price + " 元");
        gamePay_sb_money.setText(Math.round(Util.getDouble(price) * 10) + "");
        tMoney.setTextColor(Color.RED);

        area = (Spinner) findViewById(R.id.gamePay_gp_area);
        server = (Spinner) findViewById(R.id.gamePay_gp_service);

        if (gameName.equals("魔兽世界30元4000分钟 直充") || gameName.equals("魔兽世界15元直充")) {
            LinearLayout la = (LinearLayout) findViewById(R.id.gamePay_gc12);
            LinearLayout ls = (LinearLayout) findViewById(R.id.gamePay_gc13);
            la.setVisibility(View.VISIBLE);
            ls.setVisibility(View.VISIBLE);
        }

        if (success.equals("no")) {
            LinearLayout la = (LinearLayout) findViewById(R.id.gamePay_gc6);
            LinearLayout ls = (LinearLayout) findViewById(R.id.gamePay_gc7);
            la.setVisibility(View.GONE);
            ls.setVisibility(View.GONE);
        }
        // 得到区和服并绑定下拉框
        try {
            Util.asynTask(this, "正在获取游戏大区...", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    HashMap<String, List<GameCommit>> map = (HashMap<String, List<GameCommit>>) runData;
                    List<GameCommit> list = map.get("list");

                    String areaStr = "请选择游戏大区";
                    String serverStr = "请选择游戏服务器";
                    final Map<String, List<String>> area_server = new HashMap<String, List<String>>();
                    List<String> areaList = new ArrayList<String>();
                    if (list != null && list.size() != 0) {
                        for (GameCommit cc : list) {

                            if (!areaList.contains(cc.getArea()))
                                areaStr += ("," + cc.getArea());
                            List<String> serverList = new ArrayList<String>();
                            if (area_server.containsKey(cc.getArea()))
                                serverList.addAll(area_server.get(cc.getArea()));
                            if (!Util.isNull(cc.getServer())) {
                                serverList.add(cc.getServer());
                            }
                            area_server.put(cc.getArea(), serverList);
                            areaList.add(cc.getArea());
                        }
                    }
                    areaList.clear();

                    areaArr = areaStr.split(",");
                    serverArr = serverStr.split(",");

                    if (areaArr.length <= 1) {
                        LinearLayout la = (LinearLayout) findViewById(R.id.gamePay_gc6);
                        la.setVisibility(View.GONE);
                    } else {
                        ArrayAdapter<String> aa = new ArrayAdapter<String>(
                                GameCommitFream.this,
                                android.R.layout.simple_spinner_item, areaArr);
                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        area.setAdapter(aa);
                    }
//					if (serverArr.length <= 1) {
//						LinearLayout ls = (LinearLayout) findViewById(R.id.gamePay_gc7);
//						ls.setVisibility(View.GONE);
//					} else {

                    area.setOnItemSelectedListener(new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int position, long id) {

                            TextView text = (TextView) view;
                            List<String> valueList = area_server.get(text.getText().toString());
                            if (null == valueList || 0 == valueList.size())
                                return;
                            String[] values = valueList.toArray(new String[]{});
                            ArrayAdapter<String> aa = new ArrayAdapter<String>(
                                    GameCommitFream.this,
                                    android.R.layout.simple_spinner_item, values);
                            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            server.setAdapter(aa);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    area.setSelection(0);

//					}
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.getGameAera, "userId="
                            + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&cradid="
                            + cardId);
                    List<GameCommit> list = web.getList(GameCommit.class);
                    HashMap<String, List<GameCommit>> map = new HashMap<String, List<GameCommit>>();
                    map.put("list", list);
                    return map;
                }
            });

            // 提交数据
            Button btn = (Button) findViewById(R.id.gamePay_commitBtn);
            btn.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    v.setEnabled(false);
                    final TextView tAccount = (TextView) findViewById(R.id.gamePay_gp_account);
                    final TextView tAccount_ok = (TextView) findViewById(R.id.gamePay_gp_account_ok);
                    final TextView tpass = (TextView) findViewById(R.id.gamePay_gp_twopass);
                    final TextView tzw = (TextView) findViewById(R.id.gamePay_gp_zw);
                    final TextView tzw_ok = (TextView) findViewById(R.id.gamePay_gp_zw_ok);

                    final String payMoneyType = getPayType();
                    v.setEnabled(true);
                    // 判断是否是魔兽
                    if (gameName.equals("魔兽世界30元4000分钟 直充")
                            || gameName.equals("魔兽世界15元直充")) {
                        if (tzw.getText().toString().equals("")) {
                            Util.show("请输入战网账号!", GameCommitFream.this);
                            return;
                        }
                        if (!tzw_ok.getText().toString()
                                .equals(tzw.getText().toString())) {
                            Util.show("两次输入战网账号不一致,请重新输入!!",
                                    GameCommitFream.this);
                            return;
                        }
                    }
                    if (tAccount.getText().toString().equals("")) {
                        Util.show("请输入游戏账号!", GameCommitFream.this);
                        return;
                    }
                    if (!tAccount_ok.getText().toString()
                            .equals(tAccount.getText().toString())) {
                        Util.show("两次输入游戏账号不一致,请重新输入!!", GameCommitFream.this);
                        return;
                    }
                    // 判断是否是Q币充值
                    if (success.equals("no")) {
                        if (areaArr.length > 1) {
                            if (area.getSelectedItem().equals("0")
                                    || area.getSelectedItem().equals("")) {
                                Util.show("请选择游戏区!", GameCommitFream.this);
                                return;
                            }
                        }
                        if (serverArr.length > 1) {
                            if (server.getSelectedItem().equals("0")
                                    || server.getSelectedItem().equals("")) {
                                Util.show("请选择游戏服!", GameCommitFream.this);
                                return;
                            }
                        }
                    }
                    if ("2".equals(payMoneyType)
                            && "".equals(tpass.getText().toString())) {
                        Util.show("请输入交易密码!", GameCommitFream.this);
                        return;
                    }
                    try {
                        Util.asynTask(GameCommitFream.this, "正在充值游戏...",
                                new IAsynTask() {
                                    @Override
                                    public void updateUI(Serializable runData) {
                                        if (null == runData) {
                                            Util.show("网络错误，请重试！",
                                                    GameCommitFream.this);
                                            return;
                                        }
                                        if ("success".equals(runData + "")) {
                                            Util.showIntent("恭喜你，充值成功！",
                                                    GameCommitFream.this,
                                                    Lin_MainFrame.class,
                                                    Lin_MainFrame.class);
                                            return;
                                        } else if ((runData + "").contains(":")) {
                                            String[] ayy = (runData + "")
                                                    .split(":");
                                            if ("6".equals(payMoneyType))
                                                wxPay(ayy[1], Double
                                                        .parseDouble(ayy[2]));
//											else
//												aliPay(ayy[1], Double
//														.parseDouble(ayy[2]));
                                        } else
                                            Util.show(runData + "",
                                                    GameCommitFream.this);
                                    }

                                    @Override
                                    public Serializable run() {
                                        Web web = new Web(
                                                Web.payGameOrder,
                                                "userId="
                                                        + UserData.getUser()
                                                        .getUserId()
                                                        + "&md5Pwd="
                                                        + UserData.getUser()
                                                        .getMd5Pwd()
                                                        + "&twpPwd="
                                                        + (new MD5()
                                                        .getMD5ofStr(tpass
                                                                .getText()
                                                                .toString())
                                                        + "&oldMoney="
                                                        + gameMz
                                                        + "&newMoney="
                                                        + price
                                                        + "&num="
                                                        + gameNum
                                                        + "&unum=" + unum
                                                        + "&userName="
                                                        + tAccount
                                                        .getText()
                                                        .toString()
                                                        + "&area="
                                                        + (null == area
                                                        .getSelectedItem() ? ""
                                                        : area.getSelectedItem())
                                                        + "&server="
                                                        + (null == server
                                                        .getSelectedItem() ? ""
                                                        : server.getSelectedItem())
                                                        + "&wow="
                                                        + tzw.getText()
                                                        .toString()
                                                        + "&cardId="
                                                        + cardId
                                                        + "&payMoneyType=" + payMoneyType));
                                        return web.getPlan();
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Util.show("网络错误，请重试!", GameCommitFream.this);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Util.showIntent("获取游戏区服错误!", GameCommitFream.this,
                    Lin_MainFrame.class);
        }
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
        new MyPopWindow.MyBuilder(this,msg,"确定",null)
                .setTitle("支付结果通知").build().showCenter();

    }


    private void wxPay(String orderId, double money) {
        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(this, "微信支付中...");
        new MMPay(this, pd, money, orderId, "微信网游支付").pay();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId, "网游充值", money, new AliPayCallBack() {
            @Override
            public void doSuccess(String aliResultCode) {
                Util.show("充值成功！", GameCommitFream.this);
            }

            @Override
            public void doFailure(String aliResultCode) {
                Util.show("充值失败，支付宝状态码：" + aliResultCode, GameCommitFream.this);
            }
        });
    }

    @OnClick({R.id.tv_more})
    public void hideOrShow(View v) {
        TextView tv_more = (TextView) v;
        View ll_hide = findViewById(R.id.ll_hide);
        if (ll_hide.getVisibility() == View.GONE) {
            ll_hide.setVisibility(View.VISIBLE);
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pay_more_show, 0);
        } else {
            ll_hide.setVisibility(View.GONE);
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pay_more_hide, 0);
        }


    }
}
