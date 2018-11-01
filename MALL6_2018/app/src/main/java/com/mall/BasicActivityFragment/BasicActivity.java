package com.mall.BasicActivityFragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mall.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import cn.finalteam.rxgalleryfinal.BuildConfig;

/**
 * Created by Administrator on 2017/11/1.
 */

public abstract  class BasicActivity extends AppCompatActivity {

    public Context context;


    public static final String EXTRA_PREFIX = BuildConfig.APPLICATION_ID;
    public static final String EXTRA_CONFIGURATION = EXTRA_PREFIX + ".Configuration";

    private final String CLASS_NAME = getClass().getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
              ButterKnife.bind(this);
        context=this;

//注册事件
        EventBus.getDefault().register(this);
        initAllMembersView(savedInstanceState);
    }



    public abstract int getContentViewId();
    public abstract void initAllMembersView(Bundle savedInstanceState);

    public abstract void EventCallBack(MessageEvent messageEvent);

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(MessageEvent messageEvent){
        Log.e("EventBus"+((Activity)context).getClass(), "回调" + messageEvent.toString());
        EventCallBack(messageEvent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(this);
    }

}
