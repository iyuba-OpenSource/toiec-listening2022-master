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
 * @author yao 查看用户状态-doings
 */
public class RequestNewDoingsInfo extends VOABaseJsonRequest {
	public static final String protocolCode = "31001";
	public String md5Status = "1"; // 0=未加密,1=加密
	public static final String pageCounts = "10";

	public RequestNewDoingsInfo(String userId, int pageNumber) {
		super(protocolCode);
		setRequestParameter("uid", userId);
		setRequestParameter("sign",
				MD5.getMD5ofStr(protocolCode + userId + "iyubaV2"));
		setRequestParameter("pageNumber", String.valueOf(pageNumber));
		setRequestParameter("pageCounts", pageCounts);
	}
	
	public RequestNewDoingsInfo(String userId, String reqType,int pageNumber) {
		super(protocolCode);
		// TODO Auto-generated constructor stub
		setRequestParameter("uid", userId);
		setRequestParameter("feeds", reqType);
		setRequestParameter("sign",
				MD5.getMD5ofStr(protocolCode + userId + "iyubaV2"));
		setRequestParameter("pageNumber", String.valueOf(pageNumber));
		setRequestParameter("pageCounts", pageCounts);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseNewDoingsInfo();
	}

}
