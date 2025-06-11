package com.iyuba.core.teacher.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class ResponsePublishMood extends BaseJSONResponse {

	public String result;
	public String message;
	public String jiFen;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO 自动生成的方法存根
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			result = jsonObjectRoot.getString("result");
			message = jsonObjectRoot.getString("message");
			try {
				jiFen = jsonObjectRoot.getString("jiFen");
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return true;
	}

}
