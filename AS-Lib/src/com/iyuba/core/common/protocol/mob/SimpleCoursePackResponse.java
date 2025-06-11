package com.iyuba.core.common.protocol.mob;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.sqlite.mode.mob.CoursePack;

public class SimpleCoursePackResponse extends BaseJSONResponse {
	public ArrayList<CoursePack> coursePacks;
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
				coursePacks = new ArrayList<CoursePack>();
				JSONArray JsonArrayData = jsonObjectRoot.getJSONArray("data");
				if (JsonArrayData != null) {
					for (int i = 0; i < JsonArrayData.length(); i++) {
						JSONObject jsonObjectData = JsonArrayData
								.getJSONObject(i);
						CoursePack coursePack = new CoursePack();
						try {
							coursePack.id = Integer.parseInt(jsonObjectData
									.getString("id"));
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							coursePack.price = Double
									.parseDouble(jsonObjectData
											.getString("price"));
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							coursePack.desc = jsonObjectData.getString("desc");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							coursePack.name = jsonObjectData.getString("name");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							coursePack.ownerid = Integer
									.parseInt(jsonObjectData
											.getString("ownerid"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							coursePack.picUrl = jsonObjectData.getString("pic");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							coursePack.classNum = Integer
									.parseInt(jsonObjectData
											.getString("classNum"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						coursePacks.add(coursePack);
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
