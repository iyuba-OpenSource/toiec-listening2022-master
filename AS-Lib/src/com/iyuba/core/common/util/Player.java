package com.iyuba.core.common.util;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

public class Player implements OnBufferingUpdateListener, OnCompletionListener,
		MediaPlayer.OnPreparedListener {
	public MediaPlayer mediaPlayer;
	private SeekBar skbProgress;
	private Context mContext;
	private Timer mTimer = new Timer();
	private OnPlayStateChangedListener opscl;
	private String audioUrl;
	private Boolean flag=true;//设置线程是否关闭

	public Player(Context context, OnPlayStateChangedListener opscl) {
		this.mContext = context;
		this.opscl = opscl;
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);

		} catch (Exception e) {

		}

	}

	public Player(Context context, OnPlayStateChangedListener opscl,
			SeekBar skbProgress) {
		this.skbProgress = skbProgress;
		skbProgress.setEnabled(false);
		this.mContext = context;
		this.opscl = opscl;
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);

		} catch (Exception e) {
			// Log.e("mediaPlayer", "error", e);
		}

		// mTimer.schedule(mTimerTask, 0, 1000);
		// mTimer.cancel();
	}

	/*******************************************************
	 * 通过定时器和Handler来更新进度条
	 ******************************************************/
	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			try {
				if (mediaPlayer == null)
					return;
				if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
					handleProgress.sendEmptyMessage(0);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				int position = mediaPlayer.getCurrentPosition();
				int duration = mediaPlayer.getDuration();
				System.out.println("mediaPlayer.getCurrentPosition===="
						+ position);
				System.out.println("mediaPlayer.getDuration()====" + duration);
				// Log.e("-------------播放器当前播放位置",
				// "mediaPlayer.getCurrentPosition():"+position);
				// Log.e("播放器时长", "mediaPlayer.getDuration():"+duration);
				if (duration > 0) {
					long pos = skbProgress.getMax() * position / duration;
					// Log.e("播放进度条位置计算", "skbProgress.getMax() * position = "
					// +skbProgress.getMax() *
					// position+", 除以 duration后的值pos："+pos);
					skbProgress.setProgress((int) pos);
				}

				if (opscl != null) {
					opscl.setPlayTime(getAudioCurrTime(), getAudioAllTime());
				}
			}
		};
	};

	// *****************************************************

	public void play() {
		mediaPlayer.start();
	}

	public void playUrl(final String videoUrl) {
		this.audioUrl = videoUrl;
		handler.sendEmptyMessage(1);
	}

	public void pause() {
		mediaPlayer.pause();
	}

	public void stop() {
		flag=false;
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		mTimer.cancel();
		skbProgress.setEnabled(false);
		handleProgress.removeMessages(0);
	}

	public boolean isPlaying() {
		if (mediaPlayer == null) {
			return false;
		}
		return mediaPlayer.isPlaying();
	}

	public int getDur() {
		if (mediaPlayer != null) {
			return mediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	@Override
	/**
	 * 通过onPrepared播放
	 */
	public void onPrepared(MediaPlayer arg0) {
		arg0.start();
		Log.e("mediaPlayer", "onPrepared");
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		skbProgress.setProgress(0);
	    Log.e("mediaPlayer", "onCompletion");
	    if (opscl != null) {
			opscl.playCompletion();
		}

		if (opscl != null) {
			opscl.setPlayTime("00:00", getAudioAllTime());
		}
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
		skbProgress.setSecondaryProgress(bufferingProgress);
		// int currentProgress = skbProgress.getMax()
		// * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		// Log.e(currentProgress+"% play", bufferingProgress + "% buffer");
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(mContext,
						"请检查网络连接!",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				try {
					mediaPlayer.reset();
					mediaPlayer.setDataSource(audioUrl);
					new Thread() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();
							Log.e("flag",""+flag);
							if(flag){
								try {
									mediaPlayer.prepare();
									handler.sendEmptyMessage(2);
								} catch (IllegalStateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
						}
					}.start();

				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					if (opscl != null) {
						opscl.playFaild();
					}
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					if (opscl != null) {
						opscl.playFaild();
					}
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if (opscl != null) {
						opscl.playFaild();
					}
					e.printStackTrace();
				}
				break;
			case 2:
				mTimer.schedule(mTimerTask, 0, 1000);
				if (opscl != null) {
					opscl.setPlayTime(getAudioCurrTime(), getAudioAllTime());
				}
				skbProgress.setEnabled(true);
				break;
			}
		}
	};

	/**
	 * 获取音频总长
	 * 
	 * @return
	 */
	public String getAudioAllTime() {
		StringBuffer timeBuffer = new StringBuffer("");
		if (mediaPlayer != null) {
			int musicTime = mediaPlayer.getDuration() / 1000;// 秒
			String minit = "00";// 分
			String second = "00";// 秒
			if ((musicTime / 60) < 10)// 分
			{
				minit = "0" + String.valueOf(musicTime / 60);
				// timeBuffer.append("0").append(musicTime / 60).append(":")
				// .append(musicTime % 60);
			} else {
				minit = String.valueOf(musicTime / 60);
			}
			if ((musicTime % 60) < 10)// 秒
			{
				second = "0" + String.valueOf(musicTime % 60);
			} else {
				second = String.valueOf(musicTime % 60);
			}
			timeBuffer.append(minit).append(":").append(second);

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
			String minit = "00";// 分
			String second = "00";// 秒
			if ((musicTime / 60) < 10)// 分
			{
				minit = "0" + String.valueOf(musicTime / 60);
				// timeBuffer.append("0").append(musicTime / 60).append(":")
				// .append(musicTime % 60);
			} else {
				minit = String.valueOf(musicTime / 60);
			}
			if ((musicTime % 60) < 10)// 秒
			{
				second = "0" + String.valueOf(musicTime % 60);
			} else {
				second = String.valueOf(musicTime % 60);
			}
			timeBuffer.append(minit).append(":").append(second);
		}
		return timeBuffer.toString();
	}

}
