/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.VOABaseJsonRequest;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.common.util.TextAttr;

/**
 * @author ct 显示用户的基本资料信息 protocolCode 20001
 */

public class RequestUpdateState extends VOABaseJsonRequest {
	public static final String protocolCode = "30006";
	public String md5Status = "1"; // 0=未加密,1=加密

	public RequestUpdateState(String userId, String userName, String message) {
		super(protocolCode);
		// TODO Auto-generated constructor stub;
		setRequestParameter("uid", userId);
		setRequestParameter("username", userName);
		setRequestParameter("from", "android");
		setRequestParameter("message", TextAttr.encode(message));
		setRequestParameter(
				"sign",
				MD5.getMD5ofStr("30006" + userId + userName + message
						+ "iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseUpdateState();
	}

}
