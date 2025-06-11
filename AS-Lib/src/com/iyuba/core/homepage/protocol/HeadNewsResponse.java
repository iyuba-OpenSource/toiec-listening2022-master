package com.iyuba.core.homepage.protocol;

import com.iyuba.configation.WebConstant;
import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.homepage.entity.NewsInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/10.
 */
public class HeadNewsResponse extends BaseJSONResponse {
    public List<NewsInfo> mList = new ArrayList<NewsInfo>();
    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        JSONObject jsonObjectRoot;
        try {
            jsonObjectRoot = new JSONObject(bodyElement);
            JSONArray arr = new JSONArray(jsonObjectRoot.getString("data"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject temp = (JSONObject) arr.get(i);
                NewsInfo lwd = new NewsInfo();
                lwd.itemId = "4";
                lwd.id = Integer.parseInt(temp.getString("NewsId"));
                lwd.picUrl = WebConstant.HTTP_STATIC+com.iyuba.core.util.Constant.IYBHttpHead+"/cms/news/image/"+temp.getString("Pic");
                lwd.time = temp.getString("CreatTime");
                lwd.titleEn = temp.getString("Title");
                lwd.title = temp.getString("Title_cn");
                lwd.readTimes = temp.getString("ReadCount");
                lwd.source = temp.getString("Source");
                lwd.content = temp.getString("DescCn");
                lwd.category = temp.getString("Category");
                lwd.topicId = temp.getString("TopicId");
                lwd.flag = temp.getString("Flag");
                lwd.wordCount = temp.getString("WordCount");
                lwd.hardWeight = temp.getString("HardWeight");
                try {
                    lwd.musicUrl = temp.getString("Sound");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                mList.add(lwd);
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
