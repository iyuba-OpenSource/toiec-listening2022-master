package com.iyuba.toeiclistening.frame.protocol;

import java.io.IOException;
import java.io.InputStream;

import com.iyuba.toeiclistening.frame.network.INetStateReceiver;

/**
 * http鍥炲鍖呮帴鍙?
 * 
 * @author zhouyin
 * 
 */
public interface BaseHttpResponse extends BaseResponse {

	/**
	 * 浠庢湇鍔″櫒绔幏鍙栫殑http杈撳叆娴佽В鏋愭帴鍙?
	 * 
	 * @param rsqCookie
	 *            : 瀵瑰簲璇锋眰鏍囪瘑Cookie
	 * @param request
	 *            锛氳鍥炲瀵瑰簲鐨勮姹?
	 * @param inputStream
	 *            锛氬洖澶嶈緭鍏ユ祦
	 * @param len
	 *            : 杈撳叆娴侀暱搴︼紝涓?1鏃惰〃绀烘棤娉曡幏鍙栬緭鍏ユ祦闀垮害
	 * @param stateRecevier
	 *            : 鐘舵?鎺ユ敹鍣?
	 * @return: 濡傛灉瑙ｆ瀽鎴愬姛杩斿洖null,鍚﹀垯杩斿洖鐩稿簲閿欒鍥炲鍖?
	 * @throws IOException
	 */
	public ErrorResponse parseInputStream(int rspCookie,
			BaseHttpRequest request, InputStream inputStream, int len,
			INetStateReceiver stateReceiver) throws IOException;

	/**
	 * 鏄惁鍏佽鍏抽棴杈撳叆娴佹帴鍙?姝ゆ帴鍙ｄ负鎬ц兘闇?眰鑰岃锛岃?铏戝埌鏌愪簺搴旂敤鍙兘鍦ㄦ渶涓婂眰澶勭悊鍘熷杈撳叆娴佹洿鏈夋晥鐜?
	 * 
	 * @return
	 */
	public boolean isAllowCloseInputStream();

	/**
	 * 鑾峰緱杈撳叆娴?
	 * 
	 * @return
	 */
	public InputStream getInputStream();
}
