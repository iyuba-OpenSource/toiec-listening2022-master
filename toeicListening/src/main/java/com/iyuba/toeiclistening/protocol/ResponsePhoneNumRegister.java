package com.iyuba.toeiclistening.protocol;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.toeiclistening.frame.protocol.BaseJSONResponse;



public class ResponsePhoneNumRegister extends BaseJSONResponse{
	public String resultCode;
	public String message;
	public boolean isRegSuccess;
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			resultCode= jsonObjectRoot.getString("result");
			message= jsonObjectRoot.getString("message");
			if (resultCode.equals("111")) {
				isRegSuccess=true;
			}else{
				isRegSuccess=false;
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	return true;

	}

}
