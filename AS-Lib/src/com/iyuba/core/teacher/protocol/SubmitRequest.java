package com.iyuba.core.teacher.protocol;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.teacher.sqlite.mode.Teacher;

public class SubmitRequest extends BaseJSONRequest {
	private String format = "json";
	
	public SubmitRequest(String uid ) {
		setAbsoluteURI("http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/teacher/api/submit.jsp?format=json&uid="
	   +uid
		);
      Log.e("iyuba","http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/teacher/api/submit.jsp?format=json&uid="
    		   +uid
				);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return  new  SubmitResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
