package com.mall.serving.filmticket;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.CommonTicketModel;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.messageboard.MyToast;
public class CommonTicket extends Activity {
	@ViewInject(R.id.common_ticket_list)
	private ListView common_ticket_list;
	@ViewInject(R.id.area)
	private TextView area;
	@ViewInject(R.id.cinema_name)
	private TextView cinema_name;
	private String areaname = "", cinemaname = "", cityid = "", filmid = "";
	private List<CommonTicketModel> list = new ArrayList<CommonTicketModel>();
	private CommonTicketAdapter adapter;
	private User user;
	@ViewInject(R.id.bottom_layout)
	private LinearLayout boLinearLayout;
	@ViewInject(R.id.phone)
	private EditText phone;
	@ViewInject(R.id.submit)
	private TextView submit;
	private CommonTicketModel choosed;
	private int num = 1;
	@ViewInject(R.id.layout_container)
	private LinearLayout layout_container;
	private int index=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_ticket);
		ViewUtils.inject(this);
	}
	@Override
	protected void onStart() {
		if (UserData.getUser() == null) {
			Util.showIntent(this, LoginFrame.class);
		} else {
			num=1;//将数量归一
			index=0;
			user = UserData.getUser();
			this.list.clear();
			if(adapter!=null){
				adapter.list.clear();
				adapter.notifyDataSetChanged();
			}
			init();
		}
		super.onStart();
	}
	private void getIntentData() {
		areaname = this.getIntent().getStringExtra("Area");
		cinemaname = this.getIntent().getStringExtra("CniemaName");
		cityid = this.getIntent().getStringExtra("CityNo");
		filmid = this.getIntent().getStringExtra("FilmNo");
	}

	private void init() {
		Util.initTop(this, "通兑票", Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonTicket.this.finish();
			}
		});
		getIntentData();
		area.setText(areaname+"--"+cinemaname);
		cinema_name.setText(cinemaname);
		loadData();
		submit.setOnClickListener(new OnClickListener() {  
			@Override
			public void onClick(View v) {
				if (choosed != null) {
					System.out.println("选中的兑换票===="+choosed.getCinemaNo()+"    "+choosed.getGoodsName()+"   "+choosed.getGoodsPrice());
					if (!Util.isNull(phone.getText().toString())
							&& Util.checkPhoneNumber(phone.getText().toString())) {
						EditText ed=(EditText) layout_container.getChildAt(index).findViewById(R.id.number);
						writeOrder(choosed.getGoodsID(), phone.getText()
								.toString(), ed.getText().toString()+"", choosed.getGoodsPrice(),
								choosed.getGoodsName(), choosed.getVocherDate());
					} else {
						MyToast.makeText(CommonTicket.this, "请填写正确的手机号", 5)
								.show();
					}
				} else {
					MyToast.makeText(CommonTicket.this, "请选择兑换票", 5).show();
				}
			}
		});
	}
	private void initContainer(){
		layout_container.removeAllViews();
		LayoutInflater inflater=LayoutInflater.from(this);
		ViewHolder vh = null;
		for(int i=0;i<list.size();i++){
			CommonTicketModel ct = list.get(i);
			vh=new ViewHolder();
			final FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.common_ticket_item,null);
			vh.GoodsName = (TextView) convertView.findViewById(R.id.goods_name);
			vh.price = (TextView) convertView.findViewById(R.id.price);
			vh.date = (TextView) convertView.findViewById(R.id.date);
			vh.add = (TextView) convertView.findViewById(R.id.add);
			vh.sub = (TextView) convertView.findViewById(R.id.sub);
			vh.number = (EditText) convertView.findViewById(R.id.number);
			vh.layer = (LinearLayout) convertView.findViewById(R.id.layer);
			vh.GoodsName.setText(ct.getGoodsName());
			vh.price.setText(ct.getGoodsPrice());
			vh.date.setText(ct.getVocherDate());
			if(Util.isNull(ct.getBuynum())){
				vh.number.setText("1");
			}else{
				vh.number.setText(ct.getBuynum());
			}
			final TextView numberT = vh.number;
			vh.number.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					if(!Util.isNull(s)&&!Util.isNull(s.toString())){
						if(Util.isInt(s.toString())){
							num=Integer.parseInt(s.toString());
						}
					}
				}
			});
			vh.add.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					numberAddOrSub(numberT, true);
				}
			});
			vh.sub.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					numberAddOrSub(numberT, false);
				}
			});
			vh.layer.getBackground().setAlpha(50); 
			final LinearLayout lay = vh.layer;
			final CommonTicketModel ctt = ct;
			if (ctt.getChoosed().equals("yes")) {
				lay.setVisibility(View.VISIBLE);
				choosed=ct;
				convertView.setTag("yes");
				index=i;
			} else {
				lay.setVisibility(View.GONE);
				convertView.setTag("no");
			}
			final int ind=i;
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(convertView.getTag().equals("no")){
						lay.setVisibility(View.VISIBLE);
						choosed=ctt;//点击选中
						convertView.setTag("yes");
						noti(ind);
						num=Integer.parseInt(numberT.getText().toString()); 
						index=ind;
					}
				}
			});
		  layout_container.addView(convertView);
		}
	}
	private void noti(int index){
		for(int i=0;i<layout_container.getChildCount();i++){
			if(i!=index){
				FrameLayout f=(FrameLayout) layout_container.getChildAt(i);
				f.setTag("no");
				f.findViewById(R.id.layer).setVisibility(View.GONE);
			}
		}
	}
	private void writeOrder(final String goodsId, final String mobile,
			final String buyNum, final String formPrice,
			final String GoodsName, final String vdate) {
		Util.asynTask(this, "", new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				String result = (String) runData;
				if(result.contains("success:")){
					String price="",order="";
					if(result.split(":").length==3){
						 price=result.split(":")[2];
						 order=result.split(":")[1];
					}
					Intent intent=new Intent(CommonTicket.this,PayCommonTicket.class);
					intent.putExtra("goodsname", choosed.getGoodsName());
					intent.putExtra("totalprice", price);
					intent.putExtra("ordernumber", order);
					CommonTicket.this.startActivity(intent);
				}else{
					MyToast.makeText(CommonTicket.this, "生成失败,"+result, 5).show();
				}
			}
			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service,
						Web.Film_createCommTicketOrder, "goodsId=" + goodsId
								+ "&mobile=" + mobile + "&buyNum=" + buyNum
								+ "&cinemaNo=" + filmid + "&formPrice="
								+ formPrice + "&areaNo=" + cityid
								+ "&GoodsName=" + GoodsName + "&vdate=" + vdate+"&userId="+user.getUserId()+"&md5Pwd="+user.getMd5Pwd());
				return web.getPlan();
			}
		});
	}
	protected void jsonCityToObject(String result) {
		JSONArray json_data_array;
		try {
			JSONObject myjObject = new JSONObject(result);
			String datas = myjObject.getString("Object");
//			System.out.println("result======" + datas);
			json_data_array = new JSONArray(datas);
			for (int i = 0; i < json_data_array.length(); i++) {
				JSONObject obj = json_data_array.getJSONObject(i);
				String GoodsID = obj.getString("GoodsID");
				String GoodsName = obj.getString("GoodsName");
				String GoodsPrice = obj.getString("GoodsPrice");
				String CinemaNo = obj.getString("CinemaNo");
				String VoucherDate = obj.getString("VoucherDate");
				String WebMemo = obj.getString("WebMemo");

				CommonTicketModel cm = new CommonTicketModel();
				cm.setGoodsID(GoodsID);
				cm.setGoodsName(GoodsName);
				cm.setGoodsPrice(GoodsPrice);
				cm.setCinemaNo(CinemaNo);
				cm.setVocherDate(VoucherDate);
				cm.setWebMemo(i+"");
				//将得到的数据第一个设为选中状态
				if (i == 0) {
					cm.setChoosed("yes");
				} else {
					cm.setChoosed("no");
				}
				list.add(cm);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private void loadData() {
		Util.asynTask(this, "", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				String result = runData + "";        
				if (Util.isNull(result)) {
					MyToast.makeText(CommonTicket.this, "未获取到数据", 5).show();
				} else {
					jsonCityToObject(result);
				}
//				if (adapter == null) {
//					adapter = new CommonTicketAdapter(CommonTicket.this);
//					common_ticket_list.setAdapter(adapter);
//				}
//				adapter.setList(list);
				if(list.size()>0){
					initContainer();           
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service,
						Web.Film_getCommTickets, "areaNo=" + cityid
								+ "&cinemaNo=" + filmid);
				String result = web.getPlan();
				return result;
			}
		});
	}
	public class CommonTicketAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater inflater;
		public List<CommonTicketModel> list = new ArrayList<CommonTicketModel>();
        private boolean ischoosed=true;
		public CommonTicketAdapter(Context c) {
			this.context = c;
			inflater = LayoutInflater.from(c);
		}

		public void setList(List<CommonTicketModel> list) {
			this.list = list;
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder vh = null;
			CommonTicketModel ct = this.list.get(position);
			
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = inflater.inflate(R.layout.common_ticket_item,null);
				vh.GoodsName = (TextView) convertView.findViewById(R.id.goods_name);
				vh.price = (TextView) convertView.findViewById(R.id.price);
				vh.date = (TextView) convertView.findViewById(R.id.date);
				vh.add = (TextView) convertView.findViewById(R.id.add);
				vh.sub = (TextView) convertView.findViewById(R.id.sub);
				vh.number = (EditText) convertView.findViewById(R.id.number);
				vh.layer = (LinearLayout) convertView.findViewById(R.id.layer);
				convertView.setTag(vh);
				convertView.setTag(-7, "one");
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.GoodsName.setText(ct.getGoodsName());
			vh.price.setText(ct.getGoodsPrice());
			vh.date.setText(ct.getVocherDate());
			if(Util.isNull(ct.getBuynum())){
				vh.number.setText("1");
			}else{
				vh.number.setText(ct.getBuynum());
			}
			//键盘消失，notify，这是一个问题
			final EditText et=vh.number;
			final TextView numberT = vh.number;
			vh.add.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					numberAddOrSub(numberT, true);
				}
			});
			vh.sub.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					numberAddOrSub(numberT, false);
				}
			});
			vh.layer.getBackground().setAlpha(50);
			final LinearLayout lay = vh.layer;
			final CommonTicketModel ctt = ct;
			if (ctt.getChoosed().equals("yes")) {
				lay.setVisibility(View.VISIBLE);
				choosed=ct;
			} else {
				lay.setVisibility(View.GONE);
			}
			// 让默认的第一个显示出来,点击后选择,更新数据
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
						if (ctt.getChoosed().equals("yes")) {
							lay.setVisibility(View.VISIBLE);
							ctt.setChoosed("no");
							newList(list, position);
							choosed=null;
//							notifyDataSetChanged();
						} else if (ctt.getChoosed().equals("no")) {
							lay.setVisibility(View.GONE);
							ctt.setChoosed("yes");
							choosed=ctt;
							newList(list, position);
//							notifyDataSetChanged();
						}
			     }
			});
			return convertView;
		}
        private void checkChoosed(CommonTicketModel ctt){
        	for(int i=0;i<this.list.size();i++){
        		if(this.list.get(i).getChoosed().equals("yes")){
        			ischoosed=true;
        		}else{
        			ischoosed=false;
        		}
        	}
        }
		private void newList(List<CommonTicketModel> list, int position) {
			for (int i = 0; i < list.size(); i++) {
				if (i != position) {
					list.get(i).setChoosed("no");
				}
			}
		}
		private void numberAddOrSub(TextView t, boolean sub) {
			int numx = 1;
			if (Util.isInt(t.getText().toString())) {
				numx = Integer.parseInt(t.getText().toString());
				if (sub) {
					num = numx + 1;
					t.setText((numx + 1) + "");
				} else {
					if ((numx - 1) < 1) {
						MyToast.makeText(context, "购买数量应为正整数", 5).show();
					} else {
						num = numx - 1;
						t.setText((numx - 1) + "");
					}
				}
			} else {
				MyToast.makeText(context, "购买数量应为正整数", 5).show();
			}
		}
	}
	private void newList(List<CommonTicketModel> list, int position) {
		for (int i = 0; i < list.size(); i++) {
			if (i != position) {
				list.get(i).setChoosed("no");
			}
		}
	}
	private void numberAddOrSub(TextView t, boolean sub) {
		int numx = 1;
		if (Util.isInt(t.getText().toString())) {
			numx = Integer.parseInt(t.getText().toString());
			if (sub) {
				num = numx + 1;
				t.setText((numx + 1) + "");
			} else {
				if ((numx - 1) < 1) {
					MyToast.makeText(this, "购买数量应为正整数", 5).show();
				} else {
					num = numx - 1;
					t.setText((numx - 1) + "");
				}
			}
		} else {
			MyToast.makeText(this, "购买数量应为正整数", 5).show();
		}
	}
	public class ViewHolder {
		TextView GoodsName;
		TextView price;
		TextView date;
		TextView add;
		TextView sub;
		EditText number;
		LinearLayout layer;
	}
}
