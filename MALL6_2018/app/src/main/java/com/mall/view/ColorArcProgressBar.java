package com.mall.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * colorful arc progress bar
 * Created by shinelw on 12/4/15.
 */

public class ColorArcProgressBar extends View{

    private int mWidth;
    private int mHeight;
    private int diameter = 500;  //直径
    private float centerX;  //圆心X坐标
    private float centerY;  //圆心Y坐标

    private Paint allArcPaint1;
    private Paint allArcPaint;
    private Paint allArcPaint3;

    private Paint progressPaint;
    private TextPaint vTextPaint;
    private Paint hintPaint;
    private Paint degreePaint;
    private Paint curSpeedPaint;

    private RectF bgRect1;
    private RectF bgRect2;
    private RectF bgRect3;

    float   radius;

    private ValueAnimator progressAnimator;
    private PaintFlagsDrawFilter mDrawFilter;
    private SweepGradient sweepGradient;
    private Matrix rotateMatrix;

    private float startAngle = 270;
    private float sweepAngle = 270;
    private float currentAngle = 0;
    private float lastAngle;
    private int[] colors = new int[]{Color.GREEN, Color.YELLOW, Color.RED, Color.RED};
    private float maxValues = 60;
    private float curValues = 0;
    private float bgArcWidth = dipToPx(2);
    private float progressWidth = dipToPx(10);
    private float textSize = dipToPx(15);
    private float hintSize = dipToPx(15);
    private float curSpeedSize = dipToPx(13);
    private int aniSpeed = 3000;
    private float longdegree = dipToPx(13);
    private float shortdegree = dipToPx(5);
    private final int DEGREE_PROGRESS_DISTANCE = dipToPx(8);

    private String hintColor = "#676767";
    private String longDegreeColor = "#111111";
    private String shortDegreeColor = "#111111";
    private String bgArcColor = "#F2F2F2";
    private String titleString;
    private String hintString;

    private boolean isShowCurrentSpeed = true;
    private boolean isNeedTitle;
    private boolean isNeedUnit;
    private boolean isNeedDial;
    private boolean isNeedContent;

    private float bitMapx;
    private float bitMapy;

    private float centerRect1x;
    private float centerRect1y;

    // sweepAngle / maxValues 的值
    private float k;

    public ColorArcProgressBar(Context context) {
        super(context, null);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initCofig(context, attrs);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCofig(context, attrs);
        initView();
    }

    /**
     * 初始化布局配置
     * @param context
     * @param attrs
     */
    private void initCofig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorArcProgressBar);
        int color1 = a.getColor(R.styleable.ColorArcProgressBar_front_color1, Color.GREEN);
        int color2 = a.getColor(R.styleable.ColorArcProgressBar_front_color2, color1);
        int color3 = a.getColor(R.styleable.ColorArcProgressBar_front_color3, color1);
        colors = new int[]{color1, color2, color3, color3};

        sweepAngle = a.getInteger(R.styleable.ColorArcProgressBar_total_engle, 270);
        bgArcWidth = a.getDimension(R.styleable.ColorArcProgressBar_back_width, dipToPx(2));
        progressWidth = a.getDimension(R.styleable.ColorArcProgressBar_front_width, dipToPx(10));
        isNeedTitle = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_title, false);
        isNeedContent = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_content, false);
        isNeedUnit = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_unit, false);
        isNeedDial = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_dial, false);
        hintString = a.getString(R.styleable.ColorArcProgressBar_string_unit);
        titleString = a.getString(R.styleable.ColorArcProgressBar_string_title);
        curValues = a.getFloat(R.styleable.ColorArcProgressBar_current_value, 0);
        maxValues = a.getFloat(R.styleable.ColorArcProgressBar_max_value, 60);
        setCurrentValues(curValues);
        setMaxValues(maxValues);
        a.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (int) (2 * longdegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE);
        int height= (int) (2 * longdegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE);
        setMeasuredDimension(width, height);
    }

    private void initView() {

        diameter =  (getScreenWidth() / 2)-dipToPx(50);
//        diameter = 300;
        //弧形的矩阵区域
        bgRect1 = new RectF();
        bgRect2 = new RectF();
        bgRect3 = new RectF();
        bgRect1.top = longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE;
        bgRect2.top = longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE+30;
        bgRect3.top = longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE+60;


        Log.e("bgRect1.top","bgRect1.top:"+bgRect1.top);//bitMapx bitMapy: bitMapx:216.0bitMapy:39.5

        bgRect1.left = longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE;
        bgRect2.left = longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE+30;
        bgRect3.left = longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE+60;


        bgRect1.right = diameter + (longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE);
        bgRect2.right = diameter + (longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE)-30;
        bgRect3.right = diameter + (longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE)-60;


        bgRect1.bottom = diameter + (longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE);
        bgRect2.bottom = diameter + (longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE)-30;
        bgRect3.bottom = diameter + (longdegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE)-60;



        radius= (bgRect1.right- bgRect1.left)/2;


        //圆心
        centerX = (2 * longdegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE)/2;
        centerY = (2 * longdegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE)/2;

        //外部刻度线
        degreePaint = new Paint();
        degreePaint.setColor(Color.parseColor(longDegreeColor));


        //外部弧形
        allArcPaint1 = new Paint();
        allArcPaint1.setAntiAlias(true);
        allArcPaint1.setStyle(Paint.Style.STROKE);
        allArcPaint1.setStrokeWidth(10);
        allArcPaint1.setColor(Color.parseColor(bgArcColor));
        allArcPaint1.setStrokeCap(Paint.Cap.ROUND);

        //整个弧形
        allArcPaint = new Paint();
        allArcPaint.setAntiAlias(true);
        allArcPaint.setStyle(Paint.Style.STROKE);
        allArcPaint.setStrokeWidth(20);
        allArcPaint.setColor(Color.parseColor(bgArcColor));
        allArcPaint.setStrokeCap(Paint.Cap.ROUND);

        allArcPaint3 = new Paint();
        allArcPaint3.setAntiAlias(true);
        allArcPaint3.setStyle(Paint.Style.STROKE);
        allArcPaint3.setStrokeWidth(5);
        allArcPaint3.setColor(Color.parseColor(bgArcColor));
        allArcPaint3.setStrokeCap(Paint.Cap.ROUND);

        //当前进度的弧形
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(20);
        progressPaint.setColor(Color.GREEN);

        //内容显示文字
        vTextPaint = new TextPaint();
        vTextPaint.setTextSize(textSize);
        vTextPaint.setColor(Color.parseColor("#FFB01C"));
        vTextPaint.setTextAlign(Paint.Align.CENTER);

        //显示单位文字
        hintPaint = new Paint();
        hintPaint.setTextSize(hintSize);
        hintPaint.setColor(Color.parseColor(hintColor));
        hintPaint.setTextAlign(Paint.Align.CENTER);

        //显示标题文字
        curSpeedPaint = new Paint();
        curSpeedPaint.setTextSize(curSpeedSize);
        curSpeedPaint.setColor(Color.parseColor(hintColor));
        curSpeedPaint.setTextAlign(Paint.Align.CENTER);

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        sweepGradient = new SweepGradient(centerX, centerY, colors, null);
        rotateMatrix = new Matrix();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //抗锯齿
        canvas.setDrawFilter(mDrawFilter);

        if (isNeedDial) {
            //画刻度线
            for (int i = 0; i < 40; i++) {
                if (i > 15 && i < 25) {
                    canvas.rotate(9, centerX, centerY);
                    continue;
                }
                if (i % 5 == 0) {
                    degreePaint.setStrokeWidth(dipToPx(2));
                    degreePaint.setColor(Color.parseColor(longDegreeColor));
                    canvas.drawLine(centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE,
                            centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE - longdegree, degreePaint);
                } else {
                    degreePaint.setStrokeWidth(dipToPx(1.4f));
                    degreePaint.setColor(Color.parseColor(shortDegreeColor));
                    canvas.drawLine(centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE - (longdegree - shortdegree) / 2,
                            centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE - (longdegree - shortdegree) / 2 - shortdegree, degreePaint);
                }

                canvas.rotate(9, centerX, centerY);
            };
        }


//        bitMapx=(bgRect1.right-bgRect1.left)/2;
//        bitMapy=bgRect1.top;
        centerRect1x=bgRect1.centerX();
        centerRect1y=bgRect1.centerY();

//        bitMapy= (float) (radius*Math.sin(Math.toRadians(30)));
//        bitMapx= (float) (radius*Math.cos(Math.toRadians(currentAngle)));

        bitMapy= (float) (centerRect1x+(radius*Math.sin(Math.toRadians(currentAngle-90f))));
        bitMapx= (float) (centerRect1y+(radius*Math.cos(Math.toRadians(currentAngle-90f))));


        //整个弧
//
//        canvas.drawArc(bgRect1, startAngle, sweepAngle, false, allArcPaint1);
        canvas.drawArc(bgRect2, startAngle, sweepAngle, false, allArcPaint);
        canvas.drawArc(bgRect3, startAngle, sweepAngle, false, allArcPaint3);


        //设置渐变色
        rotateMatrix.setRotate(130, centerX, centerY);
        sweepGradient.setLocalMatrix(rotateMatrix);
        progressPaint.setShader(sweepGradient);
        allArcPaint1.setShader(sweepGradient);

        //当前进度
        canvas.drawArc(bgRect2, startAngle, currentAngle, false, progressPaint);
        canvas.drawArc(bgRect1, startAngle, currentAngle, false, allArcPaint1);

        if (isNeedContent) {


            if (maxValues==700){
                canvas.drawText(   String.format("%.0f", 0.0)+"天", centerX, centerY + textSize / 3, vTextPaint);
            }else{
//                canvas.drawText(   String.format("%.0f", curValues)+"天", centerX, centerY + textSize / 3, vTextPaint);
                canvas.drawText(   curValues+"天", centerX, centerY + textSize / 3, vTextPaint);
            }




//            Log.e("arseInt(bgRect3.width",Math.floor(bgRect3.width())+"LL");
//
//            StaticLayout layout = new StaticLayout("距离下次开启还有\t\n"+String.format("%.0f", maxValues-curValues)+"天"
//                    ,vTextPaint,  (int)Math.floor(bgRect3.width()), Layout.Alignment.ALIGN_NORMAL,1.0F,1.0F,true);
//
//
//
//            canvas.save();
//            canvas.translate(centerX, centerY + textSize );//从20，20开始画
//            layout.draw(canvas);
//            canvas.restore();//别忘了restore



        }
        if (isNeedUnit) {
            canvas.drawText(hintString, centerX, centerY + 2 * textSize / 3, hintPaint);
        }
        if (isNeedTitle) {
            canvas.drawText(titleString, centerX, centerY - 2 * textSize / 3, curSpeedPaint);
        }
        Bitmap bmp2 = BitmapFactory.decodeResource(getResources(),
                R.drawable.rockets);
        // Matrix类进行图片处理（缩小或者旋转）
        Matrix matrix1 = new Matrix();
        // 缩小
        matrix1.postScale(0.6f, 0.6f);

        float degrees=180;
        if (currentAngle<=90&&currentAngle>0){
            degrees=270-(90-currentAngle);
//            Log.e("degrees","degrees"+degrees);
        }else if(currentAngle<=180&&currentAngle>90){
            degrees=270+(90-(180-currentAngle));
//            Log.e("90~180",degrees+"LL");
        }else if(currentAngle<=270&&currentAngle>180){
            degrees=90-(270-currentAngle);
        }else if (currentAngle<=360&&currentAngle>270){
            degrees=180-(360-currentAngle);
        }
        matrix1.postRotate(degrees);
        // 生成新的图片
        Bitmap dstbmp1 = Bitmap.createBitmap(bmp2, 0, 0, bmp2.getWidth(),
                bmp2.getHeight(), matrix1, true);
        // 添加到canvas
//        canvas.drawBitmap(dstbmp1, 130, 100, null);
//        canvas.drawBitmap(dstbmp1,((bgRect.right-bgRect.left)/2),bgRect.top,null);
//        canvas.rotate(180);
        canvas.drawBitmap(dstbmp1,bitMapx-(dstbmp1.getWidth()/2),bitMapy-(dstbmp1.getHeight()/2),null);

        invalidate();

    }

    /**
     * 设置最大值
     * @param maxValues
     */
    public void setMaxValues(float maxValues) {
        this.maxValues = maxValues;
        k = sweepAngle/maxValues;
    }

    /**
     * 设置当前值
     * @param currentValues
     */
    public void setCurrentValues(float currentValues) {
        if (currentValues > maxValues) {
            currentValues = maxValues;
        }
        if (currentValues < 0) {
            currentValues = 0;
        }
        this.curValues = currentValues;
        lastAngle = currentAngle;
        setAnimation(lastAngle, currentValues * k, aniSpeed);
    }

    /**
     * 设置整个圆弧宽度
     * @param bgArcWidth
     */
    public void setBgArcWidth(int bgArcWidth) {
        this.bgArcWidth = bgArcWidth;
    }

    /**
     * 设置进度宽度
     * @param progressWidth
     */
    public void setProgressWidth(int progressWidth) {
        this.progressWidth = progressWidth;
    }

    /**
     * 设置速度文字大小
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * 设置单位文字大小
     * @param hintSize
     */
    public void setHintSize(int hintSize) {
        this.hintSize = hintSize;
    }

    /**
     * 设置单位文字
     * @param hintString
     */
    public void setUnit(String hintString) {
        this.hintString = hintString;
        invalidate();
    }

    /**
     * 设置直径大小
     * @param diameter
     */
    public void setDiameter(int diameter) {
        this.diameter = dipToPx(diameter);
    }

    /**
     * 设置标题
     * @param title
     */
    private void setTitle(String title){
        this.titleString = title;
    }

    /**
     * 设置是否显示标题
     * @param isNeedTitle
     */
    private void setIsNeedTitle(boolean isNeedTitle) {
        this.isNeedTitle = isNeedTitle;
    }

    /**
     * 设置是否显示单位文字
     * @param isNeedUnit
     */
    private void setIsNeedUnit(boolean isNeedUnit) {
        this.isNeedUnit = isNeedUnit;
    }

    /**
     * 设置是否显示外部刻度盘
     * @param isNeedDial
     */
    private void setIsNeedDial(boolean isNeedDial) {
        this.isNeedDial = isNeedDial;
    }

    /**
     * 为进度设置动画
     * @param last
     * @param current
     */
    private void setAnimation(float last, float current, int length) {
        progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(length);
        progressAnimator.setTarget(currentAngle);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle= (float) animation.getAnimatedValue();
//                curValues = currentAngle/k;
            }
        });
        progressAnimator.start();
    }

    /**
     * dip 转换成px
     * @param dip
     * @return
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int)(dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 得到屏幕宽度
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
