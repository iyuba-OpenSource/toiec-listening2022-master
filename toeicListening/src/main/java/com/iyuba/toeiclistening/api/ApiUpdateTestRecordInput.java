package com.iyuba.toeiclistening.api;


import com.iyuba.core.common.network.EvaSendBean;
import com.iyuba.core.common.network.TestSendBean;


import java.util.Map;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import timber.log.Timber;

/**
 * Created by iyuba on 2017/8/21.
 */

public interface ApiUpdateTestRecordInput {

//    String ENDPOINT = "http://daxue.iyuba.cn/ecollege/";

    @POST("updateTestRecordInput.jsp")
    Call<TestSendBean> sendTestRecord(@Body RequestBody body);
//    Single<Object> sendTestRecord(@QueryMap Map<String, String> params, @Body RequestBody body);

}
