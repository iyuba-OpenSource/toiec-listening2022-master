package com.iyuba.core.microclass.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * 反馈请求
 * 
 * @author chentong
 * 
 */
public class FeedBackJsonRequest extends BaseJSONRequest {
	private String email;
	private String uid;

	public FeedBackJsonRequest(String content, String email, String uid) {
		String para[] = content.split(" ");
		StringBuffer keyBuffer = new StringBuffer();
		for (int i = 0; i < para.length - 1; i++) {
			keyBuffer.append(para[i]).append("%20");
		}
		keyBuffer.append(para[para.length - 1]);
		content = keyBuffer.toString();
		if (uid != null && uid.length() != 0) {
			this.uid = uid;
		} else {
			this.uid = "";
		}
		if (email != null && email.length() != 0) {
			this.email = email;
		} else {
			this.email = "";
		}
		setAbsoluteURI(Constant.feedBackUrl + this.uid + "&content=" + content
				+ "&email=" + this.email);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new FeedBackJsonResponse();
	}

}
