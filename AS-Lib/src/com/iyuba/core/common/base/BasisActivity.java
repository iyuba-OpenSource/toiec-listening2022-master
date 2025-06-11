/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.base;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import androidx.fragment.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 类名
 * 
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class BasisActivity extends FragmentActivity {
	private WindowManager mWindowManager;
	private static ArrayList<View> myView;
	private static boolean isNight;
	private boolean isAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
//		PushAgent.getInstance(BasisActivity.this).onAppStart();
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		}
		if (myView == null) {
			myView = new ArrayList<View>();
		}
		if (isNight && !isAdd) {
			night();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override 
	public void finish(){
		super.finish();
		remove();
	}

	public void night() {
		isNight = true;
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.BOTTOM;
		params.y = 10;
		View temp = new View(this);
		temp.setBackgroundColor(0x80000000);
		try {
			mWindowManager.addView(temp, params);
			myView.add(temp);
			isAdd = true;
		} catch (Exception e) {
		}
	}

	public void day() {
		isNight = false;
		remove();
	}

	private void remove() {
		int index = myView.size();
		if (myView != null && index != 0) {
			mWindowManager.removeView(myView.get(index - 1));
			myView.remove(index - 1);
			isAdd = false;
		}
	}

}
