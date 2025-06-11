package com.iyuba.toeiclistening.protocol;

import java.io.IOException;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseXMLRequest;
import com.iyuba.toeiclistening.frame.util.XmlSerializer;


public class DictRequest extends BaseXMLRequest {
	
	String word="";
	
	public DictRequest(String word){
		this.word=word;
		setAbsoluteURI("http://word."+com.iyuba.core.util.Constant.IYBHttpHead+"/words/apiWord.jsp?q="+word);
	}
	
	@Override
	protected void fillBody(XmlSerializer serializer) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new DictResponse();
	}

}
