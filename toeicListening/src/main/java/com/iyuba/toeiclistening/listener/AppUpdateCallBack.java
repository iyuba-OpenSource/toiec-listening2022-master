package com.iyuba.toeiclistening.listener;

public interface AppUpdateCallBack {
	/**
	 * 鎻愮ず鏈夋柊鐗堟湰鏇存柊
	 */
	public void appUpdateSave(String version_code,String newAppNetworkUrl);
	/**
	 * 妫?煡鏂扮増鏈け璐ユ垨娌℃湁鏂扮増鏈洿鏂?
	 */
	public void appUpdateFaild();
	/**
	 * 鎵ц鏂扮増鏈洿鏂板姩浣?
	 * @param newAppNetworkUrl 鏇存柊璺緞
	 */
	public void appUpdateBegin(String newAppNetworkUrl);
}
