/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.util.Constant;

/**
 * @author 搜索好友
 */
public class RequestSearchList extends BaseJSONRequest {
	public static final String protocolCode = "52001";

	public RequestSearchList(String uid, String search, String type,
			String pageNumber) {
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol="
				+ protocolCode + "&uid=" + uid + "&search="
				+ URLEncoder.encode(search) + "&pageNumber=" + pageNumber
				+ "&type=" + type + "&pageNumber=" + pageNumber + "&sign="
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
		return new ResponseSearchList();
	}

}
