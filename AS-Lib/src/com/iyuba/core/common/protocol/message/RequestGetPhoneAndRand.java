package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.util.Constant;

public class RequestGetPhoneAndRand extends BaseJSONRequest {

	public RequestGetPhoneAndRand() {
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/getPhoneAndRand.jsp?format=json");
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO 自动生成的方法存根

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO 自动生成的方法存根
		return null;
	}

}
