package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.model.UserInfo;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.WebRequestCallBack;
import com.mall.pushmessage.PushMessage;
import com.mall.util.CharacterParser;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MemberFrame extends Activity {

    private Handler handler = new Handler();
    private int p = 1;
    boolean isFoot = false; // 用来判断是否已滑动到底部
    // private MemberAdapterListener adapter = null;

    private BaseMallAdapter<UserInfo> adapter = null;

    private List<UserInfo> UserinfoList = null;
    private List<UserInfo> namelist = new ArrayList<>();
    private List<UserInfo> timelist = new ArrayList<>();
    private List<UserInfo> shangjialist = new ArrayList<>();
    private List<UserInfo> chuangkelist = new ArrayList<>();

    int allnumber = 0;
    int shangjianumber = 0;
    int chuangkenumber = 0;

    @ViewInject(R.id.huiyuannumber)
    private TextView huiyuannumber_tv;

    @ViewInject(R.id.chuangkenumber)
    private TextView chuangkenumber_tv;

    @ViewInject(R.id.shangjianumber)
    private TextView shangjianumber_tv;
    @ViewInject(R.id.my_zhaoshang)
    private TextView my_zhaishang;
    @ViewInject(R.id.my_tuijian)
    private TextView my_tuijian;
    @ViewInject(R.id.my_tixi)
    private TextView my_tixi;

    private View yaoqingLayout;
    private TextView center;
    private View fudaoLayout;
    private View tixi_layout;
    // private int zhaoshang;
    // private int tuijian;

    @ViewInject(R.id.juesefenglei)
    private View juesefenglei;

    @ViewInject(R.id.re2)
    private View re2;

    @ViewInject(R.id.member)
    private ListView listView;

    @ViewInject(R.id.letter_dialog)
    private TextView letter_dialog;

    @ViewInject(R.id.sidrbar)
    private SideBars sidrbar;

    @ViewInject(R.id.paixunlin)
    private View paixunlin;

    private TextView search;
    private ImageView speak;
    private EditText search_edit;

    private Context context;

    String type = "";
    int size = 999;

    String all = "";
    String tunjian = "";
    String fudao = "";
    String txuser = "";

    String title = "我的数据";

    PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_frame);
        ViewUtils.inject(this);
        characterParser = CharacterParser.getInstance();
        init();

        pinyinComparator = new PinyinComparator();
        Intent intent = getIntent();

        String str = intent.getStringExtra("title");
        if (!Util.isNull(str)) {
            title = str;
        }

        String str1 = intent.getStringExtra("type");

        if (!Util.isNull(str1)) {
            type = str1;
        }

        int nu = intent.getIntExtra("p", 0);
        p = nu;


        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setAlwaysDrawnWithCacheEnabled(true);

        Util.initTitle(this, title, new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initAdapter();

        if (title.equals("我的辅导")) {
            re2.setVisibility(View.GONE);
            juesefenglei.setVisibility(View.VISIBLE);
            getZhaoshangTuijian(type, p);
        } else if (title.equals("我的数据")) {
            juesefenglei.setVisibility(View.GONE);
            re2.setVisibility(View.VISIBLE);
            bind(listView, 1);
        } else if (title.equals("我的邀请")) {
            re2.setVisibility(View.GONE);
            juesefenglei.setVisibility(View.VISIBLE);
            getZhaoshangTuijian(type, p);
        } else if (title.equals("我的体系")) {
            re2.setVisibility(View.GONE);
            juesefenglei.setVisibility(View.VISIBLE);
            getTixi(type, p);
        }

        getPersonNum();

        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                // 判断是否已滑动或者处于底部
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && isFoot) {
                    // 滚动到底部
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {

                    }

                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    isFoot = true;
                }
            }
        });

        setlistener();
        initSide();

    }

    /**
     * 为ListView填充数据
     *
     * @param
     * @return
     */

    private CharacterParser characterParser;

    @SuppressLint("DefaultLocale")
    private List<UserInfo> filledData(List<UserInfo> infoList) {

        for (int i = 0; i < infoList.size(); i++) {

            UserInfo userInfo = infoList.get(i);

            String name = userInfo.getName();
            if (Util.isNull(name)) {
                name = userInfo.getUserid();
            }

            if (!Util.isNull(name)) {

                // 汉字转换成拼音
                String pinyin = characterParser.getSelling(name);

                String sortString = pinyin.substring(0, 1).toUpperCase();

                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    userInfo.setSortLetter(sortString.toUpperCase());
                } else {
                    userInfo.setSortLetter("#");
                }
            } else {
                userInfo.setSortLetter("#");
            }


        }
        return infoList;

    }


    private void initSide() {
        sidrbar.setOnTouchingLetterChangedListener(new SideBars.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // TODO Auto-generated method stub
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {

                    // shopadapter = (ShopUser) member_list.getAdapter();
                    if (null == adapter) {
                        return;
                    }
                    View item = adapter.getView(0, null, listView);
                    item.measure(0, 0);
                    int ih = item.getMeasuredHeight();
//					if (adapter.getCount() > listView.getMeasuredHeight() / ih) {
//						adapter.setState("gone", position);
//						adapter.notifyDataSetChanged();
//					}

                    listView.setSelection(position);

                }
            }
        });
        sidrbar.setTextView(letter_dialog);
    }

    @OnClick({R.id.paixunlin})
    private void click1(View view) {
        sortDialog();

    }

    public void sortDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_client_sort,
                null);
        TextView sortDate = (TextView) view
                .findViewById(R.id.dialog_client_sort_date);
        TextView name = (TextView) view
                .findViewById(R.id.dialog_client_sort_letter);
        TextView js = (TextView) view.findViewById(R.id.dialog_client_sort_js);


//		if (title.equals("我的数据")){
        js.setVisibility(View.GONE);
//		}

        final Dialog dialog = new Dialog(MemberFrame.this, R.style.dialog);
        dialog.setContentView(view);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        dialog.show();
        android.view.WindowManager.LayoutParams parm = dialog.getWindow()
                .getAttributes();
        parm.width = w / 5 * 4;
        dialog.getWindow().setAttributes(parm);
        sortDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generted method stub
                dialog.dismiss();
                sidrbar.setVisibility(View.GONE);
                adapter.clear();
                adapter.add(timelist);
                adapter.updateUI();
                inittextstate(null, true);

            }
        });
        name.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                sidrbar.setVisibility(View.VISIBLE);
                adapter.clear();
                adapter.add(namelist);
                adapter.updateUI();
                inittextstate(null, true);

            }
        });
        js.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                sidrbar.setVisibility(View.GONE);
                inittextstate(null, true);

            }
        });

    }


    @OnClick({R.id.huiyuannumber, R.id.chuangkenumber, R.id.shangjianumber})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.huiyuannumber:
                adapter.clear();
                adapter.add(timelist);
                adapter.updateUI();
                break;
            case R.id.chuangkenumber:
                adapter.clear();
                adapter.add(chuangkelist);
                adapter.updateUI();
                break;
            case R.id.shangjianumber:
                adapter.clear();
                adapter.add(shangjialist);
                adapter.updateUI();
                break;
        }
        sidrbar.setVisibility(View.GONE);
        inittextstate(view, false);
    }

    private void inittextstate(View view, boolean isclean) {

        huiyuannumber_tv.setTextColor(Color.parseColor("#ffffff"));
        chuangkenumber_tv.setTextColor(Color.parseColor("#ffffff"));
        shangjianumber_tv.setTextColor(Color.parseColor("#ffffff"));

        if (isclean) {
            return;
        }

        switch (view.getId()) {
            case R.id.huiyuannumber:
                huiyuannumber_tv.setTextColor(Color.parseColor("#FF2145"));
                break;
            case R.id.chuangkenumber:
                chuangkenumber_tv.setTextColor(Color.parseColor("#FF2145"));
                break;
            case R.id.shangjianumber:
                shangjianumber_tv.setTextColor(Color.parseColor("#FF2145"));
                break;
        }

    }

    private void getPersonNum() {

        NewWebAPI.getNewInstance().getWebRequest("/Merchants.aspx?call=getAllPerson&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd(),
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", MemberFrame.this);
                        return;
                    }

                    @Override
                    public void success(Object result) {

                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", MemberFrame.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), MemberFrame.this);
                            return;
                        }

                        all = json.getString("all");
                        tunjian = json.getString("inviter");
                        fudao = json.getString("merchants");
                        txuser = json.getString("txuser");

                        my_tuijian.setText(tunjian + "人");
                        my_zhaishang.setText(fudao + "人");
                        my_tixi.setText(txuser + "人");
                    }

                    @Override
                    public void requestEnd() {

                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });

    }

    private void initAdapter() {
        adapter = (BaseMallAdapter<UserInfo>) listView.getAdapter();
        if (null == adapter) {

            adapter = new BaseMallAdapter<UserInfo>(R.layout.member_item, MemberFrame.this, UserinfoList) {

                public int getPositionForSection(int section) {
                    for (int i = 0; i < getCount(); i++) {
                        String sortStr = UserinfoList.get(i).getSortLetter();
                        char firstChar = sortStr.toUpperCase().charAt(0);
                        if (firstChar == section) {
                            return i;
                        }
                    }
                    return -1;

                }

                @Override
                public View getView(final int position, View convertView, ViewGroup parent, final UserInfo t) {
                    // TODO Auto-generated method stub
                    setText(R.id.member_name, t.getUserid());
                    TextView name = convertView.findViewById(R.id.name_);
                    if (t.getName().equals("")) {
                        setText(R.id.name_, "暂无姓名");
                        name.setTextColor(Color.parseColor("#dedede"));
                    } else {
                        setText(R.id.name_, t.getName());
                        name.setTextColor(Color.parseColor("#484848"));
                    }

                    final TextView phone = convertView.findViewById(R.id.phone_);
                    final TextView letter = convertView.findViewById(R.id.item_shopuser_tvletter);
                    phone.setText(t.getPhone());
                    if (!Util.isNull(phone.getText().toString())) {
                        phone.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Util.doPhone(phone.getText().toString(), MemberFrame.this);
                            }
                        });
                    }
                    Log.e("数据等级", t.getSortLetter() + "LLLL");


                    if (Util.isNull(t.getSortLetter())) {
                        letter.setVisibility(View.GONE);
                    } else {
                        int section = t.getSortLetter().charAt(0);

                        if (position == getPositionForSection(section)) {

                            //
                            // Message.obtain(handler, LETTER,
                            // s.getSortLetters().charAt(0)).sendToTarget();
                            // Message.obtain(handler, POSITION, arg0).sendToTarget();
                            letter.setVisibility(View.VISIBLE);

                            letter.setText(t.getSortLetter());


                        } else {
                            letter.setVisibility(View.GONE);
                        }
                    }


                    convertView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            String username = t.getName() + "";
                            if (Util.isNull(username)) {
                                username = t.getUserid() + "";
                            }
                            showMessage(username, t.getSex() + "",
                                    t.getPhone() + "",
                                    t.getDate() + "",
                                    t.getUserLevel() + "",
                                    t.getShopType() + "");

                        }

                    });
                    convertView.setOnLongClickListener(new OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            final Dialog dialog = new Dialog(MemberFrame.this, R.style.dialog);
                            dialog.setContentView(R.layout.dialog_onlongclick_mydata);
                            View layout = dialog.findViewById(R.id.dialog_onlong_ll_layout);
                            LayoutParams lp = (LayoutParams) layout.getLayoutParams();
                            lp.width = getWindowManager().getDefaultDisplay().getWidth() / 2;
                            lp.height = lp.WRAP_CONTENT;
                            layout.setLayoutParams(lp);
                            TextView sms = (TextView) dialog.findViewById(R.id.dialog_sms);
                            TextView call = (TextView) dialog.findViewById(R.id.dialog_call);
                            TextView push = (TextView) dialog.findViewById(R.id.dialog_push);
                            push.setVisibility(View.GONE);
                            User user = UserData.getUser();
                            int levelid = Integer.parseInt(user.getLevelId());
                            int shopTypeId = Integer.parseInt(user.getShopTypeId());
                            if (5 == levelid || 6 == levelid) {
                                push.setVisibility(View.VISIBLE);

                            } else {
                                if (shopTypeId >= 3 && shopTypeId < 10) {
                                    push.setVisibility(View.VISIBLE);

                                }
                            }
                            sms.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    dialog.dismiss();

                                    Uri smsUri = Uri.parse("smsto:" + phone.getText().toString());
                                    Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
                                    // intent.putExtra("sms_body",
                                    // "请输入内容...");
                                    startActivity(intent);

                                }
                            });
                            call.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    if (!Util.isNull(phone.getText().toString())) {
                                        Util.doPhone(phone.getText().toString(), MemberFrame.this);
                                    } else {
                                        Util.show("你选择的用户，暂无电话号码！", MemberFrame.this);
                                    }

                                }
                            });
                            push.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    dialog.dismiss();
                                    Intent intent = new Intent(MemberFrame.this, PushMessage.class);
                                    String types = "";
                                    if ("我的辅导".equals(center.getText().toString())) {
                                        types = "fudao";
                                    }
                                    if ("我的邀请".equals(center.getText().toString())) {
                                        types = "yaoqing";
                                    }
                                    if ("我的数据".equals(center.getText().toString())) {
                                        types = "alldata";
                                    }
                                    intent.putExtra("types", types);
                                    String s = t.getName() + ",,," + t.getUserid() + ",,," + t.getDate()
                                            + t.getUserid();
                                    intent.putExtra("itemInfo", s);
                                    intent.putExtra("from", "mydata");
                                    startActivity(intent);
                                }
                            });
                            dialog.show();
                            return true;
                        }
                    });
                    return convertView;
                }
            };
            listView.setAdapter(adapter);
        }

    }

    public void showMessage(String name, String sex, String phone, String date, String level, String shopType) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MemberFrame.this);
        View layout = LayoutInflater.from(MemberFrame.this).inflate(R.layout.member_detail, null);
        dialog.setIcon(android.R.drawable.ic_dialog_dialer);
        dialog.setTitle("详细信息:");
        dialog.setView(layout);
        // 得到控件，并赋值
        TextView mname = (TextView) layout.findViewById(R.id.mname);
        TextView mphone = (TextView) layout.findViewById(R.id.mphone);
        TextView msex = (TextView) layout.findViewById(R.id.msex);
        TextView mlevel = (TextView) layout.findViewById(R.id.mlevel);
        TextView mdate = (TextView) layout.findViewById(R.id.mdate);
        mname.setText(name);
        mphone.setText(phone);
        msex.setText(sex);
        if (level.equals("城市经理(商家)")) {
            level = "联盟商家";
        }
        mlevel.setText(level);
        mdate.setText(date);
        dialog.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void setlistener() {
        // TODO Auto-generated method stub
        fudaoLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(MemberFrame.this, MemberFrame.class);
                intent.putExtra("title", "我的辅导");
                intent.putExtra("type", "2");
                intent.putExtra("p", 1);
                startActivity(intent);
            }
        });
        tixi_layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MemberFrame.this, MemberFrame.class);
                intent.putExtra("title", "我的体系");
                intent.putExtra("type", "3");
                intent.putExtra("p", 1);
                startActivity(intent);
            }
        });
        yaoqingLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(MemberFrame.this, MemberFrame.class);
                intent.putExtra("title", "我的邀请");
                intent.putExtra("type", "1");
                intent.putExtra("p", 1);
                startActivity(intent);
            }
        });

        speak.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Util.startVoiceRecognition(MemberFrame.this, new DialogRecognitionListener() {
                    @Override
                    public void onResults(Bundle results) {
                        ArrayList<String> rs = results != null ? results.getStringArrayList(RESULTS_RECOGNITION) : null;
                        if (rs != null && rs.size() > 0) {
                            String str = rs.get(0).replace("。", "").replace("，", "");
                            search_edit.setText(str);
                            if (Util.isNull(search_edit.getText().toString())) {
                                p = 1;
                                bind(listView, p);

                            } else {
                                p = 1;
                                goSearch(search_edit.getText().toString(), p);
                            }
                        }
                    }
                });
            }
        });

        search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String searchStr = search_edit.getText().toString();
                sidrbar.setVisibility(View.GONE);
                if (Util.isNull(searchStr)) {
                    p = 1;
                    bind(listView, p);

                } else {
                    p = 1;
                    goSearch(searchStr, p);
                }

            }
        });

    }

    public class PinyinComparator implements Comparator<UserInfo> {
        public int compare(UserInfo o1, UserInfo o2) {
            if (o1.getSortLetter().equals("@")
                    || o2.getSortLetter().equals("#")) {
                return -1;
            } else if (o1.getSortLetter().equals("#")
                    || o2.getSortLetter().equals("@")) {
                return 1;
            } else {
                return o1.getSortLetter().compareTo(o2.getSortLetter());
            }
        }
    }

    private void getZhaoshangTuijian(final String type, final int page) {
        if (page == 1) {
            adapter.clear();
            adapter.updateUI();
        }
        User u = UserData.getUser();
        String calltype = "";
        if ("1".equals(type)) {
            calltype = "getMyInviter";
        }
        if ("2".equals(type)) {
            calltype = "getMyMerchants";
        }
        final CustomProgressDialog dialog = Util.showProgress("数据加载中....", MemberFrame.this);
        NewWebAPI.getNewInstance().getWebRequest("/Merchants.aspx?call=" + calltype + "&page=" + page + "&size=" + size
                + "&userId=" + u.getUserId() + "&md5Pwd=" + u.getMd5Pwd(), new NewWebAPIRequestCallback() {

            @Override
            public void timeout() {
                dialog.cancel();
                dialog.dismiss();
                Util.show("网络超时！", MemberFrame.this);
                return;
            }

            @Override
            public void success(Object result) {
                dialog.cancel();
                dialog.dismiss();
                if (Util.isNull(result)) {
                    Util.show("网络异常，请重试！", MemberFrame.this);
                    return;
                }
                JSONObject json = JSON.parseObject(result.toString());
                if (200 != json.getIntValue("code")) {
                    Util.show(json.getString("message"), MemberFrame.this);
                    return;
                }
                UserinfoList = JSON.parseArray(json.getString("list"), UserInfo.class);
                if (null != UserinfoList && UserinfoList.size() > 0) {
                    try {
                        timelist = Util.deepCopy(UserinfoList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    Log.e("加了拼音分级", "LL");
                    UserinfoList = filledData(UserinfoList);
                    Collections.sort(UserinfoList, pinyinComparator);
                    Log.e("加了拼音分级", UserinfoList.get(0).getSortLetter() + "LL");
                    adapter.add(timelist);
                    adapter.updateUI();
                    namelist.clear();
                    namelist.addAll(UserinfoList);
                    huiyuannumber_tv.setText("会员：" + UserinfoList.size());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i = 0; i < UserinfoList.size(); i++) {
                                    UserInfo bean = UserinfoList.get(i);
                                    String level = bean.getUserLevel() + "";
                                    if (level.equals("城市总监") || level.equals("城市经理")) {
                                        chuangkenumber++;  // 创客
                                        chuangkelist.add(bean);
                                    } else if (level.equals("联盟商家")) {
                                        shangjianumber++;  //商家
                                        shangjialist.add(bean);
                                    }
                                }
                                handler.post(new Runnable() {
                                    public void run() {
                                        Log.e("最后长度", "会员：" + UserinfoList.size() + "创客：" + chuangkelist.size() + "商家：" + shangjialist.size());
                                        chuangkenumber_tv.setText("创客：" + chuangkenumber);
                                        shangjianumber_tv.setText("商家：" + shangjianumber);
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    Util.show("暂无更多数据", MemberFrame.this);
                }
            }

            @Override
            public void requestEnd() {
                dialog.cancel();
                dialog.dismiss();
            }

            @Override
            public void fail(Throwable e) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
    }

    private void getTixi(String type, int page) {
        if (page == 1) {
            adapter.clear();
            adapter.updateUI();
        }
        User u = UserData.getUser();
        final CustomProgressDialog dialog = Util.showProgress("数据加载中....", MemberFrame.this);
        NewWebAPI.getNewInstance().getTiXiUser(u.getUserId(), u.getMd5Pwd(), page + "", size + "", "",
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        dialog.cancel();
                        dialog.dismiss();
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", MemberFrame.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), MemberFrame.this);
                            return;
                        }
                        String jsonStr = json.getString("list").replace("level", "userLevel").replace("mobilePhone", "phone");
                        UserinfoList = JSON.parseArray(jsonStr, UserInfo.class);
                        if (null != UserinfoList && UserinfoList.size() > 0) {
                            try {
                                timelist = Util.deepCopy(UserinfoList);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            Log.e("加了拼音分级", "LL");
                            UserinfoList = filledData(UserinfoList);
                            Collections.sort(UserinfoList, pinyinComparator);
                            Log.e("加了拼音分级", UserinfoList.get(0).getSortLetter() + "LL");
                            adapter.add(timelist);
                            adapter.updateUI();
                            namelist.clear();
                            namelist.addAll(UserinfoList);
                            huiyuannumber_tv.setText("会员：" + UserinfoList.size());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        for (int i = 0; i < UserinfoList.size(); i++) {
                                            UserInfo bean = UserinfoList.get(i);
                                            String level = bean.getUserLevel() + "";
                                            if (level.equals("城市总监") || level.equals("城市经理")) {
                                                chuangkenumber++;  // 创客
                                                chuangkelist.add(bean);
                                            } else if (level.equals("联盟商家")) {
                                                shangjianumber++;  //商家
                                                shangjialist.add(bean);
                                            }

                                        }
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Log.e("最后长度", "会员：" + UserinfoList.size() + "创客：" + chuangkelist.size() + "商家：" + shangjialist.size());
                                                chuangkenumber_tv.setText("创客：" + chuangkenumber);
                                                shangjianumber_tv.setText("商家：" + shangjianumber);
                                            }
                                        });

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                        } else {
                            Util.show("暂无更多数据", MemberFrame.this);
                        }
                    }

                    @Override
                    public void timeout() {
                        dialog.cancel();
                        dialog.dismiss();
                        Util.show("网络超时！", MemberFrame.this);
                        return;
                    }

                    @Override
                    public void requestEnd() {
                        dialog.cancel();
                        dialog.dismiss();
                    }

                    @Override
                    public void fail(Throwable e) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });
    }

    public void bind(final ListView listView, final int page) {
        User u = UserData.getUser();
        if (1 == page) {
            adapter.clear();

            adapter.updateUI();
        }
        final CustomProgressDialog dialog = Util.showProgress("数据加载中....", MemberFrame.this);

        NewWebAPI.getNewInstance().getWebRequest("/Merchants.aspx?call=getAllUser" + "&page=" + page + "&size=" + size
                + "&userId=" + u.getUserId() + "&md5Pwd=" + u.getMd5Pwd(), new NewWebAPIRequestCallback() {

            @Override
            public void timeout() {
                Util.show("网络超时！", MemberFrame.this);
                dialog.cancel();
                dialog.dismiss();
                return;
            }

            @Override
            public void success(Object result) {

                dialog.cancel();
                dialog.dismiss();
                if (Util.isNull(result)) {
                    Util.show("网络异常，请重试！", MemberFrame.this);
                    return;
                }
                JSONObject json = JSON.parseObject(result.toString());
                if (200 != json.getIntValue("code")) {
                    Util.show(json.getString("message"), MemberFrame.this);
                    return;
                }


                UserinfoList = JSON.parseArray(json.getString("list"), UserInfo.class);
                if (null != UserinfoList && UserinfoList.size() > 0) {
                    try {
                        timelist = Util.deepCopy(UserinfoList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    Log.e("加了拼音分级", "LL");
                    UserinfoList = filledData(UserinfoList);

                    Collections.sort(UserinfoList, pinyinComparator);

                    Log.e("加了拼音分级", UserinfoList.get(0).getSortLetter() + "LL");

                    namelist.clear();
                    namelist.addAll(UserinfoList);

                    adapter.add(timelist);
                    adapter.updateUI();

                } else {
                    Util.show("暂无更多数据", MemberFrame.this);
                }

            }

            @Override
            public void requestEnd() {
                dialog.cancel();
                dialog.dismiss();
            }

            @Override
            public void fail(Throwable e) {

                dialog.cancel();
                dialog.dismiss();
                Util.show("网络异常，请重试！", MemberFrame.this);
                return;
            }
        });
    }

    public void setAdapter(final ListView listView, final MemberAdapterListener ma) {
        handler.post(new Runnable() {
            public void run() {
                listView.setAdapter(ma);
            }
        });
    }

    public void init() {
        center = this.findViewById(R.id.center);
        fudaoLayout = this.findViewById(R.id.fudao_layout);
        yaoqingLayout = this.findViewById(R.id.yaoqing_layout);
        tixi_layout = this.findViewById(R.id.tixi_layout);
        search_edit = this.findViewById(R.id.search_edit);
        search = this.findViewById(R.id.member_the_search);
        speak = this.findViewById(R.id.member_the_speak);
        if (null != UserData.getUser()) {
            if (UserData.getUser().getLevelId().equals("5")) {
                tixi_layout.setVisibility(View.VISIBLE);
            } else {
                tixi_layout.setVisibility(View.GONE);
            }
        } else {
            Util.showIntent("对不起，您还没有登录。", this, LoginFrame.class);
            return;
        }

    }


    private void goSearch(final String search, final int page) {

        User u = UserData.getUser();
        String calltype = "";
        if (page == 1) {
            adapter.clear();
            adapter.updateUI();
        }
        if ("我的体系".equals(center.getText().toString())) {
            final CustomProgressDialog dialog = Util.showProgress("数据加载中....", MemberFrame.this);
            NewWebAPI.getNewInstance().getTiXiUser(u.getUserId(), u.getMd5Pwd(), page + "", size + "", search,
                    new WebRequestCallBack() {
                        @Override
                        public void success(Object result) {
                            dialog.cancel();
                            dialog.dismiss();
                            if (Util.isNull(result)) {
                                Util.show("网络异常，请重试！", MemberFrame.this);
                                return;
                            }
                            JSONObject json = JSON.parseObject(result.toString());
                            if (200 != json.getIntValue("code")) {
                                Util.show(json.getString("message"), MemberFrame.this);
                                return;
                            }
                            String jsonStr = json.getString("list").replace("level", "userLevel").replace("mobilePhone", "phone");
                            UserinfoList = JSON.parseArray(jsonStr, UserInfo.class);
                            if (null != UserinfoList && UserinfoList.size() > 0) {
                                try {
                                    timelist = Util.deepCopy(UserinfoList);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                                Log.e("加了拼音分级", "LL");
                                UserinfoList = filledData(UserinfoList);
                                Collections.sort(UserinfoList, pinyinComparator);
                                Log.e("加了拼音分级", UserinfoList.get(0).getSortLetter() + "LL");
                                adapter.add(timelist);
                                adapter.updateUI();
                                namelist.clear();
                                namelist.addAll(UserinfoList);
                            } else {
                                Util.show("暂无更多数据", MemberFrame.this);
                            }
                        }

                        @Override
                        public void timeout() {
                            dialog.cancel();
                            dialog.dismiss();
                            Util.show("网络超时！", MemberFrame.this);
                            return;
                        }

                        @Override
                        public void requestEnd() {
                            dialog.cancel();
                            dialog.dismiss();
                        }

                        @Override
                        public void fail(Throwable e) {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });
        } else {
            if ("我的辅导".equals(center.getText().toString())) {
                calltype = "searchMyMerchants";
            }
            if ("我的邀请".equals(center.getText().toString())) {
                calltype = "searchMyInviter";
            }
            if ("我的数据".equals(center.getText().toString())) {
                calltype = "searchMyData";
            }
            final CustomProgressDialog dialog = Util.showProgress("数据加载中....", MemberFrame.this);
            NewWebAPI.getNewInstance()
                    .getWebRequest(
                            "/merchants.aspx?call=" + calltype + "&search=" + search + "&userId=" + u.getUserId()
                                    + "&md5Pwd=" + u.getMd5Pwd() + "&page=" + page + "&size=" + size,
                            new NewWebAPIRequestCallback() {

                                @Override
                                public void timeout() {
                                    dialog.cancel();
                                    dialog.dismiss();
                                    Util.show("网络超时！", MemberFrame.this);
                                    return;
                                }

                                @Override
                                public void success(Object result) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                    if (Util.isNull(result)) {
                                        Util.show("网络异常，请重试！", MemberFrame.this);
                                        return;
                                    }
                                    JSONObject json = JSON.parseObject(result.toString());
                                    if (200 != json.getIntValue("code")) {
                                        Util.show(json.getString("message"), MemberFrame.this);
                                        return;
                                    }

                                    UserinfoList = JSON.parseArray(json.getString("list"), UserInfo.class);
                                    if (null != UserinfoList && UserinfoList.size() > 0) {
                                        try {
                                            timelist = Util.deepCopy(UserinfoList);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        Log.e("加了拼音分级", "LL");
                                        UserinfoList = filledData(UserinfoList);

                                        Collections.sort(UserinfoList, pinyinComparator);

                                        Log.e("加了拼音分级", UserinfoList.get(0).getSortLetter() + "LL");


                                        adapter.add(timelist);
                                        adapter.updateUI();

                                        namelist.clear();
                                        namelist.addAll(UserinfoList);
                                    } else {
                                        Util.show("暂无更多数据", MemberFrame.this);
                                    }

                                }

                                @Override
                                public void requestEnd() {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }

                                @Override
                                public void fail(Throwable e) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            });
        }
    }
}

class MemberAdapterListener extends SimpleAdapter {
    private Context context;

    private List<Map<String, String>> data;

    public MemberAdapterListener(Context context, List<Map<String, String>> data, int resource, String[] from,
                                 int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.data = data;
    }

    public void addView(List<Map<String, String>> data1) {
        this.data.addAll(data1);
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.data.clear();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (0 == this.data.size()) {
            synchronized (this) {
                if (0 == this.data.size()) {
                    return convertView;
                }
            }
        }
        View v = super.getView(position, convertView, parent);
        final TextView phone = (TextView) v.findViewById(R.id.rname);
        phone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                String phoneNumber = phone.getText().toString();
                Util.doPhone(phoneNumber, context);
                v.setEnabled(true);
            }
        });
        v.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                showMessage(data.get(position).get("name") + "", data.get(position).get("sex") + "",
                        data.get(position).get("phone") + "", data.get(position).get("date") + "",
                        data.get(position).get("olevel") + "", data.get(position).get("shopType") + "");
            }
        });
        return v;
    }

    public void showMessage(String name, String sex, String phone, String date, String level, String shopType) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.member_detail, null);
        dialog.setIcon(android.R.drawable.ic_dialog_dialer);
        dialog.setTitle("详细信息:");
        dialog.setView(layout);
        // 得到控件，并赋值
        TextView mname = layout.findViewById(R.id.mname);
        TextView mphone = layout.findViewById(R.id.mphone);
        TextView msex = layout.findViewById(R.id.msex);
        TextView mlevel = layout.findViewById(R.id.mlevel);
        TextView mdate = layout.findViewById(R.id.mdate);
        mname.setText(name);
        mphone.setText(phone);
        msex.setText(sex);
        if (level.equals("城市经理(商家)")) {
            level = "联盟商家";
        }
        mlevel.setText(shopType);
        mdate.setText(date);
        dialog.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
