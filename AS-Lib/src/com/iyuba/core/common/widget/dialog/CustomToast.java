package com.iyuba.core.common.widget.dialog;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * 重载后toast 可同时触发
 * 
 * @author 陈彤
 */
public class CustomToast {

	private static Toast mToast;
	private static Handler mHandler = new Handler();
	private static Runnable r = new Runnable() {
		public void run() {
			mToast.cancel();
		}
	};

//	public static void showToast(Context mContext, String text, int duration) {
//
//		mHandler.removeCallbacks(r);
//		if (mToast != null)
//			mToast.setText(text);
//		else
//			mToast = Toast.makeText(mContext, text, duration);
//		mHandler.postDelayed(r, duration);
//
//		mToast.show();
//	}


//	public static void showToast(Context mContext, int resId, int duration) {
//		showToast(mContext, mContext.getResources().getString(resId), duration);
//	}
//
	public static void showToast(Context mContext, int resId) {
		if (mToast!=null){
			mToast.cancel();
		}
		mToast = Toast.makeText(mContext, mContext.getResources().getString(resId),Toast.LENGTH_SHORT);
		mToast.show();
	}

	public static void showToast(Context mContext, String text) {
		//showToast(mContext, text, 1000);
		if (mToast!=null){
			mToast.cancel();
		}
		mToast = Toast.makeText(mContext,text,Toast.LENGTH_SHORT);
		mToast.show();
	}

}
