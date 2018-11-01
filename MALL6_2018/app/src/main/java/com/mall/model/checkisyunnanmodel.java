package com.mall.model;

import java.util.List;

/**
 * Created by Administrator on 2017/12/7.
 */

public class checkisyunnanmodel {


    /**
     * code : 200
     * cache : 35567111
     * message : 查询成功！
     * list : [{"userid":"ynan21","zoneid":"2676","START_DATE":"","INVITER_ZONEID":"2671","SJ_DATE":""}]
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
         * userid : ynan21
         * zoneid : 2676
         * START_DATE :
         * INVITER_ZONEID : 2671
         * SJ_DATE :
         */

        private String userid;
        private String zoneid;
        private String START_DATE;
        private String INVITER_ZONEID;
        private String SJ_DATE;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getZoneid() {
            return zoneid;
        }

        public void setZoneid(String zoneid) {
            this.zoneid = zoneid;
        }

        public String getSTART_DATE() {
            return START_DATE;
        }

        public void setSTART_DATE(String START_DATE) {
            this.START_DATE = START_DATE;
        }

        public String getINVITER_ZONEID() {
            return INVITER_ZONEID;
        }

        public void setINVITER_ZONEID(String INVITER_ZONEID) {
            this.INVITER_ZONEID = INVITER_ZONEID;
        }

        public String getSJ_DATE() {
            return SJ_DATE;
        }

        public void setSJ_DATE(String SJ_DATE) {
            this.SJ_DATE = SJ_DATE;
        }
    }
}
