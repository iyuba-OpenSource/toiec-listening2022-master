package com.iyuba.core.common.protocol.news;

import java.util.ArrayList;
import java.util.Vector;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;
import com.iyuba.core.common.sqlite.mode.Word;

public class WordSynResponse extends BaseXMLResponse {
	public ArrayList<Word> wordList = new ArrayList<Word>();
	public int total;
	private String user;
	public int firstPage;
	public int prevPage;
	public int nextPage;
	public int lastPage;

	public WordSynResponse(String user) {
		this.user = user;
	}

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO Auto-generated method stub
		Vector rankVector = bodyElement.getChildren();
		total = Integer.parseInt(Utility
				.getSubTagContent(bodyElement, "counts"));
		firstPage = Integer.parseInt(Utility.getSubTagContent(bodyElement,
				"firstPage"));
		prevPage = Integer.parseInt(Utility.getSubTagContent(bodyElement,
				"prevPage"));
		nextPage = Integer.parseInt(Utility.getSubTagContent(bodyElement,
				"nextPage"));
		lastPage = Integer.parseInt(Utility.getSubTagContent(bodyElement,
				"lastPage"));
		int size = rankVector.size();
		kXMLElement ranKXMLElement;
		Word word;
		for (int i = 0; i < size; i++) {
			ranKXMLElement = (kXMLElement) rankVector.elementAt(i);
			if (ranKXMLElement.getTagName().equals("row")) {
				word = new Word();
				try {
					word.key = Utility.getSubTagContent(ranKXMLElement, "Word");
				} catch (Exception e) {
					// TODO: handle exception
				}
				try {
					word.audioUrl = Utility.getSubTagContent(ranKXMLElement,
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
				word.userid = user;
				wordList.add(word);
			}
		}
		return true;
	}

}
