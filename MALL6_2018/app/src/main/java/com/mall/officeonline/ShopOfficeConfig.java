package com.mall.officeonline;

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

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.ShopOfficeInfo;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.SelectPicActivity;

public class ShopOfficeConfig extends Activity {
	@ViewInject(R.id.title)
	private TextView title;
	@ViewInject(R.id.vedio_img)
	private ImageView vedio_img;
	private ImageView addImage;
	@ViewInject(R.id.main_image_url)
	private EditText main_image_url;
	private BitmapUtils bmUtils;
	@ViewInject(R.id.content)
	private EditText content;
	@ViewInject(R.id.main_image_tip)
	private TextView main_image_tip;
	@ViewInject(R.id.logo_img)
	private ImageView logo_img;
	private ImageView add_logo_img;
	private String offLog = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_office_config);
		ViewUtils.inject(this);
		bmUtils = new BitmapUtils(this);
		init();
	}

	@OnClick(R.id.submit)
	public void Submit(final View v) {
		String offName = title.getText().toString();
		String remark = content.getText().toString();
		String offBanner = main_image_url.getText().toString();
		SetOfficeInfo(offName, offLog, "", remark, offBanner);
	}

	private void init() {
		Util.initTop(this, "设置空间", Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShopOfficeConfig.this.finish();
			}
		});
		main_image_tip.setText(Util.spanRedWithString("空间主图", "【文件大小300K以内，建议尺寸1000PX * 260PX的图。】"));
		addImage = (ImageView) this.findViewById(R.id.add_vedio_image);
		addImage.setOnClickListener(new ImageSelectOnClick(801));
		add_logo_img = (ImageView) this.findViewById(R.id.add_logo_image);
		add_logo_img.setOnClickListener(new ImageSelectOnClick(802));
		if (UserData.getOfficeInfo() != null) {
			ShopOfficeInfo info = UserData.getOfficeInfo();
			if (!Util.isNull(info.getOfficename())) {
				title.setText(info.getOfficename());
			}
			if (!Util.isNull(info.getImgLogo1())) {
				String url = "";
				if (info.getImgLogo1().contains("http")) {
					url = info.getImgLogo1();
				} else {
					url = "http://" + Web.webImage + info.getImgLogo1();
				}
				main_image_url.setText(url);
				addImage.setVisibility(View.GONE);
				vedio_img.setOnClickListener(new ImageSelectOnClick(801));
				bmUtils.display(vedio_img, url, new BitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						vedio_img.setImageBitmap(arg2);
					}

					@Override
					public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
						vedio_img.setImageResource(R.drawable.no_get_banner);
					}
				});
			}
			if (!Util.isNull(info.getContent())) {
				content.setText(Util.Html2Text(info.getContent()));
			}
			if (!Util.isNull(info.getImgLogo())) {
				String logourl = "";
				if (info.getImgLogo1().contains("http")) {
					logourl = info.getImgLogo();
				} else {
					logourl = "http://" + Web.webImage + info.getImgLogo();
				}
				add_logo_img.setVisibility(View.GONE);
				logo_img.setOnClickListener(new ImageSelectOnClick(802));
				bmUtils.display(logo_img, logourl, new BitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						logo_img.setImageBitmap(arg2);
					}

					@Override
					public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
						logo_img.setImageResource(R.drawable.no_get_image);
					}
				});
			}
		}
	}

	private void SetOfficeInfo(final String offName, final String offLog, final String singer, final String remark,
			final String offBanner) {
		if (UserData.getUser() == null) {
			Util.showIntent(this, LoginFrame.class);
		}
		if (UserData.getOfficeInfo().getUserid().equals(UserData.getUser().getUserIdNoEncodByUTF8())) {
			Util.asynTask(this, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if (runData != null) {
						if ("ok".equals(runData + "")) {
							ShopOfficeConfig.this.finish();
						}
					} else {

					}
				}

				@Override
				public Serializable run() {
					Web web = new Web(Web.officeUrl, Web.SetOfficeInfo,
							"offName=" + offName + "&offLog=" + offLog + "&singer=" + singer + "&remark=" + remark
									+ "&offBanner=" + offBanner + "&userID=" + UserData.getUser().getUserId()
									+ "&userPaw=" + UserData.getUser().getMd5Pwd());
					return web.getPlan();
				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && (requestCode == 800 || requestCode == 801 || requestCode == 802
				|| requestCode == 803 || requestCode == 804 || requestCode == 805 || requestCode == 806
				|| requestCode == 807 || requestCode == 808 || requestCode == 809)) {
			String picPath = "";
			String pictype = "";
			if (!Util.isNull(data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH))) {
				picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
				pictype = picPath.substring(picPath.lastIndexOf(".") + 1, picPath.length());
			}
			final int _80dp = Util.dpToPx(this, 80F);
			Bitmap tagBm = null;
			int networkType = Util.getNetworkType(this);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(picPath, opts);
			if (1 == networkType) {
				if (opts.outWidth > 480 && opts.outHeight > 800)
					tagBm = Util.getLocationThmub(picPath, 480, 800);
				else
					tagBm = BitmapFactory.decodeFile(picPath);
			} else if (2 == networkType) {
				if (opts.outWidth > 240 && opts.outHeight > 400)
					tagBm = Util.getLocationThmub(picPath, 240, 400);
				else
					tagBm = BitmapFactory.decodeFile(picPath);
			} else if (3 == networkType) {
				if (opts.outWidth > 320 && opts.outHeight > 480)
					tagBm = Util.getLocationThmub(picPath, 320, 480);
				else
					tagBm = BitmapFactory.decodeFile(picPath);
			} else {
				Util.show("请检查您的网络!", this);
				return;
			}
			Bitmap bm = Util.zoomBitmap(tagBm, _80dp, _80dp);
			switch (requestCode) {
			case 801:
				vedio_img.setImageBitmap(bm);
				vedio_img.setTag(tagBm);
				vedio_img.setTag(-7, pictype);
				addImage.setVisibility(View.GONE);
				vedio_img.setOnClickListener(new ImageSelectOnClick(801));
				upload("banner");
				break;
			case 802:
				logo_img.setImageBitmap(bm);
				logo_img.setTag(tagBm);
				logo_img.setTag(-7, pictype);
				add_logo_img.setVisibility(View.GONE);
				logo_img.setOnClickListener(new ImageSelectOnClick(802));
				upload("logo");
				break;
			default:
				break;
			}
		}
	}

	@SuppressLint("NewApi")
	private void upload(final String id) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.setMessage(dialog, "图片上传中...");
		dialog.setCancelable(false);
		dialog.show();
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				String result = (String) runData;
				if (result.contains("success:")) {
					String[] s = result.split(":");
					if (s.length == 2 && s[1] != null) {
						if (id.equals("banner")) {
							main_image_url.setText("http://" + Web.webImage + s[1]);
						} else {
							offLog = "http://" + Web.webImage + s[1];
						}
					}
				}
				dialog.dismiss();
			}

			@Override
			public Serializable run() {
				String result = "";
				String NAMESCROPE = "http://mynameislin.cn/";
				String METHOD_NAME = "UploadOfficeUserPhoto";
				String URL = "http://" + Web.webImage + "/api_test/MyOffice.asmx";
				String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
				List<Bitmap> imgList = new ArrayList<Bitmap>();
				List<String> picTypes = new ArrayList<String>();
				int counts = 0;
				if (null != vedio_img.getTag()) {
					imgList.add((Bitmap) vedio_img.getTag());
					picTypes.add(vedio_img.getTag(-7).toString());
				}
				if (null != logo_img.getTag()) {
					imgList.add((Bitmap) logo_img.getTag());
					picTypes.add(logo_img.getTag(-7).toString());
				}
				StringBuilder sb = new StringBuilder();
				for (int k = 0; k < imgList.size(); k++) {
					Bitmap bm = imgList.get(k);
					if (bm.isRecycled()) {
						continue;
					}
					int bmByteCount = 0;
					try {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
						bm.compress(compressFormat, 80, out);// 将Bitmap压缩到ByteArrayOutputStream中
						InputStream sbs = new ByteArrayInputStream(out.toByteArray());
						byte[] buffer = new byte[30 * 1024];
						int count = 0;
						int i = 0;
						if (out.size() % (30 * 1024) == 0) {
							counts = out.size() / (30 * 1024);
						} else {
							counts = out.size() / (30 * 1024) + 1;
						} // 计算每张图片上传的次数
						String fileName = System.currentTimeMillis() + "" + new Random().nextDouble() + "."
								+ picTypes.get(k);
						bmByteCount = out.size();
						while ((count = sbs.read(buffer)) >= 0) {
							String uploadBuffer = new String(Base64.encode(buffer, 0, count, Base64.DEFAULT));
							SoapObject request = new SoapObject(NAMESCROPE, METHOD_NAME);
							Date curDate = new Date(System.currentTimeMillis());
							request.addProperty("userKey", Util.getUSER_KEY(curDate));
							request.addProperty("userKeyPwd", Util.getUSER_KEYPWD(curDate));
							request.addProperty("image", uploadBuffer);
							request.addProperty("fileName", fileName);
							request.addProperty("userID", UserData.getUser().getUserId());
							request.addProperty("userPaw", UserData.getUser().getMd5Pwd());
							request.addProperty("end", counts);
							request.addProperty("tag", i);
							SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
							envelope.bodyOut = request;
							envelope.dotNet = true;
							envelope.setOutputSoapObject(request);
							HttpTransportSE ht = new HttpTransportSE(URL);
							ht.debug = true;
							try {
								ht.call(SOAP_ACTION, envelope);
								Object obj = envelope.bodyIn;
								String s = obj.toString();
								System.out.println("obj.toString()================" + obj.toString());
								result = s.substring(s.indexOf("success"), s.indexOf(";"));
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

	public class ImageSelectOnClick implements OnClickListener {
		private int code = 0;

		public ImageSelectOnClick(int code) {
			this.code = code;
		}

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(ShopOfficeConfig.this, SelectPicActivity.class);
			startActivityForResult(intent, code);
		}
	}
}
