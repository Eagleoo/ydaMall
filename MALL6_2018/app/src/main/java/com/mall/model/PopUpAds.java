package com.mall.model;

import java.util.List;

/**
 * Created by Administrator on 2017/11/23.
 */

public class PopUpAds {

    /**
     * code : 200
     * cache : 11213197
     * message : 查询成功！
     * list : [{"id":"34","url_android":"s","url_iPhone":"wwww","cate":"2","image":"http://test.mall666.cn/user/Annex/130906569055263907.png"}]
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
         * id : 34
         * url_android : s
         * url_iPhone : wwww
         * cate : 2
         * image : http://test.mall666.cn/user/Annex/130906569055263907.png
         */

        private String id;
        private String url_android;
        private String url_iPhone;
        private String cate;
        private String image;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl_android() {
            return url_android;
        }

        public void setUrl_android(String url_android) {
            this.url_android = url_android;
        }

        public String getUrl_iPhone() {
            return url_iPhone;
        }

        public void setUrl_iPhone(String url_iPhone) {
            this.url_iPhone = url_iPhone;
        }

        public String getCate() {
            return cate;
        }

        public void setCate(String cate) {
            this.cate = cate;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
