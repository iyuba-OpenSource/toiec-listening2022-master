package com.iyuba.core.microclass.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * 用户登录
 * 
 * @author chentong
 */
public class appUpdateRequest extends BaseJSONRequest {

	private int version;

	public appUpdateRequest(int version) {
		this.version = version;
		setAbsoluteURI(Constant.appUpdateUrl + this.version);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new appUpdateResponse();
	}

}
