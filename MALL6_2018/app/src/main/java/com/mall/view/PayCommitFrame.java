package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.ShopAddress;
import com.mall.model.ShopCarItem;
import com.mall.model.ShopCarNumberModel;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.carMall.OrderBeanAll;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能： 生成订单确认页面<br>
 * 时间： 2013-7-3<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class PayCommitFrame extends Activity {
    // 总金额
    @ViewInject(R.id.pay_summoney)
    private TextView sumMoney;
    // 总积分
    @ViewInject(R.id.pay_sumjifen)
    private TextView pay_sumjifen;
    // 总分账
    @ViewInject(R.id.pay_sumrebate1_hf)
    private TextView pay_sumrebate1_hf;
    @ViewInject(R.id.pay_sumrebate1_sb)
    private TextView pay_sumrebate1_sb;

    @ViewInject(R.id.pay_commit_list)
    private ListView listView; // 显示当前需要生成订单的产品
    @ViewInject(R.id.order_commit_scrollView)
    private ScrollView scrollView;
    @ViewInject(R.id.pay_sum)
    private TextView pay_sum;
    @ViewInject(R.id.pay_sbsum)
    private TextView sbsm;
    @ViewInject(R.id.order_commit_submit)
    private TextView order_commit_submit;
    @ViewInject(R.id.order_commit_bottom)
    private View order_commit_bottom;
    @ViewInject(R.id.order_commit_submit_loading)
    private ImageView order_commit_submit_loading;

    @ViewInject(R.id.addressname)
    private TextView addressname;

    @ViewInject(R.id.sellername)
    private TextView sellername;

    @ViewInject(R.id.addresschoose)
    private View addresschoose;


    // 收货地址ID
    private String addId = "";
    private String str="";

    private PayListAdapter adapter;
    private static List<ShopCarItem> shopCarItemList = null;

    // 要够买的guid，组合字符串，包含'
    private String guidAll;
    private String goodsimg;
    private RadioButton radio, radio1, radio2, radio3;
    private RadioGroup radioGroup_addList;
    ShopAddress sa1 = null;
    ShopAddress sa2 = null;
    ShopAddress sa3 = null;
    ShopAddress sa = null;
    boolean defal = false;
    private Context context;

    List<ShopAddress> addlist = new ArrayList<ShopAddress>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.order_commit_frame);
        ViewUtils.inject(this);
        context = this;
        Util.initTitle(this, "付款确认", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findView();
        addresschoose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(context, ShippingAddressActivity.class);
                intent.putExtra("addId", addressname.getTag() + "");
                startActivityForResult(intent, 123);
            }
        });

        if (null != shopCarItemList)
            shopCarItemList.clear();
        init();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.e("请求", resultCode + "");
        switch (requestCode) {
            case 123:
                if (data != null) {
                    str = data.getStringExtra("addId");
                    Log.e("数据", str);
                    Log.e("地址长度", addlist.size() + "");
                    getShopadd1(str);
                }
                break;
            case 456:
                getShopadd();
                break;
            default:
                break;
        }
    }

    private void findView() {
        // TODO Auto-generated method stub
        radioGroup_addList = (RadioGroup) PayCommitFrame.this
                .findViewById(R.id.shop_address_line);

        radio1 = (RadioButton) PayCommitFrame.this.findViewById(R.id.radio1);
        radio2 = (RadioButton) PayCommitFrame.this.findViewById(R.id.radio2);
        radio3 = (RadioButton) PayCommitFrame.this.findViewById(R.id.radio3);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && 0 == event.getRepeatCount()
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Util.showIntent(PayCommitFrame.this, ShopCarFrame.class);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setZoneid(final String zoneid) {
        Util.asynTask(this, "正在获取商品信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<ShopCarItem>> map = (HashMap<String, List<ShopCarItem>>) runData;
                List<ShopCarItem> list = map.get("result");
                if (null != list) {
                    DecimalFormat df = new DecimalFormat("#0.00");
                    shopCarItemList = list;
                    double money = 0.0D;
                    boolean isSBQ = false;
                    String SBQString = "";
                    String NormalString = "";
                    double yf = 0.0D;
                    double sbMoney = 0.0D;
                    int productCount = 0;
                    double sumJifen = 0.0;
                    double sumbate1 = 0.0D;
                    for (ShopCarItem item : list) {
                        // 数量
                        int a = Integer.parseInt(item.getAmount());
                        // 远大售价
                        double p = Double.parseDouble(item.getPrice());
                        // 最低支付
                        double e = Double.parseDouble(item.getExp1());
                        // 邮费
                        double y = Double.parseDouble(item.getYoufei());
                        money += a * p + y;
                        yf += y;
                        SBQString += "11".equals(item.getPfrom()) + "";
                        NormalString += "0".equals(item.getPfrom()) + "";
                        sbMoney += Util.getDouble(item.getSb()) * a + (y * 7);
                        productCount += a;
                        sumJifen += (p - e) * a;
                        sumbate1 += Util.getDouble(item.getRebate1());
                    }
                    if (NormalString.contains("true")
                            && SBQString.contains("true")) {
                        Toast.makeText(PayCommitFrame.this,
                                "您不能同时购买商品区和常规分类的商品！", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    pay_sum.setText(productCount + "");
                    if (!isSBQ) {// 不是商币区，显示正常金额
                        sumMoney.setText("￥" + df.format(money) + "");
                        sbsm.setText((int) sbMoney + "");
                        pay_sumjifen.setText("  " + df.format(sumJifen) + "");
                    } else {
                        yf = 0.00D;
                        sumMoney.setText(sbMoney + "");
                        sbsm.setText((int) sbMoney + "");
                    }
                    pay_sumrebate1_hf.setText("￥" + df.format(sumbate1) + "");
                    pay_sumrebate1_sb.setText("￥" + df.format(sumbate1) + "");
                    if (null != adapter)
                        adapter.delete();
                    listView.setAdapter(adapter = new PayListAdapter(
                            PayCommitFrame.this, list));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.shopItem, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&guid=" + guidAll
                        + "&zid=" + zoneid);
                HashMap<String, List<ShopCarItem>> map = new HashMap<String, List<ShopCarItem>>();
                map.put("result", web.getList(ShopCarItem.class));
                return map;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * 添加收货地址
     *
     * @param v
     */
    @OnClick(R.id.order_pay_add_address)
    public void addAddress(View v) {
        String[] keys = new String[]{"guid"};
        String[] values = new String[]{guidAll};
        Util.showIntent(this, ShopAddressFrame.class, keys, values);
    }

    /**
     * 选择其他收货地址
     *
     * @param view
     */
    @OnClick(R.id.order_pay_sel_ohtherAddress)
    public void selAddress(View view) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        final View rootView = LayoutInflater.from(this).inflate(
                R.layout.sel_all_address, null);
        Util.asynTask(this, "正在获取更多收货地址...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<ShopAddress>> map = (HashMap<String, List<ShopAddress>>) runData;
                List<ShopAddress> list = map.get("list");
                if (null == list) {
                    Util.show("网络错误，请重试！", PayCommitFrame.this);
                    return;
                }
                if (0 == list.size()) {
                    Util.show("没有获取到收货地址！", PayCommitFrame.this);
                    return;
                }
                // OnClickListener click = new OnClickListener() {
                // @Override
                // public void onClick(View v) {
                // addId = v.getTag() + "";
                // // radioGroup_addList = (RadioGroup) PayCommitFrame.this
                // // .findViewById(R.id.shop_address_line);
                // int selRadioId = radioGroup_addList
                // .getCheckedRadioButtonId();
                // if (-1 != selRadioId) {
                // radio = (RadioButton) radioGroup_addList
                // .findViewById(selRadioId);
                // if (radio.isChecked()) {
                // radio.setChecked(false);
                //
                // radio.invalidate();
                // }
                //
                //
                //
                // radioGroup_addList.removeAllViews();
                // radioGroup_addList.removeAllViewsInLayout();
                // radio1.setText(sa.getName() + " - " + sa.getMobilePhone()
                // + " - " + sa.getRegion() + " - " + sa.getAddress());
                //
                // radioGroup_addList.addView(radio1);
                // }
                // setZoneid(v.getTag(-7) + "");
                //
                // dialog.cancel();
                // dialog.dismiss();
                // }
                // };
                LinearLayout root = (LinearLayout) rootView
                        .findViewById(R.id.sel_address_container);
                for (final ShopAddress sa : list) {
                    View itemView = LayoutInflater.from(PayCommitFrame.this)
                            .inflate(R.layout.sel_all_address_item, null);
                    TextView item = (TextView) itemView
                            .findViewById(R.id.sel_item_address);
                    item.setText(sa.getName() + " - " + sa.getMobilePhone()
                            + " - " + sa.getRegion() + " - " + sa.getAddress());
                    item.setTag(sa.getShoppingAddId());
                    item.setTag(-7, sa.getZone());
                    if (sa.getShoppingAddId().equals(addId)) {
                        Resources res = PayCommitFrame.this.getResources();
                        Drawable checked = res
                                .getDrawable(R.drawable.pay_item_checked);
                        checked.setBounds(0, 0, checked.getMinimumWidth(),
                                checked.getMinimumHeight());
                        item.setCompoundDrawables(checked, null, null, null);
                    }
                    item.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            addId = v.getTag() + "";
                            // radioGroup_addList = (RadioGroup)
                            // PayCommitFrame.this
                            // .findViewById(R.id.shop_address_line);
                            // int selRadioId = radioGroup_addList
                            // .getCheckedRadioButtonId();
                            // if (-1 != selRadioId) {
                            // radio = (RadioButton) radioGroup_addList
                            // .findViewById(selRadioId);
                            // if (radio.isChecked()) {
                            // radio.setChecked(false);
                            //
                            // radio.invalidate();
                            // }

                            radioGroup_addList.removeAllViews();
                            radioGroup_addList.removeAllViewsInLayout();
                            radio1.setText(sa.getName() + " - "
                                    + sa.getMobilePhone() + " - "
                                    + sa.getRegion() + " - " + sa.getAddress());

                            sellername.setText(sa.getName() + "   " + sa.getMobilePhone());
                            addressname.setText(sa.getRegion() + " - " + sa.getAddress());
                            addressname.setTag(sa.getShoppingAddId());

                            radio1.setTag(sa.getShoppingAddId());
                            radio1.setChecked(true);
                            radio1.invalidate();
                            radioGroup_addList.invalidate();
                            radioGroup_addList.addView(radio1);
                            // }
                            setZoneid(v.getTag(-7) + "");
                            Log.e("地址id", v.getTag(-7) + "");

                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });
                    root.addView(itemView);
                }
                dialog.setView(rootView, 0, 0, 0, 0);
                dialog.show();
            }

            @Override
            public Serializable run() {
                User user = UserData.getUser();
                Web web = new Web(Web.getShopAddress, "userId="
                        + user.getUserId() + "&md5Pwd=" + user.getMd5Pwd()
                        + "&size=1024");
                List<ShopAddress> list = web.getList(ShopAddress.class);
                HashMap<String, List<ShopAddress>> map = new HashMap<String, List<ShopAddress>>();
                map.put("list", list);
                return map;
            }
        });
    }

    /**
     * 订单付款
     *
     * @param v
     */
    @OnClick(R.id.order_commit_layout)
    public void payOrder(View v) {
        final Map<String, String> data = new HashMap<String, String>();
        RadioGroup addList = (RadioGroup) this
                .findViewById(R.id.shop_address_line);
        int selRadioId = addList.getCheckedRadioButtonId();
        if (-1 != selRadioId) {
            RadioButton radio = (RadioButton) addList.findViewById(selRadioId);
            if (radio.isChecked()) {
                addId = radio.getTag() + "";
            }
        }
        if ("".equals(addId)) {
            Util.show("请选择您的收货地址。", this);
            return;
        }
        StringBuffer ids = new StringBuffer();
        for (ShopCarItem sci : shopCarItemList) {
            // id|名称|颜色|尺码|数量
            String[] cz = sci.getColorAndSize().split("\\|");
            String color = "";
            String size = "";
            if (cz.length == 1) {
                String temp = cz[0];
                if (-1 == temp.indexOf("："))
                    continue;
                String[] temps = temp.split("：");
                if (0 == temp.indexOf("颜色"))
                    color = 2 == temps.length ? temps[1] : "";
                else
                    size = 2 == temps.length ? temps[1] : "";
            } else if (cz.length == 2) {
                for (int i = 0; i < cz.length; i++) {
                    if (-1 == cz[i].indexOf("："))
                        continue;
                    String[] temps = cz[i].split("：");
                    if (0 == cz[i].indexOf("颜色"))
                        color = 2 == temps.length ? temps[1] : "";
                    else
                        size = 2 == temps.length ? temps[1] : "";
                }
            }
            ids.append(sci.getPid() + "||" + Util.get(color) + "|"
                    + Util.get(size) + "|" + sci.getAmount() + ",");
        }
        if (ids.length() > 1)
            ids.setLength(ids.length() - 1);

        StringBuffer message = new StringBuffer();
        for (int i = 0; i < listView.getChildCount(); i++) {
            LinearLayout line = (LinearLayout) listView.getChildAt(i);
            View temp = line.findViewById(R.id.pay_commit_frame_item_message);
            if (temp instanceof EditText) {
                EditText m = (EditText) temp;
                message.append(Util.get(m.getText().toString()) + "|,|");
            }
        }
        if (1 < message.toString().length())
            message.setLength(message.length() - 3);

        order_commit_submit.setTextColor(PayCommitFrame.this.getResources()
                .getColor(R.color.black));
        order_commit_submit.setText("正在创建订单");
        order_commit_bottom.setBackgroundColor(PayCommitFrame.this
                .getResources().getColor(R.color.gray_backgroud));
        AnimeUtil.startImageAnimation(order_commit_submit_loading);

        if (!str.equals("")){
            addId=str;
        }
        data.put("addressId", addId);
        data.put("ids", ids.toString());
        data.put("message", message.toString());
        data.put("userId", UserData.getUser().getUserId());
        data.put("md5Pwd", UserData.getUser().getMd5Pwd());
        data.put("guids", guidAll);

        Log.e("ids=====", ids.toString() + "");
        Log.e("message=====", message.toString() + "");
        Log.e("guids=====", guidAll.toString() + "");

        // for (String key : data.keySet()) {
        // Log.e(key + "=====", data.get(key) + "!!!");
        // }
        Util.asynTask(PayCommitFrame.this, "正在创建您的订单...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (-1 != (runData + "").indexOf("success:")) {
                    String[] strs = (runData + "").split(":");
                    DbUtils db = DbUtils.create(PayCommitFrame.this);
                    try {
                        db.delete(ShopCarNumberModel.class, WhereBuilder.b(
                                "userid", "=", UserData.getUser().getUserId()));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    if (strs.length == 2) {


                        getOrders(strs[1]);


                    } else
                        Util.showIntent(PayCommitFrame.this, OrderFrame.class);
                } else if ((runData + "").contains("-2")) {
                    Util.show("商品信息异常，请重试！", PayCommitFrame.this);
                    AnimeUtil.cancelImageAnimation(order_commit_submit_loading);
                    return;
                } else {
                    Util.show(runData + "", PayCommitFrame.this);
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.createOrder, data);
                return web.getPlan();
            }
        });
    }

    public void getOrders(final String tid) {
        User user = UserData.getUser();
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        map.put("state", "");
        map.put("page", "1");
        map.put("size", "999");
        map.put("orderid", tid);
        final CustomProgressDialog cpd = CustomProgressDialog
                .showProgressDialog(this, "加载中...");
        NewWebAPI.getNewInstance().getWebRequest(
                "/YdaOrder.aspx?call=getMallOrder", map,
                new WebRequestCallBack() {

                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常,请重试", PayCommitFrame.this);
                            return;
                        }
                        JSONObject jsons = JSON.parseObject(result.toString());
                        if (200 != jsons.getIntValue("code")) {
                            Util.show("网络异常,请重试", PayCommitFrame.this);
                            return;
                        }
                        Gson gson = new Gson();
                        OrderBeanAll orderBeanAll = gson.fromJson(result.toString(), OrderBeanAll.class);
                        if (orderBeanAll.getOrder().get(0).getOrdertype().equals("7")) {
                            Util.showIntent(PayCommitFrame.this,
                                    PayMoneyFrame1.class, new String[]{"tid",
                                            "img", "red"}, new String[]{tid,
                                            goodsimg, adapter.getred() + ""});
                        } else {
                            Util.showIntent(PayCommitFrame.this,
                                    PayMoneyFrame.class, new String[]{"tid",
                                            "img", "red"}, new String[]{tid,
                                            goodsimg, adapter.getred() + ""});
                        }


                        Util.order = null;
                        Util.order = orderBeanAll;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.dismiss();
                        super.requestEnd();
                    }

                });
    }


    private void getShopadd1(final String str) {
        Util.asynTask(this, "正在获取收货地址...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {

                HashMap<String, List<ShopAddress>> map = (HashMap<String, List<ShopAddress>>) runData;
                List<ShopAddress> list = map.get("result");


                if (null != list && 0 != list.size()) {

                    addlist.clear();
                    addlist.addAll(list);
                    for (int i = 0; i < addlist.size(); i++) {
                        if (str.equals(addlist.get(i).getShoppingAddId())) {
                            ShopAddress sa1 = addlist.get(i);
                            sellername.setText(sa1.getName() + "   " + sa1.getMobilePhone());
                            addressname.setText(sa1.getRegion() + " - " + sa1.getAddress());
                            addressname.setTag(sa1.getShoppingAddId());
                        }

                    }
                }

            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getShopAddress, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&size=99");
                List<ShopAddress> list = web.getList(ShopAddress.class);
                HashMap<String, List<ShopAddress>> map = new HashMap<String, List<ShopAddress>>();
                map.put("result", list);
                return map;
            }
        });
    }

    private void getShopadd() {
        Util.asynTask(this, "正在获取收货地址...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {

                HashMap<String, List<ShopAddress>> map = (HashMap<String, List<ShopAddress>>) runData;
                List<ShopAddress> list = map.get("result");
                addlist.clear();
                addlist.addAll(list);
                if (null != list && 0 != list.size()) {
                    radioGroup_addList.setVisibility(View.VISIBLE);
                    for (int i = list.size() - 1; i >= 0; i--) {
                        sa = list.get(i);
                        if ("False".equals(sa.getIsDefault())) {
                            defal = false;
                            continue;
                        } else {
                            radioGroup_addList.removeAllViews();
                            radioGroup_addList.removeAllViewsInLayout();
                            defal = true;
                            list.remove(sa);
                            radio1.setText(sa.getName() + " - "
                                    + sa.getMobilePhone() + " - "
                                    + sa.getRegion() + " - " + sa.getAddress());

                            radio1.setTag(sa.getShoppingAddId());

                            sellername.setText(sa.getName() + "   " + sa.getMobilePhone());
                            addressname.setText(sa.getRegion() + " - " + sa.getAddress());
                            addressname.setTag(sa.getShoppingAddId());


                            radio1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(
                                        CompoundButton buttonView,
                                        boolean isChecked) {

                                    if (isChecked) {

                                        radio1.setTag(sa.getShoppingAddId());
                                        setZoneid(sa.getZone());

                                    }
                                }
                            });

                            if (null != list && list.size() >= 1) {
                                sa2 = list.get(list.size() - 1);
                                radio2.setText(sa2.getName() + " - "
                                        + sa2.getMobilePhone() + " - "
                                        + sa2.getRegion() + " - "
                                        + sa2.getAddress());

                                radio2.setTag(sa2.getShoppingAddId());
                            } else {
                                radio2.setVisibility(View.GONE);
                            }

                            if (list.size() >= 2) {
                                sa3 = list.get(list.size() - 2);
                                radio3.setText(sa3.getName() + " - "
                                        + sa3.getMobilePhone() + " - "
                                        + sa3.getRegion() + " - "
                                        + sa3.getAddress());
                                radio3.setTag(sa3.getShoppingAddId());

                            } else {
                                radio3.setVisibility(View.GONE);
                            }
                            break;
                        }

                    }

                    if (!defal) {
                        radioGroup_addList.removeAllViews();
                        radioGroup_addList.removeAllViewsInLayout();

                        if (list.size() == 1) {
                            sa1 = list.get(0);
                            radio1.setText(sa1.getName() + " - "
                                    + sa1.getMobilePhone() + " - "
                                    + sa1.getRegion() + " - "
                                    + sa1.getAddress());
                            addId = sa1.getShoppingAddId();
                            radio1.setTag(addId);
                            radio2.setVisibility(View.GONE);
                            radio3.setVisibility(View.GONE);
                        } else {

                            sa1 = list.get(list.size() - 1);

                            radio1.setText(sa1.getName() + " - "
                                    + sa1.getMobilePhone() + " - "
                                    + sa1.getRegion() + " - "
                                    + sa1.getAddress());
                            radio1.setTag(sa1.getShoppingAddId());
                            radio1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(
                                        CompoundButton buttonView,
                                        boolean isChecked) {

                                    if (isChecked) {

                                        radio1.setTag(sa1.getShoppingAddId());
                                        setZoneid(sa1.getZone());

                                    }
                                }
                            });
                            sa2 = list.get(list.size() - 2);
                            radio2.setText(sa2.getName() + " - "
                                    + sa2.getMobilePhone() + " - "
                                    + sa2.getRegion() + " - "
                                    + sa2.getAddress());
                            radio2.setTag(sa2.getShoppingAddId());

                            if (list.size() >= 3) {
                                sa3 = list.get(list.size() - 3);
                                radio3.setText(sa3.getName() + " - "
                                        + sa3.getMobilePhone() + " - "
                                        + sa3.getRegion() + " - "
                                        + sa3.getAddress());
                                radio3.setTag(sa3.getShoppingAddId());

                            } else {
                                radio3.setVisibility(View.GONE);
                            }

                        }

                    }

                    radio2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {

                            if (isChecked) {

                                radio2.setTag(sa2.getShoppingAddId());
                                setZoneid(sa2.getZone());

                            }
                        }
                    });
                    radio3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {

                            if (isChecked) {

                                radio3.setTag(sa3.getShoppingAddId());
                                setZoneid(sa3.getZone());

                            }
                        }
                    });

                    radioGroup_addList.addView(radio1);
                    radioGroup_addList.addView(radio2);
                    radioGroup_addList.addView(radio3);

                } else {
                    radioGroup_addList.setVisibility(View.GONE);
                    VoipDialog voiDialog = new VoipDialog("对不起，没有获取到收货地址。", PayCommitFrame.this, "去添加", "重新获取",
                            new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    Intent intent = new Intent();
                                    intent.setClass(PayCommitFrame.this,
                                            ShopAddressFrame.class);
                                    intent.putExtra("guid", guidAll);
                                    startActivityForResult(intent, 456);
//									startActivity(intent);
                                }
                            }, new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            getShopadd();

                        }

                    });
                    voiDialog.show();

                }

            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getShopAddress, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&size=99");
                List<ShopAddress> list = web.getList(ShopAddress.class);
                HashMap<String, List<ShopAddress>> map = new HashMap<String, List<ShopAddress>>();
                map.put("result", list);
                return map;
            }
        });
    }


    private void init() {


        String temps = getIntent().getStringExtra("guid");

        if (Util.isNull(temps) || 0 == temps.length()) {
            Util.show("对不起，您还没有选择要购买的产品", this);
            return;
        }
        final StringBuffer guid = new StringBuffer(temps);
        if (guid.toString().endsWith(","))
            guid.setLength(guid.length() - 1);
        guidAll = guid.toString();
        // 获取选中需要购买的商品
        setZoneid("-100");

        // 获取用户收货地址
        getShopadd();
        validatOrder(guidAll);
        listView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    scrollView.requestDisallowInterceptTouchEvent(false);
                else
                    scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void validatOrder(final String guid) {
        Util.asynTask(this, "正在验证商品信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (!(runData + "").contains("success")) {
                    Util.showIntent("预创建订单错误：\n\t" + runData + "",
                            PayCommitFrame.this, ShopCarFrame.class,
                            PayCommitFrame.class);
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.validataShopOrder, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&ids="
                        + guid.toString());
                return web.getPlan();
            }
        });
    }

    class PayCommitItem {
        public ImageView img;
        public TextView name;
        public TextView amount;
        public TextView price;
        public LinearLayout colorSizeLine;
        public TextView color;
        public TextView size;
        public TextView youfei;
        public TextView jifen;
        public EditText message;
    }

    class PayListAdapter extends BaseAdapter {

        private Activity frame;
        private List<ShopCarItem> list;
        private LayoutInflater inflater;
        private BitmapUtils bmUtils;

        public PayListAdapter(Activity frame, List<ShopCarItem> list) {
            super();
            this.frame = frame;
            this.list = list;
            inflater = LayoutInflater.from(frame);
            bmUtils = new BitmapUtils(frame);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return list.get(position).hashCode();
        }

        public void delete() {
            list.clear();
        }

        double allsum = 0.00;

        public double getred() {
            for (int i = 0; i < list.size(); i++) {
                ShopCarItem item = list.get(i);
                double jf = (Util.getDouble(item.getPrice()) - Util.getDouble(item
                        .getExp1())) * Util.getInt(item.getAmount());
                if (jf > 0) {
                    double price = Util.getDouble(item.getPrice());
                    int amount = Util.getInt(item.getAmount());

                    allsum += price * amount;
                }

            }
            return allsum;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PayCommitItem payItem = null;
            if (null == convertView) {
                payItem = new PayCommitItem();
                convertView = inflater.inflate(R.layout.pay_commit_frame_item,
                        null);
                payItem.img = (ImageView) convertView
                        .findViewById(R.id.pay_commit_frame_item_img);
                payItem.name = (TextView) convertView
                        .findViewById(R.id.pay_commit_frame_item_name);
                payItem.amount = (TextView) convertView
                        .findViewById(R.id.pay_commit_frame_item_amount);
                payItem.price = (TextView) convertView
                        .findViewById(R.id.pay_commit_frame_item_money);
                payItem.colorSizeLine = (LinearLayout) convertView
                        .findViewById(R.id.pay_commit_frame_item_color_size_line);
                payItem.color = (TextView) convertView
                        .findViewById(R.id.pay_commit_frame_item_color);
                payItem.size = (TextView) convertView
                        .findViewById(R.id.pay_commit_frame_item_size);
                payItem.youfei = (TextView) convertView
                        .findViewById(R.id.pay_commit_frame_item_youfei);
                payItem.jifen = (TextView) convertView
                        .findViewById(R.id.pay_commit_frame_item_jifen);
                payItem.message = (EditText) convertView
                        .findViewById(R.id.pay_commit_frame_item_message);
                convertView.setTag(payItem);
            } else
                payItem = (PayCommitItem) convertView.getTag();
            ShopCarItem item = list.get(position);
            DecimalFormat df = new DecimalFormat("#0.00");
            if (item.getImg().contains(".")) {
                String href = item.getImg() + "";
                href = href.replaceFirst("img.mall666.cn", Web.imgServer);
                if (position == 0) {
                    goodsimg = href;
                }
                bmUtils.display(payItem.img, href);
            } else
                payItem.img.setImageResource(R.drawable.zw174);
            payItem.name.setText(item.getName());
            payItem.amount.setText("" + item.getAmount());

            String showMoney = item.getPrice();
            showMoney = Util.getDouble(Util.getDouble(showMoney), 3) + "";
            if ("11".equals(item.getPfrom())) {
                showMoney = Util.getDouble(item.getSb()) + "";
                payItem.price.setText("商币："
                        + (int) Double.parseDouble(showMoney));
            } else
                payItem.price.setText("￥"
                        + df.format(Double.parseDouble(showMoney)));
            String[] colorAndSize = (item.getColorAndSize() + "").split("\\|");
            if (2 != colorAndSize.length) {
                if (0 == colorAndSize.length) {
                    colorAndSize = new String[]{"颜色：", "尺码："};
                } else if (1 == colorAndSize.length) {
                    if (colorAndSize[0].contains("颜色：")) {
                        String tempColor = colorAndSize[0];
                        colorAndSize = new String[]{tempColor, "尺码："};
                    } else {
                        String tempSize = colorAndSize[0];
                        colorAndSize = new String[]{"颜色：", tempSize};
                    }
                }
            }
            String color = (colorAndSize[0] + "").replaceFirst("颜色：", "");
            String size = (colorAndSize[1] + "").replaceFirst("尺码：", "");
            payItem.color.setText("颜色：" + color);

            payItem.size.setText("尺码：" + size);
            if ("".equals(color) && "".equals(size)) {
                payItem.colorSizeLine.setVisibility(LinearLayout.GONE);
            } else
                payItem.colorSizeLine.setVisibility(LinearLayout.VISIBLE);
            String youfeiMoney = list.get(position).getYoufei();
            if ("11".equals(item.getPfrom()))
                youfeiMoney = "0.00";
            payItem.youfei.setText("￥"
                    + df.format(Double.parseDouble(youfeiMoney)));

            double jf = (Util.getDouble(item.getPrice()) - Util.getDouble(item
                    .getExp1())) * Util.getInt(item.getAmount());
            payItem.jifen.setText(df.format(jf));
            return convertView;
        }
    }
}
