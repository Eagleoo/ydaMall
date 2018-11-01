package com.mall.view.messageboard;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lin.component.BaseMallAdapter;
import com.mall.model.messageboard.UserMessageBoard;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

public class NoReaderFrame extends Activity {

	private ListView listView;
	private BitmapUtils bmUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.no_reader_comment_frame);
		listView = (ListView) this.findViewById(R.id.no_reader_list);
		bmUtils = new BitmapUtils(this);
		Util.initTop(this, "回复我的", Integer.MIN_VALUE, null);
		if(null == UserData.getUser())
			return ;
		String userId = UserData.getUser().getUserId();
		String md5Pwd = UserData.getUser().getMd5Pwd();
		NewWebAPI.getNewInstance().getUnReadMoodComment(userId, md5Pwd, new WebRequestCallBack(){
			@Override
			public void success(Object result) {
				if(Util.isNull(result)){
					Util.show("网络异常，请重试！", NoReaderFrame.this);
					return ;
				}
				JSONObject obj = JSON.parseObject(result.toString());
				if(200 == obj.getIntValue("code") && 0 < obj.getIntValue("message")){
					JSONArray array = obj.getJSONArray("list");
					listView.setAdapter(new BaseMallAdapter<JSONObject>(R.layout.message_board_frame_item,NoReaderFrame.this,array.toArray(new JSONObject[]{})) {
						@Override
						public View getView(int position, View convertView,
								ViewGroup parent, final JSONObject t) {
							getCacheView(R.id.message_board_frame_item_bottom).setVisibility(View.GONE);
							final String userFace = UserData.getUser().getUserFace();
							bmUtils.display(getCacheView(R.id.message_board_face), userFace,new DefaultBitmapLoadCallBack<View>(){

								@Override
								public void onLoadCompleted(View container,
										String uri, Bitmap bitmap,
										BitmapDisplayConfig config,
										BitmapLoadFrom from) {
									super.onLoadCompleted(container, uri, Util.getRoundedCornerBitmap(bitmap), config, from);
								}

								@Override
								public void onLoadFailed(View container,
										String uri, Drawable drawable) {
									ImageView img = (ImageView) container;
									int _50dp = Util.dpToPx(NoReaderFrame.this, 50F);
									Resources r = NoReaderFrame.this.getResources();
									InputStream is = r.openRawResource(R.drawable.ic_launcher);
									BitmapDrawable bmpDraw = new BitmapDrawable(is);
									Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(),_50dp, _50dp);
									img.setImageBitmap(Util.getRoundedCornerBitmap(zoomBm));
								}
								
							});
							setText(R.id.message_board_message,"\u3000\u3000"+t.getString("content"));
							getCacheView(R.id.message_board_split).setVisibility(View.GONE);
							setText(R.id.message_board_time,Util.friendly_time(t.getString("createTime")));
							setText(R.id.message_board_userId,Util.getNo_pUserId(t.getString("userId")));
							convertView.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent intent=new Intent(NoReaderFrame.this,MessageBoardComment.class);
									intent.putExtra("id", t.getString("id"));
									intent.putExtra("userId", t.getString("userId"));
									Bundle b=new Bundle();
									UserMessageBoard umb = t.toJavaObject(t, UserMessageBoard.class);
									umb.setUserFace(userFace);
									b.putSerializable("UserMessageBoard", umb);
									
									intent.putExtras( b);
									NoReaderFrame.this.startActivity(intent);
								}
							});
							return convertView;
						}
					});
				}
			}
		});
	}

}
