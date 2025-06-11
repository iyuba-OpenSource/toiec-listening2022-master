package com.iyuba.core.microclass.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class ViewCountPackResponse extends BaseJSONResponse {

	public String responseString;
	public JSONObject jsonBody;
	public String ResultCode;
	
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		try {
			jsonBody = new JSONObject(bodyElement);
			Log.e("ViewCountPackResponse jsonBody", ""+jsonBody);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try{
			ResultCode=jsonBody.getString("ResultCode");
		}catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}

}
