package com.mall.serving.orderplane;

import java.util.ArrayList;
import java.util.List;

import com.mall.util.CheckPAndI;
import com.mall.util.Util;
import com.mall.view.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddPeople extends Activity {
    private EditText name, zjhm, phone, birthdayEditxt;
    private TextView plane_people_age, zhengjian, baoxian, changyong;
    private Button submit;
    private List<ImageView> ages = new ArrayList<ImageView>();
    private List<ImageView> cards = new ArrayList<ImageView>();
    private String agetype = "", cardtype = "身份证";
    private SharedPreferences sp;
    private String originalString = "";
    private LinearLayout birthdaylayout;
    private String birthday = "";
    private String phonenumber = "";
    private String note, takeoffairport, landingairport, takeofftime,
            landingtime, startdate, enddate, tickettype, totalprice,
            flightno = "", planemode = "", AirConFee = "", city_from, city_to;
    // 订单需要的参数
    private String FuelTax = "";
    private String param = "";

    private boolean avalibility = false;
    private String names = "", cardtypes = "", cardnums = "";
    private int screentWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plane_people);
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.8);   //高度设置为屏幕的1.0
        p.width = (int) (d.getWidth() * 0.8);    //宽度设置为屏幕的0.8
        //	p.alpha = 1.0f;      //设置本身透明度
        //	p.dimAmount = 0.0f;
        p.gravity = Gravity.CENTER;//设置黑暗度
        getWindow().setAttributes(p);
        sp = this.getSharedPreferences("planepeolelist", 0);
        screentWidth = getWindowManager().getDefaultDisplay().getWidth();
        init();
        getIntentData();
    }

    private void getIntentData() {
        takeoffairport = this.getIntent().getStringExtra("takeoffairport");
        landingairport = this.getIntent().getStringExtra("landingairport");
        takeofftime = this.getIntent().getStringExtra("takeofftime");
        landingtime = this.getIntent().getStringExtra("landingtime");
        startdate = this.getIntent().getStringExtra("startdate");
        enddate = this.getIntent().getStringExtra("enddate");
        tickettype = this.getIntent().getStringExtra("tickettype");
        totalprice = this.getIntent().getStringExtra("totalprice");
        note = this.getIntent().getStringExtra("note");
        flightno = this.getIntent().getStringExtra("flightno");
        planemode = this.getIntent().getStringExtra("planemode");
        city_from = this.getIntent().getStringExtra("city_from");
        city_to = this.getIntent().getStringExtra("city_to");
        AirConFee = this.getIntent().getStringExtra("AirConFee");
        FuelTax = this.getIntent().getStringExtra("FuelTax");
        param = this.getIntent().getStringExtra("param");
        if (!Util.isNull(this.getIntent().getStringExtra("name"))) {
            names = this.getIntent().getStringExtra("name");
            name.setText(names);
        }
        if (!Util.isNull(this.getIntent().getStringExtra("agetype"))) {
            plane_people_age.setText("agetype");
        }
        if (!Util.isNull(this.getIntent().getStringExtra("cardnum"))) {
            cardnums = this.getIntent().getStringExtra("cardnum");
            zjhm.setText(cardnums);
        }
        if (!Util.isNull(this.getIntent().getStringExtra("cardtype"))) {
            cardtypes = this.getIntent().getStringExtra("cardtype");
            zhengjian.setText(cardtypes);
        }
        if (!Util.isNull(this.getIntent().getStringExtra("baoxian"))) {
            baoxian.setText(this.getIntent().getStringExtra("baoxian"));
        }
        if (!Util.isNull(this.getIntent().getStringExtra("birthday"))) {
            if (!cardtype.equals("身份证")) {
                birthdaylayout.setVisibility(View.VISIBLE);
                birthdayEditxt.setText(this.getIntent().getStringExtra("birthday"));
            }
        }
        if (!Util.isNull(this.getIntent().getStringExtra("phone"))) {
            phone.setText(this.getIntent().getStringExtra("phone"));
        }
    }

    public void init() {
        Util.initTitle(this, "添加乘机人", new OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPeople.this.finish();
            }
        });
        initView();
    }

    private void initView() {
        name = (EditText) this.findViewById(R.id.name);
        zjhm = (EditText) this.findViewById(R.id.zjhm);
        zjhm.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (cardtype.equals("身份证")) {
                    if (!hasFocus) {
                        checkShengFengzheng(zjhm.getText().toString().trim());
                    }
                }
            }
        });
        plane_people_age = (TextView) this.findViewById(R.id.plane_people_age);
        zhengjian = (TextView) this.findViewById(R.id.zhengjian);
        baoxian = (TextView) this.findViewById(R.id.baoxian);
        changyong = (TextView) this.findViewById(R.id.changyong);
        changyong.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPeople.this, PlanePeopleList.class);
                intent.putExtra("takeoffairport", takeoffairport);
                intent.putExtra("landingairport", landingairport);
                intent.putExtra("takeofftime", takeofftime);
                intent.putExtra("landingtime", landingtime);
                intent.putExtra("startdate", startdate);
                intent.putExtra("enddate", enddate);
                intent.putExtra("tickettype", tickettype);
                intent.putExtra("totalprice", totalprice);
                intent.putExtra("itemprice", totalprice);
                intent.putExtra("note", note);
                intent.putExtra("flightno", flightno);
                intent.putExtra("planemode", planemode);


                intent.putExtra("FuelTax", FuelTax);//燃油
                intent.putExtra("AirConFee", AirConFee);//机场建设
                intent.putExtra("city_from", city_from);
                intent.putExtra("city_to", city_to);
                intent.putExtra("param", param);
                AddPeople.this.startActivity(intent);
            }
        });
        submit = (Button) this.findViewById(R.id.submit);
        //		plane_people_age.setOnClickListener(new AgeOnClickListener("乘机人年龄",
        //				"儿童票(2-12岁)", "成人票(>12岁)", "age"));
        zhengjian.setOnClickListener(new ZJOnclickListener());
        baoxian.setOnClickListener(new AgeOnClickListener("投保份数", "0  份",
                "1  份(20元一份)", "baoxian"));
        phone = (EditText) this.findViewById(R.id.phone);
        birthdaylayout = (LinearLayout) this.findViewById(R.id.birthdaylayout);
        birthdayEditxt = (EditText) this.findViewById(R.id.birthday);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isNull(name.getText().toString())) {
                    Toast.makeText(AddPeople.this, "姓名不能为空", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                if (Util.isNull(plane_people_age.getText().toString())) {
                    Toast.makeText(AddPeople.this, "乘机人年龄不能为空",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (Util.isNull(zhengjian.getText().toString())) {
                    Toast.makeText(AddPeople.this, "证件类型不能为空",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (zhengjian.getText().toString().equals("身份证")) {
                    if (!avalibility) {
                        Toast.makeText(AddPeople.this, "身份证号码格式不正确", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                if (Util.isNull(zhengjian.getText().toString())) {
                    Toast.makeText(AddPeople.this, "请选择保险数量", Toast.LENGTH_LONG).show();
                    return;
                }
                if (cardtype.equals("护照") || cardtype.equals("其他")) {
                    if (Util.isNull(birthdayEditxt.getText().toString())) {
                        Toast.makeText(AddPeople.this, "证件类型为护照或者其他，则需要填写生日!", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        birthday = birthdayEditxt.getText().toString();
                    }
                }
                if (Util.isNull(phone.getText().toString())
                        && !Util.checkPhoneNumber(phone.getText().toString())) {
                    Toast.makeText(AddPeople.this, "请准确填写您的手机号码。", Toast.LENGTH_LONG).show();
                    return;
                }
                phonenumber = phone.getText().toString();
                StringBuilder builder = new StringBuilder();
                builder.append("name=" + name.getText().toString()
                        + ",,,agetype=" + plane_people_age.getText().toString()
                        + ",,,zjhm=" + zjhm.getText().toString()
                        + ",,,baoxian=" + baoxian.getText().toString()
                        + ",,,birthday=" + birthday
                        + ",,,phone=" + phonenumber
                        + ",,,cardtype=" + cardtype
                        + ",,,,");
                originalString = sp.getString("planepeople", "");
                if (originalString.contains(name.getText().toString())) {
                    Toast.makeText(AddPeople.this, "对不起，您不能填相同名称的乘机人", Toast.LENGTH_LONG).show();
                    return;
                }
                if (originalString.contains(zjhm.getText().toString())) {
                    Toast.makeText(AddPeople.this, "对不起，您不能填相同的证件号码.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (originalString.contains(phone.getText().toString())) {
                    Toast.makeText(AddPeople.this, "对不起，您不能填相同手机号码", Toast.LENGTH_LONG).show();
                    return;
                }
                originalString += builder.toString();
                sp.edit().putString("planepeople", originalString).commit();
                Intent intent = new Intent(AddPeople.this, PlanePeopleList.class);
                intent.putExtra("takeoffairport", takeoffairport);
                intent.putExtra("landingairport", landingairport);
                intent.putExtra("takeofftime", takeofftime);
                intent.putExtra("landingtime", landingtime);
                intent.putExtra("startdate", startdate);
                intent.putExtra("enddate", enddate);
                intent.putExtra("tickettype", tickettype);
                intent.putExtra("totalprice", totalprice);
                intent.putExtra("itemprice", totalprice);
                intent.putExtra("note", note);
                intent.putExtra("flightno", flightno);
                intent.putExtra("planemode", planemode);


                intent.putExtra("FuelTax", FuelTax);//燃油
                intent.putExtra("AirConFee", AirConFee);//机场建设
                intent.putExtra("city_from", city_from);
                intent.putExtra("city_to", city_to);
                intent.putExtra("param", param);
                AddPeople.this.startActivity(intent);
            }
        });
    }

    private void checkShengFengzheng(final String cardno) {
        if (CheckPAndI.isIdentityNo(cardno.trim(), AddPeople.this)) {
            avalibility = true;
            birthday = CheckPAndI.getBirthday();
        } else {
            avalibility = false;
        }
    }

    public class ZJOnclickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    AddPeople.this);
            LayoutInflater inflater = LayoutInflater.from(AddPeople.this);
            View view = inflater.inflate(R.layout.plane_add_people_card_type,
                    null);
            cards.clear();
            ImageView sfz, hz, qt;
            sfz = (ImageView) view.findViewById(R.id.sfz);
            hz = (ImageView) view.findViewById(R.id.hz);
            qt = (ImageView) view.findViewById(R.id.qt);
            cards.add(sfz);
            cards.add(hz);
            cards.add(qt);
            sfz.setOnClickListener(new ZJRadioButtonOnClick(0));
            hz.setOnClickListener(new ZJRadioButtonOnClick(1));
            qt.setOnClickListener(new ZJRadioButtonOnClick(2));
            Button submit = (Button) view.findViewById(R.id.submit);
            builder.setCancelable(false);
            final Dialog dialog;
            dialog = builder.show();
            Window window = dialog.getWindow();
            WindowManager.LayoutParams pa = window.getAttributes();
            pa.width = screentWidth * 4 / 5;
            dialog.setContentView(view, pa);
            submit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    zhengjian.setText(cardtype);
                    dialog.dismiss();
                }
            });
        }
    }

    public class ZJRadioButtonOnClick implements OnClickListener {
        private int pos;

        public ZJRadioButtonOnClick(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            ImageView view = (ImageView) v;
            if (view.getTag().toString().equals("noselected")) {
                view.setImageResource(R.drawable.radiobutton_down);
                view.setTag("selected");
            }
            for (int i = 0; i < cards.size(); i++) {
                if (i != pos) {
                    ImageView imageview = cards.get(i);
                    imageview.setImageResource(R.drawable.radiobutton_up);
                    imageview.setTag("noselected");
                }
            }
            if (pos == 0) {
                cardtype = "身份证";
                birthdaylayout.setVisibility(View.GONE);
            } else if (pos == 1) {
                cardtype = "护照";
                birthdaylayout.setVisibility(View.VISIBLE);
            } else if (pos == 2) {
                cardtype = "其他";
                birthdaylayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public class AgeOnClickListener implements OnClickListener {
        private String titles, t1s, t2s, from;

        public AgeOnClickListener(String title, String t1, String t2,
                                  String from) {
            this.titles = title;
            this.t1s = t1;
            this.t2s = t2;
            this.from = from;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    AddPeople.this);
            LayoutInflater inflater = LayoutInflater.from(AddPeople.this);
            View view = inflater.inflate(R.layout.plane_radio_choose_layout,
                    null);
            ages.clear();
            ImageView child, chengren;
            TextView t1, t2, title;
            child = (ImageView) view.findViewById(R.id.child);
            chengren = (ImageView) view.findViewById(R.id.chengren);
            ages.add(child);
            ages.add(chengren);
            child.setOnClickListener(new AgeRadioButtonOnClick(0));
            chengren.setOnClickListener(new AgeRadioButtonOnClick(1));
            Button submit = (Button) view.findViewById(R.id.submit);
            title = (TextView) view.findViewById(R.id.title);
            t1 = (TextView) view.findViewById(R.id.t1);
            t2 = (TextView) view.findViewById(R.id.t2);
            title.setText(titles);
            t1.setText(t1s);
            t2.setText(t2s);
            builder.setCancelable(false);
            final Dialog dialog;
            dialog = builder.show();
            Window window = dialog.getWindow();
            WindowManager.LayoutParams pa = window.getAttributes();
            pa.width = Util.dpToPx(AddPeople.this, 300);
            pa.height = Util.dpToPx(AddPeople.this, 280);
            dialog.setContentView(view, pa);
            submit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (from.equals("age")) {
                        if (agetype.equals("0")) {
                            plane_people_age.setText(t1s);
                        } else if (agetype.equals("1")) {
                            plane_people_age.setText(t2s);
                        }
                    } else if (from.equals("baoxian")) {
                        if (agetype.equals("0")) {
                            baoxian.setText(t1s);
                        } else if (agetype.equals("1")) {
                            baoxian.setText(t2s);
                        }
                    }
                    dialog.dismiss();
                }
            });
        }
    }

    public class AgeRadioButtonOnClick implements OnClickListener {
        private int pos;

        public AgeRadioButtonOnClick(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            ImageView view = (ImageView) v;
            if (view.getTag().toString().equals("noselected")) {
                view.setImageResource(R.drawable.radiobutton_down);
                view.setTag("selected");
            }

            for (int i = 0; i < ages.size(); i++) {
                if (i != pos) {
                    ImageView imageview = ages.get(i);
                    imageview.setImageResource(R.drawable.radiobutton_up);
                    imageview.setTag("noselected");
                }
            }
            if (pos == 0) {
                agetype = "0";
            } else if (pos == 1) {
                agetype = "1";
            }
        }
    }
}
