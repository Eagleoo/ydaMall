package com.mall.serving.orderplane;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.PlanListModel;
import com.mall.model.PlaneCabinModel;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;

public class PlaneListTwoWay extends Activity {
	
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	
	
	
	
	
	
	private String tile = "成都---北京";
	String[] week = new String[] { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
	private List<PlanListModel> startList = new ArrayList<PlanListModel>();
	private List<PlanListModel> backList = new ArrayList<PlanListModel>();
	private List<PlaneCabinModel> morecabinlist = new ArrayList<PlaneCabinModel>();
	private String city_from = "", city_to = "", city_from_name = "",
			city_to_name = "", time = "", date = "2014-06-25", backtime = "",
			backdate = "", takeofftime = "", landingtime = "", tickettype = "",
			note = "", flightno = "", planemode = "", AirConFee = "",
			FuelTax = "", backdatetwo = "", takeofftimetwo = "",
			landingtimetwo = "", tickettypetwo = "", flightnotwo = "",
			planemodetwo = "", AirConFeetwo = "", FuelTaxtwo = "";  
	private PlaneListAdapter adapter;
	private PlaneListAdapter backadapter;
	private TextView take_off_date, back_date, 
			city_to_city_two, plane_start_info, plane_back_info, total_price;
	private ListView start_listview, back_listview;
	private Button submit;
	private double totalprice = 0.00, startprice = 0.00, backprice = 0.00;
	String param = "", startparam = "", endpram = "";
	private String startcompany, backcompany;
	private String cangwei;
	@ViewInject(R.id.start_list)
	private TextView start_list;
	@ViewInject(R.id.back_list)
	private TextView back_list;
	@ViewInject(R.id.start_list_layout)
	private LinearLayout start_list_layout;
	@ViewInject(R.id.back_list_layout)
	private LinearLayout back_list_layout;
	private boolean isback=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plane_list_twoway);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		initView();
		getIntentData();
		getPlaneData(startList, "startlist", date,city_from,city_to);
	}
	@OnClick({R.id.fi1, R.id.fi2 ,R.id.start_list,R.id.back_list})
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.fi1:
			start_list.setTextColor(getResources().getColor(R.color.headertop));
			start_list.setBackgroundColor(getResources().getColor(R.color.bg));
			start_list_layout.setVisibility(View.VISIBLE);
			
			back_list.setBackgroundColor(getResources().getColor(R.color.transparent));
			back_list.setTextColor(getResources().getColor(R.color.bg));
			back_list_layout.setVisibility(View.GONE);
			break;
		case R.id.fi2:
			//双程
			back_list.setTextColor(getResources().getColor(R.color.headertop));
			back_list.setBackgroundColor(getResources().getColor(R.color.bg));
			back_list_layout.setVisibility(View.VISIBLE);
			
			start_list.setBackgroundColor(getResources().getColor(R.color.transparent));
			start_list.setTextColor(getResources().getColor(R.color.bg));
			start_list_layout.setVisibility(View.GONE);
			break;
		case R.id.back_list:
			isback=true;
			if (backList.size() == 0) {
				getPlaneData(backList, "backlist", backtime,city_to,city_from);
			} else {
				backadapter.setListType("backlist");
			}
			back_list.setTextColor(getResources().getColor(R.color.headertop));
			back_list.setBackgroundColor(getResources().getColor(R.color.bg));
			back_list_layout.setVisibility(View.VISIBLE);
			
			start_list.setBackgroundColor(getResources().getColor(R.color.transparent));
			start_list.setTextColor(getResources().getColor(R.color.bg));
			start_list_layout.setVisibility(View.GONE);
			start_listview.setVisibility(View.GONE);
			back_listview.setVisibility(View.VISIBLE);
			break;
		case R.id.start_list:
			isback=false;
			if (startList.size() == 0) {
				getPlaneData(startList, "startlist", time,city_from,city_to);
			} else {
				adapter.setListType("startlist");
			}
			start_list.setTextColor(getResources().getColor(R.color.headertop));
			start_list.setBackgroundColor(getResources().getColor(R.color.bg));
			start_list_layout.setVisibility(View.VISIBLE);
			
			back_list.setBackgroundColor(getResources().getColor(R.color.transparent));
			back_list.setTextColor(getResources().getColor(R.color.bg));
			back_list_layout.setVisibility(View.GONE);
			
			start_listview.setVisibility(View.VISIBLE);
			back_listview.setVisibility(View.GONE);
			break;
		}
	}
	private void getIntentData() {
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(System.currentTimeMillis());
		city_from = this.getIntent().getStringExtra("fromcity");// CTu
		city_from_name = this.getIntent().getStringExtra("fromcityname");// 成都
		city_to = this.getIntent().getStringExtra("cityto");// PEK
		city_to_name = this.getIntent().getStringExtra("citytoname");// 北京
		date = this.getIntent().getStringExtra("time");
		time = this.getIntent().getStringExtra("date");
		backtime = this.getIntent().getStringExtra("backtime");// 6-25
		backdate = this.getIntent().getStringExtra("backdate");// 6.25 周二
		cangwei=this.getIntent().getStringExtra("cangwei");
		tile = city_from_name + "-" + city_to_name;
		tile = city_from_name + "-" + city_to_name;
//		Util.initTop(PlaneListTwoWay.this, tile, 0, new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Util.showIntent(PlaneListTwoWay.this, OrderPlaneIndex.class);
//			}
//		}, null);
		
		top_left.setVisibility(View.VISIBLE);
		top_center.setText(tile);
		
		city_to_city_two.setText(tile);
		take_off_date.setText(ca.get(Calendar.YEAR) + "年"
				+ time.replace(" ", ""));
		back_date.setText(ca.get(Calendar.YEAR) + "年"
				+ backdate.replace(" ", ""));
	}

	private void initView() {
		take_off_date = (TextView) this.findViewById(R.id.take_off_date);
		back_date = (TextView) this.findViewById(R.id.back_date);
		start_list = (TextView) this.findViewById(R.id.start_list);
		back_list = (TextView) this.findViewById(R.id.back_list);
		plane_back_info = (TextView) this.findViewById(R.id.plane_back_info);
		plane_start_info = (TextView) this.findViewById(R.id.plane_start_info);
		total_price = (TextView) this.findViewById(R.id.total_price);
		submit = (Button) this.findViewById(R.id.submit);
		start_listview = (ListView) this.findViewById(R.id.start_listview);
		back_listview = (ListView) this.findViewById(R.id.back_listview);
		city_to_city_two = (TextView) this.findViewById(R.id.city_to_city_two);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!Util.isNull(plane_back_info.getText().toString())) {
//					if (!startcompany.trim().equals(backcompany.trim())) {
//						Util.show("请选择相同的航空公司", PlaneListTwoWay.this);
//						return;
//					} else {
						if (Util.isNull(endpram)) {
							param = startparam;
						} else {
							param = startparam + "|" + endpram;
						}
						if (Util.isNull(param)) {
							Toast.makeText(PlaneListTwoWay.this, "请选择航班",
									Toast.LENGTH_LONG).show();
							return;
						}
						if (!Util.isNull(plane_back_info.getText().toString())) {// 双程
							System.out.println("这是双程");
							Intent intent = new Intent(PlaneListTwoWay.this,
									WritePlaneOrder.class);
							intent.putExtra("param", param);
							// 去程
							intent.putExtra("takeoffairport", city_from_name);
							intent.putExtra("landingairport", city_to_name);
							intent.putExtra("takeofftime", takeofftime);
							intent.putExtra("landingtime", landingtime);
							intent.putExtra("startdate", take_off_date
									.getText().toString());
							intent.putExtra("enddate", backdate);
							intent.putExtra("tickettype", tickettype);
							intent.putExtra("totalprice", "￥" + startprice + "");
							intent.putExtra("note", note);
							intent.putExtra("planemode", planemode);
							intent.putExtra("flightno", flightno);
							intent.putExtra("city_from", city_from);
							intent.putExtra("city_to", city_to);
							intent.putExtra("AirConFee", AirConFee);
							intent.putExtra("FuelTax", FuelTax);
							// 返程
							intent.putExtra("takeofftimetwo", takeofftimetwo);
							intent.putExtra("landingtimetwo", landingtimetwo);
							intent.putExtra("startdatetwo", back_date.getText()
									.toString());
							intent.putExtra("enddatetwo", back_date.getText()
									.toString());
							intent.putExtra("tickettypetwo", tickettypetwo);
							intent.putExtra("totalpricetwo", "￥" + backprice
									+ "");
							intent.putExtra("planemodetwo", planemodetwo);
							intent.putExtra("flightnotwo", flightnotwo);
							intent.putExtra("AirConFeetwo", AirConFeetwo);
							intent.putExtra("FuelTaxtwo", FuelTaxtwo);
							intent.putExtra("planetype", "双程");
							PlaneListTwoWay.this.startActivity(intent);
						} else {// 单程
							System.out.println("单程====");
							Intent intent = new Intent(PlaneListTwoWay.this,
									WritePlaneOrder.class);
							intent.putExtra("param", param);
							// 去程
							intent.putExtra("takeoffairport", city_from_name);
							intent.putExtra("landingairport", city_to_name);
							intent.putExtra("takeofftime", takeofftime);
							intent.putExtra("landingtime", landingtime);
							intent.putExtra("startdate", take_off_date
									.getText().toString());
							intent.putExtra("enddate", take_off_date.getText()
									.toString());
							intent.putExtra("tickettype", tickettype);
							intent.putExtra("totalprice", "￥" + startprice + "");
							intent.putExtra("note", note);
							intent.putExtra("planemode", planemode);
							intent.putExtra("flightno", flightno);
							intent.putExtra("city_from", city_from);
							intent.putExtra("city_to", city_to);
							intent.putExtra("AirConFee", AirConFee);
							intent.putExtra("FuelTax", FuelTax);
							intent.putExtra("planetype", "单程");
							PlaneListTwoWay.this.startActivity(intent);
						}

					}
//				}

			}
		});
	}


	public String getFirName(String airWays) {
		String company = "";
		if (airWays.contains("MF")) {
			company = "厦门航空";
		} else if (airWays.contains("HU")) {
			company = "海南航空";
		} else if (airWays.contains("3U")) {
			company = "四川航空";
		} else if (airWays.contains("MU")) {
			company = "东方航空";
		} else if (airWays.contains("CZ")) {
			company = "南方航空";
		} else if (airWays.contains("CA")) {
			company = "国际航空";
		} else if (airWays.contains("ZH")) {
			company = "深圳航空";
		} else if (airWays.contains("HO")) {
			company = "吉祥航空";
		} else if (airWays.contains("SC")) {
			company = "山东航空";
		} else if (airWays.contains("G5")) {
			company = "华夏航空";
		} else if (airWays.contains("GS")) {
			company = "大新华快运";
		} else if (airWays.contains("FM")) {
			company = "上海航空";
		} else if (airWays.contains("F6")) {
			company = "浙江航空";
		} else if (airWays.contains("SZ")) {
			company = "西南航空";
		} else if (airWays.contains("Z2")) {
			company = "中原航空";
		} else if (airWays.contains("EU")) {
			company = "鹰联航空";
		} else if (airWays.contains("CJ")) {
			company = "北方航空";
		} else if (airWays.contains("3Q")) {
			company = "云南航空";
		} else if (airWays.contains("5J")) {
			company = "宿务航空";
		} else if (airWays.contains("WH")) {
			company = "西北航空";
		} else if (airWays.contains("IV")) {
			company = "福建航空";
		} else if (airWays.contains("WU")) {
			company = "武汉航空";
		} else if (airWays.contains("XW")) {
			company = "新华航空";
		} else if (airWays.contains("2Z")) {
			company = "长安航空";
		} else if (airWays.contains("8C")) {
			company = "山西航空";
		} else if (airWays.contains("BR")) {
			company = "长荣航空";
		} else if (airWays.contains("KN")) {
			company = "联航航空";
		} else if (airWays.contains("JD")) {
			company = "首都航空";
		} else if (airWays.contains("PN")) {
			company = "西部航空";
		} else if (airWays.contains("TV")) {
			company = "西藏航空";
		}
		return company;
	}
	
	private void getPlaneData(final List<PlanListModel> list,
			final String listtype, final String date,final String city_from,final String city_to) {
		Util.asynTask(PlaneListTwoWay.this, "正在获取航班信息", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				String result = (String) runData;
				parseBrandObject(list, listtype, result);
				if (listtype.equals("startlist")) {
					if (adapter == null) {
						adapter = new PlaneListAdapter(PlaneListTwoWay.this);
					}
					adapter.setListType(listtype);
					adapter.setLiset(list);
					start_listview.setAdapter(adapter);
				} else if (listtype.equals("backlist")) {
					if (backadapter == null) {
						backadapter = new PlaneListAdapter(PlaneListTwoWay.this);
					}
					backadapter.clear();
					backadapter.setListType(listtype);
					backadapter.setLiset(list);
					back_listview.setAdapter(backadapter);
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service, Web.Ticket_getTicket,
						"city_from=" + city_from + "&city_to=" + city_to
								+ "&date=" + date+"&cangwei="+cangwei+"&pai=");
				String result = web.getPlan();
				return result;
			}
		});
	}

	private void parseBrandObject(List<PlanListModel> list, String listtyp,
			String result) {
		JSONObject jso;
		try {
			jso = new JSONObject(result);
			String Res = jso.getString("Res");
			String FlightDatas = new JSONObject(Res).getString("FlightDatas");
			JSONArray jsonArray;
			jsonArray = new JSONArray(FlightDatas);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject myjObject = jsonArray.getJSONObject(i);
				String DepDate = myjObject.getString("DepDate");
				String DepCity = myjObject.getString("DepCity");
				String ArrCity = myjObject.getString("ArrCity");
				String DepTime = myjObject.getString("DepTime");
				String ArrTime = myjObject.getString("ArrTime");
				String FlightNo = myjObject.getString("FlightNo");
				String CarrFlightNo = myjObject.getString("CarrFlightNo");
				String Airways = myjObject.getString("Airways");
				String FlightMod = myjObject.getString("FlightMod");
				String FlyTime = myjObject.getString("FlyTime");
				String StopOver = myjObject.getString("StopOver");
				String AirConFee = myjObject.getString("AirConFee");
				String FuelTax = myjObject.getString("FuelTax");
				String DepTerm = myjObject.getString("DepTerm");
				String ArrTerm = myjObject.getString("ArrTerm");
				String ProtNum = myjObject.getString("ProtNum");
				String OtherCabin = myjObject.getString("OtherCabin");
				String CabinDatas = myjObject.getString("CabinDatas");
				String Cabin = "", CabName = "", CabType = "", Price = "", Discount = "", PolicyId = "", Note = "", PlatOth = "", HkCashBack = "", TCashBack = "", CashBack = "", PStayMoney = "", SeatNum = "";
				JSONArray array = new JSONArray(CabinDatas);
				if (0 == array.length())
					continue;
				for (int k = 0; k < array.length(); k++) {
					JSONObject jo = array.getJSONObject(k);
					Cabin = jo.getString("Cabin");
					CabName = jo.getString("CabName");
					CabType = jo.getString("CabType");
					Price = jo.getString("Price");
					Discount = jo.getString("Discount");
					PolicyId = jo.getString("PolicyId");
					Note = jo.getString("Note");
					PlatOth = jo.getString("PlatOth");
					HkCashBack = jo.getString("HkCashBack");
					TCashBack = jo.getString("TCashBack");
					CashBack = jo.getString("CashBack");
					PStayMoney = jo.getString("PStayMoney");
					SeatNum = jo.getString("SeatNum");
				}
				PlanListModel p = new PlanListModel();
				p.setDepDate(DepDate);
				p.setDepCity(DepCity);
				p.setArriveCity(ArrCity);
				p.setDepTerm(DepTerm);
				p.setDepTime(DepTime);
				p.setArriveTime(ArrTime);
				p.setFlightNo(FlightNo);
				p.setCarrFlightNo(CarrFlightNo);
				p.setAirWays(Airways);
				p.setFlightMod(FlightMod);
				p.setFlyTime(FlyTime);
				p.setStopOver(StopOver);
				p.setAirConFee(AirConFee);
				p.setFuelTax(FuelTax);
				p.setDepTerm(DepTerm);
				p.setArriveTerm(ArrTerm);
				p.setProtNum(ProtNum);
				p.setOtherCabin(OtherCabin);
				p.setCabin(Cabin);
				p.setCabName(CabName);
				p.setCabType(CabType);
				p.setPrice(Price);
				p.setDiscount(Discount);
				p.setPolicyId(PolicyId);
				p.setNote(Note);
				p.setPlatOth(PlatOth);
				p.setHkCashBack(HkCashBack);
				p.setTCashBack(TCashBack);
				p.setCashBack(CashBack);
				p.setPStayMoney(PStayMoney);
				p.setSeatNum(SeatNum);
				list.add(p);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public class PlaneListAdapter extends BaseAdapter {
		private Context c;
		private LayoutInflater inflater;
		private List<PlanListModel> list = new ArrayList<PlanListModel>();
		private String listtype = "";

		public PlaneListAdapter(Context c) {
			this.c = c;
			inflater = LayoutInflater.from(c);
		}

		private void clear() {
			this.list.clear();
			this.notifyDataSetChanged();
		}

		private void setListType(String listType) {
			this.listtype = listType;
		}

		private void setLiset(List<PlanListModel> list) {
			this.list=list;
			for(int i=0;i<this.list.size();i++){
				
			}
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
			PlanListModel p = list.get(position);
			ViewHolder h = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.plane_list_item_two,null);
				h = new ViewHolder();
				h.take_off_airport = (TextView) convertView
						.findViewById(R.id.take_off_airport);
				h.take_off_time = (TextView) convertView
						.findViewById(R.id.take_off_time);
				h.landing_time = (TextView) convertView
						.findViewById(R.id.landing_time);
				h.landing_airport = (TextView) convertView
						.findViewById(R.id.landing_airport);
				h.price = (TextView) convertView
						.findViewById(R.id.ticket_price);
				h.discount = (TextView) convertView
						.findViewById(R.id.dicount_info);
				h.cabingNum = (TextView) convertView
						.findViewById(R.id.tickets_number);
				h.planeType = (TextView) convertView
						.findViewById(R.id.plane_type);
				h.logo = (ImageView) convertView
						.findViewById(R.id.plane_company_logo);
				h.more = (TextView) convertView.findViewById(R.id.more_cabin);
				convertView.setTag(h);
			} else {
				h = (ViewHolder) convertView.getTag();
			}
			if(!isback){
				h.take_off_airport.setText(city_from_name);
				h.landing_airport.setText(city_to_name);
			}else{
				h.landing_airport.setText(city_from_name);
				h.take_off_airport.setText(city_to_name);
			}
			
			h.take_off_time.setText(p.getDepTime());
			h.landing_time.setText(p.getArriveTime());
			h.price.setText("￥" + p.getPrice() + "元");
			Double d = Double.parseDouble(p.getDiscount());
			h.discount.setText(d * 100 + "%");
			String s = "";
			String company = "";
			if (p.getFlightNo().contains("MF")) {
				s = "厦门航空" + p.getFlightNo();
				company = "厦门航空";
				h.logo.setImageResource(R.drawable.plane_xiamen);
			} else if (p.getFlightNo().contains("HU")) {
				s = "海南航空" + p.getFlightNo();
				company = "海南航空";
				h.logo.setImageResource(R.drawable.hu);
			} else if (p.getFlightNo().contains("3U")) {
				s = "四川航空" + p.getFlightNo();
				h.logo.setImageResource(R.drawable.plane_sichuan);
				company = "四川航空";
			} else if (p.getFlightNo().contains("MU")) {
				s = "东方航空" + p.getFlightNo();
				h.logo.setImageResource(R.drawable.plane_shanghai);
				company = "东方航空";
			} else if (p.getFlightNo().contains("CZ")) {
				s = "南方航空" + p.getFlightNo();
				h.logo.setImageResource(R.drawable.plane_nanfang);
				company = "南方航空";
			} else if (p.getFlightNo().contains("CA")) {
				s = "国际航空" + p.getFlightNo();
				h.logo.setImageResource(R.drawable.plane_china);
				company = "国际航空";
			} else if (p.getFlightNo().contains("ZH")) {
				s = "深圳航空" + p.getFlightNo();
				h.logo.setImageResource(R.drawable.plane_shenzheng);
				company = "深圳航空";
			} else if (p.getFlightNo().contains("HO")) {
				s = "吉祥航空" + p.getFlightNo();
				h.logo.setImageResource(R.drawable.plane_jixiang);
				company = "吉祥航空";
			}else{
				company = getFirName(p.getAirWays());
			}
			h.planeType.setText(s);
			if (p.getSeatNum().equals("A")) {
				h.cabingNum.setText("舱位:>9");
			} else {
				h.cabingNum.setText("舱位:" + p.getSeatNum());
			}
			final String planeinfo = s;
			final PlanListModel pp = p;
			final String com = company;
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listtype.equals("startlist")) {
						if (Util.isNull(pp.getPolicyId())) {
							Toast.makeText(PlaneListTwoWay.this,"该航班暂不能预订", Toast.LENGTH_LONG).show();
							return;
						}
						if(!Util.isNull(plane_start_info.getText().toString())){
							totalprice-=startprice;
						}
						totalprice += Util.getDouble(Double.parseDouble(pp.getPrice()), 2);
						startprice = Util.getDouble(
								Double.parseDouble(pp.getPrice()), 2);
						startcompany = "";
						startcompany = com;
						System.out.println("去程航空公司=====" + startcompany);
						plane_start_info.setText(pp.getDepTime() + "  "
								+ pp.getFlightNo() + com + "  ￥"
								+ pp.getPrice() + "折扣(" + pp.getDiscount()
								+ ")");
						takeofftime = pp.getDepTime();
						landingtime = pp.getArriveTime();
						tickettype = "成人|" + pp.getCabName();
						startparam = "";
						backdate = Util
								.DateComputeAddOneDay(takeofftime, landingtime,
										take_off_date.getText().toString());
						note = pp.getNote();
						flightno = pp.getFlightNo();
						FuelTax = pp.getFuelTax();
						AirConFee = pp.getAirConFee();
						planemode = pp.getFlightMod();
						startparam += city_from_name + "_" + city_to_name + "_"+pp.getCarrFlightNo()+"_"
								+ pp.getFlightNo() + "_ _" + pp.getDepDate()
								+ " " + pp.getDepTime() + "_"
								+ pp.getArriveTime() + "_" + pp.getFlightMod()
								+ "_" + pp.getAirConFee() + "_ _"
								+ pp.getStopOver() + "_ _" + pp.getFuelTax()
								+ "_" + "无" + "_" + pp.getCabName() + "_"
								+ pp.getPrice() + "_" + city_from_name + "("
								+ pp.getDepTerm() + ")" + "_" + city_to_name
								+ "_" + pp.getCabName() + "_"
								+ pp.getDiscount() + "_" + pp.getSeatNum()
								+ "_" + com + "___" + pp.getPolicyId() + "_"
								+ pp.getPlatOth() + "_" + city_from + "_"
								+ city_to + "_" + pp.getCabin() + "_"
								+ pp.getDepTerm() + "_" + pp.getArriveTerm()
								+ "_" + pp.getAirWays();
					} else if (listtype.equals("backlist")) {
						if (Util.isNull(pp.getPolicyId())) {
							Toast.makeText(PlaneListTwoWay.this,"缺少政策ID，该航班暂不能预定", Toast.LENGTH_LONG).show();
							return;
						}
						if(!Util.isNull(plane_back_info.getText().toString())){
							totalprice-=backprice;
						}
						totalprice += Util.getDouble(Double.parseDouble(pp.getPrice()), 2);
						backprice = Util.getDouble(Double.parseDouble(pp.getPrice()), 2);
						backcompany = "";
						backcompany = com;
						System.out.println("返程航空公司=====" + startcompany);
						takeofftimetwo = pp.getDepTime();
						landingtimetwo = pp.getArriveTime();
						backdatetwo = Util.DateComputeAddOneDay(takeofftimetwo,landingtimetwo, back_date.getText().toString());
						System.out.println("backdatetwo====" + backdatetwo);
						tickettypetwo = pp.getCabName();
						flightnotwo = pp.getFlightNo();
						FuelTaxtwo = pp.getFuelTax();
						AirConFeetwo = pp.getAirConFee();
						planemodetwo = pp.getFlightMod();
						plane_back_info.setText(pp.getDepTime() + "  "
								+ pp.getFlightNo() + com + "  ￥"
								+ pp.getPrice() + "折扣(" + pp.getDiscount()
								+ ")");
						endpram = "";
						endpram += city_to_name + "_" + city_from_name + "_"
								+ pp.getCarrFlightNo() + "_" + pp.getFlightNo()
								+ "_ _" + pp.getDepDate() + " "
								+ pp.getDepTime() + "_" + pp.getArriveTime()
								+ "_" + pp.getFlightMod() + "_"
								+ pp.getAirConFee() + "_ _" + pp.getStopOver()
								+ "_ _" + pp.getFuelTax() + "_" + "无" + "_"
								+ pp.getCabName() + "_" + pp.getPrice() + "_"
								+ city_to_name + "(" + pp.getDepTerm() + ")"
								+ "_" + city_from_name + "_" + pp.getCabName()
								+ "_" + pp.getDiscount() + "_"
								+ pp.getSeatNum() + "_" + com + "" + "___"
								+ pp.getPolicyId() + "_" + pp.getPlatOth()
								+ "_" + city_to + "_" + city_from + "_"
								+ pp.getCabin() + "_" + pp.getDepTerm() + "_"
								+ pp.getArriveTerm() + "_" + pp.getAirWays();
					}
					total_price.setText("￥" + totalprice + " 元");
				}
			});
			h.more.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final StringBuilder moreCabinParam = new StringBuilder();
					moreCabinParam.append(pp.getDepDate() + ",");
					moreCabinParam.append(pp.getFlightNo() + ",");
					moreCabinParam.append(pp.getOtherCabin() + ",");
					moreCabinParam.append(city_from + ",");
					moreCabinParam.append(city_to + ",");
					moreCabinParam.append(pp.getDepTime() + ",");
					moreCabinParam.append(pp.getArriveTime());
					AlertDialog.Builder builder = new AlertDialog.Builder(PlaneListTwoWay.this);
					LayoutInflater inflater = LayoutInflater.from(PlaneListTwoWay.this);
					View view = inflater.inflate(R.layout.plane_cabin_list_two,null);
					final ListView listv = (ListView) view.findViewById(R.id.more_cabin_list);
					Util.asynTask(PlaneListTwoWay.this, "", new IAsynTask() {
						@Override
						public void updateUI(Serializable runData) {
							ChooseCabinAdapter cabinAdapter = null;
							String result = (String) runData;
							parsePlaneObject(result);
							if (adapter == null) {
								cabinAdapter = new ChooseCabinAdapter(
										PlaneListTwoWay.this, listtype);
							}
							cabinAdapter.setList(morecabinlist);
							listv.setAdapter(adapter);
						}
						@Override
						public Serializable run() {
							Web web = new Web(Web.convience_service,Web.Ticket_getMore, "param="+ Util.get(moreCabinParam.toString()));
							String result = web.getPlan();
							return result;
						}
					});
					builder.setCancelable(true);
					builder.setView(view);
				}
			});
			return convertView;
		}
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
				morecabinlist.add(p);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public class ViewHolder {
		TextView take_off_time;
		TextView landing_time;
		TextView take_off_airport;
		TextView landing_airport;
		TextView price;
		TextView discount;
		TextView cabingNum;
		TextView planeType;
		ImageView logo;
		TextView more;
	}

	public class ChooseCabinAdapter extends BaseAdapter {
		private Context c;
		private LayoutInflater inflater;
		private List<PlaneCabinModel> list = new ArrayList<PlaneCabinModel>();
		private String listtype = "";

		public ChooseCabinAdapter(Context c, String listType) {
			this.c = c;
			this.listtype = listType;
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
			ViewCabinHolder h = null;
			if (convertView == null) {
				h = new ViewCabinHolder();
				convertView = inflater.inflate(R.layout.plane_cabin_item, null);
				h.discount = (TextView) convertView.findViewById(R.id.discount);
				h.tips = (TextView) convertView.findViewById(R.id.tips);
				h.reback = (TextView) convertView.findViewById(R.id.reback);
				h.price = (TextView) convertView.findViewById(R.id.price);
				h.tickets_num = (TextView) convertView
						.findViewById(R.id.tickets_num);
				h.elseinfo = (TextView) convertView.findViewById(R.id.elseinfo);
				h.order = (TextView) convertView.findViewById(R.id.order);
				convertView.setTag(h);
			} else {
				h = (ViewCabinHolder) convertView.getTag();
			}
			h.price.setText("￥" + p.getPrice());
			Double d = Double.parseDouble(p.getDiscount());
			int pr = Integer.parseInt(p.getPrice());
			Double rew = Double.parseDouble(p.getRewRates().replace("%", "")) * 100;
			h.discount.setText(p.getCabName() + "" + (d * 10) + "折");
			if (!rew.equals("0")) {
				h.reback.setText("返" + Util.getDouble((pr * rew) / 10000, 2)
						+ "");
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
					if (Util.isNull(pp.getPolicyId())) {
						Toast.makeText(PlaneListTwoWay.this,
								"对不起，该航班暂时不能预订，请选择其他航班", Toast.LENGTH_LONG)
								.show();
						return;
					}
				}
			});
			// 更多舱位
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});
			return convertView;
		}
	}

	public class ViewCabinHolder {
		TextView discount;
		TextView tips;
		TextView reback;
		TextView price;
		TextView tickets_num;
		TextView elseinfo;
		TextView order;
	}
}
