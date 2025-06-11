package com.iyuba.toeiclistening.protocol;

import java.util.Vector;

import com.iyuba.toeiclistening.entity.NewWord;
import com.iyuba.toeiclistening.frame.protocol.BaseXMLResponse;
import com.iyuba.toeiclistening.frame.util.Utility;
import com.iyuba.toeiclistening.frame.util.kXMLElement;


public class DictResponse extends BaseXMLResponse {
	public NewWord word;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO Auto-generated method stub
		word = new NewWord();
		word.Word = Utility.getSubTagContent(bodyElement, "key");
		word.lang = Utility.getSubTagContent(bodyElement, "lang");
		word.audio= Utility.getSubTagContent(bodyElement, "audio");
		//Log.e("audio", word.audio);
		word.pron = Utility.getSubTagContent(bodyElement, "pron");
		word.def = Utility.getSubTagContent(bodyElement, "def");
		Vector rankVector = bodyElement.getChildren();
		StringBuffer sentence = new StringBuffer();
		for (int i = 0; i < rankVector.size(); i++) {
			kXMLElement ranKXMLElement = (kXMLElement) rankVector.elementAt(i);
			if (ranKXMLElement.getTagName().equals("sent")) {
				sentence.append(Utility.getSubTagContent(ranKXMLElement,
						"number"));
				sentence.append("：");
				sentence.append(Utility
						.getSubTagContent(ranKXMLElement, "orig"));
				sentence.append("<br/>");
				sentence.append(Utility.getSubTagContent(ranKXMLElement,
						"trans"));
				sentence.append("<br/>");
			}
		}
		word.examples = sentence.toString();
		return true;
	}

}