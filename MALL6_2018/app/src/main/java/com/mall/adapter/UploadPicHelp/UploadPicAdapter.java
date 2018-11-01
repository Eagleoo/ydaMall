package com.mall.adapter.UploadPicHelp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.view.R;
import com.mall.view.databinding.LayoutImageitemBinding;

import java.util.List;

/**
 * Created by Administrator on 2018/4/25.
 */

public class UploadPicAdapter extends BaseRecycleAdapter<String> {
    private int mDefaultic;

    public UploadPicAdapter(Context context, List<String> list, int defaultic) {
        super(context, list);
        mDefaultic = defaultic;
    }


    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    @Override
    public void setIteamData(ViewDataBinding mBinding, List<String> list, int position) {
        LayoutImageitemBinding imageitemBinding = (LayoutImageitemBinding) mBinding;
        if (position == 0) {
            imageitemBinding.itemImage.setImageResource(mDefaultic);
        } else {
            if (list.size() > 0) {
                Glide.with(mContext).load(list.get(position - 1)).into(imageitemBinding.itemImage);
            }


        }
    }


    @Override
    public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
        return DataBindingUtil.inflate(mInflater, R.layout.layout_imageitem, parent, false);
    }

    @Override
    public int setShowRule(int position) {
        return 0;
    }
}
