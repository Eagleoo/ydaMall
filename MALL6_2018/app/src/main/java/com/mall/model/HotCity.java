package com.mall.model;

import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */

public class HotCity {


    /**
     * code : 200
     * cache : 48266649
     * message : 85
     * list : [{"zoneid":"2617","name":"曲靖市","count":"38"},{"zoneid":"496","name":"深圳市","count":"24"},{"zoneid":"1442","name":"临沂市","count":"13"},{"zoneid":"683","name":"福州市","count":"12"},{"zoneid":"143","name":"杭州市","count":"11"},{"zoneid":"143","name":"杭州市","count":"11"},{"zoneid":"2129","name":"长沙市","count":"8"},{"zoneid":"2256","name":"成都市","count":"8"},{"zoneid":"2602","name":"昆明市","count":"8"},{"zoneid":"2602","name":"昆明市","count":"8"},{"zoneid":"2627","name":"玉溪市","count":"8"},{"zoneid":"2256","name":"成都市","count":"7"},{"zoneid":"3290","name":"兰州市","count":"7"},{"zoneid":"496","name":"深圳市","count":"7"},{"zoneid":"515","name":"佛山市","count":"7"},{"zoneid":"496","name":"深圳市","count":"6"},{"zoneid":"143","name":"杭州市","count":"6"},{"zoneid":"2461","name":"重庆市","count":"6"},{"zoneid":"2681","name":"楚雄彝族自治州","count":"6"},{"zoneid":"2602","name":"昆明市","count":"6"},{"zoneid":"1940","name":"武汉市","count":"5"},{"zoneid":"904","name":"三亚市","count":"5"},{"zoneid":"483","name":"广州市","count":"5"},{"zoneid":"586","name":"惠州市","count":"4"},{"zoneid":"2","name":"上海市","count":"4"},{"zoneid":"899","name":"海口市","count":"4"},{"zoneid":"2256","name":"成都市","count":"4"},{"zoneid":"2602","name":"昆明市","count":"3"},{"zoneid":"1155","name":"石家庄市","count":"3"},{"zoneid":"1704","name":"太原市","count":"3"},{"zoneid":"212","name":"衢州市","count":"3"},{"zoneid":"212","name":"衢州市","count":"3"},{"zoneid":"683","name":"福州市","count":"3"},{"zoneid":"683","name":"福州市","count":"2"},{"zoneid":"683","name":"福州市","count":"2"},{"zoneid":"515","name":"佛山市","count":"2"},{"zoneid":"143","name":"杭州市","count":"2"},{"zoneid":"496","name":"深圳市","count":"2"},{"zoneid":"245","name":"合肥市","count":"2"},{"zoneid":"143","name":"杭州市","count":"2"},{"zoneid":"2","name":"上海市","count":"2"},{"zoneid":"143","name":"杭州市","count":"2"},{"zoneid":"1155","name":"石家庄市","count":"2"},{"zoneid":"1801","name":"临汾市","count":"2"},{"zoneid":"2461","name":"重庆市","count":"2"},{"zoneid":"2256","name":"成都市","count":"2"},{"zoneid":"2256","name":"成都市","count":"2"},{"zoneid":"778","name":"南宁市","count":"2"},{"zoneid":"2627","name":"玉溪市","count":"2"},{"zoneid":"2617","name":"曲靖市","count":"1"},{"zoneid":"2742","name":"怒江傈僳族自治州","count":"1"},{"zoneid":"3290","name":"兰州市","count":"1"},{"zoneid":"3290","name":"兰州市","count":"1"},{"zoneid":"899","name":"海口市","count":"1"},{"zoneid":"904","name":"三亚市","count":"1"},{"zoneid":"1004","name":"屯昌县","count":"1"},{"zoneid":"1117","name":"北京市","count":"1"},{"zoneid":"","name":"","count":"1"},{"zoneid":"1155","name":"石家庄市","count":"1"},{"zoneid":"1281","name":"衡水市","count":"1"},{"zoneid":"1442","name":"临沂市","count":"1"},{"zoneid":"1498","name":"郑州市","count":"1"},{"zoneid":"1526","name":"洛阳市","count":"1"},{"zoneid":"1621","name":"南阳市","count":"1"},{"zoneid":"2256","name":"成都市","count":"1"},{"zoneid":"2256","name":"成都市","count":"1"},{"zoneid":"2602","name":"昆明市","count":"1"},{"zoneid":"2602","name":"昆明市","count":"1"},{"zoneid":"1704","name":"太原市","count":"1"},{"zoneid":"1940","name":"武汉市","count":"1"},{"zoneid":"2129","name":"长沙市","count":"1"},{"zoneid":"2256","name":"成都市","count":"1"},{"zoneid":"143","name":"杭州市","count":"1"},{"zoneid":"60","name":"苏州市","count":"1"},{"zoneid":"261","name":"芜湖市","count":"1"},{"zoneid":"269","name":"淮南市","count":"1"},{"zoneid":"358","name":"池州市","count":"1"},{"zoneid":"369","name":"南昌市","count":"1"},{"zoneid":"369","name":"南昌市","count":"1"},{"zoneid":"470","name":"抚州市","count":"1"},{"zoneid":"483","name":"广州市","count":"1"},{"zoneid":"496","name":"深圳市","count":"1"},{"zoneid":"483","name":"广州市","count":"1"},{"zoneid":"515","name":"佛山市","count":"1"},{"zoneid":"482","name":"广东省","count":"1"}]
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
         * zoneid : 2617
         * name : 曲靖市
         * count : 38
         */

        private String zoneid;
        private String name;
        private String count;

        public String getZoneid() {
            return zoneid;
        }

        public void setZoneid(String zoneid) {
            this.zoneid = zoneid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }
    }
}
