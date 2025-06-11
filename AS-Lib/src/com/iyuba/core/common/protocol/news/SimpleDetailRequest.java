package com.iyuba.core.common.protocol.news;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * 获取最新新闻索引
 * 
 * @author chentong
 * 
 */
public class SimpleDetailRequest extends BaseJSONRequest {
	String format = "json"; // 可选，默认为json格式
	String parentID; // 可选，用于获取分类下的索引

	public SimpleDetailRequest(String url) {
		setAbsoluteURI(url);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new SimpleDetailResponse();
	}

}
