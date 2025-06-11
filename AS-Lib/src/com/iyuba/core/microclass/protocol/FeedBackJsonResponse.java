package com.iyuba.core.microclass.protocol;

import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class FeedBackJsonResponse extends BaseJSONResponse {
	public String status;
	public String msg;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		return true;
	}

}
