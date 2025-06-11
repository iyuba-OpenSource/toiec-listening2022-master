/**
 * 
 */
package com.iyuba.core.common.protocol.friends;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.util.Constant;

/**
 * @author yao
 * 
 */
public class RequestSameAppFriendsList extends BaseJSONRequest {
	public static final String protocolCode = "90003";

	public RequestSameAppFriendsList(String uid, int pageNumber) {
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol="
				+ protocolCode + "&uid=" + uid + "&pagesize=20"
				+ "&pagenum=" + pageNumber);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseSameAppFriendsList();
	}

}
