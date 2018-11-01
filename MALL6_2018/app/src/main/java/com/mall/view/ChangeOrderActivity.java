package com.mall.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.DefaultOrder;
import com.mall.model.OrderState;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.order.FlimOrders;
import com.mall.order.HotelOrders;
import com.mall.order.NetPhoneActivity;
import com.mall.order.PhoneOrders;
import com.mall.order.PlaneOrders;
import com.mall.order.PlayOrders;
import com.mall.order.RefundOrder;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.util.CircleImageView;
import com.mall.util.UserData;
import com.mall.util.Util;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;
import com.squareup.picasso.Picasso;

@ContentView(R.layout.change_order_popupwindow)
public class ChangeOrderActivity extends BaseActivity {
    @ViewInject(R.id.x_ordermanger_show_all_order)
    private TextView x_ordermanger_show_all_order;
    @ViewInject(R.id.x_ordermanger_money)
    private TextView x_ordermanger_money;
    @ViewInject(R.id.x_ordermanger_fahuo)
    private TextView x_ordermanger_fahuo;
    @ViewInject(R.id.x_ordermanger_pingjia)
    private TextView x_ordermanger_pingjia;
    @ViewInject(R.id.x_ordermanger_tuikuan)
    private TextView x_ordermanger_tuikuan;
    @ViewInject(R.id.x_ordermanger_position)
    private TextView x_ordermanger_position;
    @ViewInject(R.id.x_ordermanger_write_pos)
    private ImageView x_ordermanger_write_pos;
    @ViewInject(R.id.x_ordermanger_show_all_add_order)
    private LinearLayout x_ordermanger_show_all_add_order;
    private Intent intent;
    @ViewInject(R.id.x_ordermanger_face)
    private CircleImageView x_ordermanger_face;
    private ImageLoader imageLoader;
    private User user;

    private TextView bad_fahuo;
    private TextView bad_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(context);
        Util.initTitle(this, "订单管理", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 增值
        x_ordermanger_show_all_add_order
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ValueOrders.class);
                        startActivity(intent);

                    }
                });
        // 我的
        x_ordermanger_show_all_order.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showIntent(context, OrderFrame.class);

            }
        });
        user = UserData.getUser();
        intView();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(ChangeOrderActivity.this,
                    Lin_MainFrame.class);
            intent.putExtra("toTab", "usercenter");
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("ResourceAsColor")
    private void intView() {
        bad_money = findViewById(R.id.bad_money);
        bad_fahuo = findViewById(R.id.bad_fahuo);
        // bad_money.setBackgroundColor(R.color.deep_red);
        // bad_fahuo.setBackgroundColor(R.color.deep_red);
        // bad_pingjia.setBackgroundColor(R.color.deep_red);
        // bad_tuikuan.setBackgroundColor(R.color.deep_red);
        String userFace = UserData.getUser().getUserFace();
        Glide.with(ChangeOrderActivity.this).load(userFace).into(x_ordermanger_face);
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        NewWebAPI.getNewInstance().getWebRequest(
                "/YdaOrder.aspx?call=getOrderNum", map,
                new WebRequestCallBack() {

                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常,请重试", ChangeOrderActivity.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show("网络异常,请重试", ChangeOrderActivity.this);
                            return;
                        }
                        String arrlist = json.getString("list");
                        int allQuality = 0;
                        int faANDsou = 0;
                        List<OrderState> orders = JSON.parseArray(arrlist,
                                OrderState.class);
                        if (orders.size() > 0) {
                            for (OrderState order : orders) {
                                if ("1".equals(order.state)) {
                                    // BadgeView bad = new BadgeView(context,
                                    // x_ordermanger_money);
                                    // bad.setText(order.quantity);
                                    // bad.setGravity(Gravity.CENTER);
                                    // bad.show();
                                    bad_money.setText(order.quantity);
                                    bad_money.setVisibility(View.VISIBLE);

                                } else if ("2".equals(order.state)
                                        || "3".equals(order.state)) {
                                    // BadgeView bad = new BadgeView(context,
                                    // x_ordermanger_fahuo);
                                    // bad.setText(order.quantity);
                                    // bad.setGravity(Gravity.CENTER);
                                    // bad.show();
                                    int a = Integer.parseInt(order.quantity);
                                    faANDsou += a;
                                    bad_fahuo.setText("" + faANDsou);
                                    bad_fahuo.setVisibility(View.VISIBLE);

                                } else if ("4".equals(order.state)) {
                                    // BadgeView bad = new BadgeView(context,
                                    // x_ordermanger_pingjia);
                                    // bad.setText(order.quantity);
                                    // bad.setGravity(Gravity.CENTER);
                                    // bad.show();
                                    // bad_pingjia.setText(order.quantity);
                                    // bad_pingjia.show();

                                } else if ("200".equals(order.state)
                                        || "300".equals(order.state)
                                        || "400".equals(order.state)) {
                                    int a = Integer.parseInt(order.quantity);
                                    allQuality += a;
                                }
                            }
                        }
                    }

                });


        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("userId", user.getUserId());
        map2.put("md5Pwd", user.getMd5Pwd());
        NewWebAPI.getNewInstance().getWebRequest(
                "/address.aspx?call=getMyDefaultAddress", map2,
                new WebRequestCallBack() {

                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常,请重试", ChangeOrderActivity.this);
                            return;
                        }

                        JSONObject json = JSON.parseObject(result.toString());

                        if (200 != json.getIntValue("code")) {
                            //  "code":"400","cache":"33654342","message":"认证错误！"
                            Util.show("网络异常,请重试", ChangeOrderActivity.this);

                            return;
                        }
                        String arrlist = json.getString("list");

                        if ("[]".equals(arrlist)) {
                            return;
                        }
                        List<DefaultOrder> orders = JSON.parseArray(arrlist,
                                DefaultOrder.class);
                        if (orders.size() > 0) {
                            DefaultOrder o = orders.get(0);
                            if (!Util.isNull(o.region)) {
                                x_ordermanger_position.setText("收货地址："
                                        + o.region);
                            }
                        }
                    }

                });

    }

    @OnClick({R.id.x_ordermanger_phone,
            R.id.x_ordermanger_game,
            R.id.x_ordermanger_write_pos, R.id.x_ordermanger_money,
            R.id.x_ordermanger_fahuo, R.id.x_ordermanger_tuikuan,
            R.id.x_ordermanger_pingjia})
    public void Click(View v) {
        Intent intent = new Intent(ChangeOrderActivity.this, OrderFrame.class);
        switch (v.getId()) {
            case R.id.x_ordermanger_phone:
                intent = new Intent(this, PhoneOrders.class);
                startActivity(intent);
                break;
            case R.id.x_ordermanger_game:
                intent = new Intent(this, PlayOrders.class);
                startActivity(intent);
                break;
            case R.id.x_ordermanger_write_pos:
                Util.showIntent(context, ShopAddressManagerFrame.class);
                break;
            case R.id.x_ordermanger_money:
                intent = new Intent(ChangeOrderActivity.this, OrderFrame.class);
                intent.putExtra("key", "待付款");
                startActivity(intent);
                break;
            case R.id.x_ordermanger_fahuo:
                intent = new Intent(ChangeOrderActivity.this, OrderFrame.class);
                intent.putExtra("key", "已付款");
                startActivity(intent);
                break;
            case R.id.x_ordermanger_pingjia:
                intent = new Intent(ChangeOrderActivity.this, OrderFrame.class);
                intent.putExtra("key", "成功");
                startActivity(intent);
                break;
            case R.id.x_ordermanger_tuikuan:
                Util.showIntent(ChangeOrderActivity.this, RefundOrder.class);
                break;

        }
    }

}
