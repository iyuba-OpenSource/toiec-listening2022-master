/**
 * 
 */
package com.iyuba.toeiclistening.util;

import android.util.Log;

/**
 * 鐢ㄤ簬鍒ゆ柇涓?覆鏁板瓧鏄惁鏄墜鏈哄彿
 * 
 * @author Administrator
 * 
 */
public class TelNumMatch {

	/*
	 * 绉诲姩: 2G鍙锋(GSM缃戠粶)鏈?39,138,137,136,135,134,159,158,152,151,150,
	 * 3G鍙锋(TD-SCDMA缃戠粶)鏈?57,182,183,188,187 147鏄Щ鍔═D涓婄綉鍗′笓鐢ㄥ彿娈? 鑱旈?:
	 * 2G鍙锋(GSM缃戠粶)鏈?30,131,132,155,156 3G鍙锋(WCDMA缃戠粶)鏈?86,185 鐢典俊:
	 * 2G鍙锋(CDMA缃戠粶)鏈?33,153 3G鍙锋(CDMA缃戠粶)鏈?89,180
	 */
	static String YD = "^[1]{1}(([3]{1}[4-9]{1})|([5]{1}[012789]{1})|([8]{1}[2378]{1})|([4]{1}[7]{1}))[0-9]{8}$";
	static String LT = "^[1]{1}(([3]{1}[0-2]{1})|([5]{1}[56]{1})|([8]{1}[56]{1}))[0-9]{8}$";
	static String DX = "^[1]{1}(([3]{1}[3]{1})|([5]{1}[3]{1})|([8]{1}[09]{1}))[0-9]{8}$";

	String mobPhnNum;

	public TelNumMatch(String mobPhnNum) {
		this.mobPhnNum = mobPhnNum;
		Log.d("tool", mobPhnNum);
	}

	public int matchNum() {
		/**
		 * flag = 1 YD 2 LT 3 DX 
		 */
		int flag;// 瀛樺偍鍖归厤缁撴灉
		// 鍒ゆ柇鎵嬫満鍙风爜鏄惁鏄?1浣?
		if (mobPhnNum.length() == 11) {
			// 鍒ゆ柇鎵嬫満鍙风爜鏄惁绗﹀悎涓浗绉诲姩鐨勫彿鐮佽鍒?
			if (mobPhnNum.matches(YD)) {
				flag = 1;
			}
			// 鍒ゆ柇鎵嬫満鍙风爜鏄惁绗﹀悎涓浗鑱旈?鐨勫彿鐮佽鍒?
			else if (mobPhnNum.matches(LT)) {
				flag = 2;
			}
			// 鍒ゆ柇鎵嬫満鍙风爜鏄惁绗﹀悎涓浗鐢典俊鐨勫彿鐮佽鍒?
			else if (mobPhnNum.matches(DX)) {
				flag = 3;
			}
			// 閮戒笉鍚堥? 鏈煡
			else {
				flag = 4;
			}
		}
		// 涓嶆槸11浣?
		else {
			flag = 5;
		}
		Log.d("TelNumMatch", "flag"+flag);
		//杩斿洖1,2,3 鍧囪瀹氫负鎵嬫満鍙凤紝鍏朵粬鍒欒涓轰笉鏄?
		return flag;
	}
}