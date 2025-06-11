package com.iyuba.headnewslib.protocol;

import android.util.Log;

import com.iyuba.headnewslib.util.Constant;
import com.iyuba.http.BaseHttpResponse;
import com.iyuba.http.toolbox.BaseJSONRequest;

public class GetArticleDetailRequest extends BaseJSONRequest {
	private static final String TAG = GetArticleDetailRequest.class.getSimpleName();
	private static final String DETAIL_URL = "http://cms."+Constant.IYBHttpHead+"/cmsApi/getText.jsp?";

	public GetArticleDetailRequest(int articleId) {
		StringBuilder sb = new StringBuilder(DETAIL_URL);
		sb.append("format=json").append("&NewsId=" + articleId);
		setAbsoluteURI(sb.toString());
		setMethod(Method.GET);
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new GetArticleDetailResponse();
	}

}
