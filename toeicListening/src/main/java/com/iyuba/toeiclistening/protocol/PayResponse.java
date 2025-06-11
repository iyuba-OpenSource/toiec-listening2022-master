package com.iyuba.toeiclistening.protocol;



import com.iyuba.toeiclistening.frame.protocol.BaseXMLResponse;
import com.iyuba.toeiclistening.frame.util.Utility;
import com.iyuba.toeiclistening.frame.util.kXMLElement;

/**
 * 
 * @author zqq
 * 
 *         鍔熻兘锛氭敮浠樺洖澶?飦?result 0鏄け璐ワ紝1 鏄垚鍔?飦?msg 涓烘娆℃搷浣滀俊鎭? 飦?amount
 *         鑻ヨ喘涔版垚鍔燂紝杩斿洖姝ょ敤鎴峰綋鍓嶏紙璐拱涔嬪悗锛夌殑amount鍊?
 */
public class PayResponse extends BaseXMLResponse {
	public String result;
	public String msg;
	public String amount;
	public String vipFlag;
	public String vipEndTime;

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		result = Utility.getSubTagContent(bodyElement, "result");
		msg = Utility.getSubTagContent(bodyElement, "msg");
		amount = Utility.getSubTagContent(bodyElement, "amount");
		vipFlag = Utility.getSubTagContent(bodyElement, "VipFlag");
		vipEndTime = Utility
				.getSubTagContent(bodyElement, "VipEndTime");
		
		return true;
	}

}
