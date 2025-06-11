package com.iyuba.core.common.protocol.base;

import java.io.IOException;

import com.iyuba.core.common.network.xml.XmlSerializer;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseXMLRequest;
import com.iyuba.core.common.util.TextAttr;

/**
 * 获取网页单词本
 * 
 * @author Administrator
 * 
 */
public class DictRequest extends BaseXMLRequest {

	public DictRequest(String word) {
		word = TextAttr.encode(word);
		setAbsoluteURI("http://word."+com.iyuba.core.util.Constant.IYBHttpHead+"/words/apiWord.jsp?q=" + word);
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
