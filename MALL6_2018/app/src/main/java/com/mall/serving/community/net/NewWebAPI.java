package com.mall.serving.community.net;

import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.util.Util;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class NewWebAPI {

    private static NewWebAPI api = new NewWebAPI();
    private static final String webApi = Web.newAPI;

    // app异常退出上传错误日志
    private static final String api_appException = "/AppException.aspx?call=AppException";

    // 得到登录信息
    private static final String api_getLoginInfo = "/User.aspx?call=getLoginInfo";
    // 找回交易密码
    public static final String forgetTradePassword = "/User.aspx?call=forgetTradePassword";

    // 修改签名
    private static final String api_publishSignature = "/Mood.aspx?call=publishSignature";
    // 得到最新签名
    private static final String api_getNewSignature = "/Mood.aspx?call=getNewSignature";
    // 得到附近的人
    private static final String getAllNearbyUser = "/Nearby.aspx?call=getAllNearbyUser";
    // 添加TAG
    private static final String addTag = "/Nearby.aspx?call=addTag";
    // 得到我的好友
    private static final String getMyFriend = "/Friend.aspx?call=getMyFriend";
    //得到关注
    private static final String getMyGuanzhu = "/Friend.aspx?call=getMyGuanzhu";
    // 添加好友
    private static final String addFriend = "/Friend.aspx?call=addFriend";
    // 查询我的好友
    private static final String searchMyFriend = "/Friend.aspx?call=searchMyFriend";
    // 查询商城好友
    private static final String searchFriend = "/Friend.aspx?call=searchFriend";
    // 删除好友
    private static final String deleteFriend = "/Friend.aspx?call=deleteFriend";
    // 我发出的好友请求
    private static final String getApplyNewFriend = "/Friend.aspx?call=getApplyNewFriend";
    // 清空我发出的好友请求
    private static final String clearApplyNewFriend = "/Friend.aspx?call=clearApplyNewFriend";
    // 清空我收到的好友请求
    private static final String clearVerificationFriend = "/Friend.aspx?call=clearVerificationFriend";
    // 获取好友申请列表
    private static final String getVerificationFriend = "/Friend.aspx?call=getVerificationFriend";
    // 同意或拒绝添加好友
    private static final String refusalOrAgreeFriend = "/Friend.aspx?call=refusalOrAgreeFriend";
    // 是否是我的好友
    private static final String isMyFriend = "/Friend.aspx?call=isMyFriend";
    // 得到云通讯子账户信息
    private static final String getYunTongXunInfo = "/VOIP.aspx?call=getYunTongXunInfo";
    // 通过对方userId获取对方信息
    private static final String getUserInfoDataByUserId = "/CommunityUser.aspx?call=getUserInfoDataByUserId";
    // 编辑社区资料
    private static final String updateMyData = "/CommunityUser.aspx?call=updateMyData";
    // 通过VoipAccount获取对方信息
    private static final String getUserDataByVoipAccount = "/CommunityUser.aspx?call=getUserDataByVoipAccount";
    // 得到随机电话会员
    private static final String getRandomPhone = "/VOIP.aspx?call=getRandomPhone";
    // 开启隐身模式
    private static final String startStealthMode = "/CommunityUser.aspx?call=startStealthMode";
    // 取消隐身模式
    private static final String cancelStealthMode = "/CommunityUser.aspx?call=cancelStealthMode";
    // 取消隐身模式
    private static final String isStealth = "/CommunityUser.aspx?call=isStealth";
    // 加入黑名单
    private static final String addBlacklist = "/CommunityUser.aspx?call=addBlacklist";
    // 取消黑名单
    private static final String cancelBlacklist = "/CommunityUser.aspx?call=cancelBlacklist";
    // 是否在黑名单中
    private static final String userInBlacklist = "/CommunityUser.aspx?call=userInBlacklist";
    // 黑名单列表
    private static final String getMyBlacklist = "/CommunityUser.aspx?call=getMyBlacklist";
    // 通过标签得到用户
    private static final String getUserInfoDataByUserTag = "/CommunityUser.aspx?call=getUserInfoDataByUserTag";
    // 用户鲜花排行榜
    private static final String userRanking = "/CommunityUser.aspx?call=userRanking";
    // 送花
    private static final String sendFlowers = "/Flowers.aspx?call=sendFlowers";

    // 得到相册列表
    private static final String getAlbums = "/Albums.aspx?call=getAlbums";
    // 创建相册
    private static final String createAlbums = "/Albums.aspx?call=createAlbums";
    // 删除相册
    private static final String deleteAlbums = "/Albums.aspx?call=deleteAlbums";
    // 修改相册
    private static final String updateAlbums = "/Albums.aspx?call=updateAlbums";
    // 得到相册下面的图片
    private static final String getAlbumsPhoto = "/Albums.aspx?call=getAlbumsPhoto";
    // 上传图片到相册里面
    private static final String uploadPhotoItem = "/Albums.aspx?call=uploadPhotoItem";
    // 删除相册里面的图片
    private static final String deletePhotoItem = "/Albums.aspx?call=deletePhotoItem";
    // 上传照片到照片墙
    private static final String uploadPhotoWall = "/PhotoWall.aspx?call=uploadPhotoWall";
    // 得到我上传到照片墙的照片
    private static final String getMyPhotoWall = "/PhotoWall.aspx?call=getMyPhotoWall";
    // 得到全部照片墙数据
    private static final String getAllPhotoWallData = "/PhotoWall.aspx?call=getAllPhotoWallData";
    // 删除我发到照片墙的照片
    private static final String deleteMyPhotoWall = "/PhotoWall.aspx?call=deleteMyPhotoWall";

    // 修改用户备注
    private static final String updateUserRemark = "/CommunityUser.aspx?call=updateUserRemark";
    // 举报用户
    private static final String userReport = "/Report.aspx?call=userReport";
    // 得到用户动态
    private static final String getMyFriendDynamic = "/Mood.aspx?call=getMyFriendDynamic";
    // 加入随机通话队列
    private static final String joinRandomPhoneQueue = "/VOIP.aspx?call=joinRandomPhoneQueue";
    // 离开随机通话队列
    private static final String cancelRandomPhoneQueue = "/VOIP.aspx?call=cancelRandomPhoneQueue";
    // 删除会员发布的心情
    private static final String api_deleteMood = "/Mood.aspx?call=deleteMood";
    // 搜索随机列表
    private static final String getSearchRandomPhone = "/VOIP.aspx?call=getSearchRandomPhone";
    // 发表话题
    private static final String publishTopic = "/VOIP.aspx?call=publishTopic";
    // 添加随机通话记录
    private static final String addCallInfo = "/VOIP.aspx?call=addCallInfo";
    // 得到话题
    private static final String getMyTopic = "/VOIP.aspx?call=getMyTopic";
    // 得到随机通话记录列表
    private static final String getMyCallInfo = "/VOIP.aspx?call=getMyCallInfo";
    // 删除通话记录
    private static final String deleteMyCallInfo = "/VOIP.aspx?call=deleteMyCallInfo";
    // 清空通话记录
    private static final String clearMyCallInfo = "/VOIP.aspx?call=clearMyCallInfo";

    // 发红包
    private static final String sendRed_Packets = "/user.aspx?call=sendRed_Packets";

    // 得到发送过的红包列表
    private static final String getSendRed_PacketsList = "/user.aspx?call=getSendRed_PacketsList";
    // 得到收到的红包列表
    private static final String getFromRed_Packets_InfoList = "/user.aspx?call=getFromRed_Packets_InfoList";
    // 红包详情
    private static final String getSendRed_Packets_InfoList = "/user.aspx?call=getSendRed_Packets_InfoList";
    // 登录接口
    private static final String api_doLogin = "/CommunityUser.aspx?call=doLogin";

    private NewWebAPI() {
    }

    public static NewWebAPI getNewInstance() {
        return api;
    }

    // 没参数的请求
    public void getWebRequest(String url, NewWebAPIRequestCallback callback) {
        getWebRequest(url, null, callback);
    }

    // 有参数的请求
    public void getWebRequest(String url, Map<String, String> map, NewWebAPIRequestCallback callback) {

        if (null == callback)
            callback = new WebRequestCallBack();
        final NewWebAPIRequestCallback callBack = callback;
        HttpUtils http = new HttpUtils(30000);
        RequestParams params = new RequestParams("UTF-8");
        params.addBodyParameter("v_v", Util.version);
        params.addBodyParameter("sourcePhoneType", "android");
        params.addBodyParameter("appName", "yunshang");
        Date curDate = new Date(System.currentTimeMillis());
        params.addBodyParameter("USER_KEY", com.mall.util.Util.getUSER_KEY(curDate));
        params.addBodyParameter("USER_KEYPWD", com.mall.util.Util.getUSER_KEYPWD(curDate));
        StringBuilder p = new StringBuilder("&v_v=" + Util.version + "&test=test");
        p.append("&USER_KEY=" + com.mall.util.Util.getUSER_KEY(curDate) + "&USER_KEYPWD=" + com.mall.util.Util.getUSER_KEYPWD(curDate)
                + "&sourcePhoneType=android_community");
        if (null != map) {
            for (String key : map.keySet()) {
                params.addBodyParameter(key, map.get(key));
                p.append("&" + key + "=" + map.get(key));
            }
        }
        LogUtils.e("请求数据：" + webApi + url + p.toString());
        http.send(HttpMethod.POST, webApi + url, params, new RequestCallBack<Object>() {
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                arg0.printStackTrace();
                callBack.fail(arg0);
                callBack.requestEnd();
            }

            @Override
            public void onSuccess(ResponseInfo<Object> arg0) {

                if (Util.isNull(arg0.result)) {

                } else {

                    callBack.success(arg0.result);
                }
                callBack.requestEnd();
            }

            @Override
            public void onCancelled() {
                super.onCancelled();
                callBack.fail(new Throwable("请求被取消"));
                callBack.requestEnd();
            }
        });
    }


    // 有参数的请求
    public void getWebRequest1(String url, Map<String, String> map, NewWebAPIRequestCallback callback) {

        if (null == callback)
            callback = new WebRequestCallBack();
        final NewWebAPIRequestCallback callBack = callback;
        HttpUtils http = new HttpUtils(30000);
        RequestParams params = new RequestParams("UTF-8");
        params.addBodyParameter("v_v", Util.version);
        params.addBodyParameter("sourcePhoneType", "android");
        params.addBodyParameter("appName", "yunshang");
        Date curDate = new Date(System.currentTimeMillis());
        params.addBodyParameter("USER_KEY", com.mall.util.Util.getUSER_KEY(curDate));
        params.addBodyParameter("USER_KEYPWD", com.mall.util.Util.getUSER_KEYPWD(curDate));
        StringBuilder p = new StringBuilder("&v_v=" + Util.version + "&test=test");
        p.append("&USER_KEY=" + com.mall.util.Util.getUSER_KEY(curDate) + "&USER_KEYPWD=" + com.mall.util.Util.getUSER_KEYPWD(curDate)
                + "&sourcePhoneType=android_community");
        if (null != map) {
            for (String key : map.keySet()) {
                params.addBodyParameter(key, map.get(key));
                p.append("&" + key + "=" + map.get(key));
            }
        }
//        String encode;
//        try {
//            encode = URLEncoder.encode(url, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            encode = url;
//            e.printStackTrace();
//        }
        LogUtils.e("请求数据：" + webApi + url + p.toString());
        http.send(HttpMethod.POST, webApi + url, params, new RequestCallBack<Object>() {
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                arg0.printStackTrace();
                callBack.fail(arg0);
                callBack.requestEnd();
            }

            @Override
            public void onSuccess(ResponseInfo<Object> arg0) {

                if (Util.isNull(arg0.result)) {

                } else {

                    callBack.success(arg0.result);
                }
                callBack.requestEnd();
            }

            @Override
            public void onCancelled() {
                super.onCancelled();
                callBack.fail(new Throwable("请求被取消"));
                callBack.requestEnd();
            }
        });
    }

    /**
     * 提交异常信息
     *
     * @param map
     * @param callback
     */
    public void appException(Map<String, String> map, WebRequestCallBack callback) {
        getWebRequest(api_appException, map, callback);
    }

    /**
     * 获取用户登录信息
     *
     * @param userId
     * @param      * @param callback
     */
    public void getLoginInfo(String userId, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        getWebRequest(api_getLoginInfo, param, callback);
    }

    /**
     * 修改用户签名（用于网络电话里面）
     */
    public void publishSignature(String userId, String md5Pwd, String message, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("message", message);
        getWebRequest(api_publishSignature, param, callback);
    }

    /**
     * 得到用户签名（用于网络电话里面）
     */
    public void getNewSignature(String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        getWebRequest(api_getNewSignature, param, callback);
    }

    /**
     * 获取附近的人的信息
     *
     * @param lat
     * @param lon
     * @param type
     * @param callback
     */
    public void getAllNearbyUser(String lat, String lon, String type, String sex, String city, String showLevel,
                                 String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("lat", lat);
        param.put("lon", lon);
        param.put("type", type);
        param.put("sex", sex);
        param.put("city", city);
        param.put("showLevel", showLevel);
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        getWebRequest(getAllNearbyUser, param, callback);

    }

    /**
     * 添加tag
     *
     * @param toUserId
     * @param tag
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void addTag(String toUserId, String tag, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("toUserId", toUserId);
        param.put("tag", tag);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(addTag, param, callback);

    }

    /**
     * 得到我的关注
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getMyGuanzhu(String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getMyGuanzhu, param, callback);

    }

    /**
     * 得到我的好友
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getMyFriend(String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getMyFriend, param, callback);

    }

    /**
     * 添加好友
     *
     * @param friendUserId
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void addFriend(String friendUserId, String userId, String md5pwd, WebRequestCallBack callback) {

        Map<String, String> param = new HashMap<String, String>();

        param.put("friendUserId", friendUserId);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(addFriend, param, callback);

    }

    /**
     * 查询我的好友
     *
     * @param friendUserId
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void searchMyFriend(String friendUserId, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("friendUserId", friendUserId);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(searchMyFriend, param, callback);

    }

    /**
     * 查询商城好友
     *
     * @param friendUserId
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void searchFriend(String friendUserId, String userId, String md5pwd, WebRequestCallBack callback) {

        Map<String, String> param = new HashMap<String, String>();

        param.put("friendUserId", friendUserId);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(searchFriend, param, callback);

    }

    /**
     * 删除好友
     *
     * @param friendUserId
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void deleteFriend(String friendUserId, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("friendUserId", friendUserId);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(deleteFriend, param, callback);

    }

    /**
     * 获取好友申请列表
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getVerificationFriend(String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getVerificationFriend, param, callback);

    }

    /**
     * 获取我发出的好友请求
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getApplyNewFriend(String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getApplyNewFriend, param, callback);

    }

    /**
     * 清空我发出的好友请求
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void clearApplyNewFriend(String toUserId, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);
        param.put("toUserId", toUserId);

        getWebRequest(clearApplyNewFriend, param, callback);

    }

    /**
     * 清空我收到的好友请求
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void clearVerificationFriend(String toUserId, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);
        param.put("toUserId", toUserId);

        getWebRequest(clearVerificationFriend, param, callback);

    }

    /**
     * 同意或拒绝添加好友
     *
     * @param id
     * @param type
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void refusalOrAgreeFriend(String id, String type, String userId, String md5pwd,
                                     WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("id", id);
        param.put("type", type);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(refusalOrAgreeFriend, param, callback);

    }

    /**
     * 是否是我的好友
     *
     * @param friendUserId
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void isMyFriend(String friendUserId, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("friendUserId", friendUserId);

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(isMyFriend, param, callback);

    }

    /**
     * 得到云通讯子账户信息
     *
     * @param
     * @param
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getYunTongXunInfo(String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getYunTongXunInfo, param, callback);

    }

    /**
     * 通过对方userId获取对方信息
     *
     * @param toUserId
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getUserInfoDataByUserId(String toUserId, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("toUserId", toUserId);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getUserInfoDataByUserId, param, callback);
    }

    /**
     * 编辑社区资料
     *
     * @param sex
     * @param birthday
     * @param emotion
     * @param nickName
     * @param sexuality
     * @param city
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void updateMyData(String sex, String birthday, String emotion, String nickName, String sexuality,
                             String city, String doing, String hasResources, String needResources, String ilike, String ihate,
                             String userId, String md5pwd, WebRequestCallBack callback) {

        Map<String, String> param = new HashMap<String, String>();

        param.put("sex", sex);
        param.put("birthday", birthday);
        param.put("emotion", emotion);
        param.put("nickName", nickName);
        param.put("sexuality", sexuality);
        param.put("city", city);
        param.put("doing", doing);
        param.put("hasResources", hasResources);
        param.put("needResources", needResources);
        param.put("ilike", ilike);
        param.put("ihate", ihate);

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(updateMyData, param, callback);

    }

    /**
     * 通过VoipAccount获取对方信息
     *
     * @param voipAccount
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getUserDataByVoipAccount(String voipAccount, String userId, String md5pwd,
                                         WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("voipAccount", voipAccount);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getUserDataByVoipAccount, param, callback);

    }

    /**
     * 送花
     *
     * @param toUserId
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void sendFlowers(String toUserId, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("toUserId", toUserId);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(sendFlowers, param, callback);
    }

    /**
     * 得到随机电话会员
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getRandomPhone(String userId, String md5pwd, String page, String size, String sex, String city,
                               String showLevel, String random, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        if (TextUtils.isEmpty(sex)) {
            sex = "全部";
        }
        if (TextUtils.isEmpty(city)) {
            city = "全部";
        }
        if (TextUtils.isEmpty(showLevel)) {
            showLevel = "全部会员";
        }
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);
        param.put("page", page);
        param.put("size", size);
        param.put("sex", sex);
        param.put("city", city);
        param.put("showLevel", showLevel);
        param.put("random", random);

        getWebRequest(getRandomPhone, param, callback);

    }

    /**
     * 开启隐身模式
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void startStealthMode(String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(startStealthMode, param, callback);

    }

    /**
     * 取消隐身模式
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void cancelStealthMode(String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(cancelStealthMode, param, callback);

    }

    /**
     * 取消隐身模式
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void isStealth(String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(isStealth, param, callback);

    }

    /**
     * 加入黑名单
     *
     * @param toUserId
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void addBlacklist(String toUserId, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("toUserId", toUserId);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(addBlacklist, param, callback);
    }

    /**
     * 取消黑名单
     *
     * @param toUserId
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void cancelBlacklist(String toUserId, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("toUserId", toUserId);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(cancelBlacklist, param, callback);
    }

    /**
     * 是否在黑名单中
     *
     * @param toUserId
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void userInBlacklist(String toUserId, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("toUserId", toUserId);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(userInBlacklist, param, callback);

    }

    /**
     * 黑名单列表
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getMyBlacklist(String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getMyBlacklist, param, callback);

    }

    /**
     * 通过标签得到用户
     *
     * @param tag
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getUserInfoDataByUserTag(String tag, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("tag", tag);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getUserInfoDataByUserTag, param, callback);

    }

    /**
     * 得到相册列表
     *
     * @param toUserId
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getAlbums(String toUserId, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("toUserId", toUserId);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getAlbums, param, callback);

    }

    /**
     * 创建相册
     *
     * @param name
     * @param picture
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void createAlbums(String name, String mode, String picture, String userId, String md5pwd,
                             WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("name", name);
        param.put("mode", mode);
        param.put("picture", picture);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(createAlbums, param, callback);

    }

    /**
     * 删除相册
     *
     * @param id
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void deleteAlbums(String id, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("id", id);

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(deleteAlbums, param, callback);

    }

    /**
     * 修改相册
     *
     * @param name
     * @param picture
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void updateAlbums(String name, String mode, String picture, String id, String userId, String md5pwd,
                             WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("name", name);
        param.put("mode", mode);
        param.put("picture", picture);
        param.put("id", id);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(updateAlbums, param, callback);

    }

    /**
     * 得到相册下面的图片
     *
     * @param id
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getAlbumsPhoto(String id, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("id", id);

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getAlbumsPhoto, param, callback);

    }

    /**
     * 上传图片到相册
     *
     * @param appId
     * @param name
     * @param picture
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void uploadPhotoItem(String appId, String name, String picture, String userId, String md5pwd,
                                WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("appId", appId);
        param.put("name", name);
        param.put("picture", picture);

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(uploadPhotoItem, param, callback);

    }

    /**
     * 删除相册图片
     *
     * @param id
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void deletePhotoItem(String id, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("id", id);

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(deletePhotoItem, param, callback);

    }

    /**
     * 上传图片到照片墙
     *
     * @param name
     * @param picture
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void uploadPhotoWall(String name, String picture, String userId, String md5pwd,
                                WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("name", name);
        param.put("picture", picture);

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(uploadPhotoWall, param, callback);

    }

    /**
     * 得到我上传到照片墙的照片
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getMyPhotoWall(String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getMyPhotoWall, param, callback);

    }

    /**
     * 得到全部照片墙数据
     *
     * @param page
     * @param size
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getAllPhotoWallData(String page, String size, String userId, String md5pwd,
                                    WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("page", page);
        param.put("size", size);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getAllPhotoWallData, param, callback);

    }

    /**
     * 删除我发到照片墙的照片
     *
     * @param id
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void deleteMyPhotoWall(String id, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("id", id);

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(deleteMyPhotoWall, param, callback);

    }

    /**
     * 修改用户备注
     *
     * @param toUserId
     * @param remark
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void updateUserRemark(String toUserId, String remark, String userId, String md5pwd,
                                 WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("toUserId", toUserId);
        param.put("remark", remark);

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(updateUserRemark, param, callback);

    }

    /**
     * 举报
     *
     * @param toUserId
     * @param type
     * @param content
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void userReport(String toUserId, String type, String content, String userId, String md5pwd,
                           WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();

        param.put("toUserId", toUserId);
        param.put("type", type);
        param.put("content", content);

        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(userReport, param, callback);

    }

    /**
     * 得到好友心情（用于互动社区）
     *
     * @param page
     * @param size
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void getMyFriendDynamic(String page, String size, String userId, String md5pwd,
                                   WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("page", page);
        param.put("size", size);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(getMyFriendDynamic, param, callback);

    }

    /**
     * 加入随机通话队列
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void joinRandomPhoneQueue(String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(joinRandomPhoneQueue, param, callback);

    }

    /**
     * 离开随机通话队列
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void cancelRandomPhoneQueue(String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);

        getWebRequest(cancelRandomPhoneQueue, param, callback);

    }

    /**
     * 用户鲜花排行榜
     *
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void userRanking(String page, String size, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("page", page);
        param.put("size", size);
        param.put("userId", userId);
        param.put("md5pwd", md5pwd);
        param.put("cache", "true");
        param.put("cacheKey", "rank" + page);
        param.put("cacheTime", "5");
        getWebRequest(userRanking, param, callback);

    }

    /**
     * 删除会员发布的
     *
     * @param
     * @param
     */
    public void deleteMood(String userId, String md5Pwd, String mid, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("mid", mid);
        getWebRequest(api_deleteMood, param, callback);
    }

    /**
     * 搜索随机会员
     *
     * @param
     * @param
     */
    public void getSearchRandomPhone(String key, String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("key", key);
        getWebRequest(getSearchRandomPhone, param, callback);
    }

    /**
     * 发表话题
     *
     * @param message
     * @param userId
     * @param md5Pwd
     * @param callback
     */
    public void publishTopic(String message, String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("message", message);
        getWebRequest(publishTopic, param, callback);
    }

    /**
     * 得到话题
     *
     * @param
     * @param userId
     * @param md5Pwd
     * @param callback
     */
    public void getMyTopic(String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("page", 1 + "");
        param.put("size", 1 + "");
        getWebRequest(getMyTopic, param, callback);
    }

    /**
     * 上传随机通话记录
     *
     * @param
     * @param userId
     * @param md5Pwd
     * @param callback
     */
    public void addCallInfo(String callUser, String callUserNo, String startDate, String endDate, String userId,
                            String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("callUser", callUser);
        param.put("callUserNo", callUserNo);
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        getWebRequest(addCallInfo, param, callback);
    }

    /**
     * 得到通话记录列表
     *
     * @param page
     * @param size
     * @param userId
     * @param md5Pwd
     * @param callback
     */
    public void getMyCallInfo(String page, String size, String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("page", page);
        param.put("size", size);

        getWebRequest(getMyCallInfo, param, callback);
    }

    public void deleteMyCallInfo(String id, String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("id", id);

        getWebRequest(deleteMyCallInfo, param, callback);
    }

    public void clearMyCallInfo(String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);

        getWebRequest(clearMyCallInfo, param, callback);
    }

    /**
     * 发红包
     *
     * @param times
     * @param state
     * @param type
     * @param money
     * @param remark
     * @param userId
     * @param md5Pwd
     * @param callback
     */
    public void sendRed_Packets(String times, String state, String type, String money, String remark, String tpwd,
                                String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("times", times);
        param.put("state", state);
        param.put("type", type);
        param.put("money", money);

        param.put("remark", remark);
        param.put("tpwd", tpwd);
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);

        getWebRequest(sendRed_Packets, param, callback);
    }

    /**
     * 发红包的列表
     *
     * @param page
     * @param size
     * @param userId
     * @param md5Pwd
     * @param callback
     */
    public void getSendRed_PacketsList(String page, String size, String userId, String md5Pwd,
                                       WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("page", page);
        param.put("size", size);
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);

        getWebRequest(getSendRed_PacketsList, param, callback);
    }

    public void getFromRed_Packets_InfoList(String page, String size, String userId, String md5Pwd,
                                            WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("page", page);
        param.put("size", size);
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);

        getWebRequest(getFromRed_Packets_InfoList, param, callback);
    }

    public void getSendRed_Packets_InfoList(String page, String size, String orderid, String userId, String md5Pwd,
                                            WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("page", page);
        param.put("size", size);
        param.put("orderid", orderid);
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);

        getWebRequest(getSendRed_Packets_InfoList, param, callback);
    }

    public static Map<String, String> getNewApiJson(String json) {

        Map<String, String> map = new HashMap<String, String>();

        try {
            String code = "";
            String message = "";
            String list = "";
            if (isGoodJson(json)) {
                JSONObject jObject = new JSONObject(json);
                if (jObject.has("code")) {
                    code = jObject.optString("code");
                }
                if (jObject.has("message")) {
                    message = jObject.optString("message");
                }

                if (jObject.has("list")) {
                    list = jObject.optString("list");
                }
            }

            map.put("code", code);
            map.put("message", message);
            map.put("list", list);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return map;

    }

    public static boolean isGoodJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return false;
        }
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {

            return false;
        }
    }


}
