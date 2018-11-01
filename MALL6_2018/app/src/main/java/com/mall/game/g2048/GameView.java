package com.mall.game.g2048;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Point;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mall.model.User;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.service.GameLogSaveService;
import com.mall.view.service.GameLogSaveService.GameLogBinder;

public class GameView extends LinearLayout {
    private final int LINES = 4;
    private Card[][] cardsMap = new Card[LINES][LINES];
    private List<Point> emptyPoints = new ArrayList<Point>();
    private Game2048.Score score;
    private Context c;
    private Activity act;
    private TextView scoreView;
    public ServiceConnection saveGameLogConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            User user;
            if (UserData.getUser() != null) {
                user = UserData.getUser();
            } else {
                user = new User();
            }
            GameLogSaveService.GameLogBinder service = (GameLogBinder) arg1;
            GameLogSaveService gameLogService = service.getService();
            gameLogService.setContext(c);
            gameLogService.saveGameLog("2", score.getNum() + "", user.getUserId() + "", user.getMd5Pwd() + "");
        }
    };

    public void setAct(Activity act) {
        this.act = act;
    }

    public GameView(Context context) {
        super(context);
        c = context;
        initGameView();
    }

    public void setScoreView(TextView scoreView) {
        this.scoreView = scoreView;
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        c = context;
        initGameView();
    }

    private void initGameView() {
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Color.WHITE);
        setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY, offsetX, offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;
                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            if (offsetX < -5) {
                                swipeLeft();
                            } else if (offsetX > 5) {
                                swipeRight();
                            }
                        } else {
                            if (offsetY < -5) {
                                swipeUp();
                            } else if (offsetY > 5) {
                                swipeDown();
                            }
                        }

                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        Card.width = (Math.min(width, height) - 10) / 4;
        addCards();
        startGame();
    }

    private void addCards() {
        Card c;
        LinearLayout line;
        LinearLayout.LayoutParams lineLp;
        for (int y = 0; y < LINES; y++) {
            line = new LinearLayout(getContext());
            lineLp = new LinearLayout.LayoutParams(-1, Card.width);
            addView(line, lineLp);
            for (int x = 0; x < LINES; x++) {
                c = new Card(getContext());
                line.addView(c, Card.width, Card.width);
                c.requestLayout();//兼容7.0以上不显示问题
                cardsMap[x][y] = c;
            }
        }
    }

    public void startGame() {
        for (int y = 0; y < LINES; y++) {
            for (int x = 0; x < LINES; x++) {
                cardsMap[x][y].setNum(0);
            }
        }
        scoreView.setText("");
        score.clearScore();
        addRandomNum();
        addRandomNum();
    }

    private void addRandomNum() {
        emptyPoints.clear();
        // calculate how many empty points
        for (int y = 0; y < LINES; y++) {
            for (int x = 0; x < LINES; x++) {
                if (cardsMap[x][y].getNum() <= 0) {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }

        if (emptyPoints.size() > 0) {

            Point p = emptyPoints.remove((int) (Math.random() * emptyPoints
                    .size()));
            cardsMap[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);
            cardsMap[p.x][p.y].addScaleAnimation();
        }
    }

    private void swipeLeft() {
        boolean merge = false;
        for (int y = 0; y < LINES; y++) {
            for (int x = 0; x < LINES; x++) {

                for (int x1 = x + 1; x1 < LINES; x1++) {
                    if (cardsMap[x1][y].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);

                            x--;
                            merge = true;

                        } else if (cardsMap[x][y].equals(cardsMap[x1][y])) {

                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x1][y].setNum(0);

                            score.addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

    private void swipeRight() {
        boolean merge = false;
        for (int y = 0; y < LINES; y++) {
            for (int x = LINES - 1; x >= 0; x--) {
                for (int x1 = x - 1; x1 >= 0; x1--) {
                    if (cardsMap[x1][y].getNum() > 0) {
                        if (cardsMap[x][y].getNum() <= 0) {
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);
                            x++;
                            merge = true;
                        } else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x1][y].setNum(0);
                            score.addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

    private void swipeUp() {
        boolean merge = false;
        for (int x = 0; x < LINES; x++) {
            for (int y = 0; y < LINES; y++) {
                for (int y1 = y + 1; y1 < LINES; y1++) {
                    if (cardsMap[x][y1].getNum() > 0) {
                        if (cardsMap[x][y].getNum() <= 0) {
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);
                            y--;
                            merge = true;
                        } else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x][y1].setNum(0);
                            score.addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }
                        break;
                    }
                }
            }
        }
        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

    private void swipeDown() {
        boolean merge = false;
        for (int x = 0; x < LINES; x++) {
            for (int y = LINES - 1; y >= 0; y--) {
                for (int y1 = y - 1; y1 >= 0; y1--) {
                    if (cardsMap[x][y1].getNum() > 0) {
                        if (cardsMap[x][y].getNum() <= 0) {
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);
                            y++;
                            merge = true;
                        } else if (cardsMap[x][y].equals(cardsMap[x][y1])) {

                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x][y1].setNum(0);
                            score.addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

    private void checkComplete() {
        boolean complete = true;
        ALL:
        for (int y = 0; y < LINES; y++) {
            for (int x = 0; x < LINES; x++) {
                if (cardsMap[x][y].getNum() == 0
                        || (x > 0 && cardsMap[x][y].equals(cardsMap[x - 1][y]))
                        || (x < LINES - 1 && cardsMap[x][y]
                        .equals(cardsMap[x + 1][y]))
                        || (y > 0 && cardsMap[x][y].equals(cardsMap[x][y - 1]))
                        || (y < LINES - 1 && cardsMap[x][y]
                        .equals(cardsMap[x][y + 1]))) {

                    complete = false;
                    break ALL;
                }
            }
        }
        if (complete) {
            c.getApplicationContext().bindService(new Intent(c, GameLogSaveService.class), saveGameLogConnection, Context.BIND_AUTO_CREATE);
            String tile = "";
            if (!Util.isNull(score.getNum()) && score.getNum() == 2048) {//如果已经加到2048了则提示
                tile = "恭喜您，您已经完成了游戏任务！";
            } else {
                tile = "哦哦，闯关失败了，是否再来一次？";
            }
            final VoipDialog dialog = new VoipDialog(tile, c, "确定.", "累了，休息会", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startGame();
                }
            }, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    act.finish();
                }
            });
            dialog.show();

        }
    }

    public void setScore(Game2048.Score score) {
        this.score = score;

    }
}
