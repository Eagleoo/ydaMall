package com.mall.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.Bean.ReturnGoodsBean;
import com.mall.adapter.OrderShouHuo;
import com.mall.adapter.OrderShouHuoTwo;
import com.mall.model.OrderOne;
import com.mall.model.OrderTwo;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.util.ArrayList;
import java.util.List;


@ContentView(R.layout.activity_return_goods)
public class ReturnGoodsActivity extends AppCompatActivity {
    @ViewInject(R.id.goods)
    private ListView goodlist;

    @ViewInject(R.id.tuihuo)
    private RadioButton tuihuo;

    @ViewInject(R.id.huanhuo)
    private RadioButton huanhuo;

    @ViewInject(R.id.inputStr)
    private EditText inputStr;

    @ViewInject(R.id.submit)
    private TextView submit;

    @ViewInject(R.id.rg)
    private RadioGroup rg;

    private OrderShouHuo shouhuoAdapter;

    private Context context;

    ReturnGoodsBean returnGoodsBean;
    StateListDrawable drawable1;
    StateListDrawable drawable2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        returnGoodsBean = new ReturnGoodsBean();
        returnGoodsBean.setRtype("1");

        context = this;
        drawable1 = SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#ffffff"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setCornerRadius(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#ffffff"))
                .create();
        drawable2 = SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#ffffff"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setCornerRadius(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#49afef"))
                .create();
        initList();

        inputStr.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#ffffff"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setCornerRadius(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#c7c7c7"))
                .setSelectedStrokeColor(Color.parseColor("#49afef"))
                .create());
        submit.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#49afef"))
                .setPressedBgColor(Color.parseColor("#7DC3EF"))
                .setCornerRadius(Util.dpToPx(context, 1))
                .create());
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.tuihuo:

                        returnGoodsBean.setRtype("1");
                        break;
                    case R.id.huanhuo:
                        returnGoodsBean.setRtype("2");

                        break;
                }
            }
        });
        inputStr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String message = editable.toString();
                returnGoodsBean.setRemark(message);
            }
        });
    }

    @OnClick({R.id.submit, R.id.back})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.submit:
                getSuccessOrders(returnGoodsBean);
                break;
            case R.id.back:
                finish();
                break;

        }

    }


    private void initList() {
        OrderOne orderOne = (OrderOne) getIntent().getSerializableExtra("list");
        String orderId = getIntent().getStringExtra("orderId");
        final List<OrderTwo> twoList = new ArrayList<OrderTwo>();
        twoList.addAll(JSON.parseArray(orderOne.secondOrder,
                OrderTwo.class));
        if (twoList.size() > 0) {

            returnGoodsBean.setSorderId(twoList.get(0).secondOrderId);
            for (OrderTwo orders : twoList) {
                orders.number = Integer.parseInt(orders.quantity);
                returnGoodsBean.setPid(returnGoodsBean.getPid() + orders.productId + ",");
                returnGoodsBean.setPid_v(returnGoodsBean.getPid_v() + orders.quantity + ",");
            }
            Log.e("数据1111", "LKK" + returnGoodsBean.toString());
            OrderShouHuoTwo adapter = new OrderShouHuoTwo(ReturnGoodsActivity.this, orderOne.orderId,
                    twoList, "2");
            adapter.setCallBack(new OrderShouHuoTwo.CallBack() {
                @Override
                public void doback(String tag) {
                    if (tag.equals("-1")) {
                        returnGoodsBean.setPid("");
                        returnGoodsBean.setPid_v("");
                        for (OrderTwo orderTwo : twoList) {
                            if (orderTwo.number > 0) {
                                returnGoodsBean.setPid(returnGoodsBean.getPid() + orderTwo.productId + ",");
                                returnGoodsBean.setPid_v(returnGoodsBean.getPid_v() + orderTwo.number + ",");
                            }

                        }
                        Log.e("数据", "LKK" + returnGoodsBean.toString());
                    }

                }
            });
            goodlist.setAdapter(adapter);
        }

    }

    private void getSuccessOrders(ReturnGoodsBean returnGoodsBean) {
//    private void getSuccessOrders(String sorderId, String pid, String pid_v, String remark, String rtype) {
        // TODO Auto-generated method stub
//        if (true) {
//            Log.e("最终", "returnGoodsBean" + returnGoodsBean.toString());
//            return;
//        }
        if (returnGoodsBean.getPid_v().contains("0")) {
            Toast.makeText(context, "请选择退换货的数量", Toast.LENGTH_LONG).show();
            return;
        }

        final CustomProgressDialog dialog = Util.showProgress("正在取消订单...",
                context);
        NewWebAPI.getNewInstance().getWebRequest(
                "/YdaOrder.aspx?call=Return_Order&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&sorderId=" + returnGoodsBean.getSorderId() + "&remark=" + returnGoodsBean.getRemark()
                        + "&pid=" + returnGoodsBean.getPid() + "&pid_v=" + returnGoodsBean.getPid_v() + "&rtype=" + returnGoodsBean.getRtype()

                ,
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        // TODO Auto-generated method stub
                        Util.show("网络超时，请重试！", context);
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
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        com.alibaba.fastjson.JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }

                        Util.show(json.getString("message"), context);
                        finish();


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
                        Util.show("网络异常，请重试！", context);
                        return;
                    }
                });
    }

}
