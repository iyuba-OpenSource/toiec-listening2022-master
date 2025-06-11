package com.iyuba.core.common.protocol.base;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

/**
 * 用户注册
 * 
 * @author chentong
 * 
 */
public class RegistRequest extends BaseJSONRequest {

	private String userName, email;

	public RegistRequest(String userName, String password, String email) {
		this.userName = userName;
		this.email = email;
		
		Log.d("RegistRequest:","http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=11002&email="
				+ this.email
				+ "&username="
				+ this.userName
				+ "&password="
				+ MD5.getMD5ofStr(password)
				+ "&platform=android&app="
				+ Constant.AppName
				+ "&format=xml&sign="
				+ MD5.getMD5ofStr("11002" + userName
						+ MD5.getMD5ofStr(password) + email + "iyubaV2"));
		
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=11002&email="
				+ this.email
				+ "&username="
				+ this.userName
				+ "&password="
				+ MD5.getMD5ofStr(password)
				+ "&platform=android&app="
				+ Constant.AppName
				+ "&format=xml&sign="
				+ MD5.getMD5ofStr("11002" + userName
						+ MD5.getMD5ofStr(password) + email + "iyubaV2"));
	
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new RegistResponse();
	}

}
