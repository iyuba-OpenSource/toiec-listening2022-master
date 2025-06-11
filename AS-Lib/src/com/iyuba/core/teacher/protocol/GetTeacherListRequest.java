
package com.iyuba.core.teacher.protocol;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

public class GetTeacherListRequest extends BaseJSONRequest {
	private String format = "json";
	
	public GetTeacherListRequest(int pageNum,int category) {
		setAbsoluteURI("http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/teacher/api/getTeacherList.jsp?format=json"+"&pageNum="
				+pageNum+"&category="+category 
				);

	    Log.e("iyuba", "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/teacher/api/getTeacherList.jsp?format=json"+"&pageNum="
				+pageNum+"&category="+category);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new GetTeacherListResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
