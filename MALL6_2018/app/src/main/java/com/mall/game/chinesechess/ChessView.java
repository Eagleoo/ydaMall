package com.mall.game.chinesechess;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.mall.game.chinesechess.util.Util;
import com.mall.view.R;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

class ChessView extends View {

	private static final int PHASE_LOADING = 0;
	private static final int PHASE_WAITING = 1;
	private static final int PHASE_THINKING = 2;
	private static final int PHASE_EXITTING = 3;

	private static final int COMPUTER_BLACK = 0;
	private static final int COMPUTER_RED = 1;
	private static final int COMPUTER_NONE = 2;

	private static final int RESP_HUMAN_SINGLE = -2;
	private static final int RESP_HUMAN_BOTH = -1;
	private static final int RESP_CLICK = 0;
	private static final int RESP_ILLEGAL = 1;
	private static final int RESP_MOVE = 2;
	private static final int RESP_MOVE2 = 3;
	private static final int RESP_CAPTURE = 4;
	private static final int RESP_CAPTURE2 = 5;
	private static final int RESP_CHECK = 6;
	private static final int RESP_CHECK2 = 7;
	private static final int RESP_WIN = 8;
	private static final int RESP_DRAW = 9;
	private static final int RESP_LOSS = 10;

	private Bitmap imgBackground, imgXQWLight/* ,imgThinking */;
	private static final String[] IMAGE_NAME = { null, null, null, null, null,
			null, null, null, "rk", "ra", "rb", "rn", "rr", "rc", "rp", null,
			"bk", "ba", "bb", "bn", "br", "bc", "bp", null, };
	private int widthBackground, heightBackground;

	static final int RS_DATA_LEN = 512;

	byte[] rsData = new byte[RS_DATA_LEN];

	byte[] retractData = new byte[RS_DATA_LEN];

	private Position pos = new Position();
	private Search search = new Search(pos, 12);
	private String message;
	private int cursorX, cursorY;
	private int sqSelected, mvLast;
	// Assume FullScreenMode = false
	private int normalWidth = getWidth();
	private int normalHeight = getHeight();
	volatile int phase = PHASE_LOADING;
	int moveMode, level;
	private boolean init = false;
	private Bitmap imgBoard, imgSelected, imgSelected2, imgCursor, imgCursor2,result,qitai;
	private Bitmap repeat_game,exit_game;
	private Bitmap[] imgPieces = new Bitmap[24];
	private int squareSize, width, height, left, right, top, bottom;
	private Context context;
	private Paint paint = new Paint();
	private Activity act;
	private boolean isend=false;
	private float repeat_left,repeat_top,exit_left,exit_top;
	private Handler handler;
	public ChessView(Context context) {
		super(context);
		isend=false;
		imgBackground = BitmapFactory.decodeResource(getResources(),
				R.drawable.background);
		imgXQWLight = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_arrow);
		// imgThinking = BitmapFactory.decodeResource(getResources(),
		// R.drawable.thinking);
		widthBackground = imgBackground.getWidth();
		heightBackground = imgBackground.getHeight();
		this.context = context;  
	}
	public void recycle(){
		imgBoard.recycle();  
		imgSelected.recycle();
		imgSelected2.recycle();
		imgCursor.recycle();
		if(result!=null){
			result.recycle(); 
		}
		if(qitai!=null){
			qitai.recycle();
		}
		for(int i=0;i<imgPieces.length;i++){
			if(imgPieces[i]!=null){
				imgPieces[i].recycle();
			}
		}
		if(repeat_game!=null){
			repeat_game.recycle(); 
		}                        
		if(exit_game!=null){
			exit_game.recycle();  
		}
	}          
	public void setHandler(Handler handler){
		this.handler=handler;
	}
	public void setAct(Activity act){
		this.act=act;
	}
	public void setIsEnd(boolean isend){
		this.isend=isend;
	}
	public void setResultBitmap(Bitmap n){
		this.result=n;
	}
	void load(byte rsData[], int handicap, int moveMode, int level) {
		this.moveMode = moveMode;
		this.level = level;  
		this.rsData = rsData;
		cursorX = cursorY = 7;
		sqSelected = mvLast = 0;   
		if (rsData[0] == 0) {
			pos.fromFen(Position.STARTUP_FEN[handicap]);
		} else {
			// Restore Record-Score Data
			pos.clearBoard();
			for (int sq = 0; sq < 256; sq++) {
				int pc = rsData[sq + 256];
				if (pc > 0) {
					pos.addPiece(sq, pc);
				}
			}
			if (rsData[0] == 2) {
				pos.changeSide();
			}
			pos.setIrrev();
		}
		// Backup Retract Status
		System.arraycopy(rsData, 0, retractData, 0, RS_DATA_LEN);
		// Call "responseMove()" if Computer Moves First
		phase = PHASE_LOADING;
		if (pos.sdPlayer == 0 ? moveMode == COMPUTER_RED
				: moveMode == COMPUTER_BLACK) {
			new Thread() {
				public void run() {
					while (phase == PHASE_LOADING) {
						try {
							sleep(100);
						} catch (InterruptedException e) {
							// Ignored
						}
					}      
					responseMove();
				}
			}.start();
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {
		drawChess(canvas);
		super.onDraw(canvas);
	}
	public static Bitmap readBitMap(Context context, int resId){  
		   BitmapFactory.Options opt = new BitmapFactory.Options();  
		   opt.inPreferredConfig = Bitmap.Config.RGB_565;  
		   opt.inPurgeable = true;  
		   opt.inInputShareable = true;  
		   opt.inJustDecodeBounds = false;
		   opt.inSampleSize = 4;
		   // 获取资源图片  
		   InputStream is = context.getResources().openRawResource(resId);  
		   return BitmapFactory.decodeStream(is,null,opt);  
		}
	protected void drawChess(Canvas canvas) {
		if (phase == PHASE_LOADING) {
			// 如果状态是第一次加载象棋
			// Wait 1 second for resizing
			width = getWidth();
			height = getHeight();
			for (int i = 0; i < 10; i++) {
				// 如果不是设置的全屏模式，则不绘制界面
				if (width != normalWidth || height != normalHeight) {
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				width = getWidth();  
				height = getHeight();
			}
			if (!init) {
				init = true;
				squareSize = Math.min(width / 9, height / 10);// 计算每个棋子方格的大小

				int boardWidth = squareSize * 9;
				int boardHeight = squareSize * 10;
				try {
					imgBoard = readBitMap(context,R.drawable.board);
					imgSelected = BitmapFactory.decodeResource(getResources(),    
							R.drawable.game_selected);
					imgSelected2 = BitmapFactory.decodeResource(getResources(),
							R.drawable.game_selected2);
					imgCursor = BitmapFactory.decodeResource(getResources(),
							R.drawable.cursor);
					imgCursor2 = BitmapFactory.decodeResource(getResources(),
							R.drawable.cursor2);
					for (int pc = 0; pc < 24; pc++) {
						if (IMAGE_NAME[pc] == null) {
							imgPieces[pc] = null;
						} else {
							imgPieces[pc] = readBitMap(context,getResources().getIdentifier("" + IMAGE_NAME[pc], "drawable",context.getPackageName()));
						}
					}
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				} 
				left = (width - boardWidth) / 2;
				top = (height - boardHeight) / 2;
				right = left + boardWidth;
				bottom = top + boardHeight ;
			}  
			phase = PHASE_WAITING;     
		} 
		for (int x = 0; x < width; x += widthBackground) { 
			for (int y = 0; y < height; y += heightBackground) {
				canvas.drawBitmap(imgBackground, width/2, height/2, paint);
			}       
		}     
		Bitmap mBitmap = Bitmap.createScaledBitmap(imgBoard, right - left-20, bottom - top-30, false); 
		canvas.drawBitmap(mBitmap, 10, top+15, paint);  
		for (int sq = 0; sq < 256; sq++) {
			if (Position.IN_BOARD(sq)) {
				int pc = pos.squares[sq];
				if (pc > 0) {
					drawSquare(canvas, imgPieces[pc], sq);
				}
			}
		}
		int sqSrc = 0;
		int sqDst = 0;
		if (mvLast > 0) {
			sqSrc = Position.SRC(mvLast);
			sqDst = Position.DST(mvLast);
			drawSquare(canvas, (pos.squares[sqSrc] & 8) == 0 ? imgSelected
					: imgSelected2, sqSrc);
			drawSquare(canvas, (pos.squares[sqDst] & 8) == 0 ? imgSelected
					: imgSelected2, sqDst);
		} else if (sqSelected > 0) {
			drawSquare(canvas, (pos.squares[sqSelected] & 8) == 0 ? imgSelected
					: imgSelected2, sqSelected);
		}
		int sq = Position.COORD_XY(cursorX + Position.FILE_LEFT, cursorY
				+ Position.RANK_TOP);
		if (moveMode == COMPUTER_RED) {
			sq = Position.SQUARE_FLIP(sq);
		}
		if (sq == sqSrc || sq == sqDst || sq == sqSelected) {
			drawSquare(canvas, (pos.squares[sq] & 8) == 0 ? imgCursor2
					: imgCursor, sq);
		} else {
			drawSquare(canvas, (pos.squares[sq] & 8) == 0 ? imgCursor
					: imgCursor2, sq);
		}
		if (phase == PHASE_THINKING) {
			int x, y;
			if (moveMode == COMPUTER_RED) {
				x = (Position.FILE_X(sqDst) < 8 ? left : right);
				y = (Position.RANK_Y(sqDst) < 8 ? top : bottom);
			} else {
				x = (Position.FILE_X(sqDst) < 8 ? right : left);
				y = (Position.RANK_Y(sqDst) < 8 ? bottom : top);
			}
			// canvas.drawBitmap(imgThinking, x, y, paint);
		} else if (phase == PHASE_EXITTING) {
		} 
		
		if(result!=null&&qitai!=null&&repeat_game!=null&&exit_game!=null){
			canvas.drawBitmap(qitai, (width-qitai.getWidth())/2, (height-qitai.getHeight())/2, paint); 
			canvas.drawBitmap(result, (width-result.getWidth())/2, (height-qitai.getHeight())/2+result.getHeight(), paint);  //result居中显示
			repeat_left=(width-repeat_game.getWidth()-exit_game.getWidth()-30)/2;
			repeat_top=height/2+50;
			exit_left=repeat_left+repeat_game.getWidth()+30;
			exit_top=repeat_top;
			canvas.drawBitmap(repeat_game, repeat_left, repeat_top, paint);  
			canvas.drawBitmap(exit_game, exit_left, exit_top, paint);    
		}
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if(!isend){	 
				pointerPressed(event.getX(), event.getY());
			}else{
				float positionX=event.getX();
			    float positionY=event.getY();
			    if(positionX>=repeat_left&&positionX<=repeat_left+repeat_game.getWidth()&&positionY>=repeat_top&&positionY<=repeat_top+repeat_game.getHeight()){
			    	for (int i = 0; i < RS_DATA_LEN; i++) {
						rsData[i] = 0;
					}
					load(rsData, Util.MIN_MAX(0, rsData[17], 3), Util.MIN_MAX(0, rsData[16], 2), Util.MIN_MAX(0, rsData[18], 2));
            		invalidate();
            		result=null;
            		qitai=null;  
            		repeat_game=null;    
            		exit_game=null;
            		isend=false;
			    }
			    if(positionX>=exit_left&&positionX<=exit_left+exit_game.getWidth()&&positionY>=exit_top&&positionY<=exit_top+exit_game.getHeight()){
					act.finish();
			    }
			}
		} 
		return super.onTouchEvent(event);
	}
	protected void pointerPressed(float x, float y) {

		if (phase == PHASE_THINKING) {
			return;
		}
		cursorX = Util.MIN_MAX(0, ((int) x - left) / squareSize, 8);
		cursorY = Util.MIN_MAX(0, ((int) y - top) / squareSize, 9);
		clickSquare();
		invalidate();

	}
	private void clickSquare() {
		playSound(1,100,2,1);
		int sq = Position.COORD_XY(cursorX + Position.FILE_LEFT, cursorY+ Position.RANK_TOP);
		if (moveMode == COMPUTER_RED) {
			sq = Position.SQUARE_FLIP(sq);
		}
		int pc = pos.squares[sq];
		if ((pc & Position.SIDE_TAG(pos.sdPlayer)) != 0) {
			mvLast = 0;
			sqSelected = sq;  
		} else {  
			if (sqSelected > 0 && addMove(Position.MOVE(sqSelected, sq))&& !responseMove()) {
				rsData[0] = 0;
				phase = PHASE_EXITTING;
			} else {
				
			}
		}
	}
    /** 
     * 初始化 
     */  
    private Map<Integer, Integer> initSoundpool(SoundPool sp) {  
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();   
        map.put(1, sp.load(getContext(), R.raw.click, 1));  //一般情况时候的游戏声音
        map.put(2, sp.load(getContext(), R.raw.jiang, 1)); 
        map.put(3, sp.load(getContext(), R.raw.win, 1)); 
        map.put(4, sp.load(getContext(), R.raw.loss, 1));  
        return map;
    }  
    /**
     * 
     * @param type ---声音类型---【0.背景音乐 1.一般点击音乐2.非法路径音乐 3.将军音乐】
     * @param sound
     * @param number
     * @param repeat
     */
    private void playSound(final int type,int sound, int number,final int repeat) { 
    	switch (type) {
		case 1:
			break;
		default:
			break;
		}
    	final SoundPool sp=new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    	final int sound1=sp.load(getContext(), R.raw.click, 1);
    	final Map<Integer, Integer> map = initSoundpool(sp);
        new Handler().postDelayed(new Runnable() {
			public void run() {
				sp.play(map.get(type), 100, 100, 1, repeat, 1);
			}
		}, 20);
    }  
	private void drawSquare(Canvas canvas, Bitmap bitmap, int sq) {
		int sqFlipped = (moveMode == COMPUTER_RED ? Position.SQUARE_FLIP(sq)
				: sq);
		int sqX = left + (Position.FILE_X(sqFlipped) - Position.FILE_LEFT)
				* squareSize;
		int sqY = top + (Position.RANK_Y(sqFlipped) - Position.RANK_TOP)  
				* squareSize;
		if (bitmap != null) {

			Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap, squareSize,
					squareSize, true);
			canvas.drawBitmap(mBitmap, sqX, sqY, paint);
		}

	}
	/** Player Move Result */
	private boolean getResult() {
		return getResult(moveMode == COMPUTER_NONE ? RESP_HUMAN_BOTH: RESP_HUMAN_SINGLE);
	}
	/** Computer Move Result */
	private boolean getResult(int response) {
		if (pos.isMate()) {
			message = (response < 0 ? "胜利" : "失败");
			showMessage(message);
			return true;
		} 
		int vlRep = pos.repStatus(3); 
		if (vlRep > 0) { 
			vlRep = (response < 0 ? pos.repValue(vlRep) : -pos.repValue(vlRep));
			message = (vlRep > Position.WIN_VALUE ? "失败": vlRep < -Position.WIN_VALUE ? "胜利": "打和");
			showMessage(message);
			return true;
		}
		if (pos.moveNum > 100) {
			message = "打和"; 
			showMessage(message);  
			return true;
		}
		if (response != RESP_HUMAN_SINGLE) {
			if (response >= 0) {
			} 
			// Backup Retract Status
			System.arraycopy(rsData, 0, retractData, 0, RS_DATA_LEN);
			// Backup Record-Score Data
			rsData[0] = (byte) (pos.sdPlayer + 1);
			System.arraycopy(pos.squares, 0, rsData, 256, 256);
		}
		return false;
	}
	private void showMessage(String message){
//		final VoipDialog dialog=new VoipDialog(message, getContext(), "再来一局", "确定！", new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				act.finish();
//			}
//		}, new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				for (int i = 0; i < RS_DATA_LEN; i++) { 
//					rsData[i] = 0;
//				}
//				rsData[19] = 3;
//				rsData[20] = 2;
//				int moveMode, handicap, level, sound, music;
//				moveMode = Util.MIN_MAX(0, rsData[16], 2);
//				handicap = Util.MIN_MAX(0, rsData[17], 3);
//				level = Util.MIN_MAX(0, rsData[18], 2);
//				sound = Util.MIN_MAX(0, rsData[19], 5);
//				music = Util.MIN_MAX(0, rsData[20], 5);
//			    load(rsData, handicap, moveMode, level); 
//			}   
//		}); 
//		dialog.show(); 
//		Message msg = handler.obtainMessage();
//		msg.obj="end";
		if(message.equals("胜利")){
			result=BitmapFactory.decodeResource(getResources(),R.drawable.win);
//			msg.what=1;
			playSound(3,100,2,1);
		}else if(message.equals("失败")){
//			msg.what=2;
			playSound(4,100,2,1);
			result=BitmapFactory.decodeResource(getResources(),R.drawable.fail);
		}else if(message.equals("打和")){
//			msg.what=3;
			result=BitmapFactory.decodeResource(getResources(),R.drawable.heqi);
		}
		qitai=BitmapFactory.decodeResource(getResources(), R.drawable.qitai);
		repeat_game=BitmapFactory.decodeResource(getResources(), R.drawable.exit_game);
		exit_game=BitmapFactory.decodeResource(getResources(), R.drawable.repeat_game);
		isend=true; 
		invalidate();
	}
	private boolean addMove(int mv) {
		if (pos.legalMove(mv)) {  
			if (pos.makeMove(mv)) {
				if (pos.captured()) {
					pos.setIrrev();
				}
				sqSelected = 0;
				mvLast = mv;
				return true;
			}
		}
		return false;
	}
	boolean responseMove() {
		if (getResult()) {
			return false;
		}
		if (moveMode == COMPUTER_NONE) {
			return true;
		}
		phase = PHASE_THINKING;
		invalidate();
		mvLast = search.searchMain(1000 << (level << 1));
		pos.makeMove(mvLast);
		int response = pos.inCheck() ? RESP_CHECK2: pos.captured() ? RESP_CAPTURE2 : RESP_MOVE2;
		if (pos.captured()) {
//			playSound(2,100,2,1);   
			System.out.println("-----------------将军----------------");
			pos.setIrrev();
		}
		phase = PHASE_WAITING;
		invalidate();
		return !getResult(response);
	}
	void back() {
		if (phase == PHASE_WAITING) {
		} else {
			rsData[0] = 0;
		}
	}
	void retract(int handicap) {
		// Restore Retract Status
		System.arraycopy(retractData, 0, rsData, 0, RS_DATA_LEN);
		load(rsData, handicap, moveMode, level);
		invalidate();

	}
	void about() {
		phase = PHASE_LOADING;
	}
}