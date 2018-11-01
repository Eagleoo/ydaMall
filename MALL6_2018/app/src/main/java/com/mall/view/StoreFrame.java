package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.android.common.logging.Log;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.scan.CaptureActivity;
import com.mall.util.BitmapLruCache;
import com.mall.util.Data;
import com.mall.util.Util;
import com.mall.yyrg.NewHuoDong;
import com.mall.yyrg.YYRGMainFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 逛商城 2014年8月22日 16:54:41
 *
 * @author Administrator
 */
public class StoreFrame extends Activity {
    @ViewInject(R.id.layout1_banner)
    private ViewPager layout1Banner;
    @ViewInject(R.id.main_layout1_sreach)
    private EditText layout1Serach;
    private BitmapUtils bmUtil;
    private BitmapLruCache bmLruCache;
    int mainLayout3ScrollViewY = 0;
    int mainLayout3Page = 1;

    @ViewInject(R.id.ll_detail)
    private LinearLayout ll_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout1);
        ViewUtils.inject(this);
        bmLruCache = new BitmapLruCache(StoreFrame.this);
        bmUtil = new BitmapUtils(this);
        bmUtil.configDefaultLoadFailedImage(R.drawable.zw174);
        getListDeatil();
        initLayout1();

    }

    private String[] detail = {"平台畅销", "平台精选", "热门推荐", "电子数码"
            , "生活家电", "个护化妆", "居家百货"
    };
    private String[] detailID = {"2", Util.detailID, "1", "1147"
            , "1093", "498", "526"
    };

    private Integer[] titleit = {R.drawable.red, R.drawable.green, R.drawable.yellow,
            R.drawable.green, R.drawable.green, R.drawable.green, R.drawable.green};

    private void getListDeatil() {


        final View[] views = new View[7];
        views[0] = LayoutInflater.from(StoreFrame.this).inflate(
                R.layout.layout1_item, null);
        views[1] = LayoutInflater.from(StoreFrame.this).inflate(
                R.layout.layout1_item, null);
        views[2] = LayoutInflater.from(StoreFrame.this).inflate(
                R.layout.layout1_item, null);
        views[3] = LayoutInflater.from(StoreFrame.this).inflate(
                R.layout.layout1_item, null);
        views[4] = LayoutInflater.from(StoreFrame.this).inflate(
                R.layout.layout1_item, null);
        views[5] = LayoutInflater.from(StoreFrame.this).inflate(
                R.layout.layout1_item, null);
        views[6] = LayoutInflater.from(StoreFrame.this).inflate(
                R.layout.layout1_item, null);
        NewWebAPI.getNewInstance().getWebRequest(
                "/Product.aspx?call=getHomeProduct&cache=cache&cacheKey=store",
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", StoreFrame.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"),
                                    StoreFrame.this);
                            return;
                        }
                        JSONObject[] iselite = json.getJSONArray("iselite").toArray(
                                new JSONObject[]{});
                        JSONObject[] ancient = json.getJSONArray("ancient").toArray(
                                new JSONObject[]{});
                        JSONObject[] weishang = json.getJSONArray("weishang")
                                .toArray(new JSONObject[]{});
                        JSONObject[] shuma = json.getJSONArray("shuma")
                                .toArray(new JSONObject[]{});
                        JSONObject[] fushi = json.getJSONArray("fushi")
                                .toArray(new JSONObject[]{});
                        JSONObject[] huazhuang = json.getJSONArray("huazhuang")
                                .toArray(new JSONObject[]{});
                        JSONObject[] baojian = json.getJSONArray("baojian")
                                .toArray(new JSONObject[]{});
                        List<JSONObject[]> jsonList = new ArrayList<JSONObject[]>();
                        jsonList.add(iselite);
                        jsonList.add(ancient);
                        jsonList.add(weishang);
                        jsonList.add(shuma);
                        jsonList.add(fushi);
                        jsonList.add(huazhuang);
                        jsonList.add(baojian);
                        Log.e("页面长度", views.length + "");
                        for (int i = 0; i < views.length; i++) {
                            View item = views[i];
                            ImageView imageView = (ImageView) item.findViewById(R.id.titleleft_iv);

                            TextView tv_name = (TextView) item
                                    .findViewById(R.id.tv_name);
                            TextView tv_more = (TextView) item
                                    .findViewById(R.id.tv_more);
                            TextView tv_title_1 = (TextView) item
                                    .findViewById(R.id.tv_title_1);
                            TextView tv_title_2 = (TextView) item
                                    .findViewById(R.id.tv_title_2);
                            TextView tv_title_3 = (TextView) item
                                    .findViewById(R.id.tv_title_3);
                            TextView tv_title_4 = (TextView) item
                                    .findViewById(R.id.tv_title_4);
                            TextView tv_title_5 = (TextView) item
                                    .findViewById(R.id.tv_title_5);
                            TextView tv_title_6 = (TextView) item
                                    .findViewById(R.id.tv_title_6);
                            ImageView iv_1 = (ImageView) item
                                    .findViewById(R.id.iv_1);
                            ImageView iv_2 = (ImageView) item
                                    .findViewById(R.id.iv_2);
                            ImageView iv_3 = (ImageView) item
                                    .findViewById(R.id.iv_3);
                            ImageView iv_4 = (ImageView) item
                                    .findViewById(R.id.iv_4);
                            ImageView iv_5 = (ImageView) item
                                    .findViewById(R.id.iv_5);
                            ImageView iv_6 = (ImageView) item
                                    .findViewById(R.id.iv_6);
                            final TextView[] tvs = {tv_title_1, tv_title_2,
                                    tv_title_3, tv_title_4, tv_title_5, tv_title_6};
                            final ImageView[] ivs = {iv_1, iv_2, iv_3, iv_4, iv_5, iv_6};
                            final String name = detail[i];
                            Log.e("数据标题", name + "");
                            tv_name.setText(name);
                            imageView.setImageResource(titleit[i]);
                            final String id = detailID[i];
                            setView(tvs, ivs, jsonList.get(i));
                            tv_more.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {

//									Log.e("点击","更多");
                                    Data.setProductClass(1);
                                    if (id.equals("1")) {
                                        Log.e("点击", "平台畅销");
                                        Util.showIntent(StoreFrame.this,
                                                ProductListFrame.class, new String[]{"catId",
                                                        "catName", "state"},
                                                new String[]{id, name, "1"});
                                    } else if (id.equals("2")) {
                                        Log.e("点击", "热门推荐");
                                        Util.showIntent(StoreFrame.this,
                                                ProductListFrame.class, new String[]{"catId",
                                                        "catName", "state"},
                                                new String[]{id, name, "2"});
                                    } else {
                                        Log.e("点击", "更多2");
                                        Util.showIntent(StoreFrame.this,
                                                ProductListFrame.class,
                                                new String[]{"catId",
                                                        "catName"},
                                                new String[]{id, name});
                                    }
                                }
                            });

                        }

                        ll_detail.addView(views[0]);
                        ll_detail.addView(views[1]);
                        ll_detail.addView(views[2]);
                        ll_detail.addView(views[3]);
                        ll_detail.addView(views[4]);
                        ll_detail.addView(views[5]);
                        ll_detail.addView(views[6]);

                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
//                        ll_detail.addView(views[0]);
//                        ll_detail.addView(views[1]);
//                        ll_detail.addView(views[2]);
//                        ll_detail.addView(views[3]);
//                        ll_detail.addView(views[4]);
//                        ll_detail.addView(views[5]);
//                        ll_detail.addView(views[6]);
                    }
                });
    }

    @SuppressWarnings("static-access")
    private void setView(TextView[] names, ImageView[] imgs, JSONObject[] jsons) {
        for (int i = 0; i < names.length; i++) {
            JSONObject json = null;
            if (i < jsons.length) {
                json = jsons[i];
            } else {
                json = new JSONObject();
            }
            final String pid = json.getString("pid");
            names[i].setText(json.getString("name"));
            // AnimeUtil.getImageLoad().displayImage(json.getString("thumb"),
            // imgs[i]);
            // imgs[i].setTag(json.getString("thumb"));
            // bmLruCache.loadImage(imgs[i], json.getString("thumb"), 0);
            com.mall.util.BitmapUtils.loadBitmap(json.getString("thumb"),
                    imgs[i]);
            OnClickListener clickListener = new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent();
                    intent.setClass(StoreFrame.this, ProductDeatilFream.class);
                    intent.putExtra("url", pid);
                    startActivity(intent);
                }
            };
            names[i].setOnClickListener(clickListener);
            imgs[i].setOnClickListener(clickListener);
        }
    }

    @OnClick({R.id.main_layout2_lxkf, R.id.main_layout3_lxkf,
            R.id.main_layout4_lxkf,})
    public void lxkf3(View v) {
        Util.doPhone(Util._400, this);
    }

    @OnClick({R.id.shouyeerweima, R.id.layout1_erweima00})
    public void erweimaClick(View v) {
        Util.showIntent(this, CaptureActivity.class);
    }

    @OnClick(R.id.main_layout1_remai)
    public void qianggou(View v) {
        Data.setProductClass(1);
        Util.showIntent(this, ProductListFrame.class, new String[]{"catId",
                "catName"}, new String[]{"1471", "精品鞋城"});
    }

    public static final Map<String, String> cateName = new HashMap<String, String>();

    static {
        cateName.put("419", "潮流服饰");
        cateName.put("1471", "精品鞋城");
        cateName.put("589", "精品箱包");
        cateName.put("498", "个护化妆");
        cateName.put("1178", "珠宝饰品");
        cateName.put("526", "居家百货");
        cateName.put("707", "汽车配件");
        cateName.put("469", "食品保健");
        cateName.put("1147", "手机数码");
        cateName.put("1093", "生活家电");
        cateName.put("1079", "创业工具");
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
                    layout1Serach.setText(str);
                    serach(str);
                }

            }
        });
    }

    @OnClick({R.id.shangc_yiyuan, R.id.shangc_qianggou, R.id.shangc_jifen,
            R.id.shangc_shangbr})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.shangc_yiyuan:
                Util.showIntent(this, YYRGMainFrame.class);
                break;

            case R.id.shangc_shangbr:
                Util.showIntent(this, SBQFrame.class);
                break;

            case R.id.shangc_qianggou:
                Util.showIntent(this, NewHuoDong.class);
                break;

            case R.id.shangc_jifen:

                Data.setProductClass(1);
                Util.showIntent(this, HuangouFrame.class);
                break;

        }
    }

    @OnClick({R.id.main_layout1_clfs, R.id.main_layout1_gxhz,
            R.id.main_layout1_jjbh, R.id.main_layout1_jpxb,
            R.id.main_layout1_jpxc, R.id.main_layout1_qcpj,
            R.id.main_layout1_shjd, R.id.main_layout1_sjsm,
            R.id.main_layout1_zbsp, R.id.main_layout1_spbj, R.id.ll_ywgj})
    public void doFenLei(View v) {

        Data.setProductClass(1);
        Util.showIntent(this, ProductListFrame.class, new String[]{"catId",
                        "catName"},
                new String[]{v.getTag() + "", cateName.get(v.getTag() + "")});

    }

    @OnClick(R.id.main_layout1_sbtn)
    public void layout1FDJ(View v) {
        String sValue = layout1Serach.getText().toString();
        serach(sValue);
    }

    /**
     * 查询商品的方法
     *
     * @param sValue 关键字
     */
    private void serach(String sValue) {
        if (!Util.isNull(sValue) && !"".equals(sValue.trim())) {
            Intent intent = new Intent();
            intent.setClass(this, ProductSreachFrame.class);
            intent.putExtra("type1", "查询");
            intent.putExtra("sValue", sValue);
            StoreFrame.this.startActivity(intent);
            layout1Serach.setText("");
        } else
            Util.show("请输入您要查询的商品!", StoreFrame.this);

    }

    // #start 第一屏 ---------------------------------------
    private Handler mainLayout1BannerHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            layout1Banner.setCurrentItem(Integer.parseInt(msg.obj + ""));
        }
    };

    private List<ImageView> layout1List = null;

    private int[] bannerId = {R.drawable.weixinbanner};
    private String[] urlId = {"233590"};

    public void initLayout1() {

        int lineId = R.id.serach_ajax_result1;
        int listId = R.id.serach_ajax_result_list1;
        layout1Serach.addTextChangedListener(new AjaxResultListener(this,
                layout1Serach, lineId, listId));
        layout1List = new ArrayList<ImageView>();

        for (int j = 0; j < urlId.length; j++) {
            ImageView iv = new ImageView(StoreFrame.this);
            iv.setImageResource(bannerId[j]);
            iv.setScaleType(ScaleType.CENTER_CROP);
            final int m = j;
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Util.openWeb(StoreFrame.this, urlId[m]);
                    if (urlId[m].matches("^\\d+$")) {
                        Intent intent = new Intent(StoreFrame.this,
                                ProductDeatilFream.class);
                        intent.putExtra("url", urlId[m]);
                        StoreFrame.this.startActivity(intent);
//                        Intent intent = new Intent();
//                        intent.setAction("android.intent.action.VIEW");
//                        intent.setData(Uri.parse("http://www.yda360.com/Activity/ActivitiesOne.aspx?id=34"));
//                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(StoreFrame.this,
                                ProductSreachFrame.class);
                        intent.putExtra("sValue", urlId[m]);
                        StoreFrame.this.startActivity(intent);
                    }
                }
            });

            layout1List.add(iv);
        }

        layout1Banner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        layout1Banner.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return layout1List.size();
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView((View) arg2);
            }

            @Override
            public Object instantiateItem(View arg0, int arg1) {
                ((ViewPager) arg0).addView(layout1List.get(arg1));
                return layout1List.get(arg1);
            }
        });
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    int end = layout1List.size();
                    for (int i = 0; i < end; i++) {
                        try {
                            Thread.currentThread().sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message msg = new Message();
                        int index = i + 1;
                        if (index > end)
                            index = 0;
                        msg.obj = i;
                        msg.what = -1;
                        mainLayout1BannerHanlder.sendMessage(msg);
                    }
                }
            }
        }).start();
    }

}
