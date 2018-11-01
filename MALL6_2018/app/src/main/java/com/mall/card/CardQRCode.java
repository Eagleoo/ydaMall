package com.mall.card;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardUtil;
import com.mall.view.QRCodeFrame;
import com.mall.view.R;

public class CardQRCode extends Activity{
	@ViewInject(R.id.erweima)
	private ImageView erweima;
	private Bitmap erweimaBitmap;
	private Dialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_qr_code);
		ViewUtils.inject(this);
		try {
			if (CardUtil.myCardLinkman==null) {
				tishi();
			}else {
				createImage();
			}
			
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@OnClick({R.id.top_back,R.id.to_saomiao})
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.top_back:
			finish();
			break;

		case R.id.to_saomiao:
			Intent intent=new Intent(this, QRCodeFrame.class);
			intent.putExtra("state", "state");
			startActivity(intent);
			break;
		}
	}
	// 生成二维码图片
	private void createImage() throws WriterException {
		Gson gson=new Gson();
		String str = CardUtil.myCardLinkman.getUserid();
		try {
			str=URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, 600, 600);
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
		erweimaBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);

		erweimaBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		erweima.setImageBitmap(erweimaBitmap);
	}
	private void tishi(){
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
		TextView update_count=(TextView) layout.findViewById(R.id.update_count);
		TextView yihou_update=(TextView) layout.findViewById(R.id.yihou_update);
		TextView now_update=(TextView) layout.findViewById(R.id.now_update);
		update_count.setText("您还没有创建名片，是否前去创建？");
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
				changeAdd();
				dialog.dismiss();
			}
		});
	}
	/**
	 * 选择新增名片的方式
	 */
	private void changeAdd() {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.card_change_add_type_dialog, null);
		dialog = new Dialog(this, R.style.CustomDialogStyle);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView shoujilianxi = (TextView) layout
				.findViewById(R.id.shoujilianxi);
		shoujilianxi.setText("名片扫描");
		TextView shuru = (TextView) layout.findViewById(R.id.shuru);
		shoujilianxi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CardUtil.isMe="1";
				dialog.dismiss();
			}
		});

		shuru.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				CardUtil.isMe="1";
				Intent intent = new Intent(CardQRCode.this,
						CardAddNewCard.class);
				startActivity(intent);
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
	}
}
