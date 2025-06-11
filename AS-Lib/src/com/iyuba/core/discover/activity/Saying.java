package com.iyuba.core.discover.activity;

import java.util.Random;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.sqlite.mode.Sayings;
import com.iyuba.core.common.sqlite.op.SayingsOp;
import com.iyuba.core.R;

/**
 * 谚语界面
 * 
 * @author chentong
 * @version 1.1 修改内容 增添自动模式
 */
public class Saying extends BasisActivity {
	private Context mContext;
	private Button backBtn, nextMode;
	private TextView english, chinese;
	private int id;
	private Sayings sayings;
	private SayingsOp sayingsOp;
	private Random rnd;
	private Button next;
	private boolean mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.saying);
		mContext = this;
		 CrashApplication.addActivity(this);
		mode = ConfigManager.Instance().loadBoolean("saying");
		backBtn = (Button) findViewById(R.id.button_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		nextMode = (Button) findViewById(R.id.next_mode);
		nextMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mode = !mode;
				ConfigManager.Instance().putBoolean("saying", mode);
				setButtonText();
			}
		});
		next = (Button) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(0);
			}
		});
		chinese = (TextView) findViewById(R.id.chinese);
		english = (TextView) findViewById(R.id.english);
		sayingsOp = new SayingsOp(mContext);
		setButtonText();
	}

	private void setButtonText() {
		if (mode) {
			nextMode.setText(R.string.saying_auto);
			next.setVisibility(View.VISIBLE);
			handler.removeMessages(0);
			handler.sendEmptyMessage(0);
		} else {
			nextMode.setText(R.string.saying_manul);
			next.setVisibility(View.INVISIBLE);
			handler.sendEmptyMessage(0);
		}
	}

	private void setData() {
		chinese.setText(sayings.chinese);
		english.setText(sayings.english);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				rnd = new Random();
				id = rnd.nextInt(10000) % 154 + 1;
				sayings = sayingsOp.findDataById(id);
				setData();
				if (!mode) {
					handler.sendEmptyMessageDelayed(0, 4000);
				}
				break;
			}
		}
	};

	@Override
	public void finish() {
		super.finish();
		if (!mode) {
			handler.removeMessages(0);
		}
	}
}
