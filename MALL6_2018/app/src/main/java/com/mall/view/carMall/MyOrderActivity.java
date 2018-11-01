package com.mall.view.carMall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.CustomProgressDialog;
import com.mall.adapter.OrderAllAdapter;
import com.mall.model.OrderOne;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyOrderActivity extends AppCompatActivity {

    @ViewInject(R.id.orderListView)
    ListView orderListView;

    private OrderAllAdapter orderallAdapter;
    List<OrderOne> order_list = new ArrayList<OrderOne>();

    String ordertype = "7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ViewUtils.inject(this);
        Util.initTitle(this, "我的订单", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        if (intent.hasExtra("ordertype")) {
            ordertype = intent.getStringExtra("ordertype");
        }
        getOrders();
    }

    public void getOrders() {
        User user = UserData.getUser();
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        map.put("state", "");
        map.put("page", "1");
        map.put("size", "999");
        map.put("type", ordertype);
        final CustomProgressDialog cpd = CustomProgressDialog
                .showProgressDialog(this, "正在获取订单...");
        NewWebAPI.getNewInstance().getWebRequest(
                "/YdaOrder.aspx?call=getMallOrder", map,
                new WebRequestCallBack() {

                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常,请重试", MyOrderActivity.this);
                            return;
                        }
                        JSONObject jsons = JSON.parseObject(result.toString());
                        if (200 != jsons.getIntValue("code")) {
                            Util.show("网络异常,请重试", MyOrderActivity.this);
                            return;
                        }
                        String arrlist = jsons.getString("order");
                        order_list = JSON.parseArray(arrlist, OrderOne.class);
                        if (null == order_list || order_list.size() == 0) {
                            String str = "你还没有订单，立即去购物吧";
                            new MyPopWindow.MyBuilder(MyOrderActivity.this, str, "进入创客商城", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(MyOrderActivity.this, CarShopActivity.class);
                                    MyOrderActivity.this.startActivity(intent);
                                }
                            }).setColor("#F13232")
                                    .setisshowclose(true)
                                    .build().showCenter();
                            return;
                        }
                        List<OrderOne> delList = new ArrayList();// 用来装需要删除的元素
                        Iterator<OrderOne> iterator = order_list.iterator();
                        while (iterator.hasNext()) {
                            OrderOne o = iterator.next();
                            if (Util.isNull(o.secondOrder) || "[]".equals(o.secondOrder)) {
                                delList.add(o);
                            }
                        }
                        order_list.removeAll(delList);
                        orderallAdapter = new OrderAllAdapter(MyOrderActivity.this, order_list);
                        orderListView.setAdapter(orderallAdapter);
                    }

                    @Override
                    public void requestEnd() {
                        cpd.dismiss();
                        super.requestEnd();
                    }

                });
    }
}
