/**
 * 
 */
package com.iyuba.core.common.protocol.friends;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.util.Constant;

/**
 * @author 请求周边的人
 */
public class RequestNearFriendsList extends BaseJSONRequest {
	public static final String protocolCode = "70002";

	public RequestNearFriendsList(String uid, int pageNumber, String x, String y) {
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol="
				+ protocolCode + "&uid=" + uid + "&pageCounts=" + 50
				+ "&pageNumber=" + pageNumber + "&x=" + x + "&y=" + y
				+ "&sign=" + MD5.getMD5ofStr(protocolCode + uid + "iyubaV2"));
		
		Log.d("RequestNearFriendsList:","http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol="
				+ protocolCode + "&uid=" + uid + "&pageCounts=" + 50
				+ "&pageNumber=" + pageNumber + "&x=" + x + "&y=" + y
				+ "&sign=" + MD5.getMD5ofStr(protocolCode + uid + "iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseNearFriendsList();
	}

}
