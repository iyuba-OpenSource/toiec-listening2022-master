package com.iyuba.core.microclass.protocol;



import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;

/**
 * 
 * @author zqq
 * 
 *         功能：支付回�?�?result 0是失败，1 是成�?�?msg 为此次操作信息�? �?amount
 *         若购买成功，返回此用户当前（购买之后）的amount�?
 */
public class PayResponse extends BaseXMLResponse {
	public String result;
	public String msg;
	public String amount;
	public String vipFlag;
	public String vipEndTime;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		result = Utility.getSubTagContent(bodyElement, "result");
		msg = Utility.getSubTagContent(bodyElement, "msg");
		amount = Utility.getSubTagContent(bodyElement, "amount");
		vipFlag = Utility.getSubTagContent(bodyElement, "VipFlag");
		vipEndTime = Utility
				.getSubTagContent(bodyElement, "VipEndTime");
		
		return true;
	}

}
