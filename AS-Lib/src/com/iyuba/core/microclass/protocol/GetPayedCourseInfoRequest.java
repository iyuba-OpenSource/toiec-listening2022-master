package com.iyuba.core.microclass.protocol;

import java.io.IOException;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;
import com.iyuba.core.common.util.MD5;

public class GetPayedCourseInfoRequest extends BaseXMLRequest {

	private String UserId;
	private String AppId;
	private String sign;
	private String PackageId;
	public GetPayedCourseInfoRequest(String userId,String appId,String packageId){
		if(userId == null){
			this.UserId = "0";
		}else{
			this.UserId=userId;
		}
		this.AppId=appId;
		this.PackageId=packageId;
		sign=MD5.getMD5ofStr(appId+UserId+packageId+"iyuba");
		Log.e("PayedCourseRecord","http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/apiGetPayRecord.jsp?userId="+UserId+
				"&appId="+AppId+"&packageId="+PackageId+"&sign="+sign);
		setAbsoluteURI("http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/apiGetPayRecord.jsp?userId="+UserId+
				"&appId="+AppId+"&packageId="+PackageId+"&sign="+sign);
	}
	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new GetPayedCourseInfoResponse();
	}

}

