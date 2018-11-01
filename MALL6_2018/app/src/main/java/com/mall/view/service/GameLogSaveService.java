package com.mall.view.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.util.Util;

public class GameLogSaveService extends Service{
//    public static final String TAG="com.mall.view.service.GameLogSaveService";
    public GameLogBinder binder=new GameLogBinder();
    private Context context;
    public class GameLogBinder extends Binder{
    	public GameLogSaveService getService(){
    		return GameLogSaveService.this;
    	}
    }
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
	public IBinder onBind(Intent arg0) {
		return binder;
	}
	public void saveGameLog(final String gameType,final String Fraction,final String userId,final String md5Pwd){
		NewWebAPI.getNewInstance().addGameMax("", gameType, Fraction, userId, md5Pwd, new WebRequestCallBack(){
			@Override
			public void success(Object result) {
				if(Util.isNull(result.toString())){
					return;
				}
				JSONObject obj = null;
                 try{ 
                	 obj= JSON.parseObject(result.toString());
				}catch (Exception e) {
					e.printStackTrace();  
					return;
				}
				if(200 != obj.getInteger("code").intValue()){
					return ;
				}else{
//					Util.show("游戏记录保存成功");
				}
			}
			@Override
			public void fail(Throwable e) {
				super.fail(e);
			}
		});
	}
	public Context getContext() {
		return context;
	}         
	public void setContext(Context context) {
		this.context = context;
	}

}
