package com.iyuba.core.me.protocol;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.me.sqlite.mode.ListenWordDetail;

public class ListenDetailResponse extends BaseJSONResponse {
	public List<ListenWordDetail> mList = new ArrayList<ListenWordDetail>();
	public String result;
	private String lesson;
	private String lessonId;
	private String testNum;
	private String testWd;
	private String time;
	SimpleDateFormat dateformat = new SimpleDateFormat("yy-MM-dd hh:mm:ss");

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		JSONObject jsonObjectRoot;
		try {
			jsonObjectRoot = new JSONObject(bodyElement);
			result = jsonObjectRoot.getString("result");
			JSONArray arr = new JSONArray(jsonObjectRoot.getString("data"));
			for (int i = 0; i < arr.length() - 1; i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				ListenWordDetail lwd = new ListenWordDetail();
				lwd.lesson = temp.getString("Lesson");
				lwd.lessonId = temp.getString("LessonId");
				lwd.testNum = temp.getString("TestNumber");
				lwd.testWd = temp.getString("TestWords");
				Date beginTime = dateformat.parse(temp.getString("BeginTime"));
				Date endTime = dateformat.parse(temp.getString("EndTime"));
				long diff = (endTime.getTime() - beginTime.getTime()) / 1000;
				lwd.time = String.valueOf(diff);

				mList.add(lwd);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
