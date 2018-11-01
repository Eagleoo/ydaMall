package com.mall.serving.resturant;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.mall.util.Util;
import com.mall.view.R;

public class ResturantPrice extends Activity {
	private TextView price0, price1, price2, price3, price4, price5,hotel_price_tex;
	private Button button;
	private List<String> price_star = new ArrayList<String>();
	private SeekBar ctrl_skbProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resturantprice);
	    init();
	}
	private void init() {
		button = (Button) this.findViewById(R.id.submit);
		price0 = (TextView) this.findViewById(R.id.price0);
		price1 = (TextView) this.findViewById(R.id.price1);
		price2 = (TextView) this.findViewById(R.id.price2);
		price3 = (TextView) this.findViewById(R.id.price3);
		price4 = (TextView) this.findViewById(R.id.price4);
		price5 = (TextView) this.findViewById(R.id.price5);

		price0.setOnClickListener(new PriceClick());
		price1.setOnClickListener(new PriceClick());
		price2.setOnClickListener(new PriceClick());
		price3.setOnClickListener(new PriceClick());
		price4.setOnClickListener(new PriceClick());
		price5.setOnClickListener(new PriceClick());
		
		hotel_price_tex=(TextView) this.findViewById(R.id.hotel_price_tex);
		ctrl_skbProgress=(SeekBar) this.findViewById(R.id.ctrl_skbProgress);
		ctrl_skbProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			} 
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(progress>=5){
//					if(progress==5){
//						hotel_price_tex.setText("0-800元");
//					}else if(progress>=50){
//						hotel_price_tex.setText("600-1000元");
//					}else if(progress>=40&&progress<50){
//						hotel_price_tex.setText("451-600元");
//					}else if(progress>=30&&progress<40){
//						hotel_price_tex.setText("301-450元");
//					}else if(progress>=20&&progress<30){
//						hotel_price_tex.setText("150-300元");
//					}else if(progress>=10&&progress<20){
//						hotel_price_tex.setText("0-150元");
//					}else{
					   
						hotel_price_tex.setText(progress*8+"-"+800+"元");
//					}
				}
			}
		});
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String star_price="";
				String range_price="";
			     for(int i=0;i<price_star.size();i++){
						star_price+=price_star.get(i);
				}
			    range_price=hotel_price_tex.getText().toString();
				Intent intent=new Intent(ResturantPrice.this,ResturantIndex.class);
				if(Util.isNull(star_price)){
					star_price="不限";
				}
				intent.putExtra("star_price", star_price);
				intent.putExtra("range_price", range_price);
				ResturantPrice.this.setResult(200, intent);
				ResturantPrice.this.finish();
			}
		});
	}

	private class PriceClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			TextView view = (TextView) v;
			String ta = (String) view.getTag();
			if (ta.equals("noselected")) {
				if(view.getText().toString().equals("不限")){
					  //如果选中的是不限
					  price0.setBackgroundColor(Color.TRANSPARENT);
			          price1.setBackgroundColor(Color.TRANSPARENT);
			          price2.setBackgroundColor(Color.TRANSPARENT);
			          price3.setBackgroundColor(Color.TRANSPARENT);
			          price4.setBackgroundColor(Color.TRANSPARENT);
			          price5.setTag("selected");
				}else{
					//如果不是选中的不限
					price5.setBackgroundColor(Color.TRANSPARENT);
					price5.setTag("noselected");
					if(price_star.contains("不限"))
					{
						price_star.remove("不限");
					}
				}
				view.setBackgroundColor(Color.parseColor("#dbd8d8"));
				v.setTag("selected");
				price_star.add(view.getText().toString());
			} else if (ta.equals("selected")) {
				//如果已经是被选中状态，点击则设置为白色
				view.setBackgroundColor(Color.TRANSPARENT);
				v.setTag("noselected");
				price_star.remove(view.getText().toString());
			}
//			int progress=50;
//			if(price_star.contains("五星")&&price_star.size()==1){ 
//				ctrl_skbProgress.setProgress(50);
//			}
//			if(price_star.contains("四星")&&price_star.size()==1){
//				ctrl_skbProgress.setProgress(40);
//			}
//			if(price_star.contains("三星")&&price_star.size()==1){
//				ctrl_skbProgress.setProgress(30);
//			}
//			if(price_star.contains("二星")&&price_star.size()==1){
//				ctrl_skbProgress.setProgress(20);
//			}
//			if(price_star.contains("经济型")&&price_star.size()==1){
//				ctrl_skbProgress.setProgress(10);
//			}
//			if(price_star.contains("不限")){ 
//				ctrl_skbProgress.setProgress(5);
//			}
			
			//判断如果已经选中了除不限歪其他所有。则状态改为不限.
			if (price0.getTag().equals("selected")
					& price1.getTag().equals("selected")
					&& price2.getTag().equals("selected")
					&& price3.getTag().equals("selected")
					&& price4.getTag().equals("selected")) {
	             price_star.clear();
	             price_star.add("不限");
	             price0.setBackgroundColor(Color.TRANSPARENT);
	             price1.setBackgroundColor(Color.TRANSPARENT);
	             price2.setBackgroundColor(Color.TRANSPARENT);
	             price3.setBackgroundColor(Color.TRANSPARENT);
	             price4.setBackgroundColor(Color.TRANSPARENT);
	             price0.setTag("noselected");
	             price1.setTag("noselected");
	             price2.setTag("noselected");
	             price3.setTag("noselected");
	             price4.setTag("noselected");
	             price5.setBackgroundColor(Color.parseColor("#dbd8d8"));
	             price5.setTag("selected");
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		String star_price="";
		String range_price="";
		if(keyCode==KeyEvent.KEYCODE_BACK){
			for(int i=0;i<price_star.size();i++){
				star_price+=price_star.get(i)+"|";
			}
			 range_price=hotel_price_tex.getText().toString();
		}
		Intent intent=new Intent(ResturantPrice.this,ResturantIndex.class);
		intent.putExtra("star_price", star_price);
		intent.putExtra("range_price", range_price);
		setResult(200, intent);
		return super.onKeyDown(keyCode, event);
	}
}
