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
public class RequestDoingById extends VOABaseJsonRequest {
	public static final String protocolCode = "39001";

	public RequestDoingById(String doid) {
		super(protocolCode);
		// TODO Auto-generated constructor stub
		setRequestParameter("doid", doid);
		setRequestParameter("sign",
				MD5.getMD5ofStr(protocolCode + doid + "iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseDoingById();
	}

}
