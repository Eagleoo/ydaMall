package com.mall.view;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.lidroid.xutils.util.LogUtils;
import com.mall.card.adapter.CardUtil;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.qrcode.camera.CameraManager;
import com.mall.qrcode.decoding.CaptureActivityHandler;
import com.mall.qrcode.decoding.FinishListener;
import com.mall.qrcode.decoding.InactivityTimer;
import com.mall.qrcode.view.ViewfinderView;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

public class QRCodeFrame extends Activity implements SurfaceHolder.Callback {

	private boolean hasSurface;
	private String characterSet;

	private ViewfinderView viewfinderView;
	private ProgressDialog mProgress;


	/**
	 * 活动监控器，用于省电，如果手机没有连接电源线，那么当相机开启后如果一直处于不被使用状态则该服务会将当前activity关闭。
	 * 活动监控器全程监控扫描活跃状态，与CaptureActivity生命周期相同.每一次扫描过后都会重置该监控，即重新倒计时。
	 */
	private InactivityTimer inactivityTimer;
	private CameraManager cameraManager;
	private Vector<BarcodeFormat> decodeFormats;// 编码格式
	private CaptureActivityHandler mHandler;// 解码线程
	private String state;
	private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet
			.of(ResultMetadataType.ISSUE_NUMBER,
					ResultMetadataType.SUGGESTED_PRICE,
					ResultMetadataType.ERROR_CORRECTION_LEVEL,
					ResultMetadataType.POSSIBLE_COUNTRY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initSetting();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scanner_qrcode);
		state=getIntent().getStringExtra("state");
		initView();
	}

	/**
	 * 初始化窗口设置
	 */
	private void initSetting() {
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕处于点亮状态
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 竖屏
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		cameraManager = new CameraManager(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);
		Util.initTop(this, "扫描二维码", R.drawable.flash_default, new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() == null) {
					cameraManager.setTorch(true);
					v.setTag("1");
					((ImageView)v).setImageResource(R.drawable.flash_open);
				} else {
					cameraManager.setTorch(false);
					v.setTag(null);
					((ImageView)v).setImageResource(R.drawable.flash_default);
				}
			}
		});
	}

	/**
	 * 主要对相机进行初始化工作
	 */
	@Override
	protected void onResume() {

		inactivityTimer.onActivity();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			// 如果SurfaceView已经渲染完毕，会回调surfaceCreated，在surfaceCreated中调用initCamera()
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		// 恢复活动监控器
		inactivityTimer.onResume();
		super.onResume();
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	/**
	 * 初始化摄像头。打开摄像头，检查摄像头是否被开启及是否被占用
	 * 
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			if (mHandler == null) {
				mHandler = new CaptureActivityHandler(this, decodeFormats,
						characterSet, cameraManager);
			}
		} catch (IOException ioe) {
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			displayFrameworkBugMessageAndExit();
		}
	}

	/**
	 * 初始化照相机失败显示窗口
	 */
	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("初始化相机错误！");
		builder.setPositiveButton("确定", new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	/**
	 * 暂停活动监控器,关闭摄像头
	 */
	@Override
	protected void onPause() {
		if (mHandler != null) {
			mHandler.quitSynchronously();
			mHandler = null;
		}
		// 暂停活动监控器
		inactivityTimer.onPause();
		// 关闭摄像头
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		if (mProgress != null && mProgress.isShowing()) {
			mProgress.dismiss();
		}
		super.onPause();
	}

	/**
	 * 停止活动监控器,保存最后选中的扫描类型
	 */
	@Override
	protected void onDestroy() {
		// 停止活动监控器
		inactivityTimer.shutdown();
		if (mProgress != null) {
			mProgress.dismiss();
		}
		super.onDestroy();
	}

	/**
	 * 获取扫描结果
	 * 
	 * @param rawResult
	 * @param barcode
	 * @param scaleFactor
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		parseBarCode(rawResult.getText());
	}

	// 解析二维码
	private void parseBarCode(String msg) {
		if (TextUtils.isEmpty(state)) {
			LogUtils.e("___________"+msg);
			// 手机震动
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(100);
			if (msg.startsWith("http://")
					|| msg.startsWith("https://")
					|| msg.matches("(([a-zA-z0-9]|-){1,}\\.){1,}[a-zA-z0-9]{1,}-*")) {
				if (msg.toLowerCase().contains("newprodetail")) {
					String pid = msg.substring(msg.lastIndexOf("=") + 1);
					if (Util.isInt(pid)) {
						Util.showIntent(this, ProductDeatilFream.class,
								new String[] { "url" }, new String[] { pid });
						return;
					}
				}
				if (msg.toLowerCase().contains("shopsollectspage")) {
					String pid = msg.substring(msg.lastIndexOf("=") + 1);
					if (Util.isInt(pid)) {
						Util.showIntent(this, LMSJDetailFrame.class, new String[] {
								"id", "name" }, new String[] { pid, "商家详情" });
						return;
					}
				}
				LogUtils.e("openWeb");
				Util.openWeb(this, msg);
			}	
		}else {
			Log.v("jieguo---", msg);
			Map<String, String> map=new HashMap<String, String>();
			try {
		//		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
				addNameCardShare(URLDecoder.decode(msg, "utf-8"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	/**
	 * 闪光灯调节器。自动检测环境光线强弱并决定是否开启闪光灯
	 */
	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return mHandler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	/**
	 * 在经过一段延迟后重置相机以进行下一次扫描。 成功扫描过后可调用此方法立刻准备进行下次扫描
	 * 
	 * @param delayMS
	 */
	public void restartPreviewAfterDelay(long delayMS) {
		if (mHandler != null) {
			mHandler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 发起交换
	 */
	public void addNameCardShare(final String touserid) {

		final User user = UserData.getUser();
		Util.asynTask(QRCodeFrame.this,"处理中…",new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (map.get("code") != null) {
						if (map.get("code").equals("200")) {
							Toast.makeText(QRCodeFrame.this, "发起交换成功",
									Toast.LENGTH_SHORT).show();
							finish();
						} else {
							Toast.makeText(QRCodeFrame.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						}
					} else {

						Toast.makeText(QRCodeFrame.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(QRCodeFrame.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard, Web.addNameCardShare,
						"addNameCardShare", "userId="
								+ Util.get(user.getUserId())+ "&md5Pwd="
								+ user.getMd5Pwd() + "&touserid=" + touserid);
				String s = web.getPlan();
				return s;
			}

		});
	}
}
