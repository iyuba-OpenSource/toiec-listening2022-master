package com.iyuba.toeiclistening.api;


import com.iyuba.core.common.network.TestSendBean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by iyuba on 2017/8/21.
 */

public interface ApiUpdateWordsRecordInput {

//    String ENDPOINT = "http://daxue.iyuba.cn/ecollege/";

    @POST("updateExamRecord.jsp")
    Call<TestSendBean> postWordsRecord(@Body RequestBody body);
//    Single<Object> sendTestRecord(@QueryMap Map<String, String> params, @Body RequestBody body);

}
