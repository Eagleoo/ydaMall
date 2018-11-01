package com.mall.serving.community.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;

public class BaseUpPicActivity extends Activity {
	public BaseUpPicActivity context;
	public String path = "";
	public static final int TAKE_PICTURE = 0x000000;
	public List<String> imgList;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
	}

	public void photo() {

	}

	
}
