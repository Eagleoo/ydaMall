package com.mall.model;

import java.util.List;

/**
 * Created by Administrator on 2017/10/25.
 */

public class RecBussModel {

    /**
     * code : 200
     * cache : 29979566
     * message : 26
     * list : [{"id":"416","name":"小佳","pointX":"104.074036","pointY":"30.66391","phone":"11111111111","ISELITE":"False","cate":"美容美发","zone":"四川省 成都市 青羊区 ","img":"http://img2.yda360.com/  ,"},{"id":"91","name":"星逸酒店","pointX":"104.077435","pointY":"30.668462","phone":"028-86919999","ISELITE":"False","cate":"酒店住宿","zone":"四川省 成都市 青羊区 ","img":"http://img2.yda360.com//rebate/images/202b12fc-424e-412d-8e70-32cc1626b2a1.jpg"},{"id":"418","name":"小佳美妆","pointX":"104.071504","pointY":"30.661843","phone":"15921845899","ISELITE":"False","cate":"特惠购物","zone":"四川省 成都市 青羊区 ","img":"http://img2.yda360.com/  ,"},{"id":"414","name":"陈麻婆豆腐","pointX":"104.078979","pointY":"30.661541","phone":"13718181818","ISELITE":"True","cate":"餐厅美食","zone":"四川省 成都市 青羊区 ","img":"http://img2.yda360.com//rebate/images/eb1b41c0-84cd-4b41-a865-08e1be7eb996.jpg"},{"id":"417","name":"小佳之佳","pointX":"104.063521","pointY":"30.679413","phone":"15220000001","ISELITE":"False","cate":"咖啡茶饮","zone":"四川省 成都市 金牛区 ","img":"http://img2.yda360.com/  ,"},{"id":"96","name":"米乐星世界KTV","pointX":"104.057511","pointY":"30.673861","phone":"028-86690505","ISELITE":"False","cate":"休闲娱乐","zone":"四川省 成都市 青羊区 ","img":"http://img2.yda360.com//rebate/images/89508f95-6c51-4fe5-89d5-a1024abbf557.jpg"},{"id":"315","name":"克拉美珠宝","pointX":"104.069855","pointY":"30.652159","phone":"18048822666","ISELITE":"False","cate":"特惠购物","zone":"四川省 成都市 锦江区 ","img":"http://img2.yda360.com//rebate/images/5b80a1c7-696d-4adb-b556-0f725aee8ab5.jpg"},{"id":"413","name":"j123456商家","pointX":"104.096146","pointY":"30.6704","phone":"11111111111","ISELITE":"False","cate":"餐厅美食","zone":"四川省 成都市 成华区 ","img":"http://img2.yda360.com/  ,"},{"id":"89","name":"米兰风尚酒店","pointX":"104.043156","pointY":"30.654859","phone":"028-65536868","ISELITE":"False","cate":"酒店住宿","zone":"四川省 成都市 青羊区 ","img":"http://img2.yda360.com//rebate/images/63562eac-c30c-498e-a6f8-caf9b9ab04fa.jpg"},{"id":"352","name":"大唐合盛·瓷砖（展厅3）","pointX":"104.089386","pointY":"30.692485","phone":"028-86456303","ISELITE":"False","cate":"家居建材","zone":"四川省 成都市 成华区 ","img":"http://img2.yda360.com//rebate/images/c830e327-8a43-4085-8bcc-6122a7f07d19.jpg"}]
     * adv2 : [{"id":"257","name":"","path":"http://test.yda360.cn//uploadfile/lmsjadv/130575705247532991.jpg"},{"id":"257","name":"","path":"http://test.yda360.cn//uploadfile/lmsjadv/130575705076868692.jpg"}]
     * adv : {"id":"257","name":"","path":"http://test.yda360.cn//uploadfile/lmsjadv/130575705247532991.jpg"}
     */

    private String code;
    private String cache;
    private String message;
    private AdvBean adv;
    private List<ListBean> list;
    private List<Adv2Bean> adv2;

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

    public AdvBean getAdv() {
        return adv;
    }

    public void setAdv(AdvBean adv) {
        this.adv = adv;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public List<Adv2Bean> getAdv2() {
        return adv2;
    }

    public void setAdv2(List<Adv2Bean> adv2) {
        this.adv2 = adv2;
    }

    public static class AdvBean {
        /**
         * id : 257
         * name :
         * path : http://test.yda360.cn//uploadfile/lmsjadv/130575705247532991.jpg
         */

        private String id;
        private String name;
        private String path;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class ListBean {
        /**
         * id : 416
         * name : 小佳
         * pointX : 104.074036
         * pointY : 30.66391
         * phone : 11111111111
         * Favorite: 0
         * ISELITE : False
         * cate : 美容美发
         * zone : 四川省 成都市 青羊区
         * img : http://img2.yda360.com/  ,
         */

        private String id;
        private String name;
        private String pointX;
        private String pointY;
        private String phone;
        private String ISELITE;
        private String cate;
        private String Favorite;

        public String getFavorite() {
            return Favorite;
        }

        public void setFavorite(String favorite) {
            Favorite = favorite;
        }

        private String zone;
        private String img;
        private float mm=Float.MAX_VALUE;

        public float getMm() {
            return mm;
        }

        public void setMm(float mm) {
            this.mm = mm;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPointX() {
            return pointX;
        }

        public void setPointX(String pointX) {
            this.pointX = pointX;
        }

        public String getPointY() {
            return pointY;
        }

        public void setPointY(String pointY) {
            this.pointY = pointY;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getISELITE() {
            return ISELITE;
        }

        public void setISELITE(String ISELITE) {
            this.ISELITE = ISELITE;
        }

        public String getCate() {
            return cate;
        }

        public void setCate(String cate) {
            this.cate = cate;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public static class Adv2Bean {
        /**
         * id : 257
         * name :
         * path : http://test.yda360.cn//uploadfile/lmsjadv/130575705247532991.jpg
         */

        private String id;
        private String name;
        private String path;
        private String Favorite;

        public String getFavorite() {
            return Favorite;
        }

        public void setFavorite(String favorite) {
            Favorite = favorite;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
