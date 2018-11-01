package com.mall.view.messageboard;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
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

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.mall.model.PariseBoardModel;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;
/**
 * 
 * 功能：对该心情点赞的用户列表<br>
 * 时间： 2014-8-16<br>
 * 备注： <br>
 * @author Lin.~
 *
 */
public class ParsiseBoardList extends Activity {
	private ListView listview;
	private ParsiseBoardListAdapter adapter;
	private BitmapUtils bmUtils;
	private int _50dp=50;
	private String mid="";
	private int page=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parsise_board_list);
		init();
	}
	private void init(){
		bmUtils=new BitmapUtils(this);
		_50dp=Util.dpToPx(this, 50);
		mid=this.getIntent().getStringExtra("mid");
		Util.initTop(this,"赞", Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ParsiseBoardList.this.finish();
			}
		});
		listview=(ListView) this.findViewById(R.id.parsize_board_list);
		firstpage();
		scrollPage();
	}
	private void firstpage() {
		bindData();
	}
	private void scrollPage() {
		listview.setOnScrollListener(new OnScrollListener() {
			int lastItem;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					bindData();
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}
	private void bindData(){
		Util.asynTask(ParsiseBoardList.this, "正在加载数据", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				@SuppressWarnings("unchecked")
				HashMap<String,List<PariseBoardModel>> map=(HashMap<String, List<PariseBoardModel>>) runData;
				List<PariseBoardModel> resultList=map.get("list");
				if (resultList.size() > 0) {
					if (adapter == null) {
						adapter = new ParsiseBoardListAdapter(ParsiseBoardList.this);
						adapter.setList(resultList);
						listview.setAdapter(adapter);
					} else {
						adapter.setList(resultList);
						adapter.notifyDataSetChanged();
					}
				} else if (resultList.size() == 0) {
//					Toast.makeText(MessageBoardComment.this, "没有数据了",Toast.LENGTH_LONG).show();
					listview.setOnScrollListener(null);
					page--;// 将page会滚到上一页
				}
				
			}
			@Override
			public Serializable run() {
				Web web=new Web(Web.getUserMessageBoardPraise, "id="+mid+"&size=10"+"&page="+(++page));
				List<PariseBoardModel> result=web.getList(PariseBoardModel.class);
				HashMap<String, List<PariseBoardModel>> map=new HashMap<String, List<PariseBoardModel>>();
				map.put("list", result);
				return map;
			}
		});
	}
	public class ParsiseBoardListAdapter extends BaseAdapter{
		private Context c;
		private LayoutInflater inflater;
		private List<PariseBoardModel> list=new ArrayList<PariseBoardModel>();
		private Map<Integer,View> map=new HashMap<Integer, View>();
		public ParsiseBoardListAdapter(Context c){
			this.c=c;
			inflater=LayoutInflater.from(c);
		}
        private void setList(List<PariseBoardModel> list){
        	this.list.addAll(list);
        	this.notifyDataSetChanged();
        }
		@Override
		public int getCount() {
			return this.list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return this.list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			PariseBoardModel msg=list.get(arg0);
			ViewHolder h=null;
			if(map.get(arg0)==null){
				arg1=inflater.inflate(R.layout.message_board_parise_item, null);
				h=new ViewHolder();
				h.userface=(ImageView) arg1.findViewById(R.id.message_board_user_face);
				h.title=(TextView) arg1.findViewById(R.id.comment_title);
				h.time=(TextView) arg1.findViewById(R.id.comment_time);
				h.content=(TextView) arg1.findViewById(R.id.comment_detail);
				map.put(arg0, arg1);
				arg1.setTag(h);
			}else{
				arg1=map.get(arg0);
				h=(ViewHolder) arg1.getTag();
			}
			h.title.setText(Util.getNo_pUserId(msg.getUserId()));
			String time=msg.getCreateTime();
			if(time.contains("/")){
				time=time.replace("/", "-");
			}
			h.time.setText(Util.friendly_time(time));
			h.content.setVisibility(View.GONE);
			final ViewHolder hh=h;
			bmUtils.display(hh.userface, msg.getUserFace(),
					new DefaultBitmapLoadCallBack<View>() {
						@Override
						public void onLoadCompleted(View container, String uri,
								Bitmap bitmap, BitmapDisplayConfig config,
								BitmapLoadFrom from) {
							Bitmap zoomBm = Util.zoomBitmap(bitmap, _50dp, _50dp);
							super.onLoadCompleted(container, uri,Util.getRoundedCornerBitmap(zoomBm), config,from);
						}
						@Override
						public void onLoadFailed(View container, String uri,
								Drawable drawable) {
							Resources r = c.getResources();
							InputStream is = r.openRawResource(R.drawable.ic_launcher);
							BitmapDrawable bmpDraw = new BitmapDrawable(is);
							Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(),_50dp, _50dp);
							hh.userface.setImageBitmap(Util.getRoundedCornerBitmap(zoomBm));
						}
			});
			final PariseBoardModel pbm=msg;
			h.userface.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent=new Intent(c,UserMessageBoardFrame.class);
					intent.putExtra("userId", pbm.getUserId());
					intent.putExtra("userface", pbm.getUserFace());
					c.startActivity(intent);
				}
			});
			return arg1;
		}
	}
	public class ViewHolder{
		TextView title;
		ImageView userface;
		TextView content;
		TextView time;
	}
}
