package com.iyuba.core.common.protocol.base;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class LocationResponse extends BaseJSONResponse {
	public String subLocality = "";
	public String locality = "";
	public String province = "";
	private String type = "";

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		JSONObject jsonObjectRoot;
		try {
			jsonObjectRoot = new JSONObject(bodyElement);
			JSONArray JsonArrayData = jsonObjectRoot.getJSONArray("results");
			if (JsonArrayData != null) {
				JSONObject jsonLocationData = JsonArrayData.getJSONObject(0);
				JSONArray JsonArrayDataInner = jsonLocationData
						.getJSONArray("address_components");
				if (JsonArrayDataInner != null) {
					int size = JsonArrayDataInner.length();
					JSONObject jsonPositionData;
					JSONArray JsonArrayDataType;
					for (int i = 0; i < size; i++) {
						jsonPositionData = JsonArrayDataInner.getJSONObject(i);
						JsonArrayDataType = jsonPositionData
								.getJSONArray("types");
						type = JsonArrayDataType.get(0).toString();
						if (type.equals("administrative_area_level_1")) {
							province = jsonPositionData.getString("short_name");
						}
						if (type.equals("locality")) {
							locality = jsonPositionData.getString("short_name");
						}
						if (type.equals("sublocality")) {
							subLocality = jsonPositionData.getString("short_name");
						}
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
