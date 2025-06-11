package com.iyuba.core.discover.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.iyuba.core.R;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.base.Base;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.discover.activity.mob.SimpleMobClassList;
import com.umeng.analytics.MobclickAgent;

/**
 * 发现界面 为activitygroup设计
 * 
 * @author chentong
 * @version 1.0
 */
@Deprecated
public class DiscoverForAtGroup extends Base {
	private Context mContext;
	private View news, exam, mob, all, searchWord, vibrate,
			collectWord, saying, back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 CrashApplication.addActivity(this);
		setContentView(R.layout.layout_discover);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mContext = this;
		initWidget();
	}

	private void initWidget() {
		// TODO Auto-generated method stub
		back = findViewById(R.id.button_back);
		back.setVisibility(View.GONE);
		news = findViewById(R.id.news);
		news.setOnClickListener(ocl);
		exam = findViewById(R.id.exam);
		exam.setOnClickListener(ocl);
		mob = findViewById(R.id.mob);
		mob.setOnClickListener(ocl);
		all = findViewById(R.id.all);
		all.setOnClickListener(ocl);
		searchWord = findViewById(R.id.search_word);
		searchWord.setOnClickListener(ocl);
		saying = findViewById(R.id.saying);
		saying.setOnClickListener(ocl);
		collectWord = findViewById(R.id.collect_word);
		collectWord.setOnClickListener(ocl);
//		findFriend = findViewById(R.id.discover_search_friend);
//		findFriend.setOnClickListener(ocl);
		vibrate = findViewById(R.id.discover_vibrate);
		vibrate.setOnClickListener(ocl);
	}

	private OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent;
			int id = v.getId();
			if (id == R.id.news) {
				intent = new Intent(mContext, AppGround.class);
				intent.putExtra("title", R.string.discover_news);
				startActivity(intent);
			} else if (id == R.id.exam) {
				intent = new Intent(mContext, AppGround.class);
				intent.putExtra("title", R.string.discover_exam);
				startActivity(intent);
			} else if (id == R.id.mob) {
				intent = new Intent(mContext, SimpleMobClassList.class);
				intent.putExtra("title", R.string.discover_mobclass);
				startActivity(intent);
			} else if (id == R.id.all) {
				intent = new Intent();
				intent.setClass(mContext, Web.class);
				intent.putExtra("url", "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/android");
				intent.putExtra("title",
						mContext.getString(R.string.discover_appall));
				startActivity(intent);
			} else if (id == R.id.search_word) {
				intent = new Intent();
				intent.setClass(mContext, SearchWordActivity.class);
				startActivity(intent);
			} else if (id == R.id.saying) {
				intent = new Intent();
				intent.setClass(mContext, Saying.class);
				startActivity(intent);
			} else if (id == R.id.collect_word) {
				intent = new Intent();
				intent.setClass(mContext, WordCollection.class);//WordCollectionNew
				startActivity(intent);
			} /*else if (id == R.id.discover_search_friend) {
				if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
					intent = new Intent();
					intent.setClass(mContext, SearchFriend.class);
					startActivity(intent);
				} else {
					intent = new Intent();
					intent.setClass(mContext, Login.class);
					startActivity(intent);
				}
			} else {
			}*/
		}
	};

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
}
