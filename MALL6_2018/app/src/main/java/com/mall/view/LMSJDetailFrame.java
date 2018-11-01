package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.LMSJDetailAdapter;
import com.lin.component.LMSJGalleryAdapter;
import com.lin.component.LMSJProductAdapter;
import com.mall.model.LMSJComment;
import com.mall.model.LocationModel;
import com.mall.model.Product;
import com.mall.model.ShopM;
import com.mall.model.ShopMInfo;
import com.mall.model.User;
import com.mall.net.UserFavorite;
import com.mall.net.Web;
import com.mall.scan.CaptureActivity;
import com.mall.serving.community.view.floatup.ObservableScrollView;
import com.mall.serving.community.view.picviewpager.PicViewpagerPopup;
import com.mall.serving.voip.view.gridview.NoScrollGridView;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.messageboard.PostedMerchantCommentariesActivity;
import com.mall.yyrg.adapter.MyListView;
import com.mall.yyrg.adapter.YYRGUtil;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class LMSJDetailFrame extends Activity implements KeyboardChangeListener.KeyBoardListener {

    @ViewInject(R.id.lmsj_detail_viewPager)
    private Gallery viewPager;
    @ViewInject(R.id.lmsj_detail_address)
    private TextView address;
    @ViewInject(R.id.lmsj_detail_cate)
    private TextView cate;
    @ViewInject(R.id.lmsj_detail_qq)
    private RadioButton qq;
    @ViewInject(R.id.lmsj_detail_weixin)
    private RadioButton weixin;
    @ViewInject(R.id.lmsj_detail_zone)
    private TextView zone;
    @ViewInject(R.id.lmsj_detail_name)
    private TextView name;
    @ViewInject(R.id.lmsj_detail_dianhua)
    private TextView dianhua;
    @ViewInject(R.id.lmsj_detail_kefu)
    private TextView kefu;
    @ViewInject(R.id.lmsj_detail_newcontent)
    private TextView newContent;
    @ViewInject(R.id.lmsj_detail_juli2)
    private TextView juli2;
    @ViewInject(R.id.lmsj_detail_businessPolicy)
    private TextView lmsj_detail_businessPolicy;
    @ViewInject(R.id.lmsj_detail_scrollView)
    private com.mall.serving.community.view.floatup.ObservableScrollView scrollView;
    // 评论
    @ViewInject(R.id.lmsj_detail_comment_list)
    private LinearLayout lmsj_detail_comment_list;
    // 商币，积分，商币兑换，积分换购
    @ViewInject(R.id.lmsj_detail_zssb)
    private TextView sb;
    @ViewInject(R.id.lmsj_detail_zsjf)
    private TextView jf;
    @ViewInject(R.id.lmsj_detail_sbdh)
    private TextView sbdh;
    @ViewInject(R.id.lmsj_detail_jfhg)
    private TextView jfhg;
    @ViewInject(R.id.lmsj_detail_hb)
    private TextView hb;
    @ViewInject(R.id.lmsj_detail_hbhg)
    private TextView hbhg;
    @ViewInject(R.id.comment_number)
    private TextView comment_number;
    @ViewInject(R.id.lmsj_product_no_data)
    private TextView noProduct;

    public static ShopMInfo thisM;
    @ViewInject(R.id.lmsj_detail_product)
    private LinearLayout productLine;
    @ViewInject(R.id.lmsj_product_no_data)
    private TextView lmsj_product_no_data;
    @ViewInject(R.id.lmsj_detail_product_images)
    private Gallery productImages;


    @ViewInject(R.id.tocommentariestv) //
            View tocommentariestv;

    private String x = "";
    private String y = "";

    public static String locationCity;
    private LMSJDetailAdapter lmsjDetailAdapter;
    private MyListView goods_list;
    private List<Product> allProducts = new ArrayList<Product>();
    @ViewInject(R.id.xianshigengduo)
    private TextView xianshigengduo;

    @ViewInject(R.id.lmsj_comment_comment_bar)
    private View lmsj_comment_comment_bar;

    @ViewInject(R.id.lmsj_comment_comment_value)
    private EditText commentMessage;


    private Context context;

    private KeyboardChangeListener mKeyboardChangeListener;

    private int nowy = 0;
    private int keyboardHeight = 0;

    private int viewhight = 0;

    private String parentID = "-1";

    private String userid1 = "";

    @ViewInject(R.id.totalscorrb)
    private RatingBar totalscorrb;

    @ViewInject(R.id.totalscortv)
    private TextView totalscortv;

    private String ShopType = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.lmsj_detail);
        context = this;
        ViewUtils.inject(this);
        mKeyboardChangeListener = new KeyboardChangeListener(this);
        mKeyboardChangeListener.setKeyBoardListener(this);
        goods_list = (MyListView) findViewById(R.id.goods_list);
        init();

        Spanned fromHtml = Html.fromHtml(
                "<font color='#ff535353'>远大客服：</font><u><font color='#ff535353'>" + Util._400 + "</font></u>");
        kefu.setText(fromHtml);
    }

    @OnClick({R.id.tocommentariestv})
    public void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tocommentariestv:
                if (UserData.getUser() == null) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                    Util.showIntent(LMSJDetailFrame.this, LoginFrame.class, new String[]{"finish"}, new String[]{"finish"});
                    return;
                }
                intent = new Intent(context, PostedMerchantCommentariesActivity.class);
                intent.putExtra("Type", "1");
                intent.putExtra("lmsj", getIntent().getStringExtra("id"));
                intent.putExtra("face", getIntent().getStringExtra("face"));
                intent.putExtra("ShopType", ShopType);
                intent.putExtra("name", name.getText().toString());
                startActivity(intent);
                break;
        }

    }

    @OnClick(R.id.xianshigengduo)
    public void xianshi(View view) {
        Util.showIntent(this, LMSJDetailGoodsList.class);
    }

    @OnClick(R.id.lmsj_detail_kefu)
    public void kefuClick(View v) {
        Util.doPhone(Util._400, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showMM() {
        juli2.setVisibility(TextView.VISIBLE);
        // 我操，高德地图jsAPI的经纬度和AndroidAPI的经纬度是反的。我操
        LatLng remote = new LatLng(Double.parseDouble(y), Double.parseDouble(x));
        LocationModel locationModel = LocationModel.getLocationModel();
        if (!Util.isNull(locationModel.getCity())) {
            float distanceM = AMapUtils.calculateLineDistance(
                    new LatLng(locationModel.getLatitude(), locationModel.getLongitude())
                    , remote);
            juli2.setText("从【我这里】到目的地大概距离【" + getMM(distanceM) + "】");
        }

    }

    private Handler handler = new Handler();

    /**
     * 滑动到指定位置
     */
    private void scrollToPosition() {
        handler.post(new Runnable() {

            @Override
            public void run() {
                Log.e("滑动", (nowy + keyboardHeight - viewhight) + "LL");
                if ((nowy + keyboardHeight - viewhight) < 0) {
                    return;
                }
                scrollView.scrollTo(nowy, (nowy + keyboardHeight - viewhight - 100));
            }
        });
    }

    private void init() {

        scrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
//				Log.e("滑动","x"+x+"y"+y+"oldx"+oldx+"oldy"+oldy);

                nowy = y;
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(commentMessage.getWindowToken(), 0);
                lmsj_comment_comment_bar.setVisibility(View.GONE);
                return false;
            }
        });


        final String id = getIntent().getStringExtra("id");
        final String name1 = getIntent().getStringExtra("name");
        x = getIntent().getStringExtra("x");
        y = getIntent().getStringExtra("y");
        Util.initTitle(this, "商家详情", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                fenxiangClick();
            }
        }, R.drawable.lmsj_detail_fenxiang, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showIntent(LMSJDetailFrame.this, CaptureActivity.class);
            }
        }, R.drawable.erweima);
        int _5dp = Util.dpToPx(LMSJDetailFrame.this, 5F);
        viewPager.setSpacing(_5dp);
        viewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        // 去掉scrollView对手势的监听
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        Util.asynTask(this, "正在获取商家信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                System.out.println("------------runData====" + runData);

                final ShopMInfo m = (ShopMInfo) runData;
                if (null == m || Util.isNull(m.getId())) {
                    Util.show("网络错误，联盟商家获取失败！", LMSJDetailFrame.this);
                    return;
                }
                thisM = m;

                userid1 = m.getUserid();

                cate.setText(Html.fromHtml("行业类别：" + "<font color=\"#9c9c9c\">" + m.getCate()
                        + "</font>"));
                ShopType = m.getCate();

                qq.setText(m.getQq());
                weixin.setText(m.getWeixin());

                address.setText(Html.fromHtml("详细地址：" + "<font color=\"#9c9c9c\">" + m.getAddress()
                        + "</font>"));

                zone.setText(Html.fromHtml("所在城市：" + "<font color=\"#9c9c9c\">" + m.getZone()
                        + "</font>"));
                y = m.getLat();
                x = m.getLng();
                Spanned fromHtml = Html.fromHtml("<font color='#ff535353'>商家电话：</font><u><font color='#ff535353'>"
                        + m.getDianhua() + "</font></u>");
                dianhua.setText(fromHtml);
                dianhua.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.doPhone(m.getDianhua(), LMSJDetailFrame.this);
                    }
                });
                name.setText(m.getName());
                setLinkClickIntercept(newContent);
                String zhichi2 = m.getZhichi2();
                Resources res = LMSJDetailFrame.this.getResources();
                Drawable zhichi = res.getDrawable(R.drawable.isbuzhichi);
                Drawable buzhichi = res.getDrawable(R.drawable.buzhichi);
                zhichi.setBounds(0, 0, zhichi.getMinimumWidth(), zhichi.getMinimumHeight());
                buzhichi.setBounds(0, 0, buzhichi.getMinimumWidth(), buzhichi.getMinimumHeight());
                sb.setCompoundDrawables(buzhichi, null, null, null);
                jf.setCompoundDrawables(buzhichi, null, null, null);
                sbdh.setCompoundDrawables(buzhichi, null, null, null);
                jfhg.setCompoundDrawables(buzhichi, null, null, null);
                hb.setCompoundDrawables(buzhichi, null, null, null);
                hbhg.setCompoundDrawables(buzhichi, null, null, null);
                if (!Util.isNull(zhichi2) && "1".equals(thisM.getShopcstate())) {
                    String[] zc2 = zhichi2.split(",");
                    for (String tag : zc2) {
                        if (tag.equals(sb.getTag() + "")) {
                            sb.setCompoundDrawables(zhichi, null, null, null);
                        }
                        if (tag.equals(jf.getTag() + "")) {
                            jf.setCompoundDrawables(zhichi, null, null, null);
                        }
                        if (tag.equals(sbdh.getTag() + "")) {
                            sbdh.setCompoundDrawables(zhichi, null, null, null);
                        }
                        if (tag.equals(jfhg.getTag() + "")) {
                            jfhg.setCompoundDrawables(zhichi, null, null, null);
                        }
                        if (tag.equals(hb.getTag() + "")) {
                            hb.setCompoundDrawables(zhichi, null, null, null);
                        }
                        if (tag.equals(hbhg.getTag() + "")) {
                            hbhg.setCompoundDrawables(zhichi, null, null, null);
                        }
                    }
                }
                showMM();
                initComment();
                initLMSJImage();
                String content = m.getContent();
                if ("1".equals(thisM.getIsCooper())) {
                    initBusinessPolicy();

                    //赵超2017.5.8日修改该内容(取消该方法调用，不显示"正在获取商品信息..."提示信息)
                    initLMSJProduct();

                } else {
                    lmsj_detail_businessPolicy.setText("此商家正在洽谈合作中，敬请期待……");
                    lmsj_detail_businessPolicy.setTextColor(ColorStateList.valueOf(Color.RED));
                    lmsj_product_no_data.setVisibility(View.VISIBLE);
                    lmsj_product_no_data.setText("商家还没上传商品哦，给他打电话提示一下吧！");
                    content += "<br/><font color='red'>温馨提示：此商家正在洽谈合作中，如果你觉得此商家值得推荐，请拨打400-666-3838推荐，谢谢！</font>";

                    // noProduct.setVisibility(View.GONE);
                    // DisplayMetrics dm=new DisplayMetrics();
                    // getWindowManager().getDefaultDisplay().getMetrics(dm);
                    // int width=dm.widthPixels;
                    // List<Product> products=new ArrayList<Product>();
                    // Product pp = new Product();
                    // pp.setName("特色商品示例");
                    // pp.setSbj("1000");
                    // pp.setPriceMarket("200");
                    // pp.setThumb(thisM.getLogo());
                    // products.add(pp);
                    // allProducts=products;
                    // YYRGUtil.allProducts=products;
                    // xianshigengduo.setVisibility(View.GONE);
                    // lmsjDetailAdapter=new LMSJDetailAdapter(products,
                    // LMSJDetailFrame.this, width);
                    goods_list.setAdapter(lmsjDetailAdapter);

                }
                newContent.setText(Html.fromHtml("<html><body>" + content + "</body></html>"));
            }

            @Override
            public Serializable run() {
                return new Web(Web.getShopMInfo, "id=" + id).getObject(ShopMInfo.class);
            }
        });
    }

    private void setLinkClickIntercept(TextView tv) {
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = tv.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) tv.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();// should clear old spans
            for (URLSpan url : urls) {
                if (-1 != url.getURL().indexOf("shopCollects/shopCollectsPage.aspx?cid=")) {
                    MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
                    style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                }
            }
            tv.setText(style);
        }
    }

    @Override
    public void onKeyboardChange(boolean isShow, int keyboardHeight) {

        Log.e("键盘监听", isShow + "HHHH" + keyboardHeight);
        this.keyboardHeight = keyboardHeight;
        scrollToPosition();


    }

    /**
     * 处理TextView中的链接点击事件 链接的类型包括：url，号码，email，地图 这里只拦截url，即 http:// 开头的URI
     */
    private class MyURLSpan extends ClickableSpan {
        private String mUrl; // 当前点击的实际链接

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            int index = mUrl.indexOf("?");
            if (-1 != index) {
                String urlParameter = mUrl.substring(index + 1);
                String[] args = urlParameter.split("&");
                String id = "";
                String x = "";
                String y = "";
                for (String arg : args) {
                    if (arg.startsWith("cid=")) {
                        id = arg.split("=")[1];
                    }
                    if (arg.startsWith("x=")) {
                        x = arg.split("=")[1];
                    }
                    if (arg.startsWith("y=")) {
                        y = arg.split("=")[1];
                    }

                }
                final String _id = id;
                final String lat_x = x;
                final String lng_y = y;
                if (Util.isInt(id) && !Util.isNull(x) && !Util.isNull(y)) {
                    widget.setBackgroundColor(Color.parseColor("#00000000"));
                    Util.asynTask(LMSJDetailFrame.this, "正在为您跳转...", new IAsynTask() {

                        @Override
                        public void updateUI(Serializable runData) {
                            Util.showIntent(LMSJDetailFrame.this, LMSJDetailFrame.class,
                                    new String[]{"id", "name", "x", "y"},
                                    new String[]{_id, "商家详情", lat_x, lng_y});
                        }

                        @Override
                        public Serializable run() {
                            return null;
                        }
                    });
                } else {
                    Util.openWeb(LMSJDetailFrame.this, mUrl);
                }
            }
        }
    }

    @OnClick(R.id.lmsj_product_no_data)
    public void noProductClick(View view) {
        if (!Util.isNull(thisM.getDianhua())) {
            String[] dianhuas = thisM.getDianhua().split("、|，|,|\\|/");
            String dh = "";
            if (0 < dianhuas.length)
                dh = dianhuas[0];
            if (Util.isNull(dh)) {
                Util.show("商家联系电话错误！", this);
            }
            Util.doPhone(dh.replaceAll("-", ""), this);
        } else {
            Util.show("对不起，该商家未设置联系电话！", this);
        }
    }

    /**
     * 初始化联盟商家商品信息
     */
    private void initLMSJProduct() {
//		Util.asynTask(this, "正在获取商品信息...", new IAsynTask() {
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<Product>> map = (HashMap<String, List<Product>>) runData;
                List<Product> list = map.get("list");
                if (null == list || 0 == list.size()) {
                    noProduct.setVisibility(View.VISIBLE);
                    return;
                }
                noProduct.setVisibility(View.GONE);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int width = dm.widthPixels;
                allProducts = list;
                List<Product> products = new ArrayList<Product>();
                YYRGUtil.allProducts = list;
                if (list.size() <= 2) {
                    products = list;
                    xianshigengduo.setVisibility(View.GONE);
                } else {
                    xianshigengduo.setVisibility(View.VISIBLE);
                    for (int i = 0; i < 3; i++) {
                        products.add(list.get(i));
                    }
                }
                Log.e("产品信息", products.get(0).getName() + "图片" + products.get(0).getThumb());

                lmsjDetailAdapter = new LMSJDetailAdapter(products, LMSJDetailFrame.this, width);
                goods_list.setAdapter(lmsjDetailAdapter);
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getServiceProduct, "shopMID=" + thisM.getId());
                HashMap<String, List<Product>> map = new HashMap<String, List<Product>>();
                map.put("list", web.getList(Product.class));
                return map;
            }
        });

        productImages.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LMSJProductAdapter adapter = (LMSJProductAdapter) productImages.getAdapter();
                Product pro = (Product) adapter.getItem(position);
                Util.showIntent(LMSJDetailFrame.this, ProductDeatilFream.class, new String[]{"url"},
                        new String[]{pro.getPid()});
            }
        });
    }

    /**
     * 初始化联盟商家图片
     */
    private void initLMSJImage() {
        viewPager.setAdapter(new LMSJGalleryAdapter(this, thisM, viewPager));
        viewPager.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] imgs = thisM.getImages().split("\\|凸\\|");
                if (null != imgs && imgs.length > 0) {
                    Intent intent = new Intent();
                    intent.setClass(LMSJDetailFrame.this, LMSJShowImageFrame.class);
                    intent.putExtra("name", thisM.getName());
                    intent.putExtra("imgs", imgs);
                    LMSJDetailFrame.this.startActivity(intent);
                } else
                    Util.show("该商家没有图集！", LMSJDetailFrame.this);
            }
        });
    }

    /**
     * 初始化商家政策
     */
    private void initBusinessPolicy() {
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null != runData) {
                    LMSJComment obj = (LMSJComment) runData;
                    if (!Util.isNull(obj.getContent()))
                        lmsj_detail_businessPolicy.setText(Html.fromHtml(obj.getContent()));
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getLMSJBusinessPolicy, "id=" + thisM.getId());
                return web.getObject(LMSJComment.class);
            }
        });
    }


    private void initComment() {
        // 获取评论
        Util.asynTask1(this, "正在获取网友评论...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null != runData) {
                    HashMap<String, List<LMSJComment>> map = (HashMap<String, List<LMSJComment>>) runData;
                    List<LMSJComment> list = map.get("list");
                    comment_number.setText("会员评价(" + list.size() + ")");
                    LayoutInflater flater = LayoutInflater.from(LMSJDetailFrame.this);
                    lmsj_detail_comment_list.removeAllViews();
                    lmsj_detail_comment_list.removeAllViewsInLayout();
                    if (list.size() > 2) {
                        for (int i = 0; i < 3; i++) {
                            final LMSJComment comment = list.get(i);
                            View root = flater.inflate(R.layout.lmsj_detail_comment_list_item, null);
                            ImageView usercommentface = (ImageView) root.findViewById(R.id.usercommentfaceiv);
                            Picasso.with(context).load(comment.getUser_face()).error(R.drawable.headertwo).into(usercommentface);
                            TextView callbackmessage = (TextView) root.findViewById(R.id.callbackmessage);
                            if (!comment.getExp4().equals("")) {
                                callbackmessage.setText(comment.getExp4());
                            } else {
                                callbackmessage.setVisibility(View.GONE);
                            }
//							com.mall.serving.community.view.gridview.NoScrollGridView
//							com.mall.serving.voip.view.gridview.NoScrollGridView
                            NoScrollGridView csg_images = (NoScrollGridView) root.findViewById(R.id.csg_images);
                            TextView name = (TextView) root.findViewById(R.id.lmsj_detal_comment_list_item_name);
                            RatingBar rat = (RatingBar) root.findViewById(R.id.lmsj_detal_comment_list_item_ratingbar);
                            TextView star = (TextView) root.findViewById(R.id.lmsj_detal_comment_list_item_star);
                            final TextView content = (TextView) root.findViewById(R.id.lmsj_detal_comment_list_item_comment);
                            String userid = comment.getUser();
                            name.setText(Util.getNo_pUserId(userid));
                            rat.setRating(Float.parseFloat(comment.getScore()));
//							star.setText(comment.getScore());
                            star.setText(comment.getDate());

                            content.setText(comment.getContent());

                            String imagefiles = comment.getFiles();
                            if (!imagefiles.equals("")) {
                                String[] imags = imagefiles.split("\\*\\|-_-\\|\\*");
                                Log.e("图片长度1", imags.length + "LLLL");
                                System.out.println(Arrays.toString(imags));
                                Log.e("打印地址", Arrays.toString(imags) + "LLL");
                                final List<String> piclist = Arrays.asList(imags);
                                Log.e("图片长度22", piclist.size() + "LLLL");
                                csg_images.setVisibility(View.VISIBLE);

                                csg_images.setAdapter(new BaseAdapter() {
                                    @Override
                                    public int getCount() {
                                        return piclist.size();
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
                                                .load("http://img.yda360.com/" + piclist.get(position))
                                                .error(R.drawable.ic_launcher)
                                                .placeholder(R.drawable.ic_launcher)
                                                .into(imageView);
                                        return view;
                                    }
                                });
                                csg_images.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        ArrayList<String> arrayList = new ArrayList<String>();
                                        for (int i = 0; i < piclist.size(); i++) {
                                            arrayList.add("http://img.yda360.com/" + piclist.get(i));
                                        }
                                        new PicViewpagerPopup(context, arrayList, position, true, null);
                                    }
                                });
                            } else {
                                csg_images.setVisibility(View.GONE);
                            }


                            lmsj_detail_comment_list.addView(root);
                            root.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    User tempUser = UserData.getUser();
                                    if (tempUser == null) {
                                        return;
                                    }
                                    if (!userid1.equals(Util.getNo_pUserId(tempUser.getNoUtf8UserId()))) {
                                        return;
                                    }

                                    if (!comment.getExp4().equals("")) {
                                        return;
                                    }


                                    lmsj_comment_comment_bar.setVisibility(View.VISIBLE);
                                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                                    ViewTreeObserver vto2 = view.getViewTreeObserver();
                                    vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                            viewhight = view.getHeight();
                                        }
                                    });
                                    parentID = comment.getId();

                                }
                            });

                        }
                    } else {
                        for (final LMSJComment comment : list) {
                            View root = flater.inflate(R.layout.lmsj_detail_comment_list_item, null);
                            TextView name = (TextView) root.findViewById(R.id.lmsj_detal_comment_list_item_name);
                            RatingBar rat = (RatingBar) root.findViewById(R.id.lmsj_detal_comment_list_item_ratingbar);
                            TextView callbackmessage = (TextView) root.findViewById(R.id.callbackmessage);
//							com.mall.serving.community.view.gridview.NoScrollGridView
                            GridView csg_images = (GridView) root.findViewById(R.id.csg_images);
                            if (!comment.getExp4().equals("")) {
                                callbackmessage.setText(comment.getExp4());
                            } else {
                                callbackmessage.setVisibility(View.GONE);
                            }

                            TextView star = (TextView) root.findViewById(R.id.lmsj_detal_comment_list_item_star);
                            TextView content = (TextView) root.findViewById(R.id.lmsj_detal_comment_list_item_comment);
                            String userid = comment.getUser();
                            name.setText(Util.getNo_pUserId(userid));
                            rat.setRating(Float.parseFloat(comment.getScore()));
//							star.setText(comment.getScore());
                            star.setText(comment.getDate());
                            content.setText(comment.getContent());


                            String imagefiles = comment.getFiles();
                            if (!imagefiles.equals("")) {
                                String[] imags = imagefiles.split("\\*\\|-_-\\|\\*");
                                Log.e("图片长度1", imags.length + "LLLL");
                                Log.e("打印地址", Arrays.toString(imags) + "LLL");
                                final List<String> piclist = Arrays.asList(imags);
                                Log.e("图片长度2", piclist.size() + "LLLL");
                                csg_images.setVisibility(View.VISIBLE);
                                csg_images.setAdapter(new BaseAdapter() {
                                    @Override
                                    public int getCount() {
                                        return piclist.size();
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
                                                .load("http://img.yda360.com/" + piclist.get(position))
                                                .error(R.drawable.ic_launcher)
                                                .placeholder(R.drawable.ic_launcher)
                                                .into(imageView);
                                        return view;
                                    }
                                });

                                csg_images.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        ArrayList<String> arrayList = new ArrayList<String>();
                                        for (int i = 0; i < piclist.size(); i++) {
                                            arrayList.add("http://img.yda360.com/" + piclist.get(i));
                                        }
//										com.mall.view.picviewpager
                                        new PicViewpagerPopup(context, arrayList, position, true, null);
                                    }
                                });
                            } else {
                                csg_images.setVisibility(View.GONE);
                            }


                            lmsj_detail_comment_list.addView(root);
                            root.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    User tempUser = UserData.getUser();
                                    if (tempUser == null) {
                                        return;
                                    }
                                    if (!userid1.equals(Util.getNo_pUserId(tempUser.getNoUtf8UserId()))) {
                                        return;
                                    }
                                    if (!comment.getExp4().equals("")) {
                                        return;
                                    }


                                    lmsj_comment_comment_bar.setVisibility(View.VISIBLE);
                                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                                    ViewTreeObserver vto2 = view.getViewTreeObserver();
                                    vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                            viewhight = view.getHeight();
                                        }
                                    });
                                    parentID = comment.getId();

                                }
                            });
                        }
                    }

                    int allnumber = 0;

                    for (int i = 0; i < list.size(); i++) {
                        allnumber += Float.parseFloat(list.get(i).getScore());
                    }

//					Log.e("LLL",(allnumber/list.size())+"LLL");


                    try {
                        totalscorrb.setRating(allnumber / list.size());

                        float price = allnumber / list.size();
                        DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                        totalscortv.setText(decimalFormat.format(price) + "分");
                    } catch (Exception e) {

                    }


                    list.clear();
                    list = null;
                    map.clear();
                    map = null;
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getLMSJCommentPage, "page=1&size=9999&id=" + thisM.getId());
                HashMap<String, List<LMSJComment>> map = new HashMap<String, List<LMSJComment>>();
                map.put("list", web.getList(LMSJComment.class));
                return map;
            }
        });
    }

    @OnClick(R.id.lmsj_detail_mysc)
    public void myscClick(View v) {

        UserFavorite.addFavorite(LMSJDetailFrame.this, thisM.getId() + "", "0", new UserFavorite.CallBacke() {
            @Override
            public void doback(String message) {

            }


        });
//        Util.asynTask(this, "正在加入收藏...", new IAsynTask() {
//            @Override
//            public void updateUI(Serializable runData) {
//                if (null != runData) {
//                    ShopM m = (ShopM) runData;
//                    if (null != UserData.getUser()) {
//                        if (Data.addMyShopMSc(LMSJDetailFrame.this, m))
//                            Util.show("收藏成功！", LMSJDetailFrame.this);
//                        else
//                            Util.show("收藏失败！", LMSJDetailFrame.this);
//                    } else
//                        Util.show("请先登录，再收藏", LMSJDetailFrame.this);
//                }
//            }
//
//            @Override
//            public Serializable run() {
//                Web web = new Web(Web.getShopMID, "id=" + thisM.getId());
//                return web.getObject(ShopM.class);
//            }
//        });
    }

    @OnClick(R.id.lmsj_detail_gotothat)
    public void gotoThat(View v) {
        if (null == thisM) {
            Util.show("还未获取到联盟商家信息...", this);
            return;
        }
        LocationModel locationModel = LocationModel.getLocationModel();

        if (Util.isNull(locationModel.getCity())) {
            Util.show("暂未获取到您的定位！", this);
            return;
        }
        ShopM shopM = new ShopM();
        shopM.setId(thisM.getId());
        shopM.setPointX(thisM.getLng());
        shopM.setPointY(thisM.getLat());
        shopM.setName(thisM.getName());
        shopM.setZone(thisM.getZone());
        Util.doDaoHang(this, shopM);
    }

    @OnClick(R.id.lmsj_detail_fujinde)
    public void fujinde(View v) {
        Util.showIntent(this, MapLMSJFrame.class);
    }

    private String getMM(float distanceM) {
        String distanceValue = distanceM + "";
        if (distanceM > 1000F) {
            distanceValue = Util.getDouble(Double.valueOf((distanceM / 1000F) + ""), 3) + "公里";
        } else {
            distanceValue = Util.getDouble(Double.valueOf(distanceM + ""), 3) + "米";
        }
        distanceValue = distanceValue.replaceFirst("\\.00", "");
        return distanceValue;
    }

    @OnClick(R.id.lmsj_comment)
    public void commentClick(View v) {
        Util.showIntent(this, LMSJCommentFrame.class, new String[]{"lid", "userid1"},
                new String[]{getIntent().getStringExtra("id"), userid1});
    }

    public void fenxiangClick() {
        String[] imgs = thisM.getImages().split("\\|凸\\|");
        final OnekeyShare oks = new OnekeyShare();
        final String url = "http://" + Web.webImage + "/shopCollects/shopCollectsPage.aspx?cid=" + thisM.getId();
        String name = thisM.getLogo().substring(thisM.getLogo().lastIndexOf("."));
        String imageUrl = thisM.getLogo();

        Log.e("原地址", thisM.getZone());
         String title="";
        try {
           title = thisM.getName() + "【" + thisM.getZone().split(" ")[0].replace("省","").replace("市","") + "·"
                    + thisM.getZone().split(" ")[1].replace("市","") + "】";
        }catch (Exception e){

        }

        Log.e("地址", title);
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setUrl(url);
        oks.setAddress("10086");
        oks.setComment("不错的一个商家：" + thisM.getName());
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
            public void onShare(Platform platform, ShareParams paramsToShare) {
                if ("ShortMessage".equals(platform.getName())) {
                    paramsToShare.setImageUrl(null);
                    paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());
                }
            }
        });
        oks.show(this);
    }

    @OnClick(R.id.lmsj_comment_comment_submit)
    public void submitClick(View v) {
        lmsj_comment_comment_bar.setVisibility(View.GONE);
        if (null == UserData.getUser()) {
            Util.showIntent("您还没登录是否登录？", this, LoginFrame.class);
            return;
        }
        if (Util.isNull(commentMessage.getText().toString())) {
            Util.show("请输入要评论的内容", this);
            return;
        }
        Util.asynTask(this, "正在发表您的评论...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData)
                    Util.show("评论超时，请重试", context);
                else if ("success".equals(runData + "")) {
                    initComment();
                } else
                    Util.show(runData + "", context);
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.addLMSJComment, "userid="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&lmsj=" + getIntent().getStringExtra("id") + "&message="
                        + Util.get(commentMessage.getText().toString()) + "&rating="
                        + 0 + "&parentID=" + parentID + "&files=" + "");
                return web.getPlan();
            }
        });
    }


}
