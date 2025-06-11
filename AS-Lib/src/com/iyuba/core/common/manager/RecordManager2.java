package com.iyuba.core.common.manager;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.Constant;

import java.io.File;
import java.io.IOException;

public class RecordManager2 {
    private MediaRecorder mMediaRecorder;
    private static final int MAX_LENGTH = 1000 * 60 * 10;//最大录音时间1000*60*10;
    public File file;
    public int position;
    private long startTime;
    private long endTime;
    private int BASE = 600;
    private int SPACE = 100;// 间隔取样时间
    private Handler outsideHandler;


    public void setHandler(Handler handler) {
        outsideHandler = handler;
    }

    /**
     * 开始录音 使用amr格式
     */
    public void startRecord(String fileName) {
        file = getFile(fileName);
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            mMediaRecorder.setMaxDuration(MAX_LENGTH);

            mMediaRecorder.prepare();
            mMediaRecorder.start();

            startTime = System.currentTimeMillis();
            mUpdateMicStatusTimer.run();
        } catch (Exception e) {
            Log.i("RecordManager:",
                    "call startAmr(File mRecAudioFile) failed!"
                            + e.getMessage());
        }

    }

    /**
     * 停止录音
     */
    public long stopRecord() {
        mHandler.removeCallbacks(mUpdateMicStatusTimer);
        try {
            if (mMediaRecorder == null)
                return 0L;
            if (startTime == 0 || (startTime < endTime)) {
                return 0L;
            } else {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
            }
            endTime = System.currentTimeMillis();
            return endTime - startTime;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private void updateMicStatus() {
        if (mMediaRecorder != null) {
            //int vuSize = 8 * mMediaRecorder.getMaxAmplitude() / 32768;
            //int ratio = mMediaRecorder.getMaxAmplitude() / BASE;
            int db = (int) (getVolume());
            Message msg = outsideHandler.obtainMessage();
            msg.what = 104;
            msg.arg1 = db;
            outsideHandler.sendMessage(msg);

            LogUtil.e("音量run: ------>" + "分贝" + db);
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public File getFile(String name) {
        //makeRootDirectory(Constant.envir1);//生成文件夹
        makeRootDirectory(Constant.envir);//生成文件夹
        makeRootDirectory(Constant.getEvaluateAddress());//先生成文件夹
        String tmpName = Constant.getEvaluateAddress() + "/" + name;
        File file = makeFilePath(tmpName + ".amr");
        Log.d("录音file", file.getAbsolutePath());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private File makeFilePath(String fileName) {
        File file = null;
        try {
            file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("录音文件error:" + e);
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

    public double getVolume() {
        double ratio = mMediaRecorder.getMaxAmplitude();
        if (ratio > 1)
            ratio = 20 * Math.log10(ratio);
        return ratio;
    }
}
