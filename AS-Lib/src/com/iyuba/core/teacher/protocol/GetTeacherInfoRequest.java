package com.iyuba.core.teacher.protocol;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

public class GetTeacherInfoRequest  extends BaseJSONRequest {
	private String format = "json";
	
	public GetTeacherInfoRequest(String uid) {
		setAbsoluteURI("http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/teacher/api/getTeacherInfo.jsp?format=json&uid="+uid);
		
		Log.e("iyuba", "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/teacher/api/getTeacherInfo.jsp?format=json&uid="+uid);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new GetTeacherInfoResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
