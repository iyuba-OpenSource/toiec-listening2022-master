package com.iyuba.core.common.protocol.base;

import android.util.Log;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;

public class RegistResponse extends BaseXMLResponse {
	public String result;
	public String message;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		result = Utility.getSubTagContent(bodyElement, "result");
		message = Utility.getSubTagContent(bodyElement, "message");
		
		Log.d("RegistResponse:", result);
		
		return true;
	}
}
