package com.mall.view.carMall;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.util.Util;
import com.mall.view.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class StyleDetailActivity extends AppCompatActivity {

    @ViewInject(R.id.title)
    TextView title;
    @ViewInject(R.id.name)
    TextView name;
    @ViewInject(R.id.location)
    TextView location;
    @ViewInject(R.id.date1)
    TextView date1;
    @ViewInject(R.id.date2)
    TextView date2;
    @ViewInject(R.id.carType)
    TextView carType;
    @ViewInject(R.id.price)
    TextView price;
    @ViewInject(R.id.orderNumber)
    TextView orderNumber;
    @ViewInject(R.id.message)
    TextView message;

    @ViewInject(R.id.image1)
    ImageView image1;

    @ViewInject(R.id.image2)
    ImageView image2;
    @ViewInject(R.id.image3)
    ImageView image3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_detail);
        ViewUtils.inject(this);
        Util.initTitle(this, "详情", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (getIntent().hasExtra("carbean")) {
            XibaoBean.ListBean listBean = (XibaoBean.ListBean) getIntent().getSerializableExtra("carbean");
            title.setText(listBean.getTitle());
            name.setText("会员名：" + listBean.getUserId());
            location.setText("坐标：" + listBean.getArea());
            date1.setText("出车时间：" + listBean.getCc_date());
            date2.setText("提车时间：" + listBean.getTc_date());
            carType.setText("汽车型号：" + listBean.getCar_name());
            price.setText("汽车售价：¥" + listBean.getCar_money().split("\\.")[0]);
            orderNumber.setText("排队单号：" + listBean.getOrderid());
            message.setText(Html.fromHtml(listBean.getInfo()));
            if (!Util.isNull(listBean.getImg1())) {
                setImage(image1, listBean.getImg1());
            }
            if (!Util.isNull(listBean.getImg2())) {
                setImage(image2, listBean.getImg2());
            }
            if (!Util.isNull(listBean.getImg3())) {
                setImage(image3, listBean.getImg3());
            }
        }
    }

    private void setImage(ImageView imageView, String image) {

//        Picasso.with(this).load("http://img.yda360.com" + image).into(imageView);
        Picasso.with(this).load("http://img.yda360.com" + image).transform(new Transformation() {
            @Override
            public Bitmap transform(Bitmap bitmap) {
                return setRoundRect(bitmap);
            }

            @Override
            public String key() {
                return "square()";
            }
        }).into(imageView);
    }

    private Bitmap setRoundRect(Bitmap bitmap) {
        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);
        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));
        canvas.drawRoundRect(rectF, 15, 15, paintColor);
        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);
        bitmap.recycle();
        return output;
    }
}
