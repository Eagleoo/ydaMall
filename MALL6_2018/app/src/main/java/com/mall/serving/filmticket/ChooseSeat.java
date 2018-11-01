package com.mall.serving.filmticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.mall.model.SeatModel;
import com.mall.net.Web;
import com.mall.util.BitmapCache;
import com.mall.util.CharacterParser;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.util.WHD;
import com.mall.view.R;

public class ChooseSeat extends Activity {
    private String seqNo = "", name = "", threatername = "", watchtime = "",
            fangyingS = "", frontimage = "", cinemaNo = "", HallNo = "", filmNo = "", CityNo = "", filmName = "", duration = "";
    private TextView time, threater, film_name, seat, fangying, totalprice, priceandnumber, durationT;
    private ImageView film_image;
    private RequestQueue requestqueue;
    private ImageLoader imageloader;
    private List<SeatModel> seats = new ArrayList<SeatModel>();
    private LinearLayout seatcontainer;
    private LayoutInflater inflater;
    private List<String> choosedSeatsList = new ArrayList<String>();
    private double price = 0.00;
    private String prices = "";
    private Button submit;
    private String Seats = "";
    private String LocNo = "";
    private int screenHeight = 0;
    private int screenWidht = 0;
    private int dpi = 0;
    private HorizontalScrollView horizontal_scroll;
    float originalX = 0;
    float originalY = 0;
    float nowX = 0;
    float nowY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooseseat);
        WHD wh = Util.getScreenSize(this);
        screenHeight = wh.getHeight();
        screenWidht = wh.getWidth();
        dpi = wh.getDpi();
        init();
    }

    @Override
    protected void onStart() {
        seat.setText("");
        choosedSeatsList.clear();
        getSeatInfo();
        super.onStart();
    }

    private void init() {
        Util.initTitle(ChooseSeat.this, "选择座位", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inflater = LayoutInflater.from(this);
        initView();
        getIntentData();
    }

    private void initView() {
        time = (TextView) this.findViewById(R.id.time);
        threater = (TextView) this.findViewById(R.id.threater);
        film_name = (TextView) this.findViewById(R.id.film_name);
        seat = (TextView) this.findViewById(R.id.seat);
        fangying = (TextView) this.findViewById(R.id.fangying);
        film_image = (ImageView) this.findViewById(R.id.film_image);
        seatcontainer = (LinearLayout) this.findViewById(R.id.seatcontainer);
        int _250px = Util.dpToPx(ChooseSeat.this, 250);
        int screentHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        android.view.ViewGroup.LayoutParams lp = seatcontainer.getLayoutParams();
        lp.height = (screentHeight - _250px);
        totalprice = (TextView) this.findViewById(R.id.totalprice);
        priceandnumber = (TextView) this.findViewById(R.id.priceandnumber);
        submit = (Button) this.findViewById(R.id.submit);
        durationT = (TextView) this.findViewById(R.id.duration);
    }

    private void getIntentData() {
        duration = this.getIntent().getStringExtra("duration");
        if (!Util.isNull(duration)) {
            durationT.setText(duration + "分钟");
        }
        seqNo = this.getIntent().getStringExtra("seqno");
        name = this.getIntent().getStringExtra("name");
        threatername = this.getIntent().getStringExtra("threatername");
        watchtime = this.getIntent().getStringExtra("watchtime");
        fangyingS = this.getIntent().getStringExtra("fangying");
        cinemaNo = this.getIntent().getStringExtra("cinemaNo");
        filmName = this.getIntent().getStringExtra("filmName");
        filmNo = this.getIntent().getStringExtra("filmno");
        HallNo = this.getIntent().getStringExtra("HallNo");
        prices = this.getIntent().getStringExtra("price");
        CityNo = this.getIntent().getStringExtra("cityNo");
        frontimage = this.getIntent().getStringExtra("frongimage");

        price = Double.parseDouble(prices);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        time.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + "    " + watchtime);
        threater.setText(threatername);
        film_name.setText(name);
        fangying.setText(fangyingS);
        requestqueue = Volley.newRequestQueue(ChooseSeat.this);
        imageloader = new ImageLoader(requestqueue, new BitmapCache());
        ImageListener listener = ImageLoader.getImageListener(film_image, R.drawable.no_get_image, R.drawable.no_get_image);
        imageloader.get(frontimage, listener);
        totalprice.setText("￥" + price + "元");
        priceandnumber.setText("￥" + price + "x1");
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseSeat.this, FilmPayOrder.class);
                String sd = fangyingS;
                if (Util.isNull(seat.getText().toString()) || Util.isNull(totalprice.getText().toString())) {
                    Toast.makeText(ChooseSeat.this, "您还没有选择座位，请选择", Toast.LENGTH_LONG).show();
                    return;
                }
                String[] s = seat.getText().toString().split("    ");
                Seats = "";
                for (int i = 0; i < s.length; i++) {
                    s[i] = s[i].replace("排", "-");
                    String sx = s[i].replace("列", "");
                    int col = Integer.parseInt(sx.split("-")[1]);
                    String colno = "";
                    if (col < 10) {
                        colno = "0" + col + "";
                    } else {
                        colno = sx.split("-")[1];
                    }
                    if ((i + 1) == s.length) {
                        Seats += LocNo + "_" + sx.split("-")[0] + "_" + colno;
                    } else {
                        Seats += LocNo + "_" + sx.split("-")[0] + "_" + colno + "|";
                    }
                }
                intent.putExtra("seqno", seqNo);//排期编号
                intent.putExtra("name", name);
                intent.putExtra("theatername", threatername);
                intent.putExtra("watchtime", time.getText().toString());
                intent.putExtra("fangying", fangyingS);
                intent.putExtra("cinemaNo", cinemaNo);//影院ID
                intent.putExtra("filmName", filmName);
                intent.putExtra("filmno", filmNo);//电影编号
                intent.putExtra("HallNo", HallNo);//放映厅ID
                intent.putExtra("oneprice", prices + "");//单价
                intent.putExtra("cityNo", CityNo);
                intent.putExtra("frontimage", frontimage);
                intent.putExtra("price", totalprice.getText() + "");
                intent.putExtra("seat", sd + "    " + seat.getText().toString());
                intent.putExtra("seats", choosedSeatsList.size());//作为数量
                intent.putExtra("Seats", Seats);
                seatcontainer.removeAllViews();
                ScrollView sc = (ScrollView) seatcontainer.getParent();
                sc.postInvalidate();
                sc.invalidate();
                HorizontalScrollView hi = (HorizontalScrollView) sc.getParent();
                hi.postInvalidate();
                hi.invalidate();
                seats.clear();
                ChooseSeat.this.startActivity(intent);
            }
        });

    }

    @Override
    protected void onStop() {
        if (seats != null && seats.size() > 0) {
            seats.clear();
        }
        super.onStop();
    }

    protected void jsonCityToObject(String result) {
        CharacterParser cp = CharacterParser.getInstance();
        JSONArray jsonArray;
        String[] results = result.split(",");
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject myjObject = jsonArray.getJSONObject(i);
                String RowNo = myjObject.getString("RowNo");
                String ColumnNo = myjObject.getString("ColumnNo");
                String SeatType = myjObject.getString("SeatType");
                String SeatStatus = myjObject.getString("SeatStatus");
                LocNo = myjObject.getString("LocNo");
                SeatModel sm = new SeatModel();
                sm.setRowNo(RowNo);
                sm.setColumnNo(ColumnNo);
                sm.setSeatType(SeatType);
                sm.setSeatStatus(SeatStatus);
                sm.setLocNo(LocNo);
                seats.add(sm);
            }
        } catch (JSONException e) {
        }
    }

    /*
     * zl zl zl zl 1,0,0,0,0,1,1,0,zl,zl
     * zl,zl,1,0
     */
    private void getSeatInfo() {
        Util.asynTask(ChooseSeat.this, "正在初始化放映大厅信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                seatcontainer.removeAllViews();
                seatcontainer.removeAllViewsInLayout();
                seatcontainer.invalidate();
                seatcontainer.postInvalidate();

                String result = (String) runData;
                jsonCityToObject(result);
                String rowNoumber = "";
                String colNumber = "";
                int height = 0;
                int itemwidth = 0;
                int zlIndex = computeSeats(seats);
                System.out.println("走廊下标=============" + zlIndex);
                for (int i = 0; i < seats.size(); i++) {
                    boolean kong = false;
                    LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.seat_item, null);
                    LinearLayout frame = (LinearLayout) layout.getChildAt(0);
                    LinearLayout l = (LinearLayout) frame.getChildAt(0);
                    LinearLayout seatcon = (LinearLayout) frame.getChildAt(1);
                    TextView row = (TextView) l.getChildAt(0);
                    SeatModel s = seats.get(i);
                    rowNoumber = s.getRowNo();  //行号
                    if (rowNoumber.equals("0")) {
                        kong = true;
                    }
                    if (kong) {
                        row.setVisibility(View.GONE);
                        continue;
                    } else {
                        row.setText(rowNoumber);
                    }
                    for (int k = 0; k < s.getColumnNo().split(",").length; k++) {
                        if (k < zlIndex) {
                            continue;
                        }
//						LinearLayout layout2=(LinearLayout) inflater.inflate(R.layout.seat_textview, null);
//						TextView t=(TextView) layout2.getChildAt(0);
                        TextView t = new TextView(ChooseSeat.this);
                        int _20px = Util.dpToPx(ChooseSeat.this, 20);
                        int _15px = Util.dpToPx(ChooseSeat.this, 20);
                        LinearLayout.LayoutParams lpa = new LinearLayout.LayoutParams(_20px, _15px);
                        lpa.setMargins(5, 5, 5, 5);
                        t.setLayoutParams(lpa);
                        boolean zl = false;
                        if (s.getColumnNo().split(",")[k].equals("ZL")) {
                            zl = true;
                        }
                        if (zl) {
                            t.setTag(-7, "");
                        } else {
                            colNumber = s.getColumnNo().split(",")[k];
                            //如果对应位置为1, 则已经售出，0,则未售出
                            if (!Util.isNull(s.getSeatStatus().split(",")[k])) {
                                if (s.getSeatStatus().split(",")[k].equals("1")) {
                                    t.setBackgroundResource(R.drawable.seat_choosed);
                                    t.setTag(-7, "soldout");
                                } else if (s.getSeatStatus().split(",")[k].equals("0")) {
                                    t.setBackgroundResource(R.drawable.seat_no_selected);
                                    t.setTag(-7, "noselected");
                                }
                            }
                            t.setTag(rowNoumber + "排" + colNumber + "列");
                        }
                        t.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextView t = (TextView) v;
                                if (t.getTag(-7).equals("selected")) {
                                    t.setBackgroundResource(R.drawable.seat_no_selected);
                                    choosedSeatsList.remove(t.getTag().toString());
                                    t.setTag(-7, "noselected");
                                    String s = "";
                                    for (int i = 0; i < choosedSeatsList.size(); i++) {
                                        s += choosedSeatsList.get(i) + "    ";
                                    }
                                    seat.setText(s);
                                    totalprice.setText("￥" + (price * choosedSeatsList.size()));
                                    priceandnumber.setText("￥" + price + "x" + choosedSeatsList.size());
                                } else if (t.getTag(-7).equals("noselected")) {
                                    if (choosedSeatsList.size() < 4) {
                                        t.setBackgroundResource(R.drawable.seat_selected);
                                        choosedSeatsList.add(t.getTag().toString());
                                        t.setTag(-7, "selected");
                                        String s = "";
                                        for (int i = 0; i < choosedSeatsList.size(); i++) {
                                            s += choosedSeatsList.get(i) + "    ";
                                        }
                                        seat.setText(s);
                                        totalprice.setText("￥" + (price * choosedSeatsList.size()));
                                        priceandnumber.setText("￥" + price + "x" + choosedSeatsList.size());
                                    } else {
                                        Toast.makeText(ChooseSeat.this, "对不起，每笔订单最多就能够选择4个座位.", Toast.LENGTH_LONG).show();
                                    }
                                } else if (t.getTag(-7).equals("soldout")) {
                                    return;
                                }
                            }
                        });
                        seatcon.addView(t);
                    }
                    seatcontainer.addView(layout);
                }
                seatcontainer.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("seatContainer onClick");
                        for (int i = 0; i < seatcontainer.getChildCount(); i++) {
                            LinearLayout layout = (LinearLayout) seatcontainer.getChildAt(i);
                            LinearLayout frame = (LinearLayout) layout.getChildAt(0);
                            LinearLayout l = (LinearLayout) frame.getChildAt(0);
                            TextView rowno = (TextView) l.getChildAt(0);
                            int _40px = Util.dpToPx(ChooseSeat.this, 30);
                            int _30px = Util.dpToPx(ChooseSeat.this, 25);
                            LinearLayout.LayoutParams pa = new LinearLayout.LayoutParams(_40px, _40px);
                            rowno.setLayoutParams(pa);
                            LinearLayout con = (LinearLayout) frame.getChildAt(1);
                            for (int k = 0; k < con.getChildCount(); k++) {
                                TextView t = (TextView) con.getChildAt(k);
                                LinearLayout.LayoutParams lpa = new LinearLayout.LayoutParams(_40px, _30px);
                                lpa.setMargins(5, 5, 5, 5);
                                t.setLayoutParams(lpa);
                            }
                        }
                        seatcontainer.setOnClickListener(null);
                    }
                });
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service, Web.Film_getSeat, "SeqNo=" + seqNo);
                String result = web.getPlan();
                return result;
            }
        });
    }

    private int computeSeats(List<SeatModel> seats) {
        String[] indexs = new String[seats.size()];
        for (int i = 0; i < indexs.length; i++) {
            indexs[i] = "10000";
        }
        for (int i = 0; i < seats.size(); i++) {
            SeatModel model = seats.get(i);
            String[] colums = model.getColumnNo().split(",");
            if (model.getRowNo().equals("0")) {
                indexs[i] = "10000";
                continue;
            }
            for (int k = 0; k < colums.length; k++) {
                if (!colums[k].equalsIgnoreCase("ZL") && indexs[i].equals("10000")) {
                    indexs[i] = k + "";
                }
            }
        }
        return getMax(indexs);
    }

    private int getMax(String[] indexs) {
        int minindex = Integer.parseInt(indexs[0]);
        for (int i = 0; i < indexs.length; i++) {
            int index2 = Integer.parseInt(indexs[i]);
            if (minindex > index2) {
                minindex = index2;
            }
            System.out.println(minindex + "");
        }
        return minindex;
    }
}
