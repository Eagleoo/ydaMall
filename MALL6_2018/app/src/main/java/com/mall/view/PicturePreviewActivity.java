package com.mall.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.mall.util.BitmapAndStringUtils;
import com.squareup.picasso.Picasso;
import com.stx.xhb.xbanner.XBanner;

import java.util.ArrayList;

import cn.finalteam.rxgalleryfinal.bean.MediaBean;

public class PicturePreviewActivity extends AppCompatActivity {
    XBanner vp;
    android.support.v4.view.ViewPager mPager;
    TextView top_back;
    public  static  String POSITION="position";
    public  static  String MEDIABEANLIST="mediabeanlist";

    private int pos;

    ArrayList<MediaBean> mylist;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        context=this;

        init();
    }

    private void init() {
        initview();
        initListen();
        initdata();

    }

    private void initListen() {
        top_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initview() {
        vp= (XBanner) findViewById(R.id.vp);
        mPager= (ViewPager) findViewById(R.id.pager);
        top_back= (TextView) findViewById(R.id.top_back);
    }
    private void initdata() {
        Intent intent=getIntent();
        int position =intent.getIntExtra(POSITION,0);
        ArrayList<MediaBean> list=intent.getParcelableArrayListExtra(MEDIABEANLIST);
        setviewpager(list,position);
    }

    private void setviewpager(final ArrayList<MediaBean> list, int position ) {
        mylist=list;
        vp.setData(list,null);
        vp.setmAdapter(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
//				Glide.with(mcontext).load(imageUrls.get(position)).into((ImageView) view);
                pos=position;
                MediaBean bean= (MediaBean) model;
                Log.e("图片地址",bean.getOriginalPath());
                Picasso.with(context).load("file://"+bean.getOriginalPath()).into((ImageView) view);
            }

        });
        vp.invalidate();
//        mPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PhotoView view = new PhotoView(context);
                view.enable();
//                view.enableRotate();
                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                view.setImageResource(list.get(position).getOriginalPath());
                String fileName = list.get(position).getOriginalPath();
                Log.e("图片信息",list.get(position).toString());
                Bitmap bm = BitmapAndStringUtils.readBitMap(fileName);
                Drawable drawable = new BitmapDrawable(bm);
                Log.e("图片信息2",drawable.getIntrinsicWidth()+"???"+drawable.getIntrinsicHeight());
                view.setImageDrawable(drawable);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PicturePreviewActivity.MEDIABEANLIST,mylist);
        setResult(123,intent);
    }
}
