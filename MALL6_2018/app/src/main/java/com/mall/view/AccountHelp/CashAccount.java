package com.mall.view.AccountHelp;

import java.util.HashMap;

public class CashAccount {


    private static HashMap<String, Integer> cashAccountMap;

    private static HashMap<String, Integer> getCashAccountMap() {
        if (cashAccountMap == null) {
            cashAccountMap = new HashMap();
            cashAccountMap.put("bus", 1);
            cashAccountMap.put("xj", 2);
            cashAccountMap.put("cash_c", 3);
            cashAccountMap.put("Present_account", 4);
        }
        return cashAccountMap;
    }

    public static int accountType(String key) {
        return getCashAccountMap().get(key);
    }

//    public static final String bus = "bus";
//    public static final String xj = "xj";
//    public static final String cash_c = "cash_c";
//    public static final String Present_account = "Present_account";
//    public static final String con = 0;
//    public static final String sb = 0;
//    public static final String rai = 0;
//    public static final String rec = 0;
//    public static final String pho = 0;
//    public static final String lot = 0;
//    public static final String zcb = 0;
//    public static final String shop1 = 0;
//    public static final String shop2 = 0;
//    public static final String shop3 = 0;
//    public static final String red_s = 0;
//    public static final String red_c = 0;
//
//    public static final String shopping_voucher = 0;
//    public static final String carpool_coupons = 0;


}
