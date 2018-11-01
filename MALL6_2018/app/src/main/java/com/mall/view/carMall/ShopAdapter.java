package com.mall.view.carMall;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.model.recommendbean;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.ProductDeatilFream;
import com.mall.view.ProxySiteFrame;
import com.mall.view.R;
import com.mall.view.databinding.CommodityitemBinding;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.List;

/**
 * Created by Administrator on 2018/3/30.
 */

public class ShopAdapter extends BaseRecycleAdapter<recommendbean.ListBean> {


    protected ShopAdapter(Context context, List<recommendbean.ListBean> list) {
        super(context, list);
    }

    @Override
    public void setIteamData(ViewDataBinding mBinding, List<recommendbean.ListBean> list, int position) {
        final recommendbean.ListBean bean = list.get(position);
        CommodityitemBinding binding = (CommodityitemBinding) mBinding;
        binding.name.setText(bean.getName());
        binding.price.setText("￥" + bean.getPrice());
        if (!Util.isNull(bean.getShorttitle())){
            binding.style.setText("(" + bean.getShorttitle() + ")");
        }

        Glide.with(mContext).load(bean.getThumb()).into(binding.iv);
    }

    @Override
    public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.commodityitem, parent, false);
        ScreenAdapterTools.getInstance().loadView((ViewGroup) view);
        return DataBindingUtil.bind(view);
    }

    @Override
    public int setShowRule(int position) {
        return 0;
    }

    private void shopping(final recommendbean.ListBean bean) {


        if (null == UserData.getUser()) {
            Toast.makeText(mContext, "您还没登录哦，请前去登录", Toast.LENGTH_SHORT).show();
            Util.showIntent(mContext, LoginFrame.class, new String[]{"finish"}, new String[]{"finish"});
            return;
        }

        if (!UserData.getUser().getUserLevel().contains("城市经理")
                && !UserData.getUser().getUserLevel().contains("城市总监")
                && !UserData.getUser().getUserLevel().contains("联盟商家")
                ) {

            String str = "创客和商家可参与购物拼车";
            new MyPopWindow.MyBuilder(mContext, str, "立即申请成为创客/商家", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ProxySiteFrame.class);
                    mContext.startActivity(intent);
                }
            }).setColor("#F13232")
                    .setisshowclose(true)
                    .build().showCenter();
            return;

        }
        Util.showIntent(
                mContext,
                ProductDeatilFream.class,
                new String[]{"url"},
                new String[]{bean.getPid()});


    }
}
