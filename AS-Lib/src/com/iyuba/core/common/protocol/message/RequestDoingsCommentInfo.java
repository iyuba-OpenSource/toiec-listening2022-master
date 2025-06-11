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
 * @author yao
 * 
 */
public class RequestDoingsCommentInfo extends VOABaseJsonRequest {
	public static final String protocolCode = "30002";
	public String md5Status = "1"; // 0=未加密,1=加密
	public static final String pageCounts = "100";

	public RequestDoingsCommentInfo(String id, String pageNumber) {
		super(protocolCode);
		// TODO Auto-generated constructor stub
		setRequestParameter("doing", id);
		setRequestParameter("sign",
				MD5.getMD5ofStr(protocolCode + id + "iyubaV2"));
		setRequestParameter("pageNumber", pageNumber);
		setRequestParameter("pageCounts", pageCounts);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseDoingsCommentInfo();
	}

}
