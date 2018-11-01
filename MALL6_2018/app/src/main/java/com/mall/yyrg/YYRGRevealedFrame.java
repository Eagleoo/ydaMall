package com.mall.yyrg;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.model.NewAnnounce;
/**
 * 一元热购活动揭晓功能<br>
 * @author Administrator
 *
 */
public class YYRGRevealedFrame extends Activity{
	@ViewInject(R.id.kaijiang)
	private TextView kaijiang;
	@ViewInject(R.id.jiexiao)
	private TextView jiexiao;
	@ViewInject(R.id.listView1)
	private ListView listView1;
	private int currentPageShop = 0;
	private int state=0;
	private AlreayAnnounceAdapter alreayAnnounceAdapter;
	private int width;
	private List<NewAnnounce> newAnnounces=new ArrayList<NewAnnounce>();
	private String type="";
	private  int miaos=100;
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_huodong_jiexiao);
		ViewUtils.inject(this);
		type=getIntent().getStringExtra("type");
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width=dm.widthPixels;
		if (TextUtils.isEmpty(type)) {
			alreayAnnounceAdapter=new AlreayAnnounceAdapter(this,  width,1);
			listView1.setAdapter(alreayAnnounceAdapter);
			firstpageshop();
			scrollPageshop();
		}else {
			state=1;
			changeColor(jiexiao);
			alreayAnnounceAdapter=new AlreayAnnounceAdapter(this,  width,2);
			listView1.setAdapter(alreayAnnounceAdapter);
			firstpageshop();
			scrollPageshop();
		}
	}
	@OnClick(R.id.top_back)
	public void topback(View view){
		finish();
	}
	private void changeColor(TextView view) {
		kaijiang.setTextColor(getResources().getColor(R.color.yyrg_topcolor));
		jiexiao.setTextColor(getResources().getColor(
				R.color.yyrg_topcolor));
		
		view.setTextColor(getResources().getColor(R.color.bg));
		kaijiang.setBackgroundColor(getResources().getColor(R.color.bg));
		jiexiao.setBackgroundColor(getResources().getColor(R.color.bg));
		view.setBackgroundColor(getResources().getColor(R.color.yyrg_topcolor));
	}
	@OnClick({R.id.kaijiang,R.id.jiexiao})
	public void onclick(View view){
		switch (view.getId()) {
		case R.id.kaijiang:
			state=0;
			currentPageShop=0;
			changeColor(kaijiang);
			/*kaijiang.setBackgroundColor(getResources().getColor(R.color.bg));
			jiexiao.setBackgroundColor(getResources().getColor(R.color.yyrg_jiexiao));*/
			alreayAnnounceAdapter=new AlreayAnnounceAdapter(this,  width,1);
			listView1.setAdapter(alreayAnnounceAdapter);
			firstpageshop();
			scrollPageshop();
			break;
		case R.id.jiexiao:
			state=1;
			currentPageShop=0;
			changeColor(jiexiao);
			/*kaijiang.setBackgroundColor(getResources().getColor(R.color.yyrg_jiexiao));
			jiexiao.setBackgroundColor(getResources().getColor(R.color.bg));*/
			alreayAnnounceAdapter=new AlreayAnnounceAdapter(this,  width,2);
			listView1.setAdapter(alreayAnnounceAdapter);
			firstpageshop();
			scrollPageshop();
			break;
		}
	}
	/**
	 * 得到最新揭晓的产品
	 */
	private void getNewAnnounce() {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					int index=0;
					List<NewAnnounce> list = new ArrayList<NewAnnounce>();
					list = ((HashMap<Integer, List<NewAnnounce>>) runData)
							.get(index++);
					if (list.size() > 0) {
						newAnnounces.addAll(list);
						alreayAnnounceAdapter.setList(list);
					} else {
						Toast.makeText(YYRGRevealedFrame.this, "暂无最新揭晓的产品",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(YYRGRevealedFrame.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}
			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress, Web.getNewAnnounce,
						"pageNum="+(++currentPageShop) + "&size=20");
				List<NewAnnounce> list = web.getList(NewAnnounce.class);
				HashMap<Integer, List<NewAnnounce>> map = new HashMap<Integer, List<NewAnnounce>>();
				map.put(index++, list);
				return map;
			}
		});
	}
	public void firstpageshop() {
		if (state==0) {
			getDoingProduct();
		}else if(state==1){
			getNewAnnounce();
		}
		
	}

	public void scrollPageshop() {
		listView1.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= alreayAnnounceAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (state==0) {
						getDoingProduct();
					}else if(state==1){
						getNewAnnounce();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}
	@OnItemClick(R.id.listView1)
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent=new Intent(this, YYRGHistoryGoodsMessage.class);
		intent.putExtra("goodsid", newAnnounces.get(arg2).getYppid());
		startActivity(intent);
	}
	/**
	 * 获得揭晓中的商品的信息
	 */
	private void getDoingProduct(){
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					int index=0;
					List<NewAnnounce> list = new ArrayList<NewAnnounce>();
					list = ((HashMap<Integer, List<NewAnnounce>>) runData)
							.get(index++);
					if (list.size() > 0) {
						newAnnounces.addAll(list);
						alreayAnnounceAdapter.setList(list);
					} else {
						Toast.makeText(YYRGRevealedFrame.this, "暂无最新揭晓的产品",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(YYRGRevealedFrame.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}
			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress, Web.getDoingProduct,
						"utf-8");
				List<NewAnnounce> list = web.getList(NewAnnounce.class);
				HashMap<Integer, List<NewAnnounce>> map = new HashMap<Integer, List<NewAnnounce>>();
				map.put(index++, list);
				return map;
			}
		});
	}
	class AlreayAnnounceAdapter extends BaseAdapter{
		private List<NewAnnounce>list = new ArrayList<NewAnnounce>();
		private Context context;
		private int width;
		private BitmapUtils bmUtil;
		private LayoutInflater inflater;
		private Timer timer;
		private int state;//1是揭晓中,2已揭晓
		private TimerTask task ;
		private long recLen;
		public AlreayAnnounceAdapter(Context context,List<NewAnnounce> list,int width,int state){
			this.list=list;
			this.state=state;
			this.context=context;
			this.width=width;
			timer = new Timer();
			inflater=LayoutInflater.from(context);
			bmUtil = new BitmapUtils(context);
			bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		}
		public AlreayAnnounceAdapter(Context context,int width,int state){
			this.context=context;
			this.width=width;
			this.state=state;
			timer = new Timer();
			inflater=LayoutInflater.from(context);
			bmUtil = new BitmapUtils(context);
			bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		}
		 public void setList(List<NewAnnounce> list){
		    	this.list.addAll(list);
				this.notifyDataSetChanged();
		    }
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int postion, View view, ViewGroup arg2) {
			if (view==null) {
					view=inflater.inflate(R.layout.yyrg_jiexiao_item, null);
			}
			LinearLayout lin1=(LinearLayout) view.findViewById(R.id.lin1);
			LinearLayout lin2=(LinearLayout) view.findViewById(R.id.lin2);
			if (state==1) {//揭晓中
				lin2.setVisibility(View.VISIBLE);
				lin1.setVisibility(View.GONE);
				ImageView good_img=(ImageView) view.findViewById(R.id.good_img);
				TextView title=(TextView) view.findViewById(R.id.title);
				TextView goods_price=(TextView) view.findViewById(R.id.goods_price);
				title.setText(list.get(postion).getPeriodName());
				 final TextView  daojishi=(TextView) view.findViewById(R.id.daojishi);
				 final TextView daojishi1=(TextView) view.findViewById(R.id.daojishi1);
				 daojishi1.setVisibility(View.GONE);
				setImage(good_img, list.get(postion).getPhotoThumb(), width/4, width/4);
				title.setText(list.get(postion).getProductName());
				goods_price.setText(list.get(postion).getPrice());
				recLen=180-(long)Double.parseDouble(list.get(postion).getIi());
				
//				Timer timers=new Timer();
//				timers.schedule(new TimerTask() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						 runOnUiThread(new Runnable() {      // UI thread  
//				                @Override  
//				                public void run() {  
//				                    miaos--;  
//				                   daojishi1.setText(miaos+"");
//				                    if(miaos < 0){  
//				                        miaos=100;
//				                    //    daojishi.setVisibility(View.GONE);  
//				                    }  
//				                }  
//				            }); 
//					}
//				}, 1,1);
				timer.schedule(new TimerTask() {  
			        @Override  
			        public void run() {  
			           runOnUiThread(new Runnable() {      // UI thread  
			                @Override  
			                public void run() {  
			                    recLen--;  
			                    long fen=recLen/60;
			            		long miao=(recLen%60);
			            		String fenStr=fen+"";
			            		String miaoStr=miao+"";
			            		if (fen+"".length()==1) {
									fenStr="0"+fen;
								}
			            		if (miao+"".length()==1) {
									miaoStr="0"+miao;
								}
			                    daojishi.setText("揭晓倒计时   "+fenStr+":"+miaoStr);  
			                    if(recLen < 0){  
			                        timer.cancel();  
			                    //    daojishi.setVisibility(View.GONE);  
			                    }  
			                }  
			            });  
			        }  
			    }, 1000, 1000);       // timeTask  
				/*task= new TimerTask() {  
			        @Override  
			        public void run() {  
			           runOnUiThread(new Runnable() {      // UI thread  
			                @Override  
			                public void run() {  
			                    recLen--;  
			                    long fen=recLen/(1000*60);
			            		long miao=(recLen%(1000*60))/1000;
			            		long haomiao=(recLen%(1000*60))%1000;
			                    daojishi.setText("揭晓倒计时   "+fen+":"+miao+"."+haomiao);  
			                    if(recLen < 0){  
			                        timer.cancel();  
			                    //    daojishi.setVisibility(View.GONE);  
			                    }  
			                }  
			            });  
			        }  
			    };*/
			}else if (state==2) {//已揭晓
				lin1.setVisibility(View.VISIBLE);
				lin2.setVisibility(View.GONE);
				ImageView image=(ImageView) view.findViewById(R.id.image);
				TextView huodezhe=(TextView) view.findViewById(R.id.huodezhe);
				TextView regou_number=(TextView) view.findViewById(R.id.regou_number);
				TextView price=(TextView) view.findViewById(R.id.jiage);
				TextView jiexiao_time=(TextView) view.findViewById(R.id.jiexiao_time);
				huodezhe.setText(list.get(postion).getAwardUserid());
				regou_number.setText(list.get(postion).getBuyPersonTimes()+"人次");
				DecimalFormat df = new DecimalFormat("#.00");
				price.setText("￥"+df.format(Double.parseDouble(list.get(postion).getPrice())));
				jiexiao_time.setText(list.get(postion).getAnnTime());
				setImage(image, "http://" + Web.imgServer + "/"+list.get(postion).getProductPhoto(), width/4, width/4);
			}
			return view;
		}
		private void setImage(final ImageView logo, String href, final int width,
				final int height) {
			bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
				@Override
				public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
						BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
					arg2 = Util.zoomBitmap(arg2, width, height);
					super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
				}

				@Override
				public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
					logo.setImageResource(R.drawable.new_yda__top_zanwu);
				}
			});
		}
	}
}
