package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mall.happlylot.MyHapplyLot;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;

/**
 * 功能： 基本信息<br>
 * 时间： 2013-3-7<br>
 * 备注： <br>
 *
 * @author Lin.~
 */


public class UserInfoFrame extends Activity {

    private User user = null;

    private TextView userNO;
    private TextView userLoginName = null;
    private TextView userTrueName = null;
    private TextView userPhone = null;
    private TextView userCard = null;
    private TextView userLevel = null;
    private TextView userTuiJian = null;
    private TextView userZhaoShang = null;
    private TextView userAddress = null;

    private ImageView user_info_touxiang;
    private TextView user_info_Name;

    private TextView user_info_showmore;

    private View idrl //身份证号行
            , phonerl  //手机行
            , infoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.userinfo);
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        intListener();

    }

    private void initComponent() {
        userLoginName = Util
                .getTextView(R.id.userLoginName, UserInfoFrame.this);

        userNO = Util.getTextView(R.id.userNO, this);
        userTrueName = Util.getTextView(R.id.userTrueName, UserInfoFrame.this);
        userPhone = Util.getTextView(R.id.userPhone, UserInfoFrame.this);
        userCard = Util.getTextView(R.id.userCard, UserInfoFrame.this);
        userLevel = Util.getTextView(R.id.userLevel, UserInfoFrame.this);
        userTuiJian = Util.getTextView(R.id.userTuiJian, UserInfoFrame.this);
        userZhaoShang = Util
                .getTextView(R.id.userZhaoShang, UserInfoFrame.this);
        userAddress = Util.getTextView(R.id.userAddress, UserInfoFrame.this);
        user_info_showmore = (TextView) UserInfoFrame.this
                .findViewById(R.id.user_info_showmore);
        user_info_touxiang = (ImageView) UserInfoFrame.this
                .findViewById(R.id.user_info_touxiang);
        user_info_Name = (TextView) UserInfoFrame.this
                .findViewById(R.id.user_info_Name);

        idrl = findViewById(R.id.idrl);
        phonerl = findViewById(R.id.phonerl);
        infoll = findViewById(R.id.infoll);

    }

    public void init() {
        initComponent();
        user = UserData.getUser();
        Util.initTitle(this, "用户信息", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (null == user) {
            Util.showIntent("对不起，您还没有登录！", this, LoginFrame.class);
            return;
        }
        Util.asynTask(this, "正在获取您的信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == user || Util.isNull(user.getUserNo())) {
                    Util.show("获取您的个人资料失败，请重试！", UserInfoFrame.this);
                    return;
                }
                int levelid = Integer.parseInt(user.getLevelId());
                if (levelid == 4 || levelid == 6) {
                    idrl.setVisibility(View.GONE);
                    phonerl.setVisibility(View.GONE);
                    infoll.setVisibility(View.GONE);

                }

                userLoginName.setText(Util.getNo_pUserId(user.getNoUtf8UserId()));
                userNO.setText(user.getUserNo());
                userLoginName.setTextColor(Color.RED);
                Log.e("TAGName", user.getName());
                userTrueName.setText(user.getName());
                user_info_Name.setText(user.getName());
                userPhone.setText(user.getMobilePhone());
                Log.e("TAGCard", user.getIdNo());
                userCard.setText(!Util.isNull(UserData.getUser().getIdNo()) ? UserData.getUser().getIdNo() : UserData.getUser().getPassport());

                userTuiJian.setText(Util.getNo_pUserId(user.getInviter()));

                userZhaoShang.setText(user.getMerchants());
                userLevel.setText(Html.fromHtml("<html><body>"
                        + user.getShowLevel().replaceFirst("\\[城市经理\\]",
                        "<font color='blue'>[城市经理]</font>")
                        + "</ody></html>"));
                userAddress.setText(user.getZone());
                AnimeUtil.getImageLoad().displayImage(user.getUserFace(),
                        user_info_touxiang, AnimeUtil.getImageRectOption());

            }

            @Override
            public Serializable run() {
                user = Web.reDoLogin();
                return user;
            }
        });

    }

    private void intListener() {
        user_info_showmore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Util.showIntent(UserInfoFrame.this, MyHapplyLot.class);
//				Util.showIntent(UserInfoFrame.this, AccountManagerFrame.class);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "修改资料");
        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "修改密码");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (null != UserData.getUser()) {
            switch (item.getItemId()) {
                case Menu.FIRST + 1:
                    if (!Util.isNull(UserData.getUser())) {
                        if (!Util.isNull(UserData.getUser().getIdNo()) || !Util.isNull(UserData.getUser().getPassport())) {
                            Util.showIntent("您的资料已完善，不需要再修改！", this,
                                    UserInfoFrame.class);
                            break;
                        }
                    }
                    startActivity(new Intent(this, UpdateUserFrame.class));
                    break;
                case Menu.FIRST + 2:
                    startActivity(new Intent(UserInfoFrame.this,
                            UsermodpassFrame.class));
                    break;
                default:
                    startActivity(new Intent(UserInfoFrame.this,
                            UsermodpassFrame.class));
            }
        }
        return false;
    }

}
