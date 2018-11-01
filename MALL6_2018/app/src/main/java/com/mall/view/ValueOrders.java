package com.mall.view;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.order.FlimOrders;
import com.mall.order.HotelOrders;
import com.mall.order.LotteryOrders;
import com.mall.order.NetPhoneActivity;
import com.mall.order.PhoneOrders;
import com.mall.order.PlaneOrders;
import com.mall.order.PlayOrders;
import com.mall.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ValueOrders extends Activity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaule_orders);
        ViewUtils.inject(this);
        Util.initTitle(this, "增值业务订单", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.phone_list, R.id.game_list, R.id.hotel_list,
            R.id.plane_list, R.id.lottery_list,
            R.id.telephone_fee_charging_list, R.id.flim_list})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.phone_list:
                intent = new Intent(this, PhoneOrders.class);
                startActivity(intent);
                break;

            case R.id.game_list:
                intent = new Intent(this, PlayOrders.class);
                startActivity(intent);
                break;
            case R.id.hotel_list:
                intent = new Intent(this, HotelOrders.class);
                startActivity(intent);
                break;

            case R.id.plane_list:
                intent = new Intent(this, PlaneOrders.class);
                startActivity(intent);
                break;
            case R.id.lottery_list:
                intent = new Intent(this, LotteryOrders.class);
                startActivity(intent);
                break;

            case R.id.telephone_fee_charging_list:
                intent = new Intent(this, NetPhoneActivity.class);
                startActivity(intent);
                break;
            case R.id.flim_list:
                intent = new Intent(this, FlimOrders.class);
                startActivity(intent);
                break;
        }
    }
}
