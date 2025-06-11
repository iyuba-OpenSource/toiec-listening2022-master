package com.iyuba.core.common.manager;

import android.os.Handler;
import android.os.Message;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.Constant;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.entity.EvaluateBean;
import com.iyuba.module.toolbox.GsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReadEvaluatePost {

    //    private String actionUrl= WebConstant.HTTP_AI + Constant.IYBHttpHead+"/test/eval/";
//    private String actionUrl= WebConstant.HTTP_SPEECH_ALL +"/test/eval/";
    private String actionUrl = WebConstant.HTTP_SPEECH_ALL + "/test/ai/";


    public void post(Map<String, String> params, final String filePath, final Handler handler, final int position, final int senIndex) {
        LogUtil.e("录音 评测请求开始");
        handler.sendEmptyMessage(10);//评测中
        //POST参数构造MultipartBody.Builder，表单提交
        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
                connectTimeout(15, TimeUnit.SECONDS).
                readTimeout(15, TimeUnit.SECONDS).
                writeTimeout(15, TimeUnit.SECONDS)
//                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())//配置
//                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())//配置
                .build();
        //一：文本类的
        MultipartBody.Builder urlBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (params != null) {
            for (String key : params.keySet()) {
                if (params.get(key) != null) {
                    urlBuilder.addFormDataPart(key, params.get(key));
                }
            }
        }
        //二种：文件请求体
        MediaType type = MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
        File file1 = new File(filePath);
        RequestBody fileBody = RequestBody.create(type, file1);
        urlBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + filePath + "\""), fileBody);
  /*      RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("type", "bbc")
                // .addFormDataPart("userId", "0")
                .addFormDataPart("userId", )
                .addFormDataPart("newsId", String.valueOf())
                .addFormDataPart("paraId", data. v())
                .addFormDataPart("IdIndex", data.getIdIndex())
                .addFormDataPart("sentence", )
                .addFormDataPart("file", filePath, fileBody)
                .addFormDataPart("wordId", "0")
                .addFormDataPart("flg", "0")
                .addFormDataPart("appId", Constant.APPID)
                .build();*/
        // 构造Request->call->执行
        final Request request = new Request.Builder().headers(new Headers.Builder().build())//extraHeaders 是用户添加头
                .url(actionUrl).post(urlBuilder.build())//参数放在body体里
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("录音评测失败" + e);//每次出现

                Message msg = handler.obtainMessage();
                msg.what = 12;
                msg.arg1 = 404;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    JSONObject jsonObject;
                    try {
                        assert response.body() != null;
                        jsonObject = new JSONObject(response.body().string());
                        LogUtil.e("评测==返回数据：" + jsonObject);
                        JSONObject data = jsonObject.getJSONObject("data");
                        StringBuilder textScore = new StringBuilder();// = data.getString("words");
                        String result = jsonObject.getString("result");
                        //getJSONObject("result");
                        if (data != null && result.equals("1")) {
                            LogUtil.e("录音评测成功" + data);

                            deletePCM();
                            String resultStr = data.toString();
                            EvaluateBean evaluateBean = GsonUtils.toObject(resultStr, EvaluateBean.class);
                            evaluateBean.setIdIndex(params.get("IdIndex"));
                            evaluateBean.setParaId(params.get("paraId"));
                            evaluateBean.setVoaId(params.get("newsId"));
                            evaluateBean.setPosition(position);
                            evaluateBean.setnLocalMP3Path(filePath);

                            for (EvaluateBean.WordsBean bean : evaluateBean.getWords()) {
                                textScore.append(bean.getScore()).append("-");
                            }
                            evaluateBean.setTextScore(textScore.toString());

                            LogUtil.e("每个单词得分" + textScore);

                            Message msg = handler.obtainMessage();
                            msg.what = 6;
                            msg.arg1 = (int) (Double.valueOf(evaluateBean.getTotal_score()) * 20);//分数
                            msg.arg2 = senIndex;
                            msg.obj = evaluateBean;

                            handler.sendMessage(msg);
                        } else {
                            LogUtil.e("录音评测 接口返回错误");
                            handler.sendEmptyMessage(12);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(12);
                        LogUtil.e("录音评测失败" + e);
                    }

                } else {
                    handler.sendEmptyMessage(12);
                    LogUtil.e("录音评测失败isSuccessful" + response);
                }
            }
        });
    }

    private void deletePCM() {
        String filePath = Constant.getEvaluateAddress();
        File file = new File(filePath);

        if (file.isDirectory()) {//是文件夹 目录
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                String fPath = f.getPath();
                // LogUtil.e("文件后缀名" + fPath + " 文件" + fPath.substring(fPath.length() - 4));
                if ((fPath.substring(fPath.length() - 4)).equals(".pcm") || (fPath.substring(fPath.length() - 4)).equals(".wav")) {
                    //deleteFile(f);
                    f.delete();
                }
            }
            //file.delete();//如要保留文件夹，只删除文件，请注释这行
        }
    }

}
