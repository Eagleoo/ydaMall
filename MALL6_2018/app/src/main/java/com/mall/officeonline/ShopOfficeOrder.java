package com.mall.officeonline;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

public class ShopOfficeOrder extends Activity{
	private int page=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_office_order);
		init();
	}
	private void init(){
		Util.initTop(this, "商品订单", Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShopOfficeOrder.this.finish();
			}
		});
	}
	
	private void getOrder(){
		if(UserData.getUser()==null){
			Util.showIntent(this, LoginFrame.class);
		}
		final User u=UserData.getUser();
		Util.asynTask(this,"",new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				
			}
			@Override
			public Serializable run() {
				Web web=new Web(Web.officeUrl, Web.GetOfficeUserAllOrder,"userId="+u.getUserId()+"&md5Pwd="+u.getMd5Pwd()+"&pageSize=15"+"&page="+(page++));
				return null;
			}
		});
	}

}
