package com.mall.view.topupcenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bpj.lazyfragment.LazyFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.Game;
import com.mall.net.Web;
import com.mall.officeonline.ShopOfficeList;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.App;
import com.mall.view.GameCommitFream;
import com.mall.view.Lin_MainFrame;
import com.mall.view.LoginFrame;
import com.mall.view.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/9/19.
 */

public class QQGameRechargeFragment extends LazyFragment {



    private Context context;



    @ViewInject(R.id.game_p_name)
    private Spinner gameOne_class;


    @ViewInject(R.id.game_name)
    private Spinner gameTwo_class;




    // 一级分类数组
    String gameOne_class_arr[] = { "请选择则名称" };
    String gameOne_class_arr_id[] = { "0" };
    // 二级分类数组
    String gameTwo_class_arr[] = { "请选择类型" };
    String gameTwo_class_arr_id[] = { "0" };

    private String unum="";

    String cardTwoId = "0";

    String gameName = "";

    String count_money = "";

    String m = "";

    String s = "0";

    Spinner number;

public String cid;


    public static QQGameRechargeFragment newInstance(String cid1) {
        QQGameRechargeFragment newFragment = new QQGameRechargeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("cid", cid1);
        newFragment.setArguments(bundle);

        return newFragment;

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        if (null == UserData.getUser()) {
            Util.show("对不起，您还没有登录。", context);
        }

        Bundle bundle = getArguments();

        String unum1 = bundle.getString("unum");
        cid=bundle.getString("cid");
        Log.e("cid",cid+"LLL");
        if (!Util.isNull(unum1)){
            unum=unum1;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_gamerecharge, container, false);
        ViewUtils.inject(this, view);

        return view;
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        getDate();
    }

    @OnClick({R.id.gameAdvert,R.id.game_commitBtn})
    public void click(View view){
        switch (view.getId()){
            case R.id.gameAdvert:
                Util.showIntent(context, ShopOfficeList.class);
                break;
            case R.id.game_commitBtn:
                if (null == UserData.getUser()) {
                    Util.showIntent("对不起，您还没有登录。", context,
                            LoginFrame.class);
                    return;
                }
                if (s.equals("0") || s.equals("") || s == null) {
                    Util.show("请选择名称!", context);
                    return;
                }
                if (cardTwoId.equals("0") || cardTwoId.equals("")
                        || cardTwoId == null) {
                    Util.show("请选择类别!", context);
                    return;
                }
                TextView mz = (TextView) ((Activity)context).findViewById(R.id.game_mz);
                Intent intent = new Intent();
                intent.setClass(context, GameCommitFream.class);
                intent.putExtra("name", gameName);
                intent.putExtra("mz", mz.getText());
                intent.putExtra("unum", unum);
                intent.putExtra("num", number.getSelectedItem() + "");
                intent.putExtra("price", count_money);
                intent.putExtra("cardId", cardTwoId);
                intent.putExtra("success", (cid == null || cid.equals("")
                        || cid.equals("null") ? "yes" : "no"));
                context.startActivity(intent);
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //相当于Fragment的onPause
            Log.e("Fragment","不可见");
        } else {
            //相当于Fragment的onResume
            Log.e("Fragment","可见");
        }
    }

    public void getDate() {
        try {
            Util.asynTask(context, "正在获取分类...", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {

                    Log.e("正在获取分类 runData","runData"+runData);

                    HashMap<String,List<Game>> map2 = (HashMap<String, List<Game>>)runData;
                    List<Game> list = map2.get("list");
                    Log.e("list分类","list"+(list==null)+"listsize"+list.size());
                    if (list==null||list.size()==0){
                        Util.MyToast(context, "网络异常,请检查网络！", 1).show();
                    return ;
                    }
                    String s1 = "请选择名称";
                    String s1_id = "0";
                    if (list != null && list.size() != 0) {
                        for (Game g : list) {
                            s1 += ("," + g.getCardName());
                            s1_id += ("," + g.getCardid());
                        }
                    }

                    gameOne_class_arr = s1.split(",");
                    gameOne_class_arr_id = s1_id.split(",");

                    ArrayAdapter<String> gameOneClassArray = new ArrayAdapter<String>(
                            context, android.R.layout.simple_spinner_item,
                            gameOne_class_arr);

                    gameOneClassArray
                            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    gameOne_class.setAdapter(gameOneClassArray);

                    gameOne_class
                            .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                public void onItemSelected(AdapterView<?> arg0,
                                                           View arg1, int arg2, long arg3) {
                                    s = gameOne_class_arr_id[arg2];
                                    bindGameTwoClass(gameOne_class_arr_id[arg2]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {

                                }
                            });

                    gameOne_class.setSelection(1, true);
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.getGameClass, "categoryId=" + cid);  //qq -1 网游 -2
                    List<Game> list = web.getList(Game.class);
                    HashMap<String,List<Game>> map = new HashMap<String, List<Game>>();
                    map.put("list", list);
                    return map;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            try {
                Util.showIntent("获取列表错误！", context, Lin_MainFrame.class);
            }catch (Exception ee){
                Util.MyToast(App.getContext(), "网络异常,请检查网络！", 1).show();
            }

        }
    }


    public void bindGameTwoClass(final String id) {



        try {
            Util.asynTask(context, "正在获取类型...", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {

                    HashMap<String,List<Game>> map2 = (HashMap<String, List<Game>>)runData;
                    List<Game> list = map2.get("list");
                    String str = "请选择类型";
                    String str_id = "0";

                    final List<HashMap<String, String>> lm = new ArrayList<HashMap<String, String>>();
                    lm.add(new HashMap<String, String>());
                    if (list.size() != 0 || list != null) {
                        for (int i = 1; i < list.size(); i++) {
                            Game g = list.get(i);
                            HashMap<String, String> map = new HashMap<String, String>();
                            // 保存数据
                            map.put("price", g.getPrice());
                            map.put("money", g.getMoney());
                            map.put("number", g.getAmounts());
                            lm.add(map);

                            str += ("," + g.getCardName());
                            str_id += ("," + g.getCardid());
                        }
                        Game g = list.get(0);
                        HashMap<String, String> map = new HashMap<String, String>();
                        // 保存数据
                        map.put("price", g.getPrice());
                        map.put("money", g.getMoney());
                        map.put("number", g.getAmounts());
                        lm.add(map);

                        str += ("," + g.getCardName());
                        str_id += ("," + g.getCardid());
                    }

                    gameTwo_class_arr = str.split(",");
                    gameTwo_class_arr_id = str_id.split(",");

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_item, gameTwo_class_arr);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    gameTwo_class.setAdapter(adapter);

                    gameTwo_class
                            .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                public void onItemSelected(AdapterView<?> arg0,
                                                           View arg1, int arg2, long arg3) {

                                    LinearLayout g3 = (LinearLayout) ((Activity)context).findViewById(R.id.g3);
                                    LinearLayout g4 = (LinearLayout) ((Activity)context).findViewById(R.id.g4);
                                    LinearLayout g5 = (LinearLayout) ((Activity)context).findViewById(R.id.g5);
                                    final LinearLayout g6 = (LinearLayout) ((Activity)context).findViewById(R.id.g6);

                                    if (gameTwo_class_arr_id.length <= 1) {
                                        Util.show("维护中....", context);
                                        return;
                                    }
                                    // 设置ID
                                    cardTwoId = gameTwo_class_arr_id[arg2];
                                    gameName = gameTwo_class_arr[arg2];
                                    g3.setVisibility(View.VISIBLE);
                                    g4.setVisibility(View.VISIBLE);
                                    g5.setVisibility(View.VISIBLE);
                                    // 得到对应的控件并显示对应的数据
                                    final TextView money = (TextView)((Activity)context). findViewById(R.id.game_mz);// 面值
                                    final TextView price = (TextView) ((Activity)context).findViewById(R.id.game_price);// 价格
                                    number = (Spinner) ((Activity)context).findViewById(R.id.game_num); // 数量

                                    if (arg2 != 0) {
                                        String temp = lm.get(arg2).get("money");

                                        money.setText(Util.getDouble(
                                                Double.parseDouble(temp), 2)
                                                + "");
                                        temp = lm.get(arg2).get("price");

                                        price.setText(Util.getDouble(
                                                Double.parseDouble(temp), 2)
                                                + " 元");
                                        String s = lm.get(arg2).get("number");
                                        m = lm.get(arg2).get("price");
                                        List<String> numArr = new ArrayList<String>();
                                        String[] tempStr = s.split(",");
                                        // 如果没有用,分割
                                        if (null == tempStr || 0 == tempStr.length) {
                                            // 如果也没有用-分割，那么就只有一个数量
                                            if (s.split("-").length == 0) {
                                                numArr.add(s);
                                            } else {
                                                // 说明是用-分割
                                                String[] ss = s.split("-");
                                                int start = Integer.parseInt(ss[0]);
                                                int end = Integer.parseInt(ss[1]);
                                                for (; start <= end; start++) {
                                                    numArr.add(start + "");
                                                }
                                            }
                                        }
                                        if (null != tempStr) {
                                            for (int i = 0; i < tempStr.length; i++) {
                                                if (tempStr[i].matches("\\d+")) {
                                                    numArr.add(tempStr[i]);
                                                } else {
                                                    if (tempStr[i].contains("-")) {
                                                        String[] ss = tempStr[i]
                                                                .split("-");
                                                        int start = Integer
                                                                .parseInt(ss[0]);
                                                        int end = Integer
                                                                .parseInt(ss[1]);
                                                        for (; start <= end; start++) {
                                                            numArr.add(start + "");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (numArr.size() == 0) {
                                            numArr.add(s);
                                        }
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                context,
                                                android.R.layout.simple_spinner_item,
                                                numArr);
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        number.setAdapter(adapter);

                                        // 显示总价格
                                        final TextView countMoney = (TextView) ((Activity)context).findViewById(R.id.game_count_price);
                                        countMoney.setTextColor(Color.RED);

                                        number.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                            public void onItemSelected(
                                                    AdapterView<?> arg0, View arg1,
                                                    int arg2, long arg3) {
                                                g6.setVisibility(View.VISIBLE);
                                                // System.out.println("++++++++++++++++++++++++++++"
                                                // +
                                                // price.getText().toString().replaceAll("^[\\u4e00-\\u9fa5|\\s]+$",
                                                // ""));
                                                // 设置总价
                                                float count = Float.parseFloat(m)
                                                        * Integer.parseInt(number
                                                        .getSelectedItem()
                                                        .toString());
                                                count_money = count + "";
                                                countMoney.setText(count + " 元");
                                            }

                                            @Override
                                            public void onNothingSelected(
                                                    AdapterView<?> arg0) {

                                            }
                                        });
                                    } else {
                                        g3.setVisibility(View.GONE);
                                        g4.setVisibility(View.GONE);
                                        g5.setVisibility(View.GONE);
                                        g6.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> arg0) {
                                    // TODO Auto-generated method stub

                                }
                            });
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.getGameTwoClass, "categoryId=" + id);
                    List<Game> list = web.getList(Game.class);
                    HashMap<String,List<Game>> map = new HashMap<String, List<Game>>();
                    map.put("list", list);
                    return map;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Util.show("获取二级列表错误！", context);
        }
    }
}
