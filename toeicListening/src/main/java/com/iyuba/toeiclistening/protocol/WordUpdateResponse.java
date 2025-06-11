package com.iyuba.toeiclistening.protocol;

import com.iyuba.toeiclistening.frame.protocol.BaseXMLResponse;
import com.iyuba.toeiclistening.frame.util.Utility;
import com.iyuba.toeiclistening.frame.util.kXMLElement;


public class WordUpdateResponse extends BaseXMLResponse {
//	public List<Word> words;
	
	public int result;
	public String word;
	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		result=Integer.parseInt(Utility.getSubTagContent(bodyElement, "result"));
		word=Utility.getSubTagContent(bodyElement, "word");
		return true;
	}

}
