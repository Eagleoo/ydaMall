package com.mall.serving.filmticket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.MovieTheaterModel;
import com.mall.net.Web;
import com.mall.util.BitmapCache;
import com.mall.util.CharacterParser;
import com.mall.util.IAsynTask;
import com.mall.util.TheaterPinyinComparator;
import com.mall.util.Util;
import com.mall.view.PositionService;
import com.mall.view.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MovieTheater extends Activity {
	private ListView lisetview;
	private TextView NavigateTitle, city;
	private TextView top_back;
	private MovieTheaterAdapter adapter;
	private String name = "", director = "", mainactor = "", score = "",
			type = "", duration = "", desc = "", juzhao = "", cityName = "",
			cityId = "", filmid = "", area, frontimage = "", cityNo = "";
	private TextView film_name, directorT, actors, film_detail;
	private EditText search;
	private TheaterPinyinComparator pinyinComparator;
	private List<MovieTheaterModel> originalList = new ArrayList<MovieTheaterModel>();
	private RequestQueue requestqueue;
	private ImageLoader imageloader;
	private CharacterParser cp = null;
	private ImageView film_image;
	private PositionService locationService;
	@ViewInject(R.id.choose_ticket)
	private TextView choose_ticket;
	@ViewInject(R.id.commonticket)
	private TextView commonticket;
	private List<MovieTheaterModel> ct_movietheater=new ArrayList<MovieTheaterModel>();
	private boolean ct=false;
	private ServiceConnection locationServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("MovieTheater", "bind服务成功！");
			PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
			locationService = locationBinder.getService();
			locationService.startLocation();
			locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
				@Override
				public void onProgress(AMapLocation progress) {
					cityName = progress.getCity();
					city.setText(cityName);

				}

				@Override
				public void onError(AMapLocation error) {

				}
			});
		}
	};                
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movietheater);
		pinyinComparator = new TheaterPinyinComparator();
		cp = CharacterParser.getInstance();
		ViewUtils.inject(this);
		init();
	}
	@OnClick({ R.id.choose_ticket, R.id.commonticket })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.choose_ticket:
			choose_ticket.setBackgroundColor(getResources().getColor(R.color.newgreen));
			choose_ticket.setTextColor(Color.WHITE);
			commonticket.setBackgroundColor(Color.TRANSPARENT);
			commonticket.setTextColor(getResources().getColor(R.color.newgreen));
			ct=false;
			if(originalList.size()==0){
				if(adapter!=null){
	               	 adapter.list.clear();
	               	 adapter.map.clear();
	               	 adapter.notifyDataSetChanged();
	                }
			  asyncLoadData();
			}else{
				if(adapter!=null){
					adapter.list.clear();
					adapter.map.clear();
					adapter.notifyDataSetChanged();
				}
				List<MovieTheaterModel> list=new ArrayList<MovieTheaterModel>();
				list.addAll(originalList);;
				adapter.setList(list);
				adapter.notifyDataSetChanged();
			}
			break;
		case R.id.commonticket:
			commonticket.setBackgroundColor(getResources().getColor(R.color.newgreen));
			commonticket.setTextColor(Color.WHITE);
			choose_ticket.setBackgroundColor(Color.TRANSPARENT);
			choose_ticket.setTextColor(getResources().getColor(R.color.newgreen));
			ct=true;
			if(ct_movietheater.size()==0){
				if(adapter!=null){
	               	 adapter.list.clear();
	               	 adapter.map.clear();
	               	 adapter.notifyDataSetChanged();
	                }
				loadCommonTicketCinema();
			}else{
				if(adapter!=null){
               	 adapter.list.clear();
               	 adapter.map.clear();
               	 adapter.notifyDataSetChanged();
                }
				List<MovieTheaterModel> list=new ArrayList<MovieTheaterModel>();
				list.addAll(ct_movietheater);
				adapter.setList(list);
			}
			
			break;
		default:
			break;
		}
	}
	private void init() {
		initView();
		if (!Util.isNull(this.getIntent().getStringExtra("city"))) {
			getIntentData();
			city.setText(cityName);
		} else {
			getIntentData();

			Intent intent = new Intent();
			intent.setAction(PositionService.TAG);
			intent.setPackage(getPackageName());
			getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);

		}
		film_name.setText(name);
		directorT.setText(director);
		if (mainactor.length() > 20) {
			mainactor = mainactor.substring(0, 20);
			actors.setText(mainactor);
		} else {
			actors.setText(mainactor);
		}
		asyncLoadData();
	}
	private void initView() {
		NavigateTitle = (TextView) this.findViewById(R.id.NavigateTitle);
		NavigateTitle.setText("选择影院");
		city = (TextView) this.findViewById(R.id.city);
		city.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Util.showIntent(MovieTheater.this, FilmCity.class);
			}
		});
		top_back = (TextView) this.findViewById(R.id.top_back);
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MovieTheater.this.finish();
			}
		});
		film_name = (TextView) this.findViewById(R.id.film_name);
		directorT = (TextView) this.findViewById(R.id.directorT);
		actors = (TextView) this.findViewById(R.id.actors);
		film_detail = (TextView) this.findViewById(R.id.film_detail);
		film_detail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MovieTheater.this, FileDetail.class);
				intent.putExtra("name", name);
				intent.putExtra("duration", duration);
				intent.putExtra("mainactor", mainactor);
				intent.putExtra("version", type);
				intent.putExtra("score", score);
				intent.putExtra("area", area);
				intent.putExtra("desc", desc);
				intent.putExtra("director", director);
				intent.putExtra("frontimage", frontimage);
				intent.putExtra("juzhao", juzhao);
				MovieTheater.this.startActivity(intent);
			}
		});
		lisetview = (ListView) this.findViewById(R.id.theater_list);
		film_image = (ImageView) this.findViewById(R.id.film_image);
		search = (EditText) this.findViewById(R.id.search);
		search.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				EditText e = (EditText) v;
				if (hasFocus) {
					e.setBackgroundResource(R.drawable.layout_width_no_corner_with_blue);
				} else {
					e.setBackgroundResource(R.drawable.editext_no_border);
				}
			}
		});
		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				List<MovieTheaterModel> l =new ArrayList<MovieTheaterModel>();
				//获取adapter中的List
				l.clear();
				if(ct){
				   l.addAll(ct_movietheater);
				}else{
					l.addAll(originalList);
				}
				List<MovieTheaterModel> l2 = new ArrayList<MovieTheaterModel>();
				if (Util.isNull(s.toString())) {
					adapter.list.clear();
					adapter.setList(l);
				} else {
					for (int i = 0; i < l.size(); i++) {
						if (l.get(i).getCniemaName().contains(s.toString())) {
							l2.add(l.get(i));
						}
					}
					if (l2.size() == 0) {
						adapter.setList(l);
					} else {
						adapter.setList(l2);
					}
				}
			}
		});
	}
	private void getIntentData() {
		name = this.getIntent().getStringExtra("name");
		director = this.getIntent().getStringExtra("director");
		mainactor = this.getIntent().getStringExtra("mainactor");
		score = this.getIntent().getStringExtra("score");
		type = this.getIntent().getStringExtra("version");
		duration = this.getIntent().getStringExtra("duration");
		desc = this.getIntent().getStringExtra("desc");
		juzhao = this.getIntent().getStringExtra("juzhao");
		cityName = this.getIntent().getStringExtra("city");
		cityNo = this.getIntent().getStringExtra("cityId");
		if (Util.isNull(this.getIntent().getStringExtra("cityId"))) {
			cityId = "110100";// 如果城市id为空，则设为北京
		} else {
			cityId = this.getIntent().getStringExtra("cityId");
		}
		filmid = this.getIntent().getStringExtra("filmid");
		area = this.getIntent().getStringExtra("area");
		frontimage = this.getIntent().getStringExtra("frontimage");
		requestqueue = Volley.newRequestQueue(MovieTheater.this);
		imageloader = new ImageLoader(requestqueue, new BitmapCache());
		ImageListener listener = ImageLoader.getImageListener(film_image,
				R.drawable.no_get_image, R.drawable.no_get_image);
		imageloader.get(frontimage, listener);
	}
	private void asyncLoadData() {
		Util.asynTask(MovieTheater.this, "正在获取影院信息...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<Integer, List<MovieTheaterModel>> map = (HashMap<Integer, List<MovieTheaterModel>>) runData;
				List<MovieTheaterModel> list = map.get(1);
				// Collections.sort(list, pinyinComparator);
				if (adapter == null) {
					adapter = new MovieTheaterAdapter(MovieTheater.this);
				}else{
					adapter.list.clear();
					adapter.map.clear();
					adapter.notifyDataSetChanged();
				}
				sortDataList(list);
				adapter.setList(list);
				originalList.clear();
				originalList.addAll(list);
				lisetview.setAdapter(adapter);
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service, Web.Film_loadCinema,
						"FilmNo=" + filmid + "&CityNo=" + cityId, "gb2312");
				InputStream in = web.getHtml();
				List<MovieTheaterModel> list = new ArrayList<MovieTheaterModel>();
				ListHandler handler = null;
				SAXParser parser = null;
				try {
					SAXParserFactory factory = SAXParserFactory.newInstance(); // 取得SAXParserFactory实例
					parser = factory.newSAXParser();
					handler = new ListHandler();
					list = handler.getFilms();
					parser.parse(in, handler);
				} catch (Exception e) {
					e.printStackTrace();
				}
				HashMap<Integer, List<MovieTheaterModel>> map = new HashMap<Integer, List<MovieTheaterModel>>();
				map.put(1, list);
				return map;
			}
		});
	}
	private void sortDataList(List<MovieTheaterModel> list) {
		HashMap<String, List<MovieTheaterModel>> map = new HashMap<String, List<MovieTheaterModel>>();
		for (int i = 0; i < list.size(); i++) {
			MovieTheaterModel m = list.get(i);
			if (!map.containsKey(m.getAreaName())) {
				List<MovieTheaterModel> l = new ArrayList<MovieTheaterModel>();
				l.add(m);
				map.put(m.getAreaName(), l);
			} else {
				map.get(m.getAreaName()).add(m);
			}
		}

		List<MovieTheaterModel> newlist = new ArrayList<MovieTheaterModel>();
		Set<String> keyset = map.keySet();
		Iterator<String> ite = keyset.iterator();
		for (String key : keyset) {
			newlist.addAll(map.get(ite.next()));
		}
		list.clear();
		list.addAll(newlist);
	}
	public class MovieTheaterAdapter extends BaseAdapter implements
			SectionIndexer {
		private Context c;
		private LayoutInflater inflater;
		private List<MovieTheaterModel> list = new ArrayList<MovieTheaterModel>();     
		private String areaName = "";
		private Map<Integer, View> map = new HashMap<Integer, View>();
		private Map<String, Integer> sections = new HashMap<String, Integer>();
		private List<String> l = new ArrayList<String>();

		public MovieTheaterAdapter(Context context) {
			this.c = context;
			inflater = LayoutInflater.from(context);

		}

		private List<MovieTheaterModel> getList() {
			return this.list;
		}

		private void clearList() {
			this.list.clear();
		}

		private void setList(List<MovieTheaterModel> list) {
			this.list = list;
			this.sections.clear();
			// Map保存第一次出现Key的位置，这样无论是向上滑动还是向下滑动，TVLetter出现的位置都是唯一的
			for (int i = 0; i < this.list.size(); i++) {
				MovieTheaterModel m = this.list.get(i);
				if (!sections.containsKey(list.get(i).getAreaName())) {
					sections.put(m.getAreaName(), i);
				}
			}
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

		@SuppressLint("DefaultLocale")
		@Override
		public View getView(int position, View v, ViewGroup arg2) {
			ViewHolder holder = null;
			MovieTheaterModel m = list.get(position);
			if (map.get(position) == null) {
				v = inflater.inflate(R.layout.theater_item, null);
				holder = new ViewHolder();
				holder.cinemaName = (TextView) v.findViewById(R.id.name);
				holder.cinemaAddress = (TextView) v.findViewById(R.id.address);
				holder.tvLetter = (TextView) v.findViewById(R.id.letter);
				holder.common_ticket = (TextView) v
						.findViewById(R.id.common_ticket);
				v.setTag(holder);
				map.put(position, v);
			} else {
				v = map.get(position);
				holder = (ViewHolder) v.getTag();
			}
			if(Util.isNull(m.getAddress())){
				holder.cinemaAddress.setVisibility(View.GONE);
			}else{
				holder.cinemaAddress.setText(m.getAddress());
			}
			holder.cinemaName.setText(m.getCniemaName());
			holder.cinemaName.setTag(m.getCniemaNo());
			if (sections.get(m.getAreaName()).intValue() == position) {
				holder.tvLetter.setVisibility(View.VISIBLE);
				holder.tvLetter.setText(m.getAreaName());
			} else {
				holder.tvLetter.setVisibility(View.GONE);
			}
			final MovieTheaterModel mm = m;
			if(ct){
				holder.common_ticket.setVisibility(View.VISIBLE);
//				if (m.getSellFlag().equals("2") || m.getSellFlag().equals("3")) {
//					holder.common_ticket.setVisibility(View.VISIBLE);
					holder.common_ticket.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(MovieTheater.this,CommonTicket.class);
							intent.putExtra("FilmNo", mm.getCniemaNo());
							intent.putExtra("Area",cityName + "--" + mm.getAreaName());
							intent.putExtra("CityNo", cityId);
							intent.putExtra("CniemaName", mm.getCniemaName());
							c.startActivity(intent);
						}
					});
//				}
			}else{
				v.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(MovieTheater.this,
								WatchTime.class);
						intent.putExtra("cinemaName", mm.getCniemaName());
						intent.putExtra("cinemaNo", mm.getCniemaNo());
						intent.putExtra("filmName", name);
						intent.putExtra("frontimage", frontimage);
						intent.putExtra("filmNo", filmid);
						intent.putExtra("logo", mm.getCinemaLogo());
						intent.putExtra("address", mm.getAddress());
						intent.putExtra("phone", mm.getPhoneNo());
						intent.putExtra("intro", mm.getIntroduction());
						intent.putExtra("rating", mm.getAverageDegree());
						intent.putExtra("latlng", mm.getLatLng());
						intent.putExtra("duration", duration);
						intent.putExtra("filmName", name);
						intent.putExtra("cityNo", cityNo);
						c.startActivity(intent);
					}
				});
			}
			// if(m.getSellFlag().equals("1")||m.getSellFlag().equals("3")){
			
			// }
			return v;
		}

		public int getSectionForPosition(int position) {
			return list.get(position).getSortLetters().charAt(0);
		}

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

		@Override
		public Object[] getSections() {
			return null;
		}
	}
	public class ViewHolder {
		TextView cinemaName;
		TextView cinemaAddress;
		TextView tvLetter;
		TextView common_ticket;
	}
	public class ListHandler extends DefaultHandler {
		private List<MovieTheaterModel> theaters = new ArrayList<MovieTheaterModel>();
		private MovieTheaterModel m;
		private StringBuilder builder;

		// 返回解析后得到的Book对象集合
		public List<MovieTheaterModel> getFilms() {
			return theaters;
		}

		@Override
		public void startDocument() throws SAXException {
			builder = new StringBuilder();
			super.startDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			if (qName.equals("FilmList"))
				m = new MovieTheaterModel();
			builder.setLength(0);
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			super.characters(ch, start, length);
			builder.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			String s = builder.toString();
			if (qName.equals("SellFlag")) {
				m.setSellFlag(s);
			} else if (qName.equals("CinemaNo")) {
				m.setCniemaNo(s);
			} else if (qName.equals("CinemaName")) {
				m.setCniemaName(s);
			} else if (qName.equals("AreaNo")) {
				m.setAreaNo(s);
			} else if (qName.equals("AreaName")) {
				m.setAreaName(s);
				String paixuString = "";
				try {
					paixuString = cp.convert(s).toUpperCase();// 这个地方调试的时候是有值得，为什么会报空指针？？？//
																// 有些地名可能无法解析成拼音
					// if(theaters.size()>0){
					// for(int i=0;i<theaters.size();i++){
					// if(theaters.get(i).getSortLetters().equals(paixuString)){
					// paixuString=paixuString+paixuString.substring(0,1);
					// }
					// }
					// m.setSortLetters(paixuString);
					// }else{
					//
					// }
				} catch (Exception e) {
					paixuString = "#";
				}
				m.setSortLetters(paixuString);

			} else if (qName.equals("Address")) {
				m.setAddress(s);
			} else if (qName.equals("CinemaLogo")) {
				m.setCinemaLogo(s);
			} else if (qName.equals("PhoneNo")) {
				m.setPhoneNo(s);
			} else if (qName.equals("Traffic")) {
				m.setTraffic(s);
			} else if (qName.equals("Introduction")) {
				m.setIntroduction(s);
			} else if (qName.equals("LatLng")) {
				m.setLatLng(s);
			} else if (qName.equals("IsHasPark")) {
				m.setIsHasPark(s);
			} else if (qName.equals("IsSupportUnionPay")) {
				m.setIsSupportUnionPay(s);
			} else if (qName.equals("IsSupport3D")) {
				m.setIsSupport3D(s);
			} else if (qName.equals("IsSupportGoods")) {
				m.setIsSupportGoods(s);
			} else if (qName.equals("IsSupportRest")) {
				m.setIsSupportRest(s);
			} else if (qName.equals("AverageDegree")) {
				m.setAverageDegree(s);
			} else if (qName.equals("FilmList")) {
				theaters.add(m);
			}
			super.endElement(uri, localName, qName);
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
		}
	}
    private List<MovieTheaterModel> parseJSon(String result){
    	List<MovieTheaterModel> list=new ArrayList<MovieTheaterModel>();
    	JSONObject jso;
		try {
			jso = new JSONObject(result);
			String data=jso.getString("Object");
			JSONArray jsonArray=new JSONArray(data);
			String CinemaNo="",CinemaName="",AreaNo="",AreaName="",Address="";
			for(int i=0;i<jsonArray.length();i++){
				JSONObject obj=jsonArray.getJSONObject(i);
				MovieTheaterModel mv=new MovieTheaterModel();
				CinemaNo=obj.getString("CinemaNo");
				CinemaName=obj.getString("CinemaName");
				AreaNo=obj.getString("AreaNo");
				AreaName=obj.getString("AreaName");
				Address=obj.getString("Address");
				mv.setAreaName(AreaName);
				mv.setCniemaNo(CinemaNo);
				mv.setCniemaName(CinemaName);
				mv.setAreaNo(AreaNo);
				mv.setAddress(Address);
				ct_movietheater.add(mv);
				list.add(mv);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	return list;
    }
	private void loadCommonTicketCinema(){
    	Util.asynTask(this, "", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
                 String result=(String) runData;
                 List<MovieTheaterModel> list = parseJSon(result);
                 if(adapter!=null){
                	 adapter.list.clear();
                	 adapter.map.clear();
                	 adapter.notifyDataSetChanged();
                 }else{
                	 adapter=new MovieTheaterAdapter(MovieTheater.this);
                 }
                 sortDataList(list);
                 adapter.setList(list);
            	 adapter.notifyDataSetChanged();
            	 lisetview.setAdapter(adapter);
			}
			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service, Web.Film_loadCinema,"FilmNo=All_Cinema:" +"&CityNo=" + cityId, "gb2312");
				String result=web.getPlanGb2312();
				return result;
			}
		});
    }
}
