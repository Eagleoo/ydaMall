package com.mall.view.carMall;

import java.util.List;

/**
 * Created by Administrator on 2018/4/16.
 */

public class CarInfoBean {

    /**
     * code : 200
     * cache : 24876361
     * message : 1
     * list : [{"userid":"test01","IS_CZ":"0","IS_PC":"0","WITE_DAY":"0","IS_PC_ORDER":"0"}]
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
         * userid : test01
         * IS_CZ : 0
         * IS_PC : 0
         * WITE_DAY : 0
         * IS_PC_ORDER : 0
         */

        private String userid;
        private String IS_CZ;
        private String IS_PC;
        private String WITE_DAY;
        private String IS_PC_ORDER;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getIS_CZ() {
            return IS_CZ;
        }

        public void setIS_CZ(String IS_CZ) {
            this.IS_CZ = IS_CZ;
        }

        public String getIS_PC() {
            return IS_PC;
        }

        public void setIS_PC(String IS_PC) {
            this.IS_PC = IS_PC;
        }

        public String getWITE_DAY() {
            return WITE_DAY;
        }

        public void setWITE_DAY(String WITE_DAY) {
            this.WITE_DAY = WITE_DAY;
        }

        public String getIS_PC_ORDER() {
            return IS_PC_ORDER;
        }

        public void setIS_PC_ORDER(String IS_PC_ORDER) {
            this.IS_PC_ORDER = IS_PC_ORDER;
        }
    }
}
