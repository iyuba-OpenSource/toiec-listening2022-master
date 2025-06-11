package com.iyuba.headnewslib.fragment;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
	protected boolean isVisible;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInvisible();
		}
	}

	protected void onInvisible() {

	}

	protected void onVisible() {
		lazyload();
	}

	protected abstract void lazyload();

}
