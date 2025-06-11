package com.iyuba.toeiclistening.mvp.model;

import com.iyuba.toeiclistening.Constant;
import com.iyuba.toeiclistening.mvp.model.bean.AdEntryBean;
import com.iyuba.toeiclistening.mvp.model.bean.PdfBean;
import com.iyuba.toeiclistening.mvp.view.SplashContract;
import com.iyuba.toeiclistening.mvp.view.TestDetailContract;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class TestDetailModel implements TestDetailContract.TestDetailModel {

    @Override
    public Disposable getAdEntryAll(String appId, int flag, String uid, SplashContract.Callback callback) {

        return NetWorkManager
                .getRequest()
                .getAdEntryAll(Constant.GET_AD_ENTRY_ALL,appId, flag, uid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AdEntryBean>>() {
                    @Override
                    public void accept(List<AdEntryBean> adEntryBeans) throws Exception {

                        callback.success(adEntryBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        callback.error((Exception) throwable);
                    }
                });
    }

    @Override
    public Disposable getToeicPdfFile(String voaid, TestDetailContract.Callback callback) {

        return NetWorkManager
                .getRequest()
                .getToeicPdfFile(Constant.GET_TOEIC_PDF_FILE, voaid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PdfBean>() {
                    @Override
                    public void accept(PdfBean pdfBean) throws Exception {

                        callback.success(pdfBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        callback.error((Exception) throwable);
                    }
                });
    }

    @Override
    public Disposable updateStudyRecordNew(String format, String uid, String BeginTime, String EndTime,
                                           String Lesson, String TestMode, String TestWords, String platform,
                                           String appName, String DeviceId, String LessonId, String sign,
                                           int EndFlg, int TestNumber, int rewardVersion, TestDetailContract.UpdateStudyCallback callback) {

        return NetWorkManager
                .getRequest()
                .updateStudyRecordNew(Constant.UPDATE_STUDY_RECORD_NEW, format, uid, BeginTime, EndTime,
                        Lesson, TestMode, TestWords, platform, appName, DeviceId, LessonId,
                        sign, EndFlg, TestNumber, rewardVersion)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {

                        callback.success(responseBody);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        callback.error((Exception) throwable);
                    }
                });
    }
}
