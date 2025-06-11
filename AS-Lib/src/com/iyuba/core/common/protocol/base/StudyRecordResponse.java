package com.iyuba.core.common.protocol.base;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;

public class StudyRecordResponse extends BaseXMLResponse {
	public String result;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO Auto-generated method stub
		result = Utility.getSubTagContent(bodyElement, "result");
		return true;
	}

}
