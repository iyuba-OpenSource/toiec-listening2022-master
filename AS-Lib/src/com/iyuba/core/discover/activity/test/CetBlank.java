package com.iyuba.core.discover.activity.test;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.manager.BackgroundManager;
import com.iyuba.core.common.manager.CetDataManager;
import com.iyuba.core.common.sqlite.mode.test.CetFillInBlank;
import com.iyuba.core.common.sqlite.mode.test.CetText;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.common.widget.BackPlayer;
import com.iyuba.core.common.widget.WordCard;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.subtitle.TextPage;
import com.iyuba.core.common.widget.subtitle.TextPageSelectTextCallBack;
import com.iyuba.core.R;

/**
 * 四六级填空界面
 * 
 * @author chentong
 * @version 1.0
 * @para "title" 当前考试项目标题
 */
public class CetBlank extends BasisActivity {
	private Context mContext;
	private Button backBtn, originalBtn;
	private TextView title;
	private String titleString;
	private TextView time;
	private TextPage content;
	private BackPlayer mPlayer;
	private SeekBar seekbar;
	private Button pause;
	private int type;// 0 question 1 original
	private int times = 1;
	private ArrayList<CetFillInBlank> blanksList = new ArrayList<CetFillInBlank>();
	private ArrayList<CetText> textsList = new ArrayList<CetText>();
	private WordCard card;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cet_blank);
		mContext = this;
		 CrashApplication.addActivity(this);
		titleString = getIntent().getStringExtra("title");
		blanksList = CetDataManager.Instace().blankList;
		textsList = CetDataManager.Instace().textList;
		CustomToast.showToast(mContext, R.string.tosingle);
		initWidget();
	}

	private void initWidget() {
		// TODO Auto-generated method stub
		title = (TextView) findViewById(R.id.title);
		title.setText(titleString);
		backBtn = (Button) findViewById(R.id.button_back);
		originalBtn = (Button) findViewById(R.id.original);
		backBtn.setOnClickListener(ocl);
		originalBtn.setOnClickListener(ocl);
		card = (WordCard) findViewById(R.id.word);
		card.setVisibility(View.GONE);
		content = (TextPage) findViewById(R.id.question);
		content.setTextpageSelectTextCallBack(new TextPageSelectTextCallBack() {

			@Override
			public void selectTextEvent(String selectText) {
				// TODO Auto-generated method stub
				if (selectText.matches("^[a-zA-Z'-]*")) {
					card.setVisibility(View.VISIBLE);
					card.searchWord(selectText);
				} else {
					card.setVisibility(View.GONE);
					CustomToast.showToast(mContext,
							R.string.play_please_take_the_word);
				}
			}

			@Override
			public void selectParagraph(int paragraph) {
				// TODO Auto-generated method stub

			}
		});
		content.setOnClickListener(ocl);
		pause = (Button) findViewById(R.id.pause);
		pause.setOnClickListener(ocl);
		time = (TextView) findViewById(R.id.time);
		mPlayer = new BackPlayer(mContext);
		seekbar = (SeekBar) findViewById(R.id.seekBar);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekbar, int progress,
					boolean fromUser) {
				if (fromUser) {
					mPlayer.seekTo(progress);
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
		content.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		controlVideo();
		mPlayer.setVideoPath(blanksList.get(0).allSound + "C" + times + ".mp3");
		setContent();
	}

	private void setContent() {
		// TODO Auto-generated method stub
		String lid = "0", id = "0";
		int pos = 0;
		StringBuffer sb = new StringBuffer("");
		int size = textsList.size();
		CetText text;
		for (int i = 0; i < size; i++) {
			text = textsList.get(i);
			id = text.id;
			if (!id.equals(lid)) {
				sb.append("\n\n");
			}
			if (text.qwords.equals("0")) {
				sb.append(text.sentence);
			} else {
				if(pos < blanksList.size()){
					sb.append(blanksList.get(pos).question);
					pos++;
				}
				
			}
		}
		content.setText(TextAttr.ToDBC(sb.toString()));
	}

	private void controlVideo() {
		mPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer arg0) {

				if (BackgroundManager.Instace().bindService != null
						&& BackgroundManager.Instace().bindService.getPlayer()
								.isPlaying()) {
					BackgroundManager.Instace().bindService.getPlayer().pause();
				}

				mPlayer.start();
				setPauseBackground();
				seekbar.setMax(mPlayer.getDuration());
				videoHandler.sendEmptyMessage(0);
			}
		});
		mPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = percent;
				msg.what = 1;
				videoHandler.sendMessage(msg);
			}
		});
		mPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				videoHandler.sendEmptyMessageDelayed(2, 3000);
			}
		});
	}

	private void setPauseBackground() {
		if (mPlayer.isPlaying()) {
			pause.setBackgroundResource(R.drawable.lib_pause);
		} else {
			pause.setBackgroundResource(R.drawable.lib_play);
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				setContent();
				break;
			case 1:
				StringBuffer sb = new StringBuffer("");
				String lid = "0",
				id = "0";
				int size = textsList.size();
				CetText text;
				for (int i = 0; i < size; i++) {
					text = textsList.get(i);
					id = text.id;
					if (!id.equals(lid) && !sb.equals("")) {
						sb.append("\n\n");
					}
					sb.append(text.sentence);
				}
				content.setText(TextAttr.ToDBC(sb.toString()));
				break;
			case 5:
				CustomToast.showToast(mContext, R.string.check_network);
			}
		}
	};

	Handler videoHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				int i = mPlayer.getCurrentPosition();
				seekbar.setProgress(i);
				i /= 1000;
				int minute = i / 60;
				int second = i % 60;
				minute %= 60;
				time.setText(String.format("%02d:%02d", minute, second));
				sendEmptyMessageDelayed(0, 1000);
				break;
			case 1:
				seekbar.setSecondaryProgress(msg.arg1 * seekbar.getMax() / 100);
				break;
			case 2:
				times++;
				if (times < 4) {
					mPlayer.setVideoPath(blanksList.get(0).allSound + "C"
							+ times + ".mp3");
				} else {
					Intent intent = new Intent(mContext, CetSingleBlank.class);
					startActivity(intent);
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			int id = arg0.getId();
			if (id == R.id.button_back) {
				onBackPressed();
			} else if (id == R.id.original) {
				if (type == 0) {
					type = 1;
					handler.sendEmptyMessage(1);
				} else {
					type = 0;
					setContent();
				}
			} else if (id == R.id.pause) {
				if (mPlayer.isPlaying()) {
					mPlayer.pause();
				} else {
					mPlayer.start();
				}
				setPauseBackground();
			} else if (id == R.id.question) {
				if (type == 0) {
					Intent intent = new Intent(mContext, CetSingleBlank.class);
					startActivity(intent);
				}
			} else {
			}
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		mPlayer.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPlayer.start();
		setPauseBackground();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (BackgroundManager.Instace().bindService != null
				&& !BackgroundManager.Instace().bindService.getPlayer()
						.isPlaying()) {
			BackgroundManager.Instace().bindService.getPlayer().start();
		}

		mPlayer.stopPlayback();
	}

	@Override
	public void onBackPressed() {
		if (card.isShown()) {
			card.setVisibility(View.GONE);
		} else if (type == 1) {
			type = 0;
			setContent();
		} else {
			super.onBackPressed();
		}
	}
}
