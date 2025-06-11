package com.iyuba.toeiclistening.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseJSONRequest;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.util.MD5;


/**
 * 鐢ㄦ埛娉ㄥ唽
 * 
 * @author chentong
 * 
 */
public class RegistRequest extends BaseJSONRequest {

	private String userName, email;

	public RegistRequest(String userName, String password, String realName,
			String email) {
		this.userName = userName;
		this.email = email;
		setAbsoluteURI("http://apis."+com.iyuba.core.util.Constant.IYBHttpHead+"/v2/api.iyuba?protocol=10002&email="
				+ this.email
				+ "&username="
				+ this.userName
				+ "&password="
				+ MD5.getMD5ofStr(password)
				+ "&platform=android&app="+Constant.APP_TYPE+"&format=xml&sign="
				+ MD5.getMD5ofStr("10002" + userName + MD5.getMD5ofStr(password) + email
						+ "iyubaV2"));
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
