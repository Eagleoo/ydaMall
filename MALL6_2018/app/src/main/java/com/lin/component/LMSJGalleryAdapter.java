package com.lin.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.mall.model.ShopMInfo;
import com.mall.net.Web;
import com.mall.util.Util;
import com.mall.view.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LMSJGalleryAdapter extends BaseAdapter {
	private ShopMInfo m = null;
	private Context mContext;
	private ImageView[] mImages = null;
	private Gallery viewPager;
	private List<String> urlKey = null;
	private BitmapUtils bmUtils;

	public LMSJGalleryAdapter(Context c, ShopMInfo m, Gallery viewPager) {
		mContext = c;
		this.m = m;
		this.viewPager = viewPager;  
		urlKey = new ArrayList<String>();
		bmUtils = new BitmapUtils(c);
		if (!Util.isNull(m.getLogo()) && 0 != Util.getInt(m.getCount())) {

			String href = Web.imgServer2 + "/" + m.getLogo();
			String name = href.substring(href.lastIndexOf("/"));
			String path = Util.shopMPath + m.getId() + name;
			final String[] images = m.getImages().split("\\|凸\\|");
			mImages = new ImageView[images.length];
			for (int i = 0; i < images.length; i++) {
				if (Util.isNull(images[i]))
					continue;
				href = Web.imgServer2 + "/" + images[i];// 图片url
				name = href.substring(href.lastIndexOf("/"));
				path = Util.shopMPath + name;// 图片存放在sdcard中的地址
				urlKey.add(href + "|,|" + path);
			}
			createReflectedImages();
			if (getCount() > 0) {
				int selected = (mImages.length / 2);// 设置gallary默认选中图片
				viewPager.setSelection(selected);
				viewPager.setSelected(true);
			}
		} else
			mImages = new ImageView[0];
	}

	public int getCount() {
		return mImages.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView i = mImages[position];
		return i;
	}

	/**
	 * 创建倒影效果
	 * 
	 * @return
	 */
	public void createReflectedImages() {
		int index = 0;

		for (final String key : urlKey) {
			final ImageView imageView = new ImageView(mContext);
			// 设置imageView大小 ，也就是最终显示的图片大小
			final Gallery.LayoutParams lp = new Gallery.LayoutParams(
					Gallery.LayoutParams.FILL_PARENT,
					Gallery.LayoutParams.FILL_PARENT);
			imageView.setLayoutParams(lp);
//			imageView.setScaleType(ScaleType.FIT_XY);
			String[] imgAndPath = key.split("\\|,\\|");
			if(null == imgAndPath || 0 == imgAndPath.length){
				Bitmap bm = Util.drawable2Bitmap(mContext.getResources().getDrawable(R.drawable.zw174));
				imageView.setImageBitmap(bm);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				mImages[index++] = imageView;
				continue;
			}
				
			final String url = key.split("\\|,\\|")[0].replaceAll("///", "/");
			final String path = key.split("\\|,\\|")[1].replaceAll("//", "/");
			LogUtils.e("url="+url+"          path="+path);
			if(!new File(path).exists()){
				HttpUtils http = new HttpUtils(10000);
				imageView.setImageResource(R.drawable.progress_round);
				Gallery.LayoutParams lp2 = new Gallery.LayoutParams(
						Gallery.LayoutParams.FILL_PARENT,
						Gallery.LayoutParams.WRAP_CONTENT);
				imageView.setLayoutParams(lp2);
				final AnimationDrawable anim = (AnimationDrawable) imageView.getDrawable();
				http.download(url, path, new RequestCallBack<File>() {
					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						Bitmap bm = Util.getLocationThmub(path, 480,400);
						imageView.setLayoutParams(lp);
						imageView.setImageBitmap(bm);
						imageView.setScaleType(ScaleType.CENTER_CROP);
						anim.stop();
					}
	
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Bitmap bm = Util.drawable2Bitmap(mContext.getResources()
								.getDrawable(R.drawable.zw174));
						imageView.setImageBitmap(bm);
						imageView.setLayoutParams(lp);
						imageView.setScaleType(ScaleType.CENTER_CROP);
						anim.stop();
					}
	
					@Override
					public void onStart() {
						super.onStart();
						anim.start();
					}
				});// 下载图片
			}else{
				Bitmap bm = Util.getLocationThmub(path, 480,360);
				imageView.setImageBitmap(bm);
				imageView.setScaleType(ScaleType.CENTER_CROP);
			}

			mImages[index++] = imageView;
		}
	}
}
