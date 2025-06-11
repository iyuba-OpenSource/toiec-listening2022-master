package com.iyuba.core.common.protocol.base;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * 获取网页单词本
 * 
 * @author Administrator
 * 
 */
public class GradeRequest extends BaseJSONRequest {

	public GradeRequest(String uid) {
		setAbsoluteURI("http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/ecollege/getPaiming.jsp?format=json&uid="
				+ uid + "&appName=" + Constant.AppName);
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new GradeResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub

	}

}
