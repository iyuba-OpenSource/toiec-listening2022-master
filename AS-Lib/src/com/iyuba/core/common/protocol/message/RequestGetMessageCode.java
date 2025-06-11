/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * @author yao 请求 获取短信验证码
 */
public class RequestGetMessageCode extends BaseJSONRequest {

	public RequestGetMessageCode(String userphone) {
		// TODO Auto-generated constructor stub
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/sendMessage.jsp?"
				+ "&userphone=" + userphone + "&appId=" + Constant.APPID);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseGetMessageCode();
	}

}
