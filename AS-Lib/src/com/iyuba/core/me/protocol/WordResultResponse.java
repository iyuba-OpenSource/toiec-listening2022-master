package com.iyuba.core.me.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class WordResultResponse extends BaseJSONResponse{
	public String result;
	public String wordSum_0;
	public String wordSum_1;
	public String wordSum_2;
	public String wordSum_3;
	public String wordSum_4;
	public String message;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonBody = new JSONObject(bodyElement);
			result = jsonBody.getString("result");
			wordSum_0 = jsonBody.getString("wordSum_0");
			wordSum_1 = jsonBody.getString("wordSum_1");
			wordSum_2 = jsonBody.getString("wordSum_2");
			wordSum_3 = jsonBody.getString("wordSum_3");
			wordSum_4 = jsonBody.getString("wordSum_4");
			message = jsonBody.getString("message");
			// Log.e("jsonBody", ""+jsonBody);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
