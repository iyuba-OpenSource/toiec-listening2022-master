package com.iyuba.core.iyumooc.microclass.API;

import com.iyuba.core.iyumooc.microclass.bean.SlideShowListBean;
import com.iyuba.core.iyumooc.teacher.bean.QuestionListBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 作者：renzhy on 16/5/17 17:31
 * 邮箱：renzhongyigoo@gmail.com
 */
public interface SlidePicApiStores {
	@GET("dev/getScrollPicApi.jsp")
	Call<SlideShowListBean> getSlidePicList(@Query("type") String type);
}
