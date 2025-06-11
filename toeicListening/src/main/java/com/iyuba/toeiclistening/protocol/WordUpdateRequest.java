package com.iyuba.toeiclistening.protocol;

import java.io.IOException;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseXMLRequest;
import com.iyuba.toeiclistening.frame.util.XmlSerializer;

public class WordUpdateRequest extends BaseXMLRequest {

	String userId;
	public static final String MODE_INSERT = "insert";
	public static final String MODE_DELETE = "delete";
	String groupname = "Iyuba";
	String word;

	public WordUpdateRequest(String userId, String update_mode, String word) {
		this.userId = userId;
		this.word = word;
		setAbsoluteURI("http://word."+com.iyuba.core.util.Constant.IYBHttpHead+"/words/updateWord.jsp?userId="
				+ this.userId + "&mod=" + update_mode + "&groupName="
				+ groupname + "&word=" + this.word);
	}

	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new WordUpdateResponse();
	}

}
