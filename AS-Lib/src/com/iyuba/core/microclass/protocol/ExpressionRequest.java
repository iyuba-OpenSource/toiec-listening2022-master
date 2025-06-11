package com.iyuba.core.microclass.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;

/**
 * 发送发表评论请求
 * 
 * @author ct
 * 
 */

public class ExpressionRequest extends BaseJSONRequest {
	String PackId = "0";
	String star = "8";

	/**
	 * @param userid
	 * @param bbcid
	 * @param content
	 * 
	 *            用于发表文字评论
	 */
	public ExpressionRequest(String userid, String packid, String content,String Star) {
		this.PackId = packid;
		this.star = Star;

		setAbsoluteURI("http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/appApi/UnicomApi?platform=android&format=json&protocol=60002&shuoshuotype=0&appName=microclass&userid="
				+ userid
				+ "&voaid="
				+ PackId
				+ "&content="
				+ TextAttr.encode(TextAttr.encode(content))
				+ "&star="
				+ star);
		
		Log.d("ExpressionRequest:", "http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/appApi/UnicomApi?platform=android&format=json&protocol=60002&shuoshuotype=0&appName=microclass&userid="
				+ userid
				+ "&voaid="
				+ PackId
				+ "&content="
				+ TextAttr.encode(TextAttr.encode(content))
				+ "&star="
				+ star);

	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ExpressionResponse();
	}
}
