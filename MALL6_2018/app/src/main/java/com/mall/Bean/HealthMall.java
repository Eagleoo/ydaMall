package com.mall.Bean;

import java.util.List;

public class HealthMall {


    /**
     * code : 200
     * cache : 32590987
     * message : 查询成功！
     * list : [{"type":"7","list":[{"id":"40","url_android":"","url_iPhone":"","cate":"7","image":"http://test.yda360.cn/user/Annex/131744844093098750.png"}]},{"type":"8","list":[{"id":"45","url_android":"","url_iPhone":"","cate":"8","image":"http://test.yda360.cn/user/Annex/131744844622005000.png"},{"id":"44","url_android":"","url_iPhone":"","cate":"8","image":"http://test.yda360.cn/user/Annex/131744844577161250.png"},{"id":"43","url_android":"","url_iPhone":"","cate":"8","image":"http://test.yda360.cn/user/Annex/131744844520911250.png"},{"id":"42","url_android":"","url_iPhone":"","cate":"8","image":"http://test.yda360.cn/user/Annex/131744844440130000.png"},{"id":"41","url_android":"","url_iPhone":"","cate":"8","image":"http://test.yda360.cn/user/Annex/131744844264505000.png"}]},{"type":"9","list":[{"id":"48","url_android":"","url_iPhone":"","cate":"9","image":"http://test.yda360.cn/user/Annex/131744844974348750.png"},{"id":"47","url_android":"","url_iPhone":"","cate":"9","image":"http://test.yda360.cn/user/Annex/131744844927161250.png"},{"id":"46","url_android":"","url_iPhone":"","cate":"9","image":"http://test.yda360.cn/user/Annex/131744844872161250.png"}]},{"type":"10","list":[{"id":"50","url_android":"","url_iPhone":"","cate":"10","image":"http://test.yda360.cn/user/Annex/131744845189817500.png"},{"id":"49","url_android":"","url_iPhone":"","cate":"10","image":"http://test.yda360.cn/user/Annex/131744845141223750.png"}]},{"type":"11","list":[{"id":"51","url_android":"","url_iPhone":"","cate":"11","image":"http://test.yda360.cn/user/Annex/131744845313723750.png"}]},{"type":"12","list":[{"id":"54","url_android":"","url_iPhone":"","cate":"12","image":"http://test.yda360.cn/user/Annex/131744845499348750.png"},{"id":"53","url_android":"","url_iPhone":"","cate":"12","image":"http://test.yda360.cn/user/Annex/131744845456692500.png"},{"id":"52","url_android":"","url_iPhone":"","cate":"12","image":"http://test.yda360.cn/user/Annex/131744845414973750.png"}]}]
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
      private  String userid;
      private  String date;
      private  String integral;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getIntegral() {
            return integral;
        }

        public void setIntegral(String integral) {
            this.integral = integral;
        }
    }
}
