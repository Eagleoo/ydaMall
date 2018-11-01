package com.example.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.mall.util.Util;
import com.mall.view.R;

public class RedDetailDialog {

	private AlertDialog dialog;
	private OnClickListener left;
	private OnClickListener right;
	private View root;
	private TextView tv1,tv2,tv3,tv4,tv5;
	private TextView username,phone_number,money_tv,time_tv,realname;
	private TextView close;





	public RedDetailDialog(final Context context) {
		dialog = new AlertDialog.Builder(context,R.style.MyDialog).create();
		dialog.show();
		Window window = dialog.getWindow();

		//w.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置  
		window.setWindowAnimations(R.style.dialog_inout_anim);  //添加动画  
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 有白色背景，加

		root = LayoutInflater.from(context).inflate(R.layout.reditem, null);
		tv1= (TextView) root.findViewById(R.id.tv1);
		tv2= (TextView) root.findViewById(R.id.tv2);
		tv3= (TextView) root.findViewById(R.id.tv3);
		tv4= (TextView) root.findViewById(R.id.tv4);
		tv5= (TextView) root.findViewById(R.id.tv5);

		username= (TextView) root.findViewById(R.id.username);
		phone_number= (TextView) root.findViewById(R.id.phone_number);
		money_tv= (TextView) root.findViewById(R.id.money_tv);
		time_tv= (TextView) root.findViewById(R.id.time_tv);
		realname= (TextView) root.findViewById(R.id.realname);

		close= (TextView) root.findViewById(R.id.close);

		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		tv1.setText(Util.justifyString("用户名:",6));
		tv2.setText(Util.justifyString("手机号码:",6));
		tv3.setText(Util.justifyString("转入金额:",6));
		tv4.setText(Util.justifyString("注册时间:",6));
		tv5.setText(Util.justifyString("真实姓名:",6));

		window.setContentView(root);
	}

	public RedDetailDialog settv3(String txt){
		tv3.setText(Util.justifyString(txt,6));
		return  this;
	}

	public RedDetailDialog settv4(String txt){
		tv4.setText(Util.justifyString(txt,6));
		return  this;
	}

	public RedDetailDialog setUserName(CharSequence txt){
		username.setText(txt);
		return  this;
	}
	public RedDetailDialog setPhone(CharSequence txt){
		phone_number.setText(txt);
		return  this;
	}

	public RedDetailDialog setMoney(CharSequence txt){
		money_tv.setText(txt);
		return  this;
	}
	public RedDetailDialog setTime(CharSequence txt){
		time_tv.setText(txt);
		return  this;
	}

	public RedDetailDialog setRealname(CharSequence txt){
		realname.setText(txt);
		return  this;
	}


	public RedDetailDialog setRight(CharSequence txt){
		return this;
	}
	
	public RedDetailDialog setLeft(CharSequence txt){
		return this;
	}

	public RedDetailDialog showcancel(int ViState){
		return  this;
	}

	public RedDetailDialog showtag1(int ViState){
		return  this;
	}
	public RedDetailDialog showtag2(int ViState){
		return  this;
	}


	public RedDetailDialog showtime(int ViState){
		return  this;
	}


//		dialog.setView(root, 0, 0, 0, 0);


//		dialog.getWindow().getAttributes().width = (UIUtils.getScreenWidth(dialog.getContext())/3)*2;

	}


