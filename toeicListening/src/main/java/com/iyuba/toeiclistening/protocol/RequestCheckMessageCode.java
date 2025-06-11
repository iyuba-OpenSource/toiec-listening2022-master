package com.iyuba.toeiclistening.protocol;

import java.io.IOException;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseXMLRequest;
import com.iyuba.toeiclistening.frame.util.XmlSerializer;

/**
 * @author yao 閻犲洭鏀遍惇锟介柤鎯у槻瑜板洭鎯岄婵呯箚濡ょ姴鐭侀惁澶愭儘閿燂拷
 */
public class RequestCheckMessageCode extends BaseXMLRequest {

	public RequestCheckMessageCode(String userphone, String identifier,
			String rand_code) {

		setAbsoluteURI("http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/checkCode.jsp?userphone="
				+ userphone + "&identifier=" + identifier + "&rand_code="
				+ rand_code + "&format=xml");

	}

	
	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseCheckMessageCode();
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}

}
