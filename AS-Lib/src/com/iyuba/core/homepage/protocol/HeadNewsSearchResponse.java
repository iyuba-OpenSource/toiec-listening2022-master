package com.iyuba.core.homepage.protocol;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.headnewslib.model.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/13.
 */
public class HeadNewsSearchResponse extends BaseJSONResponse {
    public List<Article> mList = new ArrayList<>();
    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        JSONObject jsonObjectRoot;
        try {
            jsonObjectRoot = new JSONObject(bodyElement);
            JSONArray arr = new JSONArray(jsonObjectRoot.getString("data"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject temp = (JSONObject) arr.get(i);
                Article lwd = new Article();
                lwd.setTopicId(temp.getInt("TopicId"));
                lwd.setWordCount(temp.getInt("WordCount"));
                lwd.setDescCn(temp.getString("DescCn"));
                lwd.setFlag(temp.getInt("Flag"));
                lwd.setTitle_cn(temp.getString("Title_cn"));
                lwd.setHardWeight(temp.getDouble("HardWeight"));
                lwd.setTitle(temp.getString("Title"));
                lwd.setNewsId(temp.getInt("NewsId"));
                lwd.setCategory(temp.getInt("Category"));
                lwd.setCreatTime(temp.getString("CreatTime"));
                lwd.setPic(temp.getString("Pic"));
                lwd.setReadCount(temp.getInt("ReadCount"));
                lwd.setSound(temp.getString("Sound"));
                lwd.setSource(temp.getString("Source"));


                mList.add(lwd);
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
