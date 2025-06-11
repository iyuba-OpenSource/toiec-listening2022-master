package com.iyuba.core.common.protocol.message;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.util.Constant;

/**
 * 注册协议
 * 
 * @protocolCode 10002
 * @author yaoyao
 * 
 */
public class RequestPhoneNumRegister extends BaseJSONRequest {
	public static final String protocolCode = "11002";
	public String md5Status = "1"; // 0=未加密,1=加密
	public String emailStatus = "0";

	/**
	 * 
	 * @param userName
	 */

	public RequestPhoneNumRegister(String userName, String password,
			String mobile) {

		try {
			setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?platform=android&app=voa&format=json&protocol=11002"
					+ "&username="
					+ URLEncoder.encode(userName, "UTF-8")
					+ "&password="
					+ MD5.getMD5ofStr(password)
					+ "&sign="
					+ MD5.getMD5ofStr(protocolCode + userName
							+ MD5.getMD5ofStr(password) + "iyubaV2")
					+ "&mobile=" + mobile);
			
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponsePhoneNumRegister();
	}

}
