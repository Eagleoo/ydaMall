package com.mall.officeonline;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.ShopOfficeListModel;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

public class MyStoreOffice extends Activity {
	@ViewInject(R.id.listview)
	private ListView listview;
	private int page = 1;
	private ItemAdapter adapter;
	private PopupWindow distancePopup;
	private BitmapUtils bmUtils;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_store_office);
		ViewUtils.inject(this);
		bmUtils=new BitmapUtils(this);
	}
	@Override
	protected void onStart() {
		super.onStart();
		UserData.setOfficeInfo(null);
		page=1;
		init();
	}
	public void init() {
	    Util.initTop(this, "收藏的空间", Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyStoreOffice.this.finish();
			}
		});
		fisrtpage();
		scrollpage();
	}
	private void fisrtpage() {
		if(adapter!=null){
			adapter.clear();
			adapter.notifyDataSetChanged();
		}
		getOffice();
	}
	private void scrollpage(){
		listview.setOnScrollListener(new OnScrollListener() {
			int lastItem;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					getOffice();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		}); 
	}
	private void getOffice(){
		if(UserData.getUser()==null){
			return;
		}
		Util.asynTask(this, "", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if(runData!=null){
					HashMap<Integer,List<ShopOfficeListModel>> map=(HashMap<Integer, List<ShopOfficeListModel>>) runData;
					List<ShopOfficeListModel> list=map.get(page);
					if(list!=null&&list.size()>0){
						if (adapter == null) {
							adapter = new ItemAdapter(MyStoreOffice.this);
							listview.setAdapter(adapter);
						}
						adapter.setList(list);
						adapter.notifyDataSetChanged();
					}
				}else{
				}
			}
			@Override
			public Serializable run() {
				Web web=new Web(Web.officeUrl,Web.getMyFavoriteOffices,"userId="+UserData.getUser().getUserId()+"&md5Pwd="+UserData.getUser().getMd5Pwd()+"&page="+(page++)+"&size=20");
				List<ShopOfficeListModel> list=web.getList(ShopOfficeListModel.class);
				HashMap<Integer,List<ShopOfficeListModel>> map=new HashMap<Integer, List<ShopOfficeListModel>>();
				map.put(page, list);
				return map;
			}
		});
	}
	public class ItemAdapter extends BaseAdapter {
		public Context c;
		private int _100dp=100;
		private List<ShopOfficeListModel> list = new ArrayList<ShopOfficeListModel>();
		private LayoutInflater inflater;
		private HashMap<Integer, View> map = new HashMap<Integer, View>();

		public ItemAdapter(Context c) {
			this.c = c;
			inflater = LayoutInflater.from(c);
			_100dp=Util.dpToPx(c, 100);
		}

		public void setList(List<ShopOfficeListModel> list) {
			this.list.addAll(list);
			this.notifyDataSetChanged();
		}

		public void clear() {
			map.clear();
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
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder h = null;
			ShopOfficeListModel s=this.list.get(position);
//			if (convertView== null) {
				h = new ViewHolder();
				convertView = inflater.inflate(R.layout.shop_list_item, null);
				h.office_logo = (ImageView) convertView
						.findViewById(R.id.office_logo);
				h.name = (TextView) convertView.findViewById(R.id.name);
				h.member_number = (TextView) convertView
						.findViewById(R.id.member_number);
				h.vip_number = (TextView) convertView
						.findViewById(R.id.vip_number);
				h.sj_number = (TextView) convertView
						.findViewById(R.id.sj_number);
				h.kj_number = (TextView) convertView
						.findViewById(R.id.kj_number);
				h.container = (LinearLayout) convertView
						.findViewById(R.id.container);
				h.more_oper = (ImageView) convertView 
						.findViewById(R.id.more_oper);
				convertView.setTag(h);
				map.put(position, convertView); 
//			}
//				else {
//				convertView = map.get(position);
//				h = (ViewHolder) convertView.getTag();
//			}
			h.container.removeAllViews();
			if(!Util.isNull(s.getmCount())){
				h.member_number.setText(Util.spanGreenWithString("会员数量：", s.getmCount()));
			}else{
				h.member_number.setText(Util.spanGreenWithString("会员数量：", ""));
			}
			if(!Util.isNull(s.getVipMcount())){
				h.vip_number.setText(Util.spanRedWithString("VIP 数量：", s.getVipMcount()));
			}else{
				h.vip_number.setText(Util.spanRedWithString("VIP 数量：", ""));
			}
			if(!Util.isNull(s.getLmsjMerCount())){
				h.sj_number.setText(Util.spanGreenWithString("商家数量：", s.getLmsjMerCount()));
			}else{
				h.sj_number.setText(Util.spanGreenWithString("商家数量：", ""));
			}
			if(!Util.isNull(s.getShopMerCount())){
				h.kj_number.setText(Util.spanRedWithString("空间数量：", s.getShopMerCount()));
			}else{
				h.kj_number.setText(Util.spanRedWithString("空间数量：", ""));
			}
			if(!Util.isNull(s.getOfName())){ 
				h.name.setText(s.getOfName());
			}else{
				if(Util.isNull(s.getUserid())){
					h.name.setText("");
				}else{
					h.name.setText(s.getUserid());
				}
				
			}
			final ShopOfficeListModel ss=s;
			h.more_oper.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getPopupWindow();
					startOfficeItemPopupWindow(ss.getUnum(),ss.getId(),ss);
					distancePopup.showAsDropDown(v);//但是因为缓存，所以View的显示位置会错乱
				}
			});
			String url="";
			final ImageView img=h.office_logo;
			if(!Util.isNull(s.getLogo())){
				if(s.getLogo().contains("http://")){
					url=s.getLogo();
				}else{  
					url="http://"+Web.webImage+s.getLogo();
				}
				Resources r = MyStoreOffice.this.getResources();
				InputStream is = r.openRawResource(R.drawable.no_get_image);
				BitmapDrawable bmpDraw = new BitmapDrawable(is);
				Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(),
						_100dp, _100dp);
				bmUtils.configDefaultLoadingImage(Util.toRoundCorner(zoomBm,10));
				bmUtils.display(img, url, new BitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						img.setImageBitmap(Util.toRoundCorner(Util.zoomBitmap(arg2, _100dp, _100dp),10));
					}
					@Override
					public void onLoadFailed(View arg0, String arg1,
							Drawable arg2) {
						Resources r = MyStoreOffice.this.getResources();
						InputStream is = r.openRawResource(R.drawable.no_get_image);
						BitmapDrawable bmpDraw = new BitmapDrawable(is);
						Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(),
								_100dp, _100dp);
						img.setImageBitmap(Util.toRoundCorner(zoomBm,10));
					}
				});
			}else{
				Resources r = MyStoreOffice.this.getResources();
				InputStream is = r.openRawResource(R.drawable.no_get_image);
				BitmapDrawable bmpDraw = new BitmapDrawable(is);
				Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(),
						_100dp, _100dp);
				img.setImageBitmap(Util.toRoundCorner(zoomBm,10));
			}
			int crown=0;
			int sun=0;
			int moon=0;
			int star=0;
			if(!Util.isNull(s.getCrown())){
				crown=Integer.parseInt(s.getCrown());
			}
			if(!Util.isNull(s.getSun())){
				sun=Integer.parseInt(s.getSun());
			}
			if(!Util.isNull(s.getMonth())){
				moon=Integer.parseInt(s.getMonth());
			}
			if(!Util.isNull(s.getStar())){
				star=Integer.parseInt(s.getStar());
			}
			clacluate(h.container,c,crown,sun,moon,star);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(MyStoreOffice.this,ShopOfficeFrame.class);
	                intent.putExtra("userNo", ss.getUnum());
	                intent.putExtra("name", ss.getOfName());
	                intent.putExtra("offid", ss.getUserid());
	                intent.putExtra("from", "list");
	                MyStoreOffice.this.startActivity(intent);
				}
			});
			return convertView;
		}
		private void clacluate(LinearLayout Layout,Context c,int crown,int sun,int moon,int star){
			//如果小于5个心。则有灰色的心，如果有钻石，则无灰色心
			int size=crown+sun+moon+star;
			int[] ione=new int[size];
			for(int i=0;i<crown;i++){
				ione[i]=R.drawable.hg;//ione[0]=hg
			}
			for(int i=0;i<sun;i++){
				ione[i+crown]=R.drawable.hg;//ione[1]=sun,ion[2]=sun
			}       
			for(int i=0;i<moon;i++){
				ione[i+crown+sun]=R.drawable.diamond;
			}
			for(int i=0;i<star;i++){
				ione[i+crown+sun+moon]=R.drawable.red_heart;
			}
			addIndexImage(Layout,ione);
		}
		private void addIndexImage(LinearLayout Layout,int[] res){
			LinearLayout.LayoutParams l=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			l.setMargins(0, 0, 0, 0);
			int[] rank=res;
			for(int i=0;i<rank.length;i++){
				ImageView img=new ImageView(c);
				img.setLayoutParams(l);
				img.setImageResource(rank[i]);
				Layout.addView(img);
			}
		}
	}
	public class ViewHolder {
		ImageView office_logo;
		TextView name;
		TextView member_number;
		TextView vip_number;
		TextView sj_number;
		TextView kj_number;
		LinearLayout container;
		ImageView more_oper;

	}
	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
	}
	private void store(final String officesId,final ShopOfficeListModel m){
		if(UserData.getUser()!=null){
			final User user = UserData.getUser();
			Util.asynTask(MyStoreOffice.this,"",new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if(runData!=null){
						if("success".equals(runData+"")){
							Toast.makeText(MyStoreOffice.this, "收藏成功", Toast.LENGTH_LONG).show();
							m.setSc("1");
							adapter.notifyDataSetChanged();
						}
					}else{
						
					}
				}
				
				@Override
				public Serializable run() {
					Web web=new Web(Web.officeUrl, Web.favoriteOffices,"userId="+user.getUserId()+"&md5Pwd="+user.getMd5Pwd()+"&officesId="+officesId);
					return web.getPlan();
				}
			});
		}
	}
	public void deleteStore(final String officesId,final ShopOfficeListModel m){
		if(UserData.getUser()!=null){
			final User user = UserData.getUser();
			Util.asynTask(MyStoreOffice.this,"",new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if(runData!=null){
						if("success".equals(runData+"")){
							Toast.makeText(MyStoreOffice.this, "取消收藏成功", Toast.LENGTH_LONG).show();
							m.setSc("0");
							adapter.notifyDataSetChanged();
						}
					}else{
						
					}
				}
				
				@Override
				public Serializable run() {
					Web web=new Web(Web.officeUrl, Web.deletefavoriteOffices,"userId="+user.getUserId()+"&md5Pwd="+user.getMd5Pwd()+"&officesId="+officesId);
					return web.getPlan();
				}
			});
		}
	}
	/**
	 * 新建一个popupwindow实例
	 * 
	 * @param view
	 */
	private void initpoputwindow(View view) {
		distancePopup = new PopupWindow(view,
				android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.FILL_PARENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		distancePopup.setAnimationStyle(R.style.popupanimationupanddown);
	}
	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startOfficeItemPopupWindow(final String userNo,final String office_id,final ShopOfficeListModel m) {
		View pview = getLayoutInflater().inflate(
				R.layout.shop_office_list_item_popup, null, false);
		LinearLayout pop_layout=(LinearLayout) pview.findViewById(R.id.pop_layout);
		TextView store = (TextView) pview.findViewById(R.id.store);
		TextView message = (TextView) pview.findViewById(R.id.message);
		TextView in_office = (TextView) pview.findViewById(R.id.in_office);
//		if(!Util.isNull(m.getSc())&&Integer.parseInt(m.getSc())>0){
			store.setText("已收藏");
			store.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					distancePopup.dismiss();
					deleteStore(office_id,m);
				}
			});
			store.setTextColor(Color.parseColor("#49afef"));
			store.setBackgroundColor(Color.parseColor("#ddf0fe"));
//	}else{
//		store.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				distancePopup.dismiss();
//				store(office_id,m);
//   			}
//		});
//	}
		message.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			} 
		});
		in_office.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				distancePopup.dismiss();
				int crown=0;
				int sun=0;
				int moon=0;
				int star=0;
				if(!Util.isNull(m.getCrown())){
					crown=Integer.parseInt(m.getCrown());
				}
				if(!Util.isNull(m.getSun())){
					sun=Integer.parseInt(m.getSun());
				}
				if(!Util.isNull(m.getMonth())){
					moon=Integer.parseInt(m.getMonth());
				}
				if(!Util.isNull(m.getStar())){
					star=Integer.parseInt(m.getStar());
				}
				distancePopup.dismiss();
                Intent intent=new Intent(MyStoreOffice.this,ShopOfficeFrame.class);
                intent.putExtra("userNo", userNo);
                intent.putExtra("name", m.getOfName());
                intent.putExtra("crown", crown);
                intent.putExtra("sun", sun);
                intent.putExtra("moon", moon);
                intent.putExtra("star", star);
                intent.putExtra("offid", m.getUserid());
                intent.putExtra("from", "list");
                MyStoreOffice.this.startActivity(intent);
			}
		});
		initpoputwindow(pview);
	} 
}
