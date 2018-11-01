package com.mall.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.Base.BasePopWindow;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.mall.view.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/4/23.
 */

public class GetPictureHelp {
    public static final int FROM_CAMERA = 0;
    public static final int FROM_PHOTO = 1;
    private static final int FROM_CUT = 2;

    public static Uri takePhoto(Context context) {
        Uri photoUri;
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
            ContentValues values = new ContentValues();
            photoUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Log.e("1kk", photoUri + "");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            ((Activity) context).startActivityForResult(intent, FROM_CAMERA);
            return photoUri;
        } else {
            Toast.makeText(context, "内存卡不存在", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public static void getPicture(Context context) {
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*"); // 设置文件类型
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(intentFromGallery, FROM_PHOTO);
    }


    public static void PicturePreviewShow(final Context context, final List<String> pictures, final StateResh stateresh, final int state) {
        // state 0 图片预览 1商品预览
        final int[] mPosition = {0};
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_picturepreview, null, false);
        final ViewPager viewPager = view.findViewById(R.id.vp);
        TextView delete = view.findViewById(R.id.delete);
        TextView message = view.findViewById(R.id.message);
        TextView show_image_count = view.findViewById(R.id.show_image_count);
        TextView top_back = view.findViewById(R.id.top_back);
        if (state == 1 || state == 2) {
            delete.setVisibility(View.GONE);
            show_image_count.setText("商品预览");
            message.setVisibility(View.VISIBLE);
            message.setText("");
            if (state == 2) {
                delete.setVisibility(View.VISIBLE);
                delete.setText("编辑");
            }
        }
        final MyPagerAdapter madapter = new MyPagerAdapter(context, pictures);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition[0] = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        viewPager.setAdapter(madapter);
        /**
         * 获取状态栏高度——方法1
         * */
        int statusBarHeight1 = -1;
//获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = context.getResources().getDimensionPixelSize(resourceId);
        }
        Log.e("WangJ", "状态栏-方法1:" + statusBarHeight1);
        final BasePopWindow pw = new BasePopWindow.Builder(context, view).setExecutionPopdisListener(new BasePopWindow.executionPopdis() {
            @Override
            public void onExecution() {
                stateresh.callback();


            }
        }).setWidth(dm.widthPixels, dm.heightPixels - statusBarHeight1).build();
        pw.showBottom();
        top_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pictures.size() > 0) {
                    pictures.remove(mPosition[0]);
                    madapter.notifyDataSetChanged();
                    viewPager.setAdapter(madapter);
                }

            }
        });

    }

    public static String getFilePathByUri(Context context, Uri uri) {
        String path = null;
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
            return path;
        }
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static interface Loadimge {
        void upimge(String s);
    }

    public static interface StateResh {
        void callback();
    }

    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    public static String imageurl(String urlimge) {
        String regEx = "(.*?)(\\:\\w+\\:\\w+)?$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(urlimge);
        String str = "";
        while (m.find()) {
            if (str.equals("")) {
                str = m.group(1);
            }


        }
        if (!str.equals("")) {
            urlimge = str;
        }

        return urlimge;
    }

    public static void uploadImageFromLocalFiles(final List<String> pictures, final Loadimge loadimge) {

        Log.e("上传", "1");
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {

                String result = (String) runData;

                if ((runData + "").contains("success:")) {

                    loadimge.upimge(result);
                }


                Log.e("上传图片", result);

            }

            @SuppressLint("NewApi")
            @Override
            public Serializable run() {
                String result = "";
//				String NAMESCROPE = "http://lin00123.cn/";
                String NAMESCROPE = "http://mynameislin.cn/";
                String METHOD_NAME = "uploadMoodImage";
                String URL = "http://" + "img.yda360.com"
                        + "/ImageServiceUpLoad.asmx";

                String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
                List<Bitmap> imgList = new ArrayList<Bitmap>();
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < pictures.size(); k++) {
                    Bitmap bm = Util.getLocationThmub(pictures.get(k).replace("file://", ""), 480, 640);
                    int bmByteCount = 0;
                    try {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                        bm.compress(compressFormat, 100, out);// 将Bitmap压缩到ByteArrayOutputStream中
                        InputStream sbs = new ByteArrayInputStream(out
                                .toByteArray());
                        byte[] buffer = new byte[30 * 1024];
                        int count = 0;
                        int i = 0;
                        String fileName = "mood_android" + System.currentTimeMillis() + ""
                                + new Random().nextInt(Integer.MAX_VALUE / 2) + ".jpg";
                        bmByteCount = out.size();
                        while ((count = sbs.read(buffer)) >= 0) {
                            String uploadBuffer = new String(Base64.encode(buffer, 0, count, Base64.DEFAULT));
                            SoapObject request = new SoapObject(NAMESCROPE,
                                    METHOD_NAME);
                            request.addProperty("id", "");
                            Date curDate = new Date(System.currentTimeMillis());
                            request.addProperty("userKey", Util.getUSER_KEY(curDate));
                            request.addProperty("userKeyPwd", Util.getUSER_KEYPWD(curDate));
                            request.addProperty("image", uploadBuffer);
                            request.addProperty("fileName", fileName);
                            request.addProperty("tag", i);
                            request.addProperty("length", bmByteCount);
                            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                                    SoapEnvelope.VER11);
                            envelope.bodyOut = request;
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            HttpTransportSE ht = new HttpTransportSE(URL);
                            ht.debug = true;
                            try {
                                ht.call(SOAP_ACTION, envelope);
                                Object obj = envelope.bodyIn;
                                String s = obj.toString();
                                result = s.substring(s.indexOf("success"),
                                        s.indexOf(";"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            i++;
                        }
                        out.close();
                        sbs.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        bm.recycle();
                    }
                    sb.append(result);
                    sb.append("*|-_-|*");
                }
                for (Bitmap bm : imgList) {
                    if (!bm.isRecycled())
                        bm.recycle();
                }
                imgList.clear();
                return sb.toString();
            }
        });
    }

}

class MyPagerAdapter extends PagerAdapter {
    private Context context;
    private List<String> picstr;

    public MyPagerAdapter(Context context, final List<String> picstr) {
        this.context = context;
        this.picstr = picstr;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//                PhotoView photoView = new PhotoView(context);
        PhotoView photoView = new PhotoView(context);
        photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        String str = picstr.get(position);
        Glide.with(context).load(picstr.get(position)).into(photoView);
        ((ViewPager) container).addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {


        ((ViewGroup) container).removeView((View) object);

        object = null;


    }

    @Override
    public int getCount() {
        return picstr.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((PhotoView) object);
    }
}


