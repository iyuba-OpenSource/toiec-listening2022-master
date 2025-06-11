package com.iyuba.core.teacher.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.iyuba.core.teacher.fragment.HelpFragment;
import com.iyuba.core.R;

public class HelpFragmentAdapter extends FragmentPagerAdapter {
	protected static final int[] CONTENT = new int[] { R.drawable.help_toeic_1,
			R.drawable.help_toeic_2, R.drawable.help_toeic_3,
			R.drawable.help_toeic_4, R.drawable.help_toeic_5};

	public HelpFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return HelpFragment.newInstance(CONTENT[position]);
	}

	@Override
	public int getCount() {
		return CONTENT.length;
	}
}
