package com.mall.yyrg.model;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.mall.view.R;
import com.mall.yyrg.adapter.BitmapUtil;
import com.mall.yyrg.adapter.PhotoAdapter;
import com.mall.yyrg.adapter.UniversalImageLoadTool;

/**    
 */
public class PhotoFragment extends Fragment {

	public interface OnPhotoSelectClickListener {  
		public void onPhotoSelectClickListener(List<PhotoInfo> list);  

	}
	
	private OnPhotoSelectClickListener onPhotoSelectClickListener;
	
	private GridView gridView;
	private PhotoAdapter photoAdapter;
	
	private List<PhotoInfo> list;
	
	private int hasSelect = 1;
	
	private int count;
	private int maxCount=6;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		if(onPhotoSelectClickListener==null){
			onPhotoSelectClickListener = (OnPhotoSelectClickListener)activity;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.yyrg_fragment_photoselect, container, false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		gridView = (GridView)getView().findViewById(R.id.gridview);
		
		Bundle args = getArguments();
		maxCount=args.getInt("maxCount", 6);
		PhotoSerializable photoSerializable = (PhotoSerializable) args.getSerializable("list");
		list = new ArrayList<PhotoInfo>(); 
		list.addAll(photoSerializable.getList());
		hasSelect +=count;
		
		photoAdapter = new PhotoAdapter(getActivity(), list,gridView);
		gridView.setAdapter(photoAdapter);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(list.get(position).isChoose()&&hasSelect>1){
					list.get(position).setChoose(false);
					hasSelect--;
				}else if(hasSelect+BitmapUtil.imageCount<maxCount){
					list.get(position).setChoose(true);
					hasSelect++;
				}else{
					Toast.makeText(getActivity(), "最多选择"+(maxCount-1)+"张图片", Toast.LENGTH_SHORT).show();
					
				}
				photoAdapter.refreshView(position);
				onPhotoSelectClickListener.onPhotoSelectClickListener(list);
			}
		});
		
		gridView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState==0){
					UniversalImageLoadTool.resume();
				}else{
					UniversalImageLoadTool.pause();
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}
}
