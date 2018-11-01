package com.Base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.github.florent37.fiftyshadesof.FiftyShadesOf;
import com.mall.util.Util;
import com.mall.view.R;

/**
 * Created by Administrator on 2018/3/2.
 */

public abstract class BaseActivity<VB extends ViewDataBinding> extends AppCompatActivity {

    public VB mBinding;

    public Context context;

//    private HomeWatcherReceiver mHomeWatcherReceiver = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        mBinding = DataBindingUtil.setContentView(this, getContentViewId());
//        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        initView(mBinding);
        if (checknet(getattachedview())) {

            return;
        }

        initData(mBinding);
        //注册事件

    }

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver();
    }

    public abstract int getContentViewId();

    public abstract View getattachedview();

    protected abstract void initView(VB mBinding);
    protected abstract void initData(VB mBinding);


//    private void registerReceiver() {
//        mHomeWatcherReceiver = new HomeWatcherReceiver();
//        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        registerReceiver(mHomeWatcherReceiver, filter);
//    }


    @Override
    protected void onStop() {
        super.onStop();
//        if (mHomeWatcherReceiver != null) {
//            try {
//                unregisterReceiver(mHomeWatcherReceiver);
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

    }

    private boolean checknet(View attachedview) {
        Log.e("网络监测", "Util.getNetworkType(context)" + Util.getNetworkType(context));
        if (Util.getNetworkType(context) == 0) {
            if (attachedview != null) {
                FiftyShadesOf fiftyShadesOf = FiftyShadesOf.with(context)
                        .root(attachedview).start();
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.layout_nowifi, null);
                fiftyShadesOf.failure(view);
            }

            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
