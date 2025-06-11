package com.iyuba.toeiclistening.api;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by iyuba on 2017/11/4.
 */

public interface YzPhoneNumberForget {
    String YZNUMBER_URL_FORGET = "http://api."+com.iyuba.configation.Constant.IYBHttpHead2+"/";
    String FORMAT = "json";
    @GET("v2/api.iyuba?protocol=10009")
    Call<YzPhoneResultForget> getYzPhoneNumberState(@Query("format") String format, @Query("username") String userphone);
}
