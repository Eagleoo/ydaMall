package com.mall.adapter.MyFragmentPagerAdapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MECHREVO on 2017/11/1.
 */

public class BaseFragmentAdapter extends FragmentPagerAdapter {

    protected List<Fragment> mFragmentList;

    protected String[] mTitles;

    private Context mContext ;

    public BaseFragmentAdapter(Context context,FragmentManager fm) {
        this(context,fm, null, null);
    }

    public BaseFragmentAdapter(Context context, FragmentManager fm, List<Fragment> fragmentList, String[] mTitles) {
        super(fm);
        this.mContext = context ;
        if (fragmentList == null) {
            fragmentList = new ArrayList<>();
        }
        this.mFragmentList = fragmentList;
        this.mTitles = mTitles;
    }

    public void add(Fragment fragment) {
        if (isEmpty()) {
            mFragmentList = new ArrayList<>();

        }
        mFragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        //        Logger.i("BaseFragmentAdapter position=" +position);
        return isEmpty() ? null : mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return isEmpty() ? 0 : mFragmentList.size();
    }

    public boolean isEmpty() {
        return mFragmentList == null;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    /**
     * 定义一个方法 返回tab中要显示的内容；
     * 注意：在我们使用自定义的tab时，将getPagerTitle返回null，不设置也一样的
     * @param position
     * @return
     */





    /*  @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }*/


}