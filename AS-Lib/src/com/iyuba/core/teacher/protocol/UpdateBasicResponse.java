package com.iyuba.core.teacher.protocol;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.teacher.sqlite.mode.AnswerInfo;
import com.iyuba.core.teacher.sqlite.mode.Chat;

public class UpdateBasicResponse  extends BaseJSONResponse {

	public String result;
	public String message;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		JSONObject jsonBody = null;
		try {
			jsonBody = new JSONObject(bodyElement);
			result = jsonBody.getString("result");
			message = jsonBody.getString("message");
			if (result.equals("1")) {
				 
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return true;
	}

}
