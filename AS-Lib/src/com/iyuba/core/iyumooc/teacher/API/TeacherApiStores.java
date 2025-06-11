package com.iyuba.core.iyumooc.teacher.API;

import com.iyuba.core.iyumooc.teacher.bean.QuestionListBean;
import com.iyuba.core.util.LoginResponse;
import com.iyuba.core.util.ResponseFormat;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 作者：renzhy on 16/4/26 16:44
 * 邮箱：renzhongyigoo@gmail.com
 */
public interface TeacherApiStores {

	@GET("question/getQuestionList.jsp")
	Call<QuestionListBean> getQuesList(@Query("format") String format, @Query("type") int type,
									   @Query("category1") int category1, @Query("category2") int category2,
									   @Query("pageNum") int pageNum, @Query("isanswered") int isanswered );

	@GET("v2/api.iyuba")
	@ResponseFormat("json")
	Call<LoginResponse> login(@Query("protocol") String protocol,
							  @Query("username") String username,
							  @Query("password") String password,
							  @Query("x") String x,
							  @Query("y") String y,
							  @Query("appid") String appid,
							  @Query("sign") String sign,
							  @Query("format") String format);
}
