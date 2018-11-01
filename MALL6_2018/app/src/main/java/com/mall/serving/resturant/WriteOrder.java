package com.mall.serving.resturant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.User;
import com.mall.model.UserInfo;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

public class WriteOrder extends Activity {
    private String checkintime, checkouttime, hotelname, roomprice, roomname,
            roombreakfast, planId, iscard, starttime, endtime, hid,
            rid, tm1 = "", tm2 = "", latetime, latetimeandcard = "0--6:00", iscardStatic, date;
    private TextView hotel_name, check_in_time, check_out_time, room_price,
            room_type, total_price, room_keep_time, danbao;
    private TextView room_number;
    private EditText yxq;
    private ImageView sfz, passport, qt;
    private Button submit, count_submit, keep_submit;
    private String room_count = "1间";
    private List<ImageView> counts = new ArrayList<ImageView>();
    private List<ImageView> times = new ArrayList<ImageView>();
    private List<ImageView> cardTypes = new ArrayList<ImageView>();
    private double totalPrice = 0.00;

    private EditText customer_phone, customer_email, customer_icardnumber, cvv2, name_of_card, zjhm;
    private List<EditText> edts = new ArrayList<EditText>();
    private String customername, customerphone, customeremail, info = "14:00-18:00 (无需担保，建议选择此项)";
    private LinearLayout keep, l5;
    private String[] timeanddanbao = new String[3];
    private String[] latetimeandiscard = new String[3];
    private UserInfo userInfo;
    private User user;
    private String md5Pwd = "";
    private String userId;
    private boolean isCreditCardRequire = false;
    private TextView price_detail;
    private LinearLayout roomcontainer;
    private LayoutInflater inf;
    private String creditcard = "", yxYear = "", yxMonth = "", cvv2Number = "", cardName = "", cardType = "身份证", cardTypeNumber = "";
    @ViewInject(R.id.time1)
    private TextView time_1;
    @ViewInject(R.id.time2)
    private TextView time_2;
    @ViewInject(R.id.time3)
    private TextView time_3;
    @ViewInject(R.id.time_img_1)
    private ImageView time_img_1;
    @ViewInject(R.id.time_img_2)
    private ImageView time_img_2;
    @ViewInject(R.id.time_img_3)
    private ImageView time_img_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writeorder);
        ViewUtils.inject(this);
        inf = LayoutInflater.from(this);

    }

    @Override
    protected void onStart() {
        init();
        super.onStart();
    }

    private void init() {
        Util.initTitle(WriteOrder.this, "填写订单", new OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteOrder.this.finish();

            }
        });
        user = UserData.getUser();
        if (user == null) {
            Toast.makeText(WriteOrder.this, "您还没有登陆，请先登录", 0).show();
            Util.showIntent(WriteOrder.this, LoginFrame.class);
            return;
        } else {
            userId = user.getUserId();
            md5Pwd = user.getMd5Pwd();
        }
        getIntentData();
        price_detail = (TextView) this.findViewById(R.id.price_detail);
        price_detail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WriteOrder.this);
                LayoutInflater inflater = LayoutInflater.from(WriteOrder.this);
                View v = inflater.inflate(R.layout.resturant_price_detail, null);
                LinearLayout container = (LinearLayout) v.findViewById(R.id.price_detail_container);
                String[] dates = date.split("，");
                for (int i = 0; i < dates.length; i++) {
                    LinearLayout l = (LinearLayout) inflater.inflate(R.layout.resturant_price_detail_item, null);
                    TextView t1 = (TextView) l.getChildAt(0);
                    TextView t2 = (TextView) l.getChildAt(1);
                    String[] dateandmoney = dates[i].split("\\|,\\|");
                    t1.setText("日期：" + dateandmoney[0]);
                    t2.setText("房费：￥" + dateandmoney[1] + "元");
                    container.addView(l);
                }
                builder.setCancelable(true);
                final Dialog dialog;
                dialog = builder.show();
                Window window = dialog.getWindow();
                WindowManager.LayoutParams pa = window.getAttributes();
                pa.width = Util.dpToPx(WriteOrder.this, 300);
                pa.height = Util.dpToPx(WriteOrder.this, 280);
                dialog.setContentView(v, pa);
            }
        });
        hotel_name = (TextView) this.findViewById(R.id.hotel_name);
        check_in_time = (TextView) this.findViewById(R.id.check_in_time);
        check_out_time = (TextView) this.findViewById(R.id.check_out_time);
        room_price = (TextView) this.findViewById(R.id.room_price);
        room_type = (TextView) this.findViewById(R.id.room_type);
        roomcontainer = (LinearLayout) this.findViewById(R.id.roomcontainer);
        for (int i = 0; i < Integer.parseInt(room_count.replace("间", "")); i++) {
            LinearLayout layout = (LinearLayout) inf.inflate(R.layout.resturant_room_count, null);
            roomcontainer.addView(layout);
        }
        hotel_name.setText(hotelname);
        check_in_time.setText("入住:" + checkintime);
        check_out_time.setText("退房:" + checkouttime);
        room_price.setText("￥" + roomprice + "元");
        totalPrice = Double.parseDouble(roomprice);
        room_type.setText(roomname + "" + roombreakfast + "" + roomprice);

        submit = (Button) this.findViewById(R.id.submit);
        total_price = (TextView) this.findViewById(R.id.total_price);
        total_price.setText("￥" + roomprice + "元");
//		customer_name = (EditText) this.findViewById(R.id.customer_name);
        customer_email = (EditText) this.findViewById(R.id.customer_email);
        customer_phone = (EditText) this.findViewById(R.id.customer_phone);
        l5 = (LinearLayout) this.findViewById(R.id.l5);
        sfz = (ImageView) this.findViewById(R.id.sfz);
        passport = (ImageView) this.findViewById(R.id.passport);
        qt = (ImageView) this.findViewById(R.id.qt);
        cardTypes.add(sfz);
        cardTypes.add(passport);
        cardTypes.add(qt);
        sfz.setOnClickListener(new CardTypeRadioButton(0));
        passport.setOnClickListener(new CardTypeRadioButton(1));
        qt.setOnClickListener(new CardTypeRadioButton(2));

        customer_icardnumber = (EditText) this.findViewById(R.id.customer_icardnumber);
        cvv2 = (EditText) this.findViewById(R.id.cvv2);
        name_of_card = (EditText) this.findViewById(R.id.name_of_card);
        zjhm = (EditText) this.findViewById(R.id.zjhm);
//		edts.add(customer_name);
        if (!Util.isNull(user.getMobilePhone())) {
            customer_phone.setText(user.getMobilePhone());
        }
        edts.add(customer_phone);
        edts.add(customer_email);
        edts.add(customer_icardnumber);
        edts.add(cvv2);
        edts.add(name_of_card);
        edts.add(zjhm);
//		customer_name.setOnFocusChangeListener(new EditTextFocusListener(0));
        customer_phone.setOnFocusChangeListener(new EditTextFocusListener(1));
        customer_email.setOnFocusChangeListener(new EditTextFocusListener(2));
        customer_icardnumber.setOnFocusChangeListener(new EditTextFocusListener(3));
        cvv2.setOnFocusChangeListener(new EditTextFocusListener(4));
        name_of_card.setOnFocusChangeListener(new EditTextFocusListener(5));
        zjhm.setOnFocusChangeListener(new EditTextFocusListener(6));

//		TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//		customer_phone.setText(tm.getLine1Number());
        room_keep_time = (TextView) this.findViewById(R.id.room_keep_time);
        danbao = (TextView) this.findViewById(R.id.danbao);
        room_number = (TextView) this.findViewById(R.id.room_number);
        room_number.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WriteOrder.this);
                LayoutInflater inflater = LayoutInflater.from(WriteOrder.this);
                View v = inflater.inflate(R.layout.choose_room_count, null);
                ImageView one, two, three, four, five, six;
                one = (ImageView) v.findViewById(R.id.one);
                two = (ImageView) v.findViewById(R.id.two);
                three = (ImageView) v.findViewById(R.id.three);
                four = (ImageView) v.findViewById(R.id.four);
                five = (ImageView) v.findViewById(R.id.five);
                six = (ImageView) v.findViewById(R.id.six);
                int nowcount = Integer.parseInt(room_count.replace("间", ""));
                counts.add(one);
                counts.add(two);
                counts.add(three);
                counts.add(four);
                counts.add(five);
                counts.add(six);
                ImageView im = counts.get(nowcount - 1);
                im.setImageResource(R.drawable.radiobutton_down);
                im.setTag("selected");
                one.setOnClickListener(new RadioButtonOnClick(0));
                two.setOnClickListener(new RadioButtonOnClick(1));
                three.setOnClickListener(new RadioButtonOnClick(2));
                four.setOnClickListener(new RadioButtonOnClick(3));
                five.setOnClickListener(new RadioButtonOnClick(4));
                six.setOnClickListener(new RadioButtonOnClick(5));
                count_submit = (Button) v.findViewById(R.id.count_submit);
                builder.setCancelable(true);
                final Dialog dialog;
                dialog = builder.show();
                Window window = dialog.getWindow();
                WindowManager.LayoutParams pa = window.getAttributes();
                pa.width = Util.dpToPx(WriteOrder.this, 300);
                dialog.setContentView(v, pa);
                count_submit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        room_number.setText(room_count);
                        totalPrice = Integer.parseInt(room_count.replace("间", "")) * Double.parseDouble(roomprice);
                        total_price.setText("￥" + Util.getDouble(totalPrice, 2) + "元");
                        counts.clear();
                        dialog.dismiss();
                    }
                });
            }
        });
        try {
            timeCheck();
        } catch (Exception e) {
            e.printStackTrace();
        }
        yxq = (EditText) this.findViewById(R.id.yxq);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                customername = "";
                for (int i = 0; i < roomcontainer.getChildCount(); i++) {
                    LinearLayout l = (LinearLayout) roomcontainer.getChildAt(i);
                    EditText et = (EditText) l.getChildAt(0);
                    customername += et.getText().toString() + ",";
                }
                if (room_keep_time.getText().toString().equals("选择保留时间")) {
                    Toast.makeText(WriteOrder.this, "请选择房间保留时间", Toast.LENGTH_LONG).show();
                    return;
                }
                customername = customername.substring(0, customername.length() - 1);
                customerphone = customer_phone.getText().toString();
                customeremail = customer_email.getText().toString();
                creditcard = customer_icardnumber.getText().toString();
                cvv2Number = cvv2.getText().toString();
                cardName = name_of_card.getText().toString();
                cardTypeNumber = zjhm.getText().toString();
                if (!Util.isNull(customername) || !Util.isNull(customerphone)) {
                    if (Util.checkPhoneNumber(customerphone)) {
                        if (isCreditCardRequire) {
                            if (Util.isNull(creditcard) || Util.isNull(cvv2Number) || Util.isNull(cardName) || Util.isNull(cardTypeNumber)) {
                                Toast.makeText(WriteOrder.this, "您填写的信用卡信息不完整。", Toast.LENGTH_LONG).show();
                            } else {
                                if (cvv2.length() == 3) {
                                    if (cardType.equals("身份证") && cardTypeNumber.length() == 18) {
                                        if (Util.matchEmail(customeremail)) {
                                            String yxqs = yxq.getText().toString();
                                            String year = "", month = "";
                                            if (Util.isNull(yxqs)) {
                                                Util.show("有效期不能为空", WriteOrder.this);
                                                return;
                                            } else {
                                                if (yxqs.contains("-") && yxqs.split("-").length == 2) {
                                                    year = yxqs.split("-")[0];
                                                    month = yxqs.split("-")[1];
                                                } else {
                                                    Util.show("有效期格式为yyyy-mm", WriteOrder.this);
                                                    return;
                                                }
                                            }
                                            String paramString = "userid="
                                                    + userId + "&md5pwd=" + md5Pwd + "&sex=" + Util.get("男")
                                                    + "&allMoney=" + totalPrice + "&email=" + customeremail.trim()
                                                    + "&hid=" + hid + "&rid=" + rid + "&pid=" + planId
                                                    + "&hotelName=" + Util.get(hotelname) + "&iscard=" + iscard
                                                    + "&tm1=" + tm1 + "&tm2=" + tm2 + "&latetime="
                                                    + latetime + "&rm=" + room_count.replace("间", "") + "&guest=" + Util.get(customername)
                                                    + "&mobile=" + customerphone + "&message=nomessage"
                                                    + "&year=" + year + "&month=" + month
                                                    + "&carNum=" + creditcard + "&cvv2=" + cvv2Number + "&carName=" + Util.get(cardName) + "&idCardType=" + Util.get(cardType) + "&idCardNumber=" + cardTypeNumber;
                                            getWriteOrderNumber(paramString);
                                        } else {
                                            Toast.makeText(WriteOrder.this, "邮箱格式不正确", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(WriteOrder.this, "身份证号码位数不正确", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(WriteOrder.this, "CVV2码错误", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            String paramString = "userid="
                                    + userId + "&md5pwd=" + md5Pwd + "&sex=" + Util.get("男")
                                    + "&allMoney=" + totalPrice + "&email=" + customeremail.trim()
                                    + "&hid=" + hid + "&rid=" + rid + "&pid=" + planId
                                    + "&hotelName=" + Util.get(hotelname) + "&iscard=" + iscard
                                    + "&tm1=" + tm1 + "&tm2=" + tm2 + "&latetime="
                                    + latetime + "&rm=" + room_count.replace("间", "") + "&guest=" + Util.get(customername)
                                    + "&mobile=" + customerphone + "&message=nomessage"
                                    + "&year=2014" + "&month=06"
                                    + "&carNum=" + creditcard + "&cvv2=" + cvv2Number + "&carName=" + Util.get(cardName) + "&idCardType=" + Util.get(cardType) + "&idCardNumber=" + cardTypeNumber;
                            getWriteOrderNumber(paramString);
                        }
                    } else {
                        Toast.makeText(WriteOrder.this, "请确认手机号码格式是否正确....", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(WriteOrder.this, "客户姓名和客户电话不能为空....", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getWriteOrderNumber(final String paramString) {
        Util.asynTask(WriteOrder.this, "正在生成订单", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                if (!Util.isNull(runData)) {
                    if (result.contains("success")) {
                        Toast.makeText(WriteOrder.this, "生成订单成功,", Toast.LENGTH_LONG).show();
                        WriteOrder.this.finish();
                    } else {
                        Toast.makeText(WriteOrder.this, "生成订单失败，请确认您的信息是否填写正确", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(WriteOrder.this, "生成订单失败，请稍候再试", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service, Web.writerHotelOrder, paramString);
                String result = web.getPlan();
                return result;
            }
        });
    }

    /*
     * 根据网站规则判断保留时间类型
     */
    private void timeCheck() {
        if (iscard.equals("3")) {
            if (starttime.equals("14:00")) {
            } else if (starttime.equals("18:00")) {
                if (endtime.equals("06:00")) {
                    timeanddanbao[0] = "14:00-18:00 (无需担保，建议选择此项)";
                    timeanddanbao[1] = "18:00-23:59 (需要信用卡担保首晚房费)";
                    timeanddanbao[2] = "23:59-6:00 (需要信用卡担保首晚房费)";

                    latetimeandiscard[0] = "0--18:00";
                    latetimeandiscard[1] = "3--23:59";
                    latetimeandiscard[2] = "3--6:00";
                }
            } else if (starttime.equals("20:00")) {
                if (endtime.equals("23:59")) {
                    timeanddanbao[0] = "14:00-20:00 (无需担保，建议选择此项)";
                    timeanddanbao[1] = "20:00-23:59 (需要信用卡担保首晚房费)";
                    timeanddanbao[2] = "23:59-06:00 (无需担保，建议选择此项)";

                    latetimeandiscard[0] = "0--20:00";
                    latetimeandiscard[1] = "3--23:59";
                    latetimeandiscard[2] = "3--6:00";
                } else if (endtime.equals("06:00")) {
                    timeanddanbao[0] = "14:00-20:00 (无需担保，建议选择此项)";
                    timeanddanbao[1] = "20:00-23:59 (需要信用卡担保首晚房费)";
                    timeanddanbao[2] = "23:59-06:00 (需要信用卡担保首晚房费)";

                    latetimeandiscard[0] = "0--20:00";
                    latetimeandiscard[1] = "3--23:59";
                    latetimeandiscard[2] = "3--6:00";

                }
            } else if (starttime.equals("21:00")) {
                timeanddanbao[0] = "14:00-20:00 (无需担保，建议选择此项)";
                timeanddanbao[1] = "18:00-21:00 (无需担保，建议选择此项)";
                timeanddanbao[2] = "21:00-06:00 (需要信用卡担保首晚房费)";

                latetimeandiscard[0] = "0--18:00";
                latetimeandiscard[1] = "0--21:00";
                latetimeandiscard[2] = "3--6:00";

            } else if (starttime.equals("22:00")) {
                timeanddanbao[0] = "14:00-20:00 (无需担保，建议选择此项)";
                timeanddanbao[1] = "18:00-22:00 (无需担保，建议选择此项)";
                timeanddanbao[2] = "22:00-06:00 (需要信用卡担保首晚房费)";

                latetimeandiscard[0] = "0--18:00";
                latetimeandiscard[1] = "3--22:00";
                latetimeandiscard[2] = "3--6:00";

            } else if (starttime.equals("23:59")) {
                if (endtime.equals("06:00")) {
                    timeanddanbao[0] = "14:00-20:00 (无需担保，建议选择此项)";
                    timeanddanbao[1] = "20:00-23:59 (需要信用卡担保首晚房费)";
                    timeanddanbao[2] = "23:59-6:00 (需要信用卡担保首晚房费)";

                    latetimeandiscard[0] = "0--20:00";
                    latetimeandiscard[1] = "3--23:59";
                    latetimeandiscard[2] = "3--6:00";
                }
            }
        } else if (iscard.equals("1")) {
            timeanddanbao[0] = "20:00 (需要信用卡担保首晚房费)";
            timeanddanbao[1] = "24:00 (需要信用卡担保首晚房费)";
            timeanddanbao[2] = "6:00 (需要信用卡担保首晚房费)";

            latetimeandiscard[0] = "1--20:00";
            latetimeandiscard[1] = "1--234:00";
            latetimeandiscard[2] = "1--6:00";
        } else if (iscard.equals("0")) {
            timeanddanbao[0] = "20:00 (无需担保，建议选择此项)";
            timeanddanbao[1] = "24:00 (无需担保，建议选择此项)";
            timeanddanbao[2] = "6:00 (无需担保，建议选择此项)";
            latetimeandiscard[0] = "0--20:00";
            latetimeandiscard[1] = "0--24:00";
            latetimeandiscard[2] = "0--6:00";
        }
        if (!Util.isNull(latetimeandiscard[0])) {
            latetimeandcard = latetimeandiscard[0];
            time_1.setTag(latetimeandiscard[0]);
            latetime = latetimeandcard.split("--")[1];
            iscard = latetimeandcard.split("--")[0];
        } else {
            time_1.setTag(latetimeandcard);
        }
        if (!Util.isNull(latetimeandiscard[1])) {
            time_2.setTag(latetimeandiscard[1]);
        } else {
            time_2.setTag(latetimeandcard);
        }
        if (!Util.isNull(latetimeandiscard[2])) {
            time_3.setTag(latetimeandiscard[2]);
        } else {
            time_3.setTag(latetimeandcard);
        }
        if (!Util.isNull(timeanddanbao[0])) {
            time_1.setText(timeanddanbao[0]);
            room_keep_time.setText(timeanddanbao[0]);
        } else {
            LinearLayout lt1 = (LinearLayout) time_1.getParent();
            lt1.setVisibility(View.GONE);
        }
        if (!Util.isNull(timeanddanbao[1])) {
            time_2.setText(timeanddanbao[1]);
        } else {
            LinearLayout lt2 = (LinearLayout) time_2.getParent();
            lt2.setVisibility(View.GONE);
        }
        if (!Util.isNull(timeanddanbao[2])) {
            time_3.setText(timeanddanbao[2]);
        } else {
            LinearLayout lt3 = (LinearLayout) time_3.getParent();
            lt3.setVisibility(View.GONE);
        }
        times.add(time_img_1);
        times.add(time_img_2);
        times.add(time_img_3);
        time_img_1.setOnClickListener(new TimeRadioButton(0));
        time_img_2.setOnClickListener(new TimeRadioButton(1));
        time_img_3.setOnClickListener(new TimeRadioButton(2));
    }

    /*
     * 时间选择按钮点击
     */
    private class TimeRadioButton implements OnClickListener {
        private int index = 0;

        public TimeRadioButton(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            ImageView view = (ImageView) v;
            System.out.println(view.getTag());
            if (view.getTag().equals("noselected")) {
                view.setImageResource(R.drawable.radiobutton_down);
                view.setTag("selected");
            }
            for (int i = 0; i < times.size(); i++) {
                if (i != index) {
                    ImageView imageview = times.get(i);
                    imageview.setImageResource(R.drawable.radiobutton_up);
                    imageview.setTag("noselected");
                }
            }
            LinearLayout layout = (LinearLayout) v.getParent();
            TextView t = (TextView) layout.getChildAt(0);
            iscard = iscardStatic;
            info = t.getText().toString();
            latetimeandcard = t.getTag().toString();
            if (!Util.isNull(info) && info.contains(" ")) {
                String[] infos = info.split(" ");
                room_keep_time.setText(info);
                if (infos[0].contains("-")) {
                    //tm1 = infos[0].split("-")[0];
                    //tm2 = infos[0].split("-")[1];
                }
                danbao.setText(infos[1]);
                latetime = latetimeandcard.split("--")[1];
                iscard = latetimeandcard.split("--")[0];
                if (info.contains("信用卡担保")) {
                    l5.setVisibility(View.VISIBLE);
                    isCreditCardRequire = true;
                } else {
                    l5.setVisibility(View.GONE);
                    isCreditCardRequire = false;
                }
            }
        }
    }

    private class CardTypeRadioButton implements OnClickListener {
        private int index = 0;

        public CardTypeRadioButton(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            ImageView view = (ImageView) v;
            if (view.getTag().equals("noselected")) {
                view.setImageResource(R.drawable.radiobutton_down);
                view.setTag("selected");
            }
            for (int i = 0; i < cardTypes.size(); i++) {
                // i=0,1,2,3,4
                if (i != index) {
                    ImageView imageview = cardTypes.get(i);
                    imageview.setImageResource(R.drawable.radiobutton_up);
                    imageview.setTag("noselected");
                }
            }
            LinearLayout layout = (LinearLayout) v.getParent();
            TextView t = (TextView) layout.getChildAt(0);
            cardType = t.getText().toString();
        }
    }

    private class EditTextFocusListener implements OnFocusChangeListener {

        public EditTextFocusListener(int index) {
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText e = (EditText) v;
            if (hasFocus) {
                e.setBackgroundResource(R.drawable.editextborder_focus);
            } else {
                e.setBackgroundResource(R.drawable.editextborder);
            }
        }
    }

    private class RadioButtonOnClick implements OnClickListener {
        private int index = 0;

        public RadioButtonOnClick(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            // index=0
            ImageView view = (ImageView) v;
            if (view.getTag().equals("noselected")) {
                view.setImageResource(R.drawable.radiobutton_down);
                view.setTag("selected");
            }
            for (int i = 0; i < counts.size(); i++) {
                // i=0,1,2,3,4
                if (i != index) {
                    ImageView imageview = counts.get(i);
                    imageview.setImageResource(R.drawable.radiobutton_up);
                    imageview.setTag("noselected");
                }
            }
            if (index == 0) {
                room_count = "1间";
            } else if (index == 1) {
                room_count = "2间";
            } else if (index == 2) {
                room_count = "3间";
            } else if (index == 3) {
                room_count = "4间";
            } else if (index == 4) {
                room_count = "5间";
            } else if (index == 5) {
                room_count = "6间";
            }
            roomcontainer.removeAllViews();
            for (int i = 0; i < Integer.parseInt(room_count.replace("间", "")); i++) {
                LinearLayout layout = (LinearLayout) inf.inflate(R.layout.resturant_room_count, null);
                roomcontainer.addView(layout);
            }
        }
    }

    private void getIntentData() {
        if (this.getIntent() != null) {
            checkintime = this.getIntent().getStringExtra("checkintime");
            checkouttime = this.getIntent().getStringExtra("checkouttime");
            tm1 = checkintime;
            tm2 = checkouttime;
            hotelname = this.getIntent().getStringExtra("hotelname");
            roomprice = this.getIntent().getStringExtra("roomprice");
            roombreakfast = this.getIntent().getStringExtra("roombreakfast");
            roomname = this.getIntent().getStringExtra("roomname");
            planId = this.getIntent().getStringExtra("planId");
            iscard = this.getIntent().getStringExtra("iscard");
            iscardStatic = iscard;
            starttime = this.getIntent().getStringExtra("starttime");
            endtime = this.getIntent().getStringExtra("endtime");
            hid = this.getIntent().getStringExtra("hid");
            rid = this.getIntent().getStringExtra("rid");
            date = this.getIntent().getStringExtra("date");
        }
    }
}
