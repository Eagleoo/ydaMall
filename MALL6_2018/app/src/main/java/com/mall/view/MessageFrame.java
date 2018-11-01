package com.mall.view;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.mall.model.Message;
import com.mall.serving.school.YDNewsDetailFrame;
import com.mall.util.Data;
import com.mall.util.Util;

/**
 * 
 * 功能： 消息中心<br>
 * 时间： 2013-3-7<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class MessageFrame extends Activity {


	private Handler handler = new Handler();
	private ProgressDialog pd = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.message);
		Util.initTop(this, "云商公告", Integer.MIN_VALUE, null);
		// 加载消息
		final ListView listView = (ListView) findViewById(R.id.messageList);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setAlwaysDrawnWithCacheEnabled(true);
		bind(listView, 1);
	}

	public void bind(final ListView listView, final int page) {
		pd = ProgressDialog.show(MessageFrame.this, null, "数据加载中....");
		new Thread(new Runnable() {
			public void run() {
				Looper.prepare();

				List<Message> list = Data.getMessage(MessageFrame.this);

				MessageAdapter ma = new MessageAdapter(MessageFrame.this, list);

				// 添加并且显示
				setAdapter(listView, ma);
				pd.dismiss();
				Looper.loop();
			}
		}).start();
	}

	public void setAdapter(final ListView listView, final MessageAdapter ma) {
		handler.post(new Runnable() {
			public void run() {
				listView.setAdapter(ma);
			}
		});
	}

}

class MessageAdapter extends BaseAdapter {

	private Context c;

	private List<Message> l;
	private LayoutInflater flater;
	private BitmapUtils bmUtils;
	public MessageAdapter(Context context, List<Message> l) {
		this.c = context;
		flater = LayoutInflater.from(context);
		this.l = l;
		bmUtils = new BitmapUtils(context);
	}

	@Override
	public Object getItem(int position) {
		return l.get(position);
	}

	@Override
	public long getItemId(int position) { 
		return l.get(position).hashCode();
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = flater.inflate(R.layout.gonggao_list_item, null);
		}
		TextView gonggaotop=(TextView) convertView.findViewById(R.id.gonggaotop);
		TextView gonggaotitle=(TextView) convertView.findViewById(R.id.gonggaotitle);
		TextView gonggaotime=(TextView) convertView.findViewById(R.id.gonggaotime);
		final ImageView img=(ImageView) convertView.findViewById(R.id.gonggapimg);
		if(!Util.isNull(l.get(position).getImg()))
			bmUtils.display(img, l.get(position).getImg(),new BitmapLoadCallBack<View>() {

				@Override
				public void onLoadCompleted(View arg0, String arg1,
						Bitmap arg2, BitmapDisplayConfig arg3,
						BitmapLoadFrom arg4) {
					img.setImageBitmap(arg2);
				}

				@Override
				public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
					img.setImageResource(R.drawable.no_get_image);
					
				}
			});
		gonggaotop.setText(l.get(position).getTitle());
		gonggaotitle.setText(l.get(position).getJianjie());
		gonggaotime.setText(Util.friendly_time(l.get(position).getDate()));
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.showIntent(c, YDNewsDetailFrame.class, new String[] {
					"id", "title" }, new String[] {
					l.get(position).getId() + "",
					l.get(position).getTitle() + "" });
			}
		});
		return convertView;
	}

	@Override
	public int getCount() {
		return l.size();
	}

}
