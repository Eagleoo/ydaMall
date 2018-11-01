package com.mall.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lin.component.CustomProgressDialog;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;

public class LMSJShowImageFrame extends Activity implements
        OnItemSelectedListener {

    private ImageView is;
    private Gallery gallery;
    private List<String> list = null;
    private ImageView[] mImages = null;
    private Bitmap bmp = null;
    private BitmapUtils bmUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_frame);

        bmUtils = new BitmapUtils(this);
        String name = this.getIntent().getStringExtra("name");
        Util.initTitle(this, name, new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        is = (ImageView) findViewById(R.id.show_img_img);
        is.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int next = gallery.getSelectedItemPosition();
                if ((next + 1) >= gallery.getAdapter().getCount())
                    next = 0;
                else
                    next++;
                gallery.setSelection(next, true);
            }
        });

        gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setOnItemSelectedListener(this);
        String[] imgs = this.getIntent().getStringArrayExtra("imgs");
        list = new ArrayList<String>();
        for (String img : imgs)
            list.add(img);
        mImages = new ImageView[list.size()];
        Util.asynTask(this, "正在获取商家图片", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                gallery.setAdapter(new ImageAdapter());
            }

            @Override
            public Serializable run() {
                createReflectedImages();
                return null;
            }
        });
    }

    public class ImageAdapter extends BaseAdapter {

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = mImages[position];
            i.setAdjustViewBounds(true);
            i.setLayoutParams(new Gallery.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            return i;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               final int position, long id) {

        synchronized (LMSJShowImageFrame.this) {
            final CustomProgressDialog dialog = Util.showProgress("正在获取中...", this);
            bmUtils.display(is, Web.imgServer2 + list.get(position), new DefaultBitmapLoadCallBack<View>() {
                @Override
                public void onLoadCompleted(View container, String uri,
                                            Bitmap bitmap, BitmapDisplayConfig config,
                                            BitmapLoadFrom from) {
                    super.onLoadCompleted(container, uri, bitmap, config, from);
                    dialog.cancel();
                    dialog.dismiss();
                }

                @Override
                public void onLoadFailed(View container, String uri,
                                         Drawable drawable) {
                    is.setImageResource(R.drawable.zw174);
                    dialog.cancel();
                    dialog.dismiss();
                }
            });

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * 创建倒影效果
     *
     * @return
     */
    public boolean createReflectedImages() {
        // 倒影图和原图之间的距离
        final int reflectionGap = 4;
        int index = 0;
        for (String pi : list) {
            // 返回原图解码之后的bitmap对象
            String href = Web.imgServer2
                    + pi;
            BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inSampleSize = 4;// 图片宽高都为原来的二分之一，即图片为原来的四分之一
            // 返回原图解码之后的bitmap对象
            Bitmap originalImage = Util.getBitmap(href, options);
            if (null == originalImage)
                originalImage = BitmapFactory.decodeResource(LMSJShowImageFrame.this.getResources(), R.drawable.zw174);
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            // 创建矩阵对象
            Matrix matrix = new Matrix();

            // 指定一个角度以0,0为坐标进行旋转
            // matrix.setRotate(30);

            // 指定矩阵(x轴不变，y轴相反)
            matrix.preScale(1, -1);

            // 将矩阵应用到该原图之中，返回一个宽度不变，高度为原图1/2的倒影位图
            Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                    height / 2, width, height / 2, matrix, false);

            // 创建一个宽度不变，高度为原图+倒影图高度的位图
            Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                    (height + height / 2), Config.ARGB_8888);

            // 将上面创建的位图初始化到画布
            Canvas canvas = new Canvas(bitmapWithReflection);
            canvas.drawBitmap(originalImage, 0, 0, null);

            Paint deafaultPaint = new Paint();
            deafaultPaint.setAntiAlias(false);
            // canvas.drawRect(0, height, width, height +
            // reflectionGap,deafaultPaint);
            canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
            Paint paint = new Paint();
            paint.setAntiAlias(false);

            /**
             * 参数一:为渐变起初点坐标x位置， 参数二:为y轴位置， 参数三和四:分辨对应渐变终点， 最后参数为平铺方式，
             * 这里设置为镜像Gradient是基于Shader类，所以我们通过Paint的setShader方法来设置这个渐变
             */
            LinearGradient shader = new LinearGradient(0,
                    originalImage.getHeight(), 0,
                    bitmapWithReflection.getHeight() + reflectionGap,
                    0x70ffffff, 0x00ffffff, TileMode.MIRROR);
            // 设置阴影
            paint.setShader(shader);
            paint.setXfermode(new PorterDuffXfermode(
                    android.graphics.PorterDuff.Mode.DST_IN));
            // 用已经定义好的画笔构建一个矩形阴影渐变效果
            canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                    + reflectionGap, paint);

            // 创建一个ImageView用来显示已经画好的bitmapWithReflection
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmapWithReflection);
            // 设置imageView大小 ，也就是最终显示的图片大小
            imageView.setLayoutParams(new Gallery.LayoutParams(300, 400));
            // imageView.setScaleType(ScaleType.MATRIX);
            mImages[index++] = imageView;
        }
        return true;
    }

}
