package com.mall.net;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.mall.model.LocationModel;
import com.mall.model.User;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能： WebService数据调用<br>
 * 时间： 2013-3-7<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class  Web {

    // 测试平台
    public static String webImage = "test.yda360.cn";
    public static String esccurl = "http://" + webImage;
    public static String webServer = "test.yda360.cn";
    public static String webServer_Image = "test.yda360.cn";
    public static String payServer = "test.yda360.cn";
    public static String url =
            "http://test.yda360.cn/yuanda_test/GetUserInfo.asmx";
    public static String newAPI = "http://" + webImage + "/api_test";
    public static String yyrgAddress = "http://" + webImage +
            "/yuanda_test/HotShop.asmx";
    public static String allianService = "http://" + webImage +
            "/yuanda_test/AllianceMerchant.asmx";
    public static String convience_service = "http://" + webImage +
            "/yuanda_test/Convenience_services.asmx";
    public static String imageip = "http://" + webImage;
    public static String updateAddress = webImage + "/yuandaApp";
    public static String downAddress = "down.yda360.com/test";
    public static String officeUrl = "http://" + webImage +
            "/api_test/MyOffice.asmx";
    public static String staffManager_service = "http://" + webImage +
            "/yuanda_test/StaffManager_New.asmx";
    public static String bBusinessCard = "http://" + webImage +
            "/api_test/BusinessCard.aspx";
    public static String product = "http://" + webImage +
            "/api_test/product.aspx";
    public static String User_Game_Log = "http://" + webImage +
            "/api_test/GameFraction.aspx?call=";
    public static String redurl = "http://test.yda360.cn/yuanda_test/GetRedInfo.asmx";
    public static String esccurl1="http://apphk.esccclub.com";


    // 演示版
//    public static String webImage = "show.yda360.com";
//    public static String webServer = "show.yda360.com";
//    public static String webServer_Image = "show.yda360.com";
//    public static String payServer = "show.yda360.com";
//    public static String url = "http://" + webImage +
//            "/yuanda_test/GetUserInfo.asmx";
//    public static String newAPI = "http://" + webImage + "/api_test";
//    public static String allianService = "http://" + webImage +
//            "/yuanda_test/AllianceMerchant.asmx";
//    public static String convience_service = "http://" + webImage +
//            "/yuanda_test/Convenience_services.asmx";
//    public static String yyrgAddress = "http://" + webImage +
//            "/yuanda_test/HotShop.asmx";
//    public static String staffManager_service = "http://" + webImage +
//            "/yuanda_test/StaffManager_New.asmx";
//    public static String imageip = "http://" + webImage;
//    public static String updateAddress = "test.yda360.cn/yuandaApp";
//    public static String downAddress = "down.yda360.com/test";
//    public static String officeUrl = "http://" + webImage +
//            "/api_test/MyOffice.asmx";
//    public static String bBusinessCard = "http://" + webImage +
//            "/api_test/BusinessCard.aspx";
//    public static String product = "http://" + webImage +
//            "/api_test/product.aspx";
//    public static String User_Game_Log = "http://" + webImage +
//            "/api_test/GameFraction.aspx?call=";
//    public static String redurl =
//            "http://show.yda360.com/yuanda_test/GetRedInfo.asmx";

//     //正式版
//    public static String webImage = "appgd.yda365.com";
//    public static String webServer = "appgd.yda365.com";
//    public static String webServer_Image = "www.yda365.com";
//    public static String payServer = "pay.yda365.com";
//    public static String url = "http://" + webServer
//            + "/yuanda_test/GetUserInfo.asmx";
//    public static String url2 = "http://" + webServer + "/Merchants.aspx?call=";
//    public static String newAPI = "http://" + webServer + "/api_test";
//    public static String yyrgAddress = "http://" + webServer
//            + "/yuanda_test/HotShop.asmx";
//    public static String allianService = "http://" + webServer
//            + "/yuanda_test/AllianceMerchant.asmx";
//    public static String convience_service = "http://" + webServer
//            + "/yuanda_test/Convenience_services.asmx";
//    public static String imageip = "http://" + webServer + "/";
//    public static String updateAddress = webServer + "/yuandaApp";
//    public static String downAddress = "down.yda365.com";
//    public static String officeUrl = "http://" + webImage
//            + "/api_test/MyOffice.asmx";
//    public static String staffManager_service = "http://" + webServer
//            + "/yuanda_test/StaffManager_New.asmx";
//    public static String bBusinessCard = "http://" + webServer
//            + "/api_test/BusinessCard.aspx";
//    public static String product = "http://" + webServer
//            + "/api_test/product.aspx";
//    public static String User_Game_Log = newAPI + "/GameFraction.aspx?call=";
//    public static String redurl = "http://appgd.yda365.com/yuanda_test/GetRedInfo.asmx";
//    public static String esccurl = "http://appgd.yda365.com";
//    // 用来提示当前是演示版的
    public static String test_url = "http://test.yda360.cn/yuanda_test/GetUserInfo.asmx";
    public static String test_url2 = "http://show.mall666.cn/yuanda_test/GetUserInfo.asmx";
    public static String imgServer = "img.yda360.com";
    public static String imgUServer = "imgu.yda360.com";
    public static String imgServer2 = "http://img2.yda360.com/";
    public static String voiceApk = "http://down.yda360.com";
    public static String validate_card = "http://apis.juhe.cn/idcard/index?";

    public static String ydnews_url = "http://www.yda360.cn/ydxml.asp?action=";
    // #start 云商方法

    // 收集安装信息
    public static final String install = "/install";
    // 登录方法
    // public static final String doLogin = "/doLogin";
    // 修改二级密码
    public static final String updateTwoPwd2 = "/updateTwoPwd2";
    // 现金追加
    public static final String moneyAppend = "/moneyAppend";
    // 得到用户订单方法
    public static final String getAllOrder = "/getAllOrder";
    // 业务账户转账：0充值，1预存，2赠送
    public static final String busToMoney = "/businessToMoney";
    // 赠送账户转赠送账户
    public static final String conToCon = "/conToCon";
    // 商币账户转商币账户(普通会员赚联盟商家)
    public static final String sbToSb = "/sbToSb";
    // 商币账户转商币账户(VIP以上转任何人)
    public static final String usersbToSb = "/UsersbToSb";
    // 彩票账户转充值账户
    public static final String cpToRec = "/cpToRec";
    // 得到全部代理
    public static final String getAllProxy = "/getAllProxy";
    // 得到全部网店信息
    public static final String getAllShopSite = "/getAllShopSite";
    // 获取业务账户明细
    public static final String getBusinessAccount = "/getBusinessAccount";
    // 获取赠送账户明细
    public static final String getHandselAccount = "/getHandselAccount";
    // 获取权益账户明细
    public static final String getInterestsAccount = "/getInterestsAccount";
    // 获取充值账户明细
    public static final String getRechargeAccount = "/getRechargeAccount";
    // 获取彩票账户明细
    public static final String getLotteryAccount = "/getLotteryAccount";
    // 获取预存账户明细
    public static final String getStoredAccount = "/getStoredAccount";
    // 获取商币账户明细
    public static final String getSBDetailList = "/getSBDetailList";
    // 获取资产包（蓝钻）账户明细
    public static final String getZCBDetailList = "/getZCBDetailList";
    // 充值账户转账
    public static final String recToRecharge = "/rechargeToRecharge";
    // 预存账户转账
    public static final String stoToMoney_ = "/stoToMoney_";
    // 得到所有类别
    public static final String getAllClass = "/getAllClass";
    // 得到二级订单
    public static final String getTwoOrder = "/getTwoOrder";
    // 得到账户余额
    public static final String getMoney = "/getMoney";
    // 升级成为VIP
    public static final String upToVip = "/upToVip";
    // 申请购物卡
    public static final String requestSC = "/requestShopCard_lin";
    // 升级成为网店
    public static final String upToSite = "/upToSite_2017";
    // 大学生空间
    public static final String upToSchoolSite = "/upToSchoolSite";

    // 升级成为网店
    public static final String upToSite_ZC = "/upToSite_ZC";
    // 验证手机号码
    public static final String sendPhoneValidataCode = "/sendPhoneValidataCode";
    // 查询商家
    public static final String serachShopM = "/serachShopM";
    // 升级城市总监
    public static final String requestCityDirector = "/requestCityDirector";
    // 升级成为代理
    public static final String upToProxy = "/upToProxy";
    // 获取可申请代理的区域
    public static final String getZoneIdProxy = "/getZoneIdProxy";
    // 得到地区类
    public static final String getZone = "/getZone";
    // 得到产品(根据分类)
    public static final String getProd = "/getProductByCateId";
    // 得到精品鞋城
    public static final String getJPXC = "/getJPXC";
    // 修改密码
    public static final String getModPass = "/updatePwd";
    // 直接修改登录密码
    public static final String updateLoginPwd = "/updateLoginPwd";
    // 根据帐号获取手机号
    public static final String getPhoneByName = "/validaUserName";
    // 获取全部消息
    public static final String getAllMessage = "/getAllArticle";
    // 注册
    public static final String registor = "/registor";
    // 获取推荐会员和招商会员
    // public static final String getAllUser = "/getAllUser";
    public static final String getAllUser = "/Merchants.aspx?call=";

    // 申请网店
    public static final String getAllSite = "/getAllSite";
    // 修改资料
    public static final String updateUser = "/updateUserInfo";

    // 购物车
    public static final String getShopCar = "/getShopCar";
    // 得到话费充值话费的金额
    public static final String getPhoneMoney = "/getPhoneMoney";
    // 话费充值
    public static final String getGoPhone = "/getGoPhone";
    // 游戏一级分类
    public static final String getGameClass = "/getGameClass";
    // 游戏二级分类
    public static final String getGameTwoClass = "/getGameTwoClass";
    // 游戏区服
    public static final String getGameAera = "/getGameAera";
    // 游戏支付
    public static final String payGameOrder = "/payGameOrder";
    // 得到订单状态
    public static final String getOrderStatus = "/getAllOrderStatus";
    // 是否申请过代理
    public static final String isProxy = "/isProxy";
    // 是否申请过网店
    public static final String isSite = "/isSite";
    // 得到广告图
    public static final String getImage = "/getAllImage";
    // 查询
    public static final String serach = "/serach";
    // 提現明細
    public static final String txming = "/doTXDetails";
    // 提現明細
    public static final String cptxming = "/doCPTXDetails";
    // 申请提现
    public static final String requestTX = "/requestTX";
    // 申请提现
    public static final String cpTX = "/cpTX";
    // 得到提现可选银行
    public static final String getBank = "/getBankList";
    // 更新银行信息
    public static final String updateBank = "/updateBank";
    // 验证用户名是否存在
    public static final String valiUserName = "/validataUserName";
    // 得到首页下方产品
    public static final String getPY = "/getPY";
    // 得到区域
    public static final String getZoneByParent = "/getZoneByParent";
    // 设置二级密码和地区
    public static final String setPwdZone = "/updateTwoZone";
    // 查询单个产品信息
    public static final String getProductById = "/getProductById";
    // 得到产品规格
    public static final String getProductStandardById = "/getProductStandardById";
    // 得到转账用户信息
    public static final String getNamePhone = "/getUserNameAndPhone";
    // 開通網店
    public static final String createShopSite = "/createShopSite";
    // 得到图片集
    public static final String getImageList = "/getProductImage";
    // 得到产品颜色和尺码
    public static final String getColorSize = "/getColorAndSize";
    // 根据颜色和尺码得到价格
    public static final String getMoneyForColorAndSize = "/getColorAndSizeMoney";
    // 加入购物车
    public static final String addShopCard = "/addShopCard";
    // 逛商城
    // public static final String AddRecord = "/AddRecord";
    // 验证商品能否生成订单
    public static final String validataShopOrder = "/shopProduct";
    // 得到购物车产品
    public static final String shopItem = "/getInfo";
    // 修改购物车数量
    public static final String updateShopCarAmount = "/updateShopCarAmount";
    // 得到用户的收货地址
    public static final String getShopAddress = "/getShop_Car_Address";
    // 生成订单
    public static final String createOrder = "/createOrder";
    // 得到二级订单下属商品
    public static final String getTwoOrderProduct = "/getTwoOrderProduct";
    // 得到未完成订单
    public static final String getAlluUndoneOrder = "/getAlluUndoneOrder";
    // 根据一级订单获取商品
    public static final String getOrderProductByOID = "/getOrderProductByOID";
    // 支付商品订单
    public static final String pay = "/pay_shoppingProduct";
    // 确认收货
    public static final String endOrder = "/endOrder";
    // 添加收货地址
    public static final String addShopAddress = "/addShopAddress";
    // 修改收货地址
    public static final String updateShopAddress = "/updateShopAddress";
    // 取消订单
    public static final String quitOrder = "/quitOrder";
    // 充值账户充值
    public static final String recChongzhi = "/recChongzhi";
    // 得到文章内容
    public static final String getMessageById = "/getMessageInfo";
    // 获取用户信息
    public static final String getUserInfo_1 = "/getUserInfo_1";
    // 找回密码
    public static final String firgetPassword = "/forgetPassword";
    // 发送验证码
    public static final String sendOldCode = "/sendOldRanCode";
    // 发送验证码
    public static final String sendNewCode = "/sendNewRanCode";
    // 修改手机号码
    public static final String updatePhone = "/updatePhone";
    // 热卖
    public static final String hotProduct = "/getCategoryHost";
    // 推荐
    public static final String recProduct = "/getCategoryRec";
    // 得到增值网店
    public static final String getShopSite = "/getShopSite";
    // 查询
    public static final String getProductByQueryType = "/getProductByQueryType";
    // 得到实体店
    public static final String getShopMMM = "/getShopM";
    // 饿到推荐的实体店
    public static final String getTuiJianShopMM = "/getTuiJianShopM";
    // 通过城市名得到联盟商家
    public static final String getShopMByCName = "/getShopMByCName";
    // 得到推荐的实体店
    public static final String getTuiJianShopMMByPage = "/getTuiJianShopMByPage";
    // 根据分类得到联盟商家
    public static final String getShopMByCate = "/getShopMByCate";
    // 根据城市得到联盟商家
    public static final String getShopMByCity = "/getShopMByCity";
    // 根据城市得到联盟商家
    public static final String getShopMByCateCity = "/getShopMByCateCity";
    // 得到联盟商家分类
    public static final String getShopMCate = "/getShopMCate";
    // 得到联盟商家城市
    public static final String getShopMCity = "/getShopMCity";
    // 根据地图可视范围得到附近的联盟商家
    public static final String getShopMByLatLng = "/getShopMByLatLng";
    // 得到联盟商家详细信息
    public static final String getShopMID = "/getShopMById";
    // 得到联盟商家详细信息
    public static final String getShopMInfo = "/getShopMInfoById";
    // 得到邮费
    public static final String getYouFei = "/getYouFei";
    // 得到商品兑换区的商品列表
    public static final String getProductBySBQ = "/getProductSBQByCateId";
    // 删除购物车制定产品
    public static final String delShopCar = "/delShopCar";
    // 写入订单
    public static final String wriertOrder = "/writerOrder";
    // 得到商币区推荐
    public static final String getSBQTuijian = "/getSBQTuiJian";
    // 得到未读消息
    public static final String getUnReaderMessage = "/getAllInMail";
    // 设置消息状态
    public static final String readInMail = "/readInMail";
    // 得到产品邮费
    public static final String getYoufeiByPidAndZone = "/getYoufeiByPidAndZone";
    // 根据分类id得到换购区商品
    public static final String getHuanGouProductByCateId = "/getHuanGouProductByCateId";
    // 得到联盟商家，各个星级，评分人数和总分
    public static final String getLMSJ12345ScoreByLmsjId = "/getLMSJ12345ScoreByLmsjId";
    // 得到联盟商家分页评论
    public static final String getLMSJCommentPage = "/getLMSJCommentPage";
    // 得到联盟商家分页评论
    public static final String getShopMByCityAndCate = "/getShopMByCNameAndCate";
    // 得到联盟商家，商家政策
    public static final String getLMSJBusinessPolicy = "/getLMSJBusinessPolicy";
    // 得到推荐人信息
    public static final String getInviter = "/getInviter";
    // 联盟商家评论
    public static final String addLMSJComment = "/addLMSJComment";
    // 验证用户名是否存在
    public static final String validataUserName = "/validaUserName";
    // 获取申请网店记录
    public static final String getWebSiteRequestList = "/getWebSiteRequestList";
    // 获取申请网店记录
    public static final String getProxyInfoList = "/getProxyInfoList";
    // 获取创业大使申请记录
    public static final String getAngelInfoList = "/getAngelInfoList";
    public static final String getWebSiteLMSJRequestList = "/getWebSiteLMSJRequestList";
    // 取消网店申请
    public static final String canelWebSite = "/canelWebSite";
    // 取消代理申请
    public static final String canelProxy = "/canelProxy";
    // 创业大使申请取消
    public static final String canelAngel = "/canelAngel";
    // 网店续付款
    public static final String webSite_pay = "/webSite_pay";
    // 创业大使续付款
    public static final String angel_pay = "/angel_pay";
    // 联盟商家继续付款
    public static final String alliance_pay = "/alliance_pay";
    public static final String ydck_pay = "/payYDCK";
    // 取消网店申请
    public static final String requestAlliance = "/requestAlliance";
    // 得到收货地址
    public static final String getShopAddressByPage_lin = "/getShopAddressByPage_lin";
    // 得到用户收货地址
    public static final String getShopAddressById = "/getShopAddressById";
    // 得到热换的换购商品
    public static final String getHotHGQ = "/getHGQHost_1";
    // 删除收获地址
    public static final String deleteUserShopAddress = "/deleteUserShopAddress";
    // 更新资料
    public static final String updateUInfo = "/updateUInfo";
    public static final String updateUInfo_2017 = "/updateUInfo_2017";
    public static final String updateUInfo_2017_bypwd = "/updateUInfo_2017_bypwd";
    // 验证手机是否重复
    public static final String validataPhoneExiste = "/validataPhoneExiste";
    // 验证邮箱是否存在
    public static final String validataEmailExiste = "/validataEmailExiste";
    // 完善用户资料n1
    public static final String updateUserInfo_n1 = "/updateUserInfo_n1";
    // 完善交易密码
    public static final String updateTwoPwd_n2 = "/updateTwoPwd_n2";
    // 得到联盟商家商品
    public static final String getServiceProduct = "/getServiceProduct";
    // 得到远大卷信息
    public static final String getMallServiceByTid = "/getMallServiceByTid";
    // 得到服务型商品
    public static final String getServiceProductByLidAndPid = "/getServiceProductByPidAndLid";
    // 得到购物车总数
    public static final String getShopCarCount = "/getShopCarCount";
    // 得到商品配送方式
    public static final String getYoufeiType = "/getYoufeiType";
    // 活动商品
    public static final String getActivityProduct = "/getActivityProduct";
    // 得到手机号码归属地
    public static final String getPhoneCityForInterface = "/getPhoneCityForInterface";
    // 上传用户头像
    public static final String uploadImage = "/uploadImage";
    // 验证是否可以申请大学生创业
    public static final String isRequestStudent = "/isRequestStudent";
    // 大学生申请创业空间
    public static final String requestStudent_site = "/requestStudent_site";
    // 申请创业大师
    public static final String requestPoititionAngel = "/requestPoititionAngel";
    // 验证是否能够申请创业大师
    public static final String validataRequestAngel = "/validataRequestAngel";
    // 商币充值
    public static final String sbChongzhi = "/sbChongzhi";
    // 用户反馈
    public static final String fankuiMessage = "/fankuiMessage";
    // 得到网友留言
    public static final String getAllUserMessageBoard = "/getAllUserMessageBoard";

    //根据城市ID获取城市列表

    public static final String getAllUserMessageBoard_bycity = "/getAllUserMessageBoard_bycity";
    public static final String getAllArticle_kfzx = "/getAllArticle_kfzx";
    public static final String getAllArticle_kfzx_class = "/getAllArticle_kfzx_class";

    //获取商圈分组
    public static final String getAllUserMessagecity = "/getAllUserMessagecity";

    // 得到单个会员的所有心情
    public static final String getUserMessageBoard = "/getUserMessageBoard";
    // 点赞
    public static final String operateUserMessageBoardPraise = "/operateUserMessageBoardPraise";
    // 根据心情获取评论
    public static final String getUserMessageBoardCommentByID = "/getUserMessageBoardCommentByID";
    // 根据心情获取赞
    public static final String getUserMessageBoardPraise = "/getUserMessageBoardPraise";
    // 根据心情ID评论
    public static final String addUserMessageBoardComment = "/addUserMessageBoardComment";
    // 发布心情
    public static final String addUserMessageBoard = "/addUserMessageBoard";
    // 心情首页的Banner图
    public static final String getMoodBannerList = "/getMoodBannerList";
    // 单个会员的心情背景图
    public static final String getUserBannerList = "/getUserBannerList";
    // 更新单个会员的心情背景图
    public static final String uploadUserBanner = "/uploadUserBanner";

    // #end

    // #start 联盟商家方法

    // 得到商币赠送比例
    public static final String allian_getShopProportion = "/getShopProportion";
    // 保存商币比例
    public static final String allian_saveSendSet = "/saveSendSet";
    // 消费转积分
    public static final String allian_doSendScore = "/doSendScore";

    // #end

    // #start 酒店
    public static final String Hotel_get_city = "/Hotel_get_city";
    public static final String GetHotel = "/GetHotel";
    public static final String GetDepart = "/GetDepart";
    public static final String getAllUserByShopAndInviter = "/getAllUserByShopAndInviter";
    public static final String writerHotelOrder = "/writerHotelOrder";
    public static final String getAllCity = "/getAllCity";
    public static final String getHotBrand = "/getHotBrand";
    public static final String getCityLandmarks = "/getCityLandmarks";
    public static final String getAllUserByShopAndInviterCount = "/getAllUserByShopAndInviterCount";
    // #end

    // #start 电影票
    public static final String Film_getAreaList = "/Film_getAreaList";
    public static final String Film_getComingFilms = "/Film_getComingFilms";
    public static final String Film_loadCinema = "/Film_loadCinema";
    public static final String Film_getHot = "/Film_getHot";
    public static final String Film_getShowTimeByCinemaNoFilmNo = "/Film_getShowTimeByCinemaNoFilmNo";
    public static final String Film_getSeat = "/Film_getSeat";
    public static final String Film_createSeatTicketOrder = "/Film_createSeatTicketOrder";
    public static final String Film_getCommTickets = "/Film_getCommTickets";
    public static final String Film_createCommTicketOrder = "/Film_createCommTicketOrder";
    public static final String pay_filmCommTicket = "/pay_filmCommTicket";
    // #end

    // #start 订机票
    public static final String Ticket_getCity = "/Ticket_getCity";
    public static final String Ticket_getTicket = "/Ticket_getTicket";
    public static final String Ticket_getMore = "/Ticket_getMore";
    public static final String Ticket_addorder = "/Ticket_addorder";
    public static final String getTicketMoney = "/getTicketMoney";
    public static final String payTicketOrder = "/payTicketOrder";
    // #end

    // #start 彩票
    public static String createLotteryOrder = "/createLotteryOrder";
    public static String getLotteryInfo = "/getLotteryInfo";
    public static String getMyLotteryOrder = "/getMyLotteryOrder";
    public static String getMyLotteryOrderInfo = "/getMyLotteryOrderInfo";
    public static int issueNumber;
    // #end

    // #start 订单管理开始
    public static final String getServiceHotelOrder = "/getServiceHotelOrder";
    public static final String getServiceFilmOrder = "/getServiceFilmOrder";
    public static final String getServiceTicketOrder = "/getServiceTicketOrder";
    public static final String getServicePhoneOrder = "/getServicePhoneOrder";
    public static final String getServiceGameOrder = "/getServiceGameOrder";
    public static final String getServiceTicketOrderDetail = "/getServiceTicketOrderDetail";
    public static final String getServiceHotelOrderDetail = "/getServiceHotelOrderDetail";

    // #end

    // #start 网络电话
    public static String initPhoneAccount = "/initPhoneAccount";
    public static String getAllInMail = "/getAllInMail";
    public static String getAllArticle = "/getAllArticle";
    public static String getMessageInfo = "/getMessageInfo";
    public static String sendShortMessage = "/sendShortMessage";
    public static String getSendShortMessage = "/getSendShortMessage";
    public static String getAllYDContacts = "/getAllYDContacts";
    public static String userRegistration = "/userRegistration";
    public static String getuserRegistrationDateList = "/getuserRegistrationDateList";
    public static String rechangePhoneAccount = "/rechangePhoneAccount";
    public static String sbToPhoneAccount = "/sbToPhoneAccount";
    public static String getPhoneAccount = "/getPhoneAccount";
    public static String backupContacts = "/backupContacts";
    public static String revertContacts = "/revertContacts";
    public static String clearContacts = "/clearContacts";
    public static String getCallPhoneList = "/getCallPhoneList";
    public static String getCallPhoneInfo = "/getCallPhoneInfo";
    public static String getCardState = "/getCardState";
    public static String getCardMoney = "/getCardMoney";
    public static String topupCard = "/topupCard";
    public static String shieldUserMessage = "/shieldUserMessage";
    public static String getShieldUserList = "/getShieldUserList";
    public static String sendCallerIDCode = "/sendCallerIDCode";
    public static String validataCallerIDCode = "/validataCallerIDCode";
    public static String isCallerID = "/isCallerID";
    public static String syncUserPhoneMoney = "/syncUserPhoneMoney";
    public static String deleteCallPhoneInfo = "/deleteCallPhoneInfo";
    public static String deleteCallPhoneList = "/deleteCallPhoneList";
    public static String unCallPhoneLock = "/unCallPhoneLock";
    public static String sendRandomCodeForNoMd5Pwd = "/sendRandomCodeForNoMd5Pwd";
    public static String sendUPM = "/sendUPM";
    // #end

    // 根据type获取定单
    public static String getAllOrderByType = "/getAllOrderByType";

    // #start 一元热购
    // 得到最新揭晓的产品
    public final static String getNewAnnounce = "/GetNewAnnounce";
    // 得到最热人气商品
    public final static String getMostPopularity = "/GetMostPopularity";
    // 晒单的接口
    public final static String getAllBaskSingle = "/GetAllBaskSingle";
    // 获取商品详细信息
    public final static String getHotShopInfo = "/GetHotShopInfo";
    // 添加购物车
    public final static String addHotShopCar = "/AddHotShopCar";
    // 获得上期获奖者
    public final static String getLastPeriod = "/GetLastPeriod";
    // 获得购物车信息
    public final static String getHotShopCarOnLogin = "/GetHotShopCarOnLogin";
    // 修改购物车数量
    public final static String updateHotShopCarNum = "/UpdateHotShopCarNum";
    // 删除购物车
    public final static String delHotShopCar = "/DelHotShopCar";
    // 创建热购订单
    public final static String createHotShopOrderNum = "/CreateHotShopOrderNum";
    // 获得指定商品的晒单记录
    public final static String getBaskSingle = "/GetBaskSingle";
    // 获得参与记录
    public final static String getNewHotShopRecord = "/GetNewHotShopRecord";
    // 获得我的所有的热购记录
    public final static String getMyAllHotShopRecord = "/GetMyAllHotShopRecord";
    // 获得我的所有的热购记录
    public final static String GetMyAllHotShopRecordByType = "/GetMyAllHotShopRecordByType";
    // 一元热购商品分类
    public final static String getGoodClassRecord = "/GetGoodClassRecord";
    // 一元热购品牌列表
    public final static String getGoodBrandRecord = "/GetGoodBrandRecord";
    // 一元热购商品列表
    public final static String getGoodProductListRecord = "/GetGoodProductListRecord";
    // 获得已揭晓的商品的信息
    public final static String gethistoryHotShopInfo = "/GethistoryHotShopInfo";
    // 前面一百位订单的信息
    public final static String getTopHundredRecord = "/GetTopHundredRecord";
    // 获得中奖用户的信息
    public final static String getWinPrizeInfo = "/GetWinPrizeInfo";
    // 获取购买的期次
    public final static String getShopPeriods = "/GetShopPeriods";
    // 添加晒单信息
    public final static String addHotBuyRecord = "/AddHotBuyRecord";
    // 上传晒单图片
    public final static String upOrderImage = "/upOrderImage";
    // 得到揭晓中的商品
    public final static String getDoingProduct = "/getDoingProduct";
    // 订单确认
    public final static String pay_createOrder = "/pay_createOrder";
    // 订单支付
    public final static String pay_1yygOrder = "/pay_1yygOrder";
    // 限时抢购
    public final static String getGoodProductTimeListRecord = "/GetGoodProductTimeListRecord";
    public static final String getImportantNotes = "/getImportantNotes";
    public static final String addImportantNotes = "/addImportantNotes";
    public static final String deleteImportantNotes = "/deleteImportantNotes";
    public static final String searchImportantNotes = "/searchImportantNotes";
    public static final String updateImportantNotes = "/updateImportantNotes";
    public static final String GetTopHundredRecordCount = "/GetTopHundredRecordCount";
    // #end

    // #start 获得活动商品分类的列表
    public static final String Get_ActivityCategory = "/Get_ActivityCategory";
    // 获得活动商品的列表
    public static final String Get_ActivityProductByPage = "/Get_ActivityProductByPage";
    // 活动分区限时抢购
    public static final String Get_ActivityTimeProduct = "/Get_ActivityTimeProduct";
    //
    public static final String getAllActivityTheme = "/getAllActivityTheme";

    // #end

    // #start 获取办公室信息
    public static final String GetOfficeInfo = "/GetOfficeInfo";
    public static final String GetIndexProductList = "/GetIndexProductList";
    public static final String GetArticlesListPage = "/GetArticlesListPage";
    public static final String UpdateGoods = "/UpdateGoods";
    public static final String GetCommentList = "/GetCommentList";
    public static final String AddComment = "/AddComment";
    public static final String DelComment = "/DelComment";
    public static final String GetPhotoListPage = "/GetPhotoListPage";
    public static final String GetVideoListPage = "/GetVideoListPage";
    public static final String DeleteOfficeUserArticle = "/DeleteOfficeUserArticle";
    public static final String GetMp3ListPage = "/GetMp3ListPage";
    public static final String UploadOfficeUserPhoto = "/UploadOfficeUserPhoto";
    public static final String AddOfficePhotoClass = "/AddOfficePhotoClass";
    public static final String AddOfficeUserPhoto = "/AddOfficeUserPhoto";
    public static final String GetOfficePhotoClass = "/GetOfficePhotoClass";
    public static final String GetOfficeArticleClass = "/GetOfficeArticleClass";
    public static final String AddOfficeUserArticle = "/AddOfficeUserArticle";
    public static final String GetOfficeUserAllOrder = "/GetOfficeUserAllOrder";
    public static final String AddOfficeArticleClass = "/AddOfficeArticleClass";
    public static final String DelOfficeUserPhotoClass = "/DelOfficeUserPhotoClass";
    public static final String DelOfficeUserArticleClass = "/DelOfficeUserArticleClass";
    public static final String DeleteOfficeUserProduct = "/DeleteOfficeUserProduct";
    public static final String GetOfficeProductListPage = "/GetOfficeProductListPage";
    public static final String SetOfficeInfo = "/SetOfficeInfo";
    public static final String GetOfficeListPage = "/GetOfficeListPage";
    public static final String favoriteOffices = "/favoriteOffices";
    public static final String deletefavoriteOffices = "/deletefavoriteOffices";
    public static final String getMyFavoriteOffices = "/getMyFavoriteOffices";
    public static final String getStaffUserBlessingByUserId = "/getStaffUserBlessingByUserId";
    public static final String ShareProToOffice = "/ShareProToOffice";
    public static final String GetOfficeListPage2 = "/GetOfficeListPage2";
    public static final String GetOfficeListPage3 = "/GetOfficeListPage2";
    public static final String getUserCountById = "/getUserCountById";

    public static final String sendMessage = "/sendMessage";
    public static final String sendPushMessage = "/sendPushMessage";
    public static final String getAllUserCount = "/getAllUserCount";
    public static final String getAllShopUserCount = "/getAllShopUserCount";
    // 获得会员详细信息
    public static final String getStaffUserByUserid = "/getStaffUserByUserid";
    public static final String getStaffUserBlessingTop = "/getStaffUserBlessingTop";
    // 获得微商产品分类
    public static final String get_RelatedCategory = "/Get_RelatedCategory";
    // 获得微商产品列表
    public static final String get_RelatedProductList = "/Get_RelatedProductList";
    // #end
    // 名片通开始
    /**
     * 查询所有名片列表
     */
    public static final String getAllUserBusinessCard = "/getAllUserBusinessCard";
    /**
     * 查询用户所有名片分类
     */
    public static final String getUserBusinessCardGroup = "/getUserBusinessCardGroup";
    /**
     * 新增名片
     */
    public static final String addBusinessCard = "/addBusinessCard";
    /* 新增名片分类 */
    public static final String addUserBusinessCardGroup = "/addUserBusinessCardGroup";
    /**
     * 修改名片
     */
    public static final String editBusinessCard = "/editBusinessCard";
    /**
     * 用户所有的发起的请求信息
     */
    public static final String getAllInitiateUserNameCardShare = "/getAllInitiateUserNameCardShare";
    /**
     * 用户收到的所有的请求
     */
    public static final String getAllreceiveUserNameCardShare = "/getAllreceiveUserNameCardShare";
    /**
     * 发起交换
     */
    public static final String addNameCardShare = "/addNameCardShare";
    /**
     * 同意交换
     */
    public static final String editNameCardShareOK = "/editNameCardShareOK";
    /**
     * 拒绝交换
     */
    public static final String editNameCardShareNO = "/editNameCardShareNO";
    /**
     * 发起者删除交换信息
     */
    public static final String deleteInitiateNameCardShare = "/deleteInitiateNameCardShare";
    /**
     * 接收者删除交换信息
     */
    public static final String deleteReceiveNameCardShare = "/deleteReceiveNameCardShare";
    /**
     * 查询附近的人
     */
    public static final String nearbyNameCard = "/nearbyNameCard";
    /**
     * 删除/恢复名片分类
     */
    public static final String delUserBusinessCardGroup = "/delUserBusinessCardGroup";
    /**
     * 移动名片分组
     */
    public static final String updateBusinessCard = "/updateBusinessCard";
    /**
     * 删除名片
     */
    public static final String deleteBusinessCard = "/deleteBusinessCard";
    /**
     * 查询名片详情
     */
    public static final String getOneUserBusinessCard = "/getOneUserBusinessCard";
    /**
     * 推荐/取消推荐名片
     */
    public static final String updateStateBusinessCard = "/updateStateBusinessCard";
    /**
     * 用户收藏的名片数
     */
    public static final String getUserBusinessCardCount = "/getUserBusinessCardCount";
    /**
     * 获得推荐列表
     */
    public static final String getAllStateBusinessCard = "/getAllStateBusinessCard";
    /**
     * 显示服务性商品的信息
     */
    public static final String getServiceProductInfo = "/getServiceProductInfo";
    /**
     * 他们都在换
     */
    public static final String GetTheyExchang = "/GetTheyExchang";
    /**
     * 体系内的会员列表
     */
    public static final String gettixiuser = "/User.aspx/gettixiuser";
    /**
     * 区域内的会员列表
     */
    public static final String getquyuuser = "/User.aspx/getquyuuser";
    /**
     * 申请移动创客的接口
     */
    public static final String upMak = "/upMak";
    // 网络电话回拨
    public static final String backCallPhone = "/backCallPhone";

    public static final String get_news = "get_news";
    public static final String update_news_Praise = "update_news_Praise";
    public static final String check_news_Praise = "check_news_Praise";
    public static final String check_comment_Praise = "check_comment_Praise";
    public static final String add_comment = "add_comment";
    public static final String get_comment_xml = "get_comment_xml";
    public static final String get_news_info = "get_news_info";
    public static final String update_comment_Praise = "update_comment_Praise";


    /**
     * 移动创客升级为城市经理
     */
    public static final String upgradeCityManager = "/upgradeCityManager";
    /**
     * 见习创客
     */
    public static final String jxck = "/upMak_jianxi";

    /**
     * 绑定qq微信
     */
    public final static String Binding_third_user = "/User.aspx/Binding_third_user";
    /**
     * 查询是否绑定
     */
    public final static String SL_Binding_third_user = "/User.aspx/SL_Binding_third_user";
    /**
     * 发起代付
     */
    public final static String in_Pay_for_another_order = "/User.aspx/in_Pay_for_another_order";
    /**
     * 查询是否已经代付
     */
    public final static String sl_Pay_for_another_order = "/User.aspx/sl_pay_for_another_order";

    /**
     * 红包豆
     */
    public final static String redbean = "/red_box.aspx";


    private ResponseStream response = null;
    private HttpUtils http = new HttpUtils(45000);

    Map<String, String> map;

    public Web(String url) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Log.e("requestURL", url);
        http.configTimeout(45000);
        try {
            response = http.sendSync(HttpMethod.POST, url);
        } catch (com.lidroid.xutils.exception.HttpException e) {
            e.printStackTrace();
        }
    }

    public Web() {

    }

    /*method 接口
     *
     *
     * param 参数
     *
     */
    public Web(String method, String param) {
        this(url, method, param);
    }

    public Web(String url, String method, String param) {
        this(url, method, param, "UTF-8");
    }

    public void WebRequset(String method, String call, String param) {
        Map<String, String> map = new HashMap<String, String>();
        if (!Util.isNull(param)) {
            String[] kv = param.split("&");
            for (String kvLine : kv) {
                if (Util.isNull(kvLine))
                    continue;
                String[] k_v = kvLine.split("=");
                String rk = "";
                String rv = "";
                if (2 == k_v.length) {
                    rk = k_v[0];
                    rv = k_v[1];
                } else
                    rk = k_v[0];
                if (Util.isNull(rv))
                    rv = "";
                map.put(rk, rv);
            }
        }
        init(url, method, call, map, "utf-8");
    }

    public Web(int news, String url, String method, String call, String param) {
        Map<String, String> map = new HashMap<String, String>();
        if (!Util.isNull(param)) {
            String[] kv = param.split("&");
            for (String kvLine : kv) {
                if (Util.isNull(kvLine))
                    continue;
                String[] k_v = kvLine.split("=");
                String rk = "";
                String rv = "";
                if (2 == k_v.length) {
                    rk = k_v[0];
                    rv = k_v[1];
                } else
                    rk = k_v[0];
                if (Util.isNull(rv))
                    rv = "";
                map.put(rk, rv);
            }
        }
        init(url, method, call, map, "utf-8");
    }

    public Web(String url, String method, String call, String param,
               String encoding) {
        Map<String, String> map = new HashMap<String, String>();
        if (!Util.isNull(param)) {
            String[] kv = param.split("&");
            for (String kvLine : kv) {
                if (Util.isNull(kvLine))
                    continue;
                String[] k_v = kvLine.split("=");
                String rk = "";
                String rv = "";
                if (2 == k_v.length) {
                    rk = k_v[0];
                    rv = k_v[1];
                } else
                    rk = k_v[0];
                if (Util.isNull(rv))
                    rv = "";
                map.put(rk, rv);
            }
        }
        init(url, method, map, encoding);
    }

	/*
     *
	 * url 地址
	 *
	 * method  接口
	 *
	 * parame  参数
	 *
	 * encoding 编码格式
	 *
	 */

    public void setRequest(String method, String param, String encoding) {
        if (map == null) {
            map = new HashMap<String, String>();
        }

        map.clear();
        if (!Util.isNull(param)) {
            String[] kv = param.split("&");
            for (String kvLine : kv) {

                if (Util.isNull(kvLine))
                    continue;
                String[] k_v = kvLine.split("=");
                String rk = "";
                String rv = "";
                if (2 == k_v.length) {
                    rk = k_v[0];
                    rv = k_v[1];
                } else
                    rk = k_v[0];
                if (Util.isNull(rv))
                    rv = "";
                map.put(rk, rv);
            }
        }
        init(url, method, map, encoding);
    }

    public Web(String url, String method, String param, String encoding) {
        Map<String, String> map = new HashMap<String, String>();
        if (!Util.isNull(param)) {
            String[] kv = param.split("&");
            for (String kvLine : kv) {

                if (Util.isNull(kvLine))
                    continue;
                String[] k_v = kvLine.split("=");
                String rk = "";
                String rv = "";
                if (2 == k_v.length) {
                    rk = k_v[0];
                    rv = k_v[1];
                } else
                    rk = k_v[0];
                if (Util.isNull(rv))
                    rv = "";
                map.put(rk, rv);
            }
        }
        init(url, method, map, encoding);
    }

    public Web(String method, Map<String, String> param) {
        init(method, param);
    }

    @SuppressLint("NewApi")
    private void init(String method, Map<String, String> param) {
        init(url, method, param);
    }

    @SuppressLint("NewApi")
    private void init(String url, String method, Map<String, String> param) {
        init(url, method, param, "UTF-8");
    }

    @SuppressLint("NewApi")
    private void init(String url, String method, String call,
                      Map<String, String> param, String encoding) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        RequestParams rp = new RequestParams(encoding);
        StringBuffer request = new StringBuffer();
        for (String key : param.keySet()) {
            rp.addBodyParameter(key, param.get(key));
            request.append(key + "=" + param.get(key) + "&");
        }
        Date curDate = new Date(System.currentTimeMillis());
        rp.addBodyParameter("USER_KEY", Util.getUSER_KEY(curDate));
        rp.addBodyParameter("USER_KEYPWD", Util.getUSER_KEYPWD(curDate));
        rp.addBodyParameter("v_v", Util.version);
        rp.addBodyParameter("source", "android");
        rp.addBodyParameter("call", call);
        request.append("&USER_KEY=" + Util.getUSER_KEY(curDate)
                + "&USER_KEYPWD=" + Util.getUSER_KEYPWD(curDate) + "&v_v="
                + Util.version + "&source=android" + "&call=" + call);
        String newUrl = url + method + "?" + request.toString();
        Log.e("requestURL", newUrl);
        http.configTimeout(45000);
        try {
            response = http.sendSync(HttpMethod.POST, url + method, rp);
            Log.e("返回信息", response.toString());
        } catch (com.lidroid.xutils.exception.HttpException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
    }

    @SuppressLint("NewApi")
    private void init(String url, String method, Map<String, String> param,
                      String encoding) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        RequestParams rp = new RequestParams(encoding);
        StringBuffer request = new StringBuffer();
        for (String key : param.keySet()) {
            rp.addBodyParameter(key, param.get(key));
            request.append(key + "=" + param.get(key) + "&");
        }
        User loginUser = UserData.getUser();
        if (null != loginUser && !Util.isNull(loginUser.getSessionId())) {
            if (!request.toString().contains("sessionId"))
                rp.addBodyParameter("sessionId", loginUser.getSessionId());
        }
        Date curDate = new Date(System.currentTimeMillis());
        rp.addBodyParameter("userKey", Util.getUSER_KEY(curDate));
        rp.addBodyParameter("userKeyPwd", Util.getUSER_KEYPWD(curDate));
        rp.addBodyParameter("v_v", Util.version);
        rp.addBodyParameter("sourcePhoneType", "android");
        rp.addBodyParameter("appName", "yunshang");
        request.append("&userKey=" + Util.getUSER_KEY(curDate) + "&userKeyPwd="
                + Util.getUSER_KEYPWD(curDate) + "&v_v=" + Util.version
                + "&sourcePhoneType=android");
        String newUrl = url + method + "?" + request.toString();
        Log.e("requestURL", newUrl);
        http.configTimeout(45000);
        try {
            response = http.sendSync(HttpMethod.POST, url + method, rp);
//			Log.e("返回信息", msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //第三方登录
    public static User thirdReDoLogin(String thirdLoginId, String thirdLoginType) {
        String lat = "";
        String lon = "";
        String province = "";
        String city = "";
        try {
            LocationModel locationModel = LocationModel.getLocationModel();
            if (!Util.isNull(locationModel.getLatitude())) {
                lat = locationModel.getLatitude() + "";
                lon = locationModel.getLongitude() + "";
                province = locationModel.getProvince();
                if (!Util.isNull(province)) {
                    province = Util.get(province);
                }
                city = locationModel.getCity();
                if (!Util.isNull(city)) {
                    city = Util.get(city);
                }
            }
        } catch (Exception e) {
            LogUtils.e("获取定位错误！");
        }
        String param = "";
        param = "thirdLoginId=" + thirdLoginId + "&thirdLoginType=" + thirdLoginType;
        Date curDate = new Date(System.currentTimeMillis());
        Web web = new Web(Web.newAPI + "/user.aspx?call=doLogin", "", param
                + "&lat=" + lat + "&lon=" + lon + "&province=" + province
                + "&city=" + city + "&USER_KEY=" + Util.getUSER_KEY(curDate)
                + "&USER_KEYPWD=" + Util.getUSER_KEYPWD(curDate));
        String string = web.getString();
        User user = null;
        if (!TextUtils.isEmpty(string)) {
            JSONObject json = JSON.parseObject(string);
            if (200 != json.getIntValue("code")) {
                Util.show(json.getString("message"), App.getContext());
                UserData.setUser(null);
                return null;
            }
            try {
                user = JSON.parseObject(string, User.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (user == null || TextUtils.isEmpty(user.getUserNo())) {
            return null;
        }
        if (UserData.getUser() != null) {
            user.setMd5Pwd(UserData.getUser().getMd5Pwd());
        } else {
            return null;
        }
        UserData.setUser(user);
        return user;
    }

    public static User reDoLogin(String userId, String md5Pwd) {
        String lat = "";
        String lon = "";
        String province = "";
        String city = "";
        try {
            LocationModel locationModel = LocationModel.getLocationModel();
            if (!Util.isNull(locationModel.getLatitude())) {
                lat = locationModel.getLatitude() + "";
                lon = locationModel.getLongitude() + "";
                province = locationModel.getProvince();
                if (!Util.isNull(province)) {
                    province = Util.get(province);
                }
                city = locationModel.getCity();
                if (!Util.isNull(city)) {
                    city = Util.get(city);
                }
            }
        } catch (Exception e) {
            LogUtils.e("获取定位错误！");
        }
        String param = "";
        param = "userId=" + userId + "&md5Pwd=" + md5Pwd;
        Date curDate = new Date(System.currentTimeMillis());
        Web web = new Web(Web.newAPI + "/user.aspx?call=doLogin", "", param
                + "&lat=" + lat + "&lon=" + lon + "&province=" + province
                + "&city=" + city + "&USER_KEY=" + Util.getUSER_KEY(curDate)
                + "&USER_KEYPWD=" + Util.getUSER_KEYPWD(curDate));
        String string = web.getString();
        User user = null;
        if (!TextUtils.isEmpty(string)) {
            JSONObject json = JSON.parseObject(string);
            if (200 != json.getIntValue("code")) {
                Util.show(json.getString("message"), App.getContext());
                UserData.setUser(null);
                return null;
            }
            try {
                user = JSON.parseObject(string, User.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (user == null || TextUtils.isEmpty(user.getUserNo())) {
            return null;
        }
        if (UserData.getUser() != null) {
            user.setMd5Pwd(UserData.getUser().getMd5Pwd());
        } else {
            return null;
        }
        UserData.setUser(user);
        return user;
    }

    public static User reDoLogin() {
        User user = UserData.getUser();
        if (null != user) {
            return reDoLogin(user.getUserId(), user.getMd5Pwd());
        }
        return null;
    }

    public <T> List<T> getList(java.lang.Class<T> c) {
        if (null != response) {
            Log.e("返回信息", response.toString());
            InputStream webIn = response.getBaseStream();
            if (null != webIn) {
                try {
                    return (List<T>) new ListHandle(c, "obj", webIn).getList();
                } catch (Exception eee) {
                    eee.printStackTrace();
                } finally {
                    try {
                        webIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        return new ArrayList<T>();
    }

    public <T> List<T> getList(java.lang.Class<T> c, String matchesNode) {
        if (null != response) {

            InputStream webIn = response.getBaseStream();
            if (null != webIn) {
                try {
                    return (List<T>) new ListHandle(c, matchesNode, webIn)
                            .getList();
                } catch (Exception eee) {
                    eee.printStackTrace();
                } finally {
                    try {
                        webIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new ArrayList<T>();
    }

    public <T> T getObject(java.lang.Class<T> c) {
        if (null != response) {

            InputStream webIn = response.getBaseStream();
            if (null != webIn) {
                try {
                    return (T) new ObjectHandle(c, webIn).getObject();
                } catch (Exception eee) {
                    eee.printStackTrace();
                } finally {
                    try {
                        webIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        return null;
    }

    public String getString() {
        StringBuffer plan = new StringBuffer(10240);
        try {
            if (response != null) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getBaseStream()));
                String line = null;
                while (null != (line = reader.readLine()))
                    plan.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return plan.toString();
    }

    private String getStringEncodingByGb2312() {
        StringBuffer plan = new StringBuffer(10240);
        if (response != null) {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getBaseStream()));
                String line = null;
                while (null != (line = reader.readLine()))
                    plan.append(line);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return plan.toString();
    }

    public InputStream getHtml() {
        if (response != null) {
            return response.getBaseStream();
        }
        return null;

    }

    public String getPlan() {
        String value = getString();
        // LogUtils.v(value);
        return value.replaceAll("<([^>]*)>", "").trim();
    }

    public String getPlanGb2312() {
        String value = getString();
        // LogUtils.v(value);
        return value.replaceAll("<([^>]*)>", "").trim();
    }

}
