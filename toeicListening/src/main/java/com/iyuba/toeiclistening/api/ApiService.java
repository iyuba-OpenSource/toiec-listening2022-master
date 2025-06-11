package com.iyuba.toeiclistening.api;


import com.iyuba.core.common.network.EvaSendBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by iyuba on 2017/8/21.
 */

public interface ApiService {

    @GET("v2/api.iyuba")
    public Call<TemporaryUserJson> getTemporaryAccount(@Query("protocol") int protocol,
                                                       @Query("deviceId") String deviceId,
                                                       @Query("platform") String platform,
                                                       @Query("appid") int appid,
                                                       @Query("format") String format,
                                                       @Query("sign") String sign);

    @FormUrlEncoded
    @POST
    Call<EvaSendBean> audioSendApi(@Url String url,
                                   @Field("topic") String topic,
                                   @Field("platform") String platform,
                                   @Field("format") String format,
                                   @Field("protocol") String protocol,
                                   @Field("userid") String userid,
                                   @Field("voaid") String voaid,
                                   @Field("score") String score,
                                   @Field("shuoshuotype") String shuoshuotype,
                                   @Field("content") String content,
                                   @Field("rewardVersion") int rewardVersion,
                                   @Field("appid") int appid);


}
