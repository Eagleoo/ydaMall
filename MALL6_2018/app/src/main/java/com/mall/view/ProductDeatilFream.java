package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.android.common.logging.Log;
import com.example.view.VideoAudioDialog;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.CollectionsProduct;
import com.mall.model.ProductStandard;
import com.mall.model.ShopCarNumberModel;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.UserFavorite;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.BitmapLruCache;
import com.mall.util.Data;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.squareup.picasso.Callback;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.bleu.widget.slidedetails.SlideDetailsLayout;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

@SuppressLint("NewApi")
public class ProductDeatilFream extends Activity {
    // private Product pro = null;
    private JSONObject json = null;
    private JSONObject detailJson = null;
    private Intent data = new Intent();
    private static String[] colors;
    private static String[] sizes;
    @ViewInject(R.id.product_detail_sheng)
    private TextView psyf;
    @ViewInject(R.id.shopcar_number)
    private TextView shopcar_number;
    @ViewInject(R.id.topCenter)
    private TextView topCenter;
    @ViewInject(R.id.sfqlin)
    View sfqlin;
    private String selColor = "";
    private String selSize = "";
    private DbUtils db;
    private List<ImageView> picViews = new ArrayList<ImageView>();
    @ViewInject(R.id.product_detail_new_collect)
    private ImageView product_detail_new_collect;
    private BitmapUtils bmUtils;
    private BitmapLruCache bmLruCache;
    private PopupWindow distancePopup;
    private SharedPreferences sp;
    private List<ProductStandard> guigelist = new ArrayList<ProductStandard>();
    private String urlID = "";
    @ViewInject(R.id.product_comments)
    private TextView product_comments;
    @ViewInject(R.id.shopcard_line)
    private LinearLayout shopCardLine;
    @ViewInject(R.id.scroll)
    private ScrollView scroll;
    private int amount = 1;
    private String unum = "";
    private String activity = "";
    @ViewInject(R.id.size_color)
    private TextView color_size_click_view;
    @ViewInject(R.id.product_banner)
    private TextView product_banner;
    @ViewInject(R.id.phone_name)
    private TextView phone_name;
    @ViewInject(R.id.tv_phone_name_develop)
    private TextView tv_phone_name_develop;
    @ViewInject(R.id.tv_phone_name_up)
    private View tv_phone_name_up;

    @ViewInject(R.id.givered)
    private TextView givered;

    @ViewInject(R.id.tagredpa)
    private View tagredpa;

    @ViewInject(R.id.llky)
    private View llky;

    @ViewInject(R.id.redtis)
    private ImageView redtis;

    @ViewInject(R.id.isshowgraphic_tv)
    private MoreTextView isshowgraphic;
    @ViewInject(R.id.islide)
    private SlideDetailsLayout islide;

    @ViewInject(R.id.slidedetails_behind)
    private WebView webview;


    private String currPrice = "0.00";
    private double hb=0.00;

    int flag = 1;
    int tag = 1;
    int lines = 0;
    boolean b = false;

    // 商品分享点击
    @OnClick({R.id.product_detail_new_share, R.id.topback})
    public void shareClick(final View v) {

        switch (v.getId()) {
            case R.id.topback:
                finish();
                break;
            case R.id.product_detail_new_share:
                final OnekeyShare oks = new OnekeyShare();
                String unumUrl = "";
                if (null != UserData.getUser()) {
                    unumUrl = "&unum=" + UserData.getUser().getUserNo();
                }
                if (UserData.getUser() != null) {

                }

                final String url = "http://" + Web.webImage + "/phone/"
                        + "NewProDetail.aspx"
                        // "NewSerProDetail.aspx"
                        + "?id=" + data.getStringExtra("url") + unumUrl;
                Log.e("shareUrl---", url + "");
                final String title = "远大云商" + json.getString("name");
                oks.setTitleUrl(url);
                oks.setTitle("远大云商" + json.getString("name"));
                oks.setUrl(url);
                oks.setAddress("10086");
                oks.setComment("很好");
                if (null != detailJson)
                    oks.setText(detailJson.getString("productExplain")
                            .replaceAll("<(.|\n)*?>", "").replaceAll("&nbsp;", " "));
                oks.setImageUrl(json.getString("thumb"));
                oks.setSite("远大云商");
                oks.setSiteUrl(url);
                oks.setSilent(false);
                oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                    @Override
                    public void onShare(Platform platform,
                                        Platform.ShareParams paramsToShare) {
                        if ("ShortMessage".equals(platform.getName())) {
                            paramsToShare.setImageUrl(null);
                            paramsToShare.setText(paramsToShare.getText() + "\n"
                                    + url.toString());
                        }
                    }
                });
                // oks.setShareContentCustomizeCallback(null);
                oks.show(this);
                break;
        }

    }

    // 收藏图标点击
    @OnClick(R.id.product_detail_new_collect)
    public void collect(final View v) {
        if (null != UserData.getUser()) {


            UserFavorite.addFavorite(ProductDeatilFream.this, urlID + "", "0", new UserFavorite.CallBacke() {
                @Override
                public void doback(String message) {
                    ((TextView) v).setText("已收藏");
                    product_detail_new_collect
                            .setImageResource(R.drawable.product_detail_new_collect_pressed);
                }


            });


            Selector sel = Selector.from(CollectionsProduct.class);
            sel.where("ownerid", "=", UserData.getUser().getUserId());
            sel.and("productid", "=", data.getStringExtra("url"));
            List<CollectionsProduct> dataList = null;
            try {
                dataList = db.findAll(sel);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (null == dataList)
                dataList = new ArrayList<CollectionsProduct>();
            // 如果存在则删除
            if (0 < dataList.size()) {
                try {
                    db.delete(
                            CollectionsProduct.class,
                            WhereBuilder.b("ownerid", "=",
                                    UserData.getUser().getUserId()).and(
                                    "productid", "=",
                                    data.getStringExtra("url")));
                    product_detail_new_collect
                            .setImageResource(R.drawable.sharecoll6);
                    Util.show("取消收藏成功！", ProductDeatilFream.this);
                } catch (DbException e) {
                    e.printStackTrace();
                    try {
                        db.dropTable(CollectionsProduct.class);
                    } catch (DbException e1) {
                        e1.printStackTrace();
                        Util.show("取消收藏失败！", this);
                    }
                }
            } else {
                CollectionsProduct cp = new CollectionsProduct();
                cp.setOwnerid(UserData.getUser().getUserId());
                cp.setPressedtimes(0L);
                cp.setProductid(data.getStringExtra("url"));
                cp.setProductname(json.getString("name"));
                cp.setId(UUID.randomUUID().toString());
                cp.setCollectTime(new SimpleDateFormat("yyyy-MM-dd")
                        .format(new Date()));
                cp.setImgUrl(json.getString("thumb").replaceFirst(
                        "img.mall666.cn", Web.imgServer));
                try {
                    db.saveBindingId(cp);
                    product_detail_new_collect
                            .setImageResource(R.drawable.product_detail_new_collect_pressed);
                    List<CollectionsProduct> list2 = db
                            .findAll(CollectionsProduct.class);
                    Util.show("收藏成功！", ProductDeatilFream.this);
                } catch (DbException e) {
                    e.printStackTrace();
                    try {
                        db.dropTable(CollectionsProduct.class);
                    } catch (DbException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        db.saveBindingId(cp);
                    } catch (DbException e1) {
                        Util.show("收藏失败，请重试！", this);
                        e1.printStackTrace();
                    }
                }
            }
        } else {
            Toast.makeText(this, "您还没登录哦，请前去登录", Toast.LENGTH_SHORT).show();
            Util.showIntent(ProductDeatilFream.this, LoginFrame.class, new String[]{"finish"}, new String[]{"finish"});
            return;
        }

    }


    @OnClick({R.id.isshowgraphic_tv})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.isshowgraphic_tv:

                break;
        }
    }

    @OnClick(R.id.size_color)
    public void SizeColor(final View v) {
        getPopupWindow();
        View guige = getLayoutInflater().inflate(
                R.layout.product_size_and_color, null);
        ImageView producetimage = (ImageView) guige
                .findViewById(R.id.producetimage);
        final TextView name2 = (TextView) guige.findViewById(R.id.name);
        TextView price = (TextView) guige.findViewById(R.id.price);
        TextView stock = (TextView) guige.findViewById(R.id.stock);
        TextView sub = (TextView) guige.findViewById(R.id.sub_number);
        TextView product_detail_shop = (TextView) guige
                .findViewById(R.id.product_detail_shop);
        TextView product_detail_addcar = (TextView) guige
                .findViewById(R.id.product_detail_addcar);
        TextView add = (TextView) guige.findViewById(R.id.add_number);
        LinearLayout size_layout = (LinearLayout) guige
                .findViewById(R.id.size_layout);
        LinearLayout color_layout = (LinearLayout) guige
                .findViewById(R.id.color_layout);
        final TextView buy_number = (TextView) guige
                .findViewById(R.id.buy_number);
        ImageView cancel = (ImageView) guige.findViewById(R.id.delete);
        LinearLayout size_l = (LinearLayout) guige.findViewById(R.id.size_l);
        LinearLayout color_l = (LinearLayout) guige.findViewById(R.id.color_l);

        product_detail_shop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                shopping(false, "正在购买...", "购买失败，请重试！");
            }
        });
        product_detail_addcar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                shopping(true, "正在加入购物车...", "加入购物车失败，请重试！");
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (distancePopup != null && distancePopup.isShowing()) {
                    distancePopup.dismiss();
                }
            }
        });
        buy_number.setText(amount + "");
        sub.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int nu = Integer.parseInt(buy_number.getText().toString());
                if (nu > 1) {
                    buy_number.setText((nu - 1) + "");
                    amount = nu - 1;
                }
            }
        });
        add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int nu = Integer.parseInt(buy_number.getText().toString());
                String stocks = json.getString("stocks");
                if (null != json && !Util.isNull(stocks)) {
                    if ((nu + 1) > Util.getInt(stocks)) {
                        Toast.makeText(
                                ProductDeatilFream.this,
                                json.getString("name") + "库存为" + stocks
                                        + ",如需购买更多请联系客服", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                    buy_number.setText((nu + 1) + "");
                    amount = Integer.parseInt(buy_number.getText().toString());
                } else
                    Util.show("库存不足！", ProductDeatilFream.this);
            }
        });
        bmUtils.display(producetimage, json.getString("thumb"));
        name2.setText(json.getString("name"));
        price.setText("￥" + currPrice);
        stock.setText("库存：" + json.getString("stocks"));
        guige.setFocusable(true);
        guige.getBackground().setAlpha(150);
        initpoputwindow(guige);
        if (null != sizes && 0 < sizes.length) {
            List<String> lis = new ArrayList<String>();
            for (int i = 0; i < sizes.length; i++) {
                lis.add(sizes[i]);
            }
            initColorAndSize(lis, size_layout, "size", price, stock);
        } else {
            size_l.setVisibility(View.GONE);
        }
        if (null != colors && 0 < colors.length) {
            List<String> lis = new ArrayList<String>();
            for (int i = 0; i < colors.length; i++) {
                lis.add(colors[i]);
            }
            initColorAndSize(lis, color_layout, "color", price, stock);
        } else {
            color_l.setVisibility(View.GONE);
        }

        FrameLayout root = (FrameLayout) this.findViewById(R.id.root);
        distancePopup.showAtLocation(root, Gravity.BOTTOM, 0,
                Util.dpToPx(this, 40));
    }

    @OnClick(R.id.product_parameter)
    public void ProductGuiGe(final View v) {
        getPopupWindow();
        View guige = getLayoutInflater().inflate(R.layout.product_guige, null);
        guige.setFocusable(true);
        ImageView cancel = (ImageView) guige.findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (distancePopup != null && distancePopup.isShowing()) {
                    distancePopup.dismiss();
                }
            }
        });
        LinearLayout container = (LinearLayout) guige
                .findViewById(R.id.container);
        initGuigeContainer(container, guigelist);
        guige.getBackground().setAlpha(150);
        initpoputwindow(guige);
        FrameLayout root = (FrameLayout) this.findViewById(R.id.root);
        distancePopup.showAtLocation(root, Gravity.BOTTOM, 0,
                Util.dpToPx(this, 40));
    }

    /**
     * 查看商品 详细描述
     *
     * @param v
     */
    @OnClick(R.id.product_detail)
    public void ProductDetail(final View v) {
        if (null != detailJson) {
            Intent intent = new Intent(ProductDeatilFream.this,
                    ProductDetailHtml.class);
            intent.putExtra("content", detailJson.getString("productExplain"));
            ProductDeatilFream.this.startActivity(intent);
        }
    }

    @OnClick(R.id.product_comments)
    public void ProductComment(final View v) {
        Intent intent = new Intent(ProductDeatilFream.this,
//                ProductCommentList.class);
                NewProductCommentListActivity.class);
        intent.putExtra("pid", urlID + "");
        ProductDeatilFream.this.startActivity(intent);
    }

    private void initGuigeContainer(LinearLayout container,
                                    List<ProductStandard> guigelist) {
        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,
                Util.dpToPx(this, 35));
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(0,
                Util.dpToPx(this, 25), 1.0f);
        ll.setMargins(Util.pxToDp(this, 5), Util.pxToDp(this, 5),
                Util.pxToDp(this, 5), Util.pxToDp(this, 5));
        int rows = 0;
        if (guigelist.size() % 2 == 0) {
            rows = guigelist.size() / 2;
        } else {
            rows = guigelist.size() / 2 + 1;
        }
        for (int i = 0; i < rows; i++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(lp);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            for (int k = 0; k < 2; k++) {
                TextView t = new TextView(this);
                t.setLayoutParams(ll);
                t.setGravity(Gravity.CENTER);
                t.setTextSize(12);
                t.setTextColor(Color.parseColor("#6c6c6c"));
                if ((i * 2 + k) < guigelist.size()) {// 判断数组下标越界
                    t.setText(guigelist.get(i * 2 + k).getName() + ":"
                            + guigelist.get(i * 2 + k).getValue());
                    t.setBackgroundColor(Color.parseColor("#c9c8c8"));
                }
                layout.addView(t);
            }
            container.addView(layout);
        }
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_product_detail_frame);
        ViewUtils.inject(this);

        WebSettings webSettings = webview.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setBuiltInZoomControls(true);//显示放大缩小 controler
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // 开启 DOM storage API 功能
        //支持js
        webSettings.setJavaScriptEnabled(true);
        webview.addJavascriptInterface(this, "videolistner");


        islide.setOnSlideDetailsListener(new SlideDetailsLayout.OnSlideDetailsListener() {
            @Override
            public void onStatucChanged(SlideDetailsLayout.Status status) {
                Log.e("Status", status.toString());
                if (status.toString().equals("OPEN")) {
                    isshowgraphic.setText("下拉收起图文详情");
                    isshowgraphic.setImag(R.drawable.arrows_open_gray);
                } else {
                    isshowgraphic.setText("上拉查看图文详情");
                    isshowgraphic.setImag(R.drawable.arrows_close_gray);
                }

            }
        });
        bmUtils = new BitmapUtils(this);
        bmLruCache = new BitmapLruCache(ProductDeatilFream.this);
        Util.list.add(this);
        db = DbUtils.create(this);
        sp = this.getSharedPreferences("size_and_color", 0);
        data = getIntent();
        Log.e("sssssssssssssssss",data.getStringExtra("orderType")+"");
        sizes = null;
        colors = null;
        urlID = data.getStringExtra("url");

        android.util.Log.e("urlID", urlID + "~~~");
        unum = data.getStringExtra("unum");
        String s = data.getStringExtra("tagkk") + "";
        Log.e("数据列表", s);

        redtis.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                VideoAudioDialog dialog = new VideoAudioDialog(ProductDeatilFream.this);
                dialog.setTitle("温馨提示");
                dialog.showcancel(View.VISIBLE);
                dialog.setRight("确认");
                String str = "红包种子将在您确认收货后，才能到达您红包种子账户";
                if (data.hasExtra("shopType")) {
                    str = "1、红包种子将在您确认收货后，才能到达您红包种子账户;\n" +
                            "2、使用购物券和红包种子支付不赠送等额红包种子.";
                }
                dialog.setContent(str);
                dialog.setRightColor(getResources().getColor(R.color.red));
                dialog.show();


            }
        });


        if (Util.isNull(unum))
            unum = "";
        activity = data.getStringExtra("activity");
        if (Util.isNull(activity))
            activity = "";
        final CustomProgressDialog dialog = Util
                .showProgress("正在获取产品...", this);
        Map<String, String> params = new HashMap<String, String>();
        String uid = "";
        if (null != UserData.getUser()) {
            User user = UserData.getUser();
            if (!Util.isNull(user.getSessionId()))
                uid = user.getSessionId();
            else
                uid = user.getNoUtf8UserId();
        }
        params.put("id", urlID);
        params.put("activity", activity);
        params.put("cache", "cache");
        params.put("cacheKey", urlID + "_" + activity + "_" + Util.version);
        params.put("cacheTime", "10");
        NewWebAPI.getNewInstance().getWebRequest(
                "/Product.aspx?call=getProductInfo", params,
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", ProductDeatilFream.this);
                            return;
                        }

                        json = JSON.parseObject(result.toString());
                        Log.e("信息", json.toJSONString());
                        Log.e("信息", json.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"),
                                    ProductDeatilFream.this);
                            return;
                        }
                        init();
                    }

                    @Override
                    public void fail(Throwable e) {
                        Util.show("获取商品信息失败，请重试！", ProductDeatilFream.this);
                        super.fail(e);
                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        dialog.cancel();
                        dialog.dismiss();
                    }

                });
        if (data.getStringExtra("shopType")!=null){
            tagredpa.setVisibility(View.GONE);
        }
        else if (data.getStringExtra("orderType")!=null){
             if (data.getStringExtra("orderType").equals("7")){
                tagredpa.setVisibility(View.GONE);
            }
        }
        else {
            tagredpa.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 添加购物车
     */
    @OnClick(R.id.product_detail_addcar)
    public void addCarClick(View v) {
        shopping(true, "正在加入购物车...", "加入购物车失败，请重试！");
    }

    /**
     * 立即购买
     *
     * @param v
     */
    @OnClick(R.id.product_detail_shop)
    public void shopClick(View v) {
        shopping(false, "正在购买...", "购买失败，请重试！");
    }

    @OnClick(R.id.product_detail_cart_bg)
    public void cartClick(View v) {
        if (UserData.getUser() == null) {
            Toast.makeText(this, "您还没登录哦，请前去登录", Toast.LENGTH_SHORT).show();
            Util.showIntent(this, LoginFrame.class, new String[]{"finish"}, new String[]{"finish"});
        } else
            Util.showIntent(this, ShopCarFrame.class);
    }

    /**
     * 购买
     * <p>
     * <p>
     * 是否提示跳转
     */
    private void shopping(final boolean isTixing, String message,
                          String errorMessage) {
        if (null == json) {
            Util.show("商品数据错误！", this);
            return;
        }

        if (100 == json.getIntValue("lmsjProduct")) {
            Util.show("该商品不需要购买！", this);
            return;
        }
        if (null == UserData.getUser()) {
            Toast.makeText(this, "您还没登录哦，请前去登录", Toast.LENGTH_SHORT).show();
            Util.showIntent(ProductDeatilFream.this, LoginFrame.class, new String[]{"finish"}, new String[]{"finish"});
            return;
        }
        final StringBuffer colorAndSize = new StringBuffer();
        // 是否有颜色可选
        boolean isHaveColor = null != colors && 0 < colors.length;
        // 是否有尺码可选
        boolean isHaveSize = null != sizes && 0 < sizes.length;
        if (isHaveColor) {
            if (Util.isNull(selColor)) {
                SizeColor(color_size_click_view);
                Util.show("请选择颜色", this);
                return;
            } else
                colorAndSize.append("颜色：" + selColor);
        }
        if (isHaveSize) {
            if (Util.isNull(selSize)) {
                SizeColor(color_size_click_view);
                Util.show("请选择尺码", this);
                return;
            } else {
                if (0 == colorAndSize.length()) {
                    colorAndSize.append("尺码：" + selSize);
                } else
                    colorAndSize.append("|尺码：" + selSize);
            }
        }
        Util.asynTask(ProductDeatilFream.this, message, new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("网络异常，请重试！", ProductDeatilFream.this);
                    return;
                }
                String rd = runData + "";
                Log.e("数据来源", rd + "");
                if (rd.contains("success")) {
                    if (isTixing) {
                        int num = 0;
                        try {
                            Selector selecter = Selector
                                    .from(ShopCarNumberModel.class);
                            selecter.where(WhereBuilder
                                    .b("userid", "=", UserData.getUser()
                                            .getUserIdNoEncodByUTF8()));
                            List<ShopCarNumberModel> list = db
                                    .findAll(selecter);
                            if (null != list) {
                                for (int i = 0; i < list.size(); i++) {
                                    num = list.get(i).getNumber();
                                }
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        ShopCarNumberModel scm = new ShopCarNumberModel();
                        scm.setUserid(Util.getNo_pUserId(UserData.getUser()
                                .getNoUtf8UserId()));
                        scm.setNumber(++num);
                        try {
                            db.save(scm);
                            shopcar_number.setVisibility(View.VISIBLE);
                            shopcar_number.setText(num + "");
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        String ts = "";
                        if (rd.contains("success:")) {
                            ts = "加入购物车成功，活动已过期将会按照原价付款，是否需要去付款？";
                        } else
                            ts = "加入购物车成功，是否需要去付款？";
                        VoipDialog voipDialog = new VoipDialog(ts, ProductDeatilFream.this, "去付款", "再逛逛", new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Util.showIntent(
                                        ProductDeatilFream.this,
                                        ShopCarFrame.class);
                            }
                        }, null);
                        voipDialog.show();
                    } else {
                        if (rd.contains("success:")) {
                            Util.show("活动已过期，将会按照原价付款", ProductDeatilFream.this);
                        }
                        String guid = "'" + rd.replace("success", "") + "'";
                        Intent intent = new Intent(ProductDeatilFream.this,
                                PayCommitFrame.class);
                        intent.putExtra("guid", guid);
                        startActivity(intent);
                    }
                } else
                    Util.show(runData + "", ProductDeatilFream.this);
            }

            @Override
            public Serializable run() {
                Log.e("colorAndSize.toString()", colorAndSize.toString());
                Web web = new Web(Web.addShopCard, "id="
                        + data.getStringExtra("url") + "&uName="
                        + UserData.getUser().getUserId() + "&pwd="
                        + UserData.getUser().getMd5Pwd() + "&colorAndSize="
                        + Util.get(colorAndSize.toString()) + "&pfrom="
                        + Data.getProductClass() + "&amount=" + amount
                        + "&unum=" + unum + "&activity=" + activity);
                return web.getPlan();
            }
        });
    }

    @SuppressLint("NewApi")
    private void bindPriceAndName() {
        // phone_name= (TextView) this.findViewById(R.id.lorem_ipsum);// 商品名称
        final TextView tv_name_down = (TextView) this
                .findViewById(R.id.tv_phone_name_develop);
        final TextView tv_name_up = (TextView) this
                .findViewById(R.id.tv_phone_name_up);
        TextView ydprice = (TextView) this.findViewById(R.id.ydprice);// 远大售价
        TextView market_price = (TextView) this.findViewById(R.id.market_price);// 市场售价
        TextView cons_price = (TextView) this.findViewById(R.id.cons_price);// 积分价格
        TextView sb_price = (TextView) this.findViewById(R.id.sb_price);// 商币价格
        TextView storege = (TextView) this.findViewById(R.id.storege);// 库存
        String s = json.getString("name") + json.getString("shortTitle");
        final SpannableString ss = new SpannableString(s);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        ss.setSpan(span, json.getString("name").length(), s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // phone_name.setText(ss);

        ViewTreeObserver vto = phone_name.getViewTreeObserver();
        vto.addOnPreDrawListener(new OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                if (!b) {

                    phone_name.setText(ss);

                    b = true;
                }
                return true;
            }
        });
        phone_name.postDelayed(new Runnable() {

            @Override
            public void run() {
                lines = phone_name.getLineCount();

                if (lines > 3) {
                    phone_name.setMaxLines(3);
                    phone_name.setEllipsize(TruncateAt.END);
                    tv_phone_name_develop.setVisibility(View.VISIBLE);

                    phone_name.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            switch (tag) {
                                case 1:
                                    phone_name.setMaxLines(lines);
                                    phone_name.setText(ss);
                                    phone_name.postInvalidate();
                                    tv_name_down.setVisibility(View.GONE);
                                    tv_name_up.setVisibility(View.VISIBLE);

                                    tag = 2;
                                    break;
                                case 2:
                                    phone_name.setMaxLines(3);
                                    phone_name.setEllipsize(TruncateAt.END);
                                    phone_name.setText(ss);
                                    phone_name.postInvalidate();
                                    tv_name_down.setVisibility(View.VISIBLE);
                                    tv_name_up.setVisibility(View.GONE);
                                    tag = 1;
                                    break;
                                default:
                                    break;
                            }

                        }
                    });
                    tv_phone_name_develop
                            .setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    phone_name.setMaxLines(lines);
                                    phone_name.setText(ss);
                                    phone_name.postInvalidate();
                                    tv_name_down.setVisibility(View.GONE);
                                    tv_name_up.setVisibility(View.VISIBLE);

                                }

                            });
                    tv_name_up.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            phone_name.setMaxLines(3);
                            phone_name.setEllipsize(TruncateAt.END);
                            phone_name.setText(ss);
                            phone_name.postInvalidate();
                            tv_name_down.setVisibility(View.VISIBLE);
                            tv_name_up.setVisibility(View.GONE);
                        }
                    });
                }
            }

        }, 1000);

        currPrice = json.getString("price");
        hb=Double.valueOf( json.getString("price"))*0.25;
        ydprice.setText("￥" + json.getString("price"));
        givered.setText("赠送红包种子 : " + hb);
        if (100 == json.getIntValue("lmsjProduct")) {
            shopCardLine.setVisibility(View.GONE);
            ((FrameLayout.LayoutParams) scroll.getLayoutParams()).setMargins(0,
                    0, 0, 0);
        }
        market_price.getPaint().setFlags(
                Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);// 设置中划线
        market_price.setText("市场售价：￥" + json.getString("priceMarket"));
        if (100 == json.getIntValue("lmsjProduct"))
            sb_price.setText(json.getString("price"));
        else
            sb_price.setText(json.getString("sbj"));
        cons_price.setText("" + json.getString("jf"));

        if (json.getString("jf").equals("0.00") || json.getString("jf").equals("0")) {
            tagredpa.setVisibility(View.GONE);
            llky.setVisibility(View.GONE);
        } else {
            tagredpa.setVisibility(View.VISIBLE);
            llky.setVisibility(View.VISIBLE);
        }

        if (json.getString("jf_show").equals("0")) {
            sfqlin.setVisibility(View.GONE);


        }
        else {
            sfqlin.setVisibility(View.VISIBLE);
        }


        if (data.getStringExtra("shopType")!=null){
            tagredpa.setVisibility(View.GONE);
        }
        else if (data.getStringExtra("orderType")!=null){
            if (data.getStringExtra("orderType").equals("7")){
                tagredpa.setVisibility(View.GONE);
            }
        }
        else {
            tagredpa.setVisibility(View.VISIBLE);
        }


        storege.setText("商品库存：" + json.getString("stocks"));

    }

    private SpannableString spannYellow(String spam, String s) {
        SpannableString price_mar = new SpannableString(spam + s);
        ForegroundColorSpan span = new ForegroundColorSpan(
                Color.parseColor("#fdac2d"));
        price_mar.setSpan(span, spam.length(), price_mar.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return price_mar;
    }

    // 显示商品图片
    private void initProductImages() {
        String picUrl = json.getString("thumb");
        if (!Util.isNull(picUrl))
            if (picUrl.contains("img.mall666.com"))
                picUrl = picUrl.replaceFirst("img.mall666.com", Web.imgServer);

        JSONObject[] imageList = null;
        if (null != json.getJSONArray("thumbList"))
            imageList = json.getJSONArray("thumbList").toArray(
                    new JSONObject[]{});
        else
            imageList = new JSONObject[]{};
        for (JSONObject item : imageList) {
            final ImageView imageView = new ImageView(ProductDeatilFream.this);
            imageView.setScaleType(ScaleType.FIT_XY);
            com.mall.util.BitmapUtils.loadBitmap(item.getString("big"), imageView, 0, 0, new Callback() {

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    imageView
                            .setImageResource(R.drawable.no_get_banner);
                }
            });
            picViews.add(imageView);
        }
        final ViewPager picPager = (ViewPager) this
                .findViewById(R.id.product_detail_pager);
        picPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        scroll.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        scroll.requestDisallowInterceptTouchEvent(false);
                        break;
                    default:
                        scroll.requestDisallowInterceptTouchEvent(false);
                        break;

                }
                return false;
            }
        });
        product_banner.setVisibility(View.VISIBLE);
        product_banner.setText("1/" + picViews.size());
        picPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                picPager.setCurrentItem(arg0);
                product_banner.setText((arg0 + 1) + "/" + picViews.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        picPager.setAdapter(new BannerAdapter());
        picPager.setCurrentItem(0);
    }

    private void getColorAndSize() {
        if (null == json)
            return;
        JSONObject[] jsonColors = json.getJSONArray("colors").toArray(
                new JSONObject[]{});
        JSONObject[] jsonSizes = json.getJSONArray("sizes").toArray(
                new JSONObject[]{});
        List<String> colorAndSize = new ArrayList<String>();
        for (JSONObject color : jsonColors) {
            colorAndSize.add(color.getString("color"));
        }
        colors = colorAndSize.toArray(new String[]{});
        colorAndSize.clear();
        for (JSONObject size : jsonSizes) {
            colorAndSize.add(size.getString("size"));
        }
        sizes = colorAndSize.toArray(new String[]{});
    }

    private TextView backColor = null;
    private TextView backSize = null;

    private void initColorAndSize(final List<String> colororsize,
                                  final LinearLayout container, final String type,
                                  final TextView pricet, final TextView stock) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                Util.dpToPx(this, 25), 1.0f);
        lp.setMargins(10, 5, 10, 5);
        int rows = 0;
        if (colororsize.size() % 3 == 0) {
            rows = colororsize.size() / 3;
        } else {
            rows = colororsize.size() / 3 + 1;
        }
        for (int i = 0; i < rows; i++) {
            LinearLayout layout = new LinearLayout(this);
            LayoutParams ll = new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setLayoutParams(ll);
            for (int k = 0; k < 3; k++) {
                TextView t = new TextView(this);
                t.setLayoutParams(lp);
                t.setEllipsize(TruncateAt.END);
                t.setSingleLine(true);
                t.setTextColor(Color.parseColor("#818181"));
                t.setGravity(Gravity.CENTER);
                if ((i * 3 + k) < colororsize.size()) {// 判断数组下标越界
                    t.setText(colororsize.get(i * 3 + k));
                    if (selSize.equals(colororsize.get(i * 3 + k))) {
                        t.setBackgroundResource(R.drawable.editextborder_focus);
                        t.setTag("choosed");
                    } else if (selColor.equals(colororsize.get(i * 3 + k))) {
                        t.setBackgroundResource(R.drawable.editextborder_focus);
                        t.setTag("choosed");
                    } else {
                        t.setTag("nochoosed");
                        t.setBackgroundResource(R.drawable.editextborder);
                    }
                    t.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView t = (TextView) v;
                            t.setBackgroundResource(R.drawable.editextborder_focus);
                            if (type.equals("size")) {
                                selSize = t.getText().toString();
                                if (null != backSize) {
                                    backSize.setBackgroundResource(R.drawable.editextborder);
                                }
                                backSize = (t);
                            } else if (type.equals("color")) {
                                selColor = t.getText().toString();
                                if (null != backColor) {
                                    backColor
                                            .setBackgroundResource(R.drawable.editextborder);
                                }
                                backColor = (t);
                            }
                            Util.asynTask(ProductDeatilFream.this,
                                    "正在获取产品价格...", new IAsynTask() {
                                        @Override
                                        public void updateUI(
                                                Serializable runData) {
                                            TextView ydprice = (TextView) ProductDeatilFream.this
                                                    .findViewById(R.id.ydprice);// 远大售价
                                            TextView cons_price = (TextView) ProductDeatilFream.this
                                                    .findViewById(R.id.cons_price);// 积分价格
                                            TextView storege = (TextView) ProductDeatilFream.this
                                                    .findViewById(R.id.storege);// 库存
                                            TextView sbPrice = (TextView) ProductDeatilFream.this
                                                    .findViewById(R.id.sb_price); // 商币价
                                            String[] money = (runData + "")
                                                    .split(":");
                                            ydprice.setText("￥" + money[0]);
                                            givered.setText("赠送红包种子 : " + hb);
                                            currPrice = money[0];
                                            pricet.setText("￥" + money[0]);
                                            if (!Util.isNull(money[1])) {
                                                cons_price.setText(""
                                                        + money[1] + "");

                                                if (money[1].equals("0.00") || money[1].equals("0")) {
                                                    tagredpa.setVisibility(View.GONE);
                                                    llky.setVisibility(View.GONE);
                                                } else {
                                                    tagredpa.setVisibility(View.VISIBLE);
                                                    llky.setVisibility(View.VISIBLE);
                                                }
                                                tagredpa.setVisibility(View.GONE);

                                            }
                                            storege.setText("库存:" + money[2]
                                                    + money[3]);
                                            sbPrice.setText(money[5]);
                                            stock.setText(spannYellow("商品库存:",
                                                    money[2] + money[3] + ""));
                                        }

                                        @Override
                                        public Serializable run() {
                                            Web web = new Web(
                                                    Web.getMoneyForColorAndSize,
                                                    "id="
                                                            + data.getStringExtra("url")
                                                            + "&size="
                                                            + Util.get(selSize)
                                                            + "&color="
                                                            + Util.get(selColor)
                                                            + "&activity="
                                                            + activity);
                                            return web.getPlan();
                                        }
                                    });
                        }
                    });
                }
                layout.addView(t);
            }
            container.addView(layout);
        }

    }

    // 商品规格
    private void productGuiGe() {
        JSONObject[] guige = json.getJSONArray("standard").toArray(
                new JSONObject[]{});
        List<ProductStandard> list = new ArrayList<ProductStandard>();
        ProductStandard ps = null;
        for (JSONObject item : guige) {
            ps = new ProductStandard();
            ps.setName(item.getString("name"));
            ps.setValue(item.getString("value"));
            list.add(ps);
            ps = null;
        }
        guigelist = list;
    }

    private void getCommentAndShopCarNumber() {
        product_comments.setText("商品评论(" + json.getString("comments") + ")");
        shopcar_number.setText(json.getString("shopCarNum"));
    }

    private void initProductDetailInfo() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", json.getString("pid"));
        map.put("cache", "cache");
        map.put("cacheKey", urlID + "_" + activity);
        map.put("cacheTime", "10");
        NewWebAPI.getNewInstance().getWebRequest(
                "/Product.aspx?call=getServiceProductInfo", map,
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (Util.isNull(result)) {
                            Util.show("获取商品详细描述失败！", ProductDeatilFream.this);
                            return;
                        }
                        try {
                            detailJson = JSON.parseObject(result.toString());

                            String content = detailJson.getString("productExplain");
                            StringBuilder sb = new StringBuilder();
                            sb.append("<script type=\"text/javascript\">");
                            sb.append("var objs=document.getElementsByTagName(\"img\");");


                            sb.append("for(var i=0;i<objs.length;i++) {");

                            sb.append("if(objs[i].getAttribute(\"title\").indexOf(\"温馨提示\")>-1){");
                            sb.append("objs[i].onclick=function(){" +
                                    "window.videolistner.openImage1(\"toCar\");" +
                                    "}");
                            sb.append("}");
                            sb.append("}");
                            sb.append("</script>");


                            webview.loadDataWithBaseURL(null, content + sb, "text/html", "utf-8",
                                    "");
                        } catch (Exception e) {
                        }

                    }

                    @Override
                    public void fail(Throwable e) {
                        Util.show("获取商品详细描述失败！", ProductDeatilFream.this);
                        super.fail(e);
                    }

                });
    }

    @JavascriptInterface
    public void openImage1(String img) {
        System.out.println("KK1" + img);

        Util.showIntent(
                ProductDeatilFream.this,
                ProductDeatilFream.class,
                new String[]{"url"},
                new String[]{"138962"});


    }


    private void init() {
        if (UserData.getUser() != null && null != data) {
            Selector sel = Selector.from(CollectionsProduct.class);
            sel.where("ownerid", "=", UserData.getUser().getUserId());
            sel.and("productid", "=", data.getStringExtra("url"));
            List<CollectionsProduct> dataList = null;
            try {
                dataList = db.findAll(sel);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (null == dataList)
                dataList = new ArrayList<CollectionsProduct>();
            if (0 != dataList.size()) {
                product_detail_new_collect
                        .setImageResource(R.drawable.product_detail_new_collect_pressed);
            }
        }

        initProductDetailInfo();
        initProductImages();
        // 绑定商品数据
        bindPriceAndName();
        // initView();
        getColorAndSize();
        productGuiGe();
        // AddRecord();
        getCommentAndShopCarNumber();
    }

    private void showMessage(String message) {
        try {
            Dialog dialog = new Dialog(this);
            dialog.setTitle(null);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.tip_phone_account, null);
            TextView text = (TextView) view
                    .findViewById(R.id.dialog_phone_text);
            text.setText(message);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view);
            dialog.show();
        } catch (Exception e) {
            try {
                Dialog dialog = new Dialog(this.getParent());
                dialog.setTitle(null);
                View view = LayoutInflater.from(this).inflate(
                        R.layout.tip_phone_account, null);
                TextView text = (TextView) view
                        .findViewById(R.id.dialog_phone_text);
                text.setText(message);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view);
                dialog.show();
            } catch (Exception e3) {
                Util.show(message, this);
            }
        }
    }

    // 逛商城
    // private void AddRecord() {
    // final User user = UserData.getUser();
    // if (null == user)
    // return;
    // Util.asynTask(new IAsynTask() {
    // @Override
    // public void updateUI(Serializable runData) {
    // LogUtils.e("找商品结果：" + runData);
    // if ((runData + "").contains("success:")) {
    // String[] ayy = (runData + "").split(":");
    // showMessage("找商品赠送话费：" + ayy[1] + "元");
    // }
    // }
    //
    // @Override
    // public Serializable run() {
    // Web web = new Web(Web.AddRecord, "userID=" + user.getUserId() +
    // "&userPaw=" + UserData.getUser().getMd5Pwd() +
    // "&listID=-1&listType=-1&commodityID="
    // + json.getString("pid") + "&commodityName=" +
    // Util.get(json.getString("name")));
    // return web.getPlan();
    // }
    // });
    //
    // }

    private void getPopupWindow() {
        if (distancePopup != null && distancePopup.isShowing()) {
            distancePopup.dismiss();
        }
    }

    /**
     * 新建一个popupwindow实例
     *
     * @param view
     */
    @SuppressLint("InlinedApi")
    private void initpoputwindow(View view) {
        distancePopup = new PopupWindow(view,
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.MATCH_PARENT, true);
        distancePopup.setOutsideTouchable(true);
        distancePopup.setFocusable(true);
        distancePopup.setBackgroundDrawable(new BitmapDrawable());
        distancePopup.setAnimationStyle(R.style.downpopupanimation);
    }

    public class BannerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return picViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            // super.destroyItem(container, position, object);
            ((ViewPager) container).removeView(picViews.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(picViews.get(position));
            return picViews.get(position);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (distancePopup != null && distancePopup.isShowing()) {
                distancePopup.dismiss();
                return true;
            } else {
                ProductDeatilFream.this.finish();
                return true;
            }
        }
        return true;
    }

}
