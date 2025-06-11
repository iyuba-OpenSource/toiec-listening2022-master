package com.iyuba.core.common.protocol.mob;

import java.util.ArrayList;
import java.util.Vector;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;
import com.iyuba.core.common.sqlite.mode.mob.PayedCourseRecord;

public class SimpleGetPayedCourseResponse extends BaseXMLResponse {

	public String result;
	public ArrayList<PayedCourseRecord> pcrList=new ArrayList<PayedCourseRecord>();
	public PayedCourseRecord pcr;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO Auto-generated method stub
		result = Utility.getSubTagContent(bodyElement, "result");
		kXMLElement dataElement = Utility.getChildByName(bodyElement, "data");
		Vector<kXMLElement> XMLElements = Utility.getChildrenByName(
				dataElement, "record");
		int size = XMLElements.size();

		if (size != 0) {
			kXMLElement ranKXMLElement;
			pcrList = new ArrayList<PayedCourseRecord>();
			for (int i = 0; i < size; i++) {
				ranKXMLElement = XMLElements.get(i);
				if (ranKXMLElement.getTagName().equals("record")) {
					pcr = new PayedCourseRecord();
					pcr.PackId = Utility.getSubTagContent(ranKXMLElement,
							"PackId");
					pcr.ProductId = Utility.getSubTagContent(ranKXMLElement,
							"ProductId");
					pcr.Amount = Utility.getSubTagContent(ranKXMLElement,
							"Amount");
					pcr.flg = Utility.getSubTagContent(ranKXMLElement, "flg");
					pcrList.add(pcr);
				}
			}

		}
		return true;
	}

}
