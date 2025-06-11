/**
 * 
 */
package com.iyuba.core.common.protocol.friends;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.VOABaseJsonRequest;
import com.iyuba.core.common.util.MD5;

/**
 * @author yao 向服务器发送当前位置
 */
public class RequestSendLocation extends VOABaseJsonRequest {
	public static final String protocolCode = "70001";

	public RequestSendLocation(String uid, String x, String y) {
		super(protocolCode);
		// TODO Auto-generated constructor stub
		setRequestParameter("uid", uid);
		setRequestParameter("x", x);// 经度
		setRequestParameter("y", y);// 纬度
		setRequestParameter("sign",
				MD5.getMD5ofStr(protocolCode + uid + x + y + "iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseSendLocation();
	}

}
