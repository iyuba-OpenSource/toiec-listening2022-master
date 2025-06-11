package com.iyuba.core.microclass.protocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class AdResponse extends BaseJSONResponse {
	public String result = "";
	public String adPicUrl = "";
	public String adPicTime = "";
	public String basePicUrl = "";
	public String basePicTime = "";

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		JSONObject jsonObjectRoot;
		try {
			jsonObjectRoot = new JSONObject(bodyElement);
			result = jsonObjectRoot.getString("result");
			Log.d("AdResponse adPicUrl", "44444444444444444444");
			if (result.equals("1")) {
				JSONArray JsonArrayData = jsonObjectRoot.getJSONArray("data");
				if (JsonArrayData != null) {
					JSONObject jsonObjectData = JsonArrayData.getJSONObject(0);
					adPicTime = jsonObjectData
							.getString("startuppic_StartDate");
					adPicUrl = jsonObjectData.getString("startuppic");
					
					Log.d("AdResponse adPicUrl", adPicUrl);
					
					jsonObjectData = JsonArrayData.getJSONObject(1);
					basePicTime = jsonObjectData
							.getString("startuppic_StartDate");
					basePicUrl = jsonObjectData.getString("startuppic");
					
					Log.d("AdResponse basePicUrl", basePicUrl);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
