package com.mall.view.carMall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.lin.component.CustomProgressDialog;
import com.mall.ListHandler;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.model.YdNewsModel;
import com.mall.model.recommendbean;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.school.YdaSchoolActivity;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.AutoPollRecyclerView;
import com.mall.view.LoginFrame;
import com.mall.view.ProductDeatilFream;
import com.mall.view.ProxySiteFrame;
import com.mall.view.R;
import com.mall.view.databinding.HeaderLayoutBinding;
import com.mall.view.databinding.ItemHeaderLayoutBinding;
import com.mall.view.databinding.ItemTextFlipperBinding;
import com.mall.view.databinding.LayoutColumnBinding;
import com.squareup.picasso.Picasso;
import com.stx.xhb.xbanner.XBanner;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * Created by Administrator on 2018/3/30.
 */

public class ColumnAdapter extends BaseRecycleAdapter<String> {

    List<recommendbean.ListBean> listBeans1 = new ArrayList<>();
    List<recommendbean.ListBean> listBeans2 = new ArrayList<>();
    List<recommendbean.ListBean> listBeans3 = new ArrayList<>();
    List<XibaoBean.ListBean> list = new ArrayList<>();
    AutoPollRecyclerView recyclerView1;

    public void stop() {
        if (recyclerView1 != null) {
            recyclerView1.stop();
        }

    }

    protected ColumnAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void setIteamData(ViewDataBinding mBinding, List<String> list, int position) {

        if (mBinding instanceof LayoutColumnBinding) {

            LayoutColumnBinding binding = (LayoutColumnBinding) mBinding;

            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
            binding.re1.setLayoutManager(gridLayoutManager);

            if (position == 1) {
                ShopAdapter shopAdapter2 = new ShopAdapter(mContext, listBeans2);
                shopAdapter2.setmOnRecyclerviewItemClickListener(new OnRecyclerviewItemClickListener() {
                    @Override
                    public void onItemClickListener(View v, int position) {
                        shopping(listBeans2.get(position));
                    }
                });
                binding.re1.setAdapter(shopAdapter2);
                getrxDate("rx", 1, 0, shopAdapter2);
            }

        } else if (mBinding instanceof ItemHeaderLayoutBinding) {


            //出车公示

            ItemHeaderLayoutBinding binding = (ItemHeaderLayoutBinding) mBinding;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            binding.titleIc.setImageResource(R.drawable.cartitleiv1);
            binding.recyclerView.setLayoutManager(linearLayoutManager);
            List<CarpoolBean.ListBean> carppllist = new ArrayList<>();
            Caradpter caradpter = new Caradpter(mContext, carppllist, Gravity.CENTER);
            binding.recyclerView.setAdapter(caradpter);
            StateListDrawable drawableleft = SelectorFactory.newShapeSelector()
                    .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                    .setDefaultStrokeColor(Color.parseColor("#FFFFFF"))
                    .setCornerRadius(0, 10, 10, 0)
                    .create();
            binding.recyclerView.setBackground(drawableleft);
            recyclerView1 = binding.recyclerView;
            getCarpoollist("Get_Carpool_cc", binding, binding.recyclerView, carppllist, caradpter, linearLayoutManager);
            binding.iv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext,
                            CarListActivity.class);
                    mContext.startActivity(intent);
                }
            });


        } else if (mBinding instanceof HeaderLayoutBinding) {
//平台公告

            HeaderLayoutBinding binding = (HeaderLayoutBinding) mBinding;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            binding.titleIc.setImageResource(R.drawable.cartitleiv);
//            binding.recyclerView.setLayoutManager(linearLayoutManager);
            List<CarpoolBean.ListBean> carppllist = new ArrayList<>();

            getNews(binding, binding.vfp, carppllist);
            binding.iv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Util.showIntent(mContext, YdaSchoolActivity.class,
                            new String[]{"isNews"}, new Serializable[]{true});
                }
            });


        }

    }

    @Override
    public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = mInflater.inflate(R.layout.header_layout, parent, false);
        } else if (viewType == 2) {

            view = mInflater.inflate(R.layout.item_header_layout, parent, false);
        } else if (viewType == 1) {
            view = mInflater.inflate(R.layout.layout_column, parent, false);
        }

        ScreenAdapterTools.getInstance().loadView((ViewGroup) view);
        return DataBindingUtil.bind(view);
    }

    @Override
    public int setShowRule(int position) {
        int type = 0;
        if (position == 1) {
            type = 1;
        } else if (position == 2) {
            type = 2;
        }
        return type;
    }

    private void shopping(final recommendbean.ListBean bean) {


        if (null == UserData.getUser()) {
            Toast.makeText(mContext, "您还没登录哦，请前去登录", Toast.LENGTH_SHORT).show();
            Util.showIntent(mContext, LoginFrame.class, new String[]{"finish"}, new String[]{"finish"});
            return;
        }

        if (!UserData.getUser().getUserLevel().contains("城市经理") && !UserData.getUser().getUserLevel().contains("城市总监")
                && !UserData.getUser().getUserLevel().contains("联盟商家")
                ) {

            String str = "仅创客和商家可购买创客商城产品";
            new MyPopWindow.MyBuilder(mContext, str, "立即申请", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ProxySiteFrame.class);
                    mContext.startActivity(intent);
                }
            }).setColor("#F13232")
                    .setisshowclose(true)
                    .build().showCenter();

            return;

        }
        Util.showIntent(
                mContext,
                ProductDeatilFream.class,
                new String[]{"url", "shopType"},
                new String[]{bean.getPid(), "7"});


    }

    private void getNews(final HeaderLayoutBinding mbinding, final ViewFlipper viewFlipper, final List<CarpoolBean.ListBean> carppllist) {


        com.mall.serving.community.util.Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {

                Log.e("get_news", "get_news" + runData.toString());
                if (runData == null) {
                    com.mall.serving.community.util.Util.show("网络请求失败，请稍后再再试");
                    mbinding.getRoot().setVisibility(View.GONE);
                } else {
                    @SuppressWarnings("unchecked")
                    HashMap<Integer, List> map = (HashMap<Integer, List>) runData;
                    @SuppressWarnings("unchecked")
                    List<YdNewsModel> mlist = (List<YdNewsModel>) map.get(0);

                    Log.e("get_news1", "mlist" + (mlist == null) + "内容" + mlist.get(0).getTitle() + "size" + mlist.size());
                    if (mlist != null && mlist.size() > 0) {
                        mbinding.getRoot().setVisibility(View.VISIBLE);
                        for (int i = 0; i < mlist.size(); i++) {
                            carppllist.add(new CarpoolBean.ListBean("", "", "", "", "", "", mlist.get(i).getTitle().replace("&#8203;", ""), ""));
                        }
                        if (carppllist.size() > 0) {
                            int number = 0;
                            while (number < carppllist.size()) {
                                ItemTextFlipperBinding mbinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_text_flipper, null, false);
                                TextView textView1 = mbinding.tv1;
                                TextView textView2 = mbinding.tv2;

                                textView1.setVisibility(View.VISIBLE);
                                textView1.setText(carppllist.get(number).getRemark());
                                number++;
                                if (number < carppllist.size()) {
                                    textView2.setVisibility(View.VISIBLE);
                                    textView2.setText(carppllist.get(number).getRemark());
                                    number++;
                                }

//                                ScreenAdapterTools.getInstance().loadView((ViewGroup) mbinding.getRoot());
                                viewFlipper.addView(mbinding.getRoot());
                            }
                        }


                    } else {
                        mbinding.getRoot().setVisibility(View.GONE);
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

    private void getTichexinxi(final XBanner xBanner, final TextView textView) {

        NewWebAPI.getNewInstance().getWebRequest("/carpool.aspx?call=" + "Get_Car_put_info" + "&page=1&size=6&search=",
                new NewWebAPIRequestCallback() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！");
                            return;
                        }
                        Gson gson = new Gson();
                        XibaoBean xibaoBean = gson.fromJson(result.toString(), XibaoBean.class);
                        if (!xibaoBean.getCode().equals("200") || xibaoBean.getList() == null
                                || xibaoBean.getList().size() == 0
                                ) {
                            return;
                        }
                        if (xibaoBean.getList().size() > 10) {
                            for (int i = 0; i < 10; i++) {
                                list.add(xibaoBean.getList().get(i));
                            }
                        } else {
                            list.addAll(xibaoBean.getList());
                        }


                        xBanner.setData(list, null);
                        xBanner.setmAdapter(new XBanner.XBannerAdapter() {
                            @Override
                            public void loadBanner(XBanner banner, Object model, View view, int position) {
                                XibaoBean.ListBean listBean = (XibaoBean.ListBean) model;
                                Picasso.with(mContext).load("http://img2.yda360.com/" + listBean.getImg1()).into((ImageView) view);
                            }

                        });
                        xBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int position) {
                                textView.setText("热烈祝贺" + list.get(position).getUserId() + "喜提爱车");
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                        xBanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
                            @Override
                            public void onItemClick(XBanner banner, int position) {
//                                Intent intent = new Intent(mContext, GoodNewsActivity.class);
//                                intent.putExtra(GoodNewsActivity.XIBAOLIST, (Serializable) list);
//                                mContext.startActivity(intent);
                                Intent intent = new Intent(mContext, StyleActivity.class);
                                intent.putExtra("listcar", (Serializable) list);
                                mContext.startActivity(intent);
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

                    }
                }
        );
    }

    private void getrxDate(String type, final int i, int state, final ShopAdapter adapter) {
        NewWebAPI.getNewInstance().getWebRequest(
                "/carpool.aspx?call=getProductList_" + type + "&type=&ascOrDesc=desc&cid=&state=" + state + "&page=1&size=999",
                new WebRequestCallBack() {
                    @Override
                    public synchronized void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络错误，请重试！", mContext);
                            return;
                        }
//                        com.alibaba.fastjson.JSONObject json = JSON.parseObject(result.toString());
                        Gson gson = new Gson();
                        recommendbean bean = gson.fromJson(result.toString(), recommendbean.class);
                        if (200 != Integer.parseInt(bean.getCode())) {
                            Util.show(bean.getMessage(), mContext);
                            return;
                        }
                        if (i == 0) {
                            listBeans1.addAll(bean.getList());
//                            getrxDate("zx", 1, 0);
                        } else if (i == 1) {
                            listBeans2.addAll(bean.getList());
//                            getrxDate("zh", 2, 0);
                        } else if (i == 2) {
                            // 刷新 加载
                            listBeans3.addAll(bean.getList());
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
//                        cpd.dismiss();

                    }

                    @Override
                    public void fail(Throwable e) {
                        super.fail(e);
                    }
                });

    }

    private void getCarpoollist(String call, final ItemHeaderLayoutBinding binding, final AutoPollRecyclerView recyclerView, final List<CarpoolBean.ListBean> carppllist, final Caradpter caradpter, final LinearLayoutManager linearLayoutManager) {

        final CustomProgressDialog cpd = Util.showProgress("", mContext);
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
                            binding.getRoot().setVisibility(View.GONE);
                            return;
                        }
                        carppllist.clear();
                        carppllist.addAll(carpoolBean.getList());
                        if (carppllist.size() == 0) {
                            binding.getRoot().setVisibility(View.GONE);
                        }

                        caradpter.notifyDataSetChanged();
                        recyclerView.start();

                    }

                    @Override
                    public void fail(Throwable e) {
                        binding.getRoot().setVisibility(View.GONE);
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
}
