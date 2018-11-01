package com.mall.serving.orderplane;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.model.PlaneCabinModel;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;

public class ChooseCabin extends Activity {
    private TextView plane_info, start_city, landing_city, take_off_time, landing_time, take_off_date, landing_date;
    private ListView listview;
    private String planeinfo, startcity, landingcity, takeofftime, landingtime, takeoffdate, landingdate, city_from, city_to, othercabin, flighno;
    private String FuelTax = "", DepTerm = "", ArrTerm = "", AirConFee = "", AirWays = "", company = "", StopOver = "", shopId = "", FlightMod = "", CarrFlightNo = "";
    private StringBuilder param = new StringBuilder();
    private List<PlaneCabinModel> list = new ArrayList<PlaneCabinModel>();
    private ChooseCabinAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_cabin);
        init();
    }

    private void init() {
        Util.initTitle(this, "选择舱位", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
        getIntentData();
        getData();
    }

    private void initView() {
        plane_info = (TextView) this.findViewById(R.id.plane_info);
        start_city = (TextView) this.findViewById(R.id.start_city);
        landing_city = (TextView) this.findViewById(R.id.landing_city);
        take_off_time = (TextView) this.findViewById(R.id.take_off_time);
        landing_time = (TextView) this.findViewById(R.id.landing_time);
        take_off_date = (TextView) this.findViewById(R.id.take_off_date);
        landing_date = (TextView) this.findViewById(R.id.landing_date);
        listview = (ListView) this.findViewById(R.id.listview);
    }

    private void getIntentData() {
        planeinfo = this.getIntent().getStringExtra("planeinfo");
        startcity = this.getIntent().getStringExtra("startcity");
        landingcity = this.getIntent().getStringExtra("landingcity");
        takeofftime = this.getIntent().getStringExtra("takeofftime");
        landingtime = this.getIntent().getStringExtra("landingtime");
        takeoffdate = this.getIntent().getStringExtra("takeoffdate");
        landingdate = this.getIntent().getStringExtra("landingdate");
        city_from = this.getIntent().getStringExtra("city_from");
        city_to = this.getIntent().getStringExtra("city_to");
        othercabin = this.getIntent().getStringExtra("othercabin");
        flighno = this.getIntent().getStringExtra("flightno");

        FuelTax = this.getIntent().getStringExtra("FuelTax");
        DepTerm = this.getIntent().getStringExtra("DepTerm");
        ArrTerm = this.getIntent().getStringExtra("ArrTerm");
        AirWays = this.getIntent().getStringExtra("AirWays");
        StopOver = this.getIntent().getStringExtra("StopOver");
        company = this.getIntent().getStringExtra("company");
        FlightMod = this.getIntent().getStringExtra("FlightMod");//?
        AirConFee = this.getIntent().getStringExtra("AirConFee");
        param.append(takeoffdate + ",");
        param.append(flighno + ",");
        param.append(othercabin + ",");
        param.append(city_from + ",");
        param.append(city_to + ",");
        param.append(takeofftime + ",");
        param.append(landingtime);

        plane_info.setText(planeinfo);
        start_city.setText(startcity);
        landing_city.setText(landingcity);
        take_off_time.setText(takeofftime);
        landing_time.setText(landingtime);
        take_off_date.setText(takeoffdate);
        landing_date.setText(landingdate);
    }

    private void getData() {
        Util.asynTask(ChooseCabin.this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (runData != null) {
                    String result = (String) runData;
                    PlaneCabinModel pc = (PlaneCabinModel) ChooseCabin.this.getIntent().getSerializableExtra("model");
                    if (pc != null) {
                        list.add(pc);
                    }
                    parsePlaneObject(result);
                    if (list.size() > 0) {
                        if (adapter == null) {
                            adapter = new ChooseCabinAdapter(ChooseCabin.this);
                            listview.setAdapter(adapter);
                        }
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ChooseCabin.this, "未获取到舱位数据", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChooseCabin.this, "未获取到舱位数据", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service, Web.Ticket_getMore, "param=" + param.toString());
                String result = web.getPlan();
                return result;
            }
        });
    }

    private void parsePlaneObject(String result) {
        JSONObject jso;
        try {
            jso = new JSONObject(result);
            String Res = jso.getString("Res");
            String FlightDatas = new JSONObject(Res).getString("FlightDatas");
            JSONArray a = new JSONArray(FlightDatas);
            JSONObject o = a.getJSONObject(0);
            String CabinDatas = o.getString("CabinDatas");
            JSONArray jsonArray = new JSONArray(CabinDatas);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject myjObject = jsonArray.getJSONObject(i);
                String Cabin = myjObject.getString("Cabin");
                String CabName = myjObject.getString("CabName");
                String CabType = myjObject.getString("CabType");
                String IsPubTar = myjObject.getString("IsPubTar");
                String SeatNum = myjObject.getString("SeatNum");
                String Price = myjObject.getString("Price");
                String ADTPrice = myjObject.getString("ADTPrice");
                String ADTTax = myjObject.getString("ADTTax");
                String ADTYq = myjObject.getString("ADTYq");
                String INFPrice = myjObject.getString("INFPrice");
                String INFTax = myjObject.getString("INFTax");
                String Discount = myjObject.getString("Discount");
                String RewRates = myjObject.getString("RewRates");
                String PolicyId = myjObject.getString("PolicyId");
                String Note = myjObject.getString("Note");
                String TPrice = myjObject.getString("TPrice");
                String TDiscount = myjObject.getString("TDiscount");
                String TLaSeatNum = myjObject.getString("TLaSeatNum");
                String TRewRates = myjObject.getString("Discount");
                String PlatOth = myjObject.getString("PlatOth");

                PlaneCabinModel p = new PlaneCabinModel();
                p.setCabin(Cabin);
                p.setCabName(CabName);
                p.setCabType(CabType);
                p.setIsPubTar(IsPubTar);
                p.setSeatNum(SeatNum);
                p.setPrice(Price);
                p.setADTPrice(ADTPrice);
                p.setADTTax(ADTTax);
                p.setADTYq(ADTYq);
                p.setINFPrice(INFPrice);
                p.setINFTax(INFTax);
                p.setDiscount(Discount);
                p.setRewRates(RewRates);
                p.setPolicyId(PolicyId);
                p.setNote(Note);
                p.setTPrice(TPrice);
                p.setTDiscount(TDiscount);
                p.setTLaSeatNum(TLaSeatNum);
                p.setTRewRates(TRewRates);
                p.setPlatOth(PlatOth);
                list.add(p);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ChooseCabinAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<PlaneCabinModel> list = new ArrayList<PlaneCabinModel>();

        public ChooseCabinAdapter(Context c) {
            inflater = LayoutInflater.from(c);
        }

        private void setList(List<PlaneCabinModel> list) {
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
            PlaneCabinModel p = list.get(position);
            ViewHolder h = null;
            if (convertView == null) {
                h = new ViewHolder();
                convertView = inflater.inflate(R.layout.plane_cabin_item, null);
                h.discount = (TextView) convertView.findViewById(R.id.discount);
                h.tips = (TextView) convertView.findViewById(R.id.tips);
                h.reback = (TextView) convertView.findViewById(R.id.reback);
                h.price = (TextView) convertView.findViewById(R.id.price);
                h.tickets_num = (TextView) convertView.findViewById(R.id.tickets_num);
                h.elseinfo = (TextView) convertView.findViewById(R.id.elseinfo);
                h.order = (TextView) convertView.findViewById(R.id.order);
                convertView.setTag(h);
            } else {
                h = (ViewHolder) convertView.getTag();
            }
            h.price.setText("￥" + p.getPrice());
            Double d = Double.parseDouble(p.getDiscount());
            int pr = Integer.parseInt(p.getPrice());
            Double rew = Double.parseDouble(p.getRewRates().replace("%", "")) * 100;
            h.discount.setText(p.getCabName() + "" + Util.getDouble(d * 10, 2) + "折");
            if (!rew.equals("0")) {
                h.reback.setText("返" + Util.getDouble((pr * rew) / 10000, 2) + "");
            } else {
                h.reback.setVisibility(View.GONE);
            }
            h.reback.setVisibility(View.GONE);
            if (p.getSeatNum().equals("A")) {
                h.tickets_num.setText("舱位大于9");
            } else {
                h.tickets_num.setText("舱位" + p.getSeatNum());
            }
            final PlaneCabinModel pp = p;
            h.order.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChooseCabin.this, WritePlaneOrder.class);
                    intent.putExtra("takeoffairport", startcity);
                    intent.putExtra("landingairport", landingcity);
                    intent.putExtra("city_from", city_from);
                    intent.putExtra("city_to", city_to);
                    intent.putExtra("takeofftime", takeofftime);
                    intent.putExtra("landingtime", landingtime);
                    intent.putExtra("startdate", takeoffdate);
                    intent.putExtra("enddate", landingdate);
                    intent.putExtra("tickettype", "成人票|" + pp.getCabName());
                    intent.putExtra("totalprice", "￥" + pp.getPrice());
                    intent.putExtra("itemprice", "￥" + pp.getPrice());
                    intent.putExtra("note", pp.getNote());
                    intent.putExtra("PlatOth", pp.getPlatOth());
                    intent.putExtra("PolicyId", pp.getPolicyId());
                    intent.putExtra("FuelTax", FuelTax);//燃油
                    intent.putExtra("DepTerm", DepTerm);
                    intent.putExtra("ArrTerm", ArrTerm);
                    intent.putExtra("flightno", flighno);
                    intent.putExtra("CabName", pp.getCabName());
                    intent.putExtra("Discount", pp.getDiscount());
                    intent.putExtra("INFPrice", pp.getINFPrice());
                    intent.putExtra("AirConFee", AirConFee);//机场建设
                    intent.putExtra("isStop", StopOver);
                    intent.putExtra("SeatNum", pp.getSeatNum());
                    intent.putExtra("company", company);
                    intent.putExtra("Cabin", pp.getCabin());
                    intent.putExtra("AirWays", AirWays);
                    if (Util.isNull(pp.getPolicyId())) {
                        Toast.makeText(ChooseCabin.this, "对不起，该航班暂时不能预订，请选择其他航班", Toast.LENGTH_LONG).show();
                        return;
                    }
                    String param = "";
                    param += startcity + "_" + landingcity + "_" + CarrFlightNo + "_" + flighno + "_ _"
                            + takeoffdate + " " + takeofftime + "_" + landingtime + "_"
                            + FlightMod + "_" + AirConFee + "_ _";
                    param += StopOver + "_ _" + FuelTax + "_" + "无" + "_" + pp.getCabName() + "_"
                            + pp.getPrice() + "_" + startcity + DepTerm + "_" + landingcity + "_" + pp.getCabName()
                            + "_" + pp.getDiscount() + "_";
                    param += pp.getSeatNum() + "_" + company + "_" + pp.getINFPrice() + "_" + shopId + "_"
                            + pp.getPolicyId() + "_" + pp.getPlatOth() + "_" + city_from + "_" + city_to
                            + "_" + pp.getCabin() + "_" + DepTerm + "_" + ArrTerm + "_" + AirWays + "_" + pp.getPrice() + "";
                    intent.putExtra("param", param);
                    ChooseCabin.this.startActivity(intent);
                }
            });
            return convertView;
        }
    }

    public class ViewHolder {
        TextView discount;
        TextView tips;
        TextView reback;
        TextView price;
        TextView tickets_num;
        TextView elseinfo;
        TextView order;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ChooseCabin.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
