package com.iyuba.toeiclistening.protocol;

import com.iyuba.toeiclistening.frame.protocol.BaseXMLResponse;
import com.iyuba.toeiclistening.frame.util.Utility;
import com.iyuba.toeiclistening.frame.util.kXMLElement;


public class ResponseCheckMessageCode extends BaseXMLResponse{

	public String result;
	public int checkResultCode;
	public String message;
	
	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		result=Utility.getSubTagContent(bodyElement, "result");
		checkResultCode=Integer.parseInt(Utility.getSubTagContent(bodyElement, "checkResultCode"));
		message=Utility.getSubTagContent(bodyElement, "message");		
		// TODO Auto-generated method stub
		return true;
	}
}
