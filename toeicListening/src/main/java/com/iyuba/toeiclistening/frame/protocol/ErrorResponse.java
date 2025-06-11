package com.iyuba.toeiclistening.frame.protocol;

/**
 * 閿欒鍥炲鍖咃紝鎵?湁涓庢湇鍔″櫒浜や簰鍚庣殑閿欒鍧囧湪姝ゆ眹鎬伙紝 鍖呮嫭缃戠粶閿欒锛屾湇鍔″櫒閿欒锛屽崗璁敊璇互鍙婃湭鐭ラ敊璇瓑
 * 
 * @author zhouyin
 * 
 */
public class ErrorResponse implements BaseResponse {
	// public final static int ERROR_PARAM_INVALID = 0; // 璇锋眰鍦板潃鍙傛暟鏍煎紡閿欒
	// public final static int ERROR_NET_NO_CONNECTION = 1;// 鎵嬫満褰撳墠娌℃湁鍙敤杩炴帴
	// public final static int ERROR_NET_DISCONNECTED = 2; // 鏃犳硶杩炴帴鏈嶅姟鍣ㄦ垨鏂紑
	// public final static int ERROR_NET_TIMEOUT = 3; // 缃戠粶杩炴帴瓒呮椂
	// // public final static int ERROR_NULL_RESULT = 4; // 娌℃湁鑾峰彇鍒颁换浣曠粨鏋?
	// public final static int ERROR_SERVER = 5; // 鏈嶅姟鍣ㄥ唴閮ㄩ敊璇?
	// public final static int ERROR_PROTOTOL = 6; // 鍗忚瑙ｆ瀽閿欒
	// public final static int ERROR_THREAD = 7; // 宸ヤ綔绾跨▼閿欒
	// public final static int ERROR_UNKOWN = 8; // 鏈煡閿欒
	// public final static int ERROR_INVALID_RESULT = 9; // 鏈嶅姟鍣ㄦ棤娉曡幏鍙栨湁鏁堢粨鏋?
	// public final static int ERROR_CLIENT_NET_DISCONNECTED = 10;// 鏈湴缃戠粶涓嶅彲鐢?
	// //////////////////////////////////////////////////////////////////////////////
	public final static int ERROR_UNKNOWN = 900;// 鍏朵粬鏈煡閿欒

	public final static int ERROR_NET_CONNECTION = 911;// 杩炴帴寮傚父
	public final static int ERROR_ClLIENT_PROTOCOL = 912;// Client鍗忚寮傚父
	public final static int ERROR_NET_TIMEOUT = 913;// 缃戠粶杩炴帴瓒呮椂
	public final static int ERROR_NET_SOCKET = 914;// Socket寮傚父
	public final static int ERROR_NET_IO = 915;// IO寮傚父

	public final static int ERROR_PROTOCOL = 921;// 鍗忚瑙ｆ瀽寮傚父
	public final static int ERROR_INVALID_RESULT = 922;// 鏈嶅姟姝ｅ父锛屼絾杩斿洖缁撴灉涓嶅彲鐢?
	public final static int ERROR_SERVER = 923;// 搴旂瓟鍑虹幇閿欒
	public final static int ERROR_PARAM_INVALID = 924; // 璇锋眰鍦板潃鍙傛暟鏍煎紡閿欒

	public final static int ERROR_THREAD = 931;// 宸ヤ綔绾跨▼閿欒
	public final static int ERROR_INTERRUPT = 932;// 绾跨▼涓柇閿欒

	public final static int ERROR_SECURITY_UNKNOWN = 300;// 鏈煡鐨勫畨鍏ㄨ璇侀敊璇?
	public final static int ERROR_USERID = 301;// 鐢ㄦ埛鍚嶄笉瀛樺湪
	public final static int ERROR_PASSWORD = 302;// 瀵嗙爜閿欒
	public final static int ERROR_AUTHORITY = 303;// 鏉冮檺閿欒
	public final static int ERROR_ACCOUNT = 304;// Session鏃犳晥
	public final static int ERROR_SESSIONINVALID = 305;// 浼氳瘽澶辨晥,瀹㈡埛绔渶閲嶆柊璁よ瘉
	
	
//	缇ょ粍涓嶅瓨鍦?427 瀵嗙爜閿欒-302 宸茬粡鏄兢缁勬垚鍛?429 缇ょ粍宸叉弧-440 鏃犳潈闄?303
	
	

	public final int getErrorType() {
		return errorType;
	}

	public final String getErrorDesc() {

		return errorDesc;
	}

	public final void setError(int type, String desc) {
		errorType = type;
		errorDesc = desc;
	}

	public final void setError(int type) {
		errorType = type;
	}

	public ErrorResponse() {
	}

	public ErrorResponse(int type, String desc) {
		setError(type, desc);
	}

	public ErrorResponse(int type) {
		setError(type);
	}

	private int errorType = ERROR_UNKNOWN;
	private String errorDesc = "鎶辨瓑锛屾湭鐭ラ敊璇紒";
}
