package com.mall.serving.resturant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mall.model.Brand;
import com.mall.model.CityBrand;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;

public class ResurantName extends Activity{
	private ImageView top_back;
	private TextView submit;
	private LinearLayout search_history,hot_brand,city_card;
	private EditText search;
	private SharedPreferences sp;
	private String cityid="";
	private ResturantBrandAdapter adapter;
	private ResturantCityCardAdapter cityadapter;
	private ListView listView;
	private List<Brand> brands=new ArrayList<Brand>();
	private List<CityBrand> citybrands=new ArrayList<CityBrand>();
	private List<Brand> list3=new ArrayList<Brand>();
	private List<CityBrand> listCityBrand=new ArrayList<CityBrand>();
	private List<Brand> hengjiBrand=new ArrayList<Brand>();
	private String hengji="";
	private String from="";
	private boolean isbrands=false,iscitybrands=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resturannam);
		sp=this.getSharedPreferences("hengji", 0);
	    hengji=sp.getString("hengji", "");
	    if(!sp.equals("")||sp!=null){
	    	try{
			  parseHengji(hengji);
	    	}catch (Exception e) {
	    		e.printStackTrace();
			}
		}
		getIntentData();
		init();
	}
	private void getIntentData(){
		cityid=this.getIntent().getStringExtra("cityid");
		from=this.getIntent().getStringExtra("from");
	}
	//addkey:-name:速8酒店-lsid:4-[|]addkey:武侯祠/锦里-name:武侯祠/锦里-lsid:-[|]
	private void parseHengji(String hengji){
		String[] br=hengji.split("[|]");
		for(int i=0;i<br.length;i++){
			String[] brandx=br[i].split("-");
			Brand bran=new Brand();
		    bran.setAddKey(brandx[0].replace("addkey", ""));
		    bran.setName(brandx[1].replace("name:", ""));
		    bran.setLsid(brandx[2].replace("lsid:", ""));
		    if(!hengjiBrand.contains(bran)){
		    	 hengjiBrand.add(bran);
		    }
		}
	}
	private void init(){
		listView=(ListView) this.findViewById(R.id.listview);
		top_back=(ImageView) this.findViewById(R.id.top_back);
		hot_brand=(LinearLayout) this.findViewById(R.id.hot_brand);
		search_history=(LinearLayout) this.findViewById(R.id.search_history);
		city_card=(LinearLayout) this.findViewById(R.id.city_card);
		search=(EditText) this.findViewById(R.id.search);
		submit=(TextView) this.findViewById(R.id.submit);
		hot_brand.setVisibility(View.GONE);
		city_card.setVisibility(View.GONE);
		if(from.equals("hotbrand")){
			hot_brand.setVisibility(View.VISIBLE);
			getBrandsData();
		}else if(from.equals("citybrand")){
			city_card.setVisibility(View.VISIBLE);
			getCityMarkData();
		}
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String searcString=search.getText().toString();
				List<Brand> list4=new ArrayList<Brand>(); 
				list4.clear();
				if(!Util.isNull(searcString)){
					for(int i=0;i<list3.size();i++){
						String name=list3.get(i).getName();
						if(name.contains(searcString)){
							list4.add(list3.get(i));
						}
					}
					if(from.equals("hotbrand")){
						if(adapter==null){
							adapter=new ResturantBrandAdapter(ResurantName.this);
						}
						adapter.setList(list4);
						listView.setAdapter(adapter);
					}else if(from.equals("citybrand")){
						if(cityadapter==null){
							cityadapter=new ResturantCityCardAdapter(ResurantName.this);
						}
						List<CityBrand> listcityBrand=new ArrayList<CityBrand>();
						for(int i=0;i<list4.size();i++){
							Brand o = list4.get(i);
							CityBrand c=new CityBrand();
							c.setName(o.getName());
							c.setHotelNum("");
							listcityBrand.add(c);
						}
						cityadapter.setList(listcityBrand);
						listView.setAdapter(cityadapter);
					}
				}else{
					//list3.clear();
					if(from.equals("hotbrand")){
						adapter.setList(list3);
						listView.setAdapter(adapter);
					}else if(from.equals("citybrand")){
						cityadapter.setList(listCityBrand);
						listView.setAdapter(cityadapter);
					}	
				}
			}
		});
		search_history.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//加载搜索历史数据
				LinearLayout la=(LinearLayout) arg0;
				((TextView)la.getChildAt(1)).setVisibility(View.VISIBLE);
				((TextView)hot_brand.getChildAt(1)).setVisibility(View.GONE);
				((TextView)city_card.getChildAt(1)).setVisibility(View.GONE);
				if(adapter==null){
					adapter=new ResturantBrandAdapter(ResurantName.this); 
				}
				adapter.setList(hengjiBrand);
				listView.setAdapter(adapter);
			}
		});
		hot_brand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LinearLayout la=(LinearLayout) arg0;
				((TextView)la.getChildAt(1)).setVisibility(View.VISIBLE);
				((TextView)search_history.getChildAt(1)).setVisibility(View.GONE);
				((TextView)city_card.getChildAt(1)).setVisibility(View.GONE);
				getBrandsData();
			}
		});
		city_card.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//加载城市商圈数据
				LinearLayout la=(LinearLayout) arg0;
				((TextView)la.getChildAt(1)).setVisibility(View.VISIBLE);
				((TextView)hot_brand.getChildAt(1)).setVisibility(View.GONE);
				((TextView)search_history.getChildAt(1)).setVisibility(View.GONE);
			    getCityMarkData();
			}
		});
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ResurantName.this.finish();
			}
		});
		//逻辑：先保存城市地标和酒店名册List，
		//然后在输入框中输入查询关键词和List中的值比较，
		//如果存在则添加到list2中，如果关键词为空，则将其设为list
		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}  
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
			}
			@Override
			public void afterTextChanged(Editable e) {
				String searcString=e.toString();
				List<Brand> list4=new ArrayList<Brand>(); 
				list4.clear();
				if(!Util.isNull(searcString)){
					for(int i=0;i<list3.size();i++){
						String name=list3.get(i).getName();
						if(name.contains(searcString)){
							list4.add(list3.get(i));
						}
					}
					System.out.println("搜索关键词======"+searcString);
					if(from.equals("hotbrand")){
						if(adapter==null){
							adapter=new ResturantBrandAdapter(ResurantName.this);
						}
						System.out.println("list4.size()======="+list4.size());
						adapter.setList(list4);
						listView.setAdapter(adapter);
					}else if(from.equals("citybrand")){
						if(cityadapter==null){
							cityadapter=new ResturantCityCardAdapter(ResurantName.this);
						}
						List<CityBrand> listcityBrand=new ArrayList<CityBrand>();
						for(int i=0;i<list4.size();i++){
							Brand o = list4.get(i);
							CityBrand c=new CityBrand();
							c.setName(o.getName());
							c.setHotelNum("");
							listcityBrand.add(c);
						}
						System.out.println("listcityBrand.size()====="+listcityBrand.size());
						cityadapter.setList(listcityBrand);
						listView.setAdapter(cityadapter);
					}
				}else{
					//list3.clear();
					if(from.equals("hotbrand")){
						System.out.println("原本品牌数目======"+list3.size());
						adapter.setList(list3);
						listView.setAdapter(adapter);
					}else if(from.equals("citybrand")){
						System.out.println("原本品牌数目======"+listCityBrand.size());
						cityadapter.setList(listCityBrand);
						listView.setAdapter(cityadapter);
					}	
				}
			}
		});
	}
	private void getCityMarkData(){
		if(!iscitybrands){
			Util.asynTask(ResurantName.this,"", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					String result=(String) runData;
					parseCityBrandObject(result);
					if(cityadapter==null){
						cityadapter=new ResturantCityCardAdapter(ResurantName.this);
						cityadapter.setList(citybrands);
					}
					listCityBrand.addAll(citybrands);
					for(int i=0;i<citybrands.size();i++){
						Brand b=new Brand();
						b.setName(citybrands.get(i).getName());
						b.setHotelNum(citybrands.get(i).getHotelNum());
						list3.add(b);
					}
					listView.setAdapter(cityadapter);
					iscitybrands=true;
				}
				@Override
				public Serializable run() {
					Web web=new Web(Web.convience_service, Web.getCityLandmarks,"cityid="+cityid);
					String result=web.getPlan();
					return result;
				}
			});
		}else{
			cityadapter.setList(citybrands);
			listView.setAdapter(cityadapter);
		}
	}
	private void getBrandsData(){
		if(!isbrands){
			Util.asynTask(ResurantName.this,"", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					String result=(String) runData;
					parseBrandObject(result);
					if(adapter==null){
						adapter=new ResturantBrandAdapter(ResurantName.this);
					}
					System.out.println("brands的长度======="+brands.size());
					list3.addAll(brands);
					adapter.setList(brands);
					listView.setAdapter(adapter);
					isbrands=true;
				}
				@Override
				public Serializable run() {
					//获取热门品牌
					Web web=new Web(Web.convience_service, Web.getHotBrand,"cityid="+cityid);
					String result=web.getPlan();
					return result;
				}
			});
		}else{
			adapter.setList(brands);
			listView.setAdapter(adapter);
		}
	}
	private void parseBrandObject(String result){
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(result);
			System.out.println(jsonArray.length()+"JSON数组长度");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject myjObject = jsonArray.getJSONObject(i);
				String name=myjObject.getString("name");
				String lsid=myjObject.getString("lsid");
				Brand brand=new Brand();
				brand.setName(name);
				brand.setLsid(lsid);
				brands.add(brand);
			}
		} catch (JSONException e) {
			 e.printStackTrace();
		}
	}
	private void parseCityBrandObject(String result){
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject myjObject = jsonArray.getJSONObject(i);
				String name=myjObject.getString("name");
				String hotelNum=myjObject.getString("hotelnum");
				CityBrand brand=new CityBrand();
				brand.setName(name);
				brand.setHotelNum(hotelNum);
				citybrands.add(brand);
			}
		} catch (JSONException e) {
		}
	}
	public class ResturantCityCardAdapter extends BaseAdapter{
		private List<CityBrand> list=new ArrayList<CityBrand>();
		private LayoutInflater inflater;
		private Context c;
		public ResturantCityCardAdapter(Context c){
			this.c=c;
			inflater=LayoutInflater.from(c);
		}
		public void setList(List<CityBrand> list){
			this.list=list;
			this.notifyDataSetChanged();
		}
		public void clearList(){
			this.list.clear();
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
			CityBrand citybrand=list.get(arg0);
			ViewHolder holder=null;
			if(arg1==null){
				holder=new ViewHolder();
				arg1=inflater.inflate(R.layout.resturant_name_item, null);
				holder.name=(TextView) arg1.findViewById(R.id.name);
				holder.count=(TextView) arg1.findViewById(R.id.count);
				arg1.setTag(holder);
			}else{
				holder=(ViewHolder) arg1.getTag();
			}
			final CityBrand b=citybrand;
			holder.name.setText(citybrand.getName());
			holder.name.setTag(citybrand.getName());
			if(!Util.isNull(citybrand.getHotelNum())){
				holder.count.setText(citybrand.getHotelNum()+"家酒店");
			}else{
				holder.count.setText("");
			}
			arg1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent=new Intent(c,ResturantIndex.class);
					if(!hengji.contains(b.getName())){
						if(!Util.isNull(b.getName())){
							intent.putExtra("addkey", b.getName());
							hengji+="addkey:"+b.getName()+"-";
						}else{
							intent.putExtra("addkey", "");
							hengji+="addkey:"+""+"-";
						}
						if(!Util.isNull(b.getName())){
							intent.putExtra("name", b.getName());
							hengji+="name:"+b.getName()+"-";
						}else{
							intent.putExtra("name","");
						}
						hengji+="lsid:-[|]";//"addkey:天府广场-name:如家酒店-lisd:-[|]" 
						sp.edit().putString("hengji", hengji).commit();
					}
					if(!Util.isNull(b.getName())){
						intent.putExtra("addkey", b.getName());
					}else{
						intent.putExtra("addkey", "");
					}
					if(!Util.isNull(b.getName())){
						intent.putExtra("name", b.getName());
					}else{
						intent.putExtra("name","");
					}
					intent.putExtra("lsid", "");
					setResult(200, intent);
					ResurantName.this.finish();
				}
			});
			return arg1;
		}
		
		
	}
	public class ResturantBrandAdapter extends BaseAdapter{
		private List<Brand> list=new ArrayList<Brand>();
		private LayoutInflater inflater;
		private Context c;
		public ResturantBrandAdapter(Context c){
			this.c=c;
			inflater=LayoutInflater.from(c);
		}
		public void setList(List<Brand> list){
			System.out.println("list.size()====="+this.list.size());
			this.list=list;
			this.notifyDataSetChanged();
		}
		public void clearList(){
			this.list.clear();
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
			Brand brand=list.get(arg0);
			ViewHolder holder=null;
			if(arg1==null){
				holder=new ViewHolder();
				arg1=inflater.inflate(R.layout.resturant_name_item, null);
				holder.name=(TextView) arg1.findViewById(R.id.name);
				holder.count=(TextView) arg1.findViewById(R.id.count);
				arg1.setTag(holder);
			}else{
				holder=(ViewHolder) arg1.getTag();
			}
			holder.name.setText(brand.getName());
			holder.name.setTag(brand.getLsid());
			holder.count.setVisibility(View.GONE);
			final Brand b=brand;
			arg1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent=new Intent(c,ResturantIndex.class);
					intent.putExtra("addkey", "");
                    if(!hengji.contains(b.getName())){
                    	if(!Util.isNull(b.getName())){
    						intent.putExtra("name", b.getName());
    						hengji+="addkey:-name:"+b.getName()+"-";
    					}else{
    						intent.putExtra("name","");
    					}
    					if(!Util.isNull(b.getLsid())){
    						intent.putExtra("lsid", b.getLsid());
    						hengji+="lsid:"+b.getLsid()+"-";
    					}else{
    						intent.putExtra("lsid", "");
    					}
    					hengji+="[|]";
    					sp.edit().putString("hengji", hengji).commit();
					}
                    if(!Util.isNull(b.getName())){
						intent.putExtra("name", b.getName());
					}else{
						intent.putExtra("name","");
					}
					if(!Util.isNull(b.getLsid())){
						intent.putExtra("lsid", b.getLsid());
					}else{
						intent.putExtra("lsid", "");
					}
					setResult(200, intent);
					ResurantName.this.finish();
				}
			});
			return arg1;
		}
		
	}
	public class ResturantHengjiBrandAdapter extends BaseAdapter{
		private List<Brand> list=new ArrayList<Brand>();
		private LayoutInflater inflater;
		private Context c;
		public ResturantHengjiBrandAdapter(Context c){
			this.c=c;
			inflater=LayoutInflater.from(c);
		}
		public void setList(List<Brand> list){
			this.list=list;
			System.out.println("list.size()====="+this.list.size());
			this.notifyDataSetChanged();
		}
		public void clearList(){
			this.list.clear();
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
			Brand brand=list.get(arg0);
			ViewHolder holder=null;
			if(arg1==null){
				holder=new ViewHolder();
				arg1=inflater.inflate(R.layout.resturant_name_item, null);
				holder.name=(TextView) arg1.findViewById(R.id.name);
				holder.count=(TextView) arg1.findViewById(R.id.count);
				arg1.setTag(holder);
			}else{
				holder=(ViewHolder) arg1.getTag();
			}
			holder.name.setText(brand.getName());
			holder.name.setTag(brand.getLsid());
			holder.count.setVisibility(View.GONE);
			final Brand b=brand;
			arg1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent=new Intent(c,ResturantIndex.class);
					if(!Util.isNull(b.getName())){
						intent.putExtra("addkey", b.getAddKey());
					}else{
						intent.putExtra("addkey", "");
					}
					if(!Util.isNull(b.getName())){
						intent.putExtra("name", b.getName());
					}else{
						intent.putExtra("name","");
					}
					if(!Util.isNull(b.getLsid())){
						intent.putExtra("lsid", b.getLsid());
					}else{
						intent.putExtra("lsid", "");
					}
					setResult(200, intent);
					ResurantName.this.finish();
				}
			});
			return arg1;
		}
		
	}
	public class ViewHolder{
		TextView name;
		TextView count;
	}

}
