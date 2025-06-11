package com.iyuba.core.teacher.protocol;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class AgreeAgainstResponse extends BaseJSONResponse {

	public String result;
	public String message;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO 自动生成的方法存根
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			result = jsonObjectRoot.getString("result");
			message = jsonObjectRoot.getString("message");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return true;
	}

}
