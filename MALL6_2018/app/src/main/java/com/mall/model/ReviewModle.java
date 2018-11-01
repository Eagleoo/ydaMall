package com.mall.model;

import java.util.List;

public class ReviewModle {


    /**
     * code : 200
     * cache : 28341455
     * message : 4
     * list : [{"CASHAPPLYID":"30477","userid":"测试1100","userface":"http://test.yda360.cn//userface/27472379_97_97.jpg?_=131642711126547500","type":"处理中","comment":"提现时间为5个工作日，请耐心等待","cashcost":"1000.00","sj_money":"920.00","date":"2018/5/29 21:46:32","tx_type":"微信"},{"CASHAPPLYID":"30476","userid":"测试1100","userface":"http://test.yda360.cn//userface/27472379_97_97.jpg?_=131642711126547500","type":"处理中","comment":"提现时间为5个工作日，请耐心等待","cashcost":"1000.00","sj_money":"920.00","date":"2018/5/29 21:41:53","tx_type":"微信"},{"CASHAPPLYID":"30475","userid":"测试1100","userface":"http://test.yda360.cn//userface/27472379_97_97.jpg?_=131642711126547500","type":"处理中","comment":"提现时间为5个工作日，请耐心等待","cashcost":"1000.00","sj_money":"920.00","date":"2018/5/29 21:41:46","tx_type":"微信"},{"CASHAPPLYID":"30474","userid":"测试1100","userface":"http://test.yda360.cn//userface/27472379_97_97.jpg?_=131642711126547500","type":"处理中","comment":"提现时间为5个工作日，请耐心等待","cashcost":"1000.00","sj_money":"920.00","date":"2018/5/29 21:39:26","tx_type":"微信"}]
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
         * CASHAPPLYID : 30477
         * userid : 测试1100
         * userface : http://test.yda360.cn//userface/27472379_97_97.jpg?_=131642711126547500
         * type : 处理中
         * comment : 提现时间为5个工作日，请耐心等待
         * cashcost : 1000.00
         * sj_money : 920.00
         * date : 2018/5/29 21:46:32
         * tx_type : 微信
         */

        private String CASHAPPLYID;
        private String userid;
        private String userface;
        private String type;
        private String comment;
        private String cashcost;
        private String sj_money;
        private String dz_money;
        private String date;
        private String tx_type;

        public String getDz_money() {
            return dz_money;
        }

        public void setDz_money(String dz_money) {
            this.dz_money = dz_money;
        }

        public String getCASHAPPLYID() {
            return CASHAPPLYID;
        }

        public void setCASHAPPLYID(String CASHAPPLYID) {
            this.CASHAPPLYID = CASHAPPLYID;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUserface() {
            return userface;
        }

        public void setUserface(String userface) {
            this.userface = userface;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getCashcost() {
            return cashcost;
        }

        public void setCashcost(String cashcost) {
            this.cashcost = cashcost;
        }

        public String getSj_money() {
            return sj_money;
        }

        public void setSj_money(String sj_money) {
            this.sj_money = sj_money;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTx_type() {
            return tx_type;
        }

        public void setTx_type(String tx_type) {
            this.tx_type = tx_type;
        }
    }
}
