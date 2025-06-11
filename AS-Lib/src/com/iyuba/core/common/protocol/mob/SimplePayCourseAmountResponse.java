package com.iyuba.core.common.protocol.mob;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;

public class SimplePayCourseAmountResponse extends BaseXMLResponse {

	public String result;
	public String amount;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO Auto-generated method stub
		result = Utility.getSubTagContent(bodyElement, "result");
		amount = Utility.getSubTagContent(bodyElement, "amount");
		return true;
	}

}
