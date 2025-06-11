package com.iyuba.core.discover.activity.mob;

import java.util.ArrayList;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.manager.MobManager;
import com.iyuba.core.common.sqlite.mode.mob.MbText;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.widget.BackPlayer;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.R;

public class SimpleMobClassBase extends BasisActivity {
	public final static String MOB_CLASS_COURSE_IMAGE = "http://static1."+com.iyuba.core.util.Constant.IYBHttpHead+"/PClass/";
	private Context mContext;
	private ArrayList<MbText> mbList;
	private String OwnerId;
	private String PackId;
	private String Title;
	private String TitleId;
	private String url;

	// widget
	private SeekBar seekBar = null;
	private BackPlayer vv;
	private boolean isPaused;
	private int curImage = 0;// 记录当前播放的图片ID
	private String imagePath = "";
	private String imageDir = "";
	private boolean playerShow = true;
	// 显示图片界面
	private ImageView coursePictures;

	private RelativeLayout rlPlayBar;
	private Button ibPrePic;
	private RelativeLayout rlMobClassBaseTitle;
	private Button ibNextPic;
	private Button btnBack;
	private Button btnPlay;
	private TextView tvCurPicNum;
	private TextView tvAllPicNum;
	private TextView tvMobClassTitle;
	private TextView tvMobclassCurTime;
	private TextView tvMobclassAllTime;
	private String duration;
	private TextView clock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置为无标题样式
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.simple_mobclass_base);
		mContext = this;
		vv = new BackPlayer(mContext);
		mbList = MobManager.Instance().mbList;
		OwnerId = MobManager.Instance().ownerid + "";
		PackId = MobManager.Instance().packid + "";
		Title = getIntent().getStringExtra("title");
		TitleId = getIntent().getStringExtra("titleid");
		findView();
		setView();
		initPlayer();
		url = "http://static1."+com.iyuba.core.util.Constant.IYBHttpHead+"/PClass/" + OwnerId + "/" + PackId + "/"
				+ TitleId + "/" + TitleId + ".mp3";
		vv.setVideoPath(url);
		controlVideo();
		imageDir = MOB_CLASS_COURSE_IMAGE + OwnerId + "/" + PackId + "/"
				+ TitleId + "/";
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		videoHandler.removeMessages(0);
		vv.stopPlayback();
	}

	private void findView() {
		// TODO Auto-generated method stub

		rlPlayBar = (RelativeLayout) this.findViewById(R.id.all_PlayBar);
		clock = (TextView) this.findViewById(R.id.clock);
		ibPrePic = (Button) this.findViewById(R.id.audio_pre);
		rlMobClassBaseTitle = (RelativeLayout) this
				.findViewById(R.id.RL_course_title);
		ibNextPic = (Button) this.findViewById(R.id.audio_next);
		btnBack = (Button) this.findViewById(R.id.mobClassBaseBtnBack);
		btnPlay = (Button) this.findViewById(R.id.audio_play);
		tvCurPicNum = (TextView) this.findViewById(R.id.tv_mobclassBaseCurPics);
		tvAllPicNum = (TextView) this.findViewById(R.id.tv_mobclassBaseAllPics);
		tvMobClassTitle = (TextView) this
				.findViewById(R.id.tv_mobclassBaseTitle);
		tvMobclassCurTime = (TextView) this.findViewById(R.id.textView_curtime);
		tvMobclassAllTime = (TextView) this.findViewById(R.id.textView_alltime);
		coursePictures = (ImageView) this.findViewById(R.id.course_pictures);
		coursePictures.setScaleType(ScaleType.FIT_XY);
		clock.setVisibility(View.GONE);
	}

	private void setView() {
		// TODO Auto-generated method stub
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		ibPrePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (curImage > 1) {// 不是第一张
					curImage -= 2;
					int preTime = mbList.get(curImage).seconds * 1000;
					vv.seekTo(preTime);
				} else {
					CustomToast.showToast(mContext, "这里是第一张");
				}
			}
		});
		ibNextPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (curImage < mbList.size()) {// 不是最后一篇
					int nextTime = mbList.get(curImage).seconds * 1000;
					vv.seekTo(nextTime);
				} else {
					CustomToast.showToast(mContext, "已经是最后一张");
				}
			}
		});
		btnPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				isPaused = !isPaused;
				if (isPaused) {
					btnPlay.setBackgroundResource(R.drawable.mob_play_button_pressed);
					vv.pause();
				} else {
					btnPlay.setBackgroundResource(R.drawable.mob_pause_button_pressed);
					vv.start();
				}
			}
		});
	}

	/**
	 * 
	 */
	private void initPlayer() {
		// TODO Auto-generated method stub
		tvMobClassTitle.setText(Title);
		tvAllPicNum.setText("/" + mbList.size());
		seekBar = (SeekBar) findViewById(R.id.small_seekBar_player);
		seekBar.getParent().requestDisallowInterceptTouchEvent(true);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress,
					boolean fromUser) {
				if (fromUser) {
					vv.seekTo(progress);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		});
	}

	private String formatTime(int time) {
		int i = time / 1000;
		int minute = i / 60;
		int second = i % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	private void setSeekbar() {
		int i = vv.getDuration();
		seekBar.setMax(i);
		duration = formatTime(i);
		tvMobclassAllTime.setText(duration);
	}

	private void controlVideo() {
		vv.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer arg0) {
				setSeekbar();
				vv.start();
				videoHandler.sendEmptyMessage(0);
			}
		});
		vv.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = percent;
				msg.what = 1;
				videoHandler.sendMessage(msg);
			}
		});
	}

	/**
	 * 
	 * 由目前播放到的时间在MbText时间点里面查找那张图片的位置
	 * 
	 * @param second目前播放到的时间
	 * @return
	 */
	public int getImage(int second) {
		int step = 0;
		if (mbList != null && mbList.size() != 0) {
			for (int i = 0; i < mbList.size(); i++) {
				if (second >= mbList.get(i).seconds) {
					step = i + 1;
				} else {
					break;
				}
			}
		}
		return step;
	}

	Handler videoHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				int i = vv.getCurrentPosition();
				seekBar.setProgress(i);
				String currTime = formatTime(i);
				tvMobclassCurTime.setText(currTime);
				clock.setText(currTime + "/" + duration);
				curImage = getImage(i / 1000);
				imagePath = imageDir + mbList.get(curImage - 1).imageName
						+ ".jpg";
				GitHubImageLoader.Instace(mContext).setPic(imagePath,
						coursePictures, R.drawable.waitting);
				tvCurPicNum.setText(String.valueOf(curImage));
				try {
					if (vv.isPlaying()) {
						btnPlay.setBackgroundResource(R.drawable.mob_pause_button);
					} else {
						btnPlay.setBackgroundResource(R.drawable.mob_play_button);
					}
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					videoHandler.sendEmptyMessageDelayed(0, 1000);
				}
				break;
			case 1:
				seekBar.setSecondaryProgress(msg.arg1 * seekBar.getMax() / 100);
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_UP:
			if (playerShow == true) {
				rlMobClassBaseTitle.setVisibility(View.GONE);
				ibPrePic.setVisibility(View.GONE);
				ibNextPic.setVisibility(View.GONE);
				rlPlayBar.setVisibility(View.GONE);
				clock.setVisibility(View.VISIBLE);
				playerShow = false;
			} else {
				rlMobClassBaseTitle.setVisibility(View.VISIBLE);
				ibPrePic.setVisibility(View.VISIBLE);
				ibNextPic.setVisibility(View.VISIBLE);
				rlPlayBar.setVisibility(View.VISIBLE);
				clock.setVisibility(View.GONE);
				playerShow = true;
			}
			break;
		}
		return super.onTouchEvent(event);
	}
}
