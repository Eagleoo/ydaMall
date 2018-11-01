package com.mall.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.game.chinesechess.ChessGame;
import com.mall.game.ddz.GameDDZ;
import com.mall.game.fivechess.index.BackgammonActivity;
import com.mall.game.g2048.Game2048;
import com.mall.model.messageboard.UserMessageBoard;
import com.mall.net.Web;
import com.mall.note.NoteMainFrame;
import com.mall.serving.school.YdaSchoolActivity;
import com.mall.util.ConnectionDetector;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class FindFrame extends AppCompatActivity {
    @ViewInject(R.id.user_logo)
    private ImageView user_logo;
    private BitmapUtils bmUtils;
    private int _50dp;
    private int _100dp;

    private Context context;

    @OnClick(R.id.game_fivechess)
    public void FiveChess(View v) {
        Util.showIntent(this, BackgammonActivity.class);
    }

    @OnClick(R.id.ydnews_layout)
    public void YDNews(final View v) {
        Util.showIntent(this, YdaSchoolActivity.class,
                new String[]{"isNews"}, new Serializable[]{true});
    }

    @OnClick(R.id.game_ddz)
    public void GameDDZ(final View v) {
        Util.showIntent(this, GameDDZ.class);
    }

    @OnClick(R.id.game_chinesechess)
    public void GameCC(final View v) {
        Util.showIntent(this, ChessGame.class);
    }

    @OnClick(R.id.game2048)
    public void Game2048(final View v) {
        Util.showIntent(this, Game2048.class);
    }

    @OnClick(R.id.ll_bmfw)
    public void ll_bmfw(View view) {
        if (UserData.getUser() == null) {
            Toast.makeText(this, "您还没登录哦，请前去登录", Toast.LENGTH_SHORT).show();
            Util.showIntent(FindFrame.this, LoginFrame.class, new String[]{"PhoneFream"}, new String[]{"PhoneFream"});
        } else
            Util.showIntent(this, PhoneFream.class);
    }

    @OnClick(R.id.ll_qrcode)
    public void Card(View v) {
        if (hasApplication(this, "com.yda.rzt")) {
            Intent intent = new Intent();
            ComponentName cn = new ComponentName("com.yda.rzt", "com.yda.rzt.activity.LaunchActivity");
            intent.setComponent(cn);
            if (!Util.isNull(UserData.getUser())) {
                intent.putExtra("name", UserData.getUser().getUserId());
                intent.putExtra("password", UserData.getUser().getPassword());
            }
            startActivity(intent);
        } else {
            Toast.makeText(this, "应用不存在", Toast.LENGTH_LONG).show();
            Util.openWeb(context, "http://m.rzt.yda360.cn/app/rzt.apk");
        }
    }

    public boolean hasApplication(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        //获取系统中安装的应用包的信息
        List<PackageInfo> listPackageInfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < listPackageInfo.size(); i++) {
            if (listPackageInfo.get(i).packageName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }

    @OnClick(R.id.tojs)
    public void tojsfaction(View view) {

        Intent intent = new Intent(FindFrame.this, NoteMainFrame.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_frame);
        ViewUtils.inject(this);
        context = this;
        bmUtils = new BitmapUtils(this);
        _50dp = Util.dpToPx(this, 50);
        _100dp = Util.dpToPx(this, 100);
        System.gc();
        // getOffice();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean connected = ConnectionDetector.isWifiConnected(FindFrame.this);
        if (!connected) {
            return;
        }
        UserData.setOfficeInfo(null);
        init();
    }

    private void init() {
        Util.initTitle(this, "发现");
        bindUserFace();
    }

    private void getFirstXQ() {
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    return;
                }
                HashMap<String, List<UserMessageBoard>> map = (HashMap<String, List<UserMessageBoard>>) runData;
                List<UserMessageBoard> list = map.get("list");
                String s = "";
                if (list == null || list.size() <= 0) {
                    s = "";
                } else {
                    if (list.size() >= 2) {
                        UserMessageBoard u = list.get(1);
                        s = u.getUserFace();
                    } else {
                        s = list.get(0).getUserFace();
                    }
                }
                Log.i("tag", "[][][][" + s);
                bmUtils.display(user_logo, s,
                        new DefaultBitmapLoadCallBack<View>() {
                            @Override
                            public void onLoadCompleted(View container,
                                                        String uri, Bitmap bitmap,
                                                        BitmapDisplayConfig config,
                                                        BitmapLoadFrom from) {
                                Bitmap zoomBm = Util.zoomBitmap(bitmap, _50dp,
                                        _50dp);
                                super.onLoadCompleted(container, uri,
                                        Util.getRoundedCornerBitmap(zoomBm),
                                        config, from);
                            }

                            @Override
                            public void onLoadFailed(View container,
                                                     String uri, Drawable drawable) {
                                Resources r = FindFrame.this.getResources();
                                InputStream is = r
                                        .openRawResource(R.drawable.ic_launcher_black_white);
                                BitmapDrawable bmpDraw = new BitmapDrawable(is);
                                Bitmap zoomBm = Util.zoomBitmap(
                                        bmpDraw.getBitmap(), _50dp, _50dp);
                                user_logo.setImageBitmap(Util
                                        .getRoundedCornerBitmap(zoomBm));
                            }
                        });
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getAllUserMessageBoard,
                        "userId=&page=1&size=2" + "&loginUser=");
                List<UserMessageBoard> list = web
                        .getList(UserMessageBoard.class);
                HashMap<String, List<UserMessageBoard>> map = new HashMap<String, List<UserMessageBoard>>();
                map.put("list", list);
                return map;
            }
        });
    }

    private void bindUserFace() {
        getFirstXQ();
    }

}
