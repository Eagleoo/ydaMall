package com.mall.yyrg.adapter;

import java.util.HashMap;
import java.util.WeakHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.widget.ImageView;

import com.mall.view.R;

public class BitmapCompress extends AsyncTask<String, Integer, Boolean> {

	private ImageView imageView;
	private Context context;
	private WeakHashMap<String, Bitmap> weakHashMap;
	private HashMap<String, BitmapCompress> bitmapHashMap;
	private Bitmap bitmap;
	private int width ;
	
	public BitmapCompress(Context context, ImageView imageView,
			WeakHashMap<String, Bitmap> weakHashMap,
			HashMap<String, BitmapCompress> bitmapHashMap,int width) {
		// TODO Auto-generated constructor stub
		this.imageView = imageView;
		this.context = context;
		this.weakHashMap = weakHashMap;
		this.bitmapHashMap = bitmapHashMap;
		this.width=width;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		String id = params[0];
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		bitmap = MediaStore.Images.Thumbnails.getThumbnail(
				context.getContentResolver(), Integer.parseInt(id),
				Images.Thumbnails.MINI_KIND, options);
		weakHashMap.put(id, bitmap);
		bitmapHashMap.remove(id);
	//	System.out.println(weakHashMap.toString());
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		//int widths = bitmap.getWidth();
		if (bitmap!=null) {
			Bitmap b = Bitmap.createBitmap(bitmap, width/4, 0, width/4, width/4);
			imageView.setImageBitmap(b);
			imageView.requestLayout();
		}else {
			Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.new_yda__top_zanwu);
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			/**
			 */
			Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, width/4, width/4);
			imageView.setImageBitmap(b);
		}
		/*Bitmap b = Bitmap.createBitmap(bitmap, width/4, 0, width/4, width/4);
		imageView.setImageBitmap(b);
		imageView.requestLayout();*/
	}
}
