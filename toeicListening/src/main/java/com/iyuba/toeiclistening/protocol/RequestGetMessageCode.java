package com.iyuba.toeiclistening.protocol;

import java.io.IOException;

import android.util.Log;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseXMLRequest;
import com.iyuba.toeiclistening.frame.util.XmlSerializer;

/**
 * @author yao 鐠囬攱鐪?閼惧嘲褰囬惌顓濅繆妤犲矁鐦夐惍锟?
 */
public class RequestGetMessageCode extends BaseXMLRequest {
	public static final String protocolCode = "1";

	public RequestGetMessageCode(String userphone) {
		
		// TODO Auto-generated constructor stub
		setAbsoluteURI("http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/sendMessage.jsp?format=xml&userphone="+userphone);
		Log.e("ss", "http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/sendMessage.jsp?format=xml&userphone="+userphone);
	}

	
	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseGetMessageCode();
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
