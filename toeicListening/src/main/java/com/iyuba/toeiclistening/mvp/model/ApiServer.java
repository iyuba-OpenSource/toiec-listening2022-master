package com.iyuba.toeiclistening.mvp.model;


import com.iyuba.toeiclistening.mvp.model.bean.AdEntryBean;
import com.iyuba.toeiclistening.mvp.model.bean.PdfBean;
import com.iyuba.toeiclistening.mvp.model.bean.RewardBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiServer {


    /**
     * 获取pdf
     *
     * @param url
     * @param voaid
     * @return
     */
    @GET
    Observable<PdfBean> getToeicPdfFile(@Url String url, @Query("voaid") String voaid);


    /**
     * 获取广告
     *
     * @param appId
     * @param flag  2 广告顺序  5自家广告内容
     * @param uid
     * @return
     */
    @GET
    Observable<List<AdEntryBean>> getAdEntryAll(@Url String url, @Query("appId") String appId, @Query("flag") int flag, @Query("uid") String uid);


    /**
     * 上传学习记录（语音）
     *
     * @param format
     * @param uid
     * @param BeginTime
     * @param EndTime
     * @param Lesson
     * @param TestMode
     * @param TestWords
     * @param platform
     * @param appId
     * @param DeviceId
     * @param LessonId
     * @param sign
     * @return
     */
    @GET
    Observable<ResponseBody> updateStudyRecordNew(@Url String url, @Query("format") String format, @Query("uid") String uid,
                                                  @Query("BeginTime") String BeginTime, @Query("EndTime") String EndTime,
                                                  @Query("Lesson") String Lesson, @Query("TestMode") String TestMode,
                                                  @Query("TestWords") String TestWords, @Query("platform") String platform,
                                                  @Query("appId") String appId, @Query("DeviceId") String DeviceId,
                                                  @Query("LessonId") String LessonId, @Query("sign") String sign,
                                                  @Query("EndFlg") int EndFlg, @Query("TestNumber") int TestNumber,
                                                  @Query("rewardVersion") int rewardVersion);



    /**
     * 我的钱包
     *
     * @param uid
     * @param pages
     * @param pageCount
     * @param sign
     * @return
     */
    @GET
    Observable<RewardBean> getUserActionRecord(@Url String url, @Query("uid") int uid, @Query("pages") int pages, @Query("pageCount") int pageCount, @Query("sign") String sign);


}
