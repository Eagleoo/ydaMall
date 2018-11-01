package com.mall.view.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.mall.util.IAsynTask;
import com.mall.util.Util;

import java.io.File;
import java.io.Serializable;

public class UpdateService extends Service {
//	public static String TAG = "com.mall.view.service.UpdateService";
	private UpdateBinder uUpdateBinder = new UpdateBinder();

	public class UpdateBinder extends Binder {
		public UpdateService getService() {
			return UpdateService.this;
		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent,startId);
		Util.asynTask(new IAsynTask() {
			
			@Override
			public void updateUI(Serializable runData) {
				String[] updateInfo = (String[])runData;
				if(null != updateInfo && 2 <= updateInfo.length){
					download(updateInfo[0], updateInfo[1]);
				}
			}
			
			@Override
			public Serializable run() {
				return Util.update();
			}
		});
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return uUpdateBinder;
	}

	/**
	 * 下载新版本
	 * 
	 * @param url
	 */
	public void download(String url, final String serVer) {
//		LogUtils.e("tishi xiazai ");
//		final Notification notification = new Notification(
//				android.R.drawable.stat_sys_download, "远大云商" + serVer
//						+ "版下载中...", System.currentTimeMillis());// 概要
//		final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//				new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);// 1为请求码
//																	// 0为Flag标志位
//		notification.setLatestEventInfo(this, "远大云商" + serVer + "版下载中...",
//				"正在为您后台下载【远大云商" + serVer + "】版", pendingIntent);// 点击通知时候进行的活动：用contentIntent传递
//		// notification.defaults = Notification.DEFAULT_SOUND;// 发送状态栏的默认铃声
//		 notification.flags = Notification.FLAG_NO_CLEAR;
//		final NotificationManager manager = (NotificationManager) this
//				.getSystemService(Context.NOTIFICATION_SERVICE);// 得到系统通知服务
//		final int id = 0;
//		manager.notify(id, notification);// 通知系统我们定义的notification，id为该notification的id；这里定义为100.
//
//		HttpUtils http = new HttpUtils(10000);
//		http.download(url, Util.apkPath + "Mall" + serVer + ".apk", false,
//				false, new RequestCallBack<File>() {
//					private long updateTotal = 0L;
//
//					@Override
//					public void onSuccess(ResponseInfo<File> arg0) {
//						Intent intent = new Intent();
//						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						intent.setAction(android.content.Intent.ACTION_VIEW);
//						intent.setDataAndType(Uri.fromFile(arg0.result),
//								"application/vnd.android.package-archive");
//						PendingIntent pendingIntent = PendingIntent
//								.getActivity(UpdateService.this, 0, intent,
//										PendingIntent.FLAG_UPDATE_CURRENT);// 1为请求码
//						notification.icon = R.drawable.ic_launcher;
//						notification.setLatestEventInfo(UpdateService.this,
//								"远大云商" + serVer + "下载完成", "远大云商" + serVer
//										+ "下载完成，点击安装", pendingIntent);
//						notification.defaults = Notification.DEFAULT_SOUND;
//						notification.flags = Notification.FLAG_AUTO_CANCEL;
//						manager.notify(id, notification);
//					}
//
//					@Override
//					public void onLoading(long total, long current,
//							boolean isUploading) {
//						super.onLoading(total, current, isUploading);
//						updateTotal += current;
//						if (updateTotal >= 100L * 1024L) {
//							notification.setLatestEventInfo(
//									UpdateService.this,
//									"远大云商" + serVer + "下载中...",
//									"下载进度："
//											+ ""
//											+ (int) ((Util.getDouble(current
//													+ "") / Util
//														.getDouble(total + "")) * 100)
//											+ "%", pendingIntent);
//							LogUtils.d(current + "/" + total);
//							manager.notify(id, notification);
//							updateTotal = 0;
//						}
//					}
//
//					@Override
//					public void onFailure(HttpException arg0, String arg1) {
//						notification.flags = Notification.FLAG_AUTO_CANCEL;
//						notification.setLatestEventInfo(UpdateService.this,
//								"远大商城" + serVer + "下载失败。", "下载失败：" + arg1,
//								pendingIntent);
//						manager.notify(0, notification);
//					}
//				});
	}

	/**
	 * 安装
	 */
	public void instance(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

}
