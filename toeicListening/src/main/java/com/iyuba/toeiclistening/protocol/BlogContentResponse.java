package com.iyuba.toeiclistening.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.toeiclistening.frame.protocol.BaseJSONResponse;

import android.util.Log;

public class BlogContentResponse extends BaseJSONResponse {
	
	public String result;
	public String blogid;
	public String subject;
	public String viewnum;
	public String message;
	private String responseString;
	public BlogContentResponse() {
		// TODO 鑷姩鐢熸垚鐨勬瀯閫犲嚱鏁板瓨鏍?
	}
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍?
		try {
			responseString=bodyElement.toString().trim();
			JSONObject jsonObjectRootRoot = new JSONObject(responseString.substring(
					responseString.indexOf("{", 2), responseString.lastIndexOf("}") + 1));
			//JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
			result = bodyElement.charAt(10)+"";
			message = jsonObjectRootRoot.getString("message");
			blogid = jsonObjectRootRoot.getString("blogid");
			viewnum = jsonObjectRootRoot.getString("viewnum");
			subject = jsonObjectRootRoot.getString("subject");
			//Log.d("result", result+"");
			//Log.d("message", message+"");
		if (result.equals("1")) {
			Log.d("鑾峰彇鍐呭鎴愬姛", "鎴愬姛");
		}
		else {
			Log.d("鑾峰彇鍐呭澶辫触", "澶辫触");
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	

}
