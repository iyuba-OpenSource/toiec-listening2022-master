package com.iyuba.toeiclistening.frame.crash;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.TreeSet;

import com.iyuba.toeiclistening.service.NotificationService;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler {
	/** Debug Log Tag */
	public static final String TAG = "CrashHandler";
	/** 鏄惁寮?惎鏃ュ織杈撳嚭, 鍦―ebug鐘舵?涓嬪紑鍚? 鍦≧elease鐘舵?涓嬪叧闂互鎻愬崌绋嬪簭鎬ц兘 */
	public static final boolean DEBUG = true;
	/** CrashHandler瀹炰緥 */
	private static CrashHandler INSTANCE;
	/** 绋嬪簭鐨凜ontext瀵硅薄 */
	private Context mContext;
	/** 绯荤粺榛樿鐨刄ncaughtException澶勭悊绫? */
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	/** 浣跨敤Properties鏉ヤ繚瀛樿澶囩殑淇℃伅鍜岄敊璇爢鏍堜俊鎭? */
	private Properties mDeviceCrashInfo = new Properties();
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
	private static final String STACK_TRACE = "STACK_TRACE";
	/** 閿欒鎶ュ憡鏂囦欢鐨勬墿灞曞悕 */
	private static final String CRASH_REPORTER_EXTENSION = ".cr";

	/** 淇濊瘉鍙湁涓?釜CrashHandler瀹炰緥 */
	private CrashHandler() {
	}

	/** 鑾峰彇CrashHandler瀹炰緥 ,鍗曚緥妯″紡 */
	public static CrashHandler getInstance() {
		if (INSTANCE == null)
			INSTANCE = new CrashHandler();
		return INSTANCE;
	}

	/**
	 * 鍒濆鍖?娉ㄥ唽Context瀵硅薄, 鑾峰彇绯荤粺榛樿鐨刄ncaughtException澶勭悊鍣?
	 * 璁剧疆璇rashHandler涓虹▼搴忕殑榛樿澶勭悊鍣?
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 褰揢ncaughtException鍙戠敓鏃朵細杞叆璇ュ嚱鏁版潵澶勭悊
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 濡傛灉鐢ㄦ埛娌℃湁澶勭悊鍒欒绯荤粺榛樿鐨勫紓甯稿鐞嗗櫒鏉ュ鐞?
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			// Sleep涓?細鍚庣粨鏉熺▼搴?
			// 鏉ヨ绾跨▼鍋滄涓?細鏄负浜嗘樉绀篢oast淇℃伅缁欑敤鎴凤紝鐒跺悗Kill绋嬪簭
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error : ", e);
			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}
	}

	/**
	 * 鑷畾涔夐敊璇鐞?鏀堕泦閿欒淇℃伅 鍙戦?閿欒鎶ュ憡绛夋搷浣滃潎鍦ㄦ瀹屾垚.
	 * 寮?彂鑰呭彲浠ユ牴鎹嚜宸辩殑鎯呭喌鏉ヨ嚜瀹氫箟寮傚父澶勭悊閫昏緫
	 * 
	 * @param ex
	 * @return true:濡傛灉澶勭悊浜嗚寮傚父淇℃伅;鍚﹀垯杩斿洖false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		final String msg = ex.getLocalizedMessage();
		// 浣跨敤Toast鏉ユ樉绀哄紓甯镐俊鎭?
		new Thread() {
			@Override
			public void run() {
				// Toast 鏄剧ず闇?鍑虹幇鍦ㄤ竴涓嚎绋嬬殑娑堟伅闃熷垪涓?
				Looper.prepare();
				Intent intent = new Intent(mContext, NotificationService.class);
				intent.setAction(NotificationService.ACTION_NOTIFICATION_CONTROL);
				intent.putExtra(NotificationService.COMMAND_KEY,
						NotificationService.KEY_COMMAND_REMOVE);
				mContext.startService(intent);
				Looper.loop();
			}
		}.start();

		if (DEBUG) {
			// 鏀堕泦璁惧淇℃伅
			collectCrashDeviceInfo(mContext);
			// 淇濆瓨閿欒鎶ュ憡鏂囦欢
			// String crashFileName = saveCrashInfoToFile(ex);
			saveCrashInfoToFile(ex);
			// // 鍙戦?閿欒鎶ュ憡鍒版湇鍔″櫒
			// sendCrashReportsToServer(mContext);
		}
		return true;
	}

	/**
	 * 鏀堕泦绋嬪簭宕╂簝鐨勮澶囦俊鎭?
	 * 
	 * @param ctx
	 */
	public void collectCrashDeviceInfo(Context ctx) {
		try {
			// Class for retrieving various kinds of information related to the
			// application packages that are currently installed on the device.
			// You can find this class through getPackageManager().
			PackageManager pm = ctx.getPackageManager();
			// getPackageInfo(String packageName, int flags)
			// Retrieve overall information about an application package that is
			// installed on the system.
			// public static final int GET_ACTIVITIES
			// Since: API Level 1 PackageInfo flag: return information about
			// activities in the package in activities.
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				// public String versionName The version name of this package,
				// as specified by the <manifest> tag's versionName attribute.
				mDeviceCrashInfo.put(VERSION_NAME,
						pi.versionName == null ? "not set" : pi.versionName);
				// public int versionCode The version number of this package,
				// as specified by the <manifest> tag's versionCode attribute.
				mDeviceCrashInfo.put(VERSION_CODE, pi.versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Error while collect package info", e);
		}
		// 浣跨敤鍙嶅皠鏉ユ敹闆嗚澶囦俊鎭?鍦˙uild绫讳腑鍖呭惈鍚勭璁惧淇℃伅,
		// 渚嬪: 绯荤粺鐗堟湰鍙?璁惧鐢熶骇鍟?绛夊府鍔╄皟璇曠▼搴忕殑鏈夌敤淇℃伅
		// 杩斿洖 Field 瀵硅薄鐨勪竴涓暟缁勶紝杩欎簺瀵硅薄鍙嶆槧姝?Class 瀵硅薄鎵?〃绀虹殑绫绘垨鎺ュ彛鎵?０鏄庣殑鎵?湁瀛楁
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				// setAccessible(boolean flag)
				// 灏嗘瀵硅薄鐨?accessible 鏍囧織璁剧疆涓烘寚绀虹殑甯冨皵鍊笺?
				// 閫氳繃璁剧疆Accessible灞炴?涓簍rue,鎵嶈兘瀵圭鏈夊彉閲忚繘琛岃闂紝涓嶇劧浼氬緱鍒颁竴涓狪llegalAccessException鐨勫紓甯?
				field.setAccessible(true);
				mDeviceCrashInfo.put(field.getName(), field.get(null));
				if (DEBUG) {
					Log.d(TAG, field.getName() + " : " + field.get(null));
				}
			} catch (Exception e) {
				Log.e(TAG, "Error while collect crash info", e);
			}
		}
	}

	/**
	 * 淇濆瓨閿欒淇℃伅鍒版枃浠朵腑
	 * 
	 * @param ex
	 * @return
	 */
	private String saveCrashInfoToFile(Throwable ex) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		// printStackTrace(PrintWriter s)
		// 灏嗘 throwable 鍙婂叾杩借釜杈撳嚭鍒版寚瀹氱殑 PrintWriter
		ex.printStackTrace(printWriter);

		// getCause() 杩斿洖姝?throwable 鐨?cause锛涘鏋?cause 涓嶅瓨鍦ㄦ垨鏈煡锛屽垯杩斿洖 null銆?
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}

		try {
			// toString() 浠ュ瓧绗︿覆鐨勫舰寮忚繑鍥炶缂撳啿鍖虹殑褰撳墠鍊笺?
			String result = info.toString();
			printWriter.close();
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd , HH:mm:ss,");
			Date curDate = new Date(System.currentTimeMillis());// 鑾峰彇褰撳墠鏃堕棿
			String timestamp = formatter.format(curDate);
			String data = "crash-" + timestamp + ":  " + result + "\r\n";
			return timestamp;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing report file...", e);
			return "";
		}

		// mDeviceCrashInfo.put(STACK_TRACE, result);
		//
		// try {
		// long timestamp = System.currentTimeMillis();
		// String fileName = "crash-" + String.valueOf(timestamp)
		// + CRASH_REPORTER_EXTENSION;
		// // 淇濆瓨鏂囦欢
		// FileOutputStream trace = mContext.openFileOutput(fileName,
		// Context.MODE_PRIVATE);
		// mDeviceCrashInfo.store(trace, "123");
		// trace.flush();
		// trace.close();
		// return fileName;
		// } catch (Exception e) {
		// Log.e(TAG, "an error occured while writing report file...", e);
		// }
		// return null;

	}

	/**
	 * 鎶婇敊璇姤鍛婂彂閫佺粰鏈嶅姟鍣?鍖呭惈鏂颁骇鐢熺殑鍜屼互鍓嶆病鍙戦?鐨?
	 * 
	 * @param ctx
	 */
	private void sendCrashReportsToServer(Context ctx) {
		String[] crFiles = getCrashReportFiles(ctx);
		if (crFiles != null && crFiles.length > 0) {
			TreeSet<String> sortedFiles = new TreeSet<String>();
			sortedFiles.addAll(Arrays.asList(crFiles));

			for (String fileName : sortedFiles) {
				File cr = new File(ctx.getFilesDir(), fileName);
				postReport(cr);
				cr.delete();// 鍒犻櫎宸插彂閫佺殑鎶ュ憡
			}
		}
	}

	/**
	 * 鑾峰彇閿欒鎶ュ憡鏂囦欢鍚?
	 * 
	 * @param ctx
	 * @return
	 */
	private String[] getCrashReportFiles(Context ctx) {
		File filesDir = ctx.getFilesDir();
		// 瀹炵幇FilenameFilter鎺ュ彛鐨勭被瀹炰緥鍙敤浜庤繃婊ゅ櫒鏂囦欢鍚?
		FilenameFilter filter = new FilenameFilter() {
			// accept(File dir, String name)
			// 娴嬭瘯鎸囧畾鏂囦欢鏄惁搴旇鍖呭惈鍦ㄦ煇涓?枃浠跺垪琛ㄤ腑銆?
			public boolean accept(File dir, String name) {
				return name.endsWith(CRASH_REPORTER_EXTENSION);
			}
		};
		// list(FilenameFilter filter)
		// 杩斿洖涓?釜瀛楃涓叉暟缁勶紝杩欎簺瀛楃涓叉寚瀹氭鎶借薄璺緞鍚嶈〃绀虹殑鐩綍涓弧瓒虫寚瀹氳繃婊ゅ櫒鐨勬枃浠跺拰鐩綍
		return filesDir.list(filter);
	}

	private void postReport(File file) {
		// TODO 浣跨敤HTTP Post 鍙戦?閿欒鎶ュ憡鍒版湇鍔″櫒
		// 杩欓噷涓嶅啀璇﹁堪,寮?彂鑰呭彲浠ユ牴鎹甇PhoneSDN涓婄殑鍏朵粬缃戠粶鎿嶄綔
		// 鏁欑▼鏉ユ彁浜ら敊璇姤鍛?
	}

	/**
	 * 鍦ㄧ▼搴忓惎鍔ㄦ椂鍊? 鍙互璋冪敤璇ュ嚱鏁版潵鍙戦?浠ュ墠娌℃湁鍙戦?鐨勬姤鍛?
	 */
	public void sendPreviousReportsToServer() {
		sendCrashReportsToServer(mContext);
	}

}
