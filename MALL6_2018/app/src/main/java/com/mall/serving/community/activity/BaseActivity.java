package com.mall.serving.community.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

public class BaseActivity extends Activity {
    public Activity context;
    public InternalReceiver internalReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

    }

    public void registerReceiverAtBase(String[] actionArray) {
        IntentFilter intentfilter = new IntentFilter();

        for (String action : actionArray) {
            intentfilter.addAction(action);
        }

        if (internalReceiver == null) {
            internalReceiver = new InternalReceiver();
        }
        context.registerReceiver(internalReceiver, intentfilter);

    }

    public void onReceiveBroadcast(Intent intent) {

    }

    class InternalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null || TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            onReceiveBroadcast(intent);
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (internalReceiver != null) {
            context.unregisterReceiver(internalReceiver);
        }

    }
}
