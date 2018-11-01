package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.BusinessDetails.BusinessDetailsActivity;
import com.mall.model.AD;
import com.mall.model.LocationModel;
import com.mall.model.ShopM;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.util.Data;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能： 远大商城手机客户端主页面<br>
 * 时间： 2013-3-7<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class FindLMSJFrame extends Activity {
    // # start --------------- 首页 ------------------
    @ViewInject(R.id.serachText)
    private EditText sreach = null;
    @ViewInject(R.id.maingrid)
    private ScrollView scrollView;
    private BitmapUtils bmUtil;
    private List<View> dots = new ArrayList<View>();
    @ViewInject(R.id.main_layout1_sreach)
    private EditText main_layout1_sreach;
    @ViewInject(R.id.tv_tuijian_count)
    private TextView tv_tuijian_count;
    @ViewInject(R.id.vp)
    private ViewPager viewPager;
    private List<ImageView> imageViews = new ArrayList<ImageView>();

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (-1 == msg.what) {
                if (null != msg.obj) {
                    viewPager.setCurrentItem(Integer.parseInt(msg.obj + ""));
                }
            }
        }

    };

    @OnClick({R.id.sbtn, R.id.main_layout1_sbtn})
    public void sreachProduct(View v) {
        String sValue = main_layout1_sreach.getText().toString();
        sreach(sValue);
    }

    /**
     * 查询商家
     *
     * @param sValue 关键字
     */
    private void sreach(String sValue) {
        if (!Util.isNull(sValue) && !"".equals(sValue.trim())) {

            Intent intent = new Intent();
            intent.setClass(FindLMSJFrame.this, LMSJFrame.class);
            intent.putExtra("type", "search");
            intent.putExtra("keyword", sValue);
            FindLMSJFrame.this.startActivity(intent);
            main_layout1_sreach.setText("");
        } else
            Util.show("请输入您要查询的商家!", FindLMSJFrame.this);
    }

    @OnClick({R.id.speak, R.id.main_layout1_speak})
    public void speak(final View v) {
        Util.startVoiceRecognition(this, new DialogRecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> rs = results != null ? results
                        .getStringArrayList(RESULTS_RECOGNITION) : null;
                if (rs != null && rs.size() > 0) {

                    String str = rs.get(0).replace("。", "").replace("，", "");
                    main_layout1_sreach.setText(str);
                    sreach(str);
                }
            }
        });
    }

    private Handler jumpHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (R.id.speak == msg.what)
                sreach.setText((String) msg.obj);
        }

        ;
    };

    // #start --------------- 第四瓶 --------------------
    private boolean isLoadLayout4 = false;
    @ViewInject(R.id.main_layout4_lmsjList)
    private LinearLayout layout4GridLine;
    @ViewInject(R.id.main_layout4_city)
    private TextView layout4City;
    @ViewInject(R.id.new_layout4_loadmore)
    private TextView layout4Loadmore;

    @OnClick(R.id.main_layout4_fujinde)
    public void layout4fujin(View v) {
        Util.showIntent(this, MapLMSJFrame.class, new String[]{"tpye"},
                new String[]{"tuijian"});
    }

    @OnClick({R.id.main_layout4_meishi, R.id.main_layout4_gengduo,
            R.id.main_layout4_jianshen, R.id.main_layout4_jiudian,
            R.id.main_layout4_kafei, R.id.main_layout4_lvyou,
            R.id.main_layout4_meirong, R.id.main_layout4_wangba,
            R.id.main_layout4_yaodian, R.id.main_layout4_yule})
    public void layout4meishi(View v) {
        String[] key = null;
        String[] values = null;
        key = new String[]{"type", "cateid", "title"};
        values = new String[]{"cate", v.getTag() + "", ((TextView) v).getText().toString()};
        Util.showIntent(this, LMSJFrame.class, key, values);
    }

    // #end ----------------- 第四屏 --------------------
    LocationModel locationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout4);
        ViewUtils.inject(this);
        bmUtil = new BitmapUtils(this);
        bmUtil.configDefaultLoadFailedImage(R.drawable.zw174);
        // 提示输入要查询的商家名
        main_layout1_sreach.setHint("要查询的商家");
        locationModel = LocationModel.getLocationModel();
        layout4City.setText(locationModel.getCity());
        initdos();
        initLayout4();
    }

    private void initdos() {
        dots = new ArrayList<View>();
        dots.add((View) FindLMSJFrame.this.findViewById(R.id.v_dot0));
        dots.add((View) FindLMSJFrame.this.findViewById(R.id.v_dot1));
        dots.add((View) FindLMSJFrame.this.findViewById(R.id.v_dot2));
        dots.add((View) FindLMSJFrame.this.findViewById(R.id.v_dot3));
        dots.add((View) FindLMSJFrame.this.findViewById(R.id.v_dot4));
        // dots.add((View) FindLMSJFrame.this.findViewById(R.id.v_dot5));
        // dots.add((View) FindLMSJFrame.this.findViewById(R.id.v_dot6));
        // dots.add((View) FindLMSJFrame.this.findViewById(R.id.v_dot7));
        // dots.add((View) FindLMSJFrame.this.findViewById(R.id.v_dot8));
        // dots.add((View) FindLMSJFrame.this.findViewById(R.id.v_dot9));

    }

    @OnClick(R.id.topback)
    public void toback(View view) {
        finish();
    }

    @OnClick({R.id.main_layout4_saoyisao, R.id.main_layout4_city})
    public void layout4City(View v) {
        this.startActivityForResult(new Intent(this, CitySelectActivity.class),
                CitySelectActivity._RESUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == R.id.speak || requestCode == R.id.main_layout1_speak)
                && resultCode == FindLMSJFrame.RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.size() > 0) {
                Message msg = new Message();
                msg.obj = matches.get(0);
                msg.what = requestCode;
                jumpHandler.sendMessage(msg);
            }
        }
        if (resultCode == CitySelectActivity._RESUESTCODE) {
            LogUtils.e(data.getStringExtra("id") + "___"
                    + data.getStringExtra("name"));
            layout4City.setText(data.getStringExtra("name"));
            clearLMSJData();
            mainLayout4Page = 1;
            loadLayout4LMSJ(true);
        }
    }

    // #start 第四瓶 ----------------------------------------

    int mainLayout4ScrollViewY = 0;
    int mainLayout4Page = 1;
    String requestMethod = Web.getTuiJianShopMMByPage;
    String backCityId = "";

    public void initLayout4() {
        mainLayout4ScrollViewY = 0;
        if (isLoadLayout4 && 1 < layout4GridLine.getChildCount())
            return;
        if (1 > layout4GridLine.getChildCount())
            mainLayout4Page = 1;
        isLoadLayout4 = true;
        loadLayout4LMSJ(false);
    }

    @OnClick(R.id.new_layout4_loadmore)
    public void loadMoreClick(View view) {
        if (4 == mainLayout4Page) {
            Util.showIntent(FindLMSJFrame.this, LMSJFrame.class);
        } else {
            loadLayout4LMSJ(false);
        }

    }

    private void loadLayout4LMSJ(final boolean isCityClick) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", mainLayout4Page + "");
        params.put("size", "999");
        String city = "";
        String call = "getHomeLMSJ";
        if (Util.isNull(locationModel.getCity()))
            call = "getTJLMSJ";// 没有定位获取推荐
        if (!isCityClick) {
            city = Util.isNull(layout4City.getText().toString()) ? locationModel
                    .getCity() : layout4City.getText().toString();
        } else {

            city = layout4City.getText().toString();
        }
        if (!Util.isNull(locationModel.getLatitude())) {
            params.put("lat", locationModel.getLatitude() + "");
            params.put("lon", locationModel.getLongitude()
                    + "");
        }
        params.put("cName", city);
        mainLayout4Page++;
        final CustomProgressDialog cpd = CustomProgressDialog
                .showProgressDialog(this, "正在获取联盟商家...");
        NewWebAPI.getNewInstance().getWebRequest("/Alliance.aspx?call=" + call,
                params, new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", FindLMSJFrame.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"),
                                    FindLMSJFrame.this);
                            return;
                        }
                        tv_tuijian_count.setText(json.getString("message")
                                + "家");
                        // 联盟商家广告
                        // final WHD whd =
                        // Util.getScreenSize(FindLMSJFrame.this);
                        // final JSONObject advObj = json.getJSONObject("adv");
                        // bmUtil.display(lmsjadv, advObj.getString("path"),new
                        // DefaultBitmapLoadCallBack<View>() {
                        // @Override
                        // public void onLoadCompleted(View arg0, String arg1,
                        // Bitmap arg2, BitmapDisplayConfig arg3,
                        // BitmapLoadFrom arg4) {
                        // arg2 = Util.zoomBitmap(arg2, whd.getWidth(), 360);
                        // super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
                        //
                        // }
                        //
                        // @Override
                        // public void onLoadFailed(View arg0, String arg1,
                        // Drawable arg2) {
                        // }
                        //
                        // });
                        String list2 = json.getString("adv2");

                        List<AD> listad = JSON.parseArray(list2, AD.class);
                        for (final AD ad : listad) {
                            ImageView im = new ImageView(FindLMSJFrame.this);
                            bmUtil.display(im, ad.path);

                            im.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    Util.showIntent(
                                            FindLMSJFrame.this,
                                            BusinessDetailsActivity.class,
                                            new String[]{BusinessDetailsActivity.BUSINESS_ID, "name",
                                                    "x", "y", "face", BusinessDetailsActivity.BUSINESS_Favorite},
                                            new String[]{ad.id, ad.name,

                                                    "",
                                                    "", "", ad.Favorite});

                                }
                            });

                            imageViews.add(im);
                        }
                        for (int i = 0; i < dots.size(); i++) {
                            if (i < imageViews.size())
                                dots.get(i).setVisibility(View.VISIBLE);
                            else
                                dots.get(i).setVisibility(View.GONE);
                        }

                        viewPager.setAdapter(new VpAdaper());
                        // lmsjadv.setOnClickListener(new OnClickListener() {
                        // @Override
                        // public void onClick(View arg0) {
                        // Util.showIntent(FindLMSJFrame.this,
                        // LMSJDetailFrame.class, new String[] {
                        // "id", "name", "x", "y" },
                        // new String[] { advObj.getString("id"),
                        // advObj.getString("name"),
                        // "","" });
                        // }
                        // });
                        // 联盟商家
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    int count = viewPager.getAdapter()
                                            .getCount();
                                    int index = viewPager.getCurrentItem();
                                    for (int i = 0; i < count; i++) {
                                        Message msg = new Message();
                                        try {
                                            Thread.currentThread().sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        index = viewPager.getCurrentItem() + 1;
                                        if (index > count)
                                            index = 0;
                                        msg.obj = i;
                                        msg.what = -1;
                                        handler.sendMessage(msg);
                                    }
                                }
                            }
                        }).start();
                        System.gc();

                        viewPager
                                .setOnPageChangeListener(new OnPageChangeListener() {
                                    private int oldPosition = 0;

                                    @Override
                                    public void onPageSelected(int position) {
                                        if (position > dots.size())
                                            return;
                                        dots.get(oldPosition)
                                                .setBackgroundResource(
                                                        R.drawable.dot_normal);
                                        dots.get(position)
                                                .setBackgroundResource(
                                                        R.drawable.dot_focused);
                                        oldPosition = position;

                                    }

                                    @Override
                                    public void onPageScrolled(int arg0,
                                                               float arg1, int arg2) {

                                    }

                                    @Override
                                    public void onPageScrollStateChanged(
                                            int arg0) {

                                    }
                                });
                        final int _80dp = Util.dpToPx(FindLMSJFrame.this, 80F);
                        final int _100dp = Util
                                .dpToPx(FindLMSJFrame.this, 100F);
                        final BitmapDisplayConfig config = new BitmapDisplayConfig();
                        config.setBitmapMaxSize(new BitmapSize(_100dp, _80dp));
                        List<ShopM> list = JSON.parseArray(
                                json.getString("list"), ShopM.class);
                        LayoutInflater layout = LayoutInflater
                                .from(FindLMSJFrame.this);
                        LogUtils.v("current item size = " + list.size()
                                + "     ");
                        if (Util.isNull(layout4City.getText().toString())) {
                            layout4City.setText(locationModel.getCity());
                        }
                        if (list.size() < 20) {
                            layout4Loadmore.setVisibility(View.GONE);
                        }
                        List<ShopM> newData = new ArrayList<ShopM>();
                        newData.addAll(list);

                        for (final ShopM m : newData) {
                            // 如果5秒后，还没获取到定位，那么则默当前位置认为成都
                            // 这里的5秒是打开Layout4之后的5秒。而不是打开首页5秒
                            if (Util.isNull(locationModel.getLocationType())) {
                                m.setMm(Float.MAX_VALUE);
                                continue;
                            }
                            // 我操，高德地图jsAPI的经纬度和AndroidAPI的经纬度是反的。我操
                            LatLng remote = new LatLng(Double.parseDouble(m
                                    .getPointY()), Double.parseDouble(m
                                    .getPointX()));

                            float distanceM = AMapUtils.calculateLineDistance(
                                    new LatLng(Double.parseDouble(locationModel.getLatitude() + ""), Double.parseDouble(locationModel.getLatitude() + "")), remote);
                            m.setMm(distanceM);
                        }
                        Collections.sort(newData, new Comparator<ShopM>() {
                            @Override
                            public int compare(ShopM o1, ShopM o2) {
                                return Double
                                        .valueOf(o1.getMm() + "")
                                        .compareTo(
                                                Double.valueOf(o2.getMm() + ""));
                            }
                        });
                        if (0 == list.size() && 2 == mainLayout4Page) {
                            ShopM m = new ShopM();
                            m.setCate("暂无分类");
                            m.setId("-7");
                            m.setImg("http://" + Web.webImage
                                    + "/rebate/nodata.jpg");
                            m.setName("您的所在城市，暂无联盟商家");
                            m.setMm(Float.MAX_VALUE);
                            m.setPhone("400-666-3838");
                            newData.add(m);
                        }
                        for (final ShopM m : newData) {
                            View root = layout.inflate(
                                    R.layout.main_layout4_shopm_item, null);
                            LinearLayout line = (LinearLayout) root
                                    .findViewById(R.id.main_layout4_shopM_item_line);
                            final ImageView img = (ImageView) root
                                    .findViewById(R.id.main_layout4_shopM_item_logo);

                            m.setImg(m.getImg().replaceFirst(Web.webImage,
                                    Web.webServer));
                            bmUtil.display(img, m.getImg(), config,
                                    new BitmapLoadCallBack<View>() {
                                        @Override
                                        public void onLoadCompleted(View arg0,
                                                                    String arg1, Bitmap arg2,
                                                                    BitmapDisplayConfig arg3,
                                                                    BitmapLoadFrom arg4) {
                                            Bitmap bm = Util.zoomBitmap(arg2,
                                                    _100dp, _80dp);
                                            img.setImageBitmap(bm);
                                        }

                                        @Override
                                        public void onLoadFailed(View arg0,
                                                                 String arg1, Drawable arg2) {
                                            bmUtil.display(img, m.getImg(),
                                                    config);
                                        }
                                    });
                            TextView nameText = (TextView) root
                                    .findViewById(R.id.main_layout4_shopM_item_name);
                            nameText.setText(m.getName());
                            TextView phoneText = (TextView) root
                                    .findViewById(R.id.main_layout4_shopM_item_phone);
                            Spanned fromHtml = Html
                                    .fromHtml("<font color='#ff535353'>电话：</font><u><font color='#ff535353'>"
                                            + m.getPhone() + "</font></u>");
                            phoneText.setText(fromHtml);
                            phoneText.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    Util.doPhone(m.getPhone(),
                                            FindLMSJFrame.this);
                                }
                            });
                            TextView cate = (TextView) root
                                    .findViewById(R.id.main_layout4_shopM_item_cate);
                            cate.setText(m.getCate());
                            final TextView juli = (TextView) root
                                    .findViewById(R.id.main_layout4_shopM_item_juli);
                            if (m.getMm() == Float.MAX_VALUE)
                                juli.setText("无法计算");
                            else
                                juli.setText(getMM(m.getMm()));

                            line.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!"-7".equals(m.getId())) {
                                        Util.showIntent(
                                                FindLMSJFrame.this,
                                                LMSJDetailFrame.class,
                                                new String[]{"id", "name",
                                                        "x", "y"},
                                                new String[]{m.getId(),
                                                        m.getName(),
                                                        m.getPointX(),
                                                        m.getPointY()});
                                    }
                                }
                            });
                            TextView gotoThat = (TextView) root
                                    .findViewById(R.id.main_layout4_shopM_item_goto);
                            gotoThat.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    synchronized (v) {
                                        if (Util.isNull(m.getPointX())
                                                || Util.isNull(m.getPointY())) {
                                            Util.show("对不起，该商家没有定位传送门！",
                                                    FindLMSJFrame.this);
                                            return;
                                        }
                                        Util.doDaoHang(FindLMSJFrame.this, m
                                        );
                                    }
                                }
                            });
                            TextView mysc = (TextView) root
                                    .findViewById(R.id.main_layout4_shopM_item_mysc);
                            mysc.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!"-7".equals(m.getId())) {
                                        if (null != UserData.getUser()) {
                                            if (Data.addMyShopMSc(
                                                    FindLMSJFrame.this, m))
                                                Util.show("收藏成功！",
                                                        FindLMSJFrame.this);
                                            else
                                                Util.show("收藏失败！",
                                                        FindLMSJFrame.this);
                                        } else
                                            Util.show("请先登录，在收藏",
                                                    FindLMSJFrame.this);
                                    } else {
                                        Util.show("该商家不能收藏！",
                                                FindLMSJFrame.this);
                                    }
                                }
                            });
                            LinearLayout nullLine = new LinearLayout(
                                    FindLMSJFrame.this);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.FILL_PARENT, Util
                                    .dpToPx(FindLMSJFrame.this, 5F));
                            lp.setMargins(0, 0, 0, lp.height);
                            nullLine.setLayoutParams(lp);
                            final int listSize = list.size();
                            layout4GridLine.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    int allLmsj = Integer
                                            .parseInt(tv_tuijian_count
                                                    .getText().toString()
                                                    .replaceFirst("家", ""));
                                    if (allLmsj > listSize)
                                        layout4Loadmore
                                                .setVisibility(TextView.VISIBLE);
                                    else
                                        layout4Loadmore
                                                .setVisibility(TextView.GONE);
                                }
                            }, 1000);
                            layout4GridLine.addView(root,
                                    layout4GridLine.getChildCount() - 1);
                            layout4GridLine.addView(nullLine,
                                    layout4GridLine.getChildCount() - 1);
                        }
                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        cpd.cancel();
                        cpd.dismiss();
                    }
                });
        // Util.asynTask(FindLMSJFrame.this, "正在获取联盟商家...", new AsynTask() {
        // @Override
        // public void updateUI(Serializable runData) {
        // final int _80dp = Util.dpToPx(FindLMSJFrame.this, 80F);
        // final int _100dp = Util.dpToPx(FindLMSJFrame.this, 100F);
        // final BitmapDisplayConfig config = new BitmapDisplayConfig();
        // config.setBitmapMaxSize(new BitmapSize(_100dp, _80dp));
        // HashMap<String, List<ShopM>> map = (HashMap<String, List<ShopM>>)
        // runData;
        // List<ShopM> list = map.get("list");
        // LayoutInflater layout = LayoutInflater.from(FindLMSJFrame.this);
        // LogUtils.v("current item size = " + list.size() + "     ");
        // if (Util.isNull(layout4City.getText().toString())) {
        // layout4City.setText(LocationService.getCity());
        // }
        // if(list.size() < 20){
        // layout4Loadmore.setVisibility(View.GONE);
        // }
        // List<ShopM> newData = new ArrayList<ShopM>();
        // newData.addAll(list);
        //
        // for (final ShopM m : newData) {
        // // 如果5秒后，还没获取到定位，那么则默当前位置认为成都
        // // 这里的5秒是打开Layout4之后的5秒。而不是打开首页5秒
        // if (null == LocationService.getLocationLatlng()) {
        // m.setMm(Float.MAX_VALUE);
        // continue;
        // }
        // // 我操，高德地图jsAPI的经纬度和AndroidAPI的经纬度是反的。我操
        // LatLng remote = new LatLng(
        // Double.parseDouble(m.getPointY()), Double
        // .parseDouble(m.getPointX()));
        // float distanceM = AMapUtils.calculateLineDistance(
        // LocationService.getLocationLatlng(), remote);
        // m.setMm(distanceM);
        // }
        // Collections.sort(newData, new Comparator<ShopM>() {
        // @Override
        // public int compare(ShopM o1, ShopM o2) {
        // return Double.valueOf(o1.getMm() + "").compareTo(
        // Double.valueOf(o2.getMm() + ""));
        // }
        // });
        // if (0 == list.size() && 2 == mainLayout4Page) {
        // ShopM m = new ShopM();
        // m.setCate("暂无分类");
        // m.setId("-7");
        // m.setImg("http://" + Web.webImage + "/rebate/nodata.jpg");
        // m.setName("您的所在城市，暂无联盟商家");
        // m.setMm(Float.MAX_VALUE);
        // m.setPhone("400-666-3838");
        // newData.add(m);
        // }
        // for (final ShopM m : newData) {
        // View root = layout.inflate(
        // R.layout.main_layout4_shopm_item, null);
        // LinearLayout line = (LinearLayout) root
        // .findViewById(R.id.main_layout4_shopM_item_line);
        // final ImageView img = (ImageView) root
        // .findViewById(R.id.main_layout4_shopM_item_logo);
        //
        // m.setImg(m.getImg().replaceFirst(Web.webImage,
        // Web.webServer));
        // bmUtil.display(img, m.getImg(), config,
        // new BitmapLoadCallBack<View>() {
        // @Override
        // public void onLoadCompleted(View arg0,
        // String arg1, Bitmap arg2,
        // BitmapDisplayConfig arg3,
        // BitmapLoadFrom arg4) {
        // Bitmap bm = Util.zoomBitmap(arg2, _100dp,
        // _80dp);
        // img.setImageBitmap(bm);
        // }
        //
        // @Override
        // public void onLoadFailed(View arg0,
        // String arg1, Drawable arg2) {
        // bmUtil.display(img, m.getImg(), config);
        // }
        // });
        // TextView nameText = (TextView) root
        // .findViewById(R.id.main_layout4_shopM_item_name);
        // nameText.setText(m.getName());
        // TextView phoneText = (TextView) root
        // .findViewById(R.id.main_layout4_shopM_item_phone);
        // Spanned fromHtml = Html
        // .fromHtml("<font color='#ff535353'>电话：</font><u><font color='#ff535353'>"
        // + m.getPhone() + "</font></u>");
        // phoneText.setText(fromHtml);
        // phoneText.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        //
        // Util.doPhone(m.getPhone(), FindLMSJFrame.this);
        // }
        // });
        // TextView cate = (TextView) root
        // .findViewById(R.id.main_layout4_shopM_item_cate);
        // cate.setText(m.getCate());
        // final TextView juli = (TextView) root
        // .findViewById(R.id.main_layout4_shopM_item_juli);
        // if (m.getMm() == Float.MAX_VALUE)
        // juli.setText("无法计算");
        // else
        // juli.setText(getMM(m.getMm()));
        //
        // line.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // if (!"-7".equals(m.getId())) {
        // Util.showIntent(FindLMSJFrame.this,
        // LMSJDetailFrame.class, new String[] {
        // "id", "name", "x", "y" },
        // new String[] { m.getId(), m.getName(),
        // m.getPointX(), m.getPointY() });
        // }
        // }
        // });
        // TextView gotoThat = (TextView) root
        // .findViewById(R.id.main_layout4_shopM_item_goto);
        // gotoThat.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // synchronized (v) {
        // if (Util.isNull(m.getPointX())
        // || Util.isNull(m.getPointY())) {
        // Util.show("对不起，该商家没有定位传送门！",
        // FindLMSJFrame.this);
        // return;
        // }
        // Util.doDaoHang(FindLMSJFrame.this, m,
        // LocationService.getLocation());
        // }
        // }
        // });
        // TextView mysc = (TextView) root
        // .findViewById(R.id.main_layout4_shopM_item_mysc);
        // mysc.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // if (!"-7".equals(m.getId())) {
        // if (null != UserData.getUser()) {
        // if (Data.addMyShopMSc(FindLMSJFrame.this, m))
        // Util.show("收藏成功！", FindLMSJFrame.this);
        // else
        // Util.show("收藏失败！", FindLMSJFrame.this);
        // } else
        // Util.show("请先登录，在收藏", FindLMSJFrame.this);
        // } else {
        // Util.show("该商家不能收藏！", FindLMSJFrame.this);
        // }
        // }
        // });
        // LinearLayout nullLine = new LinearLayout(FindLMSJFrame.this);
        // LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
        // LinearLayout.LayoutParams.FILL_PARENT, Util.dpToPx(
        // FindLMSJFrame.this, 5F));
        // lp.setMargins(0, 0, 0, lp.height);
        // nullLine.setLayoutParams(lp);
        // final int listSize = list.size();
        // layout4GridLine.postDelayed(new Runnable() {
        // @Override
        // public void run() {
        // int allLmsj =
        // Integer.parseInt(tv_tuijian_count.getText().toString().replaceFirst("家",
        // ""));
        // if (allLmsj > listSize)
        // layout4Loadmore.setVisibility(TextView.VISIBLE);
        // else
        // layout4Loadmore.setVisibility(TextView.GONE);
        // }
        // }, 1000);
        // layout4GridLine.addView(root,
        // layout4GridLine.getChildCount() - 1);
        // layout4GridLine.addView(nullLine,
        // layout4GridLine.getChildCount() - 1);
        // }
        // }
        //
        // @Override
        // public Serializable run() {
        // Web web = null;
        // List<ShopM> list = new ArrayList<ShopM>();
        // if (!isCityClick) {
        // // 如果15秒定位失败，则获取推荐联盟商家
        // if (Util.isNull(LocationService.getCity())) {
        // web = new Web(Web.getTuiJianShopMMByPage, "page="
        // + mainLayout4Page + "&size=20");
        // requestMethod = Web.getTuiJianShopMMByPage;
        // } else {
        // String city = Util.isNull(layout4City.getText()
        // .toString()) ? LocationService.getCity()
        // : layout4City.getText().toString();
        // // 定位成功，则获取当前城市的联盟上
        // web = new Web(Web.getShopMByCName, "page="
        // + mainLayout4Page + "&size=20&cName="
        // +
        // Util.get(city)+"&lat="+LocationService.getLocationLatlng().latitude+"&lon="+LocationService.getLocationLatlng().longitude);
        // requestMethod = Web.getShopMByCName;
        // }
        // } else {
        // String city = layout4City.getText().toString();
        // // 定位成功，则获取当前城市的联盟上
        // web = new Web(Web.getShopMByCName, "page="
        // + mainLayout4Page + "&size=20&cName="
        // +
        // Util.get(city)+"&lat="+LocationService.getLocationLatlng().latitude+"&lon="+LocationService.getLocationLatlng().longitude);
        // requestMethod = Web.getShopMByCName;
        // }
        // list = web.getList(ShopM.class);
        // HashMap<String, List<ShopM>> map = new HashMap<String,
        // List<ShopM>>();
        // map.put("list", list);
        // mainLayout4Page++;
        // return map;
        // }
        // });
    }

    private void clearLMSJData() {
        int end = layout4GridLine.getChildCount();
        for (int i = 0; i < end - 1; i++)
            layout4GridLine.removeViewAt(0);
        System.gc();
    }

    private String getMM(float distanceM) {
        String distanceValue = distanceM + "";
        if (distanceM > 1000F) {
            distanceValue = Util.getDouble(
                    Double.valueOf((distanceM / 1000F) + ""), 2)
                    + "公里";
        } else {
            distanceValue = Util.getDouble(Double.valueOf(distanceM + ""), 2)
                    + "米";
        }
        distanceValue = distanceValue.replaceFirst("\\.00", "");
        return distanceValue;
    }

    private class VpAdaper extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            container.addView(imageViews.get(position),
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            return imageViews.get(position);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }

    }
}
