/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.VOABaseJsonRequest;
import com.iyuba.core.common.util.MD5;

/**
 * @author yao 请求通知 condition可取的参数 doing 心情评论 blog 日志评论 comment 评论回复 uid 留言板 pic
 *         图片评论 follow 关注通知 system 系统通知 invite 邀请通知 app 应用通知
 */
public class RequestNotificationInfo extends VOABaseJsonRequest {
	public static final String protocolCode = "61002";

	public RequestNotificationInfo(String uid, String pageNumber,
			String condition) {
		super(protocolCode);
		// TODO Auto-generated constructor stub
		setRequestParameter("uid", uid);
		setRequestParameter("pageNumber", pageNumber);
		setRequestParameter("pageCounts", "20");
		setRequestParameter("asc", "0");
		setRequestParameter("condition", condition);
		setRequestParameter("sign",
				MD5.getMD5ofStr(protocolCode + uid + "iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ResponseNotificationInfo();
	}

}
