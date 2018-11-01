package com.mall.serving.filmticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.mall.util.BitmapCache;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;

public class FileDetail extends Activity {
    private TextView rating_number, version, area, film_length, descT,
            director, actors, film_name;
    private RatingBar film_ratimg;
    private String name = "", directorS = "", score = "", mainactor = "", type = "", areaS = "", duration = "", desc = "", frontimage = "", juzhao = "";
    private Gallery juzhaoImage;
    private List<String> imageUrls = new ArrayList<String>();
    private GalleryAdapter adapter;
    private ImageView film_image;
    private RequestQueue requestqueue;
    private ImageLoader imageloader;
    private Button submit;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filmdetail);
        init();
    }

    private void init() {
        Util.initTitle(FileDetail.this, "影片详情", new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FileDetail.this.finish();
            }
        });

        getIntentData();
        initView();
    }

    private void initView() {
        film_name = (TextView) this.findViewById(R.id.film_name);
        film_ratimg = (RatingBar) this.findViewById(R.id.film_ratimg);
        rating_number = (TextView) this.findViewById(R.id.rating_number);
        version = (TextView) this.findViewById(R.id.version);
        area = (TextView) this.findViewById(R.id.area);
        film_length = (TextView) this.findViewById(R.id.film_length);
        descT = (TextView) this.findViewById(R.id.descT);
        director = (TextView) this.findViewById(R.id.director);
        actors = (TextView) this.findViewById(R.id.actors);
        film_name.setText(name);
        film_ratimg.setRating((float) (Double.parseDouble(score) / 2));
        rating_number.setText(score);
        version.setText(type);
        area.setText(areaS);
        film_length.setText(duration + "分钟");
        director.setText(directorS);
        actors.setText(mainactor);
        descT.setText("  " + desc);
        juzhaoImage = (Gallery) this.findViewById(R.id.juzhaoimage);
        downloadImage();
        film_image = (ImageView) this.findViewById(R.id.film_image);
        requestqueue = Volley.newRequestQueue(FileDetail.this);
        imageloader = new ImageLoader(requestqueue, new BitmapCache());
        ImageListener listener = ImageLoader.getImageListener(film_image, R.drawable.no_get_image, R.drawable.no_get_image);
        imageloader.get(frontimage, listener);
        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FileDetail.this.finish();
            }
        });
        juzhaoImage.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FileDetail.this, FlimDetailImage.class);
                intent.putExtra("juzhao", juzhao);
                intent.putExtra("position", position);
                FileDetail.this.startActivity(intent);
            }
        });
    }

    private void getIntentData() {
        name = this.getIntent().getStringExtra("name");
        directorS = this.getIntent().getStringExtra("director");
        mainactor = this.getIntent().getStringExtra("mainactor");
        score = this.getIntent().getStringExtra("score");
        type = this.getIntent().getStringExtra("version");
        duration = this.getIntent().getStringExtra("duration");
        areaS = this.getIntent().getStringExtra("area");
        desc = this.getIntent().getStringExtra("desc");
        frontimage = this.getIntent().getStringExtra("frontimage");
        juzhao = this.getIntent().getStringExtra("juzhao");
        for (int i = 0; i < juzhao.split(",,,").length; i++) {
            juzhao.split(juzhao.split(",,,")[i]);
            if (imageUrls.size() > 7) {
                break;
            } else {
                imageUrls.add(juzhao.split(",,,")[i]);
            }
        }
    }

    private void downloadImage() {
        Util.asynTask(FileDetail.this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (adapter == null) {
                    adapter = new GalleryAdapter(FileDetail.this);
                }
                adapter.setImageUrls(imageUrls);
                if (imageUrls.size() >= 1 && !Util.isNull(juzhao)) {
                    juzhaoImage.setAdapter(adapter);
                    juzhaoImage.setSelection(1);
                }
//				juzhaoImage.scrollBy(-30, 0);
            }

            @Override
            public Serializable run() {
                return null;
            }
        });
    }

    public class GalleryAdapter extends BaseAdapter {
        private Context c;
        private RequestQueue mQueue;
        private ImageLoader mImageLoader;
        private List<String> imageUrls = new ArrayList<String>();
        ;

        public GalleryAdapter(Context c) {
            this.c = c;
            mQueue = Volley.newRequestQueue(c);
            mImageLoader = new ImageLoader(mQueue, new BitmapCache());
        }

        private void setImageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.imageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return this.imageUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView image = new ImageView(c);
            image.setAdjustViewBounds(true);
            int _80dp = Util.dpToPx(c, 100);
            int _150dp = Util.dpToPx(c, 150);
            image.setLayoutParams(new Gallery.LayoutParams(_150dp, _150dp));
            ImageListener listener = ImageLoader.getImageListener(image, R.drawable.no_get_image, R.drawable.no_get_image);
            if (!Util.isNull(this.imageUrls.get(position))) {
                mImageLoader.get(imageUrls.get(position), listener);
            }
            return image;
        }
    }

}
