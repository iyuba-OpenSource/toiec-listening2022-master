package com.iyuba.headnewslib.protocol;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.headnewslib.model.Article;
import com.iyuba.headnewslib.util.GsonUtils;
import com.iyuba.http.toolbox.BaseJSONResponse;

public class GetArticlesResponse extends BaseJSONResponse {
	private static final String TAG = GetArticlesResponse.class.getSimpleName();

	public String result = "";
	public String total = "0";
	public List<Article> articles = new ArrayList<Article>();

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		try {
			JSONObject jsonRoot = new JSONObject(bodyElement);
			result = jsonRoot.getString("result");
			total = jsonRoot.getString("total");
			if (hasLegalData()) {
				articles = GsonUtils.toObjectList(jsonRoot.getString("data"), Article.class);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean hasLegalData() {
		return result.equals("1") && (Integer.parseInt(total) > 0);
	}

}
