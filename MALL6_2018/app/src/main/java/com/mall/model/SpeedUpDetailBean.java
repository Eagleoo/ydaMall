package com.mall.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/9/30.
 */

public class SpeedUpDetailBean implements Serializable{


    /**
     * code : 200
     * cache : 48079088
     * message : 2
     * list : [{"userId":"jnnjbb8","sex":"False","operationDate":"VIP会员","mobilePhone":"18583645972","date":"2017/9/29 20:00:28","Red_Seed":"10.00"},{"userId":"策搜索","sex":"True","operationDate":"VIP会员","mobilePhone":"18583342964","date":"2017/9/29 17:31:10","Red_Seed":"10.00"}]
     */

    private String code;
    private String cache;
    private String message;
    private String addUpFriend;
    private String allAchievement;
    private String allusercount;

    private String allRedbean; //部门加速明细

    private String addUpSection; //部门加速度时间

    private String ZJE; //总金额/总金额


    private String CAN_JS_MONEY;  //可加速金额/加速金额


    private String CAN_JS_DAY;  //可加速天数/加速天数


    private String COUNT_DAY_NUM;//累计加速天数/累计加速天数
    private String count_1;
    private String count_2;
    private String count_3;

    public String getCount_1() {
        return count_1;
    }

    public void setCount_1(String count_1) {
        this.count_1 = count_1;
    }

    public String getCount_2() {
        return count_2;
    }

    public void setCount_2(String count_2) {
        this.count_2 = count_2;
    }

    public String getCount_3() {
        return count_3;
    }

    public void setCount_3(String count_3) {
        this.count_3 = count_3;
    }

    public String getCount_4() {
        return count_4;
    }

    public void setCount_4(String count_4) {
        this.count_4 = count_4;
    }

    private String count_4;




    public String getZJE() {
        return ZJE;
    }

    public void setZJE(String ZJE) {
        this.ZJE = ZJE;
    }

    public String getCAN_JS_MONEY() {
        return CAN_JS_MONEY;
    }

    public void setCAN_JS_MONEY(String CAN_JS_MONEY) {
        this.CAN_JS_MONEY = CAN_JS_MONEY;
    }

    public String getCAN_JS_DAY() {
        return CAN_JS_DAY;
    }

    public void setCAN_JS_DAY(String CAN_JS_DAY) {
        this.CAN_JS_DAY = CAN_JS_DAY;
    }

    public String getCOUNT_DAY_NUM() {
        return COUNT_DAY_NUM;
    }

    public void setCOUNT_DAY_NUM(String COUNT_DAY_NUM) {
        this.COUNT_DAY_NUM = COUNT_DAY_NUM;
    }

    public String getAllRedbean() {
        return allRedbean;
    }

    public void setAllRedbean(String allRedbean) {
        this.allRedbean = allRedbean;
    }

    public String getAddUpSection() {
        return addUpSection;
    }

    public void setAddUpSection(String addUpSection) {
        this.addUpSection = addUpSection;
    }

    public String getAllusercount() {
        return allusercount;
    }

    public void setAllusercount(String allusercount) {
        this.allusercount = allusercount;
    }

    public String getAddUpFriend() {
        return addUpFriend;
    }

    public void setAddUpFriend(String addUpFriend) {
        this.addUpFriend = addUpFriend;
    }

    public String getAllAchievement() {
        return allAchievement;
    }

    public void setAllAchievement(String allAchievement) {
        this.allAchievement = allAchievement;
    }

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

    public static class ListBean implements Serializable{
        /**
         * userId : jnnjbb8
         * sex : False
         * operationDate : VIP会员
         * mobilePhone : 18583645972
         * date : 2017/9/29 20:00:28
         * Red_Seed : 10.00
         */

        private String userId;
        private String sex;
        private String operationDate;
        private String mobilePhone;
        private String mobilephone;
        private String MOBILEPHONE;
        private String CAN_JS_DAY;
        private String BM_JS_DAY;
        private String XZTS;

        public String getXZTS() {
            return XZTS;
        }

        public void setXZTS(String XZTS) {
            this.XZTS = XZTS;
        }

        public String getCAN_JS_DAY() {
            return CAN_JS_DAY;
        }

        public void setCAN_JS_DAY(String CAN_JS_DAY) {
            this.CAN_JS_DAY = CAN_JS_DAY;
        }

        public String getBM_JS_DAY() {
            return BM_JS_DAY;
        }

        public void setBM_JS_DAY(String BM_JS_DAY) {
            this.BM_JS_DAY = BM_JS_DAY;
        }

        public String getNOW_BM_JS_DAY() {
            return NOW_BM_JS_DAY;
        }

        public void setNOW_BM_JS_DAY(String NOW_BM_JS_DAY) {
            this.NOW_BM_JS_DAY = NOW_BM_JS_DAY;
        }

        private String NOW_BM_JS_DAY;

        public String getMOBILEPHONE() {
            return MOBILEPHONE;
        }

        public void setMOBILEPHONE(String MOBILEPHONE) {
            this.MOBILEPHONE = MOBILEPHONE;
        }

        public String getLineNum() {
            return lineNum;
        }

        public void setLineNum(String lineNum) {
            this.lineNum = lineNum;
        }

        private String redboxmoney;
        private String lineNum;

        private String RED_R;

        private  String RED_REC_MONEY;

        public String getRED_R() {
            return RED_R;
        }

        public void setRED_REC_MONEY(String RED_REC_MONEY) {
            this.RED_REC_MONEY = RED_REC_MONEY;
        }



        public void setRED_R(String RED_R) {
            this.RED_R = RED_R;
        }

        public String getRED_REC_MONEY() {
            return RED_REC_MONEY;
        }

        public String getRedboxmoney() {
            return redboxmoney;
        }

        public void setRedboxmoney(String redboxmoney) {
            this.redboxmoney = redboxmoney;
        }

        public String getMobilephone() {
            return mobilephone;
        }

        public void setMobilephone(String mobilephone) {
            this.mobilephone = mobilephone;
        }

        private String date;
        private String Red_Seed;
        private String phone;
        private String name;
        private String juese;
        private String bm;
        private String cy_num;
        private String red_r;
        private String allAchievement;
        private String sumincome;
        private String czdate;

        public String getCzdate() {
            return czdate;
        }

        public void setCzdate(String czdate) {
            this.czdate = czdate;
        }

        public String getSumincome() {
            return sumincome;
        }

        public void setSumincome(String sumincome) {
            this.sumincome = sumincome;
        }

        public String getAllAchievement() {
            return allAchievement;
        }

        public void setAllAchievement(String allAchievement) {
            this.allAchievement = allAchievement;
        }

        public String getRed_r() {
            return red_r;
        }

        public void setRed_r(String red_r) {
            this.red_r = red_r;
        }

        public String getCy_num() {
            return cy_num;
        }

        public void setCy_num(String cy_num) {
            this.cy_num = cy_num;
        }

        public String getBm() {
            return bm;
        }

        public void setBm(String bm) {
            this.bm = bm;
        }

        public String getJuese() {
            return juese;
        }

        public void setJuese(String juese) {
            this.juese = juese;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        private String income;
        private String sortLetter = "";

        public String getSortLetter() {
            return sortLetter;
        }

        public void setSortLetter(String sortLetter) {
            this.sortLetter = sortLetter;
        }

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getOperationDate() {
            return operationDate;
        }

        public void setOperationDate(String operationDate) {
            this.operationDate = operationDate;
        }

        public String getMobilePhone() {
            return mobilePhone;

        }

        public void setMobilePhone(String mobilePhone) {
            this.mobilePhone = mobilePhone;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getRed_Seed() {
            return Red_Seed;
        }

        public void setRed_Seed(String Red_Seed) {
            this.Red_Seed = Red_Seed;
        }
    }
}
