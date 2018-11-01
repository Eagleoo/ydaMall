package com.mall.view.messageboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.MessageBoardCommentModel;
import com.mall.model.User;
import com.mall.model.messageboard.UserMessageBoard;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageBoardComment extends Activity {
	@ViewInject(R.id.message_board_comment_list)
	private ListView listView;
	private EditText comment_edit;
	private Button submit;
	private String id = "";
	private int page = 0;
	private BitmapUtils bmUtils;
	private MessageBoardCommentAdapter adapter;
	private String userId = "";
	private int _50dp;
	private UserMessageBoard umb;
	@ViewInject(R.id.message_board_userId)
	private TextView message_board_userId;
	@ViewInject(R.id.message_board_time)
	private TextView message_board_time;
	@ViewInject(R.id.message_board_city)
	private TextView message_board_city;
	@ViewInject(R.id.message_board_face)
	private ImageView message_board_face;
	@ViewInject(R.id.message_board_message)
	private TextView message_board_message;
	@ViewInject(R.id.message_board_images2)
	private LinearLayout message_board_images2;
	private LayoutInflater inflater;
	@ViewInject(R.id.scrollvie)
	private ScrollView scroll;
	private int _80dp = 80;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_board_comment);
		ViewUtils.inject(this);
		bmUtils = new BitmapUtils(this);
		_50dp = Util.dpToPx(this, 50);
		user = UserData.getUser();
		if (user == null) {
			Toast.makeText(MessageBoardComment.this, "请先登录", Toast.LENGTH_LONG)
			.show();
			Util.showIntent(MessageBoardComment.this, LoginFrame.class);
		} else {
			init();
		}
	}

	private void init() {
		_80dp = Util.dpToPx(this, 80f);
		inflater = LayoutInflater.from(this);
		id = this.getIntent().getStringExtra("id");
		userId = this.getIntent().getStringExtra("userId");
		umb = (UserMessageBoard) getIntent().getSerializableExtra(
				"UserMessageBoard");
		if (umb != null) {
			bindMoodData(umb);
		}
		Util.initTop(this, "评论", Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Util.showIntent(MessageBoardComment.this,
						MessageBoardFrame.class);
			}
		});
		submit = (Button) this.findViewById(R.id.btn_send);
		comment_edit = (EditText) this.findViewById(R.id.et_sendmessage1);
		comment_edit.setTag(-7,"-1");
		comment_edit.setTag(-8,userId);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (null == UserData.getUser()) {
					Util.show("您还没登录，请先登录！", MessageBoardComment.this);
					Util.showIntent(MessageBoardComment.this, LoginFrame.class);
					return;
				}
				if (Util.isNull(comment_edit.getText().toString())) {
					Util.show("请输入评论内容", MessageBoardComment.this);
					return;
				}
				DoComment();
			}
		});
		firstpage();
		scrollPage();
	}

	private void bindMoodData(final UserMessageBoard umb) {
		// 显示头像
		bmUtils.display(message_board_face, umb.getUserFace(),
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
				Resources r = MessageBoardComment.this.getResources();
				InputStream is = r
						.openRawResource(R.drawable.ic_launcher);
				BitmapDrawable bmpDraw = new BitmapDrawable(is);
				Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(),
						_50dp, _50dp);
				message_board_face.setImageBitmap(Util
						.getRoundedCornerBitmap(zoomBm));
			}
		});
		// 头像点击，查看该用户的信息
		message_board_face.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MessageBoardComment.this,
						UserMessageBoardFrame.class);
				intent.putExtra("userId", umb.getUserId());
				intent.putExtra("userface", umb.getUserFace());
				MessageBoardComment.this.startActivity(intent);
			}
		});
		message_board_userId.setText(Util.getMood_No_pUserId(umb.getUserId()));
		message_board_city.setText(umb.getCity());
		message_board_time.setText(Util.friendly_time(umb.getCreateTime()));
		String con="";
		if(umb.getContent().length()>100){
			con=umb.getContent().substring(0,100)+"...";
		}else{
			con=umb.getContent();
		}  
		SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(this, "\u3000\u3000" + con);
		message_board_message.setText(spannableString);
		// 显示心情带的的图片
		String[] files = umb.getFiles().split("\\|,\\|");
		if (Util.isNull(umb.getFiles())) {
			LayoutParams lpsc = scroll.getLayoutParams();
			lpsc.height = LayoutParams.WRAP_CONTENT;
		}
		int end = files.length;
		List<String> listtest = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].contains("mood_")) {
				listtest.add(files[i].replace("mood_", ""));
			} else {
				listtest.add(files[i]);
			}
		}
		end = listtest.size();
		int rowsize = 3;
		int rows = 0;
		if (end % rowsize == 0) {
			rows = end / rowsize;
		} else {
			rows = end / rowsize + 1;
		}
		if (Util.isNull(umb.getFiles())) {
			rows = 0;
		}
		// 最多显示三排
		if (rows > 3) {
			rows = 3;
		}
		message_board_images2.removeAllViews();
		for (int i = 0; i < rows; i++) {
			LinearLayout l = (LinearLayout) inflater.inflate(
					R.layout.message_xq_pic_row, null);
			LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, _80dp);
			l.setLayoutParams(lp);
			for (int j = 0; j < rowsize; j++) {
				ImageView img = (ImageView) l.getChildAt(2 * j + 1);
				img.setTag(rowsize * i + j);
				if (rowsize * i + j >= end) {
					img.setImageResource(R.drawable.ic_arrow);
					img.setOnClickListener(null);
				} else {
					bmUtils.display(img, listtest.get(rowsize * i + j));
					img.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							ImageView imgv = (ImageView) arg0;
							Intent intent = new Intent(
									MessageBoardComment.this,
									MessageBoardPicShow.class);
							intent.putExtra("picFiles", umb.getFiles());
							intent.putExtra("currentPic", imgv.getTag()
									.toString());
							MessageBoardComment.this.startActivity(intent);
						}
					});
				}
			}
			message_board_images2.addView(l);
		}
	}

	private void firstpage() {
		bindData();
	}

	private void scrollPage() {
		listView.setOnScrollListener(new OnScrollListener() {
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

	private void bindData() {
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				@SuppressWarnings("unchecked")
				HashMap<String, List<MessageBoardCommentModel>> map = (HashMap<String, List<MessageBoardCommentModel>>) runData;
				List<MessageBoardCommentModel> resultList = map.get("list");
				if (resultList.size() > 0) {
					if (adapter == null) {
						adapter = new MessageBoardCommentAdapter(MessageBoardComment.this,comment_edit);
						adapter.setList(resultList);
						listView.setAdapter(adapter);
					} else {
						adapter.setList(resultList);
						adapter.notifyDataSetChanged();
					}
				} else if (resultList.size() == 0) {
					// Toast.makeText(MessageBoardComment.this,
					// "没有数据了",Toast.LENGTH_LONG).show();
					listView.setOnScrollListener(null);
					page--;// 将page会滚到上一页
				}

			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getUserMessageBoardCommentByID, "id="
						+ id + "&size=10" + "&page=" + (++page) + "&loginUser="
						+ user.getUserId());
				List<MessageBoardCommentModel> result = web
						.getList(MessageBoardCommentModel.class);
				HashMap<String, List<MessageBoardCommentModel>> map = new HashMap<String, List<MessageBoardCommentModel>>();
				map.put("list", result);
				return map;
			}           
		});
	}

	private void DoComment() {
		Util.asynTask(MessageBoardComment.this, "正在提交评论", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				String result = (String) runData;
				if ("success".equals(result)) {
					Util.show("评论成功", MessageBoardComment.this);
					comment_edit.setText("");
					comment_edit.setHint("请输入您的评论...");
					comment_edit.setTag(-7,"-1");
					comment_edit.setTag(-8,userId);
					page = 0;
					if (adapter != null) {
						adapter.list.clear();
					}
					bindData();
				}
			}

			@Override
			public Serializable run() {
				Log.e("发布信息","1"+userId);
				Web web = new Web(Web.addUserMessageBoardComment, "mid=" + id
						+ "&userId=" + UserData.getUser().getUserId()
						+ "&toUserId=" + userId + "&message="
						+ comment_edit.getText().toString() + "&userPaw="
						+ UserData.getUser().getMd5Pwd()+"&parentId="+comment_edit.getTag(-7));
				String result = web.getPlan();
				return result;
			}
		});
	}

	@OnClick(R.id.message_board_user_publish)
	public void publishUserClick(View view){
		comment_edit.setHint("回复楼主");
		comment_edit.setTag(-7,"-1");
		comment_edit.setTag(-8,userId);
	}

	@SuppressLint("UseSparseArrays")
	public class MessageBoardCommentAdapter extends BaseAdapter {
		private Context c;
		private EditText inputText;
		private LayoutInflater inflater;
		private List<MessageBoardCommentModel> list = new ArrayList<MessageBoardCommentModel>();

		public MessageBoardCommentAdapter(Context c,EditText inputText) {
			this.c = c;
			this.inputText = inputText;
			inflater = LayoutInflater.from(c);
		}

		private void setList(List<MessageBoardCommentModel> list) {
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
			final MessageBoardCommentModel msg = list.get(arg0);
			ViewHolder h = null;
			if (arg1 == null) {
				arg1 = inflater.inflate(R.layout.message_board_comment_item,
						null);
				h = new ViewHolder();
				h.item = (LinearLayout) arg1
						.findViewById(R.id.message_board_list_item);
				h.userface = (ImageView) arg1
						.findViewById(R.id.message_board_user_face);
				h.title = (TextView) arg1.findViewById(R.id.comment_title);
				h.time = (TextView) arg1.findViewById(R.id.comment_time);
				h.content = (TextView) arg1.findViewById(R.id.comment_detail);
				h.lou = (TextView) arg1.findViewById(R.id.comment_lou);
				arg1.setTag(h);
			} else {
				h = (ViewHolder) arg1.getTag();
			}
			h.title.setText(Util.getMood_No_pUserId(msg.getUserId()));
			h.time.setText(Util.friendly_time(msg.getCreateTime()));
			SpannableString spannableString = FaceConversionUtil.getInstace()
					.getExpressionString(c, msg.getMessage());
			h.content.setText(spannableString);
			final ViewHolder hh = h;
			h.lou.setText(msg.getExp2());
			final String hint = h.title.getText().toString();
			h.item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!msg.getUserId().equals(user.getNoUtf8UserId())){
						//inputText.setHint("回复"+msg.getExp2()+"：");
						//有昵称显示 否则显示几楼
						if(!Util.isNull(hint))
							inputText.setHint("回复"+hint+"：");
						
						else
							inputText.setHint("回复"+msg.getExp2()+"：");
						inputText.setTag(-7, msg.getId());
						inputText.setTag(-8, msg.getUserId());
					}else if(msg.getUserId().equals(user.getNoUtf8UserId())){
						inputText.setHint("回复:");
					}
				}
			});
			bmUtils.display(hh.userface, msg.getUserFace(),
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
							.openRawResource(R.drawable.ic_launcher);
					BitmapDrawable bmpDraw = new BitmapDrawable(is);
					Bitmap zoomBm = Util.zoomBitmap(
							bmpDraw.getBitmap(), _50dp, _50dp);
					hh.userface.setImageBitmap(Util
							.getRoundedCornerBitmap(zoomBm));
				}
			});
			return arg1;
		}
	}

	public class ViewHolder {
		LinearLayout item;
		TextView title;
		ImageView userface;
		TextView content;
		TextView time;
		TextView lou;
	}
}
