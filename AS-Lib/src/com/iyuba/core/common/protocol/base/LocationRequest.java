package com.iyuba.core.common.protocol.base;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * 用户登录
 * 
 * @author chentong
 */
public class LocationRequest extends BaseJSONRequest {

	public LocationRequest(String latitude, String longitude) {
		setAbsoluteURI("http://maps.google.cn/maps/api/geocode/json?latlng="
				+ latitude + "," + longitude + "&sensor=true&language=zh-CN");
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new LocationResponse();
	}

}
