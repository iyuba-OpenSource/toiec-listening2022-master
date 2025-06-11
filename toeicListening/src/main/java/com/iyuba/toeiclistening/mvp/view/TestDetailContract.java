package com.iyuba.toeiclistening.mvp.view;

import com.iyuba.toeiclistening.mvp.model.BaseModel;
import com.iyuba.toeiclistening.mvp.model.bean.AdEntryBean;
import com.iyuba.toeiclistening.mvp.model.bean.PdfBean;
import com.iyuba.toeiclistening.mvp.model.bean.TestRecordBean;
import com.iyuba.toeiclistening.mvp.presenter.IBasePresenter;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public interface TestDetailContract {


    interface TestDetailView extends LoadingView {

        void getToeicPdfFile(PdfBean pdfBean);

        void getAdEntryAllComplete(AdEntryBean adEntryBean);

        void showUpdateStudyRecord(TestRecordBean testRecordBean);
    }

    interface TestDetailPresenter extends IBasePresenter<TestDetailView> {

        void getToeicPdfFile(String voaid);

        void getAdEntryAll(String appId, int flag, String uid);


        void updateStudyRecordNew(String format, String uid,
                                  String BeginTime, String EndTime,
                                  String Lesson, String TestMode,
                                  String TestWords, String platform,
                                  String appName, String DeviceId,
                                  String LessonId, String sign, int EndFlg, int TestNumber, int rewardVersion);
    }


    interface TestDetailModel extends BaseModel {

        Disposable getAdEntryAll(String appId, int flag, String uid, SplashContract.Callback callback);

        Disposable getToeicPdfFile(String voaid, Callback callback);

        Disposable updateStudyRecordNew(String format, String uid,
                                        String BeginTime, String EndTime,
                                        String Lesson, String TestMode,
                                        String TestWords, String platform,
                                        String appName, String DeviceId,
                                        String LessonId, String sign, int EndFlg, int TestNumber, int rewardVersion, UpdateStudyCallback callback);
    }


    interface UpdateStudyCallback {

        void success(ResponseBody responseBody);

        void error(Exception e);

    }

    interface Callback {

        void success(PdfBean pdfBean);

        void error(Exception e);
    }
}
