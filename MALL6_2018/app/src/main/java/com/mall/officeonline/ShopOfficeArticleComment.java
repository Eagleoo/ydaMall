package com.mall.officeonline;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.ShopOfficeArticleModel;
import com.mall.model.ShopOfficeCommentMoel;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.messageboard.FaceConversionUtil;

public class ShopOfficeArticleComment extends Activity{
	@ViewInject(R.id.comment_container)
	private LinearLayout container;
	@ViewInject(R.id.web)
	private WebView web;
	@ViewInject(R.id.et_sendmessage1)
	private EditText message;
	private String articleId="";
	private ShopOfficeArticleModel sm;
	private String tile="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_office_article_comment);
		ViewUtils.inject(this);
		init();
	}
	private void init(){
		if(!Util.isNull(this.getIntent().getStringExtra("title"))){
			tile=this.getIntent().getStringExtra("title");
		}else{
			tile="评论";
		}
		Util.initTop(this, tile, Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShopOfficeArticleComment.this.finish();
			}
		});
		articleId=this.getIntent().getStringExtra("articleid");
		sm=(ShopOfficeArticleModel) this.getIntent().getSerializableExtra("article");
		WebSettings setting=web.getSettings();
		setting.setJavaScriptEnabled(true);
		web.setWebViewClient(new WebViewClient(){    
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);  
				return true;
			}
		});
		web.loadDataWithBaseURL("",sm.getContent(), "text/html", "UTF-8","");
		getCommentById();
	}
	private void initContainer(List<ShopOfficeCommentMoel> list){
		container.removeAllViews();
		for(int i=0;i<list.size();i++){
			if(i>=10){
				break;
			}
			LinearLayout layout=(LinearLayout) getLayoutInflater().inflate(R.layout.office_comment_message_item, null);
			TextView name=(TextView) layout.findViewById(R.id.username);
			TextView content=(TextView) layout.findViewById(R.id.content);
			SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(this, "\u3000\u3000" + list.get(i).getContent());
			System.out.println("评论的内容====="+list.get(i).getContent());
			if(!Util.isNull(list.get(i).getContent())){
				content.setText(spannableString);
			}else{
				content.setText("");
			}
			if(!Util.isNull(list.get(i).getUserid())){
				name.setText(list.get(i).getUserid()+":");
			}else{
				name.setText("");
			}
			final ShopOfficeCommentMoel ss=list.get(i);
			layout.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Util.showChoosedDialog(ShopOfficeArticleComment.this, "是否要删除该评论？", "点错了","确定删除" , new OnClickListener() {
						@Override
						public void onClick(View v) {
							deleteComment(ss.getId());
						}
					});
					return true;
				}
			});
			container.addView(layout);
		}
	}
	private void deleteComment(final String id){
		if(UserData.getUser()==null){
			Util.showIntent(this, LoginFrame.class);
		}
		if(UserData.getOfficeInfo()!=null){
			if(UserData.getOfficeInfo().getUserid().equals(UserData.getUser().getUserIdNoEncodByUTF8())){
				Util.asynTask(this, "正在删除评论", new IAsynTask() {
					@Override
					public void updateUI(Serializable runData) {
						String result=(String) runData;
						if(!Util.isNull(result)){
							if(result.equals("ok")){
								Toast.makeText(ShopOfficeArticleComment.this, "删除成功", Toast.LENGTH_LONG).show();
								getCommentById();
							}
						}else{
							Toast.makeText(ShopOfficeArticleComment.this, "删除失败", Toast.LENGTH_LONG).show();
						}
					}
					@Override
					public Serializable run() {
						Web web=new Web(Web.officeUrl, Web.DelComment,"id="+id+"&userID="+UserData.getUser().getUserId()+"&userPaw="+UserData.getUser().getMd5Pwd());
						return web.getPlan();
					}
				});
			}
		}
	}
	private void getCommentById(){
		Util.asynTaskTwo(this, "正在获取评论....", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if(runData!=null){
					HashMap<Integer, List<ShopOfficeCommentMoel>> map=(HashMap<Integer, List<ShopOfficeCommentMoel>>) runData;
					List<ShopOfficeCommentMoel> list=map.get(1);
					if(list!=null&&list.size()>0){
						initContainer(list);
					}
				}else{
//					Toast.makeText(ShopOfficeArticleComment.this, "没有评论数据", Toast.LENGTH_LONG).show();
				}
			}
			@Override
			public Serializable run() {
				Web web=new Web(Web.officeUrl, Web.GetCommentList,"cPage=1&articleid="+articleId);
				List<ShopOfficeCommentMoel> list=web.getList(ShopOfficeCommentMoel.class);
				HashMap<Integer, List<ShopOfficeCommentMoel>> map=new HashMap<Integer, List<ShopOfficeCommentMoel>>();
				map.put(1, list);
				return map;
			}
		});
	}
	@OnClick(R.id.btn_send)
	public void DoComment(final View view){
		if(UserData.getUser()==null){
			Util.showIntent(this, LoginFrame.class);
		}
		if(!Util.isNull(message.getText().toString())){
			Util.asynTaskTwo(this, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					String result=(String) runData;
					if(!Util.isNull(result)){
						if(result.equals("ok")){
							Toast.makeText(ShopOfficeArticleComment.this, "评论成功", Toast.LENGTH_LONG).show();
							getCommentById();
						}
					}else{
						Toast.makeText(ShopOfficeArticleComment.this, "评论提交失败", Toast.LENGTH_LONG).show();
					}
				}
				@Override
				public Serializable run() {
					Web web=new Web(Web.officeUrl, Web.AddComment,"articleid="+articleId+"&text="+message.getText().toString()+"&userID="+UserData.getUser().getUserId()+"&userPaw="+UserData.getUser().getMd5Pwd());
					return web.getPlan();
				}
			});
		}else{
			Toast.makeText(this, "评论内容不能为空", Toast.LENGTH_LONG).show();
		}
	}
}
