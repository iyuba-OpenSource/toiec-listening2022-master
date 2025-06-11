package com.iyuba.multithread;

public interface DownloadProgressListener {
	 public void onDownloadStart(FileDownloader fileDownloader,int id,int fileTotalSize);
	 public void onDownloadStoped(int id);
	 public void onProgressUpdate(int id,String downloadurl,int fileDownloadSize);
	 public void onDownloadComplete(int id,String savePathFullName);
	 public void onDownloadError(String errorMessage);
}
