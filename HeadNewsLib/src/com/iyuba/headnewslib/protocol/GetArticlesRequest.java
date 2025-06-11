package com.iyuba.headnewslib.protocol;

import com.iyuba.headnewslib.util.Constant;
import com.iyuba.http.BaseHttpResponse;
import com.iyuba.http.toolbox.BaseJSONRequest;

public class GetArticlesRequest extends BaseJSONRequest {
	private static final String TAG = GetArticlesRequest.class.getSimpleName();
	private static final String TITLE_URL = "http://cms."+Constant.IYBHttpHead+"/cmsApi/getNewsList.jsp?";

	public GetArticlesRequest(int count, int categoryId, int maxId) {
		this(count, categoryId, maxId, 1);
	}
	
	public GetArticlesRequest(int count, int categoryId, int maxId, int page){
		StringBuilder sb = new StringBuilder(TITLE_URL);
		sb.append("pageCounts=" + count).append("&pageNum=" + page).append("&maxId=" + maxId);
		if (categoryId != 119)
			sb.append("&categoryId=" + categoryId);
		setAbsoluteURI(sb.toString());
		setMethod(Method.GET);
	}

	@Override
	public BaseHttpResponse createResponse() {
		return new GetArticlesResponse();
	}

}
