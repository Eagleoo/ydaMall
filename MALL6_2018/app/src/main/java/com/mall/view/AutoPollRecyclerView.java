package com.mall.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.mall.serving.community.util.Util;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/4/4.
 */

public class AutoPollRecyclerView extends RecyclerView {

    private boolean isStop = false;

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    private long TIME_AUTO_POLL = 30;
    AutoPollTask autoPollTask;
    private boolean running; //标示是否正在自动轮询
    private boolean canRun;//标示是否可以自动轮询,可在不需要的是否置false

    public AutoPollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        autoPollTask = new AutoPollTask(this);
    }

    static class AutoPollTask implements Runnable {
        private final WeakReference<AutoPollRecyclerView> mReference;

        //使用弱引用持有外部类引用->防止内存泄漏
        public AutoPollTask(AutoPollRecyclerView reference) {
            this.mReference = new WeakReference<AutoPollRecyclerView>(reference);
        }


        @Override
        public void run() {
            AutoPollRecyclerView recyclerView = mReference.get();
//            Log.e("是否是", "recyclerView.running" + recyclerView.running+" recyclerView.canRun"+ recyclerView.canRun);
            if (recyclerView != null && recyclerView.running && recyclerView.canRun) {
//                Log.e("是否是", "isVisBottom" + isVisBottom(recyclerView));
                if (isVisBottom(recyclerView)) {
                    recyclerView.smoothScrollToPosition(0);
                    recyclerView.start();
                } else {

                    if (recyclerView.isStop()) {
                        recyclerView.TIME_AUTO_POLL = 1000L;
                        try {
                            recyclerView.scrollBy(2, Util.dpToPx(30) - 8);
                        } catch (Exception e) {
                            Log.e("滚动异常", "1");

                        }
                    } else {
                        recyclerView.scrollBy(2, 2);


                    }
//                    if (recyclerView.isStop()) {
//                        recyclerView.scrollBy(2, 20);
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        recyclerView.scrollBy(2, 2);
//                    }


                    recyclerView.postDelayed(recyclerView.autoPollTask, recyclerView.TIME_AUTO_POLL);
                }

            }
        }
    }

    //开启:如果正在运行,先停止->再开启
    public void start() {
        if (running)
            stop();
        canRun = true;
        running = true;
        postDelayed(autoPollTask, TIME_AUTO_POLL);

    }

    public void stop() {
        running = false;
        removeCallbacks(autoPollTask);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (running)
                    stop();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (canRun)
                    start();
                break;
        }
        return super.onTouchEvent(e);
    }

    public static boolean isVisBottom(AutoPollRecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE) {
            return true;
        } else {
            return false;
        }
    }
}
