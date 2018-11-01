package com.lin.component;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.exception.DbException;
import com.mall.model.LocationModel;
import com.mall.model.User;
import com.mall.model.messageboard.UserMessageBoard;
import com.mall.model.messageboard.UserMessageBoardCache;
import com.mall.model.messageboard.UserMessageBoardPraise;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LMSJDetailFrame;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.messageboard.FaceConversionUtil;
import com.mall.view.messageboard.MessageBoardComment;
import com.mall.view.messageboard.MessageBoardPicShow;
import com.mall.view.messageboard.MyToast;
import com.mall.view.messageboard.NoReaderFrame;
import com.mall.view.messageboard.ParsiseBoardList;
import com.mall.view.messageboard.UserMessageBoardFrame;
import com.mall.view.service.UploadService;
import com.mall.view.service.UploadService.UploadBinder;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class XQAdapter extends BaseAdapter {
	private PopupWindow distancePopup;
	private Context c;
	private LayoutInflater inflater;
	public List<UserMessageBoard> list = new ArrayList<UserMessageBoard>();
	public static HashMap<Integer, View> map = new HashMap<Integer, View>();
	private BitmapUtils bmUtils = null;
	private User user;
	private FrameLayout fram;
	private int _50dp = 50;
	private int _38dp = 38;
	private int _80dp = 80;
	private int _5dp = 5;
	private Bitmap default97;
	private Bitmap default38;
	private boolean isIndex = false;
	private List<ImageView> bannersView;
	private TextView[] dots = null;
	private boolean isContinue = true;
	private ViewPager banner;
	int imgindex = 0;
	private MyThread threa;
	private ListView listview;
	private boolean stop = false;
	private LinearLayout dotc;
	private DbUtils db;
	private UploadService uploadService;
	private UserMessageBoardCache umbcache;
	private String content = "";
	private String from = "";
	private boolean isCache = false;
	private List<Bitmap> images = new ArrayList<Bitmap>();
	private String localFilesPaths = "";
	private long id;
	public ServiceConnection uploadImageServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			UploadService.UploadBinder uploadBinder = (UploadBinder) service;
			uploadService = uploadBinder.getService();
			umbcache.setUserFace(UserData.getUser().getUserFace());
			uploadService.setContent(content);
			uploadService.setContext(c);
			uploadService.setId(id);
			uploadService.setBitmaps(images);
			uploadService.setImgLocalFiles(localFilesPaths);
		}
	};
	private Handler bannerHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isContinue) {
				if (null != msg.obj) {
					banner.setCurrentItem(Integer.parseInt(msg.obj + ""));
					if (threa == null) {
						threa = new MyThread();
						threa.start();
					}
				}
			}
		};
	};

	public XQAdapter(Context c, ListView listview) {
		map.clear();
		this.list.clear();
		this.c = c;
		db = DbUtils.create(c, "xqcache");
		this.inflater = LayoutInflater.from(c);
		this.listview = listview;
		bmUtils = new BitmapUtils(c);
		_50dp = Util.dpToPx(c, 50F);
		_38dp = Util.dpToPx(c, 30F);
		_80dp = Util.dpToPx(c, 100f);
		_5dp = Util.dpToPx(c, 5F);
		user = UserData.getUser();
		umbcache = new UserMessageBoardCache();
		Resources r = c.getResources();
		InputStream is = r.openRawResource(R.drawable.ic_launcher);
		BitmapDrawable bmpDraw = new BitmapDrawable(is);
		default97 = Util.getRoundedCornerBitmap(Util.zoomBitmap(
				bmpDraw.getBitmap(), _50dp, _50dp));
		default38 = Util.getRoundedCornerBitmap(Util.zoomBitmap(
				bmpDraw.getBitmap(), _38dp, _38dp));
	}

	public void setFrame(FrameLayout fr) {
		this.fram = fr;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void updateUser() {
		user = UserData.getUser();
	}

	public void setIsIndex(boolean is) {
		this.isIndex = is;
	}

	public List<UserMessageBoard> getList() {
		return this.list;
	}

	public void clear() {
		this.list.clear();
	}

	public boolean isCache() {
		return isCache;
	}

	public void setCache(boolean isCache) {
		this.isCache = isCache;
	}

	public void finishThread() {
		this.setStop(true);
	}

	public void setStop(boolean st) {
		this.stop = st;
	}

	public void setList(List<UserMessageBoard> list) {
		if (map != null) {
			map.clear();
		}
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
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		MessageBoardHolder h = null;
		UserMessageBoard um = list.get(arg0);
		if (map.get(arg0) == null) {
			arg1 = inflater.inflate(R.layout.message_board_frame_item, null);
			h = new MessageBoardHolder();
			h.message_board_comment = (TextView) arg1
					.findViewById(R.id.message_board_comment);
			h.message_board_face = (ImageView) arg1
					.findViewById(R.id.message_board_face);
			h.message_board_forward = (ImageView) arg1
					.findViewById(R.id.message_board_forward);
			h.message_board_images = (LinearLayout) arg1
					.findViewById(R.id.message_board_images);
			h.message_board_item = (LinearLayout) arg1
					.findViewById(R.id.message_board_item);
			h.message_board_message = (TextView) arg1
					.findViewById(R.id.message_board_message);
			h.message_board_praise = (TextView) arg1
					.findViewById(R.id.message_board_praise);
			h.message_board_priase_user = (LinearLayout) arg1
					.findViewById(R.id.message_board_priase_user);
			h.message_board_time = (TextView) arg1
					.findViewById(R.id.message_board_time);
			h.message_board_userId = (TextView) arg1
					.findViewById(R.id.message_board_userId);
			h.message_board_no_read = (TextView) arg1
					.findViewById(R.id.no_read);
			h.message_board_city = (TextView) arg1
					.findViewById(R.id.message_board_city);
			h.upload_state = (TextView) arg1.findViewById(R.id.upload_state);
			h.move_to_rubblish = (ImageView) arg1
					.findViewById(R.id.move_to_rubblish);
			h.bringtotop = (TextView) arg1.findViewById(R.id.bringtotop);
			bindItemData(h, um);
			map.put(arg0, arg1);
			arg1.setTag(h);
		} else {
			arg1 = map.get(arg0);
			h = (MessageBoardHolder) arg1.getTag();
		}
		if (isIndex && arg0 == 0) {
			if (null != listview) {
				banner.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_MOVE:
							listview.requestDisallowInterceptTouchEvent(true);
							break;
						case MotionEvent.ACTION_UP:
						case MotionEvent.ACTION_CANCEL:
							listview.requestDisallowInterceptTouchEvent(false);
							break;
						}
						return false;
					}
				});
			}
			h.app_banner_close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					isIndex = false;
				}
			});
			// 第一次发出消息
			sendMessage();
		}
		final UserMessageBoard umm = um;
		if (!Util.isNull(um.getFlag()) && "no".equals(um.getFlag())) {
			if (!Util.isNull(from) && !from.equals("writemessage")
					&& !Util.isNull(um.getFrom())
					&& um.getFrom().equals("secondtime")) {// 是否是已经上传过了
				h.upload_state.setVisibility(View.GONE);
			}
			h.upload_state.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					content = umm.getContent();
					if (!Util.isNull(umm.getList())) {
						images.addAll(umm.getList());
					}
					id = umm.getDateaId();
					localFilesPaths = umm.getLocalFilesPaths();
					localFilesPaths = localFilesPaths.replace("spkxqadapter",
							"|,|");
					c.getApplicationContext().bindService(
							new Intent(c,UploadService.class),
							uploadImageServiceConnection,
							Context.BIND_AUTO_CREATE);
					v.setVisibility(View.GONE);
					Toast.makeText(c, "心情已转入后台上传，请下拉刷新查看!", Toast.LENGTH_LONG)
							.show();
				}
			});
		}
		final UserMessageBoard umf = um;
		if (user != null) {
	
			Log.e("名字", umf.getUserId()+"间隔"+user.getNoUtf8UserId());
			if (umf.getUserId().equals(user.getNoUtf8UserId())
					|| "远大mall".equals(user.getNoUtf8UserId())
					|| "东京暖冬".equals(user.getNoUtf8UserId())
				
					) {
				h.move_to_rubblish.setVisibility(View.VISIBLE);
				
			} else if(umf.getUserId().equals("远大云商")&&user.getNoUtf8UserId().equals("远大云商008")){
				h.move_to_rubblish.setVisibility(View.VISIBLE);
			} 
			
			else {
				h.move_to_rubblish.setVisibility(View.GONE);
			}
		}
		h.move_to_rubblish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteDialog(umf, arg0);

			}
		});
		arg1.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (null == user)
					return true;
				if (umf.getUserId().equals(user.getNoUtf8UserId())
						|| "远大mall".equals(user.getNoUtf8UserId())
						|| "东京暖冬".equals(user.getNoUtf8UserId())) {
					deleteDialog(umf, arg0);
				}
				return true;
			}
		});
		if (arg0 == 0) {
			if (um.getUserId().equals("远大云商")|um.getUserId().equals("远大云商008")) {
				h.bringtotop.setVisibility(View.VISIBLE);
				h.bringtotop.setTextColor(c.getResources().getColor(
						R.color.headertop));
				h.bringtotop.setText("已置顶");
			}
		}
		return arg1;
	}

	private void deleteDialog(final UserMessageBoard umf, final int position) {
		final VoipDialog dialog = new VoipDialog("是否需要删除心情", c, "确定", "否",
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (Util.isNull(umf.getId())) {
							try {
								db.deleteById(UserMessageBoardCache.class,
										umf.getDateaId());
								map.clear();
								list.remove(position);
								XQAdapter.this.notifyDataSetChanged();
							} catch (DbException e) {
								e.printStackTrace();
							}
						} else {
							if (user != null) {
								final CustomProgressDialog dialog = Util
										.showProgress("正在删除", c);
								NewWebAPI.getNewInstance().deleteMood(
										user.getUserId(), user.getMd5Pwd(),
										umf.getId(), new WebRequestCallBack() {
											@Override
											public void success(Object result) {
												MyToast.makeText(c, "删除成功", 5)
														.show();
												map.clear();
												list.remove(position);
												XQAdapter.this
														.notifyDataSetChanged();
												dialog.cancel();
												dialog.dismiss();
												super.success(result);
											}
										});
							} else {
								MyToast.makeText(c, "您还没有登录，请先登录", 25).show();
							}
						}
					}
				}, null);
		dialog.show();
	}

	public void sendMessage() {
		Message msg = bannerHandler.obtainMessage();
		msg.what = 1;
		msg.obj = 0;
		bannerHandler.sendMessage(msg);
		BannerMethod(banner, dotc);
	}

	private void getUnReaderComment(final TextView noread) {
		if (null != UserData.getUser()) {
			String userId = UserData.getUser().getUserId();
			String md5Pwd = UserData.getUser().getMd5Pwd();
			NewWebAPI.getNewInstance().getUnReadMoodCommentCount(userId,
					md5Pwd, new WebRequestCallBack() {
						@Override
						public void success(Object result) {
							if (null == result) {
								Util.show("网络异常，请稍后...", c);
								return;
							}
							JSONObject obj = JSON.parseObject(result.toString());
							if (200 == obj.getIntValue("code")) {
								int amount = obj.getIntValue("message");
								if (1 > amount)
									return;
								noread.setText(" " + amount + " ");
								noread.setVisibility(View.VISIBLE);
							} else {
								Util.show(obj.getString("message"), c);
							}
						}
					});
		}
	}

	// 绑定Item事件
	public void bindItemData(final MessageBoardHolder holder,
			final UserMessageBoard umb) {
		if (!umb.getNoRead().equals("0")) {
			holder.message_board_no_read.setVisibility(View.VISIBLE);
			holder.message_board_no_read.setText(umb.getNoRead());
		}
		Log.e("头像",umb.getUserFace());
		// 显示头像
		if (!Util.isNull(umb.getUserFace())) {
			
			Log.e("头像1","不为空");
			
			//new_huiyuan_logo
			
			if (umb.getUserId().equals("远大云商")|umb.getUserId().equals("远大云商008")) {
				Resources r = c.getResources();
				InputStream is = r
						.openRawResource(R.drawable.new_huiyuan_logo);
				BitmapDrawable bmpDraw = new BitmapDrawable(is);
				Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(), _50dp, _50dp);
				holder.message_board_face.setImageBitmap(Util
						.getRoundedCornerBitmap(zoomBm));
			}else{
				
			
			
			
			bmUtils.display(holder.message_board_face, umb.getUserFace(),
					new DefaultBitmapLoadCallBack<View>() {
						@Override
						public void onLoadCompleted(View container, String uri,
								Bitmap bitmap, BitmapDisplayConfig config,
								BitmapLoadFrom from) {
							Bitmap zoomBm = Util.zoomBitmap(bitmap, _50dp,
									_50dp);
							super.onLoadCompleted(container, uri,
									Util.getRoundedCornerBitmap(zoomBm),
									config, from);
						}

						@Override
						public void onLoadFailed(View container, String uri,
								Drawable drawable) {
							Resources r = c.getResources();
							InputStream is = r
									.openRawResource(R.drawable.ic_launcher_black_white);
							BitmapDrawable bmpDraw = new BitmapDrawable(is);
							Bitmap zoomBm = Util.zoomBitmap(
									bmpDraw.getBitmap(), _50dp, _50dp);
							holder.message_board_face.setImageBitmap(Util
									.getRoundedCornerBitmap(zoomBm));
						}
					});
		}
		} else {
			Log.e("头像1","为空");
			if (umb.getUserId().equals("远大云商")|umb.getUserId().equals("远大云商008")) {
				Resources r = c.getResources();
				InputStream is = r
						.openRawResource(R.drawable.new_huiyuan_logo);
				BitmapDrawable bmpDraw = new BitmapDrawable(is);
				Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(), _50dp, _50dp);
				holder.message_board_face.setImageBitmap(Util
						.getRoundedCornerBitmap(zoomBm));
			}else{
				Resources r = c.getResources();
				InputStream is = r
						.openRawResource(R.drawable.ic_launcher_black_white);
				BitmapDrawable bmpDraw = new BitmapDrawable(is);
				Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(), _50dp, _50dp);
				holder.message_board_face.setImageBitmap(Util
						.getRoundedCornerBitmap(zoomBm));
			}
	
		}
		// 头像点击，查看该用户的信息
		holder.message_board_face.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!c.getClass().getSimpleName().toLowerCase()
						.contains("usermessageboardframe")) {
					Intent intent = new Intent(c, UserMessageBoardFrame.class);
					intent.putExtra("userId", umb.getUserId());
					intent.putExtra("userface", umb.getUserFace());
					c.startActivity(intent);
				}
			}
		});
		if (umb.getUserId().equals("远大云商")|umb.getUserId().equals("远大云商008")) {
			holder.message_board_userId.setText("远大云商");
		}else{
			holder.message_board_userId.setText(Util.getMood_No_pUserId(umb
					.getUserId()));
		}
		
		String come = "来自";
		if (!Util.isNull(umb.getCity()))
			come += umb.getCity();
		else
			come += "家人社区";
		holder.message_board_city.setText(come);
		holder.message_board_time.setText(Util.friendly_time(umb
				.getCreateTime()));
		Log.e("发布内容",umb.getContent()+"HHHH");
		SpannableString spannableString = FaceConversionUtil.getInstace()
				.getExpressionString(c, umb.getContent());
		holder.message_board_message.setText(spannableString);
		holder.message_board_message
				.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {

						final Dialog dialog = new Dialog(c,
								R.style.FullHeightDialog);
						dialog.setContentView(R.layout.community_find_transmit);

						LinearLayout ll = (LinearLayout) dialog
								.findViewById(R.id.ll_zz_dialog);
						TextView tv_copy = (TextView) dialog
								.findViewById(R.id.tv_community_item_copy);
						TextView tv_zz = (TextView) dialog
								.findViewById(R.id.tv_community_item_zhuanzan);
						tv_copy.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								dialog.dismiss();
								ClipboardManager clip = (ClipboardManager) c
										.getSystemService(c.CLIPBOARD_SERVICE);
								clip.setText(holder.message_board_message
										.getText().toString());

								Util.show("复制成功!", c);

							}
						});

						tv_zz.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								final String text = umb.getContent();
								final String picUrl = umb
										.getFiles()
										.replaceAll("http://img.yda360.com//",
												"")
										.replaceAll("\\|,\\|", "*|-_-|*");
								String city = "";
								String lat = "";
								String lon = "";
								LocationModel position=LocationModel.getLocationModel();
								Log.e("位置信息",position.toString());
								String province = position.getProvince();
								String ci = position.getCity();
								if (!Util.isNull(province) && !Util.isNull(ci)) {
									city = Util.get(province + ci);
								}
								if (!Util.isNull(position.getLocationType())) {
									lat = position.getLatitude()
											+ "";
									lon =  position.getLongitude()
											+ "";
								}

								final String flat = lat;
								final String flon = lon;
								final String adds = city;
								dialog.cancel();
								dialog.dismiss();

								Util.asynTask(c, "转载中...", new IAsynTask() {

									@Override
									public void updateUI(Serializable runData) {
										if ((runData + "").contains("success")) {
											Util.show("转载成功!", c);

										} else
											Util.show(runData + "", c);

									}

									@Override
									public Serializable run() {
										Web web = new Web(
												Web.addUserMessageBoard,
												"userId="
														+ UserData.getUser()
																.getUserId()
														+ "&userPaw="
														+ UserData.getUser()
																.getMd5Pwd()
														+ "&message=" + text
														+ "&files=" + picUrl
														+ "&forward=-1"
														+ "&city=" + adds
														+ "&lat=" + flat
														+ "&lon=" + flon);
										return web.getPlan();

									}
								});

							}
						});
						dialog.show();

						return true;
					}
				});
		// 显示心情带的的图片
		String imgPaths = "";
		if (!Util.isNull(umb.getFiles())) {
			imgPaths = umb.getFiles();
		} else if (!Util.isNull(umb.getLocalFilesPaths())) {
			imgPaths = umb.getLocalFilesPaths();
			imgPaths = imgPaths.replace("spkxqadapter", "|,|");
		}
		String[] files = imgPaths.split("\\|,\\|");
		final String imfFiles = imgPaths;
		int end = files.length;
		List<String> listtest = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			String newUrl = files[i];
			if (files[i].contains("mood_")) {
				newUrl = files[i].replace("mood_", "");
			}
			listtest.add(newUrl);
		}
		end = listtest.size();
		int rowsize = 3;
		rowsize = 3;
		int rows = 0;
		if (end % rowsize == 0) {
			rows = end / rowsize;
		} else {
			rows = end / rowsize + 1;
		}
		if (Util.isNull(umb.getFiles())
				&& Util.isNull(umb.getLocalFilesPaths())) {
			rows = 0;
		}
		// 最多显示三排
		if (rows > 3) {
			rows = 3;
		}
		for (int i = 0; i < rows; i++) {
			LinearLayout l = (LinearLayout) inflater.inflate(
					R.layout.message_xq_pic_row, null);
			LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, _80dp);
			l.setLayoutParams(lp);
			for (int j = 0; j < rowsize; j++) {
				final ImageView img = (ImageView) l.getChildAt(2 * j + 1);
				img.setTag(rowsize * i + j);
				img.setScaleType(ScaleType.CENTER_CROP);
				if (rowsize * i + j >= end) {
					img.setImageResource(R.drawable.ic_arrow);
					img.setOnClickListener(null);
				} else {
					final String path = listtest.get(rowsize * i + j);
					bmUtils.display(img, listtest.get(rowsize * i + j),
							new BitmapDisplayConfig(),
							new BitmapLoadCallBack<View>() {
								@Override
								public void onLoadCompleted(View arg0,
										String arg1, Bitmap arg2,
										BitmapDisplayConfig arg3,
										BitmapLoadFrom arg4) {
									img.setImageBitmap(arg2);
								}

								@Override
								public void onLoadFailed(View arg0,
										String arg1, Drawable arg2) {
									img.setImageBitmap(Util.getLocationThmub(
											path, 100, 100));
								}
							});
					img.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							ImageView imgv = (ImageView) arg0;
							Intent intent = new Intent(c,
									MessageBoardPicShow.class);
							Log.e("图片地址",imfFiles);
							intent.putExtra("picFiles", imfFiles);
							intent.putExtra("currentPic", imgv.getTag()
									.toString());
							c.startActivity(intent);
						}
					});
				}
			}
			holder.message_board_images.addView(l);
		}
		if (0 != end) {
			holder.message_board_images.setVisibility(View.VISIBLE);
		}
		holder.message_board_praise.setText(umb.getPraise());
		if ("1".equals(umb.getPraiseState())) {
			Resources res = this.c.getResources();
			Drawable praise = res
					.getDrawable(R.drawable.message_board_praise_click);
			praise.setBounds(0, 0, praise.getMinimumWidth(),
					praise.getMinimumHeight());
			holder.message_board_praise.setCompoundDrawables(null, null,
					praise, null);
		}
		holder.message_board_praise.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!Util.isNull(umb.getId())) {
					praiseClick(v, holder, umb);
				}
			}
		});
		holder.message_board_comment.setText(umb.getComments());
		holder.message_board_comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (null == user) {
					Util.showIntent("对不起，请先登录！", c, LoginFrame.class);
					return;
				}
				Intent intent = new Intent(c, MessageBoardComment.class);
				intent.putExtra("id", umb.getId());
				intent.putExtra("userId", umb.getUserId());
				Bundle b = new Bundle();
				b.putSerializable("UserMessageBoard", umb);
				intent.putExtras(b);
				if (!Util.isNull(umb.getId())) {
					c.startActivity(intent);
				}
			}
		});
		holder.message_board_forward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!Util.isNull(umb.getId())) {
					fenxiangClick(umb);
				}
			}
		});
	
		// 显示最新赞的人
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<String, List<UserMessageBoardPraise>> map = (HashMap<String, List<UserMessageBoardPraise>>) runData;
				List<UserMessageBoardPraise> list = map.get("list");
				LinearLayout.LayoutParams praiseLP = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				for (UserMessageBoardPraise ump : list) {
					final ImageView img = new ImageView(c);
					img.setPadding(0, 0, _5dp, 0);
					img.setLayoutParams(praiseLP);
					img.setAdjustViewBounds(true);
					if (!Util.isNull(ump.getUserFace())) {
						bmUtils.display(img, ump.getUserFace(),
								new DefaultBitmapLoadCallBack<View>() {
									@Override
									public void onLoadCompleted(View container,
											String uri, Bitmap bitmap,
											BitmapDisplayConfig config,
											BitmapLoadFrom from) {
										Bitmap bmp = Util.zoomBitmap(bitmap,
												_38dp, _38dp);
										super.onLoadCompleted(
												container,
												uri,
												Util.getRoundedCornerBitmap(bmp),
												config, from);
									}

									@Override
									public void onLoadFailed(View container,
											String uri, Drawable drawable) {
										img.setImageBitmap(default38);
									}
								});
					} else {

						img.setImageBitmap(default38);
					}
					img.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent(c,
									ParsiseBoardList.class);
							intent.putExtra("mid", umb.getId());
							c.startActivity(intent);
						}
					});
					holder.message_board_priase_user.addView(img);
				}
			}

			@Override
			public Serializable run() {
				HashMap<String, List<UserMessageBoardPraise>> map = new HashMap<String, List<UserMessageBoardPraise>>();
				if (null == umb) {
					map.put("list", new ArrayList<UserMessageBoardPraise>());
				} else {
					Web web = new Web(Web.getUserMessageBoardPraise, "id="
							+ umb.getId() + "&page=1&size=5");
					List<UserMessageBoardPraise> list = web
							.getList(UserMessageBoardPraise.class);

					map.put("list", list);
				}
				return map;
			}
		});
	}

	// 点赞
	private void praiseClick(View view, final MessageBoardHolder holder,
			final UserMessageBoard umb) {
		if (null == user) {
			Util.showIntent("您还没登录，请先登录！", this.c, LoginFrame.class);
			return;
		}
		view.setEnabled(false);
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (null == runData) {
					Util.show("网络错误，请重试！", XQAdapter.this.c);
					return;
				}
				Resources res = XQAdapter.this.c.getResources();
				Drawable praise = null;
				String currPraise = holder.message_board_praise.getText()
						.toString();
				if ("success:已赞".equals(runData + "")) {
					praise = res
							.getDrawable(R.drawable.message_board_praise_click);
					holder.message_board_praise.setText((Util
							.getInt(currPraise) + 1) + "");
				} else {
					praise = res.getDrawable(R.drawable.message_board_praise);
					holder.message_board_praise.setText((Util
							.getInt(currPraise) - 1) + "");
				}
				praise.setBounds(0, 0, praise.getMinimumWidth(),
						praise.getMinimumHeight());
				holder.message_board_praise.setCompoundDrawables(null, null,
						praise, null);
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.operateUserMessageBoardPraise, "mid="
						+ umb.getId() + "&userId=" + user.getUserId()
						+ "&userPaw=" + user.getMd5Pwd());
				return web.getPlan();
			}
		});
		view.setEnabled(true);
	}

	public void fenxiangClick(UserMessageBoard umb) {
		String url = "";
		final String title = "分享" + Util.getMood_No_pUserId(umb.getUserId())
				+ "的心情";
		String imageUrl = "http://www.yda360.com/newPage/130926/images/logo.png", imageFiles = "";
		imageFiles = umb.getFiles();
		if (!Util.isNull(imageFiles)) {
			imageUrl = imageFiles.split("\\|,\\|")[0].replace("mood_", "");
		} else {
			if (!Util.isNull(umb.getUserFace())) {
				imageUrl = umb.getUserFace();
			}
		}
		OnekeyShare oks = new OnekeyShare();
		oks.setTitle("心情分享");
		oks.setTitleUrl(imageUrl);
		oks.setUrl(imageUrl);
		oks.setAddress("10086");
		oks.setText(umb.getContent());
		oks.setSite("远大云商");
		oks.setSiteUrl(url);
		oks.setSilent(false);
		oks.setImageUrl(imageUrl);
		oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
			@Override
			public void onShare(Platform platform, ShareParams paramsToShare) {
				if ("ShortMessage".equals(platform.getName())) {
					paramsToShare.setImageUrl(null);
					
				}
			}
		});
		oks.show(c);
	}

	private void initBannersView() {
		bannersView = new ArrayList<ImageView>();
		bannersView.clear();
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, 70);

		ImageView banner1 = new ImageView(c);
		bmUtils.display(banner1, "assets/mood1.jpg");
		banner1.setTag("http://www.yda360.com/shopCollects/shopCollectsPage.aspx?cid=268");
		bindBannerItemClick(268, "茗仁缘茶莊", "24.984619", "102.71462", banner1);
		banner1.setLayoutParams(lp);

		ImageView banner2 = new ImageView(c);
		bmUtils.display(banner2, "assets/mood2.jpg");
		banner2.setTag("http://www.yda360.com/shopCollects/shopCollectsPage.aspx?cid=1369");
		bindBannerItemClick(1369, "禧相逢KTV", "24.002494", "102.180764", banner2);
		banner2.setLayoutParams(lp);

		// ImageView banner3 = new ImageView(c);
		// bmUtils.display(banner3, "assets/mood3.jpg");
		// banner3.setTag("http://www.yda360.com/shopCollects/shopCollectsPage.aspx?cid=2360");
		// bindBannerItemClick(2360, "阿波罗温泉水疗", "25.490636", "103.785004",
		// banner3);
		// banner3.setLayoutParams(lp);

		banner1.setScaleType(ScaleType.CENTER_CROP);
		banner2.setScaleType(ScaleType.CENTER_CROP);
		// banner3.setScaleType(ScaleType.CENTER_CROP);

		bannersView.add(banner1);
		bannersView.add(banner2);
		// bannersView.add(banner3);

	}

	private void bindBannerItemClick(final int id, final String name,
			final String x, final String y, final ImageView item) {
		item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.showIntent(c, LMSJDetailFrame.class, new String[] { "id",
						"name", "x", "y" },
						new String[] { id + "", name, x, y });
			}
		});
	}

	private void initDotContainer(LinearLayout dotcontainer) {
		dotcontainer.removeAllViews();
		dots = new TextView[bannersView.size()];
		for (int i = 0; i < bannersView.size(); i++) {
			TextView dot;
			if (i == 0) {
				dot = createDot();
				dot.setBackgroundResource(R.drawable.dot_corner_round);
				dotcontainer.addView(dot);
				dots[i] = dot;
			} else {
				dot = createDot();
				dot.setBackgroundResource(R.drawable.dot_corner_round_normal);
				dotcontainer.addView(dot);
				dots[i] = dot;
			}
		}
	}

	private TextView createDot() {
		TextView te = new TextView(c);
		int _10dp = Util.dpToPx(c, 6);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(_10dp,
				_10dp);
		params.setMargins(5, 0, 5, 0);
		te.setLayoutParams(params);
		return te;
	}

	private void BannerMethod(final ViewPager banner, LinearLayout layout) {
		initBannersView();
		initDotContainer(layout);
		banner.setAdapter(new BannerAdapter());
		banner.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				banner.setCurrentItem(arg0);
				for (int i = 0; i < dots.length; i++) {
					if (arg0 == i) {
						dots[i].setBackgroundResource(R.drawable.dot_corner_round);
					} else {
						dots[i].setBackgroundResource(R.drawable.dot_corner_round_normal);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private class BannerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return bannersView.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(bannersView.get(arg1));
			return bannersView.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}

	class MessageBoardHolder {
		public LinearLayout message_board_item;
		public ImageView message_board_face;
		public TextView message_board_userId;
		public TextView message_board_time;
		public TextView message_board_message;
		public LinearLayout message_board_images;
		public TextView message_board_praise;
		public TextView message_board_comment;
		public LinearLayout message_board_priase_user;
		public ImageView message_board_forward;
		public TextView message_board_no_read;
		public TextView message_board_city;
		public TextView upload_state;
		public ImageView move_to_rubblish;
		public ImageView app_banner_close;
		FrameLayout frame;
		public TextView bringtotop;
	}

	public class MyThread extends Thread {
		@Override
		public void run() {
			if (stop) {
				return;
			} else {
				while (true && !stop) {
					int count = bannersView.size();
					for (int i = 0; i < count; i++) {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Message msdx = bannerHandler.obtainMessage();
						int index = banner.getCurrentItem() + 1;
						if (index >= bannersView.size()) {
							index = 0;
						}
						msdx.obj = index;
						bannerHandler.sendMessage(msdx);
					}
				}
			}
		}
	}

	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param content
	 */
	private void startPopupWindow(final String content) {
		View pview = inflater.inflate(R.layout.xq_pic_store, null, false);
		TextView store = (TextView) pview.findViewById(R.id.store);
		TextView cancel = (TextView) pview.findViewById(R.id.cancel);
		store.setText("复制到剪贴板");
		store.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClipboardManager clip = (ClipboardManager) c
						.getSystemService(Context.CLIPBOARD_SERVICE);
				clip.setText(content);
				Toast.makeText(c, "复制到剪贴板", Toast.LENGTH_LONG).show();
				distancePopup.dismiss();
			}
		});
		cancel.setText("取消");
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				distancePopup.dismiss();
			}
		});
		initpoputwindow(pview);
	}

	/**
	 * 新建一个popupwindow实例
	 * 
	 * @param view
	 */
	private void initpoputwindow(View view) {
		distancePopup = new PopupWindow(view,
				android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.FILL_PARENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		distancePopup.setAnimationStyle(R.style.popupanimationupanddown);
	}
}
