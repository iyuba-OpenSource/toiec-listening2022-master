package com.iyuba.core.common.manager;

import static com.iyuba.configation.Constant.getEvaluateAddress;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.WebConstant;

import java.io.File;
import java.io.IOException;

/**
 * amr音频处理
 * 
 * @author hongfa.yy
 * @version 创建时间2012-11-21 下午4:33:28
 * 来源网络 http://shuimuqinghua77.iteye.com/blog/1739128
 */
public class RecordManager {
	private MediaRecorder mMediaRecorder;
	public static final int MAX_LENGTH = 1000 * 60 * 10;//最大录音时间1000*60*10;
	private File file;
	private String actionUrl= WebConstant.HTTP_SPEECH_ALL +"/test/eval/";
	public int position;

	public RecordManager(File file, ImageView view) {
		this.file = file;
		this.view = view;
	}

	public RecordManager(File file) {
		this.file = file;
	}

	public RecordManager(String word) {
		this.file = getFile(word);
	}


	private long startTime;
	private long endTime;

	public File getFile(String name){
		String tmpName = getEvaluateAddress() + "/" + name;
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

	/**
	 * �?始录�? 使用amr格式
	 * @return
	 */
	public void startRecord() {
		// �?始录�?
		/* ①Initial：实例化MediaRecorder对象 */
		if (mMediaRecorder == null)
			mMediaRecorder = new MediaRecorder();
		try {
			/* ②setAudioSource/setVedioSource */
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克�?
			/*
			 * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
			 * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
			 */
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			/* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			/* ③准�? */
			mMediaRecorder.setOutputFile(file.getAbsolutePath());
			mMediaRecorder.setMaxDuration(MAX_LENGTH);

			mMediaRecorder.prepare();
			mMediaRecorder.start();
			/* ④开�? */
			
			// AudioRecord audioRecord.
			/* 获取�?始时�?* */
			startTime = System.currentTimeMillis();
			// pre=mMediaRecorder.getMaxAmplitude();
			//updateMicStatus();
		} catch (IllegalStateException e) {  
            Log.i("RecordManager:",  
                    "call startAmr(File mRecAudioFile) failed!"  
                            + e.getMessage());  
        } catch (IOException e) {
            Log.i("RecordManager:",  
                    "call startAmr(File mRecAudioFile) failed!"  
                            + e.getMessage());  
        }  

	}

	/**
	 * 停止录音
	 * 
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

	/**
	 * 更新话筒状�?? 分贝是也就是相对响度 分贝的计算公式K=20lg(Vo/Vi) Vo当前振幅�? Vi基准值为600：我是�?�么制定基准值的呢？ �?20
	 * * Math.log10(mMediaRecorder.getMaxAmplitude() / Vi)==0的时候vi就是我所�?要的基准�?
	 * 当我不对�?麦克风说任何话的时�?�，测试获得的mMediaRecorder.getMaxAmplitude()值即为基准�?��??
	 * Log.i("mic_", "麦克风的基准值：" + mMediaRecorder.getMaxAmplitude());前提时不对麦克风说任何话
	 */
	private int BASE = 600;
	private int SPACE = 100;// 间隔取样时间
	private ImageView view;

	private void updateMicStatus() {
		if (mMediaRecorder != null && view != null) {
			int vuSize = 8 * mMediaRecorder.getMaxAmplitude() / 32768;
			 int ratio = mMediaRecorder.getMaxAmplitude() / BASE;
			 int db = 0;// 分贝
			 if (ratio > 1)
			 	db = (int) (20 * Math.log10(ratio));
			mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
		}
	}

	public double getVolume(){
		double ratio = mMediaRecorder.getMaxAmplitude();
		if (ratio > 1)
			ratio = 20 * Math.log10(ratio);
		return ratio;
	}

}