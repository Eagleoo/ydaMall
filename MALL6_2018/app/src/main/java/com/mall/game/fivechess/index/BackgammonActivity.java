package com.mall.game.fivechess.index;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.mall.game.fivechess.ai.EasyAi;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.service.GameLogSaveService;
import com.mall.view.service.GameLogSaveService.GameLogBinder;

public class BackgammonActivity extends Activity {
	public static final int LINE = 15;// 五子棋标准棋盘为15*15格
	private int[][] gridBoard = new int[LINE][LINE];// 棋盘
	private boolean isUsersTurn = true;
	private EasyAi ai;
	private int[][] combo = new int[2][572]; // 记录2位玩家所有可能的连珠数，-1则为永远无法5连珠，0代表电脑，1代表玩家
	private boolean[][][][] table = new boolean[2][15][15][572];
	private Coordinate lastPlayersCoordinate;
	private GameView gameView;
	private LinearLayout container;
	private int star=1;
	public ServiceConnection saveGameLogConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			System.out.println("------------------游戏记录服务开启------------");
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
			gameLogService.setContext(BackgammonActivity.this);
			synchronized (this) {
				gameLogService.saveGameLog("4", "0", userId + "", md5Pwd + "");
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fivechess_game);
		container = (LinearLayout) this.findViewById(R.id.container);
		initGame();
		container.addView(gameView);
		initTop();
	}

	private void initTop() {
		Util.initTop2(this, "五子棋", "重新开始", new OnClickListener() {
			@Override
			public void onClick(View v) {
				BackgammonActivity.this.finish();
			}
		}, new OnClickListener() {
			@Override 
			public void onClick(View arg0) {
				if (gameView != null) {
					ai=new EasyAi();
					calculateWeight();    
					initBlackAndWhite();
					initBoardStatus();  
					if(star==1){
						isUsersTurn=false;
					}else if(star==2){
						isUsersTurn=true;
					}
					Coordinate c = ai.comTurn(null);  
					setPieceIfValid(c.x, c.y, star);
					if(star==1){
						star=2;
					}else if(star==2){ 
						star=1;
					}
				}
			}   
		});    
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			BackgammonActivity.this.finish();
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BackgammonActivity.this.getApplicationContext().bindService(
				new Intent(BackgammonActivity.this,GameLogSaveService.class), saveGameLogConnection,
				Context.BIND_AUTO_CREATE);
	}

	private void initGame() {
		GameView gameView = new GameView(this);       
		this.gameView = gameView;
		this.ai = new EasyAi();
		calculateWeight();
		initBoardStatus();
		if (!IndexActivity.playerFirst) {
			isUsersTurn = false;
			Coordinate c = ai.comTurn(null);
			setPieceIfValid(c.x, c.y, 1);
		}
		IndexActivity.playerFirst = !IndexActivity.playerFirst;
	}
   private void calculateWeight(){
	   int icount = 0;
		// 遍历所有的五连子可能情况的权值
		// 横
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 11; j++) {
				for (int k = 0; k < 5; k++) {
					this.table[1][j + k][i][icount] = true;
					this.table[0][j + k][i][icount] = true;
				}
				icount++;
			}
		// 竖
		for (int i = 0; i < 15; i++)
			for (int j = 0; j < 11; j++) {
				for (int k = 0; k < 5; k++) {
					this.table[1][i][j + k][icount] = true;
					this.table[0][i][j + k][icount] = true;
				}
				icount++;
			}
		// 右斜
		for (int i = 0; i < 11; i++)
			for (int j = 0; j < 11; j++) {
				for (int k = 0; k < 5; k++) {
					this.table[1][j + k][i + k][icount] = true;
					this.table[0][j + k][i + k][icount] = true;
				}
				icount++;
			}
		// 左斜
		for (int i = 0; i < 11; i++)
			for (int j = 14; j >= 4; j--) {
				for (int k = 0; k < 5; k++) {
					this.table[1][j - k][i + k][icount] = true;
					this.table[0][j - k][i + k][icount] = true;
				}
				icount++;
			}
   }
	// 初始化黑子白子上的每个权值上的连子数
	private void initBoardStatus() {
		for (int i = 0; i <= 1; i++)
			for (int j = 0; j < 572; j++)
				this.combo[i][j] = 0;
	}

	// 初始化棋盘黑子白子
	private void initBlackAndWhite() {
		for (int i = 0; i < LINE; i++) {
			for (int j = 0; j < LINE; j++) {
				gridBoard[i][j] = 0;
			}
		}
	}

	public int[][] getGridBoard() {
		return gridBoard;
	}

	public void setPieceIfValid(int selectX, int selectY, int id) {
		if (gridBoard[selectX][selectY] != 0 || haveWinner()) {
			return;
		}
		if (isUsersTurn && id == 2) {
			setPiece(selectX, selectY, id);     
			nextTurn();
		} else if (!isUsersTurn && id == 1) {
			setPiece(selectX, selectY, id);
			nextTurn();
		}
	}

	private boolean haveWinner() {// 检查是否有人五连珠
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 572; j++) {
				if (combo[i][j] == 5) {
					showWinner(i);
					return true;
				}
			}
		}
		return false;
	}

	private void showWinner(int id) {// 显示结束信息
		Builder builder = new AlertDialog.Builder(this);
		String title = "";
		String msg = "";
		switch (id) {
		case 0:// 电脑赢了，玩家输了
			title = this.getResources().getString(R.string.title_lose);
			msg = this.getResources().getString(R.string.text_lose);
			break;
		case 1:// 玩家赢了
			title = this.getResources().getString(R.string.title_victor);
			msg = this.getResources().getString(R.string.text_victor);
			break;
		}
		VoipDialog dialog = new VoipDialog(title, this, "退出棋局", "查看棋局",
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// Intent intent=new
						// Intent(BackgammonActivity.this,Lin_MainFrame.class);
						// intent.putExtra("toTag", "find");
						// BackgammonActivity.this.startActivity(intent);
						BackgammonActivity.this.finish();
					}
				}, new OnClickListener() {
					@Override
					public void onClick(View arg0) {

					}
				});
		dialog.show();
		// builder.setTitle(title);
		// builder.setMessage(msg);
		// builder.setPositiveButton("返回", new DialogInterface.OnClickListener()
		// {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// }
		// });
		// builder.setNegativeButton("查看棋局", null);
		// builder.show();
	}

	private void nextTurn() {// 如没有五连珠则进行下一回合，否则显示胜利方
		if (!haveWinner()) {
			isUsersTurn = !isUsersTurn;
			if (!isUsersTurn) {// 如果这一回合该电脑下子
				Coordinate c = ai.comTurn(lastPlayersCoordinate);
				setPieceIfValid(c.x, c.y, 1);
			}
		}
		gameView.invalidate();
	}

	/*
	 * setPiece方法 前置条件：已经检查过该格放入棋子不会造成冲突，并且已经检查是否轮到该色棋子下 后置条件：将指定棋子放入指定格子内
	 */
	private void setPiece(int x, int y, int id) {
		gridBoard[x][y] = id;
		switch (id) {
		case 1:// 电脑下
			for (int i = 0; i < 572; i++) {
				if (this.table[0][x][y][i] && this.combo[0][i] != -1)
					this.combo[0][i]++; // 给白子的所有五连子可能的加载当前连子数
				if (this.table[1][x][y][i]) {
					this.table[1][x][y][i] = false;
					this.combo[1][i] = -1;
				}
			}
			gameView.select(x, y);
			break;
		case 2:// 玩家下
			lastPlayersCoordinate = new Coordinate(x, y);
			for (int i = 0; i < 572; i++) {
				if (this.table[1][x][y][i] && this.combo[1][i] != -1)
					this.combo[1][i]++; // 给黑子的所有五连子可能的加载当前连子数
				if (this.table[0][x][y][i]) {
					this.table[0][x][y][i] = false;
					this.combo[0][i] = -1;
				}
			}
			if(star==2){
				gameView.select(x, y);
			}
			break;
		}
	}

}