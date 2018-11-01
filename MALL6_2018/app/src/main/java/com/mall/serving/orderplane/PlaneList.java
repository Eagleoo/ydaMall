package com.mall.serving.orderplane;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.PlanListModel;
import com.mall.model.PlaneCabinModel;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;

public class PlaneList extends BaseActivity {

	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.previous_day_low_price)
	private TextView previous_day_low_price;
	@ViewInject(R.id.next_day_low_price)
	private TextView next_day_low_price;
	@ViewInject(R.id.show_more_time)
	private LinearLayout show_more_time;

	private String tile = "成都---北京";
	String[] week = new String[] { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
	private ListView listview;
	private List<PlanListModel> plane_list = new ArrayList<PlanListModel>();
	private String city_from = "", city_to = "", date = "2014-06-25", city_from_name = "", city_to_name = "";
	private int lowPrice = 0;
	private PlaneListAdapter adapter;
	private TextView totay, lowPriceT, privious_day, next_day;
	private String year, month, day;
	private String dayofweek;
	private ImageView voice_search, sort_by_price, sort_by_time, saixuan;
	private String cangwei = "", pai = "1";
	private Calendar cal2cal;
	private int yeart, montht, dayt;
	private String namesstring = "", timstring = "", zfliststring = "", cangweistring = "";
	private String take_off_date;
	private ImageView sort_by_time_focus;
	private ImageView sort_by_price_focus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plane_list);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		initView();
		getIntentData();
		getPlaneData(date);
	}





	private void getIntentData() {
		city_from = this.getIntent().getStringExtra("fromcity");
		city_from_name = this.getIntent().getStringExtra("fromcityname");
		city_to = this.getIntent().getStringExtra("cityto");
		city_to_name = this.getIntent().getStringExtra("citytoname");
		date = this.getIntent().getStringExtra("time");
		tile = city_from_name + "-" + city_to_name;
		cangwei = this.getIntent().getStringExtra("cangwei");

		top_left.setVisibility(View.VISIBLE);
		top_center.setText(tile);

		year = this.getIntent().getStringExtra("year");
		month = this.getIntent().getStringExtra("month");
		day = this.getIntent().getStringExtra("day");
		dayofweek = this.getIntent().getStringExtra("dayofweek");

		cal2cal = Calendar.getInstance();
		cal2cal.set(Calendar.YEAR, Integer.parseInt(year));
		cal2cal.set(Calendar.MONTH, Integer.parseInt(month));
		cal2cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));

		yeart = Integer.parseInt(year);
		montht = Integer.parseInt(month) - 1;
		dayt = Integer.parseInt(day);

		String currentmonht = Util.getCurrentMonth();
		String currentday = Util.getCurrDay();
		if (currentmonht.equals(month) && currentday.equals(day)) {
			privious_day.setText("");
		} else {
			privious_day.setText("前一天");
		}
		next_day.setText("后一天 ");
		totay.setText(month + "-" + day + " " + dayofweek);
	}

	private void initView() {
		listview = (ListView) this.findViewById(R.id.listview);

		totay = (TextView) this.findViewById(R.id.totay);
		lowPriceT = (TextView) this.findViewById(R.id.lowPrice);
		privious_day = (TextView) this.findViewById(R.id.previous_day);
		next_day = (TextView) this.findViewById(R.id.next_day);
		voice_search = (ImageView) this.findViewById(R.id.voice_search);
		sort_by_price = (ImageView) this.findViewById(R.id.sort_by_price);
		sort_by_time = (ImageView) this.findViewById(R.id.sort_by_time);
		sort_by_time_focus = (ImageView) this.findViewById(R.id.sort_by_time_focus);
		sort_by_price_focus = (ImageView) this.findViewById(R.id.sort_by_price_focus);
		saixuan = (ImageView) this.findViewById(R.id.saixuan);
		saixuan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PlaneList.this, PlaneListSaiXuan.class);
				PlaneList.this.startActivityForResult(intent, 300);
			}
		});
		voice_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				voice_search.setImageResource(R.drawable.plane_voice_focus);
				sort_by_price.setImageResource(R.drawable.plane_price);
				sort_by_time.setImageResource(R.drawable.plane_time);
			}
		});
		sort_by_price.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//				sort_by_price.setImageResource(R.drawable.plane_price_focus);
				//				voice_search.setImageResource(R.drawable.plane_voice);
				//				sort_by_time.setImageResource(R.drawable.plane_time);
				sort_by_price_focus.setVisibility(View.VISIBLE);
				sort_by_price.setVisibility(View.GONE);
				sort_by_time.setVisibility(View.VISIBLE);
				sort_by_time_focus.setVisibility(View.GONE);
				pai = "1";
				getPlaneData(date);
			}
		});
		sort_by_time.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//				sort_by_time.setImageResource(R.drawable.plane_time_focus);
				//				sort_by_price.setImageResource(R.drawable.plane_price);
				//				voice_search.setImageResource(R.drawable.plane_voice);
				sort_by_price_focus.setVisibility(View.GONE);
				sort_by_price.setVisibility(View.VISIBLE);
				sort_by_time.setVisibility(View.GONE);
				sort_by_time_focus.setVisibility(View.VISIBLE);
				pai = "2";
				getPlaneData(date);
			}
		});
		next_day.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar cal_next = Calendar.getInstance();
				cal_next.set(Calendar.YEAR, yeart);
				cal_next.set(Calendar.MONTH, montht);
				cal_next.set(Calendar.DAY_OF_MONTH, dayt);
				cal_next.add(Calendar.DAY_OF_MONTH, 1);
				Calendar now = Calendar.getInstance();
				now.setTimeInMillis(System.currentTimeMillis());
				int nowday = now.get(Calendar.DAY_OF_MONTH);
				int nowmonth = now.get(Calendar.MONTH);
				yeart = cal_next.get(Calendar.YEAR);// 2014
				montht = cal_next.get(Calendar.MONTH);// 7
				dayt = cal_next.get(Calendar.DAY_OF_MONTH);// 26
				int dayofweek = cal_next.get(Calendar.DAY_OF_WEEK);
				getPlaneData(yeart + "-" + (montht + 1) + "-" + dayt);
				date=yeart + "-" + (montht + 1) + "-" + dayt;
				totay.setText((montht + 1) + "-" + dayt + " " + week[dayofweek - 1]);
				if (nowmonth == montht && dayt > nowday) {
					privious_day.setVisibility(View.VISIBLE);
					privious_day.setText("前一天");
				}
			}
		});
		privious_day.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar cal_previos = Calendar.getInstance();
				cal_previos.set(Calendar.YEAR, yeart);
				cal_previos.set(Calendar.MONTH, montht);
				cal_previos.set(Calendar.DAY_OF_MONTH, dayt);
				cal_previos.add(Calendar.DAY_OF_MONTH, -1);
				Calendar now = Calendar.getInstance();
				now.setTimeInMillis(System.currentTimeMillis());
				int nowday = now.get(Calendar.DAY_OF_MONTH);
				int nowmonth = now.get(Calendar.MONTH);
				yeart = cal_previos.get(Calendar.YEAR);
				montht = cal_previos.get(Calendar.MONTH);
				dayt = cal_previos.get(Calendar.DAY_OF_MONTH);
				if (nowmonth == montht && nowday == dayt) {
					privious_day.setVisibility(View.GONE);
				}
				int dayofweek = cal_previos.get(Calendar.DAY_OF_WEEK);
				getPlaneData(cal_previos.get(Calendar.YEAR) + "-" + (montht + 1) + "-" + dayt);
				date=cal_previos.get(Calendar.YEAR) + "-" + (montht + 1) + "-" + dayt;
				totay.setText((montht + 1) + "-" + dayt + " " + week[dayofweek - 1]);
			}
		});
		show_more_time.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PlaneList.this, PlaneDateWidget.class);
				intent.putExtra("title", "起飞");
				PlaneList.this.startActivityForResult(intent, 210);

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==300){
			if (data != null) {
				zfliststring = data.getStringExtra("zfliststring");
				namesstring = data.getStringExtra("namesstring");
				timstring = data.getStringExtra("timstring");
				cangweistring = data.getStringExtra("cangweistring");
				List<PlanListModel> list = new ArrayList<PlanListModel>();
				list.addAll(plane_list);
				for (int i = 0; i < plane_list.size(); i++) {
					PlanListModel p = plane_list.get(i);
					String company = getFirName(p.getAirWays());
					p._firName = company;
					String time_to_time = p.getDepTime() + "-" + p.getArriveTime();
					if (!Util.isNull(namesstring)) {
						if (!namesstring.contains(company)) {
							list.remove(p);
							System.out.println("list.size()====" + list.size());
						}
					}
					if (!Util.isNull(timstring)) {
						if (!timstring.contains(time_to_time)) {
							list.remove(p);
						}
					}
					if (!Util.isNull(zfliststring)) {
						if (p.getStopOver().equals("0")) {
							list.remove(p);
						}
					}
					if (!Util.isNull(cangweistring)) {
						if (!cangweistring.contains(p.getCabName())) {
							list.remove(p);
						}
					}
				}
				if (adapter != null) {
					adapter.clearlist();
				}
				adapter.setList(list);
				listview.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		}
		else if (requestCode == 210) {
			if (!Util.isNull(data)) {
				if (!Util.isNull(data.getStringExtra("takeoff")) && !Util.isNull(data.getStringExtra("year"))) {
					take_off_date = data.getStringExtra("takeoff");
					String years = data.getStringExtra("year");
					String[] ts = take_off_date.split(",,,,");
					String tx = ts[0].replace(".", "-");
					String[] dates = tx.split("-");
					String months = dates[0];
					String days = dates[1];
					yeart = Integer.parseInt(years);
					montht = Integer.parseInt(months);
					dayt = Integer.parseInt(days);	
					Calendar now = Calendar.getInstance();
					now.setTimeInMillis(System.currentTimeMillis());
					int nowday = now.get(Calendar.DAY_OF_MONTH);
					int nowmonth = now.get(Calendar.MONTH);
					if (nowmonth == montht && nowday == dayt) {
						privious_day.setVisibility(View.GONE);
					}
					Log.i("tag", "-----------------"+data.toString());
					getPlaneData(yeart + "-" + (montht) + "-" + dayt);
					date=yeart + "-" + (montht) + "-" + dayt;
					totay.setText((montht) + "-" + dayt + " " + ts[1]);

				}
			} else {
				System.out.println("传递过来的数据为空");
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void getPlaneData(final String datt) {
		AnimeUtil.setAnimationEmptyView(context, listview, true);
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if(null!=runData){	
					String[] result = runData.toString().split(";");
					if(result.length>1){
						String[] a = result[0].split("='");
						String before = a[1].replace("'", "");
						String[] b = result[2].split("='");
						String next = b[1].replace("'", "");
						previous_day_low_price.setText(before);
						next_day_low_price.setText(next);
						privious_day.setVisibility(View.VISIBLE);
						next_day_low_price.setVisibility(View.VISIBLE);
						previous_day_low_price.setVisibility(View.VISIBLE);
					}
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service, "/getTickMoneyByDate", "city_from=" + city_from + "&city_to=" + city_to + "&date=" + datt + "&cangwei=" + cangwei + "&pai="
						+ pai);
				String result = web.getPlan();
				return result;
			}
		});
		if (adapter != null) {
			adapter.clearlist();
		}

		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (adapter != null) {
					adapter.clearlist();
				}
				AnimeUtil.setNoDataEmptyView("列表为空...", R.drawable.community_dynamic_empty, context, listview, true, null);
				String result = (String) runData;
				parseBrandObject(result);
				if (plane_list.size() != 0) {
					if (adapter == null) {
						adapter = new PlaneListAdapter(PlaneList.this);
						listview.setAdapter(adapter);
					}
					if (plane_list.size() != 0) {
						getLowPrice(plane_list);
						lowPriceT.setText("￥" + lowPrice + "");
					}
					adapter.setList(plane_list);
					adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(PlaneList.this, "没有查询到对应数据", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service, Web.Ticket_getTicket, "city_from=" + city_from + "&city_to=" + city_to + "&date=" + datt + "&cangwei=" + cangwei + "&pai="
						+ pai);
				String result = web.getPlan();
				return result;
			}
		});



	}

	private void getLowPrice(List<PlanListModel> list) {
		lowPrice = Util.getInt(list.get(0).getPrice());
		for (int i = 1; i < list.size(); i++) {
			if (lowPrice > Util.getInt(list.get(i).getPrice())) {
				lowPrice = Util.getInt(list.get(i).getPrice());
			}
		}
	}

	private void parseBrandObject(String result) {
		JSONObject jso;
		plane_list.clear();
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
				String rewRates = "", Cabin = "", CabName = "", CabType = "", Price = "", Discount = "", PolicyId = "", Note = "", PlatOth = "", HkCashBack = "", TCashBack = "", CashBack = "", PStayMoney = "", SeatNum = "";
				// if(Util.isNull(CarrFlightNo)){
				// continue;
				// }
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
					rewRates = jo.getString("RewRates");
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
				p._firName = getFirName(p.getAirWays());
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
				p.setRewRates(rewRates);
				plane_list.add(p);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public class PlaneListAdapter extends BaseAdapter {
		private Context c;
		private LayoutInflater inflater;
		private List<PlanListModel> list = new ArrayList<PlanListModel>();
		private HashMap<Integer, View> map = new HashMap<Integer, View>();

		public PlaneListAdapter(Context c) {
			this.c = c;
			inflater = LayoutInflater.from(c);
		}

		private void setList(List<PlanListModel> list) {
			this.list = list;
			this.notifyDataSetChanged();
		}

		private void clearlist() {
			this.list.clear();
			System.out.println("清空adapter" + this.list.size());
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
			System.out.println("position=====" + position);
			PlanListModel p = this.list.get(position);
			ViewHolder h = null;
			if (map.get(position) == null) {
				convertView = inflater.inflate(R.layout.plane_list_item, null);
				h = new ViewHolder();
				h.take_off_airport = (TextView) convertView.findViewById(R.id.take_off_airport);
				h.take_off_time = (TextView) convertView.findViewById(R.id.take_off_time);
				h.landing_time = (TextView) convertView.findViewById(R.id.landing_time);
				h.landing_airport = (TextView) convertView.findViewById(R.id.landing_airport);
				h.price = (TextView) convertView.findViewById(R.id.ticket_price);
				h.discount = (TextView) convertView.findViewById(R.id.dicount_info);
				h.cabingNum = (TextView) convertView.findViewById(R.id.tickets_number);
				h.planeType = (TextView) convertView.findViewById(R.id.plane_type);
				h.logo = (ImageView) convertView.findViewById(R.id.plane_company_logo);
				convertView.setTag(h);
				map.put(position, convertView);
			} else {
				convertView = map.get(position);
				h = (ViewHolder) convertView.getTag();
			}
			h.take_off_airport.setText(city_from_name);
			h.landing_airport.setText(city_to_name);
			h.take_off_time.setText(p.getDepTime());
			h.landing_time.setText(p.getArriveTime());
			h.price.setText("￥" + p.getPrice() + "元");
			Double d = Double.parseDouble(p.getDiscount());
			h.discount.setText(Util.getDouble(d * 100, 2) + "%");
			String s = "";
			String company = "";
			if (p.getAirWays().contains("MF")) {
				s = "厦门航空" + p.getFlightNo();
				company = "厦门航空";
				h.logo.setImageResource(R.drawable.plane_xiamen);
			} else if (p.getAirWays().contains("HU")) {
				s = "海南航空" + p.getFlightNo();
				company = "海南航空";
				h.logo.setImageResource(R.drawable.plane_hainan);
			} else if (p.getAirWays().contains("3U")) {
				s = "四川航空" + p.getFlightNo();
				h.logo.setImageResource(R.drawable.plane_sichuan);
				company = "四川航空";
			} else if (p.getAirWays().contains("MU")) {
				s = "东方航空" + p.getFlightNo();
				h.logo.setImageResource(R.drawable.plane_shanghai);
				company = "东方航空";
			} else if (p.getAirWays().contains("CZ")) {
				s = "南方航空" + p.getFlightNo();
				h.logo.setImageResource(R.drawable.plane_nanfang);
				company = "南方航空";
			} else if (p.getAirWays().contains("CA")) {
				s = "国际航空" + p.getFlightNo();
				h.logo.setImageResource(R.drawable.plane_china);
				company = "国际航空";
			} else if (p.getAirWays().contains("ZH")) {
				s = "深圳航空" + p.getFlightNo();
				h.logo.setImageResource(R.drawable.plane_shenzheng);
				company = "深圳航空";
			} else if (p.getAirWays().contains("HO")) {
				s = "吉祥航空" + p.getFlightNo();
				h.logo.setImageResource(R.drawable.plane_jixiang);
				company = "吉祥航空";
			} else {
				s = p._firName + p.getFlightNo();
				company = p._firName;
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
					Intent intent = new Intent(PlaneList.this, ChooseCabin.class);
					intent.putExtra("planeinfo", planeinfo);
					intent.putExtra("startcity", city_from_name);
					intent.putExtra("landingcity", city_to_name);
					intent.putExtra("takeofftime", pp.getDepTime());
					intent.putExtra("landingtime", pp.getArriveTime());

					int i1 = Integer.parseInt(pp.getDepTime().replace(":", ""));
					int i2 = Integer.parseInt(pp.getArriveTime().replace(":", ""));
					Calendar c = Calendar.getInstance();
					String ldate = "";
					c.set(Calendar.YEAR, yeart);
					c.set(Calendar.MONTH, montht);
					c.set(Calendar.DAY_OF_MONTH, dayt);
					if (i2 < i1) {

						c.add(Calendar.DAY_OF_MONTH, 1);

					}
					ldate = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH);
					intent.putExtra("takeoffdate", yeart + "-" + (montht + 1) + "-" + dayt);
					intent.putExtra("landingdate", ldate);
					intent.putExtra("city_from", city_from);
					intent.putExtra("city_to", city_to);
					intent.putExtra("othercabin", pp.getOtherCabin());
					intent.putExtra("flightno", pp.getFlightNo());
					intent.putExtra("FuelTax", pp.getFuelTax());
					intent.putExtra("DepTerm", pp.getDepTerm());
					intent.putExtra("ArrTerm", pp.getArriveTerm());
					intent.putExtra("AirWays", pp.getAirWays());
					intent.putExtra("StopOver", pp.getStopOver());
					intent.putExtra("company", com);
					intent.putExtra("FlightMod", pp.getFlightMod());
					intent.putExtra("year", year);
					intent.putExtra("month", month);
					intent.putExtra("day", day);
					intent.putExtra("AirConFee", pp.getAirConFee());
					PlaneCabinModel mo = new PlaneCabinModel();
					mo.setADTPrice("");
					mo.setADTTax("0");
					mo.setADTYq("");
					mo.setCabin(pp.getCabin());
					mo.setCabName(pp.getCabName());
					mo.setCabType(pp.getCabType());
					mo.setIsPubTar(pp.getIsPubTar());// 是否公布运价，0非公布运价，1公布运价。
					mo.setSeatNum(pp.getSeatNum());
					mo.setPrice(pp.getPrice());
					mo.setDiscount(pp.getDiscount());
					if (!Util.isNull(pp.getRewRates())) {
						mo.setRewRates(pp.getRewRates());
					} else {
						mo.setRewRates("0");
					}
					mo.setPolicyId(pp.getPolicyId());
					mo.setNote(pp.getNote());
					mo.setINFPrice("");
					mo.setPlatOth(pp.getPlatOth());
					Bundle bundle = new Bundle();
					bundle.putSerializable("model", mo);
					intent.putExtras(bundle);
					PlaneList.this.startActivity(intent);
				}
			});
			return convertView;
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
	}
}
