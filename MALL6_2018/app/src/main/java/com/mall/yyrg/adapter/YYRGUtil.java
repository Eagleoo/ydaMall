package com.mall.yyrg.adapter;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.mall.model.Product;
import com.mall.yyrg.model.BaskSingle;
import com.mall.yyrg.model.HotShopCar;

public class YYRGUtil {
	public static List<Product> allProducts=new ArrayList<Product>();
	public static BaskSingle baskSingle=new BaskSingle();
	public static List<HotShopCar> hotShopCars=new ArrayList<HotShopCar>();
	 public static Bitmap compressImageFromFile(String srcPath) {
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inJustDecodeBounds = true;//只读边,不读内容
			Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			float hh = 800f;//
			float ww = 480f;//
			int be = 1;
			if (w > h && w > ww) {
				be = (int) (newOpts.outWidth / ww);
			} else if (w < h && h > hh) {
				be = (int) (newOpts.outHeight / hh);
			}
			if (be <= 0)
				be = 1;
			newOpts.inSampleSize = be;//设置采样率
			
			newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设
			newOpts.inPurgeable = true;// 同时设置才会有效
			newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
			
			bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//			return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
										//其实是无效的,大家尽管尝试
			return bitmap;
		}
    /** 
     * 根据原图和变长绘制圆形图片 
     *  
     * @param source 
     * @param min 
     * @return 
     */  
    public static Bitmap createCircleImage(Bitmap source, int min)  
    {  
        final Paint paint = new Paint();  
        paint.setAntiAlias(true);  
        Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);  
        /** 
         * 产生一个同样大小的画布 
         */  
        Canvas canvas = new Canvas(target);  
        /** 
         * 首先绘制圆形 
         */  
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);  
        /** 
         * 使用SRC_IN 
         */  
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  
        /** 
         * 绘制图片 
         */  
        canvas.drawBitmap(source, 0, 0, paint);  
        return target;  
    }  
}
