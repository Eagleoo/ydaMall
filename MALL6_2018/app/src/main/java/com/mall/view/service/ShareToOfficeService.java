package com.mall.view.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;

import java.io.Serializable;
public class ShareToOfficeService extends Service{
//	public static final String TAG = "com.mall.view.service.ShareToOfficeService";
	public ShareBinder binder=new ShareBinder();
	private Context context;
	private User user;
	private String pid="";
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
    @Override
    public void onStart(Intent intent, int startId) {
    	super.onStart(intent, startId);
    }
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	public class ShareBinder extends Binder{
		public ShareToOfficeService getService(){
			return ShareToOfficeService.this;
		}
	}
	public void ShareMethod(){
		if(user!=null){
			final NotificationManager nm=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			Notification notification =new NotificationCompat.Builder(context)
					.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
					.setSmallIcon(R.drawable.ic_launcher)
					.setTicker("分享成功")
					.setContentTitle("分享成功")
					.setContentText( "成功分享商品到空间")
					.setWhen(System.currentTimeMillis())
					.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0))
					.build();

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
			/**发起通知**/
			notificationManager.notify(0, notification);

//			Notification notification=new Notification(R.drawable.ic_launcher, "分享成功", System.currentTimeMillis());
//	        notification.flags=Notification.FLAG_ONGOING_EVENT;
//	        PendingIntent pi=PendingIntent.getActivity(context, 0, new Intent(), 0);
//			notification.setLatestEventInfo(context, "成功分享商品到空间", "....",pi);
//			nm.notify(R.drawable.ic_launcher, notification);

			Util.asynTask(context, "分享商品到空间", new IAsynTask() {  
				@Override
				public void updateUI(Serializable runData) {
					if(runData!=null){
						if("ok".equals(runData+"")){
							nm.cancel(R.drawable.ic_launcher);
							Util.showShareToOfficeDialog(context, "分享商品成功！", "继续分享", "去空间看看",R.drawable.emoji_2);
						}else{
							Toast.makeText(context,runData+"",Toast.LENGTH_LONG).show();
						}
					}else{
						Toast.makeText(context,"分享失败",Toast.LENGTH_LONG).show();
					}
				} 
				@Override 
				public Serializable run() {
					Web web=new Web(Web.officeUrl, Web.ShareProToOffice,"userID="+user.getUserId()+"&pid="+pid+"&userPaw="+user.getMd5Pwd()+"&articleid=");
					return web.getPlan();
				}
			});
		}
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
}
