package com.iyuba.toeiclistening.setting;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.toeiclistening.util.Constant;

public class SettingConfig {

	private volatile static SettingConfig instance;

	private boolean screenLit;
	private boolean syncho;
	private boolean autoLogin;
	private boolean slidechange;
	private boolean backgroundPlay;
	


	private SettingConfig() {

	}

	public static SettingConfig Instance() {

		if (instance == null) {
			synchronized (SettingConfig.class) {
				if (instance == null) {
					instance = new SettingConfig();
				}
			}

		}
		return instance;
	}

	/**
	 * 获取是否播放时屏幕常量
	 * 
	 * @return
	 */
	public boolean isScreenLit() {
		try {
			screenLit = ConfigManager.Instance().loadBoolean(Constant.KEEP_SCREEN_LIT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			screenLit =true;
		}
		return screenLit;
	}

	/**
	 * 设置屏幕常亮
	 * 
	 * @param automaticDownload
	 */
	public void setScreenLit(boolean screenLit) {
		ConfigManager.Instance().putBoolean(Constant.KEEP_SCREEN_LIT, screenLit);
	}
	/**
	 * 是否允许左右滑屏切换题目
	 * 
	 */
	public boolean isSlideChangeQuestion(){
		try {
			slidechange = ConfigManager.Instance().loadBoolean(Constant.SLIDE_CHANGE_QUESTION);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			slidechange =true;
		}
		return slidechange;
	}
	/**
	 * 设置左右滑屏切换题目
	 * 
	 */
	public void setSlideChange(boolean sildeChange){
		ConfigManager.Instance().putBoolean(Constant.SLIDE_CHANGE_QUESTION, sildeChange);
	}
	/**
	 * 设置后台播放
	 * 
	 */
	public void setBackgroundPlay(boolean backgroundPlay){
		ConfigManager.Instance().putBoolean(Constant.BACKGROUND_PLAY, backgroundPlay);
	}
	/**
	 * 是否允许后台播放
	 * 
	 */
	public boolean isBackgroundPlay(){
		try {
			backgroundPlay = ConfigManager.Instance().loadBoolean(Constant.BACKGROUND_PLAY);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			backgroundPlay =true;
		}
		return backgroundPlay;
	}
	
	
	
	/**
	 * 获取是否播放时同步
	 * 
	 * @return
	 */
	public boolean isSyncho() {
		try {
			syncho = ConfigManager.Instance().loadBoolean(Constant.TEXT_SYNS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			syncho = true;
		}
		return syncho;
	}

	/**
	 * 设置播放时同步
	 * 
	 * @param automaticDownload
	 */
	public void setSyncho(boolean syncho) {
		ConfigManager.Instance().putBoolean(Constant.TEXT_SYNS, syncho);
	}


	/**
	 * 获取是否自动登录
	 * 
	 * @return
	 */
	public boolean isAutoLogin() {
		try {
			autoLogin = ConfigManager.Instance().loadBoolean(Constant.AUTOLOGIN);
		} catch (Exception e) {
			e.printStackTrace();
			autoLogin = true;
		}
		return autoLogin;
	}

	/**
	 * 设置是否自动登录
	 * 
	 * @param automaticDownload
	 */
	public void setAutoLogin(boolean AutoLogin) {
		ConfigManager.Instance().putBoolean(Constant.AUTOLOGIN, true);//AutoLogin
	}
	/**
	 * 
	 * 设置是否是第一次登陆
	 * 
	 */
	public void setFirstLoad(int flag){
		ConfigManager.Instance().putInt(Constant.FIRST_LOAD, flag);
	}
	/**
	 * 获取是否是第一次登陆
	 * flag=0 首次登陆 flag=1 非首次登陆
	 */
	public int isFirstLoad(){
		int flag=0;
		try {
			flag=ConfigManager.Instance().loadInt(Constant.FIRST_LOAD);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag=0;
		}
		return flag;
	}
	
	/**
	 * 
	 * 设置vip
	 * @param flag=0 非vip flag=1 vip
	 */
	public void setVip(int flag){
		ConfigManager.Instance().putInt(AccountManagerLib.ISVIP, flag);
	}
	/**
	 * 获取是否是vip
	 * flag=0 首次登陆 flag=1 非首次登陆
	 */
	public int isVip(){
		int flag=0;
		try {
			flag=ConfigManager.Instance().loadInt(AccountManagerLib.ISVIP);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag=0;
		}
		return flag;
	}
	
	/**
	 * 
	 * 设置爱语吧 爱语币
	 * 
	 */
	public void setIyubaAmount(String amount){
		ConfigManager.Instance().putString(AccountManagerLib.IYUBAAMOUNT, amount);
	}
	/**
	 * 获取爱语币数量
	 *
	 */
	public String getIyubaAmount(){
		String flag="0";
		try {
			flag=ConfigManager.Instance().loadString(AccountManagerLib.IYUBAAMOUNT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag="0";
		}
		return flag;
	}
	
	
}
