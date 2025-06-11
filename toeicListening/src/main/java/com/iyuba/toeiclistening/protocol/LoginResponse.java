package com.iyuba.toeiclistening.protocol;

import com.iyuba.toeiclistening.frame.protocol.BaseXMLResponse;
import com.iyuba.toeiclistening.frame.util.Utility;
import com.iyuba.toeiclistening.frame.util.kXMLElement;


public class LoginResponse extends BaseXMLResponse {
	public String result, uid, username, imgsrc, vip, validity,amount,email;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO Auto-generated method stub
		result = Utility.getSubTagContent(bodyElement, "result");
		uid = Utility.getSubTagContent(bodyElement, "uid");
		username = Utility.getSubTagContent(bodyElement, "username");
		imgsrc= Utility.getSubTagContent(bodyElement, "imgSrc");
		vip = Utility.getSubTagContent(bodyElement, "vipStatus");
		validity = Utility.getSubTagContent(bodyElement, "expireTime");
		amount=Utility.getSubTagContent(bodyElement, "Amount");
		email=Utility.getSubTagContent(bodyElement, "email");
		return true;
	}

}
