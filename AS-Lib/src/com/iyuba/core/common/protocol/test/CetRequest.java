package com.iyuba.core.common.protocol.test;

import java.io.IOException;

import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;

public class CetRequest extends BaseXMLRequest {
	int type;

	public CetRequest(String url, int type) {
		this.type = type;
		setAbsoluteURI(url);
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new CetResponse(type);
	}

}
