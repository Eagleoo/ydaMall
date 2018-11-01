package com.mall.view.RedEnvelopesPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.RedPackageInLetBean;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.ShowPopWindow;


@ContentView(R.layout.activity_accelerator)
public class AcceleratorActivity extends AppCompatActivity {
    private Context context;

    @ViewInject(R.id.person_capb)
    private com.mall.view.ColorArcProgressBar person_capb;

    @ViewInject(R.id.team_capb)
    private com.mall.view.ColorArcProgressBar team_capb;

    @ViewInject(R.id.day_tv)
    private TextView day_tv;

    @ViewInject(R.id.teamred_tv)
            private TextView teamred_tv;

    @ViewInject(R.id.personred_tv)
            private TextView personred_tv;


    @ViewInject(R.id.person_allday)
            private TextView person_allday;  //个人累计加速天数

    @ViewInject(R.id.team_allday)
            private TextView team_allday;  //部门累计加速天数


    @ViewInject(R.id.person_day)
    private TextView person_day;// 当前个人加数天数

    @ViewInject(R.id.team_day)
    private TextView team_day;//当前部门加数天数

    @ViewInject(R.id.team_loadday)
    private TextView team_loadday;

    @ViewInject(R.id.person_loadday)
    private TextView person_loadday;

    @ViewInject(R.id.team_rednumber)
    private TextView team_rednumber;

    @ViewInject(R.id.person_rednumber)
    private TextView person_rednumber;

    @ViewInject(R.id.personshengyu_number)
            private TextView personshengyu_number; //个人可开启

    @ViewInject(R.id.teamshengyu_number)
    private  TextView teamshengyu_number;  //部门可开启

    private  int redbox_yikai=0;
    private int redbox_yikai_person=0;
    private int redbox_yikai_team=0;


//    @ViewInject(R.id.person_cb)
//    private CircleBar person_cb;
//
//    @ViewInject(R.id.team_cb)
//    private CircleBar team_cb;

    RedPackageInLetBean redPackageInLetBean;

    private int personday=0;
    private int teamday=0;
    private int allday=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ViewUtils.inject(this);
        Intent intent=getIntent();


        redPackageInLetBean= (RedPackageInLetBean) intent.getSerializableExtra("bean");

        if (!Util.isNull(redPackageInLetBean)){
            if (!Util.isNull(redPackageInLetBean.getAddUpFriend())){
                Log.e("天数1",redPackageInLetBean.getAddUpFriend().split("\\.")[0]+"KK");
                personday=Integer.parseInt(redPackageInLetBean.getAddUpFriend().split("\\.")[0]);
            }

            if (!Util.isNull(redPackageInLetBean.getAddUpSection())){
                Log.e("天数2",redPackageInLetBean.getAddUpSection().split("\\.")[0]+"KK");
                teamday=Integer.parseInt(redPackageInLetBean.getAddUpSection().split("\\.")[0]);
            }
            if (!Util.isNull(redPackageInLetBean.getZhongshu())){
                allday=Integer.parseInt(redPackageInLetBean.getZhongshu().split("\\.")[0]);
            }

            if (!Util.isNull(redPackageInLetBean.getRedbox_yikai())){
                redbox_yikai=Integer.parseInt(redPackageInLetBean.getRedbox_yikai().split("\\.")[0]);
            }

            if (!Util.isNull(redPackageInLetBean.getGrlj_count())){
                redbox_yikai_person=Integer.parseInt(redPackageInLetBean.getGrlj_count().split("\\.")[0]);
            }

            if (!Util.isNull(redPackageInLetBean.getBmlj_count())){
                redbox_yikai_team=Integer.parseInt(redPackageInLetBean.getBmlj_count().split("\\.")[0]);
            }


        }

        inin();
    }

    private void inin() {
        initview();
    }

    private void setDay(int personday,int teamday){
        day_tv.setText(Html.fromHtml("<font color='#FFFC00'><big><big>"+allday+"</big></big></font>"+""+"<font color='#ffffff'>天</font>"));
        person_allday.setText("已经加速天数:"+personday+"天");
        team_allday.setText("已经加速天数:"+teamday+"天");

//        person_day.setText("当前个人加速天数:"+personday+"天");
//        team_day.setText("当前部门加速天数:"+teamday+"天");

        if (Float.parseFloat(redPackageInLetBean.getJdFriend())==0){
            person_capb.setMaxValues(700);
            person_capb.setCurrentValues(0);
        }else{
            person_capb.setMaxValues(7);
            person_capb.setCurrentValues( Float.parseFloat(redPackageInLetBean.getJdFriend()));
            person_loadday.setText("等待加速天数:"+Float.parseFloat(redPackageInLetBean.getJdFriend())+"天");
        }


        if (Float.parseFloat(redPackageInLetBean.getJdSection())==0){

            team_capb.setMaxValues(700);
            team_capb.setCurrentValues(0);
            team_loadday.setText("等待加速天数:"+redPackageInLetBean.getZr_day()+"天");
        }else{
            if(Float.parseFloat(redPackageInLetBean.getJdSection())>7){
                team_capb.setMaxValues(Float.parseFloat(redPackageInLetBean.getJdSection()));
                team_capb.setCurrentValues( Float.parseFloat(redPackageInLetBean.getJdSection()));
                team_loadday.setText("等待加速天数:"+redPackageInLetBean.getZr_day()+"天");
            }else{
                team_capb.setMaxValues(7);
                team_capb.setCurrentValues( Float.parseFloat(redPackageInLetBean.getJdSection()));
                team_loadday.setText("等待加速天数:"+redPackageInLetBean.getZr_day()+"天");
            }

        }



        team_rednumber.setText("提前开启红包盒:"+redbox_yikai_team+"个");

        person_rednumber.setText("提前开启红包盒:"+redbox_yikai_person+"个");

        if (redbox_yikai>0){
            personred_tv.setBackgroundResource(R.drawable.butn1);
            personred_tv.setTextColor(Color.parseColor("#ffffff"));
        }

        if (redbox_yikai>0){
            teamred_tv.setBackgroundResource(R.drawable.butn1);
            teamred_tv.setTextColor(Color.parseColor("#ffffff"));
        }


        personshengyu_number.setText(Html.fromHtml("可开启红包个数:<font color='#FF2145'>"+redbox_yikai_person+"</font>个"));
        teamshengyu_number.setText(Html.fromHtml("可开启红包个数:<font color='#FF2145'>"+redbox_yikai_team+"</font>个"));
    }

    private void initview() {

        if (redPackageInLetBean==null) {
            Util.show("网络异常,请检查网络！", context);
        }else {

        }




    }


    @OnClick({R.id.personred_tv, R.id.teamred_tv
    ,R.id.personinfo_tv,R.id.teaminfo_tv
            ,R.id.top_back
            ,R.id.persontitle
            ,R.id.teamtitle
    })
    private void click(View view){
        Intent intent;
        VoipDialog  voipDialog;
        switch (view.getId()){
            case R.id.top_back:
                finish();
                break;
            case R.id.personred_tv:

                if (redbox_yikai>0){
                    intent= new Intent(context,RedEnvelopeBoxActivity.class);
                    intent.putExtra("state","1");
                    startActivity(intent);
                }

                break;
            case R.id.teamred_tv:
                if (redbox_yikai>0){
                    intent= new Intent(context,RedEnvelopeBoxActivity.class);
                    intent.putExtra("state","1");
                    startActivity(intent);
                }

                break;
            case R.id.personinfo_tv:
                if(redPackageInLetBean==null){
                    Util.show("网络异常,请检查网络！", context);
                    return;
                }

                if (redPackageInLetBean.getRedbox_weikai().equals("0")&&redPackageInLetBean.getRedbox_yikai().equals("0")
                        &&redPackageInLetBean.getRedbox_yilinqu().equals("0")
                        ){



                    if (redPackageInLetBean.getRedbean().equals("0")){
                        toaction("你还未封过红包盒,去充值",RedEnvelopeRechargeActivity.class,"去充值");
                    }else{
                        toaction("你还未封过红包盒,去封红包盒",ChangeRedEnvelopeActivity.class,"去封红包盒");

                    }



                    return;
                }

                intent= new Intent(context,PersonTeamDetailActivity.class);
                intent.putExtra("title","个人业绩");
                startActivity(intent);
                break;
            case R.id.teaminfo_tv:
                if(redPackageInLetBean==null){
                    Util.show("网络异常,请检查网络！", context);
                    return;
                }
                if (redPackageInLetBean.getRedbox_weikai().equals("0")
                        &&redPackageInLetBean.getRedbox_yikai().equals("0")
                        &&redPackageInLetBean.getRedbox_yilinqu().equals("0")
                        ){
                    if (redPackageInLetBean.getRedbean().equals("0")){
                        toaction("你还未封过红包盒,去充值",RedEnvelopeRechargeActivity.class,"去充值");
                    }else{
                        toaction("你还未封过红包盒,去封红包盒",ChangeRedEnvelopeActivity.class,"去封红包盒");

                    }
                    return;
                }
                intent= new Intent(context,PersonTeamDetailActivity.class);
                intent.putExtra("title","部门业绩");
                startActivity(intent);
                break;
            case R.id.teamtitle:


                intent = new Intent(context, MessageTitleActivity.class);
                intent.putExtra("title","部门加速规则");
                intent.putExtra("message",
                        "\t\t一、分享的会员如果是创客，则该会员就成为了自己的一个部门。\n"
                                +
                        "\t\t二、当部门有二个或二个以上时，就可以通过部门业绩加速开启自己的红包盒了。\n"
                                +
                        "\t\t三、加速时间 =【（所有小部门业绩之和÷本人充值金额）*7天】。\n"
                                +
                        "\t\t四、当天加速天数小于或大于7天时，不足天数或多余天数都会累加到满7天时间为止。\n"
                                +
                        "\t\t五、当天加速天数大于21天时，可加速开启三个红包盒，多余天数清零。\n");

                startActivity(intent);



                break;
            case R.id.persontitle:


                intent = new Intent(context, MessageTitleActivity.class);
                intent.putExtra("title","个人加速规则");
                intent.putExtra("message",
                        "\t\t一、红包盒是每七天自然开启一个。\n"
                                +
                        "\t\t二、分享的会员充值可提前开启红包盒（加速开启）。\n"
                                +
                        "\t\t三、加速时间 =【（分享的会员充值金额÷本人充值金额）*7天】。\n"
                                +
                        "\t\t四、当加速天数小于7天时，会一直累计到满7天时间为止。\n"
                                +
                        "\t\t五、当加速天数大于7天时，多余天数会继续保留等待下一次加速。\n");

                startActivity(intent);

                break;
        }
    }

    private void toaction(String str, final Class<?> cls,String surestr){
        View mContentView = LayoutInflater.from(context).inflate(R.layout.d_video_audio_dialog, null);



        int with= (int) (Util.getScreenWidth()*0.7);
        TextView message= (TextView) mContentView.findViewById(R.id.video_audio_dialog_content);
        Button cancel= (Button) mContentView.findViewById(R.id.video_audio_dialog_cancel);
        Button sure= (Button) mContentView.findViewById(R.id.video_audio_dialog_sure);
        sure.setText(surestr);
        message.setText(str);
        final PopupWindow mPopUpWindow=  ShowPopWindow.showShareWindow(mContentView,context,with, ViewGroup.LayoutParams.WRAP_CONTENT,0);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopUpWindow.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, cls);
                intent.putExtra("title","封入红包盒");
                context.startActivity(intent);
                finish();
            }
        });
    }

}
