package com.mall.scan;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dtr.zbar.build.ZBarDecoder;
import com.dtr.zbar.scan.CameraManager;
import com.dtr.zbar.scan.CameraPreview;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LMSJDetailFrame;
import com.mall.view.LoginFrame;
import com.mall.view.PayAgentFrame;
import com.mall.view.ProductDeatilFream;
import com.mall.view.R;
import com.mall.view.RegisterFrame;

public class CaptureActivity extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private CameraManager mCameraManager;

	private FrameLayout scanPreview;

	// private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private ImageView scanLine;

	private Rect mCropRect = null;
	private boolean barcodeScanned = false;
	private boolean previewing = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_capture);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		findViewById();
		// addEvents();
		initViews();
	}

	private void findViewById() {
		scanPreview = (FrameLayout) findViewById(R.id.capture_preview);

		// scanContainer = (RelativeLayout)
		// findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLine = (ImageView) findViewById(R.id.capture_scan_line);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (scanLine.getTag() != null) {
			afresh();
		}
		scanLine.setTag("");

	}

	private void afresh() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(previewCb);
			mCamera.startPreview();
			previewing = true;
			new Thread() {
				public void run() {
					mCamera.autoFocus(autoFocusCB);
				};

			}.start();
		}

	}

	private void initViews() {
		autoFocusHandler = new Handler();

		try {
			mCameraManager = new CameraManager(this);
			if (!mCameraManager.isOpen()) {
				mCameraManager.openDriver();
			}
			mCamera = mCameraManager.getCamera();
		} catch (Exception e) {
			displayFrameworkBugMessageAndExit();
			e.printStackTrace();
		}
		try {
			mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
			scanPreview.addView(mPreview);

			TranslateAnimation animation = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.85f);
			animation.setDuration(3000);
			animation.setRepeatCount(-1);
			animation.setRepeatMode(Animation.REVERSE);
			scanLine.startAnimation(animation);
		} catch (Exception ee) {
			Util.show("打开摄像头失败，情监测是否允许云商访问您的摄像头！", this);
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage("打开摄像头失败");
		builder.setPositiveButton("确定", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();

			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				finish();

			}
		});
		builder.show();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		releaseCamera();
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing && mCamera != null)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Size size = camera.getParameters().getPreviewSize();

			// 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
			byte[] rotatedData = new byte[data.length];
			for (int y = 0; y < size.height; y++) {
				for (int x = 0; x < size.width; x++)
					rotatedData[x * size.height + size.height - y - 1] = data[x
							+ y * size.width];
			}

			// 宽高也要调整
			int tmp = size.width;
			size.width = size.height;
			size.height = tmp;

			initCrop();
			ZBarDecoder zBarDecoder = new ZBarDecoder();
			String result = zBarDecoder.decodeCrop(rotatedData, size.width,
					size.height, mCropRect.left, mCropRect.top,
					mCropRect.width(), mCropRect.height());

			if (!TextUtils.isEmpty(result)) {

				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				barcodeScanned = true;
				if (result.contains("http") || result.contains("dfdd")) {
					parseBarCode(result);
				} else {
					Util.show(result, CaptureActivity.this);
					afresh();
				}

			}
		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 2000);
		}
	};

	/**
	 * 初始化截取的矩形区域
	 */
	private void initCrop() {
		int cameraWidth = mCameraManager.getCameraResolution().y;
		int cameraHeight = mCameraManager.getCameraResolution().x;

		/** 获取布局中扫描框的位置信息 */
		int[] location = new int[2];
		scanCropView.getLocationInWindow(location);

		int cropLeft = location[0];
		int cropTop = location[1] - getStatusBarHeight();

		int cropWidth = scanCropView.getWidth();
		int cropHeight = scanCropView.getHeight();

		/** 获取布局容器的宽高 */
		int containerWidth = scanPreview.getWidth();
		int containerHeight = scanPreview.getHeight();

		/** 计算最终截取的矩形的左上角顶点x坐标 */
		int x = cropLeft * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的左上角顶点y坐标 */
		int y = cropTop * cameraHeight / containerHeight;

		/** 计算最终截取的矩形的宽度 */
		int width = cropWidth * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的高度 */
		int height = cropHeight * cameraHeight / containerHeight;

		/** 生成最终的截取的矩形 */
		mCropRect = new Rect(x, y, width + x, height + y);
	}

	private int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 解析二维码
	private void parseBarCode(String msg) {
		LogUtils.e(msg);
		// 手机震动
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(100);

		if (msg.startsWith("http://") || msg.startsWith("https://")
				|| msg.matches("(([a-zA-z0-9]|-){1,}\\.){1,}[a-zA-z0-9]{1,}-*")) {
			String[] k_v = msg.substring(msg.indexOf("?")+1).split("&");
			Map<String,String> params = new HashMap<String, String>();
			for(String kv: k_v){
				if(!Util.isNull(kv)){
					String[] p = kv.split("=");
					if(0 != p.length){
						if(1 == p.length)
							p = new String[]{p[0],""};
					}
					params.put(p[0].toLowerCase(), p[1]);
				}
			}
			if (msg.toLowerCase().contains("newprodetail")) {

				String pid = params.get("pid");
				if(Util.isNull(pid))
					pid = params.get("id");
				String[] key = null;
				String[] val = null;
				if(params.containsKey("unum")){
					key = new String[]{"url","unum"};
					val = new String[]{pid,params.get("unum")};
				}else{
					key = new String[]{"url"};
					val = new String[]{pid};
				}
				if (Util.isInt(pid)) {
					Util.showIntent(this, ProductDeatilFream.class,key, val);
					finish();
					return;
				}
			}

			if ((msg.toLowerCase().contains("shopsollectspage"))) {
				String pid = msg.substring(msg.lastIndexOf("=") + 1);
				if (Util.isInt(pid)) {
					Util.showIntent(this, LMSJDetailFrame.class, new String[] {
							"id", "name" }, new String[] { params.get("cid"), "联盟商家详情" });
					finish();
					return;
				}
			}

			if (msg.toLowerCase().contains("phoneregiste") || msg.toLowerCase().contains("registe")) {
				String inviter = params.get("inviter");
				if(Util.isNull(inviter))
					inviter = params.get("unum");
				String[] key = null;
				String[] val = null;
				if(!Util.isNull(inviter)){
					key = new String[]{"inviter",};
					val = new String[]{inviter};
				}
				Util.showIntent(this, RegisterFrame.class,key,val);
				finish();
				return;
			}
			if (msg.toLowerCase().contains("pay1.aspx?p1=")) {
				String[] strings = msg.split("username=");
				if (strings.length > 1) {
					if (UserData.getUser() == null) {
						Util.showIntent(this, LoginFrame.class);
						return;
					}
					Util.showIntent(this, PayAgentFrame.class,
							new String[] { "msg" }, new String[] { strings[1] });
					finish();
					return;
				}
			}
			Util.openWeb(this, msg);
			// else {
			// String[] strings=msg.split("::");
			// if (strings.length>2) {
			// if (UserData.getUser()==null) {
			// Util.showIntent(this, LoginFrame.class);
			// return;
			// }
			// Util.showIntent(this, PayAgentFrame.class,
			// new String[] { "msg" }, new String[] { msg });
			// finish();
			// return;
			// }

		}
	}

	@OnClick({ R.id.topback, R.id.topright })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.topback:
			finish();
			break;
		case R.id.topright:
			flashlightUtils();
			if (v.getTag() == null) {

				v.setTag("1");
				((ImageView) v).setImageResource(R.drawable.flash_open);
			} else {

				v.setTag(null);
				((ImageView) v).setImageResource(R.drawable.flash_default);
			}
			break;

		default:
			break;
		}
	}

	public void flashlightUtils() {
		if (mCamera == null) {
			mCamera = Camera.open();
		}

		Parameters parameters = mCamera.getParameters();
		// String flashMode = parameters.getFlashMode();

		if (isFlashlightOn()) {

			parameters.setFlashMode(Parameters.FLASH_MODE_OFF);// 关闭
			mCamera.setParameters(parameters);
			// mCamera.release();
			// mCamera = null;

		} else {
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);// 开启
			mCamera.setParameters(parameters);

		}

	}

	public boolean isFlashlightOn() {
		if (mCamera == null) {
			mCamera = Camera.open();
		}

		Parameters parameters = mCamera.getParameters();
		String flashMode = parameters.getFlashMode();

		if (flashMode.equals(Parameters.FLASH_MODE_TORCH)) {

			return true;
		} else {
			return false;
		}
	}
}
