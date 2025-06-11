package com.iyuba.headnewslib.adapter;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HeadlinePagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> list;
	private String[] titles;

	public HeadlinePagerAdapter(FragmentManager fm, List<Fragment> list, String[] titles) {
		super(fm);
		this.list = list;
		this.titles = titles;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	@Override
	public Fragment getItem(int position) {
		return list.get(position);
	}

	@Override
	public int getCount() {
		return list.size();
	}

}
