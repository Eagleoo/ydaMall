package com.mall.view.carMall;

import java.util.List;

/**
 * Created by Administrator on 2018/4/8.
 */

public class OrderBeanAll {


    /**
     * code : 200
     * cache : 44489869
     * message : 查询成功！
     * order : [{"orderId":"AD1804080306063277","integral":"1800.00","date":"2018-04-08 15:06:06","payType":"未付款","orderStatus":"1","ordertype":"7","cost":"4000.00","secondOrder":[{"secondOrderId":"ADS1804080306063277001","postNum":"","postCpy":"","postPhone":"","colorAndSize":"","unitCost":"4000.00","quantity":"1","secondOrderStatus":"1","productName":"测试测试","productId":"180033","productThumb":"http://img.yda360.com/"}]}]
     */

    private String code;
    private String cache;
    private String message;
    private List<OrderBean> order;

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

    public List<OrderBean> getOrder() {
        return order;
    }

    public void setOrder(List<OrderBean> order) {
        this.order = order;
    }

    public static class OrderBean {
        /**
         * orderId : AD1804080306063277
         * integral : 1800.00
         * date : 2018-04-08 15:06:06
         * payType : 未付款
         * orderStatus : 1
         * ordertype : 7
         * cost : 4000.00
         * secondOrder : [{"secondOrderId":"ADS1804080306063277001","postNum":"","postCpy":"","postPhone":"","colorAndSize":"","unitCost":"4000.00","quantity":"1","secondOrderStatus":"1","productName":"测试测试","productId":"180033","productThumb":"http://img.yda360.com/"}]
         */

        private String orderId;
        private String integral;
        private String date;
        private String payType;
        private String orderStatus;
        private String ordertype;
        private String cost;
        private List<SecondOrderBean> secondOrder;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getIntegral() {
            return integral;
        }

        public void setIntegral(String integral) {
            this.integral = integral;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getPayType() {
            return payType;
        }

        public void setPayType(String payType) {
            this.payType = payType;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getOrdertype() {
            return ordertype;
        }

        public void setOrdertype(String ordertype) {
            this.ordertype = ordertype;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }

        public List<SecondOrderBean> getSecondOrder() {
            return secondOrder;
        }

        public void setSecondOrder(List<SecondOrderBean> secondOrder) {
            this.secondOrder = secondOrder;
        }

        public static class SecondOrderBean {
            /**
             * secondOrderId : ADS1804080306063277001
             * postNum :
             * postCpy :
             * postPhone :
             * colorAndSize :
             * unitCost : 4000.00
             * quantity : 1
             * secondOrderStatus : 1
             * productName : 测试测试
             * productId : 180033
             * productThumb : http://img.yda360.com/
             */

            private String secondOrderId;
            private String postNum;
            private String postCpy;
            private String postPhone;
            private String colorAndSize;
            private String unitCost;
            private String quantity;
            private String secondOrderStatus;
            private String productName;
            private String productId;
            private String productThumb;

            public String getSecondOrderId() {
                return secondOrderId;
            }

            public void setSecondOrderId(String secondOrderId) {
                this.secondOrderId = secondOrderId;
            }

            public String getPostNum() {
                return postNum;
            }

            public void setPostNum(String postNum) {
                this.postNum = postNum;
            }

            public String getPostCpy() {
                return postCpy;
            }

            public void setPostCpy(String postCpy) {
                this.postCpy = postCpy;
            }

            public String getPostPhone() {
                return postPhone;
            }

            public void setPostPhone(String postPhone) {
                this.postPhone = postPhone;
            }

            public String getColorAndSize() {
                return colorAndSize;
            }

            public void setColorAndSize(String colorAndSize) {
                this.colorAndSize = colorAndSize;
            }

            public String getUnitCost() {
                return unitCost;
            }

            public void setUnitCost(String unitCost) {
                this.unitCost = unitCost;
            }

            public String getQuantity() {
                return quantity;
            }

            public void setQuantity(String quantity) {
                this.quantity = quantity;
            }

            public String getSecondOrderStatus() {
                return secondOrderStatus;
            }

            public void setSecondOrderStatus(String secondOrderStatus) {
                this.secondOrderStatus = secondOrderStatus;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }

            public String getProductId() {
                return productId;
            }

            public void setProductId(String productId) {
                this.productId = productId;
            }

            public String getProductThumb() {
                return productThumb;
            }

            public void setProductThumb(String productThumb) {
                this.productThumb = productThumb;
            }
        }
    }
}
