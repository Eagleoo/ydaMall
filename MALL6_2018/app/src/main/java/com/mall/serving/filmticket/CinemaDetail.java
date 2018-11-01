package com.mall.serving.filmticket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.mall.util.BitmapCache;
import com.mall.util.Util;
import com.mall.view.R;

public class CinemaDetail extends Activity {
    private ImageView cinema_image;
    private TextView theatername, rating_number, theater_address, phone, intro;
    private RatingBar theater_ratimg;
    private String cinemaName, address, logo, phoneNumber, introduction, rating, latlng;
    private RequestQueue requestqueue;
    private ImageLoader imageloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theater_detail);
        init();
    }

    private void init() {
        Util.initTitle(this, "影院详情", new OnClickListener() {
            @Override
            public void onClick(View v) {
                CinemaDetail.this.finish();
            }
        });
        initView();
        getIntentData();
    }

    private void initView() {
        cinema_image = (ImageView) this.findViewById(R.id.cinema_image);
        theatername = (TextView) this.findViewById(R.id.theatername);
        rating_number = (TextView) this.findViewById(R.id.rating_number);
        theater_address = (TextView) this.findViewById(R.id.theater_address);
        phone = (TextView) this.findViewById(R.id.phone);
        intro = (TextView) this.findViewById(R.id.intro);
        theater_ratimg = (RatingBar) this.findViewById(R.id.theater_ratimg);
        theater_address.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开地图Marker
                Intent intent = new Intent(CinemaDetail.this, CinemaMarker.class);
                intent.putExtra("latlng", latlng);
                CinemaDetail.this.startActivity(intent);
            }
        });
    }

    private void getIntentData() {
        address = this.getIntent().getStringExtra("address");
        phoneNumber = this.getIntent().getStringExtra("phone");
        introduction = this.getIntent().getStringExtra("intro");
        rating = this.getIntent().getStringExtra("rating");
        logo = this.getIntent().getStringExtra("logo");
        latlng = this.getIntent().getStringExtra("latlng");
        cinemaName = this.getIntent().getStringExtra("name");

        theater_address.setText(address);
        rating_number.setText(rating + "分");
        theater_ratimg.setRating((float) (Double.parseDouble(rating) / 2));
        phone.setText("  " + phoneNumber);
        intro.setText(introduction);
        theatername.setText(cinemaName);

        requestqueue = Volley.newRequestQueue(CinemaDetail.this);
        imageloader = new ImageLoader(requestqueue, new BitmapCache());
        ImageListener listener = ImageLoader.getImageListener(cinema_image, R.drawable.no_get_image, R.drawable.no_get_image);
        imageloader.get(logo, listener);
    }

}
