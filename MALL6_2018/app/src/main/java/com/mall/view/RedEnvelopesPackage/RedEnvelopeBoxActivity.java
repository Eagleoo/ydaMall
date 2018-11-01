package com.mall.view.RedEnvelopesPackage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.mall.BasicActivityFragment.BasicActivity;
import com.mall.Fragment.ListFragment;
import com.mall.MessageEvent;
import com.mall.adapter.MyFragmentPagerAdapter.BaseFragmentAdapter;
import com.mall.model.RedPakageBean;
import com.mall.util.Util;
import com.mall.view.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class RedEnvelopeBoxActivity extends BasicActivity  {







    @BindView(R.id.weikaiqi_tv)
             TextView weikaiqi_tv;
    @BindView(R.id.yikaiqi_tv)
             TextView yikaiqi_tv;
    @BindView(R.id.yilingqu_tv)
              TextView yilingqu_tv;

    @BindView(R.id.xiaoxitongji)
    TextView xiaoxitongji;



    @BindView(R.id.viewpager)
    ViewPager vpContent;

    List<Fragment> mFragments;

    ListFragment listFragment;

    private String state="0";


    String[] mTitles = new String[]{
            "主页"
    };

    @Override
    public int getContentViewId() {
        return R.layout.activity_red_envelope_box;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {

        init();
        setupViewPager(state);
    }

    @Override
    public void EventCallBack(MessageEvent messageEvent) {

        if (messageEvent!=null){
            RedPakageBean redPakageBean =messageEvent.getRedPakageBean();
            if (redPakageBean!=null){
                String state=messageEvent.getMessage();
                if (state.equals("0")){
                    xiaoxitongji.setText("您还有"+redPakageBean.getKqcount()+"个红包盒未开启，总价值"+redPakageBean.getKqmoney()+"元");
                }else if(state.equals("1")){
                    xiaoxitongji.setText("您已成功开启"+redPakageBean.getKqcount()+"个红包盒，总价值"+redPakageBean.getKqmoney()+"元");
                }else if(state.equals("2")){
                    xiaoxitongji.setText("已领取"+redPakageBean.getKqcount()+"个红包盒，总价值"+redPakageBean.getKqmoney()+"元");
                }
            }
        }

    }


    private void setupViewPager(String state) {

        mFragments = new ArrayList<>();
             listFragment = ListFragment.newInstance(state);
            mFragments.add(listFragment);

        BaseFragmentAdapter adapter =
                new BaseFragmentAdapter(context,getSupportFragmentManager(), mFragments, mTitles);

        vpContent.setAdapter(adapter);



    }

    private void init() {
        Intent intent =getIntent();
        String str=intent.getStringExtra("state");
        if (!Util.isNull(str)){
            state=str;
        }

        if (state.equals("1")){
            setState(yikaiqi_tv);
        }

    }



    @butterknife.OnClick({R.id.weikaiqi_tv,R.id.yikaiqi_tv,R.id.yilingqu_tv,R.id.top_back})
  public    void click(View view){
        String state="0";
        switch (view.getId()){

            case R.id.top_back:
                finish();
                break;
            case R.id.weikaiqi_tv:
                setState(view);
                state="0";

                break;
            case R.id.yikaiqi_tv:
                state="1";
                setState(view);

                break;
            case R.id.yilingqu_tv:
                state="2";
                setState(view);

                break;


        }
        if (listFragment!=null){
            listFragment.setData(state);
        }

    }



    private void  setState(View view){

        weikaiqi_tv.setBackgroundResource(R.drawable.goldbox);
        yikaiqi_tv.setBackgroundResource(R.drawable.goldbox);
        yilingqu_tv.setBackgroundResource(R.drawable.goldbox);

        weikaiqi_tv.setTextColor(Color.parseColor("#000000"));
        yikaiqi_tv.setTextColor(Color.parseColor("#000000"));
        yilingqu_tv.setTextColor(Color.parseColor("#000000"));


        switch (view.getId()){
            case R.id.weikaiqi_tv:
                weikaiqi_tv.setBackgroundResource(R.drawable.darkredbox);
                weikaiqi_tv.setTextColor(Color.parseColor("#ffffff"));
                state="0";
                break;
            case R.id.yikaiqi_tv:
                yikaiqi_tv.setBackgroundResource(R.drawable.darkredbox);
                yikaiqi_tv.setTextColor(Color.parseColor("#ffffff"));
                state="1";
                break;
            case R.id.yilingqu_tv:
                yilingqu_tv.setBackgroundResource(R.drawable.darkredbox);
                yilingqu_tv.setTextColor(Color.parseColor("#ffffff"));
                state="2";
                break;
        }
//        getRedEnListInfo();


    }




}
