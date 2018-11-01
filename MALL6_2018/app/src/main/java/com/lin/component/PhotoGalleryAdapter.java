package com.lin.component;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.mall.model.ProductImage;
import com.mall.util.Util;

public class PhotoGalleryAdapter extends BaseAdapter {
	private List<ProductImage> list = null;
	private Context mContext;
	private ImageView[] mImages = null;	
	private int _130dp=130;
	private BitmapUtils bmUtils;
	
	public PhotoGalleryAdapter(Context c, List<ProductImage> list) {
		mContext = c;
		bmUtils = new BitmapUtils(c);
		this.list = list;
		_130dp = Util.dpToPx(c, 130F);
		mImages = new ImageView[list.size()];
		createReflectedImages();
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
	public boolean createReflectedImages() {
		int index = 0;
		for (ProductImage pi : list) {
			final ImageView imageView = new ImageView(mContext);
			// 设置imageView大小 ，也就是最终显示的图片大小
			imageView.setLayoutParams(new Gallery.LayoutParams(_130dp, _130dp));
			bmUtils.display(imageView, pi.getSma().replaceFirst("s", "174_174_"));
			mImages[index++] = imageView;
		}
		return true;
	}
}
