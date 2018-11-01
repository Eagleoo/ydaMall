package com.mall.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mall.util.MyLog;
import com.mall.util.NetMessageEvent;
import com.mall.util.Util;

import org.greenrobot.eventbus.EventBus;

/**
 * 网络环境监听
 */

public class NetBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        int netWorkState = Util.getNetworkType(context);
        MyLog.e(Util.getNetType(netWorkState));
        Boolean isCheckedWifi =
                context.getSharedPreferences("isCheckedWifi", context.MODE_PRIVATE)
                        .getBoolean("isCheckedWifi", false);
        Log.e("网络环境广播接收",
                "网络coad"+netWorkState+"\n"+
                "网络环境"+Util.getNetType(netWorkState)+"\n"+"是否允许wifi自动更新"+isCheckedWifi);
        if (netWorkState==10&&isCheckedWifi){
        }

        EventBus.getDefault().post(new NetMessageEvent(netWorkState));



    }



}
