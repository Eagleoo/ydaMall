package com.mall.model;

/**
 * Created by Administrator on 2017/10/10.
 */

public class openRedBean {

    /**
     * code : 200
     * cache : 49461350
     * message :  领取成功
     * CashCoupon :  41.42
     * consumption :  10.36
     */

    private String code;
    private String cache;
    private String message;
    private String CashCoupon;
    private String consumption;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCashCoupon() {
        return CashCoupon;
    }

    public void setCashCoupon(String CashCoupon) {
        this.CashCoupon = CashCoupon;
    }

    public String getConsumption() {
        return consumption;
    }

    public void setConsumption(String consumption) {
        this.consumption = consumption;
    }
}
