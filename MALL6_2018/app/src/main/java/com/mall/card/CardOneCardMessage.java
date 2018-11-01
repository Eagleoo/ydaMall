package com.mall.card;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardUtil;
import com.mall.card.bean.CardLinkman;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.adapter.AsyncImageLoader;
import com.mall.yyrg.adapter.ImageCacheManager;

public class CardOneCardMessage extends Activity {
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.username)
	private TextView username;
	@ViewInject(R.id.duty)
	private TextView duty;
	@ViewInject(R.id.company)
	private TextView company;
	@ViewInject(R.id.jiaohuan_lin)
	private LinearLayout jiaohuan_lin;
	@ViewInject(R.id.gongy_lin)
	private LinearLayout gongy_lin;
	@ViewInject(R.id.user_phone)
	private TextView user_phone;
	@ViewInject(R.id.qq)
	private TextView qq;
	@ViewInject(R.id.lin_com)
	private LinearLayout lin_com;
	@ViewInject(R.id.lin_qq)
	private LinearLayout lin_qq;
	@ViewInject(R.id.company1)
	private TextView company1;
	@ViewInject(R.id.net_lin)
	private LinearLayout net_lin;
	@ViewInject(R.id.netAddress)
	private TextView netAddress;
	private String state;
	// 职位
	@ViewInject(R.id.role)
	private TextView role;
	@ViewInject(R.id.lin_role)
	private LinearLayout lin_role;
	// 电话2
	@ViewInject(R.id.user_phone2)
	private TextView user_phone2;
	@ViewInject(R.id.phone_lin2)
	private LinearLayout phone_lin2;
	// 电话3
	@ViewInject(R.id.user_phone3)
	private TextView user_phone3;
	@ViewInject(R.id.phone_lin3)
	private LinearLayout phone_lin3;
	/**传真*/
	@ViewInject(R.id.chuanzhen)
	private TextView chuanzhen;
	@ViewInject(R.id.lin_chuanzhen)
	private LinearLayout lin_chuanzhen;
	/**地址*/
	@ViewInject(R.id.address_lin)
	private LinearLayout address_lin;
	@ViewInject(R.id.address)
	private TextView address;
	@ViewInject(R.id.user_image)
	private ImageView user_image;
	private AsyncImageLoader imageLoader;
	private BitmapUtils bmUtils;
	public static CardOneCardMessage cardMessage;
	@ViewInject(R.id.sisin)
	private TextView sisin;
	@ViewInject(R.id.jiao_card)
	private TextView jiao_card;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_user_message);
		ImageCacheManager cacheMgr = new ImageCacheManager(
				CardOneCardMessage.this);
		imageLoader = new AsyncImageLoader(CardOneCardMessage.this,
				cacheMgr.getMemoryCache(), cacheMgr.getPlacardFileCache());
		cardMessage=this;
		ViewUtils.inject(this);
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width =dm.widthPixels;
		state = getIntent().getStringExtra("state");
		if (!TextUtils.isEmpty(state)) {
			CardLinkman cardLinkman = CardUtil.cardLinkman;
			if (TextUtils.isEmpty(cardLinkman.getTouserid())||cardLinkman.getTouserid().equals(UserData.getUser().getUserId())) {
				jiao_card.setText("邀请对方");
				jiao_card.setTag("1");
			}else {
				jiao_card.setTag("2");
				jiao_card.setText("已交换");
				jiao_card.setTextColor(getResources().getColor(R.color.card_shenhei));
			}
			jiao_card.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (jiao_card.getText().toString().trim().equals("邀请对方")) {
						if (!TextUtils.isEmpty(user_phone.getText().toString().trim())) {
							Uri smsToUri = Uri.parse("smsto:"+user_phone.getText().toString().trim());  
							Intent intent1 = new Intent(Intent.ACTION_SENDTO, smsToUri);  
							intent1.putExtra("sms_body", "发现了一片新大陆：远大名片通上市啦，快来体验吧！http://"
									+ Web.webServer
									+ "phone/download.html〔远大云商〕全国客服热线："
									+ Util._400);  
							startActivity(intent1); 
						}
					}
				}
			});
			title.setText(cardLinkman.getName());
			username.setText(cardLinkman.getName());
			user_phone.setText(cardLinkman.getPhone());
			company.setText(cardLinkman.getCompanyName());
			company1.setText(cardLinkman.getCompanyName());
			duty.setText(cardLinkman.getDuty());
			netAddress.setText(cardLinkman.getWebSite());
			setImageWidthAndHeight(user_image, 1, width * 1 / 5, width * 1 / 5);
			ImageView imageView = new ImageView(CardOneCardMessage.this);
			Bitmap bmp = imageLoader.loadBitmap(imageView, cardLinkman.getUserFace(), true, width * 2 / 5, width * 2 / 5);
			if (bmp == null) {
				user_image.setImageResource(R.drawable.addgroup_item_icon);
			} else {
				user_image.setImageBitmap(bmp);
			}
			Bitmap bitmap = ((BitmapDrawable) user_image.getDrawable())
					.getBitmap();
			bitmap = Util.toRoundCorner(bitmap, 10);
			user_image.setImageBitmap(bitmap);
			qq.setText(cardLinkman.getQq());
			address.setText(cardLinkman.getCompanyAddress());
			if (Util.isNull(cardLinkman.getPhon2())) {
				phone_lin2.setVisibility(View.GONE);
			}else {
				phone_lin2.setVisibility(View.VISIBLE);
				user_phone2.setText(cardLinkman.getPhon2());
			}
			if (Util.isNull(cardLinkman.getPhone3())) {
				phone_lin3.setVisibility(View.GONE);
			}else {
				phone_lin3.setVisibility(View.VISIBLE);
				user_phone3.setText(cardLinkman.getPhon2());
			}
			
			if (Util.isNull(cardLinkman.getCompanyAddress())) {
				address_lin.setVisibility(View.GONE);
			}else {
					address_lin.setVisibility(View.VISIBLE);
					address.setText(cardLinkman.getCompanyAddress());
				}
			if (Util.isNull(cardLinkman.getWebSite())) {
				net_lin.setVisibility(View.GONE);
			}else {
				net_lin.setVisibility(View.VISIBLE);
			}
			if (Util.isNull(cardLinkman.getFaxNumber())) {
				lin_chuanzhen.setVisibility(View.GONE);
			}else {
				lin_chuanzhen.setVisibility(View.VISIBLE);
			}
			if (Util.isNull(cardLinkman.getQq())) {
				lin_qq.setVisibility(View.GONE);
			}else {
				lin_qq.setVisibility(View.VISIBLE);
			}

		}
	}
	@OnClick(R.id.jiao_card)
	public void jiaohuan(View view){
		if (jiao_card.getText().toString().trim().equals("邀请对方")) {
			if (!TextUtils.isEmpty(user_phone.getText().toString().trim())) {
				Uri smsToUri = Uri.parse("smsto:"+user_phone.getText().toString().trim());  
				Intent intent1 = new Intent(Intent.ACTION_SENDTO, smsToUri);  
				intent1.putExtra("sms_body", "发现了一片新大陆：远大名片通上市啦，快来体验吧！http://"
						+ Web.webServer
						+ "phone/download.html〔远大云商〕全国客服热线："
						+ Util._400);  
				startActivity(intent1); 
			}
		}
	}
	@OnClick(R.id.sisin)
	public void sixin(View view){
		if (!TextUtils.isEmpty(user_phone.getText().toString().trim())) {
			Uri smsToUri = Uri.parse("smsto:"+user_phone.getText().toString().trim());  
			Intent intent1 = new Intent(Intent.ACTION_SENDTO, smsToUri);  
			intent1.putExtra("sms_body", "");  
			startActivity(intent1); 
		}
		
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

						int _100dp = Util.dpToPx(CardOneCardMessage.this, 100F);

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

	@OnClick({ R.id.bianji ,R.id.jiao_card,R.id.sisin,R.id.more,R.id.top_back})
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.top_back:
			finish();
			break;
		case R.id.bianji:
			Intent intent = new Intent(this, CardAddNewCard.class);
			intent.putExtra("state", "state");
			startActivity(intent);
			break;

		case R.id.jiao_card:
			
			break;
		case R.id.sisin:
			
			break;
		case R.id.more:
			try{
			String searchPhoen = "";
			 if(!Util.isNull(user_phone.getText().toString())){
				 searchPhoen = user_phone.getText().toString();
				 if(Util.isNull(searchPhoen)){
					 searchPhoen = user_phone2.getText().toString();
					 if(Util.isNull(searchPhoen)){
						 searchPhoen = user_phone3.getText().toString();
					 }
				 }
			 }
			 if(!Util.isNull(searchPhoen)){
				String number = searchPhoen;
		        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/"+number);
		        ContentResolver resolver = this.getContentResolver();
		        Cursor cursor = resolver.query(uri, new String[]{android.provider.ContactsContract.Data.DISPLAY_NAME}, null, null, null);   
		        if(cursor.moveToFirst()){
		            String name = cursor.getString(0);
		            new VoipDialog("该名片手机号已存在在通讯录【"+name+"】中，是否继续添加？", this
		            		, "取消", "重复添加", null, new OnClickListener() {
								@Override
								public void onClick(View v) {
									addUser();
								}
							}).show();
		            LogUtils.e(name);
		        }else{
					 addUser();
		        }
		        cursor.close();
		        return ;
			 }
			 addUser();
			}catch(Exception e){
				Util.show("请检查是否允许远大云商读取/写入通讯录", this);
			}
	        break;
		}
	}
	
	private void addUser(){

		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = this.getContentResolver();
        ContentValues values = new ContentValues();
        long contactid = ContentUris.parseId(resolver.insert(uri, values));
        
        uri = Uri.parse("content://com.android.contacts/data");
         
        //添加姓名
        values.put("raw_contact_id", contactid);
        values.put(Data.MIMETYPE, "vnd.android.cursor.item/name");
        values.put("data1", username.getText().toString());
        resolver.insert(uri, values);
        values.clear();
        // 手机
        if(!Util.isNull(user_phone.getText().toString())){
        	boolean isPhone = Util.isPhone(user_phone.getText().toString());
        	addPhone(uri,resolver,values,contactid,isPhone?"2":"1",user_phone.getText().toString());
        }

        // 住宅
        if(!Util.isNull(user_phone2.getText().toString())){
        	boolean isPhone = Util.isPhone(user_phone2.getText().toString());
        	addPhone(uri,resolver,values,contactid,isPhone?"2":"1",user_phone2.getText().toString());
        }
        if(!Util.isNull(user_phone3.getText().toString())){
        	boolean isPhone = Util.isPhone(user_phone3.getText().toString());
        	addPhone(uri,resolver,values,contactid,isPhone?"2":"1",user_phone3.getText().toString());
        }
         
        //添加Email
        if(!Util.isNull(qq.getText().toString())){
	        values.put("raw_contact_id", contactid);
	        values.put(Data.MIMETYPE, "vnd.android.cursor.item/email_v2");
	        values.put("data1", qq.getText().toString()+"@qq.com");
	        resolver.insert(uri, values);
	        values.clear();
        }
        Util.show("加入通讯录成功！", this);
	}
	
	private void addPhone(Uri uri, ContentResolver resolver, ContentValues values , long contactid,String phoneType,String phone){
        values.put("raw_contact_id", contactid);
        values.put(Data.MIMETYPE, "vnd.android.cursor.item/phone_v2"); 
        values.put("data2", phoneType);  
        values.put("data1", phone); 
        resolver.insert(uri, values);
        values.clear();
	}
}
