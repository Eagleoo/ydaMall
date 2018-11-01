package com.mall.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mall.BusinessDetails.BusinessDetailsActivity;
import com.mall.model.RecBussModel;
import com.mall.util.Util;
import com.mall.view.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2018/1/16.
 */

public class HomeBottomBannerPagerAdapter extends PagerAdapter {

    List<RecBussModel.Adv2Bean> adv2Beans;

    Context context;

    public HomeBottomBannerPagerAdapter(List<RecBussModel.Adv2Bean> adv2Beans, Context context) {
        this.adv2Beans = adv2Beans;
        this.context = context;
    }

    @Override
    public int getCount() {
        return adv2Beans.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        final RecBussModel.Adv2Bean adv2Bean = adv2Beans.get(position);


        View root = LayoutInflater.from(context).inflate(R.layout.itembottombanner, view, false);
        final ImageView banneriv = (ImageView) root.findViewById(R.id.banneriv);
        banneriv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showIntent(
                        context,
                        BusinessDetailsActivity.class,
                        new String[]{BusinessDetailsActivity.BUSINESS_ID, "name",
                                "x", "y", "face", BusinessDetailsActivity.BUSINESS_Favorite},
                        new String[]{adv2Bean.getId(),
                                adv2Bean.getName(),
                                "",
                                "", "", adv2Bean.getFavorite()});
            }
        });

        Picasso.with(context).load(adv2Bean.getPath()).into(banneriv);
        TextView shapename = root.findViewById(R.id.shapename);
        shapename.setText(adv2Bean.getName());
        view.addView(root);
        return root;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

}
