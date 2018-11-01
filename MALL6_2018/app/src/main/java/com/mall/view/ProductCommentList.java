package com.mall.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lin.component.CustomProgressDialog;
import com.mall.model.ProductCommentListModel;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.Util;

public class ProductCommentList extends Activity {
	private ListView listview;
	private String pid = "0";
	private int page = 1;
	private BitmapUtils bmUtils;
	private ProductCommentListAdapter pcAdapter;
    private  List<ProductCommentListModel> list=new ArrayList<ProductCommentListModel>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_comment_list);
		bmUtils=new BitmapUtils(this);
		Util.initTop(this, "商品评论", Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View v) {
				ProductCommentList.this.finish();
			}
		});
		listview = (ListView) this.findViewById(R.id.listview);
		pid = this.getIntent().getStringExtra("pid");
		getData();
		scroll();

	}

	private void scroll() {
		listview.setOnScrollListener(new OnScrollListener() {
			int lastItem;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= listview.getAdapter().getCount()&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					  getData();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}
    private void JsonToObject(String result){
    	this.list.clear();
    	try {
			String list=new JSONObject(result).getString("list");
			org.json.JSONArray array=new JSONArray(list);
			for(int i=0;i<array.length();i++){
				JSONObject obj=array.getJSONObject(i);
				ProductCommentListModel pc=new ProductCommentListModel();
				pc.setContent(obj.getString("content"));
				pc.setTime(obj.getString("date"));
				pc.setFace(obj.getString("userFace"));
				pc.setName(obj.getString("userId"));
				this.list.add(pc);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
	private void getData() {
		final CustomProgressDialog dialog = Util.showProgress("数据加载中...", this);
		NewWebAPI.getNewInstance().getProductComment(pid, page, 10,
				new WebRequestCallBack() {
					@Override
					public void success(Object result) {						
						com.alibaba.fastjson.JSONObject obj = JSON.parseObject(result.toString());
						if (200 != obj.getInteger("code").intValue()) {
							Util.show(obj.getString("message"),
									ProductCommentList.this);
							return;
						}
						JsonToObject(result.toString());
						if(pcAdapter==null){
							pcAdapter=new ProductCommentListAdapter(ProductCommentList.this);
							listview.setAdapter(pcAdapter);
						}
						pcAdapter.setList(list);
						++page;
					}

					@Override
					public void requestEnd() {
						dialog.cancel();
						dialog.dismiss();
					}
				});
	}

	public class ProductCommentListAdapter extends BaseAdapter {
		private List<ProductCommentListModel> list = new ArrayList<ProductCommentListModel>();
		private Context c;
		private LayoutInflater inflater;
		private Bitmap defaultFace = null;
		public ProductCommentListAdapter(Context c) {
			this.c = c;
			inflater = LayoutInflater.from(c);
			Resources r = c.getResources();
			InputStream is = r.openRawResource(R.drawable.ic_launcher);
			BitmapDrawable bmpDraw = new BitmapDrawable(is);
			defaultFace = Util.zoomBitmap(bmpDraw.getBitmap(),Util.dpToPx(c, 50), Util.dpToPx(c, 50));
		}

		public void setList(List<ProductCommentListModel> list) {
			this.list.addAll(list);
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
			ViewHolder h=null;
			ProductCommentListModel pc=this.list.get(position);
			if(convertView==null){
				h=new ViewHolder();
				convertView=inflater.inflate(R.layout.prodcut_comment_list_item, null);
				h.content=(TextView) convertView.findViewById(R.id.comment);
				h.time=(TextView) convertView.findViewById(R.id.time);
				h.face=(ImageView) convertView.findViewById(R.id.face);
				h.name=(TextView) convertView.findViewById(R.id.name);
				convertView.setTag(h);
			}else{
				h=(ViewHolder) convertView.getTag();
			}
			if(!Util.isNull(pc.getContent())){
			   h.content.setText(pc.getContent());
			}
			if(!Util.isNull(pc.getTime())){
				h.time.setText(Util.friendly_time(pc.getTime()));
			}
			if(Util.isNull(pc.getFace())){
				h.face.setImageBitmap(defaultFace);
			}else{
				final ImageView fa=h.face;
				bmUtils.display(h.face, pc.getFace(),new BitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						fa.setImageBitmap(Util.getRoundedCornerBitmap(arg2));
					}
					@Override
					public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
						fa.setImageBitmap(defaultFace);
					}
				});
			}
			if(pc.getName().length()>10){
				h.name.setText(pc.getName().subSequence(0, 10));
			}else{
				h.name.setText(pc.getName());
			}
			
			if(position%2==0){
				convertView.setBackgroundColor(Color.WHITE);
			}else{
				convertView.setBackgroundColor(Color.parseColor("#e5e5e5"));
			}
			return convertView;
		}
	}
	
	public class ViewHolder{
		TextView content;
		TextView time;
		ImageView face;
		TextView name;
	}
}
