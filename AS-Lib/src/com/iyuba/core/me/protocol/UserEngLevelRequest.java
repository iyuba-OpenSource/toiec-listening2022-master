package com.iyuba.core.me.protocol;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.util.Constant;

public class UserEngLevelRequest extends BaseJSONRequest {
	public static final String protocolCode = "200031";

	public UserEngLevelRequest(String uid, String plevel, String preadLevel,
			String ptalkLevel, String glevel, String gtalkLevel,
			String greadLevel) throws UnsupportedEncodingException {
		// TODO Auto-generated constructor stub

		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?platform=android&format=json&protocol="
				+ protocolCode
				+ "&id="
				+ uid
				+ "&sign="
				+ MD5.getMD5ofStr(protocolCode + uid + "iyubaV2")
				+ "&key="
				+ "ptalklevel,preadlevel,plevel,glevel,gtalklevel,greadlevel"
				+ "&value="
				+ ptalkLevel
				+ ","
				+ preadLevel
				+ ","
				+ URLEncoder.encode(URLEncoder.encode("'" + plevel + "'"))
				+","
				+glevel+","+gtalkLevel+","+greadLevel);

//		Log.e("url",
//				"http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/v2/api.iyuba?platform=android&format=json&protocol="
//						+ protocolCode
//						+ "&id="
//						+ uid
//						+ "&sign="
//						+ MD5.getMD5ofStr(protocolCode + uid + "iyubaV2")
//						+ "&key="
//						+ "ptalklevel,preadlevel,plevel,glevel,gtalklevel,greadlevel"
//						+ "&value="
//						+ ptalkLevel
//						+ ","
//						+ preadLevel
//						+ ","
//						+ URLEncoder.encode(URLEncoder.encode("'" + plevel + "'"))
//						+","
//						+glevel+","+gtalkLevel+","+greadLevel);

	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new UserEngLevelResponse();
	}

}
