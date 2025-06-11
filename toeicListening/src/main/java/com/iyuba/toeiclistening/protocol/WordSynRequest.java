package com.iyuba.toeiclistening.protocol;

import java.io.IOException;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseXMLRequest;
import com.iyuba.toeiclistening.frame.util.XmlSerializer;

public class WordSynRequest extends BaseXMLRequest {
	String user;

	public WordSynRequest(String userid, String userName) {
		this.user = userName;
		setAbsoluteURI("http://word."+com.iyuba.core.util.Constant.IYBHttpHead+"/words/wordListService.jsp?u="
				+ userid + "&pageNumber=1000&pageCounts=1000");
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new WordSynRespose(user);
	}

}
