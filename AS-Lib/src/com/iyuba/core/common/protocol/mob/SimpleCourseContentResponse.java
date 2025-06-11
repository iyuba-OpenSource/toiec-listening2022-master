package com.iyuba.core.common.protocol.mob;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.sqlite.mode.mob.CourseContent;

public class SimpleCourseContentResponse extends BaseJSONResponse {
	public ArrayList<CourseContent> courseContents;
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
				JSONArray JsonArrayData = jsonObjectRoot.getJSONArray("data");
				courseContents = new ArrayList<CourseContent>();
				if (JsonArrayData != null) {
					for (int i = 0; i < JsonArrayData.length(); i++) {
						JSONObject jsonObjectData = JsonArrayData
								.getJSONObject(i);
						CourseContent courseContent = new CourseContent();
						try {
							courseContent.id = Integer.parseInt(jsonObjectData
									.getString("id"));
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							courseContent.cost = Double
									.parseDouble(jsonObjectData
											.getString("cost"));
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							courseContent.desc = jsonObjectData
									.getString("desc");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							courseContent.titleName = jsonObjectData
									.getString("titleName");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							courseContent.credit = Integer
									.parseInt(jsonObjectData
											.getString("credit"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							courseContent.viewCount = Integer
									.parseInt(jsonObjectData
											.getString("viewCount"));
						} catch (Exception e) {
							// TODO: handle exception
						}
						courseContents.add(courseContent);
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
