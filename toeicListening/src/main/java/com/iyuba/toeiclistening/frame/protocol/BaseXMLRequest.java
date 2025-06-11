package com.iyuba.toeiclistening.frame.protocol;

import java.io.IOException;
import java.io.OutputStream;

import com.iyuba.toeiclistening.frame.network.INetStateReceiver;
import com.iyuba.toeiclistening.frame.network.NetworkData;
import com.iyuba.toeiclistening.frame.util.KXmlSerializer;
import com.iyuba.toeiclistening.frame.util.XmlSerializer;

/**
 * XML鍗忚璇锋眰鍖呮娊璞″熀绫伙紝鎼缓璇锋眰鍖呯殑鍩烘湰楠ㄦ灦锛?瀹屾垚鍗忚鍏叡澶寸殑鏋勫缓锛屽苟瀵瑰鎻愪緵蹇呰
 * 鎺ュ彛鏂逛究璇锋眰鐨勫彂閫佸強鐩稿簲鍥炲鐨勫垱寤?
 * 
 * @author zhouyin
 * 
 */
public abstract class BaseXMLRequest extends BaseHttpRequest {

	public BaseXMLRequest() {
		if (NetworkData.sessionId != null && !NetworkData.sessionId.equals("")) {
			setAbsoluteURI(NetworkData.accessPoint + ";jsessionid="
					+ NetworkData.sessionId);
		} else {
			setAbsoluteURI(NetworkData.accessPoint);
		}
	}


	public void fillOutputStream(int cookie, OutputStream output,
			INetStateReceiver stateReceiver) throws IOException {
		XmlSerializer serializer = new KXmlSerializer();
		// serializer.setOutput(output, "utf-8");
		// serializer.startDocument(null, null);
		// serializer.startTag(null, "HPDSVRR");
		// serializer.attribute(null, "v", "1.0");
		// fillHeader(serializer);
		// serializer.startTag(null, "HdpRbody");
		// fillBody(serializer);
		// serializer.endTag(null, "HdpRbody");
		// serializer.endTag(null, "HPDSVRR");
		// serializer.endDocument();
	}

	@Override
	public String[][] getExtraHeaders() {
		String[][] aryHeaders = new String[1][2];
		aryHeaders[0][0] = "Content-Type";
		aryHeaders[0][1] = "text/html;charset=utf-8";
		return aryHeaders;
	}

	/**
	 * 璇锋眰鍖呬綋濉厖鎶借薄鎺ュ彛锛屽瓙绫诲疄鐜版鎺ュ彛瀹屾垚鍏蜂綋璇锋眰鍖呬綋鐨勬瀯寤?
	 * 
	 * @param serializer
	 *            锛歺ml娴佹瀯寤哄櫒
	 */
	protected abstract void fillBody(XmlSerializer serializer)
			throws IOException;

	private void fillHeader(XmlSerializer serializer) throws IOException {
		serializer.startTag(null, "HpdRheader");

		serializer.startTag(null, "svrRno");
		// serializer.attribute(null, "v", "1.0");
		serializer.attribute(null, "v", requestVersion);
		serializer.text(getRequestId());
		serializer.endTag(null, "svrRno");

		serializer.startTag(null, "sid");
		String sid = NetworkData.sessionId;
		serializer.text((sid == null) ? "" : sid);
		serializer.endTag(null, "sid");

		serializer.startTag(null, "id");
		// serializer.text(ClientSession.theClientSession.getUserName());
		serializer.text("");
		serializer.endTag(null, "id");

		serializer.startTag(null, "pd");
		// if (AccountManagerLib.Instance().currentUser != null) {
		// if (AccountManagerLib.Instance().currentUser.role == User.ROLE_GUEST) {
		// serializer.text("guest");
		// } else if (AccountManagerLib.Instance().currentUser.role ==
		// User.ROLE_USER) {
		// serializer.text("user");
		// }
		// } else {
		// serializer.text("guest");
		// }
		serializer.endTag(null, "pd");

		serializer.startTag(null, "fee");
		serializer.text("free");
		serializer.endTag(null, "fee");

		serializer.startTag(null, "sec");
		serializer.text("no");
		serializer.endTag(null, "sec");

		serializer.endTag(null, "HpdRheader");
	}

	// protected String requestId;

	protected String requestVersion = BaseProtocolDef.PROTOCOL_VERSION_1;

	// public void setVersion(String version) {
	// this.CurrentVersion = version;
	// }
}