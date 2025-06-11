/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.util.Constant;

/**
 * @author
 * 
 */
public class RequestMessageLetterContentList extends BaseJSONRequest {
	public static final String protocolCode = "60004";

	public RequestMessageLetterContentList(String uid, String friendid, int page) {
		// super(protocolCode);
		// TODO Auto-generated constructor stub
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol="
				+ protocolCode + "&uid=" + uid + "&friendid=" + friendid
				+ "&pageNumber=" + page + "&pageCounts=20&asc=0&sign="
				+ MD5.getMD5ofStr(protocolCode + uid + "iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		// return null;
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseMessageLetterContentList();
	}

}
