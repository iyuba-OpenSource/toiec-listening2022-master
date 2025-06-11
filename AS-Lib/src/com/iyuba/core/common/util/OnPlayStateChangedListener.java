package com.iyuba.core.common.util;

public interface OnPlayStateChangedListener {
	public void playSuccess();
	public void setPlayTime(String currTime,String allTime);
	public void playFaild();
	public void playCompletion();
}
