package com.mall.view.carMall;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mall.adapter.BaseRecycleAdapter;
import com.mall.view.R;
import com.mall.view.databinding.ItemCardmodeBinding;
import com.mall.view.databinding.PopwindtextBinding;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.List;

/**
 * Created by Administrator on 2018/4/13.
 */

public class CarModeAdapter extends BaseRecycleAdapter<CarModeBean> {
    private int type = 0;

    protected CarModeAdapter(Context context, List<CarModeBean> list, int type) {
        super(context, list);
        this.type = type;
    }

    @Override
    public void setIteamData(ViewDataBinding mBinding, List<CarModeBean> list, int position) {
        CarModeBean carModeBean = list.get(position);
        if (mBinding instanceof ItemCardmodeBinding) {
            ItemCardmodeBinding binding = (ItemCardmodeBinding) mBinding;
//            binding.setItem(carModeBean);
            binding.setPosition(position);
            binding.setAdapter(this);

        } else {
            PopwindtextBinding binding = (PopwindtextBinding) mBinding;
//            binding.setItem(carModeBean);
            binding.setPosition(position);
            binding.setAdapter(this);
        }

    }

    @Override
    public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
        View view;
        if (type == 0) {
            view = mInflater.inflate(R.layout.item_cardmode, parent, false);
        } else {
            view = mInflater.inflate(R.layout.popwindtext, parent, false);
        }

        ScreenAdapterTools.getInstance().loadView((ViewGroup) view);
        return DataBindingUtil.bind(view);
    }

    @Override
    public int setShowRule(int position) {
        return 0;
    }

    public void onItemClick(View view, int position) {

        SelectCarNormActivity selectCarNormActivity = (SelectCarNormActivity) mContext;

        Log.e("执行1", "position" + position);

        for (int i = 0; i < getmList().size(); i++) {

            getmList().get(i).setSelect(false);
            if (position == i) {
                Log.e("执行2", "position" + position);
                getmList().get(i).setSelect(true);
                selectCarNormActivity.setSelectdr(getmList().get(position).getItemStr());
            }
        }
        notifyDataSetChanged();
    }

}
