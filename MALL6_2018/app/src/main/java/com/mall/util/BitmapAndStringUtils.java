package com.mall.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.yalantis.ucrop.util.BitmapLoadUtils.calculateInSampleSize;

/**
 * Created by Administrator on 2017/12/1.
 */

public class BitmapAndStringUtils {

    public static Bitmap readBitMap(String path){
        InputStream  stream = null;
        
        try {
              stream = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        return BitmapFactory.decodeStream(stream,null,opt);
    }
    public static Bitmap createImageThumbnail(String filePath) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new
                BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);
        opts.inSampleSize =
                computeSampleSize(opts, -1, 128 * 128) / 2;
        System.out.println("option参数" + opts.inSampleSize);
        opts.inJustDecodeBounds = false;
        try {
            bitmap =
                    BitmapFactory.decodeFile(filePath, opts);
        } catch (Exception e) {
            //TODO: handle exception
        }
        return bitmap;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
                      int maxNumOfPixels) {
        int initialSize =
                computeInitialSampleSize(options, minSideLength,
                        maxNumOfPixels);
        int roundedSize;
        if (initialSize <=
                4) {
            roundedSize = 1;
            while (roundedSize <
                    initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 3) / 4 * 4;
        }
        return
                roundedSize;
    }

    private static int
    computeInitialSampleSize(BitmapFactory.Options options, int
            minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels ==
                -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int
                upperBound = (minSideLength == -1) ? 128 : (int)
                Math.min(Math.floor(w / minSideLength), Math.floor(h /
                        minSideLength));
        if (upperBound < lowerBound) { //return the larger one when there is no overlapping zone.
             return lowerBound;
        }
        if ((maxNumOfPixels == -1)
                && (minSideLength == -1)) {
            return
                    1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }



    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        //避免出现内存溢出的情况，进行相应的属性设置。
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
}
