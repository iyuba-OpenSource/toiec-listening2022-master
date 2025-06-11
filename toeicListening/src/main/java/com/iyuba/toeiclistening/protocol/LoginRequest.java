package com.iyuba.toeiclistening.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseJSONRequest;
import com.iyuba.toeiclistening.util.MD5;



/**
 * 鐢ㄦ埛鐧诲綍
 * 
 * @author chentong
 * 
 */
public class LoginRequest extends BaseJSONRequest {

	private String userName, password;

	public LoginRequest(String userName, String password,String x,String y) {
		this.userName = userName;
		this.password = password;
		if(x!=null&&y!=null){
			setAbsoluteURI("http://apis."+com.iyuba.core.util.Constant.IYBHttpHead+"/v2/api.iyuba?protocol=10001&username="
					+ this.userName
					+ "&password="
					+ MD5.getMD5ofStr(password)
					+ "&sign="
					+ MD5.getMD5ofStr("10001" + userName
							+ MD5.getMD5ofStr(this.password) + "iyubaV2")
					+ "&format=xml&x="+x+"&y="+y);
			Log.e("lofgin","http://apis."+com.iyuba.core.util.Constant.IYBHttpHead+"/v2/api.iyuba?protocol=10001&username="
					+ this.userName
					+ "&password="
					+ MD5.getMD5ofStr(password)
					+ "&sign="
					+ MD5.getMD5ofStr("10001" + userName
							+ MD5.getMD5ofStr(this.password) + "iyubaV2")
					+ "&format=xml&x="+x+"&y="+y);
		}else{
			setAbsoluteURI("http://apis."+com.iyuba.core.util.Constant.IYBHttpHead+"/v2/api.iyuba?protocol=10001&username="
					+ this.userName
					+ "&password="
					+ MD5.getMD5ofStr(password)
					+ "&sign="
					+ MD5.getMD5ofStr("10001" + userName
							+ MD5.getMD5ofStr(this.password) + "iyubaV2")
					+ "&format=xml");
			Log.e("lofgin","http://apis."+com.iyuba.core.util.Constant.IYBHttpHead+"/v2/api.iyuba?protocol=10001&username="
					+ this.userName
					+ "&password="
					+ MD5.getMD5ofStr(password)
					+ "&sign="
					+ MD5.getMD5ofStr("10001" + userName
							+ MD5.getMD5ofStr(this.password) + "iyubaV2")
					+ "&format=xml&x="+x+"&y="+y);
		}
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new LoginResponse();
	}

}
