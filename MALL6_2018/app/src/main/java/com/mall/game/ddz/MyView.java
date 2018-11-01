package com.mall.game.ddz;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mall.util.Util;
import com.mall.view.R;

public class MyView extends SurfaceView implements SurfaceHolder.Callback,
        Runnable {
    SurfaceHolder surfaceHolder;
    Canvas canvas;
    Boolean repaint = false;
    Boolean start;
    Thread gameThread, drawThread;
    // 判断当前是否要牌
    int[] flag = new int[3];
    // 屏幕宽度和高度
    int screen_height;
    int screen_width;
    float density = 0;
    // 图片资源
    Bitmap cardBitmap[] = new Bitmap[54];
    Bitmap bgBitmap; // 背面
    Bitmap cardBgBitmap;// 图片背面
    Bitmap dizhuBitmap;// 地主图标
    // 基本参数
    int cardWidth, cardHeight;
    // 画笔
    Paint paint;
    // 牌对象
    Card card[] = new Card[54];
    // 按钮
    String buttonText[] = new String[2];
    // 提示
    String message[] = new String[3];
    boolean hideButton = true;
    // List
    List<Card> playerList[] = new Vector[3];
    // 地主牌
    List<Card> dizhuList = new Vector<Card>();
    // 谁是地主
    int dizhuFlag = -1;
    // 轮流
    int turn = -1;
    // 已出牌表
    List<Card> outList[] = new Vector[3];
    Handler handler;
    private Context c;

    // 构造函数
    public MyView(Context context, Handler handler) {
        super(context);
        Common.view = this;
        c = context;
        this.handler = handler;
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
    }

    public void recycle() {
        if (cardBgBitmap != null) {
            cardBgBitmap.recycle();
        }
        if (dizhuBitmap != null) {
            dizhuBitmap.recycle();
        }
        if (bgBitmap != null) {
            bgBitmap.recycle();
        }
        for (int i = 0; i < cardBitmap.length; i++) {
            if (cardBitmap[i] != null) {
                cardBitmap[i].recycle();
            }
        }
    }

    private Map<String, SoftReference<Bitmap>> map = new HashMap<String, SoftReference<Bitmap>>();

    public Bitmap getCacheBitmap(String id) {
        SoftReference<Bitmap> cache = null;
        if (map.containsKey(id)) {
            cache = map.get(id);
        } else {
            cache = new SoftReference<Bitmap>(BitmapFactory.decodeResource(getResources(), Integer.parseInt(id)));
            map.put(id, cache);
        }
        return cache.get();
    }

    // 初始化图片,参数
    public void InitBitMap() {
        for (int i = 0; i < 3; i++)
            flag[i] = 0;
        dizhuFlag = -1;
        turn = -1;
        int count = 0;
        int divide_number = (int) (6 * density);
        int pw = Util.dpToPx(c, (screen_width - 20) / divide_number);
        int ph = (int) (Util.dpToPx(c, screen_width / divide_number) * 1.2);
        for (int i = 1; i <= 4; i++) {
            for (int j = 3; j <= 15; j++) {
                // 根据名字找出ID
                String name = "a" + i + "_" + j;
                ApplicationInfo appInfo = getContext().getApplicationInfo();
                int id = getResources().getIdentifier(name, "drawable", appInfo.packageName);
                if (cardBitmap[count] != null) {
                    cardBitmap[count].recycle();
                }
                cardBitmap[count] = Bitmap.createScaledBitmap(getCacheBitmap(id + ""), pw, ph, true);
                card[count] = new Card(cardBitmap[count].getWidth(), cardBitmap[count].getHeight(), cardBitmap[count]);
                // 设置Card的名字
                card[count].setName(name);
                count++;
            }
        }
        // 最后小王，大王
        if (null != cardBitmap[52]) {
            cardBitmap[52].recycle();
        }
        cardBitmap[52] = Bitmap.createScaledBitmap(getCacheBitmap((R.drawable.a5_16) + ""), pw, ph, true);
        card[52] = new Card(cardBitmap[52].getWidth(), cardBitmap[52].getHeight(), cardBitmap[52]);
        card[52].setName("a5_16");
        if (null != cardBitmap[53]) {
            cardBitmap[53].recycle();
        }
        cardBitmap[53] = Bitmap.createScaledBitmap(getCacheBitmap((R.drawable.a5_17) + ""), pw, ph, true);
        card[53] = new Card(cardBitmap[53].getWidth(), cardBitmap[53].getHeight(), cardBitmap[53]);
        card[53].setName("a5_17");
        Bitmap bd = Util.zoomBitmap(getCacheBitmap((R.drawable.a5_16) + ""), screen_width / 23, (int) (Util.dpToPx(c, screen_width / divide_number) * 1.2));
        cardWidth = bd.getWidth();
        cardHeight = bd.getHeight();

        // 地主图标
        dizhuBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dizhu);
        // 背景
        if (null != bgBitmap) {
            bgBitmap.recycle();
        }
        bgBitmap = getCacheBitmap(R.drawable.game_ddz_bg + "");
        cardBgBitmap = getCacheBitmap(R.drawable.cardbg1 + "");
        // 按钮
        for (int i = 0; i < 2; i++) {
            buttonText[i] = new String();
        }
        buttonText[0] = "抢地主";
        buttonText[1] = "不抢";
        // 消息,已出牌
        for (int i = 0; i < 3; i++) {
            message[i] = new String("");
            outList[i] = new Vector<Card>();
        }

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(cardWidth * 2 / 3);
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(1.0f);
        paint.setTextAlign(Align.CENTER);
        System.gc();
    }

    public void setDensity(float density) {
        this.density = density;
    }

    // 画背景
    public void drawBackground() {
        Rect src = new Rect(0, 0, bgBitmap.getWidth(), bgBitmap.getHeight());
        Rect dst = new Rect(0, 0, screen_width, screen_height);
        canvas.drawBitmap(bgBitmap, src, dst, null);
    }

    // 玩家牌
    public void drawPlayer(int player) {
        if (playerList[player] != null && playerList[player].size() > 0) {
            for (Card card : playerList[player])
                drawCard(card);
        }
    }

    // 画牌
    public void drawCard(Card card) {
        Bitmap tempbitBitmap;
        if (card.rear)// 底牌
            tempbitBitmap = cardBgBitmap;
        else {
            tempbitBitmap = card.bitmap;
        }
        // canvas.drawBitmap(tempbitBitmap, card.getDST().left-30,
        // card.getDST().top, paint);
        canvas.drawBitmap(tempbitBitmap, card.getSRC(), card.getDST(), null);
    }

    // 洗牌
    public void washCards() {
        // 打乱顺序
        for (int i = 0; i < 100; i++) {
            Random random = new Random();
            int a = random.nextInt(54);
            int b = random.nextInt(54);
            Card k = card[a];
            card[a] = card[b];
            card[b] = k;
        }
    }

    // 发牌
    public void handCards() {
        // 开始发牌
        int t = 0;
        for (int i = 0; i < 3; i++) {
            playerList[i] = new Vector<Card>();
        }
        for (int i = 0; i < 54; i++) {
            if (i > 50)// 地主牌
            {
                // 放置地主牌
                card[i].setLocation(screen_width * 2 / 5 + (i % 50 - 3 / 2) * cardWidth, 4);
                dizhuList.add(card[i]);
                update();
                continue;
            }
            switch ((t++) % 3) {
                case 0:
                    // 左边玩家
                    card[i].setLocation(cardWidth / 2, cardHeight / 2 + i * cardHeight / 21);
                    playerList[0].add(card[i]);
                    break;
                case 1:
                    // 我
                    // (10-1/3) (10-4/3) 10
                    card[i].setLocation(screen_width / 11 + (i * cardWidth / 3 + 15), screen_height - cardHeight);
                    card[i].rear = false;// 翻开
                    card[i].setHuman(true);
                    playerList[1].add(card[i]);
                    break;
                case 2:
                    // 右边玩家
                    int x = screen_width - cardWidth * 3 / 2 ;
                    card[i].setLocation((int) (x), cardHeight / 2 + i * cardHeight / 21);
                    playerList[2].add(card[i]);
                    break;
            }
            update();
            Sleep(80);
        }
        // 重新排序
        for (int i = 0; i < 3; i++) {
            Common.setOrder(playerList[i]);
            Common.rePosition(this, playerList[i], i);
        }
        // 打开按钮
        hideButton = false;
        update();
    }

    // sleep();
    public void Sleep(long i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 按钮(抢地主，不抢，出牌，不抢)
    public void drawButton() {
        if (!hideButton) {
            int t = screen_height / 2 - cardHeight / 4;
            int b = screen_height / 2 + cardHeight / 4;
            int dstance = (b - t) / 2;
            canvas.drawText(buttonText[0], screen_width / 2 - 2 * cardWidth, t + dstance, paint);
            canvas.drawText(buttonText[1], screen_width / 2 + 2 * cardWidth, t + dstance, paint);
            canvas.drawRect(new RectF(screen_width / 2 - 3 * cardWidth, t, screen_width / 2 - cardWidth, b), paint);
            canvas.drawRect(new RectF(screen_width / 2 + cardWidth, t, screen_width / 2 + 3 * cardWidth, b), paint);
        }

    }

    // Message
    public void drawMessage() {
        if (!message[1].equals("")) {
            canvas.drawText(message[1], screen_width / 2, screen_height / 2, paint);
        }
        if (!message[0].equals("")) {
            canvas.drawText(message[0], cardWidth * 3, screen_height / 4, paint);
        }
        if (!message[2].equals("")) {
            canvas.drawText(message[2], screen_width - cardWidth * 3,
                    screen_height / 4, paint);
        }
    }

    // 下一个玩家
    public void nextTurn() {
        turn = (turn + 1) % 3;
    }

    // 画地主头像
    public void drawDizhuIcon() {
        if (dizhuFlag >= 0) {
            float x = 0f, y = 0f;
            if (dizhuFlag == 0) {
                x = cardWidth / 2f;
                y = dizhuBitmap.getHeight() / 2;
            }
            if (dizhuFlag == 1) {
                x = cardWidth;
                y = screen_height - cardHeight - dizhuBitmap.getHeight();
            }
            if (dizhuFlag == 2) {
                x = screen_width - cardWidth / 2f - dizhuBitmap.getWidth();
                y = dizhuBitmap.getHeight() / 2;
            }
            canvas.drawBitmap(dizhuBitmap, x, y, null);
        }
    }

    // 画已走的牌
    public void drawOutList() {
        int x = 0, y = 0;
        for (int i = 0, len = outList[1].size(); i < len; i++) {
            Bitmap b = Util.zoomBitmap(outList[1].get(i).bitmap, Util.dpToPx(c, 40), Util.dpToPx(c, 48));
            x = screen_width / 2 + (i - len / 2) * cardWidth / 3;
            y = screen_height - 3 * cardHeight / 2;
            canvas.drawBitmap(b, x, y, null);
        }
        for (int i = 0, len = outList[0].size(); i < len; i++) {
            Bitmap b = Util.zoomBitmap(outList[0].get(i).bitmap, Util.dpToPx(c, 40), Util.dpToPx(c, 48));
            x = 3 * cardWidth;
            y = screen_height / 2 + i * cardHeight / 8 - len * cardHeight / 20 - cardHeight * 3 / 4;
            canvas.drawBitmap(b, x, y, null);
        }
        for (int i = 0, len = outList[2].size(); i < len; i++) {
            Bitmap b = Util.zoomBitmap(outList[2].get(i).bitmap, Util.dpToPx(c, 40), Util.dpToPx(c, 48));
            x = screen_width - cardWidth * 4;
            y = screen_height / 2 + i * cardHeight / 8 - len * cardHeight / 20 - cardHeight * 3 / 4;
//			y = screen_height / 2 + (i - len / 5 - 7) * cardHeight /4+cardHeight;
            //y=i*cardHeight/4-len*cardHeight/5-cardHeight*0.75
            canvas.drawBitmap(b, x, y, null);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        start = true;
//		screen_width=getWidth();
//		screen_height=getHeight();
        // 初始化
        InitBitMap();
        // 洗牌
        washCards();
        // 开始游戏进程
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 开始发牌
                handCards();
                // 等待地主选完
                while (start) {
                    switch (turn) {
                        case 0:
                            player0();
                            break;
                        case 1:
                            player1();
                            break;
                        case 2:
                            player2();
                            break;
                        default:
                            break;
                    }
                    win();
                }
            }
        });
        gameThread.start();
        // 开始绘图进程
        drawThread = new Thread(this);
        drawThread.start();
    }

    // player0
    public void player0() {
        // Log.i("mylog", "玩家0");
        List<Card> player0 = null;
        Common.currentFlag = 0;
        if (flag[1] == 0 && flag[2] == 0) {
            player0 = Common.getBestAI(playerList[0], null);
        } else if (flag[2] == 0) {
            Common.oppoerFlag = 1;
            player0 = Common.getBestAI(playerList[0], outList[1]);
        } else {
            Common.oppoerFlag = 2;
            player0 = Common.getBestAI(playerList[0], outList[2]);
        }
        message[0] = "";
        outList[0].clear();
        setTimer(3, 0);
        if (player0 != null) {
            outList[0].addAll(player0);
            playerList[0].removeAll(player0);
            Common.rePosition(this, playerList[0], 0);
            message[0] = "";
            flag[0] = 1;
        } else {
            message[0] = "不要";
            flag[0] = 0;
        }
        update();
        nextTurn();
    }

    // player2
    public void player2() {
        // Log.i("mylog", "玩家2");
        Common.currentFlag = 2;
        List<Card> player2 = null;
        if (flag[1] == 0 && flag[0] == 0) {
            player2 = Common.getBestAI(playerList[2], null);
        } else if (flag[1] == 0) {
            player2 = Common.getBestAI(playerList[2], outList[0]);
            Common.oppoerFlag = 0;
        } else {
            player2 = Common.getBestAI(playerList[2], outList[1]);
            Common.oppoerFlag = 1;
        }
        message[2] = "";
        outList[2].clear();
        setTimer(3, 2);
        if (player2 != null) {
            outList[2].addAll(player2);
            playerList[2].removeAll(player2);
            Common.rePosition(this, playerList[2], 2);
            message[2] = "";
            flag[2] = 1;
        } else {
            message[2] = "不要";
            flag[2] = 0;
        }
        update();
        nextTurn();
    }

    // player1
    public void player1() {
        Sleep(1000);
        // 开始写出牌的了
        buttonText[0] = "出牌";
        buttonText[1] = "不要";
        hideButton = false;
        outList[1].clear();
        update();
        // 倒计时
        int i = 28;
        while ((turn == 1) && (i-- > 0)) {
            // 计时器函数draw timer.画出计时画面
            message[1] = i + "";
            update();
            Sleep(1000);
        }
        hideButton = true;
        update();
        if (turn == 1 && i <= 0)// 说明用户没有任何操作
        {
            // 自动不要，或者选一张随便出
            message[1] = "不要";
            flag[1] = 0;
            nextTurn();
        }
        update();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        start = false;
    }

    public int getScreen_height() {
        return screen_height;
    }

    public void setScreen_height(int screen_height) {
        this.screen_height = screen_height;
    }

    public int getScreen_width() {
        return screen_width;
    }

    public void setScreen_width(int screen_width) {
        this.screen_width = screen_width;
    }

    // 主要绘图线程
    @SuppressLint("WrongCall")
    @Override
    public void run() {
        while (start) {
            if (repaint) {
                onDraw();
                repaint = false;
                Sleep(33);
            }
        }
    }

    // 画图函数
    public void onDraw() {
        // 枷锁
        synchronized (surfaceHolder) {
            try {
                canvas = surfaceHolder.lockCanvas();
                // 画背景
                drawBackground();
                // 画牌
                for (int i = 0; i < 3; i++)
                    drawPlayer(i);
                // 地主牌
                for (int i = 0, len = dizhuList.size(); i < len; i++)
                    drawCard(dizhuList.get(i));
                // 画按钮( 抢地主,不抢,出牌,不出)
                drawButton();
                // message部分 用3个String存
                drawMessage();
                // 画地主图标
                drawDizhuIcon();
                // 出牌界面(3个地方,用3个vector存)
                drawOutList();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    // 更新函数
    public void update() {
        repaint = true;
    }

    // 触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 只接受按下事件
        if (event.getAction() != MotionEvent.ACTION_UP)
            return true;
        // 点选牌
        EventAction eventAction = new EventAction(this, event);
        Card card = eventAction.getCard();
        if (card != null) {
            Log.i("mylog", card.name);
            if (card.clicked)
                card.y += card.height / 3;
            else
                card.y -= card.height / 3;
            card.clicked = !card.clicked;
            update();// 重绘
        }
        // 按钮事件
        eventAction.getButton();
        return true;
    }

    // 计时器
    public void setTimer(int t, int flag) {
        while (t-- > 0) {
            Sleep(1000);
            message[flag] = t + "";
            update();
        }
        message[flag] = "";
    }

    // 判断成功
    public void win() {
        int flag = -1;
        if (playerList[0].size() == 0)
            flag = 0;
        // 我先出完
        if (playerList[1].size() == 0)
            flag = 1;
        if (playerList[2].size() == 0)
            flag = 2;
        if (flag > -1) {
            for (int i = 0; i < playerList[0].size(); i++) {
                playerList[0].get(i).bitmap = Util.zoomBitmap(card[i].bitmap, Util.dpToPx(c, 40), Util.dpToPx(c, 48));
                playerList[0].get(i).rear = true;
            }
            for (int i = 0; i < playerList[2].size(); i++) {
                playerList[2].get(i).bitmap = Util.zoomBitmap(card[i].bitmap, Util.dpToPx(c, 40), Util.dpToPx(c, 48));
                playerList[2].get(i).rear = true;
            }
            for (int i = 0; i < playerList[1].size(); i++) {
                playerList[1].get(i).rear = false;
            }
            update();
            start = false;
            Message msg = new Message();
            msg.what = 0;
            Bundle builder = new Bundle();
            if (flag == 1)
                builder.putString("data", "恭喜你赢了");
            // 地主先出完
            if (flag == dizhuFlag && flag != 1)
                builder.putString("data", "对不起你输了。");
            if (flag != dizhuFlag && dizhuFlag == 1) {
                builder.putString("data", "对不起你输了。");
            }
            if (flag != dizhuFlag && dizhuFlag != 1) {
                builder.putString("data", "恭喜你赢了");
            }
            msg.setData(builder);
            handler.sendMessage(msg);

        }

    }

}
