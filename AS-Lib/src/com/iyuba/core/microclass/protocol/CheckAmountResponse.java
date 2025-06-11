package com.iyuba.core.microclass.protocol;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;



public class CheckAmountResponse extends BaseXMLResponse {
	public String result;
	public String msg;
	public String amount;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		result = Utility.getSubTagContent(bodyElement, "result");
		msg = Utility.getSubTagContent(bodyElement, "msg");
		amount = Utility.getSubTagContent(bodyElement, "amount");
		return true;
	}

}

