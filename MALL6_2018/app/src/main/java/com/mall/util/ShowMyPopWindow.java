package com.mall.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by MECHREVO on 2018/3/12.
 */

public class ShowMyPopWindow  extends PopupWindow

        implements PopupWindow.OnDismissListener
{
    private  executionPopdis mListener;

    //写一个设置接口监听的方法
    public  void setExecutionPopdisListener(executionPopdis listener) {
        mListener = listener;
    }


    public interface executionPopdis {
        public void onExecution();
    }
    private View mContentView;
    private Context mContext;

    /**
     *
     * @param view
     * @param context  这个是父类的
     * @param with
     * @param hight
     * @param animationStyle
     */
    public ShowMyPopWindow( View view, final Context context, int with, int hight, LocationConstants location, int animationStyle){
        mContext=context;
        mContentView=view;
        setContentView(mContentView);
        setWidth(with);
        setHeight(hight);
        // 如果想要popupWindow 遮挡住状态栏可以加上这句代码
//        setClippingEnabled(false);
        if (animationStyle!=0){
            setAnimationStyle(animationStyle);
        }
//        setOutsideTouchable(true);
//        setBackgroundDrawable(new BitmapDrawable());

        switch (location){
            case NO_GRAVITY:
                showAtLocation(((Activity)context).getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
            case CENTER:
                showAtLocation(((Activity)context).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                break;
            case BOTTOM:
                showAtLocation(((Activity)context).getWindow().getDecorView(),  Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0, 0);
                break;
        }

        darkenBackground(0.45f,mContext);
        setOnDismissListener(this);
    }

    @Override
    public void onDismiss() {
        if (mListener!=null){
            mListener.onExecution();
        }
        darkenBackground(1f,mContext);
    }


    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
        super.showAsDropDown(anchor);
    }

    /**
     * 改变背景颜色
     */
    private static void darkenBackground(Float bgcolor,Context context){
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = bgcolor;
        ((Activity)context). getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity)context).getWindow().setAttributes(lp);

    }


}
