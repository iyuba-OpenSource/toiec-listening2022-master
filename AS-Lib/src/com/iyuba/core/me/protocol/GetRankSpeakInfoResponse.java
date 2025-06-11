package com.iyuba.core.me.protocol;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.me.sqlite.mode.RankBean;
import com.iyuba.headnewslib.util.GsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import com.iyuba.headnewslib.util.GsonUtils;

/**
 * Created by Administrator on 2017/1/3.
 */

public class GetRankSpeakInfoResponse extends BaseJSONResponse {


    public String myImgSrc = "";
    public String myId = "";
    public String myRanking = "";
    public String myName = "";
    public String result = "";
    public String message = "";

    public String myScores = "";//总分
    public String myCount= "";//总文章数

    public List<RankBean> rankBeans = new ArrayList<RankBean>();

    @Override
    protected boolean extractBody(JSONObject headerElement, String bodyElement) {
        Log.e("GetRank-口语排行榜", "===================="+bodyElement);
        try {
            JSONObject jsonRoot = new JSONObject(bodyElement);
            message = jsonRoot.getString("message");

            if (message.equals("Success")) {

                if (jsonRoot.has("myimgSrc"))
                    myImgSrc = jsonRoot.getString("myimgSrc");//---
                if (jsonRoot.has("myid")) //-----
                    myId = jsonRoot.getString("myid");
                if (jsonRoot.has("myranking"))
                    myRanking = jsonRoot.getString("myranking"); //---
                if (jsonRoot.has("result"))
                    result = jsonRoot.getString("result");//---
                if (jsonRoot.has("myname"))
                    myName = jsonRoot.getString("myname");

                if (jsonRoot.has("myscores"))
                    myScores = jsonRoot.getString("myscores"); //总分
                if (jsonRoot.has("mycount"))
                    myCount = jsonRoot.getString("mycount"); //总文章数



                rankBeans = GsonUtils.toObjectList(jsonRoot.getString("data"), RankBean.class);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
