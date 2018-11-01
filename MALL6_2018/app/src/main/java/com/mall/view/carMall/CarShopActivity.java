package com.mall.view.carMall;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.Base.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lin.component.CustomProgressDialog;
import com.mall.Bean.CkMall;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.school.YdaSchoolActivity;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.AutoPollRecyclerView;
import com.mall.view.LoginFrame;
import com.mall.view.ProductDeatilFream;
import com.mall.view.ProxySiteFrame;
import com.mall.view.R;
import com.mall.view.RedEnvelopesPackage.RedEnvelopeRechargeActivity;
import com.mall.view.SPSearch;
import com.mall.view.databinding.ActivityCarshopBinding;
import com.mall.view.databinding.ActivityCarshopOneBinding;
import com.mall.view.databinding.ActivityCarshopPlacardBinding;
import com.mall.view.databinding.ActivityCarshopShowBinding;
import com.mall.view.databinding.ActivityCarshopTopbannerBinding;
import com.mall.view.databinding.ActivityCarshopTwoBinding;
import com.mall.view.databinding.ItemtextBinding;
import com.mall.view.databinding.R10itemBinding;
import com.mall.view.databinding.R11itemBinding;
import com.mall.view.databinding.R12itemBinding;
import com.mall.view.databinding.R9itemBinding;
import com.stx.xhb.xbanner.XBanner;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.mall.ListHandler;
import com.mall.model.YdNewsModel;
import com.mall.net.Web;
import com.mall.serving.community.util.IAsynTask;
import com.mall.view.databinding.ItemTextFlipperBinding;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CarShopActivity extends BaseActivity<ActivityCarshopBinding> {
    ActivityCarshopTopbannerBinding carshopTopbannerBinding;
    ActivityCarshopOneBinding activityCarshopOneBinding;
    ActivityCarshopTwoBinding activityCarshopThreeBinding;
    ActivityCarshopTwoBinding activityCarshopTwoBinding;
    ActivityCarshopShowBinding activityCarshopShowBinding;
    ActivityCarshopPlacardBinding carshopPlacardBinding;
    NewMyAdapter newMyAdapter;
    List<CkMall.ListBeanX.ListBean> ls = new ArrayList<>();

    @Override
    public int getContentViewId() {
        return R.layout.activity_carshop;
    }

    @Override
    public View getattachedview() {
        return null;
    }


    boolean isFrist = true;

    @Override
    protected void initView(final ActivityCarshopBinding mBinding) {
        ScreenAdapterTools.getInstance().loadView((ViewGroup) getWindow().getDecorView());
        mBinding.titleLin.setBackgroundColor(Color.parseColor("#00ffffff"));
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mBinding.shopList.setLayoutManager(new LinearLayoutManager(context));
        //头部banner
        carshopTopbannerBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_carshop_topbanner, null, false);
        //新闻公告
        carshopPlacardBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_carshop_placard, null, false);
        //栏目1
        activityCarshopOneBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_carshop_one, null, false);
        //栏目2
        activityCarshopThreeBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_carshop_two, null, false);
        activityCarshopThreeBinding.re.setLayoutManager(new GridLayoutManager(this, 3));
        //栏目3
        activityCarshopTwoBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_carshop_two, null, false);
        activityCarshopTwoBinding.re.setLayoutManager(new GridLayoutManager(this, 2));
        //出车公示
        activityCarshopShowBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_carshop_show, null, false);

        listaddView(carshopTopbannerBinding.getRoot());
        listaddView(carshopPlacardBinding.getRoot());
        listaddView(activityCarshopOneBinding.getRoot());
        listaddView(activityCarshopThreeBinding.getRoot());
        listaddView(activityCarshopShowBinding.getRoot());
        listaddView(activityCarshopTwoBinding.getRoot());

        newMyAdapter = new NewMyAdapter(context, ls);
        mBinding.shopList.setAdapter(newMyAdapter);


        mBinding.shopList.setPullRefreshEnabled(false);
        mBinding.shopList.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);

        mBinding.backtopIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smoothMoveToPosition(mBinding.shopList, 0);
            }
        });

        mBinding.shopList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("滚动", "dy:" + dy);
                /*当RecyclerView滚动。这将是
                 称为滚动后完成。这个回调也被称为后如果可见项目范围改变布局
                 计算。在这种情况下,dx和dy将0。
                 dx表示水平滚动，dy表示垂直滚动*/

                if (mShouldScroll) {
                    mShouldScroll = false;
                    smoothMoveToPosition(recyclerView, mToPosition);
                }

            }


            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //LinearLayoutManager
                LinearLayoutManager linearLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
                int viewfrist = linearLayout.findFirstVisibleItemPosition();
                Log.e("第一个view", "viewfrist:" + viewfrist);
                if (viewfrist == 1) {
                    mBinding.titleLin.setBackgroundColor(Color.parseColor("#00ffffff"));
                    mBinding.backtopIv.setVisibility(View.GONE);
                    isFrist = true;
                } else {
                    mBinding.titleLin.setBackgroundColor(Color.parseColor("#88F01802"));
                    mBinding.backtopIv.setVisibility(View.VISIBLE);
                    isFrist = false;
                }
            }
        });


        mBinding.toback.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (isFrist) {
                    finish();
                } else {
                    smoothMoveToPosition(mBinding.shopList, 0);
                }


            }
        });

        mBinding.toSearch.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Util.showIntent(context, SPSearch.class);

            }
        });
        mBinding.toSearch.setBackground(SelectorFactory.newShapeSelector()
                .

                        setDefaultBgColor(Color.parseColor("#FFFFFF"))
                .

                        setDefaultStrokeColor(Color.parseColor("#787878"))
                .

                        setSelectedStrokeColor(Color.parseColor("#FF2145"))
                .

                        setCornerRadius(30)
                .

                        create());


        mBinding.shopList.setLoadingListener(new XRecyclerView.LoadingListener()

        {
            @Override
            public void onRefresh() {
                //refresh data here
            }

            @Override
            public void onLoadMore() {
                // load more data here
                getData();
            }
        });

    }

    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    private void listaddView(View view) {
        ScreenAdapterTools.getInstance().loadView((ViewGroup) view);
        mBinding.shopList.addHeaderView(view);
    }


    @Override
    protected void initData(ActivityCarshopBinding mBinding) {
        getShopData();
        activityCarshopShowBinding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        getCarpoollist("Get_Carpool_cc", activityCarshopShowBinding.recyclerView);
        getNews(carshopPlacardBinding.vfp);

    }

    public void loadShop(Map<String, List<CkMall.ListBeanX.ListBean>> map) {
        loadtopbanner(map.get("7"));
        loadShopOne(map.get("8"));
        loadShopTwo(map.get("9"));
        loadShopThree(map.get("10"));
    }

    public void loadmoreShop(Map<String, List<CkMall.ListBeanX.ListBean>> map) {

        if ((map.get("11") == null || map.get("11").size() == 0) && (map.get("12") == null || map.get("12").size() == 0)) {
            page--;
            return;
        }

        if (map.get("11") != null && map.get("11").size() != 0) {
            ls.addAll(map.get("11"));
        }
        if (map.get("12") != null && map.get("12").size() != 0) {
            CkMall.ListBeanX.ListBean listBean = new CkMall.ListBeanX.ListBean();
            List<CkMall.ListBeanX.ListBean.Imagebean> imagebeanList = new ArrayList<>();
            for (CkMall.ListBeanX.ListBean bean :
                    map.get("12")) {
                CkMall.ListBeanX.ListBean.Imagebean imagebean = new CkMall.ListBeanX.ListBean.Imagebean();
                imagebean.setUrl_android(bean.getUrl_android());
                imagebean.setImage(bean.getImage());
                imagebeanList.add(imagebean);
            }
            listBean.setCate("12");
            listBean.setLinnumber(map.get("12").size() + "");
            listBean.setList(imagebeanList);

            ls.add(listBean);
        }

        // 12
        ;

        newMyAdapter.notifyDataSetChanged();
    }

    private void loadShopTwo(List<CkMall.ListBeanX.ListBean> goods9) {
        if (goods9 != null) {
            activityCarshopThreeBinding.re.setAdapter(new MyAdapter(context, goods9, "9"));
        }
    }

    private void loadShopThree(List<CkMall.ListBeanX.ListBean> goods10) {
        activityCarshopTwoBinding.re.setAdapter(new MyAdapter(context, goods10, "10"));
    }

    private void loadShopOne(List<CkMall.ListBeanX.ListBean> goods8) {
        if (goods8 != null && goods8.size() == 5) {
            Glide.with(context).load(goods8.get(4).getImage()).into(activityCarshopOneBinding.iv1);
            ivclick(activityCarshopOneBinding.iv1, goods8.get(4).getUrl_android());

            Glide.with(context).load(goods8.get(3).getImage()).into(activityCarshopOneBinding.iv2);
            ivclick(activityCarshopOneBinding.iv2, goods8.get(3).getUrl_android());

            Glide.with(context).load(goods8.get(2).getImage()).into(activityCarshopOneBinding.iv3);
            ivclick(activityCarshopOneBinding.iv3, goods8.get(2).getUrl_android());

            Glide.with(context).load(goods8.get(1).getImage()).into(activityCarshopOneBinding.iv4);
            ivclick(activityCarshopOneBinding.iv4, goods8.get(1).getUrl_android());

            Glide.with(context).load(goods8.get(0).getImage()).into(activityCarshopOneBinding.iv5);
            ivclick(activityCarshopOneBinding.iv5, goods8.get(0).getUrl_android());

        }
    }

    private void loadtopbanner(List<CkMall.ListBeanX.ListBean> listBeans) {

        carshopTopbannerBinding.topbanner.setData(listBeans, null);
        carshopTopbannerBinding.topbanner.setmAdapter(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                CkMall.ListBeanX.ListBean image = (CkMall.ListBeanX.ListBean) model;
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

                if (!UserData.getUser().getUserLevel().contains("城市经理") && !UserData.getUser().getUserLevel().contains("城市总监")
                        && !UserData.getUser().getUserLevel().contains("联盟商家")
                        ) {

                    String str = "仅创客和商家可参与购物拼车";
                    new MyPopWindow.MyBuilder(CarShopActivity.this, str, "立即申请", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(CarShopActivity.this, ProxySiteFrame.class);
                            CarShopActivity.this.startActivity(intent);
                        }
                    }).setColor("#F13232")
                            .setisshowclose(true)
                            .build().showCenter();

                    return;

                }

                Util.showIntent(context, RedEnvelopeRechargeActivity.class,
                        new String[]{"userKey"}, new String[]{"购物券充值"}
                );
            }
        });
        carshopTopbannerBinding.topbanner.invalidate();
    }

    private void ivclick(View view, final String id) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toShop(id);
            }
        });
    }

    private void toShop(String shopid) {


        if (null == UserData.getUser()) {
            Toast.makeText(context, "您还没登录哦，请前去登录", Toast.LENGTH_SHORT).show();
            Util.showIntent(context, LoginFrame.class, new String[]{"finish"}, new String[]{"finish"});
            return;
        }

        if (!UserData.getUser().getUserLevel().contains("城市经理") && !UserData.getUser().getUserLevel().contains("城市总监")
                && !UserData.getUser().getUserLevel().contains("联盟商家")
                ) {

            String str = "仅创客和商家可购买创客商城产品";
            new MyPopWindow.MyBuilder(context, str, "立即申请", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProxySiteFrame.class);
                    context.startActivity(intent);
                }
            }).setColor("#F13232")
                    .setisshowclose(true)
                    .build().showCenter();

            return;

        }
        Util.showIntent(
                context,
                ProductDeatilFream.class,
                new String[]{"url", "shopType"},
                new String[]{shopid, "7"});
    }

    public void getShopData() {

        Map<String, String> map = new HashMap<String, String>();
        map.put("cate", "6");

        NewWebAPI.getNewInstance().getWebRequest("/banner.aspx?call=getBannerByCateIndex", map, new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                super.success(result);
                if (Util.isNull(result))
                    return;
                JSONObject json = JSON.parseObject(result.toString());
                if (200 != json.getIntValue("code"))
                    return;

                Gson gson = new Gson();
                CkMall ckMall = gson.fromJson(result.toString(), CkMall.class);
                final Map<String, List<CkMall.ListBeanX.ListBean>> map = new HashMap<>();
                for (CkMall.ListBeanX listBeanX : ckMall.getList()) {
                    map.put(listBeanX.getType(), listBeanX.getList());
                }
                loadShop(map);
            }
        });

    }

    int page = 0;

    public void getData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("page", page + "");

        NewWebAPI.getNewInstance().getWebRequest("/banner.aspx?call=getBannerByCateAjax", map, new WebRequestCallBack() {
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
                loadmoreShop(map);
            }

            @Override
            public void requestEnd() {
                super.requestEnd();
                mBinding.shopList.loadMoreComplete();
            }

            //            mBinding.shopList
        });
    }

    private void getCarpoollist(String call, final AutoPollRecyclerView recyclerView) {

        final CustomProgressDialog cpd = Util.showProgress("", context);
        com.mall.serving.community.net.NewWebAPI.getNewInstance().getWebRequest("/carpool.aspx?call=" + call + "&page=1&size=999&search=",
                new NewWebAPIRequestCallback() {
                    @Override
                    public synchronized void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！");
                            return;
                        }
                        Log.e("--------", result.toString());
                        Gson gson = new Gson();
                        CarpoolBean carpoolBean = gson.fromJson(result.toString(), CarpoolBean.class);
                        if (!carpoolBean.getCode().equals("200") || carpoolBean.getList() == null || carpoolBean.getList().size() == 0) {
                            return;
                        }

                        recyclerView.setAdapter(new Caradpter(context, carpoolBean.getList(), Gravity.CENTER));
                        recyclerView.start();
                        activityCarshopShowBinding.outcarLin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context,
                                        CarListActivity.class);
                                context.startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void fail(Throwable e) {
                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！");
                        return;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
        );
    }

    private void getNews(final ViewFlipper viewFlipper) {


        com.mall.serving.community.util.Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {

                Log.e("get_news", "get_news" + runData.toString());
                if (runData == null) {
                    com.mall.serving.community.util.Util.show("网络请求失败，请稍后再再试");
                } else {
                    @SuppressWarnings("unchecked")
                    HashMap<Integer, List> map = (HashMap<Integer, List>) runData;
                    @SuppressWarnings("unchecked")
                    List<YdNewsModel> mlist = (List<YdNewsModel>) map.get(0);

                    Log.e("get_news1", "mlist" + (mlist == null) + "内容" + mlist.get(0).getTitle() + "size" + mlist.size());
                    List<CarpoolBean.ListBean> carppllist = new ArrayList<>();
                    if (mlist != null && mlist.size() > 0) {
                        for (int i = 0; i < mlist.size(); i++) {
                            carppllist.add(new CarpoolBean.ListBean("", "", "", "", "", "", mlist.get(i).getTitle().replace("&#8203;", ""), ""));
                        }
                        if (carppllist.size() > 0) {
                            int number = 0;
                            while (number < carppllist.size()) {
                                ItemTextFlipperBinding mbinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_text_flipper, null, false);
                                TextView textView1 = mbinding.tv1;

                                textView1.setVisibility(View.VISIBLE);
                                textView1.setText(carppllist.get(number).getRemark());
                                number++;


//                                ScreenAdapterTools.getInstance().loadView((ViewGroup) mbinding.getRoot());
                                viewFlipper.addView(mbinding.getRoot());
                            }
                            viewFlipper.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Util.showIntent(context, YdaSchoolActivity.class,
                                            new String[]{"isNews"}, new Serializable[]{true});
                                }
                            });
                        }


                    } else {
                    }


                }
            }

            @SuppressLint("UseSparseArrays")
            @Override
            public Serializable run() {


                Web web = new Web(Web.ydnews_url, Web.get_news
                        + "&pagesize_=12&curpage=" + 1, "");
                InputStream in = web.getHtml();
                HashMap<Integer, List<YdNewsModel>> map = new HashMap<Integer, List<YdNewsModel>>();
                ListHandler handler = null;
                SAXParser parser = null;
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance(); // 取得SAXParserFactory实例
                    parser = factory.newSAXParser();
                    handler = new ListHandler();
                    parser.parse(in, handler);
                    map.put(0, handler.getList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return map;
            }
        });


    }


    class MyAdapter extends BaseRecycleAdapter<CkMall.ListBeanX.ListBean> {
        String mType;

        protected MyAdapter(Context context, List<CkMall.ListBeanX.ListBean> list, String type) {
            super(context, list);
            this.mType = type;
        }

        @Override
        public void setIteamData(ViewDataBinding mBinding, final List<CkMall.ListBeanX.ListBean> list, final int position) {
            ImageView iv = null;
            if (mType.equals("9")) {
                R9itemBinding r9itemBinding = (R9itemBinding) mBinding;
                iv = r9itemBinding.iv;
            } else if (mType.equals("10")) {
                R10itemBinding r10itemBinding = (R10itemBinding) mBinding;
                iv = r10itemBinding.iv;
            }

            Glide.with(context).load(list.get(position).getImage()).into(iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toShop(list.get(position).getUrl_android());
                }
            });
        }

        @Override
        public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
            View view = null;
            if (mType.equals("9")) {
                view = LayoutInflater.from(mContext).inflate(R.layout.r9item, parent, false);
            } else if (mType.equals("10")) {
                view = LayoutInflater.from(mContext).inflate(R.layout.r10item, parent, false);
            } else if (mType.equals("12")) {
                view = LayoutInflater.from(mContext).inflate(R.layout.r12item, parent, false);
            }
            ScreenAdapterTools.getInstance().loadView((ViewGroup) view);
            return DataBindingUtil.bind(view);
        }

        @Override
        public int setShowRule(int position) {
            return 0;
        }
    }

    class NewMyAdapter extends BaseRecycleAdapter<CkMall.ListBeanX.ListBean> {

        protected NewMyAdapter(Context context, List<CkMall.ListBeanX.ListBean> list) {
            super(context, list);
        }

        @Override
        public void setIteamData(ViewDataBinding mBinding, List<CkMall.ListBeanX.ListBean> list, int position) {
            int type = 0;
            if (Integer.parseInt(ls.get(position).getCate()) == 12) {
                type = Integer.parseInt(ls.get(position).getLinnumber());
            } else {
                type = Integer.parseInt(ls.get(position).getCate());
            }
            final CkMall.ListBeanX.ListBean listBean = list.get(position);
            if (type == 3 || type == 2) {
                R12itemBinding r12itemBinding = (R12itemBinding) mBinding;


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


            } else {
                R11itemBinding r11itemBinding = (R11itemBinding) mBinding;
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
                view = LayoutInflater.from(mContext).inflate(R.layout.r12item, parent, false);
                View iv2 = view.findViewById(R.id.number2);
                View iv3 = view.findViewById(R.id.number3);
                if (viewType == 2) {
                    iv2.setVisibility(View.VISIBLE);
                    iv3.setVisibility(View.GONE);
                } else {
                    iv3.setVisibility(View.VISIBLE);
                    iv2.setVisibility(View.GONE);
                }
            } else {
                view = LayoutInflater.from(mContext).inflate(R.layout.r11item, parent, false);
            }


            ScreenAdapterTools.getInstance().loadView((ViewGroup) view);
            return DataBindingUtil.bind(view);
        }

        @Override
        public int setShowRule(int position) {
            int type = 0;
            if (Integer.parseInt(ls.get(position).getCate()) == 12) {
                type = Integer.parseInt(ls.get(position).getLinnumber());
            } else {
                type = Integer.parseInt(ls.get(position).getCate());
            }
            return type;
        }
    }


}
