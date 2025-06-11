package com.iyuba.core.me.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONResponse;

public class UserEngLevelResponse extends BaseJSONResponse {
	 public int result;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
		    result= jsonObjectRoot.getInt("result");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return true;
	}

}
