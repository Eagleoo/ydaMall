package com.mall.serving.school;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.model.User;
import com.mall.model.YdNewsModel;
import com.mall.net.Web;
import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

import java.io.Serializable;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class YdaSchoolAdapter extends NewBaseAdapter {

	private String search;
	
	public YdaSchoolAdapter(Context c, List list) {
		super(c, list);
	}

	public void setSearch(String search) {
		this.search = search;
	}



	@Override
	public View getView(int p, View v, ViewGroup arg2) {
		if (v == null) {
			ViewHolder h = new ViewHolder();
			v = inflater.inflate(R.layout.ydnews_list_item, null);
			h.newsimg = (ImageView) v.findViewById(R.id.newsimg);
			h.title = (TextView) v.findViewById(R.id.title);
			h.comment = (TextView) v.findViewById(R.id.comment);
			h.favour = (TextView) v.findViewById(R.id.favour);
			h.publish_time = (TextView) v.findViewById(R.id.publish_time);
			h.content = (TextView) v.findViewById(R.id.content);
			h.share = (ImageView) v.findViewById(R.id.share);
			v.setTag(h);
		}

		ViewHolder h = (ViewHolder) v.getTag();
		final YdNewsModel y = (YdNewsModel) list.get(p);
		if (Util.isNull(y.getPicurl())) {
			h.newsimg.setImageResource(R.drawable.no_get_image);
		} else {
			AnimeUtil.getImageLoad().displayImage(y.getPicurl(), h.newsimg);
		}
		String title = y.getTitle().replace("&#8203;", "");
		if (!Util.isNull(y.getTitle())) {
			h.title.setText(title);
		}
		if(!Util.isNull(search)){
			if(title.toLowerCase().contains(search.toLowerCase())){
				int index = title.toLowerCase().indexOf(search.toLowerCase());
				SpannableStringBuilder style=new SpannableStringBuilder(title);
				style.setSpan(new ForegroundColorSpan(Color.RED),index,index+search.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
				style.setSpan(new ForegroundColorSpan(Color.RED),index,index+search.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
				h.title.setText(style);
			}
		}
		if (!Util.isNull(y.getComment())) {
			h.comment.setText(y.getComment());
		} else {
			h.comment.setText("0");
		}
		if (!Util.isNull(y.getPraise())) {
			h.favour.setText(y.getPraise());
		} else {
			h.favour.setText("0");
		}

		check_news_parise(y, h.favour);

		h.favour.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				TextView tv = (TextView) v;
				Favours(y, tv);
			}
		});

		String content = y.getContent();
		h.content.setText(content);

		if(!Util.isNull(search)){
			if(content.toLowerCase().contains(search.toLowerCase())){
				int index = content.toLowerCase().indexOf(search.toLowerCase());
				SpannableStringBuilder style=new SpannableStringBuilder(content);
				style.setSpan(new ForegroundColorSpan(Color.RED),index,index+search.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
				style.setSpan(new ForegroundColorSpan(Color.RED),index,index+search.length(),Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
				h.content.setText(style);
			}
		}
		if (!Util.isNull(y.getNewdate())) {
			String arr[] = y.getNewdate().split(" ");
			if (2 == arr.length)
				h.publish_time.setText(arr[0]);
			else
				h.publish_time.setText(y.getNewdate());
		} else {
			h.publish_time.setText("");
		}
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, YDNewsDetailFrame.class);
				Bundle b = new Bundle();
				b.putSerializable("ydnews", y);
				intent.putExtras(b);
				context.startActivity(intent);
			}
		});
		h.share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = "www.yda360.cn/News_show.asp?id=" + y.getId()
						+ "&typeid=1&ntypeid=23";
				System.out.println("----------url===" + url);
				share(url, y.getPicurl(), y.getTitle(), y.getContent());
			}
		});

		return v;
	}

	public class ViewHolder {
		ImageView newsimg;
		TextView title;
		TextView content;
		TextView publish_time;
		TextView favour;
		TextView comment;
		ImageView share;
	}

	private void share(final String url, String imgurl, String title, String brief) {
		OnekeyShare oks = new OnekeyShare();
		oks.setTitle(title);
		oks.setTitleUrl(url);
		oks.setUrl(url);
		oks.setAddress("10086");
		oks.setSite("远大云商");
		oks.setSiteUrl(url);
		oks.setImageUrl(imgurl);
		oks.setText(brief);
		oks.setComment(brief);
		oks.setSilent(false);
		oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
			@Override
			public void onShare(Platform platform, ShareParams paramsToShare) {
				if ("ShortMessage".equals(platform.getName())) {
					paramsToShare.setImageUrl(null);
					paramsToShare.setText(paramsToShare.getText()+"\n"+url.toString());
				}
			}
		});
		oks.show(context);
	}

	private void check_news_parise(final YdNewsModel yy, final TextView tv) {
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
				Web web = new Web(Web.ydnews_url, Web.check_news_Praise
						+ "&userid=" + user.getUserId() + "&userno="
						+ user.getUserNo() + "&newsid=" + yy.getId()
						+ "&md5Pwd=" + user.getMd5Pwd(), "");
				return web.getPlan();
			}
		});
	}

	private void Favours(final YdNewsModel yy, final TextView tv) {
		final User user = UserData.getUser();
		if (user == null) {
			return;
		}
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (runData == null) {
					Toast.makeText(context, "网络访问出现问题，请稍后再试", Toast.LENGTH_LONG)
							.show();
				} else {
					Log.e("点击","runData"+runData.toString());
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
				Web web = new Web(Web.ydnews_url, Web.update_news_Praise
						+ "&userid=" + user.getUserId() + "&userno="
						+ user.getUserNo() + "&newsid=" + yy.getId()
						+ "&md5Pwd=" + user.getMd5Pwd(), "");
				return web.getPlan();
			}
		});
	}
}
