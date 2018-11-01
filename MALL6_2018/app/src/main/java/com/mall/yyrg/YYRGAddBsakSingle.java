package com.mall.yyrg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.messageboard.GestureDetector;
import com.mall.view.messageboard.ImageViewTouch;
import com.mall.view.messageboard.PagerAdapter;
import com.mall.view.messageboard.ScaleGestureDetector;
import com.mall.view.messageboard.ViewPager;
import com.mall.yyrg.adapter.BitmapUtil;
import com.mall.yyrg.adapter.MyGridView;
import com.mall.yyrg.adapter.SelcctPhotoAdapter;
import com.mall.yyrg.adapter.YYRGUtil;
import com.mall.yyrg.model.PhotoInfo;
/**
 * 晒单的功能
 * @author Administrator
 *
 */
public class YYRGAddBsakSingle extends Activity {
	private MyGridView add_bask_image;
	private SelcctPhotoAdapter photoAdapter;
	private List<PhotoInfo> allPhotoInfos = new ArrayList<PhotoInfo>();
	private int width;
	private List<Bitmap> addBitmaps = new ArrayList<Bitmap>();
	private int temp = 0;
	private PopupWindow distancePopup = null;
	private String picPath;
	private Uri photoUri;
	private TextView show_image_count;
	private Dialog dialog;
	private Map<String, String> goodsMessage = new HashMap<String, String>();
	// 选择是拍照还是本地上传,1是拍照，2是本地上传
	private int uploadState;
	private List<ImageView> arrImages;
	private String nowView = "2";
	@ViewInject(R.id.top_view)
	private LinearLayout top_view;
	private static final int PAGER_MARGIN_DP = 40;
	private ViewPager mViewPager;
	private int pagerMarginPixels = 0;
	private ImagePagerAdapter mPagerAdapter;
	private GestureDetector mGestureDetector;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private int currentPic = 0;
	private boolean mPaused;
	private boolean mOnScale = false;
	private boolean mOnPagerScoll = false;
	private boolean mControlsShow = false;
	private ScaleGestureDetector mScaleGestureDetector;
	public Map<Integer, ImageViewTouch> viewss = new HashMap<Integer, ImageViewTouch>();
	private BitmapUtils bmUtil;
	private Bitmap bitmap;
	private int imageCount = 0;
	private int imageTag = 0;
	private Handler handler;
	private int count = 0;
	private String imagepath="";
	private String yppid;
	private String ypid;
	private String hidperiodName;
	@ViewInject(R.id.bask_title)
	private EditText bask_title;
	@ViewInject(R.id.bask_context)
	private EditText bask_context;
	@ViewInject(R.id.fabiao_shaidan)
	private Button fabiao_shaidan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_bask_single);
		ViewUtils.inject(this);
		yppid = getIntent().getStringExtra("yppid");
		ypid = getIntent().getStringExtra("ypid");
		hidperiodName = getIntent().getStringExtra("hidperiodName");
		if (TextUtils.isEmpty(ypid) || TextUtils.isEmpty(yppid)
				|| TextUtils.isEmpty(hidperiodName)) {
			Toast.makeText(this, "网络不给力，请检查网络是否连接", Toast.LENGTH_SHORT).show();
			fabiao_shaidan.setVisibility(View.INVISIBLE);
		} else {
			fabiao_shaidan.setVisibility(View.VISIBLE);
		}
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		add_bask_image = (MyGridView) findViewById(R.id.add_bask_image);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		Bitmap bitmap = getBitmapFromResources(this, R.drawable.issue_tjtx);
		PhotoInfo photoInfo = new PhotoInfo();
		photoInfo.setBitmap(bitmap);
		allPhotoInfos.add(photoInfo);
		if (photoAdapter == null) {
			photoAdapter = new SelcctPhotoAdapter(this, allPhotoInfos,
					add_bask_image, width * 2 / 9);
		}
		add_bask_image.setAdapter(photoAdapter);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					List<Object> list = (List<Object>) msg.obj;
					if (list != null) {
						if (list.size() > 0) {
							upload("goodsimage", (Bitmap) list.get(0),
									(Integer) list.get(1));
						}
					}
					break;
				case 2:
					count++;
					if (count == allPhotoInfos.size() - 1) {
						addHotBuyRecord();
					} else {
					}

					break;
				}
			}
		};
	}

	@OnClick({ R.id.top_back, R.id.add_bask_image, R.id.fabiao_shaidan })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.fabiao_shaidan:
			if (TextUtils.isEmpty(bask_title.getText().toString().trim())) {
				Toast.makeText(this, "请输入晒单标题", Toast.LENGTH_SHORT).show();
				return;
			}
			if (TextUtils.isEmpty(bask_context.getText().toString().trim())) {
				Toast.makeText(this, "请输入晒单内容", Toast.LENGTH_SHORT).show();;
				return;
			}
			if ((allPhotoInfos == null || allPhotoInfos.size() < 2)) {
				Toast.makeText(this, "请选择晒单图片", Toast.LENGTH_SHORT).show();
			}  else {
				if (allPhotoInfos.size() > 6) {
					Toast.makeText(this, "上传的图片不能多于5张，请删除多余的图片",
							Toast.LENGTH_SHORT).show();
					return;
				}
				for (int i = 1; i < allPhotoInfos.size(); i++) {
					Bitmap bitmap = null;
					if (allPhotoInfos.get(i).getBitmap() != null) {
						bitmap = allPhotoInfos.get(i).getBitmap();
						List<Object> list = new ArrayList<Object>();
						list.add(bitmap);
						list.add(i);
						Message msg = new Message();
						msg.what = 1;
						msg.obj = list;
						handler.sendMessage(msg);
					} else {
						ImageView logo = new ImageView(this);
						setImage(logo, allPhotoInfos.get(i).getPath_file(), i);
					}
				}

			}

			break;
		case R.id.top_back:
			finish();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1000 && resultCode == 1001) {
			if (uploadState == 2) {
				allPhotoInfos.addAll(BitmapUtil.getPhotoInfos);
				photoAdapter = new SelcctPhotoAdapter(this, allPhotoInfos,
						add_bask_image, width * 2 / 9);
				add_bask_image.setAdapter(photoAdapter);
			}

		} else if (resultCode == Activity.RESULT_OK) {
			doPhoto(requestCode, data);
		}
	}

	public static Bitmap getBitmapFromResources(Activity act, int resId) {
		Resources res = act.getResources();
		return BitmapFactory.decodeResource(res, resId);
	}

	@OnItemClick(R.id.add_bask_image)
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 0) {
			if (allPhotoInfos.size() < 6) {
				BitmapUtil.imageCount=allPhotoInfos.size()-1;
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(),

						InputMethodManager.HIDE_NOT_ALWAYS);
				getPopupWindow();
				startPopupWindow();
				distancePopup.showAsDropDown(view);
			} else {
				Toast.makeText(this, "晒单最多上传5张图片", Toast.LENGTH_SHORT).show();
			}
		} else {
			getPopupWindow();
			startPopupWindows(position);
			distancePopup.showAsDropDown(top_view);
		}

	}

	/**
	 * 上传晒单的图片信息
	 * 
	 * @param id
	 */
	private void upload(final String id, final Bitmap bitmap, final int temp) {

		final ProgressDialog pd = ProgressDialog
				.show(this, null, "正在上传图片...");
		pd.setCancelable(true);
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				pd.cancel();
				pd.dismiss();
				if (runData!=null) {
					if (runData.toString().length() > 0) {
						if (runData.toString().length() > 0) {
							Log.v("jieguo", runData.toString());
								imagepath=imagepath+"Y"+runData.toString().split(":")[0];
							if ((runData.toString().split(":")[1]
											.equals(runData.toString().split(":")[2]))) {
								Message msg=new Message();
								msg.what=2;
								handler.sendMessage(msg);
								System.out.println(imagepath);
							}
							return;
						}
					}	
				}else {
					Util.show("上传失败！", YYRGAddBsakSingle.this);
				}
				
				
			}

			@SuppressLint("NewApi")
			@Override
			public Serializable run() {
				String NAMESCROPE = "http://lin00123.cn/";
				String METHOD_NAME = "upOrderImage";
				String URL = Web.imgServer2+"ImageServiceUpLoad.asmx";
				String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
				String userPhoto = null;
				List<String> list = new ArrayList<String>();
				int resultVlaue = 0;
				int counts = 0;
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
					bitmap.compress(compressFormat, 85, out);
					InputStream sbs = new ByteArrayInputStream(out
							.toByteArray());
					byte[] buffer = new byte[30 * 1024];
					int count = 0;
					if (out.size() % (30 * 1024) == 0) {
						counts = out.size() / (30 * 1024);
					} else {
						counts = out.size() / (30 * 1024) + 1;
					}
					
					imageCount = counts;
					// System.out.println(resultVlaue+"   ---------------当前图片的长度");
					int i = 1;
					String fileName = "a_"+System.currentTimeMillis() + ".jpg";
					while ((count = sbs.read(buffer)) >= 0) {
						String uploadBuffer = new String(Base64.encode(buffer,
								0, count, Base64.DEFAULT));
						SoapObject request = new SoapObject(NAMESCROPE,
								METHOD_NAME);
						request.addProperty("userKey", "a");
						request.addProperty("userKeyPwd", "b");
						request.addProperty("image", uploadBuffer);
						request.addProperty("fileName", fileName);
						request.addProperty("tag", i);
						request.addProperty("end", counts);
						imageTag = i;
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
							resultVlaue++;
							Log.v("------", obj + "");
							Log.v("----tag----", i + "");
							Log.v("----end----", counts + "");

							String[] userPhotos = obj.toString().split(
									"success:");
							if (userPhotos.length == 2) {
								userPhotos = userPhotos[1].split(";");
								if (userPhotos.length == 2) {
									userPhoto = userPhotos[0];
									userPhoto = userPhoto + ":" + i + ":"
											+ counts;
								}
							}

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
				}
				return userPhoto;
			}
		});
	}

	/**
	 * 上传晒单信息
	 */
	private void addHotBuyRecord() {
		Util.asynTask(this, "正在上传...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (null == runData) {
					Util.show("网络错误，请重试！", YYRGAddBsakSingle.this);
					return;
				}
				String string = (String) runData;
				if (string.equals("success")) {// 上传成功
					Toast.makeText(YYRGAddBsakSingle.this, "晒单成功", Toast.LENGTH_SHORT).show();
					finish(); 
					Util.showIntent(YYRGAddBsakSingle.this, YYRGBaskSingle.class);
				} else {// 添加出错，打印出错信息
					Toast.makeText(YYRGAddBsakSingle.this, string,
							Toast.LENGTH_SHORT).show();
				}

			}

			@Override
			public Serializable run() {
				Web web = null;
				try {
					web = new Web(Web.yyrgAddress, Web.addHotBuyRecord, "userID="
							+ UserData.getUser().getUserId()
							+ "&userPaw="
							+ UserData.getUser().getMd5Pwd()
							+ "&imgPaths="
							+ imagepath
							+ ","
							+ "&spoid_=-1"
							+ "&ypid="
							+ ypid
							+ "&yppid="
							+ yppid
							+ "&txtTitle="
							+ bask_title.getText().toString().trim()
							+ "&txtContent="
							+ bask_context.getText().toString().trim()
							+ "&hidperiodName=" + hidperiodName);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String string = web.getPlan();
				return string;
			}

		});
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow() {

		View pview = getLayoutInflater().inflate(R.layout.select_pic_layout,
				null, false);
		Button btn_take_photo = (Button) pview
				.findViewById(R.id.btn_take_photo);
		Button btn_pick_photo = (Button) pview
				.findViewById(R.id.btn_pick_photo);
		Button btn_cancel = (Button) pview.findViewById(R.id.btn_cancel);
		btn_take_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				uploadState = 1;
				takePhoto();
				distancePopup.dismiss();
			}
		});
		btn_pick_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				uploadState = 2;
				Intent intent = new Intent(YYRGAddBsakSingle.this,
						YYRGSelectBaskImage.class);
				startActivityForResult(intent, 1000);
				distancePopup.dismiss();
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				distancePopup.dismiss();
			}
		});
		initpoputwindow(pview);
	}

	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
	}

	/**
	 * 新建一个popupwindow实例
	 * 
	 * @param view
	 */
	private void initpoputwindow(View view) {
		distancePopup = new PopupWindow(view,
				android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		distancePopup.setAnimationStyle(R.style.popupanimation);
	}

	private void takePhoto() {
		String SDState = Environment.getExternalStorageState();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
			ContentValues values = new ContentValues();
			photoUri = this.getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
			startActivityForResult(intent, 1);
		} else {
			Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 选择图片后，获取图片的路径
	 * 
	 * @param requestCode
	 * @param data
	 */
	private void doPhoto(int requestCode, Intent data) {

		String[] pojo = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
			cursor.moveToFirst();
			picPath = cursor.getString(columnIndex);
			// 在android4.0之后的会自动的关闭cursor 所以要加这个处理
			if (VERSION.SDK_INT < 14) {
				cursor.close();
			}
		}
		if (picPath != null
				&& (picPath.endsWith(".png") || picPath.endsWith(".PNG")
						|| picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
			final int _50dp = Util.dpToPx(this, 50F);
			final int _1024dp = Util.dpToPx(this, 500F);
			final int _768dp = Util.dpToPx(this, 300F);
			Bitmap bm = Util.getLocalBitmap(picPath, _50dp, _50dp);
			Bitmap tagBm = Util.getLocationThmub(picPath, _1024dp, _768dp);
			PhotoInfo photoInfo = new PhotoInfo();
			photoInfo.setBitmap(tagBm);
			List<PhotoInfo> list = new ArrayList<PhotoInfo>();
			list.add(photoInfo);
			allPhotoInfos.addAll(list);
			photoAdapter = new SelcctPhotoAdapter(this, allPhotoInfos,
					add_bask_image, width * 2 / 9);
			add_bask_image.setAdapter(photoAdapter);

		} else {
			Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindows(final int postion) {
		View pview = getLayoutInflater().inflate(
				R.layout.yyrg_show_select_image, null, false);
		TextView returns = (TextView) pview.findViewById(R.id.top_back);
		TextView delete = (TextView) pview.findViewById(R.id.delete);
		show_image_count = (TextView) pview.findViewById(R.id.show_image_count);
		TextView show_wancheng = (TextView) pview
				.findViewById(R.id.show_wancheng);
		show_wancheng.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				photoAdapter = new SelcctPhotoAdapter(YYRGAddBsakSingle.this,
						allPhotoInfos, add_bask_image, width * 2 / 9);
				add_bask_image.setAdapter(photoAdapter);
				distancePopup.dismiss();
			}
		});
		returns.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				distancePopup.dismiss();

			}
		});
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteImage();
			}
		});
		mViewPager = (ViewPager) pview.findViewById(R.id.vp);
		arrImages = new ArrayList<ImageView>();
		DisplayMetrics dm = new DisplayMetrics();
		bitmaps.clear();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;
		for (int i = 1; i < allPhotoInfos.size(); i++) {
			ImageView imageView = new ImageView(this);
			Bitmap bitmap=null;
			if (allPhotoInfos.get(i).getBitmap()!=null) {
				bitmap=allPhotoInfos.get(i).getBitmap();
			}else {
				 bitmap= YYRGUtil.compressImageFromFile(allPhotoInfos.get(i)
							.getPath_absolute());
				}
			imageView.setImageBitmap(bitmap);
			imageView.setTag(i + 1);
			arrImages.add(imageView);
			Bitmap image = ((BitmapDrawable) imageView.getDrawable())
					.getBitmap();
			bitmaps.add(image);
		}
		mViewPager.setPageMargin(pagerMarginPixels);
		mPagerAdapter = new ImagePagerAdapter();
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(mPageChangeListener);
		mViewPager.setCurrentItem(currentPic);
		setupOnTouchListeners(mViewPager);
		show_image_count.setText("1/" + arrImages.size());
		initpoputwindow(pview);
	}

	private void deleteImage() {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.tuichu_login, null);
		dialog = new AlertDialog.Builder(this).create();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView update_number = (TextView) layout
				.findViewById(R.id.update_number);
		update_number.setText("要删除这张图片吗？");
		TextView no_tuichu = (TextView) layout.findViewById(R.id.no_tuichu);
		no_tuichu.setText("否");
		TextView queding_tuichu = (TextView) layout
				.findViewById(R.id.queding_tuichu);
		queding_tuichu.setText("是");
		no_tuichu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		queding_tuichu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (allPhotoInfos.size() < 1) {
					dialog.dismiss();
					distancePopup.dismiss();
					return;
				}
				// TODO Auto-generated method stub
				int temp = Integer.parseInt(nowView) - 1;
				allPhotoInfos.remove(temp);
				bitmaps.clear();
				arrImages = new ArrayList<ImageView>();
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				int height = dm.heightPixels;
				for (int i = 1; i < allPhotoInfos.size(); i++) {
					ImageView imageView = new ImageView(YYRGAddBsakSingle.this);
					final int _1024dp = Util.dpToPx(YYRGAddBsakSingle.this,
							600F);
					final int _768dp = Util
							.dpToPx(YYRGAddBsakSingle.this, 400F);
					Bitmap bitmap = YYRGUtil.compressImageFromFile(allPhotoInfos
							.get(i).getPath_absolute());
					imageView.setImageBitmap(bitmap);
					imageView.setTag(i + 1);
					arrImages.add(imageView);
					Bitmap image = ((BitmapDrawable) imageView.getDrawable())
							.getBitmap();
					bitmaps.add(image);
				}

				mViewPager.setPageMargin(pagerMarginPixels);
				mPagerAdapter = new ImagePagerAdapter();
				mViewPager.setAdapter(mPagerAdapter);
				mViewPager.setOnPageChangeListener(mPageChangeListener);
				mViewPager.setCurrentItem(currentPic);
				setupOnTouchListeners(mViewPager);
				if (temp != 1) {
					mViewPager.setCurrentItem(temp);
					show_image_count.setText(temp + 1 + "/" + arrImages.size());
				} else if (temp + 1 == arrImages.size()) {
					show_image_count.setText(arrImages.size() + "/"
							+ arrImages.size());
				} else {
					show_image_count.setText("1/" + arrImages.size());
				}
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
	}

	private class ImagePagerAdapter extends PagerAdapter {
		public Map<Integer, ImageViewTouch> views = new HashMap<Integer, ImageViewTouch>();

		@Override
		public int getCount() {
			return bitmaps.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			ImageViewTouch imageView = new ImageViewTouch(
					YYRGAddBsakSingle.this);
			imageView.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			imageView.setFocusableInTouchMode(true);
			Bitmap b = bitmaps.get(position);
			imageView.setImageBitmapResetBase(b, true);
			imageView.setTag(position + 1);
			((ViewPager) container).addView(imageView);
			views.put(position, imageView);
			viewss.put(position, imageView);
			return imageView;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			ImageViewTouch imageView = (ImageViewTouch) object;
			imageView.setImageBitmapResetBase(null, true);
			imageView.clear();
			((ViewPager) container).removeView(imageView);
			views.remove(position);
		}

		@Override
		public void startUpdate(View container) {
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((ImageViewTouch) object);
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}
	}

	private ImageViewTouch getCurrentImageView() {
		if (mPagerAdapter.views != null) {
			if (mPagerAdapter.views.get(mViewPager.getCurrentItem()) != null) {
				return (ImageViewTouch) mPagerAdapter.views.get((mViewPager
						.getCurrentItem()));
			} else {
				ImageViewTouch img = new ImageViewTouch(YYRGAddBsakSingle.this);
				img.setImageBitmap(BitmapFactory.decodeResource(
						YYRGAddBsakSingle.this.getResources(),
						R.drawable.ic_launcher));
				return img;
			}
		} else {
			ImageViewTouch img = new ImageViewTouch(YYRGAddBsakSingle.this);
			img.setImageBitmap(BitmapFactory.decodeResource(
					YYRGAddBsakSingle.this.getResources(),
					R.drawable.ic_launcher));
			return img;
		}
	}

	ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageSelected(int position, int prePosition) {
			ImageView imageView = viewss.get(position);
			nowView = "" + imageView.getTag();
			show_image_count.setText(imageView.getTag() + "/"
					+ arrImages.size());
			mViewPager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			mOnPagerScoll = true;
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_DRAGGING) {
				mOnPagerScoll = true;
			} else if (state == ViewPager.SCROLL_STATE_SETTLING) {
				mOnPagerScoll = false;
			} else {
				mOnPagerScoll = false;
			}
		}
	};

	private void setupOnTouchListeners(View rootView) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
			mScaleGestureDetector = new ScaleGestureDetector(this,
					new MyOnScaleGestureListener());
		}
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		OnTouchListener rootListener = new OnTouchListener() {
			@SuppressLint("NewApi")
			public boolean onTouch(View v, MotionEvent event) {
				if (!mOnScale) {
					if (!mOnPagerScoll) {
						mGestureDetector.onTouchEvent(event);
					}
				}

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
					if (!mOnPagerScoll) {
						mScaleGestureDetector.onTouchEvent(event);
					}
				}

				ImageViewTouch imageView = getCurrentImageView();
				if (imageView == null) {
					return true;
				}
				if (!mOnScale) {
					Matrix m = imageView.getImageViewMatrix();
					RectF rect = new RectF(0, 0, imageView.mBitmapDisplayed
							.getBitmap().getWidth(), imageView.mBitmapDisplayed
							.getBitmap().getHeight());
					m.mapRect(rect);
					if (!(rect.right > imageView.getWidth() + 0.1 && rect.left < -0.1)) {
						try {
							mViewPager.onTouchEvent(event);
						} catch (ArrayIndexOutOfBoundsException e) {
						}
					}
				}
				return true;
			}
		};
		rootView.setOnTouchListener(rootListener);
	}

	private class MyOnScaleGestureListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {

		float currentScale;
		float currentMiddleX;
		float currentMiddleY;

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			final ImageViewTouch imageView = getCurrentImageView();
			if (imageView == null) {
				return;
			}
			if (currentScale > imageView.mMaxZoom) {
				imageView
						.zoomToNoCenterWithAni(currentScale
								/ imageView.mMaxZoom, 1, currentMiddleX,
								currentMiddleY);
				currentScale = imageView.mMaxZoom;
				imageView.zoomToNoCenterValue(currentScale, currentMiddleX,
						currentMiddleY);
			} else if (currentScale < imageView.mMinZoom) {
				imageView.zoomToNoCenterWithAni(currentScale,
						imageView.mMinZoom, currentMiddleX, currentMiddleY);
				currentScale = imageView.mMinZoom;
				imageView.zoomToNoCenterValue(currentScale, currentMiddleX,
						currentMiddleY);
			} else {
				imageView.zoomToNoCenter(currentScale, currentMiddleX,
						currentMiddleY);
			}
			imageView.center(true, true);
			imageView.postDelayed(new Runnable() {
				@Override
				public void run() {
					mOnScale = false;
				}
			}, 300);
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mOnScale = true;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector, float mx, float my) {
			ImageViewTouch imageView = getCurrentImageView();
			if (imageView == null) {
				return true;
			}
			float ns = imageView.getScale() * detector.getScaleFactor();
			currentScale = ns;
			currentMiddleX = mx;
			currentMiddleY = my;
			if (detector.isInProgress()) {
				imageView.zoomToNoCenter(ns, mx, my);
			}
			return true;
		}
	}

	private class MyGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// Log.d(TAG, "gesture onScroll");
			if (mOnScale) {
				return true;
			}
			if (mPaused) {
				return false;
			}
			ImageViewTouch imageView = getCurrentImageView();
			if (imageView == null) {
				return true;
			}
			imageView.panBy(-distanceX, -distanceY);
			imageView.center(true, true);

			// 超出边界效果去掉这个
			imageView.center(true, true);

			return true;
		}

		@Override
		public boolean onUp(MotionEvent e) {
			return super.onUp(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (mControlsShow) {
			} else {
			}

			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (mPaused) {
				return false;
			}
			ImageViewTouch imageView = getCurrentImageView();
			if (imageView == null) {
				return true;
			}
			if (imageView.mBaseZoom < 1) {
				if (imageView.getScale() > 2F) {
					imageView.zoomTo(1f);
				} else {
					imageView.zoomToPoint(3f, e.getX(), e.getY());
				}
			} else {
				if (imageView.getScale() > (imageView.mMinZoom + imageView.mMaxZoom) / 2f) {
					imageView.zoomTo(imageView.mMinZoom);
				} else {
					imageView.zoomToPoint(imageView.mMaxZoom, e.getX(),
							e.getY());
				}
			}
			return true;
		}
	}

	private void setImage(final ImageView logo, String href, final int i) {
		bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {

				// bitmap = arg2;
				List<Object> list = new ArrayList<Object>();
				list.add(arg2);
				list.add(i);
				Message msg = new Message();
				msg.what = 1;
				msg.obj = list;
				handler.sendMessage(msg);
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.new_yda__top_zanwu);
			}
		});
	}

}
