package com.mall.view.healthMall;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.Base.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mall.Bean.CkMall;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.model.PopUpAds;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.ProductDeatilFream;
import com.mall.view.ProxySiteFrame;
import com.mall.view.R;
import com.mall.view.RedEnvelopesPackage.RedEnvelopeRechargeActivity;
import com.mall.view.ShopAddressManagerFrame;
import com.mall.view.ShopCarFrame;
import com.mall.view.carMall.CarManageActivity;
import com.mall.view.carMall.CarShopActivity;
import com.mall.view.carMall.MyOrderActivity;
import com.mall.view.databinding.ActivityCarshopBinding;
import com.mall.view.databinding.ActivityCarshopTopbannerBinding;
import com.mall.view.databinding.ActivityHealthShopBinding;
import com.mall.view.databinding.LayoutHealthsortBinding;
import com.mall.view.databinding.LayoutImageBinding;
import com.mall.view.databinding.R11itemBinding;
import com.mall.view.databinding.R12itemBinding;
import com.mall.view.databinding.R14itemBinding;
import com.mall.view.databinding.R15itemBinding;
import com.stx.xhb.xbanner.XBanner;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HealthShopActivity extends BaseActivity<ActivityHealthShopBinding> implements View.OnClickListener {

    ActivityCarshopTopbannerBinding carshopTopbannerBinding;

    LayoutHealthsortBinding healthsortBinding;

    LayoutImageBinding imageBinding;

    NewMyAdapter newMyAdapter;
    List<CkMall.ListBeanX.ListBean> ls = new ArrayList<>();

    //layout_healthsort
    @Override
    public int getContentViewId() {
        return R.layout.activity_health_shop;
    }

    @Override
    public View getattachedview() {
        return null;
    }

    @Override
    protected void initView(ActivityHealthShopBinding mBinding) {
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        mBinding.xrecyle.setLayoutManager(new LinearLayoutManager(context));
        mBinding.toback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //头部banner
        carshopTopbannerBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_carshop_topbanner, null, false);
        healthsortBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_healthsort, null, false);
        imageBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_image, null, false);
        healthsortBinding.healthAdd.setOnClickListener(this);
        healthsortBinding.healthAddress.setOnClickListener(this);
        healthsortBinding.healthOrder.setOnClickListener(this);
        healthsortBinding.healthShop.setOnClickListener(this);
        listaddView(carshopTopbannerBinding.getRoot());
        listaddView(healthsortBinding.getRoot());
        newMyAdapter = new NewMyAdapter(context, ls);
        mBinding.xrecyle.setAdapter(newMyAdapter);
        mBinding.xrecyle.setPullRefreshEnabled(false);
        mBinding.xrecyle.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        mBinding.xrecyle.setLoadingListener(new XRecyclerView.LoadingListener()

        {
            @Override
            public void onRefresh() {
                //refresh data here
            }

            @Override
            public void onLoadMore() {
                // load more data here
//                getData();
                getShopData("16");
            }
        });

    }

    private void listaddView(View view) {
        ScreenAdapterTools.getInstance().loadView((ViewGroup) view);
        mBinding.xrecyle.addHeaderView(view);
    }

    @Override
    protected void initData(ActivityHealthShopBinding mBinding) {
        getBannerData();
        getShopData("15");
    }

    private void getBannerData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("cate", "13");

        NewWebAPI.getNewInstance().getWebRequest("/banner.aspx?call=getBannerByCate", map, new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                super.success(result);
                if (Util.isNull(result))
                    return;
                JSONObject json = JSON.parseObject(result.toString());
                if (200 != json.getIntValue("code"))
                    return;

                Gson gson = new Gson();
                PopUpAds popUpAds = gson.fromJson(result.toString(), PopUpAds.class);
                CkMall ckMall = gson.fromJson(result.toString(), CkMall.class);
                final Map<String, List<CkMall.ListBeanX.ListBean>> map = new HashMap<>();
                for (CkMall.ListBeanX listBeanX : ckMall.getList()) {
                    map.put(listBeanX.getType(), listBeanX.getList());
                }
//                loadShop(popUpAds);

                loadtopbanner(popUpAds.getList());
            }
        });
    }

    int page = 0;

    boolean isA, isB = false;

    public void getShopData(final String cate) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("page", page + "");
        map.put("cate", cate);

        NewWebAPI.getNewInstance().getWebRequest("/banner.aspx?call=getBannerByCateArr", map, new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                super.success(result);
                if (Util.isNull(result))
                    return;
                JSONObject json = JSON.parseObject(result.toString());
                if (200 != json.getIntValue("code"))
                    return;

                page++;


                Gson gson = new Gson();
                CkMall ckMall = gson.fromJson(result.toString(), CkMall.class);
                final Map<String, List<CkMall.ListBeanX.ListBean>> map = new HashMap<>();
                for (CkMall.ListBeanX listBeanX : ckMall.getList()) {
                    map.put(listBeanX.getType(), listBeanX.getList());
                }
                if (cate.equals("15")) {

                    page = 0;
                    getShopData("14");
                }
                if (isA) {
                    isA = false;
                    map.put("A", null);
                }
                if (isB) {
                    isB = false;
                    map.put("B", null);
                }

                loadmoreShop(map);
            }

            @Override
            public void requestEnd() {
                super.requestEnd();
                mBinding.xrecyle.loadMoreComplete();
            }

            //            mBinding.shopList
        });
    }

    public void loadmoreShop(Map<String, List<CkMall.ListBeanX.ListBean>> map) {

//        Iterator iterator = map.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry entry = (Map.Entry) iterator.next();
//            String value = (String) entry.getValue();
//            String key = entry.getKey().toString();
//            Log.e("map数据类型", "key:" + key + "value:" + value);
//        }

        if ((map.get("14") == null || map.get("14").size() == 0) && (map.get("15") == null || map.get("15").size() == 0)) {
            page--;
            return;
        }

        if (map.containsKey("A")) {
            ls.add(new CkMall.ListBeanX.ListBean("A"));
        }
        if (map.containsKey("B")) {
            ls.add(new CkMall.ListBeanX.ListBean("B"));
        }


        if (map.get("14") != null && map.get("14").size() != 0) {
            ls.addAll(map.get("14"));
        }
        Log.e("数据类型", "type" + map.get("15").size());
        if (map.get("15") != null && map.get("15").size() != 0) {
            CkMall.ListBeanX.ListBean listBean = new CkMall.ListBeanX.ListBean();
            List<CkMall.ListBeanX.ListBean.Imagebean> imagebeanList = new ArrayList<>();
            for (CkMall.ListBeanX.ListBean bean :
                    map.get("15")) {
                CkMall.ListBeanX.ListBean.Imagebean imagebean = new CkMall.ListBeanX.ListBean.Imagebean();
                imagebean.setUrl_android(bean.getUrl_android());
                imagebean.setImage(bean.getImage());
                imagebeanList.add(imagebean);
            }
            listBean.setCate("15");
            listBean.setLinnumber(map.get("15").size() + "");
            listBean.setList(imagebeanList);

            ls.add(listBean);
        }

        for (int position = 0; position < ls.size(); position++) {
            Log.e("setShowRule", "position" + position + "Bean" + ls.get(position).toString());
        }


        newMyAdapter.notifyDataSetChanged();
    }


    private void loadtopbanner(List<PopUpAds.ListBean> listBeans) {
        carshopTopbannerBinding.topbanner.setData(listBeans, null);
        carshopTopbannerBinding.topbanner.setmAdapter(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                PopUpAds.ListBean image = (PopUpAds.ListBean) model;
                Glide.with(context).load(image.getImage()).into((ImageView) view);
            }

        });
        carshopTopbannerBinding.topbanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, int position) {
                if (!Util.checkLoginOrNot()) {
                    Util.show("请先登录", context);
                    Util.showIntent(context, LoginFrame.class);
                    return;
                }

//                if (!UserData.getUser().getUserLevel().contains("城市经理") && !UserData.getUser().getUserLevel().contains("城市总监")
//                        && !UserData.getUser().getUserLevel().contains("联盟商家")
//                        ) {
//
//                    String str = "仅创客和商家可参与购物拼车";
//                    new MyPopWindow.MyBuilder(context, str, "立即申请", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent(context, ProxySiteFrame.class);
//                            context.startActivity(intent);
//                        }
//                    }).setColor("#F13232")
//                            .setisshowclose(true)
//                            .build().showCenter();
//
//                    return;
//
//                }

//                Util.showIntent(context, RedEnvelopeRechargeActivity.class,
//                        new String[]{"userKey"}, new String[]{"购物券充值"}
//                );
            }
        });
        carshopTopbannerBinding.topbanner.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.healthAdd:
                Util.showIntent(this, ShopCarFrame.class);
                break;
            case R.id.healthAddress:
                Util.showIntent(this, ShopAddressManagerFrame.class);
                break;
            case R.id.healthOrder:
                Util.showIntent(context, MyOrderActivity.class, new String[]{"ordertype"}, new String[]{"66"});
                break;
            case R.id.healthShop:
                Util.showIntent(context, MyHealthActivity.class);
                break;
        }
    }

    class NewMyAdapter extends BaseRecycleAdapter<CkMall.ListBeanX.ListBean> {

        protected NewMyAdapter(Context context, List<CkMall.ListBeanX.ListBean> list) {
            super(context, list);
        }

        @Override
        public void setIteamData(ViewDataBinding mBinding, List<CkMall.ListBeanX.ListBean> list, int position) {
            int type = 0;
            if (ls.get(position).getType().equals("A") || ls.get(position).getType().equals("B")) {
                type = 9;
            } else if (Integer.parseInt(ls.get(position).getCate()) == 15) {
                type = Integer.parseInt(ls.get(position).getLinnumber());
            } else {
                type = Integer.parseInt(ls.get(position).getCate());
            }
            final CkMall.ListBeanX.ListBean listBean = list.get(position);
            if (type == 3 || type == 2) {
                R15itemBinding r12itemBinding = (R15itemBinding) mBinding;


                for (int i = 0; i < listBean.getList().size(); i++) {
                    ImageView imageView = r12itemBinding.iv0;

                    CkMall.ListBeanX.ListBean.Imagebean imagebean = listBean.getList().get(i);
                    final String url_android = imagebean.getUrl_android();
                    if (type == 2) {
                        if (i == 0) {
                            imageView = r12itemBinding.iv01;
                        } else if (i == 1) {
                            imageView = r12itemBinding.iv02;
                        }
                    } else {
                        if (i == 0) {
                            imageView = r12itemBinding.iv0;
                        } else if (i == 1) {
                            imageView = r12itemBinding.iv1;
                        } else if (i == 2) {
                            imageView = r12itemBinding.iv2;
                        }
                    }

                    Glide.with(context).load(imagebean.getImage()).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toShop(url_android);
                        }
                    });
                }


            } else if (type == 9) {
                if (listBean.getType().equals("A")) {
                    LayoutImageBinding layoutImageBinding = (LayoutImageBinding) mBinding;
                    layoutImageBinding.lineimage.setImageResource(R.drawable.hlin1);
                } else {
                    LayoutImageBinding layoutImageBinding = (LayoutImageBinding) mBinding;
                    layoutImageBinding.lineimage.setImageResource(R.drawable.hlin2);
                }


            } else {
                R14itemBinding r11itemBinding = (R14itemBinding) mBinding;
                Glide.with(context).load(listBean.getImage()).into(r11itemBinding.iv);
                r11itemBinding.iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toShop(listBean.getUrl_android());
                    }
                });
            }

        }

        @Override
        public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 2 || viewType == 3) {
                view = LayoutInflater.from(mContext).inflate(R.layout.r15item, parent, false);
                View iv2 = view.findViewById(R.id.number2);
                View iv3 = view.findViewById(R.id.number3);
                if (viewType == 2) {
                    iv2.setVisibility(View.VISIBLE);
                    iv3.setVisibility(View.GONE);
                } else {
                    iv3.setVisibility(View.VISIBLE);
                    iv2.setVisibility(View.GONE);
                }
            } else if (viewType == 9) {
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_image, parent, false);
            } else {
                view = LayoutInflater.from(mContext).inflate(R.layout.r14item, parent, false);
            }


            ScreenAdapterTools.getInstance().loadView((ViewGroup) view);
            return DataBindingUtil.bind(view);
        }

        @Override
        public int setShowRule(int position) {
            int type = 0;

            if (ls.get(position).getType().equals("A") || ls.get(position).getType().equals("B")) {
                type = 9;
            } else if (Integer.parseInt(ls.get(position).getCate()) == 15) {
                type = Integer.parseInt(ls.get(position).getLinnumber());
            } else {
                type = Integer.parseInt(ls.get(position).getCate());
            }
            return type;
        }
    }

    private void toShop(String shopid) {


        if (null == UserData.getUser()) {
            Toast.makeText(context, "您还没登录哦，请前去登录", Toast.LENGTH_SHORT).show();
            Util.showIntent(context, LoginFrame.class, new String[]{"finish"}, new String[]{"finish"});
            return;
        }

        //if (!UserData.getUser().getUserLevel().contains("城市经理") && !UserData.getUser().getUserLevel().contains("城市总监")
        //        && !UserData.getUser().getUserLevel().contains("联盟商家")
        //        ) {

        //    String str = "仅创客和商家可购买创客商城产品";
        //    new MyPopWindow.MyBuilder(context, str, "立即申请", new View.OnClickListener() {
        //        @Override
        //        public void onClick(View view) {
        //            Intent intent = new Intent(context, ProxySiteFrame.class);
        //            context.startActivity(intent);
        //        }
        //    }).setColor("#F13232")
        //            .setisshowclose(true)
        //            .build().showCenter();
        //
        //    return;
        //
        //}
        Util.showIntent(
                context,
                ProductDeatilFream.class,
                new String[]{"url", "shopType"},
                new String[]{shopid, "7"});
    }

}
