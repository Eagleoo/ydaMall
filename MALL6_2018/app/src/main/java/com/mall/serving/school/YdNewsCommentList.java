package com.mall.serving.school;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
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
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.User;
import com.mall.model.YdNewsCommentModel;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.messageboard.FaceConversionUtil;

public class YdNewsCommentList extends Activity{
	@ViewInject(R.id.listview)
	private ListView listView;
	private String newsid="";
	private YdNewsComentAdapter adapter;
	private int page=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ydnews_comment_list);
		ViewUtils.inject(this);
		init();
	}
	private void init(){
		Util.initTop(this, "新闻评论", Integer.MIN_VALUE,null);
		newsid=this.getIntent().getStringExtra("newsid");
		page();
		listView.setOnScrollListener(new OnScrollListener() {
			int lastItem;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					page();
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}
	private void page(){
		Util.asynTask(this, "", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (runData == null) {
					Toast.makeText(YdNewsCommentList.this, "网络请求失败，请稍后再再试",
							Toast.LENGTH_LONG).show();
				} else {
					@SuppressWarnings("unchecked")
					HashMap<Integer, List<YdNewsCommentModel>> map = (HashMap<Integer, List<YdNewsCommentModel>>) runData;
					List<YdNewsCommentModel> list = map.get(1);
					if(adapter==null){
						adapter=new YdNewsComentAdapter(YdNewsCommentList.this);
						listView.setAdapter(adapter);
					}
					if (list != null && list.size() > 0) {
						adapter.setList(list);
					}
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.ydnews_url, Web.get_comment_xml
						+ "&pagesize_=10&curpage="+(page++)+"&newsid=" + newsid, "");
				InputStream in = web.getHtml();
				HashMap<Integer, List<YdNewsCommentModel>> map = new HashMap<Integer, List<YdNewsCommentModel>>();
				ListHandler handler = null;
				SAXParser parser = null;
				try {
					SAXParserFactory factory = SAXParserFactory.newInstance(); // 取得SAXParserFactory实例
					parser = factory.newSAXParser();
					handler = new ListHandler();
					parser.parse(in, handler);
					map.put(1, handler.getList());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return map;
			}
		});
	}
	public class YdNewsComentAdapter extends BaseAdapter{
		private List<YdNewsCommentModel> list=new ArrayList<YdNewsCommentModel>();
		private Context c;
		private LayoutInflater inflater;
		private BitmapUtils bmUtils;
		public YdNewsComentAdapter(Context c){
			this.c=c;
			inflater=LayoutInflater.from(c);
			bmUtils=new BitmapUtils(c);
		}
		public void setList(List<YdNewsCommentModel> list){
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
			final YdNewsCommentModel y=list.get(position);
			Holder h=null;
			if(convertView==null){
				h=new Holder();
				convertView=inflater.inflate(R.layout.ydnews_comment_item, null);
				h.user_name= (TextView) convertView.findViewById(R.id.user_name);
				h.time_and_role = (TextView) convertView
						.findViewById(R.id.time_and_role);
				h.favours = (TextView) convertView.findViewById(R.id.favours);
				h. content= (TextView) convertView.findViewById(R.id.content);
				h.img = (ImageView) convertView.findViewById(R.id.img); 
				convertView.setTag(h);
			}else{
				h=(Holder) convertView.getTag();
			}
			if(Util.isNull(y.getSource())||y.getSource().equals("(null)")){  
				h.time_and_role.setText("远大网友   "
					+ Util.friendly_time(y.getAdddate()));
			}else{
				h.time_and_role.setText(y.getSource()+"  "+ Util.friendly_time(y.getAdddate()));
			}
			h.user_name.setText(y.getUserid());
			h.favours.setText(y.getPraise());
			
			
			
			check_comment_Praise(y, h.favours);
			
		
			h.favours.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					TextView tv=(TextView) view;
					Favours(y,tv);
				}
			});
			SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(c, y.getInfo());
			h.content.setText(spannableString);
			String url="http://"+Web.webImage+"/userFace/"+y.getUserno()+"_97_97.jpg";
			final ImageView fimg=h.img;
			bmUtils.display(h.img, url,new BitmapLoadCallBack<View>() {
				@Override
				public void onLoadCompleted(View arg0, String arg1,
						Bitmap arg2, BitmapDisplayConfig arg3,
						BitmapLoadFrom arg4) {
					fimg.setImageBitmap(Util.getRoundedCornerBitmap(arg2));
				}

				@Override
				public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
					Resources res=c.getResources();
					Bitmap bit = BitmapFactory.decodeResource(res, R.drawable.ic_launcher_black_white);
					fimg.setImageBitmap(Util.getRoundedCornerBitmap(bit));
				}
			});
			return convertView;
		}
		
		
		private void check_comment_Praise(final YdNewsCommentModel yy, final TextView tv) {
			final User user = UserData.getUser();
			if (user == null) {
				return;
			}
			Util.asynTask(new IAsynTask() {

				@Override
				public void updateUI(Serializable runData) {
					if (runData == null) {

					} else {
						if ("0".equals(runData.toString())) {

							tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,
									R.drawable.community_dynamic_praise_pink, 0);
						} else {

							tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,
									R.drawable.community_dynamic_praise, 0);
						}

					}

				}

				@Override
				public Serializable run() {
					Web web = new Web(Web.ydnews_url, Web.check_comment_Praise
							+ "&userid=" + user.getUserId() + "&userno="
							+ user.getUserNo() + "&id=" + yy.getId()
							+ "&md5Pwd=" + user.getMd5Pwd(), "");
					return web.getPlan();
				}
			});
		}
		private void Favours(final YdNewsCommentModel yy,final TextView tv) {
			final User user = UserData.getUser();
			if (user == null) {
				return;
			}
			Util.asynTask(c, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if (runData == null) {
						
					} else {
						if ("1".equals(runData.toString())) {
							int parise = Integer.parseInt(yy.getPraise());
							yy.setPraise((parise + 1) + "");
							tv.setText((parise + 1) + "");
							tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,
									R.drawable.community_dynamic_praise_pink, 0);
						} else {

							int parise = Integer.parseInt(yy.getPraise());
							yy.setPraise((parise - 1) + "");
							tv.setText((parise - 1) + "");
							tv.setCompoundDrawablesWithIntrinsicBounds(0, 0,
									R.drawable.community_dynamic_praise, 0);
						}

					}
				}

				@Override
				public Serializable run() {
					Web web = new Web(Web.ydnews_url, Web.update_comment_Praise
							+ "&userid=" + user.getUserId() + "&userno="
									+ user.getUserNo()+ "&id=" + yy.getId(), "");
					return web.getPlan();
				}
			});
		}
	}
	public class Holder{
		TextView user_name;
		ImageView img;
		TextView favours;
		TextView content;
		TextView time_and_role;
	}
	public class ListHandler extends DefaultHandler {
		private List<YdNewsCommentModel> list = new ArrayList<YdNewsCommentModel>();
		private YdNewsCommentModel y;
		private StringBuilder builder;

		public List<YdNewsCommentModel> getList() {
			return list;
		}

		@Override
		public void startDocument() throws SAXException {
			list.clear();
			builder = new StringBuilder();
			super.startDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			if (qName.equals("list"))
				y = new YdNewsCommentModel();
			builder.setLength(0);
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			super.characters(ch, start, length);
			builder.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			String s = builder.toString();
			if (qName.equals("id")) {
				y.setId(s);
			} else if (qName.equals("newsid")) {
				y.setNewsid(s);
			} else if (qName.equals("Praise")) {
				y.setPraise(s);
			} else if (qName.equals("userid")) {
				y.setUserid(s);
			} else if (qName.equals("adddate")) {
				y.setAdddate(s);
			} else if (qName.equals("info")) { 
				y.setInfo(s);
			} else if(qName.equals("Source")){
				y.setSource(s);
			}else if(qName.equals("userno")){
				y.setUserno(s);
			}else if (qName.equals("list")) {
				list.add(y);
			}
			super.endElement(uri, localName, qName);
		}

		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
		}
	}
}
