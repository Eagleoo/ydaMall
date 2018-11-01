package com.mall.view.messageboard;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.XQAdapter;
import com.mall.model.User;
import com.mall.model.messageboard.UserMessageBoard;
import com.mall.model.messageboard.UserMessageBoardCache;
import com.mall.net.Web;
import com.mall.util.ConnectionDetector;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.PushMessageAndroidFrame;
import com.mall.view.R;
import com.mall.view.service.UploadService;
import com.mall.view.service.UploadService.UploadBinder;
import com.pulltorefresh.PullToRefreshListView;
import com.pulltorefresh.PullToRefreshListView.PullToRefreshListener;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 功能： 商城全部会员心情页面<br>
 * 时间： 2014-8-16<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class MessageBoardFrame extends Activity {
    private PopupWindow distancePopup = null;
    @ViewInject(R.id.message_list)
    private ListView listview;
    @ViewInject(R.id.refreshable_view)
    private PullToRefreshListView refreshable_view;
    private List<UserMessageBoard> list = new ArrayList<UserMessageBoard>();
    private XQAdapter adapter;
    private User user;
    private int page = 1;
    private int size = 10;
    int imgindex = 0;
    private BitmapUtils bmUtils = null;
    private int _30dp = 30;
    private DbUtils db;
    private String from = "";
    private UploadService uploadService;
    private Handler updateListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.setFrom("nowritemessage");//将from标志位设置为nowritemessage
            adapter.notifyDataSetChanged();
        }
    };
    public ServiceConnection uploadImageServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UploadService.UploadBinder uploadBinder = (UploadBinder) service;
            UploadService uploadService = uploadBinder.getService();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        //每个30秒钟check一次是上传的状态
                        try {
                            LogUtils.e("sleep120000");
                            Thread.sleep(120000);
                            boolean connected = ConnectionDetector.isWifiConnected(MessageBoardFrame.this);
                            if (adapter != null) {
                                List<UserMessageBoard> list = adapter.getList();//当前ListView中的数据项
                                List<UserMessageBoard> cachList = getNoUpload();//当前缓存数据库中的数据项
                                for (int i = 0; i < cachList.size(); i++) {
                                    for (int k = 0; k < list.size(); k++) {
                                        //如果还在缓存中没有上传成功的记录ID等于adapterItem中的记录ID，则该记录未上传成功
                                        boolean s = cachList.get(i).getDateaId() == list.get(k).getDateaId() ? true : false;
                                        if (cachList.get(i).getDateaId() == list.get(k).getDateaId()) {
                                            list.get(k).setFlag("no");
                                            list.get(k).setFrom("secondtime");
                                        } else {
                                            list.get(k).setFlag("yes");
                                        }
                                    }
                                }
                                Message msg = updateListHandler.obtainMessage();
                                updateListHandler.sendMessage(msg);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.message_board_frame);
        ViewUtils.inject(this);
        bmUtils = new BitmapUtils(this);
        _30dp = Util.dpToPx(this, 30);
        db = DbUtils.create(this, "xqcache");
        if (!Util.isNetworkConnected(this)) {
            Util.show("请检查您的网络连接...", this);
            return;
        }
        user = UserData.getUser();
        if (adapter != null) {
            adapter.setStop(false);
            adapter.sendMessage();
            adapter.map.clear();
            adapter.updateUser();
        }
        init();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (distancePopup != null && distancePopup.isShowing()) {
            distancePopup.dismiss();
        }
        if (adapter != null) {
            adapter.finishThread();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MessageBoardFrame.this.getApplicationContext().bindService(new Intent(MessageBoardFrame.this, UploadService.class), uploadImageServiceConnection, Context.BIND_AUTO_CREATE);
        from = this.getIntent().getStringExtra("from");
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceConversionUtil.getInstace().getFileText(getApplication());
            }
        }).start();
    }

    /**
     * 新建一个popupwindow实例
     *
     * @param view
     */
    private void initpoputwindow(View view) {
        distancePopup = new PopupWindow(view,
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
        distancePopup.setOutsideTouchable(true);
        distancePopup.setFocusable(true);
        distancePopup.setBackgroundDrawable(new BitmapDrawable());
        // distancePopup.setAnimationStyle(R.style.popupanimation);
    }

    private void getPopupWindow() {
        if (distancePopup != null && distancePopup.isShowing()) {
            distancePopup.dismiss();
        }
    }

    /**
     * 初始化并弹出popupwindow
     *
     * @param
     */
    private void startPopupWindow() {
        View pview = getLayoutInflater().inflate(R.layout.message_board_bar, null, false);
        LinearLayout my = (LinearLayout) pview.findViewById(R.id.my);
        LinearLayout fabu = (LinearLayout) pview.findViewById(R.id.fabu);
        LinearLayout sjts = (LinearLayout) pview.findViewById(R.id.sjts);
        final ImageView logo = (ImageView) pview.findViewById(R.id.logo);
        if (user != null) {
            bmUtils.display(logo, user.getUserFace(), new BitmapLoadCallBack<View>() {
                @Override
                public void onLoadCompleted(View container, String uri,
                                            Bitmap bitmap, BitmapDisplayConfig config,
                                            BitmapLoadFrom from) {
                    Bitmap zoomBm = Util.zoomBitmap(bitmap, _30dp, _30dp);
                    logo.setImageBitmap(Util.getRoundedCornerBitmap(zoomBm));
                }

                @Override
                public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                    Resources r = MessageBoardFrame.this.getResources();
                    InputStream is = r.openRawResource(R.drawable.ic_launcher);
                    BitmapDrawable bmpDraw = new BitmapDrawable(is);
                    Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(), _30dp, _30dp);
                    logo.setImageBitmap(Util.getRoundedCornerBitmap(zoomBm));
                }
            });
        }
        fabu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != user) {
                    Util.showIntent(MessageBoardFrame.this, WriterNewMessageBoardFrame.class);
                } else {
                    Util.showIntent("对不起，请先登录！", MessageBoardFrame.this, LoginFrame.class);
                }
            }
        });
        my.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != user) {
                    Intent intent = new Intent(MessageBoardFrame.this, UserMessageBoardFrame.class);
                    intent.putExtra("userId", user.getNoUtf8UserId());
                    intent.putExtra("userface", user.getUserFace());
                    MessageBoardFrame.this.startActivity(intent);
                } else {
                    Util.showIntent("对不起，请先登录！", MessageBoardFrame.this, LoginFrame.class);
                }
            }
        });
        sjts.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showIntent(MessageBoardFrame.this, PushMessageAndroidFrame.class);
            }
        });
        initpoputwindow(pview);
    }

    private void init() {
        Util.initTitle(this, "动态",
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        MessageBoardFrame.this.finish();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getPopupWindow();
                        startPopupWindow();
                        distancePopup.showAsDropDown(v);
                    }
                }, R.drawable.ic_action_overflow);
        firstpage(true);//onStart中的加载数据需检查本地数据库
        scrollPage();
        refreshable_view.setOnRefreshListener(new PullToRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                firstpage(true);
            }
        }, 0);
    }

    private void firstpage(boolean isFlush) {
        List<UserMessageBoard> cache_list = getNoUpload();//获取本地缓存数据库中的数据项
        if (cache_list != null && cache_list.size() > 0) {
            if (adapter == null) {
                adapter = new XQAdapter(MessageBoardFrame.this, listview);
                adapter.setList(cache_list);
                adapter.setIsIndex(true);
                if (isFlush) {//如果是下拉刷新
                    adapter.setFrom("notwritemessage");
                }
                if (!Util.isNull(from)) {
                    adapter.setFrom(from);
                } else {
                    adapter.setFrom("notwritemessage");
                }
                listview.setAdapter(adapter);
            } else {
                if (isFlush) {
                    page = 1;
                    adapter.list.clear();
                }
                adapter.setList(cache_list);
                adapter.notifyDataSetChanged();
            }
        } else {
            if (isFlush) {
                page = 1;
                if (null != adapter)
                    adapter.list.clear();
            }
        }
        initData(true);
    }

    private List<UserMessageBoard> getNoUpload() {
        List<UserMessageBoardCache> list = new ArrayList<UserMessageBoardCache>();
        List<UserMessageBoard> cache_list = new ArrayList<UserMessageBoard>();
        try {
            list.clear();
            Selector sel = Selector.from(UserMessageBoardCache.class);
            sel.where(WhereBuilder.b("flag", "=", "no"));
            sel.orderBy("createTime", true);
            list = db.findAll(sel);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    UserMessageBoard usb = new UserMessageBoard();
                    List<Bitmap> bitmaps = new ArrayList<Bitmap>();
                    UserMessageBoardCache u = list.get(i);
                    if (!Util.isNull(u.getCacheImgFiles())) {
                        usb.setLocalFilesPaths(u.getCacheImgFiles());
                        usb.setImgCacheFiles(u.getCacheImgFiles());
                        String[] s = u.getCacheImgFiles().split("spkxqadapter");
                        usb.setLocalFilesPaths(u.getCacheImgFiles());
                        for (int k = 0; k < s.length; k++) {
                            if (!Util.isNull(s[k])) {
                                if (bitmaps.size() > 9) {
                                    break;
                                }
//								Bitmap bitmap=Util.getLocationThmub(s[k],174,174);
//								bitmaps.add(bitmap);
                            }
                        }
                    }
                    if (!Util.isNull(u.getCity())) {
                        usb.setCity(u.getCity());
                    }
                    if (!Util.isNull(u.getCreateTime())) {
                        usb.setCreateTime(u.getCreateTime());
                    }
                    if (!Util.isNull(u.getUserId())) {
                        usb.setUserId(u.getUserId());
                    }
                    if (!Util.isNull(u.getUserFace())) {
                        usb.setUserFace(u.getUserFace());
                    }
                    if (!Util.isNull(u.getContent())) {
                        usb.setContent(u.getContent());
                    }
                    usb.setFrom("secondtime");
                    usb.setFlag("no");
                    usb.setDateaId(u.getId());
                    usb.setNoRead("0");
                    usb.setFiles("");
                    if (bitmaps != null && bitmaps.size() > 0) {
                        usb.setList(bitmaps);
                    }
                    cache_list.add(usb);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return cache_list;
    }

    private void scrollPage() {
        listview.setOnScrollListener(new OnScrollListener() {
            int lastItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (lastItem >= adapter.getCount()
                        && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    initData(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });
    }

    private void initData(boolean isShowProgressDialog) {

        IAsynTask asynTask = new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("网络错误，请重试！", MessageBoardFrame.this);
                    return;
                }
                HashMap<String, List<UserMessageBoard>> map = (HashMap<String, List<UserMessageBoard>>) runData;
                list = map.get("list");
                if (list.size() > 0) {
                    if (adapter == null) {
                        adapter = new XQAdapter(MessageBoardFrame.this, listview);
                        adapter.setList(list);
                        adapter.setIsIndex(true);
                        listview.setAdapter(adapter);
                    } else {
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
                    }
                } else if (list.size() == 0) {
                    listview.setOnScrollListener(null);
                    page--;// 将page会滚到上一页
                }
                refreshable_view.finishRefreshing();
            }

            @Override
            public Serializable run() {
                String userId = "";
                if (null != user)
                    userId = user.getUserId();
                Web web = new Web(Web.getAllUserMessageBoard, "userId=" + userId + "&page=" + (page++) + "&size=" + size + "&loginUser=");
                List<UserMessageBoard> list = web.getList(UserMessageBoard.class);
                HashMap<String, List<UserMessageBoard>> map = new HashMap<String, List<UserMessageBoard>>();
                map.put("list", list);
                return map;
            }
        };
        if (isShowProgressDialog)
            Util.asynTask(this, "正在获取网友动态...", asynTask);
        else
            Util.asynTask(asynTask);
    }

    @OnClick(R.id.message_board_me)
    public void clickMe(View view) {
        if (null != user) {
            Intent intent = new Intent(this, UserMessageBoardFrame.class);
            intent.putExtra("userId", user.getNoUtf8UserId());
            intent.putExtra("userface", user.getUserFace());
            this.startActivity(intent);
        } else {
            Util.showIntent("对不起，请先登录！", this, LoginFrame.class);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MessageBoardFrame.this.finish();
        }
        return true;
    }
}
