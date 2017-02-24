package com.example.s.why_no.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ywl on 10/15/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private String[] titles;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments,String[] titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
