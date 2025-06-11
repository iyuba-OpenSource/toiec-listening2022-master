package com.iyuba.toeiclistening.listener;

public interface OnPlayStateChangedListener {
	public void playSuccess();
	public void setPlayTime(String currTime,String allTime);
	public void playFaild();
	public void playCompletion();
	public void playPause();
	public void playResume();
	public void playStop();
}
