package com.mall.net;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.lin.component.CustomProgressDialog;
import com.mall.newmodel.CollectionModel;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.App;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/20.
 */

public class UserFavorite {

    public interface CallBacke{
        void doback(String message);
    }

    public interface CollectionCallBacke{
        void doback(List<CollectionModel.ListBean> models);
    }

    public interface CallBackDelete{
        void doback(String message);
    }

    /**
     *
     * @param pid 商品/店铺id
     * @param ptype 0为商品 1为店铺
     * @param callBacke
     */
    public static void addFavorite(Context context,String pid, String ptype, final CallBacke callBacke) {
        final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(context, "加载中...");
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", UserData.getUser().getUserId());
        params.put("md5Pwd", UserData.getUser().getMd5Pwd());
        params.put("pid", pid);
        params.put("ptype", ptype);
        NewWebAPI.getNewInstance().getWebRequest("/user.aspx?call=Add_UserFavorite", params, new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                super.success(result);
                if (Util.isNull(result)) {
                    Util.show("网络异常！", App.getContext());
                    return;
                }
                com.alibaba.fastjson.JSONObject json = JSON.parseObject(result.toString());
                if (200 != json.getIntValue("code")) {
                    Util.show(json.getString("message"), App.getActivity());
                    return;
                }
                callBacke.doback(json.getString("message"));

            }

            @Override
            public void requestEnd() {
                super.requestEnd();
                cpd.cancel();
                cpd.dismiss();
            }

        });
    }

    /**
     *
     * @param ptype 0为商品 1为店铺 不传为全部
     * @param callBacke
     */
    public static void getFavorite(String ptype, final CollectionCallBacke callBacke) {
        final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(App.getActivity(), "加载中...");
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", UserData.getUser().getUserId());
        params.put("md5Pwd", UserData.getUser().getMd5Pwd());
        params.put("ptype", ptype);
        NewWebAPI.getNewInstance().getWebRequest("/user.aspx?call=Get_UserFavorite", params, new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                super.success(result);
                if (Util.isNull(result)) {
                    Util.show("网络异常！", App.getContext());
                    return;
                }
                Gson gson=new Gson();
                CollectionModel collectionModel=gson.fromJson(result.toString(),CollectionModel.class);
                List<CollectionModel.ListBean> listBeans=  collectionModel.getList();

                callBacke.doback(listBeans);

            }

            @Override
            public void requestEnd() {
                super.requestEnd();
                cpd.cancel();
                cpd.dismiss();
            }

        });
    }

    public static void deletFavorite(Context context,String pid,String ptype , final CallBackDelete callBacke) {
        final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(context, "加载中...");
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", UserData.getUser().getUserId());
        params.put("md5Pwd", UserData.getUser().getMd5Pwd());
        params.put("pid", pid);
        params.put("ptype", ptype);
        NewWebAPI.getNewInstance().getWebRequest("/user.aspx?call=Del_UserFavorite", params, new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                super.success(result);
                if (Util.isNull(result)) {
                    Util.show("网络异常！", App.getContext());
                    return;
                }
                com.alibaba.fastjson.JSONObject json = JSON.parseObject(result.toString());
                if (200 != json.getIntValue("code")) {
                    Util.show(json.getString("message"), App.getActivity());
                    return;
                }
                callBacke.doback(json.getString("message"));

            }

            @Override
            public void requestEnd() {
                super.requestEnd();
                cpd.cancel();
                cpd.dismiss();
            }

        });
    }
}
