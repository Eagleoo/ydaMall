package com.mall.model;

import java.util.List;

/**
 * Created by Administrator on 2017/9/29.
 */

public class RedAccountBean {


    /**
     * code : 200
     * cache : 58523817
     * message : 4
     * list : [{"lineNum":"4","type":"5","income":"10.00","detail":"","date":"2017/9/29 10:27:58","comments":"test01","balance":"30.00"},{"lineNum":"3","type":"4","income":"10.00","detail":"","date":"2017/9/29 10:27:58","comments":"test01","balance":"30.00"},{"lineNum":"2","type":"充值升级VIP","income":"10.00","detail":"","date":"2017/9/29 10:27:58","comments":"","balance":"30.00"},{"lineNum":"1","type":"充值升级VIP","income":"10.00","detail":"","date":"2017/9/29 10:27:58","comments":"test01","balance":"30.00"}]
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
         * lineNum : 4
         * type : 5
         * income : 10.00
         * detail :
         * date : 2017/9/29 10:27:58
         * comments : test01
         * balance : 30.00
         */

        private String lineNum;
        private String type;
        private String income;
        private String detail;
        private String date;
        private String comment;
        private String balance;
        private String orderid;
        private String cashcost;

        public String getTx_type() {
            return tx_type;
        }

        public void setTx_type(String tx_type) {
            this.tx_type = tx_type;
        }

        private String tx_type;

        public String getCashcost() {
            return cashcost;
        }

        public void setCashcost(String cashcost) {
            this.cashcost = cashcost;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getLineNum() {
            return lineNum;
        }

        public void setLineNum(String lineNum) {
            this.lineNum = lineNum;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getComments() {
            return comment;
        }

        public void setComments(String comments) {
            this.comment = comments;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }
    }
}
