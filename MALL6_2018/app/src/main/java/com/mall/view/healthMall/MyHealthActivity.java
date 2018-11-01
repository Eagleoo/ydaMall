package com.mall.view.healthMall;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.Base.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lin.component.CustomProgressDialog;
import com.mall.Bean.CkMall;
import com.mall.Bean.HealthMall;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.model.OrderOne;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.order.RefundOrder;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.RecyclerViewSpacesItemDecoration;
import com.mall.view.databinding.ActivityMyHealthBinding;
import com.mall.view.databinding.ItemHealthTitleBinding;
import com.mall.view.databinding.LayoutImageBinding;
import com.mall.view.databinding.R14itemBinding;
import com.mall.view.databinding.R15itemBinding;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyHealthActivity extends BaseActivity<ActivityMyHealthBinding> {


    @Override
    public int getContentViewId() {
        return R.layout.activity_my_health;
    }

    @Override
    public View getattachedview() {
        return null;
    }

    NewMyAdapter newMyAdapter;
    List<HealthMall.ListBeanX> listorder = new ArrayList<>();

    @Override
    protected void initView(ActivityMyHealthBinding mBinding) {
        HashMap<String, Integer> distance = new HashMap<String, Integer>();
        distance.put("bottom_decoration",10);
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        newMyAdapter = new NewMyAdapter(context, listorder);
        mBinding.toback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.xrecyle.setLayoutManager(new LinearLayoutManager(context));
        mBinding.xrecyle.setAdapter(newMyAdapter);
        mBinding.xrecyle.setPullRefreshEnabled(false);
        mBinding.xrecyle.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        mBinding.xrecyle.addItemDecoration(new RecyclerViewSpacesItemDecoration(distance));
        mBinding.xrecyle.setLoadingListener(new XRecyclerView.LoadingListener()

        {
            @Override
            public void onRefresh() {
                //refresh data here
                page = 1;
            }

            @Override
            public void onLoadMore() {
                // load more data here
//                getData();
                page++;
                getOrders();
            }
        });
    }

    int page = 1;


    public void getOrders() {
        User user = UserData.getUser();
        Date curDate = new Date(System.currentTimeMillis());
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        map.put("state", "4");
        map.put("page", "" + page);
        map.put("size", "999");
        map.put("type", "66");
        map.put("USER_KEYold", Util.getUSER_KEY(curDate));
        map.put("USER_KEYPWDold", Util.getUSER_KEYPWD(curDate));
        map.put("userKey", Util.getUSER_KEY(curDate));
        map.put("userKeyPwd", Util.getUSER_KEYPWD(curDate));
        NewWebAPI.getNewInstance().getWebRequest("/YdaOrder.aspx?call=getHealthOrder", map, new WebRequestCallBack() {

            @Override
            public void success(Object result) {
                super.success(result);
                if (null == result) {
                    Util.show("网络异常,请重试", MyHealthActivity.this);
                    return;
                }
                JSONObject jsons = JSON.parseObject(result.toString());
                if (200 != jsons.getIntValue("code")) {
                    Util.show("网络异常,请重试", MyHealthActivity.this);
                    return;
                }
                String arrlist = jsons.getString("order");
                List<HealthMall.ListBeanX> order_list = JSON.parseArray(arrlist, HealthMall.ListBeanX.class);
                //List<HealthMall.ListBeanX> order_list1=new ArrayList<>();
                if ((order_list.size() == 0)) {
//                    for (int i=0;i<10;i++){
//                        HealthMall.ListBeanX listBeanX=new HealthMall.ListBeanX();
//                        listBeanX.setDate("");
//                        listBeanX.setIntegral("8888");
//                        listBeanX.setUserid("张远航");
//                        order_list1.add(listBeanX);
//                    }
                    Util.show("暂无订单信息!", MyHealthActivity.this);
                    return;
                }
                if (page == 1) {
                    listorder.clear();
                }
                listorder.addAll(order_list);


            }

            @Override
            public void requestEnd() {
                super.requestEnd();
                mBinding.xrecyle.loadMoreComplete();
            }

        });
    }

    @Override
    protected void initData(ActivityMyHealthBinding mBinding) {
        getOrders();

    }

    private void listaddView(View view) {
        ScreenAdapterTools.getInstance().loadView((ViewGroup) view);
        mBinding.xrecyle.addHeaderView(view);
    }

    class NewMyAdapter extends BaseRecycleAdapter<HealthMall.ListBeanX> {

        protected NewMyAdapter(Context context, List<HealthMall.ListBeanX> list) {
            super(context, list);
        }


        @Override
        public void setIteamData(ViewDataBinding mBinding, List<HealthMall.ListBeanX> list, int position) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            Date date = new Date(System.currentTimeMillis());
            ItemHealthTitleBinding itemHealthTitleBinding = (ItemHealthTitleBinding) mBinding;
            HealthMall.ListBeanX listBeanX = list.get(position);
            String username=listBeanX.getUserid();
            String replace=username.substring(0,username.length()-2)+"**";
            //Util.show("截取"+replace);
            itemHealthTitleBinding.tv1.setText(replace);
            itemHealthTitleBinding.tv2.setText(listBeanX.getIntegral());
            itemHealthTitleBinding.mTime.setText(simpleDateFormat.format(date));

        }

        @Override
        public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
            View view = null;

            view = LayoutInflater.from(mContext).inflate(R.layout.item_health_title, parent, false);


            ScreenAdapterTools.getInstance().loadView((ViewGroup) view);
            return DataBindingUtil.bind(view);
        }

        @Override
        public int setShowRule(int position) {
            int type = 0;

            return type;
        }
    }
}
