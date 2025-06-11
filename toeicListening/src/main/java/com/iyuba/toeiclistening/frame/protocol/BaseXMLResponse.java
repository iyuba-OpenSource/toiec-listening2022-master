package com.iyuba.toeiclistening.frame.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.iyuba.toeiclistening.frame.network.INetStateReceiver;
import com.iyuba.toeiclistening.frame.network.NetworkData;
import com.iyuba.toeiclistening.frame.util.Utility;
import com.iyuba.toeiclistening.frame.util.kXMLElement;
import com.iyuba.toeiclistening.frame.util.kXMLParseException;




/**
 * XML鍗忚鍥炲鍖呮娊璞″熀绫伙紝瀛愮被缁ф壙姝ょ被瀹屾垚鍥炲鏋勫缓
 * 
 * @author zhouyin
 * 
 */
public abstract class BaseXMLResponse implements BaseHttpResponse {

	private InputStream inputStream = null;

	public ErrorResponse parseInputStream(int rspCookie,
			BaseHttpRequest request, InputStream inputStream, int len,
			INetStateReceiver stateReceiver) throws IOException {

		this.inputStream = inputStream;

		kXMLElement doc = parseResponse(inputStream);

//		// protocol parse error?
//		if (doc == null) {
//			return new ErrorResponse(ErrorResponse.ERROR_PROTOCOL);
//		}
//
//		// validate check fail?
//		ErrorResponse errorResponse = null;
//		errorResponse = validateCheck(doc, ((BaseXMLRequest) request)
//				.getRequestId());
//		if (errorResponse != null) {
//			return errorResponse;
//		}
//
//		// derived class parses concrete xml protocol
//		kXMLElement headerElement = Utility.getChildByName(doc, "HpdAheader");
//		kXMLElement bodyElement = Utility.getChildByName(doc, "HdpAbody");
		if (!extractBody(null, doc)) {
			return new ErrorResponse(ErrorResponse.ERROR_PROTOCOL);
		}

		// 閲婃斁鍐呭瓨
		doc = null;
		// parse successfully
		return null;
	}

	/**
	 * get inputstream
	 * 
	 * @return InputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	public boolean isAllowCloseInputStream() {
		return true;
	}

	/**
	 * 鍗忚鍥炲鍐呭鎻愬彇鎶借薄鎺ュ彛
	 * 
	 * @param headerElement
	 * @param bodyElement
	 * @return true--success, false--fail
	 */
	protected abstract boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement);

	private kXMLElement parseResponse(InputStream response) throws IOException {
		kXMLElement doc = new kXMLElement();
		try {
			doc.parseFromReader(new InputStreamReader(response, "UTF-8"));
			return doc;
		} catch (kXMLParseException e) {
			return null;
		}
	}

	private ErrorResponse validateCheck(kXMLElement doc, String requestId) {

		// root tag must be 'HPDSVRA'
		if ((doc.getTagName() != null) && !doc.getTagName().equals("HPDSVRA")) {
			return new ErrorResponse(ErrorResponse.ERROR_PROTOCOL);
		}

		// must contain 'HdpAbody' tag
		kXMLElement bodyElement = Utility.getChildByName(doc, "HdpAbody");
		if (bodyElement == null) {
			return new ErrorResponse(ErrorResponse.ERROR_PROTOCOL);
		}

		// must contain 'HpdAheader' tag
		kXMLElement headerElement = Utility.getChildByName(doc, "HpdAheader");
		if (headerElement == null) {
			return new ErrorResponse(ErrorResponse.ERROR_PROTOCOL);
		}

		// must contain the follow five specified tags
		String[] aryHeaders = { "svrAno", "svrRP", "sid", "stm", "sec" };
		for (int i = 0; i < aryHeaders.length; i++) {
			kXMLElement childElement = Utility.getChildByName(headerElement,
					aryHeaders[i]);

			if (childElement == null) {
				return new ErrorResponse(ErrorResponse.ERROR_PROTOCOL);
			}

			// response id must be equal with request id
			if (aryHeaders[i].equals("svrAno")
					&& ((childElement.getContents() == null) || !childElement
							.getContents().equals(requestId))) {
				return new ErrorResponse(ErrorResponse.ERROR_PROTOCOL);
			}
			// set current sessionID
			if (aryHeaders[i].equals("sid")) {
				NetworkData.sessionId = childElement.getContents();
			}

			// value of 'svrRP' tag must be 'OK', 'NULL' ,'ERR' or 'INVALID'
			if (aryHeaders[i].equals("svrRP")) {
				if (childElement.getContents() == null) {
					return new ErrorResponse(ErrorResponse.ERROR_PROTOCOL);
				}
				// if (childElement.getContents().equals("NULL")) {
				// return new ErrorResponse(ErrorResponse.ERROR_NULL_RESULT);
				// }
				if (childElement.getContents().equals("ERR")) {
					if (Utility.getChildByName(bodyElement, "Error") != null) {
						return new ErrorResponse(Integer.valueOf(Utility
								.getChildByName(bodyElement, "Error")
								.getProperty("ErrCode")), Utility
								.getSubTagContent(Utility.getChildByName(
										bodyElement, "Error"), "error"));
					} else {
						return new ErrorResponse(ErrorResponse.ERROR_SERVER);
					}
				}
				if (childElement.getContents().equals("INVALID")) {
					if (Utility.getChildByName(bodyElement, "Error") != null) {
						return new ErrorResponse(
								ErrorResponse.ERROR_INVALID_RESULT, Utility
										.getSubTagContent(Utility
												.getChildByName(bodyElement,
														"Error"), "error"));
					} else {
						return new ErrorResponse(ErrorResponse.ERROR_SERVER);
					}
				}
				if (!childElement.getContents().equals("OK")&&!childElement.getContents().equals("NULL")) {
					return new ErrorResponse(ErrorResponse.ERROR_PROTOCOL);
				}
			}
		}

		// pass validate check
		return null;
	}
}