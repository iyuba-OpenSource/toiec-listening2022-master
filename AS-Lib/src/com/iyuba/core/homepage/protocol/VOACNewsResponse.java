package com.iyuba.core.homepage.protocol;

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
public class VOACNewsResponse extends BaseJSONResponse {
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
                lwd.itemId = "1";
                lwd.id = Integer.parseInt(temp.getString("VoaId"));
                lwd.url = temp.getString("Url");
                lwd.picUrl = temp.getString("Pic");
                lwd.time = temp.getString("CreatTime");
                lwd.titleEn = temp.getString("Title");
                lwd.title = temp.getString("Title_cn");
                lwd.readTimes = temp.getString("ReadCount");
                lwd.content = temp.getString("DescCn");
                lwd.musicUrl = temp.getString("Sound");

                mList.add(lwd);
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
