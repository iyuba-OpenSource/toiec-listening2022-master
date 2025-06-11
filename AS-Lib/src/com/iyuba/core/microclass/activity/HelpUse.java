package com.iyuba.core.microclass.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.widget.viewpagerindicator.CirclePageIndicator;
import com.iyuba.core.microclass.adapter.HelpFragmentAdapter;
import com.iyuba.core.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 使用说明Activity
 * 
 * @author chentong
 * 
 */
public class HelpUse extends FragmentActivity {
	private ViewPager viewPager;
	private CirclePageIndicator pi;
	private String source;
	private Button close;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.help_use);
		 CrashApplication.addActivity(this);
		source = getIntent().getStringExtra("source");
		pi = (CirclePageIndicator) findViewById(R.id.pageIndicator);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		close = (Button) findViewById(R.id.close);
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (source.equals("welcome")) {
					Intent intent = new Intent();
					intent.setClass(HelpUse.this, SettingActivity.class);
					startActivity(intent);
					finish();
				} else {
					finish();
				}
			}
		});
		viewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				pi.setCurrentItem(arg0);
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
					if (viewPager.getCurrentItem() == 4) {
						handler.sendEmptyMessage(1);
					} else {
						handler.sendEmptyMessage(2);
					}
					break;
				}
			}
		});
		viewPager.setAdapter(new HelpFragmentAdapter(
				getSupportFragmentManager()));
		pi.setViewPager(viewPager, false);
		pi.setCentered(true);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (source.equals("welcome")) {
			Intent intent = new Intent();
			intent.setClass(HelpUse.this, SettingActivity.class);
			startActivity(intent);
			finish();
		} else {
			finish();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				close.setVisibility(View.VISIBLE);
				break;
			case 2:
				close.setVisibility(View.GONE);
				break;
			}

		}
	};
}