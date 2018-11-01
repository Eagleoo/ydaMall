package com.Base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by Administrator on 2018/3/26.
 */

public class BasePopWindow extends PopupWindow

        implements PopupWindow.OnDismissListener {

    public enum LocationConstants {
        CENTER, LEFT, RIGHT, TOP, BOTTOM, NO_GRAVITY
    }


    private executionPopdis mListener;

    public interface executionPopdis {
        public void onExecution();
    }

    private final View mContentView;
    private final Context mContext;


    @Override
    public void onDismiss() {
        if (mListener != null) {
            mListener.onExecution();
        }
        Log.e("onDismiss", "onDismiss");
        darkenBackground(1f, mContext);
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     *
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return window显示的左上角的xOff, yOff坐标
     */

    public static boolean PopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        // 测量contentView
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);

        return isNeedShowUp;
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public void showLeft() {
        showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.LEFT, 0, 0);
    }

    public void showRight() {
        showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.RIGHT, 0, 0);
    }

    public void showBottom() {
        showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    public void showTop() {
        showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.TOP, 0, 0);
    }

    public void showCenter() {
        showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    public void showViewanchorLeft(View anchor) {
        int h = getHeight() > anchor.getHeight() ? (getHeight() - anchor.getHeight()) / 2 :
                (anchor.getHeight() - getHeight()) / 2;
        int[] positions = new int[2];
        anchor.getLocationOnScreen(positions);
        showAtLocation(anchor, Gravity.NO_GRAVITY,
                positions[0] - getWidth() - 15,
                getHeight() > anchor.getHeight() ? positions[1] - h : positions[1] + h);
    }

    public void showViewanchorRight(View anchor) {
        int h = getHeight() > anchor.getHeight() ? (getHeight() - anchor.getHeight()) / 2 :
                (anchor.getHeight() - getHeight()) / 2;
        int[] positions = new int[2];
        anchor.getLocationOnScreen(positions);
        showAtLocation(anchor, Gravity.NO_GRAVITY,
                positions[0] + anchor.getWidth(),
                getHeight() > anchor.getHeight() ? positions[1] - h : positions[1] + h);
    }

    public int setrelativeposition(LocationConstants location, View anchor) {
        int distance = 0;
        switch (location) {
            case LEFT:
                distance = 0;
                break;
            case CENTER:
                distance = getWidth() > anchor.getWidth() ? (getWidth() - anchor.getWidth()) / 2 :
                        (anchor.getWidth() - getWidth()) / 2;

        }
        return distance;
    }

    public void showViewanchorBottom(View anchor) {
        if (!PopWindowPos(anchor, mContentView)) {
            int w = getWidth() > anchor.getWidth() ? (getWidth() - anchor.getWidth()) / 2 :
                    (anchor.getWidth() - getWidth()) / 2;
            //得到呼出view的坐标
            int[] positions = new int[2];
            anchor.getLocationOnScreen(positions);
            showAtLocation(anchor, Gravity.NO_GRAVITY,
                    getWidth() > anchor.getWidth() ? positions[0] - w : positions[0] + w,
                    positions[1] + anchor.getHeight());
        } else {
            showViewanchorTop(anchor);
        }


    }

    public void showViewanchorTop(View anchor) {
        if (PopWindowPos(anchor, mContentView)) {
            int w = getWidth() > anchor.getWidth() ? (getWidth() - anchor.getWidth()) / 2 :
                    (anchor.getWidth() - getWidth()) / 2;
            //得到呼出view的坐标
            int[] positions = new int[2];
            anchor.getLocationOnScreen(positions);
            showAtLocation(anchor, Gravity.NO_GRAVITY,
                    getWidth() > anchor.getWidth() ? positions[0] - w : positions[0] + w,
                    positions[1] - anchor.getHeight());
        } else {
            showViewanchorBottom(anchor);
        }

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
    private static void darkenBackground(Float alpha, Context context) {
        Log.e("改变背景颜色", "改变背景颜色1");
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = alpha;
        ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity) context).getWindow().setAttributes(lp);


//        if (alpha < 0 || alpha > 1) return;
//        WindowManager.LayoutParams windowLP = ((Activity) context).getWindow().getAttributes();
//        windowLP.alpha = alpha;
//        if (alpha == 1) {
//            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
//        } else {
//            ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
//        }
//        ((Activity) context).getWindow().setAttributes(windowLP);

    }

    public BasePopWindow(Builder builder) {
        Log.e("MyPopWindow", "0");
        mContentView = builder.mContentView;
        mContext = builder.mContext;
        mListener = builder.ex;
        setContentView(mContentView);

        if (builder.mWidth == 0 && builder.mHeight == 0) {
            mContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            setWidth(mContentView.getMeasuredWidth());
            setHeight(mContentView.getMeasuredHeight());
        } else {
            setWidth(builder.mWidth);
            setHeight(builder.mHeight);
        }
        if (builder.mDrawable == null) {

            setBackgroundDrawable(new BitmapDrawable());
        } else {
            setBackgroundDrawable(builder.mDrawable);
        }

//        setClippingEnabled(false);
        setFocusable(true);
        setOutsideTouchable(true);
        setFocusable(true);

        if (builder.animationStyle != 0) {
            setAnimationStyle(builder.animationStyle);
        }
        Log.e("是否显示", "ischagebackcolor" + builder.ischagebackcolor);

        // 如果想要popupWindow 遮挡住状态栏可以加上这句代码

        setOnDismissListener(this);
        ////        if (builder.ischagebackcolor) {
        darkenBackground(0.45f, mContext);
////        }
    }

    public static class Builder {
        private final Context mContext;
        private final View mContentView;  //布局view
        private int mWidth;
        private int mHeight;
        private Drawable mDrawable;
        private int animationStyle;
        private View anchorView;  //呼出View
        private boolean ischagebackcolor;
        private executionPopdis ex;

        //写一个设置接口监听的方法
        public Builder setExecutionPopdisListener(executionPopdis listener) {
            ex = listener;
            return this;
        }

        public Builder(Context context, View contentview) {
            this.mContext = context;
            this.mContentView = contentview;
        }


        public Builder setIschagebackcolor(Boolean ischagebackcolor) {
            this.ischagebackcolor = ischagebackcolor;
            return this;
        }

        public Builder setAnchorView(View anchorView) {
            this.anchorView = anchorView;
            return this;
        }


        public Builder setAnimationStyle(int animationStyle) {
            this.animationStyle = animationStyle;
            return this;
        }

        public Builder setWidth(int width, int hight) {
            mWidth = width;
            mHeight = hight;
            return this;
        }


        public Builder setDrawable(Drawable drawable) {
            mDrawable = drawable;
            return this;
        }


        public BasePopWindow build() {
            return new BasePopWindow(this);
        }

    }

}
