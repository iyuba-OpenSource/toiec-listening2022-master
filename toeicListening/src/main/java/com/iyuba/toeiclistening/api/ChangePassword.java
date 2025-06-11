package com.iyuba.toeiclistening.api;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by iyuba on 2017/11/4.
 */

public interface ChangePassword {
    String CHANGE_URL_PASSWORD = "http://api." + com.iyuba.configation.Constant.IYBHttpHead2 + "/";
    String FORMAT = "json";

    @GET("v2/api.iyuba?protocol=10014")
    Call<ChangePasswordBean> changePassword(
            @Query("username") String username,
            @Query("password") String passsword,
            @Query("sign") String sign,
            @Query("userPhone") String userPhone,
            @Query("format") String format);
}
