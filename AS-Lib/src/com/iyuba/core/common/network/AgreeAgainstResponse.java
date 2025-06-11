package com.iyuba.core.common.network;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class AgreeAgainstResponse extends BaseJSONResponse {
	
	public String result;
	public String message;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			result= jsonObjectRoot.getString("ResultCode");
			message= jsonObjectRoot.getString("Message");
			if (result.equals("001")) {
				
			}else if (result.equals("000")) {
				
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		return true;
	}

}
