/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author yao 验证短信验证码
 */
public class RequestChangePassWord extends BaseJSONRequest {

	public RequestChangePassWord(String userName,String passWord,String sign,String userPhone) {
		String YZNUMBER_URL_FORGET = "http://api."+com.iyuba.configation.Constant.IYBHttpHead2+"/";

		setAbsoluteURI(YZNUMBER_URL_FORGET+"v2/api.iyuba?protocol=10014"
				+ "&format=json"
				+ "&username=" + userName
				+ "&password=" + passWord
				+ "&sign=" + sign
				+ "&userPhone=" + userPhone);
		Log.e("RequestForgetCode", YZNUMBER_URL_FORGET+"v2/api.iyuba?protocol=10014&format=json"
				+ "&username=" + userName
				+ "&password=" + passWord
				+ "&sign=" + sign
				+ "&userPhone=" + userPhone);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		Log.e("tag","失败的解析！");
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new ResponseChangePassWord();
	}

}
