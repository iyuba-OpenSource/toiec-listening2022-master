package com.iyuba.toeiclistening.protocol;

import com.iyuba.toeiclistening.frame.protocol.BaseXMLResponse;
import com.iyuba.toeiclistening.frame.util.Utility;
import com.iyuba.toeiclistening.frame.util.kXMLElement;

public class GetVipInfoResponse extends BaseXMLResponse{

	public String result;
	public String msg;
	public String vipFlg;
	public String amount;
	public String vipEndTime;
	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO Auto-generated method stub
		result=Utility.getSubTagContent(bodyElement, "result");
		msg=Utility.getSubTagContent(bodyElement, "msg");
		amount=Utility.getSubTagContent(bodyElement, "amount");
		vipFlg=Utility.getSubTagContent(bodyElement, "VipFlg");
		vipEndTime=Utility.getSubTagContent(bodyElement, "VipEndTime");
		return true;
	}
}
