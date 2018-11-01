package com.mall.serving.orderplane;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.lidroid.xutils.util.LogUtils;
import com.mall.model.PlaneCityModel;
import com.mall.net.Web;
import com.mall.util.CharacterParser;
import com.mall.util.IAsynTask;
import com.mall.util.PlaneCityCompartor;
import com.mall.util.Util;
import com.mall.view.PositionService;
import com.mall.view.R;
import com.mall.view.SideBar;
import com.mall.view.SideBar.OnTouchingLetterChangedListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaneCities extends Activity {
    private ListView listview;
    private List<PlaneCityModel> city_list = new ArrayList<PlaneCityModel>();
    private List<PlaneCityModel> all_List = new ArrayList<PlaneCityModel>();
    private CityAdapter adapter;
    private SideBar sideBar;
    private TextView dialog;
    private CharacterParser cp;
    private PlaneCityCompartor pinyinComparator;
    private SharedPreferences sp;
    private EditText search;

    private String[] hotCity = new String[]{"北京首都", "北京南苑", "上海浦东", "上海虹桥", "广州", "深圳", "武汉", "天津", "重庆", "成都", "厦门", "昆明", "杭州", "西安", "三亚"};
    private String[] hotCityId = new String[]{"PEK", "NAY", "PVG", "SHA", "CAN", "SZX", "WUH", "TSN", "CKG", "CTU", "XMN", "KMG", "HGH", "XIY", "SYX"};
    private PositionService locationService;
    private String cityName, cityId;
    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("PlaneCities", "bind服务成功！");
            PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
            locationService = locationBinder.getService();
            locationService.startLocation();
            locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
                @Override
                public void onProgress(AMapLocation progress) {

                    if (!Util.isNull(progress.getCity())) {
                        cityName = progress.getCity();
                        if (!TextUtils.isEmpty(cityName) && cityName.endsWith("市")) {
                            cityName = cityName.replace("市", "");
                        }
                    } else {
                        cityName = "定位失败";
                    }
                    checkCity();

                }

                @Override
                public void onError(AMapLocation error) {
                    checkCity();
                    cityName = "定位失败";
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filmcity);
        cp = CharacterParser.getInstance();
        sp = this.getSharedPreferences("PlaneCities", 0);
        pinyinComparator = new PlaneCityCompartor();
        init();
    }

    private void init() {
        Util.initTitle(PlaneCities.this, "起飞城市", new OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaneCities.this.finish();
            }
        });
        initView();
        LogUtils.e(sp.getString("planecities", "") + "__________");
        if (!Util.isNull(sp.getString("planecities", ""))) {

            Intent intent = new Intent();
            intent.setAction(PositionService.TAG);
            intent.setPackage(getPackageName());
            getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);

        } else {
            getCities();
        }

    }

    private void initView() {
        listview = (ListView) this.findViewById(R.id.city_list);
        sideBar = (SideBar) this.findViewById(R.id.sidrbar);
        dialog = (TextView) this.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                if (s.equals("热门")) {
                    listview.setSelection(1);
                } else if (s.equals("当前")) {
                    listview.setSelection(0);
                } else {
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        listview.setSelection(position);
                    }
                }
            }
        });
        search = (EditText) this.findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable e) {
                List<PlaneCityModel> list2 = new ArrayList<PlaneCityModel>();
                String s = e.toString();
                if (Util.isNull(search.getText().toString())) {
                    if (adapter == null) {
                        adapter = new CityAdapter(PlaneCities.this);
                        listview.setAdapter(adapter);
                    }
                    adapter.setList(all_List);
                    adapter.notifyDataSetChanged();
                } else {
                    list2.clear();
                    for (int i = 0; i < all_List.size(); i++) {
                        if (all_List.get(i).getName().contains(e.toString())) {
                            list2.add(all_List.get(i));
                        }
                    }
                    adapter.setList(list2);
                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void checkCity() {
        if (!Util.isNull(sp.getString("planecities", ""))) {
            parseCities(sp.getString("planecities", ""));
        } else {
            getCities();// 如果为保存城市列表，则重新加载
        }
    }

    private void parseCities(String result) {
        String[] results = result.split("@");
        all_List.clear();
        for (int i = 0; i < results.length; i++) {
            PlaneCityModel pm = new PlaneCityModel();
            String[] rs = results[i].split("\\|");
            pm.setName(rs[0]);
            pm.setSortLetters(rs[1]);
            pm.setCity(rs[2]);
            pm.setIshot("no");
            String ci = cityName.replace("市", "");
            if (cityName.contains(rs[0])) {
                cityId = rs[2];
            }
            city_list.add(pm);
        }
        PlaneCityModel poc = new PlaneCityModel();
        poc.setName(cityName);
        poc.setIshot("no");
        poc.setCity(cityId);
        poc.setSortLetters("-");
        all_List.add(poc);
        for (int i = 0; i < hotCity.length; i++) {
            PlaneCityModel pc = new PlaneCityModel();
            pc.setName(hotCity[i]);
            pc.setSortLetters(",");
            pc.setCity(hotCityId[i]);
            pc.setIshot("yes");
            all_List.add(pc);
        }
        Collections.sort(city_list, pinyinComparator);
        all_List.addAll(city_list);

        if (adapter == null) {
            adapter = new CityAdapter(PlaneCities.this);
            listview.setAdapter(adapter);
        }
        adapter.setList(all_List);
        adapter.notifyDataSetChanged();
    }

    private void getCities() {
        Util.asynTask(PlaneCities.this, "正在获取城市数据...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                sp.edit().putString("planecities", result).commit();
                if (!Util.isNull(result)) {
                    parseCities(result);
                } else {
                    sideBar.setOnTouchingLetterChangedListener(null);
                    Toast.makeText(PlaneCities.this, "暂未获取到城市数据，请稍后再试", Toast.LENGTH_SHORT).show();
                }

                if (adapter == null) {
                    adapter = new CityAdapter(PlaneCities.this);
                    listview.setAdapter(adapter);
                }
                adapter.setList(all_List);
                adapter.notifyDataSetChanged();
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service, Web.Ticket_getCity, "");
                String result = web.getPlanGb2312();
                return result;
            }
        });
    }

    public class CityAdapter extends BaseAdapter implements SectionIndexer {
        private Context c;
        private List<PlaneCityModel> list = new ArrayList<PlaneCityModel>();
        private LayoutInflater inflater;

        public CityAdapter(Context c) {
            this.c = c;
            inflater = LayoutInflater.from(c);
        }

        public void setList(List<PlaneCityModel> list) {
            this.list = list;
            this.notifyDataSetChanged();
        }

        public List<PlaneCityModel> getList() {
            return this.list;
        }

        public void clearList() {
            this.list.clear();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            PlaneCityModel cx = list.get(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.city_item, null);
                holder = new ViewHolder();
                holder.city_name = (TextView) convertView
                        .findViewById(R.id.city_name);
                holder.hotel_count = (TextView) convertView
                        .findViewById(R.id.hotel_count);
                holder.tvLetter = (TextView) convertView
                        .findViewById(R.id.catalog);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == 0) {
                holder.tvLetter.setVisibility(View.VISIBLE);
                holder.tvLetter.setText("GPS定位城市");
            } else {
                if (cx.getIshot().equals("yes")) {
                    if (position == 1) {
                        holder.tvLetter.setVisibility(View.VISIBLE);
                        holder.tvLetter.setText("热门城市");
                    } else {
                        holder.tvLetter.setVisibility(View.GONE);
                    }
                } else if (cx.getIshot().equals("no")) {
                    // 根据position获取分类的首字母的Char ascii值
                    int section = getSectionForPosition(position);

                    // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
                    if (position == getPositionForSection(section)) {
                        holder.tvLetter.setVisibility(View.VISIBLE);
                        if (cx.getSortLetters().length() > 1) {
                            String le = cx.getSortLetters().substring(0, 1)
                                    .toUpperCase();
                            holder.tvLetter.setText(le);
                        } else {
                            holder.tvLetter.setText(cx.getSortLetters().toUpperCase());
                        }
                    } else {
                        holder.tvLetter.setVisibility(View.GONE);
                    }
                }
            }
            final PlaneCityModel cc = cx;
            holder.city_name.setText(cx.getName());
            holder.city_name.setTag(cx.getSortLetters());
            holder.hotel_count.setVisibility(View.GONE);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c, OrderPlaneIndex.class);
                    intent.putExtra("name", cc.getName());
                    intent.putExtra("city", cc.getCity());
                    setResult(200, intent);
                    PlaneCities.this.finish();
                }
            });

            return convertView;
        }

        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        public int getSectionForPosition(int position) {
            return list.get(position).getSortLetters().charAt(0);
        }

        /**
         * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
         */
        public int getPositionForSection(int section) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = list.get(i).getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }

            return -1;
        }

        /**
         * 提取英文的首字母，非英文字母用#代替。
         *
         * @param str
         * @return
         */
        private String getAlpha(String str) {
            String sortStr = str.trim().substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortStr.matches("[A-Z]")) {
                return sortStr;
            } else {
                return "#";
            }
        }

        @Override
        public Object[] getSections() {
            return null;
        }

    }

    public class ViewHolder {
        TextView city_name;
        TextView hotel_count;
        TextView tvLetter;
    }
}
