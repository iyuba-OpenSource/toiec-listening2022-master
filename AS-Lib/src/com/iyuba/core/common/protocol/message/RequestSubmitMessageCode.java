/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author yao 验证短信验证码
 */
public class RequestSubmitMessageCode extends BaseJSONRequest {

	public RequestSubmitMessageCode(String userphone) {
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=10009&format=gson"
				+ "&username=" + userphone);
		LogUtil.e("RequestSubmitMessageCode", "http://api."+Constant.IYBHttpHead2+"/sendMessage3.jsp?format=json"
				+ "&userphone=" + userphone);
		LogUtil.e("RequestSubmitMessageCode  http://api."+ WebConstant.COM_CN_SUFFIX +"v2/api.iyuba?protocol=10009&format=gson" +
				"&username=" + userphone);
	}
    //http://api.iyuba.com.cn/v2/api.iyuba?protocol=10009&format=gson&username=18701342549
	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new ResponseSubmitMessageCode();
	}

}
