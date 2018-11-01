package com.mall.card;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardUtil;
import com.mall.card.bean.CardCount;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.AccountManagerFrame;
import com.mall.view.R;
import com.mall.view.SelectPicActivity;
import com.mall.view.SettingFrame;
import com.mall.view.UserInfoFrame;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class CardUserCentre extends Activity {
	private List<CardCount> cardCounts = new ArrayList<CardCount>();
	@ViewInject(R.id.card_count)
	private TextView card_count;
	@ViewInject(R.id.card_lianxi)
	private TextView card_lianxi;
	@ViewInject(R.id.card_shouc)
	private TextView card_shouc;
	@ViewInject(R.id.user_name)
	private TextView user_name;
	@ViewInject(R.id.user_duty)
	private TextView user_duty;
	@ViewInject(R.id.user_com)
	private TextView user_com;
	private Dialog dialog;
	@ViewInject(R.id.user_image)
	private ImageView user_image;
	private BitmapUtils bmUtils;
	@ViewInject(R.id.chongpai)
	private TextView chongpai;
	@ViewInject(R.id.user_juese)
	private TextView user_juese;
	public static final int TO_SELECT_PHOTO = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_my_centre);
		ViewUtils.inject(this);
		
		bmUtils = new BitmapUtils(this);
		user_image.setImageResource(R.drawable.progress_round);
		AnimationDrawable anim = (AnimationDrawable) user_image
				.getDrawable();
		user_juese.setText("系统角色："+UserData.getUser().getShowLevel());
		showUserFace(anim, UserData.getUser().getUserFace());
		card_lianxi.setText(CardUtil.cardCount+"");
		getUserBusinessCardCount();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		CardUtil.isMe="1";
		if (CardUtil.myCardLinkman==null) {
			chongpai.setText("拍摄我的名片");
			Tishi();
		}else {
			chongpai.setText("重拍我的名片");
			user_name.setText(CardUtil.myCardLinkman.getName());
			user_duty.setText(CardUtil.myCardLinkman.getDuty());
			user_com.setText(CardUtil.myCardLinkman.getCompanyName());
		}
	}
	private void showUserFace(final AnimationDrawable anim, String url) {
		user_image.setImageResource(R.drawable.progress_round);
		if (null != anim)
			anim.start();
		bmUtils.display(user_image, url,
				new DefaultBitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View container, String uri,
							Bitmap bitmap, BitmapDisplayConfig config,
							BitmapLoadFrom from) {

						int _100dp = Util.dpToPx(CardUserCentre.this, 100F);

						int w = bitmap.getWidth();
						int h = bitmap.getHeight();
						float nw = _100dp / w;

						final Bitmap newBm = Util.zoomBitmap(bitmap,
								(int) (w * nw), (int) (h * nw));
						if (null != anim)
							anim.stop();
						super.onLoadCompleted(container, uri, newBm, config,
								from);
					}

					@Override
					public void onLoadFailed(View container, String uri,
							Drawable drawable) {
						if (null != anim)
							anim.stop();
						user_image
								.setImageResource(R.drawable.new_membercenter_face);
						super.onLoadFailed(container, uri, drawable);
					}
				});
	}
	@OnClick({ R.id.my_xinxi, R.id.my_zhanghu, R.id.share_card,
			R.id.find_shouren, R.id.yanqing, R.id.chongpai,R.id.card_setting ,R.id.user_image})
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.user_image:
			Intent intent = new Intent(this, SelectPicActivity.class);
			startActivityForResult(intent, TO_SELECT_PHOTO);
			break;
		case R.id.card_setting:
			Util.showIntent(this, SettingFrame.class);
			break;
		case R.id.my_xinxi:
			Util.showIntent(this, UserInfoFrame.class);
			break;
		case R.id.my_zhanghu:
			Util.showIntent(this, AccountManagerFrame.class);
			break;
		case R.id.share_card:
			if (CardUtil.myCardLinkman==null) {
				Toast.makeText(this, "您还没有名片，请先去完善名片信息", Toast.LENGTH_SHORT).show();
				return;
			}
			final OnekeyShare oks = new OnekeyShare();
			final String url = "http://app.yda360.com/phone/namecard_detail.aspx?id="+Util.get(UserData.getUser().getUserNo());
			final String title = "我的名片";
			oks.setTitle(title);
			oks.setTitleUrl(url);
			oks.setImageUrl(UserData.getUser().getUserFace());
			oks.setUrl(url);
			oks.setAddress("10086");
			oks.setComment("分享");
			oks.setText("姓名："+CardUtil.myCardLinkman.getName()+"\n电话："+CardUtil.myCardLinkman.getPhone()
					+"\n公司："+CardUtil.myCardLinkman.getCompanyName()
					+"\n职位："+CardUtil.myCardLinkman.getDuty());
			oks.setSite("远大云商");
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
			oks.show(this);
			break;
		case R.id.find_shouren:
			Util.showIntent(this, CardPeopleNearby.class);
			break;

		case R.id.yanqing:
			/*Intent intent = new Intent();

			//系统默认的action，用来打开默认的短信界面
			intent.setAction(Intent.ACTION_SEND);
			this.startActivity(intent);*/
			Uri smsToUri = Uri.parse("smsto:");  
			  
			Intent intent1 = new Intent(Intent.ACTION_SENDTO, smsToUri);  
			  
			intent1.putExtra("sms_body", "发现了一片新大陆：远大名片通上市啦，快来体验吧！http://"
					+ Web.webServer
					+ "phone/download.html〔远大云商〕全国客服热线："
					+ Util._400);  
			  
			startActivity(intent1);  
			break;
		case R.id.chongpai:
			break;
		}
	}

	/**
	 * 获得排行
	 */
	public void getUserBusinessCardCount() {
		final User user = UserData.getUser();
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
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
					if (map == null) {
						Toast.makeText(CardUserCentre.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG)
								.show();
					} else {
						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Gson gson = new Gson();
								cardCounts = gson.fromJson(map.get("list"),
										new TypeToken<List<CardCount>>() {
										}.getType());
								if (cardCounts.size()>0) {
									card_count.setText(cardCounts.get(0).getCount());
									card_shouc.setText(cardCounts.get(0).getBycount());
								}
							} else {
								Toast.makeText(CardUserCentre.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							Toast.makeText(CardUserCentre.this,
									"网络不给力哦，请检查网络是否连接后，再试一下",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(CardUserCentre.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(1, Web.bBusinessCard,
						Web.getUserBusinessCardCount,
						"getUserBusinessCardCount", "userId="
								+ user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd());
				String s = web.getPlan();
				return s;
			}

		});
	}
	/**
	 * 提示前去完善自己的名片
	 */
	private void Tishi() {
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.card_delete_dialog, null);
		dialog = new Dialog(this, R.style.CustomDialogStyle);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView yihou_update = (TextView) layout
				.findViewById(R.id.yihou_update);
		TextView now_update = (TextView) layout.findViewById(R.id.now_update);
		TextView update_count=(TextView) layout.findViewById(R.id.update_count);
		update_count.setText("您未创建自己的名片,是否前去创建？");
		yihou_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		now_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO) {
			final String picPath = data
					.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
			final int _100dp = Util.dpToPx(this, 100F);
			final Bitmap bm = Util.getLocationThmub(picPath, _100dp, _100dp);
			final Bitmap newBm = Util.getLocationThmub(picPath, _100dp, _100dp);

			final String imgType = picPath
					.substring(picPath.lastIndexOf(".") + 1);
			bm.recycle();
			Util.asynTask(this, "正在上传您的头像...", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					User user = UserData.getUser();
					bmUtils.clearCache(user.getUserFace());
					user.setUserFace("http://" + Web.webImage + "/userface/"
							+ user.getUserNo() + "_97_97.jpg?r="
							+ System.currentTimeMillis());
					user_image.setImageResource(R.drawable.progress_round);
					final AnimationDrawable anim = (AnimationDrawable) user_image
							.getDrawable();
					bmUtils.display(user_image, user.getUserFace(),
							new DefaultBitmapLoadCallBack<View>() {
								@Override
								public void onLoadCompleted(View container,
										String uri, Bitmap bitmap,
										BitmapDisplayConfig config,
										BitmapLoadFrom from) {
									int _100dp = Util.dpToPx(
											CardUserCentre.this, 100F);
									int w = bitmap.getWidth();
									int h = bitmap.getHeight();
									float nw = _100dp / w;
									final Bitmap newBm = Util.zoomBitmap(
											bitmap, (int) (w * nw),
											(int) (h * nw));
									anim.stop();
									super.onLoadCompleted(container, uri,
											newBm, config, from);
								}

								@Override
								public void onLoadFailed(View container,
										String uri, Drawable drawable) {
									anim.stop();
									user_image
											.setImageResource(R.drawable.new_membercenter_face);
									super.onLoadFailed(container, uri, drawable);
								}
							});
					Util.show("上传成功！", CardUserCentre.this);
				}

				@SuppressLint("NewApi")
				@Override
				public Serializable run() {
					User user = UserData.getUser();
					String NAMESCROPE = "http://mywebservice.cn/";
					String METHOD_NAME = "uploadResume";
					String URL = "http://" + Web.webImage
							+ "/WebService_1.asmx";
					String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
					try {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
						if (imgType.equalsIgnoreCase("png"))
							compressFormat = Bitmap.CompressFormat.PNG;
						newBm.compress(compressFormat, 80, out);
						InputStream sbs = new ByteArrayInputStream(out
								.toByteArray());
						byte[] buffer = new byte[80 * 1024];
						int count = 0;
						int i = 0;
						while ((count = sbs.read(buffer)) >= 0) {
							String uploadBuffer = new String(Base64.encode(
									buffer, 0, count, Base64.DEFAULT));
							SoapObject request = new SoapObject(NAMESCROPE,
									METHOD_NAME);
							request.addProperty("userNo", user.getUserNo());
							request.addProperty("imgType", imgType);
							request.addProperty("image", uploadBuffer);
							request.addProperty("tag", i);
							request.addProperty("size", out.size());
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
								LogUtils.v(obj + "");
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
					return null;
				}
			});
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
