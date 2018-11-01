package com.mall.game.g2048;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.service.GameLogSaveService;
import com.mall.view.service.GameLogSaveService.GameLogBinder;

public class Game2048 extends Activity {
	private TextView scoreTextView, bestScoreTextView;
	private GameView gameView = null;
	private Score score;       
	public ServiceConnection saveGameLogConnection=new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName arg0) {  
		}
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
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
			gameLogService.setContext(Game2048.this);
			synchronized (this) {
				gameLogService.saveGameLog("2", score.getNum()+"",userId+"",md5Pwd+"");
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game2048);
		scoreTextView = (TextView) findViewById(R.id.score);
		bestScoreTextView = (TextView) findViewById(R.id.bestScore);
		gameView = (GameView) findViewById(R.id.gameView);
		score = new Score();
		gameView.setScore(score);
		gameView.setAct(Game2048.this);   
		gameView.setScoreView(scoreTextView);  
		init();
	}
	private void init() {
		Util.initTop2(this, "2048", "重新开始", new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent=new Intent(Game2048.this,Lin_MainFrame.class);
//				intent.putExtra("toTab", "find"); 
				Game2048.this.finish();
			}
		}, new OnClickListener() {
			@Override
			public void onClick(View view) {
				gameView.startGame();
			}
		});
		showBestScore();  
	}          
	private void startNewGame() {
		score.clearScore();  
		showScore();
		showBestScore();

	}
	private void showBestScore() {
		bestScoreTextView.setText(String.valueOf(score.getBestScore()));
	}
	private void showScore() {
		scoreTextView.setText(String.valueOf(score.getScore()));
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Game2048.this.getApplicationContext().bindService(new Intent(Game2048.this,GameLogSaveService.class), saveGameLogConnection,Context.BIND_AUTO_CREATE);
	}
	class Score {
		private int score = 0;
		private int num = 0;
		public int getNum() {
			return num;
		}
		private static final String SP_KEY_BEST_SCORE = "bestScore";
		public void clearScore() {
			score = 0;
		}
		public int getScore() {
			return score;
		}
		public void addScore(int s) {
			score += s;
			if (s > num) {
				num = s;
			}
			showScore();
			saveBestScore(Math.max(score, getBestScore()));
			showBestScore();
		}

		public void saveBestScore(int s) {
			Editor e = getPreferences(MODE_PRIVATE).edit();
			e.putInt(SP_KEY_BEST_SCORE, s);
			e.commit();
		}  
		public int getBestScore() {
			return getPreferences(MODE_PRIVATE).getInt(SP_KEY_BEST_SCORE, 0);
		}    
	}  
}
