package com.mall.serving.orderplane;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mall.model.PlanePeople;
import com.mall.util.Util;
import com.mall.view.R;

public class PlanePeopleList extends Activity {
	private ListView listview;
	private TextView people_count;
	private Button submit;
	private SharedPreferences sp;
	private List<PlanePeople> list=new ArrayList<PlanePeople>();
	private PlanePeopleListAdapter adapter;
	private String people="";
	private List<PlanePeople> choosedPeopleList=new ArrayList<PlanePeople>();
	private String note, takeoffairport, landingairport, takeofftime,
	landingtime, startdate, enddate, tickettype, totalprice,
	flightno = "", planemode = "", AirConFee = "",city_from,city_to;  
	// 订单需要的参数
	private String FuelTax = "";

	private String param;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plane_people_list);
		sp=this.getSharedPreferences("planepeolelist", 0);
		people=sp.getString("planepeople", "");
		init();
		getIntentData();
	}
	private void getIntentData() {
		takeoffairport = this.getIntent().getStringExtra("takeoffairport");
		landingairport = this.getIntent().getStringExtra("landingairport");
		takeofftime = this.getIntent().getStringExtra("takeofftime");
		landingtime = this.getIntent().getStringExtra("landingtime");
		startdate = this.getIntent().getStringExtra("startdate");
		enddate = this.getIntent().getStringExtra("enddate");
		tickettype = this.getIntent().getStringExtra("tickettype");
		totalprice = this.getIntent().getStringExtra("totalprice");
		note = this.getIntent().getStringExtra("note");
		flightno = this.getIntent().getStringExtra("flightno");
		planemode = this.getIntent().getStringExtra("planemode");
        city_from=this.getIntent().getStringExtra("city_from");
        city_to=this.getIntent().getStringExtra("city_to");
        AirConFee = this.getIntent().getStringExtra("AirConFee");
        FuelTax=this.getIntent().getStringExtra("FuelTax");
        param=this.getIntent().getStringExtra("param");
	}
	private void init(){
		getData(people);
		choosedPeopleList.addAll(list);
		String righttitle="添加";
		if(list.size()>=9){
			Util.initTop2(PlanePeopleList.this, "常用乘机人", "", new OnClickListener() {
				@Override
				public void onClick(View v) {
					PlanePeopleList.this.finish();
				}
			}, null);
		}else{
			Util.initTop2(PlanePeopleList.this, "常用乘机人", righttitle, new OnClickListener() {
				@Override
				public void onClick(View v) {
					PlanePeopleList.this.finish();
				}
			}, new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(PlanePeopleList.this,AddPeople.class);
					intent.putExtra("takeoffairport", takeoffairport);
					intent.putExtra("landingairport", landingairport);
					intent.putExtra("takeofftime", takeofftime);
					intent.putExtra("landingtime", landingtime);
					intent.putExtra("startdate", startdate);
					intent.putExtra("enddate", enddate);
					intent.putExtra("tickettype",tickettype);
					intent.putExtra("totalprice", totalprice);
					intent.putExtra("itemprice", totalprice);
					intent.putExtra("note",note);
					intent.putExtra("flightno", flightno);
				    intent.putExtra("planemode", planemode);
				    
					intent.putExtra("FuelTax", FuelTax);//燃油
					intent.putExtra("AirConFee", AirConFee);//机场建设
					intent.putExtra("city_from", city_from);
					intent.putExtra("city_to", city_to);
				    intent.putExtra("param", param);
				    PlanePeopleList.this.startActivity(intent);
				}
			});
		}
	    
		initView();
		adapter=new PlanePeopleListAdapter(this);
		adapter.setList(list);
		listview.setAdapter(adapter);
	}
	private void initView(){
		listview=(ListView) this.findViewById(R.id.listview);
		people_count=(TextView) this.findViewById(R.id.people_count);
		submit=(Button) this.findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringBuilder  builder=new StringBuilder();
				for(int i=0;i<choosedPeopleList.size();i++){
					PlanePeople p=choosedPeopleList.get(i);
					builder.append("name=" + p.getName()
							+ ",,,agetype=" + p.getAge()
							+ ",,,zjhm=" + p.getCarnum()
							+ ",,,baoxian=" + p.getBaoxian()
							+ ",,,birthday=" + p.getBirthday() 
							+ ",,,phone=" + p.getPhone()
							+ ",,,cardtype=" + p.getCardtype()
							+ ",,,,");
				}
				sp.edit().putString("choosedpeople", builder.toString()).commit();
				Intent intent=new Intent(PlanePeopleList.this,WritePlaneOrder.class);
				intent.putExtra("takeoffairport", takeoffairport);
				intent.putExtra("landingairport", landingairport);
				intent.putExtra("takeofftime", takeofftime);
				intent.putExtra("landingtime", landingtime);
				intent.putExtra("startdate", startdate);
				intent.putExtra("enddate", enddate);
				intent.putExtra("tickettype",tickettype);
				intent.putExtra("totalprice", totalprice);
				intent.putExtra("itemprice", totalprice);
				intent.putExtra("note",note);
				intent.putExtra("flightno", flightno);
			    intent.putExtra("planemode", planemode);
			    
			    
				intent.putExtra("FuelTax", FuelTax);//燃油
				intent.putExtra("AirConFee", AirConFee);//机场建设
				intent.putExtra("city_from", city_from);
				intent.putExtra("city_to", city_to);
			    intent.putExtra("param", param);
				PlanePeopleList.this.startActivity(intent);
			}
		});
		people_count.setText(list.size()+"");
	}
	private void getData(String people){
		if(Util.isNull(people)){
			return;
		}else{
			String[] peopleArray=people.split(",,,,");
			for(int i=0;i<peopleArray.length;i++){
				String one=peopleArray[i];
				String name=one.split(",,,")[0].split("=")[1];
				String agetype=one.split(",,,")[1].split("=")[1]; 
				String card=one.split(",,,")[2].split("=")[1];
				String boxian=one.split(",,,")[3].split("=")[1];
				String birthday="";
				if(one.split(",,,")[4].split("=").length==2){
					if(Util.isNull(one.split(",,,")[4].split("=")[1])){
						String s=card.substring(6,14);
						 birthday=s.subSequence(0, 4)+"-"+s.subSequence(4, 6)+"-"+s.substring(6);
					}else{
						birthday=one.split(",,,")[4].split("=")[1];
					}
				}else{
					String s=card.substring(6,14);
					birthday=s.subSequence(0, 4)+"-"+s.subSequence(4, 6)+"-"+s.substring(6);
				}
				String phone=one.split(",,,")[5].split("=")[1];
				PlanePeople p=new PlanePeople();
				if(boxian.contains("1")){
					p.setBaoxian("1");
				}else{
					p.setBaoxian("0");
				}
				String cardty="";
				if(one.split(",,,")[6].split("=").length==2){
					if(Util.isNull(one.split(",,,")[6].split("=")[1])){
						cardty="1";
					}else{
						cardty=one.split(",,,")[6].split("=")[1];
					}
				}else{
					cardty="1";
				}
				System.out.println("cardty=========="+cardty);
				if(cardty.equals("身份证")){
					p.setCardtype("1");
				}else if(cardty.equals("护照")){
					p.setCardtype("2");
				}else if(cardty.equals("其他")){
					p.setCardtype("3");
				}
				p.setName(name);
				p.setAge(agetype);
				p.setCarnum(card);
				p.setPhone(phone);
				p.setBirthday(birthday);
				list.add(p);
			}
		}
	}
	public class PlanePeopleListAdapter extends BaseAdapter{
        private Context c;
        private List<PlanePeople> list=new ArrayList<PlanePeople>();
        private LayoutInflater inflater;
        public PlanePeopleListAdapter(Context c){
        	this.c=c;
        	inflater=LayoutInflater.from(c);
        }
        private void setList(List<PlanePeople> list){
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
			PlanePeople p=list.get(position);
			ViewHolder h=null;
			if(convertView==null){
				h=new ViewHolder();
				convertView=inflater.inflate(R.layout.plane_people_list_item, null);
				h.checkbox=(ImageView) convertView.findViewById(R.id.image);
				h.nameAndAge=(TextView) convertView.findViewById(R.id.nameandage);
				h.cardNum=(TextView) convertView.findViewById(R.id.sfz);
				h.birthday=(TextView) convertView.findViewById(R.id.birthday);
				h.baoxian=(TextView) convertView.findViewById(R.id.baoxian);
				convertView.setTag(h);
			}else{
				 h=(ViewHolder) convertView.getTag();
			}
			if(p.getBaoxian().equals("0")){
				h.baoxian.setVisibility(View.GONE);
			}
			final TextView b=h.baoxian;
			final PlanePeople pp=p;
			h.checkbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AnimationUtils aniUtils=new AnimationUtils();
					ImageView image=(ImageView) v;
					if(image.getTag().equals("on")){
						image.setImageResource(R.drawable.ic_launcher);
						image.setTag("off");
						/*Animation animation=aniUtils.loadAnimation(c, R.anim.left_to_right);
						b.setAnimation(animation);
						b.startAnimation(animation);*/
						b.setVisibility(View.INVISIBLE);
						if(choosedPeopleList.contains(pp)){
							choosedPeopleList.remove(pp);
							people_count.setText(choosedPeopleList.size()+"");
						}
					}else if(image.getTag().equals("off")){
						image.setImageResource(R.drawable.ic_launcher);
						image.setTag("on");
//						Animation animation=aniUtils.loadAnimation(c, R.anim.left_to_right);
//						b.setAnimation(animation);
//						b.startAnimation(animation);
						b.setVisibility(View.VISIBLE);
						if(!choosedPeopleList.contains(pp)){
							choosedPeopleList.add(pp);
							people_count.setText(choosedPeopleList.size()+"");
						}
					}
					
				}
			});
//			String s="";
//			if(Integer.parseInt(p.getAge())>12){
//				s="(成人票)";
//			}else{
//				s="(儿童票)";
//			}
			h.nameAndAge.setText(p.getName()+" "+p.getAge());
			h.cardNum.setText("身份证："+p.getCarnum());
			h.birthday.setText("生     日："+p.getBirthday());
			return convertView;
		}
		
	}
	
	public class ViewHolder{
		ImageView checkbox;
		TextView nameAndAge;
		TextView cardNum;
		TextView birthday;
		TextView baoxian;
	}

}
