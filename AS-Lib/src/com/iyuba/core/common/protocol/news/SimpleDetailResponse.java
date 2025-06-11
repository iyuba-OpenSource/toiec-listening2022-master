package com.iyuba.core.common.protocol.news;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.common.sqlite.mode.CommonNewsDetail;

public class SimpleDetailResponse extends BaseJSONResponse {
	public List<CommonNewsDetail> voaDetailsTemps = new ArrayList<CommonNewsDetail>();

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			JSONArray JsonArrayData = jsonObjectRoot.getJSONArray("data");
			if (JsonArrayData != null) {
				int size = JsonArrayData.length();
				JSONObject jsonObjectData;
				CommonNewsDetail voaDetailTemp;
				for (int i = 0; i < size; i++) {
					jsonObjectData = JsonArrayData.getJSONObject(i);
					voaDetailTemp = new CommonNewsDetail();
					try {
						voaDetailTemp.startTime = Double
								.parseDouble(jsonObjectData.getString("Timing"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						voaDetailTemp.endTime = Double
								.parseDouble(jsonObjectData
										.getString("EndTiming"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						voaDetailTemp.sentence_cn = jsonObjectData
								.getString("sentence_cn");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						voaDetailTemp.paraid = jsonObjectData
								.getString("ParaId");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						voaDetailTemp.idindex = jsonObjectData
								.getString("IdIndex");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						voaDetailTemp.sentence = jsonObjectData
								.getString("Sentence");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					voaDetailsTemps.add(voaDetailTemp);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
