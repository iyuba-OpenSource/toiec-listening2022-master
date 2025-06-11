package com.iyuba.toeiclistening.frame.protocol;

public class BaseProtocolDef {

	public static boolean protocolTest = true;

	// xml鍗忚璧勬簮璇锋眰缁濆鍦板潃(鍒濆)
	private static String xmlAbsoluteURI_final = "http://apps."+com.iyuba.core.util.Constant.IYBHttpHead+"/voa/";

	private static String xmlAbsoluteURI_test = "http://apps."+com.iyuba.core.util.Constant.IYBHttpHead+"/voa/";

	public static final String PROTOCOL_VERSION_1 = "1.0";

	public static final String PROTOCOL_VERSION_2 = "2.0";
	
	// 鏁版嵁婧愶紝0锛歮apabc(楂樺痉); 1: mapbar(鍥惧惂)
	public static int dataSource = 0;
	

	// 榛樿娓稿鐢ㄦ埛鍚?
	public static final String defGuestName = "guest";

	// 榛樿娓稿瀵嗙爜
	public static final String defGuestPassword = "guest";

	public static String getXmlAbsoluteURI() {
		if (protocolTest) {
			return xmlAbsoluteURI_test;
		} else {
			return xmlAbsoluteURI_final;
		}

	}

}
