package com.iyuba.core.me.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.me.sqlite.mode.TestResultDetail;

public class TestDetailResponse extends BaseJSONResponse{
	public List<TestResultDetail> mList = new ArrayList<TestResultDetail>();
	public String result;


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
				TestResultDetail lwd = new TestResultDetail();
				lwd.testTime = temp.getString("TestTime");
				lwd.lessonId = temp.getString("LessonId");
				lwd.testNum = temp.getString("TestNumber");
				lwd.testWords = temp.getString("TestWords");
				lwd.beginTime = temp.getString("BeginTime");
				lwd.testindex = temp.getString("testindex");
				lwd.userAnswer = temp.getString("UserAnswer");
				lwd.rightAnswer = temp.getString("RightAnswer");
				lwd.score = temp.getString("Score");
				lwd.updateTime = temp.getString("UpdateTime");

				mList.add(lwd);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return true;
	}

}
