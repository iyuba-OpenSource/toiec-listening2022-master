/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.discover.activity.test;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.OnPlayStateChangedListener;
import com.iyuba.core.common.manager.CetDataManager;
import com.iyuba.core.common.sqlite.mode.test.CetFillInBlank;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.R;

/**
 * 四六级填空答题界面
 * 
 * @author chentong
 * @version 1.0
 */
public class CetSingleBlank extends BasisActivity implements
		OnPlayStateChangedListener {
	private Context mContext;
	private Button backBtn;
	private TextView number, question;
	private EditText answer;
	private Button previous, next, qsound, submit;
	private Player qPlayer;
	private int curPos;
	private ArrayList<CetFillInBlank> blanks = new ArrayList<CetFillInBlank>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cet_single_blank);
		mContext = this;
		 CrashApplication.addActivity(this);
		blanks = CetDataManager.Instace().blankList;
		initWidget();
	}

	private void initWidget() {
		// TODO Auto-generated method stub
		backBtn = (Button) findViewById(R.id.button_back);
		backBtn.setOnClickListener(ocl);
		number = (TextView) findViewById(R.id.number);
		question = (TextView) findViewById(R.id.question);
		answer = (EditText) findViewById(R.id.answer);
		previous = (Button) findViewById(R.id.preview);
		next = (Button) findViewById(R.id.next);
		qsound = (Button) findViewById(R.id.qsound);
		submit = (Button) findViewById(R.id.submit);
		qPlayer = new Player(mContext, this);
		previous.setOnClickListener(ocl);
		next.setOnClickListener(ocl);
		qsound.setOnClickListener(ocl);
		submit.setOnClickListener(ocl);
		curPos = 0;
		setContent();
	}

	private void setContent() {
		// TODO Auto-generated method stub
		number.setText("第" + blanks.get(curPos).id + "题");
		question.setText(blanks.get(curPos).question);
		answer.setText(blanks.get(curPos).yourAnswer);
	}

	private void preview() {
		if (curPos > 0) {
			curPos--;
			handler.sendEmptyMessage(0);
			handler.sendEmptyMessage(1);
		}
		if (curPos == blanks.size() - 2) {
			handler.sendEmptyMessage(4);
		}
		if (curPos == 0) {
			handler.sendEmptyMessage(2);
		}
	}

	private void next() {
		if (curPos < blanks.size() - 1) {
			handler.sendEmptyMessage(4);
			curPos++;
			handler.sendEmptyMessage(0);
		}
		if (curPos == blanks.size() - 1) {
			handler.sendEmptyMessage(3);
		}
		if (curPos == 1) {
			handler.sendEmptyMessage(1);
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
			}
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
			} else if (id == R.id.preview) {
				blanks.get(curPos).yourAnswer = answer.getText().toString();
				preview();
			} else if (id == R.id.next) {
				blanks.get(curPos).yourAnswer = answer.getText().toString();
				next();
			} else if (id == R.id.submit) {
				intent = new Intent(mContext, CetSubmit.class);
				intent.putExtra("type", 1);
				startActivity(intent);
				finish();
			} else if (id == R.id.qsound) {
				qPlayer.playUrl(blanks.get(curPos).sound);
			} else {
			}
		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		qPlayer.pause();
		qPlayer.reset();
	}

	@Override
	public void playCompletion() {
		// TODO Auto-generated method stub
		qPlayer.reset();
		next();
	}

	@Override
	public void playFaild() {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(5);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		qPlayer.stop();
	}
}
