package com.iyuba.toeiclistening.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class FailOpera {
	private static FailOpera instance;
	private static Context mContext;

	private FailOpera() {
	}

	public static synchronized FailOpera Instace(Context context) {
		mContext = context;
		if (instance == null) {
			instance = new FailOpera();
		}
		return instance;
	}

	public void openFile(String filePath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		// 鎴栬?
		intent.setDataAndType(Uri.fromFile(new File(filePath)),
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
}
