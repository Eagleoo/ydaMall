package com.mall.serving.filmticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.mall.model.WatchTimeModel;
import com.mall.net.Web;
import com.mall.util.BitmapCache;
import com.mall.util.CharacterParser;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;

public class WatchTime extends Activity {
    private ListView listview;
    private TextView detail, today, tomrrow, dat, cinemaNameT, film_name;
    private String name, cinemaName, cinemaNo, frontimage, filmNo, address, logo, phone, intro, rating, latlng, duration, cityNo = "", filmName = "";
    private ImageView film_image;
    private RequestQueue requestqueue;
    private ImageLoader imageloader;
    private List<WatchTimeModel> timeList = new ArrayList<WatchTimeModel>();
    private WatchTimeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watchtime);
        init();
    }

    private void initView() {
        detail = (TextView) this.findViewById(R.id.detail);
        today = (TextView) this.findViewById(R.id.today);
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        today.setText("今天" + month + "月" + day + "日");
        tomrrow = (TextView) this.findViewById(R.id.tomrrow);
        dat = (TextView) this.findViewById(R.id.dat);
        listview = (ListView) this.findViewById(R.id.time_list);
        film_image = (ImageView) this.findViewById(R.id.film_image);
        detail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WatchTime.this, CinemaDetail.class);
                intent.putExtra("name", cinemaName);
                intent.putExtra("logo", logo);
                intent.putExtra("address", address);
                intent.putExtra("phone", phone);
                intent.putExtra("rating", rating);
                intent.putExtra("latlng", latlng);
                intent.putExtra("intro", intro);
                WatchTime.this.startActivity(intent);
            }
        });
        cinemaNameT = (TextView) this.findViewById(R.id.cinemaNameT);
        film_name = (TextView) this.findViewById(R.id.film_name);
    }

    private void getIntentData() {
        name = this.getIntent().getStringExtra("filmName");
        cinemaName = this.getIntent().getStringExtra("cinemaName");
        cinemaNo = this.getIntent().getStringExtra("cinemaNo");
        frontimage = this.getIntent().getStringExtra("frontimage");
        filmNo = this.getIntent().getStringExtra("filmNo");
        address = this.getIntent().getStringExtra("address");
        phone = this.getIntent().getStringExtra("phone");
        intro = this.getIntent().getStringExtra("intro");
        rating = this.getIntent().getStringExtra("rating");
        logo = this.getIntent().getStringExtra("logo");
        latlng = this.getIntent().getStringExtra("latlng");
        duration = this.getIntent().getStringExtra("duration");
        cityNo = this.getIntent().getStringExtra("cityNo");
        requestqueue = Volley.newRequestQueue(WatchTime.this);
        imageloader = new ImageLoader(requestqueue, new BitmapCache());
        ImageListener listener = ImageLoader.getImageListener(film_image, R.drawable.no_get_image, R.drawable.no_get_image);
        imageloader.get(frontimage, listener);
        cinemaNameT.setText(cinemaName + "   ");
        film_name.setText(name);
    }

    private void init() {

        initView();
        Util.initTitle(WatchTime.this, "影片排期", new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                WatchTime.this.finish();
            }
        });
        getIntentData();
        asyncLoadData();
    }

    private void asyncLoadData() {
        Util.asynTask(WatchTime.this, "正在获取影院排期...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                if (adapter == null) {
                    adapter = new WatchTimeAdapter(WatchTime.this);
                }
                jsonToObject(result);
                adapter.setList(timeList);
                listview.setAdapter(adapter);
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service, Web.Film_getShowTimeByCinemaNoFilmNo, "cinemaNo=" + cinemaNo + "&filmNo=" + filmNo);
                String result = web.getPlan();
                return result;
            }
        });
    }

    protected void jsonToObject(String result) {
        JSONArray jsonArray;
        CharacterParser cp = CharacterParser.getInstance();
        String[] results = result.split(",");
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject myjObject = jsonArray.getJSONObject(i);
                String SeqNo = myjObject.getString("SeqNo");
                String ShowDate = myjObject.getString("ShowDate");
                String ShowTime = myjObject.getString("ShowTime");
                String ShowType = myjObject.getString("ShowType");
                String Language = myjObject.getString("Language");
                String HallNo = myjObject.getString("HallNo");
                String HallName = myjObject.getString("HallName");
                String SeatNum = myjObject.getString("SeatNum");
                String CinemaPrice = myjObject.getString("CinemaPrice");
                String SalePrice = myjObject.getString("SalePrice");

                WatchTimeModel w = new WatchTimeModel();
                w.setSeqNo(SeqNo);
                w.setShowDate(ShowDate);
                w.setShowTime(ShowTime);
                w.setShowType(ShowType);
                w.setLanguage(Language);
                w.setHallNo(HallNo);
                w.setHallName(HallName);
                w.setSeatNum(SeatNum);
                w.setCinemaPrice(CinemaPrice);
                w.setSalePrice(SalePrice);
                timeList.add(w);
            }
        } catch (JSONException e) {
        }
    }

    public class WatchTimeAdapter extends BaseAdapter {
        private Context c;
        private LayoutInflater inflater;
        private List<WatchTimeModel> list = new ArrayList<WatchTimeModel>();

        public WatchTimeAdapter(Context c) {
            this.c = c;
            inflater = LayoutInflater.from(c);
        }

        private void setList(List<WatchTimeModel> list) {
            this.list = list;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            ViewHolder holder;
            WatchTimeModel w = list.get(arg0);
            if (arg1 == null) {
                holder = new ViewHolder();
                arg1 = inflater.inflate(R.layout.watchtime_item, null);
                holder.starttime = (TextView) arg1.findViewById(R.id.t1);
                holder.version = (TextView) arg1.findViewById(R.id.t2);
                holder.ydPrice = (TextView) arg1.findViewById(R.id.t3);
                holder.endtime = (TextView) arg1.findViewById(R.id.t4);
                holder.watchroom = (TextView) arg1.findViewById(R.id.t5);
                holder.marketprice = (TextView) arg1.findViewById(R.id.t6);
                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }
            final WatchTimeModel wc = w;
            holder.starttime.setText(w.getShowTime());
            holder.ydPrice.setText("￥" + w.getSalePrice());
            holder.marketprice.setText("￥" + w.getCinemaPrice());
            holder.watchroom.setText(w.getHallName());
            if (!Util.isNull(w.getLanguage())) {
                holder.version.setText(w.getLanguage());
            } else {
                holder.version.setText("");
            }
            try {
                double d = Double.parseDouble(duration);
                String starttime = wc.getShowTime();
                int hours = (int) (d / 60);
                int minutes = (int) (d % 6);
                String[] times = starttime.split(":");
                int starthour = (int) Double.parseDouble(times[0]);
                int startminu = (int) Double.parseDouble(times[1]);
                int endhour = hours + starthour;
                int endminu = minutes + startminu;
                if (endminu > 60) {
                    endhour += 1;
                    endminu = endminu % 60;
                }
                if (endminu < 10) {
                    holder.endtime.setText(endhour + ":0" + endminu);
                } else {
                    holder.endtime.setText(endhour + ":" + endminu);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            arg1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WatchTime.this, ChooseSeat.class);
                    intent.putExtra("name", name);
                    intent.putExtra("threatername", cinemaName);
                    intent.putExtra("watchtime", wc.getShowTime());
                    intent.putExtra("fangying", wc.getHallName());
                    intent.putExtra("frongimage", frontimage);
                    intent.putExtra("seqno", wc.getSeqNo());
                    intent.putExtra("price", wc.getSalePrice());
                    System.out.println("远大电影售价====" + wc.getSalePrice());
                    intent.putExtra("cinemaNo", cinemaNo);
                    intent.putExtra("filmno", filmNo);
                    intent.putExtra("HallNo", wc.getHallNo());
                    intent.putExtra("cityNo", cityNo);
                    intent.putExtra("duration", duration);
                    intent.putExtra("filmName", filmName + cinemaName);
                    c.startActivity(intent);
                }
            });
            return arg1;
        }
    }

    public class ViewHolder {
        TextView starttime;
        TextView version;
        TextView ydPrice;
        TextView endtime;
        TextView watchroom;
        TextView marketprice;
    }
}
