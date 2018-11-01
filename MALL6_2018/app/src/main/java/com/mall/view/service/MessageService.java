package com.mall.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.mall.model.InMaill;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.AccountDetailsFrame;
import com.mall.view.PushMessageFrame;
import com.mall.view.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class MessageService extends Service {
    //	public static final String TAG = "com.mall.view.service.MessageService";
    private static final int skipTime = 10000 * 60 * 30; // 30分钟
    private DbUtils db = null;
    private Thread thread = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        start();
    }

    private void start() {
        db = DbUtils.create(this);
        thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    doRun();
                }
            }
        });
        thread.start();
    }

    private void doRun() {
        LogUtils.d("messgae service");
        boolean isNull = null == UserData.getUser();
        final String userid = (isNull ? "" : UserData.getUser().getUserId());
        final String pwd = (isNull ? "" : UserData.getUser().getMd5Pwd());
        if (Util.isNetworkConnected(this)) {
            Looper.prepare();
            Util.asynTask(new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    HashMap<String, List<InMaill>> map = (HashMap<String, List<InMaill>>) runData;
                    List<InMaill> list = map.get("list");
                    if (null != list && 0 != list.size()) {
                        for (InMaill maill : list) {
                            if (isShow(maill.getSenderTypeID())) {
                                if ("0".equals(maill.getSenderTypeID())) {
                                    InMaill sqllite = null;
                                    try {
                                        sqllite = db.findById(InMaill.class, maill.getId());
                                        if (null == sqllite)
                                            sqllite = new InMaill();
                                    } catch (DbException e) {
                                        sqllite = new InMaill();
                                        sqllite.setId(maill.getId());
                                        sqllite.setState("0");
                                    }
                                    if ("1".equals(sqllite.getState()))
                                        continue;
                                    maill.setState("1");
                                    try {
                                        db.saveOrUpdate(maill);
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                    }
                                }
                                String sendTyp = "1";
                                Log.e("推送1", maill.getSenderType());
                                if ("全部会员".equals(maill.getSenderType())) {
                                    sendTyp = "0";
                                } else if ("系统推送".equals(maill.getSenderType()))
                                    sendTyp = "99";
                                int notificationId = -1;
                                if (maill.getTitle().contains("金额变更")) {
                                    String name = maill.getTitle().replace("金额变更", "");
                                    String key = "";
                                    Log.e("推送2", name + "name");
                                    if (name.contains("商币"))
                                        key = "sb";
                                    else if (name.contains("话费"))
                                        key = "pho";
                                    else if (name.contains("业务"))
                                        key = "bus";
                                    else if (name.contains("消费券"))
                                        key = "han";
                                    else if (name.contains("充值"))
                                        key = "rec";
                                    notificationId = showNotificaion(name, key, maill.getId(), maill.getTid(), maill.getSender(), maill.getTitle(), maill.getDescription(), sendTyp);
                                } else
                                    notificationId = showNotificaion(maill.getId(), maill.getTid(), maill.getSender(), maill.getTitle(), maill.getDescription(), sendTyp);
                                LogUtils.d("accept push message add notification.id is " + notificationId + "   message.id is=" + maill.getId());
                            }
                        }
                    }
                    LogUtils.e("read length = " + list.size() + "  next read time = " + skipTime);
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.getUnReaderMessage, "userid=" + userid
                            + "&md5Pwd=" + pwd + "&page=1&pageSize=10");
                    List<InMaill> list = web.getList(InMaill.class);
                    HashMap<String, List<InMaill>> map = new HashMap<String, List<InMaill>>();
                    map.put("list", list);
                    return map;
                }
            });
            Looper.loop();
        }
        // 没登录
        if (isNull) {
            while (null == UserData.getUser()) {
                try {
                    thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            synchronized (thread) {
                try {
                    thread.sleep(skipTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int showNotificaion(String parentName, String parentKey, String id, String tid, String senderUser, String title, String message, String sendertype) {
        Intent intent = new Intent(MessageService.this, AccountDetailsFrame.class);
        intent.putExtra("parentName", parentName);
        intent.putExtra("userKey", parentKey);
        Bundle bundle = new Bundle();
        intent.addFlags(Intent.FILL_IN_DATA);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        bundle.putString("nid", id);
        bundle.putString("mid", tid);
        bundle.putString("userid", tid);
        bundle.putString("sendertype", sendertype);
        intent.putExtras(bundle);
        intent.setAction(String.valueOf(System.currentTimeMillis()));
        return Util.addNotification(Integer.parseInt(id), MessageService.this,
                R.drawable.ic_launcher, title.replace("_P", "").replace("_p", ""),
                message.replace("_P", "").replace("_p", ""), intent, null);
    }

    private int showNotificaion(String id, String tid, String senderUser, String title, String message, String sendertype) {
        Intent intent = new Intent(MessageService.this, PushMessageFrame.class);
        Bundle bundle = new Bundle();
        intent.addFlags(Intent.FILL_IN_DATA);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        bundle.putString("nid", id);
        bundle.putString("mid", tid);
        bundle.putString("userid", tid);
        bundle.putString("sendertype", sendertype);
        intent.putExtras(bundle);
        intent.setAction(String.valueOf(System.currentTimeMillis()));
        return Util.addNotification(Integer.parseInt(id), MessageService.this,
                R.drawable.ic_launcher, title.replace("_P", "").replace("_p", ""),
                message.replace("_P", "").replace("_p", ""), intent, null);
    }

    private void readPushMessage(String userId, String md5Pwd, String mid) {
        if (Util.isNull(userId) || Util.isNull(md5Pwd)) return;
        if (!Util.isNetworkConnected(getApplicationContext())) return;
        NewWebAPI.getNewInstance().readPush(userId, md5Pwd, mid, null);
    }

    private boolean isShow(String senderTypeId) {
        boolean isNull = null == UserData.getUser();
        String level = isNull ? "0" : UserData.getUser().getLevelId();
        String shop = isNull ? "0" : UserData.getUser().getShopTypeId();
        int lid = Util.getInt(level);
        int sid = Util.getInt(shop);
        boolean isShow = false;
        if ("1".equals(senderTypeId)) {
            if ("-1".equals(shop) && "1".equals(level))
                isShow = true;
        } else if ("2".equals(senderTypeId)) {
            if ("2".equals(level) && "-1".equals(shop))
                isShow = true;
        } else if ("3".equals(senderTypeId)) {
            if ("2".equals(level) && -1 < sid)
                isShow = true;
        } else if ("4".equals(senderTypeId)) {
            if ("-1".equals(shop) && 2 < lid)
                isShow = true;
        } else if ("5".equals(senderTypeId)) {
            if ("10".equals(shop))
                isShow = true;
        } else
            isShow = true;
        return isShow;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}