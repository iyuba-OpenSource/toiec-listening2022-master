/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.VOABaseJsonRequest;
import com.iyuba.core.common.util.MD5;

/**
 * @author ct 显示用户的基本资料信息 protocolCode 20001
 */

public class RequestBasicUserInfo extends VOABaseJsonRequest {
	public static final String protocolCode = "20001";
	public String md5Status = "1"; // 0=未加密,1=加密

	public RequestBasicUserInfo(String userId, String myid) {
		super(protocolCode);
		// TODO Auto-generated constructor stub;
		setRequestParameter("id", userId);
		setRequestParameter("myid", myid);
		setRequestParameter("sign",
				MD5.getMD5ofStr(protocolCode + userId + "iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseBasicUserInfo();
	}

}
