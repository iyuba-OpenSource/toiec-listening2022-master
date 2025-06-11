package com.iyuba.core.common.protocol.mob;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.sqlite.mode.mob.MbText;

public class SimpleCourseMbTextResponse extends BaseJSONResponse {
	public ArrayList<MbText> mbTextList;
	public MbText mbText = new MbText();
	public String result;
	public boolean has;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			result = jsonObjectRoot.getString("result");
			if (result.equals("1")) {
				has = true;
				mbTextList = new ArrayList<MbText>();
				JSONArray JsonArrayData = jsonObjectRoot.getJSONArray("data");

				if (JsonArrayData != null) {
					for (int i = 0; i < JsonArrayData.length(); i++) {
						JSONObject jsonObjectData = JsonArrayData
								.getJSONObject(i);
						MbText mbText = new MbText();
						try {
							mbText.id = Integer.parseInt(jsonObjectData
									.getString("id"));
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							mbText.imageName = jsonObjectData
									.getString("imageName");
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							mbText.seconds = Integer.parseInt(jsonObjectData
									.getString("seconds"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							mbText.answer = Integer.parseInt(jsonObjectData
									.getString("answer"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							mbText.number = Integer.parseInt(jsonObjectData
									.getString("number"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							mbText.type = Integer.parseInt(jsonObjectData
									.getString("type"));
						} catch (Exception e) {
							// TODO: handle exception
						}
						mbTextList.add(mbText);
					}
				}
			} else {
				has = false;
			}
		} catch (NumberFormatException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (JSONException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		return true;
	}

}
