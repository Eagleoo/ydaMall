package com.mall.view;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amap.api.location.AMapLocation;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.serving.community.util.SoundUtil;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;

public class ShockToShock extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
	@ViewInject(R.id.shock_bg_container)
	private LinearLayout shock_bg_container;
	private ImageView shock_bg_icon;
	@ViewInject(R.id.img1_layout)
	private LinearLayout img1_layout;
	@ViewInject(R.id.img2_layout)
	private LinearLayout img2_layout;  
	
	private SharedPreferences sp;
	//1代表商币账户  2.代表充值账户转账
	private PositionService locationService;
	private double location_x=0;  
	private double location_y=0;
	private ServiceConnection locationServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder service) {



			PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
			locationService = locationBinder.getService();
			locationService.startLocation();
			locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
				@Override
				public void onProgress(AMapLocation progress) {
					location_x=progress.getLongitude();
					location_y=progress.getLatitude();
				}

				@Override
				public void onError(AMapLocation error) {

				}
			});
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
	};
	@OnClick(R.id.topback)
	public void Back(View v) {
		// Intent intent=new Intent(this,Lin_MainFrame.class);
		// intent.putExtra("toTab", "find");
		// ShockToShock.this.startActivity(intent);
		ShockToShock.this.finish();
	}
	@OnClick(R.id.topright)
	public void right(View v) {
		Util.showIntent(this, SettingOfShock.class);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shock_to_shock);
		ViewUtils.inject(this);
	}
	@Override
	protected void onStart() {
		super.onStart();
		SoundUtil.InitSound();
		sp = this.getSharedPreferences("shock_data", 0);
		initSensor();
		Intent intent = new Intent();
		intent.setAction(PositionService.TAG);
		intent.setPackage(getPackageName());
		getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ShockToShock.this.finish(); 
		}
		return true;
	}
	
    private void page(){
    	Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				mSensorManager.registerListener(ShockToShock.this,  
						mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
						SensorManager.SENSOR_DELAY_NORMAL);//重新注册
				if(runData==null){
					return;
				}else{      
					System.out.println("------摇一摇结果"+runData);
				}          
			}
			@Override
			public Serializable run() {
//				Web web=new Web(Web.shock, Web.get_Shake_List,"&USER_KEY=a&USER_KEYPWD=b"+"&type="+1+"&x="+location_x+"&y="+location_y+"&remark="+"&userId="+UserData.getUser().getUserId()+"&md5Pwd="+UserData.getUser().getMd5Pwd());
//				return web.getPlan();
				return null;
			}  
		});
    }
    private void initRotateAnimation(){
    }
	public void startAnim() { // 定义摇一摇动画动画
		final ScaleAnimation animation_open =new ScaleAnimation(1f, 1f, 0.0f, 1f, 
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f); 
		animation_open.setDuration(500);
		final ScaleAnimation animation_close =new ScaleAnimation(1f, 1f, 1f, 0f, 
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f); 
		animation_close.setDuration(500); 
		animation_close.setFillAfter(true);
		shock_bg_icon=new ImageView(this);                        
		shock_bg_container.removeAllViews();  
		if (!Util.isNull(sp.getString("shock_img_path", ""))) {  
			Bitmap b=Util.getLocalBitmap(sp.getString("shock_img_path", ""));
			System.out.println("--------图片宽度"+b.getWidth());
			shock_bg_icon.setImageBitmap(Util.getLocalBitmap(sp.getString("shock_img_path", "")));
		}else{      
			shock_bg_icon.setImageResource(R.drawable.ic_launcher);
		}
		LinearLayout.LayoutParams lp=new android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.FILL_PARENT,android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		shock_bg_container.addView(shock_bg_icon);                                      
		shock_bg_icon.setAnimation(animation_open);    
		shock_bg_icon.startAnimation(animation_open);
		animation_open.setAnimationListener(new AnimationListener() {  
			@Override  
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override           
			public void onAnimationEnd(Animation arg0) {
				shock_bg_icon.setAnimation(animation_close);
				shock_bg_icon.startAnimation(animation_close);     
			}
		});
		animation_close.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				shock_bg_container.removeAllViews();
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
			}
		});
		// 图片的动画效果的添加
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mup0 = new TranslateAnimation(         
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mup0.setDuration(500);
		TranslateAnimation mup1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mup1.setDuration(500);
		// 延迟执行1秒
		mup1.setStartOffset(500);
		animup.addAnimation(mup0);
		animup.addAnimation(mup1);
		
		img1_layout.startAnimation(animup);
		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mdn0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mdn0.setDuration(500);
		TranslateAnimation mdn1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mdn1.setDuration(500);
		// 延迟执行1秒
		mdn1.setStartOffset(500);
		animdn.addAnimation(mdn0);
		animdn.addAnimation(mdn1);
		img2_layout.startAnimation(animdn);
		mdn1.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				if (null == UserData.getUser()
						|| Util.isNull(UserData.getUser().getUserId())) {
					Util.showIntent(ShockToShock.this, LoginFrame.class);
					return;
				}
				mSensorManager.unregisterListener(ShockToShock.this);
//				Intent intent = new Intent(ShockToShock.this,
//						MoneyToMoneyFrame.class);
//				intent.putExtra("parentName", "商币");
//				intent.putExtra("userKey", "sb");
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				ShockToShock.this.startActivity(intent);  
				page();
			}
		});  
	
	}  
	private void initSensor() {
		// 获取传感器管理服务
		mSensorManager = (SensorManager) this
				.getSystemService(Service.SENSOR_SERVICE);
		// 加速度传感器
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	@Override
	protected void onStop() {
		super.onStop();
		mSensorManager.unregisterListener(this);
	}
	@Override
	public void onAccuracyChanged(Sensor event, int arg1) {
	}
	@Override 
	public void onSensorChanged(SensorEvent event) {
//		int sensorType = event.sensor.getType();
//		// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
//		float[] values = event.values;
//		if (sensorType == Sensor.TYPE_ACCELEROMETER) {
//			if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math
//					.abs(values[2]) > 17)) {
//				SoundUtil.playSound(6);          
//				startAnim();  
//			}
//		}   
	}
}
