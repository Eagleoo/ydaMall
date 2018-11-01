package com.mall.officeonline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.ShopOfficeArticleModel;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;

public class ShopOfficeMp3 extends Activity{
	private int page=1;
	@ViewInject(R.id.listview)
	private ListView listView;
	private ShopOfficeArticleAdapter adapter;
	private String officeId="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_office_article);
		ViewUtils.inject(this);
		init();
	}
	private void init() {
		officeId = this.getIntent().getStringExtra("officeid");
		Util.initTopWithTwoButton(this, "日志", R.drawable.vedio_search,
				R.drawable.note_add, new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 搜索功能
					}
				}, new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 添加视频
					}
				}, new OnClickListener() {
					@Override
					public void onClick(View v) {
						ShopOfficeMp3.this.finish();
					}
				});
	}
	
	public void getMP3ByPage(){
		Util.asynTaskTwo(this, "获取日志", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					HashMap<Integer, List<ShopOfficeArticleModel>> map = (HashMap<Integer, List<ShopOfficeArticleModel>>) runData;
					List<ShopOfficeArticleModel> list = map.get(page);
					if (adapter == null) {
						adapter = new ShopOfficeArticleAdapter(ShopOfficeMp3.this);
						listView.setAdapter(adapter);
					}
					if(list!=null&&list.size()>0){
						adapter.setList(list);
						adapter.notifyDataSetChanged();
					}
				} else {
					Toast.makeText(ShopOfficeMp3.this, "未获取到日志数据...",Toast.LENGTH_LONG).show();
				}
			}
			@Override
			public Serializable run() {
				Web web = new Web(Web.officeUrl, Web.GetMp3ListPage,"officeid=" + officeId + "&cPage=" + (page++)+ "&flag=1&typeid=3&sec=1");
				List<ShopOfficeArticleModel> list = web.getList(ShopOfficeArticleModel.class);
				HashMap<Integer, List<ShopOfficeArticleModel>> map = new HashMap<Integer, List<ShopOfficeArticleModel>>();
				map.put(page, list);
				return map;
			}
		});
	}
	public class ShopOfficeArticleAdapter extends BaseAdapter {
		private Context c;
		private List<ShopOfficeArticleModel> list = new ArrayList<ShopOfficeArticleModel>();
		private LayoutInflater inflater;

		public ShopOfficeArticleAdapter(Context c) {
			this.c = c;
			inflater = LayoutInflater.from(c);
		}

		public void setList(List<ShopOfficeArticleModel> list) {
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
			ShopOfficeArticleModel sm = this.list.get(position);
			ViewHolder h = null;
			if (convertView == null) {  
				h = new ViewHolder();
				convertView = inflater.inflate(R.layout.shop_office_article_item, null); 
				h.message = (TextView) convertView.findViewById(R.id.message);
				h.comment = (TextView) convertView.findViewById(R.id.comment);
				h.time = (TextView) convertView.findViewById(R.id.time);
				h.read = (TextView) convertView.findViewById(R.id.read);
				h.share = (TextView) convertView.findViewById(R.id.share);
				convertView.setTag(h);  
			}else{   
				h=(ViewHolder) convertView.getTag();    
			}
			if(!Util.isNull(sm.getCreateTime())){
				h.time.setText(Util.friendly_time(sm.getCreateTime()));   
			}
			if(!Util.isNull(sm.getContent())){
				String content=Util.Html2Text(sm.getContent());
				if(content.length()>100){
					h.message.setText(content.subSequence(0, 100)+"...");
				}else{
					h.message.setText(content);
				}
			}
			if(!Util.isNull(sm.getCommentCount())){
				h.comment.setText(Util.spannBlueFromBegin("评论   ","(" + sm.getCommentCount() + ")"));
			}
            if(!Util.isNull(sm.getClicks())){
            	h.read.setText(Util.spannBlueFromBegin("赞   ","(" + sm.getGoodclicks() + ")"));
			}
            final ShopOfficeArticleModel smm=sm;
            h.read.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TextView t=(TextView) v;
					GoodClick(smm.getArticleid(),t,Integer.parseInt(smm.getGoodclicks()));
				}
			});  
            convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(ShopOfficeMp3.this,ShopOfficeArticleComment.class);
					intent.putExtra("articleid", smm.getArticleid());
					Bundle bun=new Bundle();
					bun.putSerializable("article", smm);
					intent.putExtras(bun);
					ShopOfficeMp3.this.startActivity(intent);
				}
			});
			h.share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				}
			});         
			return convertView;
		}
		private void GoodClick(final String id,final TextView t,final int previous){
			Util.asynTaskTwo(ShopOfficeMp3.this, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if("ok".equals(runData+"")){
						int num=previous+1;
						t.setText(Util.spannBlueFromBegin("赞   ","(" + num + ")"));
					}
				}
				@Override
				public Serializable run() {
					Web web=new Web(Web.officeUrl, Web.UpdateGoods,"articleid="+id);
					String result=web.getPlan();
					return result;
				}
			});
		}
		private void Share(){
			OnekeyShare oks=new OnekeyShare();
			
		}
	}

	public class ViewHolder {
		TextView message;
		TextView time;
		TextView comment;
		TextView read;
		TextView share;
	}
}
