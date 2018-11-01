package com.mall.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mall.model.SharingredMoneyInfo;
import com.mall.view.R;

import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */

public class SharingAdapter extends BaseQuickAdapter<SharingredMoneyInfo.ListBean,BaseViewHolder> {
    public SharingAdapter(@LayoutRes int layoutResId, @Nullable List<SharingredMoneyInfo.ListBean> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, SharingredMoneyInfo.ListBean item) {
        helper.setText(R.id.dates,item.getDate().split(" ")[0]);
        //2017-11-10 17:45:40
        helper.setText(R.id.money,item.getMoney());
    }
}