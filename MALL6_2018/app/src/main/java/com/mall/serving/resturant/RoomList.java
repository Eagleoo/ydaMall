package com.mall.serving.resturant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.Resturant;
import com.mall.net.Web;
import com.mall.util.BitmapCache;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;

public class RoomList extends Activity {
    private LinearLayout hotel_name_layout;
    private ListView listview;
    private List<Resturant> list = new ArrayList<Resturant>();
    private RoomListAdapter adapter;
    private String hotelid, hotelname, hoteladdress, checkintime, checkouttime,
            intro, lat, lng, hotellogo;
    private TextView hotel_name, hotel_address, hotel_intro;
    private ImageView hotel_image;
    private Button chek_way, route_mode_submit;
    private String cityName = "";
    private double startLat = 0.00, startLng = 0.00;
    private String rearchMode = "1";
    private List<ImageView> modes = new ArrayList<ImageView>();
    @ViewInject(R.id.room_list_back)
    private ImageView room_list_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomlist);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        getIntentData();
        String title = "";
        if (hotelname.length() > 10) {
            title = hotelname.substring(0, 10);
        } else {
            title = hotelname;
        }
        room_list_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                RoomList.this.finish();
            }
        });
        hotel_name = (TextView) this.findViewById(R.id.hotel_name);
        hotel_name.setText(hotelname);
        hotel_intro = (TextView) this.findViewById(R.id.hotel_intro);
        hotel_intro.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(RoomList.this, HotelDetail.class);
                intent.putExtra("intro", intro);
                RoomList.this.startActivity(intent);
            }
        });
        if (!Util.isNull(intro)) {
            hotel_intro.setText("\t\t" + intro);
        } else {
            hotel_intro.setText("暂无详细介绍......");
        }
        hotel_address = (TextView) this.findViewById(R.id.hotel_address);
        String addr = "";
        if (!Util.isNull(hoteladdress)) {
            if (hoteladdress.length() > 15) {
                addr = hoteladdress.substring(0, 15) + "...";
            } else {
                addr = hoteladdress;
            }
        }
        hotel_image = (ImageView) this.findViewById(R.id.hotel_image);
        if (!Util.isNull(hotellogo)) {
            if (Util.getBitmap(hotellogo) != null) {
                hotel_image.setImageBitmap(Util.getBitmap(hotellogo.replace("160x120", "500x375")));
            } else {
                hotel_image.setImageResource(R.drawable.no_get_banner);
            }
        }
        hotel_address.setText(addr);
        hotel_name_layout = (LinearLayout) this.findViewById(R.id.hotel_name_layout);
        hotel_name_layout.getBackground().setAlpha(150);
        listview = (ListView) this.findViewById(R.id.room_list);
        firstpage();
        // scrollPage();
        chek_way = (Button) this.findViewById(R.id.chek_way);
        chek_way.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RoomList.this);
                LayoutInflater inflater = LayoutInflater.from(RoomList.this);
                View v = inflater.inflate(R.layout.choose_route_mode, null);
                ImageView mode1, mode2, mode3;
                mode1 = (ImageView) v.findViewById(R.id.mode_1);
                mode2 = (ImageView) v.findViewById(R.id.mode_2);
                mode3 = (ImageView) v.findViewById(R.id.mode_3);
                modes.add(mode1);
                modes.add(mode2);
                modes.add(mode3);
                mode1.setOnClickListener(new ModeRadioButton(0));
                mode2.setOnClickListener(new ModeRadioButton(1));
                mode3.setOnClickListener(new ModeRadioButton(2));
                builder.setCancelable(true);
                final Dialog dialog;
                dialog = builder.show();
                Window window = dialog.getWindow();
                WindowManager.LayoutParams pa = window.getAttributes();
                pa.width = Util.dpToPx(RoomList.this, 300);
                pa.height = Util.dpToPx(RoomList.this, 280);
                dialog.setContentView(v, pa);
                route_mode_submit = (Button) v.findViewById(R.id.route_mode_submit);
                route_mode_submit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(RoomList.this, RouteMap.class);
                        intent.putExtra("startLat", startLat);
                        intent.putExtra("startLng", startLng);
                        intent.putExtra("cityname", cityName);
                        intent.putExtra("endLat", Double.parseDouble(lat));
                        intent.putExtra("endLng", Double.parseDouble(lng));
                        if (rearchMode != null) {
                            intent.putExtra("rearchMode", rearchMode);
                        } else {
                            intent.putExtra("rearchMode", "1");
                        }
                        RoomList.this.startActivity(intent);
                    }
                });
            }
        });
    }

    private void getIntentData() {
        if (this.getIntent() != null) {
            hotelid = this.getIntent().getStringExtra("hotelid");
            hotelname = this.getIntent().getStringExtra("hotel_name");
            hoteladdress = this.getIntent().getStringExtra("hoteladdress");
            checkintime = this.getIntent().getStringExtra("checkintime");
            checkouttime = this.getIntent().getStringExtra("checkouttime");
            lat = this.getIntent().getStringExtra("lat");
            lng = this.getIntent().getStringExtra("lng");
            hotellogo = this.getIntent().getStringExtra("hotellogo");
            intro = this.getIntent().getStringExtra("intro");
            cityName = this.getIntent().getStringExtra("cityname");
            startLat = this.getIntent().getDoubleExtra("startLat", 0.00);
            startLng = this.getIntent().getDoubleExtra("startLng", 0.00);
        }
    }

    private void firstpage() {
        asyncLoadData();
    }


    private void asyncLoadData() {
        Util.asynTask(RoomList.this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                jsonToObject(result);
                if (adapter == null) {
                    adapter = new RoomListAdapter(RoomList.this);
                }
                adapter.setList(list);
                listview.setAdapter(adapter);
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service, Web.GetDepart, "hid="
                        + hotelid + "&tm1=" + checkintime + "&tm2=" + checkouttime);
                String result = web.getPlan();
                return result;
            }

        });
    }

    protected void jsonToObject(String result) {
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject myjObject = jsonArray.getJSONObject(i);
                String room_name = myjObject.getString("RoomName");
                String room_breakfast = myjObject.getString("Breakfast");
                String room_leave_numbers = myjObject.getString("RoomNums");
                String room_internet = myjObject.getString("Internet");
                String room_price = myjObject.getString("DalPrice");
                String room_equip = myjObject.getString("RoomTypeDesc");
                String logo = myjObject.getString("logo");
                String planId = myjObject.getString("planId");
                String iscard = myjObject.getString("iscard");
                String stattime = myjObject.getString("stattime");
                String endtime = myjObject.getString("endtime");
                String maxrooms = myjObject.getString("maxrooms");
                String roomcode = myjObject.getString("RoomCode");
                String roomStatus = myjObject.getString("roomStatus");
                String date = myjObject.getString("date");

                Resturant resurant = new Resturant();
                resurant.setRoomname(room_name);
                resurant.setRoombreakfast(room_breakfast);
                resurant.setRoomequip(room_equip);
                resurant.setRoominternet(room_internet);
                resurant.setRoomprice(room_price);
                resurant.setRoomnumbers(room_leave_numbers);
                resurant.setLogo(logo);
                resurant.setPlanId(planId);
                resurant.setIscard(iscard);
                resurant.setStattime(stattime);
                resurant.setEndtime(endtime);
                resurant.setRoomCode(roomcode);
                resurant.setMaxroom(maxrooms);
                resurant.setRoomStatus(roomStatus);
                resurant.setDate(date);
                list.add(resurant);
            }
        } catch (JSONException e) {
        }
    }

    public class ModeRadioButton implements OnClickListener {
        private int index = 0;

        public ModeRadioButton(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            ImageView view = (ImageView) v;
            if (view.getTag().equals("noselected")) {
                view.setImageResource(R.drawable.radiobutton_down);
                view.setTag("selected");
            }
            for (int i = 0; i < modes.size(); i++) {
                if (i != index) {
                    ImageView imageview = modes.get(i);
                    imageview.setImageResource(R.drawable.radiobutton_up);
                    imageview.setTag("noselected");
                }
            }

            LinearLayout layout = (LinearLayout) v.getParent();
            TextView t = (TextView) layout.getChildAt(1);
            rearchMode = t.getText().toString();
            if (t.getText().toString().contains("公交车")) {
                rearchMode = "1";
            } else if (t.getText().toString().contains("驾车")) {
                rearchMode = "2";
            } else if (t.getText().toString().contains("步行")) {
                rearchMode = "3";
            }
        }
    }

    public class RoomListAdapter extends BaseAdapter {
        private Context context;
        private List<Resturant> list = new ArrayList<Resturant>();
        private LayoutInflater inflater;
        private Map<Integer, View> map = new HashMap<Integer, View>();
        private RequestQueue mQueue;
        private ImageLoader mImageLoader;

        public RoomListAdapter(Context c) {
            this.context = c;
            inflater = LayoutInflater.from(c);
            mQueue = Volley.newRequestQueue(c);
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }

        public void setList(List<Resturant> list) {
            this.list.addAll(list);
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
            ViewHolder holder = null;
            Resturant r = list.get(position);
            if (map.get(position) == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.room_item, null);
                holder.room_price = (TextView) convertView
                        .findViewById(R.id.room_price);
                holder.room_name = (TextView) convertView
                        .findViewById(R.id.room_name);
                holder.room_eqip = (TextView) convertView
                        .findViewById(R.id.room_eqip);
                holder.room_breakfreast = (TextView) convertView
                        .findViewById(R.id.room_breakfreast);
                holder.room_internet = (TextView) convertView
                        .findViewById(R.id.room_internet);
                holder.room_number = (TextView) convertView
                        .findViewById(R.id.room_number);
                holder.logo = (ImageView) convertView
                        .findViewById(R.id.room_image);
                holder.hotel_reserve = (Button) convertView
                        .findViewById(R.id.hotel_reserve);
                map.put(position, convertView);
                convertView.setTag(holder);
            } else {
                convertView = map.get(position);
                holder = (ViewHolder) convertView.getTag();
            }
            final Resturant h = r;
            holder.room_price.setText("￥" + r.getRoomprice());
            holder.room_name.setText(r.getRoomname());
            holder.room_eqip.setText(r.getRoomequip());
            holder.room_internet.setText(r.getRoominternet());
            holder.room_number.setText(r.getRoomnumbers());
            holder.room_breakfreast.setText(r.getRoombreakfast());
            ImageListener listener = ImageLoader.getImageListener(holder.logo, R.drawable.no_get_image, R.drawable.no_get_image);
            mImageLoader.get(r.getLogo(), listener);
            holder.hotel_reserve.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //roomStatus为零的时候
                    if (!h.getRoomnumbers().equals("有房")) {
                        Toast.makeText(context, "该房型暂无可预订房间", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(RoomList.this, WriteOrder.class);
                    intent.putExtra("hotelname", hotelname);
                    intent.putExtra("roomname", h.getRoomname());
                    intent.putExtra("checkintime", checkintime);
                    intent.putExtra("checkouttime", checkouttime);
                    intent.putExtra("roomprice", h.getRoomprice());
                    intent.putExtra("roombreakfast", h.getRoombreakfast());
                    intent.putExtra("planId", h.getPlanId());
                    intent.putExtra("iscard", h.getIscard());
                    intent.putExtra("starttime", h.getStattime());
                    intent.putExtra("endtime", h.getEndtime());
                    intent.putExtra("maxrooms", h.getMaxroom());
                    intent.putExtra("hid", hotelid);
                    intent.putExtra("rid", h.getRoomCode());
                    intent.putExtra("date", h.getDate());
                    context.startActivity(intent);
                }
            });
            return convertView;
        }
    }

    public static class ViewHolder {
        ImageView logo;
        TextView room_name;
        TextView room_eqip;
        TextView room_breakfreast;
        TextView room_internet;
        TextView room_number;
        TextView room_price;
        Button hotel_reserve;
    }

}
