package com.iyuba.core.common.manager;

import java.util.ArrayList;

import android.content.Context;

import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.common.sqlite.mode.DownloadFile;

public class DownloadManager {
	Context mContext;
	public static DownloadManager downloadManager;

	public DownloadManager() {
	}

	public static synchronized DownloadManager Instance() {
		if (downloadManager == null) {
			downloadManager = new DownloadManager();
			downloadManager.mContext = RuntimeManager.getContext();
		}
		return downloadManager;
	}

	public ArrayList<DownloadFile> fileList = new ArrayList<DownloadFile>();
}
