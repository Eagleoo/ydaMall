package com.mall.serving.resturant;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.mall.model.City;
import com.mall.net.Web;
import com.mall.util.CharacterParser;
import com.mall.util.CityPinyinComparator;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.PositionService;
import com.mall.view.R;
import com.mall.view.SideBar;
import com.mall.view.SideBar.OnTouchingLetterChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResturantCity extends Activity {
    private EditText search_city;
    private ListView listview;
    private List<City> city_list = new ArrayList<City>();
    private List<City> all_List = new ArrayList<City>();
    private CityAdapter adapter;
    private List<City> list3 = new ArrayList<City>();
    private SideBar sideBar;
    private TextView dialog;
    private SharedPreferences sp;
    private TextView zmpx, sfpx;
    private String[] hotCity = new String[]{"北京", "上海", "广州", "深圳", "武汉", "天津", "重庆", "成都", "厦门", "昆明", "杭州", "西安", "三亚"};
    private String[] hotCityId = new String[]{"0101", "0201", "2001", "2003", "1801", "0301", "0401", "2301", "1401", "2501", "1201", "2701", "2201"};
    //按照省份排序存在的问题 --------------广东和广西区分不出来
    private PositionService locationService;
    private String cityName, cityId;
    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("FilmList", "bind服务成功！");
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
                        } else {
                            cityName = "定位失败";
                        }
                    }
                    checkCity();
                }

                @Override
                public void onError(AMapLocation error) {
                    checkCity();

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resturantcity);
        init();
        sp = this.getSharedPreferences("resturantcities", 0);
        if (Util.isNull(sp.getString("resturantcity", ""))) {
            getCities("字母");
        } else {
            final Handler cityHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (adapter == null) {
                        if (adapter == null) {
                            adapter = new CityAdapter(ResturantCity.this);
                            listview.setAdapter(adapter);
                        }
                        adapter.setList(all_List);
                        adapter.notifyDataSetChanged();
                    }
                }
            };
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();

                    Intent intent = new Intent();
                    intent.setAction(PositionService.TAG);
                    intent.setPackage(getPackageName());
                    getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);

                    Message m = new Message();
                    m.arg1 = 0;
                    cityHandler.sendMessage(m);
                    Looper.loop();
                    Looper.loop();
                }
            }).start();
        }
    }

    private void init() {

        Util.initTitle(ResturantCity.this, "入住城市", new OnClickListener() {
            @Override
            public void onClick(View v) {
                ResturantCity.this.finish();
            }
        });

        search_city = (EditText) this.findViewById(R.id.search_city);
        listview = (ListView) this.findViewById(R.id.city_list);
        search_city.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText e = (EditText) v;
                String key = e.getText().toString();
                if (hasFocus) {
                    e.setText("");
                    e.setBackgroundResource(R.drawable.editextborder_focus);
                }
            }
        });
        search_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable e) {
                List<City> list2 = new ArrayList<City>();
                if (Util.isNull(e.toString())) {
                    if (adapter == null) {
                        adapter = new CityAdapter(ResturantCity.this);
                        listview.setAdapter(adapter);
                    }
                    adapter.setList(all_List);
                    adapter.notifyDataSetChanged();

                } else {
                    list2.clear();
                    for (int i = 0; i < all_List.size(); i++) {
                        if (all_List.get(i).getcName().contains(e.toString())) {
                            list2.add(all_List.get(i));
                        }
                    }
                    adapter.setList(list2);
                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });
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
        zmpx = (TextView) this.findViewById(R.id.zmpx);
        sfpx = (TextView) this.findViewById(R.id.sfpx);
        zmpx.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                zmpx.setBackgroundResource(R.drawable.textview_red_corner);
                zmpx.setTextColor(Color.WHITE);
                sfpx.setBackgroundResource(R.drawable.textview_white);
                sfpx.setTextColor(Color.RED);
                if (Util.isNull(sp.getString("resturantcity", ""))) {
                    getCities("字母");
                } else {
                    String result = sp.getString("resturantcity", "");
                    jsonToObject(result, "字母");
                    if (adapter == null) {
                        if (adapter == null) {
                            adapter = new CityAdapter(ResturantCity.this);
                        }
                        adapter.clearList();
                        adapter.setList(city_list);
                        adapter.setPaiXu("字母");
                        list3.addAll(city_list);
                        listview.setAdapter(adapter);
                    }
                }
            }
        });
        sfpx.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sfpx.setBackgroundResource(R.drawable.textview_red_corner);
                sfpx.setTextColor(Color.WHITE);
                zmpx.setBackgroundResource(R.drawable.textview_white);
                zmpx.setTextColor(Color.RED);
                if (Util.isNull(sp.getString("resturantcity", ""))) {
                    getCities("省份");
                } else {
                    String result = sp.getString("resturantcity", "");
                    jsonToObject(result, "省份");
                    if (adapter == null) {
                        if (adapter == null) {
                            adapter = new CityAdapter(ResturantCity.this);
                        }
                        adapter.clearList();
                        adapter.setList(city_list);
                        adapter.setPaiXu("省份");
                        list3.addAll(city_list);
                        listview.setAdapter(adapter);
                    }
                }
            }
        });
    }

    private void checkCity() {
        if (!Util.isNull(sp.getString("resturantcity", ""))) {
            jsonToObject(sp.getString("resturantcity", ""), "字母");
        } else {
            getCities("字母");// 如果为保存城市列表，则重新加载
        }
    }

    private void getCities(final String sortBy) {
        Util.asynTask(ResturantCity.this, "正在获取城市数据...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                if (!Util.isNull(result)) {
                    jsonToObject(result, sortBy);
                } else {
                    sideBar.setOnTouchingLetterChangedListener(null);
                    Toast.makeText(ResturantCity.this, "暂未获取到城市数据，请稍后再试", Toast.LENGTH_LONG).show();
                }
                sp.edit().putString("resturantcity", result).commit();
                if (adapter == null) {
                    adapter = new CityAdapter(ResturantCity.this);
                    listview.setAdapter(adapter);
                }
                adapter.setList(all_List);
                list3.addAll(all_List);
                adapter.notifyDataSetChanged();

            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service, Web.getAllCity, "");
                String result = web.getPlanGb2312();
                return result;
            }
        });
    }

    @Override
    protected void onStop() {
        ResturantCity.this.finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (city_list != null)
            city_list.clear();
        if (all_List != null)
            all_List.clear();
        super.onDestroy();
    }

    protected void jsonToObject(String result, String sortBy) {
        CityPinyinComparator pinyinComparator;
        pinyinComparator = new CityPinyinComparator();
        all_List.clear();
        city_list.clear();
        JSONArray jsonArray;
        CharacterParser cp = CharacterParser.getInstance();
        String[] results = result.split(",");
        try {
            Runtime.getRuntime().gc();
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject myjObject = jsonArray.getJSONObject(i);
                City city = new City();
                String cName = myjObject.getString("cName");
                String pName = myjObject.getString("pName");
                String hotel = myjObject.getString("hotel");
                String suoxie = myjObject.getString("suoxie");
                String cid = myjObject.getString("cid");
                String paixuString = "";
                if (sortBy.equals("字母")) {
                    paixuString = cp.convert(cName).toUpperCase();
                } else if (sortBy.equals("省份")) {
                    paixuString = cp.convert(pName).toUpperCase();
                }
                city.setcName(cName);
                city.setpName(pName);
                city.setHotel(hotel);
                city.setSuoxie(suoxie);
                city.setCityId(cid);
                city.setSortLetters(paixuString);
                if (cName.contains("亳")) {
                    System.out.println("亳州=======转ASCNII========" + paixuString);
                }
                if (cName.contains(cityName) || cityName.contains(cName)) {
                    cityId = cid;
                }
                city.setIshot("no");
                city_list.add(city);
            }
            Collections.sort(city_list, pinyinComparator);
        } catch (JSONException e) {
        }
        City cc = new City();
        cc.setCityId(cityId);
        cc.setCityName(cityName);
        cc.setIshot("no");
        cc.setSortLetters("-");
        cc.setSuoxie(" ");
        cc.setHotel("");
        cc.setcName(cityName);
        all_List.add(cc);
        for (int i = 0; i < hotCity.length; i++) {
            City c = new City();
            c.setCityId(hotCityId[i]);
            c.setCityName(hotCity[i]);
            c.setcName(hotCity[i]);
            c.setSortLetters(",");
            c.setSuoxie("热门");
            c.setIshot("yes");
            c.setHotel("");
            all_List.add(c);
        }
        all_List.addAll(city_list);
    }

    public class CityAdapter extends BaseAdapter implements SectionIndexer {
        private Context c;
        private List<City> list = new ArrayList<City>();
        private LayoutInflater inflater;
        private String paixu = "字母";

        public CityAdapter(Context c) {
            this.c = c;
            inflater = LayoutInflater.from(c);
        }

        private void clearList() {
            this.list.clear();
            this.notifyDataSetChanged();
        }

        private void setPaiXu(String p) {
            this.paixu = p;
        }

        public void setList(List<City> list) {
            this.list = list;
            this.notifyDataSetChanged();
        }

        public List<City> getList() {
            return this.list;
        }


        @Override
        public int getCount() {
            return list.size();
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
            ViewHolder holder = null;
            City cx = this.list.get(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.city_item, null);
                holder = new ViewHolder();
                holder.city_name = (TextView) convertView.findViewById(R.id.city_name);
                holder.hotel_count = (TextView) convertView.findViewById(R.id.hotel_count);
                holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
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
                    //根据position获取分类的首字母的Char ascii值
                    int section = getSectionForPosition(position);

                    //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
                    if (position == getPositionForSection(section)) {
                        holder.tvLetter.setVisibility(View.VISIBLE);
                        if (cx.getSortLetters().length() > 1) {
                            if (paixu.equals("字母")) {
                                String le = cx.getSortLetters().substring(0, 1).toUpperCase();
                                holder.tvLetter.setText(le);
                            } else if (paixu.equals("省份")) {
                                holder.tvLetter.setText(cx.getpName());
                            }
                        } else {
                            if (paixu.equals("字母")) {
                                holder.tvLetter.setText(cx.getSortLetters().toUpperCase());
                            } else if (paixu.equals("省份")) {
                                holder.tvLetter.setText(cx.getpName());
                            }
                        }

                    } else {
                        holder.tvLetter.setVisibility(View.GONE);
                    }
                }
            }
            final City cc = cx;
            if (cx.getcName().equals(".") || cx.getcName().equals("..")) {
                holder.city_name.setText(cx.getpName());
            } else {
                holder.city_name.setText(cx.getcName());
            }
            holder.city_name.setTag(cx.getCityId());
            if (Util.isNull(cx.getHotel())) {
                holder.hotel_count.setVisibility(View.GONE);
            } else {
                holder.hotel_count.setText(cx.getHotel() + "家酒店");
            }
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c, ResturantIndex.class);
                    intent.putExtra("cityName", cc.getcName());
                    intent.putExtra("cityId", cc.getCityId());
                    setResult(200, intent);
                    ResturantCity.this.finish();
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
