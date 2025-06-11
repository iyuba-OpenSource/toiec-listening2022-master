package com.iyuba.toeiclistening.frame.protocol;

import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.toeiclistening.frame.network.INetStateReceiver;
import com.iyuba.toeiclistening.frame.network.NetworkData;




public abstract class BaseJSONRequest extends BaseHttpRequest {

	public BaseJSONRequest() {
		if (NetworkData.sessionId != null && !NetworkData.sessionId.equals("")) {
			setAbsoluteURI(NetworkData.accessPoint + ";jsessionid="
					+ NetworkData.sessionId);
		} else {
			setAbsoluteURI(NetworkData.accessPoint);
		}
	}

	@Override
	public void fillOutputStream(int cookie, OutputStream output,
			INetStateReceiver stateReceiver) throws IOException {
	}

	@Override
	public String[][] getExtraHeaders() {
		String[][] aryHeaders = new String[1][2];
		aryHeaders[0][0] = "Content-Type";
		aryHeaders[0][1] = "application/json; charset=UTF-8";
		return aryHeaders;
	}

	/**
	 * 璇锋眰鍖呬綋濉厖鎶借薄鎺ュ彛锛屽瓙绫诲疄鐜版鎺ュ彛瀹屾垚鍏蜂綋璇锋眰鍖呬綋鐨勬瀯寤?
	 * 
	 * @param serializer
	 *            锛歺ml娴佹瀯寤哄櫒
	 */
	protected abstract void fillBody(JSONObject jsonObject)
			throws JSONException;

	@SuppressWarnings("unused")
	private void fillHeader(JSONObject jsonObject) throws JSONException {

	}

	protected String requestVersion = BaseProtocolDef.PROTOCOL_VERSION_1;

}
