/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.protocol.news;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * 类名
 * 
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class ReadCountAddRequest extends BaseJSONRequest {

	public ReadCountAddRequest(String id) {
		setAbsoluteURI("http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/appApi/UnicomApi?protocol=70001&counts=1&format=json&appName=music&voaids="
				+ id);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ReadCountAddResponse();
	}

}
