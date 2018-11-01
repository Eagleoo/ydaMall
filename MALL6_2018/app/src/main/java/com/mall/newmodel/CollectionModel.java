package com.mall.newmodel;

import java.util.List;

/**
 * Created by Administrator on 2018/3/20.
 */

public class CollectionModel {


    /**
     * code : 200
     * cache : 28721033
     * message : 查询成功！
     * list : [{"userId":"测试1100","id":"10633","productid":"416","date":"2018/3/20 14:44:52","ptype":"1","SHOPCPHONE":"11111111111","PRODUCTPHOTO":"http://img2.yda360.com/ ,","PRODUCTNAME":"小佳"}]
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
         * id : 10633
         * productid : 416
         * date : 2018/3/20 14:44:52
         * ptype : 1
         * SHOPCPHONE : 11111111111
         * PRODUCTPHOTO : http://img2.yda360.com/ ,
         * PRODUCTNAME : 小佳
         */

        private String userId;
        private String id;
        private String productid;
        private String date;
        private String ptype;
        private String SHOPCPHONE;
        private String PRODUCTPHOTO;
        private String PRODUCTNAME;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProductid() {
            return productid;
        }

        public void setProductid(String productid) {
            this.productid = productid;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPtype() {
            return ptype;
        }

        public void setPtype(String ptype) {
            this.ptype = ptype;
        }

        public String getSHOPCPHONE() {
            return SHOPCPHONE;
        }

        public void setSHOPCPHONE(String SHOPCPHONE) {
            this.SHOPCPHONE = SHOPCPHONE;
        }

        public String getPRODUCTPHOTO() {
            return PRODUCTPHOTO;
        }

        public void setPRODUCTPHOTO(String PRODUCTPHOTO) {
            this.PRODUCTPHOTO = PRODUCTPHOTO;
        }

        public String getPRODUCTNAME() {
            return PRODUCTNAME;
        }

        public void setPRODUCTNAME(String PRODUCTNAME) {
            this.PRODUCTNAME = PRODUCTNAME;
        }
    }
}
