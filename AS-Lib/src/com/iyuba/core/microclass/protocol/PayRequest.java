package com.iyuba.core.microclass.protocol;

import java.io.IOException;

import android.util.Log;

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
	private String appId;// 
	private String sign;

	public PayRequest(String userId, String amount) {
		this.appId = Constant.APPID;
		this.sign = MD5.getMD5ofStr(amount + appId + userId + "00iyuba");
		String string="http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/apiPayByDate.jsp?userId="
			+ userId + "&amount=" + amount + "&appId=" + appId
			+ "&productId=0&month=0&sign=" + sign;
		setAbsoluteURI(string);
		Log.e("payrequest", string);
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

