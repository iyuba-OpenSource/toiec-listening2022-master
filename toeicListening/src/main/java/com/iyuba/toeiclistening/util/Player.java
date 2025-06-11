package com.iyuba.toeiclistening.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.PlaybackParams;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.toeiclistening.listener.OnPlayStateChangedListener;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Player implements OnBufferingUpdateListener, OnCompletionListener,
        MediaPlayer.OnPreparedListener {
    public MediaPlayer mediaPlayer;
    private SeekBar skbProgress;
    private Context mContext;
    private Timer mTimer = new Timer();
    private OnPlayStateChangedListener opscl;
    private String audioUrl;
    private int allTime;
    public boolean isAudioPlay = true;

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
     * 閫氳繃瀹氭椂鍣ㄥ拰Handler鏉ユ洿鏂拌繘搴︽潯
     ******************************************************/
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            // Log.e("dingshiqi", "!!!!!!!!!!!");
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

    @SuppressLint("HandlerLeak")
    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int position = mediaPlayer.getCurrentPosition() / 1000 + 1;
                int duration = (allTime + 1);
                //LogUtil.e("时间宝贵 "+duration+" "+mediaPlayer.getDuration()+"position"+position);
                if (duration > 0) {
                    //获取当前seekbar对应的位置
                    long pos = skbProgress.getMax() * position / duration;
                    //long pos = allTime * position / duration;
                    skbProgress.setProgress((int) pos);
                }

                if (opscl != null) {
                    opscl.setPlayTime(getAudioCurrTime(), getAudioAllTime());
                }
            }
        }

        ;
    };

    /*
     * 音乐播放器的各种操作
     *
     *///播放音频时快进，前进的时间
    public static final int SEEK_NEXT = 5000;

    public void play() {

        mediaPlayer.start();
        if (opscl != null) {
            opscl.playResume();
        }

    }

    public void playUrl(final String videoUrl) {
        this.audioUrl = videoUrl;
        handler.sendEmptyMessage(1);
    }

    public void playAnother(final String videoUrl) {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(videoUrl);
                mediaPlayer.prepare();
                play();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
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
    }

    public void playAnother(final String videoUrl, boolean isPlay) {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(videoUrl);
                mediaPlayer.prepare();
                isAudioPlay = isPlay;
                if (isPlay) {
                    play();
                }
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
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
    }

    public void pause() {
        mediaPlayer.pause();
        if (opscl != null) {
            opscl.playPause();
        }

    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (skbProgress != null) {
            mTimer.cancel();
            skbProgress.setEnabled(false);
            handleProgress.removeMessages(0);
        }
        if (opscl != null) {
            opscl.playStop();
        }
    }


    public void nextSpeed() {
        int seekNextTime = getCurrentPosition() + Constant.SEEK_NEXT;
        if (seekNextTime > getDur()) {
            seekNextTime = getDur();
        }
        seekTo(seekNextTime);

    }

    public void preSpeed() {
        int seekPreTime = getCurrentPosition() + Constant.SEEK_PRE;
        if (seekPreTime < 0) {
            seekPreTime = 0;
        }
        seekTo(seekPreTime);
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) {
            return false;
        }
        return mediaPlayer.isPlaying();
    }

    public int getDur() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int msec) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(msec);
        }
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            play();
        }
    }


    /**
     * 控制速度 speed
     *
     * @param speed
     */
    public void setSpeed(float speed) {
        if (mediaPlayer != null) {

            PlaybackParams playbackParams = new PlaybackParams();
            playbackParams.setSpeed(speed);
            mediaPlayer.setPlaybackParams(playbackParams);
        }
    }

    @Override
    /**
     * 閫氳繃onPrepared鎾斁
     */
    public void onPrepared(MediaPlayer arg0) {
        if (isAudioPlay)
            arg0.start();
        if (opscl != null) {
            opscl.playSuccess();
        }
        // Log.e("mediaPlayer", "onPrepared");
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        if (skbProgress != null) {
            skbProgress.setProgress(0);
        }
        if (opscl != null) {
            opscl.playCompletion();
        }
        //播放完成之后暂停
        mediaPlayer.pause();
        // Log.e("mediaPlayer", "onCompletion");
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        if (skbProgress != null) {
            skbProgress.setSecondaryProgress(bufferingProgress);
        }
        // int currentProgress = skbProgress.getMax()
        // * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        // Log.e(currentProgress+"% play", bufferingProgress + "% buffer");
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    try {
                        //如果正在播放，则停止，报告目前播放结束
                        if (mediaPlayer.isPlaying()) {
                            if (opscl != null) {
                                opscl.playFaild();
                            }
                        }
                        mediaPlayer.reset();
                        LogUtil.d("media url", audioUrl);
//                        System.out.println(audioUrl + "-----------------------------");

                        mediaPlayer.setDataSource(audioUrl);
                        mediaPlayer.prepare();
                        new Thread() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                super.run();
                                try {
                                    if (opscl != null) {
                                        opscl.setPlayTime(getAudioCurrTime(),
                                                getAudioAllTime());
                                    }
                                    if (skbProgress != null) {
                                        handler.sendEmptyMessage(2);
                                    }
                                    // }
                                } catch (IllegalStateException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
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
                    mTimer.cancel();
                    mTimer = new Timer();
                    mTimer.schedule(mTimerTask, 0, 1000);
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

    public String getAudioAllTime(int i) {
        StringBuffer timeBuffer = new StringBuffer("");
        int musicTime = i;
        allTime = i;//此处赋值
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
        return timeBuffer.toString();
    }

}
