package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.dingwei.CharacterParser;
import com.mall.dingwei.ClearEditText;
import com.mall.dingwei.PinyinComparator;
import com.mall.dingwei.SideBar;
import com.mall.dingwei.SideBar.OnTouchingLetterChangedListener;
import com.mall.dingwei.SortAdapter;
import com.mall.dingwei.SortModel;
import com.mall.model.HotCity;
import com.mall.model.LocationModel;
import com.mall.model.Zone;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.Data;
import com.mall.util.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CitySelectActivity extends Activity {

    public static final int _RESUESTCODE = 10001;

    @ViewInject(R.id.city_province)
    private ListView provinceList;
    @ViewInject(R.id.cityList)
    private GridView cityList;

    private BaseMallAdapter<Zone> provinceAdapter;
    private BaseMallAdapter<Zone> cityAdapter;
    private View provinceView;
    private View cityView;

    // 赵超修改
    @ViewInject(R.id.btn_myselect_sheng)
    private TextView btn_myselect_sheng;
    @ViewInject(R.id.btn_myselect_city)
    private TextView btn_myselect_city;
    @ViewInject(R.id.tv_dangqian_map)
    private TextView tv_dangqian_map;// 当前定位城市

    private GridView hotcity_gv;
    private View vHead;

    private boolean isShow = false;
    private boolean isShowcity = false;
    private List<Zone> sheng;
    private List<Zone> cityDataList;// 城市列表(市)
    private List<Zone> newZoneList1;
    private List<Zone> newZoneList2 = new ArrayList<Zone>();

    // 赵超修改
    @ViewInject(R.id.country_lvcountry)
    private ListView sortListView;
    private com.mall.dingwei.SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;

    CustomProgressDialog cpd;

    // 赵超修改,汉字转换成拼音的类
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    // 赵超修改，根据拼音来排列ListView里面的数据类
    private PinyinComparator pinyinComparator;


    private String sheng1 = "";


    private static class MyHandler extends Handler {
        private final WeakReference<CitySelectActivity> mActivity;

        public MyHandler(CitySelectActivity activity) {
            mActivity = new WeakReference<CitySelectActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println(msg);
            if (mActivity.get() == null) {
                return;
            }
            if (msg.what == 0x123) {

                mActivity.get().provinceList.setAdapter(mActivity.get().provinceAdapter = new BaseMallAdapter<Zone>(R.layout.city_item, mActivity.get(), mActivity.get().sheng) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent, Zone t) {
                        getCacheView(R.id.catalog).setVisibility(View.GONE);
                        getCacheView(R.id.hotel_count).setVisibility(View.GONE);
                        setText(R.id.city_name, t.getName()).setTag(t.getId());
                        return convertView;
                    }
                });

                mActivity.get().initcity();


            } else if (msg.what == 0x456) {
                mActivity.get().cityAdapter = new BaseMallAdapter<Zone>(R.layout.city_item, mActivity.get(), mActivity.get().cityDataList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent, Zone t) {
                        getCacheView(R.id.catalog).setVisibility(View.GONE);
                        getCacheView(R.id.hotel_count).setVisibility(View.GONE);
                        setText(R.id.city_name, t.getName()).setTag(t.getId());
                        return convertView;
                    }
                };
                mActivity.get().cityList.setAdapter(mActivity.get().cityAdapter);
                if (mActivity.get().cpd != null) {
                    mActivity.get().cpd.cancel();
                    mActivity.get().cpd.dismiss();
                }

                mActivity.get().cityinit();
            }
        }
    }

    private Context context;
    Myadapter myadapter;

    private List<HotCity.ListBean> hotcitylist = new ArrayList<>();
    private final Handler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.city_select);
        ViewUtils.inject(this);

        context = this;
        vHead = LayoutInflater.from(context).inflate(R.layout.layout_hotcity, null);
        hotcity_gv = vHead.findViewById(R.id.hotcity_gv);
        sortListView.addHeaderView(vHead);
        init();


        // 赵超修改
        initDingWei();

        cityList = findViewById(R.id.cityList);

        getHotCitylist();

    }


    public void getHotCitylist() {
        final CustomProgressDialog cpd = CustomProgressDialog
                .showProgressDialog(this, "正在获取热门城市...");

        NewWebAPI.getNewInstance().getWebRequest("/Alliance.aspx?call=" + "getHomeLMSJ_Hot_City",
                new WebRequestCallBack() {

                    @Override
                    public void success(Object result) {


                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"),
                                    context);
                            return;
                        }
                        Gson gson = new Gson();
                        HotCity hotCity = gson.fromJson(result.toString(), HotCity.class);

                        hotcitylist.addAll(hotCity.getList());

                        myadapter.notifyDataSetChanged();


                    }

                    @Override
                    public void fail(Throwable e) {
                        LogUtils.e("网络请求错误：", e);
                    }

                    @Override
                    public void timeout() {
                        LogUtils.e("网络请求超时！");
                        Util.show("小二很忙，系统很累，请稍候...", App.getContext());
                    }

                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }

                });
    }

    public void cityinit() {
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplication(), ((SortModel) adapter.getItem(position - 1)).getName(), Toast.LENGTH_SHORT)
                        .show();


                CitySelectActivity.this.setResult(_RESUESTCODE, getResultIntent2((SortModel) adapter.getItem(position - 1)));
                CitySelectActivity.this.finish();
                Util.zoneid = ((SortModel) adapter.getItem(position - 1)).getZoneid();


            }
        });
        SourceDateList = filledData2(newZoneList2);
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);
    }


    private void initDingWei() {
        // TODO Auto-generated method stub
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        // sideBar=(SideBars) findViewById(R.id.sidrbar);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // TODO Auto-generated method stub
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });


        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    // 为ListView填充数据2
    private List<SortModel> filledData2(List<Zone> cityDataList) {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        for (int i = 0; i < cityDataList.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(cityDataList.get(i).getName());
            sortModel.setZoneid(cityDataList.get(i).getId());

            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(cityDataList.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }

        return mSortList;
    }

    // 为ListView填充数据
    private List<SortModel> filledData(String[] date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        for (int i = 0; i < date.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date[i]);
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }

        return mSortList;
    }

    // 根据输入框中的值来过滤数据并更新ListView
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    private void init() {
        inintMyHotCityAdapter();
        // 赵超修改,设置当前定位城市名称
        tv_dangqian_map.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                .setDefaultStrokeColor(Color.parseColor("#DEDEDE"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setCornerRadius(Util.dpToPx(context, 3))
                .create());
        try {
            LocationModel model = LocationModel.getLocationModel();
            if (!Util.isNull(model.getCity())) {
                tv_dangqian_map.setText(model.getCity());
            }

        } catch (Exception e) {

        }

        tv_dangqian_map.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("点击item", "点击");
                Toast.makeText(getApplication(), tv_dangqian_map.getText().toString(), Toast.LENGTH_SHORT)
                        .show();

//dddd
//
//                CitySelectActivity.this.setResult(_RESUESTCODE, getResultIntent2((SortModel) adapter.getItem(position - 1)));
//                CitySelectActivity.this.finish();
//                Util.zoneid = ((SortModel) adapter.getItem(position - 1)).getZoneid();
//
                Intent intent = new Intent();
                intent.putExtra("name", tv_dangqian_map.getText().toString());
                CitySelectActivity.this.setResult(_RESUESTCODE, intent);
                CitySelectActivity.this.finish();
            }
        });


        Util.initTitle(this, "选择城市", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btn_myselect_sheng.setText("您正在看:" + getIntent().getStringExtra("dqCity_name"));

        // 选择省、市的监听器
        MyOnClickListener myListener = new MyOnClickListener();
        btn_myselect_sheng.setOnClickListener(myListener);
        btn_myselect_city.setOnClickListener(myListener);
        // 当用户点击省列表后，隐藏省列表
        provinceList.setVisibility(View.GONE);
        isShow = false;

        cpd = CustomProgressDialog.showProgressDialog(this, "正在获取城市...");


        new Thread() {
            public void run() {
                sheng = Data.getShen();
                mHandler.sendEmptyMessage(0x123);

            }
        }.start();


    }

    private void initcity() {
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < sheng.size(); i++) {

                    // 赵超修改为18，让其默认显示四川省各级城市
                    initCityData(provinceAdapter.getItem(18).getId());

                    newCityList(provinceAdapter.getItem(i).getId());
                }
                mHandler.sendEmptyMessage(0x456);
            }
        }.start();


    }

    private void inintMyHotCityAdapter() {
        myadapter = new Myadapter(context, hotcitylist);
        hotcity_gv.setHorizontalSpacing(Util.dpToPx(context, 25));
        hotcity_gv.setVerticalSpacing(Util.dpToPx(context, 8));
        hotcity_gv.setAdapter(myadapter);
        hotcity_gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("点击item", "点击");
                Toast.makeText(getApplication(), hotcitylist.get(position).getName(), Toast.LENGTH_SHORT)
                        .show();
                //赵超修改
                Intent intent = new Intent();
                intent.putExtra("name", hotcitylist.get(position).getName());
                intent.putExtra("id", hotcitylist.get(position).getZoneid());
                CitySelectActivity.this.setResult(_RESUESTCODE, intent);
                CitySelectActivity.this.finish();
            }
        });
    }

    // 赵超添加
    private void newCityList(String newZongId) {


        // 赵超添加方法，为下拉城市列表数据进行填充
        if (newZoneList1 == null) {
            newZoneList1 = new ArrayList<Zone>();
        }
        newZoneList1.clear();

        newZoneList1 = Data.getShi(newZongId);

        for (Zone zone : newZoneList1) {
            sheng1 += zone.getName() + ",父级" + zone.getParentid();
            Log.e("城市名", sheng1);
            newZoneList2.add(zone);
        }

    }


    private void initCityData(String zoneId) {

        // 赵超修改，设置定位


        cityDataList = Data.getShi(zoneId);

    }

    @OnItemClick(R.id.city_province)
    public void provinceItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != provinceView)
            provinceView.setBackgroundColor(Color.parseColor("#ffffff"));
        view.setBackgroundColor(Color.parseColor("#f0f0f0"));
        provinceView = view;
        initCityData(provinceAdapter.getItem(position).getId());
    }

    @OnItemClick(R.id.cityList)
    public void cityItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != cityView)
            cityView.setBackgroundColor(Color.parseColor("#ffffff"));
        view.setBackgroundColor(Color.parseColor("#f0f0f0"));
        cityView = view;
        this.setResult(_RESUESTCODE, getResultIntent(cityAdapter.getItem(position)));
        this.finish();
    }

    //赵超修改
    private Intent getResultIntent2(SortModel sortModel) {
        Intent intent = new Intent();
        intent.putExtra("id", sortModel.getZoneid());
        intent.putExtra("name", sortModel.getName());
        intent.putExtra("sortLetters", sortModel.getSortLetters());
        return intent;
    }


    private Intent getResultIntent(Zone zone) {
        Intent intent = new Intent();
        intent.putExtra("name", zone.getName());
        intent.putExtra("id", zone.getId());
        return intent;
    }

    // 赵超修改，选择省、城市的按钮监听器类
    class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_myselect_sheng:
                    if (isShow) {
                        provinceList.setVisibility(View.GONE);
                        isShow = false;
                    } else {
                        provinceList.setVisibility(View.VISIBLE);
                        cityList.setVisibility(View.GONE);
                        isShowcity = false;
                        isShow = true;
                    }
                    break;
                case R.id.btn_myselect_city:
                    if (isShowcity) {
                        cityList.setVisibility(View.GONE);
                        isShowcity = false;
                    } else {
                        cityList.setVisibility(View.VISIBLE);
                        provinceList.setVisibility(View.GONE);
                        isShow = false;
                        isShowcity = true;
                    }
                    break;

                default:
                    break;
            }
        }

    }

    class Myadapter extends BaseAdapter {
        List<HotCity.ListBean> citylist;
        Context context;


        public Myadapter(Context context, List<HotCity.ListBean> citylist) {
            this.context = context;
            this.citylist = citylist;
        }

        @Override
        public int getCount() {
            return citylist == null ? 0 : citylist.size();
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
            HotCity.ListBean listBean = citylist.get(position);
            ViewCache cache;
            if (convertView == null) {
                cache = new ViewCache();
                convertView = LayoutInflater.from(context).inflate(R.layout.itemtext, parent, false);
                cache.cityitem = (TextView) convertView.findViewById(R.id.tv);
                cache.cityitem.setInputType(InputType.TYPE_NULL);
                convertView.setTag(cache);
            }
            cache = (ViewCache) convertView.getTag();
            cache.cityitem.setHeight(Util.dpToPx(context, 40));


            cache.cityitem.setBackground(SelectorFactory.newShapeSelector()
                    .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                    .setDefaultStrokeColor(Color.parseColor("#DEDEDE"))
                    .setStrokeWidth(Util.dpToPx(context, 1))
                    .setCornerRadius(Util.dpToPx(context, 3))
                    .create());


            cache.cityitem.setText(listBean.getName());


            return convertView;
        }

        class ViewCache {
            TextView cityitem;

        }
    }

}
