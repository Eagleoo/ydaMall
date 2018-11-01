package com.mall.view.carMall;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.Base.BaseActivity;
import com.mall.net.Web;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.databinding.ActivityCarRightBinding;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CarRightActivity extends BaseActivity<ActivityCarRightBinding> {


    @Override
    public int getContentViewId() {
        return R.layout.activity_car_right;
    }

    @Override
    public View getattachedview() {
        return null;
    }

    @Override
    protected void initView(ActivityCarRightBinding mBinding) {
        mBinding.setActivity(this);
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        mBinding.toCar.setBackground(com.mall.view.SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#FD0F2B"))
                .setCornerRadius(20)
                .create());
        Date dataDay = new Date(System.currentTimeMillis());//获取当前时间

        SimpleDateFormat dateDay = new SimpleDateFormat("yyyy-MM-dd   HH:mm");
        String day = dateDay.format(dataDay);
        mBinding.subminttime.setText("提交时间为:" + day);
        mBinding.toCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Util.showIntent(context, StyleActivity.class);
                Util.showIntent(context, WebViewActivity.class, new String[]{"url", "title"}, new String[]{Web.imageip + "/phone/car/car.html", "车辆一览"});
                finish();
            }
        });
    }

    @Override
    protected void initData(ActivityCarRightBinding mBinding) {

    }
}
