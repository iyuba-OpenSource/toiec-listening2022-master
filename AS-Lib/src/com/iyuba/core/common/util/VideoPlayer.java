/**
 * 
 */
package com.iyuba.core.common.util;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;


/**
 * @author yao 视频播放类
 */
public class VideoPlayer implements OnBufferingUpdateListener,
		OnCompletionListener, MediaPlayer.OnPreparedListener,
		SurfaceHolder.Callback{
	private int videoWidth;
	private int videoHeight;
	public MediaPlayer mediaPlayer;
	private SurfaceHolder surfaceHolder;
	private SeekBar skbProgress;
	private Timer mTimer = new Timer();
	private OnPlayStateChangedListener opscl;
	public VideoPlayer(SurfaceView surfaceView, SeekBar skbProgress,OnPlayStateChangedListener opscl) {
		this.skbProgress = skbProgress;
		this.opscl = opscl;
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mTimer.schedule(mTimerTask, 0, 500);
	}

	/*******************************************************
	 * 通过定时器和Handler来更新进度条
	 ******************************************************/
	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
				handleProgress.sendEmptyMessage(0);
			}
		}
	};
	public void setOpscl(OnPlayStateChangedListener opscl) {
		this.opscl = opscl;
	}
	Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {

			int position = mediaPlayer.getCurrentPosition();
			int duration = mediaPlayer.getDuration();

			if(skbProgress!=null){
				if (duration > 0) {
					long pos = skbProgress.getMax() * position / duration;
					skbProgress.setProgress((int) pos);
				}
			}
			if (opscl != null) {
				opscl.setPlayTime(getAudioCurrTime(), getAudioAllTime());
			}
		};
	};

	// *****************************************************

	public void play() {
		mediaPlayer.start();
	}

	public void playUrl(String videoUrl) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(videoUrl);
			mediaPlayer.prepare();// prepare之后自动播放
			// mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pause() {
		mediaPlayer.pause();
	}

	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		Log.e("mediaPlayer", "surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDisplay(surfaceHolder);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);
			
		} catch (Exception e) {
			Log.e("mediaPlayer", "error", e);
		}
		Log.e("mediaPlayer", "surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.e("mediaPlayer", "surface destroyed");
	}

	@Override
	/**  
	 * 通过onPrepared播放  
	 */
	public void onPrepared(MediaPlayer arg0) {
		videoWidth = mediaPlayer.getVideoWidth();
		videoHeight = mediaPlayer.getVideoHeight();
		if (videoHeight != 0 && videoWidth != 0) {
			//arg0.start();
		}
		Log.e("mediaPlayer", "onPrepared");
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		if (skbProgress != null) {
			skbProgress.setProgress(0);
		}
		if (opscl != null) {
			opscl.playCompletion();
		}

		if (opscl != null) {
			opscl.setPlayTime("00:00", getAudioAllTime());
		}
		Log.e("onCompletion",""+skbProgress);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		skbProgress.setSecondaryProgress(bufferingProgress);
		int currentProgress = skbProgress.getMax()
				* mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
	//	Log.e(currentProgress + "% play", bufferingProgress + "% buffer");

	}

	
	/**
	 * 获取音频总长
	 * 
	 * @return
	 */
	public String getAudioAllTime() {
		StringBuffer timeBuffer = new StringBuffer("");
		if (mediaPlayer != null) {
			int musicTime = mediaPlayer.getDuration() / 1000;

			String minu = String.valueOf(musicTime / 60);
			if (minu.length() == 1) {
				minu = "0" + minu;
			}
			String sec = String.valueOf(musicTime % 60);
			if (sec.length() == 1) {
				sec = "0" + sec;
			}

			timeBuffer.append(minu).append(":").append(sec);
		}
		return timeBuffer.toString();
	}

	/**
	 * 获取音频当前播放进度时间
	 * 
	 * @return
	 */
	public String getAudioCurrTime() {
		StringBuffer timeBuffer = new StringBuffer("");
		if (mediaPlayer != null) {
			int musicTime = mediaPlayer.getCurrentPosition() / 1000;

			String minu = String.valueOf(musicTime / 60);
			if (minu.length() == 1) {
				minu = "0" + minu;
			}
			String sec = String.valueOf(musicTime % 60);
			if (sec.length() == 1) {
				sec = "0" + sec;
			}

			timeBuffer.append(minu).append(":").append(sec);
		}
		return timeBuffer.toString();
	}

}

