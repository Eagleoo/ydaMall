package com.mall.view.messageboard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.XQAdapter;
import com.mall.model.User;
import com.mall.model.messageboard.UserMessageBoard;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.SelectPicActivity;
import com.pulltorefresh.PullToRefreshListView;
import com.pulltorefresh.PullToRefreshListView.PullToRefreshListener;

/**
 * 
 * 功能：商城某个会员的心情页面<br>
 * 时间： 2014-8-16<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class UserMessageBoardFrame extends Activity {
	@ViewInject(R.id.message_list)
	private ListView listview;
	@ViewInject(R.id.message_board_user_face)
	private ImageView userfaceView;
	private TextView message_board_userId;
	@ViewInject(R.id.message_board_time)
	private TextView message_board_time;
	@ViewInject(R.id.user_bg_layout)
	private ImageView user_bg_layout;
	private List<UserMessageBoard> list = new ArrayList<UserMessageBoard>();
	private XQAdapter adapter;
	private int page = 1;
	private int size = 3;
	private BitmapUtils bmUtils;
	private String userId;
	private String userface = "";
	private int _50dp = 50;
	private User user ;
	@ViewInject(R.id.refreshable_view)
	private PullToRefreshListView refreshable_view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.user_message_board_frame);
		ViewUtils.inject(this);
		bmUtils = new BitmapUtils(this);
		init();

	}
	@Override
	protected void onStart() {
		super.onStart();
		user = UserData.getUser();
		if(null != adapter)
			adapter.updateUser();
	}
	private void init() {
		user = UserData.getUser();
		_50dp = Util.dpToPx(this, 50F);  
		Intent intent = getIntent();
		userId = intent.getStringExtra("userId");
		message_board_userId = (TextView) findViewById(R.id.message_board_userId);
		if (!Util.isNull(userId)) {
			
			
			message_board_userId.setText(Util.getNo_pUserId(userId));
			
			if (userId.equals("远大云商008")) {
				message_board_userId.setText(Util.getNo_pUserId("远大云商"));
			}
			
		}
		message_board_time.setVisibility(View.GONE);
		NewWebAPI.getNewInstance().getLoginInfo(userId, new WebRequestCallBack(){
			@Override
			public void success(Object result) {
				if(!Util.isNull(result)){
					JSONObject obj =  JSON.parseObject(result.toString());
					if(200 == obj.getIntValue("code")){
						message_board_time.setText("来自："+obj.getString("source")+"\u3000于"+Util.friendly_time(obj.getString("loginTime")));
						message_board_time.setVisibility(View.VISIBLE);
					}
				}
			}			
		});
		userface = intent.getStringExtra("userface");
		Util.initTop(this, "心情", Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Util.showIntent(UserMessageBoardFrame.this, MessageBoardFrame.class);
			}
		});
		firstpage();  
		scrollPage(); 
		refreshable_view.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				page=1;
				initData(false);
			}
		},0);
		getUserFace();
		getUserBackground();
	}
	@OnClick(R.id.user_bg_layout)
	public void userBgOnclick(View vi) {
		if(null != user){
			if(userId.equals(user.getUserIdNoEncodByUTF8().trim())){
				Intent intent = new Intent(UserMessageBoardFrame.this,
						SelectPicActivity.class);
				UserMessageBoardFrame.this.startActivityForResult(intent, 100);
			}
		}
	}
	private void upload(final Bitmap bm, final String picTypes) {
		Util.asynTask(UserMessageBoardFrame.this, "", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if ((runData + "").contains("success:")) {
					Toast.makeText(UserMessageBoardFrame.this, "图片上传成功", 1).show();
					getUserBackground();
				} else {
					Util.show("上传失败！", UserMessageBoardFrame.this);
				}
			}

			@SuppressLint("NewApi")
			@Override
			public Serializable run() {
				String result = "";
				String NAMESCROPE = "http://mynameislin.cn/";
				String METHOD_NAME = "uploadUserBanner";
				String URL = "http://" + Web.webImage+ "/Convenience_services.asmx";
				String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
				StringBuilder sb = new StringBuilder();
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
					bm.compress(compressFormat, 85, out);// 将Bitmap压缩到ByteArrayOutputStream中
					InputStream sbs = new ByteArrayInputStream(out.toByteArray());
					byte[] buffer = new byte[30 * 1024];
					int count = 0;
					int i = 0;
					String fileName = "mood_"+ java.util.UUID.randomUUID().toString() + "."+picTypes;
					Log.i("fileName", fileName);
					while ((count = sbs.read(buffer)) >= 0) {
						String uploadBuffer = new String(Base64.encode(buffer,0, count, Base64.DEFAULT));
						SoapObject request = new SoapObject(NAMESCROPE,METHOD_NAME);
						request.addProperty("userId",user.getUserId());
						request.addProperty("userPaw",user.getMd5Pwd());
						Date curDate = new Date(System.currentTimeMillis());
						request.addProperty("userKey", Util.getUSER_KEY(curDate));
						request.addProperty("userKeyPwd", Util.getUSER_KEYPWD(curDate));
						request.addProperty("image", uploadBuffer);
						request.addProperty("userNo", UserData.getUser().getUserNo());
						request.addProperty("tag", i);
						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
								SoapEnvelope.VER11);
						envelope.bodyOut = request;
						envelope.dotNet = true;
						envelope.setOutputSoapObject(request);

						HttpTransportSE ht = new HttpTransportSE(URL);
						ht.debug = true;
						try {
							ht.call(SOAP_ACTION, envelope);
							Object obj = envelope.bodyIn;
							String s = obj.toString();
							System.out.println("obj.toString()================"+ obj.toString());
							result = s.substring(s.indexOf("success"),s.indexOf(";"));
						} catch (Exception e) {
							e.printStackTrace();
						}
						i++;
					}
					out.close();
					sbs.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				sb.append(result);
				return sb.toString();
			}
		});
	}
	private void firstpage() {
		if(adapter!=null){
			if(adapter.map!=null){
				adapter.map.clear();
			}
			if(adapter.list!=null){
				adapter.list.clear();
			}
		}
		initData(true);
	}
	private void scrollPage() {
		listview.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					initData(true);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}
	private void initData(final boolean isShowProgressDialog) {
		IAsynTask asynTask = new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (null == runData) {
					Util.show("网络错误，请重试！", UserMessageBoardFrame.this);
					return;
				}
				HashMap<String, List<UserMessageBoard>> map = (HashMap<String, List<UserMessageBoard>>) runData;
				list = map.get("list");
				if(!isShowProgressDialog){
					if(adapter!=null){
						adapter.list.clear();
						adapter.notifyDataSetChanged();
						if(adapter.map!=null){
							adapter.map.clear();
						}
					}
				}
				if (list.size() > 0) {
					if (adapter == null) {
						adapter = new XQAdapter(UserMessageBoardFrame.this,null);
						adapter.setList(list);
						listview.setAdapter(adapter);
					} else {
						adapter.setList(list);
						adapter.notifyDataSetChanged();
					}
				} else if (list.size() == 0) {
					page--;// 将page会滚到上一页
				}
				refreshable_view.finishRefreshing();
			}

			@Override 
			public Serializable run() {
				String loginUser = "";
				if(null != user)
					loginUser = user.getUserId();
				Web web = new Web(Web.getUserMessageBoard, "userId=" + userId
						+ "&page=" + (page++) + "&size=" + size+"&loginUser="+loginUser);
				List<UserMessageBoard> list = web.getList(UserMessageBoard.class);
				HashMap<String, List<UserMessageBoard>> map = new HashMap<String, List<UserMessageBoard>>();
				map.put("list", list);
				return map;
			}
		};
		if(isShowProgressDialog)
			Util.asynTask(this, "正在获取网友心情...", asynTask);
		else
			Util.asynTask(asynTask);
	}
	private void getUserFace() {
		bmUtils.display(userfaceView, userface,
				new DefaultBitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View container, String uri,
							Bitmap bitmap, BitmapDisplayConfig config,
							BitmapLoadFrom from) {
						Bitmap zoomBm = Util.zoomBitmap(bitmap, _50dp, _50dp);
						super.onLoadCompleted(container, uri,
								Util.getRoundedCornerBitmap(zoomBm), config,
								from);
					}

					@Override
					public void onLoadFailed(View container, String uri,
							Drawable drawable) {
						Resources r = UserMessageBoardFrame.this.getResources();
						InputStream is = r
								.openRawResource(R.drawable.ic_launcher);
						BitmapDrawable bmpDraw = new BitmapDrawable(is);
						Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(),
								_50dp, _50dp);
						userfaceView.setImageBitmap(Util
								.getRoundedCornerBitmap(zoomBm));
					}
				});
	}
	private void getUserBackground() {
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				String result = (String) runData;
				Log.i("UserMessageBoardFrame", result);
				if (Util.isNull(result)) {
					user_bg_layout.setImageResource(R.drawable.message_board_banner2);
				} else {
					bmUtils.display(user_bg_layout, result,
							new BitmapLoadCallBack<View>() {
								@Override
								public void onLoadCompleted(View container,
										String uri, Bitmap bitmap,
										BitmapDisplayConfig config,
										BitmapLoadFrom from) {
									user_bg_layout.setImageBitmap(bitmap);
								}
								@Override
								public void onLoadFailed(View arg0,
										String arg1, Drawable arg2) {
									user_bg_layout.setImageResource(R.drawable.message_board_banner2);
								}
							});
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service, Web.getUserBannerList,
						"userId="+Util.get(userId));
				String result = web.getPlan();
				return result;
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			if (!Util.isNull(data)) {
				final String picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
				String pictype = picPath.substring(picPath.lastIndexOf(".") + 1, picPath.length());
				final int _1024dp = Util.dpToPx(this, 600F);
				final int _768dp = Util.dpToPx(this, 130F);
				final Bitmap tagBm = Util.getLocationThmub(picPath, _1024dp, _768dp);
				user_bg_layout.setImageBitmap(tagBm);
				upload(tagBm, pictype);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    @Override
    protected void onDestroy() {
	    super.onDestroy();
    }
    @Override
    protected void onStop() {
    	super.onStop();
    }
}
