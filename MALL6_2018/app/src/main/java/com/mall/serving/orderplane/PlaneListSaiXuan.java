package com.mall.serving.orderplane;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mall.view.R;

public class PlaneListSaiXuan extends Activity{
	private TextView zfzz,hkgs,qfsd,cangwei;
	private LinearLayout container;
	private ImageView buxian;
	private String[] zf=new String[]{"仅限直达"};
	private String[] companyname=new String[]{"厦门航空","海南航空","四川航空","东方航空","南方航空","国际航空","深圳航空","吉祥航空"};
	private String[] times=new String[]{"06:00-11:59","12:00-12:59","13:00-28:59","19:00-23:59"};
	private String[] cangweis=new String[]{"经济舱","头等舱"};
	private List<String> paramters=new ArrayList<String>();
	private List<String> zfList=new ArrayList<String>();
	private List<String> companynamesList=new ArrayList<String>();
	private List<String> timeList=new ArrayList<String>();   
	private List<String> cangweiList=new ArrayList<String>();
	LayoutInflater inflater;
	private TextView submit,reset;
	private String zfliststring="",namesstring="",timstring="",cangweistring="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.planelistsaixuan);
		super.onCreate(savedInstanceState);
		inflater=LayoutInflater.from(this);
		init();
	}
	
	private void init(){
		zfzz=(TextView) this.findViewById(R.id.zfzz);
		hkgs=(TextView) this.findViewById(R.id.hkgs);
		qfsd=(TextView) this.findViewById(R.id.qfsd);
		cangwei=(TextView) this.findViewById(R.id.cangwei);
		container=(LinearLayout) this.findViewById(R.id.container);
		buxian=(ImageView) this.findViewById(R.id.buxian);
		zfzz.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initContainer(zf,zfList);
			}
		});
		hkgs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initContainer(companyname,companynamesList);
			}
		});
		qfsd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initContainer(times,timeList);
			}
		});
		cangwei.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initContainer(cangweis,cangweiList);
			}
		});
		submit=(TextView) this.findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				zfliststring=getParameterString(zfList, zfliststring);
				namesstring=getParameterString(companynamesList, namesstring);
				timstring=getParameterString(timeList, timstring);
				cangweistring=getParameterString(cangweiList, cangweistring);
				System.out.println(zfliststring);
				System.out.println(namesstring);
				System.out.println(timstring);
				System.out.println(cangweistring);
				Intent intent=new Intent(PlaneListSaiXuan.this,PlaneList.class);
				intent.putExtra("zfliststring", zfliststring);
				intent.putExtra("namesstring", namesstring);
				intent.putExtra("timstring", timstring);
				intent.putExtra("cangweistring", cangweistring);
				PlaneListSaiXuan.this.setResult(100,intent);
				PlaneListSaiXuan.this.finish();
			}
		});
		reset=(TextView) this.findViewById(R.id.reset);
		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PlaneListSaiXuan.this.finish();
			}
		});
		initContainer(zf,zfList);
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==event.KEYCODE_BACK){
			PlaneListSaiXuan.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	private String getParameterString(List<String> list,String str){
		String result="";
		for(int i=0;i<list.size();i++){
			result+=list.get(i)+",";
		}
		return result;
	}
	private void initContainer(final String[] s,final List<String> list){
		container.removeAllViews();
		LinearLayout bl=(LinearLayout) inflater.inflate(R.layout.saixuan_item, null);
		TextView bx=(TextView) bl.getChildAt(1);
		bx.setText("不限");
		ImageView bux=(ImageView) bl.getChildAt(0);
		bux.setImageResource(R.drawable.ic_launcher);
		bux.setTag("selected");
		bux.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				list.clear();
				initContainer(s, list);
			}
		});
		container.addView(bl);
		for(int i=0;i<s.length;i++){
			LinearLayout l=(LinearLayout) inflater.inflate(R.layout.saixuan_item, null);
			container.addView(l);
			TextView t=(TextView) l.getChildAt(1);
			t.setText(s[i]);
			final TextView tt=t;
			ImageView image=(ImageView) l.getChildAt(0);
			image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ImageView view=(ImageView) v;
					if(view.getTag().equals("selected")){
						view.setImageResource(R.drawable.ic_launcher);
						list.remove(tt.getText());
						view.setTag("noselected");                
					}else if(view.getTag().equals("noselected")){                                
						view.setImageResource(R.drawable.ic_launcher);
						list.add(tt.getText().toString());
						view.setTag("selected");
					}
					if(list.size()>0){
						System.out.println("list.size()===="+list.size());
						LinearLayout lay=(LinearLayout) container.getChildAt(0);
						ImageView im=(ImageView) lay.getChildAt(0);
						TextView t=(TextView) lay.getChildAt(1);
						im.setImageResource(R.drawable.ic_launcher);
					}else if(list.size()==0){
						System.out.println("list.size()===="+list.size());
						LinearLayout lay=(LinearLayout) container.getChildAt(0);
						ImageView im=(ImageView) lay.getChildAt(0);
						im.setImageResource(R.drawable.ic_launcher);
						im.setTag("selected");
					}
					if(list.size()==s.length&&s.length>=2){
						list.clear();
						initContainer(s, list);
						System.out.println("list.size()===="+list.size());
						LinearLayout lay=(LinearLayout) container.getChildAt(0);
						ImageView im=(ImageView) lay.getChildAt(0);
						im.setImageResource(R.drawable.ic_launcher);
						im.setTag("selected");
					}
				}                      
			});
		}
		
	}

}
