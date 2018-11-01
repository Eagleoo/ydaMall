package com.mall.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/9/29.
 */

public class RedPakageBean implements Serializable{


    /**
     * code : 200
     * cache : 38148078
     * message : 查询成功！
     * list : [{"id":"1","userid":"59","money":"100","state":"0","timeopen":"2017-11-12","onendate":"","remark":"备注"},{"id":"1","userid":"59","money":"100","state":"0","timeopen":"2017-11-12","onendate":"","remark":"备注"},{"id":"1","userid":"59","money":"100","state":"0","timeopen":"2017-11-12","onendate":"","remark":"备注"},{"id":"1","userid":"59","money":"100","state":"0","timeopen":"2017-11-12","onendate":"","remark":"备注"}]
     */

    private String code;
    private String cache;
    private String message;
    private String kqcount;
    private String kqmoney;

    public String getKqcount() {
        return kqcount;
    }

    public void setKqcount(String kqcount) {
        this.kqcount = kqcount;
    }

    public String getKqmoney() {
        return kqmoney;
    }

    public void setKqmoney(String kqmoney) {
        this.kqmoney = kqmoney;
    }

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

    public static class ListBean implements Serializable{
        /**
         * id : 1
         * userid : 59
         * money : 100
         * state : 0
         * timeopen : 2017-11-12
         * onendate :
         * remark : 备注
         */

        private String id;
        private String userid;
        private String money;
        private String state;
        private String timeopen;
        private String onendate;
        private String remark;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getTimeopen() {
            return timeopen;
        }

        public void setTimeopen(String timeopen) {
            this.timeopen = timeopen;
        }

        public String getOnendate() {
            return onendate;
        }

        public void setOnendate(String onendate) {
            this.onendate = onendate;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
