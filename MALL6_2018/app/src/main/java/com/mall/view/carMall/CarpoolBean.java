package com.mall.view.carMall;

import java.util.List;

/**
 * Created by Administrator on 2018/4/3.
 */

public class CarpoolBean {

    /**
     * code : 200
     * cache : 8261103
     * message : 1
     * list : [{"userId":"wg002","orderid":"234234234","type":"1","state":"1","orderno":"43453535345","cc_date":"","remark":"ydadmin修改状态，备注：测试修改状态为已出车","date":"2018-03-13"}]
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
         * userId : wg002
         * orderid : 234234234
         * type : 1
         * state : 1
         * orderno : 43453535345
         * cc_date :
         * remark : ydadmin修改状态，备注：测试修改状态为已出车
         * date : 2018-03-13
         */

        private String userId;
        private String orderid;
        private String type;
        private String state;
        private String orderno;
        private String cc_date;
        private String remark;
        private String date;

        public ListBean(String userId, String orderid, String type, String state, String orderno, String cc_date, String remark, String date) {
            this.userId = userId;
            this.orderid = orderid;
            this.type = type;
            this.state = state;
            this.orderno = orderno;
            this.cc_date = cc_date;
            this.remark = remark;
            this.date = date;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getOrderno() {
            return orderno;
        }

        public void setOrderno(String orderno) {
            this.orderno = orderno;
        }

        public String getCc_date() {
            return cc_date;
        }

        public void setCc_date(String cc_date) {
            this.cc_date = cc_date;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
