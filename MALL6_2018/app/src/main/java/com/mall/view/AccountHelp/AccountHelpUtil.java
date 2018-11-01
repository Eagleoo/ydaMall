package com.mall.view.AccountHelp;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.util.HashMap;
import java.util.Map;

public class AccountHelpUtil {

    public static String bus; //业务账户
    public static String con;//消费券
    public static String sb;//商币账户
    public static String rai;
    public static String rec;//充值账户
    public static String pho;
    public static String lot;
    public static String zcb;
    public static String shop1;
    public static String shop2;
    public static String shop3;
    public static String red_s;//红包种子
    public static String red_c;//现金红包
    public static String cash_c;//现金券
    public static String shopping_voucher;//购物券
    public static String carpool_coupons;//拼车券

    public interface CallBackAccountYE {
        void getMoney(double account_money);

        void getMoneyStr(String account_money);
    }

    public static void getAccountYE(Context context, final String key, final CallBackAccountYE callBackAccountYE) {
        final CustomProgressDialog dialog = Util.showProgress("数据加载中...", context);
        dialog.show();
        User user = UserData.getUser();
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        map.put("type", "1,2,3,4,5,6,7,8,9,10,13,14,15,16,17,18");//,19

        NewWebAPI.getNewInstance().getWebRequest("/Money.aspx?call=getMoney",
                map, new NewWebAPIRequestCallback() {

                    @Override
                    public void success(Object result) {
                        // TODO Auto-generated method stub
                        if (null == result) {
                            Util.show("网络异常,请重试");
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show("网络异常,请重试");
                            return;
                        }
                        if (key == null) {
                            callBackAccountYE.getMoneyStr(result.toString());
                            return;
                        }

                        String number = json.getString(key);
                        double account_money = 0;
                        if (Util.isDouble(number)) {
                            account_money = Double.parseDouble(number);
                        }

                        Log.e("userKey", key + "账户,余额:" + account_money);

                        callBackAccountYE.getMoney(account_money);
                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {

                    }

                    @Override
                    public void requestEnd() {

                        dialog.dismiss();
                    }
                });
    }

}
