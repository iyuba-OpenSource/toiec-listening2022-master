package com.iyuba.toeiclistening.protocol;

import com.iyuba.toeiclistening.frame.protocol.BaseXMLResponse;
import com.iyuba.toeiclistening.frame.util.Utility;
import com.iyuba.toeiclistening.frame.util.kXMLElement;



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
