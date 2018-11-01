package com.mall.model;

import java.util.List;

/**
 * Created by Administrator on 2017/12/1.
 */

public class AccountType {


    /**
     * code : 200
     * cache : 14560759
     * message : 查询成功！
     * list : [{"type_id":"1","type_name":"开通账户"},{"type_id":"12","type_name":"提现"},{"type_id":"40","type_name":"用户转账"},{"type_id":"3002","type_name":"招商业务"}]
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
        public ListBean(String type_id, String type_name) {
            this.type_id = type_id;
            this.type_name = type_name;
        }

        /**
         * type_id : 1
         * type_name : 开通账户
         */


        private String type_id;
        private String type_name;

        public String getType_id() {
            return type_id;
        }

        public void setType_id(String type_id) {
            this.type_id = type_id;
        }

        public String getType_name() {
            return type_name;
        }

        public void setType_name(String type_name) {
            this.type_name = type_name;
        }
    }
}
