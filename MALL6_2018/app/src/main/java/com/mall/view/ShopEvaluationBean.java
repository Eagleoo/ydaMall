package com.mall.view;

import java.util.List;

/**
 * Created by Administrator on 2018/4/25.
 */

public class ShopEvaluationBean {


    /**
     * code : 200
     * cache : 23242250
     * message : 5
     * list : [{"id":"7798","userId":"测试1100","orderid":"ADS1806140400052677001","productid":"179723","list":[{"userFace":"http://test.yda360.cn/userface/27472379_97_97.jpg?_=131734207945802500","date":"2018/6/14 17:03:06","Product_comment_img":"","content":"vbnnnbnn"},{"userFace":"http://test.yda360.cn/userface/27472379_97_97.jpg?_=131734207945802500","date":"2018/6/14 16:45:26","Product_comment_img":"/mood/mood_android1528966020889321136003.jpg,/mood/mood_android1528966022385700852931.jpg,","content":"dsdasd"}]},{"id":"7764","userId":"测试1100","orderid":"ADS1803260448283601001","productid":"179723","list":[{"userFace":"http://test.yda360.cn/userface/27472379_97_97.jpg?_=131734207945802500","date":"2018/5/10 8:59:24","Product_comment_img":"/mood/mood_android1525913991902130296500.jpg,","content":"dssss😉"},{"userFace":"http://test.yda360.cn/userface/27472379_97_97.jpg?_=131734207945802500","date":"2018/5/9 17:20:24","Product_comment_img":"/mood/mood_android1525857650969672767971.jpg,/mood/mood_android15258576511121009230884.jpg,/mood/mood_android15258576512391034816686.jpg,/mood/mood_android15258576514031052515136.jpg,/mood/mood_android1525857651565564268781.jpg,/mood/mood_android1525857651768539837226.jpg,/mood/mood_android1525857651945115359172.jpg,/mood/mood_android1525857652208163260410.jpg,/mood/mood_android1525857652388177880868.jpg,","content":"卡罗拉"},{"userFace":"http://test.yda360.cn/userface/27472379_97_97.jpg?_=131734207945802500","date":"2018/5/9 15:56:29","Product_comment_img":"/mood/mood_android15258526144741069457105.jpg,","content":"咯莫"}]}]
     */

    private String code;
    private String cache;
    private String message;
    private List<ListBeanX> list;

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

    public List<ListBeanX> getList() {
        return list;
    }

    public void setList(List<ListBeanX> list) {
        this.list = list;
    }

    public static class ListBeanX {
        /**
         * id : 7798
         * userId : 测试1100
         * orderid : ADS1806140400052677001
         * productid : 179723
         * list : [{"userFace":"http://test.yda360.cn/userface/27472379_97_97.jpg?_=131734207945802500","date":"2018/6/14 17:03:06","Product_comment_img":"","content":"vbnnnbnn"},{"userFace":"http://test.yda360.cn/userface/27472379_97_97.jpg?_=131734207945802500","date":"2018/6/14 16:45:26","Product_comment_img":"/mood/mood_android1528966020889321136003.jpg,/mood/mood_android1528966022385700852931.jpg,","content":"dsdasd"}]
         */

        private String id;
        private String userId;
        private String orderid;
        private String productid;
        private List<ListBean> list;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getProductid() {
            return productid;
        }

        public void setProductid(String productid) {
            this.productid = productid;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * userFace : http://test.yda360.cn/userface/27472379_97_97.jpg?_=131734207945802500
             * date : 2018/6/14 17:03:06
             * Product_comment_img :
             * content : vbnnnbnn
             */

            private String userFace;
            private String date;
            private String Product_comment_img;
            private String content;

            public String getUserFace() {
                return userFace;
            }

            public void setUserFace(String userFace) {
                this.userFace = userFace;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getProduct_comment_img() {
                return Product_comment_img;
            }

            public void setProduct_comment_img(String Product_comment_img) {
                this.Product_comment_img = Product_comment_img;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}
