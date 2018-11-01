package com.mall.view.carMall;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/4/3.
 */

public class XibaoBean implements Serializable {


    /**
     * code : 200
     * cache : 29944200
     * message : 2
     * list : [{"id":"2","userId":"wg002","orderid":"868768575675","title":"附件ask累计发卡量时间啊","area":"2136","img1":"/rebate/images/02477640-240a-473c-beff-fe321bca9772.jpg","img2":" ,","img3":" ,","car_name":"奔驰","car_money":"777777.0000","cc_date":"2018-04-02","info":"&lt;p&gt;发放给&lt;/p&gt;","add_date":"2018-04-03"},{"id":"1","userId":"wg002","orderid":"3534534535353","title":"","area":"2136","img1":"/rebate/images/8c1a51f5-b87b-46e3-bc33-410ea447448c.jpg","img2":"","img3":"","car_name":"阿斯顿马丁","car_money":"888888888.0000","cc_date":"2018-04-04","info":"&lt;p&gt;式发爱的发的发&nbsp;啊反倒是啊打发&lt;br/&gt;&lt;/p&gt;","add_date":"2018-04-03"}]
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

    public static class ListBean implements Serializable {
        /**
         * id : 2
         * userId : wg002
         * orderid : 868768575675
         * title : 附件ask累计发卡量时间啊
         * area : 2136
         * img1 : /rebate/images/02477640-240a-473c-beff-fe321bca9772.jpg
         * img2 :  ,
         * img3 :  ,
         * car_name : 奔驰
         * car_money : 777777.0000
         * cc_date : 2018-04-02
         * tc_date : 2018-04-02
         * info : &lt;p&gt;发放给&lt;/p&gt;
         * add_date : 2018-04-03
         */

        private String id;
        private String userId;
        private String orderid;
        private String title;
        private String area;
        private String img1;
        private String img2;
        private String img3;
        private String car_name;
        private String car_money;
        private String cc_date;
        private String tc_date;
        private String info;
        private String add_date;

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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getImg1() {
            return img1;
        }

        public void setImg1(String img1) {
            this.img1 = img1;
        }

        public String getImg2() {
            return img2;
        }

        public void setImg2(String img2) {
            this.img2 = img2;
        }

        public String getImg3() {
            return img3;
        }

        public void setImg3(String img3) {
            this.img3 = img3;
        }

        public String getCar_name() {
            return car_name;
        }

        public void setCar_name(String car_name) {
            this.car_name = car_name;
        }

        public String getCar_money() {
            return car_money;
        }

        public void setCar_money(String car_money) {
            this.car_money = car_money;
        }

        public String getTc_date() {
            return tc_date;
        }

        public void setTc_date(String tc_date) {
            this.tc_date = tc_date;
        }

        public String getCc_date() {
            return cc_date;
        }

        public void setCc_date(String cc_date) {
            this.cc_date = cc_date;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getAdd_date() {
            return add_date;
        }

        public void setAdd_date(String add_date) {
            this.add_date = add_date;
        }
    }
}
