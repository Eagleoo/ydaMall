package com.mall.serving.filmticket;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.FilmItem;
import com.mall.net.Web;
import com.mall.util.BitmapCache;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.BMFWFrane;
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
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class FilmList extends Activity {
	private ListView listview;
	private EditText search;      
	private FilmListAdapter adapter;
	private TextView NavigateTitle, city;
	private TextView top_back;
	private String cityName = "";
	private String areaId = "";
	private SharedPreferences sp;
	private List<FilmItem> originalList = new ArrayList<FilmItem>();
	private PositionService locationService;
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
					if (!Util.isNull(progress.getCity())){
						cityName = progress.getCity();
					}else {
						cityName="城市";
					}
					city.setText(cityName);
					checkCity();
				}

				@Override
				public void onError(AMapLocation error) {
					checkCity();
					cityName="城市";
				}
			});
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filemlist);
		sp = this.getSharedPreferences("filmcity", 0);
		ViewUtils.inject(this);
		init();
	}
	@OnClick(R.id.shy_advert)
	public void advert(View v){
		Util.showIntent(this, BMFWFrane.class);
	}
	private void init() {
		initView();
		getIntentData();
	}
	private void initView() {
		NavigateTitle = (TextView) this.findViewById(R.id.NavigateTitle);
		NavigateTitle.setText("选择电影");
		city = (TextView) this.findViewById(R.id.city);
		city.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Util.showIntent(FilmList.this, FilmCity.class);
			}
		});
		top_back = (TextView) this.findViewById(R.id.top_back);
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FilmList.this.finish();
				sp.edit().putString("cities", "").commit();// 在用户退出订电影票的时候，清除掉sp中的值
			}
		});
		listview = (ListView) this.findViewById(R.id.film_list);
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
				if(adapter!=null){
					if(!Util.isNull(adapter.getList())){
						List<FilmItem> l = adapter.getList();
						List<FilmItem> l2 = new ArrayList<FilmItem>();
						if (Util.isNull(s.toString())) {
							adapter.setList(originalList);
							adapter.notifyDataSetChanged();
						} else {
							for (int i = 0; i < l.size(); i++) {
								if (l.get(i).getFilmName().contains(s.toString())) {
									l2.add(l.get(i));
								}
							}
							if (l2.size() == 0) {
								adapter.setList(originalList);
								adapter.notifyDataSetChanged();
							} else {
								adapter.setList(l2);
								adapter.notifyDataSetChanged();
							}

						}
					}
				}
			}
		});
	}
	private void getIntentData() {
		try {
			if (this.getIntent() != null&& !Util.isNull(this.getIntent().getStringExtra("areaname"))&& !Util.isNull(this.getIntent().getStringExtra("areano"))) {
				cityName = this.getIntent().getStringExtra("areaname");
				city.setText(cityName);
			    areaId = this.getIntent().getStringExtra("areano");
				if (!Util.isNull(sp.getString("cities", ""))) {
					jsonCityToObject(sp.getString("cities", ""));
				} else {
					getCities();
				}
				firstpage();
			}else{
				Intent intent = new Intent();
				intent.setAction(PositionService.TAG);
				intent.setPackage(getPackageName());
				getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void firstpage() {
		asyncLoadData();
	}
	private void asyncLoadData() {
		Util.asynTask(FilmList.this, "正在获取电影信息....", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<Integer, List<FilmItem>> map = (HashMap<Integer, List<FilmItem>>) runData;
				List<FilmItem> list = map.get(1);
				if (adapter == null) {
					adapter = new FilmListAdapter(FilmList.this);
					listview.setAdapter(adapter);
				}
				originalList.clear();
				originalList.addAll(list);
				System.out.println("originalList.size()===++=="+originalList.size());
				if (list.size() != 0) {
					adapter.setList(list);
				} else {
					Toast.makeText(FilmList.this, "该城市暂无影片信息...",Toast.LENGTH_LONG).show();
				}
				adapter.notifyDataSetChanged();
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service, Web.Film_getHot,
						"areaNo=" + areaId + "&CinemaNo=");
				InputStream in = web.getHtml();
				List<FilmItem> list = new ArrayList<FilmItem>();
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
				HashMap<Integer, List<FilmItem>> map = new HashMap<Integer, List<FilmItem>>();
				map.put(1, list);
				return map;
			}

		});
	}
	private void getCities() {
		Util.asynTask(FilmList.this, "正在获取城市数据...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				String result = (String) runData;
				sp.edit().putString("cities", result).commit();
				jsonCityToObject(result);
			}
			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service, Web.Film_getAreaList,"");
				String result = web.getPlanGb2312();
				return result;
			}
		});
	}
    private void checkCity(){
    	if (!Util.isNull(sp.getString("cities", ""))) {
			 jsonCityToObject(sp.getString("cities", ""));
		} else {
			 getCities();// 如果为保存城市列表，则重新加载
		}
    }
	// 针对当前定位城市，判断定位城市的areaid
	protected void jsonCityToObject(String result) {
		JSONArray jsonArray;
		String[] results = result.split(",");
		try {
			jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject myjObject = jsonArray.getJSONObject(i);
				String AreaNo=myjObject.getString("AreaNo");
				String AreaName=myjObject.getString("AreaName");
				String AreaLevel=myjObject.getString("AreaLevel");
				if (city.getText().toString().contains(AreaName.trim())&&!AreaLevel.equals("1")) {
					areaId = AreaNo;
					break;
				}
			}
			firstpage();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public class FilmListAdapter extends BaseAdapter {
		private Context context;
		private List<FilmItem> list = new ArrayList<FilmItem>();
		private LayoutInflater inflater;
		public Map<Integer, View> map = new HashMap<Integer, View>();
		private RequestQueue mQueue;
		private ImageLoader mImageLoader;
		public FilmListAdapter(Context c) {
			this.context = c;
			inflater = LayoutInflater.from(c);
			mQueue = Volley.newRequestQueue(c);
			mImageLoader = new ImageLoader(mQueue, new BitmapCache());
		}

		private void clearList() {
			this.list.clear();
		}

		private List<FilmItem> getList() {
			return this.list;
		}

		public void setList(List<FilmItem> list) {
			this.list=list;
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
			FilmItem f = list.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.film_list_item, null);
				holder.film_image = (ImageView) convertView
						.findViewById(R.id.film_image);
				holder.film_name = (TextView) convertView
						.findViewById(R.id.film_name);
				holder.film_special_price = (TextView) convertView
						.findViewById(R.id.film_special_price);
				holder.film_intro = (TextView) convertView
						.findViewById(R.id.film_intro);
				holder.film_price = (TextView) convertView
						.findViewById(R.id.film_price);
				holder.film_ratimg = (RatingBar) convertView
						.findViewById(R.id.film_ratimg);
				holder.rating_number = (TextView) convertView
						.findViewById(R.id.rating_number);
				convertView.setTag(holder);
			} else {
//				convertView = map.get(position);
				holder = (ViewHolder) convertView.getTag();
			}
			String desc="";
			if(f.getFilmDesc().length()>30){
				desc=f.getFilmDesc().substring(0, 30);
			}else{
				desc=f.getFilmDesc();
			}
			holder.film_intro.setText(desc);
			holder.film_price.setText("￥" + f.getTicketPrice() + "元");
			holder.film_name.setText(f.getFilmName());
			holder.rating_number.setText(f.getAverageDegree());
			double half = Double.parseDouble(f.getAverageDegree()) / 2;
			holder.film_ratimg.setRating((float) half);
			ImageListener listener = ImageLoader.getImageListener(holder.film_image, R.drawable.no_get_image,R.drawable.no_get_image);
			mImageLoader.get(f.getFrontImg(), listener);
			final FilmItem fm = f;
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(FilmList.this,MovieTheater.class);
					intent.putExtra("name", fm.getFilmName());
					intent.putExtra("director", fm.getDirectors());
					intent.putExtra("mainactor", fm.getMainActors());
					String juzhao = "";
					for (int i = 0; i < fm.getPictureUrls().size(); i++) {
						juzhao += fm.getPictureUrls().get(i) + ",,,";
					}
					// for(int k=0;k<li.size();k++){
					// threeid=threeArea.get(i)+"..."
					// }
					intent.putExtra("score", fm.getAverageDegree());
					intent.putExtra("version", fm.getFilmType());
					intent.putExtra("duration", fm.getDuration());
					intent.putExtra("desc", fm.getFilmDesc());
					intent.putExtra("juzhao", juzhao);
					intent.putExtra("city", cityName);
					intent.putExtra("cityId", areaId);
					intent.putExtra("filmid", fm.getFilmNo());
					intent.putExtra("area", fm.getOriginArea());
					intent.putExtra("frontimage", fm.getFrontImg());
					context.startActivity(intent);
				}
			});
			return convertView;
		}
	}

	private static class ViewHolder {
		ImageView film_image;
		TextView film_name;
		TextView film_special_price;
		TextView film_intro;
		RatingBar film_ratimg;
		TextView rating_number;
		TextView film_price;
	}

	public class ListHandler extends DefaultHandler {
		private List<FilmItem> films = new ArrayList<FilmItem>();
		private FilmItem film;
		private StringBuilder builder;

		// 返回解析后得到的Book对象集合
		public List<FilmItem> getFilms() {
			return films;
		}

		@Override
		public void startDocument() throws SAXException {
			films.clear();
			builder = new StringBuilder();
			super.startDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			if (qName.equals("RWList"))
				film = new FilmItem();
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
			if (qName.equals("tradaId")) {
				film.setTradaId(s);
			} else if (qName.equals("timeStamp")) {
				film.setTimeStamp(s);
			} else if (qName.equals("FilmNo")) {
				film.setFilmNo(s);
			} else if (qName.equals("FilmName")) {
				film.setFilmName(s);
			} else if (qName.equals("FrontImg")) {
				film.setFrontImg(s);
			} else if (qName.equals("TicketPrice")) {
				film.setTicketPrice(s);
			} else if (qName.equals("FouseAmount")) {
				film.setFouseAmount(s);
			} else if (qName.equals("SaleAmount")) {
				film.setSaleAmount(s);
			} else if (qName.equals("AverageDegree")) {
				film.setAverageDegree(s);
			} else if (qName.equals("MainActors")) {
				film.setMainActors(s);
			} else if (qName.equals("Director")) {
				film.setDirectors(s);
			} else if (qName.equals("OriginArea")) {
				film.setOriginArea(s);
			} else if (qName.equals("FilmType")) {
				film.setFilmType(s);
			} else if (qName.equals("Duration")) {
				film.setDuration(s);
			} else if (qName.equals("FilmType")) {
				film.setFilmType(s);
			} else if (qName.equals("Language")) {
				film.setLanguage(s);
			} else if (qName.equals("info")) {
				film.setFilmDesc(s);
			} else if (qName.equals("Pictures")) {
				String[] ss=s.split("\\|,\\|");
				for(int i=0;i<ss.length;i++){
					film.getPictureUrls().add(ss[i]);
				}
				
			} else if (qName.equals("RWList")) {
				films.add(film);
			}
			super.endElement(uri, localName, qName);
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
		}
	}
	@Override
	protected void onDestroy() {
		if(originalList!=null){
			if(originalList.size()>0){
				originalList.clear();
			}
		}
		if(adapter!=null){
			if(adapter.map!=null){
				adapter.map.clear();
			}
		}
		super.onDestroy();
	}
}
