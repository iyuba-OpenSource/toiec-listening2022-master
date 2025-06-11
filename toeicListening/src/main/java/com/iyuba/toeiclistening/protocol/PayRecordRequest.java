package com.iyuba.toeiclistening.protocol;

import java.io.IOException;

import android.util.Log;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseXMLRequest;
import com.iyuba.toeiclistening.frame.util.XmlSerializer;
import com.iyuba.toeiclistening.util.Constant;



public class PayRecordRequest extends BaseXMLRequest {
	private String appId;// 213

	/**
	 * 鏍规嵁userId璇锋眰鏄惁宸蹭粯璐?
	 * 
	 * @param userId
	 * @param record
	 */
	public PayRecordRequest(String userId) {

		this.appId = Constant.APPID;		
		setAbsoluteURI("http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/recordApi.jsp?userId="
				+ userId + "&appId=" + appId + "&productId=0");
		Log.e("record","http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/recordApi.jsp?userId="
			+ userId + "&appId=" + appId + "&productId=0");
		
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new PayRecordResponse();
	}

}
