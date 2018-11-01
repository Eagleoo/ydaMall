package com.mall.pushmessage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.example.view.VideoAudioDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.MemberInfo;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.util.IAsynTask;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.Lin_MainFrame;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.messageboard.MyToast;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PushMessage extends Activity {
    public static boolean isFirstOpen = true;
    @ViewInject(R.id.push_edit1)
    private EditText message;


    @ViewInject(R.id.all_zs_count)
    private TextView all_zs_count;
    @ViewInject(R.id.zshy_img)
    private CheckBox zshy_img;

    @ViewInject(R.id.zdy_layout)
    private LinearLayout zdy_layout;
    @ViewInject(R.id.a_push_allmem_show_or_close)
    private LinearLayout a_push_allmem_show_or_close;
    @ViewInject(R.id.a_push_shopmem_show_or_close)
    private LinearLayout a_push_shopmem_show_or_close;
    @ViewInject(R.id.a_push_zhsmem_show_or_close)
    private LinearLayout a_push_zhsmem_show_or_close;
    @ViewInject(R.id.all_check_count)
    private TextView all_check_count;
    @ViewInject(R.id.fudao_check_count)
    private TextView fudao_check_count;
    @ViewInject(R.id.yaoqing_check_count)
    private TextView yaoqing_check_count;
    @ViewInject(R.id.all_user_count)
    private TextView all_user_count;
    @ViewInject(R.id.zdy_img)
    private CheckBox zdy_img;
    @ViewInject(R.id.all_tj_count)
    private TextView all_tj_count;

    @ViewInject(R.id.tjhy_img)
    private CheckBox tjhy_img;

    @ViewInject(R.id.glance)
    private TextView glance;


    @ViewInject(R.id.tv_tag1)
    private TextView tv_tag1;

    @ViewInject(R.id.tv_tag2)
    private TextView tv_tag2;

    @ViewInject(R.id.tv_tag3)
    private TextView tv_tag3;

    @ViewInject(R.id.topright)
    private TextView topright;

    private TextView top_back;

    private String md5Pwd = "";
    private String userId = "";
    private User user;
    private String alluserCount = "0";// 所有会员数量
    private String allZSUserCount = "0";// 招商会员数量
    private String allTJcount = "0";// 推荐会员数量
    private String[] to_user = new String[]{"0", "0", "0"};
    private String person = "";
    private SharedPreferences sp;
    private String spmessage = "";
    private ArrayList<String> zshy_choosed = new ArrayList<String>();
    private ArrayList<String> tjhy_choosed = new ArrayList<String>();
    private ArrayList<String> zdyhy_choosed = new ArrayList<String>();
    private ArrayList<String> all_spit_3 = new ArrayList<String>();
    private ArrayList<String> yaoqing_spit_3 = new ArrayList<String>();
    private ArrayList<String> fudao_spit_3 = new ArrayList<String>();
    private String types = "";
    private String from = "";
    private String itemInfo = "";
    private View AllMemTitle;
    private View ShopMemTitle;
    private View ShopMemShow;
    private View ZhsMemTitle;
    private View ZhsMemShow;

    private ImageView ShopMemArrUp;
    private ImageView ShopMemArrDown;
    private ImageView ZhsMemArrUp;
    private ImageView ZhsMemArrDown;
    private String allNumber;
    private String allfudaoNumber;
    private String allyaoqingNumber;
    private String phonenumber;
    private boolean allChecked;
    private boolean fdChecked;
    private boolean yqChecked;

    String level = "";

    @OnClick({R.id.message_ts, R.id.share_to_xq, R.id.glance, R.id.a_push_send_msg})
    public void BottomTexViewClick(final View v) {
        switch (v.getId()) {
            case R.id.message_ts:
                sendData();
                break;
            case R.id.share_to_xq:
                upContent();
                break;
            case R.id.glance:
                VideoAudioDialog dialog = new VideoAudioDialog(this);
                dialog.showcancel(View.GONE).showtag1(View.VISIBLE).showtag2(View.VISIBLE).showtime(View.VISIBLE);
                dialog.setSure("确认");
                dialog.setTitle("推送消息");
                dialog.setContent(message.getText().toString());
                dialog.setlin1("辅导老师 : " + UserData.getUser().getUserId());
                dialog.setlin2("联系方式 : " + UserData.getUser().getMobilePhone());
                dialog.settime(Util.getCurrentYear() + "-"
                        + Util.getCurrentMonth() + "-" + Util.getCurrDay());
                dialog.show();
                break;
            case R.id.a_push_send_msg:
                if (Util.isNull(message.getText().toString().trim())) {
                    Util.MyToast(PushMessage.this, "请输入发送内容!", 1);
                    return;
                }
                if (allChecked || (fdChecked && yqChecked)) {
                    phonenumber = allNumber;
                    if (Util.isNull(phonenumber)) {
                        Util.MyToast(PushMessage.this, "你选中的都没有填写手机号\n请选择推送对象!", 1);
                        return;
                    }
                } else if (fdChecked) {
                    phonenumber = allfudaoNumber;
                    if (Util.isNull(phonenumber)) {
                        Util.MyToast(PushMessage.this, "你选中的都没有填写手机号\n请选择推送对象!", 1);
                        return;
                    }
                } else if (yqChecked) {
                    phonenumber = allyaoqingNumber;
                    if (Util.isNull(phonenumber)) {
                        Util.MyToast(PushMessage.this, "你选中的都没有填写手机号\n请选择推送对象!", 1);
                        return;
                    }
                }
                if ((!"0个".equals(all_check_count.getText().toString()) ||
                        !"0个".equals(fudao_check_count.getText().toString()) ||
                        !"0个".equals(yaoqing_check_count.getText().toString())
                ) && Util.isNull(phonenumber)) {
                    Util.MyToast(PushMessage.this, "你选中的都没有填写手机号\n请选择推送对象!", 1);
                    return;
                }
                if (Util.isNull(phonenumber)) {
                    Util.MyToast(PushMessage.this, "请选择推送对象!", 1);
                    return;
                }
                if (phonenumber.split(";").length > 100) {
                    Util.MyToast(PushMessage.this, "号码总数超过100个,不符合短信群发规定,请重新选择!", 1);
                    return;
                }

                VideoAudioDialog dialog1 = new VideoAudioDialog(this);
                dialog1.setSure("发送");
                dialog1.setTitle("温馨提示");
                dialog1.setContent("“发送短信费用”将由服务运营提供商以“短信收费标准”收取");
                dialog1.setLeft(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.e("发送号码", "" + phonenumber);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //intent.putExtra("address", ""+phonenumber);
                        intent.putExtra("address", "" + phonenumber);
                        intent.putExtra("sms_body", message.getText().toString());
                        intent.setType("vnd.android-dir/mms-sms");
                        startActivity(intent);
                    }
                });
                dialog1.show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        if ("mydata".equals(from)) {
            top_back = (TextView) this.findViewById(R.id.top_back);
            top_back.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PushMessage.this, Lin_MainFrame.class);
                    intent.putExtra("toTab", "usercenter");
                    startActivity(intent);
                    finish();

                }
            });
        }

        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.a_push_allmem_show_or_close, R.id.a_push_shopmem_show_or_close, R.id.a_push_zhsmem_show_or_close})
    public void LayoutOnClick(final View v) {
        switch (v.getId()) {
            case R.id.a_push_allmem_show_or_close://全部会员
                a_push_allmem_show_or_close.setBackgroundColor(Color.parseColor("#fafafa"));
                if ("已选0个".equals(all_check_count.getText().toString())) {
                    sp.edit().putString("spmessage", message.getText().toString()).commit();
                    Intent intent = new Intent(PushMessage.this, AllUserList.class);
                    if (level.equals("城市总裁") || level.equals("城市CEO")) {
                        intent.putExtra("title", "全部会员");
                    } else {
                        intent.putExtra("title", "全部会员");
                    }

                    //				intent.putExtra("totalcount", alluserCount);
                    PushMessage.this.startActivity(intent);
                } else {
                    if (("noselected").equals(zdy_img.getTag())) {
                        zdy_img.setTag("selected");
                        to_user[0] = "1";
                    }
                    a_push_allmem_show_or_close.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            float nowX = 0;
                            float original = 0;
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    original = event.getX();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    nowX = event.getX();
                                    float distance = original - nowX;
                                    if (Math.abs(distance) > 20) {
                                        sp.edit().putString("spmessage", message.getText().toString()).commit();
                                        Intent intent = new Intent(PushMessage.this, PushUsersList.class);
                                        intent.putExtra("listtype", "zdy");
                                        intent.putStringArrayListExtra("zdylist", zdyhy_choosed);
                                        intent.putExtra("totalcount", alluserCount);
                                        PushMessage.this.startActivity(intent);
                                    }

                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                }
                break;
            case R.id.a_push_shopmem_show_or_close://辅导
                a_push_shopmem_show_or_close.setBackgroundColor(Color.parseColor("#fafafa"));
                if ("已选0个".equals(fudao_check_count.getText().toString())) {
                    sp.edit().putString("spmessage", message.getText().toString()).commit();
                    Intent intent = new Intent(PushMessage.this, AllUserList.class);
                    //				intent.putExtra("comefrome", "zshy");
                    //				intent.putExtra("totalcount", allZSUserCount);

                    if (level.equals("城市总裁") || level.equals("城市CEO")) {
                        intent.putExtra("title", "创客");
                    } else {
                        intent.putExtra("title", "我的辅导");
                    }

                    PushMessage.this.startActivity(intent);
                } else {
                    if (("noselected").equals(zshy_img.getTag())) {
                        zshy_img.setTag("selected");
                        to_user[1] = "1";
                    }
                    a_push_shopmem_show_or_close.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            float nowX = 0;
                            float original = 0;
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    original = event.getX();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    nowX = event.getX();
                                    float distance = original - nowX;
                                    if (Math.abs(distance) > 20) {
                                        sp.edit().putString("spmessage", message.getText().toString()).commit();
                                        Intent intent = new Intent(PushMessage.this, PushUsersList.class);
                                        intent.putExtra("listtype", "zshy");
                                        intent.putStringArrayListExtra("zshylist", zshy_choosed);
                                        intent.putExtra("totalcount", allZSUserCount);
                                        PushMessage.this.startActivity(intent);
                                    }

                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                }
                break;
            case R.id.a_push_zhsmem_show_or_close://邀请会员
                a_push_zhsmem_show_or_close.setBackgroundColor(Color.parseColor("#fafafa"));
                if ("已选0个".equals(yaoqing_check_count.getText().toString())) {
                    sp.edit().putString("spmessage", message.getText().toString()).commit();
                    Intent intent = new Intent(PushMessage.this, AllUserList.class);
                    //				intent.putExtra("comefrome", "tjhy");
                    //				intent.putExtra("totalcount", allTJcount);

                    if (level.equals("城市总裁") || level.equals("城市CEO")) {
                        intent.putExtra("title", "商家");
                    } else {
                        intent.putExtra("title", "我的邀请");

                    }
                    PushMessage.this.startActivity(intent);
                } else {
                    if (("noselected").equals(tjhy_img.getTag())) {
                        tjhy_img.setTag("selected");
                        to_user[2] = "1";
                    }
                    a_push_zhsmem_show_or_close.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            float nowX = 0;
                            float original = 0;
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    original = event.getX();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    nowX = event.getX();
                                    float distance = original - nowX;
                                    if (Math.abs(distance) > 20) {
                                        sp.edit().putString("spmessage", message.getText().toString()).commit();
                                        Intent intent = new Intent(PushMessage.this, PushUsersList.class);
                                        intent.putExtra("listtype", "yaoqing");
                                        intent.putStringArrayListExtra("tjhylist", tjhy_choosed);
                                        intent.putExtra("totalcount", allTJcount);
                                        PushMessage.this.startActivity(intent);
                                    }

                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (isFirstOpen) {
                String str = "您发送的内容将自动显示您的名称、地址、联络电话等相关信息。请一定不要填写错误的、非法的文字内容，以免造成不必要的误会，甚至影响您的形象哦!";
                new MyPopWindow.MyBuilder(this, str, "确定", null)
                        .build().showCenter();
                isFirstOpen = false;
            }

        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pushmessage);
        ViewUtils.inject(this);
        sp = this.getSharedPreferences("pushmessage", 0);
        Intent intent = getIntent();
        types = intent.getStringExtra("types");
        itemInfo = intent.getStringExtra("itemInfo");
        from = intent.getStringExtra("from");
        init();

        Log.e("UserLevel", UserData.getUser().getUserLevel() + "kk");

        level = UserData.getUser().getUserLevel() + "";

        if (level.equals("城市总裁") || level.equals("城市CEO")) {
            tv_tag1.setText("全部会员");
            tv_tag2.setText("创客");
            tv_tag3.setText("商家");
        }


    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        getPersonNum();
//		getAllPersonNumber();
        super.onStart();
    }

    public void getActivityResult() {
        // 得到返回的选中的会员
        List<String> zslist = this.getIntent().getStringArrayListExtra("allzshylist");
        List<String> tjlist = this.getIntent().getStringArrayListExtra("alltjhylist");
        List<String> zdylist = this.getIntent().getStringArrayListExtra("zdylist");
        if (zdylist != null && zdylist.size() >= 0) {
            zdyhy_choosed.clear();
            zdyhy_choosed.addAll(zdylist);
            //			TextView t1 = (TextView) zdy_layout.getChildAt(1);// 全部数量
            //			TextView t2 = (TextView) zdy_layout.getChildAt(2);// 选中数量
            String count = this.getIntent().getStringExtra("totalcount");
            all_user_count.setText(count + "个");
            all_check_count.setText(zdylist.size() + "个");
            to_user[0] = "1";
        }
        if (zslist != null && zslist.size() >= 0) {
            zshy_choosed.clear();
            zshy_choosed.addAll(zslist);
            //			TextView t1 = (TextView) zshy_layout.getChildAt(1);// 全部数量
            //			TextView t2 = (TextView) zshy_layout.getChildAt(2);// 选中数量
            String count = this.getIntent().getStringExtra("totalcount");
            all_zs_count.setText(count + "个");
            fudao_check_count.setText(zslist.size() + "个");
            to_user[1] = "1";
        }
        if (tjlist != null && tjlist.size() >= 0) {
            tjhy_choosed.clear();
            tjhy_choosed.addAll(tjlist);
            //			TextView t1 = (TextView) tjhy_layout.getChildAt(1);// 全部数量
            //			TextView t2 = (TextView) tjhy_layout.getChildAt(2);// 选中数量
            String count = this.getIntent().getStringExtra("totalcount");
            all_tj_count.setText(count + "个");
            yaoqing_check_count.setText(tjlist.size() + "个");
            to_user[2] = "1";
        }
        if ("mydata".equals(from)) {

            if ("alldata".equals(types)) {
                to_user[0] = "1";
            }
            if ("fudao".equals(types)) {
                to_user[1] = "1";
            }
            if ("yaoqing".equals(types)) {
                to_user[2] = "1";
            }
        }
        getPersonString();
    }

    // 获取最终推送消息接收的会员
    private void getPersonString() {
        StringBuilder sb = new StringBuilder();
        if (zdyhy_choosed != null && zdyhy_choosed.size() >= 0) {
            for (int i = 0; i < zdyhy_choosed.size(); i++) {
                String[] s = zdyhy_choosed.get(i).split(",,,");
                person += s[3] + "|,|";
                if (!Util.isNull(s[1])) {
                    sb.append(s[1] + ";");

                }
            }
        }
        if (zshy_choosed != null && zshy_choosed.size() >= 0) {
            for (int i = 0; i < zshy_choosed.size(); i++) {
                String[] s = zshy_choosed.get(i).split(",,,");
                person += s[3] + "|,|";
                if (!Util.isNull(s[1])) {
                    sb.append(s[1] + ";");
                }
            }
        }
        if (tjhy_choosed != null && tjhy_choosed.size() >= 0) {
            for (int i = 0; i < tjhy_choosed.size(); i++) {
                String[] s = tjhy_choosed.get(i).split(",,,");
                person += s[3] + "|,|";
                if (!Util.isNull(s[1])) {
                    sb.append(s[1] + ";");
                }
            }
        }
        if ("mydata".equals(from)) {
            String[] s = itemInfo.split(",,,");
            person = s[s.length - 1];

        }
        phonenumber = sb.toString();
    }

    private void init() {

        AllMemTitle = this.findViewById(R.id.a_push_allmem_rl_title);
//		AllMemShow = this.findViewById(R.id.a_push_allmem_show_or_close);
        ShopMemTitle = this.findViewById(R.id.a_push_shopmem_rl_title);
        ShopMemShow = this.findViewById(R.id.a_push_shopmem_show_or_close);
        ZhsMemTitle = this.findViewById(R.id.a_push_zhsmem_rl_title);
        ZhsMemShow = this.findViewById(R.id.a_push_zhsmem_show_or_close);

        ShopMemArrUp = (ImageView) this
                .findViewById(R.id.a_push_shopmem_arrow_pink_up);
        ShopMemArrDown = (ImageView) this
                .findViewById(R.id.a_push_shopmem_arrow_pink_down);
        ZhsMemArrUp = (ImageView) this
                .findViewById(R.id.a_push_zhsmem_arrow_up);
        ZhsMemArrDown = (ImageView) this
                .findViewById(R.id.a_push_zhsmem_arrow_down);

        ShopMemTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (View.VISIBLE == ShopMemShow.getVisibility()) {
                    ShopMemShow.setVisibility(View.GONE);
                    ShopMemArrDown.setVisibility(View.VISIBLE);
                    ShopMemArrUp.setVisibility(View.GONE);
                } else {
                    ShopMemShow.setVisibility(View.VISIBLE);
                    ShopMemArrDown.setVisibility(View.GONE);
                    ShopMemArrUp.setVisibility(View.VISIBLE);
                }
            }
        });
        ZhsMemTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (View.VISIBLE == ZhsMemShow.getVisibility()) {
                    ZhsMemShow.setVisibility(View.GONE);
                    ZhsMemArrDown.setVisibility(View.VISIBLE);
                    ZhsMemArrUp.setVisibility(View.GONE);
                } else {
                    ZhsMemShow.setVisibility(View.VISIBLE);
                    ZhsMemArrDown.setVisibility(View.GONE);
                    ZhsMemArrUp.setVisibility(View.VISIBLE);
                }
            }
        });
        if (UserData.getUser() != null) {
            user = UserData.getUser();
            md5Pwd = user.getMd5Pwd();
            userId = user.getUserId();
        } else {
            Util.showIntent(this, LoginFrame.class);
        }
        spmessage = sp.getString("spmessage", "");
        if (!Util.isNull(spmessage)) {
            message.setText(spmessage);
        }
        Util.initTop2(this, "我的推送", "搜索会员", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PushMessage.this, AllUserList.class);
                intent.putExtra("comefrome", "zdy");
                intent.putExtra("totalcount", alluserCount);
                PushMessage.this.startActivity(intent);
            }
        });


        //所有
        zdy_img.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    all_check_count.setText(all_user_count.getText().toString());
                    phonenumber = allNumber;
                    zdyhy_choosed.addAll(all_spit_3);
                    allChecked = true;
                } else {
                    zdyhy_choosed.clear();
                    phonenumber = "";
                    allChecked = false;
                    all_check_count.setText("0个");
                }

            }
        });
        //辅导

        zshy_img.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fudao_check_count.setText(all_zs_count.getText().toString());
                    fdChecked = true;
                    zshy_choosed.addAll(fudao_spit_3);
                } else {
                    zshy_choosed.clear();
                    fdChecked = false;
                    fudao_check_count.setText("0个");
                }

            }
        });
        //邀请
        tjhy_img.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    yaoqing_check_count.setText(all_tj_count.getText().toString());
                    yqChecked = true;
                    tjhy_choosed.addAll(yaoqing_spit_3);

                } else {
                    tjhy_choosed.clear();
                    yaoqing_check_count.setText("0个");
                    yqChecked = false;
                }

            }
        });

        getActivityResult();

    }

    private boolean checkSendWay() {
        boolean b = false;
        for (int i = 0; i < to_user.length; i++) {
            if (to_user[i].equals("1"))
                b = true;
        }
        return b;
    }

    public void sendData() {
        if (message.getText().toString().equals("")) {
            MyToast.makeText(PushMessage.this, "请填写发送内容！", 5).show();
            return;
        }
        if (zdy_img.isChecked()) {
            to_user[0] = "1";
        } else {
            to_user[0] = "0";
        }
        if (zshy_img.isChecked()) {
            to_user[1] = "1";
        } else {
            to_user[1] = "0";
        }
        if (tjhy_img.isChecked()) {
            to_user[2] = "1";
        } else {
            to_user[2] = "0";
        }
        if (checkSendWay() || !Util.isNull(person)) {
            final CustomProgressDialog pd = CustomProgressDialog.createDialog(this);
            pd.setMessage(pd, "正在发送");
            pd.show();
            NewWebAPI.getNewInstance().pushMessage(to_user[0], to_user[1], to_user[2], person, Util.get(message.getText().toString()), userId, md5Pwd, new WebRequestCallBack() {
                @Override
                public void success(Object result) {
                    System.out.println("推送消息结果=============" + result.toString());
                    pd.dismiss();
                    pd.cancel();
                    com.alibaba.fastjson.JSONObject ob = JSON.parseObject(result.toString());
                    if (!Util.isNull(result)) {
                        if (200 == ob.getIntValue("code")) {
                            Toast.makeText(PushMessage.this, "推送成功", Toast.LENGTH_LONG).show();
                            sp.edit().putString("spmessage", "").commit();
                            PushMessage.this.finish();
                        } else {
                            Util.show(ob.getString("message"), PushMessage.this);
                        }
                    } else {
                        Util.show("发送失败，请稍后再试！", PushMessage.this);
                    }
                }

                @Override
                public void requestEnd() {
                    pd.cancel();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(PushMessage.this, "请选择推送信息接收会员", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    //	public void Dialog_With_WebView(final Context context, final String userid, final String content, int width, int height) {
    //		Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_bottom_out);
    //		glance.setAnimation(animation);
    //		glance.startAnimation(animation);
    //		LayoutInflater inflaterDl = LayoutInflater.from(context);
    //		LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.dialog_with_webview, null);
    //		final Dialog dialog = new AlertDialog.Builder(context).create();
    //		dialog.show();
    //		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
    //		params.width = width * 4 / 5;
    //		dialog.getWindow().setAttributes(params);
    //		dialog.getWindow().setContentView(layout);
    //		TextView title = (TextView) layout.findViewById(R.id.title);
    //		title.setText("推送消息预览");
    //		title.setVisibility(View.GONE);
    //		TextView queding = (TextView) layout.findViewById(R.id.queding);
    //		queding.setOnClickListener(new OnClickListener() {
    //			@Override
    //			public void onClick(View v) {
    //				dialog.dismiss();
    //			}
    //		});
    //		final WebView web = (WebView) layout.findViewById(R.id.web);
    //		web.getSettings().setJavaScriptEnabled(true);
    //		web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    //		web.getSettings().setDatabaseEnabled(true);
    //		web.setWebViewClient(new WebViewClient() {
    //			@Override
    //			public boolean shouldOverrideUrlLoading(WebView view, String url) {
    //				view.loadUrl(url);
    //				return true;
    //			}
    //
    //			@Override
    //			public void onPageFinished(WebView view, String url) {
    //			}
    //
    //			@Override
    //			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    //				MyToast.makeText(context, "对不起，加载预览页面失败", 5).show();
    //				dialog.dismiss();
    //			}
    //		});
    //		web.loadUrl("http://" + Web.webImage + "/notificationPreview.aspx?userId=" + userid + "&context=" + content + "&Mall=Mall");
    //	}

    private void upContent() {
        if (message.getText().toString().equals("")) {
            MyToast.makeText(PushMessage.this, "请填写发送内容！", 5).show();
            return;
        } else {
            Util.asynTask(PushMessage.this, "", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (Util.isNull(runData)) {
                        Util.show("网络错误，请重试！", PushMessage.this);
                        return;
                    }
                    if ((runData + "").contains("success")) {
                        Toast.makeText(PushMessage.this, "成功分享到心情空间", Toast.LENGTH_LONG).show();
                        PushMessage.this.finish();
                    } else {
                        Util.show(runData + "", PushMessage.this);
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.addUserMessageBoard, "userId=" + userId + "&message=" + message.getText().toString() + "&files=" + "" + "&forward=-1" + "&userPaw="
                            + md5Pwd);
                    return web.getPlan();
                }
            });
        }
    }

    private void getAllPersonNumber() {
        //全部

        final CustomProgressDialog pd = CustomProgressDialog.createDialog(this);
        pd.setMessage(pd, "数据加载中...");
        pd.show();
        NewWebAPI.getNewInstance().getAllMyUser(userId, md5Pwd, 1, 2000, "", "", new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                com.alibaba.fastjson.JSONObject res = JSON.parseObject(result.toString());
                String arrlist = res.getString("list");
                List<MemberInfo> list0 = JSON.parseArray(arrlist, MemberInfo.class);

                if (list0.size() != 0) {
                    StringBuilder sb0 = new StringBuilder();
                    for (MemberInfo m : list0) {
                        if (!Util.isNull(m.getPhone()))
                            sb0.append(m.getPhone() + ";");
                    }
                    allNumber = sb0.toString();
                    for (int i = 0; i < list0.size(); i++) {
                        String s = list0.get(i).getName()
                                + ",,,"
                                + list0.get(i).getPhone()
                                + ",,,"
                                + list0.get(i).getRegTime()
                                + ",,,"
                                + list0.get(i).getUserid();
                        all_spit_3.add(s);
                    }
                } else {
                }
                super.success(result);
            }
        });
        //邀请
        NewWebAPI.getNewInstance().getMyInviter(userId, md5Pwd, 1, 99999, "", "", new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                com.alibaba.fastjson.JSONObject res = JSON.parseObject(result.toString());
                String arrlist = res.getString("list");
                List<MemberInfo> list1 = JSON.parseArray(arrlist, MemberInfo.class);

                if (list1.size() != 0) {
                    StringBuilder sb1 = new StringBuilder();
                    for (MemberInfo m : list1) {
                        if (!Util.isNull(m.getPhone())) {
                            sb1.append(m.getPhone() + ";");
                        }
                        String s = m.getName()
                                + ",,,"
                                + m.getPhone()
                                + ",,,"
                                + m.getRegTime()
                                + ",,,"
                                + m.getUserid();
                        yaoqing_spit_3.add(s);
                    }
                    allyaoqingNumber = sb1.toString();
                } else {
                }
                super.success(result);
            }
        });
        //辅导
        NewWebAPI.getNewInstance().getMyMerchants(userId, md5Pwd, 1, 2000, "", "", new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                com.alibaba.fastjson.JSONObject res = JSON.parseObject(result.toString());
                String arrlist = res.getString("list");
                List<MemberInfo> list2 = JSON.parseArray(arrlist, MemberInfo.class);
                if (list2.size() != 0) {
                    StringBuilder sb2 = new StringBuilder();
                    for (MemberInfo m : list2) {
                        if (!Util.isNull(m.getPhone())) {
                            sb2.append(m.getPhone() + ";");
                        }
                        String s = m.getName()
                                + ",,,"
                                + m.getPhone()
                                + ",,,"
                                + m.getRegTime()
                                + ",,,"
                                + m.getUserid();
                        fudao_spit_3.add(s);
                    }
                    allfudaoNumber = sb2.toString();
                } else {
                }
                pd.cancel();
                pd.dismiss();
                super.success(result);
            }
        });
    }

    private void getPersonNum() {

        NewWebAPI.getNewInstance().getWebRequest(
                "/Merchants.aspx?call=getAllPerson&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd(),
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        Util.show("网络异常,请检查!", PushMessage.this);
                        return;
                    }

                    @Override
                    public void success(Object result) {

                        if (Util.isNull(result)) {
                            Util.show("网络异常,请检查!", PushMessage.this);
                            return;
                        }
                        String all = null;
                        String tunjian = null;
                        String fudao = null;
                        try {
                            JSONObject o;
                            try {
                                o = new JSONObject(result.toString());
                                all = o.getString("all");
                                tunjian = o.getString("inviter");
                                fudao = o.getString("merchants");
                            } catch (org.json.JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                        //						JSONObject json = JSON.parseObject(result.toString());
                        //						if (200 != json.getIntValue("code")) {
                        //							Util.show(json.getString("message"),
                        //									AllUserList.this);
                        //							return;
                        //						}

                        //						all = json.getString("all");
                        //						tunjian = json.getString("inviter");
                        //						fudao = json.getString("merchants");

                        all_tj_count.setText(tunjian + "个");
                        all_zs_count.setText(fudao + "个");
                        all_user_count.setText(all + "个");

                    }

                    @Override
                    public void requestEnd() {

                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });

    }


}
