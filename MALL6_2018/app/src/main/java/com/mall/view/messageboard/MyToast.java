package com.mall.view.messageboard;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.view.App;
import com.mall.view.R;

public class MyToast extends Toast{
    static Toast result ;
	public MyToast(Context context) {
		super(context);
	}
    public static Toast makeText(Context context,String message,int duration){
        if (result==null){
            result = new Toast(App.getContext());
        }
        //获取LayoutInflater对象
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //由layout文件创建一个View对象
        View layout = inflater.inflate(R.layout.dialog_toast, null);
        //实例化ImageView和TextView对象
        TextView textView = (TextView) layout.findViewById(R.id.message);
        textView.setText(message);
        result.setView(layout);
        result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        result.setDuration(duration);
        return result;
    }
}
