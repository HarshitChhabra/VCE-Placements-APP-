package com.example.pc.hackathonacumen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AppliedSelected_pagerAdapter extends FragmentPagerAdapter {

    private String title[] = {"Applied","Selected"};

    public AppliedSelected_pagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return AppliedSelecteed_tabFragment.getInstance(position);
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
