package com.iyuba.core.me.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.me.protocol.TestResultRequest;
import com.iyuba.core.me.protocol.TestResultResponse;
import com.iyuba.core.me.protocol.UserRankRequest;
import com.iyuba.core.me.protocol.UserRankResponse;
import com.iyuba.core.R;

public class TestResultActivity extends Activity {
	private Context mContext;
	private TextView tv, lsTotQues, lsAvgScore, skTotQues, skAvgScore,
			rdTotQues, rdAvgScore, wrTotQues, wrAvgScore, othrTotQues,
			othrAvgScore;
	private String testSum_0, testSum_1, testSum_2, testSum_3, testSum_4,
			score_0, score_1, score_2, score_3, score_4;
	private String totalTest = "", positionByTest = "", totalRate = "",
			positionByRate = "";
	private Button othrDetail, listenDetail, speakDetail, readDetail, wrDetail,
			backBtn;
	private CustomDialog waitDialog;
	private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {

		@Override
		public void onStartConnect(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnected(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartSend(BaseHttpRequest request, int rspCookie,
				int totalLen) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSend(BaseHttpRequest request, int rspCookie, int len) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSendFinish(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartRecv(BaseHttpRequest request, int rspCookie,
				int totalLen) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNetError(BaseHttpRequest request, int rspCookie,
				ErrorResponse errorInfo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intel_test_result);
		mContext = this;
		waitDialog = WaittingDialog.showDialog(TestResultActivity.this);

		tv = (TextView) findViewById(R.id.intel_conclusion);
		lsTotQues = (TextView) findViewById(R.id.ls_ques_num);
		lsAvgScore = (TextView) findViewById(R.id.ls_avg_score);
		skTotQues = (TextView) findViewById(R.id.sk_ques_num);
		skAvgScore = (TextView) findViewById(R.id.sk_avg_score);
		rdTotQues = (TextView) findViewById(R.id.rd_ques_num);
		rdAvgScore = (TextView) findViewById(R.id.rd_avg_score);
		wrTotQues = (TextView) findViewById(R.id.wr_ques_num);
		wrAvgScore = (TextView) findViewById(R.id.wr_avg_score);
		othrTotQues = (TextView) findViewById(R.id.othr_ques_num);
		othrAvgScore = (TextView) findViewById(R.id.othr_avg_score);

		othrDetail = (Button) findViewById(R.id.othrd);
		speakDetail = (Button) findViewById(R.id.sd);
		listenDetail = (Button) findViewById(R.id.ld);
		readDetail = (Button) findViewById(R.id.rd);
		wrDetail = (Button) findViewById(R.id.wrd);
		backBtn = (Button) findViewById(R.id.button_back);

		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		othrDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("testMode", "0");
				intent.setClass(mContext, TestDetailActivity.class);
				startActivity(intent);

			}
		});

		listenDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("testMode", "1");
				intent.setClass(mContext, TestDetailActivity.class);
				startActivity(intent);
			}
		});

		speakDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("testMode", "2");
				intent.setClass(mContext, TestDetailActivity.class);
				startActivity(intent);
			}
		});

		readDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("testMode", "3");
				intent.setClass(mContext, TestDetailActivity.class);
				startActivity(intent);
			}
		});

		wrDetail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("testMode", "4");
				intent.setClass(mContext, TestDetailActivity.class);
				startActivity(intent);
			}
		});

		waitDialog.show();
		new DataThread().start();
	}

	private class DataThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// waitDialog.show();
			String uid = AccountManagerLib.Instace(mContext).userId;

			ClientSession.Instace().asynGetResponse(new UserRankRequest(uid),
					new IResponseReceiver() {
						@Override
						public void onResponse(BaseHttpResponse response,
								BaseHttpRequest request, int rspCookie) {
							UserRankResponse tr = (UserRankResponse) response;

							if (tr != null && tr.result.equals("1")) {
								totalTest = tr.totalTest;
								positionByTest = tr.positionByTest;
								totalRate = tr.totalRate;
								positionByRate = tr.positionByRate;
							}
							// waitDialog.dismiss();
						}
					}, null, mNetStateReceiver);

			ClientSession.Instace().asynGetResponse(new TestResultRequest(uid),
					new IResponseReceiver() {
						@Override
						public void onResponse(BaseHttpResponse response,
								BaseHttpRequest request, int rspCookie) {
							TestResultResponse tr = (TestResultResponse) response;

							if (tr != null && tr.result.equals("1")) {
								testSum_0 = tr.testSum_0;
								score_0 = tr.score_0;
								testSum_1 = tr.testSum_1;
								score_1 = tr.score_1;
								testSum_2 = tr.testSum_2;
								score_2 = tr.score_2;
								testSum_3 = tr.testSum_3;
								score_3 = tr.score_3;
								testSum_4 = tr.testSum_4;
								score_4 = tr.score_4;

								handler.sendEmptyMessage(1);
							}
							// waitDialog.dismiss();
						}
					}, null, mNetStateReceiver);

		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// waitDialog.show();
				if (totalTest == "" || positionByTest == "" || totalRate == ""
						|| positionByRate == "") {
					handler.sendEmptyMessageDelayed(1, 500);
				} else {
					othrTotQues.setText(testSum_0);
					othrAvgScore.setText(score_0);
					lsTotQues.setText(testSum_1);
					lsAvgScore.setText(score_1);
					skTotQues.setText(testSum_2);
					skAvgScore.setText(score_2);
					rdTotQues.setText(testSum_3);
					rdAvgScore.setText(score_3);
					wrTotQues.setText(testSum_4);
					wrAvgScore.setText(score_4);

					SpannableStringBuilder style1, style2, style3, style4, style5, style6;
					String r3 = "已做题" + totalTest, r4 = "道，按做题量排名："
							+ positionByTest, r5 = "\n做题正确率：" + totalRate, r6 = "\n按正确率全站排名："
							+ positionByRate;
					style3 = new SpannableStringBuilder(r3);
					style4 = new SpannableStringBuilder(r4);
					style5 = new SpannableStringBuilder(r5);
					style6 = new SpannableStringBuilder(r6);

					style3.setSpan(new ForegroundColorSpan(Color.YELLOW),
							r3.indexOf(totalTest), r3.indexOf(totalTest)
									+ totalTest.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					style4.setSpan(
							new ForegroundColorSpan(Color.YELLOW),
							r4.indexOf(positionByTest),
							r4.indexOf(positionByTest)
									+ positionByTest.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					style5.setSpan(new ForegroundColorSpan(Color.YELLOW),
							r5.indexOf(totalRate), r5.indexOf(totalRate)
									+ totalRate.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					style6.setSpan(
							new ForegroundColorSpan(Color.YELLOW),
							r6.indexOf(positionByRate),
							r6.indexOf(positionByRate)
									+ positionByRate.length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					style3 = style3
							.append(style4.append(style5.append(style6)));
					tv.setText(style3);

					waitDialog.dismiss();
				}
				break;
			default:
				break;
			}
		}
	};

}
