package com.iyuba.toeiclistening.protocol;

import java.io.IOException;
import java.net.URLEncoder;

import android.util.Log;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseXMLRequest;
import com.iyuba.toeiclistening.frame.util.XmlSerializer;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.util.MD5;

/**
 * 濞夈劌鍞介崡蹇氼唴
 * 
 * @protocolCode 11002
 * @author yaoyao
 * 
 */
public class RequestPhoneNumRegister extends BaseXMLRequest {
	public static final String protocolCode = "11002";
	public String md5Status = "1"; // 
	public String emailStatus = "0";

	/**
	 * 
	 * @param wordKey
	 */
	public RequestPhoneNumRegister(String userName, String password,
			String mobile) {
		
		String string="http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/v2/api.iyuba?platform=android&format=json&protocol=11002"
			+"&username="+URLEncoder.encode(userName)
			+"&password="+MD5.getMD5ofStr(password)
			+"&sign="+MD5.getMD5ofStr(protocolCode+userName+MD5.getMD5ofStr(password)+"iyubaV2")
			+"&mobile="+mobile;
		Log.e("", string);
		setAbsoluteURI("http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/v2/api.iyuba?platform=android&format=json&protocol=11002"
				+"&username="+URLEncoder.encode(userName)
				+"&password="+MD5.getMD5ofStr(password)
				+"&sign="+MD5.getMD5ofStr(protocolCode+userName+MD5.getMD5ofStr(password)+"iyubaV2")
				+"&mobile="+mobile+"&app="+Constant.APP_TYPE);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponsePhoneNumRegister();
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
