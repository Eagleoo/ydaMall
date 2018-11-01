package com.mall.order;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.CustomProgressDialog;
import com.mall.adapter.OrderTuiKuan;
import com.mall.model.OrderOne;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.tuikuan_layout)
public class RefundOrder extends Activity {
    @ViewInject(R.id.ListView)
    private ListView ListView;
    private int page = 1;
    List<OrderOne> order_list_one = new ArrayList<OrderOne>();
    private OrderTuiKuan adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        Util.initTitle(RefundOrder.this, "退款/退换货", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter = new OrderTuiKuan(RefundOrder.this, order_list_one);
        ListView.setAdapter(adapter);
        getOrders();
    }

    public void getOrders() {
        User user = UserData.getUser();
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        map.put("state", "" + 200 + "," + 300 + "," + 400);
        map.put("page", "" + page);
        map.put("size", "9999");
        map.put("pass", "1");
        final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "正在获取订单...");
        NewWebAPI.getNewInstance().getWebRequest("/YdaOrder.aspx?call=getMallOrder", map, new WebRequestCallBack() {

            @Override
            public void success(Object result) {
                super.success(result);
                if (null == result) {
                    Util.show("网络异常,请重试", RefundOrder.this);
                    return;
                }
                JSONObject jsons = JSON.parseObject(result.toString());
                if (200 != jsons.getIntValue("code")) {
                    Util.show("网络异常,请重试", RefundOrder.this);
                    return;
                }
                String arrlist = jsons.getString("order");
                List<OrderOne> order_list = JSON.parseArray(arrlist, OrderOne.class);
                if ((order_list.size() == 0)) {
                    Util.show("暂无订单信息!", RefundOrder.this);
                    return;
                }
                List<OrderOne> list = new ArrayList<>();
                for (OrderOne orderOne : order_list) {
                    Log.e("secondOrder", "ll" + orderOne.secondOrder);
                    if (!Util.isNull(orderOne.secondOrder) && !orderOne.secondOrder.equals("[]")) {
                        list.add(orderOne);
                    }

                }

                adapter.addAll(list);
            }

            @Override
            public void requestEnd() {
                cpd.dismiss();
                page++;
                super.requestEnd();
            }

        });
    }

}
