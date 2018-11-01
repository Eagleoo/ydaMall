package com.mall.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.util.List;

import static com.mall.view.FeedbackFrame.strings;

public class ImageBrowseActivity extends AppCompatActivity {
    android.support.v4.view.ViewPager imageBrowseViewPager;
    TextView counttv, deletetv;
    //    List<String> strings = new ArrayList<>();
    int index = 0;
    ImageBrowseAdapter imageBrowseAdapter;

    interface CallBack {
        void doCall(List<String> strings);
    }

    CallBack callBack;

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browse);
        imageBrowseViewPager = (ViewPager) findViewById(R.id.imageBrowseViewPager);
        counttv = (TextView) findViewById(R.id.counttv);
        deletetv = (TextView) findViewById(R.id.deletetv);
        deletetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strings.remove(index);
                imageBrowseAdapter.notifyDataSetChanged();
                if (strings.size() > 0) {
                    imageBrowseViewPager.setCurrentItem(strings.size() - 1);
                    counttv.setText(strings.size() + "/" + strings.size());
                } else {
                    counttv.setText("");
                }

                if (callBack != null) {
                    callBack.doCall(strings);
                }

            }
        });
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            index = intent.getIntExtra("index", 0);
        }
        imageBrowseAdapter = new ImageBrowseAdapter(this, strings);
        imageBrowseViewPager.setAdapter(imageBrowseAdapter);
        imageBrowseViewPager.setCurrentItem(index);
        counttv.setText((index + 1) + "/" + strings.size());
        imageBrowseViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
                counttv.setText((position + 1) + "/" + strings.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class ImageBrowseAdapter extends PagerAdapter {
        Context context;
        List<String> strings;

        public ImageBrowseAdapter(Context context, List<String> strings) {
            this.context = context;
            this.strings = strings;
        }

        @Override
        public int getCount() {
            return strings != null ? strings.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView image = new PhotoView(context);
            image.enable();
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            image.setMaxScale(2.5f);

            // 加载图片
            Glide.with(context)
                    .load(strings.get(position))
                    .into(image);


            container.addView(image);
            return image;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            ((ViewPager) container).removeView((View) object);

        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
