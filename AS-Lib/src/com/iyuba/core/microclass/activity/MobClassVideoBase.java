package com.iyuba.core.microclass.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.MobManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.Base64Coder;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.GetDeviceInfo;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.microclass.protocol.AddCreditsRequest;
import com.iyuba.core.microclass.protocol.AddCreditsResponse;
import com.iyuba.core.microclass.protocol.UploadStudyRecordRequest;
import com.iyuba.core.microclass.protocol.UploadStudyRecordResponse;
import com.iyuba.core.microclass.protocol.ViewCountTitleRequest;
import com.iyuba.core.microclass.protocol.ViewCountTitleResponse;
import com.iyuba.core.microclass.sqlite.mode.StudyRecordInfo;
import com.iyuba.core.microclass.sqlite.op.StudyRecordOp;
import com.iyuba.core.R;
import com.mob.MobSDK;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MobClassVideoBase extends BasisActivity {

	public final static String MOB_CLASS_COURSE_IMAGE = "http://static3."+com.iyuba.core.util.Constant.IYBHttpHead+"/resource/";
	private Context mContext;

	private String OwnerId;
	private String PackId;
	private String Title;
	private String TitleId;

	private int hasVideo;
	private int IsAllDownload;
	private int IsAudioDownload;
	private int IsVideoDownload;

	private StudyRecordOp studyRecordOp;

	private String playDir = "";

	// widget
	private SeekBar seekBar = null;
	private ProgressDialog waitting;
	private boolean isPaused;

	private Boolean playerShow = true;

	private RelativeLayout rlPlayBar;
	private RelativeLayout rlPlayTimeAllScreen;
	private RelativeLayout rlMobClassBaseTitle;
	private Button btnBack;
	private Button btnChange;
	private Button btnPlay;
	private ImageView ivCourseVideoShare;

	private TextView tvMobClassTitle;
	private TextView tvMobclassCurTime;
	private TextView tvMobclassAllTime;

	private TextView tvMobclassCurTimeAllScreen;
	private TextView tvMobclassAllTimeAllScreen;

	private ImageView ivMicrossBaseVideoReplay;

	private StudyRecordInfo studyRecordInfo;
	private GetDeviceInfo getDeviceInfo;
	private String Lesson;

	private String BeginTime;
	private String EndTime;

	private SurfaceView surfaceVideo;
	private MediaPlayer mediaPlayer;

	//用于判断用户左右滑屏时使用
	private float oldTouchValue;

	private static String shareCourseTitleUrl;
	private String shareCourseTitleImageUrl;

	private int postion = 0;
	private boolean bLocalPlayAvai = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置为无标题样式
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.lib_microclass_mobclass_video_base);
		mContext = this;

		mediaPlayer = new MediaPlayer();

		studyRecordOp = new StudyRecordOp(mContext);
		OwnerId = MobManager.Instance().ownerid + "";
		PackId = MobManager.Instance().packid + "";

		Title = getIntent().getStringExtra("title");
		TitleId = getIntent().getStringExtra("titleid");
		Lesson = getIntent().getStringExtra("lesson");

		shareCourseTitleUrl = "http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/m.jsp?id=" + TitleId;
		shareCourseTitleImageUrl = "http://static3."+com.iyuba.core.util.Constant.IYBHttpHead+"/resource/categoryIcon/" + PackId + ".png";

		hasVideo = getIntent().getIntExtra("hasVideo", 0);
		IsAllDownload = getIntent().getIntExtra("IsAllDownload", 0);
		IsAudioDownload = getIntent().getIntExtra("IsAudioDownload", 0);
		IsVideoDownload = getIntent().getIntExtra("IsVideoDownload", 0);

		initStudyRecord();

		waitting = new ProgressDialog(mContext);

		findView();

		initPlayer();

		controlVideo();

		playDir = Constant.envir + "res" + "/" + TitleId + "/" + TitleId + ".mp4";

		File fileLocal = new File(playDir);
		if (fileLocal.exists() &&  IsAllDownload == 1){
			bLocalPlayAvai = true;
		}else{
			bLocalPlayAvai = false;
		}

	}


	public void initStudyRecord() {
		getDeviceInfo = new GetDeviceInfo(mContext);
		studyRecordInfo = new StudyRecordInfo();

		BeginTime = getDeviceInfo.getCurrentTime();
		studyRecordInfo.BeginTime = BeginTime;
		studyRecordInfo.LessonId = PackId;
		studyRecordInfo.TestNumber = TitleId;
		studyRecordInfo.appId = Constant.APPID;
		studyRecordInfo.appName = Constant.AppName;
		studyRecordInfo.IP = getDeviceInfo.getLocalIPAddress();
		studyRecordInfo.DeviceId = getDeviceInfo.getLocalMACAddress();
		studyRecordInfo.Device = getDeviceInfo.getLocalDeviceType();

		studyRecordInfo.updateTime = "   ";
		studyRecordInfo.EndFlg = " ";

		if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
			studyRecordInfo.uid = AccountManagerLib.Instace(mContext).userId;
		} else {
			studyRecordInfo.uid = "0";
		}
	}


	private void findView() {
		// TODO Auto-generated method stub

		surfaceVideo = (SurfaceView) this.findViewById(R.id.surfaceVideo);
		rlPlayBar = (RelativeLayout) this.findViewById(R.id.all_PlayBar);
		rlPlayTimeAllScreen = (RelativeLayout) this
				.findViewById(R.id.RL_mobclassBaseCurAllTimeAllScreen);
		rlMobClassBaseTitle = (RelativeLayout) this
				.findViewById(R.id.RL_course_title);
		btnBack = (Button) this.findViewById(R.id.mobClassBaseBtnBack);

		btnChange = (Button) this.findViewById(R.id.mobClassBaseBtnChangeAudio);
		if (IsAudioDownload == 0 && IsAllDownload == 0) {
			btnChange.setVisibility(View.INVISIBLE);
		}

		btnPlay = (Button) this.findViewById(R.id.video_play);
		tvMobClassTitle = (TextView) this
				.findViewById(R.id.tv_mobclassBaseTitle);
		tvMobclassCurTime = (TextView) this.findViewById(R.id.textView_curtime);
		tvMobclassAllTime = (TextView) this.findViewById(R.id.textView_alltime);

		tvMobclassCurTimeAllScreen = (TextView) this
				.findViewById(R.id.tv_mobclassBaseCurTimeAllScreen);
		tvMobclassAllTimeAllScreen = (TextView) this
				.findViewById(R.id.tv_mobclassBaseAllTimeAllScreen);
		ivCourseVideoShare = (ImageView) this.findViewById(R.id.iv_course_video_share);

		rlPlayTimeAllScreen.setVisibility(View.GONE);

		ivMicrossBaseVideoReplay = (ImageView) findViewById(R.id.iv_microclass_base_video_replay);

		//设置播放时打开屏幕
		surfaceVideo.getHolder().setKeepScreenOn(true);
		surfaceVideo.getHolder().addCallback(new SurfaceViewLis());

		btnChange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("切换至音频：", "音频切换按钮被按下！");
				Intent intent = new Intent();
				intent.putExtra("titleid", TitleId);
				intent.putExtra("title", Title);
				intent.putExtra("lesson", Lesson);

				intent.putExtra("hasVideo", hasVideo);
				intent.putExtra("IsAllDownload", IsAllDownload);
				intent.putExtra("IsAudioDownload", IsAudioDownload);
				intent.putExtra("IsVideoDownload", IsVideoDownload);

				intent.setClass(mContext, MobClassBase.class);
				mContext.startActivity(intent);
				onBackPressed();
			}
		});
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		btnPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					if (mediaPlayer.isPlaying()) {
						btnPlay.setBackgroundResource(R.drawable.mob_play_button_pressed);
						mediaPlayer.pause();
					} else {
						btnPlay.setBackgroundResource(R.drawable.mob_pause_button_pressed);
						mediaPlayer.start();
					}
					ivMicrossBaseVideoReplay.setVisibility(View.INVISIBLE);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		ivCourseVideoShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (AccountManagerLib.Instace(mContext).checkUserLogin()) {

					String addIntegralUrl = "http://api."+com.iyuba.core.util.Constant.IYBHttpHead+"/credits/updateScore.jsp?srid=49";
					String url = addIntegralUrl +
							"&uid=" + AccountManagerLib.Instace(mContext).userId +
							"&idindex=" + TitleId +
							"&mobile=1" +
							"&flag=" + "1234567890" + Base64Coder.getTime();
					Log.d("Share Coin:", url);

					showShare();

				} else {
					Intent intent;
					intent = new Intent();
					intent.setClass(mContext, LoginActivity.class);
					mContext.startActivity(intent);
				}
			}
		});

		ivMicrossBaseVideoReplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initPlayer();
				controlVideo();

				try {
					play();
					mediaPlayer.seekTo(postion);
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

				btnPlay.setBackgroundResource(R.drawable.mob_pause_button_pressed);

				mediaPlayer.start();

				BeginTime = getDeviceInfo.getCurrentTime();
				studyRecordInfo.BeginTime = BeginTime;
				studyRecordInfo.appId = Constant.APPID;

				ivMicrossBaseVideoReplay.setVisibility(View.INVISIBLE);

			}
		});
	}

	public void play() throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException {
		mediaPlayer.reset();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setDataSource(playDir);

		// 把视频输出到SurfaceView上
		mediaPlayer.setDisplay(surfaceVideo.getHolder());
		mediaPlayer.prepare();
		mediaPlayer.start();

		BeginTime = getDeviceInfo.getCurrentTime();
		studyRecordInfo.BeginTime = BeginTime;
		studyRecordInfo.appId = Constant.APPID;

		ivMicrossBaseVideoReplay.setVisibility(View.INVISIBLE);

	}

	private class SurfaceViewLis implements SurfaceHolder.Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
								   int height) {

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (postion == 0) {
				try {
					play();
					mediaPlayer.seekTo(postion);
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

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {

		}

	}

	@Override
	protected void onPause() {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				// 保存当前播放的位置
				postion = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
			}
		}
		super.onPause();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (mediaPlayer != null && bLocalPlayAvai) {
			mediaPlayer.start();
		}
		super.onResume();
	}


	@Override
	protected void onDestroy() {
		if (mediaPlayer.isPlaying())
			mediaPlayer.stop();
//			 mediaPlayer.release(); 
		super.onDestroy();
	}


	/**
	 *
	 */
	private void initPlayer() {
		// TODO Auto-generated method stub
		tvMobClassTitle.setText(Title);
		seekBar = (SeekBar) findViewById(R.id.small_seekBar_player);
		seekBar.getParent().requestDisallowInterceptTouchEvent(true);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress,
										  boolean fromUser) {
				if (fromUser) {
					mediaPlayer.seekTo(progress);
					handler.sendEmptyMessage(0);
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

	private void setSeekbar() {
		int i = mediaPlayer.getDuration();
		seekBar.setMax(i);
		i /= 1000;
		int minute = i / 60;
		int second = i % 60;
		minute %= 60;
		tvMobclassAllTime.setText(String.format("/%02d:%02d", minute, second));
		tvMobclassAllTimeAllScreen.setText(String.format("/%02d:%02d", minute,
				second));
	}


	private void controlVideo() {
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer arg0) {
				setSeekbar();
				mediaPlayer.start();
				if (waitting != null && waitting.isShowing()) {
					handler.sendEmptyMessage(2);
				}
				videoHandler.sendEmptyMessage(0);
			}
		});
		mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = percent;
				msg.what = 1;
				videoHandler.sendMessage(msg);
			}
		});
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				return true;
			}
		});
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				EndTime = getDeviceInfo.getCurrentTime();
				studyRecordInfo.Lesson = Lesson;
				studyRecordInfo.EndTime = EndTime;
				studyRecordInfo.EndFlg = "1";
				studyRecordInfo.IsUpload = false;
				studyRecordOp.saveStudyRecord(studyRecordInfo);
				//----------------学习记录，插入数据库一条就上传一条----------------------------------------------
				Log.d("执行到的地方测试：", "获取将要上传的学习记录！！！！！！！");

				Message msg = new Message();
				msg.what = 1;
				msg.obj = studyRecordInfo;
				studyHandler.sendMessageDelayed(msg, 1500);

				//----------------学习记录，插入数据库一条就上传一条----------------------------------------------

				mediaPlayer.seekTo(0);
//		    		mediaPlayer.pause();

				btnPlay.setBackgroundResource(R.drawable.mob_play_button_pressed);
				mediaPlayer.pause();

//		    		mediaPlayer.stop();
				seekBar.setProgress(0);
				setSeekbar();
				ivMicrossBaseVideoReplay.setVisibility(View.VISIBLE);
			}
		});
	}

	private void showShare() {
		//ShareSDK.initSDK(this);
		MobSDK.init(this);

		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		//oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//			 oks.setTitle("爱语微课");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(shareCourseTitleUrl);
		// text是分享文本，所有平台都需要这个字段
//			 oks.setText("小伙伴们来听一下我们的爱语微课吧～");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片

//			 oks.setImageUrl(shareCourseTitleImageUrl);

		// url仅在微信（包括好友和朋友圈）中使用
		Log.d("微信分享：", shareCourseTitleUrl);
//			 oks.setUrl(shareCourseTitleUrl);
//			 oks.setUrl("http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/m.jsp?id=2");

		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment(Constant.APP_NAME);
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(Constant.APP_NAME);
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(shareCourseTitleUrl);
		//图片的网络路径，新浪微博、人人、QQ空间和Linked-in
		oks.setImageUrl(shareCourseTitleImageUrl);

		oks.setText("小伙伴们来听一下我们的托业听力吧～");
		oks.setTitle(Title);
		oks.setUrl(shareCourseTitleUrl);

//			 oks.setText("测试分享文字 http://www.baidu.com");
//			 oks.setTitle("分享");
//			 oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
//			 oks.setUrl("http://www.baidu.com");
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				// TODO Auto-generated method stub

				Log.d("执行分享完毕的回调：", "分享完毕之后的回调！");
				handler.sendEmptyMessage(10);

			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});

		// 启动分享GUI
		oks.show(this);
	}


	Handler videoHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
				case 0:

					if (mediaPlayer != null) {
						if (!mediaPlayer.isPlaying()) {
							Log.d("videoHandler:", "播放器停止播放,跳过获取位置");
							break;
						}
					}
					int i = mediaPlayer.getCurrentPosition();
					seekBar.setProgress(i);
					i /= 1000;
					int minute = i / 60;
					int second = i % 60;
					minute %= 60;

					tvMobclassCurTime.setText(String.format("%02d:%02d", minute,
							second));
					tvMobclassCurTimeAllScreen.setText(String.format("%02d:%02d",
							minute, second));
					try {
						if (mediaPlayer.isPlaying()) {
							handler.sendEmptyMessage(0);
							btnPlay.setBackgroundResource(R.drawable.mob_pause_button);
						} else {
							btnPlay.setBackgroundResource(R.drawable.mob_play_button);
							handler.sendEmptyMessage(5);
						}
					} catch (Exception e) {
						// TODO: handle exception
					} finally {
						sendEmptyMessageDelayed(0, 1000);
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
				oldTouchValue = event.getX();
				break;
			case MotionEvent.ACTION_UP:

				//判断如何显示全屏状态
				if (playerShow == true) {
					rlMobClassBaseTitle.setVisibility(View.GONE);
					rlPlayBar.setVisibility(View.GONE);
					rlPlayTimeAllScreen.setVisibility(View.VISIBLE);

					playerShow = false;
				} else {
					rlMobClassBaseTitle.setVisibility(View.VISIBLE);
					rlPlayBar.setVisibility(View.VISIBLE);
					rlPlayTimeAllScreen.setVisibility(View.GONE);

					playerShow = true;
				}

				break;
		}
		return super.onTouchEvent(event);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			AddCreditsResponse addCoinResponse = (AddCreditsResponse) msg.obj;
			switch (msg.what) {
				case 0:
					// currentTextView.snyParagraph(currPara);
					break;
				case 1:
					waitting.show();
					break;
				case 2:
					waitting.dismiss();
					break;
				case 3:
					CustomToast.showToast(mContext,
							R.string.play_please_take_the_word);
					break;
				case 4:
					CustomToast.showToast(mContext, R.string.check_network);
					break;
				case 5:
					break;
				case 6:
					CustomToast.showToast(mContext, "视频文件不存在,请返回下载！");
					break;
				case 7:
					break;
				case 8:
					Toast.makeText(mContext, "分享成功加5分！您当前总积分:" + addCoinResponse.totalcredit, Toast.LENGTH_SHORT).show();
					break;
				case 9:
					Toast.makeText(mContext, "您已分享过该课程，请换个课程！", Toast.LENGTH_SHORT).show();
					break;
				case 10:
					ClientSession.Instace().asynGetResponse(
							new AddCreditsRequest(
									AccountManagerLib.Instace(mContext).userId, TitleId),
							new IResponseReceiver() {
								@Override
								public void onResponse(
										BaseHttpResponse response,
										BaseHttpRequest request, int rspCookie) {
									AddCreditsResponse addCoinResponse = (AddCreditsResponse) response;
									if (addCoinResponse.result == 200) {
										Message msg = Message.obtain();
										msg.obj = addCoinResponse;
										msg.what = 8;
										handler.sendMessage(msg);
									} else if (addCoinResponse.result == 201) {
										handler.sendEmptyMessage(9);
									}

								}
							});
					break;
			}
		}
	};

	Handler studyHandler = new Handler() {
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					final StudyRecordInfo studyRecordInfo = (StudyRecordInfo) msg.obj;
					ClientSession.Instace().asynGetResponse(
							new UploadStudyRecordRequest(studyRecordInfo),
							new IResponseReceiver() {

								@Override
								public void onResponse(
										BaseHttpResponse response,
										BaseHttpRequest request, int rspCookie) {
									// TODO Auto-generated method stub
									Log.d("Upload(单条上传记录)", "StudyRecordReq的内容");

									UploadStudyRecordResponse res = (UploadStudyRecordResponse) response;

									if (res.result.equals("1")) {
										Log.d("Upload结果为1", "结果为1");
										studyRecordOp.setIsUpload(studyRecordInfo.appId, studyRecordInfo.BeginTime);
									}
								}

							}, null, null);
					break;
				default:
					break;

			}
		}
	};

	Handler handlerRequest = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
				case 0:
					try {
						ExeProtocol.exe(new ViewCountTitleRequest(PackId, TitleId),
								new ProtocolResponse() {

									@Override
									public void finish(BaseHttpResponse bhr) {
										// TODO Auto-generated method stub
										Looper.prepare();

										ViewCountTitleResponse res = (ViewCountTitleResponse) bhr;

										if (res.ResultCode.equals("1")) {
											Toast.makeText(mContext, "获取Title浏览量正确！", Toast.LENGTH_SHORT);
										} else {
											Toast.makeText(mContext, "获取Title浏览量出错！", Toast.LENGTH_SHORT);
										}
										Looper.loop();
									}

									@Override
									public void error() {
										// TODO Auto-generated method stub

									}

								});
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
	};
}
