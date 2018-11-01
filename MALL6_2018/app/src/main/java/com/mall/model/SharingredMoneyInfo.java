package com.mall.model;

import java.util.List;

/**
 * Created by Administrator on 2017/11/10.
 */

public class SharingredMoneyInfo {


    /**
     * code : 200
     * cache : 27503167
     * message : 5
     * list : [{"id":"6","state":"0","userid":"test01","redbox_ly":"hkwdf","money":"5.90","date":"2017-11-10 15:15:54","opendate":""},{"id":"5","state":"0","userid":"test01","redbox_ly":"sjlu","money":"0.97","date":"2017-11-10 13:55:26","opendate":""},{"id":"4","state":"0","userid":"test01","redbox_ly":"ghhn123","money":"9.48","date":"2017-11-10 13:46:40","opendate":""},{"id":"2","state":"0","userid":"test01","redbox_ly":"gjjj555","money":"9.74","date":"2017-11-10 12:30:46","opendate":""},{"id":"1","state":"0","userid":"test01","redbox_ly":"ghjj","money":"2.46","date":"2017-11-10 12:24:11","opendate":""}]
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
         * id : 6
         * state : 0
         * userid : test01
         * redbox_ly : hkwdf
         * money : 5.90
         * date : 2017-11-10 15:15:54
         * opendate :
         */

        private String id;
        private String state;
        private String userid;
        private String redbox_ly;
        private String money;
        private String date;
        private String opendate;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getRedbox_ly() {
            return redbox_ly;
        }

        public void setRedbox_ly(String redbox_ly) {
            this.redbox_ly = redbox_ly;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getOpendate() {
            return opendate;
        }

        public void setOpendate(String opendate) {
            this.opendate = opendate;
        }
    }
}
