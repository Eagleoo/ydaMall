package com.mall.view.AccountHelp;

/**
 * Created by Administrator on 2018/5/18.
 */

public class CashBean {

    /**
     * @param cashAccountName 提现账户类型
     */
    public CashBean(String cashAccountName, double cashStarMoney, int cashMagn) {
        mCashAccountName = cashAccountName;
        mCashStarMoney = cashStarMoney;
        mCashMagn = cashMagn;

    }

    public enum CashObjects { //提现对象
        OperationsCenter, HeadQuarter
    }

    public enum CashType { //提现到 支付宝 银行卡 微信
        zhifubao("2"), bank("0"), weixin("1"), no("-1");
        String type_;

        CashType(String type) {
            type_ = type;
        }

        public String getType_() {
            return type_;
        }

        public void setType_(String type_) {
            this.type_ = type_;
        }
    }




//    public enum CashAccount {
//        bus("bus", 1), //业务账户
//        con("con", 0),//消费券
//        sb("sb", 0),//商币账户
//        rai("rai", 0),
//        rec("rec", 0),
//        pho("pho", 0),
//        lot("lot", 0),
//        zcb("zcb", 0),
//        shop1("shop1", 0),
//        shop2("shop2", 0),
//        shop3("shop3", 0),
//        red_s("red_s", 0),//红包种子
//        red_c("red_c", 2),//现金红包
//        xj("xj", 2),//现金红包
//        cash_c("cash_c", 3),//现金券
//        shopping_voucher("shopping_voucher", 0),//购物券
//        carpool_coupons("carpool_coupons", 0),//拼车券
//        Present_account("Present_account", 4); //Present_account
//
//        // 成员变量
//        private String name;
//        private int type;
//
//        public int getType() {
//            return type;
//        }
//
//        public void setType(int type) {
//            this.type = type;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        // 构造方法
//        private CashAccount(String name, int type) {
//            this.name = name;
//            this.type = type;
//
//        }
//
//        //覆盖方法
//        @Override
//        public String toString() {
//            return this.name;
//        }
//    }

    String mCashAccountName;

    public String getmCashAccountName() {
        return mCashAccountName;
    }

    public void setmCashAccountName(String mCashAccountName) {
        this.mCashAccountName = mCashAccountName;
    }

    CashObjects mCashObjects;
    CashType mCashType;

    private String OperationsCenteruserid;//运营中心id
    private String cashPwd;//提现密码
    private String cashAccount;//提现密码
    private double cashMoney;//提现金额
    private double mCashFees;//手续费 百分比
    private double mAccountBalance;//当前账户余额
    private int mCashMagn;//提现倍率
    private double mCashStarMoney;//提现起始金额

    public String getOperationsCenteruserid() {
        return OperationsCenteruserid;
    }

    public void setOperationsCenteruserid(String operationsCenteruserid) {
        OperationsCenteruserid = operationsCenteruserid;
    }


    public CashType getmCashType() {
        return mCashType;
    }

    public void setmCashType(CashType mCashType) {
        this.mCashType = mCashType;
    }


    public CashObjects getmCashObjects() {
        return mCashObjects;
    }

    public void setmCashObjects(CashObjects mCashObjects) {
        this.mCashObjects = mCashObjects;
    }


    public double getmCashStarMoney() {
        return mCashStarMoney;
    }

    public void setmCashStarMoney(double mCashStarMoney) {
        this.mCashStarMoney = mCashStarMoney;
    }

    public int getmCashMagn() {
        return mCashMagn;
    }



    public String getCashPwd() {
        return cashPwd;
    }

    public void setCashPwd(String cashPwd) {
        this.cashPwd = cashPwd;
    }

    public String getCashAccount() {
        return cashAccount;
    }

    public void setCashAccount(String cashAccount) {
        this.cashAccount = cashAccount;
    }

    public double getCashMoney() {
        return cashMoney;
    }

    public void setCashMoney(double cashMoney) {
        this.cashMoney = cashMoney;
    }

    public double getmCashFees() {
        return mCashFees;
    }

    public void setmCashFees(double mCashFees) {
        this.mCashFees = mCashFees;
    }

    public double getmAccountBalance() {
        return mAccountBalance;
    }

    public void setmAccountBalance(double mAccountBalance) {
        this.mAccountBalance = mAccountBalance;
    }
}
