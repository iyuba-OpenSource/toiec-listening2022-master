package com.iyuba.toeiclistening.mvp.presenter;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iyuba.toeiclistening.ToeicApplication;
import com.iyuba.toeiclistening.mvp.model.TestDetailModel;
import com.iyuba.toeiclistening.mvp.model.bean.AdEntryBean;
import com.iyuba.toeiclistening.mvp.model.bean.PdfBean;
import com.iyuba.toeiclistening.mvp.model.bean.TestRecordBean;
import com.iyuba.toeiclistening.mvp.view.SplashContract;
import com.iyuba.toeiclistening.mvp.view.TestDetailContract;

import java.io.IOException;
import java.util.List;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class TestDetailPresenter extends BasePresenter<TestDetailContract.TestDetailView, TestDetailContract.TestDetailModel>
        implements TestDetailContract.TestDetailPresenter {


    @Override
    protected TestDetailContract.TestDetailModel initModel() {
        return new TestDetailModel();
    }

    @Override
    public void getToeicPdfFile(String voaid) {

        Disposable disposable = model.getToeicPdfFile(voaid, new TestDetailContract.Callback() {
            @Override
            public void success(PdfBean pdfBean) {

                view.getToeicPdfFile(pdfBean);
            }

            @Override
            public void error(Exception e) {

                view.toast("请求超时");
            }
        });
        addSubscribe(disposable);
    }

    @Override
    public void getAdEntryAll(String appId, int flag, String uid) {

        Disposable disposable = model.getAdEntryAll(appId, flag, uid, new SplashContract.Callback() {
            @Override
            public void success(List<AdEntryBean> adEntryBeans) {

                if (adEntryBeans.size() != 0) {

                    AdEntryBean adEntryBean = adEntryBeans.get(0);
                    if (adEntryBean.getResult().equals("1")) {

                        view.getAdEntryAllComplete(adEntryBean);
                    }
                }
            }

            @Override
            public void error(Exception e) {

                Log.d("splash", e.toString());
            }
        });
        addSubscribe(disposable);
    }

    @Override
    public void updateStudyRecordNew(String format, String uid, String BeginTime, String EndTime, String Lesson,
                                     String TestMode, String TestWords, String platform, String appName, String DeviceId,
                                     String LessonId, String sign, int EndFlg, int TestNumber, int rewardVersion) {

        Disposable disposable = model.updateStudyRecordNew(format, uid, BeginTime, EndTime, Lesson,
                TestMode, TestWords, platform, appName,
                DeviceId, LessonId, sign, EndFlg, TestNumber, rewardVersion, new TestDetailContract.UpdateStudyCallback() {
                    @Override
                    public void success(ResponseBody responseBody) {


                        TestRecordBean testRecordBean = null;
                        try {
                            testRecordBean = new Gson().fromJson(responseBody.string().trim(), TestRecordBean.class);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!testRecordBean.getReward().equals("0")) {

                            view.showUpdateStudyRecord(testRecordBean);
                        } else if (testRecordBean.getJiFen() != null) {

                            Toast.makeText(ToeicApplication.getApplication(), "获得" + testRecordBean.getJiFen() + "积分", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void error(Exception e) {


                        Log.d("error", e.toString());
                    }
                });
    }
}
