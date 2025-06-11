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
 * @author yao
 * 
 */
public class RequestSetMessageLetterRead extends BaseJSONRequest {
	public static final String protocolCode = "60003";

	public RequestSetMessageLetterRead(String uid, String plid) {
		// super(protocolCode);
		// TODO Auto-generated constructor stub

		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol="
				+ protocolCode + "&uid=" + uid + "&plid=" + plid
				// + "&pageNumber=" + 50
				+ "&sign="
				+ MD5.getMD5ofStr(protocolCode + uid + plid + "iyubaV2"));
		/*
		 * MD5 m=new MD5(); setRequestParameter("uid", uid);
		 * setRequestParameter("plid", plid);
		 * setRequestParameter("sign",MD5.md5(protocolCode+uid+plid+"iyubaV2")
		 * );
		 */
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		// return null;
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseSetMessageLetterRead();
	}

}
