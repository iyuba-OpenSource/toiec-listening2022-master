/**
 * 
 */
package com.iyuba.toeiclistening.protocol;

import com.iyuba.toeiclistening.frame.protocol.BaseXMLResponse;
import com.iyuba.toeiclistening.frame.util.Utility;
import com.iyuba.toeiclistening.frame.util.kXMLElement;

public class ResponseGetMessageCode extends BaseXMLResponse {

	public String result;
	public int res_code;
	public String userphone;
	public String identifier;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO Auto-generated method stub
		result=Utility.getSubTagContent(bodyElement, "result");
		if(!result.equals("-1")){
			res_code=Integer.parseInt(Utility.getSubTagContent(bodyElement, "res_code"));
			userphone=Utility.getSubTagContent(bodyElement, "userphone");
			identifier=Utility.getSubTagContent(bodyElement, "identifier");
		}
		return true;
	}

}
