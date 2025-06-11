package com.iyuba.core.common.protocol.news;

import java.io.IOException;

import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;

public class WordSynRequest extends BaseXMLRequest {
	String user;

	public WordSynRequest(String userid, String userName, int page) {
		this.user = userid;
		setAbsoluteURI("http://word."+com.iyuba.core.util.Constant.IYBHttpHead+"/words/wordListService.jsp?u="
				+ userid + "&pageCounts=50&pageNumber=" + page);
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new WordSynResponse(user);
	}

}
