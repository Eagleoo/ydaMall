package com.mall.net;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;

import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.mall.model.User;
import com.mall.util.UserData;
import com.mall.util.Util;

public final class NewWebAPI {
    private static NewWebAPI api = new NewWebAPI();
    private static final String webApi = Web.newAPI;
    // 删除相册
    private static final String deleteAlbums = "/Albums.aspx?call=deleteAlbums";

    private NewWebAPI() {
    }

    public static NewWebAPI getNewInstance() {
        return api;
    }
    // #start 新API的方法

    // app异常退出上传错误日志
    private static final String api_appException = "/AppException.aspx?call=AppException";

    // 找回交易密码
    public static final String forgetTradePassword = "/User.aspx?call=forgetTradePassword";
    // 取消用户的屏蔽
    private static final String api_showException = "/AppException.aspx?call=showException";
    // 取消用户的屏蔽
    private static final String api_quitShieldUser = "/VOIP.aspx?call=quitShieldUser";
    // 修改真实姓名
    private static final String api_updateRealName = "/User.aspx?call=updateRealName";
    // 得到登录信息
    private static final String api_getLoginInfo = "/User.aspx?call=getLoginInfo";
    // 完善用户信息（注册成功后）
    private static final String api_updateUserInfo = "/User.aspx?call=updateUserInfo";
    // 修改签名
    private static final String api_publishSignature = "/Mood.aspx?call=publishSignature";
    // 得到最新签名
    private static final String api_getNewSignature = "/Mood.aspx?call=getNewSignature";
    // 删除会员发布的心情
    private static final String api_deleteMood = "/Mood.aspx?call=deleteMood";
    // 得到我未读评论
    private static final String api_getUnReadMoodComment = "/Mood.aspx?call=getUnReadMoodComment";
    // 得到我未读评论
    private static final String api_getUnReadMoodCommentCount = "/Mood.aspx?call=getUnReadMoodCommentCount";
    // 得到我的推送信息
    private static final String api_getAllPush = "/Push.aspx?call=getAllPush";
    // 得到未读推送信息
    private static final String api_get_Push_replay_noread = "/Push.aspx?call=Get_Push_replay_noread";
    // 修改推送为已读
    private static final String api_readPush = "/Push.aspx?call=readPush";
    // 得到推荐联盟商家的数量
    private static final String api_getAllAllianceCount = "/Alliance.aspx?call=getAllAllianceCount";
    // 得到需要我审批的业务
    private static final String api_getMyAdudit = "/Business.aspx?call=getMyAdudit";
    // 审批我的业务
    private static final String api_aduditBusiness = "/Business.aspx?call=aduditBusiness";
    // 创业空间升级城市总监
    private static final String api_upgradeCity = "/Business.aspx?call=upgradeCity";
    // 得到商品评论
    private static final String api_productComment = "/Product.aspx?call=getCommentByProductId";
    // 得到推荐会员
    private static final String api_getMyInviter = "/user.aspx?call=getMyInviter";
    // 得到招商会员
    private static final String api_getMyMerchants = "/user.aspx?call=getMyMerchants";
    // 得到全部会员
    private static final String api_getAllMyUser = "/user.aspx?call=getAllMyUser";
    // 消息推送
    private static final String api_push = "/push.aspx?call=pushMessage";
    // 获取购物卡账户
    private static final String api_getStoAccountType = "/Account.aspx?call=getStoAccountType";
    // 获取购物卡账户
    private static final String api_getLMSJADV = "/Alliance.aspx?call=getLMSJADV";
    // 回复推送消息
    private static final String api_ReplyPush = "/Push.aspx?call=ReplyPush";
    // 分页获取某个推送消息下面所有的回复
    private static final String api_getAllReplyPush = "/Push.aspx?call=getAllReplyPush";
    // 得到我推送过的消息
    private static final String api_getAllPushSender = "/Push.aspx?call=getAllPushSender";
    // 得到我发送的某个消息的用户列表和回复统计
    private static final String api_getAllPushUserSender = "/Push.aspx?call=getAllPushUserSender";
    // 删除推送消息回复
    private static final String api_deleteReplyPush = "/Push.aspx?call=DeleteReplyPush";
    /**
     * 体系内的会员列表
     */
    private static final String tiXiUser = "/User.aspx?call=gettixiuser";
    // 运营中心开通业务
    private static final String api_doCreateY = "/Business.aspx?call=doCreateY";
    // 运营中心区域内业务
    private static final String api_myAreaBus = "/User.aspx?call=getquyuuser";
    // 登录接口
    private static final String api_doLogin = "/user.aspx?call=doLogin";
    // 申请城市总监
    private static final String api_requestCityDirector = "/Business.aspx?call=requestCityZongJian";
    // 得到摇一摇历史消息
    private static final String api_get_shake_old_list = "/user.aspx?call=Get_Old_Shake_List";
    // 得到摇一摇要到的人
    private static final String api_get_shake_list = "/user.aspx?call=Get_Shake_List";
    // 保存游戏记录
    public static final String api_addGameMax = "/GameFraction.aspx?call=addGameMax";
    // 得到游戏排行榜
    public static final String api_getAllGameRanking = "/GameFraction.aspx?call=getAllGameRanking";
    // #end

    // 没参数的请求
    public void getWebRequest(String url, NewWebAPIRequestCallback callback) {
        getWebRequest(url, null, callback);
    }

    // 有参数的请求
    public void getWebRequest(final String url, Map<String, String> map, NewWebAPIRequestCallback callback) {
        if (null == callback)
            callback = new WebRequestCallBack();
        final NewWebAPIRequestCallback callBack = callback;
        HttpUtils http = new HttpUtils(60000);
        RequestParams params = new RequestParams("UTF-8");
        params.addBodyParameter("v_v", Util.version);
        params.addBodyParameter("sourcePhoneType", "android");
        params.addBodyParameter("appName", "yunshang");
        Date curDate = new Date(System.currentTimeMillis());
        params.addBodyParameter("USER_KEY", Util.getUSER_KEY(curDate));
        params.addBodyParameter("USER_KEYPWD", Util.getUSER_KEYPWD(curDate));
        if (null == map) {
            map = new HashMap<String, String>();
        }
        User user = UserData.getUser();
        if (null != user) {
            if (!map.containsKey("sessionId") && !Util.isNull(user.getSessionId())) {
                map.put("sessionId", user.getSessionId());
            }
        }
        params.addHeader("yda360App", "java");
        final StringBuilder p = new StringBuilder("&v_v=" + Util.version);
        p.append("&USER_KEY=" + Util.getUSER_KEY(curDate) + "&USER_KEYPWD=" + Util.getUSER_KEYPWD(curDate));
        if (null != map) {
            for (String key : map.keySet()) {
                params.addBodyParameter(key, map.get(key));
                p.append("&" + key + "=" + map.get(key));
            }
        }
        final String newWebAPI = ("/MMPay".equals(url) ? webApi.replaceFirst("/api", ":8080/WeiXinPay/api") : webApi);
        final String strs = newWebAPI + url + p.toString();
        LogUtils.e("请求数据：" + strs);
        http.send(HttpMethod.POST, newWebAPI + url, params, new RequestCallBack<Object>() {
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                arg0.printStackTrace();
                onSuccess(null);
                callBack.requestEnd();
            }

            @Override
            public void onSuccess(ResponseInfo<Object> arg0) {

                if (null == arg0 || null == arg0.result) {
                    LogUtils.e("接口繁忙            " + newWebAPI + url);
                }
                try {

                    callBack.success(arg0 == null ? null : arg0.result);
                    Log.e("返回信息", newWebAPI + url + arg0.result + "");
                } catch (JSONException e) {
                    LogUtils.e("JSON解析错误：" + arg0.result, e);
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e("异常", e.toString());
                    Log.e("返回信息为空", "arg0.result" + strs);
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


    public void getWebRequest(final String newWebAPI, final String url, Map<String, String> map, NewWebAPIRequestCallback callback) {
        if (null == callback)
            callback = new WebRequestCallBack();
        final NewWebAPIRequestCallback callBack = callback;
        HttpUtils http = new HttpUtils(60000);
        RequestParams params = new RequestParams("UTF-8");
        params.addBodyParameter("v_v", Util.version);
        params.addBodyParameter("sourcePhoneType", "android");
        params.addBodyParameter("appName", "yunshang");
        Date curDate = new Date(System.currentTimeMillis());
        params.addBodyParameter("USER_KEY", Util.getUSER_KEY(curDate));
        params.addBodyParameter("USER_KEYPWD", Util.getUSER_KEYPWD(curDate));
        if (null == map) {
            map = new HashMap<String, String>();
        }
        User user = UserData.getUser();
        if (null != user) {
            if (!map.containsKey("sessionId") && !Util.isNull(user.getSessionId())) {
                map.put("sessionId", user.getSessionId());
            }
        }
        params.addHeader("yda360App", "java");
        StringBuilder p = new StringBuilder("&v_v=" + Util.version);
        p.append("&USER_KEY=" + Util.getUSER_KEY(curDate) + "&USER_KEYPWD=" + Util.getUSER_KEYPWD(curDate));
        if (null != map) {
            for (String key : map.keySet()) {
                params.addBodyParameter(key, map.get(key));
                p.append("&" + key + "=" + map.get(key));
            }
        }
        final String strs = newWebAPI + url + p.toString();
        LogUtils.e("请求数据：" + strs);
        http.send(HttpMethod.POST, newWebAPI + url, params, new RequestCallBack<Object>() {
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                arg0.printStackTrace();
                onSuccess(null);
                callBack.requestEnd();
            }

            @Override
            public void onSuccess(ResponseInfo<Object> arg0) {

                if (null == arg0 || null == arg0.result) {
                    LogUtils.e("接口繁忙            " + newWebAPI + url);
                }
                try {

                    callBack.success(arg0 == null ? null : arg0.result);
                    Log.e("返回信息", newWebAPI + url + arg0.result + "");
                } catch (JSONException e) {
                    LogUtils.e("JSON解析错误：" + arg0.result, e);
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e("异常", e.toString());
                    Log.e("返回信息为空", "arg0.result"+strs);
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
     * 取消用户屏蔽（用于网络电话消息里面）
     */
    public void quitShieldUser(String userId, String md5Pwd, String quitUserId, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("quitUserId", quitUserId);
        getWebRequest(api_quitShieldUser, param, callback);
    }

    /**
     * 修改用户真实姓名（用于网络电话里面）
     */
    public void updateRealName(String userId, String md5Pwd, String realName, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("realName", realName);
        getWebRequest(api_updateRealName, param, callback);
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
     * 获取上传的异常信息
     *
     * @param page
     * @param size
     */
    public void showException(int page, int size, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("page", page + "");
        param.put("size", size + "");
        getWebRequest(api_showException, param, callback);
    }

    /**
     * 删除会员发布的
     *
     * @param page
     * @param size
     */
    public void deleteMood(String userId, String md5Pwd, String mid, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("mid", mid);
        getWebRequest(api_deleteMood, param, callback);
    }

    /**
     * 得到我接收到的的推送信息
     *
     * @param userId
     * @param md5Pwd
     */
    public void getAllPush(int page, int size, String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("page", page + "");
        param.put("size", size + "");
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        getWebRequest(api_getAllPush, param, callback);
    }

    /**
     * 得到我未读的推送信息
     *
     * @param userId
     * @param md5Pwd
     */
    public void Get_Push_replay_noread(String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        getWebRequest(api_get_Push_replay_noread, param, callback);
    }

    /**
     * 得到我所有推送过的推送信息
     *
     * @param userId
     * @param md5Pwd
     */
    public void getAllPushSender(int page, int size, String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("page", page + "");
        param.put("size", size + "");
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        getWebRequest(api_getAllPushSender, param, callback);
    }

    /**
     * 修改推送为已读
     *
     * @param userId
     * @param md5Pwd
     * @param callback
     */
    public void readPush(String userId, String md5Pwd, String mid, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("mid", mid);
        getWebRequest(api_readPush, param, callback);
    }

    /**
     * 获取用户登录信息
     *
     * @param userId
     * @param mid
     * @param callback
     */
    public void getLoginInfo(String userId, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        getWebRequest(api_getLoginInfo, param, callback);
    }

    /**
     * 获取我的未读评论
     *
     * @param userId
     * @param mid
     * @param callback
     */
    public void getUnReadMoodComment(String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        getWebRequest(api_getUnReadMoodComment, param, callback);
    }

    /**
     * 获取我的未读评论
     *
     * @param userId
     * @param
     * @param callback
     */
    public void getUnReadMoodCommentCount(String userId, String md5Pwd, WebRequestCallBack callback) {
        Log.e("TAG", "测试");
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        getWebRequest(api_getUnReadMoodCommentCount, param, callback);
    }

    /**
     * 获取当前城市的推荐联盟商家的数量
     *
     * @param city
     * @param callback
     */
    public void getAllAllianceCount(String city, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("city", city);
        getWebRequest(api_getAllAllianceCount, param, callback);
    }

    /**
     * 完善用户信息（用于注册成功后）
     */
    public void updateUserInfo(String userId, String md5Pwd, String area, String zoneid, String phone, String code,
                               String inviter, String realName, String idNo, String sex, String overseas, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("area", area);
        param.put("zoneid", zoneid);
        param.put("code", code);
        param.put("phone", phone);
        param.put("inviter", inviter);
        param.put("realName", realName);
        param.put("overseas", overseas);
        param.put("sex", sex);
        if (overseas.equals("1")) {
            param.put("passport", idNo);
        } else {
            param.put("idNo", idNo);
        }


        getWebRequest(api_updateUserInfo, param, callback);
    }

    /*
     * 得到商品评论
     */
    public void getProductComment(String pid, int page, int size, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("pid", pid);
        param.put("page", page + "");
        param.put("size", size + "");
        getWebRequest(api_productComment, param, callback);
    }

    /**
     * 得到渠道代理可以审批的业务
     *
     * @param userId
     * @param md5Pwd
     * @param page
     * @param size
     * @param callback
     */
    public void getMyAdudit(String userId, String md5Pwd, int page, int size, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("page", page + "");
        param.put("size", size + "");
        getWebRequest(api_getMyAdudit, param, callback);
    }

    /**
     * 创业空间升级城市总监
     */
    public void upgradeCity(String userId, String md5Pwd, String payType, String twoPwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("payType", payType);
        param.put("twoPwd", twoPwd);
        getWebRequest(api_upgradeCity, param, callback);
    }

    /**
     * 渠道代理审批开通业务
     *
     * @param userId
     * @param md5Pwd
     * @param page
     * @param size
     * @param callback
     */
    public void aduditBusiness(String userId, String md5Pwd, String applyId, String audioType, String twoPwd,
                               String remark, String openOrBack, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("applyId", applyId);
        param.put("audioType", audioType);
        param.put("twoPwd", twoPwd);
        param.put("remark", remark);
        param.put("openOrBack", openOrBack);
        getWebRequest(api_aduditBusiness, param, callback);
    }

    // /**
    // * 获取用户相册
    // * @param userId
    // * @param page
    // * @param size
    // * @param callback
    // */
    // public void getAlbumPath(String userId,int page,int
    // pagesize,WebRequestCallBack callback){
    // Map<String, String> param = new HashMap<String, String>();
    // param.put("userId", userId);
    // param.put("page", page+"");
    // param.put("pagesize", pagesize+"");
    // getWebRequest(api_aduditBusiness, param, callback);
    // }
    //
    // /**
    // * 获取网上办公室文章
    // * @param userId
    // * @param page
    // * @param size
    // * @param callback
    // */
    // public void getShopOfficeArticle(String userId,int page,int
    // pagesize,WebRequestCallBack callback){
    // Map<String,String> param=new HashMap<String, String>();
    // param.put("userId", userId);
    // param.put("page", page+"");
    // param.put("pagesize", pagesize+"");
    // getWebRequest(api_aduditBusiness, param,callback);
    // }

    /**
     * 获取推荐会员
     *
     * @param userId
     * @param md5Pwd
     * @param page
     * @param pagesize
     * @param callback
     */
    public void getMyInviter(String userId, String md5Pwd, int page, int pagesize, String orderby, String ascOrDesc,
                             WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("page", page + "");
        param.put("size", pagesize + "");
        param.put("orderField", orderby);
        param.put("ascOrDesc", ascOrDesc);
        getWebRequest(api_getMyInviter, param, callback);
    }

    /**
     * 获取招商会员
     *
     * @param userId
     * @param md5Pwd
     * @param page
     * @param pagesize
     * @param callback
     */
    public void getMyMerchants(String userId, String md5Pwd, int page, int pagesize, String orderby, String ascOrDesc,
                               WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("page", page + "");
        param.put("size", pagesize + "");
        param.put("orderField", orderby);
        param.put("ascOrDesc", ascOrDesc);
        getWebRequest(api_getMyMerchants, param, callback);
    }

    /**
     * 获取全部会员
     *
     * @param userId
     * @param md5Pwd
     * @param page
     * @param pagesize
     * @param callback
     */
    public void getAllMyUser(String userId, String md5Pwd, int page, int pagesize, String orderby, String ascOrDesc,
                             WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        param.put("page", page + "");
        param.put("size", pagesize + "");
        param.put("orderField", orderby);
        param.put("ascOrDesc", ascOrDesc);
        getWebRequest(api_getAllMyUser, param, callback);
    }

    /**
     * 消息推送
     *
     * @param merchants
     * @param inviter
     * @param all
     * @param persons
     * @param content
     * @param callback
     */
    public void pushMessage(String merchants, String inviter, String all, String person, String content, String userId,
                            String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("merchants", merchants);
        param.put("inviter", inviter);
        param.put("all", all);
        param.put("person", person + "");
        param.put("content", content + "");
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        getWebRequest(api_push, param, callback);
    }

    /**
     * 获取购物卡类型
     *
     * @param userId
     * @param md5Pwd
     * @param callback
     */
    public void getStoAccountType(String userId, String md5Pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        getWebRequest(api_getStoAccountType, param, callback);
    }

    /**
     * 得到联盟商家广告图
     *
     * @param callback
     */
    public void getLMSJADV(WebRequestCallBack callback) {
        getWebRequest(api_getLMSJADV, callback);
    }

    /**
     * @param mid      用户收到的每条消息对应的Id
     * @param pid      上一次回复的Id，无则传0
     * @param info
     * @param userId
     * @param md5Pwd
     * @param callback
     */
    public void ReplyPushMessage(String mid, String pid, String info, String userId, String md5Pwd,
                                 WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("mid", mid);
        param.put("pid", pid);
        param.put("info", info);
        param.put("userId", userId);
        param.put("md5Pwd", md5Pwd);
        getWebRequest(api_ReplyPush, param, callback);
    }

    /**
     * 得到某个推送消息下的回复
     *
     * @param pid
     * @param page
     * @param size
     * @param callback
     */
    public void getReplyMessage(String mid, int page, int size, String userid, String md5pwd,
                                WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("mid", mid);
        param.put("page", page + "");
        param.put("size", size + "");
        param.put("userId", userid);
        param.put("md5Pwd", md5pwd);
        getWebRequest(api_getAllReplyPush, param, callback);
    }

    /**
     * 得到我发送的某个消息的用户列表和回复统计
     *
     * @param page
     * @param size
     * @param userid
     * @param md5pwd
     * @param callback
     */
    public void getAllPushUserSender(int page, int size, String userid, String md5pwd, String id,
                                     WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("page", page + "");
        param.put("size", size + "");
        param.put("userId", userid);
        param.put("md5Pwd", md5pwd);
        param.put("id", id);
        getWebRequest(api_getAllPushUserSender, param, callback);
    }

    /**
     * 删除推送消息回复
     *
     * @param id
     * @param userId
     * @param md5pwd
     * @param callback
     */
    public void DeleteReplyPush(String id, String userId, String md5pwd, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("md5Pwd", md5pwd);
        param.put("id", id);
        getWebRequest(api_deleteReplyPush, param, callback);
    }

    /**
     * 微信支付
     *
     * @param callback
     */
    public void mmPay(String orderId, String orderTitle, double money, WebRequestCallBack callback) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("orderId", orderId);
        param.put("orderTitle", orderTitle);
        param.put("money", money + "");
        getWebRequest("/MMPay", param, callback);
    }

    /**
     * 体系内会员列表
     */
    public void getTiXiUser(String userId, String md5Pwd, String page, String size, String search, WebRequestCallBack callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("md5Pwd", md5Pwd);
        map.put("page", page);
        map.put("size", size);
        map.put("search", search);
        getWebRequest(tiXiUser, map, callBack);
    }

    /**
     * 区域内会员列表
     */
    public void myAreaBus(String userId, String md5Pwd, String page, String size, WebRequestCallBack callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("md5Pwd", md5Pwd);
        map.put("page", page);
        map.put("size", size);
        getWebRequest(api_myAreaBus, map, callBack);
    }

    /**
     * 运营中心开通城市总监
     *
     * @param userId
     * @param md5Pwd
     * @param twoPwd
     * @param id
     * @param callBack
     */
    public void doCreateY(String userId, String md5Pwd, String twoPwd, String id, WebRequestCallBack callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("md5Pwd", md5Pwd);
        map.put("twoPwd", twoPwd);
        map.put("id", id);
        getWebRequest(api_doCreateY, map, callBack);
    }

    /**
     * @param userId
     * @param md5Pwd
     * @param lat
     * @param lon
     * @param province
     * @param city
     * @param thirdLoginId   第三方登录ID
     * @param thirdLoginType 第三方登录类型
     * @param callBack
     */
    public void doLogin(String userId, String md5Pwd, String lat, String lon, String province, String city,
                        String thirdLoginId, String thirdLoginType, String sessionId, WebRequestCallBack callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("md5Pwd", md5Pwd);
        map.put("lat", lat);
        map.put("lon", lon);
        map.put("province", province);
        map.put("city", city);
        map.put("thirdLoginId", thirdLoginId);
        map.put("thirdLoginType", thirdLoginType);
        map.put("sessionId", sessionId);
        getWebRequest(api_doLogin, map, callBack);
    }

    /**
     * @param mobile
     * @param code
     * @param imgcode
     * @param callBack
     */
    public void doLoginPhone(String mobile, String code, String imgcode, WebRequestCallBack callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", mobile);
        map.put("code", code);
        map.put("imgcode", imgcode);
        getWebRequest(api_doLogin, map, callBack);
    }

    /**
     * 申请城市总监
     *
     * @param userId
     * @param md5Pwd
     * @param zsdw
     * @param requestType
     * @param payType
     * @param callBack
     */
    public void requestCityDirector(String userId, String md5Pwd, String twoPwd, String zsdw, String requestType,
                                    String payType, WebRequestCallBack callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("md5Pwd", md5Pwd);
        map.put("twoPwd", twoPwd);
        map.put("zsdw", zsdw);
        map.put("requestType", requestType);
        map.put("payType", payType);
        getWebRequest(api_requestCityDirector, map, callBack);
    }

    /**
     * 得到摇一摇历史记录
     *
     * @param userId
     * @param md5Pwd
     * @param page
     * @param size
     * @param type
     * @param callBack
     */
    public void GetOldShakeList(String userId, String md5Pwd, int page, int size, String type,
                                WebRequestCallBack callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("md5Pwd", md5Pwd);
        map.put("page", page + "");
        map.put("size", size + "");
        map.put("type", type);
        getWebRequest(api_get_shake_old_list, map, callBack);
    }

    /**
     * 得到摇到的人
     *
     * @param userId
     * @param md5Pwd
     * @param type
     * @param x
     * @param y
     * @param remark
     * @param callBack
     */
    public void GetShakeList(String userId, String md5Pwd, String type, String x, String y, String remark,
                             WebRequestCallBack callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("md5Pwd", md5Pwd);
        map.put("type", type);
        map.put("x", x);
        map.put("y", y);
        map.put("remark", remark);
        getWebRequest(api_get_shake_list, map, callBack);
    }

    /**
     * 保存游戏记录（最大值）
     *
     * @param remark
     * @param gameType
     * @param Fraction
     * @param userId
     * @param md5Pwd
     * @param callBack
     */
    public void addGameMax(String remark, String gameType, String Fraction, String userId, String md5Pwd,
                           WebRequestCallBack callBack) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("remark", remark);
        map.put("gameType", gameType);
        map.put("Fraction", Fraction);
        map.put("userId", userId);
        map.put("md5Pwd", md5Pwd);
        getWebRequest(api_addGameMax, map, callBack);
    }

    public void getGameRanking(int curpage, int pagesize, WebRequestCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("curpage", curpage + "");
        map.put("pagesize", pagesize + "");
        getWebRequest(api_getAllGameRanking, map, callback);
    }

    public void forgetTradePassword(String userId, String md5Pwd, String phone, WebRequestCallBack callback) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", userId);
        map.put("md5Pwd", md5Pwd);
        map.put("phone", phone);
        getWebRequest(forgetTradePassword, map, callback);
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
}
