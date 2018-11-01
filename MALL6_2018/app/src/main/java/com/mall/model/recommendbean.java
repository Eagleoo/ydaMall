package com.mall.model;

import java.util.List;

/**
 * Created by Administrator on 2018/3/16.
 */

public class recommendbean {


    /**
     * code : 200
     * cache : 45523402
     * message : 查询成功！
     * list : [{"pid":"179243","name":"巴乐乐伊2014夏季男童套装 时尚休闲短袖童装两件套 童装套装 童套装","sbj":"399","price":"59.00","jifen":"2.00","thumb":"http://img.yda360.com/ProductImg/22671586/2014/04/26/230_230_20140426113021375.jpg"},{"pid":"175008","name":"七匹狼背心7色新品纯棉背心男士背心罗纹宽肩全棉吸汗透气","sbj":"134","price":"19.90","jifen":"0.86","thumb":"http://img.yda360.com/ProductImg/26645400/2014/03/21/230_230_2014032103500493.jpg"},{"pid":"179658","name":"测试女鞋子2","sbj":"1","price":"0.10","jifen":"0.00","thumb":"http://img.yda360.com/"},{"pid":"179659","name":"无规格","sbj":"77","price":"11.00","jifen":"0.00","thumb":"http://img.yda360.com/"},{"pid":"172745","name":"高夫GF男士NO.2活力去屑洗发水400ml","sbj":"251","price":"36.80","jifen":"0.96","thumb":"http://img.yda360.com/ProductImg/22592643/2014/03/01/230_230_20140301025029781.jpg"},{"pid":"180016","name":"食品测试123456","sbj":"35","price":"5.00","jifen":"0.00","thumb":"http://img.yda360.com/"},{"pid":"179918","name":"Huhb004","sbj":"378","price":"60.00","jifen":"6.00","thumb":"http://img.yda360.com//ProductImg/25346740_ervice/2015-04-30/174_174_14303602997321645890796.jpg"},{"pid":"179917","name":"Huhb003","sbj":"7","price":"1.00","jifen":"0.10","thumb":"http://img.yda360.com//ProductImg/25346740_ervice/2015-04-30/174_174_14303602997321645890796.jpg"},{"pid":"179622","name":"BESTBAO/百诗堡 韩版时尚松紧腰半身裙 简单大方多层斜下摆","sbj":"186","price":"106.00","jifen":"79.50","thumb":"http://img.yda360.com/ProductImg/25050321/2014/04/29/230_230_20140429045730984.jpg"},{"pid":"179919","name":"Huhb005","sbj":"7","price":"1.00","jifen":"0.10","thumb":"http://img.yda360.com//ProductImg/25346740_ervice/2015-04-30/174_174_14303602997321645890796.jpg"},{"pid":"179924","name":"贴吧里","sbj":"62216","price":"8888.00","jifen":"0.00","thumb":"http://img.yda360.com//ProductImg/22407345_ervice/2015-05-19/230_230_1432027609411-1057598959.jpg"},{"pid":"179922","name":"vggggf","sbj":"55995","price":"8888.00","jifen":"888.80","thumb":"http://img.yda360.com//ProductImg/25565833_ervice/2015-05-06/174_174_1430894946255-1832304445.jpg"}]
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
         * pid : 179243
         * name : 巴乐乐伊2014夏季男童套装 时尚休闲短袖童装两件套 童装套装 童套装
         * sbj : 399
         * price : 59.00
         * jifen : 2.00
         * thumb : http://img.yda360.com/ProductImg/22671586/2014/04/26/230_230_20140426113021375.jpg
         */

        private String pid;
        private String name;
        private String sbj;
        private String price;
        private String jifen;
        private String thumb;
        private String shorttitle;

        public String getShorttitle() {
            return shorttitle;
        }

        public void setShorttitle(String shorttitle) {
            this.shorttitle = shorttitle;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSbj() {
            return sbj;
        }

        public void setSbj(String sbj) {
            this.sbj = sbj;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getJifen() {
            return jifen;
        }

        public void setJifen(String jifen) {
            this.jifen = jifen;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }
    }
}
