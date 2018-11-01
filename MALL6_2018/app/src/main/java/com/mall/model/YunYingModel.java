package com.mall.model;

import java.util.List;

public class YunYingModel {


    /**
     * code : 200
     * cache : 5132117
     * message : 14
     * list : [{"userid":"远大商城","name":"远大商城"},{"userid":"13822222227_p","name":"二七"},{"userid":"asdf001","name":"a001"},{"userid":"北京市","name":"北京市"},{"userid":"福建福州","name":"福建福州"},{"userid":"广东佛山","name":"广东佛山"},{"userid":"广东深圳","name":"广东深圳"},{"userid":"广东珠海","name":"广东珠海"},{"userid":"内蒙古呼伦贝尔","name":"内蒙古呼伦贝尔"},{"userid":"四川绵阳","name":"四川绵阳"},{"userid":"云南保山","name":"云南保山"},{"userid":"云南昆明","name":"云南昆明"},{"userid":"云南丽江","name":"云南丽江"},{"userid":"云南普洱","name":"云南普洱"},{"userid":"云南玉溪","name":"云南玉溪"}]
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
         * userid : 远大商城
         * name : 远大商城
         */

        private String userid;
        private String name;

        public ListBean(String userid, String name) {
            this.userid = userid;
            this.name = name;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
