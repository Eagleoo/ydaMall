package com.mall.BasicActivityFragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/11/1.
 */

public abstract class BaseV4Fragment extends Fragment {

    private View mRootView;

    private Unbinder unbinder;

    public Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context=getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootView=inflater.inflate(getContentViewId(),container,false);
        unbinder =   ButterKnife.bind(this,mRootView);//绑定framgent
        initAllMembersView(savedInstanceState);
        return mRootView;
    }
    public abstract int getContentViewId();
    public abstract void initAllMembersView(Bundle savedInstanceState);
    public abstract  void  onDes();
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();//解绑
        onDes();

    }
}
