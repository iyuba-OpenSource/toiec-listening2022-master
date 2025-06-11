package com.iyuba.toeiclistening.protocol;

import java.util.ArrayList;
import java.util.Vector;

import com.iyuba.toeiclistening.entity.NewWord;
import com.iyuba.toeiclistening.frame.protocol.BaseXMLResponse;
import com.iyuba.toeiclistening.frame.util.Utility;
import com.iyuba.toeiclistening.frame.util.kXMLElement;

public class WordSynRespose extends BaseXMLResponse {
	public ArrayList<NewWord> wordList = new ArrayList<NewWord>();
	public int total;
	private String user;
	
	public WordSynRespose(String user){
		this.user=user;
	}

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO Auto-generated method stub
		Vector rankVector = bodyElement.getChildren();
		total = Integer.parseInt(Utility
				.getSubTagContent(bodyElement, "counts"));
		for (int i = 0; i < rankVector.size(); i++) {
			kXMLElement ranKXMLElement = (kXMLElement) rankVector.elementAt(i);
			if (ranKXMLElement.getTagName().equals("row")) {
				NewWord word = new NewWord();
				try {
					word.Word = Utility.getSubTagContent(ranKXMLElement, "Word");
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					word.audio = Utility.getSubTagContent(ranKXMLElement,
							"Audio");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					word.pron = Utility
							.getSubTagContent(ranKXMLElement, "Pron");
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					word.def = Utility.getSubTagContent(ranKXMLElement, "Def");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				word.userName=user;
				wordList.add(word);
			}
		}
		return true;
	}

}
