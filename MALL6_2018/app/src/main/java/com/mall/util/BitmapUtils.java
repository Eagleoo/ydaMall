package com.mall.util;

import java.io.File;
import java.io.IOException;

import com.lidroid.xutils.util.LogUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.widget.ImageView;


public class BitmapUtils {

	
	private static int ERROR_IMAGE = -1;
	private static int LOADING_IMAGE = -1;

	public BitmapUtils() {
		
	}

	public static void setERROR_IMAGE(int eRROR_IMAGE) {
		ERROR_IMAGE = eRROR_IMAGE;
	}

	public static void setLOADING_IMAGE(int lOADING_IMAGE) {
		LOADING_IMAGE = lOADING_IMAGE;
	}

	public static void loadBitmap(String urlOrPath, ImageView view){
		loadBitmap(urlOrPath,view,0,0);
	}

	public static void loadBitmap(String urlOrPath,ImageView view,int width,int height){
		loadBitmap(urlOrPath,view,width,height, null);
	}

	public static void loadBitmap(File file, ImageView view){
		loadBitmap(Uri.fromFile(file),view,0,0);
	}
	
	public static void loadBitmap(File file,ImageView view,int width,int height){
		loadBitmap(Uri.fromFile(file),view,width,height, null);
	}

	public static void loadBitmap(Uri uri, ImageView view){
		loadBitmap(uri,view,0,0);
	}
	
	public static void loadBitmap(Uri uri,ImageView view,int width,int height){
		loadBitmap(uri,view,width,height, null);
	}
	
	public static void loadBitmap(String urlOrPath,ImageView view,int width,int height,com.squareup.picasso.Callback callback){
		if(Util.isNull(urlOrPath)){
			LogUtils.e("loadBitmap url is null "+urlOrPath);
			if(null != callback){
				callback.onError();
			}
			return ;
		}
		RequestCreator rc = Picasso.with(view.getContext()).load(urlOrPath);
		if(-1 != ERROR_IMAGE)
			rc.error(ERROR_IMAGE);
		if(-1 != LOADING_IMAGE)
			rc.placeholder(LOADING_IMAGE);
		if(0 != width && height != 0){
			rc.resize(width, height).centerCrop();
		}
		rc.into(view,callback);  
	}
	
	public static void loadBitmap(Uri uri,ImageView view,int width,int height,com.squareup.picasso.Callback callback){
		RequestCreator rc = Picasso.with(view.getContext()).load(uri);
		if(-1 != ERROR_IMAGE)
			rc.error(ERROR_IMAGE);
		if(-1 != LOADING_IMAGE)
			rc.placeholder(LOADING_IMAGE);
		if(0 != width && height != 0){
			rc.resize(width, height).centerCrop();
		}
		rc.into(view,callback);  
	}

	public static int caculateInSampleSize(Options options, int reqWidth, int reqHeight) {
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {  
            // 计算出实际宽高和目标宽高的比率  
            final int heightRatio = Math.round((float) height / (float) reqHeight);  
            final int widthRatio = Math.round((float) width / (float) reqWidth);  
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高  
            // 一定都会大于等于目标的宽和高。  
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;  
        }  
		return inSampleSize;
	}

	public static Bitmap fileToBitmap(File file, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		options.inJustDecodeBounds = false;
		options.inSampleSize = caculateInSampleSize(options, width, height);
		options.inDither = false;
		options.inPreferredConfig = Config.ARGB_8888;
		bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options); 
		return bitmap;
	}
	
	
    /**
     * 读取图片属性：旋转的角度
     * 
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return degree;
    }

    /**
	 * 放大缩小图片
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		Bitmap newbmp = null;
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidht = ((float) w / width);
			float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidht, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		}
		return newbmp;
	}
    
    /**
     * 图片圆角
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap filletBitmap(Bitmap bitmap, int pixels) {
    	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
    	Canvas canvas = new Canvas(output);
    	final Paint paint = new Paint();
    	final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    	final RectF rectF = new RectF(rect);
    	final float roundPx = pixels;
    	paint.setAntiAlias(true);
    	canvas.drawARGB(0, 0, 0, 0);
    	canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
    	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    	canvas.drawBitmap(bitmap, rect, rect, paint);
    	return output;
	} 
    
    /**
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

}
