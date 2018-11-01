package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.adapter.OrderAllAdapter;
import com.mall.adapter.OrderDealSuccessAdapter;
import com.mall.adapter.OrderFuKuan;
import com.mall.adapter.OrderShouHuo;
import com.mall.model.OrderOne;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 功能： 订单管理<br>
 * 时间： 2013-3-7<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class OrderFrame extends Activity {

    private boolean isRefreshFoot = false;
    private int currentPageShop = 1;
    @ViewInject(R.id.fukuan)
    private TextView fukuan;
    @ViewInject(R.id.all)
    private TextView all;
    @ViewInject(R.id.shouhuo)
    private TextView shouhuo;
    @ViewInject(R.id.chenggong)
    private TextView chenggong;
    @ViewInject(R.id.all_bottom)
    private View all_bottom;
    @ViewInject(R.id.fukuan_bottom)
    private View fukuan_bottom;
    @ViewInject(R.id.shouhuo_bottom)
    private View shouhuo_bottom;
    @ViewInject(R.id.orderListView)
    private ListView orderListView;
    @ViewInject(R.id.chenggong_bottom)
    private View chenggong_bottom;
    private View[] allViews;
    private TextView[] allTextViews;

    private OrderAllAdapter orderallAdapter;
    private OrderDealSuccessAdapter adapter;
    private String state = "";
    List<OrderOne> order_list_one = new ArrayList<OrderOne>();
    private VoipDialog voip;
    private OrderShouHuo shouhuoAdapter = null;
    private OrderFuKuan fukuanAdapter;
    private VoipDialog voipDialog;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        ViewUtils.inject(this);
        user = UserData.getUser();

        allTextViews = new TextView[]{all, fukuan, shouhuo, chenggong};
        allViews = new View[]{all_bottom, fukuan_bottom, shouhuo_bottom,
                chenggong_bottom};
    }

    @Override
    public void onStart() {
        super.onStart();
        currentPageShop = 1;
        init();
    }

    @OnClick({R.id.all, R.id.fukuan, R.id.shouhuo, R.id.chenggong})
    public void onclick(View view) {
        order_list_one.clear();
        switch (view.getId()) {

            case R.id.all:// 所有
                change(0);
                currentPageShop = 1;
                state = "";
                orderListView.setAdapter(null);
                orderallAdapter = new OrderAllAdapter(OrderFrame.this,
                        order_list_one);
                orderListView.setAdapter(orderallAdapter);
                firstpageshop(false);
                // scrollPageshop();
                break;

            case R.id.fukuan:// 付款
                change(1);
                currentPageShop = 1;
                state = "1";
                // orderAdapter = new OrderAdapter(nowState);
                fukuanAdapter = new OrderFuKuan(OrderFrame.this, order_list_one);
                orderListView.setAdapter(fukuanAdapter);
                firstpageshop(false);
                scrollPageshop();
                break;
            case R.id.shouhuo:// 收货
                change(2);
                currentPageShop = 1;
                state = "2";
                // orderAdapter = new OrderAdapter(nowState);
                shouhuoAdapter = new OrderShouHuo(OrderFrame.this, order_list_one);
                orderListView.setAdapter(shouhuoAdapter);

                shouhuoAdapter.setCallBackSh(new OrderShouHuo.CallBackSH() {

                    public void doback(String tag, int point) {
                        if (tag.equals("1")) {
                            shouhuoAdapter.clear2();
                            currentPageShop = 1;
                            firstpageshop(false);
                        } else if(tag.equals("4")){
                            change(3);
                            currentPageShop = 1;
                            state = "4";
                            adapter = new OrderDealSuccessAdapter(OrderFrame.this);
                            adapter.clear();
                            orderListView.setAdapter(adapter);
                            getSuccessOrders();
                            scrollPageshop();


                        }else {

                            Intent intent = new Intent(OrderFrame.this, ReturnGoodsActivity.class);
                            intent.putExtra("list", (Serializable) order_list_one.get(point));
                            startActivity(intent);
                        }

                    }
                });
                firstpageshop(false);
                scrollPageshop();
                break;
            case R.id.chenggong:// 成功
                change(3);
                currentPageShop = 1;
                state = "4";
                adapter = new OrderDealSuccessAdapter(OrderFrame.this);
                adapter.clear();
                orderListView.setAdapter(adapter);
                getSuccessOrders();
                scrollPageshop();
                break;
        }
    }

    private void getSuccessOrders() {
        // TODO Auto-generated method stub
        final CustomProgressDialog dialog = Util.showProgress("正在获取您的订单信息...",
                OrderFrame.this);
        NewWebAPI.getNewInstance().getWebRequest(
                "/YdaOrder.aspx?call=getMallOrder&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&pageSize=20&page="
                        + (currentPageShop) + "&state=" + state,
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        // TODO Auto-generated method stub
                        Util.show("网络超时，请重试！", OrderFrame.this);
                        dialog.cancel();
                        dialog.dismiss();
                        return;
                    }

                    @Override
                    public void success(Object result) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                        dialog.dismiss();
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", OrderFrame.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"),
                                    OrderFrame.this);
                            return;
                        }
                        List<OrderOne> list = JSON.parseArray(
                                json.getString("order"), OrderOne.class);
                        if (null == list || list.size() == 0) {
                            if (currentPageShop == 1) {
                                Util.show("您还没有订单信息...", OrderFrame.this);
                            } else {
                                Util.show("没有更多的订单信息...", OrderFrame.this);
                            }
                            return;
                        } else {

                            DisplayMetrics dm = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(
                                    dm);

                            adapter.setWidth(dm.widthPixels / 6 * 5);
                            adapter.setList(list);
                        }

                    }

                    @Override
                    public void requestEnd() {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                        dialog.dismiss();
                    }

                    @Override
                    public void fail(Throwable e) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                        dialog.dismiss();
                        Util.show("网络异常，请重试！", OrderFrame.this);
                        return;
                    }
                });
    }

    public void change(int state) {
        for (int i = 0; i < 4; i++) {
            allTextViews[i].setBackgroundColor(getResources().getColor(
                    R.color.transparent));
            allViews[i].setVisibility(View.INVISIBLE);
            allTextViews[i].setTextColor(getResources()
                    .getColor(R.color.black2));
        }
        allTextViews[state].setTextColor(getResources().getColor(
                R.color.new_headertop));
        allViews[state].setVisibility(View.VISIBLE);

    }

    public void firstpageshop(boolean isFirst) {
        getOrders(isFirst);

    }

    public void scrollPageshop() {
        orderListView.setOnScrollListener(new OnScrollListener() {
            int lastItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentPageShop++;
                if ("".equals(state)) {
                    if (lastItem >= orderallAdapter.getCount()
                            && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                        // getFlimOrders();
                        getOrders(false);
                    }
                } else if ("1".equals(state)) {
                    if (lastItem >= fukuanAdapter.getCount()
                            && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                        // getFlimOrders();
                        getOrders(false);
                    }
                } else if ("2".equals(state)) {
                    if (lastItem >= shouhuoAdapter.getCount()
                            && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                        // getFlimOrders();
                        getOrders(false);
                    }
                } else if ("4".equals(state)) {
                    if (lastItem >= adapter.getCount()
                            && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                        // getFlimOrders();
                        getSuccessOrders();
                    }
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    public void getOrders(final boolean isFirst) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        if (state.equals("2")) {
            map.put("state", "2,3");
        } else {
            map.put("state", state);
        }
        map.put("page", "" + currentPageShop);
        map.put("size", "20");
        final CustomProgressDialog cpd = CustomProgressDialog
                .showProgressDialog(this, "正在获取订单...");
        NewWebAPI.getNewInstance().getWebRequest(
                "/YdaOrder.aspx?call=getMallOrder", map,
                new WebRequestCallBack() {

                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常,请重试", OrderFrame.this);
                            return;
                        }
                        JSONObject jsons = JSON.parseObject(result.toString());
                        if (200 != jsons.getIntValue("code")) {
                            Util.show("网络异常,请重试", OrderFrame.this);
                            return;
                        }
                        String arrlist = jsons.getString("order");
                        List<OrderOne> order_list = JSON.parseArray(arrlist,
                                OrderOne.class);
                        if (null == order_list || order_list.size() == 0) {
                            if (currentPageShop == 1) {
                                Util.show("暂无订单信息!", OrderFrame.this);
                            } else {
//								Util.show("暂无更多订单信息!", OrderFrame.this);
                            }
                            return;
                        }
                        List<OrderOne> delList = new ArrayList();// 用来装需要删除的元素
                        Iterator<OrderOne> iterator = order_list.iterator();
                        while (iterator.hasNext()) {
                            OrderOne o = iterator.next();
                            if (Util.isNull(o.secondOrder)
                                    || "[]".equals(o.secondOrder)) {
                                delList.add(o);
                            }

                        }
                        order_list.removeAll(delList);
                        if (state.equals("1")) {
                            Log.i("tag", "--1-");
                            if (null == fukuanAdapter) {
                                fukuanAdapter = new OrderFuKuan(
                                        OrderFrame.this, order_list_one);
                                orderListView.setAdapter(fukuanAdapter);
                            }
                            if (1 == currentPageShop) {
                                fukuanAdapter.clear();
                            }
                            fukuanAdapter.setList(order_list);

                            // order_list_one.addAll(order_list);
                        } else if (state.equals("2")) {
                            Log.i("tag", "-2--");


                            if (null == shouhuoAdapter) {

                                shouhuoAdapter = new OrderShouHuo(
                                        OrderFrame.this, order_list_one);
                                orderListView.setAdapter(shouhuoAdapter);
                            }
                            if (1 == currentPageShop) {

                                shouhuoAdapter.clear2();
                            }

                            shouhuoAdapter.setList(order_list);
                            // order_list_one.addAll(order_list);
                        } else if (state.equals("")) {
                            Log.i("tag", "---");
                            if (null == orderallAdapter) {
                                orderallAdapter = new OrderAllAdapter(
                                        OrderFrame.this, order_list_one);
                                orderListView.setAdapter(orderallAdapter);
                            }
                            if (1 == currentPageShop) {
                                orderallAdapter.clear();
                            }

                            orderallAdapter.setList(order_list);
                            // order_list_one.addAll(order_list);
                        }

                        // orderallAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void requestEnd() {
                        cpd.dismiss();
                        super.requestEnd();
                    }

                });
    }

    public void init() {

        orderListView = findViewById(R.id.orderListView);
        Util.initTitle(this, "我的购物订单", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getIntentData();
        //		getOrders(true);
        // if (null == orderallAdapter) {
        // orderallAdapter = new OrderAllAdapter(OrderFrame.this,
        // order_list_one);
        // orderListView.setAdapter(orderallAdapter);
        // }

        String key = OrderFrame.this.getIntent().getStringExtra("key") + "";
        if (key.contains("已发货")) {
            OrderFrame.this.findViewById(R.id.shouhuo).performClick();
        } else if (key.contains("已付款")) {
            OrderFrame.this.findViewById(R.id.shouhuo).performClick();

        } else if (key.contains("确认收货")) {
            OrderFrame.this.findViewById(R.id.chenggong).performClick();
        } else if (key.contains("待付款")) {
            OrderFrame.this.findViewById(R.id.fukuan).performClick();
        } else if (key.contains("成功")) {
            OrderFrame.this.findViewById(R.id.chenggong).performClick();
        } else {
            OrderFrame.this.findViewById(R.id.all).performClick();
        }
    }

    private void getIntentData() {

        String type = this.getIntent().getStringExtra("key");
        if (!Util.isNull(type)) {
            if ("待付款".equals(type)) {
                state = "1";
            } else if ("已付款".equals(type)) {
                state = "2";
            } else if ("成功".equals(type)) {
                state = "4";
            }
        }
    }

}
