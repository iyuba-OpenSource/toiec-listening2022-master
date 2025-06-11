package com.iyuba.core.common.protocol.mob;

import java.io.IOException;

import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;

public class SimpleGetPayedCourseRequest extends BaseXMLRequest {

	public SimpleGetPayedCourseRequest(String url) {
		setAbsoluteURI(url);
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new SimpleGetPayedCourseResponse();
	}

}
