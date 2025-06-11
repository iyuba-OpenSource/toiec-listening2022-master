package com.iyuba.toeiclistening.listener;

import java.util.ArrayList;

import com.iyuba.toeiclistening.entity.DownTest;

/**
 * 
 * @author zqq
 * 
 *         功能：监听下载状态接口
 * 
 */
public interface OnDownloadStateListener {
	/**
	 * 
	 * 
	 * 功能：准备下载
	 */
	public ArrayList<DownTest> onPreparedListener();// 准备下载数据

	/**
	 * 
	 * 
	 * 功能：开始下载，得到下载进度
	 */
	public void onStartListener(int progress);// 开始下载

	/*
	 * 
	 * 
	 * 功能：暂停下载
	 */
	public void onPausedListener(int progress);// 暂停下载

	/**
	 * 
	 * 
	 * 功能：完成下载
	 */
	public void onFinishedListener(int progress);// 完成

	/**
	 * 
	 * 
	 * 功能：下载出错，网络异常，异常中断。。。给出异常信息在主线程显示
	 */
	public void onErrorListener(String errorDesc, int progress);// 下载出错，网络异常，异常中断。。。

	/**
	 * 
	 * @return 功能：手动暂停
	 */
	public boolean isPausedListener();// 暂停
}
