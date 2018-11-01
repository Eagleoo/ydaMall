package com.lin.component;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.Product;
import com.mall.net.Web;
import com.mall.util.Util;
import com.mall.view.R;

public class LMSJProductAdapter extends BaseAdapter {

	private List<Product> list ;
	private Context context;
	private LayoutInflater flater;
	private int _200dp=200;
	private int _130dp=130;
	private BitmapUtils bmUtils = null;
	
	private class LMSJProductHolder{
		@ViewInject(R.id.lmsj_detail_product_image_item)
		public ImageView img;
		@ViewInject(R.id.lmsj_detail_product_image_name)
		public TextView name;
		@ViewInject(R.id.lmsj_detail_product_image_line)
		public LinearLayout line;
	}
	
	public LMSJProductAdapter(List<Product> list, Context context) {
		super();
		this.list = list;
		this.context = context;
		bmUtils = new BitmapUtils(context);
		flater = LayoutInflater.from(context);
		_200dp = Util.dpToPx(context, 200F);
		_130dp = Util.dpToPx(context, 130F);
	}
	
	public void updateUI(){
		this.notifyDataSetChanged();
	}
	
	public void clear(){
		this.list.clear();
	}
	
	public void add(Product product){
		this.list.add(product);
	} 
	
	public void add(List<Product> list){
		this.list.addAll(list);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		synchronized(this){
			if(0 == list.size())
				return convertView;
		}
		Product pro = list.get(position);
		LMSJProductHolder holder = null;
		if(null == convertView){
			holder = new LMSJProductHolder();
			convertView = flater.inflate(R.layout.lmsj_detail_product_images, null);
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		}else
			holder = (LMSJProductHolder)convertView.getTag();
		final String url = pro.getThumb().replaceFirst("img.mall666.cn", Web.imgServer);
		
		Log.e("图片信息", url+"");
		bmUtils.display(holder.img, url);
		holder.name.setText(pro.getName());
		holder.line.getBackground().setAlpha(80);
		return convertView;
	}

}
