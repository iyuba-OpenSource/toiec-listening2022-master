package com.iyuba.toeiclistening.activity.test;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.Constant;
import com.iyuba.module.toolbox.GsonUtils;
import com.iyuba.core.entity.EvaluateBean;

import org.apache.commons.cli.ParseException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
//import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 新的评测管理，需要自己录音
 * 12.21  ZH
 */
public class ReadEvaluateManager {
    private Context mContext;
    private static ReadEvaluateManager instance;
    private AudioRecord mAudioRecord;
    private int mRecorderBufferSize;
    private byte[] mAudioData;

    /*默认数据*/
    private int mSampleRateInHZ = 8000; //采样率
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;  //位数
    private int mChannelConfig = AudioFormat.CHANNEL_IN_MONO;   //声道 单声道


    public boolean isRecording = false; //是否录音中，默认否
    private String mTmpFileAbs = "";
    private String mWavPath = "";
    public String mMp3Path;
    private Map<String, String> maps;
    public Handler mHandler;
    public boolean isEvaluating;

    private long startDate, endDate;

    private int senIndex;
    public int position;

    private ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    public ReadEvaluateManager(Context context) {
        this.mContext = context;
    }

    public static ReadEvaluateManager getInstance(Context context) {
        if (instance == null) {
            instance = new ReadEvaluateManager(context);
        }
        return instance;
    }

    private void initRecord() {
        mRecorderBufferSize = AudioRecord.getMinBufferSize(mSampleRateInHZ, mChannelConfig, mAudioFormat);
        mAudioData = new byte[320];
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, mSampleRateInHZ,
                mChannelConfig, mAudioFormat, mRecorderBufferSize);
    }

    //开始评测
    public void startEvaluate(int clickPosition, int senIndex, Map<String, String> params, final Handler handler) throws ParseException {
        this.senIndex = senIndex;
        maps = params;
        mHandler = handler;
        position = clickPosition;
        isRecording = true;//录音为开启状态
        startDate = new Date().getTime();//开始讲话
        initRecord();
        isEvaluating =true;
        if (isRecording) {
            LogUtil.e("录音 已开始");
        }
        //拼接的录音地址
        String tmpName = Constant.getEvaluateAddress() +"/"+ senIndex + clickPosition;
        makeRootDirectory(Constant.envir);//生成文件夹
        makeRootDirectory(Constant.getEvaluateAddress());//先生成文件夹

        final File tmpFile = makeFilePath(tmpName + ".pcm");//不能越级创建文件
        final File tmpOutFile =  makeFilePath(tmpName + ".wav");
        mTmpFileAbs = tmpFile.getAbsolutePath();
        mWavPath = tmpOutFile.getAbsolutePath();
        //LogUtil.e("录音 pcm文件地址：" + mTmpFileAbs);
        //LogUtil.e("录音 wav文件地址：" + mWavPath);

        mAudioRecord.startRecording();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int time = 0;
                try {
                    FileOutputStream outputStream = new FileOutputStream(tmpFile.getAbsoluteFile());
                    while (isRecording) {
                        int readSize = 0;

                        //read 方法不调用不行 调用2次不行
                        int r =mAudioRecord.read(mAudioData, 0, mAudioData.length);
                        //从内存获取数据
                        outputStream.write(mAudioData);

                        int v = getVolume(r, mAudioData);
//
                        Message msg = mHandler.obtainMessage();
                        msg.what = 104;
                        msg.arg1 = v;
                        mHandler.sendMessage(msg);

                        LogUtil.e("run: ------>" + readSize + "分贝" + v);
                        time = 0;
                    }
                    LogUtil.e("run: ------>" + time);

                    outputStream.close();
                    pcmToWave(tmpFile.getAbsolutePath(), tmpOutFile.getAbsolutePath());

                    //convertAudio(tmpOutFile);//转化为mp3文件

                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.e("录音异常"+e);
                }
            }
        });
    }

    public void stopEvaluate() {
        if (!isRecording) {
            LogUtil.e("已结束");
            return;
        }
        endDate = new Date().getTime();
        //结束讲话
        isRecording = false;
        mAudioRecord.stop();
    }

    //评测取消
    public void cancelEvaluate(boolean cancel) {
        stopEvaluate();
    }

    public void post(String actionUrl, Map<String, String> params, final String filePath, final Handler handler) {
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

                isEvaluating =false;
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
                        StringBuilder textScore= new StringBuilder();// = data.getString("words");
                        String result = jsonObject.getString("result");
                        //getJSONObject("result");
                        if (data != null && result.equals("1")) {
                            LogUtil.e("录音评测成功" + data);

                            deletePCM();

                            String resultStr = data.toString();
                            EvaluateBean evaluateBean = GsonUtils.toObject(resultStr, EvaluateBean.class);
                            evaluateBean.setPosition(position);
                            evaluateBean.setnLocalMP3Path(filePath);

                            for (EvaluateBean.WordsBean bean:evaluateBean.getWords()){
                                textScore.append(bean.getScore()).append("-");
                            }
                            evaluateBean.setTextScore(textScore.toString());

                            LogUtil.e("每个单词得分"+textScore);


                            Message msg = handler.obtainMessage();
                            msg.what = 6;
                            msg.arg1 = (int) (Double.valueOf(evaluateBean.getTotal_score()) * 20);//分数
                            msg.arg2 = senIndex;
                            msg.obj = evaluateBean;

                            handler.sendMessage(msg);
                            isEvaluating =false;

                        } else {
                            LogUtil.e("录音评测 接口返回错误");
                            handler.sendEmptyMessage(12);
                            isEvaluating =false;


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(12);
                        isEvaluating =false;
                        LogUtil.e("录音评测失败" + e);
                    }

                } else {
                    handler.sendEmptyMessage(12);
                    isEvaluating =false;
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

    // 生成文件
    private File makeFilePath(String fileName) {
        File file = null;
        try {
            file = new File( fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("录音文件error:"+e);
        }
        return file;
    }

    // 生成文件夹
    private  void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            LogUtil.i("录音生成文件夹error:"+ e);
        }
    }

    private void pcmToWave(String inFileName, String outFileName) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long longSampleRate = mSampleRateInHZ;
        long totalDataLen = totalAudioLen + 36;
        int channels = 1;//你录制是单声道就是1 双声道就是2（如果错了声音可能会急促等）
        long byteRate = 16 * longSampleRate * channels / 8;

        byte[] data = new byte[mRecorderBufferSize];
        try {
            in = new FileInputStream(inFileName);
            out = new FileOutputStream(outFileName);

            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            writeWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 获取录音的声音分贝值
     *
     * @return
     */
    private int getVolume(int r, byte[] bytes_pkg) {
        //way 1
        int v = 0;
        //将 buffer 内容取出，进行平方和运算
        for (byte aBytes_pkg : bytes_pkg) {
            // 这里没有做运算的优化，为了更加清晰的展示代码
            v += aBytes_pkg * aBytes_pkg;
        }
        //平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
        int volume = (int) (v / (float) r);
        return volume;
    }



    /*
   任何一种文件在头部添加相应的头文件才能够确定的表示这种文件的格式，wave是RIFF文件结构，每一部分为一个chunk，其中有RIFF WAVE chunk，
   FMT Chunk，Fact chunk,Data chunk,其中Fact chunk是可以选择的，
    */
    private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
                                     int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (1 * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

//    private void convertAudio(File file) {
//        /**
//         *  Update with a valid audio file!
//         *  Supported formats: {@link AndroidAudioConverter.AudioFormat}
//         */
//        IConvertCallback callback = new IConvertCallback() {
//            @Override
//            public void onSuccess(File convertedFile) {
//                LogUtil.e("录音 SUCCESS: " + convertedFile.getPath());
//                mMp3Path = convertedFile.getPath();
//                String path = "http://ai."+Constant.IYBHttpHead+"/test/eval/";//:9001
//                //String path = "https://speech." + Constant.IYBHttpHead + "/test/eval/";//爱语吧评测接口请求头
//
//                try {
//                    post(path, maps, convertedFile.getPath(), mHandler);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    LogUtil.e("评测请求失败" + e);
//                }
//                //停止录制
//                try {
//                    // 防止某些手机崩溃，例如联想
//                    mAudioRecord.stop();
//                    // 彻底释放资源
//                    mAudioRecord.release();
//                    mAudioRecord = null;
//                } catch (IllegalStateException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Exception error) {
//                LogUtil.e("录音 ERROR: " + error.getMessage());
//
//            }
//        };
//        LogUtil.e("录音 Converting audio file...");
//        AndroidAudioConverter.with(mContext)
//                .setFile(file)
//                .setFormat(cafe.adriel.androidaudioconverter.model.AudioFormat.MP3)
//                .setCallback(callback)
//                .convert();
//    }

}
