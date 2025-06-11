package com.iyuba.core.common.protocol.base;

import java.io.IOException;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;
import com.iyuba.core.common.util.MD5;

/**
 * 付钱
 * 
 * @author chentong
 * 
 */
public class PayRequest extends BaseXMLRequest {
	private String appId;// 英语四级是205 英语六级是206
	private String sign;

	public PayRequest(String userId, int amount) {
		this.appId = Constant.APPID;
		this.sign = MD5.getMD5ofStr(amount + appId + userId + "00" + "iyuba");
		setAbsoluteURI("http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/apiPayByDate.jsp?userId="
				+ userId + "&amount=" + amount + "&month=0&appId=" + appId
				+ "&productId=0&sign=" + sign);
	}

	public PayRequest(String userId, int amount, int month) {
		this.appId = Constant.APPID;
		this.sign = MD5.getMD5ofStr(amount + appId + userId + month + "iyuba");
		setAbsoluteURI("http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/payVipApi.jsp?userId="
				+ userId + "&amount=" + amount + "&appId=" + appId
				+ "&productId=" + month + "&sign=" + sign);
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new PayResponse();
	}

}
