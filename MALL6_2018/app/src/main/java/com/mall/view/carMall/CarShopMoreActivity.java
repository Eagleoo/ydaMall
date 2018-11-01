package com.mall.view.carMall;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.Base.BaseActivity;
import com.google.gson.Gson;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.model.recommendbean;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.ProductDeatilFream;
import com.mall.view.ProxySiteFrame;
import com.mall.view.R;
import com.mall.view.databinding.LayoutCarshopmoreBinding;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/8.
 */

public class CarShopMoreActivity extends BaseActivity<LayoutCarshopmoreBinding> {
    ShopAdapter myShop;
    List<recommendbean.ListBean> listBeans = new ArrayList<>();
    public static String TITLE = "TITLE";
    private String title = "";

    @Override
    public int getContentViewId() {
        return R.layout.layout_carshopmore;
    }

    @Override
    public View getattachedview() {
        return null;
    }

    @Override
    protected void initView(LayoutCarshopmoreBinding mBinding) {
        mBinding.setActivity(this);
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        myShop = new ShopAdapter(context, listBeans);
        Intent intent = getIntent();
        title = intent.getStringExtra(TITLE) + "";
        mBinding.center.setText(title);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        mBinding.morere.setLayoutManager(gridLayoutManager);
        mBinding.morere.setAdapter(myShop);
        myShop.setmOnRecyclerviewItemClickListener(new BaseRecycleAdapter.OnRecyclerviewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                shopping(listBeans.get(position));
            }
        });
    }

    @Override
    protected void initData(LayoutCarshopmoreBinding mBinding) {
        int state = 0;
        String type = "";
        if (title.equals("组合产品区")) {
            type = "zh";
        } else if (title.equals("自选产品区")) {
            type = "zx";
        } else if (title.equals("热门购买区")) {
            type = "rx";
            state = 2;
        }
        getrxDate(type, 0, state, myShop);
    }

    private void getrxDate(String type, final int i, int state, final ShopAdapter adapter) {
        NewWebAPI.getNewInstance().getWebRequest(
                "/carpool.aspx?call=getProductList_" + type + "&type=&ascOrDesc=desc&cid=&state=" + state + "&page=1&size=999",
                new WebRequestCallBack() {
                    @Override
                    public synchronized void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络错误，请重试！", context);
                            return;
                        }
//                        com.alibaba.fastjson.JSONObject json = JSON.parseObject(result.toString());
                        Gson gson = new Gson();
                        recommendbean bean = gson.fromJson(result.toString(), recommendbean.class);
                        if (200 != Integer.parseInt(bean.getCode())) {
                            Util.show(bean.getMessage(), context);
                            return;
                        }
                        if (bean.getList().size() == 0) {
                            return;
                        }
                        listBeans.addAll(bean.getList());

                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
//                        cpd.dismiss();

                    }

                    @Override
                    public void fail(Throwable e) {
                        super.fail(e);
                    }
                });

    }

    private void shopping(final recommendbean.ListBean bean) {


        if (null == UserData.getUser()) {
            Toast.makeText(context, "您还没登录哦，请前去登录", Toast.LENGTH_SHORT).show();
            Util.showIntent(context, LoginFrame.class, new String[]{"finish"}, new String[]{"finish"});
            return;
        }

        if (!UserData.getUser().getUserLevel().contains("城市经理") && !UserData.getUser().getUserLevel().contains("城市总监")
                || !UserData.getUser().getUserLevel().contains("联盟商家")
                ) {

            String str = "创客和商家可参与购物拼车";
            new MyPopWindow.MyBuilder(context, str, "立即申请成为创客/商家", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProxySiteFrame.class);
                    context.startActivity(intent);
                }
            }).setColor("#F13232")
                    .setisshowclose(true)
                    .build().showCenter();
            return;

        }
        Util.showIntent(
                context,
                ProductDeatilFream.class,
                new String[]{"url"},
                new String[]{bean.getPid()});



    }
}
