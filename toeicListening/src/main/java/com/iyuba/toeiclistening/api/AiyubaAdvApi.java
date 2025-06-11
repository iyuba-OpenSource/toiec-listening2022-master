package com.iyuba.toeiclistening.api;



import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by iyuba on 2017/9/2.
 */

public interface AiyubaAdvApi {

    String BASEURL = "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/dev/";
    String FLAG = "4";
    String KPFLAG = "1";
    @GET("getAdEntryAll.jsp")
    Call<List<AiyubaAdvResult>> getAdvByaiyuba(@Query("uid") String uid, @Query("appId") String appid,
                                               @Query("flag") String flag);

}
