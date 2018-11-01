package com.mall.view.carMall;

import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.Base.BaseActivity;
import com.google.gson.Gson;
import com.lin.component.CustomProgressDialog;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.serving.community.net.NewWebAPI;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.databinding.ActivityCarListBinding;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.ArrayList;
import java.util.List;

public class CarListActivity extends BaseActivity<ActivityCarListBinding> {


    Caradpter caradpter;
    List<CarpoolBean.ListBean> list = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;


    private void initView() {
        initrecycle();
    }

    private void initrecycle() {
        linearLayoutManager = new LinearLayoutManager(this);

        mBinding.recyclerView.setLayoutManager(linearLayoutManager);
        caradpter = new Caradpter(this, list, 0);
        mBinding.recyclerView.setAdapter(caradpter);
    }


    @Override
    public int getContentViewId() {
        return R.layout.activity_car_list;
    }

    @Override
    public View getattachedview() {
        return null;
    }

    @Override
    protected void initView(ActivityCarListBinding mBinding) {
        mBinding.setActivity(this);
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        initView();

    }

    @Override
    protected void initData(final ActivityCarListBinding mBinding) {
        final CustomProgressDialog cpd = Util.showProgress("", this);
        NewWebAPI.getNewInstance().getWebRequest("/carpool.aspx?call=" + "Get_Carpool_cc" + "&page=1&size=999&search=",
                new NewWebAPIRequestCallback() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！");
                            return;
                        }
                        Log.e("--------", result.toString());
                        Gson gson = new Gson();
                        CarpoolBean carpoolBean = gson.fromJson(result.toString(), CarpoolBean.class);
                        if (!carpoolBean.getCode().equals("200") || carpoolBean.getList() == null || carpoolBean.getList().size() == 0) {
                            Util.show("暂无数据");
                            return;
                        }
                        list.clear();
                        list.addAll(carpoolBean.getList());

                        caradpter.notifyDataSetChanged();


                        mBinding.recyclerView.start();

                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！");
                        return;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBinding.recyclerView.stop();
    }
}
