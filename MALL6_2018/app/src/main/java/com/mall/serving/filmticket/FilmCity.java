package com.mall.serving.filmticket;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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
import com.mall.model.FilmCityModel;
import com.mall.net.Web;
import com.mall.util.CharacterParser;
import com.mall.util.IAsynTask;
import com.mall.util.PinyinComparator;
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


public class FilmCity extends Activity {
    private ListView listview;
    private List<FilmCityModel> city_list = new ArrayList<FilmCityModel>();
    private List<FilmCityModel> all_List = new ArrayList<FilmCityModel>();
    private CityAdapter adapter;
    private SideBar sideBar;
    private TextView dialog;
    private CharacterParser cp;
    private PinyinComparator pinyinComparator;
    private SharedPreferences sp;
    private String[] hotCity = new String[]{"北京市", "上海市", "广州市", "深圳市", "武汉市", "天津市", "重庆市", "成都市", "厦门市", "昆明市", "杭州市", "西安市", "三亚市"};
    private String[] hotCityId = new String[]{"110100", "310100", "440100", "440300", "420100", "120100", "500100", " ", "350200", " ", "330100", "610100", " "};
    private EditText search;
    private PositionService locationService;
    private String cityName, cityId;
    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("FilmList", "bind服务成功！");
            Log.e("LocationMarkerActivity", "bind服务成功！");
            PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
            locationService = locationBinder.getService();
            locationService.startLocation();
            locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
                @Override
                public void onProgress(AMapLocation progress) {

                    if (!Util.isNull(progress.getCity())) {
                        cityName = progress.getCity();
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
        sp = this.getSharedPreferences("filmcity", 0);
        pinyinComparator = new PinyinComparator();
        init();
    }

    private void init() {
        Util.initTitle(FilmCity.this, "所在城市", new OnClickListener() {
            @Override
            public void onClick(View v) {
                FilmCity.this.finish();
            }
        });
        initView();
        if (!Util.isNull(sp.getString("cities", ""))) {
            final Handler cityHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (adapter == null) {
                        adapter = new CityAdapter(FilmCity.this);
                        listview.setAdapter(adapter);
                    }
                    adapter.setList(all_List);
                    adapter.notifyDataSetChanged();
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
                }
            }).start();
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
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable e) {
                List<FilmCityModel> list2 = new ArrayList<FilmCityModel>();
                if (Util.isNull(e.toString())) {
                    if (adapter == null) {
                        adapter = new CityAdapter(FilmCity.this);
                        listview.setAdapter(adapter);
                    }
                    adapter.setList(all_List);
                    adapter.notifyDataSetChanged();
                } else {
                    list2.clear();
                    for (int i = 0; i < all_List.size(); i++) {
                        if (all_List.get(i).getAreaName().contains(e.toString())) {
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
        if (!Util.isNull(sp.getString("cities", ""))) {
            jsonToObject(sp.getString("cities", ""));
        } else {
            getCities();// 如果为保存城市列表，则重新加载
        }
    }

    private void getCities() {
        Util.asynTask(FilmCity.this, "正在获取城市数据...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                if (Util.isNull(result)) {
                    sideBar.setOnTouchingLetterChangedListener(null);
                    Toast.makeText(FilmCity.this, "暂未获取到城市数据，请稍后再试", Toast.LENGTH_SHORT).show();
                } else {
                    jsonToObject(result);
                    if (adapter == null) {
                        adapter = new CityAdapter(FilmCity.this);
                        adapter.setList(all_List);
                    }
                    listview.setAdapter(adapter);
                }

            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service, Web.Film_getAreaList, "", "gb2312");
                String result = web.getPlanGb2312();
                return result;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        FilmCity.this.finish();
        super.onStop();
    }

    protected void jsonToObject(String result) {
        all_List.clear();
        JSONArray jsonArray;
        String[] results = result.split(",");
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject myjObject = jsonArray.getJSONObject(i);
                FilmCityModel city = new FilmCityModel();
                String AreaNo = myjObject.getString("AreaNo");
                String AreaName = myjObject.getString("AreaName");
                String AreaLevel = myjObject.getString("AreaLevel");
                //剔除掉省一级的区域
                if (!AreaLevel.equals("2")) {
                    continue;
                }
                String pAreaNo = myjObject.getString("pAreaNo");
                String paixuString = "";
                try {
                    paixuString = cp.convert(AreaName).toUpperCase();//这个地方调试的时候是有值得，为什么会报空指针？？？
                } catch (Exception e) {
                    System.out.println("AreaName-====" + AreaName + "_______" + cp.convert(AreaName));
                    paixuString = "#";
                }
                if (AreaName.contains(cityName) || cityName.contains(AreaName)) {
                    cityId = AreaNo;
                }
                city.setAreaNo(AreaNo);
                city.setAreaName(AreaName);
                city.setAreaLevel(AreaLevel);
                city.setpAreaNo(pAreaNo);
                city.setSortLetters(paixuString);
                city.setIsHot("no");
                city_list.add(city);
            }
        } catch (JSONException e) {
        }
        FilmCityModel fc = new FilmCityModel();
        fc.setAreaName(cityName);
        fc.setAreaNo(cityId);
        fc.setIsHot("no");
        fc.setSortLetters("-");
        fc.setAreaLevel("2");
        all_List.add(fc);

        //热门城市
        for (int i = 0; i < hotCity.length; i++) {
            FilmCityModel c = new FilmCityModel();
            c.setAreaName(hotCity[i]);
            c.setAreaLevel("2");
            c.setAreaNo(hotCityId[i]);
            c.setSortLetters(",");
            c.setIsHot("yes");
            all_List.add(c);
        }
        Collections.sort(city_list, pinyinComparator);
        all_List.addAll(city_list);
    }

    public class CityAdapter extends BaseAdapter implements SectionIndexer {
        private Context c;
        private List<FilmCityModel> list = new ArrayList<FilmCityModel>();
        private LayoutInflater inflater;
        private int in = 0;

        public CityAdapter(Context c) {
            this.c = c;
            inflater = LayoutInflater.from(c);
        }

        public void setList(List<FilmCityModel> list) {
            this.list = list;
            this.notifyDataSetChanged();
        }

        public List<FilmCityModel> getList() {
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
            FilmCityModel cx = list.get(position);
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
                if (cx.getIsHot().equals("yes")) {
                    if (position == 1) {
                        holder.tvLetter.setVisibility(View.VISIBLE);
                        holder.tvLetter.setText("热门城市");
                    } else {
                        holder.tvLetter.setVisibility(View.GONE);
                    }
                } else if (cx.getIsHot().equals("no")) {
                    //根据position获取分类的首字母的Char ascii值
                    int section = getSectionForPosition(position);
                    //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
                    if (position == getPositionForSection(section)) {
                        holder.tvLetter.setVisibility(View.VISIBLE);
                        if (cx.getSortLetters().length() > 1) {
                            String le = cx.getSortLetters().substring(0, 1).toUpperCase();
                            holder.tvLetter.setText(le);
                        } else {
                            holder.tvLetter.setText(cx.getSortLetters().toUpperCase());
                        }
                    } else {
                        holder.tvLetter.setVisibility(View.GONE);
                    }
                }
            }
            final FilmCityModel cc = cx;
            holder.city_name.setText(cx.getAreaName());
            holder.city_name.setTag(cx.getAreaNo());
            holder.hotel_count.setVisibility(View.GONE);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(c, FilmList.class);
                    intent.putExtra("areaname", cc.getAreaName());
                    intent.putExtra("areano", cc.getAreaNo());
                    intent.putExtra("arealevel", cc.getAreaLevel());
                    c.startActivity(intent);
                    FilmCity.this.finish();
                }
            });

            return convertView;
        }

        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        public int getSectionForPosition(int position) {
            if (list.get(position).getIsHot().equals("yes")) {
                return 0;
            } else {
                return list.get(position).getSortLetters().charAt(0);
            }
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
