package com.iyuba.toeiclistening.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.adapter.ViewPagerAdapter;
import com.iyuba.toeiclistening.widget.pageIndicator.PageIndicator;

import java.util.ArrayList;

public class HelpUseActivity extends Activity {
    private ViewPager viewPager;
    private ArrayList<View> mListViews;
    private PageIndicator pi;
    private ViewPagerAdapter viewPagerAdapter;
    private LayoutInflater inflater;
    private int lastIntoCount;
    private int goInfo = 0;// 0=第一次使用程序 1=从设置界面进入

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.help);

        goInfo = this.getIntent().getIntExtra("isFirstInfo", 0);
        inflater = getLayoutInflater();
        mListViews = new ArrayList<View>();
        addContent(R.drawable.help_toeic_1);
        addContent(R.drawable.help_toeic_2);
        addContent(R.drawable.help_toeic_3);
        addContent(R.drawable.help_toeic_4);
        addContent(R.drawable.help_toeic_5);
        pi = (PageIndicator) findViewById(R.id.pageIndicator);
        viewPagerAdapter = new ViewPagerAdapter(mListViews);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                pi.setCurrIndicator(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
                switch (arg0) {
                    case 0: // 停止变更
                        if (viewPager.getCurrentItem() == mListViews.size() - 1) {
                            lastIntoCount = lastIntoCount + 1;
                        }
                        break;
                    case 1:
                        break;
                    case 2: // 已经变更
                        lastIntoCount = 0;
                        break;
                }
                if (lastIntoCount > 1) {
                    if (arg0 == 0) {
                        if (goInfo == 0) {
                            Intent intent = new Intent();
                            intent.setClass(HelpUseActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                }
            }
        });
        viewPager.setAdapter(viewPagerAdapter);
        pi.setIndicator(mListViews.size());
        pi.setCurrIndicator(0);
    }

    public void addContent(int drawableid) {
        View view = inflater.inflate(R.layout.help_use_content, null);
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.help_use_content_rl);
        relativeLayout.setBackgroundResource(drawableid);
        mListViews.add(view);
    }
}
