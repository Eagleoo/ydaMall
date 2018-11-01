package com.mall.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2018/3/28.
 */

public class SquareImageView extends android.support.v7.widget.AppCompatImageView {

    private int  measureWidth;

    private int  measuredHight;


    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureWidth=getMeasuredWidth();
        measuredHight=getMeasuredHeight();
        if (measureWidth>measuredHight){
            measureWidth=measuredHight;
        }else {
            measuredHight=measureWidth;
        }

        setMeasuredDimension(measureWidth,measuredHight);

    }
}

