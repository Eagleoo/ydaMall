package com.mall.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.UserData;
import com.mall.util.Util;

public class ElectricCard extends Activity {
	@ViewInject(R.id.erweima_img)
	private ImageView erweima_img;
	@ViewInject(R.id.user_name)
	private TextView user_name;
	@ViewInject(R.id.user_phone)
	private TextView user_phone;
	@ViewInject(R.id.user_role)
	private TextView user_role;
	private User user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.electric_card);
		ViewUtils.inject(this);
		init();
	}
	
	private void init(){
		Util.initTop(this,"电子名片",Integer.MIN_VALUE,new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ElectricCard.this.finish();
			}
		});
		if(UserData.getUser()!=null){
			user=UserData.getUser();
			if(!Util.isNull(user.getUserId())){
				user_name.setText(Util.getNo_pUserId(user.getUserId()));
			}else{
				user_name.setText("");
			}
			if(!Util.isNull(user.getMobilePhone())){
				user_phone.setText(user.getMobilePhone());
			}else{
				user_phone.setText("");
			}
			if(!Util.isNull(user.getLevel())){
				user_role.setText(user.getLevel());
			}else{
				user_role.setText("");
			}
			try {
				createImage();
			} catch (WriterException e) {
				e.printStackTrace();
			}
		}else{
			Util.showIntent(ElectricCard.this, LoginFrame.class);
		}
	}
	public void createImage() throws WriterException {
		String str = "http://"+Web.webImage+"/phone/registe.aspx?inviter="
				+ UserData.getUser().getUserId();
		int _300dp = Util.dpToPx(this, 300F);
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, _300dp, _300dp);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				} else { // 无信息设置像素点为白色
					pixels[y * width + x] = 0xffffffff;
				}
			}
		}
		Bitmap erweimaBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		erweimaBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		erweima_img.setImageBitmap(erweimaBitmap);
	}

}
