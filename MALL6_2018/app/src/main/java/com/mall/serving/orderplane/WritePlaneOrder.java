package com.mall.serving.orderplane;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.PlanePeople;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.voip.view.myswitchbutton.SwitchButton;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.CheckPAndI;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class WritePlaneOrder extends Activity {


	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.tuigaifei)
	private TextView tuigaifei;
	private String cardtypenum="1";
	private SwitchButton baoxiao;
	private TextView start_airport, landing_airport, start_time, end_time,
	start_date, end_date, ticket_type;
	private TextView start_airport_two, landing_airport_two, start_time_two, end_time_two,start_date_two, end_date_two, ticket_type_two,total_price_two, item_price_two,ticket_cost_detail_two;
	private TextView total_price, item_price, ticket_cost_detail, open,
	displine;
	private LinearLayout plane_ticket_info_layout;
	private ScrollView scroll;
	private float originalY;
	private String note, takeoffairport, landingairport, takeofftime,
	landingtime, startdate, enddate, tickettype, totalprice, itemprice,
	mobile = "", flightno = "", planemode = "", AirConFee = "",city_from,city_to;
	// 联系人信息
	private String cst_com = "";// 联系人姓名
	public String cst_link_tel = "";// 联系人固定电话
	public String lnk_fax = "";// 联系人传真号码
	public String lnk_email = "";// 联系人邮箱
	public String istype = "1";// 不知道?????????????
	private String per_name_a = "";//
	// 乘机人信息
	private String per_ischild_a = "";// 机票类型：成人（儿童）
	public String per_card_type_id_a = "";// 证件类型
	public String per_cardno_a = "";// 证件号码
	public String per_phone_a = "";// 乘机人手机号码
	public String per_birday_a = "1";// 乘机人生日
	public String per_bx_a = "";// 乘机人保险份数
	// 取票方式选择         
	public String col_consignee = "";// 收件人姓名
	public String col_tel = "";
	public String col_addr = "";
	public String col_code = "";
	public String col_post = "快递";//
	public String snum = "1";
	public String oter = "";//
	private String param = "";
	public String pram2 = "";
	public String pram1 = "";
	private String userId="";
	private String md5Pwd="";
	private User user;
	private String cardtype = "身份证";
	private int goumaiBX;//是否购买保险
	int total,oneprice,lastprice=0;
	// 订单需要的参数
	private String  FuelTax = "", CabName = "" ;
	private TextView addpeople;
	private SharedPreferences sp;
	private List<PlanePeople> choosedList = new ArrayList<PlanePeople>();
	private List<PlanePeople> allPeopleList = new ArrayList<PlanePeople>();
	private String choosedPeople = "", allpople = "";
	private LinearLayout peoplecontainer;
	private LayoutInflater iflat;
	private Button writeorder;
	private EditText connect_name,mobileT;
	private EditText col_consigneeT,col_telT,col_addrT;
	private boolean needBaoxiaoOrNot=false;
	private LinearLayout baoxiaolayout;
	private TextView b_total_price; 
	//返程
	private List<ImageView> cards = new ArrayList<ImageView>();
	private LinearLayout combackLayout;
	private String planetype="单程";
	private double doubletotal=0.00,onedoubletotal=0.00;
	private SwitchButton baoxian;
	private TextView shenfenzhenghao;
	private TextView sfzh_but2;
	private TextView birthday;
	private EditText connect_shengfen;
	private LinearLayout birthdaylayout;
	private LinearLayout sfzhLine;
	private boolean avalibility;
	private TextView changyong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_plane_order);
		ViewUtils.inject(this);
		sp = this.getSharedPreferences("planepeolelist", 0);
		iflat = LayoutInflater.from(this);
		init();
	}
	private void init() {
		user=new UserData().getUser();
		if(user==null){
			Util.showIntent(this, LoginFrame.class);
		}else{
			userId=user.getUserId();
			md5Pwd=user.getMd5Pwd();  
		}
		//		Util.initTop(WritePlaneOrder.this, "订单填写", 0, new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				WritePlaneOrder.this.finish();
		//			}
		//		}, null);

		top_left.setVisibility(View.VISIBLE);
		top_center.setText("订单填写");

		initView();
		getIntentData();
		getPeopleData();
	}
	private void getData(String people, List<PlanePeople> list) {
		if (Util.isNull(people)) {
			return;
		} else {
			String[] peopleArray = people.split(",,,,");
			for (int i = 0; i < peopleArray.length; i++) {
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
				if(cardty.equals("身份证")){
					p.setCardtype("1");
				}else if(cardty.equals("护照")){
					p.setCardtype("2");
				}else if(cardty.equals("其他")){
					p.setCardtype("3");
				}else{
					p.setCardtype(cardty);
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
	private void checkShengFengzheng(final String cardno){
		if (CheckPAndI.isIdentityNo(cardno.trim(), WritePlaneOrder.this)) {

		}else{
			Toast.makeText(WritePlaneOrder.this, "身份证号码格式不正确",Toast.LENGTH_LONG).show();
			return;
		}
	}
	@SuppressLint("WrongViewCast")
	private void initView() {
		start_airport = (TextView) this.findViewById(R.id.take_off_airport);
		birthday = (TextView) this.findViewById(R.id.birthday);
		sfzh_but2 = (TextView) this.findViewById(R.id.sfzh_but2);
		landing_airport = (TextView) this.findViewById(R.id.landing_airport);
		start_time = (TextView) this.findViewById(R.id.take_off_time);
		//end_time = (TextView) this.findViewById(R.id.landing_time);
		start_date = (TextView) this.findViewById(R.id.start_date);
		end_date = (TextView) this.findViewById(R.id.end_date);
		ticket_type = (TextView) this.findViewById(R.id.ticket_type);
		total_price = (TextView) this.findViewById(R.id.total_price);
		birthdaylayout = (LinearLayout) this.findViewById(R.id.birthdaylayout);
		item_price = (TextView) this.findViewById(R.id.item_price);
		changyong = (TextView) this.findViewById(R.id.changyong);
		ticket_cost_detail = (TextView) this.findViewById(R.id.ticket_cost_detail);
		open = (TextView) this.findViewById(R.id.open);
		//		displine = (TextView) this.findViewById(R.id.displine);
		//		discipline_layout = (LinearLayout) this.findViewById(R.id.discipline_layout);
		plane_ticket_info_layout = (LinearLayout) this.findViewById(R.id.plane_ticket_info_layout);

		//乘机人信息
		connect_name=(EditText) this.findViewById(R.id.connect_name);
		connect_shengfen=(EditText) this.findViewById(R.id.connect_shengfen);
		mobileT=(EditText) this.findViewById(R.id.mobile);
		col_consigneeT=(EditText) this.findViewById(R.id.col_consignee);
		col_telT=(EditText) this.findViewById(R.id.col_tel);
		col_addrT=(EditText) this.findViewById(R.id.col_addr);
		baoxiao = (SwitchButton) this.findViewById(R.id.baoxiao);
		baoxian = (SwitchButton) this.findViewById(R.id.baoxian);
		baoxiaolayout=(LinearLayout) this.findViewById(R.id.baoxiaolayout);
		sfzhLine=(LinearLayout) this.findViewById(R.id.sfzhLine);
		b_total_price=(TextView) this.findViewById(R.id.b_total_price);               
		baoxiao.setChecked(true);       
		baoxiao.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					needBaoxiaoOrNot=false;
					baoxiaolayout.setVisibility(View.GONE);
				}else{
					needBaoxiaoOrNot=true;
					baoxiaolayout.setVisibility(View.VISIBLE);

				}

			}
		});
		changyong.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(WritePlaneOrder.this,PlanePeopleList.class);
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
				WritePlaneOrder.this.startActivity(intent);
				
			}
		});
		sfzh_but2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						WritePlaneOrder.this);
				LayoutInflater inflater = LayoutInflater.from(WritePlaneOrder.this);
				View view = inflater.inflate(R.layout.plane_add_people_card_type,
						null);
				cards.clear();
				ImageView sfz, hz, qt;
				sfz = (ImageView) view.findViewById(R.id.sfz);
				hz = (ImageView) view.findViewById(R.id.hz);
				qt = (ImageView) view.findViewById(R.id.qt);
				cards.add(sfz);
				cards.add(hz);
				cards.add(qt);
				sfz.setOnClickListener(new ZJRadioButtonOnClick(0));
				hz.setOnClickListener(new ZJRadioButtonOnClick(1));
				qt.setOnClickListener(new ZJRadioButtonOnClick(2));
				Button submit = (Button) view.findViewById(R.id.submit);
				builder.setCancelable(false);
				final Dialog dialog;
				dialog = builder.show();
				Window window = dialog.getWindow();
				WindowManager.LayoutParams pa = window.getAttributes();
				pa.width = Util.getScreenSize(WritePlaneOrder.this).getWidth()/5*4;
				dialog.setContentView(view, pa);
				submit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});


			}
		});
		baoxian.setOnCheckedChangeListener(new OnCheckedChangeListener() {


			private int num;

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				double money = Double.parseDouble(b_total_price.getText().toString().replace("￥", "").replace("元", ""));
				if(allPeopleList.size()!=0){
					num = allPeopleList.size();
				}else{
					num =1;
				}
				if(!isChecked){//选中
					goumaiBX=1;
					money=money+20*num;

				}else{//不限中
					money=money-20*num;
					Log.i("tag", "---money2--"+money);
					goumaiBX=0;
				}
				b_total_price.setText("￥ "+(money+"").replace(".0", "")+"元");
			}
		});
		tuigaifei.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				VoipDialog dialo =new VoipDialog(note, WritePlaneOrder.this, "确定", null, null, null);
				dialo.show();

			}
		});
		plane_ticket_info_layout.setTag("opened");
		//discipline_layout.setTag("closed");
		//		open.setOnClickListener(new OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				if (discipline_layout.getTag().equals("closed")) {
		//					discipline_layout.setVisibility(View.VISIBLE);
		//					discipline_layout.setTag("open");
		//				} else {
		//					discipline_layout.setVisibility(View.GONE);
		//					discipline_layout.setTag("closed");
		//				}
		//			}
		//		});
		scroll = (ScrollView) this.findViewById(R.id.scroll);
		scroll.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					originalY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					float newY = event.getY();
					int distance = (int) (newY - originalY);
					if (plane_ticket_info_layout.getTag().equals("closed")
							) {
						// 完全显示下面的ScrollView
						// 向下拉-----------50f or >150f
						if (Math.abs(distance) < 150) {
							scroll.scrollBy(0, distance);
						}
					} else if (plane_ticket_info_layout.getTag().equals(
							"opened")
							) {
						if (Math.abs(distance) < 150) {
							scroll.scrollBy(0, distance);
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					float y = event.getY();
					int distances = (int) (y - originalY);
					if (plane_ticket_info_layout.getTag().equals("closed")
							) {
						// 完全显示下面的ScrollView
						// 向下拉-----------50f or >150f
						if (distances > 150) {
							// 展开Layout
							plane_ticket_info_layout
							.setVisibility(View.VISIBLE);
							plane_ticket_info_layout.setTag("opened");
						}
					} else if (plane_ticket_info_layout.getTag().equals(
							"opened")
							) {
						if (distances > 150) {
							// 展开Layout

						}

						if (distances < -150) {
							// 展开Layout
							plane_ticket_info_layout.setVisibility(View.GONE);
							plane_ticket_info_layout.setTag("closed");
						}

					} else if (plane_ticket_info_layout.getTag().equals(
							"opened")
							) {
						if (distances < -150) {
							// 展开Layout

						}
					}
					break;
				}
				return true;
			}
		});
		addpeople = (TextView) this.findViewById(R.id.addpeople);
		addpeople.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(allPeopleList.size()<9){
					System.out.println("保存下来的planepeople=============="+sp.getString("planepeople", ""));
					if (Util.isNull(sp.getString("planepeople", ""))) {
						Intent intent=new Intent(WritePlaneOrder.this,AddPeople.class);
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
						WritePlaneOrder.this.startActivity(intent);
					}
					if (!Util.isNull(sp.getString("planepeople", ""))) {
						Intent intent=new Intent(WritePlaneOrder.this,PlanePeopleList.class);
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
						WritePlaneOrder.this.startActivity(intent);
					}
				}else{
					if (!Util.isNull(sp.getString("planepeople", ""))) {
						Intent intent=new Intent(WritePlaneOrder.this,AddPeople.class);
						WritePlaneOrder.this.startActivity(intent);
					}
				}
			}
		});
		peoplecontainer = (LinearLayout) this.findViewById(R.id.people_container);
		writeorder = (Button) this.findViewById(R.id.writeorder);
		writeorder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mobile=mobileT.getText().toString();
				cst_com=connect_name.getText().toString();

				if(Util.isNull(mobile)||!Util.checkPhoneNumber(mobile)){
					Toast.makeText(WritePlaneOrder.this, "联系人手机号码格式不正确", Toast.LENGTH_LONG).show();
					return;
				}
				if(Util.isNull(cst_com)){
					Toast.makeText(WritePlaneOrder.this, "联系人姓名不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				if(needBaoxiaoOrNot){
					col_consignee=col_consigneeT.getText().toString();
					col_tel=col_telT.getText().toString();
					col_addr=col_addrT.getText().toString();
				}
				writeOrder();
			}
		});
		//返程
		combackLayout=(LinearLayout) this.findViewById(R.id.comback_layou);
		start_airport_two = (TextView) this.findViewById(R.id.take_off_airport_two);
		landing_airport_two = (TextView) this.findViewById(R.id.landing_airport_two);
		start_time_two= (TextView) this.findViewById(R.id.take_off_time_two);
		end_time_two = (TextView) this.findViewById(R.id.landing_time_two);
		start_date_two = (TextView) this.findViewById(R.id.start_date_two);
		end_date_two = (TextView) this.findViewById(R.id.end_date_two);
		ticket_type_two = (TextView) this.findViewById(R.id.ticket_type_two);
		total_price_two = (TextView) this.findViewById(R.id.total_price_two);
		item_price_two = (TextView) this.findViewById(R.id.item_price_two);
		ticket_cost_detail_two=(TextView) this.findViewById(R.id.ticket_cost_detail_two);
	}
	public class ZJRadioButtonOnClick implements OnClickListener {
		private int pos;

		public ZJRadioButtonOnClick(int pos) {
			this.pos = pos;
			cardtypenum=pos+"";
		}

		@Override
		public void onClick(View v) {
			ImageView view = (ImageView) v;
			if (view.getTag().toString().equals("noselected")) {
				view.setImageResource(R.drawable.radiobutton_down);
				view.setTag("selected");
			} 
			for (int i = 0; i < cards.size(); i++) {
				if (i != pos) {
					ImageView imageview = cards.get(i);
					imageview.setImageResource(R.drawable.radiobutton_up);
					imageview.setTag("noselected");
				}
			}
			if (pos == 0) {
				cardtype = "身份证";

				birthdaylayout.setVisibility(View.GONE);
			} else if (pos == 1) {
				cardtype = "护照";
				birthdaylayout.setVisibility(View.VISIBLE);
			} else if (pos == 2) {
				cardtype = "其他";
				birthdaylayout.setVisibility(View.VISIBLE);
			}
			sfzh_but2.setText(cardtype);

		}
	}
	private void getPeopleData(){
		choosedPeople = sp.getString("choosedpeople", "");
		allpople = sp.getString("planepeople", "");
		peoplecontainer.removeAllViews();
		choosedList.clear();
		allPeopleList.clear();
		getData(choosedPeople, choosedList);
		getData(allpople, allPeopleList);
		for (int i = 0; i < choosedList.size(); i++) {
			//			System.out.println("---------------------choosedlist.size()====="+choosedList.size());
			final PlanePeople p = choosedList.get(i);
			LinearLayout l = (LinearLayout) iflat.inflate(R.layout.plane_people_onboard, null);
			TextView na = (TextView) l.findViewById(R.id.nameandage);
			TextView zjhm = (TextView) l.findViewById(R.id.zjhm);
			ImageView delte = (ImageView) l.findViewById(R.id.delete);
			na.setText(p.getName() + "   " + p.getAge());
			zjhm.setText(p.getCarnum());
			connect_name.setText(choosedList.get(0).getName());
			mobileT.setText(choosedList.get(0).getPhone());
			per_name_a+=p.getName()+",";
			per_card_type_id_a+=p.getCardtype()+",";
			per_cardno_a+=p.getCarnum()+",";
			per_phone_a+=p.getPhone()+",";
			per_birday_a+=p.getBirthday()+",";
			per_ischild_a+="1,";
			per_bx_a+=p.getBaoxian()+",";			
			if(planetype.equals("双程")){
				lastprice+=onedoubletotal;
			}else{
				lastprice+=oneprice;
			}
			System.out.println("lastprice======"+lastprice);
			if(per_bx_a.contains("1")){
				goumaiBX=1;
				if(planetype.equals("双程")){
					lastprice+=40;
				}else{
					lastprice+=20;
				}
			}
			//			System.out.println("总价格===="+lastprice);
			b_total_price.setText("￥ "+lastprice+"元");
			delte.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					System.out.println("删除前的长度"+allPeopleList.size());
					for(int i=0;i<allPeopleList.size();i++){
						if(allPeopleList.get(i).getName().contains(p.getName())){
							allPeopleList.remove(i);
						}
					}
					allPeopleList.remove(p);
					choosedList.remove(p);
					System.out.println("删除后的长度"+allPeopleList.size());
					putDataToSharedPreference();
					getPeopleData();
				}
			});
			l.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(WritePlaneOrder.this,AddPeople.class);
					intent.putExtra("name", p.getName());
					intent.putExtra("agetype", p.getAge());
					intent.putExtra("cardnum", p.getCarnum());
					intent.putExtra("cardtype", p.getCardtype());
					intent.putExtra("baoxian", p.getBaoxian());
					intent.putExtra("birthday", p.getBirthday());
					intent.putExtra("phone", p.getPhone());

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
					//					choosedList.remove(p);
					//					allPeopleList.remove(p);
					putDataToSharedPreference();
					WritePlaneOrder.this.startActivity(intent);
				}
			});
			peoplecontainer.addView(l);
			snum=peoplecontainer.getChildCount()+"";//乘机人的数量
		}
		if(peoplecontainer.getChildCount()==0){
			sfzhLine.setVisibility(View.VISIBLE);
		}


	}
	private void putDataToSharedPreference(){
		StringBuilder builder = new StringBuilder();
		StringBuilder builder2 = new StringBuilder();
		for (int i = 0; i < allPeopleList.size(); i++) {
			PlanePeople p = allPeopleList.get(i);
			builder.append("name=" + p.getName() + ",,,agetype="
					+ p.getAge() + ",,,zjhm=" + p.getCarnum()
					+ ",,,baoxian=" + p.getBaoxian() 
					+ ",,,birthday=" + p.getBirthday() 
					+ ",,,phone=" + p.getPhone()
					+ ",,,cardtype=" + p.getCardtype()
					+ ",,,,");
		}
		for (int i = 0; i < choosedList.size(); i++) {
			PlanePeople p = choosedList.get(i);
			builder2.append("name=" + p.getName() + ",,,agetype="
					+ p.getAge() + ",,,zjhm=" + p.getCarnum()
					+ ",,,baoxian=" + p.getBaoxian() 
					+ ",,,birthday=" + p.getBirthday() 
					+ ",,,phone=" + p.getPhone()
					+ ",,,cardtype=" + p.getCardtype()
					+ ",,,,");
		}
		sp.edit().putString("planepeople", builder.toString()).commit();
		sp.edit().putString("choosedpeople", builder2.toString()).commit();
	}
	private void writeOrder() {
		if(1==goumaiBX){
			per_bx_a="1";
		}else{
			per_bx_a="0";	
		}
		if(Util.isNull(per_card_type_id_a)){
			per_card_type_id_a=cardtypenum;
		}
		if(Util.isNull(per_cardno_a)){
			per_cardno_a=connect_shengfen.getText().toString();
			checkShengFengzheng(per_cardno_a);
		}
		if(Util.isNull(per_name_a)){
			per_name_a=cst_com;
		}
		if(Util.isNull(per_ischild_a)){
			per_ischild_a="1";
		}
		if(Util.isNull(per_phone_a)){
			per_phone_a=mobile;
		}
		if(Util.isNull(per_birday_a)){
			per_birday_a=birthday.getText().toString();
		}
		if(0==Integer.parseInt(snum)){
			snum="1";
		}

		Util.asynTask(WritePlaneOrder.this, "正在生成机票订单", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				String result = (String) runData;
				String pnr="";
				String gassid="";
				System.out.println("机票订单结果===="+result);
				if(result.contains("success")){
					gassid=result.split(":")[1];
					pnr=result.split(":")[2];
					System.out.println(b_total_price.getText().toString().replace("￥", ""));
					Intent intent=new Intent(WritePlaneOrder.this,PayPlaneOrder.class);
					intent.putExtra("startcity", takeoffairport);
					intent.putExtra("landingcity", landingairport);				
					intent.putExtra("seatnum", snum);
					intent.putExtra("flightno", flightno+"");
					intent.putExtra("cabinname", CabName);
					intent.putExtra("takeofftime", takeofftime);
					intent.putExtra("landingtime", landingtime);
					intent.putExtra("con_name", cst_com);
					intent.putExtra("con_phone", mobile);
					intent.putExtra("startdate", startdate);
					intent.putExtra("gassid", gassid);
					intent.putExtra("pnr", pnr);
					WritePlaneOrder.this.startActivity(intent);
				}else{
					Toast.makeText(WritePlaneOrder.this, result, Toast.LENGTH_LONG).show();
				}

			}
			@Override
			public Serializable run() {
				String paramter="";
				paramter="userid="+userId+"&md5pwd="+md5Pwd+"&cst_link_mobile=" + mobile + "&cst_com=" + cst_com
						+ "&cst_link_tel=" + cst_link_tel + "&lnk_fax="
						+ lnk_fax + "&lnk_email=" + lnk_email
						+ "&istype=" + istype + "&col_consignee="
						+ col_consignee + "&col_tel=" + col_tel
						+ "&col_addr=" + col_addr + "&col_code="
						+ col_code + "&col_post=" + col_post + "&snum="
						+ snum+ "&per_name_a=" + per_name_a
						+ "&per_ischild_a=" + per_ischild_a
						+ "&per_card_type_id_a=" + per_card_type_id_a
						+ "&per_cardno_a=" + per_cardno_a 
						+ "&per_phone_a=" + per_phone_a
						+ "&per_birday_a=" + per_birday_a
						+ "&per_bx_a=" + per_bx_a + "&pram=" + param
						+ "&pram1=" + pram1 + "&pram2=" + pram2
						+ "&oter=" + oter;
				Web web = new Web(Web.convience_service, Web.Ticket_addorder,paramter);
				String result = web.getPlan();
				return result;
			}
		});
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
		itemprice = this.getIntent().getStringExtra("itemprice");
		note = this.getIntent().getStringExtra("note");
		flightno = this.getIntent().getStringExtra("flightno");
		planemode = this.getIntent().getStringExtra("planemode");
		city_from=this.getIntent().getStringExtra("city_from");
		city_to=this.getIntent().getStringExtra("city_to");
		AirConFee = this.getIntent().getStringExtra("AirConFee");
		FuelTax=this.getIntent().getStringExtra("FuelTax");
		param=this.getIntent().getStringExtra("param");

		start_airport.setText(takeoffairport);
		landing_airport.setText("-"+landingairport);
		top_center.setText(takeoffairport+"→"+landingairport);
		start_time.setText(takeofftime);
		//end_time.setText(landingtime);
		start_date.setText(startdate);
		end_date.setText(enddate);
		ticket_type.setText(tickettype);
		item_price.setText(itemprice);
		//displine.setText(note);
		Log.i("tag", "-------------"+totalprice);
		total=(int) (Double.parseDouble(totalprice.replace("￥", ""))+Integer.parseInt(AirConFee)+Integer.parseInt(FuelTax));
		item_price.setText(totalprice);
		oneprice=total;
		//total_price.setText("￥"+total+"元");
		b_total_price.setText("￥"+total+"元");
		ticket_cost_detail.setText("机建:"+"￥"+AirConFee+"元"+"   燃油:￥"+FuelTax);
		System.out.println("拼接后的参数=========="+param);
		if(!Util.isNull(this.getIntent().getStringExtra("planetype"))){
			planetype=this.getIntent().getStringExtra("planetype");
		}
		if(planetype.equals("双程")){
			combackLayout.setVisibility(View.VISIBLE);
			start_airport_two.setText(landingairport);
			landing_airport_two.setText(takeoffairport);
			if(!Util.isNull(this.getIntent().getStringExtra("takeofftimetwo"))){
				start_time_two.setText(this.getIntent().getStringExtra("takeofftimetwo"));
			}
			if(!Util.isNull(this.getIntent().getStringExtra("landingtimetwo"))){
				end_time_two.setText(this.getIntent().getStringExtra("landingtimetwo"));
			}
			if(!Util.isNull(this.getIntent().getStringExtra("startdatetwo"))){
				start_date_two.setText(this.getIntent().getStringExtra("startdatetwo"));
			}
			if(!Util.isNull(this.getIntent().getStringExtra("enddatetwo"))){
				end_date_two.setText(this.getIntent().getStringExtra("enddatetwo"));
			}
			if(!Util.isNull(this.getIntent().getStringExtra("tickettypetwo"))){
				ticket_type_two.setText(this.getIntent().getStringExtra("tickettypetwo"));
			}
			String ariconfee="",fueltax="",totalpricetwo="";
			ariconfee=this.getIntent().getStringExtra("AirConFeetwo");
			fueltax=this.getIntent().getStringExtra("FuelTaxtwo");
			double totaltwo=0;
			if(!Util.isNull(this.getIntent().getStringExtra("totalpricetwo"))){
				totalpricetwo=this.getIntent().getStringExtra("totalpricetwo");
				totaltwo=(int) (Double.parseDouble(totalpricetwo.replace("￥", ""))+Integer.parseInt(ariconfee)+Integer.parseInt(fueltax));
				doubletotal=totaltwo+total;
				onedoubletotal=doubletotal;
				total_price_two.setText("￥"+totaltwo+"元");
				b_total_price.setText("￥"+doubletotal+"元");
			}
			item_price_two.setText(this.getIntent().getStringExtra("totalpricetwo"));
			//ticket_cost_detail_two.setText("机建:"+"￥"+ariconfee+"元"+"   燃油:￥"+fueltax+"  航空意外险  ￥20（可选）");
			ticket_cost_detail_two.setText("机建:"+"￥"+ariconfee+"元"+"   燃油:￥"+fueltax);
		}
	}
}
