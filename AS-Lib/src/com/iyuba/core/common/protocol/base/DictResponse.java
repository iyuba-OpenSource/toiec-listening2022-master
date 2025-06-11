package com.iyuba.core.common.protocol.base;

import java.util.Vector;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;
import com.iyuba.core.common.sqlite.mode.Word;

public class DictResponse extends BaseXMLResponse {
	public Word word;
	public String result;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO Auto-generated method stub
		word = new Word();
		result = Utility.getSubTagContent(bodyElement, "result");
		word.key = Utility.getSubTagContent(bodyElement, "key");
		word.lang = Utility.getSubTagContent(bodyElement, "lang");
		word.audioUrl = Utility.getSubTagContent(bodyElement, "audio");
		word.pron = Utility.getSubTagContent(bodyElement, "pron");
		word.def = Utility.getSubTagContent(bodyElement, "def");
		Vector<kXMLElement> XMLElements = Utility.getChildrenByName(
				bodyElement, "sent");
		kXMLElement ranKXMLElement;
		int size = XMLElements.size();
		if (size != 0) {
			StringBuffer sentence = new StringBuffer();
			for (int i = 0; i < size; i++) {
				ranKXMLElement = XMLElements.get(i);
				sentence.append(Utility.getSubTagContent(ranKXMLElement,
						"number"));
				sentence.append(": ");
				sentence.append(Utility
						.getSubTagContent(ranKXMLElement, "orig"));
				sentence.append("<br/>");
				sentence.append(Utility.getSubTagContent(ranKXMLElement,
						"trans"));
				sentence.append("<br/><br/>");
			}
			word.examples = sentence.toString();
		}
		return true;
	}

}
