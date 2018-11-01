package com.mall.view.WithdrawalBusiness;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.model.ReviewModle;
import com.mall.view.R;
import com.mall.view.SelectorFactory;
import com.mall.view.databinding.ItemApplicationBinding;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.List;

public class WithdrawalBusinessAdapter extends BaseRecycleAdapter<ReviewModle.ListBean> {
    WithdrawalBusinessApplicationActivity.Adaptercall MyAdaptercall;

    protected WithdrawalBusinessAdapter(Context context, List<ReviewModle.ListBean> list, WithdrawalBusinessApplicationActivity.Adaptercall adaptercall) {
        super(context, list);
        MyAdaptercall = adaptercall;
    }

    @Override
    public void setIteamData(ViewDataBinding mBinding, List<ReviewModle.ListBean> list, int position) {

        //提现金额 : ￥10000.00

        ReviewModle.ListBean listBean = list.get(position);
        Spanned price1str = Html.fromHtml("提现金额" + "<font color=\"#db2c2e\">￥" + listBean.getCashcost()
                + "</font>" + "元");
        Spanned price2str = Html.fromHtml("到账金额" + "<font color=\"#db2c2e\">￥" + listBean.getSj_money()
                + "</font>" + "元");
        ItemApplicationBinding itemApplicationBinding = (ItemApplicationBinding) mBinding;
        itemApplicationBinding.setAdapter(this);
        itemApplicationBinding.setPosition(position);
        Glide.with(mContext).load(listBean.getUserface())
//                .placeholder(R.drawable.community_image_head_rect)
                .error(R.drawable.community_image_head_rect).into(itemApplicationBinding.face);
        itemApplicationBinding.name.setText(listBean.getUserid());
        itemApplicationBinding.price1.setText(price1str);
        itemApplicationBinding.price2.setText(price2str);
        if (listBean.getType().equals("处理中")) {
            itemApplicationBinding.reviewTv.setText("审核");
        } else {
            itemApplicationBinding.reviewTv.setText(listBean.getType());
        }


    }

    @Override
    public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_application, parent, false);
        ScreenAdapterTools.getInstance().loadView((ViewGroup) view);
        ItemApplicationBinding itemApplicationBinding = DataBindingUtil.bind(view);
        itemApplicationBinding.reviewTv.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#49b0ee"))
                .setCornerRadius(40)
                .create());
        return itemApplicationBinding;
    }

    @Override
    public int setShowRule(int position) {
        return 0;
    }

    public void openMenu(int position) {
        final ReviewModle.ListBean listBean = getmList().get(position);
        if (listBean.getType().equals("处理中")) {
            MyAdaptercall.call(listBean);
        }


    }


}
