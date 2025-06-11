/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.util.Constant;

/**
 * @author yao 验证短信验证码
 */
public class RequestCheckMessageCode extends BaseJSONRequest {

	public RequestCheckMessageCode(String userphone, String identifier,
			String rand_code) {
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/checkCode.jsp?&format=json"
				+ "&userphone=" + userphone + "" + "&identifier=" + identifier
				+ "&rand_code=" + rand_code);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseCheckMessageCode();
	}

}
