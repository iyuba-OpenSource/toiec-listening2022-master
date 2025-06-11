package com.iyuba.toeiclistening.activity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.frame.network.ClientSession;
import com.iyuba.toeiclistening.frame.network.IResponseReceiver;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.protocol.RequestCheckMessageCode;
import com.iyuba.toeiclistening.protocol.RequestGetMessageCode;
import com.iyuba.toeiclistening.protocol.ResponseCheckMessageCode;
import com.iyuba.toeiclistening.protocol.ResponseGetMessageCode;
import com.iyuba.toeiclistening.util.NetWorkState;
import com.iyuba.toeiclistening.util.TelNumMatch;
import com.iyuba.toeiclistening.widget.dialog.CustomDialog;

public class RegistByMobileActivity extends Activity{

	private String phoneNumString, messageCodeString;
	private EditText phoneNum, messageCode;
	private Button nextBtn, regBackBtn, button_getCode;
	private CustomDialog waitingDialog;
	private static String identifier;
	private Timer timer;
	private TextView emailregist;
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phoneregister);
		mContext = this;
		waitingDialog = waitingDialog();
		initWidget();
	}
	
	public void initWidget() {
		regBackBtn = (Button) findViewById(R.id.button_back);
		phoneNum = (EditText) findViewById(R.id.register_phoneNum);
		messageCode = (EditText) findViewById(R.id.register_messagecode);
		nextBtn = (Button) findViewById(R.id.register_nextBtn);
		button_getCode = (Button) findViewById(R.id.button_getCode);
		emailregist = (TextView) findViewById(R.id.emailregist);
		nextBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (verification()) {// 注册
					handler.sendEmptyMessage(3);
					ClientSession.Instace().asynGetResponse(
							new RequestCheckMessageCode(phoneNumString,
									identifier, messageCode.getText()
											.toString()),
							new IResponseReceiver() {

								@Override
								public void onResponse(
										BaseHttpResponse response,
										BaseHttpRequest request, int rspCookie) {
									// TODO Auto-generated method stub
									ResponseCheckMessageCode res = (ResponseCheckMessageCode) response;
									if (res.result.equals("1")) {
										if (res.checkResultCode == 1) {
											// 验证成功
											Intent intent = new Intent();
											intent.setClass(mContext,
													SetUserNameAndPsActivity.class);
											intent.putExtra("phonenum",
													phoneNumString);
											startActivity(intent);
										} else if (res.checkResultCode == 0) {
											// 验证码错误
											handler.sendEmptyMessage(5);
										} else if (res.checkResultCode == -1) {
											// 验证码过期
											handler.sendEmptyMessage(6);
										}
									} else if (res.result.equals("0")) {
										// 验证失败
										// handler.sendEmptyMessage(7);
										if (res.checkResultCode == 0) {
											// 验证码错误
											handler.sendEmptyMessage(5);
										} else if (res.checkResultCode == -1) {
											// 验证码过期
											handler.sendEmptyMessage(6);
										}
									}
									handler.sendEmptyMessage(4);
								}

							}, null, null);
				}
			}
		});
		button_getCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 获取短信验证码
				if (verificationNum()) {
					timer = new Timer();

					// 创建一个TimerTask
					// TimerTask是个抽象类,实现了Runnable接口，所以TimerTask就是一个子线程
					TimerTask timerTask = new TimerTask() {
						// 倒数10秒
						int i = 60;

						@Override
						public void run() {
							Message msg = new Message();
							msg.what = i--;
							handler_time.sendMessage(msg);
						}
					};
					if (new NetWorkState(mContext).isConnectingToInternet()) {
						timer.schedule(timerTask, 1000, 1000);// 3秒后开始倒计时，倒计时间隔为1秒
						ClientSession.Instace().asynGetResponse(
								new RequestGetMessageCode(phoneNumString),
								new IResponseReceiver() {
									@Override
									public void onResponse(
											BaseHttpResponse response,
											BaseHttpRequest request,
											int rspCookie) {
										// TODO Auto-generated method stub
										ResponseGetMessageCode res = (ResponseGetMessageCode) response;
										if (res.result.equals("1")) {
											if (res.res_code == 0) {
												// 短信发送成功
												identifier = res.identifier;
												Log.e("identifier", ""
														+ identifier);
											} else {
												// 短信发送失败，请重新发送
												Message message = handler_time
														.obtainMessage(0);
												handler_time
														.sendMessage(message);
											}
										} else if (res.result.equals("0")) {
											handler.sendEmptyMessage(8);
											Message message = handler_time
													.obtainMessage(0);
											handler_time.sendMessage(message);
										} else if (res.result.equals("-1")) {
											handler.sendEmptyMessage(9);
											Message message = handler_time
													.obtainMessage(0);
											handler_time.sendMessage(message);
										}
									}
								}, null, null);
					} else {
						handler.sendEmptyMessage(10);
					}
				}
			}
		});

		regBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		emailregist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mContext, RegistActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	Handler handler_time = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			// Handler处理消息
			if (msg.what > 0) {
				button_getCode.setText("重新发送(" + msg.what + "s)");
				button_getCode.setTextColor(Color.WHITE);
				button_getCode.setEnabled(false);
			} else {
				button_getCode.setEnabled(true);
				button_getCode.setText("获取验证码");
				// 结束Timer计时器
				timer.cancel();
			}

		}
	};

	/**
	 * 验证
	 */
	public boolean verification() {
		phoneNumString = phoneNum.getText().toString();
		messageCodeString = messageCode.getText().toString();
		if (phoneNumString.length() == 0) {
			phoneNum.setError("手机号不能为空");
			return false;
		}
		if (!checkPhoneNum(phoneNumString)) {
			phoneNum.setError("手机号输入错误");
			return false;
		}
		if (messageCodeString.length() == 0) {
			messageCode.setError("验证码不能为空");
			return false;
		}
		return true;
	}

	/**
	 * 验证
	 */
	public boolean verificationNum() {
		phoneNumString = phoneNum.getText().toString();
		messageCodeString = messageCode.getText().toString();
		if (phoneNumString.length() == 0) {
			phoneNum.setError("手机号不能为空");
			return false;
		}
		if (!checkPhoneNum(phoneNumString)) {
			phoneNum.setError("手机号输入错误");
			return false;
		}

		return true;
	}

	/**
	 * 匹配用户名
	 * 
	 * @param userId
	 * @return
	 */
	public boolean checkPhoneNum(String userId) {
		if (userId.length() < 2)
			return false;
		/*
		 * String check = "^[a-zA-Z0-9\u4e00-\u9fa5_]+$"; Pattern regex =
		 * Pattern.compile(check); Matcher matcher = regex.matcher(userId);
		 * return matcher.matches();
		 */
		TelNumMatch match = new TelNumMatch(userId);
		int flag = match.matchNum();
		if (flag == 1 || flag == 2 || flag == 3) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 匹配密码
	 * 
	 * @param userPwd
	 * @return
	 */
	public boolean checkUserPwd(String userPwd) {
		if (userPwd.length() < 6 || userPwd.length() > 12)
			return false;
		return true;
	}

	/**
	 * email格式匹配
	 * 
	 * @param email
	 * @return
	 */
	public boolean emailCheck(String email) {
		String check = "^([a-z0-ArrayA-Z]+[-|\\.]?)+[a-z0-ArrayA-Z]@([a-z0-ArrayA-Z]+(-[a-z0-ArrayA-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(email);
		return matcher.matches();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(mContext, R.string.tip_regist_success_text,
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(mContext, R.string.errorInfo, Toast.LENGTH_SHORT)
						.show();
				break;
			case 3:
				waitingDialog.show();
				break;
			case 4:
				waitingDialog.dismiss();
				break;
			case 5:
				Toast.makeText(mContext, "验证码错误，请重新输入", Toast.LENGTH_SHORT)
						.show();
				break;
			case 6:
				Toast.makeText(mContext, "验证码已过期，请重新获取", Toast.LENGTH_SHORT)
						.show();
				break;
			case 7:
				Toast.makeText(mContext, "验证失败，请重新验证", Toast.LENGTH_SHORT)
						.show();
				break;
			case 8:
				Toast.makeText(mContext, "您的手机号码不合法，请输入正确的手机号", 1000).show();
				break;
			case 9:
				Toast.makeText(mContext, "您的的手机号已注册，请直接登录", 1000).show();
				break;
			case 10:
				Toast.makeText(mContext, "您尚未连接网络，请检查网络连接", 1000).show();
			}
		}
	};

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	public CustomDialog waitingDialog() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.wetting, null);
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
		CustomDialog cDialog = customBuilder.setContentView(layout).create();
		return cDialog;
	}
}
