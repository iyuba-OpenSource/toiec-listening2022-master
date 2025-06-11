package com.iyuba.core.common.protocol.mob;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

public class SimpleCoursePackRequest extends BaseJSONRequest {
	
	public SimpleCoursePackRequest(String url) {
		setAbsoluteURI(url);
	}
	
	
	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new SimpleCoursePackResponse();
	}

}
