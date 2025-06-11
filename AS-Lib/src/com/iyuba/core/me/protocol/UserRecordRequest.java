package com.iyuba.core.me.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

public class UserRecordRequest extends BaseJSONRequest {

	public UserRecordRequest(String uid) {
		// TODO Auto-generated constructor stub

		setAbsoluteURI("http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/getStudyRecord.jsp?uid="
				+ uid);

	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new UserRecordResponse();
	}

}
