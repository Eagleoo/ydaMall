package com.mall.model;

import java.util.List;

/**
 * Created by Administrator on 2018/3/14.
 */

public class ExpireBean {


    /**
     * code : 200
     * cache : 44990379
     * message : 查询成功！
     * list : [{"userId":"测试1100","DAYS":"90","DAY_MONEY":"0.0000","GQ_MONEY":"0.0000","SR_MONEY":"0.0000","XF_MONEY":"0.0000"}]
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
         * userId : 测试1100
         * DAYS : 90
         * DAY_MONEY : 0.0000
         * GQ_MONEY : 0.0000
         * SR_MONEY : 0.0000
         * XF_MONEY : 0.0000
         */

        private String userId;
        private String DAYS;
        private String DAY_MONEY;
        private String GQ_MONEY;
        private String SR_MONEY;
        private String XF_MONEY;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getDAYS() {
            return DAYS;
        }

        public void setDAYS(String DAYS) {
            this.DAYS = DAYS;
        }

        public String getDAY_MONEY() {
            return DAY_MONEY;
        }

        public void setDAY_MONEY(String DAY_MONEY) {
            this.DAY_MONEY = DAY_MONEY;
        }

        public String getGQ_MONEY() {
            return GQ_MONEY;
        }

        public void setGQ_MONEY(String GQ_MONEY) {
            this.GQ_MONEY = GQ_MONEY;
        }

        public String getSR_MONEY() {
            return SR_MONEY;
        }

        public void setSR_MONEY(String SR_MONEY) {
            this.SR_MONEY = SR_MONEY;
        }

        public String getXF_MONEY() {
            return XF_MONEY;
        }

        public void setXF_MONEY(String XF_MONEY) {
            this.XF_MONEY = XF_MONEY;
        }
    }
}
