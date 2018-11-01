package com.mall.serving.filmticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;

public class FlimDetailImage extends Activity {
    private ImageView flim_jz;
    private List<String> imageUrls = new ArrayList<String>();
    private int postion = 0;
    private RequestQueue mQueue;
    private ImageLoader mImageLoader;
    private float originalX = 0;
    private float originalY = 0;
    private ViewPager detail_image;
    private List<ImageView> images = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flimdetail_image);
        init();
    }

    private void init() {
        Util.initTitle(FlimDetailImage.this, "影片剧照", new OnClickListener() {
            @Override
            public void onClick(View v) {
                FlimDetailImage.this.finish();
            }
        });
//		flim_jz=(ImageView) this.findViewById(R.id.flim_jz);
        detail_image = (ViewPager) this.findViewById(R.id.detail_image);
        getIntentData();
        initViewPager();
        /*downloadImage(postion);
		flim_jz.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					originalX=event.getX();
					originalY=event.getY();
					System.out.println("起始位置====="+originalX);
					break;
				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_MOVE:   
					float newX=event.getX();
					float newY=event.getY();
					float distance=newX-originalX;
					if(distance>150f){
						postion-=1;
						downloadImage(postion-1);
					}else if(distance<-150f){
						postion+=1;
						downloadImage(postion+1);
					}
					break;
				}
				return true;               
			}
		});*/
    }

    private void initViewPager() {
        Util.asynTask(FlimDetailImage.this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if ("1".equals(runData + "")) {
                    detail_image.setAdapter(new viewpagerAdapter());
                    detail_image.setCurrentItem(postion);
                    detail_image.setOnPageChangeListener(new OnPageChangeListener() {
                        @Override
                        public void onPageSelected(int arg0) {
                            detail_image.setCurrentItem(arg0);
                        }

                        @Override
                        public void onPageScrolled(int arg0, float arg1, int arg2) {
                        }

                        @Override
                        public void onPageScrollStateChanged(int arg0) {
                        }
                    });
                }
            }

            @Override
            public Serializable run() {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
                for (int i = 0; i < imageUrls.size(); i++) {
                    ImageView v = new ImageView(FlimDetailImage.this);
                    String href = imageUrls.get(i);
                    String name = href.substring(href.lastIndexOf("Upload"));
                    String path = "/sdcard/lmsj/prod/" + i + name;
                    try {
                        Util.downLoad(href, path);
                        v.setImageBitmap(Util.getLocalBitmap(path, opts));
                    } catch (Exception e) {
                        v.setImageBitmap(Util.getBitmap(href));
                    }
                    images.add(v);
                }
                return "1";
            }
        });
    }

    private void getIntentData() {
        String juzhao = this.getIntent().getStringExtra("juzhao");
        juzhao = this.getIntent().getStringExtra("juzhao");
        for (int i = 0; i < juzhao.split(",,,").length; i++) {
            juzhao.split(juzhao.split(",,,")[i]);
            if (imageUrls.size() > 7) {
                break;
            } else {
                imageUrls.add(juzhao.split(",,,")[i]);
            }
        }
        for (int i = 0; i < imageUrls.size(); i++) {
            System.out.println(imageUrls.get(i));
        }
        postion = this.getIntent().getIntExtra("position", 0);
        if (postion < 0) {
            postion = 0;
        } else if (postion >= imageUrls.size()) {
            postion = (imageUrls.size() - 1);
        }
    }

    public class viewpagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(images.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(images.get(position));
            return images.get(position);
        }

    }

    @Override
    protected void onStop() {
        images.clear();
        super.onStop();
    }
}
