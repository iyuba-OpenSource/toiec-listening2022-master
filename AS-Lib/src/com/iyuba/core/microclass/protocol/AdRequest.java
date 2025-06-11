package com.iyuba.core.microclass.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * 用户登录
 * 
 * @author chentong
 */
public class AdRequest extends BaseJSONRequest {

	public AdRequest() {
		setAbsoluteURI("http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/dev/getStartPicApi.jsp?format=json&appId="
				+ Constant.APPID);
		Log.d("AdRequest","http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/dev/getStartPicApi.jsp?format=json&appId="
				+ Constant.APPID);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new AdResponse();
	}

}
