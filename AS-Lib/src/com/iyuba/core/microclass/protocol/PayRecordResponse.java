package com.iyuba.core.microclass.protocol;

import android.util.Log;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;


/**
 * 
 * @author zqq
 * 
 *         鍔熻兘锛氫粯娆捐褰曟煡璇㈢粨鏋?鑻ュ瓨鍦紝result涓?锛宮sg涓?褰撴椂璐拱鏃堕棿 鑻ヤ笉瀛樺湪锛宺esult涓? msg涓?don't exit
 */
public class PayRecordResponse extends BaseXMLResponse {
	public String result;
	public String msg;
	public static String time;
	public static String amount;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		result = Utility.getSubTagContent(bodyElement, "result");
		msg = Utility.getSubTagContent(bodyElement, "msg");
		time = Utility.getSubTagContent(bodyElement, "time");
		amount = Utility.getSubTagContent(bodyElement, "amount");
		Log.e("return ",result + " " + msg + " " + time + " " + amount);
		return true;
	}

}

