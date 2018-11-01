package com.mall.yyrg.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mall.view.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.bean.MediaBean;

/**
 * 相片适配器
 * @author GuiLin
 */
public class NewSelcctPhotoAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<MediaBean> list=new ArrayList<MediaBean>();
	private ViewHolder viewHolder;
	private Context context;
	private int width ;
	public NewSelcctPhotoAdapter(Context context, List<MediaBean> list, int width){
		this.context=context;
		mInflater = LayoutInflater.from(context);
		this.list = list;
		this.width=width;
	}

	public void setList(List<MediaBean> list) {
		this.list.addAll(list);
		this.notifyDataSetChanged();
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
	/**
	 * 刷新view
	 * @param
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.yyrg_list_item_selectphotos, null);
			viewHolder.image = (ImageView)convertView.findViewById(R.id.imageView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		LayoutParams layoutParams = viewHolder.image.getLayoutParams();
		layoutParams.height = width;
		layoutParams.width=width;
		viewHolder.image.setLayoutParams(layoutParams);

			if (position==0){
				Picasso.with(context).load(R.drawable.tianjaishizhi)
						.into(viewHolder.image);
			}else {
				Log.e("数据：",list.get(position).toString()+"KL");

				Picasso.with(context).load("file://"+list.get(position).getThumbnailSmallPath())
						.into(viewHolder.image);
			}


		return convertView;
	}
	public class ViewHolder{
		public ImageView image;
	}
}
