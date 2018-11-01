package com.mall.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.BaseMallAdapter;
import com.mall.model.MallServiceItem;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

/**
 * 
 * 功能： 远大卷页面<br>
 * 时间： 2014-1-27<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class MallServiceFrame extends Activity {
	@ViewInject(R.id.mall_service_list)
	private ListView listView;
	private String tid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mall_service_frame);
		ViewUtils.inject(this);
		Util.initTop(this, "订单信息", Integer.MIN_VALUE, null);
		tid = this.getIntent().getStringExtra("tid");
		Util.asynTask(this, "正在获取您的订单信息...", new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				HashMap<String, List<MallServiceItem>> map = (HashMap<String, List<MallServiceItem>>) runData;
				List<MallServiceItem> list = map.get("list");
				listView.setAdapter(new MallServiceAdapter(
						MallServiceFrame.this, list));
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getMallServiceByTid, "userId="
						+ UserData.getUser().getUserId() + "&md5Pwd="
						+ UserData.getUser().getMd5Pwd() + "&tid=" + tid);
				HashMap<String, List<MallServiceItem>> map = new HashMap<String, List<MallServiceItem>>();
				map.put("list", web.getList(MallServiceItem.class));
				return map;
			}
		});
	}
}

class MallServiceAdapter extends BaseMallAdapter<MallServiceItem> {

	private class MallServiceHolder {
		@ViewInject(R.id.mall_service_juan)
		public ImageView ran;
		@ViewInject(R.id.mall_service_proName)
		public TextView pName;
		@ViewInject(R.id.mall_service_shopName)
		public TextView uName;
		@ViewInject(R.id.mall_service_shopTime)
		public TextView shopTime;
		@ViewInject(R.id.mall_service_useTime)
		public TextView useTime;
		@ViewInject(R.id.mall_service_useUser)
		public TextView useUser;
		@ViewInject(R.id.mall_service_ran)
		public TextView ranCode;
		@ViewInject(R.id.mall_service_status)
		public TextView status;
	}


	public MallServiceAdapter(Context context, List<MallServiceItem> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent,
			MallServiceItem model) {
		MallServiceHolder holder = null;
		if (null == convertView) {
			holder = new MallServiceHolder();
			convertView = flater.inflate(R.layout.mall_service_item, null);
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else
			holder = (MallServiceHolder) convertView.getTag();
		if ("1".equals(model.getStatus())) {
			holder.useTime.setText(model.getModifyTime());
			holder.useUser.setText(model.getModifyUser());
			holder.useTime.setVisibility(View.VISIBLE);
			holder.useUser.setVisibility(View.VISIBLE);
			holder.status
					.setText(Html
							.fromHtml("<html><body><font color='#535353'>卷码状态：</font><font color='#cf0000'>已使用</font></body></html>"));
		} else {
			holder.useTime.setVisibility(View.GONE);
			holder.useUser.setVisibility(View.GONE);
			holder.status
					.setText(Html
							.fromHtml("<html><body><font color='#535353'>卷码状态：</font><font color='green'>未使用</font></body></html>"));
		}
		holder.pName.setText("商品名称：" + model.getPname());
		holder.shopTime.setText("购买时间：" + model.getCreateTime());
		holder.uName.setText("来自【" + model.getShopName() + "】的远大消费卷");
		holder.ranCode.setText("远大卷码：" + model.getRanCode());

		final AnimationDrawable ranAnim = (AnimationDrawable) holder.ran
				.getBackground();
		ranAnim.start();
		Bitmap bm = createQRCode(model.getRanCode());
		holder.ran.setBackgroundDrawable(null);
		holder.ran.setImageBitmap(bm);
		ranAnim.stop();

		return convertView;
	}

	private Bitmap createQRCode(String code) {
		File f = new File(Util.qrPath+ code + ".jpg");
		if (f.exists()) {
			Options opts = new Options();
			opts.inSampleSize = 1;
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			return Util.getLocalBitmap(Util.qrPath + code + ".jpg", opts);
		}
		try {
			// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
			BitMatrix matrix;
			matrix = new MultiFormatWriter().encode(code,
					BarcodeFormat.QR_CODE, 238, 238);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			// 二维矩阵转为一维像素数组,也就是一直横着排了
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			// 通过像素数组生成bitmap,具体参考api
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			FileOutputStream out = null;
			try {
				f.createNewFile();
				out = new FileOutputStream(f);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != out) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
}