package com.iyuba.headnewslib;

import java.util.ArrayList;
import java.util.List;

import com.astuetz.PagerSlidingTabStrip;
import com.iyuba.headnewslib.adapter.HeadlinePagerAdapter;
import com.iyuba.headnewslib.fragment.HeadlinesFragment;
import com.iyuba.headnewslib.model.HeadlineTheme;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class HeadlineActivity extends FragmentActivity {
    private static final String TAG = HeadlineActivity.class.getSimpleName();

    View titleBar;
    ImageButton back_btn;
    TextView title;

    PagerSlidingTabStrip tabs;
    ViewPager viewPager;
    private HeadlinePagerAdapter pagerAdapter;

    private List<Fragment> fragmentList;

    HeadlineTheme mTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.headnewslib_headline);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        mTheme = getIntent().getParcelableExtra(HeadlineTheme.TAG);
        if (mTheme == null)
            mTheme = HeadlineTheme.DEFAULT_THEME;

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        initTitlebar();
        initViewPager();
        tabs.setViewPager(viewPager);
        tabs.setIndicatorColor(mTheme.indicatorColor);
    }

    private void initTitlebar() {
        titleBar = findViewById(R.id.main_titlebar);
        //titleBar.setBackgroundColor(mTheme.titleBgColor);
        title = (TextView) findViewById(R.id.titlebar_title);
        title.setText(getString(R.string.discover_headline));
        title.setTextColor(mTheme.titleTextColor);
        back_btn = (ImageButton) findViewById(R.id.titlebar_back_button);
        back_btn.setImageResource(mTheme.backBtnResId);
        back_btn.setOnClickListener(ocl);
    }

    OnClickListener ocl = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.titlebar_back_button) {
                onBackPressed();
            }
        }
    };

    private void initViewPager() {
        viewPager.setOffscreenPageLimit(1);
        fragmentList = new ArrayList<Fragment>();
        int[] ids = getResources().getIntArray(R.array.headline_category_id);
        for (int i = 0; i < ids.length; i++) {
            fragmentList.add(HeadlinesFragment.newInstance(ids[i], mTheme));
        }
        String[] titles = getResources().getStringArray(R.array.headline_category_title);
        pagerAdapter = new HeadlinePagerAdapter(getSupportFragmentManager(), fragmentList, titles);
        viewPager.setAdapter(pagerAdapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        viewPager.setPageMargin(pageMargin);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }

}
