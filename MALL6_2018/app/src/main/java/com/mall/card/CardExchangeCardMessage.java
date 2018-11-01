package com.mall.card;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardUtil;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.adapter.AsyncImageLoader;
import com.mall.yyrg.adapter.ImageCacheManager;

/**
 * 交换者的信息
 * @author admin
 *
 */
public class CardExchangeCardMessage extends Activity {
	@ViewInject(R.id.userimg)
	private ImageView user_img;
	@ViewInject(R.id.username)
	private TextView username;
	@ViewInject(R.id.city)
	private TextView city;
	@ViewInject(R.id.message)
	private TextView message;
	@ViewInject(R.id.delete)
	private TextView delete;
	private String cardId;
	private String name;
	private String image;
	private String messages;
	private AsyncImageLoader imageLoader;
	private int width;
	private BitmapUtils bmUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_exchange_card_request_message);
		ViewUtils.inject(this);
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.addgroup_item_icon);
		name=getIntent().getStringExtra("name");
		cardId=getIntent().getStringExtra("id");
		image=getIntent().getStringExtra("image");
		messages=getIntent().getStringExtra("messages");
		username.setText(name);
		Log.v("image", image);
		city.setVisibility(View.INVISIBLE);
		message.setText(messages);
		ImageCacheManager cacheMgr = new ImageCacheManager(
				CardExchangeCardMessage.this);
		imageLoader = new AsyncImageLoader(CardExchangeCardMessage.this,
				cacheMgr.getMemoryCache(), cacheMgr.getPlacardFileCache());
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width=dm.widthPixels;
		setImageWidthAndHeight(user_img, 1, width * 1 / 4, width * 1 / 4);
		setImage(user_img, image, width/4, width/4);
	}
	private void setImage(final ImageView logo, String href, final int width,
			final int height) {
		bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				arg2 = Util.zoomBitmap(arg2, width, height);
				arg2=Util.toRoundCorner(arg2, 10);
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.addgroup_item_icon);
			}
		});
	}
	private void setImageWidthAndHeight(ImageView imageView, int state,
			int width, int height) {
		if (state == 1) {
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) imageView
					.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.width = width;
			linearParams.height = height;
			imageView.getLayoutParams();
		}
		if (state == 2) {
			RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) imageView
					.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.width = width;
			linearParams.height = height;
			imageView.getLayoutParams();
		}

	}
	@OnClick({R.id.exchange,R.id.top_back,R.id.repulse,R.id.delete})
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.exchange:
			editNameCardShareOK(cardId);
			break;
		case R.id.top_back:
			finish();
			break;
		case R.id.repulse:
			editNameCardShareNO(cardId);
			break;
		case R.id.delete:
			deleteReceiveNameCardShare(cardId);
			break;
		}
	}
	/**
	 * 同意交换名片
	 */
	public void editNameCardShareOK(final String id){
		final User user = UserData.getUser();
		Util.asynTask(this,"操作中……",new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (map.get("code") != null) {
						if (map.get("code").equals("200")) {
							Toast.makeText(CardExchangeCardMessage.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
							finish();
							
						} else {
							Toast.makeText(CardExchangeCardMessage.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						}
					} else {

						Toast.makeText(CardExchangeCardMessage.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(CardExchangeCardMessage.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard,
						Web.editNameCardShareOK,
						"editNameCardShareOK", "userId="
								+ user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd() + "&id="+id
								+ "&remessage=" );
				String s = web.getPlan();
				return s;
			}

		});
	}
	/**
	 * 拒绝交换名片
	 */
	public void editNameCardShareNO(final String id){
		final User user = UserData.getUser();
		Util.asynTask(this,"操作中……",new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (map.get("code") != null) {
						if (map.get("code").equals("200")) {
							Toast.makeText(CardExchangeCardMessage.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
							finish();
						} else {
							Toast.makeText(CardExchangeCardMessage.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						}
					} else {

						Toast.makeText(CardExchangeCardMessage.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(CardExchangeCardMessage.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}
			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard,
						Web.editNameCardShareNO,
						"editNameCardShareNO", "userId="
								+ user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd() + "&id="+id
								+ "&remessage=" );
				String s = web.getPlan();
				return s;
			}

		});
	}
	/**
	 * 删除交换名片
	 */
	public void deleteReceiveNameCardShare(final String id){
		final User user = UserData.getUser();
		Util.asynTask(this,"删除中……",new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (map.get("code") != null) {
						if (map.get("code").equals("200")) {
							Toast.makeText(CardExchangeCardMessage.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
							finish();
						} else {
							Toast.makeText(CardExchangeCardMessage.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						}
					} else {

						Toast.makeText(CardExchangeCardMessage.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(CardExchangeCardMessage.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}
			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard,
						Web.deleteReceiveNameCardShare,
						"deleteReceiveNameCardShare", "userId="
								+ user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd() + "&id="+id
								+ "&remessage=" );
				String s = web.getPlan();
				return s;
			}

		});
	}
	
}
