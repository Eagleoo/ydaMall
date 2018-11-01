package com.mall.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.github.florent37.fiftyshadesof.FiftyShadesOf;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.BusinessDetails.BusinessDetailsActivity;
import com.mall.model.LMSJCity;
import com.mall.model.ShopM;
import com.mall.model.ShopMCate;
import com.mall.net.UserFavorite;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.Map.LocationMarkerActivity;
import com.mall.widget.DropDownMenu;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

@ContentView(R.layout.lmsj_frame)
public class LMSJFrame extends Activity {

    @ViewInject(R.id.lmsj_frame_city)
    private TextView lsmj_city;
    @ViewInject(R.id.lmsj_frame_cityImg)
    private ImageView lmsj_frame_cityImg;
    @ViewInject(R.id.lmsj_ditu_2_3)
    private ImageView lmsj_ditu_2_3;

    @ViewInject(R.id.dropDownMenu)
    private DropDownMenu dropDownMenu;
    @ViewInject(R.id.topCenter)
    private TextView topCenter;
    @ViewInject(R.id.root)
    private ViewGroup root;

    private String headers[] = {"全部", "附近", "智能排序"};
    private TextView colorItem1;//控制颜色
    private TextView colorItem2_1;//控制颜色
    private TextView colorItem2_2;//控制颜色
    private TextView colorItem3;//控制颜色
    private String unselectColor = "#424242";
    private String selectColor = "#49afef";
    private String types[] = {"智能排序", "离我最近", "好评优先", "人气最高"};
    private String distances[] = {"附近", "1千米", "3千米", "5千米", "10千米"};
    private PositionService locationService;
    public AMapLocation aMapLocation;
    private LMSJListAdapter adapter;
    private View FooterView;
    private boolean isRefreshFoot = false;
    private int page = 1;
    private int size = 10;
    private String zoneid = "";
    private String area = "";
    private String cate = "-1";
    private String orderby = "0";
    private String distance = "" + 100000000;
    private String keyword = "";
    private boolean isbottom = false;
    private int loadCount = 0;
    private List<ShopM> dataList = new ArrayList<ShopM>();
    private TextView emptyView;
    private ListView listView;
    private CustomProgressDialog cpd;
    private Boolean other = false;

    private void zed() {
        other = false;
        headers[0] = "全部";
        headers[1] = "附近";
        headers[2] = "智能排序";
        isRefreshFoot = false;
        page = 1;
        size = 10;
        zoneid = "";
        area = "";
        cate = "-1";
        orderby = "0";
        distance = "" + 100000000;
        isbottom = false;
        listView.removeFooterView(FooterView);
        adapter.clear(true);
    }

    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("LocationMarkerActivity", "bind服务成功！");
            PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
            locationService = locationBinder.getService();
            locationService.startLocation();
            locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
                @Override
                public void onProgress(AMapLocation amapLocation) {
                    if (cpd != null) {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                    if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                        aMapLocation = amapLocation;
                        Log.e("定位成功后回调函数设置经纬度", "精度" + amapLocation.getLongitude() + "纬度" + amapLocation.getLatitude());
                        lsmj_city.setText(amapLocation.getCity());

                        zoneid = amapLocation.getCity();
                        if (!Util.isNull(Util.getCityStr())) {
                            lsmj_city.setText(Util.getCityStr());
                            zoneid = Util.getCityStr();
                            if (type.equals("search")) {
                                zoneid = "";
                            }

                        }
                        page();
                        init();
                    } else {
                        String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                        Toast.makeText(LMSJFrame.this, errText, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(AMapLocation error) {
                    if (error.getErrorCode() == 4) {
                        FiftyShadesOf fiftyShadesOf = FiftyShadesOf.with(LMSJFrame.this)
                                .root(R.id.lin).start();
                        View view = LayoutInflater.from(LMSJFrame.this)
                                .inflate(R.layout.layout_nowifi, null);
                        fiftyShadesOf.failure(view);
                        Toast.makeText(LMSJFrame.this, "定位失败,网络异常，未连接到网络", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LMSJFrame.this, "定位失败," + error.getErrorInfo(), Toast.LENGTH_SHORT).show();
                    }

                    if (cpd != null) {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    String type = "";

    //    LoadingFramelayout mLoadingFramelayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.setContentView(R.layout.lmsj_frame);
//         mLoadingFramelayout = new LoadingFramelayout(this,R.layout.lmsj_frame);
//        setContentView(mLoadingFramelayout);
        ViewUtils.inject(this);
        type = getIntent().getStringExtra("type") + "";
        if (!Util.isNull(type)) {
            Log.e("type", type);
            if (type.equals("search")) {
                keyword = getIntent().getStringExtra("keyword");
                topCenter.setText("搜索结果");
                lsmj_city.setVisibility(View.INVISIBLE);
                lmsj_frame_cityImg.setVisibility(View.INVISIBLE);
                lmsj_ditu_2_3.setVisibility(View.INVISIBLE);
            } else if (type.equals("cate")) {
                cate = getIntent().getStringExtra("cateid");
                if (!getIntent().getStringExtra("title").equals("更多分类")) {
                    headers[0] = getIntent().getStringExtra("title");
                } else {
                    other = true;
                }
                topCenter.setText("联盟商家");
            } else if (type.equals("orderby")) {
                orderby = getIntent().getStringExtra("orderby");
                topCenter.setText("联盟商家");
                headers[2] = "人气最高";
            }
        }
        getLocation();
    }

    private void init() {
        View contentView = getLayoutInflater().inflate(R.layout.lmsj_content, null);
        listView = contentView.findViewById(R.id.lmsj_frame_lmsjList);
        emptyView = contentView.findViewById(R.id.lmsj_frame_emptyView);
        dropDownMenu.setDropDownMenu(Arrays.asList(headers), initViewData(), contentView);
        adapter = new LMSJListAdapter(new LinkedList<ShopM>(), LMSJFrame.this);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isRefreshFoot) {
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        synchronized (view) {
                            page++;
                        }
                        page();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 判断是否滑动到底部弄
                isRefreshFoot = (firstVisibleItem + visibleItemCount == totalItemCount);
            }
        });
    }

    @OnClick(value = {R.id.lmsj_ditu_2_3, R.id.topback, R.id.lmsj_frame_city, R.id.lmsj_frame_cityImg})
    public void cityClick(View v) {
        switch (v.getId()) {
            case R.id.topback:
                finish();
                break;
            case R.id.lmsj_frame_city:
            case R.id.lmsj_frame_cityImg:
                Intent intent = new Intent(this, CitySelectActivity.class);
                intent.putExtra("city", Util.SaveCity);
                intent.putExtra("dqCity_name", lsmj_city.getText().toString());
                this.startActivityForResult(intent, CitySelectActivity._RESUESTCODE);
                break;
            case R.id.lmsj_ditu_2_3:
                checkpermissions();
                break;
        }
    }

    public void getLocation() {
        cpd = CustomProgressDialog.showProgressDialog(
                this, "位置查询中...");
        Intent intent = new Intent();
        intent.setAction(PositionService.TAG);
        intent.setPackage(getPackageName());
        this.getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationService != null) {
            locationService.stopLocation();
            this.getApplicationContext().unbindService(locationServiceConnection);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CitySelectActivity._RESUESTCODE) {
            Log.e("当前城市2", "data.getStringExtra(\"name\")" + data.getStringExtra("name"));
            lsmj_city.setText(data.getStringExtra("name") + "");
            dropDownMenu.clear();
            zed();
            init();
            zoneid = data.getStringExtra("name") + "";
            page();
        }
    }

    private void checkpermissions() {
        Log.e("是否第一次打开app", "1");
        final int[] num = {0, 0, 0};
        final String[] requestPermissionstr = {

                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,

        };
        Log.e("是否第一次打开app", "2");
        RxPermissions rxPermissions = new RxPermissions(this);
        Log.e("是否第一次打开app", "3");
        rxPermissions.requestEach(requestPermissionstr)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {

                        if (permission.granted) {
                            Log.e("是否第一次打开app", "4");
                            num[0]++;
                            Log.e("是否第一次打开app", "同意权限");
                            if (num[0] == requestPermissionstr.length) {

                                Util.showIntent(LMSJFrame.this, LocationMarkerActivity.class);

                            }

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』");
                            num[1]++;
                            Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』" + num[1]);
                            if (num[1] == 1) {
                                com.mall.util.Util.show("请允许应用权限请求...");

                            }
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.e("权限检查", "用户拒绝了该权限，并且选中『不再询问");
                            num[2]++;
                            Log.e("权限检查", "用户拒绝了该权限，并且选中『不再询问" + num[2]);
                            if (num[2] == 1) {
                                com.mall.util.Util.show("请允许应用权限请求...");

                                try {
                                    Uri packageURI = Uri.parse("package:" + LMSJFrame.this.getPackageName());
                                    Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                    startActivity(intent1);
                                } catch (Exception e2) {
                                    // TODO: handle exception
                                }

                            }

                        }

                    }
                });
    }

    private List<View> initViewData() {
        List<View> viewDatas = new ArrayList<>();
        viewDatas.add(getTabView1());
        viewDatas.add(getTabView2());
        viewDatas.add(getTabView3());
        return viewDatas;
    }

    private View getTabView1() {
        View v = getLayoutInflater().inflate(R.layout.lmsj_tabview, null);
        final LinearLayout shopCateLinearLayout = v.findViewById(R.id.linearLayout);
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<ShopMCate>> map = (HashMap<String, List<ShopMCate>>) runData;
                List<ShopMCate> list = map.get("list");
                if (list == null || list.size() <= 0) {
                    return;
                }
                initLMSJClassify(list, shopCateLinearLayout);
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getShopMCate, "");
                HashMap<String, List<ShopMCate>> map = new HashMap<String, List<ShopMCate>>();
                map.put("list", web.getList(ShopMCate.class));
                return map;
            }
        });
        return v;
    }

    private View getTabView2() {
        View v = getLayoutInflater().inflate(R.layout.lmsj_tabview, null);
        final LinearLayout areaLinearLayout = v.findViewById(R.id.linearLayout);
        final LinearLayout distanceLinearLayout = v.findViewById(R.id.distanceLinearLayout);
        if (type.equals("search")) {
            v.findViewById(R.id.leftScroll).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.leftScroll).setVisibility(View.VISIBLE);
        }
        distanceLinearLayout.setVisibility(View.VISIBLE);
        Util.asynTask(new IAsynTask() {
            @Override
            public Serializable run() {
                Web web = new Web(Web.getShopMCity, "");
                List<LMSJCity> listCity = web.getList(LMSJCity.class);
                List<LMSJCity> listArea = new ArrayList<>();
                for (int i = 0; i < listCity.size(); i++) {
                    String name = listCity.get(i).getName();
                    if (name.contains(lsmj_city.getText().toString()) && name.split(" ").length == 3) {
                        LMSJCity lmsjCity = new LMSJCity();
                        lmsjCity.setId(listCity.get(i).getId());
                        lmsjCity.setName(listCity.get(i).getName().split(" ")[2]);
                        listArea.add(lmsjCity);
                    }
                }
                return (Serializable) listArea;
            }

            @Override
            public void updateUI(Serializable runData) {
                initLMSJArea((List<LMSJCity>) runData, areaLinearLayout, distanceLinearLayout);
            }
        });
        return v;
    }

    private View getTabView3() {
        View v = getLayoutInflater().inflate(R.layout.lmsj_tabview, null);
        final LinearLayout typeLinearLayout = v.findViewById(R.id.linearLayout);
        initLMSJType(typeLinearLayout);
        return v;
    }

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

    private void initLMSJClassify(List<ShopMCate> list, LinearLayout container) {
        ShopMCate sm = new ShopMCate();
        sm.setId("18");
        sm.setName("其他");
        ShopMCate sm1 = new ShopMCate();
        sm1.setId("-1");
        sm1.setName("全部");
        list.add(sm);
        list.add(sm1);
        for (int i = 0; i < list.size(); i++) {
            final ShopMCate s = list.get(i);
            TextView t = new TextView(this);
            LinearLayout.LayoutParams lp_text = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp_text.setMargins(40, 20, 20, 20);
            t.setGravity(Gravity.CENTER_VERTICAL);
            if (s.getName().equals(headers[0])) {
                t.setTextColor(Color.parseColor(selectColor));
                colorItem1 = t;
            } else {
                t.setTextColor(Color.parseColor(unselectColor));
            }
            t.setCompoundDrawablesWithIntrinsicBounds(getLMSJDrawable(Integer.parseInt(s.getId())), null, null, null);
            t.setCompoundDrawablePadding(20);
            t.setTextSize(15);
            t.setLayoutParams(lp_text);
            t.setText(s.getName());
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropDownMenu.setTabText(0, s.getName());//设置tab标签文字
                    colorItem1.setTextColor(Color.parseColor(unselectColor));
                    colorItem1 = (TextView) v;
                    colorItem1.setTextColor(Color.parseColor(selectColor));
                    cate = s.getId();
                    page = 1;
                    isbottom = false;
                    listView.removeFooterView(FooterView);
                    adapter.clear(true);
                    page();
                    dropDownMenu.closeMenu();//关闭menu
                }
            });
            container.addView(t);
        }
        if (other) {
            dropDownMenu.getTab(0).performClick();
        }

    }

    private void initLMSJArea(List<LMSJCity> list, LinearLayout container, final LinearLayout distanceLinearLayout) {
        LMSJCity lmsjCity = new LMSJCity();
        lmsjCity.setName("附近");
        list.add(0, lmsjCity);
        for (int i = 0; i < list.size(); i++) {
            final String s = list.get(i).getName();
            TextView t = new TextView(this);
            LinearLayout.LayoutParams lp_text = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp_text.setMargins(40, 20, 20, 20);
            t.setPadding(10, 10, 10, 10);
            t.setGravity(Gravity.CENTER_VERTICAL);
            if (s.equals(headers[1])) {
                t.setTextColor(Color.parseColor(selectColor));
                colorItem2_1 = t;
                colorItem2_1.setBackgroundResource(R.drawable.select_bg);
                t.setTag(true);
            } else {
                t.setTextColor(Color.parseColor(unselectColor));
                t.setTag(false);
            }
            t.setTextSize(15);
            t.setLayoutParams(lp_text);
            t.setText(s);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((boolean) v.getTag()) {
                        distanceLinearLayout.setVisibility(View.VISIBLE);
                    } else {
                        distanceLinearLayout.setVisibility(View.GONE);
                        dropDownMenu.setTabText(1, s);//设置tab标签文字
                        area = s;
                        distance = 100000000 + "";
                        page = 1;
                        isbottom = false;
                        adapter.clear(true);
                        listView.removeFooterView(FooterView);
                        page();
                        dropDownMenu.closeMenu();//关闭menu
                    }
                    colorItem2_1.setTextColor(Color.parseColor(unselectColor));
                    colorItem2_1.setBackground(null);
                    colorItem2_1 = (TextView) v;
                    colorItem2_1.setTextColor(Color.parseColor(selectColor));
                    colorItem2_1.setBackgroundResource(R.drawable.select_bg);
                }
            });
            container.addView(t);
        }
        for (int i = 0; i < 5; i++) {
            final String s = distances[i];
            TextView t = new TextView(this);
            LinearLayout.LayoutParams lp_text = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp_text.setMargins(40, 20, 20, 20);
            t.setGravity(Gravity.CENTER_VERTICAL);
            t.setPadding(10, 10, 10, 10);
            if (s.equals(headers[1])) {
                t.setTextColor(Color.parseColor(selectColor));
                colorItem2_2 = t;
                colorItem2_2.setBackgroundResource(R.drawable.select_bg);
            } else {
                t.setTextColor(Color.parseColor(unselectColor));
            }
            t.setTextSize(15);
            t.setLayoutParams(lp_text);
            t.setText(s);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropDownMenu.setTabText(1, s);//设置tab标签文字
                    colorItem2_2.setTextColor(Color.parseColor(unselectColor));
                    colorItem2_2.setBackground(null);
                    colorItem2_2 = (TextView) v;
                    colorItem2_2.setTextColor(Color.parseColor(selectColor));
                    colorItem2_2.setBackgroundResource(R.drawable.select_bg);
                    if (s.equals("附近")) {
                        distance = 100000000 + "";
                    } else if (s.equals("1千米")) {
                        distance = 1000 + "";
                    } else if (s.equals("3千米")) {
                        distance = 3000 + "";
                    } else if (s.equals("5千米")) {
                        distance = 5000 + "";
                    } else if (s.equals("10千米")) {
                        distance = 10000 + "";
                    }
                    area = "";
                    page = 1;
                    isbottom = false;
                    listView.removeFooterView(FooterView);
                    adapter.clear(true);
                    page();
                    dropDownMenu.closeMenu();//关闭menu
                }
            });
            distanceLinearLayout.addView(t);
        }

    }

    private void initLMSJType(LinearLayout container) {
        for (int i = 0; i < types.length; i++) {
            final String s = types[i];
            TextView t = new TextView(this);
            LinearLayout.LayoutParams lp_text = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp_text.setMargins(40, 20, 20, 20);
            t.setGravity(Gravity.CENTER_VERTICAL);
            t.setPadding(10, 10, 10, 10);
            if (s.equals(headers[2])) {
                t.setTextColor(Color.parseColor(selectColor));
                colorItem3 = t;
                colorItem3.setBackgroundResource(R.drawable.select_bg);
            } else {
                t.setTextColor(Color.parseColor(unselectColor));
            }
            t.setTextSize(15);
            t.setLayoutParams(lp_text);
            t.setText(s);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropDownMenu.setTabText(2, s);//设置tab标签文字
                    colorItem3.setTextColor(Color.parseColor(unselectColor));
                    colorItem3.setBackground(null);
                    colorItem3 = (TextView) v;
                    colorItem3.setTextColor(Color.parseColor(selectColor));
                    colorItem3.setBackgroundResource(R.drawable.select_bg);
                    page = 1;
                    if (s.equals("智能排序")) {
                        orderby = 0 + "";
                    } else if (s.equals("离我最近")) {
                        orderby = 1 + "";
                    } else if (s.equals("好评优先")) {
                        orderby = 2 + "";
                    } else if (s.equals("人气最高")) {
                        orderby = 3 + "";
                    }
                    adapter.clear(true);
                    isbottom = false;
                    listView.removeFooterView(FooterView);
                    page();
                    dropDownMenu.closeMenu();//关闭menu
                }
            });
            container.addView(t);
        }

    }

    private void page() {
        if (isbottom) {
            return;
        }
        Util.asynTask(LMSJFrame.this, "正在加载更多数据...", new IAsynTask() {
            public void updateUI(Serializable runData) {
                HashMap<String, List<ShopM>> map = (HashMap<String, List<ShopM>>) runData;
                List<ShopM> list = map.get("list");
                if (null == list)
                    list = new ArrayList<ShopM>();
                dataList.addAll(list);
                loadCount = list.size();
                LinkedList<ShopM> newData = new LinkedList<ShopM>();
                newData.addAll(list);
                for (ShopM m : newData) {
                    LatLng remote = new LatLng(Double.parseDouble(m
                            .getPointY()), Double.parseDouble(m
                            .getPointX()));
                    String distanceValue = "";
                    float distanceM = 0F;
                    if (null != aMapLocation)
                        distanceM = AMapUtils.calculateLineDistance(
                                new com.amap.api.maps.model.LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), remote);
                    m.setMm(distanceM);
                    distanceValue = LMSJListAdapter.getMM(distanceM);
                    LMSJListAdapter.juliMap.put(m.getId(), distanceValue);
                }
                if (null == adapter) {
                    adapter = new LMSJListAdapter(newData, LMSJFrame.this);
                    listView.setAdapter(adapter);
                } else {
                    adapter.addData(newData);
                }
                adapter.updateUI();
                if (0 == loadCount) {
                    isbottom = true;
                    if (page == 1) {
                        listView.setEmptyView(emptyView);
                    } else {
                        FooterView = LayoutInflater.from(LMSJFrame.this).inflate(R.layout.listfoot, null);
                        listView.addFooterView(FooterView);
                    }
                    System.gc();
                }
            }

            @Override
            public Serializable run() {
                String method = Web.getShopMByCateCity;
                String param = "";
                if (UserData.getUser() != null && UserData.getUser().getUserId() != null) {
                    param = "page=" + page + "&size=" + size + "&zoneid=" + zoneid + "&cate=" + cate + "&orderby=" + orderby + "&distance=" + distance + "&level=6" + "&area=" + area + "&keyword=" + Util.get(keyword)

                            + "&userId=" + UserData.getUser().getUserId() + "&md5Pwd=" +
                            UserData.getUser().getMd5Pwd();
                } else {
                    param = "page=" + page + "&size=" + size + "&zoneid=" + zoneid + "&cate=" + cate + "&orderby=" + orderby + "&distance=" + distance + "&level=6" + "&area=" + area + "&keyword=" + Util.get(keyword);

                }

                if (null != aMapLocation) {
                    param = param + "&lat="
                            + aMapLocation.getLatitude()
                            + "&lng="
                            + aMapLocation.getLongitude();
                }
                Web web = new Web(method, param);
                List<ShopM> list = web.getList(ShopM.class);
                HashMap<String, List<ShopM>> map = new HashMap<String, List<ShopM>>();
                map.put("list", list);
                return map;
            }
        });
    }

    public TextView getEmptyView() {
        return emptyView;
    }

    public ListView getListView() {
        return listView;
    }

}

class LMSJListAdapter extends BaseAdapter {
    private List<ShopM> dataList;
    private LayoutInflater mInflater;
    private Map<String, Bitmap> bmMap;
    private boolean isLoad = false;
    private Context context;
    public static Map<String, String> juliMap = new HashMap<String, String>();// 存放距离

    public LMSJListAdapter(List<ShopM> dataList, Context context) {
        super();
        this.context = context;
        this.dataList = dataList;
        this.mInflater = LayoutInflater.from(context);
        this.bmMap = new HashMap<String, Bitmap>();
        isLoad = true;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).hashCode();
    }

    public void clear(boolean isClear) {
        isLoad = false;
        if (isClear) {
            for (String key : bmMap.keySet()) {
                Bitmap bm = bmMap.get(key);
                if (bm.isRecycled()) {
                    bm.recycle();
                }
            }
            juliMap.clear();
            bmMap.clear();
        }
        this.dataList.clear();
    }

    public void addData(ShopM m) {
        this.dataList.add(dataList.size(), m);
    }

    public List<ShopM> getDataList() {
        return dataList;
    }

    public void addData(List<ShopM> list) {
        this.dataList.addAll(dataList.size(), list);
    }

    public void updateUI() {
        isLoad = true;
        this.notifyDataSetChanged();
        if (0 == this.dataList.size()) {
            ((LMSJFrame) context).getEmptyView().setVisibility(View.VISIBLE);
            ((LMSJFrame) context).getListView().setVisibility(View.GONE);
        } else {
            ((LMSJFrame) context).getListView().setVisibility(View.VISIBLE);
            ((LMSJFrame) context).getEmptyView().setVisibility(View.GONE);
        }
    }

    public void setLoad(boolean isLoad) {
        this.isLoad = isLoad;
    }

    public static String getMM(float distanceM) {
        String distanceValue = distanceM + "";
        if (distanceM > 1000F) {
            distanceValue = Util.getDouble(
                    Double.valueOf((distanceM / 1000F) + ""), 3)
                    + "公里";
        } else {
            distanceValue = Util.getDouble(Double.valueOf(distanceM + ""), 3)
                    + "米";
        }
        distanceValue = distanceValue.replaceFirst("\\.00", "");
        return distanceValue;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HolderView holderView = null;
        final ShopM m = dataList.get(position);
        if (0 == dataList.size())
            return convertView;
        if (null == m || Util.isNull(m.getImg()))
            return convertView;
        if (!isLoad)
            return new LinearLayout(context);
        m.setImg(m.getImg().replaceFirst(Web.webImage, Web.webServer));
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.lmsj_frame_list_item, null);
            holderView = new HolderView();
            holderView.line = convertView.findViewById(R.id.lmsj_list_item_line);
            holderView.cate = convertView.findViewById(R.id.lmsj_list_item_cate);
            holderView.name = convertView.findViewById(R.id.lmsj_list_item_name);
            holderView.phone = convertView.findViewById(R.id.lmsj_list_item_phoneNumber);
            holderView.juli = convertView.findViewById(R.id.lmsj_list_item_juli);
            holderView.logo = convertView.findViewById(R.id.lmsj_list_item_logo);
            holderView.gotoThat = convertView.findViewById(R.id.lmsj_list_item_goto);
            holderView.mysc = convertView.findViewById(R.id.lmsj_list_item_mysc);
            convertView.setTag(holderView);
        } else
            holderView = (HolderView) convertView.getTag();
        holderView.line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Util.showIntent(
                        context,
                        BusinessDetailsActivity.class,
                        new String[]{BusinessDetailsActivity.BUSINESS_ID, "name",
                                "x", "y", "face", BusinessDetailsActivity.BUSINESS_Favorite},
                        new String[]{m.getId(),
                                m.getName(),
                                m.getPointX(),
                                m.getPointY(), m.getImg(), m.getFavorite()});
            }
        });
        Spanned fromHtml = Html.fromHtml("<font color='#ff535353'>电话：</font><u><font color='#ff535353'>" + m.getPhone() + "</font></u>");

        holderView.phone.setText(fromHtml);
        holderView.phone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Util.doPhone(m.getPhone(), context);
            }
        });
        holderView.name.setText(m.getName());
        holderView.cate.setText(m.getCate());
        holderView.gotoThat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.doDaoHang(context, m);
            }
        });
        holderView.mysc.setText(m.getFavorite() != null && m.getFavorite().equals("1") ? "已收藏" : "收藏");
        holderView.mysc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (null != UserData.getUser()) {
                    if (m.getFavorite().equals("0")) {
                        UserFavorite.addFavorite(context, m.getId() + "", "1", new UserFavorite.CallBacke() {
                            @Override
                            public void doback(String message) {
                                ((TextView) v).setText("已收藏");
                                m.setFavorite("1");
                            }


                        });
                    } else {
                        UserFavorite.deletFavorite(context, m.getId() + "", "1", new UserFavorite.CallBackDelete() {
                            @Override
                            public void doback(String message) {
                                ((TextView) v).setText("收藏");
                                m.setFavorite("0");
                            }

                        });
                    }
                } else {
                    Util.show("请先登录，在收藏",
                            context);
                    Util.showIntent(context, LoginFrame.class, new String[]{"home"}, new String[]{"home"});
                }

            }
        });
        final ImageView img = holderView.logo;
        String str = "http://img01.sogoucdn.com/v2/thumb/resize/w/120/h/120/zi/on?t=2&appid=200524&url=" + m.getImg();
        Log.e("imagepath2", str);
        Picasso.with(context).load(str).into(img);
        holderView.juli.setText("计算中");
        if (juliMap.containsKey(m.getId())) {
            holderView.juli.setText(juliMap.get(m.getId()) + "");
        } else {
            calculateDistance(m, holderView.juli);
        }
        return convertView;
    }

    private void calculateDistance(final ShopM m, final TextView v) {
        v.post(new Runnable() {
            public void run() {
                Util.asynTask(new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        v.setText(runData + "");
                    }

                    @Override
                    public Serializable run() {
                        if (!juliMap.containsKey(m.getId())) {
                            if (Util.isNull(m.getPointX())
                                    || Util.isNull(m.getPointY())) {
                                juliMap.put(m.getId(), "无法计算");
                                return juliMap.get(m.getId());
                            } else {
                                if (null == ((LMSJFrame) context).aMapLocation) {
                                    juliMap.put(m.getId(), "无法计算");
                                    return (juliMap.get(m.getId()));
                                }
                                LatLng remote = new LatLng(Double.parseDouble(m
                                        .getPointY()), Double.parseDouble(m
                                        .getPointX()));
                                String distanceValue = "";
                                LatLng local = new LatLng(((LMSJFrame) context).aMapLocation.getLatitude(), ((LMSJFrame) context).aMapLocation.getLongitude());
                                float distanceM = AMapUtils
                                        .calculateLineDistance(local, remote);
                                m.setMm(distanceM);
                                distanceValue = getMM(distanceM);
                                juliMap.put(m.getId(), distanceValue);
                                return (juliMap.get(m.getId()));
                            }
                        } else
                            return (juliMap.get(m.getId()));
                    }
                });
            }
        });
    }
}

class HolderView {
    public LinearLayout line;
    public TextView name;
    public TextView phone;
    public TextView juli;
    public TextView cate;
    public ImageView logo;
    public TextView gotoThat;
    public TextView mysc;
}
