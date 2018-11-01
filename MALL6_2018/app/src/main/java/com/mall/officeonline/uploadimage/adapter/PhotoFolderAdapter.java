package com.mall.officeonline.uploadimage.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mall.officeonline.AlbumInfo;
import com.mall.util.ThumbnailsUtil;
import com.mall.view.R;
import com.mall.yyrg.adapter.RotateImageViewAware;
import com.mall.yyrg.adapter.UniversalImageLoadTool;

/**
 * 相册适配器
 * @author GuiLin
 */
public class PhotoFolderAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<AlbumInfo> list;
	private ViewHolder viewHolder;
	
	public PhotoFolderAdapter(Context context,List<AlbumInfo> list){
		mInflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_photofolder, null);
			viewHolder.image = (ImageView)convertView.findViewById(R.id.imageView);
			viewHolder.text = (TextView)convertView.findViewById(R.id.info);
			viewHolder.num = (TextView)convertView.findViewById(R.id.num);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final AlbumInfo albumInfo = list.get(position);
		UniversalImageLoadTool.disPlay(ThumbnailsUtil.MapgetHashValue(albumInfo.getImage_id(),albumInfo.getPath_file()), 
				new RotateImageViewAware(viewHolder.image,albumInfo.getPath_absolute()), R.drawable.ic_launcher);
		viewHolder.text.setText(albumInfo.getName_album());
		viewHolder.num.setText("("+list.get(position).getList().size()+"张)");
		return convertView;
	}
	
	public class ViewHolder{
		public ImageView image;
		public TextView text;
		public TextView num;
	}
}
