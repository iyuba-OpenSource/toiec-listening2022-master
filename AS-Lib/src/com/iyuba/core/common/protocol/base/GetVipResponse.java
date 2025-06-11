package com.iyuba.core.common.protocol.base;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;

public class GetVipResponse extends BaseXMLResponse {

	public String result, msg, amount, VipFlg, VipEndTime;

	public GetVipResponse() {
		// TODO 自动生成的构造函数存根
	}

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO 自动生成的方法存根
		result = Utility.getSubTagContent(bodyElement, "result");
		msg = Utility.getSubTagContent(bodyElement, "msg");
		amount = Utility.getSubTagContent(bodyElement, "amount");
		VipFlg = Utility.getSubTagContent(bodyElement, "VipFlg");
		VipEndTime = Utility.getSubTagContent(bodyElement, "VipEndTime");
		return true;
	}

}
