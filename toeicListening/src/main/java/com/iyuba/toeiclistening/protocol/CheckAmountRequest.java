package com.iyuba.toeiclistening.protocol;

import java.io.IOException;

import android.util.Log;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseXMLRequest;
import com.iyuba.toeiclistening.frame.util.XmlSerializer;


/**
 * 鏌ヨ鐖辫甯?
 * 
 * @author chentong
 * 
 */
public class CheckAmountRequest extends BaseXMLRequest {
	public CheckAmountRequest(String userId) {
		setAbsoluteURI("http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/checkApi.jsp?userId=" + userId);
		Log.e("check","http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/checkApi.jsp?userId=" + userId );
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new CheckAmountResponse();
	}

}
