/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.util.Constant;

/**
 * @author 加关注
 */
public class RequestAddAttention extends BaseJSONRequest {
	public static final String protocolCode = "50001";

	public RequestAddAttention(String uid, String followid) {
		// super(protocolCode);
		// TODO Auto-generated constructor stub
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol="
				+ protocolCode + "&uid=" + uid + "&followid=" + followid
				+ "&sign="
				+ MD5.getMD5ofStr(protocolCode + uid + followid + "iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		// return null;
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseAddAttention();
	}

}
