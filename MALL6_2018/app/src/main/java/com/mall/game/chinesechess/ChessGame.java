/*
XQWLMIDlet.java - Source Code for XiangQi Wizard Light, Part III

XiangQi Wizard Light - a Chinese Chess Program for Java ME
Designed by Morning Yellow, Version: 1.42, Last Modified: May 2010
Copyright (C) 2004-2010 www.xqbase.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.mall.game.chinesechess;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.game.chinesechess.util.Util;
import com.mall.util.UserData;
import com.mall.view.R;
import com.mall.view.service.GameLogSaveService;
import com.mall.view.service.GameLogSaveService.GameLogBinder;


public class ChessGame extends Activity {
	static final String[] SOUND_NAME = { "click", "illegal", "move", "move2",
			"capture", "capture2", "check", "check2", "win", "draw", "loss", };             
	static final int RS_DATA_LEN = 512;
	/**
	 * 0: Status, 0 = Startup Form, 1 = Red To Move, 2 = Black To Move<br>
	 * 16: Player, 0 = Red, 1 = Black (Flipped), 2 = Both<br>
	 * 17: Handicap, 0 = None, 1 = 1 Knight, 2 = 2 Knights, 3 = 9 Pieces<br>
	 * 18: Level, 0 = Beginner, 1 = Amateur, 2 = Expert<br>
	 * 19: Sound Level, 0 = Mute, 5 = Max<br>
	 * 20: Music Level, 0 = Mute, 5 = Max<br>
	 * 256-511: Squares
	 */
	byte[] rsData = new byte[RS_DATA_LEN];
	int moveMode, handicap, level, sound, music;
	private ChessView mChessView;
	private LinearLayout container;
	private ImageView close,xinjun;
	@ViewInject(R.id.chess_result)
	private ImageView chess_result;
	@ViewInject(R.id.qitai_layout)
	private LinearLayout qitaiLayout;
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
		}
	};
	@OnClick(R.id.repeat)
	public void Repeat(View v){
		destroyApp(true);  
		Position.readBookData(ChessGame.this);
		startApp();  
		mChessView.setIsEnd(false);
		mChessView.setResultBitmap(null);
		mChessView.invalidate();
	}
	@OnClick(R.id.exit_game)
	public void ExitGame(View v){
		ChessGame.this.finish();
	}
	public ServiceConnection saveGameLogConnection=new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName arg0) {  
		}
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			System.out.println("------------------游戏记录服务开启------------");
			String userId="";
			String md5Pwd="";
			if(UserData.getUser()!=null){
				userId=UserData.getUser().getUserId();
				md5Pwd=UserData.getUser().getMd5Pwd();             
			}else{  
				userId="匿名玩家";  
			}  
			GameLogSaveService.GameLogBinder service=(GameLogBinder) arg1;
			GameLogSaveService gameLogService=service.getService();
			gameLogService.setContext(ChessGame.this);
			synchronized (this) {
				gameLogService.saveGameLog("5", "0",userId+"",md5Pwd+"");
			}
		}
	};
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChessView=new ChessView(this);
		mChessView.setAct(this);
		mChessView.setHandler(handler);
		setContentView(R.layout.game_chinesechess);  
		container=(LinearLayout) this.findViewById(R.id.container);
		container.addView(mChessView);
		close=(ImageView) this.findViewById(R.id.close);
		xinjun=(ImageView) this.findViewById(R.id.xinjun);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ChessGame.this.finish();
			}
		});  
		xinjun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				destroyApp(true);  
				Position.readBookData(ChessGame.this);
				startApp();  
				mChessView.setIsEnd(false);
				mChessView.setResultBitmap(null);
				mChessView.invalidate();
			}
		});
		Position.readBookData(this);
		startApp();
	}
	private boolean started = false;
	public void startApp() {
		if (started) {
			return;
		}
		started = true;
		for (int i = 0; i < RS_DATA_LEN; i++) {
			rsData[i] = 0;
		}
		rsData[19] = 3;
		rsData[20] = 2;
		moveMode = Util.MIN_MAX(0, rsData[16], 2);
		handicap = Util.MIN_MAX(0, rsData[17], 3);
		level = Util.MIN_MAX(0, rsData[18], 2);
		sound = Util.MIN_MAX(0, rsData[19], 5);
		music = Util.MIN_MAX(0, rsData[20], 5);
		mChessView.load(rsData, handicap, moveMode, level);

	}
	@Override
	protected void onDestroy() {          
		destroyApp(true);             
		mChessView.recycle();
		super.onDestroy();
		ChessGame.this.getApplicationContext().bindService(new Intent(ChessGame.this,GameLogSaveService.class), saveGameLogConnection,Context.BIND_AUTO_CREATE);
	}
	public void destroyApp(boolean unc) {
		rsData[16] = (byte) moveMode;
		rsData[17] = (byte) handicap;
		rsData[18] = (byte) level;
		rsData[19] = (byte) sound;
		rsData[20] = (byte) music;

		started = false;
	}
}