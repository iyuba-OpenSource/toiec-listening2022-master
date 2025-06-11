package com.iyuba.core.common.protocol.base;

import java.io.IOException;

import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;
import com.iyuba.core.common.util.MD5;

public class GetVipRequest extends BaseXMLRequest {

	public GetVipRequest(String userId, String appId, String productId) {
		setAbsoluteURI("http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/apiGetVip.jsp?" + "&userId="
				+ userId + "&appId=" + appId + "&productId=" + productId
				+ "&sign="
				+ MD5.getMD5ofStr(appId + userId + productId + "iyuba")
				+ "&format=xml");
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO 自动生成的方法存根

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO 自动生成的方法存根
		return new GetVipResponse();
	}

}
