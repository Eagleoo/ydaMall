package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.util.Ios_switchButton;
import com.mall.util.Util;

public class SettingOfShock extends Activity{
	private Ios_switchButton topswitchButton;
	private Ios_switchButton viberate_button;
	private String localImagePath="";
	@ViewInject(R.id.choosed_image)
	private ImageView choosed_image;
	@ViewInject(R.id.choosed_default_img)
	private ImageView choosed_default_img;
	private SharedPreferences sp;
	@OnClick(R.id.choose_bg)
	public void ChooseBG(View v){
		Intent intent = new Intent(this, SelectPicActivity.class);
		startActivityForResult(intent, 3);
	}
	@OnClick(R.id.confirum)
	private void Confirum(View v){
		SettingOfShock.this.finish();
	}
	@OnClick(R.id.default_bg)
	public void Default(View v){
		sp.edit().putString("shock_img_path","").commit();
		 choosed_default_img.setVisibility(View.VISIBLE);
		 choosed_image.setVisibility(View.GONE);
	}
	@OnClick(R.id.shock_log)
	public void ShockLog(View v){
		Intent intent=new Intent(this,ListOfShock.class);
		intent.putExtra("type", "1");
		intent.putExtra("title", "摇一摇记录");
		this.startActivity(intent);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_of_shock);
		ViewUtils.inject(this);   
	}                                
	@Override
	protected void onStart() {   
		super.onStart();  
		sp=this.getSharedPreferences("shock_data", 0);
		init(); 
	}
	private void init(){
		 topswitchButton=(Ios_switchButton) this.findViewById(R.id.center_switch_button);
		 viberate_button=(Ios_switchButton) this.findViewById(R.id.viber_button);
		 topswitchButton.imageInit(this, R.drawable.switch_btn_bg_green, R.drawable.switch_btn_bg_white, R.drawable.ios_switch_btn_normal, R.drawable.ios_switch_btn_pressed);
		 boolean b_voice=sp.getBoolean("isvoice", true);
		 boolean b_viberate=sp.getBoolean("viberate", true);
		 if(b_voice){
			 topswitchButton.setChecked(true);  
		 }else{
			 topswitchButton.setChecked(false);    
		 }
		 topswitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		        @Override
		        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
		        	sp.edit().putBoolean("isvoice", b).commit();
		        }
		    });
		 
		 if(b_viberate){
			 viberate_button.setChecked(true);  
		 }else{
			 viberate_button.setChecked(false);    
		 } 
		 viberate_button.imageInit(this, R.drawable.switch_btn_bg_green, R.drawable.switch_btn_bg_white, R.drawable.ios_switch_btn_normal, R.drawable.ios_switch_btn_pressed);
		 viberate_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		        @Override
		        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
		        	sp.edit().putBoolean("viberate", b).commit();
		        }
		    });
		 
		 
		 if(!Util.isNull(sp.getString("shock_img_path", ""))){
			 choosed_image.setVisibility(View.VISIBLE);
			 choosed_image.setImageBitmap(Util.getLocalBitmap(sp.getString("shock_img_path", "")));
			 choosed_default_img.setVisibility(View.GONE);
		 }else{
			 choosed_default_img.setVisibility(View.VISIBLE);
			 choosed_image.setVisibility(View.GONE);
		 }
	}              
	@OnClick(R.id.topback)              
	public void Back(View v){  
		this.finish();
	}  
	protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == 3) {
			localImagePath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
			choosed_image.setImageBitmap(Util.getLocalBitmap(localImagePath));
			sp.edit().putString("shock_img_path",localImagePath).commit();
			choosed_image.setVisibility(View.VISIBLE);
			choosed_default_img.setVisibility(View.GONE);
		}
	};
}
