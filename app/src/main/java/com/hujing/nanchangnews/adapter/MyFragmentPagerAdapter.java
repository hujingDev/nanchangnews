package com.hujing.nanchangnews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by acer on 2017/4/12.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> mFragmentList;
    List<String> mFragmentTitles;
    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList,
                                  List<String> fragmentTitles) {
        super(fm);
        mFragmentList=fragmentList;
        mFragmentTitles=fragmentTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }
}
