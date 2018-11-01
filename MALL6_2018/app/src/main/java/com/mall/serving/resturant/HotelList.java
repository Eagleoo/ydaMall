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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.mall.model.HotelItem;
import com.mall.net.Web;
import com.mall.util.BitmapCache;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;
public class HotelList extends Activity {
	//查询酒店列表的条件数据
	private String cityId="";
	private String hname="";
	private String intime="";
	private String outtime="";
	private String pricerange="";
	private String addkey="";
	private int currentPage=0;
	private String cityName="";
	private TextView NavigateTitle,city,in,out,hotel_name,price_low,price_top,all,toptitle,top_back;
	private ListView hotel_list;
	private HotelListAdapter adapter;
	private List<HotelItem> list=new ArrayList<HotelItem>();
	private double lat,lng;
	private String px="0";
	private BitmapUtils bmUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotellist);
		bmUtils=new BitmapUtils(this);
		init();
	}
	private void init(){
		initView();
		getQueryData();
		firstpage(cityId,addkey,px);
		scrollPage(cityId,addkey,px);
	}
	private void initView(){
		NavigateTitle=(TextView) this.findViewById(R.id.NavigateTitle);
		NavigateTitle.setText(cityName);
		top_back=(TextView) this.findViewById(R.id.top_back);
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HotelList.this.finish();
			}
		});
		city=(TextView) this.findViewById(R.id.city);
		city.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NavigateTitle.setText("");
				Intent intent=new Intent(HotelList.this,ResturantCity.class);
				HotelList.this.startActivityForResult(intent, 300);
			}
		});
		in=(TextView) this.findViewById(R.id.in);
		hotel_list=(ListView) this.findViewById(R.id.hotel_list);
		hotel_name=(TextView) this.findViewById(R.id.hotel_name);
		hotel_name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(HotelList.this,ResurantName.class);
				intent.putExtra("from", "hotbrand");
				intent.putExtra("cityid",cityId);
				HotelList.this.startActivityForResult(intent, 301);
			}
		});
		out=(TextView) this.findViewById(R.id.out);
		toptitle = (TextView) this.findViewById(R.id.toptitle);
		price_low=(TextView) this.findViewById(R.id.price_low);
		price_top=(TextView) this.findViewById(R.id.price_top);
		all=(TextView) this.findViewById(R.id.all);
		price_low.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				price_low.setTextColor(getResources().getColor(R.color.new_headertop));
				all.setTextColor(Color.BLACK);
				price_top.setTextColor(Color.BLACK);
				px="2";
				currentPage=0;
				adapter.clear();
				firstpage(cityId,addkey,px);
				scrollPage(cityId,addkey,px);
			}
		});
		price_top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				price_top.setTextColor(getResources().getColor(R.color.new_headertop));
				all.setTextColor(Color.BLACK);
				price_low.setTextColor(Color.BLACK);
				px="4";
				currentPage=0;
				adapter.clear();
				firstpage(cityId,addkey,px);
				scrollPage(cityId,addkey,px);
			}
		});
		all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				all.setTextColor(getResources().getColor(R.color.new_headertop));
				price_low.setTextColor(Color.BLACK);
				price_top.setTextColor(Color.BLACK);
				px="0";
				currentPage=0;
				adapter.clear();
				firstpage(cityId,addkey,px);
				scrollPage(cityId,addkey,px);
			}
		});
	}
	//获取条件数据
	private void getQueryData(){
		cityId=this.getIntent().getStringExtra("cityId");
		hname=this.getIntent().getStringExtra("hname");
		intime=this.getIntent().getStringExtra("intime");
		outtime=this.getIntent().getStringExtra("outtime");
		pricerange=this.getIntent().getStringExtra("pricerange");
		cityName=this.getIntent().getStringExtra("cityName");
		lat=this.getIntent().getDoubleExtra("lat", 0);
		lng=this.getIntent().getDoubleExtra("lng", 0);
		addkey=this.getIntent().getStringExtra("addkey");
		toptitle.setText(cityName);
		city.setText(cityName);
		in.setText("住"+intime);
		out.setText("退"+outtime);
		if(!Util.isNull(hname)){
			hotel_name.setText(hname);
			if(hname.length()>7){
				hotel_name.setTextSize(12);
			}
			hotel_name.setTextColor(Color.parseColor("#cf0908"));
		}
	}
	private void firstpage(String cityId,String addkey,String px){
		asyncLoadData(cityId,addkey,px);
	}
	private void scrollPage(final String cityid,final String addkey,final String px){
		hotel_list.setOnScrollListener(new OnScrollListener() {
			int lastItem;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					asyncLoadData(cityid,addkey,px);
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;

			}
		});
	}
	private void asyncLoadData(final String cityid,final String addkey,final String pxs) {
		Util.asynTask(HotelList.this, "", new IAsynTask() {
			@Override  
			public void updateUI(Serializable runData) {
				String result = (String) runData;
				if(Util.isNull(result)){
					all.setOnClickListener(null);
					price_low.setOnClickListener(null);
					price_top.setOnClickListener(null);
				}else{
					jsonToObject(result);
					if(adapter==null){
						adapter=new HotelListAdapter(HotelList.this);
						hotel_list.setAdapter(adapter);
					}
					adapter.setList(list);
					adapter.notifyDataSetChanged();
				}
			}
			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service, Web.GetHotel,
						"cityId=" +cityid+ "&intime="+intime
								+ "&outtime="+outtime + "&page="+(++currentPage)
								+ "&hname="+Util.get(hname)+"&pricerange="+pricerange.split(":")[1].replace("元", "")+"&addkey="+Util.get(addkey)+"&hoteltype="+"&lsid="+""+"&px="+pxs);
				String result = web.getPlan();
				return result;
			}

		});
	}
	protected void jsonToObject(String result) {
		JSONArray jsonArray;
		list.clear();
		try {
			jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject myjObject = jsonArray.getJSONObject(i);
				String hotelid = myjObject.getString("hotelid");
				String hotelname = myjObject.getString("hotelname");
				String hotel_address = myjObject.getString("address");
				String hotel_logo=myjObject.getString("hotellogo");
				String min_jiage=myjObject.getString("min_jiage");
				String xingji=myjObject.getString("xingji");
				String intro=myjObject.getString("intro");
				String lat=myjObject.getString("lat");
				String lng=myjObject.getString("lng");
				HotelItem hotel=new HotelItem();
				hotel.setHotelid(hotelid);
				hotel.setHotelname(hotelname);
				hotel.setAddress(hotel_address);
				hotel.setHotellogo(hotel_logo);
				hotel.setXingji(xingji);
				hotel.setMin_jiage(min_jiage);
				hotel.setIntro(intro);
				hotel.setLat(lat);
				hotel.setLng(lng);
				list.add(hotel);
			}
		} catch (JSONException e) {
		}
	}
	public class HotelListAdapter extends BaseAdapter {
        private Context context;
        private List<HotelItem> list=new ArrayList<HotelItem>();
        private LayoutInflater inflater;
        private Map<Integer,View> map=new HashMap<Integer, View>();
        private RequestQueue mQueue;
        private ImageLoader mImageLoader;
        public HotelListAdapter(Context c){
        	this.context=c;
        	inflater=LayoutInflater.from(c);
        	mQueue = Volley.newRequestQueue(c);
    		mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }
        public void setList(List<HotelItem> list){
        	this.list.addAll(list);
        	this.notifyDataSetChanged();
        }
        private void clear(){
        	this.list.clear();
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
			 return this.list.get(position).hashCode();
		}
		@Override                     
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			HotelItem r=list.get(position);
			if(convertView==null){
				holder=new ViewHolder();
				convertView=inflater.inflate(R.layout.hotel_item, null);
				holder.hotel_image=(ImageView) convertView.findViewById(R.id.hotel_image);
				holder.hotel_name=(TextView) convertView.findViewById(R.id.hotel_name);
				holder.score=(TextView) convertView.findViewById(R.id.score);
				holder.star=(TextView) convertView.findViewById(R.id.star);
				holder.how_far=(TextView) convertView.findViewById(R.id.how_far);
				holder.hotel_price=(TextView) convertView.findViewById(R.id.hotel_price);
//				map.put(position, convertView);
				convertView.setTag(holder);
			}else{
//				convertView=map.get(position);
				holder=(ViewHolder)convertView.getTag();
			}
			holder.hotel_name.setText(r.getHotelname());        
			final HotelItem h=r;
			LatLng start=new LatLng(lat,lng);
			LatLng end=new LatLng(Double.parseDouble(h.getLat()),Double.parseDouble(h.getLng()));
			double distance = AMapUtils.calculateLineDistance(start, end)/1000F;
			distance=Util.getDouble(distance, 2);
			if(lat!=0&&lng!=0){
		      holder.how_far.setText("距离"+h.getHotelname()+distance+"米");
			}
			holder.score.setText("");
			holder.star.setText("["+r.getXingji()+"星级]");
			holder.hotel_price.setText("￥"+r.getMin_jiage());
//			ImageListener listener = ImageLoader.getImageListener(holder.hotel_image, R.drawable.no_get_image,R.drawable.no_get_image);
//	        mImageLoader.get(r.getHotellogo(), listener);
			bmUtils.configDefaultLoadingImage(R.drawable.no_get_image);
			final ImageView img=holder.hotel_image;
			bmUtils.display(holder.hotel_image,r.getHotellogo(),new BitmapLoadCallBack<View>() {
				@Override
				public void onLoadCompleted(View arg0, String arg1,
						Bitmap arg2, BitmapDisplayConfig arg3,
						BitmapLoadFrom arg4) {
					img.setImageBitmap(arg2);
				}            
				@Override
				public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
					img.setImageResource(R.drawable.no_get_image);
				}
				@Override
				public void onPreLoad(View container, String uri,
						BitmapDisplayConfig config) {
					img.setImageResource(R.drawable.no_get_image);
				}
			});
	        convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(context,RoomList.class);
					intent.putExtra("hotelid", h.getHotelid());
					intent.putExtra("hotel_name", h.getHotelname());
					intent.putExtra("hoteladdress", h.getAddress());
					intent.putExtra("checkintime", intime);
					intent.putExtra("checkouttime", outtime);
					intent.putExtra("lat", h.getLat());
					intent.putExtra("lng", h.getLng());
					intent.putExtra("startLat", lat);
					intent.putExtra("startLng", lng);
					intent.putExtra("intro", h.getIntro());
					intent.putExtra("hotellogo", h.getHotellogo());
					intent.putExtra("cityname", cityName);
					context.startActivity(intent);
				}
			});
			return convertView;
		}
	}
	public static class ViewHolder{
		ImageView hotel_image;
		TextView hotel_name;
		TextView score;
		TextView how_far;
		TextView star;
		TextView hotel_price;
	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(data!=null){
    		if(requestCode==300){
    			 cityName=data.getStringExtra("cityName");
        		 cityId=data.getStringExtra("cityId");
        		 NavigateTitle.setText("");
        		 NavigateTitle.setText(cityName);
        		 city.setText(cityName);
        		 list.clear();
        		 adapter.clear();
        		 firstpage(cityId,addkey,px);
 				 scrollPage(cityId,addkey,px);
    		}else if(requestCode==301){
    			if(data.getStringExtra("addkey")!=null||data.getStringExtra("lsid")!=null||data.getStringExtra("name")!=null){
        			addkey=data.getStringExtra("addkey");
        			hname=data.getStringExtra("name");
        			if(!Util.isNull(hname)){
        				hotel_name.setText(hname);
        				hotel_name.setTextColor(Color.parseColor("#cf0908"));
        			}
        			list.clear();
        			adapter.clear();
        			firstpage(cityId,addkey,px);
    				scrollPage(cityId,addkey,px);
        		}else{
        		}
    		}
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
}
