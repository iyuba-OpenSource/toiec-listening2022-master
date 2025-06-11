package com.iyuba.toeiclistening.api;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by iyuba on 2017/11/4.
 */

public interface YzPhoneNumber {
    String YZNUMBER_URL = "http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/";
    String FORMAT = "json";
    @GET("sendMessage3.jsp")
    Call<YzPhoneResult> getYzPhoneNumberState(@Query("format") String format, @Query("userphone") String userphone);
}
