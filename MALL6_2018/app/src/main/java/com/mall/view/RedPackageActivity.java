package com.mall.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.Util;
import com.mall.util.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedPackageActivity extends BaseActivity {

    LinearLayout l1, l2, l3, l4, buttom;
    GridLayout grid;
    TextView l4text, money, count, guize;
    ImageView back;
//    TextView allcleaner;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_package);
        findView();
        initData();
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }

    private void initData() {
        final User user = UserData.getUser();
        if (user == null) {
            l1.setVisibility(0);
            buttom.setVisibility(8);
        } else {
            if (Lin_MallActivity.list.size() != 0) {
                l4.setVisibility(0);
                getL4();
            } else {
                l4.setVisibility(View.GONE);
                if (Double.parseDouble(user.getRed_Seed()) < 500) {
                    l3.setVisibility(0);
                } else {
                    l2.setVisibility(0);
                }
            }
        }
        guize.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                GuiZeDialog guiZeDialog = new GuiZeDialog(RedPackageActivity.this);
                guiZeDialog.show();
            }
        });
//        allcleaner.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Util.asynTask(RedPackageActivity.this, "正在领取所有红包...", new IAsynTask() {
//                    @Override
//                    public Serializable run() {
//                        Web web = new Web(Web.redurl, "/Open_User_All_RedPackage",
//                                "userId=" + UserData.getUser().getUserId() + "&md5Pwd="
//                                        + UserData.getUser().getMd5Pwd() );
//                        String result = web.getPlan();
//                        return result;
//                    }
//
//                    @Override
//                    public void updateUI(Serializable runData) {
//                        String result = (String) runData;
//                        Log.e("红包返回","result"+result);
//                        try {
//                            JSONObject jsonObject = new JSONObject(result);
//                            if (jsonObject.optString("code").equals("200")) {
//                                Toast.makeText(context,jsonObject.optString("msg"),Toast.LENGTH_LONG).show();
//                                User user = UserData.getUser();
//                                Map<String, String> map = new HashMap<String, String>();
//                                map.put("userId", user.getUserId());
//                                map.put("md5Pwd", user.getMd5Pwd());
//                                map.put("type", "13,14");
//                                NewWebAPI.getNewInstance().getWebRequest("/Money.aspx?call=getMoney", map,
//                                        new WebRequestCallBack() {
//
//                                            @Override
//                                            public void success(Object result) {
//                                                // TODO
//                                                // Auto-generated
//                                                // method
//                                                // stub
//                                                super.success(result);
//                                                com.alibaba.fastjson.JSONObject json = JSON
//                                                        .parseObject(result.toString());
//                                                money.setText(json.getString("red_s"));// 红包种子
//                                                count.setText(json.getString("red_c"));// 现金红包
//                                                UserData.getUser().setRed_Cash(json.getString("red_c"));
//                                                UserData.getUser().setRed_Seed(json.getString("red_s"));
//                                                getTite();
//                                            }
//                                        });
//
//                            }else {
//                                Toast.makeText(context,jsonObject.optString("msg"),Toast.LENGTH_LONG).show();
//                            }
//                        }catch (Exception e){
//
//                        }
//
//
//                    }
//                });
//            }
//        });
    }


    private void getTite(){
        if (com.mall.util.Util.checkLoginOrNot()) {
            com.mall.util.Util.asynTask1(this, "红包查询中", new com.mall.util.IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    String result = (String) runData;
                    Log.e("---", result);
                    Lin_MallActivity.list.clear();
                    try {
                        JSONArray jsonArray = new JSONObject(result)
                                .getJSONArray("list");
                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            RedPacket redPacket = gson.fromJson(
                                    jsonObject.toString(), RedPacket.class);
                            Lin_MallActivity.list.add(redPacket);
                        }
                        if (jsonArray.length()==0){
                            Lin_MallActivity.list.clear();
                        }
                        getL4();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public Serializable run() {
                    // TODO Auto-generated method stub
                    Web web = new Web(Web.redurl, "/Get_RedPackage_Allday",
                            "userId=" + UserData.getUser().getUserId()
                                    + "&md5Pwd="
                                    + UserData.getUser().getMd5Pwd());
                    String result = web.getPlan();
                    return result;
                }

            });
        }

    }

    private void getL4() {
//		 final List<RedPacket> list=Lin_MallActivity.list;
        final List<RedPacket> list = new ArrayList<>();
        list.addAll(Lin_MallActivity.list);
        size = list.size();
        l4text.setText("你有" + size + "个红包未领取！");

        try{
            grid.removeAllViews();
        }catch (Exception e){

        }


        final int screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        for (int i = 0; i < size; i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.red_item1, null, false);
            v.setMinimumWidth(screenWidth / 3);
            TextView time = (TextView) v.findViewById(R.id.time);
            ImageView get = (ImageView) v.findViewById(R.id.get);
            get.setTag(i);
            get.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View view) {
                    // TODO Auto-generated method stub

                    //赵超2017.5.8添加日志打印
                    Log.e("msg", "你点击了‘立即领取’按钮，领取红包!");

                    final OpenRedDialog o = new OpenRedDialog(RedPackageActivity.this);

                    o.show();


                    o.setOnClickListener(new OpenRedDialog.OnOpenListener() {

                        @Override
                        public void onClick() {
                            // TODO Auto-generated method stub
                            final int i = Integer.parseInt(view.getTag() + "");
                            Util.asynTask(RedPackageActivity.this, "红包领取中...", new IAsynTask() {
                                @Override
                                public void updateUI(Serializable runData) {
                                    String result = (String) runData;
                                    Log.e("---", result);
                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        if (jsonObject.optString("code").equals("200")) {
                                            o.setMoney(jsonObject.optString("money") + "元");
                                            User user = UserData.getUser();
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("userId", user.getUserId());
                                            map.put("md5Pwd", user.getMd5Pwd());
                                            map.put("type", "13,14");
                                            NewWebAPI.getNewInstance().getWebRequest("/Money.aspx?call=getMoney", map,
                                                    new WebRequestCallBack() {

                                                        @Override
                                                        public void success(Object result) {
                                                            // TODO
                                                            // Auto-generated
                                                            // method
                                                            // stub
                                                            super.success(result);
                                                            com.alibaba.fastjson.JSONObject json = JSON
                                                                    .parseObject(result.toString());
                                                            money.setText(json.getString("red_s"));// 红包种子
                                                            count.setText(json.getString("red_c"));// 现金红包
                                                            UserData.getUser().setRed_Cash(json.getString("red_c"));
                                                            UserData.getUser().setRed_Seed(json.getString("red_s"));
                                                        }
                                                    });
                                            o.startAnimator();
                                            grid.removeViewAt(i);

                                            Log.e("移除", "位置" + i);
                                            View v = LayoutInflater.from(RedPackageActivity.this)
                                                    .inflate(R.layout.red_item2, null, false);
                                            v.setMinimumWidth(screenWidth / 3);
                                            TextView date = (TextView) v.findViewById(R.id.date);
                                            date.setText(com.mall.util.Util.getDateStr(list.get(i).getDate().split(" ")[0], 1));
                                            size -= 1;
                                            Lin_MallActivity.list.remove(list.get(i));

                                            Log.e("移除后红包种子的长度", list.size() + "");
                                            l4text.setText("你有" + size + "个红包未领取！");
                                            if (size==0){
//                                                allcleaner.setVisibility(View.GONE);
                                            }
                                            Log.e("添加", "位置" + i);
                                            grid.addView(v, i);

                                        } else {
                                            Toast.makeText(RedPackageActivity.this, jsonObject.optString("msg"), 1)
                                                    .show();
                                        }
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch
                                        // block
                                        e.printStackTrace();
                                        Log.e("请求异常", e.toString());
                                    }
                                }

                                @Override
                                public Serializable run() {


                                    Log.e("红包种子长度", Lin_MallActivity.list.size() + "");

                                    Log.e("下标", "位置" + i);


                                    String date = list.get(i).getDate();
                                    String id = list.get(i).getId();
                                    Log.e("红包列表", date + "!!!!!" + id);


                                    Web web = new Web(Web.redurl, "/Open_RedPackage",
                                            "userId=" + UserData.getUser().getUserId() + "&md5Pwd="
                                                    + UserData.getUser().getMd5Pwd() + "&date="
                                                    + date + "&id="
                                                    + id);
                                    String result = web.getPlan();
                                    return result;
                                }
                            });
                        }
                    });
                }
            });

//			Log.e("红包数据",com.mall.util.Util.getDateStr(Lin_MallActivity.list.get(i).getDate().split(" ")[0], 1));
            time.setText(com.mall.util.Util.getDateStr(list.get(i).getDate().split(" ")[0], 1));
            grid.addView(v);
        }
    }

    private void findView() {
        // TODO Auto-generated method stub
        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        l3 = findViewById(R.id.l3);
        l4 = findViewById(R.id.l4);
        buttom = findViewById(R.id.buttom);
        grid = findViewById(R.id.grid);
        l4text = findViewById(R.id.l4text);
        back = findViewById(R.id.back);
        money = findViewById(R.id.money);
        count = findViewById(R.id.count);
        guize = findViewById(R.id.guize);
//        allcleaner = findViewById(R.id.allcleaner);
//        allcleaner.setBackground(SelectorFactory.newShapeSelector()
//                .setDefaultBgColor(Color.parseColor("#fde627"))
//                .setStrokeWidth(com.mall.util.Util.dpToPx(context, 1))
//                .setCornerRadius(com.mall.util.Util.dpToPx(context, 3))
//                .create());
    }

    public void click(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.register:
                Intent intent = new Intent(this, RegisterFrame.class);
                startActivity(intent);
                break;
            case R.id.p1:
                String[] keys = new String[]{"parentName", "userKey", "yeMoney"};
                String[] vals = new String[]{"红包种子账户", "hb", money.getText().toString()};
                Util.showIntent(this, AccountListFrame.class, keys, vals);
                break;
            case R.id.p2:
                String[] keys1 = new String[]{"parentName", "userKey", "yeMoney"};
                String[] vals1 = new String[]{"现金红包账户", "xj", count.getText().toString()};
                Util.showIntent(this, AccountListFrame.class, keys1, vals1);
                break;
            case R.id.sj:
                Util.showIntent(this, FindLMSJFrame.class);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        if (UserData.getUser() != null) {
            money.setText(UserData.getUser().getRed_Seed());
            count.setText(UserData.getUser().getRed_Cash());
        }
        super.onStart();
    }
}
