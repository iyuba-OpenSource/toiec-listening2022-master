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
 *
 *
 */
public class RequestAttentionList extends VOABaseJsonRequest {
	public static final String protocolCode = "51001";
	public String md5Status = "1"; // 0=未加密,1=加密

	public RequestAttentionList(String uid, String page) {
		super(protocolCode);
		setRequestParameter("uid", uid);
		setRequestParameter("pageNumber", page);
		setRequestParameter("pageCounts", "20");
		setRequestParameter("sign",
				MD5.getMD5ofStr(protocolCode + uid + "iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseAttentionList();
	}
}
