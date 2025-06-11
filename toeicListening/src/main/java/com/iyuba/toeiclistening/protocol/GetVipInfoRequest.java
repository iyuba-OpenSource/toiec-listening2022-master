package com.iyuba.toeiclistening.protocol;

import java.io.IOException;

import android.util.Log;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseXMLRequest;
import com.iyuba.toeiclistening.frame.util.XmlSerializer;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.util.MD5;

public class GetVipInfoRequest extends BaseXMLRequest {

	private String userId;
	private String appId;
	private String sign;
	private String productId;
	public GetVipInfoRequest(String userId,String producdId){
		this.userId=userId;
		this.appId=Constant.APPID;
		this.productId=producdId;
		sign=MD5.getMD5ofStr(appId+userId+producdId+"iyuba");
		Log.e("requestVIp","http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/apiGetVip.jsp?userId="+userId+
				"&appId="+appId+"&productId="+producdId+"&sign="+sign);
		setAbsoluteURI("http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/apiGetVip.jsp?userId="+userId+
				"&appId="+appId+"&productId="+producdId+"&sign="+sign);
	}
	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new GetVipInfoResponse();
	}

}
