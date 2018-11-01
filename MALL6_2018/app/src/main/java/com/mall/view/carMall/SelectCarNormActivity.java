package com.mall.view.carMall;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Base.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.zhouwei.library.CustomPopWindow;
import com.lin.component.CustomProgressDialog;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.databinding.ActivitySelectCarNormBinding;
import com.mall.view.databinding.PopwindowrecycleBinding;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SelectCarNormActivity extends BaseActivity<ActivitySelectCarNormBinding> {

    CarModeAdapter carModeAdapter;
    CarModeAdapter carModelistAdapter;

    Drawable selectdr = SelectorFactory.newShapeSelector()
            .setStrokeWidth(1)
            .setDefaultBgColor(Color.parseColor("#FF364E"))
            .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
            .setPressedStrokeColor(Color.parseColor("#F3F3F3"))
            .setCornerRadius(2)
            .create();

    Drawable unselectdr = SelectorFactory.newShapeSelector()
            .setStrokeWidth(1)
            .setDefaultBgColor(Color.parseColor("#ECECEC"))
            .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
            .setPressedStrokeColor(Color.parseColor("#F3F3F3"))
            .setCornerRadius(2)
            .create();

    List<CarModeBean> modes = new ArrayList<CarModeBean>();

    public String selectStr = "";

    @Override
    public int getContentViewId() {
        return R.layout.activity_select_car_norm;
    }

    @Override
    public View getattachedview() {
        return null;
    }

    int lastmoney = 0;

    @Override
    protected void initView(ActivitySelectCarNormBinding mBinding) {
        mBinding.setActivity(this);
        mBinding.topBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        Intent intent = getIntent();
        if (intent.hasExtra("money")) {
            lastmoney = Integer.parseInt(intent.getStringExtra("money"));
        }

        Observable.create(new ObservableOnSubscribe<CarModeBean>() {
            @Override
            public void subscribe(ObservableEmitter<CarModeBean> e) throws Exception {
                while (lastmoney >= 10) {


                    e.onNext(new CarModeBean(false, lastmoney + "万", selectdr, unselectdr));
                    if (Util.isNull(selectStr)) {
                        selectStr = lastmoney + "";
                    }

                    lastmoney -= 5;
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<CarModeBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(CarModeBean carModeBean) {
                Log.e("carModeBean", "11" + carModeBean.getItemStr());
                modes.add(carModeBean);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

                if (modes.size() > 0) {
                    modes.get(0).setSelect(true);
                }
                initRecycle();
            }


        });


        mBinding.passEditText.setBackground(SelectorFactory.newShapeSelector()
                .setStrokeWidth(1)
                .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                .setDefaultStrokeColor(Color.parseColor("#787878"))
                .setSelectedStrokeColor(Color.parseColor("#FF2145"))
                .setCornerRadius(2)
                .create());
//
        mBinding.listmodelin.setBackground(SelectorFactory.newShapeSelector()
                .setStrokeWidth(1)
                .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                .setDefaultStrokeColor(Color.parseColor("#787878"))
                .setSelectedStrokeColor(Color.parseColor("#FF2145"))
                .setCornerRadius(2)
                .create());

    }

    public void submit(View view) {

        Log.e("选择的档位", "selectStr" + selectStr);


        final CustomProgressDialog cpd = Util.showProgress("提交中...", this);

        NewWebAPI.getNewInstance().getWebRequest("/carpool.aspx" + "?call=buy_Carpool_coupons" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        + "&car_type=" + selectStr.replace("万", "") + "&twoPwd=" + new MD5().getMD5ofStr(mBinding.passEditText.getText().toString())
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
                        Util.show("提交成功", context);
                        Intent intent = new Intent(context, CarRightActivity.class);
                        startActivity(intent);
                        finish();

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

    public void openMenu(View view) {

        Log.e("点击", "vvvv" + view.getId());


        View contentView = LayoutInflater.from(this).inflate(R.layout.popwindowrecycle, null);
        PopwindowrecycleBinding mbinding = DataBindingUtil.bind(contentView);
        //处理popWindow 显示内容
        handleListView(mbinding);
        //创建并显示popWindow
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = view.getWidth();
        int height = 500;
        customPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .size(width, height)//显示大小
                .create()
                .showAsDropDown(view, 0, 20);
    }

    CustomPopWindow customPopWindow;

    private void handleListView(PopwindowrecycleBinding mbinding) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        mbinding.popRe.setLayoutManager(layoutManager);
        carModelistAdapter = new CarModeAdapter(context, modes, 1);
        mbinding.popRe.setAdapter(carModelistAdapter);
        carModelistAdapter.notifyDataSetChanged();
    }

    public void setSelectdr(String str) {
        selectStr = str;
        if (customPopWindow != null) {
            customPopWindow.dissmiss();
        }
        mBinding.carMode.setText(selectStr);
    }

    private void initRecycle() {

        if (modes.size() > 12) {
            mBinding.listmode.setVisibility(View.VISIBLE);
            mBinding.cardRcv.setVisibility(View.GONE);

        } else {
            mBinding.listmode.setVisibility(View.GONE);
            mBinding.cardRcv.setVisibility(View.VISIBLE);
            GridLayoutManager layoutManager = new GridLayoutManager(context, 4);

            mBinding.cardRcv.setLayoutManager(layoutManager);
//            HashMap<String, Integer> grmgrHashMap = new HashMap<>();
//
//            grmgrHashMap.put(com.mall.view.RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 54);//底部间距
//
//            grmgrHashMap.put(com.mall.view.RecyclerViewSpacesItemDecoration.LEFT_DECORATION, 27);//左间距
//
//            grmgrHashMap.put(com.mall.view.RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 27);//右间距
//            mBinding.cardRcv.addItemDecoration(new com.mall.view.RecyclerViewSpacesItemDecoration(grmgrHashMap));
            carModeAdapter = new CarModeAdapter(context, modes, 0);
            mBinding.cardRcv.setAdapter(carModeAdapter);
        }


    }

    @Override
    protected void initData(ActivitySelectCarNormBinding mBinding) {

    }
}
