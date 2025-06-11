package com.iyuba.toeiclistening.activity.test;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class TestPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;
    private String[] title;

    public TestPagerAdapter(FragmentManager fm,List<Fragment> fragmentList,String[] title) {
        super(fm);
        this.fragmentList =fragmentList;
        this.title =title;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    //字符序列
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
