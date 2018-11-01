package com.mall.view;

import com.mall.view.FragmentMySpace.AFrangment;

import android.os.Bundle;
import android.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class ShopOfficeFrame2 extends FragmentActivity {
	private FragmentManager manager;
	LinearLayout baobei_layout,notesLayout,vedioLayout,albumLayout,bmLayout;  //宝贝，日志，视频，相册，便民
	FrameLayout fl;
	Fragment aFragment,bFragment,cFragment,dFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_office_frame2);
		manager = getSupportFragmentManager();
		init();
		
	}
	

	private void init() {
		// TODO Auto-generated method stub
		initlayout();
		initListen();
	}





	private void initlayout() {
		// TODO Auto-generated method stub
		baobei_layout=(LinearLayout) findViewById(R.id.bottom_layout);
		notesLayout=(LinearLayout) findViewById(R.id.notesLayout);
		vedioLayout=(LinearLayout) findViewById(R.id.vedioLayout);
		albumLayout=(LinearLayout) findViewById(R.id.albumLayout);
		bmLayout=(LinearLayout) findViewById(R.id.bmLayout);
		fl=(FrameLayout) findViewById(R.id.fl);
		
	}
	private void initListen() {
		// TODO Auto-generated method stub
		
	
		
		baobei_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		notesLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		vedioLayout.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
	});
		albumLayout.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
	});
		bmLayout.setOnClickListener(new OnClickListener() {
			
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
	});
	}


	private void initFragment() {
		// TODO Auto-generated method stub
		if (aFragment==null) {
			aFragment=new AFrangment();
		}
		FragmentTransaction transaction = manager.beginTransaction();
//		transaction.add(fl, afFragment);
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shop_office_frame2, menu);
		return true;
	}


	

}
