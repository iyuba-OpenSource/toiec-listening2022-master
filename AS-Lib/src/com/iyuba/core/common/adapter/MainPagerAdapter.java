package com.iyuba.core.common.adapter;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.view.View;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
	
	private List<Fragment> mFragments;
	
	public MainPagerAdapter(FragmentManager fm,List<Fragment> list){
		super(fm);
		mFragments = list;
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存�?
		return mFragments.size();
	}
	
	@Override
	public Fragment getItem(int arg0) {
		// TODO 自动生成的方法存�?
		Fragment page = null;
        if (mFragments.size() > arg0) {
            page = mFragments.get(arg0);
            return page;
//            if (page != null ) {
//            	if (arg0 == 0) {
//				}
//                return page;
//            }
        }
			return null;
	}
	
	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub
		return super.instantiateItem(container, position);
	}

}
