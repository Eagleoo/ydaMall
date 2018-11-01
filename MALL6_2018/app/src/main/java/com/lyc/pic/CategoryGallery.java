package com.lyc.pic;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

public class CategoryGallery extends Gallery {  
    
    private static final int OFFSETX = 0;  
  
    public CategoryGallery(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    public CategoryGallery(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public CategoryGallery(Context context) {  
        super(context);  
    }  
  
    float startX;  
  
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {  
            startX = ev.getX();  
        } else {  
            float abs = Math.abs(startX - ev.getX());  
            if (abs > OFFSETX) {  
                return true;  
            }  
        }  
        return super.onInterceptTouchEvent(ev);  
    }  
  
}  
