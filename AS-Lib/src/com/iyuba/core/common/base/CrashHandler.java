package com.iyuba.core.common.base;

/**
 * 崩溃后的处理
 * 
 * @author chentong
 * @version 1.0
 * 
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.configation.RuntimeManager;

public class CrashHandler implements UncaughtExceptionHandler {

	private static final String TAG = "CrashHandler";
	private static final boolean DEBUG = true;

	private static final String PATH =
			Constant.envir + "Crash/log/";
	private static final String FILE_NAME = "crash";
	private static final String FILE_NAME_SUFFIX = ".trace";

	private static CrashHandler sInstance = new CrashHandler();
	private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
	private Context mContext;

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (sInstance == null)
			sInstance = new CrashHandler();
		return sInstance;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		mContext = ctx.getApplicationContext();
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
//		if (!handleException(ex) && mDefaultCrashHandler != null) {
//			// 如果用户没有处理则让系统默认的异常处理器来处理
//			mDefaultCrashHandler.uncaughtException(thread, ex);
//		} else {
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(0);
//		}
		try {
			dumpExceptionToSDCard(ex);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ex.printStackTrace();
		if(mDefaultCrashHandler != null){
			mDefaultCrashHandler.uncaughtException(thread,ex);
		}else {
			Process.killProcess(Process.myPid());
		}
//		if (!handleException(ex) && mDefaultHandler != null) {
//			mDefaultHandler.uncaughtException(thread, ex);
//		} else {
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		new Thread() {
			@Override
			public void run() {
				// Toast 显示需要出现在一个线程的消息队列中
				NotificationManager notiManager = (NotificationManager) RuntimeManager
						.getContext().getSystemService(
								Context.NOTIFICATION_SERVICE);
				notiManager.cancel(999);
			}
		}.start();
		return true;
	}

	private void dumpExceptionToSDCard(Throwable ex) throws IOException {
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			if(DEBUG){
				Log.w(TAG,"sdcard unmounted,skip dump exception");
				return;
			}
		}

		File dir = new File(PATH);
		if(!dir.exists()){
			dir.mkdirs();
		}
		long current = System.currentTimeMillis();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
		File file = new File(PATH+FILE_NAME+time+FILE_NAME_SUFFIX);

		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			pw.println(time);
			dumpPhoneInfo(pw);
			pw.println();
			ex.printStackTrace(pw);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException{
		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),PackageManager.GET_ACTIVITIES);
		//应用版本信息
		pw.print("App Version: ");
		pw.print(pi.versionName);
		pw.print('_');
		pw.println(pi.versionCode);
		//Android系统版本
		pw.print("OS version:");
		pw.print(Build.VERSION.RELEASE);
		pw.print('_');
		pw.println(Build.VERSION.SDK);
		//手机品牌
		pw.print("Vendor: ");
		pw.println(Build.MANUFACTURER);
		//手机型号
		pw.print("Model: ");
		pw.println(Build.MODEL);
		//CPU架构
		pw.print("CPU ABI: ");
		pw.println(Build.CPU_ABI);
	}

	private void uploadExceptionToServer(){

	}


//	private boolean handleException(Throwable ex) {
//		if (ex == null) {
//			return false;
//		}
//		//使用Toast来显示异常信息
//		new Thread() {
//			@Override
//			public void run() {
//				Looper.prepare();
//				Toast.makeText(mContext, "程序异常,即将退出……", Toast.LENGTH_LONG).show();
//				Looper.loop();
//			}
//		}.start();
//		//收集设备参数信息
//		collectDeviceInfo(mContext);
//		//保存日志文件
//		String filePath = saveCrashInfo2File(ex);
//		//上传服务器
//		//uploadFile(filePath,"www."+com.iyuba.core.util.Constant.IYBHttpHead+"");
//		return true;
//	}
}
