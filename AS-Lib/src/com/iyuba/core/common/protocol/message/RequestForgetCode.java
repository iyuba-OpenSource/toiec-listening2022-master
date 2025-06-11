/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author yao 验证短信验证码
 */
public class RequestForgetCode extends BaseJSONRequest {

	public RequestForgetCode(String userphone) {
		String YZNUMBER_URL_FORGET = "http://api."+com.iyuba.configation.Constant.IYBHttpHead2+"/";

		setAbsoluteURI(YZNUMBER_URL_FORGET+"v2/api.iyuba?protocol=10009"
				+ "&format=" + "json"
				+ "&username=" + userphone);
		Log.e("RequestForgetCode", YZNUMBER_URL_FORGET+"v2/api.iyuba?protocol=10009&format=json"
				+ "&username=" + userphone);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new ResponseForgetCheck();
	}

}
