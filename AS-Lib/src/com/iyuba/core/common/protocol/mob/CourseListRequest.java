package com.iyuba.core.common.protocol.mob;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

public class CourseListRequest extends BaseJSONRequest {

	{
		requestId = 0;
	}

	private String id, type;

	public CourseListRequest(String id, String type) {
		this.id = id;
		this.type = type;
		setAbsoluteURI("http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/getClass.iyuba?protocol=10102&id="
				+ this.id
				+ "&type="
				+ this.type
				+ "&sign="
				+ MD5.getMD5ofStr("10102class" + this.id));

	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new CourseListResponse();
	}

}
