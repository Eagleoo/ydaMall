package com.mall.view.carMall;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mall.adapter.BaseRecycleAdapter;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.databinding.ItemtextBinding;

import java.util.List;

/**
 * Created by Administrator on 2018/4/3.
 */

class Caradpter extends BaseRecycleAdapter<CarpoolBean.ListBean> {

    private int gravity;

    protected Caradpter(Context context, List<CarpoolBean.ListBean> list, int gravity) {
        super(context, list);
        this.gravity = gravity;
    }

    @Override
    public void setIteamData(ViewDataBinding mBinding, List<CarpoolBean.ListBean> list, int position) {
        ItemtextBinding itemtextBinding = (ItemtextBinding) mBinding;
        CarpoolBean.ListBean listBean = list.get(position);
        itemtextBinding.tv.setText(Html.fromHtml("▪ 恭喜创客<font color=\"#F00314\">" + Util.protectionUserName(listBean.getUserId()) + "</font>获得<font color=\"#F00314\">" + listBean.getType() + "万</font>档出车资格"));
//        itemtextBinding.tv.setText(Html.fromHtml("恭喜创客<font color=\"#F00314\">" + "18583342684_p" + "</font>获得<font color=\"#F00314\">" + listBean.getType() + "万</font>档提车资格"));
        itemtextBinding.tv.setPadding(0, 0, 0, 18);
        itemtextBinding.tv.setGravity(Gravity.LEFT);
        if (gravity == 3) {
            itemtextBinding.tv.setPadding(18, 0, 0, 10);
            itemtextBinding.tv.setText(listBean.getRemark());
        }


    }

    @Override
    public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
        return DataBindingUtil.inflate(mInflater, R.layout.itemtext, parent, false);
    }

    @Override
    public int setShowRule(int position) {
        return 0;
    }
}