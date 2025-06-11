package com.iyuba.core.discover.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

/**
 * 获取评论的Response
 * 
 */

public class SendCommentResponse extends BaseJSONResponse {
	public String result;
	public String message;
	
	public boolean isSendSuccess=false;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		JSONObject jsonBody = null;
		try {
			jsonBody = new JSONObject(bodyElement);
		} catch (JSONException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		try {
			result = jsonBody.getString("result");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			message = jsonBody.getString("message");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result.equals("341")) {
			isSendSuccess=true;
		}else{
			isSendSuccess=false;
		}
		return true;
	}

	

}
