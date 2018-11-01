package com.mall.newmodel;

import android.content.Context;
import android.util.Log;

import com.mall.view.service.DBController;

import java.sql.SQLException;

import cn.woblog.android.downloader.DownloadService;
import cn.woblog.android.downloader.callback.DownloadManager;
import cn.woblog.android.downloader.config.Config;
import cn.woblog.android.downloader.domain.DownloadInfo;

/**
 * Created by Administrator on 2017/10/31.
 */
public class DownloadUtli {
    private  static DownloadManager downloadManager;
    private  static DBController dbController;

    private static DownloadInfo downloadInfo;

    public   static DownloadManager getDownloadManager(Context context){
        Config config = new Config();
//set database path.
//    config.setDatabaseName("/sdcard/a/d.db");
//      config.setDownloadDBController(dbController);

//set download quantity at the same time.
        config.setDownloadThread(10);

//set each download info thread number
        config.setEachDownloadThread(5);

// set connect timeout,unit millisecond
        config.setConnectTimeout(20000);

// set read data timeout,unit millisecond
        config.setReadTimeout(20000);
        downloadManager =   DownloadService.getDownloadManager(context.getApplicationContext(),config);;
        return downloadManager;
    }
    public   static  DBController getDBController(Context context){
        try {
            dbController = DBController.getInstance(context.getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("数据库穿件失败","e:"+e.toString());
        }

        return  dbController;
    }



}

