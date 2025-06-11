package com.iyuba.toeiclistening.widget.pageIndicator;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iyuba.toeiclistening.R;


public class PageIndicator extends LinearLayout {

	private Context mContext;
	private List<ImageView> indicators = new ArrayList<ImageView>();
	private int currPage = 0;

	public PageIndicator(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initPageIndicator(context);
	}

	public PageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initPageIndicator(context);
	}

	public void initPageIndicator(Context context) {
		this.mContext = context;
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL
				| Gravity.CENTER_VERTICAL);
		refPageIndicator();

	}

	public void refPageIndicator() {
		if (indicators != null && indicators.size() != 0) {
			for (int i = 0; i < indicators.size(); i++) {
				ImageView ivTemp = indicators.get(i);
				addView(ivTemp);
			}
		} else {
			ImageView ivTemp = new ImageView(mContext);
			ivTemp.setPadding(5, 5, 5, 5);
			ivTemp.setImageResource(R.drawable.page_indicator_s);
			indicators.add(ivTemp);
			addView(ivTemp);
		}
	}

	public void setIndicator(int pageNum) {
		indicators.clear();
		removeAllViews();
		for (int i = 0; i < pageNum; i++) {
			ImageView ivTemp = new ImageView(mContext);
			ivTemp.setPadding(5, 5, 5, 5);
			ivTemp.setImageResource(R.drawable.page_indicator_h);
			indicators.add(ivTemp);
		}
		refPageIndicator();
		setCurrIndicator(currPage);
	}

	public void setCurrIndicator(int currPage) {
		this.currPage = currPage;
		for (int i = 0; i < indicators.size(); i++) {
			ImageView ivTemp = indicators.get(i);
			if (i == this.currPage) {
				ivTemp.setImageResource(R.drawable.page_indicator_s);

			} else {
				ivTemp.setImageResource(R.drawable.page_indicator_h);
			}
		}
	}

}
