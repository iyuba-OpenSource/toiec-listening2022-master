package com.iyuba.toeiclistening.frame.util;

import java.util.Vector;

public class Utility {
	public static kXMLElement getChildByName(kXMLElement parent, String tagName) {
		Vector vecChilds = parent.getChildren();
		for (int i = 0; i < vecChilds.size(); i++) {
			kXMLElement element = (kXMLElement) vecChilds.elementAt(i);
			if ((element.getTagName() != null)
					&& element.getTagName().equals(tagName)) {
				return element;
			}
		}
		return null;
	}

	public static String getSubTagContent(kXMLElement parent, String tagName) {
		kXMLElement xmlElement = getChildByName(parent, tagName);
		if (xmlElement != null) {
			return xmlElement.getContents();
		}
		return "";
	}

	// 杩斿洖鑺傜偣涓寚瀹氬悕绉扮殑瀛愯妭鐐归泦鍚?
	public static Vector<kXMLElement> getChildrenByName(kXMLElement parent,
			String tagName) {
		Vector<kXMLElement> vecRet = new Vector<kXMLElement>();
		vecRet.clear();
		Vector vecChilds = parent.getChildren();
		for (int i = 0; i < vecChilds.size(); i++) {
			kXMLElement element = (kXMLElement) vecChilds.elementAt(i);
			if ((element.getTagName() != null)
					&& element.getTagName().equals(tagName)) {
				vecRet.add(element);
			}
		}
		return vecRet;
	}
}
