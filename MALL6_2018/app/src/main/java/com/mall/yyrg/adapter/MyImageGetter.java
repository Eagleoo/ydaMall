package com.mall.yyrg.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html.ImageGetter;
import android.widget.TextView;

import com.mall.util.MD5;
import com.mall.view.R;

public class MyImageGetter implements ImageGetter {

	private Context context;
	private TextView tv;
	private int width;
	private int height;

	public MyImageGetter(Context context, TextView tv, int width, int height) {
		this.context = context;
		this.width = width;
		this.height = height;
		this.tv = tv;
	}
	@Override
	public Drawable getDrawable(String source) {

		// TODO Auto-generated method stub
		// 将source进行MD5加密并保存至本地
		MD5 md5 = new MD5();
		String imageName = md5.getMD5ofStr(source);
		String sdcardPath = Environment.getExternalStorageDirectory()
				.toString(); // 获取SDCARD的路径
		// 获取图片后缀名
		String[] ss = source.split("\\.");
		String ext = ss[ss.length - 1];

		// 最终图片保持的地址
		String savePath = sdcardPath + "/" + context.getPackageName() + "/"
				+ imageName + "." + ext;

		// 不存在文件时返回默认图片，并异步加载网络图片
		Resources res = context.getResources();
		URLDrawable drawable = new URLDrawable(
				res.getDrawable(R.drawable.new_yda__top_zanwu));
		new ImageAsync(drawable).execute(savePath, source);
		return drawable;

	}

	private class ImageAsync extends AsyncTask<String, Integer, Drawable> {

		private URLDrawable drawable;

		public ImageAsync(URLDrawable drawable) {
			this.drawable = drawable;
		}

		@Override
		protected Drawable doInBackground(String... params) {
			// TODO Auto-generated method stub
			String savePath = params[0];
			String url = params[1];
			InputStream in = null;
			try {
				// 获取网络图片
				HttpGet http = new HttpGet(url);
				DefaultHttpClient client = new DefaultHttpClient();
				HttpResponse response = (HttpResponse) client.execute(http);
				BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(
						response.getEntity());
				in = bufferedHttpEntity.getContent();

			} catch (Exception e) {
				try {
					if (in != null)
						in.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}

			if (in == null)
				return drawable;

			try {
				File file = new File(savePath);
				String basePath = file.getParent();
				File basePathFile = new File(basePath);
				if (!basePathFile.exists()) {
					basePathFile.mkdirs();
				}
				file.createNewFile();
				FileOutputStream fileout = new FileOutputStream(file);
				byte[] buffer = new byte[2 * 1024];
				while (in.read(buffer) != -1) {
					fileout.write(buffer);
				}
				fileout.flush();

				Drawable mDrawable = Drawable.createFromPath(savePath);
				mDrawable=zoomDrawable(mDrawable, width, height);
				return mDrawable;
			} catch (Exception e) {
			}
			return drawable;
		}

		@Override
		protected void onPostExecute(Drawable result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				drawable.setDrawable(result);
				int temp=0;
				System.out.println("图片的张数：--"+(temp++));
	//			tv.setText(tv.getText()); // 通过这里的重新设置 TextView 的文字来更新UI
			}
		}
	}

	public class URLDrawable extends BitmapDrawable {

		private Drawable drawable;

		public URLDrawable(Drawable defaultDraw) {
			setDrawable(defaultDraw);
		}
		

		private void setDrawable(Drawable nDrawable) {
			drawable = nDrawable;
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
		}

		@Override
		public void draw(Canvas canvas) {
			// TODO Auto-generated method stub
			drawable.draw(canvas);
		}

	}

	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable); // drawable 转换成 bitmap
		Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
		float scaleWidth = ((float) w*1.5f / width); // 计算缩放比例
		float scaleHeight = ((float) w *1.5f/ width);
		matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true); // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
		return new BitmapDrawable(newbmp); // 把 bitmap 转换成 drawable 并返回
	}

	public static Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成
																// bitmap
	{
		int width = drawable.getIntrinsicWidth(); // 取 drawable 的长宽
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565; // 取 drawable 的颜色格式
		Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应
																	// bitmap
		Canvas canvas = new Canvas(bitmap); // 建立对应 bitmap 的画布
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas); // 把 drawable 内容画到画布中
		return bitmap;
	}
}
