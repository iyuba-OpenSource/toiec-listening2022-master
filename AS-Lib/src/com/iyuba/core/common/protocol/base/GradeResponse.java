package com.iyuba.core.common.protocol.base;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class GradeResponse extends BaseJSONResponse {
	public String totalTime;
	public String positionByTime;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		JSONObject jsonObjectRoot;
		try {
			jsonObjectRoot = new JSONObject(bodyElement);
			totalTime = jsonObjectRoot.getString("totalTime");
			positionByTime = jsonObjectRoot.getString("positionByTime");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
