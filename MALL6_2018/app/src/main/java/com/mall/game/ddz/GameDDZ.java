package com.mall.game.ddz;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.mall.serving.community.util.Util;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.view.service.GameLogSaveService;
import com.mall.view.service.GameLogSaveService.GameLogBinder;

public class GameDDZ extends Activity {
	MyView myView;
	String messString;
	private int w_p = 0;
	private int h_p = 0;
	private float density = 0;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				messString = msg.getData().getString("data");
				showDialog(messString);
			}
		}
	};
	public ServiceConnection saveGameLogConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			String userId = "";
			String md5Pwd = "";
			if (UserData.getUser() != null) {
				userId = UserData.getUser().getUserId();
				md5Pwd = UserData.getUser().getMd5Pwd();
			} else {
				userId = "匿名玩家";
			}
			GameLogSaveService.GameLogBinder service = (GameLogBinder) arg1;
			GameLogSaveService gameLogService = service.getService();
			gameLogService.setContext(GameDDZ.this);
			synchronized (this) {
				gameLogService.saveGameLog("1", "0", userId + "", md5Pwd + "");
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		// 隐藏标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐藏状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 锁定横屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		myView = new MyView(this, handler);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		h_p = metric.heightPixels;
		w_p = metric.widthPixels;
		density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		myView.setScreen_height((int) (h_p));
		myView.setScreen_width((int) (w_p));
		myView.setDensity(density);
		setContentView(myView);
		System.gc();
	}

	public void showDialog(String messString) {
		if (Util.isNull(messString)) {
			messString = "再来一次";
		}
		final VoipDialog dialog = new VoipDialog(messString, this, "重新开始",
				"退出游戏", new OnClickListener() {
					@Override
					public void onClick(View v) {
						reGame();
					}
				}, new OnClickListener() {
					@Override
					public void onClick(View v) {
						GameDDZ.this.finish();
					}
				});
		dialog.setCancelable(false);
		dialog.show();
	}

	// 重新开始游戏
	public void reGame() {
		System.gc();
		MyView myView = new MyView(this, handler);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		h_p = metric.heightPixels;
		w_p = metric.widthPixels;
		density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		myView.setScreen_height((int) (h_p));
		myView.setScreen_width((int) (w_p));
		myView.setDensity(density);
		setContentView(myView);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		myView.recycle();
		GameDDZ.this.getApplicationContext().bindService(
				new Intent(GameDDZ.this,GameLogSaveService.class), saveGameLogConnection,
				Context.BIND_AUTO_CREATE);
	}
}
