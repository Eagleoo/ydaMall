package com.bpj.lazyfragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.CustomProgressDialog;
import com.mall.model.OfficeProduct;
import com.mall.model.ShopOfficeInfo;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.officeonline.ShopOfficeFrame;
import com.mall.officeonline.ShopOfficeFrame.ItemAdapter;
import com.mall.officeonline.ShopOfficeFrame.ViewHolder;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.ProductDeatilFream;
import com.mall.view.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.NoCopySpan.Concrete;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;


public class Fragment1  extends LazyFragment {
	
//	@ViewInject(R.id.gridview)
	private GridView Gridv;
	public ItemAdapter adapter;
	private Context mContext;
	private ShopOfficeInfo shopoffice;
	private int page = 1;
	private BitmapUtils bmUtils;
	private String officeUserNo = "";
	private String userNo = "";
	private User user;

	
	public static Fragment1 newInstance(){
		Fragment1 fragment1 = new Fragment1();
		return fragment1;
	}
	
	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView()
					.setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Log.e("mContext","Fragment1 mContext创建");
		View view = inflater.inflate(R.layout.fragment1,null);
		Gridv=(GridView) view.findViewById(R.id.gridview);
		Log.e("mContext","mContext创建");
		mContext=getActivity();
	

		Log.e("为空",(mContext==null)+"");
		bmUtils = new BitmapUtils(mContext);
		
		return view;
	}
	
	@Override
	protected void onVisible() {
		System.out.println("Fragment1��ʼ���ء�����");

	}
	
	public void scrollpage() {
		Gridv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 滚动到底部自动加载(很重要)
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						// 加载更多
						getProduct(shopoffice);
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}
	
	public void getProduct(final ShopOfficeInfo shopoffice) {
		this.shopoffice=shopoffice;
		Log.e("为空1",(mContext==null)+"");
		final CustomProgressDialog dialog = Util
				.showProgress("获取宝贝数据...", mContext);
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<String, List<OfficeProduct>> map = (HashMap<String, List<OfficeProduct>>) runData;
				List<OfficeProduct> list = map.get("list");
				if (null != list && 0 != list.size()) {
					if (null != adapter) {
						adapter.notifyDataSetChanged();
					} else {
						adapter = new ItemAdapter(mContext);
						Gridv.setAdapter(adapter);
					}
					adapter.setList(list);
					adapter.notifyDataSetChanged();
				}
				dialog.cancel();
				dialog.dismiss();
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.officeUrl, Web.GetOfficeProductListPage,
						"officeid=" + shopoffice.getOffice_id() + "&cPage="
								+ (page++) + "&flag=1&cateid=1");
				List<OfficeProduct> list = web.getList(OfficeProduct.class);
				HashMap<String, List<OfficeProduct>> map = new HashMap<String, List<OfficeProduct>>();
				map.put("list", list);
				return map;
			}
		});
	}
	
	public class ItemAdapter extends BaseAdapter {
		private List<OfficeProduct> list = new ArrayList<OfficeProduct>();
		private Context c;
		private LayoutInflater inflater;

		public ItemAdapter(Context context) {
			inflater = LayoutInflater.from(context);
			this.c = context;
		}

		public void setList(List<OfficeProduct> list) {
			this.list.addAll(list);
			this.notifyDataSetChanged();
		}

		public void clear() {
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
			OfficeProduct p = this.list.get(position);
			if (convertView == null) {
				h = new ViewHolder();
				convertView = inflater.inflate(R.layout.shop_office_item, null);
				h.office_pro = (ImageView) convertView
						.findViewById(R.id.office_pro);
				h.title = (TextView) convertView.findViewById(R.id.title);
				h.market_price = (TextView) convertView
						.findViewById(R.id.market_price);
				h.yd_price = (TextView) convertView.findViewById(R.id.yd_price);
				h.sb_price = (TextView) convertView.findViewById(R.id.sb_price);
				convertView.setTag(h);
			} else {
				h = (ViewHolder) convertView.getTag();
			}
			final OfficeProduct pp = p;
			String href = p.getProductThumb().replace("174_174", "460_460");
			bmUtils.display(h.office_pro, href);
			h.title.setText(p.getContent());
			h.market_price.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);// 设置中划线
			h.market_price.setText("市场售价：￥"
					+ Util.getDouble(Double.parseDouble(p.getPriceMarket())));
			h.yd_price.setText(Util.spanRed("远大售价：￥",
					Util.getDouble(Double.parseDouble(p.getPrice()))));
			int sbprice = 0;
			sbprice = Integer.parseInt(p.getPriceOriginal());
			if (Double.parseDouble(p.getPriceOriginal()) > sbprice) {
				sbprice += 1;
			}
			h.sb_price.setText(Util.spanGreenInt("商币兑换：", sbprice + ""));
			convertView.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setClass(c, ProductDeatilFream.class);
					intent.putExtra("className", c.getClass().toString());
					intent.putExtra("url", pp.getProductid());
					intent.putExtra("unum", officeUserNo);
					intent.putExtra("title", "分享宝贝购买");
					c.startActivity(intent);
				}
			});
			User user2 = UserData.getUser();
			if (user2 != null && user2.getUserNo().equals(userNo)) {
				convertView.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						Util.showChoosedDialog(c, "是否要删除该分享商品？", "点错了", "确定删除",
								new OnClickListener() {
									@Override
									public void onClick(View v) {
										deleteProduct(pp.getShare_id());
									}
								});
						return true;
					}
				});
			}

			return convertView;
		}

		private void deleteProduct(final String articleid) {
			if (UserData.getUser() != null) {
				user = UserData.getUser();
				Util.asynTask(mContext, "", new IAsynTask() {
					@Override
					public void updateUI(Serializable runData) {
						if (runData != null) {
							System.out.println("rundata=========" + runData);
							if ("ok".equals(runData + "")) {
								Toast.makeText(mContext, "删除成功",
										Toast.LENGTH_LONG).show();
								clear();
								page = 1;
								getProduct(shopoffice);
							}
						} else {
							Toast.makeText(mContext, "删除失败",
									Toast.LENGTH_LONG).show();
						}

					}

					@Override
					public Serializable run() {
						Web web = new Web(Web.officeUrl,
								Web.DeleteOfficeUserProduct, "userID="
										+ user.getUserId() + "&userPaw="
										+ user.getMd5Pwd() + "&articleid="
										+ articleid);
						return web.getPlan();
					}
				});
			}
		}
	}
	public class ViewHolder {
		ImageView office_pro;
		TextView title;
		TextView market_price;
		TextView yd_price;
		TextView sb_price;
	}
	 public interface OnArticleSelectedListener{  
	      public void onArticleSelected(Fragment1 fragment1, ShopOfficeInfo shopoffice );  
	  } 
	
}
