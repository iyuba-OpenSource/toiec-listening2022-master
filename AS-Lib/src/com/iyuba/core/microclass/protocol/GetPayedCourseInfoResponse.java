package com.iyuba.core.microclass.protocol;

import java.util.ArrayList;
import java.util.Vector;

import android.util.Log;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;
import com.iyuba.core.microclass.sqlite.mode.PayedCourseRecord;

public class GetPayedCourseInfoResponse extends BaseXMLResponse{

	public String result;
	public String msg;
	public kXMLElement data;
	
	public ArrayList<PayedCourseRecord>  pcrList=new ArrayList<PayedCourseRecord>();
	
	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO Auto-generated method stub
		result=Utility.getSubTagContent(bodyElement, "result");
		msg=Utility.getSubTagContent(bodyElement, "msg");
		data = Utility.getChildByName(bodyElement, "data");
		
		Vector rankVector = data.getChildren();
		Log.d("mark", ""+rankVector.size());
		for (int i = 0; i < rankVector.size(); i++) {
		//	Log.d("mark", "mark3");
			kXMLElement ranKXMLElement = (kXMLElement) rankVector.elementAt(i);
			if (ranKXMLElement.getTagName().equals("record")) {
				PayedCourseRecord payedCourseRecord=new PayedCourseRecord();
			//	Log.d("mark", "mark4");
				try {
					payedCourseRecord.PackId = Utility.getSubTagContent(ranKXMLElement, "PackId");
//					Log.d("PackId:", payedCourseRecord.PackId);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				try {
					payedCourseRecord.ProductId=Utility.getSubTagContent(ranKXMLElement,"ProductId");
//					Log.d("ProductId:", payedCourseRecord.ProductId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					payedCourseRecord.Amount = Utility.getSubTagContent(ranKXMLElement,"Amount");
//					Log.d("Amount:", payedCourseRecord.Amount);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				try {
					payedCourseRecord.flg =Utility.getSubTagContent(ranKXMLElement,"flg");
//					Log.d("flg:", payedCourseRecord.flg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pcrList.add(payedCourseRecord);
			}
		}
		return true;
	}
}

