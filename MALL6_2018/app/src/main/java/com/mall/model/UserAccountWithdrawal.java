package com.mall.model;

import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */

public class UserAccountWithdrawal {

    /**
     * code : 200
     * cache : 961467
     * message : 查询成功！
     * list : [{"wx":"1","alipay":"0","bank":"1"}]
     */

    private String code;
    private String cache;
    private String message;
    private List<ListBean> list;

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

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * wx : 1
         * alipay : 0
         * bank : 1
         */

        private String wx;
        private String alipay;
        private String bank;
        private String qq;

        public String getQq() {
            return qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public String getWx() {
            return wx;
        }

        public void setWx(String wx) {
            this.wx = wx;
        }

        public String getAlipay() {
            return alipay;
        }

        public void setAlipay(String alipay) {
            this.alipay = alipay;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }
    }
}
