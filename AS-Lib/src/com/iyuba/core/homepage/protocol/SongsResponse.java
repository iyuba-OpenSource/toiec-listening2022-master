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
public class SongsResponse extends BaseJSONResponse {
    public List<NewsInfo> mList = new ArrayList<>();
    @Override
    protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
        JSONObject jsonObjectRoot;
        try {
            jsonObjectRoot = new JSONObject(bodyElement);
            JSONArray arr = new JSONArray(jsonObjectRoot.getString("data"));
            for (int i = 0; i < arr.length(); i++) {
                JSONObject temp = (JSONObject) arr.get(i);
                NewsInfo lwd = new NewsInfo();
                lwd.itemId = "3";
                lwd.id = Integer.parseInt(temp.getString("SongId"));
                lwd.picUrl = WebConstant.HTTP_STATIC+com.iyuba.core.util.Constant.IYBHttpHead+"/images/song/"+temp.getString("Pic");
                lwd.time = temp.getString("CreatTime");
                lwd.titleEn = temp.getString("Title");
                lwd.singer = temp.getString("Singer");
                lwd.readTimes = temp.getString("ReadCount");
                lwd.announcer = temp.getString("Announcer");
                lwd.title = "歌手 "+temp.getString("Singer")+"\n"+"播音 "+temp.getString("Announcer");
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
