package com.mall.BusinessDetails;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.Base.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.model.BranchStore;
import com.mall.model.LMSJComment;
import com.mall.model.LocationModel;
import com.mall.model.Product;
import com.mall.model.RecBussModel;
import com.mall.model.ShopM;
import com.mall.model.ShopMInfo;
import com.mall.net.UserFavorite;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.net.NewWebAPI;
import com.mall.serving.community.view.picviewpager.PicViewpagerPopup;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LMSJCommentFrame;
import com.mall.view.LMSJShowImageFrame;
import com.mall.view.LoginFrame;
import com.mall.view.MapLMSJFrame;
import com.mall.view.R;
import com.mall.view.RecAdapter;
import com.mall.view.RecyclerViewSpacesItemDecoration;
import com.mall.view.SelectorFactory;
import com.mall.view.ServiceProductDeatil;
import com.mall.view.databinding.AppraiseitemBinding;
import com.mall.view.databinding.LayoutBusinessdetailsBinding;
import com.mall.view.databinding.ShopitemBinding;
import com.mall.view.messageboard.PostedMerchantCommentariesActivity;
import com.mall.yyrg.adapter.YYRGUtil;
import com.squareup.picasso.Picasso;
import com.stx.xhb.xbanner.XBanner;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2018/3/27.
 */

public class BusinessDetailsActivity extends BaseActivity<LayoutBusinessdetailsBinding> {

    public static String BUSINESS_ID = "businessid";
    public static String BUSINESS_Favorite = "BUSINESS_Favorite";
    public String strping = "";
    private String id = "";
    private MyAdapter adapter;
    private ShopAdapter shopAdapter;
    private List<LMSJComment> lmsjComments = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    List<BranchStore> branchStores = new ArrayList<>();
    ShopMInfo shopMInfo;
    List<RecBussModel.ListBean> shopMList = new ArrayList<>();


    RecAdapter recAdapter;
    public boolean isCollection = false;

    private Drawable getLMSJDrawable(int id) {
        Resources res = getResources();
        Drawable drawable;
        if (-1 == id) {
            drawable = res.getDrawable(res.getIdentifier("lmsj_more", "drawable", this.getPackageName()));
        } else if (18 == id) {
            drawable = res.getDrawable(res.getIdentifier("lmsj_class_more", "drawable", this.getPackageName()));
        } else {
            drawable = res.getDrawable(res.getIdentifier("lmsj_class_" + id, "drawable", this.getPackageName()));
        }
        return drawable;
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_businessdetails;
    }

    @Override
    public View getattachedview() {
        return null;
    }

    @Override
    protected void initView(LayoutBusinessdetailsBinding mBinding) {
        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void initData(LayoutBusinessdetailsBinding mBinding) {
        init();
    }


    private void init() {
        mBinding.setActivity(this);

        Intent intent = getIntent();
        mBinding.souchang.setImageResource(R.drawable.collectionunelected);
        if (intent.hasExtra(BUSINESS_ID)) {
            id = intent.getStringExtra(BUSINESS_ID);
        }
        if (intent.hasExtra(BUSINESS_Favorite)) {
            String str = intent.getStringExtra(BUSINESS_Favorite) + "";
            if (str.equals("1")) {
                isCollection = true;
                mBinding.souchang.setImageResource(R.drawable.collectionselected);
            }

        }
        recAdapter = new RecAdapter(context, shopMList);
        mBinding.listcont.setAdapter(recAdapter);
        initRecycle();
        getNet();


    }

    public void qupingjia(View view) {
        if (UserData.getUser() == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Util.showIntent(context, LoginFrame.class, new String[]{"finish"}, new String[]{"finish"});
            return;
        }
        Intent intent = new Intent(context, PostedMerchantCommentariesActivity.class);
        intent.putExtra("Type", "1");
        intent.putExtra("lmsj", id);
        intent.putExtra("face", getIntent().getStringExtra("face"));
        intent.putExtra("ShopType", shopMInfo.getCate());
        intent.putExtra("name", shopMInfo.getName());
        startActivity(intent);
    }


    public void share() {
        String[] imgs = shopMInfo.getImages().split("\\|凸\\|");
        final OnekeyShare oks = new OnekeyShare();
        final String url = "http://" + Web.webImage + "/shopCollects/shopCollectsPage.aspx?cid=" + shopMInfo.getId();
        Log.e("**********************",url);
        String name = shopMInfo.getLogo().substring(shopMInfo.getLogo().lastIndexOf("."));
        String imageUrl = shopMInfo.getLogo();

        Log.e("原地址", shopMInfo.getZone());

        String title = "";
        try {
            title = shopMInfo.getName() + "【" + shopMInfo.getZone().split(" ")[0].replace("省", "").replace("市", "") + "·"
                    + shopMInfo.getZone().split(" ")[1].replace("市", "") + "】";
        } catch (Exception e) {

        }

        Log.e("地址", title);
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setUrl(url);
        oks.setAddress("10086");
        oks.setComment("不错的一个商家：" + shopMInfo.getName());
        LocationModel locationModel = LocationModel.getLocationModel();
        oks.setVenueName(locationModel.getCity());
        oks.setText("为来店消费的客人赠送红包种子。天天领红包，日日有惊喜，分享有钱赚…");
        Log.i("ImageUrl", imageUrl);
        oks.setImageUrl(imageUrl);
        oks.setSite("远大云商");
        oks.setVenueDescription("我在" + locationModel.getCity() + "");
        oks.setSiteUrl("http://" + Web.webImage + "");
        if (!Util.isNull(locationModel.getCity())) {
            oks.setLatitude((float) locationModel.getLatitude());
            oks.setLongitude((float) locationModel.getLongitude());
        }
        oks.setSilent(false);
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                if ("ShortMessage".equals(platform.getName())) {
                    paramsToShare.setImageUrl(null);
                    paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());
                }
            }
        });
        oks.show(this);
    }


    //评论
    public void more(View view) {


        Util.showIntent(this, LMSJCommentFrame.class, new String[]{"lid", "userid1"},
                new String[]{id, shopMInfo.getUserid()});

    }

    public void moreshop(View view) {
//        Util.showIntent(context, LMSJDetailGoodsList.class);
        if (products.size() == 0) {
            return;
        }
        Intent intent = new Intent(this, ServiceProductDeatil.class);
        intent.putExtra("products", (Serializable) products);
        intent.putExtra("position", 0);
        startActivity(intent);

    }

    public void BrowsingStoresImage() {
        Log.e("浏览", "11");
        String[] imgs = shopMInfo.getImages().split("\\|凸\\|");
        if (null != imgs && imgs.length > 0) {
            Intent intent = new Intent();
            intent.setClass(context, LMSJShowImageFrame.class);
            intent.putExtra("name", shopMInfo.getName());
            intent.putExtra("imgs", imgs);
            startActivity(intent);
        } else
            Util.show("该商家没有图集！", context);
    }

    public void Collection(View view) {

        if (null == UserData.getUser()) {
            Util.show("请先登录，在收藏",
                    context);
            Util.showIntent(context, LoginFrame.class, new String[]{"home"}, new String[]{"home"});
            return;
        }
        if (isCollection) {
            UserFavorite.deletFavorite(context, id + "", "1", new UserFavorite.CallBackDelete() {
                @Override
                public void doback(String message) {
                    isCollection = false;
                    mBinding.souchang.setImageResource(R.drawable.collectionunelected);
                }
            });
        } else {
            UserFavorite.addFavorite(context, id + "", "1", new UserFavorite.CallBacke() {
                @Override
                public void doback(String message) {

                    if (message.equals("1")) {
                        isCollection = true;
                        mBinding.souchang.setImageResource(R.drawable.collectionselected);
                    }

                }


            });
        }

    }

    //分店
    public void Branches(View view) {

        Intent intent = new Intent(context, MyBranchesShopActivity.class);
        intent.putExtra("listobj", (Serializable) branchStores);
        startActivity(intent);

//        Toast.makeText(context, "平面未出", Toast.LENGTH_LONG).show();

    }

    public void callphone(View view) {
        Util.doPhone(shopMInfo.getDianhua(), context);
    }

    public void callCustomer(View view) {
        Util.doPhone(Util._400, this);
    }

    //附近商家
    public void NearbyBusinesses(View view) {
        Util.showIntent(this, MapLMSJFrame.class);
    }

    public void shopDetais(List<Product> products, int position) {
        Intent intent = new Intent(this, ServiceProductDeatil.class);
        intent.putExtra("products", (Serializable) products);
        intent.putExtra("position", position);
        startActivity(intent);
    }


    private void initRecycle() {


        //特色产品
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.shopRe.setLayoutManager(manager);
        shopAdapter = new ShopAdapter(context, products);
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, Util.dpToPx(context, 5));//左间距

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, Util.dpToPx(context, 5));//右间距
        mBinding.shopRe.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        mBinding.shopRe.setAdapter(shopAdapter);
        shopAdapter.setmOnRecyclerviewItemClickListener(new BaseRecycleAdapter.OnRecyclerviewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                shopDetais(products, position);
            }
        });

        //会员评价
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        linearLayoutManager.setAutoMeasureEnabled(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.sorRv.setLayoutManager(linearLayoutManager);
        mBinding.sorRv.setHasFixedSize(true);
        mBinding.sorRv.setNestedScrollingEnabled(false);

        adapter = new MyAdapter(context, lmsjComments);
        mBinding.sorRv.setAdapter(adapter);
    }

    public void getNet() {
        getShopMInfo();

        getShoplist();


    }

    @Override
    protected void onResume() {
        super.onResume();
        getcommentlist();
    }

    private void checkFendian() {
        Util.asynTask(new IAsynTask() {
            @Override
            public Serializable run() {
                Web web = new Web(Web.allianService, "/getAllAssociateShop2", "id=" + id + "&userId=" + shopMInfo.getUserid());
                List<BranchStore> list = web.getList(BranchStore.class);
                HashMap<String, List<BranchStore>> map = new HashMap<String, List<BranchStore>>();
                map.put("list", list);
                return map;
            }

            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<BranchStore>> map = (HashMap<String, List<BranchStore>>) runData;
                if (null == runData) {
                    Util.show("网络错误，请重试！", context);
                    return;
                }
                final List<BranchStore> list = map.get("list");
                Log.e("分店列表", "list" + list.size());
                if (list != null && list.size() > 0) {
                    mBinding.fenddianiv.setVisibility(View.VISIBLE);
                    branchStores.clear();
                    branchStores.addAll(list);
                } else {
                    mBinding.fenddianiv.setVisibility(View.GONE);
                }

            }
        });

    }

    public void getShopMInfo() {


        Util.asynTask(this, "正在获取商家信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                System.out.println("------------runData====" + runData);

                ShopMInfo te = (ShopMInfo) runData;
                if (null == te || Util.isNull(te.getId())) {
                    Util.show("网络错误，联盟商家获取失败！", context);
                    return;
                }
                shopMInfo = te;
                mBinding.setShopinfo(shopMInfo);
                checkFendian();

                try {
                    mBinding.typeIv.setImageDrawable(getLMSJDrawable(Integer.parseInt(Util.getShopCate(shopMInfo.getCate()))));
                } catch (Exception e) {

                }


                mBinding.typeTv.setText(shopMInfo.getCate());
                getsimilarrecommend();
                try {
                    String[] imgs = shopMInfo.getImages().split("\\|凸\\|");
                    mBinding.topbanner.setData(Arrays.asList(imgs), null);
                    mBinding.topbanner.setmAdapter(new XBanner.XBannerAdapter() {
                        @Override
                        public void loadBanner(XBanner banner, Object model, View view, int position) {
                            String image = (String) model;
                            Picasso.with(context).load("http://img2.yda360.com/" + image).into((ImageView) view);
                        }

                    });
                    mBinding.topbanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
                        @Override
                        public void onItemClick(XBanner banner, int position) {
                            BrowsingStoresImage();
                        }
                    });
                    mBinding.topbanner.invalidate();
                } catch (Exception e) {

                }


                Util.setLinkClickIntercept(mBinding.jianje, context);
                String content = shopMInfo.getContent();
                if ("1".equals(shopMInfo.getIsCooper()))  {


                } else {
                    content += "<br/><font color='red'>温馨提示：此商家正在洽谈合作中，如果你觉得此商家值得推荐，请拨打400-666-3838推荐，谢谢！</font>";
                }

                mBinding.jianje.setText(Html.fromHtml("<html><body>" + content + "</body></html>"));
                try {
                    mBinding.juli.setText("距离当前位置" + Util.showMM(Double.parseDouble(shopMInfo.getLat()), Double.parseDouble(shopMInfo.getLng())));
                } catch (Exception e) {
                    mBinding.juli.setText("距离当前位置");
                }

                final ShopM shopM = new ShopM();
                shopM.setCate(shopMInfo.getCate());
                shopM.setId(shopMInfo.getId());
                shopM.setName(shopMInfo.getName());
                shopM.setPointX(shopMInfo.getLng());
                shopM.setPointY(shopMInfo.getLat());
                shopM.setPhone(shopMInfo.getDianhua());
                shopM.setZone(shopMInfo.getZone());
                mBinding.tomap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            Util.doDaoHang(BusinessDetailsActivity.this,
                                    shopM);
                        } else {
                            final int[] num = {0};
                            RxPermissions rxPermissions = new RxPermissions(BusinessDetailsActivity.this);
                            rxPermissions.requestEach(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                    .subscribe(new Consumer<Permission>() {
                                        @Override
                                        public void accept(Permission permission) throws Exception {
                                            if (permission.granted) {
                                                // 用户已经同意该权限
                                                num[0]++;
                                                if (num[0] == 2) {
                                                    Util.doDaoHang(BusinessDetailsActivity.this,
                                                            shopM);

                                                }

                                            } else if (permission.shouldShowRequestPermissionRationale) {
                                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                            } else {
                                                // 用户拒绝了该权限，并且选中『不再询问』
                                            }
                                        }
                                    });
                        }


                    }
                });

//                checkFendian();
                mBinding.yinyesj.setText("营业时间：" + shopMInfo.getShopyysj());

                if (!"-7".equals(shopMInfo.getId())) {
                    mBinding.shoucll.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public Serializable run() {
                return new Web(Web.getShopMInfo, "id=" + id).getObject(ShopMInfo.class);
            }
        });


    }


    public void getcommentlist() {


        // 获取评论
        Util.asynTask1(this, "正在获取网友评论...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null != runData) {
                    HashMap<String, List<LMSJComment>> map = (HashMap<String, List<LMSJComment>>) runData;
                    List<LMSJComment> list = map.get("list");
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    lmsjComments.clear();
                    if (list.size() > 3) {
                        lmsjComments.addAll(list.subList(0, 3));
                    } else {
                        lmsjComments.addAll(list);
                    }

                    Log.e("lmsjComments", "lmsjComments" + lmsjComments.size());
                    adapter.notifyDataSetChanged();
                    int number = 0;
                    for (int i = 0; i < lmsjComments.size(); i++) {
                        number += Float.parseFloat(lmsjComments.get(i).getScore());
                    }

                    mBinding.ratingbar.setStar(number / lmsjComments.size());
                    mBinding.fen.setText(new DecimalFormat("0.0").format(number / lmsjComments.size()) + "分");

                    strping = "评价(" + lmsjComments.size() + ")";
                    mBinding.pinglunumber.setText(strping);

                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getLMSJCommentPage, "page=1&size=9999&id=" + id);
                HashMap<String, List<LMSJComment>> map = new HashMap<String, List<LMSJComment>>();
                map.put("list", web.getList(LMSJComment.class));
                return map;
            }
        });


    }

    private void getShoplist() {


        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<Product>> map = (HashMap<String, List<Product>>) runData;
                List<Product> list = map.get("list");
                Log.e("Product list", "Product" + (list == null) + "kk" + list.size());
                Log.e("Product list", "1");
                if (null != list && 0 != list.size()) {
                    Log.e("Product list", "2");
                    products.addAll(list);
                    shopAdapter.notifyDataSetChanged();
                    YYRGUtil.allProducts.clear();
                    YYRGUtil.allProducts.addAll(products);
                    mBinding.lmsjProductNoData.setVisibility(View.GONE);

                } else {
                    mBinding.lmsjProductNoData.setVisibility(View.VISIBLE);
                    mBinding.shopRe.setVisibility(View.GONE);
                }

            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getServiceProduct, "shopMID=" + id);
                HashMap<String, List<Product>> map = new HashMap<String, List<Product>>();
                map.put("list", web.getList(Product.class));
                return map;
            }
        });


    }

    class MyAdapter extends BaseRecycleAdapter<LMSJComment> {
        protected MyAdapter(Context context, List<LMSJComment> list) {
            super(context, list);
        }


        @Override
        public void setIteamData(ViewDataBinding mBinding, List<LMSJComment> list, int position) {
            LMSJComment lmsjComment = list.get(position);
            AppraiseitemBinding binding = (AppraiseitemBinding) mBinding;
            final ArrayList<String> strings = new ArrayList<>();
            String[] imags = lmsjComment.getFiles().split("\\*\\|-_-\\|\\*");
            for (int i = 0; i < imags.length; i++) {
                String s = imags[i];
                if (!Util.isNull(s)) {
                  
                    strings.add(s);
                }
                //
            }
            Picasso.with(context).load(lmsjComment.getUser_face()).error(R.drawable.headertwo).into(binding.face);
            binding.star.setStar(Float.parseFloat(lmsjComment.getScore()));
            binding.name.setText(lmsjComment.getUser());
            binding.content.setText(lmsjComment.getContent());
            binding.time.setText(lmsjComment.getDate());
            binding.csgImages.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return strings.size();
                }

                @Override
                public Object getItem(int position) {
                    return position;
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = LayoutInflater.from(context).inflate(R.layout.imageitem, parent, false);
                    ImageView imageView = (ImageView) view.findViewById(R.id.upImageView);

                    int width = Util.getScreenSize(context).getWidth() - 200;
                    Log.e("设置宽度1", "width:" + width);
                    int itemwidth = (int) (width / 5);
////										LinearLayout.LayoutParams
////												para = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                    ViewGroup.LayoutParams
                            para = imageView.getLayoutParams();
                    para.width = itemwidth;
                    para.height = itemwidth;

                    view.setLayoutParams(para);
                    Picasso.with(context)
                            .load("http://img.yda360.com/" + strings.get(position))
                            .error(R.drawable.ic_launcher)
                            .placeholder(R.drawable.ic_launcher)
                            .into(imageView);
                    return view;
                }
            });

            binding.csgImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    ArrayList<String> arrayList = new ArrayList<String>();
                    for (int i = 0; i < strings.size(); i++) {
                        arrayList.add("http://img.yda360.com/" + strings.get(i));
                    }
//										com.mall.view.picviewpager
                    new PicViewpagerPopup(context, arrayList, position, true, null);
                }
            });

        }

        @Override
        public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
            return DataBindingUtil.inflate(mInflater, R.layout.appraiseitem, parent, false);
        }

        @Override
        public int setShowRule(int position) {
            return 0;
        }
    }

    class ShopAdapter extends BaseRecycleAdapter<Product> {


        protected ShopAdapter(Context context, List<Product> list) {
            super(context, list);
        }

        @Override
        public void setIteamData(ViewDataBinding mBinding, List<Product> list, int position) {
            Product product = list.get(position);
            Log.e("Product", "Product" + product.toString());
            ShopitemBinding shopitemBinding = (ShopitemBinding) mBinding;
            Picasso.with(context).load(product.getThumb()).error(R.drawable.ic_launcher).into(shopitemBinding.shopsiv);
            shopitemBinding.name.setText(product.getName());
            shopitemBinding.shopivline.setBackground(SelectorFactory.newShapeSelector()
                    .setDefaultBgColor(Color.parseColor("#F3F3F3"))
                    .setStrokeWidth(Util.dpToPx(context, 1))
                    .setDefaultStrokeColor(Color.parseColor("#DDDDDD"))
                    .setCornerRadius(Util.dpToPx(context, 2))
                    .create());
        }

        @Override
        public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
            return DataBindingUtil.inflate(mInflater, R.layout.shopitem, parent, false);
        }

        @Override
        public int setShowRule(int position) {
            return 0;
        }


    }

    private void getsimilarrecommend() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("lat", Util.citylat + "");
        params.put("lon", Util.citylong
                + "");
        Log.e("getRecBusiness", "3");
        params.put("cName", Util.getCityStr());
        params.put("cate", Util.getShopCate(shopMInfo.getCate()));

        NewWebAPI.getNewInstance().getWebRequest("/Alliance.aspx?call=" + "getTjLMSJ",
                params, new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        com.alibaba.fastjson.JSONObject json = JSON
                                .parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"),
                                    context);
                            return;
                        }


                        Gson gson = new Gson();


                        final RecBussModel recBussModel = gson.fromJson(
//                                str
                                result.toString()
                                , RecBussModel.class);

                        Log.e("tag1", "c");
                        shopMList.addAll(recBussModel.getList());
                        Log.e("tag1", "d" + shopMList.size());
                        recAdapter.notifyDataSetChanged();
                        Log.e("tag1", "e");

                    }

                    @Override
                    public void fail(Throwable e) {
                        LogUtils.e("网络请求错误：", e);
                    }

                    @Override
                    public void timeout() {
                        LogUtils.e("网络请求超时！");
//						Util.show("小二很忙，系统很累，请稍候...", App.getContext());
                    }

                    public void requestEnd() {

                    }
                });
    }
}
