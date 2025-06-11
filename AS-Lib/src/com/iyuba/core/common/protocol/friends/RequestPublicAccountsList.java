/**
 * 
 */
package com.iyuba.core.common.protocol.friends;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.VOABaseJsonRequest;

/**
 * @author yao
 * 
 */
public class RequestPublicAccountsList extends VOABaseJsonRequest {
	public static final String protocolCode = "10008";

	public RequestPublicAccountsList(String uid, int pageNumber) {
		super(protocolCode);
		// TODO Auto-generated constructor stub
		setRequestParameter("uid", uid);
		setRequestParameter("pageCounts", "50");
		setRequestParameter("pagenum", String.valueOf(pageNumber));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponsePublicAccountsList();
	}

}
