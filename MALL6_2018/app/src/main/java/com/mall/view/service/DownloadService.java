package com.mall.view.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.ErrorEntity;
import com.arialyy.aria.core.download.DownloadTarget;
import com.arialyy.aria.core.download.DownloadTask;
import com.mall.MessageEvent;
import com.mall.util.FileUtil;
import com.mall.util.MyLog;
import com.mall.util.Util;
import com.mall.view.R;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;


/**
 * Created by Administrator on 2017/10/31.
 */

public class DownloadService extends Service {

    private static final String DOWNLOAD_URL =
            "http://down.yda360.com/Mall.apk";

    private Notification notification;

    private NotificationManager notificationManager;

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    private void setNotificationProgess(DownloadTask downloadTask, String downloadinfo ){


        long len = downloadTask.getFileSize();
        int progress = (int) (downloadTask.getCurrentProgress() * 100 / len);


        Log.e("setNotificationProgess","setNotificationProgess"+progress);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentTitle("远大云商");
        if (downloadinfo.equals("停止下载")||downloadinfo.equals("下载失败")||downloadinfo.equals("取消下载")
                ||downloadinfo.equals("下载中")
                ){
            EventBus.getDefault().post(new MessageEvent(downloadinfo,progress));
            builder.setProgress(100, progress, false);
            builder.setContentText(progress + "%");

        }else  if(downloadinfo.equals("下载完成")){
            EventBus.getDefault().post(new MessageEvent(downloadinfo,100));
            builder.setProgress(100, 100, false);
            builder.setContentText(100 + "%");
        }else if (downloadinfo.equals("开始下载")){
            progress=0;
        }


        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setTicker(downloadinfo);




        builder.setContentIntent(progress>=100&&isinstallation?getContentIntent():
                PendingIntent.getActivity(this,0,new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
        notification = builder.build();
        notificationManager.notify(0, notification);

    }

    private boolean isinstallation=false;

    private  PendingIntent getContentIntent(){
        isinstallation=false;
        Log.e("tag", "getContentIntent()");
        Log.e("应用安装", filesavepath);
        final File apkFile = new File(filesavepath);
        MyLog.e("文件是否存在"+apkFile.exists());
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Log.e("版本", "android.os.Build.VERSION.SDK_INT" + android.os.Build.VERSION.SDK_INT);
        Log.e("获取绝对路径", apkFile.getAbsolutePath());

        if (android.os.Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            Uri apkUri =
                    FileProvider.getUriForFile(this, "com.mall.view.fileprovider", apkFile);
            Log.e("7.0以上路径:", "路径" + apkUri.getPath());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()),
                    "application/vnd.android.package-archive");
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        startActivity(intent);
        return pendingIntent;
    }

    String filesavepath="";

    @Override public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Aria.download(this).register();
        filesavepath=FileUtil.FILE_FILE+"doc/"+ "Mall"+Util.netversion+".apk";
        List<ErrorEntity> list = Aria.get(this). getErrorLog();

        if (list!=null&&list.size()>0){
            Log.e("下载错误日志长度","长度"+list.size());
            for (ErrorEntity errorEntity:
                    list) {
                Log.e("下载错误日志",errorEntity.toString());
            }
        }
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {

        boolean isexists= Aria.download(this).taskExists(DOWNLOAD_URL);
        MyLog.e("下载任务",
                "任务是否存在"+isexists
        );
        isinstallation=true;
        if (!isexists){
            FileUtil.deleteDir(new File(filesavepath));
        }
        DownloadTarget downloadTarget=Aria.download(this)
                .load(DOWNLOAD_URL)
                .setDownloadPath(filesavepath);
        MyLog.e("下载任务",
                downloadTarget.getDownloadEntity().getUrl()+":"+downloadTarget.isRunning());

        try {
            Bundle bundle = intent.getExtras();
            String downloadtype =  bundle.getString("downloadtype");

            int state=downloadTarget.getTaskState();
            Log.e("下载任务执行状态","state"+state);
            if (state==-1||state==0){
                downloadTarget.reStart();
            }else if(state==1){

            }else if(state==2){
                downloadTarget.resume();
            }else  if(state==3){
                downloadTarget.start();
            }else if(state==4){
                downloadTarget.stop();
            }
        }catch (Exception e){

        }


        return super.onStartCommand(intent, flags, startId);
    }


    @Override public void onDestroy() {
        super.onDestroy();
        Aria.download(this).unRegister();
    }

    @Download.onPre void onPre(DownloadTask task) {
        Log.e("下载","该下载任务预处理");
        setNotificationProgess(task,"开始下载");
    }
    @Download.onTaskResume void taskResume(DownloadTask task) {
        Log.e("下载","任务恢复时");
    }

    @Download.onNoSupportBreakPoint public void onNoSupportBreakPoint(DownloadTask task) {
        Log.e("下载","该下载链接不支持断点");
    }

    @Download.onTaskStart public void onTaskStart(DownloadTask task) {
        long len = task.getFileSize();
        int progress = (int) (task.getCurrentProgress() * 100 / len);
        Log.e("下载","开始下载"+progress);

        setNotificationProgess(task,"开始下载");

    }

    @Download.onTaskStop public void onTaskStop(DownloadTask task) {
        long len = task.getFileSize();
        int progress = (int) (task.getCurrentProgress() * 100 / len);
        Log.e("下载","停止下载"+progress);

        setNotificationProgess(task,"停止下载");
    }

    @Download.onTaskCancel public void onTaskCancel(DownloadTask task) {
        long len = task.getFileSize();
        int progress = (int) (task.getCurrentProgress() * 100 / len);
        Log.e("下载","取消下载"+progress);
        notificationManager.cancel(0);
        setNotificationProgess(task,"取消下载");
    }

    @Download.onTaskFail public void onTaskFail(DownloadTask task) {
        long len = task.getFileSize();
        int progress = (int) (task.getCurrentProgress() * 100 / len);
        Log.e("下载","下载失败"+progress);
        setNotificationProgess(task,"下载失败");
    }

    @Download.onTaskComplete public void onTaskComplete(DownloadTask task) {
        long len = task.getFileSize();
        int progress = (int) (task.getCurrentProgress() * 100 / len);
        Log.e("下载","下载完成"+progress);
        Aria.download(this).load(DOWNLOAD_URL).stop();
        Aria.download(this).load(DOWNLOAD_URL).removeRecord();
        setNotificationProgess(task,"下载完成");
    }

    @Download.onTaskRunning public void onTaskRunning(DownloadTask task) {

        long len = task.getFileSize();
        int p = (int) (task.getCurrentProgress() * 100 / len);
        Log.e("下载","下载中"+p);
        setNotificationProgess(task,"下载中");

        boolean isCheckedWifi= getSharedPreferences("isCheckedWifi", MODE_PRIVATE).getBoolean("isCheckedWifi", false);

        if (isCheckedWifi){ //允许wifi下自动更新 下载的时候检查是否处于wifi下的
            int netWorkState = Util.getNetworkType(this);
            if (netWorkState!=10){//不处于wifi下停止下载
//                DownloadTarget
                Aria.download(this).load(DOWNLOAD_URL).stop();
            }
        }
    }
}

