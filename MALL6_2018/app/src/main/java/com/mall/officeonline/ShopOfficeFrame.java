package com.mall.officeonline;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.AlbumClassifyModel;
import com.mall.model.OfficeProduct;
import com.mall.model.ShopOfficeInfo;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.App;
import com.mall.view.BMFWFrane;
import com.mall.view.LoginFrame;
import com.mall.view.ProductDeatilFream;
import com.mall.view.ProxySiteFrame;
import com.mall.view.R;
import com.mall.view.StoreFrame;

public class ShopOfficeFrame extends Activity {
    private BitmapUtils bmUtils;
    @ViewInject(R.id.layer)
    private LinearLayout layer;
    @ViewInject(R.id.logo)
    private ImageView OfficeLogo;
    @ViewInject(R.id.name)
    private TextView name;
    @ViewInject(R.id.visitors)
    private TextView Visitors;
    @ViewInject(R.id.gridview)
    private GridView Gridv;
    @ViewInject(R.id.baobei)
    private TextView BaoBei;
    @ViewInject(R.id.notes)
    private TextView Notes;
    @ViewInject(R.id.vedios)
    private TextView Vedios;
    @ViewInject(R.id.album)
    private TextView Albums;
    @ViewInject(R.id.office_banner)
    private ImageView OfficeBanner;
    @ViewInject(R.id.rank_container)
    private LinearLayout rank_container;
    private int page = 1;
    public static final int PAGE_SIZE = 10;
    public ItemAdapter adapter;
    private User user;
    private ShopOfficeInfo shopoffice;
    private int _50dp = 50;
    private PopupWindow distancePopup = null;
    private String userNo = "";
    private String names = "";
    private int crown = 0, sun = 0, moon = 0, star = 0;
    @ViewInject(R.id.topright)
    private ImageView topright;
    private String officeid = "";
    private String from = "";
    private String officeUserNo = "";
    private String id = "";
    private String my = "";

    @OnClick(R.id.store)
    public void Store(View v) {
        store(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_office_frame);
        ViewUtils.inject(this);
        bmUtils = new BitmapUtils(this);
        userNo = this.getIntent().getStringExtra("userNo");
        my = this.getIntent().getStringExtra("my");
        if (TextUtils.isEmpty(my)) {
            crown = this.getIntent().getIntExtra("crown", 0);
            sun = this.getIntent().getIntExtra("sun", 0);
            moon = this.getIntent().getIntExtra("moon", 0);
            star = this.getIntent().getIntExtra("star", 0);
            names = this.getIntent().getStringExtra("name");
            officeid = this.getIntent().getStringExtra("offid");
            from = this.getIntent().getStringExtra("from");

            id = this.getIntent().getStringExtra("officeid");
        }

        if (TextUtils.isEmpty(names)) {
            names = "我的空间";
        }
        if (names.length() > 6) {
            names = names.substring(0, 6) + "...";
        }
        Util.initTitle(this, names, new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }

    private void store(final String officesId) {
        if (UserData.getUser() != null) {
            final User user = UserData.getUser();
            Util.asynTask(ShopOfficeFrame.this, "", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (runData != null) {
                        if ("success".equals(runData + "")) {
                            Toast.makeText(ShopOfficeFrame.this, "收藏成功",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.officeUrl, Web.favoriteOffices,
                            "userId=" + user.getUserId() + "&md5Pwd="
                                    + user.getMd5Pwd() + "&officesId="
                                    + officesId);
                    return web.getPlan();
                }
            });
        } else {
            Util.showIntent(this, LoginFrame.class);
        }
    }

    public static void clacluate(LinearLayout Layout, Context c, int crown,
                                 int sun, int moon, int star) {
        int size = crown + sun + moon + star;
        int[] ione = new int[size];
        for (int i = 0; i < crown; i++) {
            ione[i] = R.drawable.hg;
        }
        for (int i = 0; i < sun; i++) {
            ione[i + crown] = R.drawable.hg;
        }
        for (int i = 0; i < moon; i++) {
            ione[i + crown + sun] = R.drawable.diamond;
        }
        for (int i = 0; i < star; i++) {
            ione[i + crown + sun + moon] = R.drawable.red_heart;
        }
        addIndexImage(Layout, ione);
    }

    private static void addIndexImage(LinearLayout Layout, int[] res) {
        Layout.removeAllViews();
        LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        l.setMargins(0, 0, 0, 0);
        int[] rank = res;
        for (int i = 0; i < rank.length; i++) {
            ImageView img = new ImageView(App.getContext());
            img.setLayoutParams(l);
            img.setImageResource(rank[i]);
            Layout.addView(img);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getPopupWindow() {
        if (distancePopup != null && distancePopup.isShowing()) {
            distancePopup.dismiss();
        }
    }

    private void getClassifyId() {
        if (UserData.getOfficeInfo() != null) {
            Util.asynTask(this, "", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (runData != null) {
                        HashMap<Integer, List<AlbumClassifyModel>> map = (HashMap<Integer, List<AlbumClassifyModel>>) runData;
                        List<AlbumClassifyModel> list = map.get(1);
                        if (list != null && list.size() > 0) {
                            Albums.setText(list.size() + "");
                        }
                    } else {
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.officeUrl, Web.GetOfficePhotoClass,
                            "officeid="
                                    + UserData.getOfficeInfo().getOffice_id());
                    List<AlbumClassifyModel> list = web
                            .getList(AlbumClassifyModel.class);
                    HashMap<Integer, List<AlbumClassifyModel>> map = new HashMap<Integer, List<AlbumClassifyModel>>();
                    map.put(1, list);
                    return map;
                }
            });
        }
    }

    /**
     * 新建一个popupwindow实例
     *
     * @param view
     */
    private void initpoputwindow(View view) {
        distancePopup = new PopupWindow(view,
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
        distancePopup.setOutsideTouchable(true);
        distancePopup.setFocusable(true);
        distancePopup.setBackgroundDrawable(new BitmapDrawable());
        distancePopup.setAnimationStyle(R.style.popupanimation);
    }

    /**
     * 初始化并弹出popupwindow
     *
     * @param i
     */
    private void startPopupWindow() {
        View pview = getLayoutInflater().inflate(
                R.layout.shop_office_configuration, null, false);
        TextView office_share = (TextView) pview
                .findViewById(R.id.office_share);
        TextView office_order = (TextView) pview
                .findViewById(R.id.office_order);
        TextView office_config = (TextView) pview
                .findViewById(R.id.office_config);
        TextView add_baobei = (TextView) pview.findViewById(R.id.add_baobei);

        office_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                distancePopup.dismiss();
                officeShare();
            }
        });
        add_baobei.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                distancePopup.dismiss();
                Util.showIntent(ShopOfficeFrame.this, StoreFrame.class);
            }
        });
        office_order.setVisibility(View.GONE);
        office_order.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                distancePopup.dismiss();
                // 跳转到Order页面
                Util.showIntent(ShopOfficeFrame.this, ShopOfficeOrder.class);
            }
        });
        office_config.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                distancePopup.dismiss();
                Util.showIntent(ShopOfficeFrame.this, ShopOfficeConfig.class);
            }
        });
        initpoputwindow(pview);
    }

    private void init() {
        if (UserData.getUser() != null) {
            user = UserData.getUser();
        } else {
            // Util.showIntent(this, LoginFrame.class);
        }
        _50dp = Util.pxToDp(this, 150);
        layer.getBackground().setAlpha(150);
        if (!Util.isNull(names)) {
            if (names.length() > 6) {
                names = names.substring(0, 6) + "...";
            }
            name.setText(names.replace("_p", ""));
        } else {
            name.setText("");
        }
        if (!Util.isNull(from) && from.equals("list")) {
            if (UserData.getOfficeInfo() != null) {
                initOffice(UserData.getOfficeInfo());
            } else {
                getShopOfficeInfo();
            }
        } else {
            getShopOfficeInfo();
        }
    }

    private void initTop() {
        String title = "";
        if (UserData.getUser() != null && UserData.getOfficeInfo() != null) {// 未登录
            if (!Util.isNull(UserData.getOfficeInfo().getOfficename())) {
                title = UserData.getOfficeInfo().getOfficename();
            } else {
                title = "创业空间";
            }
            if (shopoffice != null) {
                officeid = shopoffice.getUserid();
            }

            if (!UserData.getUser().getUserIdNoEncodByUTF8().equals(officeid)) {

            } else {
                initTopTwo(title);
            }
        }
    }


    private void initTopTwo(String title) {
        Util.initTop(this, title, R.drawable.morewhite, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserData.getUser() != null
                        && UserData.getOfficeInfo() != null) {
                    if (UserData.getUser().getUserIdNoEncodByUTF8()
                            .equals(UserData.getOfficeInfo().getUserid())) {
                        getPopupWindow();
                        startPopupWindow();
                        distancePopup.showAsDropDown(v);
                    }
                }
            }
        });
    }

    private void officeShare() {
        if (UserData.getUser() == null) {
            Util.showIntent(this, LoginFrame.class);
        }
        if (UserData.getOfficeInfo() == null) {
            return;
        }
        final String url;
        String s = Web.webServer_Image.replace("test", UserData.getOfficeInfo()
                .getDomainstr());
        s = s.replace("www", UserData.getOfficeInfo().getDomainstr());
        url = "http://" + s + "/user/office/myOffices.aspx?unum="
                + UserData.getOfficeInfo().getUserNo();
        String logourl = "";
        if (!Util.isNull(shopoffice.getImgLogo1())) {
            if (shopoffice.getImgLogo1().contains("http")) {
                logourl = shopoffice.getImgLogo();
            } else {
                logourl = "http://" + Web.webServer_Image + shopoffice.getImgLogo();
            }
        } else {
            logourl = UserData.getUser().getUserFace();
        }
        User user = UserData.getUser();
        if (user != null) {
            if ("6".equals(user.getLevelId())) {
                Util.show("对不起，请登录您的城市总监账号在进行此操作！", this);
                return;
            }
            if (Util.getInt(user.getShopTypeId()) >= 3) {
                final OnekeyShare oks = new OnekeyShare();
                final String title = "创业空间分享-远大云商";
                oks.setTitle(title);
                oks.setTitleUrl(url);
                oks.setUrl(url);
                oks.setAddress("10086");
                oks.setComment("快来注册吧");
                oks.setText("hi，您想低成本创业赚钱吗？我在此向您郑重推荐中国首家移动电商第三方创业服务平台〔远大云商〕。点击本链接"
                        + "http://"
                        + Web.webServer_Image
                        + "/user/office/myOffices.aspx?unum="
                        + user.getUserNo()
                        + "。可直接进入我的创业空间了解项目、参观购物。中国已进入4G时代，让您的手机也变成获取财富的工具吧。咨询电话："
                        + Util._400);

                oks.setSite("远大云商");
                oks.setSiteUrl("http://" + Web.webServer_Image
                        + "/Shop/ShopSite.aspx?unum=" + user.getUserNo());
                oks.setSilent(false);
                oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                    @Override
                    public void onShare(Platform platform,
                                        ShareParams paramsToShare) {
                        if ("ShortMessage".equals(platform.getName())) {
                            paramsToShare.setImageUrl(null);
                            paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());
                        }

                    }
                });
                oks.show(this);
            } else {
                Util.showIntent("对不起，您还没有创业空间，您可以前去申请创业空间!", this, "去申请",
                        "再逛逛", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Util.showIntent(ShopOfficeFrame.this,
                                        ProxySiteFrame.class);
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        });
            }
        } else {
            Util.showIntent("您还没有登录，要先去登录吗？", this, LoginFrame.class);
        }
    }

    @OnClick(R.id.connect)
    public void PhoneClick(final View v) {
        Util.doPhone(Util._400, this);
    }

    @OnClick(R.id.albumLayout)
    public void AlbumClick(final View v) {
        Intent intent = new Intent(this, AlbumList.class);
        intent.putExtra("offid", officeid);
        this.startActivity(intent);
    }

    @OnClick(R.id.logo)
    public void LogoClick(final View v) {
        if (UserData.getUser() != null && UserData.getOfficeInfo() != null) {
            System.out.println("UserData.getUser().getUserIdNoEncodByUTF8()=="
                    + UserData.getUser().getUserIdNoEncodByUTF8()
                    + "UserData.getOfficeInfo().getUserid()=="
                    + UserData.getOfficeInfo().getUserid());
            if (UserData.getUser().getUserIdNoEncodByUTF8().equals(officeid)) {
                Util.showIntent(ShopOfficeFrame.this, ShopOfficeConfig.class);
            }
        }
    }

    @OnClick(R.id.notesLayout)
    public void NotesClick(final View v) {
        Intent intent = new Intent(this, ShopOfficeArticle.class);
        intent.putExtra("offid", officeid);
        intent.putExtra("crown", crown);
        intent.putExtra("sun", sun);
        intent.putExtra("moon", moon);
        intent.putExtra("star", star);
        intent.putExtra("Albums", Albums.getText().toString());
        intent.putExtra("officeid", shopoffice.getOffice_id());
        intent.putExtra("userNo", userNo);
        this.startActivity(intent);
    }

    @OnClick(R.id.vedioLayout)
    public void VedioClick(final View v) {
        Intent intent = new Intent(this, ShopOfficeVedio.class);
        intent.putExtra("offid", officeid);
        intent.putExtra("crown", crown);
        intent.putExtra("sun", sun);
        intent.putExtra("moon", moon);
        intent.putExtra("star", star);
        intent.putExtra("Albums", Albums.getText().toString());
        intent.putExtra("userNo", userNo);
        this.startActivity(intent);
    }

    @OnClick(R.id.bmLayout)
    public void BmClick(final View v) {
        Intent intent = new Intent();
        intent.setClass(this, BMFWFrane.class);
        intent.putExtra("unum", userNo);
        this.startActivity(intent);
    }

    private void initOffice(ShopOfficeInfo shopOffice) {
        initTop();
        if (Util.isNull(shopOffice)) {
            return;
        }
        if (!Util.isNull(shopoffice.getClicks())) {
            Visitors.setText("访客：" + shopoffice.getClicks());
        } else {
            Visitors.setText("");
        }
        if (!Util.isNull(shopoffice.getProductcount())) {
            BaoBei.setText(shopoffice.getProductcount());
        } else {
            BaoBei.setText("0");
        }
        officeUserNo = shopOffice.getUserNo();
        if (!Util.isNull(shopoffice.getVdieo_count())) {
            Vedios.setText(shopoffice.getVdieo_count());
        }
        if (!Util.isNull(shopoffice.getArt_count())) {
            Notes.setText(shopoffice.getArt_count());
        }


        if (!Util.isNull(shopoffice.getImgLogo())) {
            String logourl = "";
            if (shopoffice.getImgLogo1().contains("http")) {
                logourl = shopoffice.getImgLogo();
            } else {
                logourl = "http://" + Web.webImage + shopoffice.getImgLogo();
            }
            bmUtils.display(OfficeLogo, logourl,
                    new DefaultBitmapLoadCallBack<View>() {
                        @Override
                        public void onLoadCompleted(View container, String uri,
                                                    Bitmap bitmap, BitmapDisplayConfig config,
                                                    BitmapLoadFrom from) {
                            Bitmap zoomBm = Util.zoomBitmap(bitmap, _50dp,
                                    _50dp);
                            super.onLoadCompleted(container, uri,
                                    Util.toRoundCorner(zoomBm, 5), config, from);
                        }

                        @Override
                        public void onLoadFailed(View container, String uri,
                                                 Drawable drawable) {
                            Resources r = ShopOfficeFrame.this.getResources();
                            InputStream is = r
                                    .openRawResource(R.drawable.ic_launcher_black_white);
                            BitmapDrawable bmpDraw = new BitmapDrawable(is);
                            Bitmap zoomBm = Util.zoomBitmap(
                                    bmpDraw.getBitmap(), _50dp, _50dp);
                            OfficeLogo.setImageBitmap(Util.toRoundCorner(
                                    zoomBm, 5));
                        }
                    });
        } else {
            Resources r = ShopOfficeFrame.this.getResources();
            InputStream is = r
                    .openRawResource(R.drawable.ic_launcher_black_white);
            BitmapDrawable bmpDraw = new BitmapDrawable(is);
            Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(), _50dp, _50dp);
            OfficeLogo.setImageBitmap(Util.toRoundCorner(zoomBm, 5));
        }
        String url = "";
        if (shopoffice.getImgLogo1().contains("http")) {
            url = shopoffice.getImgLogo1();
        } else {
            url = "http://" + Web.webImage + shopoffice.getImgLogo1();
        }
        bmUtils.display(OfficeBanner, url,
                new DefaultBitmapLoadCallBack<View>() {
                    @Override
                    public void onLoadCompleted(View container, String uri,
                                                Bitmap bitmap, BitmapDisplayConfig config,
                                                BitmapLoadFrom from) {
                        super.onLoadCompleted(container, uri, bitmap, config,
                                from);
                    }

                    @Override
                    public void onLoadFailed(View container, String uri,
                                             Drawable drawable) {
                        OfficeBanner
                                .setImageResource(R.drawable.shop_office_banner);
                    }
                });
        if (crown == 0 && sun == 0 && moon == 0 && star == 0) {
            if (!Util.isNull(shopOffice.getCrown())) {
                crown = Integer.parseInt(shopOffice.getCrown());
            }
            if (!Util.isNull(shopOffice.getSun())) {
                sun = Integer.parseInt(shopOffice.getSun());
            }
            if (!Util.isNull(shopOffice.getMoon())) {
                moon = Integer.parseInt(shopOffice.getMoon());
            }
            if (!Util.isNull(shopOffice.getStar())) {
                star = Integer.parseInt(shopOffice.getStar());
            }

        }
        clacluate(rank_container, this, crown, sun, moon, star);
        getClassifyId();
        firstpage();
        scrollpage();
    }

    private void getShopOfficeInfo() {
        Util.asynTask(this, "正在获取空间信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (runData != null && shopoffice != null) {
                    UserData.setOfficeInfo(shopoffice);
                    initOffice(shopoffice);
                } else {
                    Toast.makeText(ShopOfficeFrame.this, "未获取到空间数据",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public Serializable run() {
                Web web = null;
                if (Util.isNull(userNo)) {
                    web = new Web(Web.officeUrl, Web.GetOfficeInfo, "unum="
                            + user.getUserNo());
                } else {
                    web = new Web(Web.officeUrl, Web.GetOfficeInfo, "unum="
                            + userNo);
                }
                shopoffice = web.getObject(ShopOfficeInfo.class);
                return shopoffice;
            }
        });
    }

    public void firstpage() {
        getProduct();
    }

    public void scrollpage() {
        Gridv.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 滚动到底部自动加载(很重要)
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        // 加载更多
                        getProduct();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private void getProduct() {
        final CustomProgressDialog dialog = Util
                .showProgress("获取宝贝数据...", this);
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<OfficeProduct>> map = (HashMap<String, List<OfficeProduct>>) runData;
                List<OfficeProduct> list = map.get("list");
                if (null != list && 0 != list.size()) {
                    if (null != adapter) {
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter = new ItemAdapter(ShopOfficeFrame.this);
                        Gridv.setAdapter(adapter);
                    }
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                }
                dialog.cancel();
                dialog.dismiss();
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.officeUrl, Web.GetOfficeProductListPage,
                        "officeid=" + shopoffice.getOffice_id() + "&cPage="
                                + (page++) + "&flag=1&cateid=1");
                List<OfficeProduct> list = web.getList(OfficeProduct.class);
                HashMap<String, List<OfficeProduct>> map = new HashMap<String, List<OfficeProduct>>();
                map.put("list", list);
                return map;
            }
        });
    }

    public class ItemAdapter extends BaseAdapter {
        private List<OfficeProduct> list = new ArrayList<OfficeProduct>();
        private Context c;
        private LayoutInflater inflater;

        public ItemAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            this.c = context;
        }

        public void setList(List<OfficeProduct> list) {
            this.list.addAll(list);
            this.notifyDataSetChanged();
        }

        public void clear() {
            this.list.clear();
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder h = null;
            OfficeProduct p = this.list.get(position);
            if (convertView == null) {
                h = new ViewHolder();
                convertView = inflater.inflate(R.layout.shop_office_item, null);
                h.office_pro = (ImageView) convertView
                        .findViewById(R.id.office_pro);
                h.title = (TextView) convertView.findViewById(R.id.title);
                h.market_price = (TextView) convertView
                        .findViewById(R.id.market_price);
                h.yd_price = (TextView) convertView.findViewById(R.id.yd_price);
                h.sb_price = (TextView) convertView.findViewById(R.id.sb_price);
                convertView.setTag(h);
            } else {
                h = (ViewHolder) convertView.getTag();
            }
            final OfficeProduct pp = p;
            String href = p.getProductThumb().replace("174_174", "460_460");
            bmUtils.display(h.office_pro, href);
            h.title.setText(p.getContent());
            h.market_price.getPaint().setFlags(
                    Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);// 设置中划线
            h.market_price.setText("市场售价：￥"
                    + Util.getDouble(Double.parseDouble(p.getPriceMarket())));
            h.yd_price.setText(Util.spanRed("远大售价：￥",
                    Util.getDouble(Double.parseDouble(p.getPrice()))));
            int sbprice = 0;
            sbprice = Integer.parseInt(p.getPriceOriginal());
            if (Double.parseDouble(p.getPriceOriginal()) > sbprice) {
                sbprice += 1;
            }
            h.sb_price.setText(Util.spanGreenInt("商币兑换：", sbprice + ""));
            convertView.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    Intent intent = new Intent();
                    intent.setClass(c, ProductDeatilFream.class);
                    intent.putExtra("className", c.getClass().toString());
                    intent.putExtra("url", pp.getProductid());
                    intent.putExtra("unum", officeUserNo);
                    intent.putExtra("title", "分享宝贝购买");
                    c.startActivity(intent);
                }
            });
            User user2 = UserData.getUser();
            if (user2 != null && user2.getUserNo().equals(userNo)) {
                convertView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Util.showChoosedDialog(c, "是否要删除该分享商品？", "点错了", "确定删除",
                                new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteProduct(pp.getShare_id());
                                    }
                                });
                        return true;
                    }
                });
            }

            return convertView;
        }

        private void deleteProduct(final String articleid) {
            if (UserData.getUser() != null) {
                user = UserData.getUser();
                Util.asynTask(ShopOfficeFrame.this, "", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if (runData != null) {
                            System.out.println("rundata=========" + runData);
                            if ("ok".equals(runData + "")) {
                                Toast.makeText(ShopOfficeFrame.this, "删除成功",
                                        Toast.LENGTH_LONG).show();
                                clear();
                                page = 1;
                                getProduct();
                            }
                        } else {
                            Toast.makeText(ShopOfficeFrame.this, "删除失败",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.officeUrl,
                                Web.DeleteOfficeUserProduct, "userID="
                                + user.getUserId() + "&userPaw="
                                + user.getMd5Pwd() + "&articleid="
                                + articleid);
                        return web.getPlan();
                    }
                });
            }
        }
    }

    public class ViewHolder {
        ImageView office_pro;
        TextView title;
        TextView market_price;
        TextView yd_price;
        TextView sb_price;
    }
}
