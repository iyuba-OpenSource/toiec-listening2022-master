package com.iyuba.headnewslib.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.headnewslib.model.ArticleDetail;
import com.iyuba.headnewslib.util.GsonUtils;
import com.iyuba.http.toolbox.BaseJSONResponse;

public class GetArticleDetailResponse extends BaseJSONResponse {
	private static final String TAG = GetArticleDetailResponse.class.getSimpleName();

	public String result;
	public String total;
	public List<ArticleDetail> details;

	public GetArticleDetailResponse() {
		result = "";
		total = "";
		details = new ArrayList<ArticleDetail>();
	}

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		try {
			JSONObject jsonRoot = new JSONObject(bodyElement);
			result = jsonRoot.getString("result");
			total = jsonRoot.getString("total");
			if (isResponseSuccess()) {
				details = GsonUtils.toObjectList(jsonRoot.getString("data"), ArticleDetail.class);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean isResponseSuccess() {
		return result.equals("1");
	}

}
