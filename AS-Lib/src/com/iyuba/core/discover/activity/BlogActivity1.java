/**
 * 
 */
package com.iyuba.core.discover.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.core.R;
import com.iyuba.core.common.manager.DataManager;
import com.iyuba.core.common.util.OnPlayStateChangedListener;
import com.iyuba.core.common.util.Player;
import com.iyuba.core.common.util.TextLength;
import com.iyuba.core.common.util.VideoPlayer;
import com.iyuba.core.common.widget.dialog.CustomDialog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author yao 显示日志内容
 */
public class BlogActivity1 extends THSActivity implements
		OnPlayStateChangedListener {

	private Button backButton, blogComments,share;
	private String message = "";
	private TextView blogSubject, blogTitle, blogTime;
	private TextView test;
	private String subject = "";
	int i = 0;
	private CustomDialog waitingDialog;
	// ------------------------单词释义相关控件
	private TextView keyText, pronText, defText;
	private Button addWordsBtn;
	private ProgressBar loadingBar;
	Spanned sp;
	// 播放
	private LinearLayout playerLayout;
	private ImageButton pause;
	private TextView durationTextView = null, playedTextView = null;
	private SurfaceView vv = null;
	private SeekBar seekBar = null;
	private boolean isPaused = true;
	private static int screenWidth = 0, screenHeight = 0;
	private ImageView image_stop;
	private Player m = null;// 播放音频
	private VideoPlayer videoPlayer = null;
	// 获取VIP信息
	private static String vip = "0";
	private Bitmap bm;
	private Boolean flag=false;//判断是否是正在播放
	private int first=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_blog);
		waitingDialog = waitingDialog();
		message = DataManager.Instance().blogContent.message;
		subject = DataManager.Instance().blogContent.subject;
		test = (TextView) findViewById(R.id.test);
		
		initTranslationPanel();
		initWidget();
		
		handler.sendEmptyMessage(2);
		Thread thread = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				sp = Html.fromHtml(message, new Html.ImageGetter() {
					@Override
					public Drawable getDrawable(String source) {
						InputStream is = null;
						try {
							is = (InputStream) new URL(source).getContent();
							Drawable d = Drawable.createFromStream(is, "src");
							d.setBounds(0, 0, d.getIntrinsicWidth(),
									d.getIntrinsicHeight());
							is.close();
							return d;
						} catch (Exception e) {
							return null;
						}
					}
				}, null);
				handler.sendEmptyMessage(1);
			}
		};
		thread.start();

	}

	private void initTranslationPanel() {
		// TODO Auto-generated method stub
		keyText = (TextView) findViewById(R.id.word_key);
		pronText = (TextView) findViewById(R.id.word_pron);
		defText = (TextView) findViewById(R.id.word_def);
		
		loadingBar = (ProgressBar) findViewById(R.id.progressBar_get_Interperatatior);
	}

	private void initWidget() {
		// TODO Auto-generated method stub
		backButton = (Button) findViewById(R.id.blog_back_btn);
		blogSubject = (TextView) findViewById(R.id.blog_subject);
		blogTitle = (TextView) findViewById(R.id.blog_title);
		blogTime = (TextView) findViewById(R.id.blog_time);
		blogComments = (Button) findViewById(R.id.blogcomments);
		playerLayout = (LinearLayout) findViewById(R.id.playerLayout);
		pause = (ImageButton) findViewById(R.id.video_play);
		durationTextView = (TextView) findViewById(R.id.textView_alltime);
		playedTextView = (TextView) findViewById(R.id.textView_currenttime);
		getScreenSize();
		vv = (SurfaceView) findViewById(R.id.videoView_small);
		LayoutParams lp = vv.getLayoutParams();
		lp.height = 200;
		lp.width = screenWidth - 100;
		vv.setLayoutParams(lp);
		seekBar = (SeekBar) findViewById(R.id.seekBar_player);
		image_stop = (ImageView) findViewById(R.id.image_stop);
		
		if (DataManager.Instance().blogContent.mp3flag.equals("1")) {
			playerLayout.setVisibility(View.VISIBLE);
			vv.setVisibility(View.GONE);
			image_stop.setVisibility(View.GONE);
			initAudioPlay();
		} else if (DataManager.Instance().blogContent.mp3flag.equals("2")) {			
			playerLayout.setVisibility(View.VISIBLE);
			vv.setVisibility(View.VISIBLE);
			image_stop.setVisibility(View.VISIBLE);
			initVideoPlay();
		} else {
			playerLayout.setVisibility(View.GONE);
			vv.setVisibility(View.GONE);
		}
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BlogActivity1.this.finish();
			}
		});
		blogComments.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, BlogCommentsActivity.class);
				mContext.startActivity(intent);
			}
		});
		blogSubject.setText(subject);
		blogTime.setText(DateFormat.format(
				"yyyy-MM-dd kk:mm:ss",
				Long.parseLong(DataManager.Instance().blogContent.dateline) * 1000));
		if (DataManager.Instance().blogContent.username == null
				|| DataManager.Instance().blogContent.username.equals("")) {
			blogTitle.setText("日志");
		} else {
			blogTitle.setText(DataManager.Instance().blogContent.username
					+ "的日志");
		}

		share=(Button)findViewById(R.id.blogshare);
		share.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				prepareMessage();
			}
		});
	}
	private void prepareMessage() {
		//String imagePath = ImageOperation.getSharePicPath(String.valueOf(DataManager.Instace().blogContent.blogid));
		final String imagePath =""; //ScreenShot.takeScreenShot(BlogActivity1.this);
		String text = "#爱语吧# "+DataManager.Instance().blogContent.username+"《"
				+  DataManager.Instance().blogContent.subject + "》"
				+ " [http://blog."+com.iyuba.core.util.Constant.IYBHttpHead+"/GetBlogText?flag=1&uid=928&blog.blogid=" + DataManager.Instance().blogContent.blogid
				+ " ]"+" @爱语吧";
		String chinese = "";
		int length = TextLength.getLength(text);
		if (chinese.length() < 140 - length) {
			text = text + chinese;
		} else {
			int sublength = (int) (138.5 - length + TextLength
					.getEnglishCount(chinese) / 4);
			text = text + chinese.substring(0, sublength) + "...";
		}
		sendMessage(imagePath, text);
	}
	private void sendMessage(String imagePath, String text) {
		Intent shareInt = new Intent(Intent.ACTION_SEND);
		shareInt.setType("text/*");
		File f = new File(imagePath);
		shareInt.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
		shareInt.putExtra(Intent.EXTRA_TEXT, text);
		shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shareInt.putExtra("sms_body", text);
		startActivity(Intent.createChooser(shareInt,"选择分享方式"));
		
	}
	private void initVideoPlay() {
		// TODO Auto-generated method stub
		videoPlayer = new VideoPlayer(vv,seekBar,BlogActivity1.this);
		if (vip.equals("1")) {
			Log.e("视频地址", Constant.VIDEO_VIP_ADD+"  "+DataManager.Instance().blogContent.mp3path);
		//	videoPlayer.playUrl(Constant.VIDEO_VIP_ADD+DataManager.Instace().blogContent.mp3path);
		} else {
		//	videoPlayer.playUrl(Constant.VIDEO_ADD+DataManager.Instace().blogContent.mp3path); 	
		}
		pause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isPaused) {
					pause.setEnabled(false);
					pause.setBackgroundResource(R.drawable.pause_button);
					startVideo();
				} else {
					pause.setBackgroundResource(R.drawable.play);
					pauseVideo();
				}
				isPaused = !isPaused;
			}
		});
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progress;

			@Override
			public void onProgressChanged(SeekBar seekbar, int progress,
					boolean fromUser) {
				if(seekBar.getMax()!=0){
					this.progress = progress * videoPlayer.mediaPlayer.getDuration()
							/ seekBar.getMax();
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				if (videoPlayer!= null) {
					Log.e("seekTo", "" + this.progress);
					videoPlayer.mediaPlayer.seekTo(this.progress);
				}
			}
		});
	}
	private void pauseVideo() {
		// TODO Auto-generated method stub
		if(videoPlayer.mediaPlayer.isPlaying()){
			flag=true;
			videoPlayer.pause();
		}else{
			videoPlayer.play();
		}	
	
	}

	private void startVideo() {
		// TODO Auto-generated method stub
		if(videoPlayer.mediaPlayer.isPlaying()){
			videoPlayer.pause();	
		}else{
			if(!flag){
				if (vip.equals("1")) {
					Log.e("视频地址", Constant.VIDEO_VIP_ADD+"  "+DataManager.Instance().blogContent.mp3path);
					videoPlayer.playUrl(Constant.VIDEO_VIP_ADD+DataManager.Instance().blogContent.mp3path);
				} else {
					videoPlayer.playUrl(Constant.VIDEO_VIP_ADD+DataManager.Instance().blogContent.mp3path); 	
				}
			}
			videoPlayer.play();
			setScreen();
			image_stop.setVisibility(View.GONE);
		}
		
	}
	private void initAudioPlay() {
		// TODO Auto-generated method stub
		
		
		pause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(first==0){
					m = new Player(mContext, BlogActivity1.this, seekBar);
					if (vip.equals("1")) {
						m.playUrl(Constant.AUDIO_VIP_ADD+ DataManager.Instance().blogContent.mp3path);
					} else {
						m.playUrl(Constant.AUDIO_ADD+ DataManager.Instance().blogContent.mp3path);
					}
					first++;
				}
				if (isPaused) {
					pause.setEnabled(false);
					pause.setBackgroundResource(R.drawable.pause_button);
					startAudio();
				} else {
					pause.setBackgroundResource(R.drawable.play_button);
					pauseAudio();
				}
				isPaused = !isPaused;
			}
		});
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progress;

			@Override
			public void onProgressChanged(SeekBar seekbar, int progress,
					boolean fromUser) {
				this.progress = progress * m.mediaPlayer.getDuration()
						/ seekBar.getMax();

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				if (m != null) {
					Log.e("seekTo", "" + this.progress);
					m.mediaPlayer.seekTo(this.progress);
				}
			}
		});
	}

	private void setScreen() {
		int width = screenWidth - 20;
		int height;
		if(videoPlayer.mediaPlayer.getVideoWidth()!=0){
			height = (screenWidth - 120) * videoPlayer.mediaPlayer.getVideoHeight()
					/ videoPlayer.mediaPlayer.getVideoWidth();
		}else{
			height=150;
		}
	
		LayoutParams lp = vv.getLayoutParams();
		lp.height = height;
		lp.width = width;
		vv.setLayoutParams(lp);
	}

	private void startAudio() {
		if (vip.equals("1")) {
			Log.e("vip url",""+Constant.AUDIO_VIP_ADD+ DataManager.Instance().blogContent.mp3path);
			if(m.isPlaying()){
				m.pause();
				Log.e("VIP startAudio","pause");
			}else{
				try {
					m.mediaPlayer.prepare();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
				m.play();
				Log.e("VIP startAudio","play");
			}		
		} else {		
			Log.e("url",""+Constant.AUDIO_ADD+ DataManager.Instance().blogContent.mp3path);
			if(m.isPlaying()){
				m.pause();
				Log.e("startAudio","pause");
			}else{
				try {
					m.mediaPlayer.prepare();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
				m.play();
				Log.e("startAudio","play");
			}	
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.e("onPause","onPause");
		if(videoPlayer!=null){
			videoPlayer.pause();
		}
		super.onPause();
	}

	private void pauseAudio() {
		if(m.isPlaying()){
			m.pause();
			Log.e("VIP pauseAudio","pause");
		}else{
			m.play();
			Log.e("VIP pauseAudio","play");
		}	
	}
	private void getScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				test.setText(sp);
				handler.sendEmptyMessage(3);
				
				break;
			case 2:
				waitingDialog.show();
				break;
			case 3:
				waitingDialog.dismiss();
				break;
			
			case 22:
				image_stop.setImageBitmap(bm);
				image_stop.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}

	};

	public CustomDialog waitingDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.wetting_layout, null);
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
		CustomDialog cDialog = customBuilder.setContentView(layout).create();	
		return cDialog;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (m != null) {
			m.stop();
		}
		if(videoPlayer!=null){
			videoPlayer.stop();
		}
		first=0;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initWidget();
	}

	public void playSuccess() {
		// TODO Auto-generated method stub

	}

	public void setPlayTime(String currTime, String allTime) {
		// TODO Auto-generated method stub
		playedTextView.setText(currTime);
		durationTextView.setText(allTime);
		pause.setEnabled(true);
	}

	@Override
	public void playFaild() {
		// TODO Auto-generated method stub
		pause.setBackgroundResource(R.drawable.play);
	}

	@Override
	public void playCompletion() {
		// TODO Auto-generated method stub
		pause.setBackgroundResource(R.drawable.play);
		Log.e("播放完成","");
		isPaused=true;		
	}
	}
