package com.mall.net;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.mall.view.R;

/**
 * Created by Administrator on 2018/2/23.
 */

public class LoadingFramelayout extends FrameLayout {
    private RelativeLayout mLoadingView;
    private AnimationDrawable mAnimationDrawable;
    private OnReloadListener mOnReloadListener;
    //构造方法，在我们需要网络加载页面是调用，看清楚，第二个传入的一个布局
    public LoadingFramelayout(Context context, @LayoutRes int res) {
        super(context);
        LayoutInflater mInflater = LayoutInflater.from(context);
        mInflater.inflate(res,this);
        View rootView = mInflater.inflate(R.layout.fragment_loading_framelayout,this);
        mLoadingView = (RelativeLayout) rootView.findViewById(R.id.load_view);
    }
    // 构造方法，在我们需要网络加载页面是调用，看清楚，第二个传入的一个View
    public LoadingFramelayout(Context context,View view) {
        super(context);
        addView(view);
        LayoutInflater mInflater = LayoutInflater.from(context);
        View rootView = mInflater.inflate(R.layout.fragment_loading_framelayout,this);
        mLoadingView = (RelativeLayout) rootView.findViewById(R.id.load_view);
    }
    //这里是一个动画效果，我们这里采用6张不同加载时候的图片

    //这一个用于完成加载的调用方法
    public void completeLoading(){
        mLoadingView.setVisibility(GONE);
    }
    //成功和失败后停着加载，将动画stop
    private void stopLoading() {
    }

    //失败
    public void failureLoading() {
        mLoadingView.setVisibility(VISIBLE);
    }

    //在重新加载的时候用来回调重新加载的方法
    public void setOnReloadListener(OnReloadListener onReloadListener) {
        mOnReloadListener = onReloadListener;
    }
    //实现接口，重新加载用作回调
    public interface OnReloadListener {
        void onReload();
    }
}


