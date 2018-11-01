package com.bpj.lazyfragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.ShopOfficeArticleModel;
import com.mall.model.ShopOfficeCommentMoel;
import com.mall.model.ShopOfficeVedioModel;
import com.mall.net.Web;
import com.mall.officeonline.ShopOfficeVedio;
import com.mall.officeonline.VedioWeb;
import com.mall.officeonline.ShopOfficeVedio.VedioAdapter;
import com.mall.officeonline.ShopOfficeVedio.ViewHolder;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.messageboard.FaceConversionUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;


public class Fragment3  extends LazyFragment {
	private ListView listView;
	private Context mContext;
	private int page = 1;
	private VedioAdapter adapter;
	private BitmapUtils bmUtils = null;   
	private List<ShopOfficeVedioModel> original = new ArrayList<ShopOfficeVedioModel>();
	private boolean searched = false; 
	private LinearLayout comment_layout;
	public static Fragment3 newInstance(){
		Fragment3 fragment1 = new Fragment3();
		return fragment1;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Log.e("mContext","Fragment3 mContext创建");
		View view = inflater.inflate(R.layout.fragment3,null);
		listView=(ListView) view.findViewById(R.id.vedio_list);
		comment_layout=(LinearLayout) view.findViewById(R.id.comment_layout);
		mContext=getActivity();
		bmUtils = new BitmapUtils(mContext);
		firstPage();
		scrollPage();
		return view;
	}
	
	private void firstPage() {
		getVedios();
	}
	
	private void scrollPage() {
		listView.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (searched) {
						adapter.clear();
						adapter.setList(original);
					}
					getVedios();
					searched = false;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}
	
	public void getVedios() {
		if (UserData.getOfficeInfo() != null) {
			Util.asynTask((Activity)mContext, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if (runData != null) {
						HashMap<Integer, List<ShopOfficeVedioModel>> map = (HashMap<Integer, List<ShopOfficeVedioModel>>) runData;
						List<ShopOfficeVedioModel> list = map.get(page);
						if (list != null && list.size() > 0) {
							if (adapter == null) {
								adapter = new VedioAdapter(mContext);
								adapter.setLayout(comment_layout);
								listView.setAdapter(adapter);
							}
							adapter.setList(list);
							original.addAll(list);
							adapter.notifyDataSetChanged();
						}
					} else {

					}
				}

				@Override
				public Serializable run() {
					Web web = new Web(Web.officeUrl, Web.GetVideoListPage,
							"officeid="
									+ UserData.getOfficeInfo().getOffice_id()
									+ "&cPage=" + (page++)
									+ "&flag=1&typeid=2&sec=1");
					List<ShopOfficeVedioModel> list = web
							.getList(ShopOfficeVedioModel.class);
					HashMap<Integer, List<ShopOfficeVedioModel>> map = new HashMap<Integer, List<ShopOfficeVedioModel>>();
					map.put(page, list);
					return map;
				}
			});
		}
	}
	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView()
					.setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
	
	@Override
	protected void onVisible() {
		System.out.println("Fragment3��ʼ���ء�����");
	}
	
	private void initContainer(List<ShopOfficeCommentMoel> list,final LinearLayout container, final String artid) {
		container.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			if (i >= 10) {
				break;
			}
			LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.office_comment_message_item, null);
			TextView name = (TextView) layout.findViewById(R.id.username);
			TextView content = (TextView) layout.findViewById(R.id.content);
			SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(mContext, list.get(i).getContent());
			if (!Util.isNull(list.get(i).getContent())) {
				content.setText(spannableString);
			} else {
				content.setText("");
			}
			if (!Util.isNull(list.get(i).getUserid())) {
				name.setText(list.get(i).getUserid() + ":");
			} else {
				name.setText("");
			}
			final ShopOfficeCommentMoel ss = list.get(i);
			layout.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Util.showChoosedDialog(mContext, "是否要删除该评论？",
							"点错了", "确定删除", new OnClickListener() {
								@Override
								public void onClick(View v) {
									deleteComment(ss.getId(), artid, container);
								}
							});
					return true;
				}
			});
			container.addView(layout);
		}
	}
	
	private void deleteComment(final String id, final String articleId,
			final LinearLayout container) {
		if (UserData.getUser() == null) {
			Util.showIntent(mContext, LoginFrame.class);
		}
		if (UserData.getOfficeInfo() != null) {
			if (UserData.getOfficeInfo().getUserid()
					.equals(UserData.getUser().getUserIdNoEncodByUTF8())) {
				Util.asynTask(mContext, "正在删除评论", new IAsynTask() {
					@Override
					public void updateUI(Serializable runData) {
						String result = (String) runData;
						if (!Util.isNull(result)) {
							if (result.equals("ok")) {
								Toast.makeText(mContext, "删除成功",
										Toast.LENGTH_LONG).show();
								getCommentById(articleId, container);
							}
						} else {
							Toast.makeText(mContext, "删除失败",
									Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public Serializable run() {
						Web web = new Web(Web.officeUrl, Web.DelComment, "id="
								+ id + "&userID="
								+ UserData.getUser().getUserId() + "&userPaw="
								+ UserData.getUser().getMd5Pwd());
						return web.getPlan();
					}
				});
			}
		}
	}
	
	private void getCommentById(final String articleId,final LinearLayout container) {
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					HashMap<Integer, List<ShopOfficeCommentMoel>> map = (HashMap<Integer, List<ShopOfficeCommentMoel>>) runData;
					List<ShopOfficeCommentMoel> list = map.get(1);
					if (list != null && list.size() > 0) {
						System.out.println("-----------------http------getCommentById-------------"+list.size()+"articleId===="+articleId);
						initContainer(list, container, articleId);
					}
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.officeUrl, Web.GetCommentList,"cPage=1&articleid=" + articleId);
				List<ShopOfficeCommentMoel> list = web
						.getList(ShopOfficeCommentMoel.class);
				HashMap<Integer, List<ShopOfficeCommentMoel>> map = new HashMap<Integer, List<ShopOfficeCommentMoel>>();
				map.put(1, list);
				return map;
			}
		});
	}
	
	public class VedioAdapter extends BaseAdapter {
		private Context c;
		private List<ShopOfficeVedioModel> list = new ArrayList<ShopOfficeVedioModel>();
		private LayoutInflater inflater;
		private LinearLayout lay;
		private EditText message;
		private Button btn_send;
        private HashMap<Integer, View> map=new HashMap<Integer, View>();
		public VedioAdapter(Context c) {
			this.c = c;
			inflater = LayoutInflater.from(c);
		}

		public void setLayout(LinearLayout lay) {
			this.lay = lay;
			message = (EditText) lay.findViewById(R.id.et_sendmessage1);

			btn_send = (Button) lay.findViewById(R.id.btn_send);
		}

		public void setList(List<ShopOfficeVedioModel> list) {
			this.list.addAll(list);
			this.notifyDataSetChanged();
		}

		private void clear() {
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ShopOfficeVedioModel sp = this.list.get(position);
			ViewHolder h = null;
			if (map.get(position)==null) {
				convertView = inflater.inflate(R.layout.vedio_item, null);
				h = new ViewHolder();
				h.name = (TextView) convertView.findViewById(R.id.name);
				h.createtime = (TextView) convertView
						.findViewById(R.id.createtime);
				h.vedio_face = (ImageView) convertView
						.findViewById(R.id.vedio_face);
				h.office_favor = (TextView) convertView
						.findViewById(R.id.office_favor);
				h.office_comment = (TextView) convertView
						.findViewById(R.id.office_comment);
				h.office_share = (TextView) convertView
						.findViewById(R.id.office_share);
				h.container = (LinearLayout) convertView
						.findViewById(R.id.vedio_comment_contain);
				convertView.setTag(h);
				map.put(position, convertView);
			} else {
				convertView=map.get(position);
				h = (ViewHolder) convertView.getTag();  
			}    
			final ShopOfficeVedioModel smm = sp;
			h.office_favor.setText(sp.getGoodclicks() + "  ");
			h.name.setText("《" + sp.getTitle() + "》");
			final ImageView im = h.vedio_face;
			String urlimage = "";
			if (sp.getVdeioimgurl().contains("http://")) {
				urlimage = sp.getVdeioimgurl();
			} else {
				if(sp.getVdeioimgurl().contains("s_phone_"))
					urlimage = "http://" + Web.webImage + sp.getVdeioimgurl();
				else
					urlimage = "http://" + Web.webServer_Image + sp.getVdeioimgurl();
			}
			h.createtime.setText(Util.friendly_time(sp.getCreateTime()));
			bmUtils.display(h.vedio_face, urlimage,
					new BitmapLoadCallBack<View>() {
						@Override
						public void onLoadCompleted(View arg0, String arg1,
								Bitmap arg2, BitmapDisplayConfig arg3,
								BitmapLoadFrom arg4) {
							im.setImageBitmap(arg2);
						}
						@Override
						public void onLoadFailed(View arg0, String arg1,
								Drawable arg2) {
							im.setImageResource(R.drawable.no_get_image);
						}
			});
			h.container.removeAllViews();
			getCommentById(sp.getArticleid(), h.container);
			h.office_comment.setText(sp.getCOUNT_COMMENT() + "  ");
			final TextView t = h.office_comment;
			t.setTag(position + "");
			h.office_comment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final String artId = list.get(position).getArticleid();
					lay.setVisibility(View.VISIBLE);
					btn_send.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							addComment(artId);
						}
					});
				}
			});
			convertView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Util.showChoosedDialog(mContext, "是否要删除该视频？",
							"点错了", "确定删除", new OnClickListener() {
								@Override
								public void onClick(View v) {
									deleteVedio(smm.getOfficeid(),
											smm.getArticleid());
								}
							});
					return true;
				}
			});
			final String content = sp.getContent();
			h.vedio_face.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (content.contains("src")) {
						Intent intent = new Intent(c, VedioWeb.class);
						intent.putExtra("src", getVedioSrc(content));
						c.startActivity(intent);
					}else{
						Toast.makeText(c, "没有视频连接", Toast.LENGTH_LONG).show();
					}

				}
			});
			h.office_favor.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TextView t = (TextView) v;
					int i = Integer.parseInt(t.getText().toString().trim());
					GoodClick(smm.getArticleid(), t, i);
				}
			});
			final ShopOfficeVedioModel ssp=sp;
			h.office_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TextView t = (TextView) v;
					String url="";
					if(Web.webImage.contains("test")){
						url="http://"+UserData.getOfficeInfo().getDomainstr()+".mall666.com/user/office/myArtdetail.aspx?unum="+UserData.getOfficeInfo().getUserNo()+"&articleid="+ssp.getArticleid()+"&typeid=2";
					}else if(Web.webImage.contains("app.yda360.com")){
						url="http://"+UserData.getOfficeInfo().getDomainstr()+".yda360.com/user/office/myArtdetail.aspx?unum="+UserData.getOfficeInfo().getUserNo()+"&articleid="+ssp.getArticleid()+"&typeid=2";
					}
					Share(c, smm.getTitle(), url, "http://"+ Web.webImage + smm.getVdeioimgurl(), t);
				}
			});
			return convertView;
		}

		private void addComment(final String articleId) {
			if (UserData.getUser() == null) {
				Util.showIntent(c, LoginFrame.class);
			}
			if (!Util.isNull(message.getText().toString())) {
				Util.asynTask(c, "", new IAsynTask() {
					@Override
					public void updateUI(Serializable runData) {
						String result = (String) runData;
						if (!Util.isNull(result)) {
							if (result.equals("ok")) {
								Toast.makeText(c, "评论成功", Toast.LENGTH_LONG).show();
								lay.setVisibility(View.GONE);
								clear();
								notifyDataSetChanged();
								firstPage();
							}
						} else {
							Toast.makeText(c, "评论提交失败", Toast.LENGTH_LONG)
									.show();
						}
					}

					@Override
					public Serializable run() {
						Web web = new Web(Web.officeUrl, Web.AddComment,
								"articleid=" + articleId + "&text="
										+ message.getText().toString()
										+ "&userID="
										+ UserData.getUser().getUserId()
										+ "&userPaw="
										+ UserData.getUser().getMd5Pwd());
						return web.getPlan();
					}
				});
			} else {
				Toast.makeText(c, "评论内容不能为空", Toast.LENGTH_LONG).show();
			}
		}

		private void GoodClick(final String id, final TextView t,
				final int previous) {
			Util.asynTaskTwo((Activity)mContext, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if ("ok".equals(runData + "")) {
						int num = previous + 1;
						t.setText(num + " ");
					}
				}

				@Override
				public Serializable run() {
					Web web = new Web(Web.officeUrl, Web.UpdateGoods,
							"articleid=" + id);
					String result = web.getPlan();
					return result;
				}
			});
		}

		private void Share(Context c, String content, final String url,
				String imageurl, final TextView t) {
			LogUtils.e("url="+url+"          imageurl="+imageurl);
			OnekeyShare oks = new OnekeyShare();
			oks.setTitle("网上空间视频分享");
			oks.setAddress("400-666-3838");
			oks.setTitleUrl(url);
			oks.setText(content + "...");
			oks.setUrl(url);
			oks.setImageUrl("http://app.yda360.com/phone/images/ic_launcher1.png?r=2");
			oks.setComment(c.getString(R.string.share));
			oks.setSite(c.getString(R.string.app_name));
			oks.setSiteUrl(url);
			oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
				@Override
				public void onShare(Platform platform,
						Platform.ShareParams paramsToShare) {
					if ("ShortMessage".equals(platform.getName())) {
						paramsToShare.setImageUrl(null);
						paramsToShare.setText(paramsToShare.getText()+"\n"+url.toString());
					}
				}
			});
			Platform[] plant = ShareSDK.getPlatformList();
			for (int i = 0; i < plant.length; i++) {
				Platform p = plant[i];
				p.setPlatformActionListener(new PlatformActionListener() {
					@Override
					public void onError(Platform arg0, int arg1, Throwable arg2) {

					}

					@Override
					public void onComplete(Platform arg0, int arg1,
							HashMap<String, Object> arg2) {
						int i = Integer.parseInt(t.getText().toString());
						t.setText((i + 1) + "");
					}

					@Override
					public void onCancel(Platform arg0, int arg1) {

					}
				});
			}
			oks.show(c);

		}

		private String getVedioSrc(String content) {
			int start = 0;
			int end = 1;
			try {
				String src = "";
				start = content.indexOf("src=\"") + 5;
				end = content.indexOf("width") - 2;
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
			return content.substring(start, end);
		}

		private void deleteVedio(String officeId, final String artId) {
			if (UserData.getUser() == null) {
				Util.showIntent(c, LoginFrame.class);
			}
			if (UserData.getOfficeInfo() != null) {
				System.out
						.println("-------------deleteVedio-------officeId---------------"
								+ UserData.getOfficeInfo().getOffice_id()
								+ officeId);
				// if(UserData.getOfficeInfo().getOffice_id().equals(officeId)){
				Util.asynTask(c, "", new IAsynTask() {
					@Override
					public void updateUI(Serializable runData) {
						if (runData != null) {
							if ("ok".equals(runData + "")) {
								Toast.makeText(c, "删除成功", Toast.LENGTH_LONG)
										.show();
								page = 1;
								if (adapter != null) {
									adapter.clear();
									adapter.notifyDataSetChanged();
								}
								firstPage();
							}
						} else {
							Toast.makeText(c, "删除失败", Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public Serializable run() {
						Web web = new Web(Web.officeUrl,
								Web.DeleteOfficeUserArticle, "userID="
										+ UserData.getUser().getUserId()
										+ "&userPaw="
										+ new UserData().getUser().getMd5Pwd()
										+ "&articleid=" + artId);
						return web.getPlan();
					}
				});
				// }
			}
		}
	}
	public class ViewHolder {
		TextView name;
		TextView createtime;
		ImageView vedio_face;
		TextView office_favor;
		TextView office_comment;
		TextView office_share;
		LinearLayout container;
	}
	
	public void seachadapter(String string){
		adapter.clear();
		List<ShopOfficeVedioModel> l = new ArrayList<ShopOfficeVedioModel>();
		for (int i = 0; i < original.size(); i++) {
			if (original.get(i).getTitle()
					.contains(string)) {
				l.add(original.get(i));
			}
		}
		if (l.size() > 0) {
			adapter.setList(l);
			adapter.notifyDataSetChanged();
		}
		searched = true;
	
	}
	
	 public interface OnArticleFragment3SeachListener{  
	      public void onArticleFragment3Selected(Fragment3 fragment3, String string );  
	  } 
	
}
