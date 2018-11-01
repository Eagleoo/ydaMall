package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by Administrator on 2017/11/10.
 */

public  class ShowPopWindow {
    public static PopupWindow showShareWindow
            (View mContentView, final Context context, int with, int hight, int animationStyle){
        PopupWindow mPopUpWindow = null;


        if(mPopUpWindow == null){

//            int with= (int) (Utlis.getScreenWidth(context)*0.75);
//            mPopUpWindow = new PopupWindow(mContentView, with, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopUpWindow = new PopupWindow(mContentView, with, hight);
            mPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopUpWindow.setOutsideTouchable(true);
            mPopUpWindow.setFocusable(true);
        }
        if (animationStyle!=0){
            mPopUpWindow.setAnimationStyle(animationStyle);
        }

        mPopUpWindow.showAtLocation(((Activity)context).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        mPopUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(1f,context);
            }
        });
        darkenBackground(0.45f,context);
        return mPopUpWindow;
    }

    public static PopupWindow showShareWindow_TransparentBackground
            (View mContentView, final Context context, int with, int hight, int animationStyle){
        PopupWindow mPopUpWindow = null;


        if(mPopUpWindow == null){

            mPopUpWindow = new PopupWindow(mContentView, with, hight);
            mPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopUpWindow.setOutsideTouchable(true);
            mPopUpWindow.setFocusable(true);
        }
        if (animationStyle!=0){
            mPopUpWindow.setAnimationStyle(animationStyle);
        }

        mPopUpWindow.showAtLocation(((Activity)context).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//        mPopUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                darkenBackground(1f,context);
//            }
//        });
//        darkenBackground(0.45f,context);
        return mPopUpWindow;
    }

    /**
     * 改变背景颜色
     */
    public static void darkenBackground(Float bgcolor,Context context){
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = bgcolor;
        ((Activity)context). getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity)context).getWindow().setAttributes(lp);

    }
    public static PopupWindow showSharebottomWindow(View mContentView, final Context context,int with,int hight,int animationStyle){
        PopupWindow mPopUpWindow = null;


        if(mPopUpWindow == null){

//            int with= (int) (Utlis.getScreenWidth(context)*0.75);
//            mPopUpWindow = new PopupWindow(mContentView, with, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopUpWindow = new PopupWindow(mContentView, with, hight);
            mPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopUpWindow.setOutsideTouchable(true);
            mPopUpWindow.setFocusable(true);
        }
        if (animationStyle!=0){
            mPopUpWindow.setAnimationStyle(animationStyle);
        }


        mPopUpWindow.showAtLocation(((Activity)context).getWindow().getDecorView(),  Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0, 0);
        mPopUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(1f,context);
            }
        });
        darkenBackground(0.45f,context);
        return mPopUpWindow;
    }

}
