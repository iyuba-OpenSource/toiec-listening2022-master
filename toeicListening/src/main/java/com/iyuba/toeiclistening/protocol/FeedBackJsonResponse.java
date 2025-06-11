package com.iyuba.toeiclistening.protocol;

import org.json.JSONObject;

import android.util.Log;

import com.iyuba.toeiclistening.frame.protocol.BaseJSONResponse;

public class FeedBackJsonResponse extends BaseJSONResponse {
	public String status;
	public String msg;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		Log.e("feedback.response", bodyElement);
		return true;
	}

}
