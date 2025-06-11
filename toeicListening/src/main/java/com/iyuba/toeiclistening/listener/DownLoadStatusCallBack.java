package com.iyuba.toeiclistening.listener;

public interface DownLoadStatusCallBack {
	public void downLoadSuccess(String localFilPath);
	public void downLoadFaild(String errorInfo);
	
}
