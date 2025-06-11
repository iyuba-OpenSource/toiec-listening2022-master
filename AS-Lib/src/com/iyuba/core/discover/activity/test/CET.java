package com.iyuba.core.discover.activity.test;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.OnPlayStateChangedListener;
import com.iyuba.core.common.manager.BackgroundManager;
import com.iyuba.core.common.manager.CetDataManager;
import com.iyuba.core.common.sqlite.mode.test.CetAnswer;
import com.iyuba.core.common.widget.BackPlayer;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.R;

/**
 * 四六级内容界面
 * 
 * @author chentong
 * @version 1.0
 * @para "title" 当前考试项目标题
 */
public class CET extends BasisActivity implements OnPlayStateChangedListener {
	private Context mContext;
	private Button backBtn, originalBtn;
	private TextView title;
	private String titleString;
	private TextView number, question, answer1, answer2, answer3, answer4,
			time;
	private Button previous, next, qsound, pause, submit;
	private BackPlayer mPlayer;
	private Player qPlayer;
	private SeekBar seekbar;
	private int curPos;
	private ArrayList<CetAnswer> answerList = new ArrayList<CetAnswer>();

	//是否是手动点击了下一题，如果是就不进行自动下一题，只在等待进入下一题时有效
	private boolean isLast;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cet);
		mContext = this;
		 CrashApplication.addActivity(this);
		titleString = getIntent().getStringExtra("title");
		answerList = CetDataManager.Instace().answerList;
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
		number = (TextView) findViewById(R.id.row);
		question = (TextView) findViewById(R.id.question);
		answer1 = (TextView) findViewById(R.id.answer1);
		answer2 = (TextView) findViewById(R.id.answer2);
		answer3 = (TextView) findViewById(R.id.answer3);
		answer4 = (TextView) findViewById(R.id.answer4);
		time = (TextView) findViewById(R.id.time);
		previous = (Button) findViewById(R.id.preview);
		next = (Button) findViewById(R.id.next);
		qsound = (Button) findViewById(R.id.qsound);
		pause = (Button) findViewById(R.id.pause);
		submit = (Button) findViewById(R.id.submit);
		mPlayer = new BackPlayer(mContext);
		qPlayer = new Player(mContext, this);
		seekbar = (SeekBar) findViewById(R.id.seekBar);
		previous.setOnClickListener(ocl);
		next.setOnClickListener(ocl);
		qsound.setOnClickListener(ocl);
		pause.setOnClickListener(ocl);
		submit.setOnClickListener(ocl);
		answer1.setOnClickListener(ocl);
		answer2.setOnClickListener(ocl);
		answer3.setOnClickListener(ocl);
		answer4.setOnClickListener(ocl);
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
		curPos = 0;
		controlVideo();
		setContent();
	}

	private void setContent() {
		// TODO Auto-generated method stub
		int tempPos = curPos + 1;
		int tempPosMinus = curPos;
		if (tempPos >= answerList.size()) {
			tempPos--;
		}
		if(answerList == null || answerList.size() == 0){
			return;
		}
		if (answerList.get(curPos).flag.equals("0")) {
			while (answerList.get(tempPos).flag.equals("0")) {
				if (tempPos < answerList.size() - 1) {
					tempPos++;
				} else {
					tempPos++;
					break;
				}
			}
			while (answerList.get(tempPosMinus).flag.equals("0")) {
				tempPosMinus--;
			}
			number.setText("第 " + answerList.get(tempPosMinus).id + " - "
					+ answerList.get(tempPos - 1).id + " 题");
		} else if (answerList.get(tempPos).flag.equals("0")) {
			while (answerList.get(tempPos).flag.equals("0")) {
				if (tempPos < answerList.size() - 1) {
					tempPos++;
				} else {
					tempPos++;
					break;
				}
			}
			number.setText("第 " + answerList.get(curPos).id + " - "
					+ answerList.get(tempPos - 1).id + " 题");
		} else {
			number.setText("第 " + answerList.get(curPos).id + " 题");
		}
		question.setText("Question: " + answerList.get(curPos).id);
		answer1.setText("A: " + answerList.get(curPos).a1);
		answer2.setText("B: " + answerList.get(curPos).a2);
		answer3.setText("C: " + answerList.get(curPos).a3);
		answer4.setText("D: " + answerList.get(curPos).a4);
		mPlayer.setVideoPath(answerList.get(curPos).sound);
		String answer = answerList.get(curPos).yourAnswer;
		if (answer.equals("")) {
			setAnswer(0);
		} else if (answer.equals("A")) {
			setAnswer(1);
		} else if (answer.equals("B")) {
			setAnswer(2);
		} else if (answer.equals("C")) {
			setAnswer(3);
		} else if (answer.equals("D")) {
			setAnswer(4);
		}
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
				isLast=true;
				videoHandler.sendEmptyMessageDelayed(2, 3000);
			}
		});
	}

	private void preview() {
		if (curPos > 0) {
			curPos--;
			handler.sendEmptyMessage(0);
			handler.sendEmptyMessage(1);
		}
		if (curPos == answerList.size() - 2) {
			handler.sendEmptyMessage(4);
		}
		if (curPos == 0) {
			handler.sendEmptyMessage(2);
		}
	}

	private void next() {
		if (curPos < answerList.size() - 1) {
			handler.sendEmptyMessage(4);
			curPos++;
			handler.sendEmptyMessage(0);
		}
		if (curPos == answerList.size() - 1) {
			handler.sendEmptyMessage(3);
		}
		if (curPos == 1) {
			handler.sendEmptyMessage(1);
		}
	}

	private void setAnswer(int position) {
		Drawable img = mContext.getResources().getDrawable(R.drawable.right_ok);
		answer1.setCompoundDrawables(null, null, null, null);
		answer2.setCompoundDrawables(null, null, null, null);
		answer3.setCompoundDrawables(null, null, null, null);
		answer4.setCompoundDrawables(null, null, null, null);
		switch (position) {
		case 1:
			answer1.setCompoundDrawablesWithIntrinsicBounds(img, null, null,
					null);
			answerList.get(curPos).yourAnswer = "A";
			break;
		case 2:
			answer2.setCompoundDrawablesWithIntrinsicBounds(img, null, null,
					null);
			answerList.get(curPos).yourAnswer = "B";
			break;
		case 3:
			answer3.setCompoundDrawablesWithIntrinsicBounds(img, null, null,
					null);
			answerList.get(curPos).yourAnswer = "C";
			break;
		case 4:
			answer4.setCompoundDrawablesWithIntrinsicBounds(img, null, null,
					null);
			answerList.get(curPos).yourAnswer = "D";
			break;
		default:
			answerList.get(curPos).yourAnswer = "";
			break;
		}

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
				previous.setBackgroundResource(R.drawable.previous_question);
				break;
			case 2:
				previous.setBackgroundResource(R.drawable.un_previous_question);
				break;
			case 3:
				submit.setVisibility(View.VISIBLE);
				next.setBackgroundResource(R.drawable.un_next_question);
				break;
			case 4:
				submit.setVisibility(View.GONE);
				next.setBackgroundResource(R.drawable.next_question);
				break;
			case 5:
				CustomToast.showToast(mContext, R.string.check_network);
				break;
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
				if (isLast) {
					next();
					isLast=true;
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	OnClickListener ocl = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent;
			int id = arg0.getId();
			if (id == R.id.button_back) {
				onBackPressed();
			} else if (id == R.id.original) {
				intent = new Intent(mContext, CetOriginal.class);
				if (number.getText().toString().contains("-")) {
					String[] temp = number.getText().toString().split(" ");
					intent.putExtra("curPos", temp[1]);
				} else {
					intent.putExtra("curPos", answerList.get(curPos).id);
				}
				startActivity(intent);
			} else if (id == R.id.pause) {
				if (mPlayer.isPlaying()) {
					mPlayer.pause();
				} else {
					mPlayer.start();
				}
				setPauseBackground();
			} else if (id == R.id.preview) {
				preview();
			} else if (id == R.id.next) {
				next();
				isLast=false;
			} else if (id == R.id.submit) {
				intent = new Intent(mContext, CetSubmit.class);
				startActivity(intent);
				finish();
			} else if (id == R.id.qsound) {
				mPlayer.pause();
				qPlayer.playUrl(answerList.get(curPos).qsound);
				setPauseBackground();
			} else if (id == R.id.answer1) {
				setAnswer(1);
			} else if (id == R.id.answer2) {
				setAnswer(2);
			} else if (id == R.id.answer3) {
				setAnswer(3);
			} else if (id == R.id.answer4) {
				setAnswer(4);
			} else {
			}
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		qPlayer.pause();
		qPlayer.reset();
		mPlayer.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPlayer.start();
		setPauseBackground();
	}

	@Override
	public void playCompletion() {
		// TODO Auto-generated method stub
		qPlayer.reset();
	}

	@Override
	public void playFaild() {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(5);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (BackgroundManager.Instace().bindService != null
				&& !BackgroundManager.Instace().bindService.getPlayer()
						.isPlaying()) {
			BackgroundManager.Instace().bindService.getPlayer().start();
		}
		qPlayer.stop();
		mPlayer.stopPlayback();
	}
}
